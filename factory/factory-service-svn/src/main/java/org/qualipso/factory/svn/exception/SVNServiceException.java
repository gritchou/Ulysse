/**
 * 
 * Copyright (C) 2006-2010 THALES
 * http://www.thalesgroup.fr - gregory.cunha@thalesgroup.com
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the license LGPL.
 *
 * Initial author :
 * Gregory Cunha from Thales Service, THERESIS Competence Center Open Source Software
 *
 */
package org.qualipso.factory.svn.exception;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * 
 * Subversion Service Exception
 *
 */
@WebFault
@SuppressWarnings("serial")
public class SVNServiceException extends FactoryException {
    public SVNServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Constructor
     * @param message for exception
     */
    public SVNServiceException(String message) {
        super(message);
    }

    /**
     * Constructor
     * @param rootCause for exception
     */
    public SVNServiceException(Exception rootCause) {
        super(rootCause);
    }
}


