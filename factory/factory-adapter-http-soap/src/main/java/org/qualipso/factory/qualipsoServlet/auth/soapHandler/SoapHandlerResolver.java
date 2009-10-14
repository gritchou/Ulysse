/**
 * 
 * Copyright (C) 2006-2010 THALES
 * http://www.thalesgroup.fr - emmanuel.meier@thalesgroup.com
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the license LGPL.
 *
 * Initial author :
 * Emmanuel Meier from Thales Services, THERESIS Competence Center Open Source Software
 * 
 */

/**
 * code from www.javadb.com and modified by QualiPSo members
 */
package org.qualipso.factory.qualipsoServlet.auth.soapHandler;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

public class SoapHandlerResolver implements HandlerResolver 
{
	private String login;
	private String password;
	
	@Override
	public List<Handler> getHandlerChain(PortInfo arg0) 
	{
		List<Handler> handlerChain = new ArrayList<Handler>();

	    QualipsoHandlerHeader WSSE_username_passwdDigest = new QualipsoHandlerHeader();
	    WSSE_username_passwdDigest.setCredentials(login, password);

	    handlerChain.add(WSSE_username_passwdDigest);

	    return handlerChain;
	}

	/**
	 * transfer input data into the handler
	 */
	public void setCredential(String aUser,String aPass)
	{
		this.login = aUser;
		this.password = aPass;
	}
}
