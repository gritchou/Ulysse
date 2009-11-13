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
import org.qualipso.factory.collaboration.document.entity.CollaborationFolder;
import org.qualipso.factory.collaboration.document.entity.Document;
import org.qualipso.factory.collaboration.document.entity.ListItem;
import org.qualipso.factory.collaboration.utils.CollaborationUtils;
import org.qualipso.factory.collaboration.ws.DocumentWService;
import org.qualipso.factory.collaboration.ws.beans.DocumentDTO;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;

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
    private static Log logger = LogFactory.getLog(DocumentServiceBean.class);

    private BindingService binding;
    private PEPService pep;
    private PAPService pap;
    private NotificationService notification;
    private MembershipService membership;
    private BrowserService browser;
    private CoreService core;
    //
    private SessionContext ctx;
    private EntityManager em;
    //
    private DocumentWService documentWS;

    public DocumentServiceBean() {
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String createDocument(String parentPath, Document document)
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
		String parentFolderID = CollaborationUtils.DEFAULT_FOLDER_ID;
		// Check if folder is a collaboration folder or not.
		// If it is we need to match it with a mermig folder.
		if (!parentPath.equals("/")) {
		    CollaborationFolder parFold = null;
		    try {
			parFold = readFolder(parentPath);
			if (parFold != null) {
			    parentFolderID = parFold.getId();
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
		logIt("Parent folder id " + parentFolderID);
		// We need to find if this folder ID exists on cms (mermig).
		// call the getFolder service?
		// Call the create Documet service
		HashMap<String, String> values = documentWS.createDocument(
			parentFolderID, document.getName(), document.getDate(),
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
		    path = PathHelper.normalize(parentPath) + "/" + newId;
		    Document doc = new Document();
		    doc.setId(newId);// Set the id returned by the MermigWS
		    doc.setResourcePath(path);
		    doc.setName(document.getName());
		    doc.setAuthor(caller.toString());
		    doc.setParentFolderID(parentFolderID);
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

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String createDocumentSimple(String parentPath, String name,
	    String date, String type, String keywords, String version,
	    String status, String fileName, String mimeType,
	    byte[] binaryContent) throws DocumentServiceException {
	logIt("createDocument(...) called");
	logIt("params : path=" + parentPath + ", name=" + name);
	String path = null;
	try {
	    if (name != null && binaryContent != null) {
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
		String parentFolderID = CollaborationUtils.DEFAULT_FOLDER_ID;
		// Check if folder is a collaboration folder or not.
		// If it is we need to match it with a mermig folder.
		if (!parentPath.equals("/")) {
		    CollaborationFolder parFold = null;
		    try {
			parFold = readFolder(parentPath);
			if (parFold != null) {
			    parentFolderID = parFold.getId();
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
		logIt("Parent folder id " + parentFolderID);
		// We need to find if this folder ID exists on cms (mermig).
		// call the getFolder service?
		// Call the create Documet service
		HashMap<String, String> values = documentWS.createDocument(
			parentFolderID, name, date, type, keywords, version,
			status, fileName, mimeType, binaryContent);
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
		    path = PathHelper.normalize(parentPath) + "/" + newId;
		    Document doc = new Document();
		    doc.setId(newId);// Set the id returned by the MermigWS
		    doc.setResourcePath(path);
		    doc.setName(name);
		    doc.setAuthor(caller.toString());
		    doc.setParentFolderID(parentFolderID);
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
		    document.setDate(docWS.getDate());
		    document.setType(docWS.getType());
		    document.setKeywords(docWS.getKeywords());
		    document.setVersion(docWS.getVersion());
		    document.setStatus(docWS.getStatus());
		    document.setSize(docWS.getSize());
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
	    logger.error("unable to read the document properties at path "
		    + path, e);
	    throw new DocumentServiceException(
		    "unable to read the document properties at path " + path, e);
	}
	return document;
    }

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
	    // String parentPath = "/";
	    // if (!path.equals("/"))
	    // {
	    // parentPath = PathHelper.getParentPath(path);
	    // }
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
	    // We need to find if this folder ID exists on cms (mermig). If not.
	    // should we create?
	    // TODO call the readFolder service.
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
	    // TODO return folderdto
	    HashMap<String, String> values = documentWS.readFolder(folder
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
		if (values.get("abstract") != null) {
		    String desc = (String) values.get("abstract");
		    folder.setDescription(desc);
		}
		if (values.get("dateCreated") != null) {
		    String date = (String) values.get("dateCreated");
		    folder.setDate(date);
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
	    typePattern = "Document";
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

    @PersistenceContext(unitName = "DocumentServiceBean")
    public void setEntityManager(EntityManager em) {
	this.em = em;
    }

    public EntityManager getEntityManager() {
	return this.em;
    }

    @Resource
    public void setSessionContext(SessionContext ctx) {
	this.ctx = ctx;
    }

    public SessionContext getSessionContext() {
	return this.ctx;
    }

    // @EJB(name = "BindingService")
    @EJB
    public void setBindingService(BindingService binding) {
	this.binding = binding;
    }

    public BindingService getBindingService() {
	return this.binding;
    }

    // @EJB(name = "PEPService")
    @EJB
    public void setPEPService(PEPService pep) {
	this.pep = pep;
    }

    public PEPService getPEPService() {
	return this.pep;
    }

    // @EJB(name = "PAPService")
    @EJB
    public void setPAPService(PAPService pap) {
	this.pap = pap;
    }

    public PAPService getPAPService() {
	return this.pap;
    }

    // @EJB(name = "NotificationService")
    @EJB
    public void setNotificationService(NotificationService notification) {
	this.notification = notification;
    }

    public NotificationService getNotificationService() {
	return this.notification;
    }

    // @EJB(name = "MembershipService")
    @EJB
    public void setMembershipService(MembershipService membership) {
	this.membership = membership;
    }

    public MembershipService getMembershipService() {
	return this.membership;
    }

    public BrowserService getBrowser() {
	return browser;
    }

    // @EJB(name = "BrowserService")
    @EJB
    public void setBrowser(BrowserService browser) {
	this.browser = browser;
    }

    public CoreService getCore() {
	return core;
    }

    // @EJB(name = "CoreService")
    @EJB
    public void setCore(CoreService core) {
	this.core = core;
    }

    public DocumentWService getDocumentWS() {
	return documentWS;
    }

    @EJB(name = "DocumentWService")
    public void setDocumentWS(DocumentWService documentWS) {
	this.documentWS = documentWS;
    }

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

    @Override
    public String[] getResourceTypeList() {
	return RESOURCE_TYPE_LIST;
    }

    @Override
    public String getServiceName() {
	return SERVICE_NAME;
    }

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

    private void logIt(String message) {
	if (logger.isInfoEnabled()) {
	    logger.info(message);
	}
    }

}
