package org.qualipso.factory.client.test.performance;



import javax.naming.Context;
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
import org.qualipso.factory.Factory;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.bootstrap.BootstrapService;
import org.qualipso.factory.bootstrap.BootstrapServiceException;
import org.qualipso.factory.client.test.AllTests;
import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.eventqueue.EventQueueServiceException;
import org.qualipso.factory.eventqueue.entity.PersistentEvent;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.NotificationServiceException;



/**
 * @author Huriye Yuksel
 * @date 27  novembre 2009
 */




public class EventQueueServicePTest {
    
    
    private static Context context;

    private static Log logger = LogFactory.getLog(EventQueueServicePTest.class);

    private static EventQueueService eqs;
    private static LoginContext rootLoginContext;

    private final static String pathQ1 = "/eventqueue1"; 
    private final static String pathQ2 = "/eventqueue2";
    private final static String pathQ3 = "/eventqueue3";

    /**
     * Set up service for all tests.
     * 
     * @throws LoginException
     * @throws NotificationServiceException
     * @throws MembershipServiceException
     */
    @BeforeClass
    public static void before() throws NamingException, LoginException, FactoryException {
        System.setProperty("java.security.auth.login.config", ClassLoader.getSystemResource("jaas.config").getPath());
        
        BootstrapService bootstrap = (BootstrapService) Factory.findService(BootstrapService.SERVICE_NAME);
        try {
            bootstrap.bootstrap();
        } catch (BootstrapServiceException e) {
            logger.error(e);
        }

        UsernamePasswordHandler uph = new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS);
        rootLoginContext = new LoginContext("qualipso", uph);
        eqs = (EventQueueService) Factory.findService(EventQueueService.SERVICE_NAME);
    }

    @AfterClass
    public static void after() throws LoginException, NamingException, MembershipServiceException {
    }

    @Before
    public void setUp() throws NamingException, FactoryException, LoginException {
    	rootLoginContext.login();
        eqs.createEventQueue(pathQ1);
        eqs.createEventQueue(pathQ2);
        eqs.createEventQueue(pathQ3);
        rootLoginContext.logout();
    }

    @After
    public void tearDown() throws EventQueueServiceException, LoginException {
    	rootLoginContext.login();
        eqs.removeQueue(pathQ1);
        eqs.removeQueue(pathQ2);
        eqs.removeQueue(pathQ3);
        rootLoginContext.logout();
    }

   
    @Test
    public void testCreateTenQueuesAsRoot() throws EventQueueServiceException, LoginException {
    	rootLoginContext.login();
    	long start = System.nanoTime();
        for (int i=0; i<10; i++) {
            eqs.createEventQueue("/queue" + i);
        }
        long stop = System.nanoTime();
        logger.debug("Time to create 10 queues : " + (stop - start) + " ns");
        rootLoginContext.logout();
    }
    
    @Test
    public void testPushTenEventsInAQueueAsRoot() throws EventQueueServiceException, LoginException {
    	rootLoginContext.login();
    	PersistentEvent[] events = new PersistentEvent[10];
    	for (int i=0; i<events.length; i++) {
    		events[i] = new PersistentEvent("/dummyresource", "DummyType", "dummyservice.dummyresource.create", "noargs");
        }
    	long start = System.nanoTime();
    	for (int i=0; i<events.length; i++) {
    		eqs.pushEvent(pathQ1, events[i]);
        }
        long stop = System.nanoTime();
        logger.debug("Time to push 10 event in a queue : " + (stop - start) + " ns");
        rootLoginContext.logout();
    }
    
    @Test
    public void testPushFiftyEventsInAQueueAsRoot() throws EventQueueServiceException, LoginException {
    	rootLoginContext.login();
    	PersistentEvent[] events = new PersistentEvent[50];
    	for (int i=0; i<events.length; i++) {
    		events[i] = new PersistentEvent("/dummyresource", "DummyType", "dummyservice.dummyresource.create", "noargs");
        }
    	long start = System.nanoTime();
    	for (int i=0; i<events.length; i++) {
    		eqs.pushEvent(pathQ2, events[i]);
        }
        long stop = System.nanoTime();
        logger.debug("Time to push 50 event in a queue : " + (stop - start) + " ns");
        rootLoginContext.logout();
    }
    
    @Test
    public void testPushHundredEventsInAQueueAsRoot() throws EventQueueServiceException, LoginException {
    	rootLoginContext.login();
    	PersistentEvent[] events = new PersistentEvent[100];
    	for (int i=0; i<events.length; i++) {
    		events[i] = new PersistentEvent("/dummyresource", "DummyType", "dummyservice.dummyresource.create", "noargs");
        }
    	long start = System.nanoTime();
    	for (int i=0; i<events.length; i++) {
    		eqs.pushEvent(pathQ3, events[i]);
        }
        long stop = System.nanoTime();
        logger.debug("Time to push 100 event in a queue : " + (stop - start) + " ns");
        rootLoginContext.logout();
    }
    
}
