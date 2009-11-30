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
 * El amri Firas
 * Yuksel Huriye
 */
package org.qualipso.factory.eventqueue;

import java.util.Date;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.eventqueue.entity.EventQueue;

@Remote
@WebService(name = EventQueueService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + EventQueueService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface EventQueueService extends FactoryService {

    public static final String SERVICE_NAME = "eventqueue";
    public static final String[] RESOURCE_TYPE_LIST = new String[] { EventQueue.RESOURCE_NAME };

    /**
     * 
     * @param path
     *            path of the new queue
     * @throws EventQueueServiceException
     */
    @WebMethod
    public void createEventQueue(String path) throws EventQueueServiceException;

    /**
     * 
     * @param queuePath
     *            path of the queue
     * @return return all the event in the queue associated to path
     * @throws EventQueueServiceException
     */
    @WebMethod
    public Event[] getEvents(String queuePath) throws EventQueueServiceException;

    @WebMethod
    public void pushEvent(String path, Event e) throws EventQueueServiceException;

    @WebMethod
    public Event getLastEvent(String path) throws EventQueueServiceException;

    @WebMethod
    public void deleteEvent(String path, Event e) throws EventQueueServiceException;

    @WebMethod
    public void removeQueue(String path) throws EventQueueServiceException;

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
    public Event[] findEventByRessourceType(String path, String ressourceType, boolean substring) throws EventQueueServiceException;

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
    public Event[] findEventBythrower(String path, String thrower, boolean substring) throws EventQueueServiceException;

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
    public Event[] findEventByFromRessource(String path, String fromRessource, boolean substring) throws EventQueueServiceException;

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
    public Event[] findEventByDate(String path, Date date) throws EventQueueServiceException;

    /**
     * 
     * @param path
     *            path of the EventQueue
     * @param date
     *            date of the event
     * @return array of event (return an array of event having date superior or
     *         equal of the parameter date )
     * @throws EventQueueServiceException
     */

    @WebMethod
    public Event[] findEventByDateSup(String path, Date date) throws EventQueueServiceException;

    /**
     * 
     * @param path
     *            path of the EventQueue
     * @param date
     *            date date of the event
     * @return array of event (return an array of event having date lesser or
     *         equal of the parameter date )
     * @throws EventQueueServiceException
     */
    @WebMethod
    public Event[] findEventByDateInf(String path, Date date) throws EventQueueServiceException;

    /**
     * 
     * @param path
     *            path of the EventQueue
     * @param date1
     *            date of the event
     * @param date2
     *            date of the event
     * @return array of event (return an array of event having date between
     *         date1 and date2)
     * @throws EventQueueServiceException
     */
    @WebMethod
    public Event[] findEventByDateBetween(String path, Date date1, Date date2) throws EventQueueServiceException;

    /**
     * 
     * @param path
     *            path of the EventQueue
     * @param event
     *            Type type of the event
     * @param substring
     *            true if eventType contains the string parameter eventType ,
     *            false if eventType contains exactly the parameter eventType.
     * @return array of event (return an array of event that have or contains
     *         the same eventType )
     * @throws EventQueueServiceException
     */
    @WebMethod
    public Event[] findEventByEventType(String path, String eventType, boolean substring) throws EventQueueServiceException;

    @WebMethod
    public Event[] findEventBySimpleParameter(String path, String eventType, String thrower, String resourceType, String fromRessource, Date date,
            boolean dateSup, boolean dateInf) throws EventQueueServiceException;

    @WebMethod
    public Event[] findEventByComposedParameter(String path, String eventType, String thrower, String resourceType, String fromRessource, Date date1, Date date2)
            throws EventQueueServiceException;

    @WebMethod
    public Event[] findObjectEvent(String path, Event event) throws EventQueueServiceException;

    @WebMethod
    public void generateXML(String path);

}