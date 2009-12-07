package org.qualipso.factory.notification;

import java.io.Serializable;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.xml.ws.Binding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.Depends;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.binding.PropertyNotFoundException;
import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.eventqueue.EventQueueServiceException;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.entity.Rule;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.security.pep.PEPServiceException;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;

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
    private PEPService pep;
    private MembershipService membership;
    private BindingService binding;

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
    
    @EJB
    public void setPEPService(PEPService pep) {
        this.pep = pep;
    }

    public PEPService getPEPService() {
        return this.pep;
    }    

	@EJB
	public void setMembershipService(MembershipService membership) {
		this.membership = membership;
	}

	public MembershipService getMembershipService() {
		return this.membership;
	}
	
	@EJB
	public void setBindingService(BindingService binding) {
		this.binding = binding;
	}

	public BindingService getBindingService() {
		return binding;
	}

	
    @Override
    public void onMessage(Message message){
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
                                	String owner = binding.getProperty(rule.getQueuePath(),FactoryResourceProperty.OWNER, false);
                                	pep.checkSecurity(owner,rule.getQueuePath() , "read");
                                	
                                    eventQueue.pushEvent(rule.getQueuePath(), ev);
                                } catch (EventQueueServiceException e) {
                                    logger.error("unable to push event : " + ev + "\nin queue : " + rule.getQueuePath(), e);
                                } catch (PEPServiceException e) {
                                	logger.error("acces deny to push event : " + ev + "\nin queue : " + rule.getQueuePath(), e);
									e.printStackTrace();
								} catch (InvalidPathException e) {
									logger.error("Invalid path "+rule.getQueuePath()+" "+e);
									e.printStackTrace();
								} catch (PathNotFoundException e) {
									logger.error("Invalid path "+rule.getQueuePath()+" "+e);
									e.printStackTrace();
								} catch (PropertyNotFoundException e) {
									logger.error("property "+FactoryResourceProperty.OWNER+" not found "+e);
									e.printStackTrace();
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
