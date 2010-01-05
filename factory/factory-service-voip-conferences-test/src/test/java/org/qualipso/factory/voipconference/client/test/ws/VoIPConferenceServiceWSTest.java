package org.qualipso.factory.voipconference.client.test.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ws.core.StubExt;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.voipservice.client.ws.Bootstrap;
import org.qualipso.factory.voipservice.client.ws.BootstrapServiceException_Exception;
import org.qualipso.factory.voipservice.client.ws.Bootstrap_Service;
import org.qualipso.factory.voipservice.client.ws.ConferenceDetails;
import org.qualipso.factory.voipservice.client.ws.Core;
import org.qualipso.factory.voipservice.client.ws.Core_Service;
import org.qualipso.factory.voipservice.client.ws.VoIPConferenceService;
import org.qualipso.factory.voipservice.client.ws.VoIPConferenceService_Service;

public class VoIPConferenceServiceWSTest {
    public class AccessTypes {
	public static final short PUBLIC = 0;
	public static final short PIN = 1;
	public static final short LIST = 2;
	public static final short PIN_LIST = 3;
    }

    private static Log log = LogFactory.getLog(VoIPConferenceServiceWSTest.class);
    private static VoIPConferenceService_Service service_service;
    private static VoIPConferenceService service;
    
    /** The core port. */
    private Core corePort;
    
    public VoIPConferenceServiceWSTest() {
	service_service = new VoIPConferenceService_Service();
	service = service_service.getVoIPConferenceServicePort();

	Core_Service core = new Core_Service();
	corePort = core.getCoreServiceBeanPort();
    }
 
    @BeforeClass
    public static void init() {
	try {
	    Bootstrap port = new Bootstrap_Service().getBootstrapServiceBeanPort();
	    ((StubExt) port).setConfigName("Standard WSSecurity Client");
	    port.bootstrap();
	} catch (BootstrapServiceException_Exception e) {
	    log.error("unable to bootstrap factory", e);
	}
    }

    @Before
    public void setup() {
	log.debug("********************");
	log.debug("SETUP");
	log.debug("********************");

	((StubExt) service).setConfigName("Standard WSSecurity Client");
	Map<String, Object> reqContext = ((BindingProvider) service).getRequestContext();
	reqContext.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
	reqContext.put(BindingProvider.PASSWORD_PROPERTY, "root");
	
	((StubExt) corePort).setConfigName("Standard WSSecurity Client");
	reqContext = ((BindingProvider) corePort).getRequestContext();
	reqContext.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
	reqContext.put(BindingProvider.PASSWORD_PROPERTY, "root");
	
    }

    @Test
    public void testStatusVersion() {
	log.debug("testing status an version (...)");

	try {

	    String value = service.getServiceVersion();
	    log.debug("service version: " + value);
	    assertTrue(value.equals("0.4.3-SNAPSHOT"));
	    assertFalse(value.equals("1.0.1"));

	    value = service.isDBOpen();
	    log.debug("DB status: " + value);

	    assertTrue(value.equals("true"));

	    value = service.getServiceName();
	    log.debug("service name: " + value);
	    assertTrue(value.equals("VoIPConferenceService"));

	    log.debug("end status an version test");
	} catch (Exception e) {
	    log.debug(e.getLocalizedMessage());
	}
    }

    @Test
    public void testUsers() {
	log.debug("testing users (...)");
	String userId = "test";
	String pass = "";
	String voipUsername = "user1" + GregorianCalendar.getInstance().getTimeInMillis();

	String qualipsoUserId = voipUsername;

	try {
	    List<String> values = null;
	    values = service.listUsers(userId, pass).getItem();

	    int length = values.size();

	    service.activateVoipProfile(userId, "", voipUsername, "secret", "email@email.org", "Joe", "Doe",
		    qualipsoUserId);
	    values = service.listUsers(userId, pass).getItem();

	    log.debug("listUsers (+1): " + values);
	    assertEquals(length + 1, values.size());

	    log.debug("start deactivateVoipProfile...");
	    service.deactivateVoipProfile(userId, pass, voipUsername + ".");
	    log.debug("listUsers (+1): " + values);
	    assertEquals(length + 1, values.size());

	    log.debug("check isVoIPProfileExists...");
	    assertEquals(false, service.isVoIPProfileExists(userId, pass, voipUsername + "."));
	    assertEquals(true, service.isVoIPProfileExists(userId, pass, voipUsername));
	    log.debug("check isVoIPProfileExists... [OK]");

	    log.debug("check loginStatus...");
	    assertEquals(false, service.loginStatus(null, pass, null).equals("OK"));
	    assertEquals(true, service.loginStatus(userId, pass, null).equals("OK"));
	    log.debug("check loginStatus... [OK]");

	    log.debug("check usernameFromQualipsoUsers...");
	    assertEquals(false, service.usernameFromQualipsoUsers(userId, pass, voipUsername + ".")
		    .equals(voipUsername));
	    assertEquals(true, service.usernameFromQualipsoUsers(userId, pass, voipUsername).equals(voipUsername));
	    log.debug("check usernameFromQualipsoUsers... [OK]");

	    service.deactivateVoipProfile(userId, pass, voipUsername);
	    values = service.listUsers(userId, pass).getItem();
	    log.debug("listUsers (-1): " + values);
	    assertEquals(length, values.size());

	    log.debug("end testing users(...)");
	} catch (Exception e) {
	    log.debug(e.getLocalizedMessage());
	}
    }

    @Test
    public void testConference() {
	log.debug("testing conferences (...)");
	String userId = "admin";
	String pass = "";

	try {

	    Long delta = new Long(60000);
	    String owner = userId;
	    boolean permanent = false;
	    short accessType = AccessTypes.PUBLIC;
	    String pin = "1234";
	    String adminpin = "4321";
	    Long startDate = new Long(new Date().getTime() / 1000);
	    Long endDate = new Long(new Date().getTime() / 1000 + 3600);
	    Integer maxUsers = new Integer(10);
	    String name = "name";
	    String agenda = "agenda";
	    boolean recorded = true;
	    String project = "qualipso123";

	    try {
		corePort.createFolder("/projects/", "projects", "folder for storing projects 1");
	    } catch (Exception e) {}
	    try {
		corePort.createFolder("/projects/" + project ,  project, "folder for storing projects 2");
	    } catch (Exception e) {}
	    try {
		corePort.createFolder("/projects/" + project + "/calendar" ,  "calendar", "folder for storing projects 3");
	    } catch (Exception e) {}
	    try {
		corePort.createFolder("/projects/" + project + "/calendar/voip" ,  "voip", "folder for storing projects 4");
	    } catch (Exception e) {}

	    String suffix = "_" + GregorianCalendar.getInstance().getTimeInMillis();
	    Integer confNo1 = service.createConference(userId, pass, owner, accessType, permanent, pin, adminpin,
		    startDate, endDate, maxUsers, name + suffix, agenda + suffix, recorded, project);

	     startDate += delta; endDate += delta; accessType =
	     AccessTypes.PIN; suffix = "_" +
	     GregorianCalendar.getInstance().getTimeInMillis(); Integer
	     confNo2 = service.createConference(userId, pass, owner,
	     accessType, permanent, pin, adminpin, startDate, endDate,
	     maxUsers, name + suffix, agenda + suffix, recorded, project);
	     
	     startDate += delta; endDate += delta; accessType =
	     AccessTypes.LIST; suffix = "_" +
	     GregorianCalendar.getInstance().getTimeInMillis(); Integer
	     confNo3 = service.createConference(userId, pass, owner,
	     accessType, permanent, pin, adminpin, startDate, endDate,
	     maxUsers, name + suffix, agenda + suffix, recorded, project);
	     
	     startDate += delta; endDate += delta; accessType =
	     AccessTypes.PIN_LIST; suffix = "_" +
	     GregorianCalendar.getInstance().getTimeInMillis(); Integer
	     confNo4 = service.createConference(userId, pass, owner,
	     accessType, permanent, pin, adminpin, startDate, endDate,
	     maxUsers, name + suffix, agenda + suffix, recorded, project);
	     
	     startDate += delta; endDate += delta; permanent = true;
	     accessType = AccessTypes.PIN; suffix = "_" +
	     GregorianCalendar.getInstance().getTimeInMillis(); Integer
	     confNo5 = service.createConference(userId, pass, owner,
	     accessType, permanent, pin, adminpin, startDate, endDate,
	     maxUsers, name + suffix, agenda + suffix, recorded, project);
	     
	     ConferenceDetails detail = service.getConferenceDetails(userId,
	     pass, confNo5);
	     assertTrue(detail.getAdminPin().equals(adminpin));
	     assertTrue(detail.getPin().equals(pin));
	     assertTrue(detail.getAgenda().equals(agenda + suffix));
	     assertTrue(detail.getName().equals(name + suffix));
	     assertTrue(detail.getOwner().equals(owner));
	     assertTrue(detail.getAccessType() == accessType);
	     assertTrue(detail.getConfNo() == confNo5.intValue());
	     assertTrue(detail.getEndDate() == null);
	     assertTrue(detail.getMaxUsers().equals(maxUsers));
	     assertTrue(detail.getStartDate() == null);
	     assertTrue(detail.getUserCount().equals(0));
	     assertTrue(detail.isPermanent() == permanent);
	     assertTrue(detail.isRecorded() == recorded);
	     
	     permanent = false; startDate += delta; endDate += delta;
	     service.editConference(userId, pass, confNo5, owner, accessType,
	     permanent, pin, adminpin, startDate, endDate, maxUsers, name +
	     suffix, agenda + suffix, recorded); detail =
	     service.getConferenceDetails(userId, pass, confNo5);
	     assertTrue(detail.getAdminPin().equals(adminpin));
	     assertTrue(detail.getPin().equals(pin));
	     assertTrue(detail.getAgenda().equals(agenda + suffix));
	     assertTrue(detail.getName().equals(name + suffix));
	     assertTrue(detail.getOwner().equals(owner));
	     assertTrue(detail.getAccessType() == accessType);
	     assertTrue(detail.getConfNo() == confNo5.intValue());
	     assertTrue(detail.getEndDate().equals(endDate));
	     assertTrue(detail.getMaxUsers().equals(maxUsers));
	     assertTrue(detail.getStartDate().equals(startDate));
	     assertTrue(detail.getUserCount().equals(0));
	     assertTrue(detail.isPermanent() == permanent);
	     assertTrue(detail.isRecorded() == recorded);
	     
	     log.debug("end testing conferences (...)");
	     
	     service.removeConference(userId, pass, confNo1);
	     service.removeConference(userId, pass, confNo2);
	     service.removeConference(userId, pass, confNo3);
	     service.removeConference(userId, pass, confNo4);
	     service.removeConference(userId, pass, confNo5);
	} catch (Exception e) {
	    log.debug(e.getLocalizedMessage());
	}

    }
}
