package org.qualipso.factory.test.sessionbean;

import javax.ejb.SessionContext;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.eventqueue.EventQueueServiceBean;
import org.qualipso.factory.eventqueue.entity.EventQueue;
import org.qualipso.factory.eventqueue.entity.Rule;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;

import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * @author Yiqing LI
 * @author Marlène HANTZ
 * @author Nicolas HENRY
 * @author Philippe SCHMUCKER
 */
public class EventQueueRulesServiceTest extends BaseSessionBeanFixture<EventQueueServiceBean> {
    private static Log logger = LogFactory.getLog(EventQueueRulesServiceTest.class);

    private static final Class<?>[] usedBeans = { EventQueue.class, Rule.class };
    private Mockery mockery;
    private BindingService binding;
    private PEPService pep;
    private PAPService pap;
    private MembershipService membership;
    private SessionContext ctx;
    public final String caller = "/profiles/caller";

    public static int auto_ack = Session.AUTO_ACKNOWLEDGE;

    public EventQueueRulesServiceTest() {
        super(EventQueueServiceBean.class, usedBeans);
    }

    /**
     * Set up mock objects for unit tests
     */
    public void setUp() throws Exception {
        super.setUp();
        logger.debug("Session beans");
        mockery = new Mockery();
        binding = mockery.mock(BindingService.class);
        pep = mockery.mock(PEPService.class);
        pap = mockery.mock(PAPService.class);
        membership = mockery.mock(MembershipService.class);
        ctx = mockery.mock(SessionContext.class);
        getBeanToTest().setBindingService(binding);
        getBeanToTest().setMembershipService(membership);
        getBeanToTest().setPAPService(pap);
        getBeanToTest().setPEPService(pep);
        getBeanToTest().setSessionContext(ctx);
    }

    public void testList() throws Exception {
        logger.debug("testing testList(...)");
        getBeanToTest().getEntityManager().getTransaction().begin();
        EventQueueService service = getBeanToTest();
        Rule[] tab2 = service.list();
        assertEquals(tab2.length, 0);

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(equal("/li")), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.register("subjectre", "objectre", "targetre", "/li");

        tab2 = service.list();
        assertEquals(tab2.length, 1);
        assertEquals(tab2[0].getSubjectre(), "subjectre");
        assertEquals(tab2[0].getObjectre(), "objectre");
        assertEquals(tab2[0].getTargetre(), "targetre");
        assertEquals(tab2[0].getQueuePath(), "/li");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(equal("/li")), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.unregister("subjectre", "objectre", "targetre", "/li");

        tab2 = service.list();
        assertEquals(tab2.length, 0);
        mockery.assertIsSatisfied();
    }

    public void testList2() throws Exception {
        logger.debug("testing testList2(...)");
        EventQueueService service = getBeanToTest();
        Rule[] tab = service.listByRE("subjectre", "objectre", "targetre", "queuePath");
        assertEquals(tab.length, 0);

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(equal("queuePath")), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.register("subjectre", "objectre", "targetre", "queuePath");

        tab = service.listByRE("subjectre", "objectre", "targetre", "queuePath");
        assertEquals(tab.length, 1);
        assertEquals(tab[0].getSubjectre(), "subjectre");
        assertEquals(tab[0].getObjectre(), "objectre");
        assertEquals(tab[0].getTargetre(), "targetre");
        assertEquals(tab[0].getQueuePath(), "queuePath");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(equal("queuePath")), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.unregister("subjectre", "objectre", "targetre", "queuePath");

        tab = service.listByRE("subjectre", "objectre", "targetre", "queuePath");
        assertEquals(tab.length, 0);
        mockery.assertIsSatisfied();
    }

    public void testlistByQueue() throws Exception {
        logger.debug("testing testlistByQueue(...)");

        EventQueueService service = getBeanToTest();
        Rule[] tab = service.listByQueue("/li");
        assertEquals(tab.length, 0);

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(equal("/li")), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.register("subjectre", "objectre", "targetre", "/li");

        tab = service.listByQueue("/li");
        assertEquals(tab.length, 1);
        assertEquals(tab[0].getSubjectre(), "subjectre");
        assertEquals(tab[0].getObjectre(), "objectre");
        assertEquals(tab[0].getTargetre(), "targetre");
        assertEquals(tab[0].getQueuePath(), "/li");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(equal("/li")), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.unregister("subjectre", "objectre", "targetre", "/li");

        tab = service.listByQueue("/li");
        assertEquals(tab.length, 0);
        mockery.assertIsSatisfied();
    }

    public void testlistBySubjectRE() throws Exception {
        logger.debug("testing testlistBySubjectRE(...)");

        EventQueueService service = getBeanToTest();
        Rule[] tab = service.listBySubjectRE("subjectre");
        assertEquals(tab.length, 0);

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(equal("/li")), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.register("subjectre", "objectre", "targetre", "/li");

        tab = service.listBySubjectRE("subjectre");
        assertEquals(tab.length, 1);
        assertEquals(tab[0].getSubjectre(), "subjectre");
        assertEquals(tab[0].getObjectre(), "objectre");
        assertEquals(tab[0].getTargetre(), "targetre");
        assertEquals(tab[0].getQueuePath(), "/li");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(equal("/li")), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.unregister("subjectre", "objectre", "targetre", "/li");

        tab = service.listBySubjectRE("subjectre");
        assertEquals(tab.length, 0);
        mockery.assertIsSatisfied();
    }

    public void testlistByObjectRE() throws Exception {
        logger.debug("testing testlistByObjectRE(...)");

        EventQueueService service = getBeanToTest();
        Rule[] tab = service.listByObjectRE("objectre");
        assertEquals(tab.length, 0);

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(equal("/li")), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.register("subjectre", "objectre", "targetre", "/li");

        tab = service.listByObjectRE("objectre");
        assertEquals(tab.length, 1);
        assertEquals(tab[0].getSubjectre(), "subjectre");
        assertEquals(tab[0].getObjectre(), "objectre");
        assertEquals(tab[0].getTargetre(), "targetre");
        assertEquals(tab[0].getQueuePath(), "/li");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(equal("/li")), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.unregister("subjectre", "objectre", "targetre", "/li");

        tab = service.listByObjectRE("objectre");
        assertEquals(tab.length, 0);
        mockery.assertIsSatisfied();
    }

    public void testlistByTargetRE() throws Exception {
        logger.debug("testing testlistByTargetRE(...)");

        EventQueueService service = getBeanToTest();
        Rule[] tab = service.listByTargetRE("targetre");
        assertEquals(tab.length, 0);

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(equal("/li")), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.register("subjectre", "objectre", "targetre", "/li");

        tab = service.listByTargetRE("targetre");
        assertEquals(tab.length, 1);
        assertEquals(tab[0].getSubjectre(), "subjectre");
        assertEquals(tab[0].getObjectre(), "objectre");
        assertEquals(tab[0].getTargetre(), "targetre");
        assertEquals(tab[0].getQueuePath(), "/li");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(equal("/li")), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.unregister("subjectre", "objectre", "targetre", "/li");

        tab = service.listByTargetRE("targetre");
        assertEquals(tab.length, 0);
        mockery.assertIsSatisfied();
    }

}
