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
import org.qualipso.factory.notification.Event;


//import javax.ejb.Remote;
//import javax.jws.WebMethod;
//import javax.jws.WebResult;
//import javax.jws.WebService;
//import javax.jws.soap.SOAPBinding;
//import javax.xml.bind.annotation.XmlType;
//
//import org.ejb3unit.hibernate.annotations.Entity;
//import org.qualipso.factory.FactoryService;
//
//
///**
// * Provides a time service for the factory.
// * 
// * @author <a href="mailto:christophe.bouthier@loria.fr">Christophe Bouthier</a>
// * @date 27 July 2009
// */
//
//
//@Remote
//@WebService(name = "ClockService", targetNamespace = "http://org.qualipso.funkyfactory.ws/service")
//@SOAPBinding(style = SOAPBinding.Style.RPC)
//public interface NotificationService extends FactoryService {
//
//	/**
//	 * Return the current time.
//	 * 
//	 * @return the current time, in a String
//	 * @throws NotificationServiceException
//	 */
//	@WebMethod
//	@WebResult(name = "time")
//	public String getTime() throws NotificationServiceException;
//	
//}

@Remote
@WebService(name = "NotificationService", targetNamespace = "http://org.qualipso.factory.ws/notification")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface NotificationService extends FactoryService{

	/*@WebMethod
	public void throwEvent(String pathQueue, Event event) throws NotificationServiceException;

	@WebMethod
	public Event pop(String pathQueue) throws NotificationServiceException;
	@WebMethod
	public String createQueue(String string, String caller) throws NotificationServiceException;*/
	
	@WebMethod
	public void register(String subjectre, String objectre, String targetre, String queuePath)throws NotificationServiceException;
	
	@WebMethod
	public void throwEvent(Event e) throws NotificationServiceException;

}