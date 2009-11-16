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

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.notification.entity.Rule;
import org.qualipso.factory.FactoryNamingConvention;

@Remote
@WebService(name = NotificationService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + NotificationService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface NotificationService extends FactoryService {

    public static final String SERVICE_NAME = "notification";
    public static final String[] RESOURCE_TYPE_LIST = new String[] {};

    @WebMethod
    public void register(String subjectre, String objectre, String targetre, String queuePath) throws NotificationServiceException;

    @WebMethod
    public void unregister(String subjectre, String objectre, String targetre, String queuePath) throws NotificationServiceException;
    
    @WebMethod
    public void throwEvent(Event e) throws NotificationServiceException;
    
    @WebMethod
    public Rule[] list() throws NotificationServiceException;

}
