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

public class CComplexRequestProcessor implements IRequestProcessor 
{
	private String aComplexTypeName;
	private Hashtable <String, Vector<String>> argVal;
	protected Vector<IRequestProcessor> childrenCComplex;
	
	public CComplexRequestProcessor(Hashtable <String, Vector<String>> argVal) 
	{
		this.argVal=argVal;
		this.aComplexTypeName = null;
		this.childrenCComplex = null;
	}
	
	public SOAPElement generateSOAP() throws SOAPException
	{
		SOAPFactory factory=SOAPFactory.newInstance();
		SOAPElement rootElement;
		
		if(this.aComplexTypeName != null)
			rootElement = factory.createElement(aComplexTypeName);
		else
			rootElement = factory.createElement("complex");
		
		//handle simple Element or SimpleTable element
		if(argVal == null)
			throw new SOAPException("Error, the HashTable is not instanciated ");
		
		if(argVal.size() > 0)
		{
			Iterator<Entry<String, Vector<String>>> iter = argVal.entrySet().iterator();
			while(iter.hasNext())
			{	
				Entry<String, Vector<String>> entry = iter.next();
				if (entry.getValue().size()==1)
				{
					rootElement.addChildElement(factory.createElement(entry.getKey()).addTextNode(entry.getValue().firstElement()));
				}
				else
				{
					Vector<String> values=entry.getValue();
					Iterator<String> iterValues = values.iterator();
					
					SOAPElement listElement = factory.createElement(entry.getKey());
				
					while(iterValues.hasNext())
					{
						String value=iterValues.next();
						listElement.addChildElement("item").addTextNode(value);
					}
					rootElement.addChildElement(listElement);
				}
			}
		}
		
		//handle complexChildren
		if(childrenCComplex	!=	null) {
			Iterator<IRequestProcessor> iterChild = childrenCComplex.iterator();
			while(iterChild.hasNext())
			{
				rootElement.addChildElement(iterChild.next().generateSOAP());
			}
		}
		
		return rootElement;

		
		
	}

	/**
	 * methods to manage children of complex types
	 */
	public String getNodeName()
	{
		return this.aComplexTypeName;
	}
	
	public void addChildrenCComplex(IRequestProcessor CComplexChildren)
	{
		if(this.childrenCComplex == null)
			this.childrenCComplex = new Vector<IRequestProcessor>();
			
		this.childrenCComplex.add(CComplexChildren);
	}
	
	public Vector<IRequestProcessor> getChildrenProcessor()
	{
		return this.childrenCComplex;
	}
	
	public void setAComplexTypeName(String name){
		aComplexTypeName=name;
	}
}
