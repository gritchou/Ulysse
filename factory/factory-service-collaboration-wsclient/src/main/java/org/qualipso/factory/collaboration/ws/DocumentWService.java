package org.qualipso.factory.collaboration.ws;

import java.util.HashMap;
import javax.ejb.Remote;
@Remote
public interface DocumentWService
{
    public HashMap<String, String> createDocument(String parentFolderId, String name, String date, String type,
	    String keywords, String version, String status, String docname, String mimeType, byte[] binaryContent)
	    throws Exception;

    public HashMap<String, Object> readDocument(String docId) throws Exception;

    public HashMap<String, Object> readDocumentProperties(String docId) throws Exception;
    
    public HashMap<String, String> updateDocument(String documentID, String name, String type, String keywords,
	    String status, String fileName, String mimeType, byte[] binaryContent) throws Exception;

    public HashMap<String, String> deleteDocument(String id) throws Exception;

    // Folder
    public HashMap<String, String> createFolder(String name, String parentFolderID, String abstractText)
	    throws Exception;

    public HashMap<String, String> readFolder(String folderId) throws Exception;

    public HashMap<String, String> updateFolder(String folderId, String name, String abstractText) throws Exception;

    public HashMap<String, String> deleteFolder(String folderId) throws Exception;

}
