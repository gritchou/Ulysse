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
package org.qualipso.factory.security.pdp;

import org.qualipso.factory.FactoryException;


/**
 * PDP Service exceptions.<br/>
 * All pdp service exceptions should extend this one.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@SuppressWarnings("serial")
public class PDPServiceException extends FactoryException {
    /**
     * Class constructor specifying message and root cause.
     *
     * @param message
     * @param rootCause
     */
    public PDPServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     *
     * @param message
     */
    public PDPServiceException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     *
     * @param rootCause
     */
    public PDPServiceException(Exception rootCause) {
        super(rootCause);
    }
}
