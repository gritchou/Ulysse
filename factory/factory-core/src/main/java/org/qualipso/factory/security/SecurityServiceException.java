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
package org.qualipso.factory.security;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@WebFault
@SuppressWarnings("serial")
public class SecurityServiceException extends FactoryException {
    public SecurityServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public SecurityServiceException(String message) {
        super(message);
    }

    public SecurityServiceException(Exception rootCause) {
        super(rootCause);
    }
}
