package org.qualipso.factory.collaboration.ws;

import java.util.HashMap;

import javax.ejb.Remote;

import org.qualipso.factory.collaboration.ws.beans.MessageDTO;

// TODO: Auto-generated Javadoc
/**
 * The Interface ForumWService.
 */
@Remote
public interface ForumWService {

    /**
     * Creates the forum.
     * 
     * @param forumName the forum name
     * 
     * @return the hash map< string, string>
     * 
     * @throws Exception the exception
     */
    HashMap<String, String> createForum(String forumName) throws Exception;

    /**
     * Read forum.
     * 
     * @param id the id
     * 
     * @return the hash map< string, object>
     * 
     * @throws Exception the exception
     */
    HashMap<String, Object> readForum(String id) throws Exception;

    /**
     * Update forum.
     * 
     * @param id the id
     * @param name the name
     * @param date the date
     * @param documents TODO
     * 
     * @return the hash map< string, string>
     * 
     * @throws Exception the exception
     */
    HashMap<String, String> updateForum(String id, String name, String date,
	    String[] documents) throws Exception;

    /**
     * Delete forum.
     * 
     * @param id the id
     * 
     * @return the hash map< string, string>
     * 
     * @throws Exception the exception
     */
    HashMap<String, String> deleteForum(String id) throws Exception;

    /**
     * Change forum status.
     * 
     * @param id the id
     * 
     * @return the hash map< string, string>
     * 
     * @throws Exception the exception
     */
    HashMap<String, String> changeForumStatus(String id) throws Exception;

    /**
     * Attach documents to forum.
     * 
     * @param id the id
     * @param documents the documents
     * 
     * @return the hash map< string, string>
     * 
     * @throws Exception the exception
     */
    HashMap<String, String> attachDocumentsToForum(String id, String[] documents)
	    throws Exception;

    /**
     * Creates the thread message.
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
    @Deprecated
    HashMap<String, String> createThreadMessage(String forumId,
	    String parentId, String name, String messageBody, boolean isReply)
	    throws Exception;

    /**
     * Creates the thread message.
     * 
     * @param message the message
     * @param documents the documents
     * 
     * @return the hash map< string, string>
     * 
     * @throws Exception the exception
     */
    HashMap<String, String> createThreadMessage(MessageDTO message,
	    String[] documents) throws Exception;

    /**
     * Read thread message.
     * 
     * @param forumId the forum id
     * @param id the id
     * 
     * @return the hash map< string, object>
     * 
     * @throws Exception the exception
     */
    HashMap<String, Object> readThreadMessage(String forumId, String id)
	    throws Exception;

    /**
     * Delete thread message.
     * 
     * @param id the id
     * 
     * @return the hash map< string, string>
     * 
     * @throws Exception the exception
     */
    HashMap<String, String> deleteThreadMessage(String id) throws Exception;
}
