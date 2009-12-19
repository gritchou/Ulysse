package org.qualipso.factory.collaboration.forum;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.collaboration.beans.ThreadMessageDetails;
import org.qualipso.factory.collaboration.forum.entity.Forum;
import org.qualipso.factory.collaboration.forum.entity.ThreadMessage;

/**
 * The Interface ForumService.
 */
@Remote
@WebService(name = ForumService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE
	+ ForumService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ForumService extends FactoryService {

    /** The Constant SERVICE_NAME. */
    public static final String SERVICE_NAME = "forum-management";

    /** The Constant RESOURCE_TYPE_LIST. */
    public static final String[] RESOURCE_TYPE_LIST = new String[] {
	    Forum.RESOURCE_NAME, ThreadMessage.RESOURCE_NAME };

    /**
     * Creates the forum.
     * 
     * @param parentPath the parent path
     * @param name the name
     * 
     * @return the string
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    @WebResult(name = "path")
    String createForum(@WebParam(name = "parent-path") String parentPath,
	    @WebParam(name = "name") String name) throws ForumServiceException;

    /**
     * Read forum.
     * 
     * @param path the path
     * 
     * @return the forum
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    @WebResult(name = "forum")
    Forum readForum(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    /**
     * Read forum properties.
     * 
     * @param path the path
     * 
     * @return the forum
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    @WebResult(name = "forum")
    Forum readForumProperties(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    /**
     * Update forum.
     * 
     * @param path the path
     * @param name the name
     * @param date the date
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    void updateForum(@WebParam(name = "path") String path,
	    @WebParam(name = "name") String name,
	    @WebParam(name = "date") String date)
	    throws ForumServiceException;


    /**
     * Update forum with attachments.
     * 
     * @param path the path
     * @param name the name
     * @param date the date
     * @param documentPaths the document paths
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    void updateForumWithAttachments(@WebParam(name = "path") String path,
	    @WebParam(name = "name") String name,
	    @WebParam(name = "date") String date,
	    @WebParam(name = "attachments") String[] documentPaths)
	    throws ForumServiceException;

    /**
     * Delete forum.
     * 
     * @param path the path
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    void deleteForum(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    /**
     * Change forum status.
     * 
     * @param path the path
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    void changeForumStatus(@WebParam(name = "path") String path)
	    throws ForumServiceException;



    /**
     * Creates the thread message.
     * 
     * @param parentPath the parent path
     * @param message the message
     * 
     * @return the string
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    @WebResult(name = "path")
    String createThreadMessage(
	    @WebParam(name = "parent-path") String parentPath,
	    @WebParam(name = "thread-message") ThreadMessageDetails message)
	    throws ForumServiceException;

    /**
     * Creates the thread message with attachments.
     * 
     * @param parentPath the parent path
     * @param message the message
     * @param documentPaths the document paths
     * 
     * @return the string
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    @WebResult(name = "path")
    String createThreadMessageWithAttachments(
	    @WebParam(name = "parent-path") String parentPath,
	    @WebParam(name = "thread-message") ThreadMessageDetails message,
	    @WebParam(name = "attachments") String[] documentPaths)
	    throws ForumServiceException;

    /**
     * Read thread message.
     * 
     * @param path the path
     * 
     * @return the thread message
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    @WebResult(name = "thread-message")
    ThreadMessage readThreadMessage(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    /**
     * Read thread message properties.
     * 
     * @param path the path
     * 
     * @return the thread message
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    @WebResult(name = "thread-message")
    ThreadMessage readThreadMessageProperties(
	    @WebParam(name = "path") String path) throws ForumServiceException;

    /**
     * Delete thread message.
     * 
     * @param path the path
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    void deleteThreadMessage(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    /**
     * Gets the forums.
     * 
     * @param path the path
     * 
     * @return the forums
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    @WebResult(name = "forums")
    Forum[] getForums(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    /**
     * Gets the forums by status.
     * 
     * @param path the path
     * @param status the status
     * 
     * @return the forums by status
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    @WebResult(name = "forums")
    Forum[] getForumsByStatus(@WebParam(name = "path") String path,
	    @WebParam(name = "status") String status)
	    throws ForumServiceException;

    /**
     * Gets the forum messages.
     * 
     * @param path the path
     * 
     * @return the forum messages
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    @WebResult(name = "thread-messages")
    ThreadMessage[] getForumMessages(@WebParam(name = "path") String path)
	    throws ForumServiceException;

    /**
     * Gets the thread replies.
     * 
     * @param path the path
     * 
     * @return the thread replies
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    @WebResult(name = "thread-messages")
    ThreadMessage[] getThreadReplies(@WebParam(name = "path") String path)
	    throws ForumServiceException;


    /**
     * Attach documents to forum.
     * 
     * @param path the path
     * @param documentPaths the document paths
     * 
     * @throws ForumServiceException the forum service exception
     */
    @WebMethod
    void attachDocumentsToForum(@WebParam(name = "path") String path,
	    @WebParam(name = "attachments") String[] documentPaths)
	    throws ForumServiceException;
}
