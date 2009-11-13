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


/**
 * Subversion technical exception
 */
@SuppressWarnings("serial")
public class SubversionTechnicalException extends RuntimeException {
    public SubversionTechnicalException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public SubversionTechnicalException(String message) {
        super(message);
    }

    public SubversionTechnicalException(Exception rootCause) {
        super(rootCause);
    }
}

