package org.qualipso.factory.test.sessionbean;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.qualipso.factory.binding.entity.Node;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.eventqueue.entity.Rule;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.notification.NotificationServiceBean;
import org.qualipso.factory.notification.NotificationServiceException;

import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * @author Yiqing LI
 * @author Marlène HANTZ
 * @author Nicolas HENRY
 * @author Philippe SCHMUCKER
 */
public class NotificationServiceTest extends BaseSessionBeanFixture<NotificationServiceBean> {
    private static Log logger = LogFactory.getLog(NotificationServiceTest.class);

    @SuppressWarnings("unchecked")
    private static final Class[] usedBeans = { Rule.class, Node.class, Profile.class };

    private Mockery mockery;
    private Connection connection;
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
        connection = mockery.mock(Connection.class);
        connectionfactory = mockery.mock(ConnectionFactory.class);
        session = mockery.mock(Session.class);
        om = mockery.mock(ObjectMessage.class);
        mp = mockery.mock(MessageProducer.class);

        NotificationServiceBean.setConnectionFactory(connectionfactory);
        NotificationServiceBean.setQueue(queue);
    }

    public void testthrowEvent() throws NotificationServiceException, JMSException {
        logger.debug("testing testthrowEvent(...)");

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
                oneOf(mp).close();
                oneOf(session).close();
                oneOf(connection).close();
            }
        });
        Event e = new Event("fromRessource", "ressourceType", "eventType", "");
        getBeanToTest().throwEvent(e);
        mockery.assertIsSatisfied();
    }

}
