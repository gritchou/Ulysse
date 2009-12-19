package org.qualipso.factory.collaboration.forum;

import java.util.HashMap;
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
import org.qualipso.factory.collaboration.beans.AttachmentDetails;
import org.qualipso.factory.collaboration.beans.ThreadMessageDetails;
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

/**
 * The Class ForumServiceBean.
 */
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

    /** The logger. */
    private static Log logger = LogFactory.getLog(ForumServiceBean.class);

    /** The binding. */
    private BindingService binding;
    
    /** The pep. */
    private PEPService pep;
    
    /** The pap. */
    private PAPService pap;
    
    /** The notification. */
    private NotificationService notification;
    
    /** The membership. */
    private MembershipService membership;
    //
    /** The browser. */
    private BrowserService browser;
    
    /** The core. */
    private CoreService core;
    //
    /** The ctx. */
    private SessionContext ctx;
    
    /** The em. */
    private EntityManager em;
    //
    /** The forum ws. */
    private ForumWService forumWS;
    
    /** The document service. */
    private DocumentService documentService;

    /**
     * Instantiates a new forum service bean.
     */
    public ForumServiceBean() {
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#createForum(java.lang.String, java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String createForum(String parentPath, String name)
	    throws ForumServiceException {
	String path = null;
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
		path = PathHelper.normalize(parentPath + "/" + forumId);
		logIt("path:" + path);
		// Save the new entity in the factory and bind it
		// We persist only id,path,name and status(defaults to active)
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
	    } else {
		throw new ForumServiceException(
			"No valid answer from the WS.Check logs.");
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to create the forum at path " + parentPath, e);
	    throw new ForumServiceException(
		    "Unable to create the forum at path " + parentPath, e);
	}
	return path;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#readForumProperties(java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Forum readForumProperties(String path) throws ForumServiceException {
	logIt("Read forum properties " + path);
	Forum forum = null;
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
	    forum = em.find(Forum.class, identifier.getId());
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
	} catch (Exception e) {
	    logger.error("Unable to read the forum at path " + path, e);
	    throw new ForumServiceException("Unable to read the forum at path "
		    + path, e);
	}
	return forum;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#readForum(java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Forum readForum(String path) throws ForumServiceException {
	logIt("Read forum " + path);
	Forum forum = null;
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
	    forum = em.find(Forum.class, identifier.getId());
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
		// Get messages
		String servicePattern = ForumService.SERVICE_NAME;
		String typePattern = ThreadMessage.RESOURCE_NAME;
		String[] threadsArray = browser.listChildrenOfType(path,
			servicePattern, typePattern);
		if (threadsArray != null && threadsArray.length > 0) {
		    ThreadMessage[] listofThreads = new ThreadMessage[threadsArray.length];
		    for (int i = 0; i < threadsArray.length; i++) {
			logIt("child #" + i + ". " + threadsArray[i]);
			// Get properties only
			listofThreads[i] = readThreadMessageProperties(threadsArray[i]);
			logIt(listofThreads[i].toString());
		    }
		    forum.setMessages(listofThreads);
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
	} catch (Exception e) {
	    logger.error("Unable to read the forum at path " + path, e);
	    throw new ForumServiceException("Unable to read the forum at path "
		    + path, e);
	}
	return forum;
    }

    
    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#updateForum(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateForum(String path, String name, String date)
	    throws ForumServiceException {
	
	updateForumWithAttachments(path, name, date, null);
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#updateForumWithAttachments(java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateForumWithAttachments(String path, String name, String date,
	    String[] documentPaths) throws ForumServiceException {
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
	    String[] documentIds = null;
	    AttachmentDetails[] attArray = null;
	    if (documentPaths != null && documentPaths.length > 0) {
		documentIds = new String[documentPaths.length];
		attArray = new AttachmentDetails[documentPaths.length];
		for (int i = 0; i < documentPaths.length; i++) {
		    // Get the document
		    Document doc = documentService.readDocumentProperties(documentPaths[i]);
		    if (doc != null) {
			documentIds[i] = doc.getId();
			AttachmentDetails attachment = new AttachmentDetails();
			attachment.setId(doc.getId());
			attachment.setName(doc.getName());
			attachment.setPath(documentPaths[i]);
			attachment.setResourceId(doc.getResourceId());
			attArray[i] = attachment;
		    } else {
			throw new ForumServiceException(
				"Cannot read document from given path "
					+ documentPaths[i]);
		    }
		}
	    }
	    HashMap<String, String> values = forumWS.updateForum(forum.getId(),
		    name, date, documentIds);
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
		forum.setAttachments(attArray);
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

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#deleteForum(java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#changeForumStatus(java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#createThreadMessage(java.lang.String, org.qualipso.factory.collaboration.beans.ThreadMessageDetails)
     */
    @Override
    public String createThreadMessage(String parentPath,
	    ThreadMessageDetails message) throws ForumServiceException {
	return createThreadMessageWithAttachments(parentPath, message, null);
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#createThreadMessageWithAttachments(java.lang.String, org.qualipso.factory.collaboration.beans.ThreadMessageDetails, java.lang.String[])
     */
    @Override
    public String createThreadMessageWithAttachments(String parentPath,
	    ThreadMessageDetails message, String[] documentPaths)
	    throws ForumServiceException {
	String path = null;
	logIt("createThreadMessage(...) called");
	logger.debug("params : path=" + parentPath + " isReply: "
		+ message.getIsReply());
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
	    pep.checkSecurity(caller, parentPath, "create");
	    //
	    String parentId = "";
	    if (message.getIsReply()) {
		try {
		    // Check if parent is thread
		    ThreadMessage parentMsg = readThreadMessageProperties(message
			    .getName());
		    parentId = parentMsg.getId();
		    logIt("Succefully read parent thread. Id: " + parentId);
		} catch (Exception e) {
		    throw new ForumServiceException(
			    "Path is not valid for replying to a thread.Please check "
				    + path);
		}
	    }

	    MessageDTO messageDTO = new MessageDTO();
	    messageDTO.setForumId(message.getForumId());
	    messageDTO.setParentId(parentId);
	    messageDTO.setName(message.getName());
	    messageDTO.setMessageBody(message.getMessageBody());
	    messageDTO.setReply(message.getIsReply());
	    String[] documentIds = null;
	    AttachmentDetails[] attdetArray = null;
	    if (documentPaths != null && documentPaths.length > 0) {
		// Get id for document Paths
		documentIds = new String[documentPaths.length];
		attdetArray = new AttachmentDetails[documentPaths.length];
		for (int i = 0; i < documentPaths.length; i++) {

		    // Get the document
		    Document doc = documentService
			    .readDocumentProperties(documentPaths[i]);
		    if (doc != null) {
			documentIds[i] = doc.getId();
			AttachmentDetails attachment = new AttachmentDetails();
			attachment.setId(doc.getId());
			attachment.setName(doc.getName());
			attachment.setPath(documentPaths[i]);
			attachment.setResourceId(doc.getResourceId());
			attdetArray[i] = attachment;
		    } else {
			throw new ForumServiceException(
				"Cannot read document from given path "
					+ documentPaths);
		    }
		}
	    }
	    // Call Mermig WS
	    HashMap<String, String> values = forumWS.createThreadMessage(
		    messageDTO, documentIds);
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
		// Create path based on parent path and the id that is returned
		// by the Mermig WS
		path = PathHelper.normalize(parentPath + "/" + newId);
		ThreadMessage tm = new ThreadMessage();
		tm.setId(newId);
		tm.setResourcePath(path);
		tm.setName(message.getName());
		tm.setParentId(parentId);
		tm.setForumId(message.getForumId());
		tm.setAttachments(attdetArray);
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
		logIt(tm.toString());
	    } else {
		throw new ForumServiceException(
			"No valid answer from the WS.Check logs.");
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to create the thread message under "
		    + parentPath, e);
	    throw new ForumServiceException(
		    "Unable to create the thread message under " + parentPath,
		    e);
	}
	return path;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#readThreadMessage(java.lang.String)
     */
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
		MessageDTO msgWS = (MessageDTO) values.get("ThreadMessage");
		if (msgWS != null) {
		    // We don't persist the following
		    // (messageBody,datePosted,numReplies,messageReplies)
		    // so we retrieve values from WS
		    msg.setMessageBody(msgWS.getMessageBody());
		    msg.setDatePosted(msgWS.getDatePosted());
		    // Get children threads
		    String servicePattern = ForumService.SERVICE_NAME;
		    String typePattern = ThreadMessage.RESOURCE_NAME;
		    String[] threadsArray = browser.listChildrenOfType(path,
			    servicePattern, typePattern);
		    if (threadsArray != null && threadsArray.length > 0) {
			ThreadMessage[] listofThreads = new ThreadMessage[threadsArray.length];
			for (int i = 0; i < threadsArray.length; i++) {
			    logIt("child #" + i + ". " + threadsArray[i]);
			    listofThreads[i] = readThreadMessage(threadsArray[i]);
			    logIt(listofThreads[i].toString());
			}
			msg.setMessages(listofThreads);
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

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#readThreadMessageProperties(java.lang.String)
     */
    @Override
    public ThreadMessage readThreadMessageProperties(String path)
	    throws ForumServiceException {
	logIt("readThreadMessageProperties(...) called");
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
		// method
		MessageDTO msgWS = (MessageDTO) values.get("ThreadMessage");
		if (msgWS != null) {
		    // We don't persist the following
		    // (messageBody,datePosted,numReplies,messageReplies)
		    // so we retrieve values from WS
		    msg.setMessageBody(msgWS.getMessageBody());
		    msg.setDatePosted(msgWS.getDatePosted());
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
		    "unable to read the message properties at path " + path, e);
	}
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#deleteThreadMessage(java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#getForums(java.lang.String)
     */
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
	    if (identifier != null) {
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
			items[i] = readForumProperties(forumChildren[i]);
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

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#getForumsByStatus(java.lang.String, java.lang.String)
     */
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
	    if (identifier != null) {
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
			Forum tmpForum = readForumProperties(forumChildren[i]);
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

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#getForumMessages(java.lang.String)
     */
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
		    listofThreads[i] = readThreadMessageProperties(threadsArray[i]);
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

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#getThreadReplies(java.lang.String)
     */
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

    /**
     * Gets the children.
     * 
     * @param threadsVector the threads vector
     * @param path the path
     * 
     * @return the children
     * 
     * @throws Exception the exception
     */
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
    
    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.forum.ForumService#attachDocumentsToForum(java.lang.String, java.lang.String[])
     */
    @Override
    public void attachDocumentsToForum(String path, String[] documentPaths)
	    throws ForumServiceException {
	logIt("attach documents at " + path);
	try {
	    // Get forum
	    Forum forum = readForumProperties(path);
	    if (forum != null) {
		if (documentPaths != null && documentPaths.length > 0) {
		    String[] documentIds = new String[documentPaths.length];
		    for (int i = 0; i < documentPaths.length; i++) {

			// Get the document
			Document doc = documentService
				.readDocumentProperties(documentPaths[i]);
			if (doc != null) {
			    documentIds[i] = doc.getId();
			} else {
			    throw new ForumServiceException(
				    "Cannot read document from given path "
					    + documentPaths[i]);
			}
		    }
		    // Update the forum
		    HashMap<String, String> values = forumWS
			    .attachDocumentsToForum(forum.getId(), documentIds);
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
			logIt("Attached succesfully " + documentIds.length
				+ " document(s) to forum " + forum.getName());
		    } else {
			throw new ForumServiceException(
				"No valid answer from the WS.Check logs.");
		    }
		} else {
		    throw new ForumServiceException("Nothing to attach.");
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

    /**
     * Sets the entity manager.
     * 
     * @param em the new entity manager
     */
    @PersistenceContext(unitName = "ForumServiceBean")
    public void setEntityManager(EntityManager em) {
	this.em = em;
    }

    /**
     * Gets the entity manager.
     * 
     * @return the entity manager
     */
    public EntityManager getEntityManager() {
	return this.em;
    }

    /**
     * Sets the session context.
     * 
     * @param ctx the new session context
     */
    @Resource
    public void setSessionContext(SessionContext ctx) {
	this.ctx = ctx;
    }

    /**
     * Gets the session context.
     * 
     * @return the session context
     */
    public SessionContext getSessionContext() {
	return this.ctx;
    }

    // @EJB(name = "BindingService")
    /**
     * Sets the binding service.
     * 
     * @param binding the new binding service
     */
    @EJB
    public void setBindingService(BindingService binding) {
	this.binding = binding;
    }

    /**
     * Gets the binding service.
     * 
     * @return the binding service
     */
    public BindingService getBindingService() {
	return this.binding;
    }

    // @EJB(name = "PEPService")
    /**
     * Sets the pEP service.
     * 
     * @param pep the new pEP service
     */
    @EJB
    public void setPEPService(PEPService pep) {
	this.pep = pep;
    }

    /**
     * Gets the pEP service.
     * 
     * @return the pEP service
     */
    public PEPService getPEPService() {
	return this.pep;
    }

    // @EJB(name = "PAPService")
    /**
     * Sets the pAP service.
     * 
     * @param pap the new pAP service
     */
    @EJB
    public void setPAPService(PAPService pap) {
	this.pap = pap;
    }

    /**
     * Gets the pAP service.
     * 
     * @return the pAP service
     */
    public PAPService getPAPService() {
	return this.pap;
    }

    // @EJB(name = "NotificationService")
    /**
     * Sets the notification service.
     * 
     * @param notification the new notification service
     */
    @EJB
    public void setNotificationService(NotificationService notification) {
	this.notification = notification;
    }

    /**
     * Gets the notification service.
     * 
     * @return the notification service
     */
    public NotificationService getNotificationService() {
	return this.notification;
    }

    // @EJB(name = "MembershipService")
    /**
     * Sets the membership service.
     * 
     * @param membership the new membership service
     */
    @EJB
    public void setMembershipService(MembershipService membership) {
	this.membership = membership;
    }

    /**
     * Gets the membership service.
     * 
     * @return the membership service
     */
    public MembershipService getMembershipService() {
	return this.membership;
    }

    /**
     * Gets the browser.
     * 
     * @return the browser
     */
    public BrowserService getBrowser() {
	return browser;
    }

    // @EJB(name = "BrowserService")
    /**
     * Sets the browser.
     * 
     * @param browser the new browser
     */
    @EJB
    public void setBrowser(BrowserService browser) {
	this.browser = browser;
    }

    /**
     * Gets the core.
     * 
     * @return the core
     */
    public CoreService getCore() {
	return core;
    }

    // @EJB(name = "CoreService")
    /**
     * Sets the core.
     * 
     * @param core the new core
     */
    @EJB
    public void setCore(CoreService core) {
	this.core = core;
    }

    /**
     * Gets the forum ws.
     * 
     * @return the forum ws
     */
    public ForumWService getForumWS() {
	return forumWS;
    }

    /**
     * Sets the forum ws.
     * 
     * @param forumWS the new forum ws
     */
    @EJB(name = "ForumWService")
    public void setForumWS(ForumWService forumWS) {
	this.forumWS = forumWS;
    }

    /**
     * Gets the document service.
     * 
     * @return the document service
     */
    public DocumentService getDocumentService() {
	return documentService;
    }

    // @EJB(name = "DocumentService")
    /**
     * Sets the document service.
     * 
     * @param documentService the new document service
     */
    @EJB
    public void setDocumentService(DocumentService documentService) {
	this.documentService = documentService;
    }

    /**
     * Check resource type.
     * 
     * @param identifier the identifier
     * @param resourceType the resource type
     * 
     * @throws MembershipServiceException the membership service exception
     */
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

    /* (non-Javadoc)
     * @see org.qualipso.factory.FactoryService#getResourceTypeList()
     */
    @Override
    public String[] getResourceTypeList() {
	return RESOURCE_TYPE_LIST;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.FactoryService#getServiceName()
     */
    @Override
    public String getServiceName() {
	return SERVICE_NAME;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.FactoryService#findResource(java.lang.String)
     */
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

    /**
     * Check thread values.
     * 
     * @param name the name
     * @param forumId the forum id
     * @param messageBody the message body
     * @param datePosted the date posted
     * 
     * @throws ForumServiceException the forum service exception
     */
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

    /**
     * Log it.
     * 
     * @param message the message
     */
    private void logIt(String message) {
	if (logger.isInfoEnabled()) {
	    logger.info(message);
	}
    }

}
