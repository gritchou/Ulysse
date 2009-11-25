package org.qualipso.factory.client.test.sb;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.bootstrap.BootstrapService;
import org.qualipso.factory.bootstrap.BootstrapServiceException;
import org.qualipso.factory.client.test.AllTests;
import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.eventqueue.EventQueueServiceException;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.NotificationServiceException;

public class EventQueueServiceSBTest {
    private static Context context;

    private static Log logger = LogFactory.getLog(EventQueueServiceSBTest.class);

    private static EventQueueService eqs;
    private static LoginContext loginContext;

    private final static String pathQ1 = "/eventqueue1", pathQ2 = "/eventqueue2";

    /**
     * Set up service for all tests.
     * 
     * @throws LoginException
     * @throws NotificationServiceException
     * @throws MembershipServiceException
     */
    @BeforeClass
    public static void before() throws NamingException, LoginException, MembershipServiceException {
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

        eqs = (EventQueueService) context.lookup(FactoryNamingConvention.SERVICE_PREFIX + EventQueueService.SERVICE_NAME);
        // paph = (PAPServiceHelper) context.lookup("PAPServiceHelper");
        // membership = (MembershipService)
        // context.lookup(FactoryNamingConvention.getJNDINameForService("membership"));
        // membership.createProfile("toto", "toto titi", "toto@gmail.com", 0);
        // membership.createProfile("resource", "resource",
        // "resource@gmail.com", 0);

    }

    @AfterClass
    public static void after() throws LoginException, NamingException, MembershipServiceException {
        // membership.deleteProfile("toto");
        // membership.deleteProfile("resource");

        loginContext.logout();
        context.close();
    }

    @Before
    public void setUp() throws NamingException, FactoryException {
        eqs.createEventQueue(pathQ1);
        eqs.createEventQueue(pathQ2);
    }

    @After
    public void tearDown() throws EventQueueServiceException {
        eqs.removeQueue(pathQ1);
        eqs.removeQueue(pathQ2);
    }

    @Test
    // test the creation of the EventQueue
    /**
     * Test existence of Event in EventQueue
     * 
     * **/
    // *******************************Right**************************************************
    public void test1() throws EventQueueServiceException {
        logger.debug(" Test existence of Event in EventQueue(...)");

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");
        eqs.pushEvent(pathQ1, myEvent1);
        eqs.pushEvent(pathQ1, myEvent2);
        Event[] tabEvent = eqs.getEvents(pathQ1);

        assertEquals(tabEvent[0], myEvent1);
        assertEquals(tabEvent[1], myEvent2);
        assertEquals(tabEvent.length, 2);

    }

    /**
     * push 1 Event in 2 EventQueue and verification
     * 
     * @throws EventQueueServiceException
     */
    @Test
    public void test2() throws EventQueueServiceException {
        logger.debug("push 1 Event in 2 EventQueue and verification(...)");

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        eqs.pushEvent(pathQ1, myEvent1);
        eqs.pushEvent(pathQ2, myEvent1);
        Event e1 = eqs.getLastEvent(pathQ1);
        Event e2 = eqs.getLastEvent(pathQ2);

        assertEquals(e1, myEvent1);
        assertEquals(e2, myEvent1);

    }

    /**
     * push 1 Event in inexisting EventQueue and verification
     * 
     * @throws EventQueueServiceException
     */
    @Test(expected = EventQueueServiceException.class)
    public void test3() throws EventQueueServiceException {

        logger.debug("push 1 Event inexisting EventQueue and verification(...)");
        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        eqs.pushEvent("/eventqueueinexist", myEvent1);

    }

    /**
     * test: search event(by name) not exist in eventQueue
     * 
     * @throws EventQueueServiceException
     */
    @Test
    public void test4() throws EventQueueServiceException {

        logger.debug("test: search event(by name) not exist in eventQueue(...)");

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");

        eqs.pushEvent(pathQ1, myEvent1);
        eqs.pushEvent(pathQ1, myEvent2);

        Event[] resultFind = eqs.findEventFromRessource(pathQ1, "myEvent3", false);

        assertEquals(resultFind.length, 0);

    }

    /**
     * test: search event(by creator ) existing in the eventQueue
     * 
     * @throws EventQueueServiceException
     */
    @Test
    public void test5() throws EventQueueServiceException {

        logger.debug("test: search event(by creator ) existing in eventQueue(...)");

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");
        Event myEvent3 = new Event("/path/resource/", "titi", "resourceType", "commit", "");

        eqs.pushEvent(pathQ1, myEvent1);
        eqs.pushEvent(pathQ1, myEvent2);
        eqs.pushEvent(pathQ1, myEvent3);

        Event[] resultFind = eqs.findEventFromRessource(pathQ1, "titi", false);
        assertEquals(resultFind[0], myEvent3);
        assertEquals(resultFind[1], myEvent2);
        assertEquals(resultFind.length, 2);

    }

    /**
     * 
     * test: search event(by eventType ) existing in the eventQueueEventQue
     * 
     * @throws EventQueueServiceException
     */
    @Test
    public void test6() throws EventQueueServiceException {

        logger.debug("test: search event(eventType) existing in eventQueue(...)");
        // eqs.createEventQueue("/eventqueue1");

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");
        Event myEvent3 = new Event("/path/resource/", "titi", "resourceType", "commit", "");

        eqs.pushEvent(pathQ1, myEvent1);
        eqs.pushEvent(pathQ1, myEvent2);
        eqs.pushEvent(pathQ1, myEvent3);

        Event[] resultFind = eqs.findEventFromRessource(pathQ1, "read", false);
        assertEquals(resultFind[1], myEvent2);
        assertEquals(resultFind[2], myEvent1);
        assertEquals(resultFind.length, 2);

    }

    /**
     * Test:delete an Event from EventQueue
     * 
     * @throws EventQueueServiceException
     */
    @Test
    public void test7() throws EventQueueServiceException {

        logger.debug("test: delete an Event from EventQueue(...)");
        // eqs.createEventQueue("/eventqueue1");

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");

        eqs.pushEvent(pathQ1, myEvent1);
        eqs.pushEvent(pathQ1, myEvent2);
        eqs.deleteEvent(pathQ1, myEvent1);
        eqs.deleteEvent(pathQ1, myEvent2);
        Event[] tabEvent = eqs.getEvents(pathQ1);
        assertEquals(tabEvent.length, 0);

    }

    /**
     * Test:remove an EventQueue
     * 
     * @throws EventQueueServiceException
     */
    @Test(expected = EventQueueServiceException.class)
    public void test8() throws EventQueueServiceException {

        logger.debug("test: delete an Event from EventQueue(...)");
        eqs.createEventQueue("/eventqueue3");
        eqs.removeQueue("/eventqueue3");
        eqs.getEvents("/eventqueue3");

    }

    /**
     * test: testing lastEvent in the EventQueue
     * 
     * @throws EventQueueServiceException
     */
    @Test
    public void test9() throws EventQueueServiceException {

        logger.debug("test: delete an Event from EventQueue(...)");
        // eqs.createEventQueue("/eventqueue1");
        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");
        Event myEvent3 = new Event("/path/resource/", "titi", "resourceType", "write", "");

        eqs.pushEvent(pathQ1, myEvent1);
        eqs.pushEvent(pathQ1, myEvent2);
        eqs.pushEvent(pathQ1, myEvent3);
        Event[] tabEvent = eqs.getEvents(pathQ1);

        for (int i = 0; i < tabEvent.length; i++) {
            Event e = eqs.getLastEvent(pathQ1);
            assertEquals(e, tabEvent[i]);

            eqs.deleteEvent(pathQ1, e);

        }

    }

    // ******************************** Boundary
    // ******************************************

    /**
     * Test:push null in an eventQueue
     * 
     * @throws EventQueueServiceException
     */

    @Test(expected = EventQueueServiceException.class)
    public void testPushNullInEventQueue() throws EventQueueServiceException {

        logger.debug("test: push null in an eventqueue");

        eqs.pushEvent(pathQ1, null);

    }

    /**
     * Test: push the same event in the eventQueue
     * 
     * @throws EventQueueServiceException
     */
    @Test
    public void testPushSameEventInEventQueue() throws EventQueueServiceException {
        logger.debug("test: push the same event in the eventQueue");

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        eqs.pushEvent(pathQ1, myEvent1);
        eqs.pushEvent(pathQ1, myEvent1);
        Event[] tabEvent = eqs.getEvents(pathQ1);
        for (int i = 0; i < tabEvent.length; i++) {
            assertEquals(tabEvent[i], myEvent1);
        }

    }

    /**
     * Test: Test the desorder (delete an event before pushing the same event in
     * EventQueue)
     * 
     * @throws EventQueueServiceException
     */
  /*  @Test(expected = EventQueueServiceException.class)
    public void testDesorderPushDelete() throws EventQueueServiceException {
        logger.debug("test:Test the desorder");

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        eqs.deleteEvent(pathQ1, myEvent1);

    }*/

    
    /**
     * Test a BoudaryEvent. This test use a specific pushEvent in an EventQueue.
     * 
     * @throws EventQueueServiceException
     */

  /*  @Test(expected = EventQueueServiceException.class)
    public void testBoundaryEvent() throws EventQueueServiceException {

        int i = 0;
        while (i < 10000) {

            Event e = new Event("/path/resource/", "toto" + i, "resourceType" + i, "read", "");
            eqs.pushEvent(pathQ1, e);

            i++;
        }
        Event[] tabEvent = eqs.getEvents(pathQ1);
        assertEquals(tabEvent.length, 10000);

    }*/

    /**
     * Test Boundary EventQueue. This test the push an event in many eventQueue
     * 
     * @throws EventQueueServiceException
     */
  /*  @Test(expected = EventQueueServiceException.class)
    public void testBoundaryEventQueue() throws EventQueueServiceException {
        Event e = new Event("/path/resource/", "toto", "resourceType", "read", "");

        for (int i = 3; i < 10003; i++) {
            eqs.createEventQueue("/eventqueue" + i);
            eqs.pushEvent("/eventqueue" + i, e);
        }
        for (int j = 3; j < 10003; j++) {
            Event[] tabEvent = eqs.getEvents("/eventqueue" + j);
            assertEquals(tabEvent.length, 1);
            assertEquals(tabEvent[0], e);
        }
        for (int i = 3; i < 10003; i++) {
            eqs.removeQueue("/eventqueue" + i);
        }
    }*/

    // ******************************** Cross-check
    // ******************************************
    /**
     * Test: test the cross check of findEvent
     * 
     */
   /* @Test
    public void testcrossCheckOfFindEvent() throws EventQueueServiceException {
        logger.debug("test: Test: test the cross check of findEvent(...)");
        // eqs.createEventQueue("/eventqueue1");
        List<Event> list = new ArrayList<Event>();
        String nameOwner = "titi";

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");
        Event myEvent3 = new Event("/path/resource/", "titi", "resourceType", "commit", "");

        eqs.pushEvent(pathQ1, myEvent1);
        eqs.pushEvent(pathQ1, myEvent2);
        eqs.pushEvent(pathQ1, myEvent3);
        Event[] tabEvent = eqs.getEvents(pathQ1);

        for (int i = 0; i < tabEvent.length; i++) {
            if (tabEvent[i].getThrowedBy().equals(nameOwner)) {
                list.add(tabEvent[i]);
            }
        }

        assertEquals(list.get(0), myEvent3);
        assertEquals(list.get(1), myEvent2);
        assertEquals(list.size(), 2);

    }

    // ******************************* Performance
    // ***************************************************************
    /**
     * Test a full disk issue. This test the push an event in many eventQueue
     * 
     * @throws EventQueueServiceException
     */
   /* @Test(expected = EventQueueServiceException.class)
    public void testFullDiskEventQueue() throws EventQueueServiceException {
        Event e = new Event("/path/resource/", "toto", "resourceType", "read", "");

        int i = 3;
        while (true) {
            eqs.createEventQueue("/eventqueue" + i);
            eqs.pushEvent("/eventqueue" + i, e);

            i++;
        }

    }*/

    /**
     * Test a full disk issue. This test use a specific pushEvent in an
     * EventQueue.
     * 
     */

    /*@Test(expected = EventQueueServiceException.class)
    public void testFullDiskEvent() throws EventQueueServiceException {

        int i = 0;
        while (true) {

            Event e = new Event("/path/resource/", "toto" + i, "resourceType" + i, "read", "");
            eqs.pushEvent(pathQ1, e);

            i++;
        }

    }*/

}
