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
import org.qualipso.factory.collaboration.client.ws.BootstrapService;
import org.qualipso.factory.collaboration.client.ws.BootstrapServiceException_Exception;
import org.qualipso.factory.collaboration.client.ws.BootstrapService_Service;
import org.qualipso.factory.collaboration.client.ws.BrowserService;
import org.qualipso.factory.collaboration.client.ws.BrowserService_Service;
import org.qualipso.factory.collaboration.client.ws.CalendarItem;
import org.qualipso.factory.collaboration.client.ws.CalendarItemArray;
import org.qualipso.factory.collaboration.client.ws.CalendarService;
import org.qualipso.factory.collaboration.client.ws.CalendarService_Service;
import org.qualipso.factory.collaboration.client.ws.CollaborationFolder;
import org.qualipso.factory.collaboration.client.ws.CollaborationFolderArray;
import org.qualipso.factory.collaboration.client.ws.CoreService;
import org.qualipso.factory.collaboration.client.ws.CoreService_Service;
import org.qualipso.factory.collaboration.client.ws.Document;
import org.qualipso.factory.collaboration.client.ws.DocumentArray;
import org.qualipso.factory.collaboration.client.ws.DocumentService;
import org.qualipso.factory.collaboration.client.ws.DocumentService_Service;
import org.qualipso.factory.collaboration.client.ws.Forum;
import org.qualipso.factory.collaboration.client.ws.ForumArray;
import org.qualipso.factory.collaboration.client.ws.ForumService;
import org.qualipso.factory.collaboration.client.ws.ForumService_Service;
import org.qualipso.factory.collaboration.client.ws.ProjectService;
import org.qualipso.factory.collaboration.client.ws.ProjectService_Service;
import org.qualipso.factory.collaboration.client.ws.StringArray;
import org.qualipso.factory.collaboration.client.ws.ThreadMessage;
import org.qualipso.factory.collaboration.client.ws.ThreadMessageArray;
import org.qualipso.factory.collaboration.utils.TestUtils;

public class CollaborationFullTest
{
    private static Log logger = LogFactory.getLog(CollaborationFullTest.class);

    private DocumentService documentPort;
    private ForumService forumPort;
    private CoreService corePort;
    private ProjectService projectPort;
    private CalendarService calendarPort;

    private String projName = "p" + System.currentTimeMillis();
    private String projPath = "/" + projName;
    private String dataPath = projPath + "/data";
    private String folderName = "f" + System.currentTimeMillis();
    private String testPath = dataPath + "/" + folderName;
    private String loremIpsumText = "Lorem Ipsum.";
    private String year = "2010";
    private String month = "10";
    private Vector<String> eventPaths;
    int numDocs = 2;
    int numForums = 2;

    public CollaborationFullTest()
    {
	DocumentService_Service ds = new DocumentService_Service();
	documentPort = ds.getDocumentServicePort();

	ForumService_Service fs = new ForumService_Service();
	forumPort = fs.getForumServicePort();

	CoreService_Service core = new CoreService_Service();
	corePort = core.getCoreServicePort();

	ProjectService_Service ps = new ProjectService_Service();
	projectPort = ps.getProjectService();

	CalendarService_Service cs = new CalendarService_Service();
	calendarPort = cs.getCalendarServicePort();
    }

    @BeforeClass
    public static void init()
    {
	try
	{
	    BootstrapService port = new BootstrapService_Service().getBootstrapServicePort();
	    ((StubExt) port).setConfigName("Standard WSSecurity Client");
	    port.bootstrap();
	} catch (BootstrapServiceException_Exception e)
	{
	    logger.error("unable to bootstrap factory", e);
	}
    }

    @Before
    public void setup() throws Exception
    {
	logger.debug("**************************************************************");
	logger.debug("		S	T	A	R	T	U	P");
	logger.debug("**************************************************************");
	((StubExt) documentPort).setConfigName("Standard WSSecurity Client");
	((StubExt) forumPort).setConfigName("Standard WSSecurity Client");
	((StubExt) calendarPort).setConfigName("Standard WSSecurity Client");
	((StubExt) corePort).setConfigName("Standard WSSecurity Client");
	((StubExt) projectPort).setConfigName("Standard WSSecurity Client");

	//
	Map<String, Object> reqContext = ((BindingProvider) documentPort).getRequestContext();
	reqContext.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
	reqContext.put(BindingProvider.PASSWORD_PROPERTY, "root");
	//
	Map<String, Object> reqContextF = ((BindingProvider) forumPort).getRequestContext();
	reqContextF.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	reqContextF.put(BindingProvider.USERNAME_PROPERTY, "root");
	reqContextF.put(BindingProvider.PASSWORD_PROPERTY, "root");
	//
	Map<String, Object> reqContextC = ((BindingProvider) calendarPort).getRequestContext();
	reqContextC.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	reqContextC.put(BindingProvider.USERNAME_PROPERTY, "root");
	reqContextC.put(BindingProvider.PASSWORD_PROPERTY, "root");
	//
	Map<String, Object> reqContextCore = ((BindingProvider) corePort).getRequestContext();
	reqContextCore.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	reqContextCore.put(BindingProvider.USERNAME_PROPERTY, "root");
	reqContextCore.put(BindingProvider.PASSWORD_PROPERTY, "root");
	//
	Map<String, Object> reqContextP = ((BindingProvider) projectPort).getRequestContext();
	reqContextP.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	reqContextP.put(BindingProvider.USERNAME_PROPERTY, "root");
	reqContextP.put(BindingProvider.PASSWORD_PROPERTY, "root");

	//
	logger.debug("**************************************************************");
	logger.debug("Create project " + projPath);
	projectPort.createProject(projPath, projName, "Qualipso project", "LPGL");

	logger.debug("Create data folder" + dataPath);
	corePort.createFolder(dataPath, "data", "folder for storing project data");
	logger.debug("**************************************************************");
	logger.debug("Create Collaboration Folder:" + testPath);
	String tmpPath = documentPort.createFolder(testPath, folderName, "Lorem ipsum");
	assertNotNull(tmpPath);

	logger.debug("**************************************************************");
	logger.debug("	D	O	C	U	M	E	N	T");
	logger.debug("**************************************************************");
	for (int k = 0; k < numDocs; k++)
	{
	    String fName = "folder" + k;
	    String folderPath = testPath + "/" + TestUtils.normalizeForPath(fName);
	    logger.debug("Create subfolder " + folderPath);
	    documentPort.createFolder(folderPath, fName, loremIpsumText);
	}
	for (int k = 0; k < numDocs; k++)
	{
	    String documentName = "document" + k;
	    String documentPath = testPath + "/" + TestUtils.normalizeForPath(documentName);
	    logger.debug("Create document" + documentPath);
	    String fileName = "D_" + System.currentTimeMillis() + ".txt";
	    documentPort.createDocument(documentPath, documentName, "2009-10-08", "Readme", "qualipso", "1.0", "DRAFT",
		    fileName, "text/plain", "TG9yZW0gaXBzdW0=".getBytes());
	}
	logger.debug("**************************************************************");
	logger.debug("		F	O	R	U	M	S");
	logger.debug("**************************************************************");
	for (int i = 0; i < numForums; i++)
	{
	    String forumName = "forumtest" + i;
	    String forumPath = testPath + "/" + TestUtils.normalizeForPath(forumName);
	    logger.debug("Create forum "+forumPath);
	    String fId = forumPort.createForum(testPath, forumName);
	    assertNotNull(fId);
	    String threadMessgage = "a" + i;
	    String threadPath = forumPath + "/" + TestUtils.normalizeForPath(threadMessgage);
	    String randomText = loremIpsumText + System.currentTimeMillis();
	    logger.debug("Create thread message "+threadPath);
	    String tId = forumPort.createThreadMessage(threadPath, threadMessgage, fId, randomText, "2009-10-21",
		    "false");
	    assertNotNull(tId);
	    for (int j = 0; j < numForums; j++)
	    {
		String threadMessgageReply = "b" + j;
		String threadPathReply = threadPath + "/" + TestUtils.normalizeForPath(threadMessgageReply);
		randomText = loremIpsumText + System.currentTimeMillis();
		logger.debug("Create thread message "+threadPathReply);
		String tId2 = forumPort.createThreadMessage(threadPathReply, threadMessgageReply, fId, randomText,
			"2009-10-21", "true");
		assertNotNull(tId2);
	    }
	}
	logger.debug("**************************************************************");
	logger.debug("		E	V	E	N	T	S");
	logger.debug("**************************************************************");
	String eventName = "E" + System.currentTimeMillis();
	logger.debug("Create event for " + year + "-" + month + "-05 that occurs daily 3 times");
	StringArray paths = calendarPort.createEvent(testPath, eventName, "Munich", year + "-" + month + "-05",
		"10:00:00", "18:00:00", "strusos", "gstro@delos.eurodyn.com", "2108094500", "daily", 3);
	assertNotNull(paths);
	eventPaths = new Vector<String>();
	for (int i = 0; i < paths.getItem().size(); i++)
	{
	    String ePath = paths.getItem().get(i);
	    eventPaths.add(ePath);
	}
	eventName = "E" + System.currentTimeMillis();
	logger.debug("Create event for " + year + "-" + month + "-12 that occurs once");
	paths = calendarPort.createEvent(testPath, eventName, "Athens", year + "-" + month + "-12", "10:00:00",
		"18:00:00", "strusos", "gstro@delos.eurodyn.com", "2108094500", "once", 1);
	assertNotNull(paths);
	for (int i = 0; i < paths.getItem().size(); i++)
	{
	    String ePath = paths.getItem().get(i);
	    eventPaths.add(ePath);
	}
	logger.debug("Create event for " + year + "-" + month + "-22 that occurs daily 5 times");
	paths = calendarPort.createEvent(testPath, eventName, "Munich", year + "-" + month + "-22", "09:00:00",
		"18:00:00", "strusos", "gstro@delos.eurodyn.com", "2108094500", "daily", 5);
	assertNotNull(paths);
	for (int i = 0; i < paths.getItem().size(); i++)
	{
	    String ePath = paths.getItem().get(i);
	    eventPaths.add(ePath);
	}
	logger.debug("**************************************************************");
    }

    @After
    public void teardown() throws Exception
    {
	logger.debug("**************************************************************");
	logger.debug("	C	L	E	A	N");
	logger.debug("**************************************************************");
	((StubExt) documentPort).setConfigName("Standard WSSecurity Client");
	((StubExt) forumPort).setConfigName("Standard WSSecurity Client");
	((StubExt) corePort).setConfigName("Standard WSSecurity Client");
	((StubExt) projectPort).setConfigName("Standard WSSecurity Client");
	((StubExt) calendarPort).setConfigName("Standard WSSecurity Client");
	//
	Map<String, Object> reqContext = ((BindingProvider) documentPort).getRequestContext();
	reqContext.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
	reqContext.put(BindingProvider.PASSWORD_PROPERTY, "root");
	//
	Map<String, Object> reqContextF = ((BindingProvider) forumPort).getRequestContext();
	reqContextF.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	reqContextF.put(BindingProvider.USERNAME_PROPERTY, "root");
	reqContextF.put(BindingProvider.PASSWORD_PROPERTY, "root");
	//
	Map<String, Object> reqContextCore = ((BindingProvider) corePort).getRequestContext();
	reqContextCore.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	reqContextCore.put(BindingProvider.USERNAME_PROPERTY, "root");
	reqContextCore.put(BindingProvider.PASSWORD_PROPERTY, "root");
	//
	Map<String, Object> reqContextP = ((BindingProvider) projectPort).getRequestContext();
	reqContextP.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	reqContextP.put(BindingProvider.USERNAME_PROPERTY, "root");
	reqContextP.put(BindingProvider.PASSWORD_PROPERTY, "root");
	//
	Map<String, Object> reqContextC = ((BindingProvider) calendarPort).getRequestContext();
	reqContextC.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	reqContextC.put(BindingProvider.USERNAME_PROPERTY, "root");
	reqContextC.put(BindingProvider.PASSWORD_PROPERTY, "root");
	logger.debug("**************************************************************");
	logger.debug("	D	O	C	U	M	E	N	T");
	logger.debug("**************************************************************");
	for (int k = 0; k < numDocs; k++)
	{
	    String fName = "folder" + k;
	    String folderPath = testPath + "/" + TestUtils.normalizeForPath(fName);
	    logger.debug("Delete subfolder " + folderPath);
	    documentPort.deleteFolder(folderPath);
	}
	for (int k = 0; k < numDocs; k++)
	{
	    String documentName = "document" + k;
	    String documentPath = testPath + "/" + TestUtils.normalizeForPath(documentName);
	    logger.debug("Delete document" + documentPath);
	    documentPort.deleteDocument(documentPath);
	}
	logger.debug("**************************************************************");
	logger.debug("		F	O	R	U	M	S");
	logger.debug("**************************************************************");
	for (int i = 0; i < numForums; i++)
	{
	    String forumName = "forumtest" + i;
	    String forumPath = testPath + "/" + TestUtils.normalizeForPath(forumName);
	    String threadMessgage = "a" + i;
	    String threadPath = forumPath + "/" + TestUtils.normalizeForPath(threadMessgage);
	    for (int j = 0; j < numForums; j++)
	    {
		String threadMessgageReply = "b" + j;
		String threadPathReply = threadPath + "/" + TestUtils.normalizeForPath(threadMessgageReply);
		logger.debug("Delete thread " + threadPathReply);
		forumPort.deleteThreadMessage(threadPathReply);
	    }
	    logger.debug("Delete thread " + threadPath);
	    forumPort.deleteThreadMessage(threadPath);
	    logger.debug("Delete forum " + forumPath);
	    forumPort.deleteForum(forumPath);
	}

	logger.debug("**************************************************************");
	logger.debug("		E	V	E	N	T	S");
	logger.debug("**************************************************************");
	if (eventPaths != null && eventPaths.size() > 0)
	{
	    String[] paths = eventPaths.toArray(new String[eventPaths.size()]);
	    for (int j = 0; j < paths.length; j++)
	    {
		logger.debug("Delete event " + paths[j]);
		calendarPort.deleteEvent(paths[j]);
	    }
	    for (int j = 0; j < paths.length; j++)
	    {
		logger.debug("Delete days created for " + paths[j]);
		String parent = PathHelper.getParentPath(paths[j]);
		if (parent != null && !parent.equals("/"))
		{
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

	logger.debug("**************************************************************");

	logger.debug("Delete test path " + testPath);
	documentPort.deleteFolder(testPath);
	logger.debug("Delete data path " + dataPath);
	corePort.deleteFolder(dataPath);
	logger.debug("Delete project " + projPath);
	projectPort.deleteProject(projPath);
	logger.debug("**************************************************************");
    }

    @Test
    public void testBrowse()
    {

	try
	{

	    logger.debug("**************************************************************");
	    logger.debug("Get forums under " + testPath);
	    ForumArray forums = forumPort.getForums(testPath);
	    assertNotNull(forums);
	    for (int j = 0; j < forums.getItem().size(); j++)
	    {
		Forum tmpForum = (Forum) forums.getItem().get(j);
		logger.debug(tmpForum.getPath());
	    }
	    String forumPath = testPath + "/" + TestUtils.normalizeForPath("forumtest0");
	    
	    logger.debug("Get thread messages for forum "+forumPath);
	    ThreadMessageArray tma = forumPort.getForumMessages(forumPath);
	    assertNotNull(tma);
	    for (int k = 0; k < tma.getItem().size(); k++)
	    {
		ThreadMessage tmpTM = (ThreadMessage) tma.getItem().get(k);
		logger.debug(tmpTM.getPath());
	    }
	    String threadMessgage = "a0";
	    String threadPath = forumPath + "/" + TestUtils.normalizeForPath(threadMessgage);
	    logger.debug("Get replies for thread "+threadPath);
	    ThreadMessageArray tmar = forumPort.getThreadReplies(threadPath);
	    assertNotNull(tmar);
	    for (int l = 0; l < tmar.getItem().size(); l++)
	    {
		ThreadMessage tmpTM = (ThreadMessage) tmar.getItem().get(l);
		logger.debug(tmpTM.getPath());
	    }
	    logger.debug("**************************************************************");
	    logger.debug("Get documents under " + testPath);
	    DocumentArray docs = documentPort.getFolderDocuments(testPath);
	    assertNotNull(docs);
	    for (int n = 0; n < docs.getItem().size(); n++)
	    {
		Document cd = (Document) docs.getItem().get(n);
		logger.debug(cd.getPath());
	    } //
	    logger.debug("Get folders under " + testPath);
	    CollaborationFolderArray folds = documentPort.getSubfolders(testPath);
	    assertNotNull(folds);
	    for (int n = 0; n < folds.getItem().size(); n++)
	    {
		CollaborationFolder cf = (CollaborationFolder) folds.getItem().get(n);
		logger.debug(cf.getPath());
	    }
	    logger.debug("**************************************************************");

	    CalendarItemArray cals = calendarPort.getCalendarItemsForMonth(testPath, year, month);
	    assertNotNull(cals);
	    logger.debug("List events of " + testPath + " for year" + year + " month" + month);
	    for (int n = 0; n < cals.getItem().size(); n++)
	    {
		CalendarItem ci = (CalendarItem) cals.getItem().get(n);
		logger.debug(ci.getPath());
	    }
	    logger.debug("**************************************************************");

	} catch (Exception e)
	{
	    e.printStackTrace();
	    fail(e.getMessage());
	}
    }
}
