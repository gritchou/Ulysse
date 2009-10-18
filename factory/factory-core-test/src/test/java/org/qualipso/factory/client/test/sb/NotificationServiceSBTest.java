package org.qualipso.factory.client.test.sb;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.bootstrap.BootstrapService;
import org.qualipso.factory.bootstrap.BootstrapServiceException;
import org.qualipso.factory.browser.BrowserService;
import org.qualipso.factory.client.test.AllTests;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.greeting.GreetingService;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.notification.NotificationServiceException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 7 october 2009
 */
public class NotificationServiceSBTest {
	private static Context context;
	private NotificationService notification;
	private GreetingService greeting;
	private EventQueueService eqs;
	private String pathQueue1;
	private String pathQueue2;
	
	/**
	 * Set up service for all tests.
	 * @throws LoginException 
	 * @throws NotificationServiceException 
	 * @throws MembershipServiceException 
	 */
	@BeforeClass
	public static void before() throws NamingException, LoginException, NotificationServiceException, MembershipServiceException {
		Properties properties = new Properties();
		properties.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
		properties.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
		properties.put("java.naming.provider.url","localhost:1099");
		System.setProperty("java.security.auth.login.config", ClassLoader.getSystemResource("jaas.config").getPath());
		context = new InitialContext(properties);
		
		UsernamePasswordHandler uph = new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS); 
		LoginContext loginContext = new LoginContext("qualipso", uph);
		loginContext.login();
		//membership =  (MembershipService) context.lookup(FactoryNamingConvention.JNDI_SERVICE_PREFIX+"MembershipService");
		//binding = (BindingService) context.lookup(FactoryNamingConvention.JNDI_SERVICE_PREFIX+"BindingService");
		//membership.createProfile("toto", "toto", "toto@gmail.com", 0);
	}
	
	@Before
	public void setUp() throws NamingException, NotificationServiceException{
		notification = (NotificationService) context.lookup(FactoryNamingConvention.JNDI_SERVICE_PREFIX + "NotificationService");
		eqs = (EventQueueService) context.lookup(FactoryNamingConvention.JNDI_SERVICE_PREFIX+"EventQueueService");
		
		/**
		 * queues sont créer par l'utilisateur donc par le test (injection dependance)
		 * eqs.createQueue(.....)
		 */
		pathQueue1 = "/eventQueue/q1";
		pathQueue2 = "/eventQueue/q2";
		
		
		notification.register("sub.*", "obj.*", "com.*", pathQueue1);
		notification.register("subject", "obj.*", "comit", pathQueue1);
		greeting = (GreetingService) context.lookup("GreetingService");
		
	}
	
	
	/*
	 * One event inserted in one queue 
	 */
	@Test
	public void testNotification1() throws Exception{
		
		//greeting.sayHello()
		
		String e1 = greeting.throwEventOK();
		
		//pause pour l'asynchronisme
		
		Event[] lEvent1 = eqs.getEvents(pathQueue1);
		assertTrue(lEvent1.length==1);
		assertEquals(lEvent1[0].getFromResource(),e1);
		
		Event[] lEvent2 = eqs.getEvents(pathQueue2);
		assertTrue(lEvent2.length==0);
	}

	@Test
	public void testNotification2() throws Exception{
		String e1 = greeting.throwEventKO();
		Event[] lEvent1 = eqs.getEvents(pathQueue1);
		assertTrue(lEvent1.length==0);


		Event[] lEvent2 = eqs.getEvents(pathQueue2);
		assertTrue(lEvent2.length==0);	
	}

//	@Test
//    //simple test
//    public void testNotification3() throws MembershipServiceException, NotificationServiceException, PAPServiceException, EventQueueServiceException{
//        
//        
//        final String subject = membership.getProfilePathForConnectedIdentifier(); 
//         
//        final String target = "mon ensemble de ressources";
//        final String action = "commit";
//        final String pathQueue = "q1";
//        final Event myEvent = new Event("/path/resource/", subject, "resourceType", "read", "");
//        final List<Event> empty = new Vector<Event>();
//        final Sequence sequence1 = mockery.sequence("sequence1");
//        
//        mockery.checking(new Expectations() {
//            {
//                oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/me")); inSequence(sequence1);
//                oneOf(notification).register(subject, target, action, pathQueue);inSequence(sequence1);
//                
//                oneOf(notification).throwEvent(myEvent);inSequence(sequence1);
//                oneOf(eQs).getEvents(pathQueue);will(returnValue(empty));inSequence(sequence1);
//            }
//        });
//       
//        
//        notification.register(subject, target, action, pathQueue);
//        notification.throwEvent(myEvent);
//        List<Event> l = eQs.getEvents(pathQueue);
//        
//        assertEquals(l.size(),0);
//     
//        
//        mockery.assertIsSatisfied();
//    }
//	
//	@Test
//    //simple test
//    public void testNotification4() throws MembershipServiceException, NotificationServiceException, PAPServiceException, EventQueueServiceException{
//        
//        
//        final String subject = membership.getProfilePathForConnectedIdentifier();
//        final String subject2 = "/members/toto";
//         
//        final String target = "mon ensemble de ressources";
//        final String action = "commit";
//        final String action2 = "read,commit";
//        final String pathQueue = "q1";
//        final String pathQueue2 = "q2";
//
//        final Event myEvent = new Event("/path/resource/", subject, "resourceType", "commit", "");
//        final List<Event> empty = new Vector<Event>();
//        final Sequence sequence1 = mockery.sequence("sequence1");
//        
//        mockery.checking(new Expectations() {
//            {
//                oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/me")); inSequence(sequence1);
//                oneOf(notification).register(subject, target, action, pathQueue);inSequence(sequence1);
//                
//                oneOf(notification).throwEvent(myEvent);inSequence(sequence1);
//                oneOf(eQs).getEvents(pathQueue);will(returnValue(empty));inSequence(sequence1);
//                oneOf(eQs).getEvents(pathQueue);will(returnValue(empty));inSequence(sequence1);
//            }
//        });
//       
//        
//        notification.register(subject, target, action, pathQueue);
//        notification.register(subject2, target, action2, pathQueue2);
//        notification.throwEvent(myEvent);
//        List<Event> l = eQs.getEvents(pathQueue);
//        List<Event> l2 = eQs.getEvents(pathQueue2);
//        assertTrue(l.contains(myEvent));
//        assertTrue(l2.contains(myEvent));
//        
//        
//        mockery.assertIsSatisfied();
//    }
}
