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
 *Elamri Firas
 *Yuksel Huriye
 *Secondary authors :
 *Amrou Mohanned Khalifa
 *Yiqing Li
 */
package org.qualipso.factory.eventqueue;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
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
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.eventqueue.entity.EventQueue;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;

/**
 * Implementation of the ClockService. Provides a time service for the factory.
 * 
 * @author <a href="mailto:christophe.bouthier@loria.fr">Christophe Bouthier</a>
 * @date 27 July 2009
 */
@Stateless(name = EventQueueService.SERVICE_NAME, mappedName = FactoryNamingConvention.SERVICE_PREFIX + EventQueueService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.eventqueue.EventQueueService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE
        + EventQueueService.SERVICE_NAME, serviceName = EventQueueService.SERVICE_NAME)
@WebContext(contextRoot = FactoryNamingConvention.WEB_SERVICE_CORE_MODULE_CONTEXT, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX
        + EventQueueService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class EventQueueServiceBean implements EventQueueService {

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
     * 
     * @param em
     *            Entity Manager
     */
    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    /**
     * cette methode retourne l Entity Manager
     * 
     * @return em l'entity manager
     */
    public EntityManager getEntityManager() {
        return this.em;
    }

    /**
     * cette methode positionne la Session Context
     * 
     * @param ctx
     *            la Session Context
     */
    @Resource
    public void setSessionContext(SessionContext ctx) {
        this.ctx = ctx;
    }

    /**
     * cette methode retourne la Session Context
     * 
     * @return la Session Context
     */
    public SessionContext getSessionContext() {
        return this.ctx;
    }

    /**
     * cette methode positionne le Binding Service
     * 
     * @param binding
     *            le Binding Service
     * 
     */
    @EJB
    public void setBindingService(BindingService binding) {
        this.binding = binding;
    }

    /**
     * 
     *cette methode retourne le Binding Service
     * 
     * @return le Binding Service
     */
    public BindingService getBindingService() {
        return this.binding;
    }

    /**
     * cette methode positionne le PEP Service
     * 
     * @param pep
     *            le PEP Service
     * 
     */
    @EJB
    public void setPEPService(PEPService pep) {
        this.pep = pep;
    }

    /**
     * cette methode retourne le PEP Service
     * 
     * @return PEP Service
     */
    public PEPService getPEPService() {
        return this.pep;
    }

    /**
     * cette methode positionne le PAP Service
     * 
     * @param pap
     *            le PAP Service
     * 
     */
    @EJB
    public void setPAPService(PAPService pap) {
        this.pap = pap;
    }

    /**
     * cette methode retourne le PAP Service
     * 
     * @return PAPS ervice
     */
    public PAPService getPAPService() {
        return this.pap;
    }

    /**
     * cette methode positionne le Notification Service
     * 
     * @param notification
     *            le Notification Service
     * 
     */
    @EJB
    public void setNotificationService(NotificationService notification) {
        this.notification = notification;
    }

    /**
     * cette methode retourne le Notification Service
     * 
     * @return le Service Notification
     */
    public NotificationService getNotificationService() {
        return this.notification;
    }

    /**
     * cette methode positionne le Membership Service
     * 
     * @param membership
     *            le Membership Service
     * 
     */
    @EJB
    public void setMembershipService(MembershipService membership) {
        this.membership = membership;
    }

    /**
     * cette methode retourne le Membership Service
     * 
     * @return le Membership Service
     */
    public MembershipService getMembershipService() {
        return this.membership;
    }

    /**
     * cette methode retourne les evenements contenu dans la queue sinon une
     * exception si la queue n est pas trouve
     * 
     * @param name
     *            le nom de la queue
     * @return returne un tableau des evenements contenu dans la queue
     */
    @Override
    public Event[] getEvents(String path) throws EventQueueServiceException {

        FactoryResourceIdentifier identifier;
        try {
            identifier = binding.lookup(path);
            if (identifier.getType().equals(EventQueue.RESOURCE_NAME)) {
                String caller = membership.getProfilePathForConnectedIdentifier();
                pep.checkSecurity(caller, path, "read");

                EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
                if (eventqueue == null) {
                    throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
                }

                Event[] evs = new Event[eventqueue.getEvents().size()];
                return eventqueue.getEvents().toArray(evs);

            } else {
                throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
            }
        } catch (Exception e) {
            logger.error("unable to get events", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to get events", e);
        }
    }

    /**
     * cette méthode permet de creer une nouvelle queue a partir de son nom
     * sinon retourne une exception si la queue n a pas pu etre cree la queue
     * est rendu persistante et bindee par la suite
     * 
     * @param name
     *            le nom de la nouvelle queue a creer
     * 
     */
    @Override
    public void createEventQueue(String path) throws EventQueueServiceException {
        logger.info("createEventQueue(...) called");
        logger.debug("params : path=" + path);
        try {

            String caller = membership.getProfilePathForConnectedIdentifier();
            pep.checkSecurity(caller, PathHelper.getParentPath(path), "create");
            EventQueue evq = new EventQueue();
            evq.setId(UUID.randomUUID().toString());
            evq.setEvents(new ArrayList<Event>());
            em.persist(evq);

            binding.bind(evq.getFactoryResourceIdentifier(), path);

            String policyId = UUID.randomUUID().toString();
			pap.createPolicy(policyId, PAPServiceHelper.addRuleToPolicy(PAPServiceHelper.buildOwnerPolicy(policyId, caller, path), "", new String[] {"create","read"}));
			binding.setProperty(path, FactoryResourceProperty.OWNER, caller);
			binding.setProperty(path, FactoryResourceProperty.POLICY_ID, policyId);


        } catch (Exception e) {
            logger.error("unable to create an event queue", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to create an event queue", e);
        }
    }

    /**
     * cette methode place un evenement dans la queue associe au name sinon
     * retourne une exception si l evenement n est pas ajoute
     * 
     * @param name
     *            le nom de l'event queue
     * @param event
     *            l evenement a pusher dans la queue
     * 
     */
    @Override
    public void pushEvent(String path, Event event) throws EventQueueServiceException {

        FactoryResourceIdentifier identifier;
        try {
            if(event==null){
                throw new IllegalArgumentException("event can't be null");
            }
            identifier = binding.lookup(path);
            if (identifier.getType().equals(EventQueue.RESOURCE_NAME)) {
                String caller = membership.getProfilePathForConnectedIdentifier();
                pep.checkSecurity(caller, path, "update");

                EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
                if (eventqueue == null) {
                    throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
                }

                eventqueue.getEvents().add(event);

                em.persist(eventqueue);
            } else {
                throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
            }
        } catch (Exception e) {
            logger.error("unable to pushEvent an event in the event queue", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to pushEvent an event in the event queue", e);
        }
    }

    /**
     * cette methode retourne l event queue associe a path sinon une exception
     * si l'event queue n'est pas trouve
     * 
     * @param path
     *            le path de l event queue
     * @return event queue associé a path
     */
    @Override
    public FactoryResource findResource(String path) throws FactoryException {
        logger.info("findResource(...) called");
        logger.debug("params : path=" + path);

        FactoryResourceIdentifier identifier = binding.lookup(path);

        if (identifier.getType().equals(EventQueue.RESOURCE_NAME)) {
            String caller = membership.getProfilePathForConnectedIdentifier();
            pep.checkSecurity(caller, path, "read");

            EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
            if (eventqueue == null) {
                throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
            }

            return eventqueue;
        }

        throw new EventQueueServiceException("Resource " + identifier + " is not managed by Event Queue Service");
    }

    @Override
    public Event getLastEvent(String path) throws EventQueueServiceException {

        FactoryResourceIdentifier identifier;
        try {
            identifier = binding.lookup(path);
            if (identifier.getType().equals(EventQueue.RESOURCE_NAME)) {
                String caller = membership.getProfilePathForConnectedIdentifier();
                pep.checkSecurity(caller, path, "read");

                EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
                if (eventqueue == null) {
                    throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
                }

                int size = eventqueue.getEvents().size();
                Event[] evq = new Event[size];
                evq = eventqueue.getEvents().toArray(evq);
                Event result = evq[size - 1];
                return result;

            } else {
                throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
            }
        } catch (Exception e) {
            logger.error("unable to get Last Event", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to get Last Event", e);
        }

    }

    /**
     * cette methode retourne le Resource Type List
     * 
     * @return Resource Type List
     */
    @Override
    public String[] getResourceTypeList() {
        return RESOURCE_TYPE_LIST;
    }

    /**
     * cette methode retourne le nom du service
     * 
     * @return le nom du cervice
     */
    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public void deleteEvent(String path, Event e) throws EventQueueServiceException {

        FactoryResourceIdentifier identifier;

        try {
            identifier = binding.lookup(path);
            if (identifier.getType().equals(EventQueue.RESOURCE_NAME)) {
                String caller = membership.getProfilePathForConnectedIdentifier();
                pep.checkSecurity(caller, path, "update");

                EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
                if (eventqueue == null) {
                    throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
                }
                /*
                int size = eventqueue.getEvents().size();
                Event[] evq = new Event[size];
                evq = eventqueue.getEvents().toArray(evq);
                */
                ArrayList<Event> newEventList = eventqueue.getEvents();
                if(newEventList.remove(e)){
                    eventqueue.setEvents(newEventList);
                    em.merge(eventqueue);
                }else {
                    throw new EventQueueServiceException("this event doesn't exist in this queue");
                }

            } else {
                throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
            }
        } catch (Exception ex) {
            logger.error("unable to delete Event", ex);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to delete Event", ex);
        }

    }

    /**
     * 
     * return an array of event that have or contains the same ressourceType
     * 
     * @param path
     *            path of the EventQueue
     * @param ressourceType
     *            the type of resource event
     * @param substring
     *            true if typeRssource contains parameter typeRessource, false
     *            if typeRssource contains exactly the parameter typeRessource
     * @return array of event
     * @throws EventQueueServiceException
     */
    @WebMethod
    public Event[] findEventByRessourceType(String path, String ressourceType, boolean substring) throws EventQueueServiceException {

        FactoryResourceIdentifier identifier;
        try {
          //li des cas exceptionnelles
            if ((path == null) || (ressourceType == null))
                throw new EventQueueServiceException("Le parametre n'est pas correct verifier le path ou le ressourceType");
           
            
            identifier = binding.lookup(path);
            if (identifier.getType().equals(EventQueue.RESOURCE_NAME)) {
                String caller = membership.getProfilePathForConnectedIdentifier();
                pep.checkSecurity(caller, path, "read");

                EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
                if (eventqueue == null) {
                    throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
                }

                ArrayList<Event> resultContains = new ArrayList<Event>();
                ArrayList<Event> resultEquals = new ArrayList<Event>();

                ArrayList<Event> listEvent = eventqueue.getEvents();
                Iterator<Event> it = listEvent.iterator();

                while (it.hasNext()) {
                    Event ev = it.next();

                    if (substring == true && ev.getResourceType().contains(ressourceType)) {
                        resultContains.add(ev);

                    }

                    if (substring == false && ev.getResourceType().equals(ressourceType)) {
                        resultEquals.add(ev);
                    }
                }

                if (substring == true) {
                    return (Event[]) (resultContains.toArray());
                } else {
                    return (Event[]) (resultEquals.toArray());
                }
            }

            else {
                throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
            }
        } catch (Exception e) {
            logger.error("unable to  find Event By Ressource Type", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to  find Event By Ressource Type", e);
        }

    }

    /**
     * return an array of event that have or contains the same thrower
     * 
     * @param path
     *            path of the EventQueue
     * @param thrower
     *            thrower of event
     * @param substring
     *            true if thrower contains the string parameter thrower , false
     *            if thrower contains exactly the parameter thrower.
     * @return array of event (return an array of event that have or contains
     *         the same thrower)
     * @throws EventQueueServiceException
     */
    @WebMethod
    public Event[] findEventBythrower(String path, String thrower, boolean substring) throws EventQueueServiceException {

        FactoryResourceIdentifier identifier;
        try {
            identifier = binding.lookup(path);
            if (identifier.getType().equals(EventQueue.RESOURCE_NAME)) {
                String caller = membership.getProfilePathForConnectedIdentifier();
                pep.checkSecurity(caller, path, "read");

                EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
                if (eventqueue == null) {
                    throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
                }

                ArrayList<Event> resultContains = new ArrayList<Event>();
                ArrayList<Event> resultFalse = new ArrayList<Event>();

                ArrayList<Event> listEvent = eventqueue.getEvents();
                Iterator<Event> it = listEvent.iterator();

                while (it.hasNext()) {
                    Event ev = it.next();

                    if (substring == true && ev.getThrowedBy().contains(thrower)) {
                        resultContains.add(ev);
                    }

                    if (substring == false && ev.getThrowedBy().equals(thrower)) {
                        resultFalse.add(ev);
                    }
                }

                if (substring == true) {
                    Event[] tab = new Event[resultContains.size()];
                    int i = 0;
                    while(i < resultContains.size()){
                        tab[i] = resultContains.get(i);
                        i++;
                    }
                    return tab;
                } else {
                    Event[] tab = new Event[resultFalse.size()];
                    int i = 0;
                    while(i < resultFalse.size()){
                        tab[i] = resultFalse.get(i);
                        i++;
                    }
                    return tab;
                }
            }

            else {
                throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
            }
        } catch (Exception e) {
            logger.error("unable to find Event By thrower", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to find Event By thrower", e);
        }
    }

    /**
     * 
     * @param path
     *            path of the EventQueue
     * @param fromRessource
     *            fromRessource of event
     * @param substring
     *            true if fromRessource contains the string parameter
     *            fromRessource , false if thrower contains exactly the
     *            parameter fromRessource.
     * @return array of event ( return an array of event that have or contains
     *         the same fromRessource )
     * @throws EventQueueServiceException
     */
    @WebMethod
    public Event[] findEventFromRessource(String path, String fromRessource, boolean substring) throws EventQueueServiceException {
        FactoryResourceIdentifier identifier;
        try {
          //li des cas exceptionnelles
            if ((path == null) || (fromRessource == null))
                throw new EventQueueServiceException("Le parametre n'est pas correct verifier le path ou fromRessource");
           
            
            
            identifier = binding.lookup(path);
            if (identifier.getType().equals(EventQueue.RESOURCE_NAME)) {
                String caller = membership.getProfilePathForConnectedIdentifier();
                pep.checkSecurity(caller, path, "read");

                EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
                if (eventqueue == null) {
                    throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
                }

                ArrayList<Event> resultContains = new ArrayList<Event>();
                ArrayList<Event> resultFalse = new ArrayList<Event>();

                ArrayList<Event> listEvent = eventqueue.getEvents();
                Iterator<Event> it = listEvent.iterator();

                while (it.hasNext()) {
                    Event ev = it.next();

                    if (substring == true && ev.getFromResource().contains(fromRessource)) {
                        resultContains.add(ev);
                    }

                    if (substring == false && ev.getFromResource().equals(fromRessource)) {
                        resultFalse.add(ev);
                    }
                }

                if (substring == true) {
                    Event[] tab = new Event[resultContains.size()];
                    int i = 0;
                    while(i < resultContains.size()){
                        tab[i] = resultContains.get(i);
                        i++;
                    }
                    return tab;
                } else {
                    Event[] tab = new Event[resultFalse.size()];
                    int i = 0;
                    while(i < resultFalse.size()){
                        tab[i] = resultFalse.get(i);
                        i++;
                    }
                    return tab;
                }
            }

            else {
                throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
            }
        } catch (Exception e) {
            logger.error("unable to find Event By from Ressource", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to find Event By from Ressource", e);
        }
    }

    /**
     * 
     * @param path
     *            path of the EventQueue
     * @param date
     *            date of the event
     * @return array of event ( return an array of event having the same date )
     * @throws EventQueueServiceException
     */
    @WebMethod
    public Event[] findEventByDate(String path, Date date) throws EventQueueServiceException {
        FactoryResourceIdentifier identifier;
        try {
            identifier = binding.lookup(path);
            if (identifier.getType().equals(EventQueue.RESOURCE_NAME)) {
                String caller = membership.getProfilePathForConnectedIdentifier();
                pep.checkSecurity(caller, path, "read");

                EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
                if (eventqueue == null) {
                    throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
                }

                ArrayList<Event> result = new ArrayList<Event>();

                ArrayList<Event> listEvent = eventqueue.getEvents();
                Iterator<Event> it = listEvent.iterator();

                while (it.hasNext()) {
                    Event ev = it.next();
                    if (ev.getDate().equals(date)) {
                        result.add(ev);
                    }

                }
                
                Event[] tab = new Event[result.size()];
                int i =0;
                while(i<result.size()){
                    tab[i] = result.get(i);
                    i++;
                }
                //return (Event[]) (result.toArray());
                return tab;
            }

            else {
                throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
            }
        } catch (Exception e) {
            logger.error("unable to find Event By Date", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to find Event By Date", e);
        }

    }

    /**
     * 
     * @param path
     *            path of the EventQueue
     * @param date
     *            date of the event
     * @return array of event (return an array of event having date lesser or
     *         equal of the parameter date )
     * @throws EventQueueServiceException
     */

    @WebMethod
    public Event[] findEventByDateInf(String path, Date date) throws EventQueueServiceException {
        FactoryResourceIdentifier identifier;
        try {
            identifier = binding.lookup(path);
            if (identifier.getType().equals(EventQueue.RESOURCE_NAME)) {
                String caller = membership.getProfilePathForConnectedIdentifier();
                pep.checkSecurity(caller, path, "read");

                EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
                if (eventqueue == null) {
                    throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
                }

                ArrayList<Event> result = new ArrayList<Event>();

                ArrayList<Event> listEvent = eventqueue.getEvents();
                Iterator<Event> it = listEvent.iterator();

                while (it.hasNext()) {
                    Event ev = (Event) it.next();
                    if (ev.getDate().before(date) || ev.getDate().equals(date)) {
                        result.add(ev);
                    }

                }// while

                //return (Event[]) (result.toArray());
                
                Event[] tab = new Event[result.size()];
                int i =0;
                while(i<result.size()){
                    tab[i] = result.get(i);
                    i++;
                }
                return tab;
            }

            else {
                throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
            }
        } catch (Exception e) {
            logger.error("unable to find Event By DateSup", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to find Event By DateSup", e);
        }
    }

    /**
     * 
     * @param path
     *            path of the EventQueue
     * @param date
     *            date date of the event
     * @return array of event (return an array of event having date superior or
     *         equal of the parameter date )
     * @throws EventQueueServiceException
     */
    @WebMethod
    public Event[] findEventByDateSup(String path, Date date) throws EventQueueServiceException {
        FactoryResourceIdentifier identifier;
        try {
            identifier = binding.lookup(path);
            if (identifier.getType().equals(EventQueue.RESOURCE_NAME)) {
                String caller = membership.getProfilePathForConnectedIdentifier();
                pep.checkSecurity(caller, path, "read");

                EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
                if (eventqueue == null) {
                    throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
                }

                ArrayList<Event> result = new ArrayList<Event>();

                ArrayList<Event> listEvent = eventqueue.getEvents();
                Iterator<Event> it = listEvent.iterator();

                while (it.hasNext()) {
                    Event ev = it.next();
                    if (ev.getDate().after(date) || ev.getDate().equals(date)) {
                        result.add(ev);
                    }

                }// while

                Event[] tab = new Event[result.size()];
                int i =0;
                while(i<result.size()){
                    tab[i] = result.get(i);
                    i++;
                }
                return tab;
            }

            else {
                throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
            }
        } catch (Exception e) {
            logger.error("unable to find Event By DateInf", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to find Event By DateInf", e);
        }
    }

    /**
     * 
     * @param path
     *            path of the EventQueue
     * @param date1
     *            date of the event
     * @param date2
     *            date of the event
     * @return array of event (return an array of event having date between
     *         date1 and date2 where date1<= date2)
     * @throws EventQueueServiceException
     */
    @WebMethod
    public Event[] findEventByDateBetween(String path, Date date1, Date date2) throws EventQueueServiceException {
        FactoryResourceIdentifier identifier;
        try {
            identifier = binding.lookup(path);
            if (identifier.getType().equals(EventQueue.RESOURCE_NAME)) {
                String caller = membership.getProfilePathForConnectedIdentifier();
                pep.checkSecurity(caller, path, "read");

                EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
                if (eventqueue == null) {
                    throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
                }

                ArrayList<Event> result = new ArrayList<Event>();

                ArrayList<Event> listEvent = eventqueue.getEvents();
                Iterator<Event> it = listEvent.iterator();
                while (it.hasNext()) {
                    Event ev = it.next();

                    // correction d'un bug concernant date1 et date2 et inversé
//                    if (((ev.getDate().before(date1)) || (ev.getDate().equals(date1))) && ((ev.getDate().after(date2)) || (ev.getDate().equals(date2)))) {
//                        result.add(ev);
//                    }
                    
                    if((ev.getDate().getTime()>=date1.getTime()) && (ev.getDate().getTime()<=date2.getTime())){
                        result.add(ev);
                    }
                    

                }// while

                Event[] tab = new Event[result.size()];
                int i =0;
                while(i<result.size()){
                    tab[i] = result.get(i);
                    i++;
                }
                return tab;
            }

            else {
                throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
            }
        } catch (Exception e) {
            logger.error("unable to find Event By Date Between", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to find Event By Date Between", e);
        }
    }

    /**
     * 
     * @param path
     * @param eventType
     * @param substring
     * @return
     * @throws EventQueueServiceException
     */
    @WebMethod
    public Event[] findEventByEventType(String path, String eventType, boolean substring) throws EventQueueServiceException {
        FactoryResourceIdentifier identifier;
        try {
            //li des cas exceptionnelles
            if ((path == null) || (eventType == null))
                throw new EventQueueServiceException("Le parametre n'est pas correct . Verifier le path ou l'eventType");
            
            identifier = binding.lookup(path);
            if (identifier.getType().equals(EventQueue.RESOURCE_NAME)) {
                String caller = membership.getProfilePathForConnectedIdentifier();
                pep.checkSecurity(caller, path, "read");

                EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
                if (eventqueue == null) {
                    throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
                }

                ArrayList<Event> resultContains = new ArrayList<Event>();
                ArrayList<Event> resultEquals = new ArrayList<Event>();

                ArrayList<Event> listEvent = eventqueue.getEvents();
                Iterator<Event> it = listEvent.iterator();

                while (it.hasNext()) {
                    Event ev = it.next();
                    if ((substring == true) && (ev.getEventType().contains(eventType))) {
                        resultContains.add(ev);
                    }

                    if ((substring == false) && (ev.getEventType().equals(eventType))) {
                        resultEquals.add(ev);
                    }
                }// while

                if (substring == true) {
                    Event[] tab = new Event[resultContains.size()];
                    int i = 0;
                    while(i < resultContains.size()){
                        tab[i] = resultContains.get(i);
                        i++;
                    }
                    return tab;
                } else {
                    Event[] tab = new Event[resultEquals.size()];
                    int i = 0;
                    while(i < resultEquals.size()){
                        tab[i] = resultEquals.get(i);
                        i++;
                    }
                    return tab;
                }
            }

            else {
                throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
            }
        } catch (Exception e) {
            logger.error("unable to find Event By EventType", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to find Event By EventType", e);
        }
    }

    public Event[] findObjectEvent(String path, Event event) throws EventQueueServiceException {

        FactoryResourceIdentifier identifier;
        try {
            identifier = binding.lookup(path);
            if (identifier.getType().equals(EventQueue.RESOURCE_NAME)) {
                String caller = membership.getProfilePathForConnectedIdentifier();
                pep.checkSecurity(caller, path, "read");

                EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
                if (eventqueue == null) {
                    throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
                }

                ArrayList<Event> result = new ArrayList<Event>();
                ArrayList<Event> listEvent = eventqueue.getEvents();
                Iterator<Event> it = listEvent.iterator();

                while (it.hasNext()) {
                    Event ev = it.next();
                    if (ev.equals(event)) {
                        result.add(ev);
                    }
                }
                
                Event[] tab = new Event[result.size()];
                int i = 0;
                while(i < result.size()){
                    tab[i] = result.get(i);
                    i++;
                }
                return tab;
            }

            else {
                throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
            }
        } catch (Exception e) {
            logger.error("unable to find Object Event", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to find Object Event", e);
        }
    }

    @Override
    public void removeQueue(String path) throws EventQueueServiceException {

        FactoryResourceIdentifier identifier;
        try {
            identifier = binding.lookup(path);
            if (identifier.getType().equals(EventQueue.RESOURCE_NAME)) {
                String caller = membership.getProfilePathForConnectedIdentifier();
                pep.checkSecurity(caller, path, "update");

                EventQueue eventqueue = em.find(EventQueue.class, identifier.getId());
                if (eventqueue == null) {
                    throw new EventQueueServiceException("unable to find an event queue for id " + identifier.getId());
                }

                em.remove(eventqueue);
                binding.unbind(path);
            } else {
                throw new CoreServiceException("Resource " + identifier + " is not managed by Event Queue Service");
            }
        } catch (Exception e) {
            logger.error("unable to remove  event queue", e);
            ctx.setRollbackOnly();
            throw new EventQueueServiceException("unable to create an event queue", e);
        }

    }

    @WebMethod
    public Event[] findEventBySimpleParameter(String path, String eventType, String thrower, String resourceType, String fromRessource, Date date,
            boolean dateSup, boolean dateInf) throws EventQueueServiceException {
        return null;
    }

    @WebMethod
    public Event[] findEventByComposedParameter(String path, String eventType, String thrower, String resourceType, String fromRessource, Date date1, Date date2)
            throws EventQueueServiceException {
        return null;
    }

}