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

import org.qualipso.factory.FactoryException;

/**
 * Notification Service exceptions.<br/>
 * All notification service exceptions should extend this one.
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @author Nicolas HENRY
 * @author Marlène HANTZ
 * @date 20 May 2009
 */
@SuppressWarnings("serial")
public class NotificationServiceException extends FactoryException {
    /**
     * Class constructor specifying message and root cause.
     * 
     * @param message
     * @param rootCause
     */
    public NotificationServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     * 
     * @param message
     */
    public NotificationServiceException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     * 
     * @param rootCause
     */
    public NotificationServiceException(Exception rootCause) {
        super(rootCause);
    }
}
