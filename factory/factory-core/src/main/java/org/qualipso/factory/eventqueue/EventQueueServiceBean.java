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
package org.qualipso.factory.eventqueue;

import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.eventqueue.entity.EventQueue;

/**
 * Implementation of the ClockService. Provides a time service for the factory.
 * 
 * @author <a href="mailto:christophe.bouthier@loria.fr">Christophe Bouthier</a>
 * @date 27 July 2009
 */
@Stateless(name = "EventQueue", mappedName = FactoryNamingConvention.JNDI_SERVICE_PREFIX+"EventQueueService")
@WebService(endpointInterface = "org.qualipso.factory.eventqueue.EventQueueService", targetNamespace = "http://org.qualipso.funkyfactory.ws/service/eventqueue", serviceName = "EventQueueService", portName = "EventQueueServicePort")
@WebContext(contextRoot = "/factory-core", urlPattern = "/eventqueue")
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class EventQueueServiceBean implements EventQueueService {

    private static final String SERVICE_NAME = "EventQueueService";
    private static final String[] RESOURCE_TYPE_LIST = new String[] { "EventQueue" };

    private static Log logger = LogFactory.getLog(EventQueueServiceBean.class);

    private BindingService binding;
    private PEPService pep;
    private PAPService pap;
    private NotificationService notification;
    private MembershipService membership;
    private SessionContext ctx;
    private EntityManager em;

    public EventQueueServiceBean() {
    }

    
    /**
     * cette methode positionne l Entity Manager
    *@param em Entity Manager   
   */
    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    
    /**    
     * cette methode retourne l Entity Manager 
     * @return em l'entity manager
    */
    public EntityManager getEntityManager() {
        return this.em;
    }

    
    /**
     * cette methode positionne la Session Context
     * @param ctx la Session Context 
    */
    @Resource
    public void setSessionContext(SessionContext ctx) {
        this.ctx = ctx;
    }

    
    /**
     * cette methode retourne la Session Context
     * @return la Session Context
    */
    public SessionContext getSessionContext() {
        return this.ctx;
    }

    
    /**
     * cette methode positionne le Binding Service
     * @param binding le  Binding Service
     * 
    */
    @EJB(name = "BindingService")
    public void setBindingService(BindingService binding) {
        this.binding = binding;
    }

    
    /**
     *
     *cette methode retourne le Binding Service
     * @return le Binding Service
    */
    public BindingService getBindingService() {
        return this.binding;
    }

    /**
     * cette methode positionne le PEP Service
     * @param pep le  PEP Service
     * 
    */
    @EJB(name = "PEPService")
    public void setPEPService(PEPService pep) {
        this.pep = pep;
    }

    /**
     * cette methode retourne le PEP Service
     * @return PEP Service
    */
    public PEPService getPEPService() {
        return this.pep;
    }

    
    /**
     * cette methode positionne le PAP Service
     * @param pap le  PAP Service
     * 
    */
    @EJB(name = "PAPService")
    public void setPAPService(PAPService pap) {
        this.pap = pap;
    }

    /**
     * cette methode retourne le PAP Service
     * @return PAPS ervice
    */
    public PAPService getPAPService() {
        return this.pap;
    }

    /**
     * cette methode positionne le Notification Service
     * @param notification le  Notification Service
     * 
    */
    @EJB(name = "NotificationService")
    public void setNotificationService(NotificationService notification) {
        this.notification = notification;
    }

    
    /**
     * cette methode retourne le Notification Service
     * @return le Service Notification
    */
    public NotificationService getNotificationService() {
        return this.notification;
    }

    /**
     * cette methode positionne le Membership Service
     * @param membership le Membership Service
     * 
    */
    @EJB(name = "MembershipService")
    public void setMembershipService(MembershipService membership) {
        this.membership = membership;
    }

    /**
     * cette methode retourne le Membership Service
     * @return le Membership Service
    */
    public MembershipService getMembershipService() {
        return this.membership;
    }

    
    
   /**
    * cette methode retourne les evenements contenu dans la queue 
    * sinon une exception si la queue n est pas trouve
    * @param   name le nom de la queue    
    * @return  returne un tableau des evenements contenu dans la queue  
   */
    @Override
    public Event[] getEvents(String name) throws EventQueueServiceException {
        String path = getEventQueuePathFromName(name);
        
        FactoryResourceIdentifier identifier;
        try {
            identifier = binding.lookup(path);
            if (identifier.getType().equals("EventQueue")) {
                String caller = membership.getProfilePathForConnectedIdentifier();
                pep.checkSecurity(caller, path, "read");

                EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
                if (eventqueue == null) {
                    throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
                }
                
                Event[] evs=new Event[eventqueue.getEvents().size()];
                return eventqueue.getEvents().toArray(evs);
                
            }else{
                throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
            }
        } catch ( Exception e ) {
            logger.error("unable to create an event queue", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to create an event queue", e);
        }
    }

    
    /**
     * cette méthode permet de creer une nouvelle queue a partir de son nom
     * sinon retourne une exception si la queue n a pas pu etre cree
     * la queue est rendu persistante et bindee par la suite
    * @param name le nom de la nouvelle queue a creer   
    *  
   */
    @Override
    public void createEventQueue(String name) throws EventQueueServiceException {
        logger.info("createEventQueue(...) called");
        logger.debug("params : name=" + name);
        String path = getEventQueuePathFromName(name);
        try {
            String caller = membership.getProfilePathForConnectedIdentifier();
            pep.checkSecurity(caller, PathHelper.getParentPath(path), "create");
            
            EventQueue evq=new EventQueue();
            evq.setEvents(new ArrayList<Event>());
            evq.setName(name);
            evq.setResourcePath(path);
            
            em.persist(evq);
            
            binding.bind(evq.getFactoryResourceIdentifier(), path);
           
            String policyId = UUID.randomUUID().toString();
            pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, path, path));
            // il manque peut être les setProperty
            
        } catch ( Exception e ) {
            logger.error("unable to create an event queue", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to create an event queue", e);
        }
    }
    
    
    /**
     * cette methode retourne le path de l event queue associe a name
     * @param name le nom de la queue
     * @return le path de l event queue associé au nom name
    */
    private String getEventQueuePathFromName(String name){
        return PROFILES_PATH+"/"+name;
    }

    
    /**
     * cette methode place un evenement dans la queue associe au name
     * sinon retourne une exception si l evenement n est pas ajoute
     * @param name le nom de l'event queue
     * @param event l evenement a pusher dans la queue
     * 
    */
    @Override
    public void pushEvent(String name, Event event) throws EventQueueServiceException {
        String path = getEventQueuePathFromName(name);
        
        FactoryResourceIdentifier identifier;
        try {
            identifier = binding.lookup(path);
            if (identifier.getType().equals("EventQueue")) {
                String caller = membership.getProfilePathForConnectedIdentifier();
                pep.checkSecurity(caller, path, "update");

                EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
                if (eventqueue == null) {
                    throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
                }
                
                eventqueue.getEvents().add(event);
                
                em.persist(eventqueue);
            }else{
                throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
            }
        } catch ( Exception e ) {
            logger.error("unable to create an event queue", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to create an event queue", e);
        }
    }

    /**
     * cette methode retourne l event queue associe a path
     * sinon une exception si l'event queue n'est pas trouve 
     * @param path le path de l event queue 
     * @return event queue associé a path
    */
    @Override
    public FactoryResource findResource(String path) throws FactoryException {
        logger.info("findResource(...) called");
        logger.debug("params : path=" + path);

        FactoryResourceIdentifier identifier = binding.lookup(path);

        if (identifier.getType().equals("EventQueue")) {
            String caller = membership.getProfilePathForConnectedIdentifier();
            pep.checkSecurity(caller, path, "read");

            EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
            if (eventqueue == null) {
                throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
            }

            return eventqueue;
        }

        throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
    }

    
    /**
     * cette methode retourne le Resource Type List
     * @return Resource Type List
    */
    @Override
    public String[] getResourceTypeList() {
        return RESOURCE_TYPE_LIST;
    }

    /**
     * cette methode retourne le nom du service
     * @return le nom du cervice
    */
    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

}