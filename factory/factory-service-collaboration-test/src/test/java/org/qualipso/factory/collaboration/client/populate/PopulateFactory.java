package org.qualipso.factory.collaboration.client.populate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ws.core.StubExt;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.collaboration.client.ws.Bootstrap;
import org.qualipso.factory.collaboration.client.ws.BootstrapServiceException_Exception;
import org.qualipso.factory.collaboration.client.ws.Bootstrap_Service;
import org.qualipso.factory.collaboration.client.ws.Calendar;
import org.qualipso.factory.collaboration.client.ws.CalendarDetails;
import org.qualipso.factory.collaboration.client.ws.Calendar_Service;
import org.qualipso.factory.collaboration.client.ws.Core;
import org.qualipso.factory.collaboration.client.ws.Core_Service;
import org.qualipso.factory.collaboration.client.ws.DocumentDetails;
import org.qualipso.factory.collaboration.client.ws.DocumentManagement;
import org.qualipso.factory.collaboration.client.ws.DocumentManagement_Service;
import org.qualipso.factory.collaboration.client.ws.ForumManagement;
import org.qualipso.factory.collaboration.client.ws.ForumManagement_Service;
import org.qualipso.factory.collaboration.client.ws.Membership;
import org.qualipso.factory.collaboration.client.ws.Membership_Service;
import org.qualipso.factory.collaboration.client.ws.Project;
import org.qualipso.factory.collaboration.client.ws.Project_Service;
import org.qualipso.factory.collaboration.client.ws.StringArray;

/**
 * A factory for creating Populate objects.
 */
public class PopulateFactory {

    /** The logger. */
    private static Log logger = LogFactory.getLog(PopulateFactory.class);

    /** The fs port. */
    private ForumManagement fsPort;

    /** The ds port. */
    private DocumentManagement dsPort;

    /** The cs port. */
    private Calendar csPort;

    /** The ps port. */
    private Project psPort;

    /** The core port. */
    private Core corePort;

    private Membership memberPort;

    /** The year. */
    private String year = "2010";

    /** The month. */
    private String month = "01";

    String projPath = "/qualipso";
    String dataPath = projPath + "/data";

    /**
     * Instantiates a new populate factory.
     */
    public PopulateFactory() {
	ForumManagement_Service fs = new ForumManagement_Service();
	fsPort = fs.getForumManagementPort();
	DocumentManagement_Service ds = new DocumentManagement_Service();
	dsPort = ds.getDocumentManagementPort();
	Calendar_Service cs = new Calendar_Service();
	csPort = cs.getCalendarPort();
	Project_Service ps = new Project_Service();
	psPort = ps.getProjectServiceBeanPort();
	Core_Service core = new Core_Service();
	corePort = core.getCoreServiceBeanPort();
	Membership_Service ms = new Membership_Service();
	memberPort = ms.getMembershipServiceBeanPort();
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

    @Test
    public void testPopulate() {

	try {
	    ((StubExt) fsPort).setConfigName("Standard WSSecurity Client");
	    ((StubExt) dsPort).setConfigName("Standard WSSecurity Client");
	    ((StubExt) csPort).setConfigName("Standard WSSecurity Client");
	    ((StubExt) psPort).setConfigName("Standard WSSecurity Client");
	    ((StubExt) corePort).setConfigName("Standard WSSecurity Client");
	    ((StubExt) memberPort).setConfigName("Standard WSSecurity Client");
//	    Map<String, Object> reqContext = ((BindingProvider) fsPort)
//		    .getRequestContext();
//	    reqContext.put(StubExt.PROPERTY_AUTH_TYPE,
//		    StubExt.PROPERTY_AUTH_TYPE_WSSE);
//	    reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
//	    reqContext.put(BindingProvider.PASSWORD_PROPERTY, "root");
//	    //
//	    Map<String, Object> reqContextDoc = ((BindingProvider) dsPort)
//		    .getRequestContext();
//	    reqContextDoc.put(StubExt.PROPERTY_AUTH_TYPE,
//		    StubExt.PROPERTY_AUTH_TYPE_WSSE);
//	    reqContextDoc.put(BindingProvider.USERNAME_PROPERTY, "root");
//	    reqContextDoc.put(BindingProvider.PASSWORD_PROPERTY, "root");
//	    //
//	    Map<String, Object> reqContextProj = ((BindingProvider) psPort)
//		    .getRequestContext();
//	    reqContextProj.put(StubExt.PROPERTY_AUTH_TYPE,
//		    StubExt.PROPERTY_AUTH_TYPE_WSSE);
//	    reqContextProj.put(BindingProvider.USERNAME_PROPERTY, "root");
//	    reqContextProj.put(BindingProvider.PASSWORD_PROPERTY, "root");
//	    //
//	    Map<String, Object> reqContextCal = ((BindingProvider) csPort)
//		    .getRequestContext();
//	    reqContextCal.put(StubExt.PROPERTY_AUTH_TYPE,
//		    StubExt.PROPERTY_AUTH_TYPE_WSSE);
//	    reqContextCal.put(BindingProvider.USERNAME_PROPERTY, "root");
//	    reqContextCal.put(BindingProvider.PASSWORD_PROPERTY, "root");
//	    //
//	    Map<String, Object> reqContextCore = ((BindingProvider) corePort)
//		    .getRequestContext();
//	    reqContextCore.put(StubExt.PROPERTY_AUTH_TYPE,
//		    StubExt.PROPERTY_AUTH_TYPE_WSSE);
//	    reqContextCore.put(BindingProvider.USERNAME_PROPERTY, "root");
//	    reqContextCore.put(BindingProvider.PASSWORD_PROPERTY, "root");
//	    //
//	    Map<String, Object> rctxMember = ((BindingProvider) memberPort)
//		    .getRequestContext();
//	    rctxMember.put(StubExt.PROPERTY_AUTH_TYPE,
//		    StubExt.PROPERTY_AUTH_TYPE_WSSE);
//	    rctxMember.put(BindingProvider.USERNAME_PROPERTY, "root");
//	    rctxMember.put(BindingProvider.PASSWORD_PROPERTY, "root");
	    logger.debug("*************");
	    logger.debug("Create project " + projPath);
	    psPort.createProject(projPath, "qualipso", "Qualipso project",
		    "LPGL");
	    logger.debug("Create data folder" + dataPath);
	    corePort.createFolder(dataPath, "data",
		    "folder for storing project data");
	    //
//	    memberPort.createGroup("/qualipso/developers", "developers",
//		    "developer's group");
//	    logger.debug("Created group /qualipso/developers");
//	    memberPort.createGroup("/qualipso/testers", "testers",
//		    "testers group");
//	    logger.debug("Created group /qualipso/testers");
//	    memberPort.createGroup("/qualipso/analysts", "analysts",
//		    "analyst group");
//	    logger.debug("Created group /qualipso/analysts");
//	    memberPort.createProfile("dev1", "developer 1",
//		    "gstro@eurodyn.com", 0);
//	    logger.debug("Created profile /profiles/dev1");
//	    memberPort.createProfile("dev2", "developer 2",
//		    "gstro@eurodyn.com", 0);
//	    logger.debug("Created profile /profiles/dev2");
//	    memberPort.createProfile("test1", "test 1", "gstro@eurodyn.com", 0);
//	    logger.debug("Created profile /profiles/test1");
//	    memberPort.createProfile("test2", "test 2", "gstro@eurodyn.com", 0);
//	    logger.debug("Created profile /profiles/test2");
//	    memberPort.createProfile("an1", "an 1", "gstro@eurodyn.com", 0);
//	    logger.debug("Created profile /profiles/an1");
//	    memberPort.createProfile("an2", "an 2", "gstro@eurodyn.com", 0);
//	    logger.debug("Created profile /profiles/an2"); //
//	    
//	    logger.debug("adding members to group");
//	    Group gi = memberPort.readGroup("/qualipso/developers");
//	    assertNotNull(gi);
//	    memberPort.addMemberInGroup("/qualipso/developers",
//		    "/profiles/dev1");
//	    memberPort.addMemberInGroup("/qualipso/developers",
//		    "/profiles/dev2");
//	    memberPort.addMemberInGroup("/qualipso/testers", "/profiles/test1");
//	    memberPort.addMemberInGroup("/qualipso/testers", "/profiles/test2");
//	    memberPort.addMemberInGroup("/qualipso/analysts", "/profiles/an1");
//	    memberPort.addMemberInGroup("/qualipso/analysts", "/profiles/an2");
	    //

	    String tempText = "Lorem ipsum";
	    logger.debug("*************");
	    String documentsPath = dsPort.createFolder(dataPath, "F_"
		    + System.currentTimeMillis(), tempText);
	    assertNotNull(documentsPath);
	    String calendarPath = documentsPath;
	    String forumsPath = documentsPath;
	    // logger.debug("Created folder:" + documentsPath);
	    // String calendarPath = dsPort.createFolder(dataPath, "calendar",
	    // tempText);
	    // assertNotNull(calendarPath);
	    // logger.debug("Created folder:" + calendarPath);
	    // String forumsPath = dsPort.createFolder(dataPath, "forums",
	    // tempText);
	    // assertNotNull(forumsPath);
	    // logger.debug("Created folder:" + forumsPath);
	    //
	    String randomDocument = "D" + System.currentTimeMillis();
	    String fileName = "D_" + System.currentTimeMillis() + ".txt";
	    DocumentDetails dd = new DocumentDetails();
	    dd.setName(randomDocument);
	    dd.setDate("2009-12-08");
	    dd.setBinaryContent("TG9yZW0gaXBzdW0=".getBytes());
	    dd.setFileName(fileName);
	    dd.setKeywords("qualipso");
	    dd.setMimeType("text/plain");
	    dd.setStatus("DRAFT");
	    dd.setVersion("1.0");
	    dd.setType("Readme");
	    String docPath = dsPort.createDocument(documentsPath, dd);
	    logger.debug("Created Document:" + docPath);
	    logger.debug("*************");
	    logger.debug("Create Forum 1");
	    String forumPath = fsPort.createForum(forumsPath, "qualipsof1");
	    logger.debug("Created Forum " + forumPath);
	    logger.debug("*************");
	    String eventName = "E" + System.currentTimeMillis();
	    //
	    CalendarDetails cd = new CalendarDetails();
	    cd.setContactEmail("gstro@delos.eurodyn.com");
	    cd.setContactName("strusos");
	    cd.setContactPhone("2108094500");
	    cd.setDate(year + "-" + month + "-25");
	    cd.setEndTime("18:00:00");
	    cd.setLocation("Munich");
	    cd.setName(eventName);
	    cd.setRecurrence("once");
	    cd.setTimes(1);
	    cd.setStartTime("10:00:00");
	    StringArray paths = csPort.createEvent(calendarPath, cd);
	    if (paths != null) {
		for (int i = 0; i < paths.getItem().size(); i++) {
		    String ePath = paths.getItem().get(i);
		    logger.debug(ePath);
		}
	    }

	    eventName = "E" + System.currentTimeMillis();
	    cd.setName(eventName);
	    cd.setDate(year + "-" + month + "-18");
	    cd.setRecurrence("daily");
	    cd.setTimes(3);
	    paths = csPort.createEvent(calendarPath, cd);
	    if (paths != null) {
		for (int i = 0; i < paths.getItem().size(); i++) {
		    String ePath = paths.getItem().get(i);
		    logger.debug(ePath);
		}
	    }

	    eventName = "E" + System.currentTimeMillis();
	    cd.setName(eventName);
	    cd.setDate(year + "-" + month + "-12");
	    cd.setRecurrence("daily");
	    cd.setTimes(5);
	    paths = csPort.createEvent(calendarPath, cd);
	    if (paths != null) {
		for (int i = 0; i < paths.getItem().size(); i++) {
		    String ePath = paths.getItem().get(i);
		    logger.debug(ePath);
		}
	    }

	    eventName = "E" + System.currentTimeMillis();
	    cd.setName(eventName);
	    cd.setDate(year + "-" + month + "-21");
	    cd.setRecurrence("once");
	    cd.setTimes(1);
	    paths = csPort.createEvent(calendarPath, cd);
	    if (paths != null) {
		for (int i = 0; i < paths.getItem().size(); i++) {
		    String ePath = paths.getItem().get(i);
		    logger.debug(ePath);
		}
	    }
	    logger.debug("*************");
	} catch (Exception e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	}
    }

}
