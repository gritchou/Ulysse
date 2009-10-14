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

public class CTableRequestProcessor implements IRequestProcessor
{

	private Hashtable <String, Vector<String>> argVal;
	
	public CTableRequestProcessor(Hashtable <String, Vector<String>> argVal) 
	{
		this.argVal=argVal;
	}

	public SOAPElement generateSOAP() throws SOAPException
	{
		Iterator<Entry<String, Vector<String>>> iter = argVal.entrySet().iterator();
		
		//a table parameter has only one key but several values
		Entry<String, Vector<String>> tableElem = iter.next();
		Vector<String> values = tableElem.getValue();
		
		SOAPFactory factory = SOAPFactory.newInstance();
		SOAPElement rootElement = factory.createElement(tableElem.getKey());
		
		Iterator<String> iterValues = values.iterator();
		
		while(iterValues.hasNext()){
			String value=iterValues.next();
			rootElement.addChildElement("item").addTextNode(value);
		}
		return(rootElement);
	}

	public String getNodeName()
	{
		return this.argVal.entrySet().iterator().next().getValue().firstElement();
	}
}
