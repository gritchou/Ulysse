package org.qualipso.factory.notification;

import java.io.Serializable;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.Depends;
import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.eventqueue.EventQueueServiceException;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.notification.entity.Rule;

@MessageDriven(mappedName = "jms/EventMessageQueue", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/EventMessageQueue"),
    @ActivationConfigProperty(propertyName = "messagingType", propertyValue = "javax.jms.MessageListener") })
@Depends ("jboss.mq.destination:service=Queue,name=eventmessage")
public class EventMessageBean implements MessageListener {
    
    private static Log logger = LogFactory.getLog(EventMessageBean.class);
    private EntityManager em;
    private EventQueueService eventQueue;
    
    public EventMessageBean() {
    }
	
    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return this.em;
    }
    
    @EJB
    public void setEventQueueService(EventQueueService eventQueue) {
        this.eventQueue = eventQueue;
    }

    public EventQueueService getEventQueueService() {
        return this.eventQueue;
    }
    
    @SuppressWarnings("unchecked")
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            try {
                Serializable o = ((ObjectMessage) message).getObject();
                if (o instanceof Event) {
                    Event ev = (Event) o;
                    Query q = em.createQuery("select * from Rule");
                    List<Rule> l = q.getResultList();
                    for (Rule rule : l) {
                        if (rule.match(ev)) {
                            try {
                                eventQueue.pushEvent(rule.getQueuePath(), ev);
                            } catch (EventQueueServiceException e) {
                                logger.error("unable to push event : " + ev + "\nin queue : " + rule.getQueuePath(), e);
                            }
                        }
                    }
                } else {
                    logger.warn("bad object message type : " + o + "\nin message : " + message);
                }
            } catch (JMSException e) {
                logger.error("unable to retrieve jms message object", e);
            }
        } else {
            logger.warn("bad message type : " + message);
        }
    }

}
