package org.qualipso.factory.collaboration.forum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.browser.BrowserService;
import org.qualipso.factory.collaboration.document.DocumentService;
import org.qualipso.factory.collaboration.document.entity.Document;
import org.qualipso.factory.collaboration.forum.entity.Forum;
import org.qualipso.factory.collaboration.forum.entity.ThreadMessage;
import org.qualipso.factory.collaboration.utils.CollaborationUtils;
import org.qualipso.factory.collaboration.ws.ForumWService;
import org.qualipso.factory.collaboration.ws.beans.ForumDTO;
import org.qualipso.factory.collaboration.ws.beans.MessageDTO;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;

@Stateless(name = "ForumServiceBean", mappedName = FactoryNamingConvention.SERVICE_PREFIX
	+ ForumService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.collaboration.forum.ForumService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE
	+ ForumService.SERVICE_NAME, serviceName = ForumService.SERVICE_NAME, portName = ForumService.SERVICE_NAME
	+ "Port")
@WebContext(contextRoot = CollaborationUtils.COLLABORATION_SERVICE_PREFIX, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX
	+ ForumService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class ForumServiceBean implements ForumService {

    private static Log logger = LogFactory.getLog(ForumServiceBean.class);

    private BindingService binding;
    private PEPService pep;
    private PAPService pap;
    private NotificationService notification;
    private MembershipService membership;
    //
    private BrowserService browser;
    private CoreService core;
    //
    private SessionContext ctx;
    private EntityManager em;
    //
    private ForumWService forumWS;
    private DocumentService documentService;

    public ForumServiceBean() {
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String createForum(String parentPath, String name)
	    throws ForumServiceException {
	String newForumId = null;
	logIt("Create forum under" + parentPath);
	try {
	    // Check name
	    if (name == null || name == "") {
		throw new ForumServiceException("Forum name is mandatory.");
	    }
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new ForumServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    // FIXME
	    // String path = forumsPath + "/" +
	    // CollaborationUtils.normalizeForPath(name);
	    // logIt("path:" + path);
	    pep.checkSecurity(caller, parentPath, "create");

	    // Call WS to create forum
	    HashMap<String, String> values = forumWS.createForum(name);
	    if (values != null && !values.isEmpty()) {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		String forumId = (String) values.get("forumId");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)
			|| (forumId == null || forumId.equals(""))) {
		    throw new ForumServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}
		// Create path based on id that is returned by the Mermig WS
		String path = PathHelper.normalize(parentPath + "/" + forumId);
		logIt("path:" + path);
		// Save the new entity in the factory and bind it
		// We persist only id,path,name
		Forum forum = new Forum();
		forum.setId(forumId);
		forum.setName(name);
		forum.setResourcePath(path);
		// save it
		em.persist(forum);
		// Bind the entity with the path and the identifier
		binding.bind(forum.getFactoryResourceIdentifier(), path);
		binding.setProperty(path,
			FactoryResourceProperty.CREATION_TIMESTAMP, ""
				+ System.currentTimeMillis());
		binding.setProperty(path,
			FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, ""
				+ System.currentTimeMillis());
		binding.setProperty(path, FactoryResourceProperty.AUTHOR,
			caller);
		// Create policy (owner)
		String policyId = UUID.randomUUID().toString();
		pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(
			policyId, caller, path));
		binding
			.setProperty(path, FactoryResourceProperty.OWNER,
				caller);
		binding.setProperty(path, FactoryResourceProperty.POLICY_ID,
			policyId);
		// notify
		notification.throwEvent(new Event(path, caller,
			Forum.RESOURCE_NAME, Event.buildEventType(
				ForumService.SERVICE_NAME, Forum.RESOURCE_NAME,
				"create"), ""));
		newForumId = forumId;
	    } else {
		throw new ForumServiceException(
			"No valid answer from the WS.Check logs.");
	    }
	    return newForumId;
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to create the forum at path " + parentPath, e);
	    throw new ForumServiceException(
		    "Unable to create the forum at path " + parentPath, e);
	}

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Forum readForum(String path) throws ForumServiceException {
	logIt("Read forum " + path);
	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new ForumServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    if (identifier == null) {
		throw new ForumServiceException("Identifier is null");
	    }
	    checkResourceType(identifier, Forum.RESOURCE_NAME);
	    // Find the entity
	    Forum forum = em.find(Forum.class, identifier.getId());
	    if (forum == null) {
		throw new ForumServiceException(
			"Unable to find a forum for id " + identifier.getId());
	    }
	    // Call the Mermig WS to retrieve info based on forum.getId()
	    HashMap<String, Object> values = forumWS.readForum(forum.getId());
	    if (values != null && !values.isEmpty()) {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)
			|| values.get("forum") == null) {
		    throw new ForumServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}
		// We don't perist the date,status and messages
		ForumDTO forWS = (ForumDTO) values.get("forum");
		forum.setDate(forWS.getDate());
		forum.setStatus(forWS.getStatus());
		// FIXME The path is missing in the mgs list.
		// WE NEED TO USE the browse for thread messages
		if (forWS.getMessages() != null) {
		    forum
			    .setMessages(convertHashMap(path, forWS
				    .getMessages()));
		}
		forum.setResourcePath(path);
		// notify
		notification.throwEvent(new Event(path, caller,
			Forum.RESOURCE_NAME, Event.buildEventType(
				ForumService.SERVICE_NAME, Forum.RESOURCE_NAME,
				"read"), ""));
		logIt(forum.toString());
	    } else {
		throw new ForumServiceException(
			"Error in recieving extra details from WS.");
	    }
	    return forum;
	} catch (Exception e) {
	    logger.error("Unable to read the forum at path " + path, e);
	    throw new ForumServiceException("Unable to read the forum at path "
		    + path, e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateForum(String path, String name, String date)
	    throws ForumServiceException {
	logger.debug("Update forum " + path);
	try {
	    // Check name
	    if (name == null || name == "") {
		throw new ForumServiceException("Forum name is mandatory.");
	    }
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new ForumServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "update");
	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, Forum.RESOURCE_NAME);
	    // Find the entity
	    Forum forum = em.find(Forum.class, identifier.getId());
	    if (forum == null) {
		throw new ForumServiceException(
			"Unable to find a forum for id " + identifier.getId());
	    }
	    HashMap<String, String> values = forumWS.updateForum(forum.getId(),
		    name, date);
	    if (values != null && !values.isEmpty()) {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
		    throw new ForumServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}
		// Update the entity
		forum.setName(name);
		forum.setDate(date);
		forum.setResourcePath(path);
		em.merge(forum);
		binding.setProperty(path,
			FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System
				.currentTimeMillis()
				+ "");
		// notify
		notification.throwEvent(new Event(path, caller,
			Forum.RESOURCE_NAME, Event.buildEventType(
				ForumService.SERVICE_NAME, Forum.RESOURCE_NAME,
				"update"), ""));
		logIt(forum.toString());
	    } else {
		throw new ForumServiceException(
			"Error in recieving extra details from WS.");
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to update the forum at path " + path, e);
	    throw new ForumServiceException(
		    "Unable to update the forum at path " + path, e);
	}

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteForum(String path) throws ForumServiceException {
	logIt("Delete forum " + path);
	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new ForumServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "delete");
	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, Forum.RESOURCE_NAME);
	    // Find the entity
	    Forum forum = em.find(Forum.class, identifier.getId());
	    if (forum == null) {
		throw new ForumServiceException(
			"Unable to find a forum for id " + identifier.getId());
	    }
	    // Check if forum has children, before deleting it.
	    if (!browser.hasChildren(path)) {
		// Call the WS to delete the forum
		HashMap<String, String> values = forumWS.deleteForum(forum
			.getId());
		if (values != null && !values.isEmpty()) {
		    String code = (String) values.get("statusCode");
		    String msg = (String) values.get("statusMessage");
		    logIt("Message Code:" + code + " Message: " + msg);
		    if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
			throw new ForumServiceException(
				"Error code recieved from the WS." + " Code"
					+ code + " Message:" + msg);
		    }
		    em.remove(forum);
		    // Delete the policy and unbind the resource from this path
		    String policyId = binding.getProperty(path,
			    FactoryResourceProperty.POLICY_ID, false);
		    pap.deletePolicy(policyId);
		    binding.unbind(path);
		    // notify
		    notification.throwEvent(new Event(path, caller,
			    Forum.RESOURCE_NAME, Event.buildEventType(
				    ForumService.SERVICE_NAME,
				    Forum.RESOURCE_NAME, "delete"), ""));
		} else {
		    throw new ForumServiceException(
			    "No valid answer from the WS.Check logs.");
		}
	    } else {
		throw new ForumServiceException(
			"Forum has children and cannot be deleted.");
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to delete the forum at path " + path, e);
	    throw new ForumServiceException(
		    "Unable to delete the forum at path " + path, e);
	}

    }

    @Override
    public void changeForumStatus(String path) throws ForumServiceException {
	logger.debug("Change forum status " + path);
	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new ForumServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "update");
	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, Forum.RESOURCE_NAME);
	    // Find the entity
	    Forum forum = em.find(Forum.class, identifier.getId());
	    if (forum == null) {
		throw new ForumServiceException(
			"Unable to find a forum for id " + identifier.getId());
	    }
	    HashMap<String, String> values = forumWS.changeForumStatus(forum
		    .getId());
	    if (values != null && !values.isEmpty()) {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
		    throw new ForumServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}
		// Update the entity
		if (forum.getStatus().equals(
			CollaborationUtils.FORUM_STATUS_ACTIVE)) {
		    forum.setStatus(CollaborationUtils.FORUM_STATUS_CLOSED);
		} else {
		    forum.setStatus(CollaborationUtils.FORUM_STATUS_ACTIVE);
		}
		forum.setResourcePath(path);
		em.merge(forum);
		binding.setProperty(path,
			FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System
				.currentTimeMillis()
				+ "");
		// notify
		notification.throwEvent(new Event(path, caller,
			Forum.RESOURCE_NAME, Event.buildEventType(
				ForumService.SERVICE_NAME, Forum.RESOURCE_NAME,
				"update"), ""));
		logIt(forum.toString());
	    } else {
		throw new ForumServiceException(
			"Error in recieving extra details from WS.");
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to update the forum's status at path " + path,
		    e);
	    throw new ForumServiceException(
		    "Unable to update the forum's status at path " + path, e);
	}

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String createThreadMessage(String parentPath, String name,
	    String forumId, String messageBody, String datePosted,
	    String isReply) throws ForumServiceException {

	logIt("createThreadMessage(...) called");
	logger.debug("params : path=" + parentPath + " isReply: " + isReply);
	String newMsgId = null;
	try {
	    // check supplied values
	    checkThreadValues(name, forumId, messageBody, datePosted);
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new ForumServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    // String parentPath = PathHelper.getParentPath(path);
	    pep.checkSecurity(caller, parentPath, "create");
	    //
	    String parentId = "";
	    boolean isReplyBoolean = Boolean.valueOf(isReply);
	    logIt("Is Reply: " + isReplyBoolean);
	    if (isReplyBoolean) {
		try {
		    // Check if parent is thread
		    ThreadMessage parentMsg = readThreadMessage(parentPath);
		    parentId = parentMsg.getId();
		    logIt("Succefully read parent thread. Id: " + parentId);
		} catch (Exception e) {
		    throw new ForumServiceException(
			    "Path is not valid for replying to a thread.Please check "
				    + parentPath);
		}
	    }
	    // Call Mermig WS
	    HashMap<String, String> values = forumWS.createThreadMessage(
		    forumId, parentId, name, messageBody, isReplyBoolean);
	    //
	    if (values != null && !values.isEmpty()
		    && values.get("messageId") != null) {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
		    throw new ForumServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}
		// We persist only id,path,name,parentId,forumId and author
		String newId = (String) values.get("messageId");
		// Create path based on parent path and the id that is returned
		// by the Mermig WS
		String path = PathHelper.normalize(parentPath + "/" + newId);
		ThreadMessage tm = new ThreadMessage();
		tm.setId(newId);
		tm.setResourcePath(path);
		tm.setName(name);
		tm.setParentId(parentId);
		tm.setForumId(forumId);
		// FIXME we need author not profile path.
		tm.setAuthor(caller);
		em.persist(tm);
		// Bind the entity with the path and the identifier
		logIt("Bind the " + tm.getFactoryResourceIdentifier() + " to "
			+ path);
		binding.bind(tm.getFactoryResourceIdentifier(), path);
		binding.setProperty(path,
			FactoryResourceProperty.CREATION_TIMESTAMP, ""
				+ System.currentTimeMillis());
		binding.setProperty(path,
			FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, ""
				+ System.currentTimeMillis());
		binding.setProperty(path, FactoryResourceProperty.AUTHOR,
			caller);
		// Create policy (owner)
		String policyId = UUID.randomUUID().toString();
		pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(
			policyId, caller, path));
		binding
			.setProperty(path, FactoryResourceProperty.OWNER,
				caller);
		binding.setProperty(path, FactoryResourceProperty.POLICY_ID,
			policyId);
		// notify
		notification.throwEvent(new Event(path, caller,
			ThreadMessage.RESOURCE_NAME, Event.buildEventType(
				ForumService.SERVICE_NAME,
				ThreadMessage.RESOURCE_NAME, "create"), ""));
		//
		newMsgId = tm.getId();
		logIt(tm.toString());
	    } else {
		throw new ForumServiceException(
			"No valid answer from the WS.Check logs.");
	    }
	    return newMsgId;
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to create the thread message at path "
		    + parentPath, e);
	    throw new ForumServiceException(
		    "Unable to create the thread message at path " + parentPath,
		    e);
	}
    }

    @Override
    public String createMessage(String path, ThreadMessage message,
	    String isReply) throws ForumServiceException {

	logIt("createThreadMessage(...) called");
	logger.debug("params : path=" + path + " isReply: " + message);
	String newMsgId = null;
	try {
	    // check supplied values
	    checkThreadValues(message.getName(), message.getForumId(), message
		    .getMessageBody(), message.getDatePosted());
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new ForumServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    String parentPath = PathHelper.getParentPath(path);
	    pep.checkSecurity(caller, parentPath, "create");
	    //
	    String parentId = "";
	    boolean isReplyBoolean = Boolean.valueOf(isReply);
	    logIt("Is Reply: " + isReplyBoolean);
	    if (isReplyBoolean) {
		try {
		    // Check if parent is thread
		    ThreadMessage parentMsg = readThreadMessage(parentPath);
		    parentId = parentMsg.getId();
		    logIt("Succefully read parent thread. Id: " + parentId);
		} catch (Exception e) {
		    throw new ForumServiceException(
			    "Path is not valid for replying to a thread.Please check "
				    + path);
		}
	    }
	    // Call Mermig WS
	    HashMap<String, String> values = forumWS.createThreadMessage(
		    message.getForumId(), parentId, message.getName(), message
			    .getMessageBody(), isReplyBoolean);
	    //
	    if (values != null && !values.isEmpty()) {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
		    throw new ForumServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}
		// We persist only id,path,name,parentId,forumId and author
		String newId = (String) values.get("messageId");
		ThreadMessage tm = new ThreadMessage();
		tm.setId(newId);
		tm.setResourcePath(path);
		tm.setName(message.getName());
		tm.setParentId(parentId);
		tm.setForumId(message.getForumId());
		// FIXME we need author not profile path.
		tm.setAuthor(caller);
		em.persist(tm);
		// Bind the entity with the path and the identifier
		logIt("Bind the " + tm.getFactoryResourceIdentifier() + " to "
			+ path);
		binding.bind(tm.getFactoryResourceIdentifier(), path);
		binding.setProperty(path,
			FactoryResourceProperty.CREATION_TIMESTAMP, ""
				+ System.currentTimeMillis());
		binding.setProperty(path,
			FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, ""
				+ System.currentTimeMillis());
		binding.setProperty(path, FactoryResourceProperty.AUTHOR,
			caller);
		// Create policy (owner)
		String policyId = UUID.randomUUID().toString();
		pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(
			policyId, caller, path));
		binding
			.setProperty(path, FactoryResourceProperty.OWNER,
				caller);
		binding.setProperty(path, FactoryResourceProperty.POLICY_ID,
			policyId);
		// notify
		notification.throwEvent(new Event(path, caller,
			ThreadMessage.RESOURCE_NAME, Event.buildEventType(
				ForumService.SERVICE_NAME,
				ThreadMessage.RESOURCE_NAME, "create"), ""));
		//
		newMsgId = tm.getId();
		logIt(tm.toString());
	    } else {
		throw new ForumServiceException(
			"No valid answer from the WS.Check logs.");
	    }
	    return newMsgId;
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to create the thread message at path " + path,
		    e);
	    throw new ForumServiceException(
		    "Unable to create the thread message at path " + path, e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public ThreadMessage readThreadMessage(String path)
	    throws ForumServiceException {
	logIt("readThreadMessage(...) called");
	logger.debug("params : path=" + path);
	ThreadMessage msg = null;
	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new ForumServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, ThreadMessage.RESOURCE_NAME);
	    // sFind the entity
	    msg = em.find(ThreadMessage.class, identifier.getId());
	    if (msg == null) {
		throw new ForumServiceException(
			"Unable to find a message for id " + identifier.getId());
	    }
	    // Call the WS to retrieve values that are not stored in
	    // factory
	    HashMap<String, Object> values = forumWS.readThreadMessage(msg
		    .getForumId(), msg.getId());
	    if (values != null && !values.isEmpty()) {
		String code = (String) values.get("statusCode");
		String msgTxt = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msgTxt);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)
			|| values.get("ThreadMessage") == null) {
		    throw new ForumServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msgTxt);
		}
		// FIXME The path is missing in the replies list....Use Browser
		// method
		MessageDTO msgWS = (MessageDTO) values.get("ThreadMessage");
		if (msgWS != null) {
		    // We don't persist the following
		    // (messageBody,datePosted,numReplies,messageReplies)
		    // so we retrieve values from WS
		    msg.setMessageBody(msgWS.getMessageBody());
		    msg.setDatePosted(msgWS.getDatePosted());
		    msg.setNumReplies(msgWS.getNumReplies());
		    if (msgWS.getMessageReplies() != null) {
			msg.setMessageReplies(convertHashMap(path, msgWS
				.getMessageReplies()));
		    }
		    // notify
		    notification.throwEvent(new Event(path, caller,
			    ThreadMessage.RESOURCE_NAME, Event.buildEventType(
				    ForumService.SERVICE_NAME,
				    ThreadMessage.RESOURCE_NAME, "read"), ""));
		    logIt(msg.toString());
		} else {
		    throw new ForumServiceException("Error in WS response.");
		}
	    } else {
		throw new ForumServiceException(
			"Error in recieving extra details from WS.");
	    }
	    return msg;
	} catch (Exception e) {
	    logger.error("unable to read the message at path " + path, e);
	    throw new ForumServiceException(
		    "unable to read the message at path " + path, e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteThreadMessage(String path) throws ForumServiceException {
	logIt("deleteThreadMessage(...) called");
	logger.debug("params : path=" + path);

	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new ForumServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "delete");
	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, ThreadMessage.RESOURCE_NAME);
	    // Find the entity
	    ThreadMessage msg = em
		    .find(ThreadMessage.class, identifier.getId());
	    if (msg == null) {
		throw new ForumServiceException("unable to find a msg for id "
			+ identifier.getId());
	    }
	    if (!browser.hasChildren(path)) {
		HashMap<String, String> values = forumWS
			.deleteThreadMessage(msg.getId());
		if (values != null && !values.isEmpty()) {
		    String code = (String) values.get("statusCode");
		    String msgTxt = (String) values.get("statusMessage");
		    logIt("Message Code:" + code + " Message: " + msgTxt);
		    if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
			throw new ForumServiceException(
				"Error code recieved from the WS." + " Code"
					+ code + " Message:" + msgTxt);
		    }
		    em.remove(msg);
		    // Delete the policy and unbind the resource from this path
		    String policyId = binding.getProperty(path,
			    FactoryResourceProperty.POLICY_ID, false);
		    pap.deletePolicy(policyId);
		    binding.unbind(path);
		    // notify
		    notification
			    .throwEvent(new Event(path, caller,
				    ThreadMessage.RESOURCE_NAME,
				    Event.buildEventType(
					    ForumService.SERVICE_NAME,
					    ThreadMessage.RESOURCE_NAME,
					    "delete"), ""));
		} else {
		    throw new ForumServiceException(
			    "No valid answer from the WS.Check logs.");
		}
	    } else {
		throw new ForumServiceException(
			"Thread message has children and cannot be deleted.");
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to delete the message at path " + path, e);
	    throw new ForumServiceException(
		    "Unable to delete the message at path " + path, e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Forum[] getForums(String path) throws ForumServiceException {
	Forum[] items = null;
	logIt("get forums " + path);
	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new ForumServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    // Check if given path is valid
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    // FIXME change restriction of parent. Forum should be created under
	    // any path
	    if (identifier != null
		    && (identifier.getService().equals(SERVICE_NAME)
			    || identifier.getService().equals(
				    CoreService.SERVICE_NAME) || identifier
			    .getService().equals(DocumentService.SERVICE_NAME))) {
		String servicePattern = ForumService.SERVICE_NAME;
		String typePattern = Forum.RESOURCE_NAME;
		String[] forumChildren = browser.listChildrenOfType(path,
			servicePattern, typePattern);
		if (forumChildren != null && forumChildren.length > 0) {
		    logIt("Number of forums under " + path + " : "
			    + forumChildren.length);
		    items = new Forum[forumChildren.length];
		    for (int i = 0; i < forumChildren.length; i++) {
			logIt("child #" + i + ". " + forumChildren[i]);
			items[i] = readForum(forumChildren[i]);
			logIt(items[i].toString());
		    }
		}
	    } else {
		throw new ForumServiceException("Given path is not valid.");
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to retrieve the forums at path " + path, e);
	    throw new ForumServiceException(
		    "Unable to retrieve the forums at path " + path, e);
	}
	return items;
    }

    @Override
    public Forum[] getForumsByStatus(String path, String status)
	    throws ForumServiceException {
	Forum[] items = null;
	logIt("get forums under" + path + " with status " + status);
	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new ForumServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    // Check if given path is valid
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    // FIXME change restriction of parent. Forum should be created under
	    // any path
	    if (identifier != null
		    && (identifier.getService().equals(SERVICE_NAME)
			    || identifier.getService().equals(
				    CoreService.SERVICE_NAME) || identifier
			    .getService().equals(DocumentService.SERVICE_NAME))) {
		String servicePattern = ForumService.SERVICE_NAME;
		String typePattern = Forum.RESOURCE_NAME;
		String[] forumChildren = browser.listChildrenOfType(path,
			servicePattern, typePattern);
		if (forumChildren != null && forumChildren.length > 0) {
		    logIt("Number of forums under " + path + " : "
			    + forumChildren.length);
		    Vector<Forum> forumsVector = new Vector<Forum>();
		    for (int i = 0; i < forumChildren.length; i++) {
			logIt("child #" + i + ". " + forumChildren[i]);
			Forum tmpForum = readForum(forumChildren[i]);
			if (tmpForum != null
				&& tmpForum.getStatus().equals(status)) {
			    forumsVector.add(tmpForum);
			}
		    }
		    if (forumsVector != null && forumsVector.size() > 0) {
			items = forumsVector.toArray(new Forum[forumsVector
				.size()]);
		    }
		}
	    } else {
		throw new ForumServiceException("Given path is not valid.");
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to retrieve the forums at path " + path, e);
	    throw new ForumServiceException(
		    "Unable to retrieve the forums at path " + path, e);
	}
	return items;
    }

    @Override
    public ThreadMessage[] getForumMessages(String path)
	    throws ForumServiceException {
	logIt("get forummessages " + path);
	ThreadMessage[] listofThreads = null;
	try {
	    // Forum givenForum = readForum(path);
	    // if(givenForum!=null){
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new ForumServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    // Check if given path is valid forum
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, Forum.RESOURCE_NAME);
	    // Get messages
	    String servicePattern = ForumService.SERVICE_NAME;
	    String typePattern = ThreadMessage.RESOURCE_NAME;
	    String[] threadsArray = browser.listChildrenOfType(path,
		    servicePattern, typePattern);
	    if (threadsArray != null && threadsArray.length > 0) {
		listofThreads = new ThreadMessage[threadsArray.length];
		for (int i = 0; i < threadsArray.length; i++) {
		    logIt("child #" + i + ". " + threadsArray[i]);
		    listofThreads[i] = readThreadMessage(threadsArray[i]);
		    logIt(listofThreads[i].toString());
		}
	    }
	    //
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to retrieve message for the forum at path "
		    + path, e);
	    throw new ForumServiceException(
		    "Unable to retrieve message for the forum at path " + path,
		    e);
	}
	return listofThreads;
    }

    @Override
    public ThreadMessage[] getThreadReplies(String path)
	    throws ForumServiceException {
	logIt("get thread replies " + path);
	ThreadMessage[] listofThreads = null;
	try {
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new ForumServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    // Check if given path is valid threadMessage
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, ThreadMessage.RESOURCE_NAME);
	    // Get messages
	    String servicePattern = ForumService.SERVICE_NAME;
	    String typePattern = ThreadMessage.RESOURCE_NAME;
	    String[] threadsArray = browser.listChildrenOfType(path,
		    servicePattern, typePattern);
	    if (threadsArray != null && threadsArray.length > 0) {
		Vector<ThreadMessage> threadsVector = new Vector<ThreadMessage>();

		for (int i = 0; i < threadsArray.length; i++) {

		    logIt("child #" + i + ". " + threadsArray[i]);
		    threadsVector.add(readThreadMessage(threadsArray[i]));
		    threadsVector = getChildren(threadsVector, threadsArray[i]);
		}
		listofThreads = threadsVector
			.toArray(new ThreadMessage[threadsVector.size()]);
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to retrieve message for the forum at path "
		    + path, e);
	    throw new ForumServiceException(
		    "Unable to retrieve message for the forum at path " + path,
		    e);
	}
	return listofThreads;
    }

    private Vector<ThreadMessage> getChildren(
	    Vector<ThreadMessage> threadsVector, String path) throws Exception {
	String servicePattern = ForumService.SERVICE_NAME;
	String typePattern = ThreadMessage.RESOURCE_NAME;
	String[] childs = browser.listChildrenOfType(path, servicePattern,
		typePattern);
	if (childs != null && childs.length > 0) {
	    for (int i = 0; i < childs.length; i++) {

		logIt("child #" + i + ". " + childs[i]);
		threadsVector.add(readThreadMessage(childs[i]));
		threadsVector = getChildren(threadsVector, childs[i]);
	    }
	}
	return threadsVector;
    }

    @Override
    public void attachDocumentToForum(String path, String documentPath)
	    throws ForumServiceException {
	logIt("attach document " + documentPath + " at " + path);
	try {
	    // Get forum
	    Forum forum = readForum(path);
	    if (forum != null) {
		// Get the document
		Document doc = documentService.readDocument(documentPath);
		if (doc != null) {
		    List<String> documents = new ArrayList<String>();
		    documents.add(doc.getId());
		    // Update the forum
		    HashMap<String, String> values = forumWS
			    .attachDocumentsToForum(forum.getId(), documents);
		    if (values != null && !values.isEmpty()) {
			String code = (String) values.get("statusCode");
			String msgTxt = (String) values.get("statusMessage");
			logIt("Message Code:" + code + " Message: " + msgTxt);
			if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
			    throw new ForumServiceException(
				    "Error code recieved from the WS."
					    + " Code" + code + " Message:"
					    + msgTxt);
			}
			logIt("Attached succesfully document " + doc.getName()
				+ " to forum " + forum.getName());
		    } else {
			throw new ForumServiceException(
				"No valid answer from the WS.Check logs.");
		    }
		} else {
		    throw new ForumServiceException(
			    "Cannot read document from given path "
				    + documentPath);
		}
	    } else {
		throw new ForumServiceException(
			"Cannot read forum from given path " + path);
	    }

	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to attach document for the forum " + path, e);
	    throw new ForumServiceException(
		    "Unable to attach document for the forum " + path, e);
	}
    }

    // @Override
    // public void attachDocumentToThread(String path, String documentPath)
    // throws ForumServiceException
    // {
    //
    //	
    // }

    @Override
    public Document[] getForumAttachments(String path)
	    throws ForumServiceException {
	logIt("get attached documents " + path);
	Document[] listDoc = null;
	try {
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new ForumServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    // Check if given path is valid Forum
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, Forum.RESOURCE_NAME);
	    // Get attachments
	    listDoc = getAttachments(path);
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error(
		    "Unable to retrieve attached documents for the forum at path "
			    + path, e);
	    throw new ForumServiceException(
		    "Unable to retrieve documents for the forum at path "
			    + path, e);
	}
	return listDoc;
    }

    @Override
    public Document[] getThreadAttachments(String path)
	    throws ForumServiceException {
	logIt("get attached documents " + path);
	Document[] listDoc = null;
	try {
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new ForumServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    // Check if given path is valid ThreadMessage
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, ThreadMessage.RESOURCE_NAME);
	    // Get attachments
	    listDoc = getAttachments(path);
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error(
		    "Unable to retrieve attached documents for the thread message at path "
			    + path, e);
	    throw new ForumServiceException(
		    "Unable to retrieve documents for the thread message at path "
			    + path, e);
	}
	return listDoc;
    }

    private Document[] getAttachments(String path) throws Exception {
	Document[] listDoc = null;
	String servicePattern = DocumentService.SERVICE_NAME;
	String typePattern = Document.RESOURCE_NAME;
	String[] docsArray = browser.listChildrenOfType(path, servicePattern,
		typePattern);
	if (docsArray != null && docsArray.length > 0) {
	    Vector<Document> docVector = new Vector<Document>();

	    for (int i = 0; i < docsArray.length; i++) {

		logIt("child #" + i + ". " + docsArray[i]);
		Document doc = documentService
			.readDocumentProperties(docsArray[i]);
		if (doc != null) {
		    docVector.add(doc);
		}
	    }
	    listDoc = docVector.toArray(new Document[docVector.size()]);
	}
	return listDoc;
    }

    @PersistenceContext(unitName = "ForumServiceBean")
    public void setEntityManager(EntityManager em) {
	this.em = em;
    }

    public EntityManager getEntityManager() {
	return this.em;
    }

    @Resource
    public void setSessionContext(SessionContext ctx) {
	this.ctx = ctx;
    }

    public SessionContext getSessionContext() {
	return this.ctx;
    }

    // @EJB(name = "BindingService")
    @EJB
    public void setBindingService(BindingService binding) {
	this.binding = binding;
    }

    public BindingService getBindingService() {
	return this.binding;
    }

    // @EJB(name = "PEPService")
    @EJB
    public void setPEPService(PEPService pep) {
	this.pep = pep;
    }

    public PEPService getPEPService() {
	return this.pep;
    }

    // @EJB(name = "PAPService")
    @EJB
    public void setPAPService(PAPService pap) {
	this.pap = pap;
    }

    public PAPService getPAPService() {
	return this.pap;
    }

    // @EJB(name = "NotificationService")
    @EJB
    public void setNotificationService(NotificationService notification) {
	this.notification = notification;
    }

    public NotificationService getNotificationService() {
	return this.notification;
    }

    // @EJB(name = "MembershipService")
    @EJB
    public void setMembershipService(MembershipService membership) {
	this.membership = membership;
    }

    public MembershipService getMembershipService() {
	return this.membership;
    }

    public BrowserService getBrowser() {
	return browser;
    }

    // @EJB(name = "BrowserService")
    @EJB
    public void setBrowser(BrowserService browser) {
	this.browser = browser;
    }

    public CoreService getCore() {
	return core;
    }

    // @EJB(name = "CoreService")
    @EJB
    public void setCore(CoreService core) {
	this.core = core;
    }

    public ForumWService getForumWS() {
	return forumWS;
    }

    @EJB(name = "ForumWService")
    public void setForumWS(ForumWService forumWS) {
	this.forumWS = forumWS;
    }

    public DocumentService getDocumentService() {
	return documentService;
    }

    // @EJB(name = "DocumentService")
    @EJB
    public void setDocumentService(DocumentService documentService) {
	this.documentService = documentService;
    }

    private void checkResourceType(FactoryResourceIdentifier identifier,
	    String resourceType) throws MembershipServiceException {
	if (!identifier.getService().equals(getServiceName())) {
	    throw new MembershipServiceException("resource identifier "
		    + identifier + " does not refer to service "
		    + getServiceName());
	}
	if (!identifier.getType().equals(resourceType)) {
	    throw new MembershipServiceException("resource identifier "
		    + identifier + " does not refer to a resource of type "
		    + resourceType);
	}
    }

    @Override
    public String[] getResourceTypeList() {
	return RESOURCE_TYPE_LIST;
    }

    @Override
    public String getServiceName() {
	return SERVICE_NAME;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FactoryResource findResource(String path) throws FactoryException {
	logIt("findResource(...) called");
	logIt("params : path=" + path);

	try {
	    FactoryResourceIdentifier identifier = binding.lookup(path);

	    if (!identifier.getService().equals(SERVICE_NAME)) {
		throw new CoreServiceException("Resource " + identifier
			+ " is not managed by " + SERVICE_NAME);
	    }

	    if (identifier.getType().equals(Forum.RESOURCE_NAME)) {
		return readForum(path);
	    } else if (identifier.getType().equals(ThreadMessage.RESOURCE_NAME)) {
		return readThreadMessage(path);
	    }

	    throw new CoreServiceException("Resource " + identifier
		    + " is not managed by " + SERVICE_NAME);

	} catch (Exception e) {
	    logger.error("unable to find the resource at path " + path, e);
	    throw new CoreServiceException(
		    "unable to find the resource at path " + path, e);
	}
    }

    private void checkThreadValues(String name, String forumId,
	    String messageBody, String datePosted) throws ForumServiceException {
	if (name == null || name == "") {
	    throw new ForumServiceException("Name is mandatory.");
	}
	if (forumId == null || forumId == "") {
	    throw new ForumServiceException("Forum is mandatory.");
	}
	if (messageBody == null || messageBody == "") {
	    throw new ForumServiceException("Message body is mandatory.");
	}
	if (datePosted == null || datePosted == "") {
	    throw new ForumServiceException("Date posted is mandatory.");
	}

    }

    private HashMap<String, ThreadMessage> convertHashMap(String path,
	    HashMap<String, MessageDTO> msgs) {
	HashMap<String, ThreadMessage> msgsMap = null;
	if (msgs != null) {
	    msgsMap = new HashMap<String, ThreadMessage>();
	    Iterator it = msgs.keySet().iterator();
	    while (it.hasNext()) {
		String key = (String) it.next();
		MessageDTO val = (MessageDTO) msgs.get(key);
		ThreadMessage msg = convertDTO(val);
		// FIXME we will not this after browser.list is fixed.
		String msgPath = path + "/"
			+ CollaborationUtils.normalizeForPath(msg.getName());
		msg.setResourcePath(msgPath);
		msgsMap.put(key, msg);
	    }

	}
	return msgsMap;
    }

    private ThreadMessage convertDTO(MessageDTO msgDTO) {
	ThreadMessage msg = null;
	if (msgDTO != null) {
	    msg = new ThreadMessage();
	    msg.setAuthor(msgDTO.getAuthor());
	    msg.setDatePosted(msgDTO.getDatePosted());
	    msg.setForumId(msgDTO.getForumId());
	    msg.setId(msgDTO.getId());
	    msg.setMessageBody(msgDTO.getMessageBody());
	    msg.setName(msgDTO.getName());
	    msg.setNumReplies(msgDTO.getNumReplies());
	    msg.setParentId(msgDTO.getParentId());
	}
	return msg;
    }

    private void logIt(String message) {
	if (logger.isInfoEnabled()) {
	    logger.info(message);
	}
    }

}
