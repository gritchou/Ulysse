package org.qualipso.factory.client.test.performance;

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
import org.junit.Before;
import org.junit.BeforeClass;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.bootstrap.BootstrapService;
import org.qualipso.factory.bootstrap.BootstrapServiceException;
import org.qualipso.factory.client.test.AllTests;
import org.qualipso.factory.client.test.sb.NotificationServiceSBTest;
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

public class NotificationServicePTest {
	
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
