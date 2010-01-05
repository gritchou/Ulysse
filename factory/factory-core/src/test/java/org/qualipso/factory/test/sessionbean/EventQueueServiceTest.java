package org.qualipso.factory.test.sessionbean;

import static org.qualipso.factory.test.jmock.action.SaveParamsAction.saveParams;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
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
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.BindingServiceException;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.eventqueue.EventQueueServiceBean;
import org.qualipso.factory.eventqueue.EventQueueServiceException;
import org.qualipso.factory.eventqueue.entity.PersistentEvent;
import org.qualipso.factory.eventqueue.entity.EventQueue;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceException;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.security.pep.PEPServiceException;

import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * 
 * @author Amrou Mohanned
 * @author Khalifa Yiqing Li
 * @author Philippe Schmucker
 * 
 */
public class EventQueueServiceTest extends BaseSessionBeanFixture<EventQueueServiceBean> {
    private static Log logger = LogFactory.getLog(EventQueueServiceTest.class);

    private static final Class<?>[] usedBeans = { EventQueue.class };
    private Mockery mockery;
    private EntityManager em;
    private BindingService binding;
    private PEPService pep;
    private PAPService pap;
    private MembershipService membership;
    private SessionContext ctx;

    public static int auto_ack = Session.AUTO_ACKNOWLEDGE;

    public EventQueueServiceTest() {
        super(EventQueueServiceBean.class, usedBeans);

    }

    /**
     * Set up mock objects for unit tests
     */
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
        getBeanToTest().setSessionContext(ctx);
    }

    /**
     * Test the creation of an eventqueue
     * 
     * @throws MembershipServiceException
     * @throws PEPServiceException
     * @throws BindingServiceException
     * @throws PAPServiceException
     * @throws EventQueueServiceException
     */

    @Test
    public void testCreateEventQueue() throws Exception {
        logger.debug("testing testCreateEventQueue(...)");
        final String name = "/queue/myQueue";
        final String caller = "theCaller";

        final Vector<Object> allParams = new Vector<Object>();
        final Sequence sequence1 = mockery.sequence("sequence1");
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(equal(PathHelper.getParentPath(name))), with(equal("create")));
                inSequence(sequence1);
                oneOf(em).persist(with(any(EventQueue.class)));
                will(saveParams(allParams));
                inSequence(sequence1);

                oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal(name)));
                inSequence(sequence1);
                oneOf(pap).createPolicy(with(any(String.class)), with(any(String.class)));
                inSequence(sequence1);
                oneOf(binding).setProperty(with(equal(name)), with(equal(FactoryResourceProperty.OWNER)), with(any(String.class)));
                inSequence(sequence1);
                oneOf(binding).setProperty(with(equal(name)), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class)));
                inSequence(sequence1);

            }
        });

        EventQueueServiceBean eqsb = getBeanToTest();

        eqsb.createEventQueue(name);

        assertEquals(((EventQueue) allParams.get(0)).getEvents().size(), 0);

        mockery.assertIsSatisfied();

    }

    /**
     * Test the creation of a queue which name was already used
     * 
     * @throws MembershipServiceException
     * @throws PEPServiceException
     * @throws BindingServiceException
     * @throws PAPServiceException
     * @throws EventQueueServiceException
     */
    @Test(expected = EventQueueServiceException.class)
    public void testCreateEventQueueUsedName() throws Exception {
        logger.debug("testing testCreateEventQueue2(...) ");
        logger.debug("an Other Queue have the same name ...");
        final String name = "/queue/myQueue";
        final String caller = "theCaller";

        final Sequence sq1 = mockery.sequence("seq1");

        mockery.checking(new Expectations() {
            {

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, PathHelper.getParentPath(name), "create");
                inSequence(sq1);
                oneOf(em).persist(with(any(EventQueue.class)));
                will(throwException(new EventQueueServiceException("illegal")));
                inSequence(sq1);

                oneOf(ctx).setRollbackOnly();
                inSequence(sq1);

            }
        });

        EventQueueServiceBean eqsb = getBeanToTest();
        try {
            eqsb.createEventQueue(name);
        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();

    }

    /**
     * Test insertion of an event in an eventqueue
     * 
     * @throws MembershipServiceException
     * @throws PEPServiceException
     * @throws BindingServiceException
     * @throws PAPServiceException
     * @throws EventQueueServiceException
     */
    @Test
    public void testpushEvent1() throws Exception {

        logger.debug("testing testPushEvent(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "/queue/myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID
                .randomUUID().toString());

        final Vector<Object> allParams = new Vector<Object>();
        final EventQueue firstQueue = new EventQueue();
        firstQueue.setEvents(new ArrayList<PersistentEvent>());
        firstQueue.setResourcePath(name);
        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "update");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

                oneOf(em).persist(with(any(EventQueue.class)));
                will(saveParams(allParams));
                inSequence(sq1);

            }
        });

        EventQueueServiceBean eqsb = getBeanToTest();

        PersistentEvent e = new PersistentEvent("aResource", "aService", "aType", "anEventType", "");
        eqsb.pushEvent(name, e);
        // l'event est bien ajoute
        assertEquals(((EventQueue) allParams.get(0)).getEvents().size(), 1);
        assertEquals(((EventQueue) allParams.get(0)).getEvents().get(0).getFromResource(), "aResource");
        assertEquals(((EventQueue) allParams.get(0)).getEvents().get(0).getThrowedBy(), "aService");
        assertEquals(((EventQueue) allParams.get(0)).getEvents().get(0).getResourceType(), "aType");
        assertEquals(((EventQueue) allParams.get(0)).getEvents().get(0).getEventType(), "anEventType");

        mockery.assertIsSatisfied();

        // ajout d'un autre event dans la queue

        logger.info("Second push of an event in the eventqueue ");
        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "update");
                inSequence(sq1);

                allowing(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue((EventQueue) (allParams.get(0))));
                inSequence(sq1);

                oneOf(em).persist(with(any(EventQueue.class)));
                will(saveParams(allParams));
                inSequence(sq1);

            }
        });

        e = new PersistentEvent("aSResource", "aSService", "aSType", "aSEventType", "");
        eqsb.pushEvent(name, e);
        // l'event est bien ajoute
        assertEquals(((EventQueue) allParams.get(1)).getEvents().size(), 2);
        assertEquals(((EventQueue) allParams.get(1)).getEvents().get(1).getFromResource(), "aSResource");
        assertEquals(((EventQueue) allParams.get(1)).getEvents().get(1).getThrowedBy(), "aSService");
        assertEquals(((EventQueue) allParams.get(1)).getEvents().get(1).getResourceType(), "aSType");
        assertEquals(((EventQueue) allParams.get(1)).getEvents().get(1).getEventType(), "aSEventType");

        mockery.assertIsSatisfied();

        mockery.assertIsSatisfied();
    }

    /**
     * Test the deletion of an event which doesn't exist in an eventqueue.
     * 
     * @throws MembershipServiceException
     * @throws PEPServiceException
     * @throws BindingServiceException
     * @throws PAPServiceException
     * @throws EventQueueServiceException
     */
    @Test
    public void testDeleteEventNotInTheQueue() throws Exception {
        logger.debug("testing testDeleteEventNotInTheQueue(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID
                .randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        PersistentEvent e = new PersistentEvent("aResource", "aService", "aType", "anEventType", "");
        ArrayList<PersistentEvent> ale = new ArrayList<PersistentEvent>();
        ale.add(e);
        firstQueue.setEvents(ale);

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "update");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

                oneOf(em).merge(with(any(EventQueue.class)));
                inSequence(sq1);

            }
        });

        EventQueueServiceBean eqsb = getBeanToTest();

        eqsb.deleteEvent(name, e);

        logger.info("TDENITQ");

        assertEquals(firstQueue.getEvents().size(), 0);

        mockery.assertIsSatisfied();

    }

    /**
     * Test the deletion of an event which exist in an eventqueue.
     * 
     * @throws MembershipServiceException
     * @throws PEPServiceException
     * @throws BindingServiceException
     * @throws PAPServiceException
     * @throws EventQueueServiceException
     */
    @Test
    public void testDeleteEventInTheQueue() throws Exception {
        logger.debug("testing testDeleteEventInTheQueue(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID
                .randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        PersistentEvent e1 = new PersistentEvent("aResource", "aService", "aType", "anEventType", "");
        ArrayList<PersistentEvent> eventL = new ArrayList<PersistentEvent>();
        eventL.add(e1);
        firstQueue.setEvents(eventL);
        firstQueue.setResourcePath(name);

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "update");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

                // 1.code source d'eventQueue a reverifier car faux!!!( n'enlve
                // rien ou peut etre dernier)
                // 2. code corrige : Amrou L460

                oneOf(em).merge(with(any(EventQueue.class)));
                inSequence(sq1);
            }
        });

        EventQueueServiceBean eqsb = getBeanToTest();
        eqsb.deleteEvent(name, e1);
        logger.info("TDENITQ");

        assertEquals(firstQueue.getEvents().size(), 0);

        mockery.assertIsSatisfied();

    }

    /**
     * Test deletion of an existing queue
     * 
     * @throws MembershipServiceException
     * @throws PEPServiceException
     * @throws BindingServiceException
     * @throws EventQueueServiceException
     */
    @Test(expected = EventQueueServiceException.class)
    public void testRemoveExistingQueue() throws Exception {

        logger.debug("testing testRemoveExistingQueue(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID
                .randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        firstQueue.setEvents(new ArrayList<PersistentEvent>());
        firstQueue.setResourcePath(name);

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "update");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

                oneOf(em).remove(with(any(EventQueue.class)));
                oneOf(binding).unbind(name);
                inSequence(sq1);
            }
        });

        EventQueueServiceBean eqsb = getBeanToTest();

        eqsb.removeQueue(name);

        logger.info("Second Passage pour verification TREQ");

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "update");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(null));
                inSequence(sq1);

                oneOf(ctx).setRollbackOnly();
                inSequence(sq1);

            }
        });

        try {
            eqsb.removeQueue(name);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();

    }

    /**
     * Test deletion of an inexistant queue
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    @Test(expected = EventQueueServiceException.class)
    public void testRemoveNotExistingQueue() throws Exception {

        logger.debug("testing testRemoveNotExistingQueue(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID
                .randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        firstQueue.setEvents(new ArrayList<PersistentEvent>());
        firstQueue.setResourcePath(name);

        logger.info(" verification TRNEQ");

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "update");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(throwException(with(any(EventQueueServiceException.class))));
                inSequence(sq1);

                oneOf(ctx).setRollbackOnly();
                inSequence(sq1);

            }
        });

        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            eqsb.removeQueue(name);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();

    }

    /**
     * Test search by date of an event in an eventqueue
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    @Test
    public void testfindEventByDate() throws Exception {

        logger.debug("testing testfindEventByDate(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID
                .randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        ArrayList<PersistentEvent> a = new ArrayList<PersistentEvent>();
        a.add(new PersistentEvent());
        a.add(new PersistentEvent());
        a.add(new PersistentEvent());

        firstQueue.setEvents(a);
        firstQueue.setResourcePath(name);

        logger.info(" verification TRNEQ");

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

            }
        });

        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findEventByDate(name, new Date()));
            assertEquals(myTab.length, 0);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();

    }

    /**
     * Test search of an event in a queue, using a time interval
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    @Test
    public void testfindEventByDateBetween() throws Exception {

        logger.debug("testing testfindEventByDateBetween(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID
                .randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        ArrayList<PersistentEvent> a = new ArrayList<PersistentEvent>();
        a.add(new PersistentEvent());

        Date dTest = new Date();
        a.add(new PersistentEvent());
        a.add(new PersistentEvent());
        Date dTest2 = new Date();

        a.add(new PersistentEvent());

        firstQueue.setEvents(a);
        firstQueue.setResourcePath(name);

        logger.info(" verification TRNEQ");

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

            }
        });

        try {

            EventQueueServiceBean eqsb = getBeanToTest();
            a.add(new PersistentEvent());

            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findEventByDateBetween(name, dTest, new Date()));
            assertEquals(myTab.length, 5);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();

        // entre 2 dates choix arbitrairement

        logger.info("2ieme test");

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

            }
        });

        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findEventByDateBetween(name, dTest, dTest2));
            assertEquals(myTab.length, 4);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();
    }

    /**
     * Test search of an event before a given date
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    @Test
    public void testfindEventByDateInf() throws Exception {

        logger.debug("testing testfindEventByDateInf(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID
                .randomUUID().toString());
        final EventQueue firstQueue = new EventQueue();
        ArrayList<PersistentEvent> a = new ArrayList<PersistentEvent>();
        a.add(new PersistentEvent());

        Date dTest = new Date();
        a.add(new PersistentEvent());
        a.add(new PersistentEvent());
        Date dTest2 = new Date();

        a.add(new PersistentEvent());

        firstQueue.setEvents(a);
        firstQueue.setResourcePath(name);

        logger.info(" verification TRNEQ");

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

            }
        });

        try {

            EventQueueServiceBean eqsb = getBeanToTest();
            a.add(new PersistentEvent());

            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findEventByDateInf(name, dTest));
            assertEquals(myTab.length, 4);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();

        // avant dTest 2

        logger.info("2ieme test");

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

            }
        });

        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findEventByDateInf(name, dTest2));
            assertEquals(myTab.length, 4);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();
    }

    /**
     * Test search of an event after a given date
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    @Test
    public void testfindEventByDateSup() throws Exception {

        logger.debug("testing testfindEventByDateInf(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID
                .randomUUID().toString());
        final EventQueue firstQueue = new EventQueue();
        ArrayList<PersistentEvent> a = new ArrayList<PersistentEvent>();
        a.add(new PersistentEvent());

        Date dTest = new Date();
        a.add(new PersistentEvent());
        a.add(new PersistentEvent());
        a.add(new PersistentEvent());

        firstQueue.setEvents(a);
        firstQueue.setResourcePath(name);

        logger.info(" verification TRNEQ");

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

            }
        });

        try {

            EventQueueServiceBean eqsb = getBeanToTest();
            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findEventByDateSup(name, dTest));
            assertEquals(myTab.length, 4);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();

        // avant dTest 2

        logger.info("2ieme test");

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

            }
        });

        try {
            Date dTest2 = new Date();
            a.add(new PersistentEvent());
            EventQueueServiceBean eqsb = getBeanToTest();
            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findEventByDateSup(name, dTest2));
            assertEquals(myTab.length, 1);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();
    }

    /**
     * Test search of an event by eventType
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    @Test
    public void testfindEventByEventType() throws Exception {

        logger.debug("testing testfindEventByEventType(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID
                .randomUUID().toString());
        final EventQueue firstQueue = new EventQueue();
        ArrayList<PersistentEvent> a = new ArrayList<PersistentEvent>();
        a.add(new PersistentEvent("/name", "Rule", "eventype", ""));
        a.add(new PersistentEvent("/ressource", "Rule", "eventype2", ""));
        a.add(new PersistentEvent("/ressource2", "Rule", "eventype3", ""));

        logger.info(" verification TRNEQ");
        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

                oneOf(ctx).setRollbackOnly();

            }
        });

        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findEventByEventType(name, "eventype", false));
            assertEquals(myTab.length, 0);
        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();

        //
        firstQueue.setEvents(a);
        firstQueue.setResourcePath(name);

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

            }
        });

        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findEventByEventType(name, "eventype", true));
            assertEquals(myTab.length, 3);
        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

            }
        });

        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findEventByEventType(name, "eventype", false));
            assertEquals(myTab.length, 1);
        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();
    }

    /**
     * Test exceptionnal cases of events searches with their types
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    @Test
    public void testfindEventByEventTypeNullParameter() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException {
        logger.debug("testing testfindEventByEventTypeNullParameter(...)");

        final String name = "myQueue";
        final EventQueue firstQueue = new EventQueue();
        ArrayList<PersistentEvent> a = new ArrayList<PersistentEvent>();
        a.add(new PersistentEvent("/name", "Rule", "eventype", ""));
        a.add(new PersistentEvent("/ressource", "Rule", "eventype2", ""));
        a.add(new PersistentEvent("/ressource2", "Rule", "eventype3", ""));

        firstQueue.setEvents(a);
        firstQueue.setResourcePath(name);

        mockery.checking(new Expectations() {
            {
                oneOf(ctx).setRollbackOnly();
            }
        });

        logger.info(" verification TRNEQ");
        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            eqsb.findEventByEventType(name, null, true);
            eqsb.findEventByEventType(null, "eventype", false);
        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();
    }

    /**
     * Test search of an event by type of resource
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    @Test
    public void testfindEventRessourceType() throws Exception {
        logger.debug("testing testfindEventfromRessource(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final String ressource = "RessourceType";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID
                .randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        ArrayList<PersistentEvent> a = new ArrayList<PersistentEvent>();
        a.add(new PersistentEvent("", "", ressource, "", ""));
        a.add(new PersistentEvent("", "", "anOtherRessourceType", "", ""));
        a.add(new PersistentEvent("", "", "TheRessourceType", "", ""));

        firstQueue.setEvents(a);
        firstQueue.setResourcePath(name);

        logger.info(" verification TFERT");

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

                oneOf(ctx).setRollbackOnly();

            }
        });

        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findEventByRessourceType(name, ressource, true));
            assertEquals(myTab.length, 3);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();

        // deuxieme exactement type ressource : RessourceType
        a.add(new PersistentEvent("", "", "ressourceType", "", ""));
        firstQueue.setEvents(a);

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

                oneOf(ctx).setRollbackOnly();

            }
        });

        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findEventByRessourceType(name, ressource, false));
            assertEquals(myTab.length, 1);
            assertEquals(myTab[0].getThrowedBy(), ressource);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();
    }

    /**
     * Test exceptionnal cases of events searches with their type of resource
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    @Test
    public void testfindEventByRessourceTypeNullParameter() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException {
        logger.debug("testing testfindEventByRessourceTypeNullParameter(...)");

        final String name = "myQueue";
        final EventQueue firstQueue = new EventQueue();
        ArrayList<PersistentEvent> a = new ArrayList<PersistentEvent>();
        a.add(new PersistentEvent("/name", "Rule", "eventype", ""));
        a.add(new PersistentEvent("/ressource", "Rule", "eventype2", ""));
        a.add(new PersistentEvent("/ressource2", "Rule", "eventype3", ""));

        firstQueue.setEvents(a);
        firstQueue.setResourcePath(name);

        mockery.checking(new Expectations() {
            {
                oneOf(ctx).setRollbackOnly();
            }
        });

        logger.info(" verification TFEBRTNP");
        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            eqsb.findEventByFromRessource(name, null, true);
            eqsb.findEventByFromRessource(null, "eventype", false);
        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();
    }

    /**
     * Test search of an event by resource
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    @Test
    public void testfindEventFromRessource() throws Exception {
        logger.debug("testing testfindEventfromRessource(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final String ressource = "Ressource";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID
                .randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        ArrayList<PersistentEvent> a = new ArrayList<PersistentEvent>();
        a.add(new PersistentEvent(ressource, "", "", "", ""));
        a.add(new PersistentEvent("anOtherRessource", "", "", "", ""));
        a.add(new PersistentEvent("TheRessource", "", "", "", ""));

        firstQueue.setEvents(a);
        firstQueue.setResourcePath(name);

        logger.info(" verification TFEFR");

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

                oneOf(ctx).setRollbackOnly();

            }
        });

        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findEventByRessourceType(name, ressource, true));
            assertEquals(myTab.length, 3);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();

        // deuxieme exactement nom ressource : Ressource
        a.add(new PersistentEvent("ressource", "", "", "", ""));
        firstQueue.setEvents(a);

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

                oneOf(ctx).setRollbackOnly();

            }
        });

        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findEventByRessourceType(name, ressource, false));
            assertEquals(myTab.length, 1);
            assertEquals(myTab[0].getThrowedBy(), ressource);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();
    }

    /**
     * Test exceptionnal cases of events searches from resources
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    @Test
    public void testfindEventFromRessourceNullParameter() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException {
        logger.debug("testing testfindEventFromRessourceNullParameter(...)");

        final String name = "myQueue";
        final EventQueue firstQueue = new EventQueue();
        ArrayList<PersistentEvent> a = new ArrayList<PersistentEvent>();
        a.add(new PersistentEvent("/name", "Rule", "eventype", ""));
        a.add(new PersistentEvent("/ressource", "Rule", "eventype2", ""));
        a.add(new PersistentEvent("/ressource2", "Rule", "eventype3", ""));

        firstQueue.setEvents(a);
        firstQueue.setResourcePath(name);

        mockery.checking(new Expectations() {
            {
                oneOf(ctx).setRollbackOnly();
            }
        });

        logger.info(" verification TFEFRNP");
        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            eqsb.findEventByFromRessource(name, null, true);
            eqsb.findEventByFromRessource(null, "eventype", false);
        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();
    }

    /**
     * Test if an event is in an EventQueue
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    @Test
    public void testfindObjectEvent() throws Exception {
        logger.debug("testing testfindObjectEvent(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID
                .randomUUID().toString());
        final EventQueue firstQueue = new EventQueue();
        ArrayList<PersistentEvent> a = new ArrayList<PersistentEvent>();
        PersistentEvent e1 = new PersistentEvent("/name", "Rule", "eventype", "");
        PersistentEvent e2 = new PersistentEvent("/name2", "Rule3", "eventypeAlpha", "args");
        a.add(new PersistentEvent("/name", "Rule", "eventype", ""));
        a.add(new PersistentEvent("/ressource", "Rule", "eventype2", ""));
        a.add(new PersistentEvent("/ressource2", "Rule", "eventype3", ""));
        firstQueue.setEvents(a);
        firstQueue.setResourcePath(name);

        logger.info(" verification TFOE");
        // Event e1 est dans la queue firstqueue
        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

            }
        });

        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findObjectEvent(name, e1));
            assertEquals(myTab.length, 1);
        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();
        // Event e2 n'est pas dans la queue firstqueue

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

            }
        });

        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findObjectEvent(name, e2));
            assertEquals(myTab.length, 0);
        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();
    }

    /**
     * Test search of an event by thrower in a queue
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    @Test
    public void testfindEventBythrower() throws Exception {

        logger.debug("testing testfindEventBythrower(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final String thrower1 = "Thrower";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID
                .randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        ArrayList<PersistentEvent> a = new ArrayList<PersistentEvent>();
        a.add(new PersistentEvent("", thrower1, "", "", ""));
        a.add(new PersistentEvent("", "anOtherThrower", "", "", ""));
        a.add(new PersistentEvent("", "TheThrower1", "", "", ""));

        firstQueue.setEvents(a);
        firstQueue.setResourcePath(name);

        logger.info(" verification TFEBT");

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

            }
        });

        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findEventBythrower(name, thrower1, true));
            assertEquals(myTab.length, 3);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();

        // recherche exactement nom thrower : Thrower
        a.add(new PersistentEvent("", "thrower2", "", "", ""));
        firstQueue.setEvents(a);

        mockery.checking(new Expectations() {
            {

                oneOf(binding).lookup(name);
                will(returnValue(identifier));
                inSequence(sq1);

                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sq1);

                oneOf(pep).checkSecurity(caller, name, "read");
                inSequence(sq1);

                oneOf(em).find(with(EventQueue.class), with(any(String.class)));
                will(returnValue(firstQueue));
                inSequence(sq1);

            }
        });

        try {
            EventQueueServiceBean eqsb = getBeanToTest();
            PersistentEvent[] myTab = (PersistentEvent[]) (eqsb.findEventBythrower(name, thrower1, false));
            assertEquals(myTab.length, 1);
            assertEquals(myTab[0].getThrowedBy(), thrower1);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();
    }

}
