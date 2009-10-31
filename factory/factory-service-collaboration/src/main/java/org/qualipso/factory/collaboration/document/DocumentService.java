package org.qualipso.factory.collaboration.document;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;
import org.qualipso.factory.collaboration.document.entity.CollaborationFolder;
import org.qualipso.factory.collaboration.document.entity.Document;
import org.qualipso.factory.collaboration.document.entity.ListItem;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 june 2009
 */
@Remote
@WebService(name = "DocumentService", targetNamespace = "http://org.qualipso.factory.ws/service/document")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface DocumentService extends FactoryService
{

    @WebMethod
    public void createDocument(String path, String name, String date, String type, String keywords, String version,
	    String status, String fileName, String mimeType, byte[] binaryContent) throws DocumentServiceException;

    @WebMethod
    @WebResult(name = "document")
    public Document readDocument(String path) throws DocumentServiceException;
    
    @WebMethod
    @WebResult(name = "document")
    public Document readDocumentProperties(String path) throws DocumentServiceException;

    @WebMethod
    public void updateDocument(String path, String name, String type, String keywords, String status, String fileName,
	    String mimeType, byte[] binaryContent) throws DocumentServiceException;

    @WebMethod
    public void deleteDocument(String path) throws DocumentServiceException;

    @WebMethod
    public String createFolder(String path, String name, String description) throws DocumentServiceException;

    @WebMethod
    @WebResult(name = "collaborationfolder")
    public CollaborationFolder readFolder(String path) throws DocumentServiceException;

    @WebMethod
    public void updateFolder(String path, String name, String description) throws DocumentServiceException;

    @WebMethod
    public void deleteFolder(String path) throws DocumentServiceException;
    
    @WebMethod
    @WebResult(name = "collaborationfolder")
    public CollaborationFolder readFolderContent(String path) throws DocumentServiceException;
    
    @WebMethod
    public ListItem[] getFolderContent(String path) throws DocumentServiceException;

    @WebMethod
    public CollaborationFolder[] getSubfolders(String path) throws DocumentServiceException;

    @WebMethod
    public Document[] getFolderDocuments(String path) throws DocumentServiceException;

}
