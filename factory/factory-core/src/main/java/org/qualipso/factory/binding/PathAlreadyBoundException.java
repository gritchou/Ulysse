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
 * Thrown when a you try to bind a path that is already binded to another FactoryResourceIdentifier.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June May 2009
 */
@SuppressWarnings("serial")
public class PathAlreadyBoundException extends FactoryException {
    /**
     * Class constructor specifying message and root cause.
     *
     * @param message
     * @param rootCause
     */
    public PathAlreadyBoundException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     *
     * @param message
     */
    public PathAlreadyBoundException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     *
     * @param rootCause
     */
    public PathAlreadyBoundException(Exception rootCause) {
        super(rootCause);
    }
}
