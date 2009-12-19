package org.qualipso.factory.collaboration.ws;

import java.util.HashMap;

import javax.ejb.Remote;

// TODO: Auto-generated Javadoc
/**
 * The Interface DocumentWService.
 */
@Remote
public interface DocumentWService {

    /**
     * Creates the document.
     * 
     * @param parentFolderId the parent folder id
     * @param name the name
     * @param date the date
     * @param type the type
     * @param keywords the keywords
     * @param version the version
     * @param status the status
     * @param docname the docname
     * @param mimeType the mime type
     * @param binaryContent the binary content
     * 
     * @return the hash map< string, string>
     * 
     * @throws Exception the exception
     */
    HashMap<String, String> createDocument(String parentFolderId, String name,
	    String date, String type, String keywords, String version,
	    String status, String docname, String mimeType, byte[] binaryContent)
	    throws Exception;

    /**
     * Read document.
     * 
     * @param docId the doc id
     * 
     * @return the hash map< string, object>
     * 
     * @throws Exception the exception
     */
    HashMap<String, Object> readDocument(String docId) throws Exception;

    /**
     * Read document properties.
     * 
     * @param docId the doc id
     * 
     * @return the hash map< string, object>
     * 
     * @throws Exception the exception
     */
    HashMap<String, Object> readDocumentProperties(String docId)
	    throws Exception;

    /**
     * Update document.
     * 
     * @param documentID the document id
     * @param name the name
     * @param type the type
     * @param keywords the keywords
     * @param status the status
     * @param fileName the file name
     * @param mimeType the mime type
     * @param binaryContent the binary content
     * 
     * @return the hash map< string, string>
     * 
     * @throws Exception the exception
     */
    HashMap<String, String> updateDocument(String documentID, String name,
	    String type, String keywords, String status, String fileName,
	    String mimeType, byte[] binaryContent) throws Exception;

    /**
     * Upload document version.
     * 
     * @param documentID the document id
     * @param name the name
     * @param version the version
     * @param status the status
     * @param fileName the file name
     * @param mimeType the mime type
     * @param binaryContent the binary content
     * 
     * @return the hash map< string, string>
     * 
     * @throws Exception the exception
     */
    HashMap<String, String> uploadDocumentVersion(String documentID,
	    String name, String version, String status, String fileName,
	    String mimeType, byte[] binaryContent) throws Exception;

    /**
     * Delete document.
     * 
     * @param id the id
     * 
     * @return the hash map< string, string>
     * 
     * @throws Exception the exception
     */
    HashMap<String, String> deleteDocument(String id) throws Exception;

    /**
     * Creates the folder.
     * 
     * @param name the name
     * @param parentFolderID the parent folder id
     * @param abstractText the abstract text
     * 
     * @return the hash map< string, string>
     * 
     * @throws Exception the exception
     */
    HashMap<String, String> createFolder(String name, String parentFolderID,
	    String abstractText) throws Exception;

    /**
     * Read folder.
     * 
     * @param folderId the folder id
     * 
     * @return the hash map< string, string>
     * 
     * @throws Exception the exception
     */
    HashMap<String, Object> readFolder(String folderId) throws Exception;

    /**
     * Update folder.
     * 
     * @param folderId the folder id
     * @param name the name
     * @param abstractText the abstract text
     * 
     * @return the hash map< string, string>
     * 
     * @throws Exception the exception
     */
    HashMap<String, String> updateFolder(String folderId, String name,
	    String abstractText) throws Exception;

    /**
     * Delete folder.
     * 
     * @param folderId the folder id
     * 
     * @return the hash map< string, string>
     * 
     * @throws Exception the exception
     */
    HashMap<String, String> deleteFolder(String folderId) throws Exception;

    /**
     * Search for document.
     * 
     * @param metadataFilter the metadata filter
     * @param versionFilter the version filter
     * 
     * @return the hash map< string, object>
     * 
     * @throws Exception the exception
     */
    HashMap<String, Object> searchForDocument(String[][] metadataFilter,
	    String[] versionFilter) throws Exception;

}
