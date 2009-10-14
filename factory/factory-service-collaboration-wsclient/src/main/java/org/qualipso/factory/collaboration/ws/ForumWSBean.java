package org.qualipso.factory.collaboration.ws;

import java.util.HashMap;
import java.util.Iterator;

import javax.ejb.Stateless;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.collaboration.ws.beans.ForumDTO;
import org.qualipso.factory.collaboration.ws.beans.MessageDTO;
@Stateless (name = "ForumWSBean", mappedName = "ForumWService")
public class ForumWSBean extends CollaborationWSUtils implements ForumWService
{
    private static Log logger = LogFactory.getLog(ForumWSBean.class);
    
    public ForumWSBean(){
    }
    
    @Override
    public HashMap<String, String> createForum(String forumName) throws Exception
    {
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
	if (result == null)
	{
	    throw new Exception("No result recieved from WS.");
	}
	logger.info(result.toString());
	OMElement omStatusCode = result.getFirstChildWithName(getQName("statusCode"));
	values.put("statusCode", omStatusCode.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	if (omStatusCode.getText().equals(SUCCESS_CODE))
	{
	    OMElement omForum = result.getFirstChildWithName(getQName("forumProperties"));
	    OMElement omNewId = omForum.getFirstChildWithName(getQName("forumID"));
	    values.put("forumId", omNewId.getText());
	}
	return values;
    }
    
    @Override
    public HashMap<String, Object> readForum(String id) throws Exception
    {
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
	OMElement omStatusCode = result.getFirstChildWithName(getQName("statusCode"));
	values.put("statusCode", omStatusCode.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	//
	if (omStatusCode.getText().equals(SUCCESS_CODE))
	{
	    ForumDTO forum = new ForumDTO();
	    forum.setId(id);
	    OMElement omForum = result.getFirstChildWithName(getQName("forumProperties"));
	    OMElement omTitle = omForum.getFirstChildWithName(getQName("title"));
	    forum.setName(omTitle.getText());
	    OMElement omDate = omForum.getFirstChildWithName(getQName("date"));
	    forum.setDate(omDate.getText());
	    OMElement omStatus = omForum.getFirstChildWithName(getQName("status"));
	    forum.setStatus(omStatus.getText());
	    OMElement omThreads = omForum.getFirstChildWithName(getQName("messageThreads"));
	    if (omThreads != null)
	    {
		HashMap<String, MessageDTO> msgList = new HashMap<String, MessageDTO>();
		Iterator<OMElement> msgsIterator = omThreads.getChildrenWithName(getQName("message"));
		while (msgsIterator.hasNext())
		{
		    OMElement msgEl = (OMElement) msgsIterator.next();
		    MessageDTO tm = new MessageDTO();
		    tm.setForumId(forum.getId());
		    tm.setId(msgEl.getFirstChildWithName(getQName("messageID")).getText());
		    if (msgEl.getFirstChildWithName(getQName("parentID")) != null)
		    {
			tm.setParentId(msgEl.getFirstChildWithName(getQName("parentID")).getText());
		    }
		    //tm.setMessageBody(msgEl.getFirstChildWithName(getQName("messageBody")).getText());
		    tm.setName(msgEl.getFirstChildWithName(getQName("subject")).getText());
		    tm.setDatePosted(msgEl.getFirstChildWithName(getQName("datePosted")).getText());
		    tm.setAuthor(msgEl.getFirstChildWithName(getQName("author")).getText());
		    tm.setNumReplies(msgEl.getFirstChildWithName(getQName("messageReplies")).getText());
		    msgList.put(tm.getId(), tm);
		    logger.info(tm.toString());
		    
		}
		forum.setMessages(msgList);
		logger.info("forum has "+msgList.size() +" posts");
	    }else{
		logger.info("forum has no posts.");
	    }
	    values.put("forum", forum);
	}
	return values;
    }

    @Override
    public HashMap<String, String> updateForum(String id, String name, String date) throws Exception
    {
	HashMap<String, String> values = new HashMap<String, String>();
	OMElement payload = getCreateUpdateForumPayLoad(name, id, date, true);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:updateForum");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(getQName("statusCode"));
	values.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	return values;
    }

    @Override
    public HashMap<String, String> deleteForum(String id) throws Exception
    {
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
	OMElement omStatus = result.getFirstChildWithName(getQName("statusCode"));
	values.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	return values;
    }

    /****************************************************
     * THREAD MESSAGES
     ******************************************************/

    @Override
    public  HashMap<String, String> createThreadMsg(String forumId, String parentId, String name,
	    String messageBody, boolean isReply) throws Exception
    {
	HashMap<String, String> values = new HashMap<String, String>();
	OMElement payload = getCreateThreadMsgPayLoad(forumId, parentId, name, messageBody, isReply);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:createThreadMessage");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	if (result == null)
	{
	    throw new Exception("No result recieved from WS.");
	}
	logger.info(result.toString());
	OMElement omStatusCode = result.getFirstChildWithName(getQName("statusCode"));
	values.put("statusCode", omStatusCode.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	if (omStatusCode.getText().equals(SUCCESS_CODE))
	{
	    OMElement omNewId = result.getFirstChildWithName(getQName("messageID"));
	    values.put("messageId", omNewId.getText());
	}
	return values;
    }

    @Override
    public HashMap<String, Object> readThread(String forumId, String msgId) throws Exception
    {
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
	OMElement omStatusCode = result.getFirstChildWithName(getQName("statusCode"));
	values.put("statusCode", omStatusCode.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	//
	if (omStatusCode.getText().equals(SUCCESS_CODE))
	{
	    MessageDTO msg = new MessageDTO();
	    msg.setId(msgId);
	    msg.setForumId(forumId);
	    OMElement omMessage = result.getFirstChildWithName(getQName("message"));
	    if (omMessage.getFirstChildWithName(getQName("parentID")) != null)
	    {
		msg.setParentId(omMessage.getFirstChildWithName(getQName("parentID")).getText());
	    }
	    // In the factory we don't persist the following (messageBody,datePosted,numReplies,messageReplies)
	    OMElement omBody = omMessage.getFirstChildWithName(getQName("body"));
	    msg.setMessageBody(omBody.getText());
	    OMElement omSubject = omMessage.getFirstChildWithName(getQName("subject"));
	    msg.setName(omSubject.getText());
	    OMElement omDate = omMessage.getFirstChildWithName(getQName("datePosted"));
	    msg.setDatePosted(omDate.getText());
	    OMElement omAuthor = omMessage.getFirstChildWithName(getQName("author"));
	    msg.setAuthor(omAuthor.getText());
	    OMElement omReplies = omMessage.getFirstChildWithName(getQName("messageReplies"));
	    if (omReplies != null)
	    {
		HashMap<String, MessageDTO> msgList = new HashMap<String, MessageDTO>();
		Iterator msgsIterator = omReplies.getChildrenWithName(getQName("message"));
		while (msgsIterator.hasNext())
		{
		    OMElement msgEl = (OMElement) msgsIterator.next();
		    MessageDTO tm = new MessageDTO();
		    tm.setForumId(msg.getForumId());
		    tm.setId(msgEl.getFirstChildWithName(getQName("messageID")).getText());
		    if (msgEl.getFirstChildWithName(getQName("parentID")) != null)
		    {
			tm.setParentId(msgEl.getFirstChildWithName(getQName("parentID")).getText());
		    }
		    tm.setMessageBody(msgEl.getFirstChildWithName(getQName("body")).getText());
		    tm.setName(msgEl.getFirstChildWithName(getQName("subject")).getText());
		    tm.setDatePosted(msgEl.getFirstChildWithName(getQName("datePosted")).getText());
		    tm.setAuthor(msgEl.getFirstChildWithName(getQName("author")).getText());
		    msgList.put(tm.getId(), tm);
		}
		msg.setMessageReplies(msgList);
	    }
	    values.put("ThreadMessage", msg);
	}
	return values;
    }

    @Override
    public HashMap<String, String> deleteThreadMessage(String id) throws Exception
    {
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
	OMElement omStatus = result.getFirstChildWithName(getQName("statusCode"));
	values.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	return values;
    }

    /**************************************************
     * Construct payloads
     **************************************************/

    private static OMElement getCreateForumPayLoad(String name)
    {
	return getCreateUpdateForumPayLoad(name, workspaceIDStr, null, false);
    }

    private static OMElement getCreateUpdateForumPayLoad(String name, String id, String date, boolean isUpdate)
    {
	OMElement method;
	if (!isUpdate)
	{
	    method = fac.createOMElement("createForum", omNs);
	} else
	{
	    method = fac.createOMElement("updateForum", omNs);
	}
	OMElement userElement = fac.createOMElement("username", omNs);
	userElement.addChild(fac.createOMText(userElement, USER_NAME));
	OMElement pwdElement = fac.createOMElement("password", omNs);
	pwdElement.addChild(fac.createOMText(pwdElement, USER_PWD));
	OMElement idElement;
	if (!isUpdate)
	{
	    idElement = fac.createOMElement("groupID", omNs);
	    idElement.addChild(fac.createOMText(idElement, id));
	} else
	{
	    idElement = fac.createOMElement("forumID", omNs);
	    idElement.addChild(fac.createOMText(idElement, id));
	}
	// title
	OMElement titleElement = fac.createOMElement("title", omNs);
	titleElement.addChild(fac.createOMText(titleElement, name));
	// isModerated
	OMElement isModeratedElement = fac.createOMElement("isModerated", omNs);
	isModeratedElement.addChild(fac.createOMText(isModeratedElement, "false"));
	// participant
	OMElement participantElement = fac.createOMElement("participant", omNs);
	participantElement.addChild(fac.createOMText(participantElement, USER_NAME));
	// TODO Handle attachment
	// Add elements to createForum
	method.addChild(userElement);
	method.addChild(pwdElement);
	method.addChild(idElement);
	method.addChild(titleElement);
	if (isUpdate)
	{
	    OMElement dateElement = fac.createOMElement("date", omNs);
	    dateElement.addChild(fac.createOMText(dateElement, date));
	    method.addChild(dateElement);
	}
	method.addChild(isModeratedElement);
	if (!isUpdate)
	{
	    OMElement statusElement = fac.createOMElement("status", omNs);
	    statusElement.addChild(fac.createOMText(statusElement, FORUM_STATUS_ACTIVE));
	    method.addChild(statusElement);
	}
	method.addChild(participantElement);
	return method;
    }

    private static OMElement getDeleteForumPayLoad(String id)
    {
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

    private static OMElement getReadForumPayLoad(String id)
    {
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

    private static OMElement getCreateThreadMsgPayLoad(String forumId, String parentId, String name,
	    String messageBody, boolean isReply)
    {
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
	if (isReply && parentId!=null&&!parentId.equals(""))
	{
	    OMElement parElement = fac.createOMElement("parentID", omNs);
	    parElement.addChild(fac.createOMText(idElement, parentId));
	    method.addChild(parElement);
	}
	method.addChild(titleElement);
	method.addChild(bodyElement);

	return method;
    }

    private static OMElement getReadThreadPayLoad(String forumId, String msgId)
    {
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

    private static OMElement getDeleteThreadMsgPayLoad(String id)
    {
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
