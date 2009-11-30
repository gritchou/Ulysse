package org.qualipso.factory.notification;

import java.io.Serializable;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.Depends;
import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.eventqueue.EventQueueServiceException;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.notification.entity.Rule;

/**
 * The message driven bean which distributes events to the event queues
 * 
 * @author Nicolas HENRY
 * @author Marlène HANTZ
 */
@MessageDriven(mappedName = "queue/EventMessageQueue", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/EventMessageQueue"),
        @ActivationConfigProperty(propertyName = "messagingType", propertyValue = "javax.jms.MessageListener") })
@Depends("jboss.mq.destination:service=Queue,name=eventmessage")
public class EventMessageBean implements MessageListener {

    private static Log logger = LogFactory.getLog(EventMessageBean.class);
    private EventQueueService eventQueue;
    private NotificationService notification;

    public EventMessageBean() {
    }

    @EJB
    public void setEventQueueService(EventQueueService eventQueue) {
        this.eventQueue = eventQueue;
    }

    public EventQueueService getEventQueueService() {
        return this.eventQueue;
    }

    @EJB
    public void setNotificationService(NotificationService notification) {
        this.notification = notification;
    }

    public NotificationService getNotificationService() {
        return this.notification;
    }

    @Override
    public void onMessage(Message message) {
    	logger.info("onMessage() called");
        if (message instanceof ObjectMessage) {
            try {
                Serializable o = ((ObjectMessage) message).getObject();
                if (o instanceof Event) {
                    Event ev = (Event) o;
                    try {
                        Rule[] l = notification.list();
                        for (Rule rule : l) {
                            if (rule.match(ev)) {
                                try {
                                    eventQueue.pushEvent(rule.getQueuePath(), ev);
                                } catch (EventQueueServiceException e) {
                                    logger.error("unable to push event : " + ev + "\nin queue : " + rule.getQueuePath(), e);
                                }
                            }
                        }
                    } catch (NotificationServiceException e1) {
                        logger.error("unable to get rules from notification service");
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
