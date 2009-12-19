package org.qualipso.factory.collaboration.document;

import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.browser.BrowserService;
import org.qualipso.factory.collaboration.beans.DocumentDetails;
import org.qualipso.factory.collaboration.beans.ListItem;
import org.qualipso.factory.collaboration.document.entity.CollaborationFolder;
import org.qualipso.factory.collaboration.document.entity.Document;
import org.qualipso.factory.collaboration.utils.CollaborationUtils;
import org.qualipso.factory.collaboration.ws.DocumentWService;
import org.qualipso.factory.collaboration.ws.beans.DocumentDTO;
import org.qualipso.factory.collaboration.ws.beans.FolderDTO;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;

/**
 * The Class DocumentServiceBean.
 */
@Stateless(name = "DocumentServiceBean", mappedName = FactoryNamingConvention.SERVICE_PREFIX
	+ DocumentService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.collaboration.document.DocumentService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE
	+ DocumentService.SERVICE_NAME, serviceName = DocumentService.SERVICE_NAME, portName = DocumentService.SERVICE_NAME
	+ "Port")
@WebContext(contextRoot = CollaborationUtils.COLLABORATION_SERVICE_PREFIX, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX
	+ DocumentService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class DocumentServiceBean implements DocumentService {
    
    /** The logger. */
    private static Log logger = LogFactory.getLog(DocumentServiceBean.class);

    /** The binding. */
    private BindingService binding;
    
    /** The pep. */
    private PEPService pep;
    
    /** The pap. */
    private PAPService pap;
    
    /** The notification. */
    private NotificationService notification;
    
    /** The membership. */
    private MembershipService membership;
    
    /** The browser. */
    private BrowserService browser;
    
    /** The core. */
    private CoreService core;
    //
    /** The ctx. */
    private SessionContext ctx;
    
    /** The em. */
    private EntityManager em;
    //
    /** The document ws. */
    private DocumentWService documentWS;

    /**
     * Instantiates a new document service bean.
     */
    public DocumentServiceBean() {
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.document.DocumentService#createDocument(java.lang.String, org.qualipso.factory.collaboration.beans.DocumentDetails)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String createDocument(String parentPath, DocumentDetails document)
	    throws DocumentServiceException {
	logIt("createDocument(...) called");
	logIt("params : path=" + parentPath + ", name=" + document.getName());
	String path = null;
	try {
	    if (document.getName() != null
		    && document.getBinaryContent() != null) {
		// Security check
		String caller = membership
			.getProfilePathForConnectedIdentifier();
		if (caller == null) {
		    throw new DocumentServiceException(
			    "Could not get connected profile");
		}
		logIt("caller: " + caller);
		// String parentPath = "/";
		// if (!parentPath.equals("/"))
		// {
		// parentPath = PathHelper.getParentPath(parentPath);
		// }
		pep.checkSecurity(caller, parentPath, "create");
		//
		String parentFolderId = CollaborationUtils.DEFAULT_FOLDER_ID;
		// Check if folder is a collaboration folder or not.
		// If it is we need to match it with a mermig folder.
		if (!parentPath.equals("/")) {
		    CollaborationFolder parFold = null;
		    try {
			parFold = readFolder(parentPath);
			if (parFold != null) {
			    parentFolderId = parFold.getId();
			}
		    } catch (Exception e) {
			logger
				.warn("Propably we create a document for a parent that it is not collaboration-folder");
		    }
		    /*
		     * else { // Should we create it? String tempName = "F" +
		     * System.currentTimeMillis(); if (parentPath.indexOf("/") >
		     * 0) { int i = parentPath.lastIndexOf("/"); if (i + 1 <
		     * parentPath.length()) { tempName =
		     * parentPath.substring(i); } }
		     * logIt("Create folder before uploading file. Path " +
		     * parentPath + " Name " + tempName); String folderID =
		     * createFolder(parentPath, tempName,
		     * "Folder created by factory"); if (folderID == null) {
		     * throw new DocumentServiceException(
		     * "Error in creating folder for the document upload.Path: "
		     * + parentPath); } parentFolderID = folderID; }
		     */
		}
		logIt("Parent folder id " + parentFolderId);
		// We need to find if this folder ID exists on cms (mermig).
		// call the getFolder service?
		// Call the create Documet service
		HashMap<String, String> values = documentWS.createDocument(
			parentFolderId, document.getName(), document.getDate(),
			document.getType(), document.getKeywords(), document
				.getVersion(), document.getStatus(), document
				.getFileName(), document.getMimeType(),
			document.getBinaryContent());
		if (values != null && !values.isEmpty()) {
		    String code = (String) values.get("statusCode");
		    String msg = (String) values.get("statusMessage");
		    String newId = (String) values.get("documentId");
		    logIt("Message Code:" + code + " Message: " + msg);
		    if (!code.equals(CollaborationUtils.SUCCESS_CODE)
			    || (newId == null || newId.equals(""))) {
			throw new DocumentServiceException(
				"Error code recieved from the WS." + " Code"
					+ code + " Message:" + msg);
		    }
		    // we persist only id,name,path and parent id and author
		    path = PathHelper.normalize(parentPath + "/" + newId);
		    Document doc = new Document();
		    doc.setId(newId);// Set the id returned by the MermigWS
		    doc.setResourcePath(path);
		    doc.setName(document.getName());
		    doc.setAuthor(caller.toString());
		    doc.setParentFolderId(parentFolderId);
		    em.persist(doc);
		    // Bind the document with the path and the identifier
		    binding.bind(doc.getFactoryResourceIdentifier(), path);
		    binding.setProperty(path,
			    FactoryResourceProperty.CREATION_TIMESTAMP, ""
				    + System.currentTimeMillis());
		    binding.setProperty(path,
			    FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, ""
				    + System.currentTimeMillis());
		    binding.setProperty(path, FactoryResourceProperty.AUTHOR,
			    caller);
		    // Create policy (owner)
		    String policyId = UUID.randomUUID().toString();
		    pap.createPolicy(policyId, PAPServiceHelper
			    .buildOwnerPolicy(policyId, caller, path));
		    binding.setProperty(path, FactoryResourceProperty.OWNER,
			    caller);
		    binding.setProperty(path,
			    FactoryResourceProperty.POLICY_ID, policyId);
		    // Notify
		    notification.throwEvent(new Event(path, caller,
			    Document.RESOURCE_NAME, Event.buildEventType(
				    DocumentService.SERVICE_NAME,
				    Document.RESOURCE_NAME, "create"), ""));

		    //
		    logIt(doc.toString());
		} else {
		    throw new DocumentServiceException(
			    "No valid answer from the WS.Check logs.");
		}
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("unable to create the document at path " + parentPath,
		    e);
	    throw new DocumentServiceException(
		    "unable to create the document at path " + parentPath, e);
	}
	return path;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.document.DocumentService#readDocument(java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Document readDocument(String path) throws DocumentServiceException {
	Document document = null;
	logIt("readDocument(...) called");
	logIt("params : path=" + path);

	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new DocumentServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, Document.RESOURCE_NAME);
	    // Find the entity
	    document = em.find(Document.class, identifier.getId());
	    if (document == null) {
		throw new DocumentServiceException(
			"unable to find a document for id "
				+ identifier.getId());
	    }
	    // Call the WS to retrieve values that are not stored in Mermig CMS
	    HashMap<String, Object> values = documentWS.readDocument(document
		    .getId());
	    if (values != null && !values.isEmpty()) {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)
			|| values.get("document") == null
			|| values.get("documentContent") == null) {
		    throw new DocumentServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}
		DocumentDTO docWS = (DocumentDTO) values.get("document");
		DocumentDTO docCont = (DocumentDTO) values
			.get("documentContent");
		if (docCont != null && docWS != null) {
		    // We don't override id,path,name,parentFolderID and author
		    // since we persist them on factory
		    // We don't persist date,type,keywords,version,status,file
		    // details
		    document.setResourceId(docWS.getResourceId());
		    document.setDate(docWS.getDate());
		    document.setType(docWS.getType());
		    document.setKeywords(docWS.getKeywords());
		    document.setVersion(docWS.getVersion());
		    document.setStatus(docWS.getStatus());
		    document.setSize(docWS.getSize());
		    //
		    document.setMimeType(docCont.getMimeType());
		    document.setFileName(docCont.getFileName());
		    document.setBinaryContent(docCont.getBinaryContent());
		    //
		    document.setResourcePath(path);
		    // Notify
		    notification.throwEvent(new Event(path, caller,
			    Document.RESOURCE_NAME, Event.buildEventType(
				    DocumentService.SERVICE_NAME,
				    Document.RESOURCE_NAME, "read"), ""));
		    //
		    logIt(document.toString());
		} else {
		    throw new DocumentServiceException(
			    "No valid answer from the WS.Check logs.");
		}
	    }
	} catch (Exception e) {
	    logger.error("unable to read the document at path " + path, e);
	    throw new DocumentServiceException(
		    "unable to read the document at path " + path, e);
	}
	return document;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.document.DocumentService#readDocumentProperties(java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Document readDocumentProperties(String path)
	    throws DocumentServiceException {
	Document document = null;
	logIt("readDocumentProperties(...) called");
	logIt("params : path=" + path);

	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new DocumentServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, Document.RESOURCE_NAME);
	    // Find the entity
	    document = em.find(Document.class, identifier.getId());
	    if (document == null) {
		throw new DocumentServiceException(
			"unable to find a document for id "
				+ identifier.getId());
	    }
	    // Call the WS to retrieve values that are not stored in Mermig CMS
	    HashMap<String, Object> values = documentWS
		    .readDocumentProperties(document.getId());
	    if (values != null && !values.isEmpty()) {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)
			|| values.get("document") == null) {
		    throw new DocumentServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}
		DocumentDTO docWS = (DocumentDTO) values.get("document");
		if (docWS != null) {
		    // We don't override id,path,name,parentFolderID and author
		    // since we persist them on factory
		    // We don't persist date,type,keywords,version,status,file
		    // details
		    document.setResourceId(docWS.getResourceId());
		    document.setDate(docWS.getDate());
		    document.setType(docWS.getType());
		    document.setKeywords(docWS.getKeywords());
		    document.setVersion(docWS.getVersion());
		    document.setStatus(docWS.getStatus());
		    document.setSize(docWS.getSize());
		    document.setResourcePath(path);
		    document.setFileName(docWS.getFileName());
		    // Notify
		    notification.throwEvent(new Event(path, caller,
			    Document.RESOURCE_NAME, Event.buildEventType(
				    DocumentService.SERVICE_NAME,
				    Document.RESOURCE_NAME, "read"), ""));
		    //
		    logIt(document.toString());
		} else {
		    throw new DocumentServiceException(
			    "No valid answer from the WS.Check logs.");
		}
	    }
	} catch (Exception e) {
	    logger.error("unable to read the document properties at path "
		    + path, e);
	    throw new DocumentServiceException(
		    "unable to read the document properties at path " + path, e);
	}
	return document;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.document.DocumentService#updateDocument(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, byte[])
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateDocument(String path, String name, String type,
	    String keywords, String status, String fileName, String mimeType,
	    byte[] binaryContent) throws DocumentServiceException {
	logIt("updateDocument(...) called");
	logIt("params : path=" + path + ", name=" + name);

	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new DocumentServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "update");
	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, Document.RESOURCE_NAME);
	    // Find the entity
	    Document doc = em.find(Document.class, identifier.getId());
	    if (doc == null) {
		throw new DocumentServiceException(
			"unable to find a document for id "
				+ identifier.getId());
	    }
	    // Call the WS to update the document
	    HashMap<String, String> values = documentWS.updateDocument(doc
		    .getId(), name, type, keywords, status, fileName, mimeType,
		    binaryContent);
	    if (values != null && !values.isEmpty()) {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
		    throw new DocumentServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}
		// In factory we persist only id,path,name,parentFolderID and
		// author. So only name is changeable.
		doc.setName(name);
		em.merge(doc);
		binding.setProperty(path,
			FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System
				.currentTimeMillis()
				+ "");
		// notify
		notification.throwEvent(new Event(path, caller,
			Document.RESOURCE_NAME, Event.buildEventType(
				DocumentService.SERVICE_NAME,
				Document.RESOURCE_NAME, "update"), ""));
		//
		logIt(doc.toString());
	    } else {
		throw new DocumentServiceException(
			"No valid answer from the WS.Check logs.");
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("unable to update the name at path " + path, e);
	    throw new DocumentServiceException(
		    "unable to update the document at path " + path, e);
	}
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.document.DocumentService#deleteDocument(java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteDocument(String path) throws DocumentServiceException {
	logIt("deleteDocument(...) called");
	logIt("params : path=" + path);

	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new DocumentServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "delete");
	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, Document.RESOURCE_NAME);
	    // Find the entity
	    Document doc = em.find(Document.class, identifier.getId());
	    if (doc == null) {
		throw new DocumentServiceException(
			"unable to find a document for id "
				+ identifier.getId());
	    }

	    HashMap<String, String> values = documentWS.deleteDocument(doc
		    .getId());
	    if (values != null && !values.isEmpty()) {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
		    throw new DocumentServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}
		em.remove(doc);
		// Delete the policy and unbind the resource from this path
		String policyId = binding.getProperty(path,
			FactoryResourceProperty.POLICY_ID, false);
		pap.deletePolicy(policyId);
		binding.unbind(path);
		// Notify
		notification.throwEvent(new Event(path, caller,
			Document.RESOURCE_NAME, Event.buildEventType(
				DocumentService.SERVICE_NAME,
				Document.RESOURCE_NAME, "delete"), ""));
	    } else {
		throw new DocumentServiceException(
			"No valid answer from the WS.Check logs.");
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("unable to delete the name at path " + path, e);
	    throw new DocumentServiceException(
		    "unable to delete the document at path " + path, e);
	}
    }

    // Folder management
    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.document.DocumentService#createFolder(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String createFolder(String parentPath, String name,
	    String description) throws DocumentServiceException {
	logIt("createFolder(...) called");
	logIt("params : parent path=" + parentPath + ", name=" + name);
	String path = null;
	try {
	    // Check mandatory values.
	    if (name == null || name.equals("")) {
		throw new DocumentServiceException("Folder name is mandatory");
	    }
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new DocumentServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, parentPath, "create");
	    // Get parent folder id
	    String parentFolderID = CollaborationUtils.DEFAULT_FOLDER_ID;
	    if (!parentPath.equals("/")) {
		// Parent path may not be CF, so continue even if it fails.
		try {
		    CollaborationFolder parentFolder = readFolder(parentPath);
		    if (parentFolder != null && parentFolder.getId() != null) {
			parentFolderID = parentFolder.getId();
		    }
		} catch (Exception e) {
		    logger
			    .warn("Error in reading parent. Probably not a collaboration folder type. Error message: "
				    + e.getMessage());
		}
	    }
	    logIt("Folder id " + parentFolderID);
	    // Call the create Folder service
	    HashMap<String, String> values = documentWS.createFolder(name,
		    parentFolderID, description);
	    if (values != null && !values.isEmpty()) {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		String newFolderId = (String) values.get("folderID");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)
			|| (newFolderId == null || newFolderId.equals(""))) {
		    throw new DocumentServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}
		// we persist only id,name, path and parent id
		// TODO should we create the path based on id?
		path = PathHelper.normalize(parentPath + "/"
			+ CollaborationUtils.normalizeForPath(name));
		CollaborationFolder folder = new CollaborationFolder();
		folder.setId(newFolderId);// Set the id returned by the MermigWS
		folder.setName(name);
		folder.setParentFolderId(parentFolderID);
		folder.setResourcePath(path);
		em.persist(folder);
		// Bind the entity with the path and the identifier
		binding.bind(folder.getFactoryResourceIdentifier(), path);
		binding.setProperty(path,
			FactoryResourceProperty.CREATION_TIMESTAMP, ""
				+ System.currentTimeMillis());
		binding.setProperty(path,
			FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, ""
				+ System.currentTimeMillis());
		binding.setProperty(path, FactoryResourceProperty.AUTHOR,
			caller);
		// Create policy (owner)
		String policyId = UUID.randomUUID().toString();
		pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(
			policyId, caller, path));
		binding
			.setProperty(path, FactoryResourceProperty.OWNER,
				caller);
		binding.setProperty(path, FactoryResourceProperty.POLICY_ID,
			policyId);
		// Notify
		notification.throwEvent(new Event(path, caller,
			CollaborationFolder.RESOURCE_NAME, Event
				.buildEventType(DocumentService.SERVICE_NAME,
					CollaborationFolder.RESOURCE_NAME,
					"create"), ""));
		//
		logIt(folder.toString());
	    } else {
		throw new DocumentServiceException(
			"No valid answer from the WS.Check logs.");
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("unable to create the folder at " + parentPath, e);
	    throw new DocumentServiceException(
		    "unable to create the folder at " + parentPath, e);
	}
	return path;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.document.DocumentService#readFolder(java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public CollaborationFolder readFolder(String path)
	    throws DocumentServiceException {
	CollaborationFolder folder = null;
	logIt("readFolder(...) called");
	logIt("params : path=" + path);

	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new DocumentServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");

	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, CollaborationFolder.RESOURCE_NAME);
	    // Find the entity
	    folder = em.find(CollaborationFolder.class, identifier.getId());
	    if (folder == null) {
		throw new DocumentServiceException(
			"unable to find a folder for id " + identifier.getId());
	    }
	    // Call WS getFolderProperties
	    HashMap<String, Object> values = documentWS.readFolder(folder
		    .getId());
	    if (values != null && !values.isEmpty()) {
		// We don't persist description and date
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
		    throw new DocumentServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}
		if(values.get("folder")!=null)
		{
		    FolderDTO  folderWS = (FolderDTO)values.get("folder");
		    folder.setDescription(folderWS.getDescription());
		    folder.setDate(folderWS.getDate());
		}
		
	    } else {
		throw new DocumentServiceException(
			"No valid answer from the WS.Check logs.");
	    }
	    folder.setResourcePath(path);
	    // Notify
	    notification.throwEvent(new Event(path, caller,
		    CollaborationFolder.RESOURCE_NAME, Event.buildEventType(
			    DocumentService.SERVICE_NAME,
			    CollaborationFolder.RESOURCE_NAME, "read"), ""));
	    //
	    logIt(folder.toString());
	} catch (Exception e) {
	    logger.error("unable to read the folder at path " + path, e);
	    throw new DocumentServiceException(
		    "unable to read the folder at path " + path, e);
	}
	return folder;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.document.DocumentService#updateFolder(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateFolder(String path, String name, String description)
	    throws DocumentServiceException {
	logIt("updateFolder(...) called");
	logIt("params : path=" + path + ", name=" + name);

	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new DocumentServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "update");

	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, CollaborationFolder.RESOURCE_NAME);
	    // Find the entity
	    CollaborationFolder folder = em.find(CollaborationFolder.class,
		    identifier.getId());
	    if (folder == null) {
		throw new DocumentServiceException(
			"unable to find a document for id "
				+ identifier.getId());
	    }
	    // Call the WS to update the folder
	    HashMap<String, String> values = documentWS.updateFolder(folder
		    .getId(), name, description);
	    if (values != null && !values.isEmpty()) {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
		    throw new DocumentServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}
		// Update the entity
		folder.setName(name);
		em.merge(folder);
		binding.setProperty(path,
			FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System
				.currentTimeMillis()
				+ "");
		// Notify
		notification.throwEvent(new Event(path, caller,
			CollaborationFolder.RESOURCE_NAME, Event
				.buildEventType(DocumentService.SERVICE_NAME,
					CollaborationFolder.RESOURCE_NAME,
					"update"), ""));
		//
		logIt(folder.toString());
	    } else {
		throw new DocumentServiceException(
			"No valid answer from the WS.Check logs.");
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("unable to update the folder at path " + path, e);
	    throw new DocumentServiceException(
		    "unable to update the folder at path " + path, e);
	}
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.document.DocumentService#deleteFolder(java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteFolder(String path) throws DocumentServiceException {
	logIt("deleteFolder(...) called");
	logIt("params : path=" + path);

	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new DocumentServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "delete");

	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, CollaborationFolder.RESOURCE_NAME);
	    // Find the entity
	    CollaborationFolder folder = em.find(CollaborationFolder.class,
		    identifier.getId());
	    if (folder == null) {
		throw new DocumentServiceException(
			"unable to find a folder for id " + identifier.getId());
	    }
	    // Check if folder has children. if yes. then don't delete.
	    if (!browser.hasChildren(path)) {
		HashMap<String, String> values = documentWS.deleteFolder(folder
			.getId());
		if (values != null && !values.isEmpty()) {
		    String code = (String) values.get("statusCode");
		    String msg = (String) values.get("statusMessage");
		    logIt("Message Code:" + code + " Message: " + msg);
		    if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
			throw new DocumentServiceException(
				"Error code recieved from the WS." + " Code"
					+ code + " Message:" + msg);
		    }
		    // remove it
		    em.remove(folder);
		    // Delete the policy and unbind the resource from this path
		    String policyId = binding.getProperty(path,
			    FactoryResourceProperty.POLICY_ID, false);
		    pap.deletePolicy(policyId);
		    binding.unbind(path);
		    // Notify
		    notification.throwEvent(new Event(path, caller,
			    CollaborationFolder.RESOURCE_NAME, Event
				    .buildEventType(
					    DocumentService.SERVICE_NAME,
					    CollaborationFolder.RESOURCE_NAME,
					    "delete"), ""));
		} else {
		    throw new DocumentServiceException(
			    "No valid answer from the WS.Check logs.");
		}
	    } else {
		throw new DocumentServiceException(
			"Folder has children and cannot be deleted.");
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("unable to delete the folder at path " + path, e);
	    throw new DocumentServiceException(
		    "unable to delete the folder at path " + path, e);
	}
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.document.DocumentService#readFolderContent(java.lang.String)
     */
    @Override
    public CollaborationFolder readFolderContent(String path)
	    throws DocumentServiceException {
	logIt("readFolderContent(...) called");
	logIt("params : path=" + path);
	CollaborationFolder folder = null;
	try {
	    folder = readFolder(path);
	    if (folder != null) {
		// Get folders
		Vector<ListItem> list = new Vector<ListItem>();
		String servicePattern = DocumentService.SERVICE_NAME;
		String typePattern = CollaborationFolder.RESOURCE_NAME;
		String[] foldersArray = browser.listChildrenOfType(path,
			servicePattern, typePattern);
		if (foldersArray != null && foldersArray.length > 0) {
		    for (int i = 0; i < foldersArray.length; i++) {
			logIt("child #" + i + ". " + foldersArray[i]);
			CollaborationFolder cf = readFolder(foldersArray[i]);
			logIt(cf.toString());
			ListItem li = new ListItem(cf.getId(), cf.getName(), cf
				.getResourcePath(), "folder");
			// listMap.put("folder_"+cf.getId(),li);
			list.add(li);
		    }
		}
		// Get documents
		typePattern = Document.RESOURCE_NAME;
		String[] docsArray = browser.listChildrenOfType(path,
			servicePattern, typePattern);
		if (docsArray != null && docsArray.length > 0) {
		    for (int i = 0; i < docsArray.length; i++) {
			logIt("child #" + i + ". " + docsArray[i]);
			Document dc = readDocumentProperties(docsArray[i]);
			logIt(dc.toString());
			ListItem li = new ListItem(dc.getId(), dc.getName(), dc
				.getResourcePath(), "document");
			list.add(li);
		    }
		}
		if (list != null && list.size() > 0) {
		    logIt("The number of items found under " + path + " is "
			    + list.size());
		    folder.setContent(list.toArray(new ListItem[list.size()]));
		}
	    } else {
		throw new DocumentServiceException(
			"unable to retrieve folder of path " + path);
	    }

	} catch (Exception e) {
	    logger
		    .error("unable to retrieve content folder of path " + path,
			    e);
	    throw new DocumentServiceException(
		    "unable to retrieve content folder of path " + path, e);
	}
	return folder;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.document.DocumentService#getFolderContent(java.lang.String)
     */
    @Override
    public ListItem[] getFolderContent(String path)
	    throws DocumentServiceException {
	logIt("getFolderDocuments(...) called");
	logIt("params : path=" + path);
	ListItem[] contList = null;
	try {
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new DocumentServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    // Check if given path is valid folder
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, CollaborationFolder.RESOURCE_NAME);
	    // Get folders
	    Vector<ListItem> listVector = new Vector<ListItem>();
	    String servicePattern = DocumentService.SERVICE_NAME;
	    String typePattern = CollaborationFolder.RESOURCE_NAME;
	    String[] foldersArray = browser.listChildrenOfType(path,
		    servicePattern, typePattern);
	    if (foldersArray != null && foldersArray.length > 0) {
		for (int i = 0; i < foldersArray.length; i++) {
		    logIt("child #" + i + ". " + foldersArray[i]);
		    CollaborationFolder cf = readFolder(foldersArray[i]);
		    logIt(cf.toString());
		    ListItem li = new ListItem(cf.getId(), cf.getName(), cf
			    .getResourcePath(), "folder");
		    listVector.add(li);
		}
	    }
	    // Get documents
	    typePattern = Document.RESOURCE_NAME;
	    String[] docsArray = browser.listChildrenOfType(path,
		    servicePattern, typePattern);
	    if (docsArray != null && docsArray.length > 0) {
		for (int i = 0; i < docsArray.length; i++) {
		    logIt("child #" + i + ". " + docsArray[i]);
		    Document dc = readDocumentProperties(docsArray[i]);
		    logIt(dc.toString());
		    ListItem li = new ListItem(dc.getId(), dc.getName(), dc
			    .getResourcePath(), "document");
		    listVector.add(li);
		}
	    }
	    if (listVector != null && listVector.size() > 0) {
		logIt("The number of items found under " + path + " is "
			+ listVector.size());
		contList = listVector.toArray(new ListItem[listVector.size()]);
	    }
	} catch (Exception e) {
	    logger
		    .error("unable to retrieve content folder of path " + path,
			    e);
	    throw new DocumentServiceException(
		    "unable to retrieve content folder of path " + path, e);
	}
	return contList;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.document.DocumentService#getFolderDocuments(java.lang.String)
     */
    @Override
    public Document[] getFolderDocuments(String path)
	    throws DocumentServiceException {
	logIt("getFolderDocuments(...) called");
	logIt("params : path=" + path);
	Document[] docList = null;
	try {
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new DocumentServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    // Check if given path is valid folder
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, CollaborationFolder.RESOURCE_NAME);
	    // Get documents
	    String servicePattern = DocumentService.SERVICE_NAME;
	    String typePattern = Document.RESOURCE_NAME;
	    String[] docsArray = browser.listChildrenOfType(path,
		    servicePattern, typePattern);
	    if (docsArray != null && docsArray.length > 0) {
		docList = new Document[docsArray.length];
		for (int i = 0; i < docsArray.length; i++) {
		    logIt("child #" + i + ". " + docsArray[i]);
		    docList[i] = readDocumentProperties(docsArray[i]);
		    logIt(docList[i].toString());
		}
	    }
	} catch (Exception e) {
	    logger.error("unable to find documents for path " + path, e);
	    throw new DocumentServiceException(
		    "unable to find documents for path " + path, e);
	}
	return docList;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.document.DocumentService#getSubfolders(java.lang.String)
     */
    @Override
    public CollaborationFolder[] getSubfolders(String path)
	    throws DocumentServiceException {
	logIt("getSubfolders(...) called");
	logIt("params : path=" + path);
	CollaborationFolder[] folderList = null;
	try {
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new DocumentServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    // Check if given path is valid folder
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, CollaborationFolder.RESOURCE_NAME);
	    // Get folders
	    String servicePattern = DocumentService.SERVICE_NAME;
	    String typePattern = CollaborationFolder.RESOURCE_NAME;
	    String[] foldersArray = browser.listChildrenOfType(path,
		    servicePattern, typePattern);
	    if (foldersArray != null && foldersArray.length > 0) {
		folderList = new CollaborationFolder[foldersArray.length];
		for (int i = 0; i < foldersArray.length; i++) {
		    logIt("child #" + i + ". " + foldersArray[i]);
		    folderList[i] = readFolder(foldersArray[i]);
		    logIt(folderList[i].toString());
		}
	    }
	} catch (Exception e) {
	    logger.error("unable to find subfolders for path " + path, e);
	    throw new DocumentServiceException(
		    "unable to find subfolders for path " + path, e);
	}
	return folderList;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.collaboration.document.DocumentService#uploadDocumentVersion(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, byte[])
     */
    @Override
    public void uploadDocumentVersion(String path, String name, String version,
	    String status, String fileName, String mimeType,
	    byte[] binaryContent) throws DocumentServiceException {

	logIt("uploadDocumentVersion(...) called");
	logIt("params : path=" + path + ", name=" + name + " version "
		+ version);

	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new DocumentServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "update");
	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, Document.RESOURCE_NAME);
	    // Find the entity
	    Document doc = em.find(Document.class, identifier.getId());
	    if (doc == null) {
		throw new DocumentServiceException(
			"unable to find a document for id "
				+ identifier.getId());
	    }
	    // Call the WS to update the document
	    HashMap<String, String> values = documentWS.uploadDocumentVersion(
		    doc.getId(), name, version, status, fileName, mimeType,
		    binaryContent);
	    if (values != null && !values.isEmpty()) {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
		    throw new DocumentServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}
		// In factory we persist only id,path,name,parentFolderID and
		// author. So only name is changeable.
		doc.setName(name);
		em.merge(doc);
		binding.setProperty(path,
			FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System
				.currentTimeMillis()
				+ "");
		// notify
		notification.throwEvent(new Event(path, caller,
			Document.RESOURCE_NAME, Event.buildEventType(
				DocumentService.SERVICE_NAME,
				Document.RESOURCE_NAME, "update"), ""));
		//
		logIt(doc.toString());
	    } else {
		throw new DocumentServiceException(
			"No valid answer from the WS.Check logs.");
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("unable to upload new version at path " + path, e);
	    throw new DocumentServiceException(
		    "unable to upload new version at path " + path, e);
	}
    }

    /**
     * Sets the entity manager.
     * 
     * @param em the new entity manager
     */
    @PersistenceContext(unitName = "DocumentServiceBean")
    public void setEntityManager(EntityManager em) {
	this.em = em;
    }

    /**
     * Gets the entity manager.
     * 
     * @return the entity manager
     */
    public EntityManager getEntityManager() {
	return this.em;
    }

    /**
     * Sets the session context.
     * 
     * @param ctx the new session context
     */
    @Resource
    public void setSessionContext(SessionContext ctx) {
	this.ctx = ctx;
    }

    /**
     * Gets the session context.
     * 
     * @return the session context
     */
    public SessionContext getSessionContext() {
	return this.ctx;
    }

    // @EJB(name = "BindingService")
    /**
     * Sets the binding service.
     * 
     * @param binding the new binding service
     */
    @EJB
    public void setBindingService(BindingService binding) {
	this.binding = binding;
    }

    /**
     * Gets the binding service.
     * 
     * @return the binding service
     */
    public BindingService getBindingService() {
	return this.binding;
    }

    // @EJB(name = "PEPService")
    /**
     * Sets the pEP service.
     * 
     * @param pep the new pEP service
     */
    @EJB
    public void setPEPService(PEPService pep) {
	this.pep = pep;
    }

    /**
     * Gets the pEP service.
     * 
     * @return the pEP service
     */
    public PEPService getPEPService() {
	return this.pep;
    }

    // @EJB(name = "PAPService")
    /**
     * Sets the pAP service.
     * 
     * @param pap the new pAP service
     */
    @EJB
    public void setPAPService(PAPService pap) {
	this.pap = pap;
    }

    /**
     * Gets the pAP service.
     * 
     * @return the pAP service
     */
    public PAPService getPAPService() {
	return this.pap;
    }

    // @EJB(name = "NotificationService")
    /**
     * Sets the notification service.
     * 
     * @param notification the new notification service
     */
    @EJB
    public void setNotificationService(NotificationService notification) {
	this.notification = notification;
    }

    /**
     * Gets the notification service.
     * 
     * @return the notification service
     */
    public NotificationService getNotificationService() {
	return this.notification;
    }

    // @EJB(name = "MembershipService")
    /**
     * Sets the membership service.
     * 
     * @param membership the new membership service
     */
    @EJB
    public void setMembershipService(MembershipService membership) {
	this.membership = membership;
    }

    /**
     * Gets the membership service.
     * 
     * @return the membership service
     */
    public MembershipService getMembershipService() {
	return this.membership;
    }

    /**
     * Gets the browser.
     * 
     * @return the browser
     */
    public BrowserService getBrowser() {
	return browser;
    }

    // @EJB(name = "BrowserService")
    /**
     * Sets the browser.
     * 
     * @param browser the new browser
     */
    @EJB
    public void setBrowser(BrowserService browser) {
	this.browser = browser;
    }

    /**
     * Gets the core.
     * 
     * @return the core
     */
    public CoreService getCore() {
	return core;
    }

    // @EJB(name = "CoreService")
    /**
     * Sets the core.
     * 
     * @param core the new core
     */
    @EJB
    public void setCore(CoreService core) {
	this.core = core;
    }

    /**
     * Gets the document ws.
     * 
     * @return the document ws
     */
    public DocumentWService getDocumentWS() {
	return documentWS;
    }

    /**
     * Sets the document ws.
     * 
     * @param documentWS the new document ws
     */
    @EJB(name = "DocumentWService")
    public void setDocumentWS(DocumentWService documentWS) {
	this.documentWS = documentWS;
    }

    /**
     * Check resource type.
     * 
     * @param identifier the identifier
     * @param resourceType the resource type
     * 
     * @throws MembershipServiceException the membership service exception
     */
    private void checkResourceType(FactoryResourceIdentifier identifier,
	    String resourceType) throws MembershipServiceException {
	if (!identifier.getService().equals(getServiceName())) {
	    throw new MembershipServiceException("resource identifier "
		    + identifier + " does not refer to service "
		    + getServiceName());
	}
	if (!identifier.getType().equals(resourceType)) {
	    throw new MembershipServiceException("resource identifier "
		    + identifier + " does not refer to a resource of type "
		    + resourceType);
	}
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.FactoryService#getResourceTypeList()
     */
    @Override
    public String[] getResourceTypeList() {
	return RESOURCE_TYPE_LIST;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.FactoryService#getServiceName()
     */
    @Override
    public String getServiceName() {
	return SERVICE_NAME;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.FactoryService#findResource(java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FactoryResource findResource(String path) throws FactoryException {
	logIt("findResource(...) called");
	logIt("params : path=" + path);

	try {
	    FactoryResourceIdentifier identifier = binding.lookup(path);

	    if (!identifier.getService().equals(SERVICE_NAME)) {
		throw new CoreServiceException("Resource " + identifier
			+ " is not managed by " + SERVICE_NAME);
	    }

	    if (identifier.getType().equals(Document.RESOURCE_NAME)) {
		return readDocument(path);
	    } else if (identifier.getType().equals(
		    CollaborationFolder.RESOURCE_NAME)) {
		return readFolder(path);
	    }

	    throw new CoreServiceException("Resource " + identifier
		    + " is not managed by " + SERVICE_NAME);

	} catch (Exception e) {
	    logger.error("unable to find the resource at path " + path, e);
	    throw new CoreServiceException(
		    "unable to find the resource at path " + path, e);
	}
    }

    /**
     * Log it.
     * 
     * @param message the message
     */
    private void logIt(String message) {
	if (logger.isInfoEnabled()) {
	    logger.info(message);
	}
    }

}
