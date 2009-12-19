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
package org.qualipso.factory.greeting;

import org.qualipso.factory.FactoryException;

import javax.xml.ws.WebFault;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 june 2009
 */
@WebFault
@SuppressWarnings("serial")
public class GreetingServiceException extends FactoryException {
    public GreetingServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public GreetingServiceException(String message) {
        super(message);
    }

    public GreetingServiceException(Exception rootCause) {
        super(rootCause);
    }
}
