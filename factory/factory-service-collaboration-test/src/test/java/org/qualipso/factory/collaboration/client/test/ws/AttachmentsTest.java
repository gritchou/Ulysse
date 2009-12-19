package org.qualipso.factory.collaboration.client.test.ws;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Vector;

import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ws.core.StubExt;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.collaboration.client.ws.Bootstrap;
import org.qualipso.factory.collaboration.client.ws.BootstrapServiceException_Exception;
import org.qualipso.factory.collaboration.client.ws.Bootstrap_Service;
import org.qualipso.factory.collaboration.client.ws.Calendar;
import org.qualipso.factory.collaboration.client.ws.CalendarDetails;
import org.qualipso.factory.collaboration.client.ws.CalendarEvent;
import org.qualipso.factory.collaboration.client.ws.CalendarItem;
import org.qualipso.factory.collaboration.client.ws.CalendarServiceException_Exception;
import org.qualipso.factory.collaboration.client.ws.Calendar_Service;
import org.qualipso.factory.collaboration.client.ws.Core;
import org.qualipso.factory.collaboration.client.ws.Core_Service;
import org.qualipso.factory.collaboration.client.ws.DocumentDetails;
import org.qualipso.factory.collaboration.client.ws.DocumentManagement;
import org.qualipso.factory.collaboration.client.ws.DocumentManagement_Service;
import org.qualipso.factory.collaboration.client.ws.DocumentServiceException_Exception;
import org.qualipso.factory.collaboration.client.ws.Forum;
import org.qualipso.factory.collaboration.client.ws.ForumManagement;
import org.qualipso.factory.collaboration.client.ws.ForumManagement_Service;
import org.qualipso.factory.collaboration.client.ws.ForumServiceException_Exception;
import org.qualipso.factory.collaboration.client.ws.StringArray;
import org.qualipso.factory.collaboration.client.ws.ThreadDetails;

// TODO: Auto-generated Javadoc
/**
 * The Class AttachmentsTest.
 */
public class AttachmentsTest {

    /** The logger. */
    private static Log logger = LogFactory.getLog(AttachmentsTest.class);

    /** The calendar port. */
    private Calendar calendarPort;

    /** The document port. */
    private DocumentManagement documentPort;

    /** The forum port. */
    private ForumManagement forumPort;

    /** The core port. */
    private Core corePort;

    /** The folder name. */
    private String folderName = "f" + System.currentTimeMillis();

    /** The test path. */
    private String testPath = "/" + folderName;

    /** The year. */
    private String year = "2010";

    /** The month. */
    private String month = "11";

    /** The lorem ipsum text. */
    private String loremIpsumText = "Lorem Ipsum.";

    /** The doc paths. */
    private Vector<String> docPaths;

    /** The event path. */
    private String eventPath;

    /** The forum path. */
    private String forumPath;

    /** The thread path. */
    private String threadPath;

    /** The num docs. */
    int numDocs = 2;

    /** The num forums. */
    int numForums = 1;

    /**
     * Instantiates a new attachments test.
     */
    public AttachmentsTest() {
	DocumentManagement_Service ds = new DocumentManagement_Service();
	documentPort = ds.getDocumentManagementPort();
	ForumManagement_Service fs = new ForumManagement_Service();
	forumPort = fs.getForumManagementPort();
	Calendar_Service cs = new Calendar_Service();
	calendarPort = cs.getCalendarPort();
	Core_Service core = new Core_Service();
	corePort = core.getCoreServiceBeanPort();
    }

    /**
     * Inits the.
     */
    @BeforeClass
    public static void init() {
	try {
	    Bootstrap port = new Bootstrap_Service()
		    .getBootstrapServiceBeanPort();
	    ((StubExt) port).setConfigName("Standard WSSecurity Client");
	    port.bootstrap();
	} catch (BootstrapServiceException_Exception e) {
	    logger.error("unable to bootstrap factory", e);
	}
    }

    /**
     * Setup.
     * 
     * @throws Exception the exception
     */
    @Before
    public void setup() throws Exception {
	try {
	    logger.debug("********************");
	    logger.debug("SETUP");
	    logger.debug("********************");
	    ((StubExt) documentPort)
		    .setConfigName("Standard WSSecurity Client");
	    ((StubExt) forumPort).setConfigName("Standard WSSecurity Client");
	    ((StubExt) calendarPort)
		    .setConfigName("Standard WSSecurity Client");
	    ((StubExt) corePort).setConfigName("Standard WSSecurity Client");
	    //
	    Map<String, Object> reqContext = ((BindingProvider) documentPort)
		    .getRequestContext();
	    reqContext.put(StubExt.PROPERTY_AUTH_TYPE,
		    StubExt.PROPERTY_AUTH_TYPE_WSSE);
	    reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
	    reqContext.put(BindingProvider.PASSWORD_PROPERTY, "root");
	    //
	    Map<String, Object> reqContextF = ((BindingProvider) forumPort)
		    .getRequestContext();
	    reqContextF.put(StubExt.PROPERTY_AUTH_TYPE,
		    StubExt.PROPERTY_AUTH_TYPE_WSSE);
	    reqContextF.put(BindingProvider.USERNAME_PROPERTY, "root");
	    reqContextF.put(BindingProvider.PASSWORD_PROPERTY, "root");
	    //
	    Map<String, Object> reqContextC = ((BindingProvider) calendarPort)
		    .getRequestContext();
	    reqContextC.put(StubExt.PROPERTY_AUTH_TYPE,
		    StubExt.PROPERTY_AUTH_TYPE_WSSE);
	    reqContextC.put(BindingProvider.USERNAME_PROPERTY, "root");
	    reqContextC.put(BindingProvider.PASSWORD_PROPERTY, "root");
	    //
	    Map<String, Object> reqContextCore = ((BindingProvider) corePort)
		    .getRequestContext();
	    reqContextCore.put(StubExt.PROPERTY_AUTH_TYPE,
		    StubExt.PROPERTY_AUTH_TYPE_WSSE);
	    reqContextCore.put(BindingProvider.USERNAME_PROPERTY, "root");
	    reqContextCore.put(BindingProvider.PASSWORD_PROPERTY, "root");
	    //
	    logger.debug("********************");
	    logger.debug("Create Collaboration Folder:" + testPath);
	    String tmpPath = documentPort.createFolder("/", folderName,
		    "Lorem ipsum");
	    assertNotNull(tmpPath);
	    logger.debug("********************");
	    logger.debug("DOCUMENT");
	    logger.debug("********************");
	    docPaths = new Vector<String>();
	    for (int k = 0; k < numDocs; k++) {
		String documentName = "document" + k;
		String fileName = "D_" + k + System.currentTimeMillis()
			+ ".txt";
		DocumentDetails dd = new DocumentDetails();
		dd.setName(documentName);
		dd.setDate("2009-12-08");
		dd.setBinaryContent("TG9yZW0gaXBzdW0=".getBytes());
		dd.setFileName(fileName);
		dd.setKeywords("qualipso");
		dd.setMimeType("text/plain");
		dd.setStatus("DRAFT");
		dd.setVersion("1.0");
		dd.setType("Readme");
		String docPath = documentPort.createDocument(testPath, dd);
		logger.debug("Created document" + docPath);
		docPaths.add(docPath);
	    }
	    logger.debug("********************");
	    logger.debug("FORUMS");
	    logger.debug("********************");
	    String forumName = "fr" + System.currentTimeMillis();
	    forumPath = forumPort.createForum(testPath, forumName);
	    logger.debug("Created forum " + forumPath);

	    logger.debug("********************");
	    logger.debug("EVENTS");
	    logger.debug("********************");
	    String eventName = "E" + System.currentTimeMillis();
	    logger.debug("Create event for " + year + "-" + month
		    + "-12 that occurs once");
	    CalendarDetails cd = new CalendarDetails();
	    cd.setContactEmail("gstro@delos.eurodyn.com");
	    cd.setContactName("strusos");
	    cd.setContactPhone("2108094500");
	    cd.setDate(year + "-" + month + "-05");
	    cd.setEndTime("18:00:00");
	    cd.setLocation("Athens");
	    cd.setName(eventName);
	    cd.setRecurrence("once");
	    cd.setTimes(1);
	    cd.setStartTime("10:00:00");
	    StringArray paths = calendarPort.createEvent(testPath, cd);
	    assertNotNull(paths);
	    eventPath = paths.getItem().get(0);
	    logger.debug("********************");
	} catch (Exception e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	}
    }

    /**
     * Test attachments.
     */
    @Test
    public void testAttachments() {
	try {
	    logger.debug("********************");

	    logger.debug("Attach forum " + forumPath + " to event" + eventPath);
	    calendarPort.attachForumToEvent(eventPath, forumPath);
	    if (docPaths != null && docPaths.size() > 0) {

		logger.debug("Attach documents to event" + eventPath);
		String[] docArray = docPaths
			.toArray(new String[docPaths.size()]);
		StringArray docs = new StringArray();
		for (int i = 0; i < docArray.length; i++) {
		    docs.getItem().add(docArray[i]);
		    logger.debug("Will add " + docArray[i]);
		}
		// iterate docs:
		for (int n = 0; n < docs.getItem().size(); n++) {
		    String cd = (String) docs.getItem().get(n);
		    logger.debug(cd);
		    documentPort.readDocumentProperties(cd);
		}
		// Get calendar to check if attachments are set ok.
		CalendarItem ev = calendarPort.readEvent(eventPath);
		assertNotNull(ev);
		Forum forum = forumPort.readForum(forumPath);
		assertNotNull(forum);
		CalendarEvent cEv = new CalendarEvent();
		cEv.setName(ev.getName());
		cEv.setContactEmail(ev.getContactEmail());
		cEv.setContactName(ev.getContactName());
		cEv.setContactPhone(ev.getContactPhone());
		cEv.setDate(ev.getDate());
		cEv.setEndTime(ev.getEndTime());
		cEv.setLocation(ev.getLocation());
		cEv.setName(ev.getName());
		cEv.setStartTime(ev.getStartTime());
		//
		calendarPort.updateEventWithAttachments(eventPath,cEv, docs, forumPath);
		// calendarPort.atttachDocumentToEvent(eventPath, docs);
		ev = calendarPort.readEvent(eventPath);
		assertNotNull(ev);
		assertNotNull(ev.getForum());
		assertNotNull(ev.getAttachments());
		ThreadDetails threadMessage = new ThreadDetails();
		threadMessage.setAuthor("qualipsouser");
		threadMessage.setDatePosted(year + "-" + month + "-12");
		threadMessage.setForumId(forum.getId());
		threadMessage.setName("tr" + System.currentTimeMillis());
		threadMessage.setIsReply(false);
		threadMessage.setMessageBody(loremIpsumText);
		threadPath = forumPort.createThreadMessageWithAttachments(
			forumPath, threadMessage, docs);
	    }
	    logger.debug("********************");
	    //
	} catch (CalendarServiceException_Exception e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	} catch (DocumentServiceException_Exception e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	} catch (ForumServiceException_Exception e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	}
    }

    /**
     * Teardown.
     * 
     * @throws Exception the exception
     */
    @After
    public void teardown() throws Exception {
	try {
	    logger.debug("********************");
	    logger.debug("CLEAN");
	    logger.debug("********************");
	    ((StubExt) documentPort)
		    .setConfigName("Standard WSSecurity Client");
	    ((StubExt) forumPort).setConfigName("Standard WSSecurity Client");
	    ((StubExt) calendarPort)
		    .setConfigName("Standard WSSecurity Client");
	    ((StubExt) corePort).setConfigName("Standard WSSecurity Client");
	    //
	    Map<String, Object> reqContext = ((BindingProvider) documentPort)
		    .getRequestContext();
	    reqContext.put(StubExt.PROPERTY_AUTH_TYPE,
		    StubExt.PROPERTY_AUTH_TYPE_WSSE);
	    reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
	    reqContext.put(BindingProvider.PASSWORD_PROPERTY, "root");
	    //
	    Map<String, Object> reqContextF = ((BindingProvider) forumPort)
		    .getRequestContext();
	    reqContextF.put(StubExt.PROPERTY_AUTH_TYPE,
		    StubExt.PROPERTY_AUTH_TYPE_WSSE);
	    reqContextF.put(BindingProvider.USERNAME_PROPERTY, "root");
	    reqContextF.put(BindingProvider.PASSWORD_PROPERTY, "root");
	    //
	    Map<String, Object> reqContextCore = ((BindingProvider) corePort)
		    .getRequestContext();
	    reqContextCore.put(StubExt.PROPERTY_AUTH_TYPE,
		    StubExt.PROPERTY_AUTH_TYPE_WSSE);
	    reqContextCore.put(BindingProvider.USERNAME_PROPERTY, "root");
	    reqContextCore.put(BindingProvider.PASSWORD_PROPERTY, "root");
	    //
	    Map<String, Object> reqContextC = ((BindingProvider) calendarPort)
		    .getRequestContext();
	    reqContextC.put(StubExt.PROPERTY_AUTH_TYPE,
		    StubExt.PROPERTY_AUTH_TYPE_WSSE);
	    reqContextC.put(BindingProvider.USERNAME_PROPERTY, "root");
	    reqContextC.put(BindingProvider.PASSWORD_PROPERTY, "root");

	    logger.debug("********************");
	    logger.debug("DOCUMENT");
	    logger.debug("********************");
	    String[] docArray = docPaths.toArray(new String[docPaths.size()]);
	    for (int k = 0; k < docArray.length; k++) {
		logger.debug("Delete document" + docArray[k]);
		documentPort.deleteDocument(docArray[k]);
	    }
	    logger.debug("********************");
	    logger.debug("FORUMS");
	    logger.debug("********************");
	    if (threadPath != null) {
		logger.debug("Delete thread " + threadPath);
		forumPort.deleteThreadMessage(threadPath);
	    }
	    if (forumPath != null) {
		// delete forum
		logger.debug("Delete forum" + forumPath);
		forumPort.deleteForum(forumPath);
	    }
	    logger.debug("********************");
	    logger.debug("EVENTS");
	    logger.debug("********************");
	    if (eventPath != null) {
		logger.debug("Delete event " + eventPath);
		calendarPort.deleteEvent(eventPath);
		logger.debug("Delete days created for " + eventPath);
		String parent = PathHelper.getParentPath(eventPath);
		if (parent != null && !parent.equals("/")) {
		    // delete day
		    corePort.deleteFolder(parent);
		    parent = PathHelper.getParentPath(parent);
		}
		// Delete month
		corePort.deleteFolder(testPath + "/" + year + "/" + month);
		// Delete year
		corePort.deleteFolder(testPath + "/" + year);
	    }
	    logger.debug("********************");

	    logger.debug("Delete test path " + testPath);
	    documentPort.deleteFolder(testPath);
	    logger.debug("********************");
	} catch (Exception e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	}
    }

}
