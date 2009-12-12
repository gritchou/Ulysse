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

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.eventqueue.entity.Event;

/**
 * @author Nicolas HENRY
 * @author Marlène HANTZ
 * @author Jean-François GRAND
 * @author Yiqing LI
 * @author Philippe SCHMUCKER
 */
@Stateless(name = NotificationService.SERVICE_NAME, mappedName = FactoryNamingConvention.SERVICE_PREFIX + NotificationService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.notification.NotificationService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE
        + NotificationService.SERVICE_NAME, serviceName = NotificationService.SERVICE_NAME)
@WebContext(contextRoot = FactoryNamingConvention.WEB_SERVICE_CORE_MODULE_CONTEXT, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX
        + NotificationService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class NotificationServiceBean implements NotificationService {

    private static Log logger = LogFactory.getLog(NotificationServiceBean.class);

    @Resource(mappedName = "ConnectionFactory")
    private static ConnectionFactory connectionFactory;

    @Resource(mappedName = "queue/EventMessageQueue")
    private static Queue queue;

    public NotificationServiceBean() {
    }

    public static void setConnectionFactory(ConnectionFactory c) {
        connectionFactory = c;
    }

    public static ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public static void setQueue(Queue q) {
        queue = q;
    }

    public static Queue getQueue() {
        return queue;
    }

    @Override
    public FactoryResource findResource(String path) throws FactoryException {
        return null;
    }

    @Override
    public String[] getResourceTypeList() {
        return RESOURCE_TYPE_LIST;
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public void throwEvent(Event event) throws NotificationServiceException {
        logger.info("throwEvent(...) called");
        if (event == null)
            throw new NotificationServiceException("impossible to throw a null event");
        Connection connection;
        try {
            connection = connectionFactory.createConnection();
            Session session = (Session) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            ObjectMessage om = session.createObjectMessage();
            om.setObject(event);
            MessageProducer messageProducer = session.createProducer(queue);
            messageProducer.send(om);
            messageProducer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            logger.error("unable to throw event", e);
            throw new NotificationServiceException("unable to throw event", e);
        }
    }
}
