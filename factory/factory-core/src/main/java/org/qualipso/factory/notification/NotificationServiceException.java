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

import org.qualipso.factory.FactoryException;

@SuppressWarnings("serial")
public class NotificationServiceException extends FactoryException {
    public NotificationServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public NotificationServiceException(String message) {
        super(message);
    }

    public NotificationServiceException(Exception rootCause) {
        super(rootCause);
    }
}
