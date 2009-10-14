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
package org.qualipso.factory.bugtracker.exception;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * Bug tracker Service Exception
 */
@WebFault
@SuppressWarnings("serial")
public class BugTrackerServiceException extends FactoryException {
    public BugTrackerServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public BugTrackerServiceException(String message) {
        super(message);
    }

    public BugTrackerServiceException(Exception rootCause) {
        super(rootCause);
    }
}

