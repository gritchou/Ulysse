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
import org.qualipso.factory.eventqueue.EventQueueServiceException;
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

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
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

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.unregister("subjectre", "objectre", "targetre", "/li");

        tab2 = service.list();
        assertEquals(tab2.length, 0);
        mockery.assertIsSatisfied();
    }

    public void testRegisterNullParameter() throws Exception {
        logger.debug("testing testRegisterNullParameter(...)");
        EventQueueService service = getBeanToTest();
        
        final Sequence sequence1 = mockery.sequence("sequence1");
        
        try {
            mockery.checking(new Expectations() {
                {
                    oneOf(membership).getProfilePathForConnectedIdentifier();
                    will(returnValue(caller));
                    inSequence(sequence1);

                    oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                    inSequence(sequence1);
                }
            });
            service.register(null, "objectre", "targetre", "/li");
        } catch (EventQueueServiceException e) {
        }
        try {
            mockery.checking(new Expectations() {
                {
                    oneOf(membership).getProfilePathForConnectedIdentifier();
                    will(returnValue(caller));
                    inSequence(sequence1);

                    oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                    inSequence(sequence1);
                }
            });
            service.register("subjectre", null, "targetre", "/li");
        } catch (EventQueueServiceException e) {
        }
        try {
            mockery.checking(new Expectations() {
                {
                    oneOf(membership).getProfilePathForConnectedIdentifier();
                    will(returnValue(caller));
                    inSequence(sequence1);

                    oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                    inSequence(sequence1);
                }
            });
            service.register("subjectre", "objectre", null, "/li");
        } catch (EventQueueServiceException e) {
        }
        try {
            mockery.checking(new Expectations() {
                {
                    oneOf(membership).getProfilePathForConnectedIdentifier();
                    will(returnValue(caller));
                    inSequence(sequence1);

                    oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                    inSequence(sequence1);
                }
            });
            service.register("subjectre", "objectre", "targetre", null);
        } catch (EventQueueServiceException e) {
        }
        Rule[] tab2 = service.list();
        assertEquals(tab2.length, 0);
        mockery.assertIsSatisfied();
    }

    public void testUnregisterNullParameter() throws Exception {
        logger.debug("testing testUnregisterNullParameter(...)");
        EventQueueService service = getBeanToTest();
        
        final Sequence sequence1 = mockery.sequence("sequence1");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subjectre", "objectre", "targetre", "/li");
        try {
            mockery.checking(new Expectations() {
                {
                    oneOf(membership).getProfilePathForConnectedIdentifier();
                    will(returnValue(caller));
                    inSequence(sequence1);

                    oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                    inSequence(sequence1);
                }
            });
            service.unregister(null, "objectre", "targetre", "/li");
        } catch (EventQueueServiceException e) {
        }
        try {
            mockery.checking(new Expectations() {
                {
                    oneOf(membership).getProfilePathForConnectedIdentifier();
                    will(returnValue(caller));
                    inSequence(sequence1);

                    oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                    inSequence(sequence1);
                }
            });
            service.unregister("subjectre", null, "targetre", "/li");
        } catch (EventQueueServiceException e) {
        }
        try {
            mockery.checking(new Expectations() {
                {
                    oneOf(membership).getProfilePathForConnectedIdentifier();
                    will(returnValue(caller));
                    inSequence(sequence1);

                    oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                    inSequence(sequence1);
                }
            });
            service.unregister("subjectre", "objectre", null, "/li");
        } catch (EventQueueServiceException e) {
        }
        try {
            mockery.checking(new Expectations() {
                {
                    oneOf(membership).getProfilePathForConnectedIdentifier();
                    will(returnValue(caller));
                    inSequence(sequence1);

                    oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                    inSequence(sequence1);
                }
            });
            service.unregister("subjectre", "objectre", "targetre", null);
        } catch (EventQueueServiceException e) {
        }
        Rule[] tab = service.list();
        assertEquals(tab.length, 1);
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("subjectre", "objectre", "targetre", "/li");
        mockery.assertIsSatisfied();
    }

    public void testlistByRE() throws Exception {
        logger.debug("testing testlistByRE(...)");
        EventQueueService service = getBeanToTest();
        Rule[] tab = service.listByRE("subjectre", null, "targetre", "queuePath");
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
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.register("subj.*", "objectre", "targetre", "queuePath");

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
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.unregister("subj.*", "objectre", "targetre", "queuePath");
        mockery.assertIsSatisfied();
    }

    public void testlistByRENullParameter() throws Exception {
        logger.debug("testing testlistByRENullParameter(...)");
        EventQueueService service = getBeanToTest();

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subjectre", "objectre", "targetre", "/li");
        try {
            service.listByRE(null, null, null, null);
        } catch (EventQueueServiceException e) {

        }
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("subjectre", "objectre", "targetre", "/li");
        mockery.assertIsSatisfied();
    }

    public void testlistBy() throws Exception {
        logger.debug("testing testlistBy(...)");
        EventQueueService service = getBeanToTest();
        Rule[] tab = service.listBy("subjectre", "objectre", "targetre", "queuePath");
        assertEquals(tab.length, 0);

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.register("subjectre", "objectre", "targetre", "queuePath");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subj.*", ".*tre", "targetre", "queuePath");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("t.*", "obje.*", "targetre", "queuePath");

        tab = service.listBy("subjectre", "objectre", "targetre", "queuePath");
        assertEquals(tab.length, 2);
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.unregister("subjectre", "objectre", "targetre", "queuePath");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.unregister("subj.*", ".*tre", "targetre", "queuePath");

        tab = service.listBy("subjectre", "objectre", "targetre", "queuePath");
        assertEquals(tab.length, 0);
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.unregister("t.*", "obje.*", "targetre", "queuePath");
        mockery.assertIsSatisfied();
    }

    public void testlistByNullParameter() throws Exception {
        logger.debug("testing testlistByQueueNullParameter(...)");
        EventQueueService service = getBeanToTest();

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subjectre", "objectre", "targetre", "/li");
        try {
            service.listBy(null, null, null, null);
        } catch (EventQueueServiceException e) {

        }
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("subjectre", "objectre", "targetre", "/li");
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

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.register("subjectre", "objectre", "targetre", "/li");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subjectre", "objectre", "targetre", "queuePath");

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

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.unregister("subjectre", "objectre", "targetre", "/li");

        tab = service.listByQueue("/li");
        assertEquals(tab.length, 0);
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("subjectre", "objectre", "targetre", "queuePath");
        mockery.assertIsSatisfied();
    }

    public void testlistByQueueNullParameter() throws Exception {
        logger.debug("testing testlistByQueueNullParameter(...)");
        EventQueueService service = getBeanToTest();

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subjectre", "objectre", "targetre", "/li");
        try {
            service.listByQueue(null);
        } catch (EventQueueServiceException e) {

        }
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("subjectre", "objectre", "targetre", "/li");
        mockery.assertIsSatisfied();
    }

    public void testlistByQueueRE() throws Exception {
        logger.debug("testing testlistByQueueRE(...)");

        EventQueueService service = getBeanToTest();
        Rule[] tab = service.listByQueueRE("/li");
        assertEquals(tab.length, 0);

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.register("subjectre", "objectre", "targetre", "/li");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subj.*", ".*tre", "targetre", "/l.*");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subj.*", ".*tre", "targetre", "/l");

        tab = service.listByQueueRE("/li");
        assertEquals(tab.length, 1);
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.unregister("subjectre", "objectre", "targetre", "/li");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.unregister("subj.*", ".*tre", "targetre", "/l.*");
        tab = service.listByQueueRE("/li");
        assertEquals(tab.length, 0);
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("subj.*", ".*tre", "targetre", "/l");
        mockery.assertIsSatisfied();
    }

    public void testlistByQueueRENullParameter() throws Exception {
        logger.debug("testing testlistByQueueRENullParameter(...)");
        EventQueueService service = getBeanToTest();

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subjectre", "objectre", "targetre", "/li");
        try {
            service.listByQueueRE(null);
        } catch (EventQueueServiceException e) {

        }
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("subjectre", "objectre", "targetre", "/li");
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

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.register("subjectre", "objectre", "targetre", "/li");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subject", "objectre", "targetre", "/li");

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

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.unregister("subjectre", "objectre", "targetre", "/li");

        tab = service.listBySubjectRE("subjectre");
        assertEquals(tab.length, 0);
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("subject", "objectre", "targetre", "/li");
        mockery.assertIsSatisfied();
    }

    public void testlistBySubjectRENullParameter() throws Exception {
        logger.debug("testing testlistBySubjectRENullParameter(...)");
        EventQueueService service = getBeanToTest();

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subjectre", "objectre", "targetre", "/li");
        try {
            service.listBySubjectRE(null);
        } catch (EventQueueServiceException e) {

        }
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("subjectre", "objectre", "targetre", "/li");
        mockery.assertIsSatisfied();
    }

    public void testlistBySubject() throws Exception {
        logger.debug("testing testlistBySubject(...)");

        final Sequence sequence1 = mockery.sequence("sequence1");

        EventQueueService service = getBeanToTest();
        Rule[] tab = service.listBySubject("subjectre");
        assertEquals(tab.length, 0);
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.register("subjectre", "objectre", "targetre", "/li");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subj.*", ".*tre", "targetre", "/l");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("t.*", "obje.*", "targetre", "queuePath");

        tab = service.listBySubject("subjectre");
        assertEquals(tab.length, 2);
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.unregister("subjectre", "objectre", "targetre", "/li");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.unregister("subj.*", ".*tre", "targetre", "/l");
        tab = service.listBySubject("subjectre");
        assertEquals(tab.length, 0);
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("t.*", "obje.*", "targetre", "queuePath");
        mockery.assertIsSatisfied();
    }

    public void testlistBySubjectNullParameter() throws Exception {
        logger.debug("testing testlistBySubjectNullParameter(...)");
        EventQueueService service = getBeanToTest();

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subjectre", "objectre", "targetre", "/li");
        try {
            service.listBySubject(null);
        } catch (EventQueueServiceException e) {

        }
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("subjectre", "objectre", "targetre", "/li");
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

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.register("subjectre", "objectre", "targetre", "/li");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subjectre", "object", "targetre", "/li");

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

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.unregister("subjectre", "objectre", "targetre", "/li");

        tab = service.listByObjectRE("objectre");
        assertEquals(tab.length, 0);
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("subjectre", "object", "targetre", "/li");
        mockery.assertIsSatisfied();
    }

    public void testlistByObjectRENullParameter() throws Exception {
        logger.debug("testing testlistByObjectRENullParameter(...)");
        EventQueueService service = getBeanToTest();

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subjectre", "objectre", "targetre", "/li");
        try {
            service.listByObjectRE(null);
        } catch (EventQueueServiceException e) {

        }
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("subjectre", "objectre", "targetre", "/li");
        mockery.assertIsSatisfied();
    }

    public void testlistByObject() throws Exception {
        logger.debug("testing testlistByObject(...)");

        EventQueueService service = getBeanToTest();
        Rule[] tab = service.listByObject("objectre");
        assertEquals(tab.length, 0);

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.register("subjectre", "objectre", "targetre", "/li");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subj.*", ".*tre", "targetre", "/l");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("t.*", "t.*", "targetre", "/li");

        tab = service.listByObject("objectre");
        assertEquals(tab.length, 2);
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.unregister("subjectre", "objectre", "targetre", "/li");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.unregister("subj.*", ".*tre", "targetre", "/l");
        tab = service.listByObject("objectre");
        assertEquals(tab.length, 0);
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("t.*", "t.*", "targetre", "/li");
        mockery.assertIsSatisfied();
    }

    public void testlistByObjectNullParameter() throws Exception {
        logger.debug("testing testlistByObjectNullParameter(...)");
        EventQueueService service = getBeanToTest();

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subjectre", "objectre", "targetre", "/li");
        try {
            service.listByObject(null);
        } catch (EventQueueServiceException e) {

        }
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("subjectre", "objectre", "targetre", "/li");
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

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.register("subjectre", "objectre", "targetre", "/li");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subjectre", "objectre", "target", "/li");

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

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.unregister("subjectre", "objectre", "targetre", "/li");

        tab = service.listByTargetRE("targetre");
        assertEquals(tab.length, 0);
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("subjectre", "objectre", "target", "/li");
        mockery.assertIsSatisfied();
    }

    public void testlistByTargetRENullParameter() throws Exception {
        logger.debug("testing testlistByTargetRENullParameter(...)");
        EventQueueService service = getBeanToTest();

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subjectre", "objectre", "targetre", "/li");
        try {
            service.listByTargetRE(null);
        } catch (EventQueueServiceException e) {

        }
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("subjectre", "objectre", "targetre", "/li");
        mockery.assertIsSatisfied();
    }

    public void testlistByTarget() throws Exception {
        logger.debug("testing testlistByTarget(...)");

        final Sequence sequence1 = mockery.sequence("sequence1");

        EventQueueService service = getBeanToTest();
        Rule[] tab = service.listByTarget("targetre");
        assertEquals(tab.length, 0);
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        service.register("subjectre", "objectre", "targetre", "/li");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subj.*", ".*tre", "tar.*", "/l");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("t.*", "t.*", "re.*", "/li");

        tab = service.listByTarget("targetre");
        assertEquals(tab.length, 2);

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.unregister("subjectre", "objectre", "targetre", "/li");
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.unregister("subj.*", ".*tre", "tar.*", "/l");
        tab = service.listByTarget("targetre");
        assertEquals(tab.length, 0);
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });

        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("t.*", "t.*", "re.*", "/li");
        mockery.assertIsSatisfied();
    }

    public void testlistByTargetNullParameter() throws Exception {
        logger.debug("testing testlistByTargetNullParameter(...)");
        EventQueueService service = getBeanToTest();

        final Sequence sequence1 = mockery.sequence("sequence1");

        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        service.register("subjectre", "objectre", "targetre", "/li");
        try {
            service.listByTarget(null);
        } catch (EventQueueServiceException e) {

        }
        
        mockery.checking(new Expectations() {
            {
                oneOf(membership).getProfilePathForConnectedIdentifier();
                will(returnValue(caller));
                inSequence(sequence1);

                oneOf(pep).checkSecurity(with(equal(caller)), with(any(String.class)), with(equal("update")));
                inSequence(sequence1);
            }
        });
        
        // Pour tester les autres méthode à partir d'une base vide
        service.unregister("subjectre", "objectre", "targetre", "/li");
        mockery.assertIsSatisfied();
    }

}
