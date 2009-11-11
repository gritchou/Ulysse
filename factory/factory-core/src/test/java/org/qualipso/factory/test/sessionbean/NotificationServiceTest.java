package org.qualipso.factory.test.sessionbean;

import static org.qualipso.factory.test.jmock.action.SaveParamsAction.saveParams;

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
import org.qualipso.factory.binding.entity.Node;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.notification.NotificationServiceBean;
import org.qualipso.factory.notification.NotificationServiceException;
import org.qualipso.factory.notification.entity.Rule;

import com.bm.testsuite.BaseSessionBeanFixture;

public class NotificationServiceTest extends BaseSessionBeanFixture<NotificationServiceBean> {
    private static Log logger = LogFactory.getLog(NotificationServiceTest.class);

    @SuppressWarnings("unchecked")
    private static final Class[] usedBeans = { Rule.class, Node.class, Profile.class };

    private Mockery mockery;
    private EntityManager em;
    private Query query;
    private Connection connection;
    // @Resource(mappedName = "jms/ConnectionFactory")
    private ConnectionFactory connectionfactory;
    private Session session;
    private ObjectMessage om;
    private MessageProducer mp;
    private Queue queue;
    public static int auto_ack = Session.AUTO_ACKNOWLEDGE;

    public NotificationServiceTest() {
        super(NotificationServiceBean.class, usedBeans);
    }

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
        getBeanToTest().setEntityManager(em);
        NotificationServiceBean.setConnectionFactory(connectionfactory);
        NotificationServiceBean.setQueue(queue);
    }

    public void testRegistersubjectre() throws NotificationServiceException {
        logger.debug("testing testRegistersubjectre(...)");
        final Vector<Object> params1 = new Vector<Object>();
        mockery.checking(new Expectations() {
            {
                oneOf(em).persist(with(any(Rule.class)));
                will(saveParams(params1));
                // list
            }
        });

        NotificationService service = getBeanToTest();
        service.register("subjectre", "objectre", "targetre", "/li");
        assertEquals(((Rule) params1.get(0)).getSubjectre(), "subjectre");
        assertEquals(((Rule) params1.get(0)).getObjectre(), "objectre");
        assertEquals(((Rule) params1.get(0)).getTargetre(), "targetre");
        assertEquals(((Rule) params1.get(0)).getQueuePath(), "/li");
        // assertTrue(service.list().length==1);
        // assertTrue(service.list()[0].get....equals("subjectre"));
        mockery.assertIsSatisfied();
    }

    public void testRegistersubjectre2() {
        logger.debug("testing testRegistersubjectre2(...)");
        NotificationService service = getBeanToTest();
        try {
            service.register(null, "objectre", "targetre", "/li");
        } catch (NotificationServiceException e) {
        }
        // assertTrue(service.list().length==0);
    }

    public void testUnregister() throws NotificationServiceException {
        logger.debug("testing testunregister(...)");
        final Vector<Object> params1 = new Vector<Object>();
        mockery.checking(new Expectations() {
            {
                oneOf(em).persist(with(any(Rule.class)));
                will(saveParams(params1));
                oneOf(em).createQuery(with(any(String.class)));
                will(returnValue(query));
                oneOf(query).setParameter(with(equal("subjectre")), with(any(String.class)));
                oneOf(query).setParameter(with(equal("objectre")), with(any(String.class)));
                oneOf(query).setParameter(with(equal("targetre")), with(any(String.class)));
                oneOf(query).setParameter(with(equal("queuePath")), with(any(String.class)));
                oneOf(query).executeUpdate();
                will(returnValue(1));
                // list
            }
        });
        NotificationService service = getBeanToTest();
        service.register("subjectre", "objectre", "targetre", "/li");
        // assertTrue(service.list().length==1);
        service.unregister("subjectre", "objectre", "targetre", "/li");
        // assertTrue(service.list().length==0);
    }

    public void testUnregister2() {
        // TODO sans register
    }

    public void testthrowEvent() throws NotificationServiceException, JMSException {
        logger.debug("testing testunregister(...)");
        final Vector<Object> params1 = new Vector<Object>();
        mockery.checking(new Expectations() {
            {
                oneOf(em).persist(with(any(Rule.class)));
                will(saveParams(params1));
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
        NotificationService service = getBeanToTest();
        // créer une queue (mock) (penser à le faire pour les autres tests)
        service.register("subjectre", "objectre", "targetre", "/li");
        // penser a faire un event qui matche la regle de la queue
        Event e = new Event("fromRessource", "ressourceType", "eventType", "");
        service.throwEvent(e);
        // récupérer l'event dans la queue
    }
}
