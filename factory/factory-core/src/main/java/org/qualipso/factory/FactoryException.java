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
 *
 */
package org.qualipso.factory;

import java.io.Serializable;

import javax.xml.ws.WebFault;


/**
 * Global framework exception.<br/>
 * All framework exceptions should extend this one.
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@WebFault
@SuppressWarnings("serial")
public class FactoryException extends Exception implements Serializable {
	
    /**
     * Class constructor specifying message and root cause.
     * 
     * @param message
     * @param rootCause
     */
    public FactoryException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     * 
     * @param message
     */
    public FactoryException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     * 
     * @param rootCause
     */
    public FactoryException(Exception rootCause) {
        super(rootCause);
    }

	/**
	 * Class constructor 
	 */
	public FactoryException() {
		super();
	}
}
