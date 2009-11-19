package org.qualipso.factory.test.sessionbean;

import static org.qualipso.factory.test.jmock.action.SaveParamsAction.saveParams;

import java.util.List;
import java.util.Vector;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.qualipso.factory.binding.BindingServiceException;
import org.qualipso.factory.binding.entity.Node;
import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.eventqueue.EventQueueServiceException;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.notification.NotificationServiceBean;
import org.qualipso.factory.notification.NotificationServiceException;
import org.qualipso.factory.notification.entity.Rule;
import org.qualipso.factory.security.pap.PAPServiceException;
import org.qualipso.factory.security.pep.PEPServiceException;

import com.bm.testsuite.BaseSessionBeanFixture;

public class NotificationServiceTest extends BaseSessionBeanFixture<NotificationServiceBean> {
    private static Log logger = LogFactory.getLog(NotificationServiceTest.class);

    @SuppressWarnings("unchecked")
    private static final Class[] usedBeans = { Rule.class, Node.class, Profile.class };

    private Mockery mockery;
    private EntityManager em;
    private Query query;
    private Connection connection;
    private ConnectionFactory connectionfactory;
    private Session session;
    private ObjectMessage om;
    private MessageProducer mp;
    private Queue queue;
    private List<Rule> list;
    private EventQueueService eventqueueservice;
   
    public static int auto_ack = Session.AUTO_ACKNOWLEDGE;

    public NotificationServiceTest() {
        super(NotificationServiceBean.class, usedBeans);
    }

    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();
        logger.debug("Session beans");
        mockery = new Mockery();
        em = mockery.mock(EntityManager.class);
        query = mockery.mock(Query.class);
        connection = mockery.mock(Connection.class);
        connectionfactory = mockery.mock(ConnectionFactory.class);
        session = mockery.mock(Session.class);
        om = mockery.mock(ObjectMessage.class);
        mp = mockery.mock(MessageProducer.class);
        queue = mockery.mock(Queue.class);
        list = mockery.mock(List.class);
        eventqueueservice = mockery.mock(EventQueueService.class);
        getBeanToTest().setEntityManager(em);
        NotificationServiceBean.setConnectionFactory(connectionfactory);
        NotificationServiceBean.setQueue(queue);
    }

    public void testList() throws NotificationServiceException {
        logger.debug("testing testList(...)");
        final Rule[] tab = new Rule[] {};
        mockery.checking(new Expectations() {
            {
                oneOf(em).createQuery(with(any(String.class)));
                will(returnValue(query));
                oneOf(query).getResultList();
                will(returnValue(list));
                oneOf(list).size();
                oneOf(list).toArray(with(any(Rule[].class)));
                will(returnValue(tab));
            }
        });
        NotificationService service = getBeanToTest();
        Rule[] tab2 = service.list();
        assertEquals(tab2.length, 0);

        final Vector<Object> params1 = new Vector<Object>();
        mockery.checking(new Expectations() {
            {
                oneOf(em).createQuery(with(any(String.class)));
                will(returnValue(query));
                oneOf(query).getResultList();
                will(returnValue(list));
                oneOf(list).size();
                oneOf(list).toArray(with(any(Rule[].class)));
                will(returnValue(tab));
                oneOf(em).persist(with(any(Rule.class)));
                will(saveParams(params1));
            }
        });
        service.register("subjectre", "objectre", "targetre", "/li");

        final Rule[] tab1 = new Rule[] { (Rule) params1.get(0) };
        mockery.checking(new Expectations() {
            {
                oneOf(em).createQuery(with(any(String.class)));
                will(returnValue(query));
                oneOf(query).getResultList();
                will(returnValue(list));
                oneOf(list).size();
                oneOf(list).toArray(with(any(Rule[].class)));
                will(returnValue(tab1));
            }
        });
        tab2 = service.list();
        assertEquals(tab2.length, 1);
        assertEquals(tab2[0].getSubjectre(), "subjectre");
        assertEquals(tab2[0].getObjectre(), "objectre");
        assertEquals(tab2[0].getTargetre(), "targetre");
        assertEquals(tab2[0].getQueuePath(), "/li");

        mockery.checking(new Expectations() {
            {
                oneOf(em).createQuery(with(any(String.class)));
                will(returnValue(query));
                oneOf(query).setParameter(with(equal("subjectre")), with(any(String.class)));
                oneOf(query).setParameter(with(equal("objectre")), with(any(String.class)));
                oneOf(query).setParameter(with(equal("targetre")), with(any(String.class)));
                oneOf(query).setParameter(with(equal("queuePath")), with(any(String.class)));
                oneOf(query).executeUpdate();
                will(returnValue(1));
            }
        });
        service.unregister("subjectre", "objectre", "targetre", "/li");

        mockery.checking(new Expectations() {
            {
                oneOf(em).createQuery(with(any(String.class)));
                will(returnValue(query));
                oneOf(query).getResultList();
                will(returnValue(list));
                oneOf(list).size();
                oneOf(list).toArray(with(any(Rule[].class)));
                will(returnValue(tab));
            }
        });
        tab2 = service.list();
        assertEquals(tab2.length, 0);
        mockery.assertIsSatisfied();
    }

    public void testRegister() throws NotificationServiceException {
        logger.debug("testing testRegister(...)");
        final Rule[] tab = new Rule[] {};
        final Vector<Object> params1 = new Vector<Object>();
        mockery.checking(new Expectations() {
            {
                oneOf(em).createQuery(with(any(String.class)));
                will(returnValue(query));
                oneOf(query).getResultList();
                will(returnValue(list));
                oneOf(list).size();
                oneOf(list).toArray(with(any(Rule[].class)));
                will(returnValue(tab));
                oneOf(em).persist(with(any(Rule.class)));
                will(saveParams(params1));
            }
        });
        NotificationService service = getBeanToTest();
        service.register("subjectre", "objectre", "targetre", "/li");
        assertEquals(((Rule) params1.get(0)).getSubjectre(), "subjectre");
        assertEquals(((Rule) params1.get(0)).getObjectre(), "objectre");
        assertEquals(((Rule) params1.get(0)).getTargetre(), "targetre");
        assertEquals(((Rule) params1.get(0)).getQueuePath(), "/li");
        mockery.assertIsSatisfied();
    }

    public void testRegister2() throws NotificationServiceException {
        logger.debug("testing testRegister2(...)");
        final Rule[] tab = new Rule[] {};       
        try {
            NotificationService service = getBeanToTest();
            service.register(null, "objectre", "targetre", "/li");
            service.register("subjectre", null, "targetre", "/li");
            service.register("subjectre", "objectre", null, "/li");
            service.register("subjectre", "objectre", "targetre", null);
        } catch (NotificationServiceException e) {

        }

        mockery.checking(new Expectations() {
            {
                oneOf(em).createQuery(with(any(String.class)));
                will(returnValue(query));
                oneOf(query).getResultList();
                will(returnValue(list));
                oneOf(list).size();
                oneOf(list).toArray(with(any(Rule[].class)));
                will(returnValue(tab));
            }
        });
        NotificationService service = getBeanToTest();
        Rule[] tab2 = service.list();
        assertEquals(tab2.length, 0);
        mockery.assertIsSatisfied();
    }

    public void testUnregister() throws NotificationServiceException {
        logger.debug("testing testunregister(...)");
        final Rule[] tab = new Rule[] {};
        mockery.checking(new Expectations() {
            {
                oneOf(em).createQuery(with(any(String.class)));
                will(returnValue(query));
                oneOf(query).getResultList();
                will(returnValue(list));
                oneOf(list).size();
                oneOf(list).toArray(with(any(Rule[].class)));
                will(returnValue(tab));
                oneOf(em).persist(with(any(Rule.class)));

            }
        });
        NotificationService service = getBeanToTest();
        service.register("subjectre", "objectre", "targetre", "/li");

        mockery.checking(new Expectations() {
            {
                oneOf(em).createQuery(with(any(String.class)));
                will(returnValue(query));
                oneOf(query).setParameter(with(equal("subjectre")), with(any(String.class)));
                oneOf(query).setParameter(with(equal("objectre")), with(any(String.class)));
                oneOf(query).setParameter(with(equal("targetre")), with(any(String.class)));
                oneOf(query).setParameter(with(equal("queuePath")), with(any(String.class)));
                oneOf(query).executeUpdate();
                will(returnValue(1));
            }
        });
        service.unregister("subjectre", "objectre", "targetre", "/li");

        mockery.checking(new Expectations() {
            {
                oneOf(em).createQuery(with(any(String.class)));
                will(returnValue(query));
                oneOf(query).getResultList();
                will(returnValue(list));
                oneOf(list).size();
                oneOf(list).toArray(with(any(Rule[].class)));
                will(returnValue(tab));
            }
        });
        Rule[] tab1 = service.list();
        assertEquals(tab1.length, 0);
        mockery.assertIsSatisfied();
    }

    public void testUnregister2() throws NotificationServiceException {
        final Rule[] tab = new Rule[] {};
        mockery.checking(new Expectations() {
            {
                oneOf(em).createQuery(with(any(String.class)));
                will(returnValue(query));
                oneOf(query).getResultList();
                will(returnValue(list));
                oneOf(list).size();
                oneOf(list).toArray(with(any(Rule[].class)));
                will(returnValue(tab));
                oneOf(em).persist(with(any(Rule.class)));

            }
        });
        NotificationService service = getBeanToTest();
        service.register("subjectre", "objectre", "targetre", "/li");
        try {
            service.unregister(null, "objectre", "targetre", "/li");
            service.unregister("subjectre", null, "targetre", "/li");
            service.unregister("subjectre", "objectre", null, "/li");
            service.unregister("subjectre", "objectre", "targetre", null);
        } catch (NotificationServiceException e) {

        }
        mockery.assertIsSatisfied();
    }

   
    public void testthrowEvent() throws NotificationServiceException, JMSException, EventQueueServiceException, MembershipServiceException, PEPServiceException, BindingServiceException, PAPServiceException {
        logger.debug("testing testunregister(...)");
        final Rule[] tab = new Rule[] {};
        final Vector<Object> params1 = new Vector<Object>();
        mockery.checking(new Expectations() {
            {
                oneOf(em).createQuery(with(any(String.class)));
                will(returnValue(query));
                oneOf(query).getResultList();
                will(returnValue(list));
                oneOf(list).size();
                oneOf(list).toArray(with(any(Rule[].class)));
                will(returnValue(tab));
                oneOf(em).persist(with(any(Rule.class)));
                will(saveParams(params1));
            }
        });
        NotificationService service = getBeanToTest();
        service.register("subjectre", "objectre", "targetre", "/li");
        
        // créer une queue (mock) (penser à le faire pour les autres tests)
        mockery.checking(new Expectations() {
            {
                oneOf(eventqueueservice).createEventQueue(with(any(String.class))); 
                
            }
        });
        eventqueueservice.createEventQueue("/rules/li");
        
        mockery.checking(new Expectations() {
            {
                oneOf(connectionfactory).createConnection();
                will(returnValue(connection));
                oneOf(connection).createSession(with(equal(false)), with(equal(auto_ack)));
                will(returnValue(session));
                oneOf(session).createObjectMessage();
                will(returnValue(om));
                oneOf(om).setObject(with(any(Event.class)));
                oneOf(session).createProducer(with(any(Queue.class)));
                will(returnValue(mp));
                oneOf(mp).send(om);
            }
        });
        Event e = new Event("fromRessource", "ressourceType", "eventType", "");
        service.throwEvent(e);
        // penser a faire un event qui matche la regle de la queue
        final Event[] event = new Event[]{e};
        mockery.checking(new Expectations() {
            {
                oneOf(eventqueueservice).getEvents(with(any(String.class))); 
                will(returnValue(event));
            }
        });
        Event[] ev = eventqueueservice.getEvents("/queues/rules/li");
        assertEquals(ev[0].getFromResource(),"fromRessource");
        assertEquals(ev[0].getResourceType(),"ressourceType");
        assertEquals(ev[0].getEventType(),"eventType");
        assertEquals(ev[0].getArgs(),"");
        mockery.assertIsSatisfied();
    }
}
