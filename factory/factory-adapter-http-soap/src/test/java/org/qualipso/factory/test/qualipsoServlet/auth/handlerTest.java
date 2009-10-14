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

package org.qualipso.factory.test.qualipsoServlet.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.test.qualipsoServlet.api.CRSimpleTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for handler header of soap message
 * 
 * @author Emmanuel Meier
 *
 */

public class handlerTest extends TestCase {
	
	private static Log logger = LogFactory.getLog(handlerTest.class);
	
	public handlerTest(String name)
	{
		super(name);
	}
	
	public void testHandlerSoap()
	{
		//TODO
		
	}
	
	public static Test suite()
	{
		return new TestSuite(handlerTest.class);
	}
}
