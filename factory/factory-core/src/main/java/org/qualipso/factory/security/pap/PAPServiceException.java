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
package org.qualipso.factory.security.pap;

import org.qualipso.factory.FactoryException;


/**
 * PAP Service exceptions.<br/>
 * All pap service exceptions should extend this one.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 May 2009
 */
@SuppressWarnings("serial")
public class PAPServiceException extends FactoryException {
    /**
     * Class constructor specifying message and root cause.
     *
     * @param message
     * @param rootCause
     */
    public PAPServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     *
     * @param message
     */
    public PAPServiceException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     *
     * @param rootCause
     */
    public PAPServiceException(Exception rootCause) {
        super(rootCause);
    }
}
