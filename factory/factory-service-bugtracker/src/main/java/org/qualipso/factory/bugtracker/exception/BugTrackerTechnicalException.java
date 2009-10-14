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


/**
 * Bug tracker technical exception
 */
@SuppressWarnings("serial")
public class BugTrackerTechnicalException extends RuntimeException {
    public BugTrackerTechnicalException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public BugTrackerTechnicalException(String message) {
        super(message);
    }

    public BugTrackerTechnicalException(Exception rootCause) {
        super(rootCause);
    }
}

