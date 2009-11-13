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

import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.notification.entity.Rule;
import org.qualipso.factory.security.auth.AuthenticationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;

@Stateless(name = "Notification", mappedName = FactoryNamingConvention.SERVICE_PREFIX + "NotificationService")
@WebService(endpointInterface = "org.qualipso.factory.notification.NotificationService", targetNamespace = "http://org.qualipso.factory.ws/service/notification", serviceName = "NotificationService", portName = "NotificationServicePort")
@WebContext(contextRoot = "/factory-core", urlPattern = "/notification")
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class NotificationServiceBean implements NotificationService {

    private static final String SERVICE_NAME = "NotificationService";
    private static final String[] RESOURCE_TYPE_LIST = new String[] {};

    private static Log logger = LogFactory.getLog(NotificationServiceBean.class);

    private PEPService pep;
    private PAPService pap;
    private BindingService binding;
    private AuthenticationService authentication;
    private SessionContext ctx;
    private EntityManager em;
    private EventQueueService eventQueue;
    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/EventMessageQueue")
    private static Queue queue;

    public NotificationServiceBean() {
    }

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return this.em;
    }

    @Resource
    public void setSessionContext(SessionContext ctx) {
        this.ctx = ctx;
    }

    public SessionContext getSessionContext() {
        return this.ctx;
    }

    @EJB
    public void setBindingService(BindingService binding) {
        this.binding = binding;
    }

    public BindingService getBindingService() {
        return this.binding;
    }

    @EJB
    public void setPEPService(PEPService pep) {
        this.pep = pep;
    }

    public PEPService getPEPService() {
        return this.pep;
    }

    @EJB
    public void setPAPService(PAPService pap) {
        this.pap = pap;
    }

    public PAPService getPAPService() {
        return this.pap;
    }

    @EJB
    public void setAuthenticationService(AuthenticationService authentication) {
        this.authentication = authentication;
    }

    public AuthenticationService getAuthenticationService() {
        return this.authentication;
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
    public void register(String subjectre, String objectre, String targetre, String queuePath) throws NotificationServiceException {
        if(subjectre == null) {
        	throw new NotificationServiceException("subjectre can't be null");
        }
        if(objectre == null) {
        	throw new NotificationServiceException("objectre can't be null");
        }
        if(targetre == null) {
        	throw new NotificationServiceException("targetre can't be null");
        }
        if(queuePath == null) {
        	throw new NotificationServiceException("queuePath can't be null");
        }
        Rule r = new Rule();
        r.setSubjectre(subjectre);
        r.setObjectre(objectre);
        r.setTargetre(targetre);
        r.setQueuePath(queuePath);
        r.setId(UUID.randomUUID().toString());
        em.persist(r);
    }

    @Override
    public void unregister(String subjectre, String objectre, String targetre, String queuePath) throws NotificationServiceException {
        if(subjectre == null) {
        	throw new NotificationServiceException("subjectre can't be null");
        }
        if(objectre == null) {
        	throw new NotificationServiceException("objectre can't be null");
        }
        if(targetre == null) {
        	throw new NotificationServiceException("targetre can't be null");
        }
        if(queuePath == null) {
        	throw new NotificationServiceException("queuePath can't be null");
        }
        Query q = em.createQuery("select * from Rule where subjectre=:subjectre and objectre=:objectre and targetre=:targetre and queuePath=:queuePath");
        q.setParameter("subjectre", subjectre);
        q.setParameter("objectre", objectre);
        q.setParameter("targetre", targetre);
        q.setParameter("queuePath", queuePath);
        int n = q.executeUpdate();
        if (n != 1) {
            logger.warn("can't unregister " + subjectre + "/" + objectre + "/" + queuePath);
        }
    }

    @Override
    public void throwEvent(Event event) throws NotificationServiceException {
        Connection connection;
        try {
            connection = connectionFactory.createConnection();
            Session session = (Session) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            ObjectMessage om = session.createObjectMessage();
            om.setObject(event);
            MessageProducer messageProducer = session.createProducer(queue);
            messageProducer.send(om);
        } catch (JMSException e) {
            logger.error("unable to throw event", e);
            throw new NotificationServiceException("unable to throw event", e);
        }
    }

}
