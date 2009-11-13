package org.qualipso.factory.test.sessionbean;

import static org.hamcrest.text.StringContains.containsString;

import javax.ejb.SessionContext;
import javax.jms.Session;
import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.BindingServiceException;
import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.eventqueue.EventQueueServiceBean;
import org.qualipso.factory.eventqueue.EventQueueServiceException;
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
	private static final Class[] usedBeans = {};
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

	/*
	 * public void testGetEvents() throws InvalidPathException,
	 * PathNotFoundException, MembershipServiceException{
	 * logger.debug("testing testGetEvents(...)"); final
	 * FactoryResourceIdentifier fe = new FactoryResourceIdentifier("aService",
	 * "EventQueue", "1"); final String caller = "Me"; mockery.checking(new
	 * Expectations() { { oneOf(binding).lookup(with(any(String.class)));
	 * will(returnValue(fe)); //
	 * oneOf(membership).getProfilePathForConnectedIdentifier();
	 * will(returnValue(caller)); oneOf(pep).checkSecurity(caller, path,
	 * "read");
	 * 
	 * } }); }
	 */
	public void testCreateEventQueue() throws MembershipServiceException,
			PEPServiceException, BindingServiceException, PAPServiceException {
		logger.debug("testing testCreateEventQueue(...)");
		final String name = "myQueue";
		final String caller = "theCaller";
		final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(
				"EventQueueService", "EventQueue", name);
		mockery.checking(new Expectations() {
			{

				// 
				oneOf(membership).getProfilePathForConnectedIdentifier();
				will(returnValue(caller));

				oneOf(pep).checkSecurity(caller, "/eventqueues", "create");
				oneOf(em).persist(with(any(EventQueue.class)));
				oneOf(binding).bind(identifier,
						EventQueueService.PROFILES_PATH + "/" + name);
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
		mockery.assertIsSatisfied();

	}

}
