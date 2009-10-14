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

package org.qualipso.factory.qualipsoServlet.impl.factory;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map.Entry;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

import org.qualipso.factory.qualipsoServlet.api.factory.IRequestProcessor;

public class CSimpleRequestProcessor implements IRequestProcessor 
{

	private Hashtable <String, Vector<String>> argVal;
	
	public CSimpleRequestProcessor(Hashtable <String, Vector<String>> argVal) 
	{
		this.argVal = argVal;
	}
	
	public SOAPElement generateSOAP() throws SOAPException
	{
		Iterator<Entry<String, Vector<String>>> iter = argVal.entrySet().iterator();
		Entry<String, Vector<String>> simpleElem=iter.next();
		
		SOAPFactory factory = SOAPFactory.newInstance();		
		return(factory.createElement(simpleElem.getKey()).addTextNode(simpleElem.getValue().firstElement()));
	}

	public String getNodeName()
	{
		return this.argVal.entrySet().iterator().next().getValue().firstElement();
	}
}
