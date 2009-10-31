package org.qualipso.factory.collaboration.forum;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;
import org.qualipso.factory.collaboration.forum.entity.Forum;
import org.qualipso.factory.collaboration.forum.entity.ThreadMessage;

@Remote
@WebService(name = "ForumService", targetNamespace = "http://org.qualipso.factory.ws/service/forum")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ForumService extends FactoryService
{

    @WebMethod
    public String createForum(String path, String name) throws ForumServiceException;

    @WebMethod
    @WebResult(name = "forum")
    public Forum readForum(String path) throws ForumServiceException;

    @WebMethod
    public void updateForum(String path, String name, String date) throws ForumServiceException;

    @WebMethod
    public void deleteForum(String path) throws ForumServiceException;

    @WebMethod
    public String createThreadMessage(String path, String name, String forumId, String messageBody, String datePosted,
	    String isReply) throws ForumServiceException;

    @WebMethod
    @WebResult(name = "thread-message")
    public ThreadMessage readThreadMessage(String path) throws ForumServiceException;

    @WebMethod
    public void deleteThreadMessage(String path) throws ForumServiceException;
    
    @WebMethod
    public Forum[] getForums(String path) throws ForumServiceException;
    
    @WebMethod
    public ThreadMessage[] getForumMessages(String path)  throws ForumServiceException;
    
    @WebMethod
    public ThreadMessage[] getThreadReplies(String path)  throws ForumServiceException;
}
