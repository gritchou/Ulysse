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
package org.qualipso.factory.search;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * Search Service exceptions.<br/>
 * All search service exceptions should extend this one.
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 26 June 2009
 */
@WebFault
@SuppressWarnings("serial")
public class SearchServiceException extends FactoryException {
    /**
     * Class constructor specifying message and root cause.
     * 
     * @param message
     * @param rootCause
     */
    public SearchServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     * 
     * @param message
     */
    public SearchServiceException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     * 
     * @param rootCause
     */
    public SearchServiceException(Exception rootCause) {
        super(rootCause);
    }
}
