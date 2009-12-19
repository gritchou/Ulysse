package org.qualipso.factory.collaboration.test.sessionbean;

import static org.hamcrest.text.StringContains.containsString;
import static org.qualipso.factory.collaboration.test.jmock.action.SaveParamsAction.saveParams;
import static org.qualipso.factory.collaboration.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;

import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.entity.Node;
import org.qualipso.factory.browser.BrowserService;
import org.qualipso.factory.collaboration.beans.DocumentDetails;
import org.qualipso.factory.collaboration.document.DocumentService;
import org.qualipso.factory.collaboration.document.DocumentServiceBean;
import org.qualipso.factory.collaboration.document.entity.CollaborationFolder;
import org.qualipso.factory.collaboration.document.entity.Document;
import org.qualipso.factory.collaboration.utils.CollaborationUtils;
import org.qualipso.factory.collaboration.ws.DocumentWService;
import org.qualipso.factory.collaboration.ws.beans.DocumentDTO;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;

import com.bm.testsuite.BaseSessionBeanFixture;

// TODO: Auto-generated Javadoc
/**
 * The Class DocumentServiceTest.
 */
public class DocumentServiceTest extends BaseSessionBeanFixture<DocumentServiceBean>
{

    /** The logger. */
    private static Log logger = LogFactory.getLog(DocumentServiceTest.class);

    /** The Constant usedBeans. */
    @SuppressWarnings("unchecked")
    private static final Class[] usedBeans = { Document.class,CollaborationFolder.class, Node.class, Profile.class };

    /** The mockery. */
    private Mockery mockery;
    
    /** The binding. */
    private BindingService binding;
    
    /** The membership. */
    private MembershipService membership;
    
    /** The pep. */
    private PEPService pep;
    
    /** The pap. */
    private PAPService pap;
    
    /** The notification. */
    private NotificationService notification;
    
    /** The browser. */
    private BrowserService browser;
    
    /** The core. */
    private CoreService core;
    
    /** The document ws. */
    private DocumentWService documentWS;
    
    /**
     * Instantiates a new document service test.
     */
    public DocumentServiceTest()
    {
	super(DocumentServiceBean.class, usedBeans);
    }

    /* (non-Javadoc)
     * @see com.bm.testsuite.BaseSessionBeanFixture#setUp()
     */
    public void setUp() throws Exception
    {
	super.setUp();
	logger.debug("injecting mock partners session beans");
	mockery = new Mockery();
	binding = mockery.mock(BindingService.class);
	membership = mockery.mock(MembershipService.class);
	pep = mockery.mock(PEPService.class);
	pap = mockery.mock(PAPService.class);
	notification = mockery.mock(NotificationService.class);
	browser = mockery.mock(BrowserService.class);
	core = mockery.mock(CoreService.class);
	documentWS = mockery.mock(DocumentWService.class);
	getBeanToTest().setMembershipService(membership);
	getBeanToTest().setNotificationService(notification);
	getBeanToTest().setBindingService(binding);
	getBeanToTest().setPEPService(pep);
	getBeanToTest().setPAPService(pap);
	getBeanToTest().setBrowser(browser);
	getBeanToTest().setCore(core);
	getBeanToTest().setDocumentWS(documentWS);
    }
    

    /**
     * Test crud document folder.
     */
    public void testCRUDDocumentFolder()
    {
	logger.debug("****************************************************************");
	logger.debug("testing CRUD DocumentFolder(...)");
	logger.debug("****************************************************************");
	final Sequence sequence1 = mockery.sequence("sequence1");
	final Vector<Object> paramDoc = new Vector<Object>();
	final Vector<Object> paramsFolder = new Vector<Object>();
	final Vector<Object> paramsFolderChild = new Vector<Object>();

	try
	{
	    final String tempText = "Lorem Ipsum "+System.currentTimeMillis();
	    String randomFolder = "F"+ System.currentTimeMillis();
	    final String randomFolderNorm = CollaborationUtils.normalizeForPath(randomFolder);
//	    String randomDocument = "D"+ System.currentTimeMillis();
//	    String randomDocumentNorm = CollaborationUtils.normalizeForPath(randomDocument);
//	    final String docPath = "/documents/"+randomFolderNorm+"/"+randomDocumentNorm;
	    //
	    final String fileName = "D_" + System.currentTimeMillis() + ".txt";
	    final String fileNameUpd= "UP" + System.currentTimeMillis() + ".txt";
	    final String folderId = CollaborationUtils.normalizeForPath(UUID.randomUUID().toString());
	    final String docFolderId = CollaborationUtils.normalizeForPath(UUID.randomUUID().toString());
	    final String docId = CollaborationUtils.normalizeForPath(UUID.randomUUID().toString());
	    final String docPath = "/documents/"+randomFolderNorm+"/"+docId;
	    final String docParentPath = "/documents/"+randomFolderNorm;
	    //
	    DocumentService service = getBeanToTest();
	    // TEST Create Folder
	    mockery.checking(new Expectations()
	    {
		{
		    //
		    oneOf(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/jayblanc"));inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal("/")), with(equal("create")));inSequence(sequence1);
		    // mock WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    resultMap.put("folderID", folderId);
		    oneOf(documentWS).createFolder(with(any(String.class)),with(any(String.class)),with(any(String.class)));will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)),with(equal("/documents")));will(saveParams(paramsFolder));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal("/documents")),with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)),with(any(String.class)));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal("/documents")),with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)),with(any(String.class)));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal("/documents")),with(equal(FactoryResourceProperty.AUTHOR)),with(equal("/profiles/jayblanc")));inSequence(sequence1);
		    oneOf(pap).createPolicy(with(any(String.class)),with(containsString("/documents")));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal("/documents")),with(equal(FactoryResourceProperty.OWNER)),with(equal("/profiles/jayblanc")));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal("/documents")),with(equal(FactoryResourceProperty.POLICY_ID)),with(any(String.class)));inSequence(sequence1);
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(DocumentService.SERVICE_NAME,CollaborationFolder.RESOURCE_NAME,"create"))));inSequence(sequence1);
		}
	    });
	    //
	    String fPath = service.createFolder("/", "documents", tempText);
	    assertNotNull(fPath);
	    
	    
	    // TEST Create Folder
	    mockery.checking(new Expectations()
	    {
		{
		    //
		    oneOf(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/jayblanc"));inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal("/documents")), with(equal("create")));inSequence(sequence1);
		    // read parent
		    oneOf(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/jayblanc"));inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal("/documents")), with(equal("read")));inSequence(sequence1);
		    oneOf(binding).lookup(with(equal("/documents")));will(returnValue(paramsFolder.get(0)));inSequence(sequence1);
		    // mock WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    resultMap.put("title", "documents");
		    resultMap.put("abstract", tempText);
		    resultMap.put("dateCreated", "2009-09-28");
		    oneOf(documentWS).readFolder(folderId);will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(DocumentService.SERVICE_NAME,CollaborationFolder.RESOURCE_NAME,"read"))));inSequence(sequence1);
		    // mock WS
		    resultMap.put("folderID",docFolderId);
		    oneOf(documentWS).createFolder(with(any(String.class)), with(any(String.class)), with(any(String.class)));will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)),with(equal("/documents/"+randomFolderNorm)));will(saveParams(paramsFolderChild));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal("/documents/"+randomFolderNorm)),with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)),with(any(String.class)));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal("/documents/"+randomFolderNorm)),with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)),with(any(String.class)));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal("/documents/"+randomFolderNorm)),with(equal(FactoryResourceProperty.AUTHOR)),with(equal("/profiles/jayblanc")));inSequence(sequence1);
		    oneOf(pap).createPolicy(with(any(String.class)),with(containsString("/documents/"+randomFolderNorm)));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal("/documents/"+randomFolderNorm)),with(equal(FactoryResourceProperty.OWNER)),with(equal("/profiles/jayblanc")));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal("/documents/"+randomFolderNorm)),with(equal(FactoryResourceProperty.POLICY_ID)),with(any(String.class)));inSequence(sequence1);
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(DocumentService.SERVICE_NAME,CollaborationFolder.RESOURCE_NAME,"create"))));inSequence(sequence1);
		}
	    });
	    //
	    fPath = service.createFolder("/documents", randomFolder, tempText);
	    assertNotNull(fPath);
	    // TEST Read Folder
	    mockery.checking(new Expectations()
	    {
		{
		    // Reading the first folder :
		    oneOf(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/jayblanc"));inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal("/documents/"+randomFolderNorm)), with(equal("read")));inSequence(sequence1);
		    oneOf(binding).lookup(with(equal("/documents/"+randomFolderNorm)));will(returnValue(paramsFolderChild.get(0)));inSequence(sequence1);
		    // mock WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    resultMap.put("abstract", tempText);
		    resultMap.put("dateCreated", "2009-09-28");
		    oneOf(documentWS).readFolder(docFolderId);will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(DocumentService.SERVICE_NAME,CollaborationFolder.RESOURCE_NAME,"read"))));inSequence(sequence1);
		}
	    });
	    CollaborationFolder folder = service.readFolder("/documents/"+randomFolderNorm);
	    assertNotNull(folder);
	    logger.debug("***********************************************************");
	    logger.debug("              Test CRUD operations for Documents");
	    logger.debug("***********************************************************");
	    
	    
	    //TEST Create Document
	    mockery.checking(new Expectations()
	    {
		{
		    	//Profile check in create document
		    	oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
		    	// Security check in create document
		    	oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal(docParentPath)), with(equal("create"))); inSequence(sequence1);
		    	//Read parent folder
		    	oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
		    	oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal(docParentPath)), with(equal("read")));inSequence(sequence1);
		    	oneOf(binding).lookup(with(equal(docParentPath)));will(returnValue(paramsFolderChild.get(0)));inSequence(sequence1);
		    	 // mock WS
			HashMap<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("statusCode", "SUCCESS");
			resultMap.put("statusMessage", "done");
			resultMap.put("abstract", tempText);
			resultMap.put("dateCreated", "2009-09-28");
			oneOf(documentWS).readFolder(docFolderId);will(returnValue(resultMap));inSequence(sequence1);
		    	//
		    	oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(DocumentService.SERVICE_NAME,CollaborationFolder.RESOURCE_NAME,"read"))));inSequence(sequence1);
		    	// mock WS
		    	resultMap.put("documentId",docId);
		    	oneOf(documentWS).createDocument(docFolderId, "My Doc 1.1", "2009-08-27",CollaborationUtils.TYPE_8, "qualipso,factory", "1.0",
		    		CollaborationUtils.STATUS_DRAFT, fileName, "text/plain", "TG9yZW0gaXBzdW0=".getBytes());will(returnValue(resultMap));inSequence(sequence1);
		    	//Create and bind document
			oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal(docPath))); will(saveParams(paramDoc));  inSequence(sequence1);
			//
			oneOf(binding).setProperty(with(equal(docPath)), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
			oneOf(binding).setProperty(with(equal(docPath)), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
			oneOf(binding).setProperty(with(equal(docPath)), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
			oneOf(pap).createPolicy(with(any(String.class)), with(containsString(docPath))); inSequence(sequence1);
			oneOf(binding).setProperty(with(equal(docPath)), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
			oneOf(binding).setProperty(with(equal(docPath)), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
			oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(DocumentService.SERVICE_NAME,Document.RESOURCE_NAME,"create")))); inSequence(sequence1);
		}
	    });
	    DocumentDetails ddt = new DocumentDetails( "My Doc 1.1", "2009-08-27",
		    CollaborationUtils.TYPE_8, "qualipso,factory", "1.0",
		    CollaborationUtils.STATUS_DRAFT, fileName, "text/plain", "TG9yZW0gaXBzdW0=".getBytes());
	    service.createDocument(docParentPath, ddt);
	    //TEST Read Document
	    mockery.checking(new Expectations()
	    {
		{
		    oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal(docPath)), with(equal("read"))); inSequence(sequence1);
		    oneOf(binding).lookup(with(equal(docPath))); will(returnValue(paramDoc.get(0))); inSequence(sequence1);
		    // mock the WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    //
		    DocumentDTO doc = new DocumentDTO();
		    doc.setId(docId);
		    doc.setDate("2009-08-27");
		    doc.setStatus(CollaborationUtils.STATUS_DRAFT);
		    doc.setVersion("1.0");
		    doc.setKeywords("qualipso,factory");
		    doc.setSize("21");
		    doc.setType(CollaborationUtils.TYPE_8);
		    doc.setMimeType("text/plain");
		    resultMap.put("document", doc);
		    //
		    doc = new DocumentDTO();
		    doc.setMimeType("text/plain");
		    doc.setFileName(fileName);
		    doc.setBinaryContent("TG9yZW0gaXBzdW0=".getBytes());
		    doc.setId(docId);
		    resultMap.put("documentContent", doc);
		    oneOf(documentWS).readDocument(docId);will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(DocumentService.SERVICE_NAME,Document.RESOURCE_NAME,"read")))); inSequence(sequence1);
		}
	    });
	    
	    Document mydoc = service.readDocument(docPath);
	    assertNotNull(mydoc);
	    //TEST Update Document
	    mockery.checking(new Expectations()
	    {
		{
		    //Update the doc : 
		    oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal(docPath)), with(equal("update"))); inSequence(sequence1);
		    oneOf(binding).lookup(with(equal(docPath))); will(returnValue(paramDoc.get(0))); inSequence(sequence1);
		    // mock the WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    oneOf(documentWS).updateDocument(docId, "test123",
			    CollaborationUtils.TYPE_8, "qualpso,factory,documents",
			    CollaborationUtils.STATUS_DRAFT, fileNameUpd, "text/plain",
			    "TG9yZW0gaXBzdW0=".getBytes());will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(binding).setProperty(with(equal(docPath)), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(DocumentService.SERVICE_NAME,Document.RESOURCE_NAME,"update")))); inSequence(sequence1);
		}
	    });
	    
	    service.updateDocument(docPath, "test123",
		    CollaborationUtils.TYPE_8, "qualpso,factory,documents",
		    CollaborationUtils.STATUS_DRAFT, fileNameUpd, "text/plain",
		    "TG9yZW0gaXBzdW0=".getBytes());
	    mockery.checking(new Expectations()
	    {
		{
		    // Delete the document :
		    oneOf(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/jayblanc"));inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal(docPath)), with(equal("delete")));inSequence(sequence1);
		    oneOf(binding).lookup(with(equal(docPath)));will(returnValue(paramDoc.get(0)));inSequence(sequence1);
		    // mock WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    oneOf(documentWS).deleteDocument(docId);will(returnValue(resultMap));inSequence(sequence1);
		    oneOf(binding).getProperty(with(equal(docPath)), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); inSequence(sequence1);
		    oneOf(pap).deletePolicy(with(any(String.class))); inSequence(sequence1);
		    oneOf(binding).unbind(with(equal(docPath)));inSequence(sequence1);
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(DocumentService.SERVICE_NAME,Document.RESOURCE_NAME,"delete"))));inSequence(sequence1);
		}
	    });
	    service.deleteDocument(docPath);
	    
	    // Delete folder
	    mockery.checking(new Expectations()
	    {
		{
		    oneOf(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/jayblanc"));inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal("/documents/"+randomFolderNorm)), with(equal("delete")));inSequence(sequence1);
		    oneOf(binding).lookup(with(equal("/documents/"+randomFolderNorm)));will(returnValue(paramsFolderChild.get(0)));inSequence(sequence1);
		    // mock broswer.hasChildren(()
		    oneOf(browser).hasChildren(with(any(String.class)));will(returnValue(false));inSequence(sequence1);
		    // mock WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    oneOf(documentWS).deleteFolder(docFolderId);will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(binding).getProperty(with(equal("/documents/"+randomFolderNorm)), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); inSequence(sequence1);
		    oneOf(pap).deletePolicy(with(any(String.class))); inSequence(sequence1);
		    oneOf(binding).unbind(with(equal("/documents/"+randomFolderNorm)));inSequence(sequence1);
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(DocumentService.SERVICE_NAME,CollaborationFolder.RESOURCE_NAME,"delete"))));inSequence(sequence1);
		}
	    });

	    service.deleteFolder("/documents/"+randomFolderNorm);
	    //
	    // Delete test folder
	    mockery.checking(new Expectations()
	    {
		{
		    oneOf(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/jayblanc"));inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal("/documents")), with(equal("delete")));inSequence(sequence1);
		    oneOf(binding).lookup(with(equal("/documents")));will(returnValue(paramsFolder.get(0)));inSequence(sequence1);
		    // mock broswer.hasChildren(()
		    oneOf(browser).hasChildren(with(any(String.class)));will(returnValue(false));inSequence(sequence1);
		    // mock WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    oneOf(documentWS).deleteFolder(folderId);will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(binding).getProperty(with(equal("/documents")), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); inSequence(sequence1);
		    oneOf(pap).deletePolicy(with(any(String.class))); inSequence(sequence1);
		    oneOf(binding).unbind(with(equal("/documents")));inSequence(sequence1);
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(DocumentService.SERVICE_NAME,CollaborationFolder.RESOURCE_NAME,"delete"))));inSequence(sequence1);
		}
	    });

	    service.deleteFolder("/documents");
	    
	    mockery.assertIsSatisfied();

	} catch (Exception e)
	{
	    e.printStackTrace();
	    logger.error(e.getMessage(), e);
	    fail(e.getMessage());
	}
    }
}
