package org.qualipso.factory.client.test.sb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.auth.callback.UsernamePasswordHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.bootstrap.BootstrapService;
import org.qualipso.factory.bootstrap.BootstrapServiceException;
import org.qualipso.factory.client.test.AllTests;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.eventqueue.EventQueueServiceException;
import org.qualipso.factory.eventqueue.entity.PersistentEvent;
import org.qualipso.factory.eventqueue.entity.Rule;
import org.qualipso.factory.greeting.GreetingService;
import org.qualipso.factory.greeting.GreetingServiceException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.NotificationServiceException;
import org.qualipso.factory.security.SecurityService;
import org.qualipso.factory.security.SecurityServiceException;

/**
 * @author HANTZ Marlène
 * @author HENRY Nicolas
 * 
 * @date 6 december 2009
 */
public class NotificationServiceSBTest {
    private static Log logger = LogFactory.getLog(NotificationServiceSBTest.class);
    private static Context context;
    private static GreetingService greeting;
    private static EventQueueService eqs;
    private static LoginContext loginContext;
    private static MembershipService membership;
    private static SecurityService security;
    private static CoreService core;
    private final static String pathQueue1 = "/tests/q1", pathQueue2 = "/tests/q2";
    private final static String pathResource = "/tests/m2log";
    private final static String valueResource = "val";

    /**
     * Set up service for all tests.
     * 
     * @throws LoginException
     * @throws NotificationServiceException
     * @throws MembershipServiceException
     */
    @BeforeClass
    public static void before() throws Exception {
        Properties properties = new Properties();
        properties.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        properties.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
        properties.put("java.naming.provider.url", "localhost:1099");
        System.setProperty("java.security.auth.login.config", ClassLoader.getSystemResource("jaas.config").getPath());
        context = new InitialContext(properties);

        BootstrapService bootstrap = (BootstrapService) context.lookup(FactoryNamingConvention.getJNDINameForService("bootstrap"));
        try {
            bootstrap.bootstrap();
        } catch (BootstrapServiceException e) {
            logger.error(e);
        }

        eqs = (EventQueueService) context.lookup(FactoryNamingConvention.SERVICE_PREFIX + EventQueueService.SERVICE_NAME);
        greeting = (GreetingService) context.lookup(FactoryNamingConvention.SERVICE_PREFIX + GreetingService.SERVICE_NAME);
        membership = (MembershipService) context.lookup(FactoryNamingConvention.SERVICE_PREFIX + MembershipService.SERVICE_NAME);
        security = (SecurityService) context.lookup(FactoryNamingConvention.SERVICE_PREFIX + SecurityService.SERVICE_NAME);
        core = (CoreService) context.lookup(FactoryNamingConvention.SERVICE_PREFIX + CoreService.SERVICE_NAME);

        loginContext = new LoginContext("qualipso", new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS));
        loginContext.login();

        membership.createProfile("kermit", "Kermit", "kermit@thefrog", 0);

        core.createFolder("/tests", "Tests", "Tests folder");
        security.addSecurityRule("/tests", "/profiles/kermit", "read,write,create,update");

        loginContext.logout();

        loginContext = new LoginContext("qualipso", new UsernamePasswordHandler("kermit", "thefrog"));
        loginContext.login();

    }

    @AfterClass
    public static void after() throws Exception {
        loginContext.logout();

        loginContext = new LoginContext("qualipso", new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS));
        loginContext.login();

        security.removeSecurityRule("/tests", "/profiles/kermit");
        core.deleteFolder("/tests");

        membership.deleteProfile("/profiles/kermit");

        loginContext.logout();
    }

    @Before
    public void setUp() throws Exception {

        eqs.createEventQueue(pathQueue1);
        eqs.createEventQueue(pathQueue2);

        eqs.register("/profiles/.*", "greeting.name.say-hello", pathResource + ".*", pathQueue1);

        assertTrue("SetUp : failed during notification.register(/profiles/.*, greeting.name.say-hello," + pathResource + ".*, pathQueue1)",
                eqs.list().length == 1);

        eqs.register("/profiles/kermit", "greeting.name.read", pathResource + ".*", pathQueue2);
        Rule[] t = eqs.list();
        Rule r = t[0];

        assertTrue("SetUp : failed during notification.register(/profiles/.*, greeting.name.read," + pathResource + ".*, pathQueue2) " + r.getQueuePath() + " "
                + r.getObjectre() + " " + r.getSubjectre() + " " + r.getTargetre() + " " + eqs.list().length, eqs.list().length == 2);
        greeting.createName(pathResource, valueResource);
    }

    @After
    public void tearDown() throws Exception {

        eqs.unregister("/profiles/.*", "greeting.name.say-hello", pathResource + ".*", pathQueue1);
        eqs.unregister("/profiles/kermit", "greeting.name.read", pathResource + ".*", pathQueue2);

        eqs.removeQueue(pathQueue1);
        eqs.removeQueue(pathQueue2);
        greeting.deleteName(pathResource);
    }

    /**
     * Test Right Throw one event into one queue and assert the event is
     * inserted only in one queue the test assert too that no other event
     * appears into the queue
     * 
     * @throws Exception
     */

    @Test(timeout = 30000)
    public void testNotificationSimple() throws Exception {
        logger.info("testNotificationSimple() called");
        String caller = membership.getProfilePathForConnectedIdentifier();

        greeting.sayHello(pathResource);
        PersistentEvent[] lEvent1 = new PersistentEvent[] {};
        // wait that an event appears into the queue
        while (lEvent1.length == 0) {
            lEvent1 = eqs.getEvents(pathQueue1);
        }
        assertTrue(lEvent1.length == 1);
        PersistentEvent e = lEvent1[0];
        assertEquals("TestNotification1 : event resource expected /name but found " + e.getFromResource(), e.getFromResource(), pathResource);
        assertEquals("TestNotification1 : event type expected greeting.name.create but found " + e.getEventType(), e.getEventType(), "greeting.name.say-hello");
        assertEquals("TestNotification1 : event throwedBy expected " + caller + " but found" + e.getThrowedBy(), e.getThrowedBy(), caller);

        PersistentEvent[] lEvent2 = eqs.getEvents(pathQueue2);
        assertTrue("TestNotification1 : expected 0 event into queue2(" + pathQueue2 + ") but found " + lEvent2.length, lEvent2.length == 0);

        Thread.sleep(10);
        // assert that no other events happen
        lEvent1 = eqs.getEvents(pathQueue1);
        assertTrue("TestNotification1 : expected 0 event into queue1(" + pathQueue1 + ") but found " + lEvent1.length, lEvent1.length == 1);
    }

    /**
     * Test Right Throw one event matching by 0 queue
     * 
     * @throws InterruptedException
     * @throws GreetingServiceException
     * @throws EventQueueServiceException
     * @throws NotificationServiceException
     */
    @Test(timeout = 20000)
    public void testNotificationEventNotMatching() throws Exception, NotificationServiceException {
        logger.info("testNotificationEventNotMatching() called");
        greeting.createName("/tests/eventNotMatching", "eventNotMatching");
        greeting.deleteName("/tests/eventNotMatching");

        Thread.sleep(1000);
        assertEquals("TestNotificationEventNotMatching : expected 0 event into queue1(" + pathQueue1 + ") but found " + eqs.getEvents(pathQueue1).length, eqs
                .getEvents(pathQueue1).length, 0);
        assertEquals("TestNotificationEventNotMatching : expected 0 event into queue1(" + pathQueue2 + ") but found " + eqs.getEvents(pathQueue2).length, eqs
                .getEvents(pathQueue2).length, 0);

    }

    /**
     * Test Right Throw 2 events matching by one queue, but user can read only
     * one event
     * 
     * @throws MembershipServiceException
     * @throws GreetingServiceException
     * @throws EventQueueServiceException
     * @throws InterruptedException
     * @throws LoginException
     * @throws SecurityServiceException
     * @throws CoreServiceException
     */
    @Test(timeout = 50000)
    public void testNotificationRightEvent() throws Exception, LoginException, SecurityServiceException, CoreServiceException {
        logger.info("testNotificationRightEvent() called");

        loginContext.logout();

        // login root
        loginContext = new LoginContext("qualipso", new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS));
        loginContext.login();

        // root create a folder and a resource on this folder and
        // create a read event on this name
        core.createFolder("/kermitFolder", "kermitFolder", "greeting folder");
        greeting.createName("/kermitFolder/kermitName", "kermitName");
        greeting.readName("/kermitFolder/kermitName");
        greeting.deleteName("/kermitFolder/kermitName");
        core.deleteFolder("/kermitFolder");

        loginContext.logout();

        loginContext = new LoginContext("qualipso", new UsernamePasswordHandler("kermit", "thefrog"));
        loginContext.login();

        greeting.sayHello(pathResource);

        PersistentEvent[] lEvent = new PersistentEvent[] {};

        while (lEvent.length < 1) {
            lEvent = eqs.getEvents(pathQueue1);
        }

        assertTrue("TestNotificationRighEvent : expected 1 events into queue1(" + pathQueue1 + ") but found " + lEvent.length, lEvent.length == 1);
        PersistentEvent e = lEvent[0];
        assertEquals("TestNotificationRighEvent : event resource expected " + pathResource + " but found " + e.getFromResource(), e.getFromResource(),
                pathResource);
        assertEquals("TestNotificationRighEvent : event type expected greeting.name.sayhello but found " + e.getEventType(), e.getEventType(),
                "greeting.name.say-hello");
        assertEquals("TestNotificationRighEvent : event throwedBy expected kermit but found" + e.getThrowedBy(), e.getThrowedBy(), "/profiles/kermit");

        Thread.sleep(60);

        assertTrue("TestNotificationRighEvent : expected 0 event into queue1 but found " + eqs.getEvents(pathQueue2).length,
                eqs.getEvents(pathQueue2).length == 0);

    }

}
