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

package org.qualipso.factory.test.qualipsoServlet.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.junit.Test;

import org.qualipso.factory.qualipsoServlet.*;

/**
 * Unit test for implementation of objects managing soap messages
 * 
 * @author T0013627
 *
 */

public class factoryImplTest
{	
	private static Log logger = LogFactory.getLog(factoryImplTest.class);
	
	public factoryImplTest()
	{
		
	}
	
	/*
	 * JUnit for different arguments
	 * Simple arguments with attachment, message skeleton of the SOAPElement:
	 * <arg0>1</arg0>
	 * <arg1>toto</arg1>
	 * <arg2>titi</arg2>
	 */
	@Test
	public void testCAttachRequest()
	{
		Hashtable <String,Vector<String>> HtSimple;
		File aTestFile;
		
		// fill the Hastable with simple arguments :
		
		
		// test the object with soap generation:

	}

	/*
	 * JUnit for different arguments
	 * complex arguments with attachment, message skeleton of the SOAPElement:
	 * <arg0>1</arg0>
	 * <arg1>
	 * 	<SubArg1>toto</SubArg1>
	 * 	<SubArg2>titi</SubArg2>
	 * 	<SubArg3>tutu</SubArg3>
	 * </arg1>
	 * <arg2>mimi</arg2>
	 */
	@Test
	public void testCComplexAttachRequest()
	{
		Hashtable <String,Vector<String>> HtComplex;
		File aTestFile;
		
		// fill the Hastable with complex arguments :
		
		
		// test the object with soap generation:

	}
	
}
