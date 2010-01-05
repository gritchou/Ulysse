package org.qualipso.factory.notification;

import java.io.Serializable;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.Depends;

/**
 * @author Jerome Blanchard <jayblanc@gmail.com>
 * @date 28 December 2009
 */
@MessageDriven(mappedName = "eventLoggerListener", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "topics/factoryNotification"),
        @ActivationConfigProperty(propertyName = "messagingType", propertyValue = "javax.jms.MessageListener") })
@Depends("jboss.mq.destination:service=Topic,name=factoryNotification")
public class EventLoggerListenerBean implements MessageListener {
	
	private static Log logger = LogFactory.getLog(EventLoggerListenerBean.class);
    
	@Override
    public void onMessage(Message message) {
        logger.info("onMessage() called");
        if (message instanceof ObjectMessage) {
        	try {
        		Serializable o = ((ObjectMessage) message).getObject();
        		if (o instanceof Event) {
        			Event ev = (Event) o;
        			logger.info(EventLoggerFormater.formatEvent(ev));
        		}
        	} catch ( JMSException e ) {
        		logger.error("unable to get event object from jms event");
        	}
        }
	}

}
