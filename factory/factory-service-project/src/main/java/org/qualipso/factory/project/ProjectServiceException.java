package org.qualipso.factory.project;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * 
 * Copyright (C) 2006-2010 THALES
 * http://www.thalesgroup.fr - thierry.deroff@thalesgroup.com
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the license LGPL.
 *
 * Initial author :
 * Thierry Deroff from Thales Service, THERESIS Competence Center Open Source Software
 *
 */

@WebFault
@SuppressWarnings("serial")
public class ProjectServiceException extends FactoryException {

	public ProjectServiceException(String message) {
		super(message);
	}

	public ProjectServiceException(Exception cause) {
		super(cause);
	}

	public ProjectServiceException(String message, Exception cause) {
		super(message, cause);
	}

}
