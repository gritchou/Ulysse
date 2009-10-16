/*
 * Qualipso Funky Factory
 * Copyright (C) 2006-2010 INRIA
 * http://www.inria.fr - molli@loria.fr
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation. See the GNU
 * Lesser General Public License in LGPL.txt for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * Initial authors :
 *
 * Jérôme Blanchard / INRIA
 * Christophe Bouthier / INRIA
 * Pascal Molli / Nancy Université
 * Gérald Oster / Nancy Université
 */
package org.qualipso.factory.notification;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.notification.Event;



@Stateless(name = "Notification", mappedName = "NotificationService")
@WebService(endpointInterface = "org.qualipso.factory.service.notification.NotificationService", targetNamespace = "http://org.qualipso.funkyfactory.ws/notification", serviceName = "NotificationService", portName = "NotificationService")
@WebContext(contextRoot = "/factory-service-notification", urlPattern = "/notification")
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")

public class NotificationServiceBean implements NotificationService{

	private static Log logger = LogFactory.getLog(NotificationServiceBean.class);

	@Override
	public FactoryResource findResource(String arg0) throws FactoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getResourceTypeList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(String subjectre, String objectre, String targetre,
			String queuePath) throws NotificationServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void throwEvent(Event e) throws NotificationServiceException {
		// TODO Auto-generated method stub
		
	}


}
