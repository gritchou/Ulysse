package org.qualipso.factory.collaboration.client.test.ws;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;
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
import org.qualipso.factory.collaboration.client.ws.CalendarItem;
import org.qualipso.factory.collaboration.client.ws.CalendarServiceException_Exception;
import org.qualipso.factory.collaboration.client.ws.Calendar_Service;
import org.qualipso.factory.collaboration.client.ws.Core;
import org.qualipso.factory.collaboration.client.ws.Core_Service;
import org.qualipso.factory.collaboration.client.ws.DocumentManagement;
import org.qualipso.factory.collaboration.client.ws.DocumentManagement_Service;
import org.qualipso.factory.collaboration.client.ws.ForumManagement;
import org.qualipso.factory.collaboration.client.ws.ForumManagement_Service;
import org.qualipso.factory.collaboration.client.ws.StringArray;
import org.qualipso.factory.collaboration.utils.TestUtils;

public class AttachmentsTest {

    private static Log logger = LogFactory.getLog(AttachmentsTest.class);
    private Calendar calendarPort;
    private DocumentManagement documentPort;
    private ForumManagement forumPort;
    private Core corePort;

    private String folderName = "f" + System.currentTimeMillis();
    private String testPath = "/" + folderName;
    private String year = "2010";
    private String month = "10";
    private Vector<String> docPaths;
    private String eventPath;
    private String forumPath;

    int numDocs = 5;
    int numForums = 1;

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
		String fileName = "D_" + System.currentTimeMillis() + ".txt";
		String docPath = documentPort.createDocumentSimple(testPath,
			documentName, "2009-12-08", "Readme", "qualipso",
			"1.0", "DRAFT", fileName, "text/plain",
			"TG9yZW0gaXBzdW0=".getBytes());
		logger.debug("Created document" + docPath);
		docPaths.add(docPath);
	    }
	    logger.debug("********************");
	    logger.debug("FORUMS");
	    logger.debug("********************");
	    for (int i = 0; i < numForums; i++) {
		String forumName = "forumtest" + i;
		String fId = forumPort.createForum(testPath, forumName);
		assertNotNull(fId);
		forumPath = testPath + "/" + fId;
		logger.debug("Created forum " + forumPath);
	    }
	    logger.debug("********************");
	    logger.debug("EVENTS");
	    logger.debug("********************");
	    String eventName = "E" + System.currentTimeMillis();
	    logger.debug("Create event for " + year + "-" + month
		    + "-12 that occurs once");
	    StringArray paths = calendarPort.createEvent(testPath, eventName,
		    "Athens", year + "-" + month + "-12", "10:00:00",
		    "18:00:00", "strusos", "gstro@delos.eurodyn.com",
		    "2108094500", "once", 1);
	    assertNotNull(paths);
	    eventPath = paths.getItem().get(0);
	    logger.debug("********************");
	} catch (Exception e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	}
    }

    @Test
    public void testAttachments() {
	try {
	    logger.debug("********************");
	    logger.debug("Attach forum "+forumPath+" to event" +eventPath);
	    calendarPort.attachForumToEvent(eventPath, forumPath);
	    if(docPaths!=null && docPaths.size()>0)
	    {
		logger.debug("Attach documents to event" +eventPath);
		String[] docArray = docPaths.toArray(new String[docPaths.size()]);
		StringArray docs  = new StringArray();
		for (int i = 0; i < docArray.length; i++) {
		    docs.getItem().add(docArray[i]);
		    logger.debug("Will add "+docArray[i]);
		}
		calendarPort.atttachDocumentToEvent(eventPath,docs);
		// Get calendar to check if attachments are set ok.
		CalendarItem ev = calendarPort.readEvent(eventPath);
		assertNotNull(ev);
		logger.debug(ev.toString());
		assertNotNull(ev.getDocumentIds());
		assertNotNull(ev.getForumId());
		assertNotNull(ev.getForum());
		assertNotNull(ev.getDocumentPaths());
		
	    }
	    logger.debug("********************");
	    //
	} catch (CalendarServiceException_Exception e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	}
    }

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
