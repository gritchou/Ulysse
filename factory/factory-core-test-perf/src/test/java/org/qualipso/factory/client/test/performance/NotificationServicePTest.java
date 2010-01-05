package org.qualipso.factory.client.test.performance;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.auth.callback.UsernamePasswordHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.qualipso.factory.Factory;
import org.qualipso.factory.bootstrap.BootstrapService;
import org.qualipso.factory.bootstrap.BootstrapServiceException;
import org.qualipso.factory.client.test.AllTests;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.NotificationServiceException;

public class NotificationServicePTest {

    private static Log logger = LogFactory.getLog(NotificationServicePTest.class);
    
    private LoginContext rootLoginContext;
    
    /**
     * Set up service for all tests.
     * 
     * @throws LoginException
     * @throws NotificationServiceException
     * @throws MembershipServiceException
     */
    @BeforeClass
    public static void before() throws Exception {
        System.setProperty("java.security.auth.login.config", ClassLoader.getSystemResource("jaas.config").getPath());
        
        BootstrapService bootstrap = (BootstrapService) Factory.findService(BootstrapService.SERVICE_NAME);
        try {
            bootstrap.bootstrap();
        } catch (BootstrapServiceException e) {
            logger.error(e);
        }

        UsernamePasswordHandler uph = new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS);
        LoginContext rootLoginContext = new LoginContext("qualipso", uph);
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test Boundary - Performance throws 10000 events in 1 queue
     * 
     * @throws GreetingServiceException
     * @throws EventQueueServiceException
     */
    /*@Test(timeout = 999999999)
    public void testNotification10000Events() throws GreetingServiceException, EventQueueServiceException {
        logger.info("testNotification10000Events called");
        for (int i = 0; i < 1; i++) {
            greeting.readName(pathResource);
        }

        logger.info("testNotification10000Events : 10000 events pushed, waiting...");

        PersistentEvent[] lEvent = new PersistentEvent[] {};
        while (lEvent.length < 1) {
            lEvent = eqs.getEvents(pathQueue);
            logger.info(lEvent.length);
        }

        assertTrue("TestNotification10000Events : expected 10000 events but found " + lEvent.length, lEvent.length == 1);
    }*/

    /**
     * Test Boundary - Performance throws 1 event into 10000 queues
     * 
     * @throws EventQueueServiceException
     * @throws NotificationServiceException
     * @throws GreetingServiceException
     */
    /*@Test(timeout = 2000000)
    public void testNotification10000Queues() throws EventQueueServiceException, NotificationServiceException, GreetingServiceException {
        logger.info("testNotification10000Queues called");
        String path = "/q";
        for (int i = 0; i < 1; i++) {
            eqs.createEventQueue(path + i);
            eqs.register("/profiles/.*", "greeting.name.read", "/m2logTestPerf.*", path + i);
        }

        greeting.readName(pathResource);

        logger.info("testNotification10000Queues : 10000 queue created, pushing 1 event matching by all queue...");
        PersistentEvent[] lEvent = new PersistentEvent[] {};
        for (int i = 0; i < 1; i++) {
            while (lEvent.length < 1) {
                lEvent = eqs.getEvents(path + i);
            }
            assertTrue("TestNotification10000Queues : expected 1 event but found " + lEvent.length, lEvent.length == 1);
        }

        logger.info("testNotification10000Queues : deleting all queue...");
        // delete
        for (int i = 0; i < 1; i++) {
            eqs.removeQueue(path + i);
            eqs.unregister("/profiles/.*", "greeting.name.read", "/m2logTestPerf.*", path + i);
        }
    }*/
}
