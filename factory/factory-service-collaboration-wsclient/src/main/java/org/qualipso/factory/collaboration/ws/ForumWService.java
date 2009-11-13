package org.qualipso.factory.collaboration.ws;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface ForumWService {
    public HashMap<String, String> createForum(String forumName)
	    throws Exception;

    public HashMap<String, Object> readForum(String id) throws Exception;

    public HashMap<String, String> updateForum(String id, String name,
	    String date) throws Exception;

    public HashMap<String, String> deleteForum(String id) throws Exception;

    public HashMap<String, String> changeForumStatus(String id)
	    throws Exception;

    public HashMap<String, String> createThreadMessage(String forumId,
	    String parentId, String name, String messageBody, boolean isReply)
	    throws Exception;

    public HashMap<String, Object> readThreadMessage(String forumId, String id)
	    throws Exception;

    public HashMap<String, String> deleteThreadMessage(String id)
	    throws Exception;

    public HashMap<String, String> attachDocumentsToForum(String id,
	    List<String> documents) throws Exception;
}
