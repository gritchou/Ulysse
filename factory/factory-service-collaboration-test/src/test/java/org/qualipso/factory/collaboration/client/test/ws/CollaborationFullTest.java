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
import org.qualipso.factory.collaboration.client.ws.CalendarItem;
import org.qualipso.factory.collaboration.client.ws.CalendarItemArray;
import org.qualipso.factory.collaboration.client.ws.Calendar_Service;
import org.qualipso.factory.collaboration.client.ws.CollaborationFolder;
import org.qualipso.factory.collaboration.client.ws.CollaborationFolderArray;
import org.qualipso.factory.collaboration.client.ws.Core;
import org.qualipso.factory.collaboration.client.ws.Core_Service;
import org.qualipso.factory.collaboration.client.ws.Document;
import org.qualipso.factory.collaboration.client.ws.DocumentArray;
import org.qualipso.factory.collaboration.client.ws.DocumentDetails;
import org.qualipso.factory.collaboration.client.ws.DocumentManagement;
import org.qualipso.factory.collaboration.client.ws.DocumentManagement_Service;
import org.qualipso.factory.collaboration.client.ws.Forum;
import org.qualipso.factory.collaboration.client.ws.ForumArray;
import org.qualipso.factory.collaboration.client.ws.ForumManagement;
import org.qualipso.factory.collaboration.client.ws.ForumManagement_Service;
import org.qualipso.factory.collaboration.client.ws.Project;
import org.qualipso.factory.collaboration.client.ws.Project_Service;
import org.qualipso.factory.collaboration.client.ws.StringArray;
import org.qualipso.factory.collaboration.client.ws.ThreadDetails;
import org.qualipso.factory.collaboration.client.ws.ThreadMessage;
import org.qualipso.factory.collaboration.client.ws.ThreadMessageArray;
import org.qualipso.factory.collaboration.utils.TestUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class CollaborationFullTest.
 */
public class CollaborationFullTest {

    /** The logger. */
    private static Log logger = LogFactory.getLog(CollaborationFullTest.class);

    /** The document port. */
    private DocumentManagement documentPort;

    /** The forum port. */
    private ForumManagement forumPort;

    /** The core port. */
    private Core corePort;

    /** The project port. */
    private Project projectPort;

    /** The calendar port. */
    private Calendar calendarPort;

    /** The proj name. */
    private String projName = "p" + System.currentTimeMillis();

    /** The proj path. */
    private String projPath = "/" + projName;

    /** The data path. */
    private String dataPath = projPath + "/data";

    /** The folder name. */
    private String folderName = "f" + System.currentTimeMillis();

    /** The test path. */
    private String testPath = dataPath + "/" + folderName;

    /** The lorem ipsum text. */
    private String loremIpsumText = "Lorem Ipsum.";

    /** The year. */
    private String year = "2010";

    /** The month. */
    private String month = "10";

    /** The event paths. */
    private Vector<String> eventPaths;

    /** The doc paths. */
    private Vector<String> docPaths;

    /** The forum paths. */
    private Vector<String> forumPaths;

    /** The thread paths. */
    private Vector<String> threadPaths;

    /** The thread reply paths. */
    private Vector<String> threadReplyPaths;

    /** The num docs. */
    int numDocs = 2;

    /** The num forums. */
    int numForums = 2;

    /**
     * Instantiates a new collaboration full test.
     */
    public CollaborationFullTest() {
	DocumentManagement_Service ds = new DocumentManagement_Service();
	documentPort = ds.getDocumentManagementPort();

	ForumManagement_Service fs = new ForumManagement_Service();
	forumPort = fs.getForumManagementPort();

	Core_Service core = new Core_Service();
	corePort = core.getCoreServiceBeanPort();

	Project_Service ps = new Project_Service();
	projectPort = ps.getProjectServiceBeanPort();

	Calendar_Service cs = new Calendar_Service();
	calendarPort = cs.getCalendarPort();
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
	    ((StubExt) projectPort).setConfigName("Standard WSSecurity Client");

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
	    Map<String, Object> reqContextP = ((BindingProvider) projectPort)
		    .getRequestContext();
	    reqContextP.put(StubExt.PROPERTY_AUTH_TYPE,
		    StubExt.PROPERTY_AUTH_TYPE_WSSE);
	    reqContextP.put(BindingProvider.USERNAME_PROPERTY, "root");
	    reqContextP.put(BindingProvider.PASSWORD_PROPERTY, "root");

	    //
	    logger.debug("********************");
	    logger.debug("Create project " + projPath);
	    projectPort.createProject(projPath, projName, "Qualipso project",
		    "LPGL");

	    logger.debug("Create data folder" + dataPath);
	    corePort.createFolder(dataPath, "data",
		    "folder for storing project data");
	    logger.debug("********************");
	    logger.debug("Create Collaboration Folder:" + testPath);
	    String tmpPath = documentPort.createFolder(dataPath, folderName,
		    "Lorem ipsum");
	    assertNotNull(tmpPath);

	    logger.debug("********************");
	    logger.debug("DOCUMENT");
	    logger.debug("********************");
	    for (int k = 0; k < numDocs; k++) {
		String fName = "folder" + k;
		String folderPath = documentPort.createFolder(testPath, fName,
			loremIpsumText);
		logger.debug("Create subfolder " + folderPath);
	    }
	    docPaths = new Vector<String>();
	    for (int k = 0; k < numDocs; k++) {
		String documentName = "document" + k;
		String fileName = "D_" + System.currentTimeMillis() + ".txt";
		DocumentDetails dd = new DocumentDetails();
		dd.setName(documentName);
		dd.setDate(year + "-" + month + "-05");
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
	    forumPaths = new Vector<String>();
	    threadPaths = new Vector<String>();
	    threadReplyPaths = new Vector<String>();
	    for (int i = 0; i < numForums; i++) {
		String forumName = "forumtest" + i;
		String forumPath = forumPort.createForum(testPath, forumName);
		assertNotNull(forumPath);
		logger.debug("Created forum " + forumPath);
		forumPaths.add(forumPath);
		Forum forum = forumPort.readForum(forumPath);
		assertNotNull(forum);
		String threadMessgage = "a" + i;
		String randomText = loremIpsumText + System.currentTimeMillis();
		ThreadDetails tmd = new ThreadDetails();
		tmd.setDatePosted(year + "-" + month + "-05");
		tmd.setForumId(forum.getId());
		tmd.setMessageBody(randomText);
		tmd.setName(threadMessgage);
		tmd.setIsReply(false);
		String threadPath = forumPort.createThreadMessage(forumPath,
			tmd);
		assertNotNull(threadPath);
		threadPaths.add(threadPath);
		logger.debug("Created thread message " + threadPath);
		for (int j = 0; j < numForums; j++) {
		    String threadMessgageReply = "b" + j;
		    randomText = loremIpsumText + System.currentTimeMillis();
		    tmd.setDatePosted(year + "-" + month + "-05");
		    tmd.setForumId(forum.getId());
		    tmd.setMessageBody(randomText);
		    tmd.setName(threadMessgageReply);
		    tmd.setIsReply(true);
		    String threadPathReply = forumPort.createThreadMessage(
			    threadPath, tmd);
		    assertNotNull(threadPathReply);
		    logger.debug("Created thread message " + threadPathReply);
		    threadReplyPaths.add(threadPathReply);
		}
	    }
	    logger.debug("********************");
	    logger.debug("EVENTS");
	    logger.debug("********************");
	    String eventName = "E" + System.currentTimeMillis();
	    logger.debug("Create event for " + year + "-" + month
		    + "-05 that occurs daily 3 times");
	    CalendarDetails cd = new CalendarDetails();
	    cd.setContactEmail("gstro@delos.eurodyn.com");
	    cd.setContactName("strusos");
	    cd.setContactPhone("2108094500");
	    cd.setDate(year + "-" + month + "-05");
	    cd.setEndTime("18:00:00");
	    cd.setLocation("Munich");
	    cd.setName(eventName);
	    cd.setRecurrence("daily");
	    cd.setTimes(3);
	    cd.setStartTime("10:00:00");
	    StringArray paths = calendarPort.createEvent(testPath, cd);
	    assertNotNull(paths);
	    eventPaths = new Vector<String>();
	    for (int i = 0; i < paths.getItem().size(); i++) {
		String ePath = paths.getItem().get(i);
		eventPaths.add(ePath);
	    }
	    eventName = "E" + System.currentTimeMillis();
	    logger.debug("Create event for " + year + "-" + month
		    + "-12 that occurs once");
	    cd.setName(eventName);
	    cd.setDate(year + "-" + month + "-12");
	    cd.setRecurrence("once");
	    cd.setTimes(1);
	    paths = calendarPort.createEvent(testPath, cd);
	    assertNotNull(paths);
	    for (int i = 0; i < paths.getItem().size(); i++) {
		String ePath = paths.getItem().get(i);
		eventPaths.add(ePath);
	    }
	    logger.debug("Create event for " + year + "-" + month
		    + "-22 that occurs daily 5 times");
	    cd.setName(eventName);
	    cd.setDate(year + "-" + month + "-22");
	    cd.setRecurrence("daily");
	    cd.setTimes(5);
	    paths = calendarPort.createEvent(testPath, cd);
	    assertNotNull(paths);
	    for (int i = 0; i < paths.getItem().size(); i++) {
		String ePath = paths.getItem().get(i);
		eventPaths.add(ePath);
	    }
	    logger.debug("********************");
	} catch (Exception e) {
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
	    ((StubExt) corePort).setConfigName("Standard WSSecurity Client");
	    ((StubExt) projectPort).setConfigName("Standard WSSecurity Client");
	    ((StubExt) calendarPort)
		    .setConfigName("Standard WSSecurity Client");
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
	    Map<String, Object> reqContextP = ((BindingProvider) projectPort)
		    .getRequestContext();
	    reqContextP.put(StubExt.PROPERTY_AUTH_TYPE,
		    StubExt.PROPERTY_AUTH_TYPE_WSSE);
	    reqContextP.put(BindingProvider.USERNAME_PROPERTY, "root");
	    reqContextP.put(BindingProvider.PASSWORD_PROPERTY, "root");
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
	    for (int k = 0; k < numDocs; k++) {
		String fName = "folder" + k;
		String folderPath = testPath + "/"
			+ TestUtils.normalizeForPath(fName);
		logger.debug("Delete subfolder " + folderPath);
		documentPort.deleteFolder(folderPath);
	    }
	    String[] docArray = docPaths.toArray(new String[docPaths.size()]);
	    for (int k = 0; k < docArray.length; k++) {
		logger.debug("Delete document" + docArray[k]);
		documentPort.deleteDocument(docArray[k]);
	    }
	    logger.debug("********************");
	    logger.debug("FORUMS");
	    logger.debug("********************");
	    if (threadReplyPaths != null && threadReplyPaths.size() > 0) {
		// delete first replyThreads
		String[] threadPathsArray = threadReplyPaths
			.toArray(new String[threadReplyPaths.size()]);
		for (int k = 0; k < threadPathsArray.length; k++) {
		    logger.debug("Delete thread" + threadPathsArray[k]);
		    forumPort.deleteThreadMessage(threadPathsArray[k]);
		}
		// delete first level Threads
		threadPathsArray = threadPaths.toArray(new String[threadPaths
			.size()]);
		for (int k = 0; k < threadPathsArray.length; k++) {
		    logger.debug("Delete thread" + threadPathsArray[k]);
		    forumPort.deleteThreadMessage(threadPathsArray[k]);
		}
		// delete forums
		String[] forumsArray = forumPaths.toArray(new String[forumPaths
			.size()]);
		for (int k = 0; k < forumsArray.length; k++) {
		    logger.debug("Delete forum" + forumsArray[k]);
		    forumPort.deleteForum(forumsArray[k]);
		}
	    }
	    logger.debug("********************");
	    logger.debug("EVENTS");
	    logger.debug("********************");
	    if (eventPaths != null && eventPaths.size() > 0) {
		String[] paths = eventPaths.toArray(new String[eventPaths
			.size()]);
		for (int j = 0; j < paths.length; j++) {
		    logger.debug("Delete event " + paths[j]);
		    calendarPort.deleteEvent(paths[j]);
		}
		for (int j = 0; j < paths.length; j++) {
		    logger.debug("Delete days created for " + paths[j]);
		    String parent = PathHelper.getParentPath(paths[j]);
		    if (parent != null && !parent.equals("/")) {
			// delete day
			corePort.deleteFolder(parent);
			parent = PathHelper.getParentPath(parent);
		    }
		}
		// Delete month
		corePort.deleteFolder(testPath + "/" + year + "/" + month);
		// Delete year
		corePort.deleteFolder(testPath + "/" + year);

	    }

	    logger.debug("********************");

	    logger.debug("Delete test path " + testPath);
	    documentPort.deleteFolder(testPath);
	    logger.debug("Delete data path " + dataPath);
	    corePort.deleteFolder(dataPath);

	    logger.debug("Delete project " + projPath);
	    projectPort.deleteProject(projPath);
	    logger.debug("********************");
	} catch (Exception e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	}
    }

    /**
     * Test browse.
     */
    @Test
    public void testBrowse() {

	try {
	    logger.debug("********************");
	    logger.debug("Get forums under " + testPath);
	    ForumArray forums = forumPort.getForums(testPath);
	    assertNotNull(forums);
	    for (int j = 0; j < forums.getItem().size(); j++) {
		Forum tmpForum = (Forum) forums.getItem().get(j);
		assertNotNull(tmpForum);
		logger.debug(tmpForum.getPath());
		ThreadMessageArray tma = forumPort.getForumMessages(tmpForum
			.getPath());
		assertNotNull(tma);
		for (int k = 0; k < tma.getItem().size(); k++) {
		    ThreadMessage tmpTM = (ThreadMessage) tma.getItem().get(k);
		    assertNotNull(tmpTM);
		    logger.debug(tmpTM.getPath());
		    ThreadMessageArray tmar = forumPort.getThreadReplies(tmpTM
			    .getPath());
		    assertNotNull(tmar);
		    for (int l = 0; l < tmar.getItem().size(); l++) {
			ThreadMessage tmpRTM = (ThreadMessage) tmar.getItem()
				.get(l);
			assertNotNull(tmpRTM);
			logger.debug(tmpRTM.getPath());
		    }
		}
	    }
	    logger.debug("********************");
	    logger.debug("Get documents under " + testPath);
	    DocumentArray docs = documentPort.getFolderDocuments(testPath);
	    assertNotNull(docs);
	    for (int n = 0; n < docs.getItem().size(); n++) {
		Document cd = (Document) docs.getItem().get(n);
		logger.debug(cd.getPath());
	    } //
	    logger.debug("Get folders under " + testPath);
	    CollaborationFolderArray folds = documentPort
		    .getSubfolders(testPath);
	    assertNotNull(folds);
	    for (int n = 0; n < folds.getItem().size(); n++) {
		CollaborationFolder cf = (CollaborationFolder) folds.getItem()
			.get(n);
		logger.debug(cf.getPath());
	    }
	    logger.debug("********************");

	    CalendarItemArray cals = calendarPort.getCalendarItemsForMonth(
		    testPath, year, month);
	    assertNotNull(cals);
	    logger.debug("List events of " + testPath + " for year" + year
		    + " month" + month);
	    for (int n = 0; n < cals.getItem().size(); n++) {
		CalendarItem ci = (CalendarItem) cals.getItem().get(n);
		logger.debug(ci.getPath());
	    }
	    logger.debug("********************");
	} catch (Exception e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	}
    }
}
