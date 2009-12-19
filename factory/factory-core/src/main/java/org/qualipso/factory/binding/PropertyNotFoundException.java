/*
 *
 * Qualipso Factory
 * Copyright (C) 2006-2010 INRIA
 * http://www.inria.fr - molli@loria.fr
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of LGPL. See licenses details in LGPL.txt
 *
 * Initial authors :
 *
 * Jérôme Blanchard / INRIA
 * Pascal Molli / Nancy Université
 * Gérald Oster / Nancy Université
 * Christophe Bouthier / INRIA
 * 
 */
package org.qualipso.factory.binding;

import org.qualipso.factory.FactoryException;


/**
 * Thrown when you try to get a property that is not define on this node or on one of this parent.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June 2009
 */
@SuppressWarnings("serial")
public class PropertyNotFoundException extends FactoryException {
    /**
     * Class constructor specifying message and root cause.
     *
     * @param message
     * @param rootCause
     */
    public PropertyNotFoundException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     *
     * @param message
     */
    public PropertyNotFoundException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     *
     * @param rootCause
     */
    public PropertyNotFoundException(Exception rootCause) {
        super(rootCause);
    }
}
