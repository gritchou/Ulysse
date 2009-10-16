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

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;
import org.qualipso.factory.notification.Event;


/**
 * Provides a time service for the factory.
 * 
 * @author <a href="mailto:christophe.bouthier@loria.fr">Christophe Bouthier</a>
 * @date 27 July 2009
 */


@Remote
@WebService(name = "EventQueueService", targetNamespace = "http://org.qualipso.factory.ws/service/eventqueue")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface EventQueueService extends FactoryService {
    
    public static final String PROFILES_PATH = "/eventqueues";

    @WebMethod
    public Event[] getEvents(String queuePath) throws EventQueueServiceException;
    
    @WebMethod
    public void createEventQueue(String path) throws EventQueueServiceException;
    
    @WebMethod
    public void pushEvent(String path, Event e) throws EventQueueServiceException;
}