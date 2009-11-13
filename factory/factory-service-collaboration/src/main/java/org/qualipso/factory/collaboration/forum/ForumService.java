package org.qualipso.factory.collaboration.forum;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.collaboration.document.entity.Document;
import org.qualipso.factory.collaboration.forum.entity.Forum;
import org.qualipso.factory.collaboration.forum.entity.ThreadMessage;

@Remote
@WebService(name = ForumService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE
	+ ForumService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ForumService extends FactoryService {
    public static final String SERVICE_NAME = "forum-management";
    public static final String[] RESOURCE_TYPE_LIST = new String[] {
	    Forum.RESOURCE_NAME, ThreadMessage.RESOURCE_NAME };

    @WebMethod
    public String createForum(@WebParam(name = "path") String parentPath,
	    @WebParam(name = "name") String name) throws ForumServiceException;

    @WebMethod
    @WebResult(name = "forum")
    public Forum readForum(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    @WebMethod
    public void updateForum(@WebParam(name = "path") String path,
	    @WebParam(name = "name") String name,
	    @WebParam(name = "date") String date) throws ForumServiceException;

    @WebMethod
    public void deleteForum(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    @WebMethod
    public void changeForumStatus(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    @WebMethod
    public String createThreadMessage(
	    @WebParam(name = "path") String parentPath,
	    @WebParam(name = "name") String name,
	    @WebParam(name = "forumId") String forumId,
	    @WebParam(name = "messageBody") String messageBody,
	    @WebParam(name = "datePosted") String datePosted,
	    @WebParam(name = "isReply") String isReply)
	    throws ForumServiceException;

    @WebMethod
    @WebResult(name = "thread-message")
    public ThreadMessage readThreadMessage(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    @WebMethod
    public void deleteThreadMessage(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    @WebMethod
    public Forum[] getForums(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    @WebMethod
    public Forum[] getForumsByStatus(@WebParam(name = "path") String path,
	    @WebParam(name = "status") String status)
	    throws ForumServiceException;

    @WebMethod
    public ThreadMessage[] getForumMessages(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    @WebMethod
    public ThreadMessage[] getThreadReplies(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    @WebMethod
    public void attachDocumentToForum(@WebParam(name = "path") String path,
	    @WebParam(name = "documentPath") String documentPath)
	    throws ForumServiceException;

    @WebMethod
    public Document[] getForumAttachments(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    @WebMethod
    public Document[] getThreadAttachments(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    @WebMethod
    public String createMessage(@WebParam(name = "path") String path,
	    @WebParam(name = "thread-message") ThreadMessage message,
	    @WebParam(name = "isReply") String isReply)
	    throws ForumServiceException;

}
