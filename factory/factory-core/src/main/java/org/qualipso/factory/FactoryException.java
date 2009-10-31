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
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@WebFault
@SuppressWarnings("serial")
public class FactoryException extends Exception implements Serializable {
    public FactoryException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public FactoryException(String message) {
        super(message);
    }

    public FactoryException(Exception rootCause) {
        super(rootCause);
    }

	public FactoryException() {
		super();
	}
}
