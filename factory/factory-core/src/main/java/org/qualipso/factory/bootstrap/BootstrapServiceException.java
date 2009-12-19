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
package org.qualipso.factory.bootstrap;

import org.qualipso.factory.FactoryException;

import javax.xml.ws.WebFault;


/**
 * Bootstrap Service exceptions.<br/>
 * All bootstrap service exceptions should extend this one.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 4 September 2009
 */
@WebFault
@SuppressWarnings("serial")
public class BootstrapServiceException extends FactoryException {
    /**
     * Class constructor specifying message and root cause.
     *
     * @param message
     * @param rootCause
     */
    public BootstrapServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     *
     * @param message
     */
    public BootstrapServiceException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     *
     * @param rootCause
     */
    public BootstrapServiceException(Exception rootCause) {
        super(rootCause);
    }
}
