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

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * Exception for the Clock service.
 * 
 * @author <a href="mailto:christophe.bouthier@loria.fr">Christophe Bouthier</a>
 * @author Nicolas HENRY
 * @author Marlène HANTZ
 * @date 27 July 2009
 */
@WebFault
@SuppressWarnings("serial")
public class EventQueueServiceException extends FactoryException {

    /**
     * Constructor
     * 
     * @param message
     *            human-readable explanation of the exception
     * @param rootCause
     *            primary exception that caused this exception (real cause of
     *            the problem)
     */
    public EventQueueServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Constructor
     * 
     * @param rootCause
     *            primary exception that caused this exception (real cause of
     *            the problem)
     */
    public EventQueueServiceException(Exception rootCause) {
        super(rootCause);
    }

    /**
     * Constructor
     * 
     * @param message
     *            human-readable explanation of the exception
     */
    public EventQueueServiceException(String message) {
        super(message);
    }

}
