package org.qualipso.factory.collaboration.document;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.collaboration.beans.DocumentDetails;
import org.qualipso.factory.collaboration.beans.ListItem;
import org.qualipso.factory.collaboration.document.entity.CollaborationFolder;
import org.qualipso.factory.collaboration.document.entity.Document;

/**
 * The Interface DocumentService.
 */
@Remote
@WebService(name = DocumentService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE
	+ DocumentService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface DocumentService extends FactoryService {
    
    /** The Constant SERVICE_NAME. */
    public static final String SERVICE_NAME = "document-management";
    
    /** The Constant RESOURCE_TYPE_LIST. */
    public static final String[] RESOURCE_TYPE_LIST = new String[] {
	    Document.RESOURCE_NAME, CollaborationFolder.RESOURCE_NAME };

    /**
     * Creates the document.
     * 
     * @param parentPath the parent path
     * @param document the document
     * 
     * @return the string
     * 
     * @throws DocumentServiceException the document service exception
     */
    @WebMethod
    @WebResult(name = "path")
    String createDocument(
	    @WebParam(name = "parent-path") String parentPath,
	    @WebParam(name = "document") DocumentDetails document)
	    throws DocumentServiceException;

    /**
     * Read document.
     * 
     * @param path the path
     * 
     * @return the document
     * 
     * @throws DocumentServiceException the document service exception
     */
    @WebMethod
    @WebResult(name = "document")
    Document readDocument(@WebParam(name = "path") String path)
	    throws DocumentServiceException;

    /**
     * Read document properties.
     * 
     * @param path the path
     * 
     * @return the document
     * 
     * @throws DocumentServiceException the document service exception
     */
    @WebMethod
    @WebResult(name = "document")
    Document readDocumentProperties(@WebParam(name = "path") String path)
	    throws DocumentServiceException;

    /**
     * Update document.
     * 
     * @param path the path
     * @param name the name
     * @param type the type
     * @param keywords the keywords
     * @param status the status
     * @param fileName the file name
     * @param mimeType the mime type
     * @param binaryContent the binary content
     * 
     * @throws DocumentServiceException the document service exception
     */
    @WebMethod
    void updateDocument(@WebParam(name = "path") String path,
	    @WebParam(name = "name") String name,
	    @WebParam(name = "type") String type,
	    @WebParam(name = "keywords") String keywords,
	    @WebParam(name = "status") String status,
	    @WebParam(name = "fileName") String fileName,
	    @WebParam(name = "mimeType") String mimeType,
	    @WebParam(name = "binaryContent") byte[] binaryContent)
	    throws DocumentServiceException;

    /**
     * Upload document version.
     * 
     * @param path the path
     * @param name the name
     * @param version the version
     * @param status the status
     * @param fileName the file name
     * @param mimeType the mime type
     * @param binaryContent the binary content
     * 
     * @throws DocumentServiceException the document service exception
     */
    @WebMethod
    void uploadDocumentVersion(@WebParam(name = "path") String path,
	    @WebParam(name = "name") String name,
	    @WebParam(name = "version") String version,
	    @WebParam(name = "status") String status,
	    @WebParam(name = "fileName") String fileName,
	    @WebParam(name = "mimeType") String mimeType,
	    @WebParam(name = "binaryContent") byte[] binaryContent)
	    throws DocumentServiceException;

    /**
     * Delete document.
     * 
     * @param path the path
     * 
     * @throws DocumentServiceException the document service exception
     */
    @WebMethod
    void deleteDocument(@WebParam(name = "path") String path)
	    throws DocumentServiceException;

    /**
     * Creates the folder.
     * 
     * @param parentPath the parent path
     * @param name the name
     * @param description the description
     * 
     * @return the string
     * 
     * @throws DocumentServiceException the document service exception
     */
    @WebMethod
    @WebResult(name = "path")
    String createFolder(@WebParam(name = "parent-path") String parentPath,
	    @WebParam(name = "name") String name,
	    @WebParam(name = "description") String description)
	    throws DocumentServiceException;

    /**
     * Read folder.
     * 
     * @param path the path
     * 
     * @return the collaboration folder
     * 
     * @throws DocumentServiceException the document service exception
     */
    @WebMethod
    @WebResult(name = "collaboration-folder")
    CollaborationFolder readFolder(@WebParam(name = "path") String path)
	    throws DocumentServiceException;

    /**
     * Update folder.
     * 
     * @param path the path
     * @param name the name
     * @param description the description
     * 
     * @throws DocumentServiceException the document service exception
     */
    @WebMethod
    void updateFolder(@WebParam(name = "path") String path,
	    @WebParam(name = "name") String name,
	    @WebParam(name = "description") String description)
	    throws DocumentServiceException;

    /**
     * Delete folder.
     * 
     * @param path the path
     * 
     * @throws DocumentServiceException the document service exception
     */
    @WebMethod
    void deleteFolder(@WebParam(name = "path") String path)
	    throws DocumentServiceException;

    /**
     * Read folder content.
     * 
     * @param path the path
     * 
     * @return the collaboration folder
     * 
     * @throws DocumentServiceException the document service exception
     */
    @WebMethod
    @WebResult(name = "collaboration-folder")
    CollaborationFolder readFolderContent(
	    @WebParam(name = "path") String path)
	    throws DocumentServiceException;

    /**
     * Gets the folder content.
     * 
     * @param path the path
     * 
     * @return the folder content
     * 
     * @throws DocumentServiceException the document service exception
     */
    @WebMethod
    @WebResult(name = "list-items")
    ListItem[] getFolderContent(@WebParam(name = "path") String path)
	    throws DocumentServiceException;

    /**
     * Gets the subfolders.
     * 
     * @param path the path
     * 
     * @return the subfolders
     * 
     * @throws DocumentServiceException the document service exception
     */
    @WebMethod
    @WebResult(name = "collaboration-folders")
    CollaborationFolder[] getSubfolders(
	    @WebParam(name = "path") String path)
	    throws DocumentServiceException;

    /**
     * Gets the folder documents.
     * 
     * @param path the path
     * 
     * @return the folder documents
     * 
     * @throws DocumentServiceException the document service exception
     */
    @WebMethod
    @WebResult(name = "documents")
    Document[] getFolderDocuments(@WebParam(name = "path") String path)
	    throws DocumentServiceException;
}
