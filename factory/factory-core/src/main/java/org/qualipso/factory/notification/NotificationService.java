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
 * Christophe Bouthier / INRIA
 * 
 */
package org.qualipso.factory.notification;

import javax.ejb.Local;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.eventqueue.entity.Event;

/**
 * 
 * NotificationService is the interface class for the Notification Core
 * Component. There are four methods you can call from the Notification Service.
 * This service allow you to match a rule to a specific queue. You can:
 * <ul>
 * <li>Register a rule to a queue.</li>
 * <li>Unregister rule from a queue.</li>
 * <li>Throw an Event.</li>
 * <li>Get the list of the rules registered.</li>
 * </ul>
 * A rule is defined by a tuple:
 * <ul>
 * <li>subject</li>
 * <li>object</li>
 * <li>target</li>
 * </ul>
 * <p>
 * A tuple (subject, object, target) example is :
 * <code>("jeff", "commit", "/p1/t1/toto")</code>. Once this tuple is registered
 * in the Notification Service, if jeff commits in <code>/p1/t1/toto</code>, the
 * rule will match this event and it will be pushed in the registered queue, but
 * if he commits in <code>/p1/t1</code>, it will not match.
 * <p>
 * You can match more generic events : <code>("j*","com*","/p1/t1/*")</code>.
 * With this rule, if jeff commits in <code>/p1/t1</code>, it will match, but if
 * jack or john also commits in <code>/p1/t1</code>, it will match as well.
 * 
 * @author Nicolas HENRY
 * @author Marlène HANTZ
 * @author Jean-Francois Grand
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @version 11/19/2009
 */
@Local
@WebService(name = NotificationService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + NotificationService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface NotificationService extends FactoryService {

    public static final String SERVICE_NAME = "notification";
    public static final String[] RESOURCE_TYPE_LIST = new String[] {};

    /**
     * 
     * Allows to throw events from the Notification Service.
     * 
     * @param e
     *            the event to be thrown, see
     *            org.qualipso.factory.eventqueue.entity.Event.
     * @throws NotificationServiceException
     */
    @WebMethod
    public void throwEvent(Event e) throws NotificationServiceException;

}
