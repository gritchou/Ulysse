package org.qualipso.factory.collaboration.ws;

import java.util.HashMap;

import javax.ejb.Stateless;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.collaboration.ws.beans.ForumDTO;
import org.qualipso.factory.collaboration.ws.beans.MessageDTO;

// TODO: Auto-generated Javadoc
/**
 * The Class ForumWSBean.
 */
@Stateless(name = "ForumWSBean", mappedName = "ForumWService")
public class ForumWSBean extends CollaborationWSUtils implements ForumWService {

    /**
     * Instantiates a new forum ws bean.
     */
    public ForumWSBean() {
    }

    /** The target epr. */
    private EndpointReference targetEPR = new EndpointReference(
	    CollaborationProperties.getInstance().MERMIG_ENDPOINT);

    /** The fac. */
    private static OMFactory fac = OMAbstractFactory.getOMFactory();

    /** The om ns. */
    private static OMNamespace omNs = fac.createOMNamespace(
	    CollaborationWSUtils.NAME_SPACE, "ns");

    /** The logger. */
    private static Log logger = LogFactory.getLog(ForumWSBean.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.ForumWService#createForum(java.
     * lang.String)
     */
    @Override
    public HashMap<String, String> createForum(String forumName)
	    throws Exception {
	HashMap<String, String> values = new HashMap<String, String>();
	OMElement payload = getCreateForumPayLoad(forumName);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:createForum");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	if (result == null) {
	    throw new Exception("No result recieved from WS.");
	}
	logger.info(result.toString());
	OMElement omStatusCode = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	values.put("statusCode", omStatusCode.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	if (omStatusCode.getText().equals(SUCCESS_CODE)) {
	    OMElement omForum = result.getFirstChildWithName(OMEUtils
		    .getQName("forumProperties"));
	    OMElement omNewId = omForum.getFirstChildWithName(OMEUtils
		    .getQName("forumID"));
	    values.put("forumId", omNewId.getText());
	}
	return values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.ForumWService#readForum(java.lang
     * .String)
     */
    @Override
    public HashMap<String, Object> readForum(String id) throws Exception {
	HashMap<String, Object> values = new HashMap<String, Object>();
	//
	OMElement payload = getReadForumPayLoad(id);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:getForumProperties");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result);
	values.put("result", result.toString());
	OMElement omStatusCode = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	values.put("statusCode", omStatusCode.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	//
	if (omStatusCode.getText().equals(SUCCESS_CODE)) {
	    ForumDTO forum = new ForumDTO();
	    forum.setId(id);
	    OMElement omForum = result.getFirstChildWithName(OMEUtils
		    .getQName("forumProperties"));
	    OMElement omTitle = omForum.getFirstChildWithName(OMEUtils
		    .getQName("title"));
	    forum.setName(omTitle.getText());
	    OMElement omDate = omForum.getFirstChildWithName(OMEUtils
		    .getQName("date"));
	    forum.setDate(omDate.getText());
	    OMElement omStatus = omForum.getFirstChildWithName(OMEUtils
		    .getQName("status"));
	    forum.setStatus(omStatus.getText());
	    // No use of messages(without path) for the factory
	    // OMElement omThreads = omForum.getFirstChildWithName(OMEUtils
	    // .getQName("messageThreads"));
	    // if (omThreads != null) {
	    // HashMap<String, MessageDTO> msgList = new HashMap<String,
	    // MessageDTO>();
	    // Iterator<OMElement> msgsIterator = omThreads
	    // .getChildrenWithName(OMEUtils.getQName("message"));
	    // while (msgsIterator.hasNext()) {
	    // OMElement msgEl = (OMElement) msgsIterator.next();
	    // MessageDTO tm = new MessageDTO();
	    // tm.setForumId(forum.getId());
	    // tm.setId(msgEl.getFirstChildWithName(
	    // OMEUtils.getQName("messageID")).getText());
	    // if (msgEl.getFirstChildWithName(OMEUtils
	    // .getQName("parentID")) != null) {
	    // tm.setParentId(msgEl.getFirstChildWithName(
	    // OMEUtils.getQName("parentID")).getText());
	    // }
	    // //
	    // tm.setMessageBody(msgEl.getFirstChildWithName(OMEUtils.getQName("messageBody")).getText());
	    // tm.setName(msgEl.getFirstChildWithName(
	    // OMEUtils.getQName("subject")).getText());
	    // tm.setDatePosted(msgEl.getFirstChildWithName(
	    // OMEUtils.getQName("datePosted")).getText());
	    // tm.setAuthor(msgEl.getFirstChildWithName(
	    // OMEUtils.getQName("author")).getText());
	    // tm.setNumReplies(msgEl.getFirstChildWithName(
	    // OMEUtils.getQName("messageReplies")).getText());
	    // msgList.put(tm.getId(), tm);
	    // logger.info(tm.toString());
	    //
	    // }
	    // forum.setMessages(msgList);
	    // logger.info("forum has " + msgList.size() + " posts");
	    // } else {
	    // logger.info("forum has no posts.");
	    // }
	    values.put("forum", forum);
	}
	return values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.ForumWService#updateForum(java.
     * lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public HashMap<String, String> updateForum(String id, String name,
	    String date, String[] documents) throws Exception {
	HashMap<String, String> values = new HashMap<String, String>();
	OMElement payload = getCreateUpdateForumPayLoad(name, id, date, true,
		documents);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:updateForum");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	values.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	return values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.ForumWService#attachDocumentsToForum
     * (java.lang.String, java.lang.String[])
     */
    @Override
    public HashMap<String, String> attachDocumentsToForum(String id,
	    String[] documents) throws Exception {
	HashMap<String, String> values = new HashMap<String, String>();
	OMElement payload = getAttachDocumentsPayLoad(id, documents);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:attachDocumentsToForum");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	values.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	return values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.ForumWService#deleteForum(java.
     * lang.String)
     */
    @Override
    public HashMap<String, String> deleteForum(String id) throws Exception {
	HashMap<String, String> values = new HashMap<String, String>();
	OMElement payload = getDeleteForumPayLoad(id);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:deleteForum");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	values.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	return values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.ForumWService#changeForumStatus
     * (java.lang.String)
     */
    @Override
    public HashMap<String, String> changeForumStatus(String id)
	    throws Exception {
	HashMap<String, String> values = new HashMap<String, String>();
	OMElement payload = getChangeStatusForumPayLoad(id);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:changeForumStatus");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	values.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	return values;
    }

    /**
     * ************************************************** THREAD MESSAGES
     * ****************************************************.
     * 
     * @param forumId the forum id
     * @param parentId the parent id
     * @param name the name
     * @param messageBody the message body
     * @param isReply the is reply
     * 
     * @return the hash map< string, string>
     * 
     * @throws Exception the exception
     */

    @Override
    @Deprecated
    public HashMap<String, String> createThreadMessage(String forumId,
	    String parentId, String name, String messageBody, boolean isReply)
	    throws Exception {
	HashMap<String, String> values = new HashMap<String, String>();
	OMElement payload = getCreateThreadMsgPayLoad(forumId, parentId, name,
		messageBody, isReply);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:createThreadMessage");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	if (result == null) {
	    throw new Exception("No result recieved from WS.");
	}
	logger.info(result.toString());
	OMElement omStatusCode = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	values.put("statusCode", omStatusCode.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	if (omStatusCode.getText().equals(SUCCESS_CODE)) {
	    OMElement omNewId = result.getFirstChildWithName(OMEUtils
		    .getQName("messageID"));
	    values.put("messageId", omNewId.getText());
	}
	return values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.ForumWService#createThreadMessage
     * (org.qualipso.factory.collaboration.ws.beans.MessageDTO,
     * java.lang.String[])
     */
    public HashMap<String, String> createThreadMessage(MessageDTO message,
	    String[] documents) throws Exception {
	HashMap<String, String> values = new HashMap<String, String>();
	OMElement payload = getCreateThreadMessagePayLoad(message, documents);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:createThreadMessage");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	if (result == null) {
	    throw new Exception("No result recieved from WS.");
	}
	logger.info(result.toString());
	OMElement omStatusCode = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	values.put("statusCode", omStatusCode.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	if (omStatusCode.getText().equals(SUCCESS_CODE)) {
	    OMElement omNewId = result.getFirstChildWithName(OMEUtils
		    .getQName("messageID"));
	    values.put("messageId", omNewId.getText());
	}
	return values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.ForumWService#readThreadMessage
     * (java.lang.String, java.lang.String)
     */
    @Override
    public HashMap<String, Object> readThreadMessage(String forumId,
	    String msgId) throws Exception {
	HashMap<String, Object> values = new HashMap<String, Object>();
	//
	OMElement payload = getReadThreadPayLoad(forumId, msgId);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:getThreadMessage");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result);
	values.put("result", result.toString());
	OMElement omStatusCode = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	values.put("statusCode", omStatusCode.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	//
	if (omStatusCode.getText().equals(SUCCESS_CODE)) {
	    MessageDTO msg = new MessageDTO();
	    msg.setId(msgId);
	    msg.setForumId(forumId);
	    OMElement omMessage = result.getFirstChildWithName(OMEUtils
		    .getQName("message"));
	    if (omMessage.getFirstChildWithName(OMEUtils.getQName("parentID")) != null) {
		msg.setParentId(omMessage.getFirstChildWithName(
			OMEUtils.getQName("parentID")).getText());
	    }
	    // In the factory we don't persist the following
	    // (messageBody,datePosted,numReplies,messageReplies)
	    OMElement omBody = omMessage.getFirstChildWithName(OMEUtils
		    .getQName("body"));
	    msg.setMessageBody(omBody.getText());
	    OMElement omSubject = omMessage.getFirstChildWithName(OMEUtils
		    .getQName("subject"));
	    msg.setName(omSubject.getText());
	    OMElement omDate = omMessage.getFirstChildWithName(OMEUtils
		    .getQName("datePosted"));
	    msg.setDatePosted(omDate.getText());
	    OMElement omAuthor = omMessage.getFirstChildWithName(OMEUtils
		    .getQName("author"));
	    msg.setAuthor(omAuthor.getText());
	    // No need to parse replies. There are of no use for the factory
	    // without the path referenece.
	    /*
	     * OMElement omReplies = omMessage.getFirstChildWithName(OMEUtils
	     * .getQName("messageReplies")); if (omReplies != null) {
	     * HashMap<String, MessageDTO> msgList = new HashMap<String,
	     * MessageDTO>(); Iterator msgsIterator =
	     * omReplies.getChildrenWithName(OMEUtils .getQName("message"));
	     * while (msgsIterator.hasNext()) { OMElement msgEl = (OMElement)
	     * msgsIterator.next(); MessageDTO tm = new MessageDTO();
	     * tm.setForumId(msg.getForumId());
	     * tm.setId(msgEl.getFirstChildWithName(
	     * OMEUtils.getQName("messageID")).getText()); if
	     * (msgEl.getFirstChildWithName(OMEUtils .getQName("parentID")) !=
	     * null) { tm.setParentId(msgEl.getFirstChildWithName(
	     * OMEUtils.getQName("parentID")).getText()); }
	     * tm.setMessageBody(msgEl.getFirstChildWithName(
	     * OMEUtils.getQName("body")).getText());
	     * tm.setName(msgEl.getFirstChildWithName(
	     * OMEUtils.getQName("subject")).getText());
	     * tm.setDatePosted(msgEl.getFirstChildWithName(
	     * OMEUtils.getQName("datePosted")).getText());
	     * tm.setAuthor(msgEl.getFirstChildWithName(
	     * OMEUtils.getQName("author")).getText()); msgList.put(tm.getId(),
	     * tm); } msg.setMessageReplies(msgList);
	     * 
	     * }
	     */
	    values.put("ThreadMessage", msg);

	}
	return values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.ForumWService#deleteThreadMessage
     * (java.lang.String)
     */
    @Override
    public HashMap<String, String> deleteThreadMessage(String id)
	    throws Exception {
	HashMap<String, String> values = new HashMap<String, String>();
	OMElement payload = getDeleteThreadMsgPayLoad(id);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:deleteThreadMessage");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	values.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	return values;
    }

    /**
     * ************************************************ Construct payloads
     * ************************************************.
     * 
     * @param name the name
     * 
     * @return the creates the forum pay load
     */

    private static OMElement getCreateForumPayLoad(String name) {
	return getCreateUpdateForumPayLoad(name, DEFAULT_WORKSPACE_STR, null,
		false, null);
    }

    /**
     * Gets the creates the update forum pay load.
     * 
     * @param name the name
     * @param id the id
     * @param date the date
     * @param isUpdate the is update
     * @param documents the documents
     * 
     * @return the creates the update forum pay load
     */
    private static OMElement getCreateUpdateForumPayLoad(String name,
	    String id, String date, boolean isUpdate, String[] documents) {
	OMElement method;
	if (!isUpdate) {
	    method = fac.createOMElement("createForum", omNs);
	} else {
	    method = fac.createOMElement("updateForum", omNs);
	}
	OMElement userElement = fac.createOMElement("username", omNs);
	userElement.addChild(fac.createOMText(userElement, USER_NAME));
	OMElement pwdElement = fac.createOMElement("password", omNs);
	pwdElement.addChild(fac.createOMText(pwdElement, USER_PWD));
	OMElement idElement;
	if (!isUpdate) {
	    idElement = fac.createOMElement("groupID", omNs);
	    idElement.addChild(fac.createOMText(idElement, id));
	} else {
	    idElement = fac.createOMElement("forumID", omNs);
	    idElement.addChild(fac.createOMText(idElement, id));
	}
	// title
	OMElement titleElement = fac.createOMElement("title", omNs);
	titleElement.addChild(fac.createOMText(titleElement, name));
	// isModerated
	OMElement isModeratedElement = fac.createOMElement("isModerated", omNs);
	isModeratedElement.addChild(fac.createOMText(isModeratedElement,
		"false"));

	// Add elements to createForum
	method.addChild(userElement);
	method.addChild(pwdElement);
	method.addChild(idElement);
	method.addChild(titleElement);
	if (isUpdate) {
	    OMElement dateElement = fac.createOMElement("date", omNs);
	    dateElement.addChild(fac.createOMText(dateElement, date));
	    method.addChild(dateElement);
	}
	method.addChild(isModeratedElement);
	if (!isUpdate) {
	    OMElement statusElement = fac.createOMElement("status", omNs);
	    statusElement.addChild(fac.createOMText(statusElement,
		    FORUM_STATUS_ACTIVE));
	    method.addChild(statusElement);
	}
	// participant
	OMElement participantElement = fac.createOMElement("participant", omNs);
	participantElement.addChild(fac.createOMText(participantElement,
		USER_NAME));
	method.addChild(participantElement);
	if (documents != null && documents.length > 0) {
	    for (int i = 0; i < documents.length; i++) {
		OMElement attElement = fac.createOMElement("attachment", omNs);
		attElement.addChild(fac.createOMText(attElement, documents[i]));
		method.addChild(attElement);
	    }
	}

	return method;
    }

    /**
     * Gets the attach documents pay load.
     * 
     * @param id the id
     * @param attachments the attachments
     * 
     * @return the attach documents pay load
     */
    private static OMElement getAttachDocumentsPayLoad(String id,
	    String[] attachments) {
	OMElement method = fac.createOMElement("attachDocumentsToForum", omNs);
	OMElement userElement = fac.createOMElement("username", omNs);
	userElement.addChild(fac.createOMText(userElement, USER_NAME));
	OMElement pwdElement = fac.createOMElement("password", omNs);
	pwdElement.addChild(fac.createOMText(pwdElement, USER_PWD));
	OMElement idElement = fac.createOMElement("forumID", omNs);
	idElement.addChild(fac.createOMText(idElement, id));
	method.addChild(userElement);
	method.addChild(pwdElement);
	method.addChild(idElement);
	if (attachments != null && attachments.length > 0) {
	    for (int i = 0; i < attachments.length; i++) {
		OMElement attElement = fac.createOMElement("attachment", omNs);
		attElement.addChild(fac
			.createOMText(attElement, attachments[i]));
		method.addChild(attElement);
	    }
	}
	return method;
    }

    /**
     * Gets the delete forum pay load.
     * 
     * @param id the id
     * 
     * @return the delete forum pay load
     */
    private static OMElement getDeleteForumPayLoad(String id) {
	OMElement method = fac.createOMElement("deleteForum", omNs);
	OMElement userElement = fac.createOMElement("username", omNs);
	userElement.addChild(fac.createOMText(userElement, USER_NAME));
	OMElement pwdElement = fac.createOMElement("password", omNs);
	pwdElement.addChild(fac.createOMText(pwdElement, USER_PWD));
	OMElement idElement = fac.createOMElement("forumID", omNs);
	idElement.addChild(fac.createOMText(idElement, id));
	method.addChild(userElement);
	method.addChild(pwdElement);
	method.addChild(idElement);

	return method;
    }

    /**
     * Gets the read forum pay load.
     * 
     * @param id the id
     * 
     * @return the read forum pay load
     */
    private static OMElement getReadForumPayLoad(String id) {
	OMElement method = fac.createOMElement("getForumProperties", omNs);
	OMElement userElement = fac.createOMElement("username", omNs);
	userElement.addChild(fac.createOMText(userElement, USER_NAME));
	OMElement pwdElement = fac.createOMElement("password", omNs);
	pwdElement.addChild(fac.createOMText(pwdElement, USER_PWD));
	OMElement idElement = fac.createOMElement("forumID", omNs);
	idElement.addChild(fac.createOMText(idElement, id));
	method.addChild(userElement);
	method.addChild(pwdElement);
	method.addChild(idElement);

	return method;
    }

    /**
     * Gets the change status forum pay load.
     * 
     * @param id the id
     * 
     * @return the change status forum pay load
     */
    private static OMElement getChangeStatusForumPayLoad(String id) {
	OMElement method = fac.createOMElement("changeForumStatus", omNs);
	OMElement userElement = fac.createOMElement("username", omNs);
	userElement.addChild(fac.createOMText(userElement, USER_NAME));
	OMElement pwdElement = fac.createOMElement("password", omNs);
	pwdElement.addChild(fac.createOMText(pwdElement, USER_PWD));
	OMElement idElement = fac.createOMElement("forumID", omNs);
	idElement.addChild(fac.createOMText(idElement, id));
	method.addChild(userElement);
	method.addChild(pwdElement);
	method.addChild(idElement);

	return method;
    }

    /**
     * Gets the creates the thread msg pay load.
     * 
     * @param forumId the forum id
     * @param parentId the parent id
     * @param name the name
     * @param messageBody the message body
     * @param isReply the is reply
     * 
     * @return the creates the thread msg pay load
     */
    private static OMElement getCreateThreadMsgPayLoad(String forumId,
	    String parentId, String name, String messageBody, boolean isReply) {
	OMElement method = fac.createOMElement("createThreadMessage", omNs);
	OMElement userElement = fac.createOMElement("username", omNs);
	userElement.addChild(fac.createOMText(userElement, USER_NAME));
	OMElement pwdElement = fac.createOMElement("password", omNs);
	pwdElement.addChild(fac.createOMText(pwdElement, USER_PWD));
	OMElement idElement = fac.createOMElement("forumID", omNs);
	idElement.addChild(fac.createOMText(idElement, forumId));
	OMElement titleElement = fac.createOMElement("title", omNs);
	titleElement.addChild(fac.createOMText(titleElement, name));
	OMElement bodyElement = fac.createOMElement("body", omNs);
	bodyElement.addChild(fac.createOMText(bodyElement, messageBody));
	method.addChild(userElement);
	method.addChild(pwdElement);
	method.addChild(idElement);
	if (isReply && parentId != null && !parentId.equals("")) {
	    OMElement parElement = fac.createOMElement("parentID", omNs);
	    parElement.addChild(fac.createOMText(idElement, parentId));
	    method.addChild(parElement);
	}
	method.addChild(titleElement);
	method.addChild(bodyElement);

	return method;
    }

    /**
     * Gets the creates the thread message pay load.
     * 
     * @param message the message
     * @param documents the documents
     * 
     * @return the creates the thread message pay load
     */
    private static OMElement getCreateThreadMessagePayLoad(MessageDTO message,
	    String[] documents) {
	OMElement method = fac.createOMElement("createThreadMessage", omNs);
	OMElement userElement = fac.createOMElement("username", omNs);
	userElement.addChild(fac.createOMText(userElement, USER_NAME));
	OMElement pwdElement = fac.createOMElement("password", omNs);
	pwdElement.addChild(fac.createOMText(pwdElement, USER_PWD));
	OMElement idElement = fac.createOMElement("forumID", omNs);
	idElement.addChild(fac.createOMText(idElement, message.getForumId()));
	OMElement titleElement = fac.createOMElement("title", omNs);
	titleElement
		.addChild(fac.createOMText(titleElement, message.getName()));
	OMElement bodyElement = fac.createOMElement("body", omNs);
	bodyElement.addChild(fac.createOMText(bodyElement, message
		.getMessageBody()));
	method.addChild(userElement);
	method.addChild(pwdElement);
	method.addChild(idElement);
	if (message.isReply() && message.getParentId() != null
		&& !message.getParentId().equals("")) {
	    OMElement parElement = fac.createOMElement("parentID", omNs);
	    parElement.addChild(fac.createOMText(idElement, message
		    .getParentId()));
	    method.addChild(parElement);
	}
	method.addChild(titleElement);
	method.addChild(bodyElement);
	if (documents != null && documents.length > 0) {
	    for (int i = 0; i < documents.length; i++) {
		OMElement attElement = fac.createOMElement("attachment", omNs);
		attElement.addChild(fac.createOMText(attElement, documents[i]));
		method.addChild(attElement);
	    }
	}

	return method;
    }

    /**
     * Gets the read thread pay load.
     * 
     * @param forumId the forum id
     * @param msgId the msg id
     * 
     * @return the read thread pay load
     */
    private static OMElement getReadThreadPayLoad(String forumId, String msgId) {
	OMElement method = fac.createOMElement("getThreadMessage", omNs);
	OMElement userElement = fac.createOMElement("username", omNs);
	userElement.addChild(fac.createOMText(userElement, USER_NAME));
	OMElement pwdElement = fac.createOMElement("password", omNs);
	pwdElement.addChild(fac.createOMText(pwdElement, USER_PWD));
	OMElement idElement = fac.createOMElement("forumID", omNs);
	idElement.addChild(fac.createOMText(idElement, forumId));
	OMElement msgIdElement = fac.createOMElement("messageID", omNs);
	msgIdElement.addChild(fac.createOMText(msgIdElement, msgId));
	method.addChild(userElement);
	method.addChild(pwdElement);
	method.addChild(idElement);
	method.addChild(msgIdElement);

	return method;
    }

    /**
     * Gets the delete thread msg pay load.
     * 
     * @param id the id
     * 
     * @return the delete thread msg pay load
     */
    private static OMElement getDeleteThreadMsgPayLoad(String id) {
	OMElement method = fac.createOMElement("deleteThreadMessage", omNs);
	OMElement userElement = fac.createOMElement("username", omNs);
	userElement.addChild(fac.createOMText(userElement, USER_NAME));
	OMElement pwdElement = fac.createOMElement("password", omNs);
	pwdElement.addChild(fac.createOMText(pwdElement, USER_PWD));
	OMElement idElement = fac.createOMElement("messageID", omNs);
	idElement.addChild(fac.createOMText(idElement, id));
	method.addChild(userElement);
	method.addChild(pwdElement);
	method.addChild(idElement);

	return method;
    }
}
