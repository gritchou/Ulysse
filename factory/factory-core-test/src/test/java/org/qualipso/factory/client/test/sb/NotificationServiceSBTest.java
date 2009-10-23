package org.qualipso.factory.client.test.sb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
	private MembershipService membership;
	private String pathQueue1;
	private String pathQueue2;
	
	/**
	 * Set up service for all tests.
	 * @throws LoginException 
	 * @throws NotificationServiceException 
	 * @throws MembershipServiceException 
	 */
	@BeforeClass
	public static void before() throws Exception {
		Properties properties = new Properties();
		properties.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
		properties.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
		properties.put("java.naming.provider.url","localhost:1099");
		System.setProperty("java.security.auth.login.config", ClassLoader.getSystemResource("jaas.config").getPath());
		context = new InitialContext(properties);
		
		UsernamePasswordHandler uph = new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS); 
		LoginContext loginContext = new LoginContext("qualipso", uph);
		loginContext.login();

		notification = (NotificationService) context.lookup(FactoryNamingConvention.JNDI_SERVICE_PREFIX + "NotificationService");
		eqs = (EventQueueService) context.lookup(FactoryNamingConvention.JNDI_SERVICE_PREFIX+"EventQueueService");
		greeting = (GreetingService) context.lookup("GreetingService");
		membership = (MembershipService) context.lookup(FactoryNamingConvention.JNDI_SERVICE_PREFIX+"MembershipService");
	}
	
	@Before
	public void setUp() throws Exception{		
		pathQueue1 = "/eventQueue/q1";
		pathQueue2 = "/eventQueue/q2";
		
		eqs.createEventQueue(pathQueue1);
		eqs.createEventQueue(pathQueue2);
		
		notification.register("/profiles/*", "/name/*", "greeting.name.create", pathQueue1);
		notification.register("/profiles/*", "/name/*", "greeting.name.sayhello", pathQueue1);		
	}
	
	@After
	public void tearDown(){
		eqs.deleteEventQueue(pathQueue1);
		eqs.deleteEventQueue(pathQueue2);
	}
	
	/**
	 * throw one event into one queue and check the event is inserted
	 * @throws Exception
	 */
	@Test(timeout=100)
	public void testNotification1() throws Exception{	
		String caller = membership.getProfilePathForConnectedIdentifier();
		
		greeting.createName("/name", "toto");
		Event[] lEvent1 = new Event[]{};

		while(lEvent1.length==0){
			lEvent1 = eqs.getEvents(pathQueue1);
		}	
		
		assertTrue(lEvent1.length==1);
		Event e = lEvent1[0];
		assertEquals(e.getFromResource(),"/name/toto");
		assertEquals(e.getEventType(),"greeting.name.create");
		assertEquals(e.getThrowedBy(),caller);
		
		Event[] lEvent2 = eqs.getEvents(pathQueue2);
		assertTrue(lEvent2.length==0);
	}
	
	/**
	 * throw 10 events into one queue and check the order of these 10 events
	 * @throws Exception 
	 */
	@Test(timeout=100)
	public void testNotification2() throws Exception{
		for(int i=0;i<10;i++){
			testNotification1();
		}

		Event[] lEvent1 = new Event[]{};
		while(lEvent1.length<9){
			lEvent1 = eqs.getEvents(pathQueue1);
		}	
		
		assertTrue(lEvent1.length==9);
		
		Event[] lEvent2 = eqs.getEvents(pathQueue2);
		assertTrue(lEvent2.length==0);
		
	}
	
	/**
	 * throw one event matching by 1/2 queue
	 */
	@Test(timeout=100)
	public void testNotification3(){
		
	}
	
	/**
	 * throw one event matching by 0 queue
	 */
	@Test(timeout=100)
	public void testNotification4(){
		
	}
	
	/**
	 * throw 2 event matching by one queue, but user
	 * can read only one event
	 */
	@Test(timeout=100)
	public void testNotification5(){
		
	}

}
