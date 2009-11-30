package org.qualipso.factory.client.test.sb;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
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
    public void testExistingEventInEQ() throws EventQueueServiceException {
        logger.debug(" Test existence of Event in EventQueue(...)");

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");
        eqs.pushEvent(pathQ1, myEvent1);
        eqs.pushEvent(pathQ1, myEvent2);
        Event[] tabEvent = eqs.getEvents(pathQ1);

        assertEquals("error in existing of myEvent2 in testExistingEventInEQ() ",myEvent2,tabEvent[0]);
        assertEquals("error in existing of myEvent1 in testExistingEventInEQ()",myEvent1,tabEvent[1]);
        assertEquals("error Length of array in testExistingEventInEQ() ",2,tabEvent.length);

    }

    /**
     * push 1 Event in 2 EventQueue and verification
     * 
     * @throws EventQueueServiceException
     */
    @Test
    public void testPushEventInTooEQ() throws EventQueueServiceException {
        logger.debug("push 1 Event in 2 EventQueue and verification(...)");

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        eqs.pushEvent(pathQ1, myEvent1);
        eqs.pushEvent(pathQ2, myEvent1);
        Event e1 = eqs.getLastEvent(pathQ1);
        Event e2 = eqs.getLastEvent(pathQ2);

        assertEquals("error existing event in the first EQ :testPushEventInTooEQ()",myEvent1,e1);
        assertEquals("error existing event in the second EQ :testPushEventInTooEQ()",myEvent1,e2);

        
    }

    /**
     * push 1 Event in inexisting EventQueue and verification
     * 
     * @throws EventQueueServiceException
     */
    @Test(expected = EventQueueServiceException.class)
    public void testpushEventInInexistEQ() throws EventQueueServiceException {

        logger.debug("push 1 Event inexisting EventQueue and verification(...)");
        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        eqs.pushEvent("/eventqueueinexist", myEvent1);

    }

    
    
    
    
    
    /**
     * test existing of Event in the EventQueue having exactly a ressourceType
     * 
     * @throws EventQueueServiceException
     */
    
    public void testFindEventByResourcetypeEx() throws  EventQueueServiceException
    {
        
        logger.debug("test: testFindEventByResourcetypeEx()...");
        eqs.createEventQueue("/eventqueue1");
      
        
        Event myEvent1 = new Event("/path/resource/", "toto", "rtype1", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "rtype2", "read,write","");
        
        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue1", myEvent2);
        
        //test existing of Event in the EventQueue having exactly a ressourceType= rtype2
        
        Event[] resultFind =  eqs.findEventByRessourceType("/eventqueue1","rtype2",false);
       
        
        assertEquals("error length of array from testFindEventByResourcetypeEx() ",1,resultFind.length);
        assertEquals("error length of array from testFindEventByResourcetypeEx() ",myEvent2,resultFind[0]);
        
        
    } 
    /**
     * test existing of Event in the EventQueue contains a string parameter resourceType
     * 
     * @throws EventQueueServiceException
     */
    
    public void testFindEventByResourcetypeContains() throws  EventQueueServiceException
    {
        
        logger.debug("test: testFindEventByResourcetypeContains()...");
        eqs.createEventQueue("/eventqueue1");
      
        
        Event myEvent1 = new Event("/path/resource/", "toto", "rtype1Resource", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "Resourcetype2", "read,write","");
        
        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue1", myEvent2);
        
        //test existing of Event in the EventQueue  having ressourceType contains ("Resource")
        
        Event[] resultFind =  eqs.findEventByRessourceType("/eventqueue1","rtype2",true);
       
        
        assertEquals("error length of array in  testFindEventByResourcetypeContains() ",2,resultFind.length);
        assertEquals("error of finding Event2 in testFindEventByResourcetypeContains() ",myEvent2,resultFind[0]);
        assertEquals("error of finding Event1 in testFindEventByResourcetypeContains() ",myEvent1,resultFind[1]);
        
    } 
    /**
     * test existing of Event in the EventQueue contains/having a string parameter throwedBy
     * 
     * @throws EventQueueServiceException
     */
    public void testFindEventByThrower() throws  EventQueueServiceException
    {
        
        logger.debug("test: testFindEventByThrower() ...");
        eqs.createEventQueue("/eventqueue1");
      
        
        Event myEvent1 = new Event("/path/resource/", "totoQualipso", "rtype1Resource", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titiQualipso", "Resourcetype2", "read,write","");
        
        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue1", myEvent2);
        
        //test existing of Event in the EventQueue  having the attribute By thrower contains ("Qualipso")
        
        Event[] resultFind =  eqs.findEventBythrower("/eventqueue1","Qualipso",true);
       
        
        assertEquals("error length of array in testFindEventByThrower()  ",2,resultFind.length);
        assertEquals("error of finding Event2 in testFindEventByThrower() ",myEvent2,resultFind[0]);
        assertEquals("error of finding Event1 in testFindEventByThrower() ",myEvent1,resultFind[1]);
        
      //test existing of Event in the EventQueue  having the attribute By thrower ="titiQualipso"
        
        Event[] resultFind1 =  eqs.findEventBythrower("/eventqueue1","titiQualipso",false);
        
        assertEquals("error length of array1 in testFindEventByThrower()  ",1,resultFind1.length);
        assertEquals("error of finding(array1) Event2 in testFindEventByThrower() ",myEvent2,resultFind1[0]);
           
    } 
    /**
     * test existing of Event in the EventQueue contains/having an string parameter path
     * 
     * @throws EventQueueServiceException
     */
    public void testFindEventByPath() throws  EventQueueServiceException
    {
        
        logger.debug("test: testFindEventByPath() ...");
        eqs.createEventQueue("/eventqueue1");
      
        
        Event myEvent1 = new Event("/path/res/eq", "totoQualipso", "rtype1Resource", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titiQualipso", "Resourcetype2", "read,write","");
        
        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue1", myEvent2);
        
        //test existing of Event in the EventQueue  having the attribute path contains ("path")
        
        Event[] resultFind =  eqs.findEventByFromRessource("/eventqueue1","path",true);
       
        
        assertEquals("error length of array in testFindEventByPath() ",2,resultFind.length);
        assertEquals("error of finding Event2 in testFindEventByPath() ",myEvent2,resultFind[0]);
        assertEquals("error of finding Event1 in testFindEventByPath() ",myEvent1,resultFind[1]);
        
      //test existing of Event in the EventQueue  having the attribute path ="/path/res/eq"
        
        Event[] resultFind1 =  eqs.findEventByFromRessource("/eventqueue1","/path/res/eq",false);
        
        assertEquals("error length of array1 in testFindEventByThrower()  ",1,resultFind1.length);
        assertEquals("error of finding(array1) Event2 in testFindEventByThrower() ",myEvent1,resultFind1[0]);
           
    } 
    
    /**
     * 
     * find event in the EventQueue by Date 
     * @throws EventQueueServiceException
     */
    public void testFindEventByDate() throws  EventQueueServiceException
    {
        
        logger.debug("test: testFindEventByDate() ...");
        eqs.createEventQueue("/eventqueue1");
      
        
        Event myEvent1 = new Event("/path/resource/", "totoQualipso", "rtype1Resource", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titiQualipso", "Resourcetype2", "read,write","");
        
        // Change the date of myEvent1 and myEvent2
        
        Date date1=new Date(2009,11,14,13,01,30);
        myEvent1.setDate(date1 );
        
        Date date2=new Date(2009,11,14,15,01,30);
        myEvent1.setDate(date2);
        
        //push myEvent1 and MyEvent2 in the EventQueue
        
        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue1", myEvent2);
        
        // finding by date1 
        
        Event[] resultFind =  eqs.findEventByDate("/eventqueue1",date1);
       
        // verify the length of array and the existing of myEent1 in the EventQueue
        
        assertEquals("error length of array in testFindByDate() ",1,resultFind.length);
        assertEquals("error of finding myEvent1 in testFindEventByDate() ",myEvent1,resultFind[0]);
        
    } 
    
    /**
     * 
     * find event in the EventQueue having Date >= date (parameter)
     * @throws EventQueueServiceException
     */
    
    public void testFindEventByDateSup() throws  EventQueueServiceException
    {
        
        logger.debug("test: testFindEventByDateSup() ...");
        eqs.createEventQueue("/eventqueue1");
      
        
        Event myEvent1 = new Event("/path/resource/", "totoQualipso", "rtype1Resource", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titiQualipso", "Resourcetype2", "read,write","");
        
        // Change the date of myEvent1 and myEvent2
        
        Date date1=new Date(2009,11,14,13,01,30);
        myEvent1.setDate(date1 );
        
        Date date2=new Date(2009,11,14,15,30,30);
        myEvent1.setDate(date2);
        
        //push myEvent1 and MyEvent2 in the EventQueue
        
        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue1", myEvent2);
        
        // finding by date>= date1 
        
        Event[] resultFind =  eqs.findEventByDateSup("/eventqueue1",date1);
       
        // verify the length of array and the existing of myEent2 in the EventQueue
        
        assertEquals("error length of array in testFindByDateSup() ",1,resultFind.length);
        assertEquals("error of finding myEvent2 in testFindEventByDateSup() ",myEvent2,resultFind[0]);
        
    } 
    
    /**
     * find event in the EventQueue having Date <= date (parameter)
     * 
     * @throws EventQueueServiceException
     */
    
    public void testFindEventByDateInf() throws  EventQueueServiceException
    {
        
        logger.debug("test: testFindEventByDateInf() ...");
        eqs.createEventQueue("/eventqueue1");
      
        
        Event myEvent1 = new Event("/path/resource/", "totoQualipso", "rtype1Resource", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titiQualipso", "Resourcetype2", "read,write","");
        
        // Change the date of myEvent1 and myEvent2
        
        Date date1=new Date(2009,11,14,13,01,30);
        myEvent1.setDate(date1 );
        
        Date date2=new Date(2009,11,14,15,30,30);
        myEvent1.setDate(date2);
        
        //push myEvent1 and MyEvent2 in the EventQueue
        
        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue1", myEvent2);
        
        // finding by date<= date2
        
        Event[] resultFind =  eqs.findEventByDateInf("/eventqueue1",date2);
       
        // verify the length of array and the existing of myEent1 in the EventQueue
        
        assertEquals("error length of array in testFindByDateInf() ",1,resultFind.length);
        assertEquals("error of finding myEvent1 in testFindEventByDateInf() ",myEvent1,resultFind[0]);
        
    } 
    
    /**
     * 
     * find event in the EventQueue having Date Between date1 and date2 (parameters)
     * @throws EventQueueServiceException
     */
    
    
    public void testFindEventByDateBetween() throws  EventQueueServiceException
    {
        
        logger.debug("test: testFindEventByDateBetween() ...");
        eqs.createEventQueue("/eventqueue1");
      
        
        Event myEvent1 = new Event("/path/resource/", "totoQualipso", "rtype1Resource", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titiQualipso", "Resourcetype2", "read,write","");
        
        // Change the date of myEvent1 and myEvent2
        
        Date date1=new Date(2009,11,14,13,01,30);
        myEvent1.setDate(date1 );
        
        Date date2=new Date(2009,11,14,15,30,30);
        myEvent1.setDate(date2);
        
        // creation of date3
        
        Date date3=new Date(2009,11,11,15,30,30);
        
        //push myEvent1 and MyEvent2 in the EventQueue
        
        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue1", myEvent2);
        
        // finding by date between date3 and and date2
        
        Event[] resultFind =  eqs.findEventByDateBetween("/eventqueue1",date3,date2);
       
        // verify the length of array and the existing of myEent1 and myEvent2 in the EventQueue
        
        assertEquals("error length of array in testFindByDateBetween() ",2,resultFind.length);
        assertEquals("error of finding myEvent2 in testFindEventByDateBetween() ",myEvent2,resultFind[0]);
        assertEquals("error of finding myEvent2 in testFindEventByDateBetween() ",myEvent1,resultFind[1]);
        
    } 
    
   
    
   
    
    /**
     * 
     * test: search event(by eventType ) existing in the eventQueue
     * 
     * @throws EventQueueServiceException
     */
    
    
    public void testFindEventByEventType() throws  EventQueueServiceException
    {
        
        logger.debug("test: search event(eventType) existing in eventQueue(...)");
        eqs.createEventQueue("/eventqueue1");
        
        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write","");
        Event myEvent3 = new Event("/path/resource/", "titi", "resourceType", "commit","");
        
        eqs.pushEvent("/eventqueue1", myEvent1);
        eqs.pushEvent("/eventqueue1", myEvent2);
        eqs.pushEvent("/eventqueue1", myEvent3);
        
        //test existing of Event in the EventQueue  having the EventType contains ("r")
        Event[] resultFind =  eqs.findEventByEventType("/eventqueue1","r",true);
        
        assertEquals("error length of array in testFindEventByType() ",2,resultFind.length);
        assertEquals("error of finding myEvent2 in testFindEventByType() ",myEvent2,resultFind[0]);
        assertEquals("error of finding myEvent1 in testFindEventByType() ",myEvent1,resultFind[1]);
        
      //test existing of Event in the EventQueue  having the EventType equals to ("read")
        Event[] resultFind1 =  eqs.findEventByEventType("/eventqueue1","read",false);
        
        assertEquals("error length of array in testFindEventByType() ",2,resultFind1.length);
        assertEquals("error of finding myEvent2 in testFindEventByType() ",myEvent2,resultFind1[0]);
        assertEquals("error of finding myEvent1 in testFindEventByType() ",myEvent1,resultFind1[1]);
        
        
    } 

    
    
    
    
    
   
    /**
     * Test:delete an Event from EventQueue
     * 
     * @throws EventQueueServiceException
     */
    @Test
    public void testDeleteeventFromEQ() throws EventQueueServiceException {

        logger.debug("test: delete an Event from EventQueue(...)");
        // eqs.createEventQueue("/eventqueue1");

        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");

        eqs.pushEvent(pathQ1, myEvent1);
        eqs.pushEvent(pathQ1, myEvent2);
        eqs.deleteEvent(pathQ1, myEvent1);
        eqs.deleteEvent(pathQ1, myEvent2);
        Event[] tabEvent = eqs.getEvents(pathQ1);
        assertEquals("error length of array intestDeleteeventFromEQ()",tabEvent.length, 0);

    }

    /**
     * Test:remove an EventQueue
     * 
     * @throws EventQueueServiceException
     */
    @Test(expected = EventQueueServiceException.class)
    public void testRemoveEventQueue() throws EventQueueServiceException {

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
    public void testLastEventInEQ() throws EventQueueServiceException {

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
    
    //@Test(expected = EventQueueServiceException.class)
   /* @Test
    public void testBoundaryEvent() throws EventQueueServiceException {
        logger.info("testBoundaryEvent()");
        int i = 0;
        int number = 750;
        while (i < number) {

            Event e = new Event("/path/resource/", "toto" + i, "resourceType" + i, "read", "");
            eqs.pushEvent(pathQ1, e);

            i++;
        }
        Event[] tabEvent = eqs.getEvents(pathQ1);
        logger.info("taille queue :"+tabEvent.length);
        assertEquals(tabEvent.length, number);

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
*/
}
