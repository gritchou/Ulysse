package org.qualipso.factory.client.test.sb;


import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import static org.junit.Assert.*;
import org.jboss.security.auth.callback.UsernamePasswordHandler;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.After;
import org.junit.AfterClass;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.BindingServiceException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceException;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.security.pep.PEPServiceException;
import org.qualipso.factory.eventqueue.*;
import org.qualipso.factory.eventqueue.entity.*;
/*import org.qualipso.funkyfactory.service.eventqueue.EventQueueService;
import org.qualipso.funkyfactory.service.eventqueue.EventQueueServiceException;
import org.qualipso.funkyfactory.service.eventqueue.entity.Event;
import org.qualipso.funkyfactory.service.eventqueue.entity.EventQueue;*/



public class EventQueueServiceSBTest {
    private static Context context;
    
    private static Log logger = LogFactory.getLog(EventQueueServiceSBTest.class);
   // private BindingService binding;
    private MembershipService membership;
    private PEPService pep;
    private PAPService pap;
    private EventQueueService eqs;
    private static LoginContext loginContext;
    private PAPServiceHelper paph;
    
    
    /**
     * Set up service for all tests.
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
        
        UsernamePasswordHandler uph = new UsernamePasswordHandler("kermit", "thefrog"); 
        loginContext = new LoginContext("tests", uph);
        loginContext.login();
       
         }
    
    
    @AfterClass
    public static void after() throws LoginException, NamingException {
        loginContext.logout();
        context.close();
    }
   
    
  
    @Before
    public void setUp() throws NamingException, FactoryException {
        
        eqs = (EventQueueService) context.lookup("EventQueueService");
       // binding = (BindingService) context.lookup("BindingService");
        paph = (PAPServiceHelper) context.lookup("PAPServiceHelper");
        membership = (MembershipService) context.lookup("MembershipService");
        membership.createProfile("toto", "toto titi", "toto@gmail.com", 0);
        membership.createProfile("resource", "resource", "resource@gmail.com", 0);
        
    }
    
    
    @Test
    
    // test the creation of the EventQueue
    /**
     * Test existence of Event in EventQueue
     * 
     * **/
     
    public void test1() throws  EventQueueServiceException
    {
        String path = "/eventqueues";
        logger.info("TestcreateEventQueue(...) called");
        logger.debug("params : path=" + path);
        eqs.createEventQueue(path);
        Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
        Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");
        eqs.pushEvent("/eventQueue/ev1", myEvent1);
        eqs.pushEvent("/eventQueue/ev1", myEvent2);
        Event[] tabEvent =  eqs.getEvents("/eventQueue/ev1");
        assertEquals(tabEvent.length,2);
        assertTrue(tabEvent[0].equals(myEvent2) && tabEvent[1].equals(myEvent1));
        
    }
    
    
    
    // ici test de push et de get
    public void testgetEvents(String path) throws MembershipServiceException, PEPServiceException, BindingServiceException, PAPServiceException, NamingException, EventQueueServiceException
    {   
        logger.info("TestgetEvents(...) called");
        logger.debug("params : path=" + path);
        
         eqs.createEventQueue("/eventQueue/ev1");
         Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
         Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");
         eqs.pushEvent("/eventQueue/ev1", myEvent1);
         eqs.pushEvent("/eventQueue/ev1", myEvent2);
         Event[] tabEvent =  eqs.getEvents("/eventQueue/ev1");
         assertEquals(tabEvent.length,2);
         
    }
    
    
    public void pushEvent(String path, Event e)throws MembershipServiceException, PEPServiceException, 
    BindingServiceException, PAPServiceException, NamingException, EventQueueServiceException{
        logger.info("TestgetEvents(...) called");
        logger.debug("params : path=" + path);
        
         eqs.createEventQueue("/eventQueue/ev1");
         Event myEvent1 = new Event("/path/resource/", "toto", "resourceType", "read", "");
         Event myEvent2 = new Event("/path/resource/", "titi", "resourceType", "read,write", "");
         eqs.pushEvent("/eventQueue/ev1", myEvent1);
         eqs.pushEvent("/eventQueue/ev1", myEvent2);
         Event[] tabEvent =  eqs.getEvents("/eventQueue/ev1");
         assertEquals(tabEvent.length,2);
     
    }    
        
        
        
        
    
    
    
    
    
    
        
    
    
    
    
    
    
}
