package org.qualipso.factory.client.test.sb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.NotificationServiceException;
import org.qualipso.factory.security.pap.PAPServiceHelper;

public class EventQueueServiceSBTest {
    private static Context context;

    private static Log logger = LogFactory.getLog(EventQueueServiceSBTest.class);
    private MembershipService membership;
    private EventQueueService eqs;
    private static LoginContext loginContext;
    private PAPServiceHelper paph;

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

    }

    @AfterClass
    public static void after() throws LoginException, NamingException {
        loginContext.logout();
        context.close();
    }

    @Before
    public void setUp() throws NamingException, FactoryException {
        eqs = (EventQueueService) context.lookup(FactoryNamingConvention.SERVICE_PREFIX + EventQueueService.SERVICE_NAME);
        //paph = (PAPServiceHelper) context.lookup("PAPServiceHelper");
        membership = (MembershipService) context.lookup(FactoryNamingConvention.getJNDINameForService("membership"));
        membership.createProfile("toto", "toto titi", "toto@gmail.com", 0);
        membership.createProfile("resource", "resource", "resource@gmail.com", 0);
    }

    @Test
    // test the creation of the EventQueue
    /**
     * Test existence of Event in EventQueue
     * 
     * **/
    // *******************************Right**************************************************
    public void test1() throws EventQueueServiceException {
        String path = "/eventqueue";
        logger.debug(" Test existence of Event in EventQueue(...)");
        eqs.createEventQueue(path);
        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");
        eqs.pushEvent("/eventqueue", myEvent1);
        eqs.pushEvent("/eventqueue", myEvent2);
        Event[] tabEvent = eqs.getEvents("/eventqueue");
        boolean b = tabEvent.length == 2;

        assertTrue(tabEvent[0].equals(myEvent2) && tabEvent[1].equals(myEvent1) && b);

    }

    /**
     * push 1 Event in 2 EventQueue and verification
     * 
     * @throws EventQueueServiceException
     */

    public void test2() throws EventQueueServiceException {
        String path1 = "/eventqueues1";
        String path2 = "/eventqueues2";
        logger.debug("push 1 Event in 2 EventQueue and verification(...)");
        eqs.createEventQueue(path1);
        eqs.createEventQueue(path2);

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue2", myEvent1);
        Event e1 = eqs.getLastEvent("/eventqueues1");
        Event e2 = eqs.getLastEvent("/eventqueues2");

        assertTrue(e1.equals(myEvent1) && e2.equals(myEvent1));

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

    public void test4() throws EventQueueServiceException {

        logger.debug("test: search event(by name) not exist in eventQueue(...)");
        eqs.createEventQueue("/eventqueue1");

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");

        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue1", myEvent2);
       
        Event[] resultFind = eqs.findEventFromRessource("/eventqueue1", "myEvent3", false);

        assertEquals(resultFind.length, 0);

    }

    /**
     * test: search event(by creator ) existing in the eventQueue
     * 
     * @throws EventQueueServiceException
     */

    public void test5() throws EventQueueServiceException {

        logger.debug("test: search event(by creator ) existing in eventQueue(...)");
        eqs.createEventQueue("/eventqueue1");

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");
        Event myEvent3 = new Event("/path/resource/", "titi", "resourceType", "commit", "");

        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue1", myEvent2);
        eqs.pushEvent("/eventqueue1", myEvent3);

        Event[] resultFind = eqs.findEventFromRessource("/eventqueue1", "titi", false);
        boolean b1 = resultFind[0].equals(myEvent3);
        boolean b2 = resultFind[1].equals(myEvent2);
        boolean b3 = resultFind.length == 2;
        assertTrue(b1 && b2 && b3);

    }

    /**
     * 
     * test: search event(by eventType ) existing in the eventQueueEventQue
     * 
     * @throws EventQueueServiceException
     */

    public void test6() throws EventQueueServiceException {

        logger.debug("test: search event(eventType) existing in eventQueue(...)");
        eqs.createEventQueue("/eventqueue1");

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");
        Event myEvent3 = new Event("/path/resource/", "titi", "resourceType", "commit", "");

        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue1", myEvent2);
        eqs.pushEvent("/eventqueue1", myEvent3);

        Event[] resultFind = eqs.findEventFromRessource("/eventqueue1", "read", false);
        boolean b1 = resultFind[1].equals(myEvent2);
        boolean b2 = resultFind[2].equals(myEvent1);
        boolean b3 = resultFind.length == 2;
        assertTrue(b1 && b2 && b3);

    }

    /**
     * Test:delete an Event from EventQueue
     * 
     * @throws EventQueueServiceException
     */
    public void test7() throws EventQueueServiceException {

        logger.debug("test: delete an Event from EventQueue(...)");
        eqs.createEventQueue("/eventqueue1");

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");

        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue1", myEvent2);
        eqs.deleteEvent("/eventqueue1", myEvent1);
        eqs.deleteEvent("/eventqueue1", myEvent2);
        Event[] tabEvent = eqs.getEvents("/eventqueue1");
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
        eqs.createEventQueue("/eventqueue1");
        eqs.removeQueue("/eventqueue1");
        Event[] tabEvent = eqs.getEvents("/eventqueue1");

    }

    /**
     * test: testing lastEvent in the EventQueue
     * 
     * @throws EventQueueServiceException
     */
    public void test9() throws EventQueueServiceException {

        logger.debug("test: delete an Event from EventQueue(...)");
        eqs.createEventQueue("/eventqueue1");
        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");
        Event myEvent3 = new Event("/path/resource/", "titi", "resourceType", "write", "");

        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue1", myEvent2);
        eqs.pushEvent("/eventqueue1", myEvent3);
        Event[] tabEvent = eqs.getEvents("/eventqueue1");

        boolean b = true;

        for (int i = 0; i < tabEvent.length; i++) {
            Event e = eqs.getLastEvent("/eventqueue1");
            if (!e.equals(tabEvent[i])) {
                b = false;
            }

            eqs.deleteEvent("/eventqueue1", e);

        }

        assertTrue(b);

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
        eqs.createEventQueue("/eventqueue1");
        eqs.pushEvent("/eventqueue1", null);

    }

    /**
     * Test: push the same event in the eventQueue
     * 
     * @throws EventQueueServiceException
     */
    public void testPushSameEventInEventQueue() throws EventQueueServiceException {
        logger.debug("test: push the same event in the eventQueue");
        eqs.createEventQueue("/eventqueue1");
        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue1", myEvent1);
        Event[] tabEvent = eqs.getEvents("/eventqueue1");
        boolean b = true;
        for (int i = 0; i < tabEvent.length; i++) {
            if (!tabEvent[i].equals(myEvent1)) {
                b = false;
            }
        }
        assertTrue(b);

    }

    /**
     * Test: Test the desorder (delete an event before pushing the same event in
     * EventQueue)
     * 
     * @throws EventQueueServiceException
     */
    @Test(expected = EventQueueServiceException.class)
    public void testDesorderPushDelete() throws EventQueueServiceException {
        logger.debug("test:Test the desorder");
        eqs.createEventQueue("/eventqueue1");
        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        eqs.deleteEvent("/eventqueue1", myEvent1);

    }

    /**
     * Test a BoudaryEvent. This test use a specific pushEvent in an EventQueue.
     * 
     * @throws EventQueueServiceException
     */

    @Test(expected = EventQueueServiceException.class)
    public void testBoundaryEvent() throws EventQueueServiceException {

        eqs.createEventQueue("/eventqueue1");
        int i = 0;
        while (i < 10000) {

            Event e = new Event("/path/resource/", "toto" + i, "resourceType" + i, "read", "");
            eqs.pushEvent("/eventqueue1", e);

            i++;
        }
        Event[] tabEvent = eqs.getEvents("/eventqueue1");
        assertEquals(tabEvent.length, 10000);

    }

    /**
     * Test Boundary EventQueue. This test the push an event in many eventQueue
     * 
     * @throws EventQueueServiceException
     */
    @Test(expected = EventQueueServiceException.class)
    public void testBoundaryEventQueue() throws EventQueueServiceException {
        Event e = new Event("/path/resource/", "toto", "resourceType", "read", "");

        int i = 0;
        while (i < 10000) {
            eqs.createEventQueue("/eventqueue" + i);
            eqs.pushEvent("/eventqueue" + i, e);

            i++;
        }
        boolean b = true;
        for (int j = 0; j < 10000; j++) {
            Event[] tabEvent = eqs.getEvents("/eventqueue" + j);
            if (!tabEvent[0].equals(e) || tabEvent.length != 1) {
                b = false;
            }
        }
        assertTrue(b);
    }

    // ******************************** Cross-check
    // ******************************************
    /**
     * Test: test the cross check of findEvent
     * 
     */

    public void testcrossCheckOfFindEvent() throws EventQueueServiceException {
        logger.debug("test: Test: test the cross check of findEvent(...)");
        eqs.createEventQueue("/eventqueue1");
        List<Event> list = new ArrayList<Event>();
        String nameOwner = "titi";

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");
        Event myEvent3 = new Event("/path/resource/", "titi", "resourceType", "commit", "");

        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue1", myEvent2);
        eqs.pushEvent("/eventqueue1", myEvent3);
        Event[] tabEvent = eqs.getEvents("/eventqueue1");

        for (int i = 0; i < tabEvent.length; i++) {
            if (tabEvent[i].getThrowedBy().equals(nameOwner)) {
                list.add(tabEvent[i]);

            }
        }

        boolean b1 = list.get(0).equals(myEvent3);
        boolean b2 = list.get(1).equals(myEvent2);
        boolean b3 = list.size() == 2;
        assertTrue(b1 && b2 && b3);

    }

    // ******************************* Performance
    // ***************************************************************
    /**
     * Test a full disk issue. This test the push an event in many eventQueue
     * 
     * @throws EventQueueServiceException
     */
    @Test(expected = EventQueueServiceException.class)
    public void testFullDiskEventQueue() throws EventQueueServiceException {
        Event e = new Event("/path/resource/", "toto", "resourceType", "read", "");

        int i = 0;
        while (true) {
            eqs.createEventQueue("/eventqueue" + i);
            eqs.pushEvent("/eventqueue" + i, e);

            i++;
        }

    }

    /**
     * Test a full disk issue. This test use a specific pushEvent in an
     * EventQueue.
     * 
     */

    @Test(expected = EventQueueServiceException.class)
    public void testFullDiskEvent() throws EventQueueServiceException {

        eqs.createEventQueue("/eventqueue1");
        int i = 0;
        while (true) {

            Event e = new Event("/path/resource/", "toto" + i, "resourceType" + i, "read", "");
            eqs.pushEvent("/eventqueue1", e);

            i++;
        }

    }

}