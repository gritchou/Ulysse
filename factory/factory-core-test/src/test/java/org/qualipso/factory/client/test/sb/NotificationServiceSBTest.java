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
import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.eventqueue.EventQueueServiceException;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.greeting.GreetingService;
import org.qualipso.factory.greeting.GreetingServiceException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.notification.NotificationServiceException;
import org.qualipso.factory.notification.entity.Rule;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 7 october 2009
 */
public class NotificationServiceSBTest {
    private static Log logger = LogFactory.getLog(NotificationServiceSBTest.class);
    private static Context context;
    private static NotificationService notification;
    private static GreetingService greeting;
    private static EventQueueService eqs;
    private static LoginContext loginContext;
    private static MembershipService membership;
    private final static String pathQueue1 = "/q1", pathQueue2 = "/q2";
    private final static String pathResource = "/m2log";
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

        UsernamePasswordHandler uph = new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS);
        loginContext = new LoginContext("qualipso", uph);
        loginContext.login();

        notification = (NotificationService) context.lookup(FactoryNamingConvention.SERVICE_PREFIX + NotificationService.SERVICE_NAME);
        eqs = (EventQueueService) context.lookup(FactoryNamingConvention.SERVICE_PREFIX + EventQueueService.SERVICE_NAME);
        greeting = (GreetingService) context.lookup(FactoryNamingConvention.SERVICE_PREFIX + GreetingService.SERVICE_NAME);
        membership = (MembershipService) context.lookup(FactoryNamingConvention.SERVICE_PREFIX + MembershipService.SERVICE_NAME);

    }

    @AfterClass
    public static void after() throws Exception {

    }

    @Before
    public void setUp() throws Exception {

        eqs.createEventQueue(pathQueue1);
        eqs.createEventQueue(pathQueue2);

        notification.register("/profiles/.*", "greeting.name.say-hello", "/m2log.*", pathQueue1);

        assertTrue("SetUp : failed during notification.register(/profiles/.*, greeting.name.say-hello,/m2log.*, pathQueue1)", notification.list().length == 1);

        notification.register("/profiles/.*", "greeting.name.read", "/m2log.*", pathQueue2);
        Rule[] t = notification.list();
        Rule r = t[0];

        assertTrue("SetUp : failed during notification.register(/profiles/.*, greeting.name.read,/m2log.*, pathQueue2) " + r.getQueuePath() + " "
                + r.getObjectre() + " " + r.getSubjectre() + " " + r.getTargetre() + " " + notification.list().length, notification.list().length == 2);
        greeting.createName(pathResource, valueResource);
    }

    @After
    public void tearDown() throws Exception {

        notification.unregister("/profiles/.*", "greeting.name.say-hello", "/m2log.*", pathQueue1);
        notification.unregister("/profiles/.*", "greeting.name.read", "/m2log.*", pathQueue2);

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
        Event[] lEvent1 = new Event[] {};
        // wait that an event appears into the queue
        while (lEvent1.length == 0) {
            lEvent1 = eqs.getEvents(pathQueue1);
        }
        assertTrue(lEvent1.length == 1);
        Event e = lEvent1[0];
        assertEquals("TestNotification1 : event resource expected /name but found " + e.getFromResource(), e.getFromResource(), pathResource);
        assertEquals("TestNotification1 : event type expected greeting.name.create but found " + e.getEventType(), e.getEventType(), "greeting.name.say-hello");
        assertEquals("TestNotification1 : event throwedBy expected " + caller + " but found" + e.getThrowedBy(), e.getThrowedBy(), caller);

        Event[] lEvent2 = eqs.getEvents(pathQueue2);
        assertTrue("TestNotification1 : expected 0 event into queue2(" + pathQueue2 + ") but found " + lEvent2.length, lEvent2.length == 0);

        Thread.sleep(10);
        // assert that no other events happen
        lEvent1 = eqs.getEvents(pathQueue1);
        assertTrue("TestNotification1 : expected 0 event into queue1(" + pathQueue1 + ") but found " + lEvent1.length, lEvent1.length == 1);
    }

    /**
     * Test Right Throw 10 events into one queue and assert the order of these
     * 10 events
     * 
     * TODO
     * 
     * @throws Exception
     * 
     @Test(timeout = 10000) public void testNotificationOrderEvent() throws
     *               Exception {
     *               logger.info("testNotificationOrderEvent() called");
     *               greeting.createName("/name", "toto");
     * 
     *               Date beginThrow = new Date(); for (int i = 0; i < 10; i++)
     *               { greeting.readName("/name"); } Date endThrow = new Date();
     * 
     *               Event[] lEvent2 = new Event[] {}; while (lEvent2.length <
     *               9) { lEvent2 = eqs.getEvents(pathQueue2); }
     * 
     *               assertEquals(lEvent2.length, 9); Date d =
     *               lEvent2[0].getDate(); int i = 1; while (i < lEvent2.length)
     *               { assertTrue("TestNotificationOrderEvent : date event " +
     *               (i - 1) + " must be before event " + i,
     *               d.before(lEvent2[i].getDate()));
     *               assertTrue("TestNotificationOrderEvent : event" + i +
     *               " must be after the begin of throw ", d.after(beginThrow));
     *               assertTrue("TestNotificationOrderEvent : event" + i +
     *               " must be after the begin of throw ", d.before(endThrow));
     *               d = lEvent2[i].getDate(); i++; }
     * 
     *               Event[] lEvent1 = eqs.getEvents(pathQueue1); assertEquals(
     *               "TestNotificationOrderEvent : expected 1 event into queue1("
     *               + pathQueue1 + ") but found " + lEvent1.length + " events",
     *               lEvent1.length, 1);
     * 
     *               greeting.deleteName("/name"); }
     */

    /**
     * Test Right Throw one event matching by 0 queue
     * 
     * @throws InterruptedException
     * @throws GreetingServiceException
     * @throws EventQueueServiceException
     * @throws NotificationServiceException
     */
    @Test(timeout = 20000)
    public void testNotificationEventNotMatching() throws InterruptedException, GreetingServiceException, EventQueueServiceException,
            NotificationServiceException {
        logger.info("testNotificationEventNotMatching() called");
        greeting.createName("/eventNotMatching", "eventNotMatching");
        greeting.deleteName("/eventNotMatching");

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
     */
    @Test(timeout = 50000)
    public void testNotificationRightEvent() throws MembershipServiceException, GreetingServiceException, EventQueueServiceException, InterruptedException,
            LoginException {
        logger.info("testNotificationRightEvent() called");
        String caller = membership.getProfilePathForConnectedIdentifier();

        // root permit to kermit to create something on /
        greeting.giveAutorization("/", "/profiles/kermit", new String[] { "create" });

        loginContext.logout();

        // login kermit
        UsernamePasswordHandler uph = new UsernamePasswordHandler("kermit", "thefrog");
        loginContext = new LoginContext("qualipso", uph);
        loginContext.login();

        // kermit create a folder and a resource on this folder and
        // create a read event on this name
        greeting.createFolder("/kermitFolder", "kermitFolder");
        greeting.createName("/kermitFolder/kermitName", "kermitName");
        greeting.readName("/kermitFolder/kermitName");
        greeting.deleteName("/kermitFolder/kermitName");
        greeting.deleteFolder("/kermitFolder");

        loginContext.logout();

        uph = new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS);
        loginContext = new LoginContext("qualipso", uph);
        loginContext.login();

        greeting.readName(pathResource);

        Event[] lEvent2 = new Event[] {};

        while (lEvent2.length < 1) {
            lEvent2 = eqs.getEvents(pathQueue2);
        }

        assertTrue("TestNotificationRighEvent : expected 1 events into queue2(" + pathQueue2 + ") but found " + lEvent2.length, lEvent2.length == 1);
        Event e = lEvent2[0];
        assertEquals("TestNotificationRighEvent : event resource expected " + pathResource + " but found " + e.getFromResource(), e.getFromResource(),
                pathResource);
        assertEquals("TestNotificationRighEvent : event type expected greeting.name.create but found " + e.getEventType(), e.getEventType(),
                "greeting.name.read");
        assertEquals("TestNotificationRighEvent : event throwedBy expected " + caller + " but found" + e.getThrowedBy(), e.getThrowedBy(), caller);

        Thread.sleep(60);

        assertTrue("TestNotificationRighEvent : expected 1 event into queue1 but found " + eqs.getEvents(pathQueue2).length,
                eqs.getEvents(pathQueue2).length == 1);

    }

    /**
     * Test Boundary Throw a null event
     * 
     * @throws NotificationServiceException
     */
    @Test(expected = NotificationServiceException.class)
    public void testNotificationThrowNull() throws NotificationServiceException {
        logger.info("testNotificationThrowNull() called");
        greeting.throwNullEvent();
    }

    // /**
    // * Test Boundary Throws 2 times the same event
    // *
    // * @throws MembershipServiceException
    // * @throws NotificationServiceException
    // * @throws EventQueueServiceException
    // */
    // @Test(timeout = 30000)
    // public void testNotificationThrow2SameEvent() throws
    // NotificationServiceException, MembershipServiceException,
    // EventQueueServiceException {
    // logger.info("testNotificationThrow2SameEvent() called");
    // greeting.throw2SameEvent("/name/toto");
    //
    // Event[] lEvent = new Event[] {};
    // while (lEvent.length < 2) {
    // lEvent = eqs.getEvents(pathQueue1);
    // }
    //
    // assertEquals("TestNotificationThrow2SameEvent : expected " +
    // lEvent[0].toString() + " but found " + lEvent[1].toString(), lEvent[0],
    // lEvent[1]);
    // }

    // /**
    // * Test Boundary Throws a false event
    // *
    // * @throws NotificationServiceException
    // */
    // @Test(expected = NotificationServiceException.class)
    // public void testNotificationFalseEvent() throws
    // NotificationServiceException {
    // logger.info("testNotificationFalseEvent() called");
    // greeting.throwFacticeEvent();
    // }

}