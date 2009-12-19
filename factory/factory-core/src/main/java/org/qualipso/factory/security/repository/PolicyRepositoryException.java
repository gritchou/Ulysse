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
package org.qualipso.factory.security.repository;

import org.qualipso.factory.FactoryException;


/**
 * PolicyRepository Service exceptions.<br/>
 * All PolicyRepository service exceptions should extend this one.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 28 August 2009
 */
@SuppressWarnings("serial")
public class PolicyRepositoryException extends FactoryException {
    /**
     * Class constructor specifying message and root cause.
     *
     * @param message
     * @param rootCause
     */
    public PolicyRepositoryException(String message, Exception exception) {
        super(message, exception);
    }

    /**
     * Class constructor specifying message.
     *
     * @param message
     */
    public PolicyRepositoryException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     *
     * @param rootCause
     */
    public PolicyRepositoryException(Exception exception) {
        super(exception);
    }
}
