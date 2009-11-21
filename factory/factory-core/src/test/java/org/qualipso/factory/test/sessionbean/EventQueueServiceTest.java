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
import org.qualipso.factory.eventqueue.entity.Event;
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
 * @authors Amrou Mohanned Khalifa Yiqing Li
 * 
 */
public class EventQueueServiceTest extends BaseSessionBeanFixture<EventQueueServiceBean> {
    private static Log logger = LogFactory.getLog(EventQueueServiceTest.class);

    @SuppressWarnings("unchecked")
    // private static final Class[] usedBeans = {Event.class, EventQueue.class
    // };
    private static final Class[] usedBeans = { EventQueue.class };
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
        getBeanToTest().setSessionContext(ctx);
    }

    /**
     * test la creation d'une eventqueue
     * 
     * @throws MembershipServiceException
     * @throws PEPServiceException
     * @throws BindingServiceException
     * @throws PAPServiceException
     * @throws EventQueueServiceException
     */

    public void testCreateEventQueue() throws MembershipServiceException, PEPServiceException, BindingServiceException, PAPServiceException,
            EventQueueServiceException {
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
     * test la creation d'une queue dont le nom a ete deja utilise
     * 
     * @throws MembershipServiceException
     * @throws PEPServiceException
     * @throws BindingServiceException
     * @throws PAPServiceException
     * @throws EventQueueServiceException
     */
    @Test(expected = EventQueueServiceException.class)
    public void testCreateEventQueueUsedName() throws MembershipServiceException, PEPServiceException, BindingServiceException, PAPServiceException,
            EventQueueServiceException {
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
     * test de l'insertion d'un event dans une liste
     * 
     * @throws MembershipServiceException
     * @throws PEPServiceException
     * @throws BindingServiceException
     * @throws PAPServiceException
     * @throws EventQueueServiceException
     */
    public void testpushEvent1() throws MembershipServiceException, PEPServiceException, BindingServiceException, PAPServiceException,
            EventQueueServiceException {

        logger.debug("testing testPushEvent(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "/queue/myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID.randomUUID().toString());

        final Vector<Object> allParams = new Vector<Object>();
        final EventQueue firstQueue = new EventQueue();
        firstQueue.setEvents(new ArrayList<Event>());
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

        Event e = new Event("aResource", "aService", "aType", "anEventType", "");
        eqsb.pushEvent(name, e);
        // l'event est bien ajoute
        assertEquals(((EventQueue) allParams.get(0)).getEvents().size(), 1);
        assertEquals(((EventQueue) allParams.get(0)).getEvents().get(0).getFromResource(), "aResource");
        assertEquals(((EventQueue) allParams.get(0)).getEvents().get(0).getThrowedBy(), "aService");
        assertEquals(((EventQueue) allParams.get(0)).getEvents().get(0).getResourceType(), "aType");
        assertEquals(((EventQueue) allParams.get(0)).getEvents().get(0).getEventType(), "anEventType");

        mockery.assertIsSatisfied();

        // ajout d'un autre event dans la queue

        logger.info(" deuxieme ajout d'evnet dans la liste ");
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

        e = new Event("aSResource", "aSService", "aSType", "aSEventType", "");
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
     * test la suppression d'un event qui n'existe pas dans une eventqueue.
     * 
     * @throws MembershipServiceException
     * @throws PEPServiceException
     * @throws BindingServiceException
     * @throws PAPServiceException
     * @throws EventQueueServiceException
     */
    public void testDeleteEventNotInTheQueue() throws MembershipServiceException, PEPServiceException, BindingServiceException, PAPServiceException,
            EventQueueServiceException {
        logger.debug("testing testDeleteEventNotInTheQueue(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID.randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        Event e = new Event("aResource", "aService", "aType", "anEventType", "");
        ArrayList<Event> ale = new ArrayList<Event>();
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
     * test la suppression d'un event qui existe pas dans une eventqueue.
     * 
     * @throws MembershipServiceException
     * @throws PEPServiceException
     * @throws BindingServiceException
     * @throws PAPServiceException
     * @throws EventQueueServiceException
     */
    public void testDeleteEventInTheQueue() throws MembershipServiceException, PEPServiceException, BindingServiceException, PAPServiceException,
            EventQueueServiceException {
        logger.debug("testing testDeleteEventInTheQueue(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID.randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        Event e1 = new Event("aResource", "aService", "aType", "anEventType", "");
        ArrayList<Event> eventL = new ArrayList<Event>();
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
     * suppression d'une queue qui existe
     * 
     * @throws MembershipServiceException
     * @throws PEPServiceException
     * @throws BindingServiceException
     * @throws EventQueueServiceException 
     */
    @Test(expected = EventQueueServiceException.class)
    public void testRemoveExistingQueue() throws MembershipServiceException, PEPServiceException, BindingServiceException, EventQueueServiceException {

        logger.debug("testing testRemoveExistingQueue(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID.randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        firstQueue.setEvents(new ArrayList<Event>());
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
     * suppression d'une queue qui n'existe pas
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    @Test(expected = EventQueueServiceException.class)
    public void testRemoveNotExistingQueue() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException {

        logger.debug("testing testRemoveNotExistingQueue(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID.randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        firstQueue.setEvents(new ArrayList<Event>());
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
     * recherche d'event dans une event queue par date
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    public void testfindEventByDate() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException {

        logger.debug("testing testfindEventByDate(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID.randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        ArrayList<Event> a = new ArrayList<Event>();
        a.add(new Event());
        a.add(new Event());
        a.add(new Event());

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
            Event[] myTab = (Event[]) (eqsb.findEventByDate(name, new Date()));
            assertEquals(myTab.length, 0);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();

    }

    /**
     * test la recherche d'event dans une queue , dans un interval de temps
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */

    public void testfindEventByDateBetween() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException {

        logger.debug("testing testfindEventByDateBetween(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID.randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        ArrayList<Event> a = new ArrayList<Event>();
        a.add(new Event());

        Date dTest = new Date();
        a.add(new Event());
        a.add(new Event());
        Date dTest2 = new Date();

        a.add(new Event());

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
            a.add(new Event());

            Event[] myTab = (Event[]) (eqsb.findEventByDateBetween(name, dTest, new Date()));
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
            Event[] myTab = (Event[]) (eqsb.findEventByDateBetween(name, dTest, dTest2));
            assertEquals(myTab.length, 4);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();
    }

    /**
     * test la recherche d'event avant une date donnée
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */

    public void testfindEventByDateInf() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException {

        logger.debug("testing testfindEventByDateInf(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID.randomUUID().toString());
        final EventQueue firstQueue = new EventQueue();
        ArrayList<Event> a = new ArrayList<Event>();
        a.add(new Event());

        Date dTest = new Date();
        a.add(new Event());
        a.add(new Event());
        Date dTest2 = new Date();

        a.add(new Event());

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
            a.add(new Event());

            Event[] myTab = (Event[]) (eqsb.findEventByDateInf(name, dTest));
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
            Event[] myTab = (Event[]) (eqsb.findEventByDateInf(name, dTest2));
            assertEquals(myTab.length, 4);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();
    }

    /**
     * test la recherche d'event après une date donnée
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    public void testfindEventByDateSup() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException {

        logger.debug("testing testfindEventByDateInf(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID.randomUUID().toString());
        final EventQueue firstQueue = new EventQueue();
        ArrayList<Event> a = new ArrayList<Event>();
        a.add(new Event());

        Date dTest = new Date();
        a.add(new Event());
        a.add(new Event());
        a.add(new Event());

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
            Event[] myTab = (Event[]) (eqsb.findEventByDateSup(name, dTest));
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
            a.add(new Event());
            EventQueueServiceBean eqsb = getBeanToTest();
            Event[] myTab = (Event[]) (eqsb.findEventByDateSup(name, dTest2));
            assertEquals(myTab.length, 1);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();
    }

    /**
     * test de recherche par eventType
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */

    public void testfindEventByEventType() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException {

        logger.debug("testing testfindEventByEventType(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID.randomUUID().toString());
        final EventQueue firstQueue = new EventQueue();
        ArrayList<Event> a = new ArrayList<Event>();
        a.add(new Event("/name", "Rule", "eventype", ""));
        a.add(new Event("/ressource", "Rule", "eventype2", ""));
        a.add(new Event("/ressource2", "Rule", "eventype3", ""));

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
            Event[] myTab = (Event[]) (eqsb.findEventByEventType(name, "eventype", false));
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
            Event[] myTab = (Event[]) (eqsb.findEventByEventType(name, "eventype", true));
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
            Event[] myTab = (Event[]) (eqsb.findEventByEventType(name, "eventype", false));
            assertEquals(myTab.length, 1);
        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();
    }

    /**
     * test des cas exceptionnelle de la recherche des events par leurs types
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    public void testfindEventByEventTypeNullParameter() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException {
        logger.debug("testing testfindEventByEventTypeNullParameter(...)");

        final String name = "myQueue";
        final EventQueue firstQueue = new EventQueue();
        ArrayList<Event> a = new ArrayList<Event>();
        a.add(new Event("/name", "Rule", "eventype", ""));
        a.add(new Event("/ressource", "Rule", "eventype2", ""));
        a.add(new Event("/ressource2", "Rule", "eventype3", ""));

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
     * test de recherche par type de ressource
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */

    public void testfindEventRessourceType() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException {
        logger.debug("testing testfindEventfromRessource(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final String ressource = "RessourceType";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID.randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        ArrayList<Event> a = new ArrayList<Event>();
        a.add(new Event("", "", ressource, "", ""));
        a.add(new Event("", "", "anOtherRessourceType", "", ""));
        a.add(new Event("", "", "TheRessourceType", "", ""));

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
            Event[] myTab = (Event[]) (eqsb.findEventByRessourceType(name, ressource, true));
            assertEquals(myTab.length, 3);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();

        // deuxieme exactement type ressource : RessourceType
        a.add(new Event("", "", "ressourceType", "", ""));
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
            Event[] myTab = (Event[]) (eqsb.findEventByRessourceType(name, ressource, false));
            assertEquals(myTab.length, 1);
            assertEquals(myTab[0].getThrowedBy(), ressource);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();
    }

    /**
     * test des cas exceptionnelle de la recherche des events a partir de types
     * de ressources
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    public void testfindEventByRessourceTypeNullParameter() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException {
        logger.debug("testing testfindEventByRessourceTypeNullParameter(...)");

        final String name = "myQueue";
        final EventQueue firstQueue = new EventQueue();
        ArrayList<Event> a = new ArrayList<Event>();
        a.add(new Event("/name", "Rule", "eventype", ""));
        a.add(new Event("/ressource", "Rule", "eventype2", ""));
        a.add(new Event("/ressource2", "Rule", "eventype3", ""));

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
            eqsb.findEventFromRessource(name, null, true);
            eqsb.findEventFromRessource(null, "eventype", false);
        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();
    }

    /**
     * test de recherche par ressource
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */

    public void testfindEventFromRessource() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException {
        logger.debug("testing testfindEventfromRessource(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final String ressource = "Ressource";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID.randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        ArrayList<Event> a = new ArrayList<Event>();
        a.add(new Event(ressource, "", "", "", ""));
        a.add(new Event("anOtherRessource", "", "", "", ""));
        a.add(new Event("TheRessource", "", "", "", ""));

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
            Event[] myTab = (Event[]) (eqsb.findEventByRessourceType(name, ressource, true));
            assertEquals(myTab.length, 3);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();

        // deuxieme exactement nom ressource : Ressource
        a.add(new Event("ressource", "", "", "", ""));
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
            Event[] myTab = (Event[]) (eqsb.findEventByRessourceType(name, ressource, false));
            assertEquals(myTab.length, 1);
            assertEquals(myTab[0].getThrowedBy(), ressource);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();
    }

    /**
     * test des cas exceptionnelle de la recherche des events a partir des
     * ressources
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    public void testfindEventFromRessourceNullParameter() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException {
        logger.debug("testing testfindEventFromRessourceNullParameter(...)");

        final String name = "myQueue";
        final EventQueue firstQueue = new EventQueue();
        ArrayList<Event> a = new ArrayList<Event>();
        a.add(new Event("/name", "Rule", "eventype", ""));
        a.add(new Event("/ressource", "Rule", "eventype2", ""));
        a.add(new Event("/ressource2", "Rule", "eventype3", ""));

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
            eqsb.findEventFromRessource(name, null, true);
            eqsb.findEventFromRessource(null, "eventype", false);
        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();
    }

    /**
     * test si un event est dans une EventQueue
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */
    public void testfindObjectEvent() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException {
        logger.debug("testing testfindObjectEvent(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID.randomUUID().toString());
        final EventQueue firstQueue = new EventQueue();
        ArrayList<Event> a = new ArrayList<Event>();
        Event e1 = new Event("/name", "Rule", "eventype", "");
        Event e2 = new Event("/name2", "Rule3", "eventypeAlpha", "args");
        a.add(new Event("/name", "Rule", "eventype", ""));
        a.add(new Event("/ressource", "Rule", "eventype2", ""));
        a.add(new Event("/ressource2", "Rule", "eventype3", ""));
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
            Event[] myTab = (Event[]) (eqsb.findObjectEvent(name, e1));
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
            Event[] myTab = (Event[]) (eqsb.findObjectEvent(name, e2));
            assertEquals(myTab.length, 0);
        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();
    }

    /**
     * test de recherche de thrower d'un event dans une queue
     * 
     * @throws InvalidPathException
     * @throws PathNotFoundException
     * @throws MembershipServiceException
     * @throws PEPServiceException
     */

    public void testfindEventBythrower() throws InvalidPathException, PathNotFoundException, MembershipServiceException, PEPServiceException {

        logger.debug("testing testfindEventBythrower(...)");
        final Sequence sq1 = mockery.sequence("seq1");

        final String name = "myQueue";
        final String caller = "theCaller";
        final String thrower1 = "Thrower";
        final FactoryResourceIdentifier identifier = new FactoryResourceIdentifier(EventQueueService.SERVICE_NAME, EventQueueService.SERVICE_NAME, UUID.randomUUID().toString());

        final EventQueue firstQueue = new EventQueue();
        ArrayList<Event> a = new ArrayList<Event>();
        a.add(new Event("", thrower1, "", "", ""));
        a.add(new Event("", "anOtherThrower", "", "", ""));
        a.add(new Event("", "TheThrower1", "", "", ""));

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
            Event[] myTab = (Event[]) (eqsb.findEventBythrower(name, thrower1, true));
            assertEquals(myTab.length, 3);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }

        mockery.assertIsSatisfied();

        // recherche exactement nom thrower : Thrower
        a.add(new Event("", "thrower2", "", "", ""));
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
            Event[] myTab = (Event[]) (eqsb.findEventBythrower(name, thrower1, false));
            assertEquals(myTab.length, 1);
            assertEquals(myTab[0].getThrowedBy(), thrower1);

        } catch (EventQueueServiceException e) {
            logger.info("exception generated : " + e.getMessage());
        }
        mockery.assertIsSatisfied();
    }

}
