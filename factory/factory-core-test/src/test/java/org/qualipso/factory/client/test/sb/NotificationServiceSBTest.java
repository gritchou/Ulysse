package org.qualipso.factory.client.test.sb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.jboss.security.auth.callback.UsernamePasswordHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.client.test.AllTests;
import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.eventqueue.EventQueueServiceException;
import org.qualipso.factory.greeting.GreetingService;
import org.qualipso.factory.greeting.GreetingServiceException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.notification.NotificationServiceException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 7 october 2009
 */
public class NotificationServiceSBTest {
    private static Context context;
    private static NotificationService notification;
    private static GreetingService greeting;
    private static EventQueueService eqs;
    private static MembershipService membership;
    private String pathQueue1;
    private String pathQueue2;

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

        UsernamePasswordHandler uph = new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS);
        LoginContext loginContext = new LoginContext("qualipso", uph);
        loginContext.login();

        notification = (NotificationService) context.lookup(FactoryNamingConvention.JNDI_SERVICE_PREFIX + "NotificationService");
        eqs = (EventQueueService) context.lookup(FactoryNamingConvention.JNDI_SERVICE_PREFIX + "EventQueueService");
        greeting = (GreetingService) context.lookup("GreetingService");
        membership = (MembershipService) context.lookup(FactoryNamingConvention.JNDI_SERVICE_PREFIX + "MembershipService");
    }

    @Before
    public void setUp() throws Exception {
        pathQueue1 = "/eventQueue/q1";
        pathQueue2 = "/eventQueue/q2";

        eqs.createEventQueue(pathQueue1);
        eqs.createEventQueue(pathQueue2);

        notification.register("/profiles/*", "/name/*", "greeting.name.create", pathQueue1);
        notification.register("/profiles/*", "/name/*", "greeting.name.read", pathQueue1);
    }

    @After
    public void tearDown() throws EventQueueServiceException {
        eqs.removeQueue(pathQueue1);
        eqs.removeQueue(pathQueue2);
    }

    /**
     * throw one event into one queue and check the event is inserted only in one queue
     * 
     * @throws Exception
     */

    @Test(timeout = 100)
    public void testNotification1() throws Exception {
        
        String caller = membership.getProfilePathForConnectedIdentifier();
        // greeting.sayHello()

        greeting.createName("/name", "toto");
        Event[] lEvent1 = new Event[] {};

        while (lEvent1.length == 0) {
            lEvent1 = eqs.getEvents(pathQueue1);
        }

        assertTrue(lEvent1.length == 1);
        Event e = lEvent1[0];
        assertEquals(e.getFromResource(), "/name/toto");
        assertEquals(e.getEventType(), "greeting.name.create");
        assertEquals(e.getThrowedBy(), caller);

        Event[] lEvent2 = eqs.getEvents(pathQueue2);
        assertTrue(lEvent2.length == 0);
        
        //assert that no other events happen
        lEvent1 = eqs.getEvents(pathQueue1);
        assertTrue(lEvent1.length==1);
    }

    /**
     * throw 10 events into one queue and check the order of these 10 events
     * 
     * @throws Exception
     */
    @Test(timeout = 100)
    public void testNotification2() throws Exception {
        for (int i = 0; i < 10; i++) {
            greeting.readName("/name/toto");
        }

        Event[] lEvent2 = new Event[] {};
        while (lEvent2.length < 9) {
            lEvent2 = eqs.getEvents(pathQueue2);
        }

        assertTrue(lEvent2.length == 9);
        Date d = lEvent2[0].getDate();
        int i = 1;
        boolean dateOrderOk = true;
        while (i < lEvent2.length && dateOrderOk) {
            dateOrderOk = d.before(lEvent2[i].getDate());
            i++;
        }

        assertTrue(dateOrderOk);

        Event[] lEvent1 = eqs.getEvents(pathQueue1);
        assertTrue(lEvent1.length == 0);

    }

    /**
     * throw one event matching by 1/2 queue
     */
    @Test(timeout = 100)
    public void testNotification3() {
        // je pense qu'il y a pas besoin, il est fait avec le 1
    }

    /**
     * throw one event matching by 0 queue
     * 
     * @throws InterruptedException
     * @throws GreetingServiceException
     * @throws EventQueueServiceException
     * @throws NotificationServiceException 
     */
    @Test(timeout = 100)
    public void testNotification4() throws InterruptedException, GreetingServiceException, EventQueueServiceException, NotificationServiceException {
        greeting.readName("/name/toto");
        Thread.sleep(60);
        assertTrue(eqs.getEvents(pathQueue1).length == 0);
        assertTrue(eqs.getEvents(pathQueue2).length == 0);

    }

    /**
     * throw 2 event matching by one queue, but user can read only one event
     * 
     * @throws MembershipServiceException
     * @throws GreetingServiceException 
     * @throws EventQueueServiceException 
     * @throws InterruptedException 
     */
    @Test(timeout = 100)
    public void testNotification5() throws MembershipServiceException, GreetingServiceException, EventQueueServiceException, InterruptedException {
        membership.createProfile("marlene", "marlene", "toto@gmail.com", 0);
        String caller = membership.getProfilePathForConnectedIdentifier();
        greeting.createNameWithUser("/name","toto", "marlene");
        greeting.readName("/name/toto");
        
        Event[] lEvent2 = new Event[] {};
        while (lEvent2.length < 1) {
            lEvent2 = eqs.getEvents(pathQueue2);
        }
        
        assertTrue(lEvent2.length == 1);
        Event e = lEvent2[0];
        assertEquals(e.getFromResource(), "/name/toto");
        assertEquals(e.getEventType(), "greeting.name.create");
        assertEquals(e.getThrowedBy(), caller);
        
        Thread.sleep(60);
        
        assertTrue(eqs.getEvents(pathQueue1).length==0);
    }
    
    /**
     * throw a null event
     * @throws NotificationServiceException 
     */
   @Test(expected=NotificationServiceException.class)
    public void testNotification6() throws NotificationServiceException{
        greeting.throwNullEvent();
    }
   
   
   /**
    * throws 2 times the same event
    * @throws MembershipServiceException 
    * @throws NotificationServiceException 
    * @throws EventQueueServiceException 
    */
   @Test(timeout = 100)
   public void testNotification7() throws NotificationServiceException, MembershipServiceException, EventQueueServiceException{
       greeting.throw2SameEvent("/name/toto");
       
       Event[] lEvent = new Event[] {};
       while (lEvent.length < 2) {
           lEvent = eqs.getEvents(pathQueue1);
       }       
       
       assertEquals(lEvent[0],lEvent[1]);
   }
   
   /**
    * throws a factice event
    * @throws NotificationServiceException 
    */
   @Test(expected=NotificationServiceException.class)
   public void testNotification8() throws NotificationServiceException{
       greeting.throwFacticeEvent();
   }
   
   /**
    * throws 10000 events
    * @throws GreetingServiceException 
 * @throws EventQueueServiceException 
    */
   @Test(timeout=1000)
   public void testNotification9() throws GreetingServiceException, EventQueueServiceException{
       for(int i=0;i<10000;i++){
           greeting.readName("/name/toto");
       }
       
       Event[] lEvent = new Event[] {};
       while (lEvent.length < 10000) {
           lEvent = eqs.getEvents(pathQueue2);
       }     
       
       assertTrue(lEvent.length==10000);
   }
   
   /**
    * throws 1 event into 10000 queues
    * @throws EventQueueServiceException 
    * @throws NotificationServiceException 
    */
   @Test(timeout = 1000)
   public void testNotification10() throws EventQueueServiceException, NotificationServiceException{
       String path = "/eventQueue/q";
       for(int i=0;i<10000;i++){
           eqs.createEventQueue(path+i);
           notification.register("/profiles/*", "/name/*", "greeting.name.read", path+i);
       }
       
       boolean okAll = true;
       Event[] lEvent = new Event[] {};
       for(int i=0;i<10000;i++){
           while(lEvent.length<1){
               lEvent = eqs.getEvents(path+i);
           }
           okAll = okAll && (lEvent.length==1);
       }
       
       assertTrue(okAll);
       
   }

}