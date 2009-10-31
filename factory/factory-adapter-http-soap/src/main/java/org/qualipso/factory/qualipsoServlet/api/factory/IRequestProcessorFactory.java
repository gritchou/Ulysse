/**
 * 
 * Copyright (C) 2006-2010 THALES
 * http://www.thalesgroup.fr - emmanuel.meier@thalesgroup.com
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the license LGPL.
 *
 * Initial authors :
 * Emmanuel Meier from Thales Services, THERESIS Competence Center Open Source Software
 * Thierry Deroff from Thales Services, THERESIS Competence Center Open Source Software
 */

package org.qualipso.factory.qualipsoServlet.api.factory;

import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;


public interface IRequestProcessorFactory 
{
	public IRequestProcessor discriminant(Hashtable<String, Vector<String>> argVal);
	public IRequestProcessor discriminant(HttpServletRequest request, String aParamName);
}
