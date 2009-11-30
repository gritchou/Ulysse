package org.qualipso.factory.client.test.sb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.auth.callback.UsernamePasswordHandler;
import org.junit.After;
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
    private static MembershipService membership;
    private final static String pathQueue1 = "/q1", pathQueue2 = "/q2";

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
        LoginContext loginContext = new LoginContext("qualipso", uph);
        loginContext.login();

        notification = (NotificationService) context.lookup(FactoryNamingConvention.SERVICE_PREFIX + NotificationService.SERVICE_NAME);
        eqs = (EventQueueService) context.lookup(FactoryNamingConvention.SERVICE_PREFIX + EventQueueService.SERVICE_NAME);
        greeting = (GreetingService) context.lookup(FactoryNamingConvention.SERVICE_PREFIX + GreetingService.SERVICE_NAME);
        membership = (MembershipService) context.lookup(FactoryNamingConvention.SERVICE_PREFIX + MembershipService.SERVICE_NAME);
    }

    @Before
    public void setUp() throws Exception {

        eqs.createEventQueue(pathQueue1);
        eqs.createEventQueue(pathQueue2);

        notification.register("/profiles/.*", "greeting.name.create", "/name.*", pathQueue1);
       
        assertTrue("SetUp : failed during notification.register(/profiles/.*, greeting.name.create,/name.*, pathQueue1)",notification.list().length==1);
        
        notification.register("/profiles/.*", "greeting.name.read", "/name.*", pathQueue2);
        Rule[] t = notification.list();
        Rule r = t[0];
        
        assertTrue("SetUp : failed during notification.register(/profiles/.*, greeting.name.read,/name.*, pathQueue2) "+r.getQueuePath()+" "+r.getObjectre()+" "+r.getSubjectre()+" "+r.getTargetre()+" "+notification.list().length,notification.list().length==2);
    }

    @After
    public void tearDown() throws Exception {

        notification.unregister("/profiles/.*", "greeting.name.create", "/name.*", pathQueue1);
        notification.unregister("/profiles/.*", "greeting.name.read", "/name.*", pathQueue2);

        eqs.removeQueue(pathQueue1);
        eqs.removeQueue(pathQueue2);
    }

    /**
     * Test Right Throw one event into one queue and assert the event is
     * inserted only in one queue the test assert too that no other event
     * appears into the queue
     * 
     * @throws Exception
     */

    @Test(timeout = 10000)
    public void testNotificationSimple() throws Exception {
    	logger.info("testNotificationSimple called");
        String caller = membership.getProfilePathForConnectedIdentifier();

        greeting.createName("/name", "toto");
        Event[] lEvent1 = new Event[] {};

        // wait that an event appears into the queue
        while (lEvent1.length == 0) {
            Thread.sleep(100);
            lEvent1 = eqs.getEvents(pathQueue1);
        }

        assertTrue(lEvent1.length == 1);
        Event e = lEvent1[0];
        assertEquals("TestNotification1 : event resource expected /name but found " + e.getFromResource(), e.getFromResource(), "/name");
        assertEquals("TestNotification1 : event type expected greeting.name.create but found " + e.getEventType(), e.getEventType(), "greeting.name.create");
        assertEquals("TestNotification1 : event throwedBy expected " + caller + " but found" + e.getThrowedBy(), e.getThrowedBy(), caller);

        Event[] lEvent2 = eqs.getEvents(pathQueue2);
        assertTrue("TestNotification1 : expected 0 event into queue2(" + pathQueue2 + ") but found " + lEvent2.length, lEvent2.length == 0);

        Thread.sleep(10);
        // assert that no other events happen
        lEvent1 = eqs.getEvents(pathQueue1);
        assertTrue("TestNotification1 : expected 0 event into queue1(" + pathQueue1 + ") but found " + lEvent1.length, lEvent1.length == 1);

        greeting.deleteName("/name");

    }

    /**
     * Test Right Throw 10 events into one queue and assert the order of these
     * 10 events
     * 
     * @throws Exception
     */
    @Test(timeout = 10000)
    public void testNotificationOrderEvent() throws Exception {

        greeting.createName("/name", "toto");

        Date beginThrow = new Date();
        for (int i = 0; i < 10; i++) {
            greeting.readName("/name");
        }
        Date endThrow = new Date();

        Event[] lEvent2 = new Event[] {};
        while (lEvent2.length < 9) {
            lEvent2 = eqs.getEvents(pathQueue2);
        }

        assertEquals(lEvent2.length, 9);
        Date d = lEvent2[0].getDate();
        int i = 1;
        while (i < lEvent2.length) {
            assertTrue("TestNotificationOrderEvent : date event " + (i - 1) + " must be before event " + i, d.before(lEvent2[i].getDate()));
            assertTrue("TestNotificationOrderEvent : event" + i + " must be after the begin of throw ", d.after(beginThrow));
            assertTrue("TestNotificationOrderEvent : event" + i + " must be after the begin of throw ", d.before(endThrow));
            d = lEvent2[i].getDate();
            i++;
        }

        Event[] lEvent1 = eqs.getEvents(pathQueue1);
        assertEquals("TestNotificationOrderEvent : expected 1 event into queue1(" + pathQueue1 + ") but found " + lEvent1.length + " events", lEvent1.length, 1);

        greeting.deleteName("/name");
    }

    /**
     * Test Right Throw one event matching by 0 queue
     * 
     * @throws InterruptedException
     * @throws GreetingServiceException
     * @throws EventQueueServiceException
     * @throws NotificationServiceException
     */
    @Test(timeout = 10000)
    public void testNotificationEventNotMatching() throws InterruptedException, GreetingServiceException, EventQueueServiceException,
            NotificationServiceException {
        
        greeting.createName("/toto", "toto");
        greeting.sayHello("/toto");
        greeting.deleteName("/toto");
        
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
     */
    @Test(timeout = 10000)
    public void testNotificationRightEvent() throws MembershipServiceException, GreetingServiceException, EventQueueServiceException, InterruptedException {
        membership.createProfile("marlene", "marlene", "toto@gmail.com", 0);
        String caller = membership.getProfilePathForConnectedIdentifier();

        greeting.createNameWithUser("/name", "toto", "marlene");
        greeting.readName("/name/toto");

        Event[] lEvent2 = new Event[] {};

        while (lEvent2.length < 1) {
            lEvent2 = eqs.getEvents(pathQueue2);
        }

        assertTrue("TestNotificationRighEvent : expected 1 events into queue2(" + pathQueue2 + ") but found " + lEvent2.length, lEvent2.length == 1);
        Event e = lEvent2[0];
        assertEquals("TestNotificationRighEvent : event resource expected /name/toto but found " + e.getFromResource(), e.getFromResource(), "/name/toto");
        assertEquals("TestNotificationRighEvent : event type expected greeting.name.create but found " + e.getEventType(), e.getEventType(),
                "greeting.name.read");
        assertEquals("TestNotificationRighEvent : event throwedBy expected " + caller + " but found" + e.getThrowedBy(), e.getThrowedBy(), caller);

        Thread.sleep(60);

        assertTrue("TestNotificationRighEvent : expected 0 event into queue1 but found " + eqs.getEvents(pathQueue1).length,
                eqs.getEvents(pathQueue1).length == 0);
    }

    /**
     * Test Boundary Throw a null event
     * 
     * @throws NotificationServiceException
     */
    @Test(expected = NotificationServiceException.class)
    public void testNotificationThrowNull() throws NotificationServiceException {
        greeting.throwNullEvent();
    }

    /**
     * Test Boundary Throws 2 times the same event
     * 
     * @throws MembershipServiceException
     * @throws NotificationServiceException
     * @throws EventQueueServiceException
     */
    @Test(timeout = 10000)
    public void testNotificationThrow2SameEvent() throws NotificationServiceException, MembershipServiceException, EventQueueServiceException {
        greeting.throw2SameEvent("/name/toto");

        Event[] lEvent = new Event[] {};
        while (lEvent.length < 2) {
            lEvent = eqs.getEvents(pathQueue1);
        }

        assertEquals("TestNotificationThrow2SameEvent : expected " + lEvent[0].toString() + " but found " + lEvent[1].toString(), lEvent[0], lEvent[1]);
    }

    /**
     * Test Boundary Throws a false event
     * 
     * @throws NotificationServiceException
     */
    @Test(expected = NotificationServiceException.class)
    public void testNotificationFalseEvent() throws NotificationServiceException {
        greeting.throwFacticeEvent();
    }

    /**
     * Test Boundary - Performance throws 10000 events in 1 queue
     * 
     * @throws GreetingServiceException
     * @throws EventQueueServiceException
     */
    // @Test(timeout = 1000)
    public void testNotification10000Events() throws GreetingServiceException, EventQueueServiceException {

        for (int i = 0; i < 10000; i++) {
            greeting.readName("/name/toto");
        }

        Event[] lEvent = new Event[] {};
        while (lEvent.length < 10000) {
            lEvent = eqs.getEvents(pathQueue2);
        }

        assertTrue("TestNotification10000Events : expected 10000 events but found " + lEvent.length, lEvent.length == 10000);
    }

    /**
     * Test Boundary - Performance throws 1 event into 10000 queues
     * 
     * @throws EventQueueServiceException
     * @throws NotificationServiceException
     */
    // @Test(timeout = 1000)
    public void testNotification10000Queues() throws EventQueueServiceException, NotificationServiceException {
        String path = "/eventQueue/q";
        for (int i = 0; i < 10000; i++) {
            eqs.createEventQueue(path + i);
            notification.register("/profiles/*", "/name/*", "greeting.name.read", path + i);
        }

        Event[] lEvent = new Event[] {};
        for (int i = 0; i < 10000; i++) {
            while (lEvent.length < 1) {
                lEvent = eqs.getEvents(path + i);
            }
            assertTrue("TestNotification10000Queues : expected 1 event but found " + lEvent.length, lEvent.length == 1);
        }
    }

}