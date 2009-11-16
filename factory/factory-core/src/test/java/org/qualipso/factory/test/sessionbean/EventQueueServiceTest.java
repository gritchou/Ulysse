package org.qualipso.factory.test.sessionbean;

import static org.hamcrest.text.StringContains.containsString;
import static org.qualipso.factory.test.jmock.action.SaveParamsAction.saveParams;

import java.util.ArrayList;
import java.util.Vector;

import javax.ejb.SessionContext;
import javax.jms.Session;
import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.junit.Test;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.BindingServiceException;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.eventqueue.EventQueueServiceBean;
import org.qualipso.factory.eventqueue.EventQueueServiceException;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.eventqueue.entity.EventQueue;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceException;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.security.pep.PEPServiceException;

import com.bm.testsuite.BaseSessionBeanFixture;

public class EventQueueServiceTest extends
		BaseSessionBeanFixture<EventQueueServiceBean> {
	private static Log logger = LogFactory
			.getLog(NotificationServiceTest.class);

	@SuppressWarnings("unchecked")
	// private static final Class[] usedBeans = {Event.class, EventQueue.class
	// };
	private static final Class[] usedBeans = {EventQueue.class};
	private Mockery mockery;
	private EntityManager em;
	private BindingService binding;
	private PEPService pep;
	private PAPService pap;
	private MembershipService membership;
	private SessionContext ctx;

	public static int auto_ack = Session.AUTO_ACKNOWLEDGE;

	public EventQueueServiceTest() {
		// super(EventQueueServiceBean.class, usedBeans);
		super(EventQueueServiceBean.class, usedBeans);

	}

	public void setUp() throws Exception {
		super.setUp();
		logger.debug("Session beans");
		mockery = new Mockery();
		em = mockery.mock(EntityManager.class);
		binding = mockery.mock(BindingService.class);
		pep = mockery.mock(PEPService.class);
		pap = mockery.mock(PAPService.class);
		membership = mockery.mock(MembershipService.class);
		ctx = mockery.mock(SessionContext.class);
		getBeanToTest().setEntityManager(em);
		getBeanToTest().setBindingService(binding);
		getBeanToTest().setMembershipService(membership);
		getBeanToTest().setPAPService(pap);
		getBeanToTest().setPEPService(pep);

	}

	
	public void testCreateEventQueue() throws MembershipServiceException,
			PEPServiceException, BindingServiceException, PAPServiceException {
		logger.debug("testing testCreateEventQueue(...)");
		final String name = "myQueue";
		final String caller = "theCaller";
		final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(
				EventQueueService.SERVICE_NAME,EventQueueService.SERVICE_NAME, name);
		
        final Vector<Object> allParams = new Vector<Object>();

		mockery.checking(new Expectations() {
			{

				// 
				oneOf(membership).getProfilePathForConnectedIdentifier();
				will(returnValue(caller));

				oneOf(pep).checkSecurity(caller, "/queues", "create");
				oneOf(em).persist(with(any(EventQueue.class)));
				will(saveParams(allParams));

				oneOf(binding).bind(identifier,
						EventQueueService.QUEUES_PATH + "/" + name);
				oneOf(pap).createPolicy(with(any(String.class)),
						with(any(String.class)));

			}
		});

		EventQueueServiceBean eqsb = getBeanToTest();
		try {
			eqsb.createEventQueue(name);
		} catch (EventQueueServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("allParams.get(0).getName() = "+((EventQueue)allParams.get(0)).getName());
		assertEquals(((EventQueue)allParams.get(0)).getName(),name);
		assertEquals(((EventQueue)allParams.get(0)).getResourcePath(),EventQueueService.QUEUES_PATH+"/"+name);
		assertEquals(((EventQueue)allParams.get(0)).getEvents().size(),0);
		
		mockery.assertIsSatisfied();

	}
	/*
	@Test(expected=EventQueueServiceException.class) 
	public void testCreateEventQueue2() throws MembershipServiceException,
	PEPServiceException, BindingServiceException, PAPServiceException, EventQueueServiceException {
		logger.debug("testing testCreateEventQueue2(...) ");
		logger.debug("an Other Queue have the same name ...");
		final String name = "myQueue";
		final String caller = "theCaller";
		final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(
				EventQueueService.SERVICE_NAME,EventQueueService.SERVICE_NAME, name);

		final Vector<Object> allParams = new Vector<Object>();

		mockery.checking(new Expectations() {
			{

				// 
				oneOf(membership).getProfilePathForConnectedIdentifier();
				will(returnValue(caller));

				oneOf(pep).checkSecurity(caller, "/queues", "create");
				oneOf(em).persist(with(any(EventQueue.class)));
				will(throwException(new EventQueueServiceException("illegal")));

				
			}
		});

		EventQueueServiceBean eqsb = getBeanToTest();
		
		eqsb.createEventQueue(name);
		mockery.assertIsSatisfied();

	}
	*/
	public void testpushEvent1() throws MembershipServiceException, PEPServiceException, BindingServiceException, PAPServiceException{
		
		logger.debug("testing testPushEvent(...)");
		final Sequence sq1 = mockery.sequence("seq1");
		final Sequence sq2 = mockery.sequence("seq2");

		final String name = "myQueue";
		final String caller = "theCaller";
		final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(
				EventQueueService.SERVICE_NAME,EventQueueService.SERVICE_NAME, name);
		
        final Vector<Object> allParams = new Vector<Object>();
        final EventQueue firstQueue = new EventQueue();
        firstQueue.setEvents(new ArrayList<Event>());
        firstQueue.setName(name);
        firstQueue.setResourcePath(EventQueueService.QUEUES_PATH+"/"+name);
		mockery.checking(new Expectations() {
			{

				oneOf(binding).lookup(EventQueueService.QUEUES_PATH+"/"+name);
				will(returnValue(firstQueue));
				inSequence(sq1);

				oneOf(membership).getProfilePathForConnectedIdentifier();
				will(returnValue(caller));
				inSequence(sq1);

				oneOf(pep).checkSecurity(caller, "/queues", "update");
				inSequence(sq1);

				allowing(em).find(EventQueue.class,with(any(String.class)));
				will(returnValue(firstQueue));
				inSequence(sq1);

				oneOf(em).persist(with(any(EventQueue.class)));
				will(saveParams(allParams));
				inSequence(sq1);


				

			}
		});

		EventQueueServiceBean eqsb = getBeanToTest();
		try {
			Event e = new Event("aResource","aService","aType","anEventType","");
			eqsb.pushEvent(name, e);
			//l'event est bien ajoute
			assertEquals(((EventQueue)allParams.get(0)).getName(),name);
			assertEquals(((EventQueue)allParams.get(0)).getEvents().size(),1);
			assertEquals(((EventQueue)allParams.get(0)).getEvents().get(0).getFromResource(),"aResource");
			assertEquals(((EventQueue)allParams.get(0)).getEvents().get(0).getThrowedBy(),"aService");
			assertEquals(((EventQueue)allParams.get(0)).getEvents().get(0).getResourceType(),"aType");
			assertEquals(((EventQueue)allParams.get(0)).getEvents().get(0).getEventType(),"anEventType");
			
			mockery.assertIsSatisfied();

			
			
		} catch (EventQueueServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//ajout d'un autre event dans la queue
		
		logger.info(" deuxieme ajout d'evnet dans la liste ");
		mockery.checking(new Expectations() {
			{

				oneOf(binding).lookup(EventQueueService.QUEUES_PATH+"/"+name);
				will(returnValue(firstQueue));
				inSequence(sq2);

				oneOf(membership).getProfilePathForConnectedIdentifier();
				will(returnValue(caller));
				inSequence(sq2);

				oneOf(pep).checkSecurity(caller, "/queues", "update");
				inSequence(sq2);

				allowing(em).find(EventQueue.class,with(any(String.class)));
				will(returnValue((EventQueue)(allParams.get(0))));
				inSequence(sq2);

				oneOf(em).persist(with(any(EventQueue.class)));
				will(saveParams(allParams));
				inSequence(sq2);


				

			}
		});
		
		try {
			Event e = new Event("aSResource","aSService","aSType","aSEventType","");
			eqsb.pushEvent(name, e);
			//l'event est bien ajoute
			assertEquals(((EventQueue)allParams.get(1)).getName(),name);
			assertEquals(((EventQueue)allParams.get(1)).getEvents().size(),2);
			assertEquals(((EventQueue)allParams.get(1)).getEvents().get(1).getFromResource(),"aSResource");
			assertEquals(((EventQueue)allParams.get(1)).getEvents().get(1).getThrowedBy(),"aSService");
			assertEquals(((EventQueue)allParams.get(1)).getEvents().get(1).getResourceType(),"aSType");
			assertEquals(((EventQueue)allParams.get(1)).getEvents().get(1).getEventType(),"aSEventType");
			
			mockery.assertIsSatisfied();

			
			
		} catch (EventQueueServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		mockery.assertIsSatisfied();
	}
	
	public void testDeleteEventNotInTheQueue() throws MembershipServiceException,
	PEPServiceException, BindingServiceException, PAPServiceException {
		logger.debug("testing testDeleteEventInTheQueue(...)");
		final Sequence sq1 = mockery.sequence("seq1");

		final String name = "myQueue";
		final String caller = "theCaller";
		final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(
				EventQueueService.SERVICE_NAME,EventQueueService.SERVICE_NAME, name);

		final Vector<Object> allParams = new Vector<Object>();
		
        final EventQueue firstQueue = new EventQueue();
        firstQueue.setEvents(new ArrayList<Event>());
        firstQueue.setName(name);
        firstQueue.setResourcePath(EventQueueService.QUEUES_PATH+"/"+name);

		mockery.checking(new Expectations() {
			{

				oneOf(binding).lookup(EventQueueService.QUEUES_PATH+"/"+name);
				will(returnValue(firstQueue));
				inSequence(sq1);

				oneOf(membership).getProfilePathForConnectedIdentifier();
				will(returnValue(caller));
				inSequence(sq1);

				oneOf(pep).checkSecurity(caller, "/queues"+"/"+name, "update");
				inSequence(sq1);

				allowing(em).find(EventQueue.class,with(any(String.class)));
				will(returnValue(firstQueue));
				inSequence(sq1);

				//code source d'eventQueue a reverifier car faux!!!( n'enlve rien ou peut etre dernier)

			}
		});

		EventQueueServiceBean eqsb = getBeanToTest();
		try {
			Event e = new Event("aResource","aService","aType","anEventType","");
			eqsb.deleteEvent(EventQueueService.QUEUES_PATH+"/"+name, e);
		} catch (EventQueueServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("TDENITQ");
		assertEquals(((EventQueue)allParams.get(0)).getName(),name);
		assertEquals(((EventQueue)allParams.get(0)).getResourcePath(),EventQueueService.QUEUES_PATH+"/"+name);
		assertEquals(((EventQueue)allParams.get(0)).getEvents().size(),0);

		mockery.assertIsSatisfied();

	}
	
	
	
	public void testDeleteEventInTheQueue() throws MembershipServiceException,
	PEPServiceException, BindingServiceException, PAPServiceException {
		logger.debug("testing testDeleteEventInTheQueue(...)");
		final Sequence sq1 = mockery.sequence("seq1");
		
		final String name = "myQueue";
		final String caller = "theCaller";
		final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(
				EventQueueService.SERVICE_NAME,EventQueueService.SERVICE_NAME, name);

		final Vector<Object> allParams = new Vector<Object>();
		
        final EventQueue firstQueue = new EventQueue();
		Event e1 = new Event("aResource","aService","aType","anEventType","");
		ArrayList<Event> eventL = new ArrayList<Event>();
		eventL.add(e1);
        firstQueue.setEvents(eventL);
        firstQueue.setName(name);
        firstQueue.setResourcePath(EventQueueService.QUEUES_PATH+"/"+name);

		mockery.checking(new Expectations() {
			{

				oneOf(binding).lookup(EventQueueService.QUEUES_PATH+"/"+name);
				will(returnValue(firstQueue));
				inSequence(sq1);

				oneOf(membership).getProfilePathForConnectedIdentifier();
				will(returnValue(caller));
				inSequence(sq1);

				oneOf(pep).checkSecurity(caller, "/queues"+"/"+name, "update");
				inSequence(sq1);

				allowing(em).find(EventQueue.class,with(any(String.class)));
				will(returnValue(firstQueue));
				inSequence(sq1);

				//code source d'eventQueue a reverifier car faux!!!( n'enlve rien ou peut etre dernier)

			}
		});

		EventQueueServiceBean eqsb = getBeanToTest();
		try {
			eqsb.deleteEvent(EventQueueService.QUEUES_PATH+"/"+name, e1);
		} catch (EventQueueServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("TDENITQ");
		assertEquals(((EventQueue)allParams.get(0)).getName(),name);
		assertEquals(((EventQueue)allParams.get(0)).getResourcePath(),EventQueueService.QUEUES_PATH+"/"+name);
		assertEquals(((EventQueue)allParams.get(0)).getEvents().size(),0);

		mockery.assertIsSatisfied();

	}
	
	
	@Test(expected=EventQueueServiceException.class)
	public void testRemoveExistingQueue() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException{
		
		
		logger.debug("testing testRemoveExistingQueue(...)");
		final Sequence sq1 = mockery.sequence("seq1");
		final Sequence sq2 = mockery.sequence("seq2");


		final String name = "myQueue";
		final String caller = "theCaller";
		final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(
				EventQueueService.SERVICE_NAME,EventQueueService.SERVICE_NAME, name);

		final Vector<Object> allParams = new Vector<Object>();
		
        final EventQueue firstQueue = new EventQueue();
        firstQueue.setEvents(new ArrayList<Event>());
        firstQueue.setName(name);
        firstQueue.setResourcePath(EventQueueService.QUEUES_PATH+"/"+name);

		mockery.checking(new Expectations() {
			{

				oneOf(binding).lookup(EventQueueService.QUEUES_PATH+"/"+name);
				will(returnValue(firstQueue));
				inSequence(sq1);

				oneOf(membership).getProfilePathForConnectedIdentifier();
				will(returnValue(caller));
				inSequence(sq1);

				oneOf(pep).checkSecurity(caller, "/queues"+"/"+name, "update");
				inSequence(sq1);

				allowing(em).find(EventQueue.class,with(any(String.class)));
				will(returnValue(firstQueue));
				inSequence(sq1);

				oneOf(em).remove(any(EventQueue.class));
			}
		});

		EventQueueServiceBean eqsb = getBeanToTest();
		try {
			eqsb.removeQueue(EventQueueService.QUEUES_PATH+"/"+name);
			
		} catch (EventQueueServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("Second Passage pour verification TREQ");
		
		mockery.checking(new Expectations() {
			{

				oneOf(binding).lookup(EventQueueService.QUEUES_PATH+"/"+name);
				will(returnValue(firstQueue));
				inSequence(sq2);

				oneOf(membership).getProfilePathForConnectedIdentifier();
				will(returnValue(caller));
				inSequence(sq2);

				oneOf(pep).checkSecurity(caller, "/queues"+"/"+name, "update");
				inSequence(sq2);

				allowing(em).find(EventQueue.class,with(any(String.class)));
				will(throwException(with(any(EventQueueServiceException.class))));
				inSequence(sq2);

			}
		});
		
		
		try {
			eqsb.removeQueue(EventQueueService.QUEUES_PATH+"/"+name);
			
		} catch (EventQueueServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	

		mockery.assertIsSatisfied();
		
		
		
	}
	
	@Test(expected=EventQueueServiceException.class)
	public void testRemoveNotExistingQueue() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException{
		
		
		logger.debug("testing testRemoveNotExistingQueue(...)");
		final Sequence sq2 = mockery.sequence("seq2");


		final String name = "myQueue";
		final String caller = "theCaller";
		final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(
				EventQueueService.SERVICE_NAME,EventQueueService.SERVICE_NAME, name);

		final Vector<Object> allParams = new Vector<Object>();
		
        final EventQueue firstQueue = new EventQueue();
        firstQueue.setEvents(new ArrayList<Event>());
        firstQueue.setName(name);
        firstQueue.setResourcePath(EventQueueService.QUEUES_PATH+"/"+name);

	

		logger.info(" verification TRNEQ");
		
		mockery.checking(new Expectations() {
			{

				oneOf(binding).lookup(EventQueueService.QUEUES_PATH+"/"+name);
				will(returnValue(firstQueue));
				inSequence(sq2);

				oneOf(membership).getProfilePathForConnectedIdentifier();
				will(returnValue(caller));
				inSequence(sq2);

				oneOf(pep).checkSecurity(caller, "/queues"+"/"+name, "update");
				inSequence(sq2);

				allowing(em).find(EventQueue.class,with(any(String.class)));
				will(throwException(with(any(EventQueueServiceException.class))));
				inSequence(sq2);

			}
		});
		
		
		try{ 
			EventQueueServiceBean eqsb = getBeanToTest();
			eqsb.removeQueue(EventQueueService.QUEUES_PATH+"/"+name);
			
		} catch (EventQueueServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	

		mockery.assertIsSatisfied();
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
}
