package org.qualipso.factory.collaboration.document;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.collaboration.document.entity.CollaborationFolder;
import org.qualipso.factory.collaboration.document.entity.Document;
import org.qualipso.factory.collaboration.document.entity.ListItem;

@Remote
@WebService(name = DocumentService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE
	+ DocumentService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface DocumentService extends FactoryService {
    public static final String SERVICE_NAME = "document-management";
    public static final String[] RESOURCE_TYPE_LIST = new String[] {
	    Document.RESOURCE_NAME, CollaborationFolder.RESOURCE_NAME };

    @WebMethod
    public String createDocumentSimple(
	    @WebParam(name = "parentPath") String parentPath,
	    @WebParam(name = "name") String name,
	    @WebParam(name = "date") String date,
	    @WebParam(name = "type") String type,
	    @WebParam(name = "keywords") String keywords,
	    @WebParam(name = "version") String version,
	    @WebParam(name = "status") String status,
	    @WebParam(name = "fileName") String fileName,
	    @WebParam(name = "mimeType") String mimeType,
	    @WebParam(name = "binaryContent") byte[] binaryContent)
	    throws DocumentServiceException;

    @WebMethod
    public String createDocument(
	    @WebParam(name = "parentPath") String parentPath,
	    @WebParam(name = "document") Document document)
	    throws DocumentServiceException;

    @WebMethod
    @WebResult(name = "document")
    public Document readDocument(@WebParam(name = "path") String path)
	    throws DocumentServiceException;

    @WebMethod
    @WebResult(name = "document")
    public Document readDocumentProperties(@WebParam(name = "path") String path)
	    throws DocumentServiceException;

    @WebMethod
    public void updateDocument(@WebParam(name = "path") String path,
	    @WebParam(name = "name") String name,
	    @WebParam(name = "type") String type,
	    @WebParam(name = "keywords") String keywords,
	    @WebParam(name = "status") String status,
	    @WebParam(name = "fileName") String fileName,
	    @WebParam(name = "mimeType") String mimeType,
	    @WebParam(name = "binaryContent") byte[] binaryContent)
	    throws DocumentServiceException;

    @WebMethod
    public void uploadDocumentVersion(@WebParam(name = "path") String path,
	    @WebParam(name = "name") String name,
	    @WebParam(name = "version") String version,
	    @WebParam(name = "status") String status,
	    @WebParam(name = "fileName") String fileName,
	    @WebParam(name = "mimeType") String mimeType,
	    @WebParam(name = "binaryContent") byte[] binaryContent)
	    throws DocumentServiceException;

    @WebMethod
    public void deleteDocument(@WebParam(name = "path") String path)
	    throws DocumentServiceException;

    @WebMethod
    public String createFolder(@WebParam(name = "path") String parentPath,
	    @WebParam(name = "name") String name,
	    @WebParam(name = "description") String description)
	    throws DocumentServiceException;

    @WebMethod
    @WebResult(name = "collaboration-folder")
    public CollaborationFolder readFolder(@WebParam(name = "path") String path)
	    throws DocumentServiceException;

    @WebMethod
    public void updateFolder(@WebParam(name = "path") String path,
	    @WebParam(name = "name") String name,
	    @WebParam(name = "description") String description)
	    throws DocumentServiceException;

    @WebMethod
    public void deleteFolder(@WebParam(name = "path") String path)
	    throws DocumentServiceException;

    @WebMethod
    @WebResult(name = "collaboration-folder")
    public CollaborationFolder readFolderContent(
	    @WebParam(name = "path") String path)
	    throws DocumentServiceException;

    @WebMethod
    public ListItem[] getFolderContent(@WebParam(name = "path") String path)
	    throws DocumentServiceException;

    @WebMethod
    public CollaborationFolder[] getSubfolders(
	    @WebParam(name = "path") String path)
	    throws DocumentServiceException;

    @WebMethod
    public Document[] getFolderDocuments(@WebParam(name = "path") String path)
	    throws DocumentServiceException;
}
