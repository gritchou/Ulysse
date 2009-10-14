package org.qualipso.factory.notification;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 16 june 2009
 */
@Stateless(name = "Notification", mappedName = "NotificationService")
@SecurityDomain(value = "JBossWSDigest")
public class NotificationServiceBean implements NotificationService {
	
    private static Log logger = LogFactory.getLog(NotificationServiceBean.class);
    
    public NotificationServiceBean() {
	}

	@Override
	public void throwEvent(Event event) throws NotificationServiceException {
		//TODO
		logger.warn("throwEvent(...) called ### NOT IMPLEMENTED");
		logger.debug("params : event=\r\n" + event);
	}
	
}
