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

package org.qualipso.factory.qualipsoServlet.api.manager;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.qualipso.factory.qualipsoServlet.api.factory.IRequestProcessorFactory;
import org.qualipso.factory.qualipsoServlet.api.factory.IRequestProcessor;
import org.qualipso.factory.qualipsoServlet.impl.factory.CRequestProcessorFactoryImpl;

/**
 * 
 * The CRequestManager allows to translate the http request into soap body elements.
 * @author Thierry Deroff from THALES, updated by Emmanuel Meier.
 *
 */

public class CRequestManager 
{
	private Vector<IRequestProcessor> requestProcessors = null;
	
	public CRequestManager()
	{
		requestProcessors = new Vector<IRequestProcessor>();
	}
	
	/**
	 * {@literalThis method makes and manages soap message from request REST... }
	 * @param HttpServletRequest is the http request from the browser
	 * @param SOAPElement is the first node of the soap message
	 * @return SOAPElement is the body part of the soap message
	 */
	public SOAPElement processRequest(HttpServletRequest request, SOAPElement root) 
		throws SOAPException
	{
		IRequestProcessorFactory factory= new CRequestProcessorFactoryImpl();
		int i=1;
		int j=0;
	
		//**************************************************
		// start the mapping from HttpServletRequest :
		//**************************************************
	
		String aParamName = request.getParameter("Param"+i+"Name"+j); // start with J=0
		
		if (aParamName == null)
		{
			j++;
			aParamName = request.getParameter("Param"+i+"Name"+j); // update J=1
		}
		
		// Management to build the soap request
		try
		{
			// first verification :
			//if (aParamName == null)
			//	throw new Exception("Error in http request, no parameters values !!");
			
			// build the HashTable for the soap message
			while(aParamName != null)
			{
				// valueTmp contains values of parameters
				Vector<String> valueTmp = new Vector<String>();
				
				// argVal contains the names and values (as a vector of values) of parameters
				// argVal is defined for each node of the soap message.
				Hashtable<String, Vector<String>> argVal = new Hashtable<String, Vector<String>>();
					
				String aTmpParamValue = request.getParameter("Param"+i+"Name"+j+"Value");
				
				while(aTmpParamValue != null) // read values of complex types or table
				{
					if(j == 0) // the parameter is a complex type ! 
					{						
						IRequestProcessor processor =factory.discriminant(request,"Param"+i+"Name");					
						requestProcessors.add(processor);
						
						// we stop the read and run with the next parameter
						aTmpParamValue = null;
					}
					else
					{
						String aValueToSave = new String(aTmpParamValue); //copy of the reference
						
						valueTmp.add(aValueToSave);			
						argVal.put(aParamName, valueTmp);
					
						j++;
						aTmpParamValue = request.getParameter("Param"+i+"Name"+j+"Value");
					}
				} // end of the while on "Param+i+Name+j+Value" with "j" as a counter
				
				if(j != 0)
				{
					// get request processor.
					IRequestProcessor processor = factory.discriminant(argVal);
					requestProcessors.add(processor);
				}
				
				// next parameter "Param+i+Name"
				i++;
				j = 0;
				
				aParamName = request.getParameter("Param"+i+"Name"+j);
				if (aParamName == null)
				{
					j++;
					aParamName = request.getParameter("Param"+i+"Name"+j); // update J=1
				}
			} // end of the "while(aParamName)"
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SOAPException("Error : do not manage to Map parameters : " + e.getMessage());
		}
		
		return this.buildSoapMessage(root);
	}
	
	/** 
	 * Build the soap message and start from the root node.
	 * @Param SOAPElement is the root node of the soap message
	 * @return SOAPElement is the part of the main soap message 
	 */
	protected SOAPElement buildSoapMessage (SOAPElement aRootNode) throws SOAPException
	{
		Enumeration<IRequestProcessor> enumRequest = requestProcessors.elements();
		
		while(enumRequest.hasMoreElements())
		{
			aRootNode.addChildElement(enumRequest.nextElement().generateSOAP());
		}
		
		return aRootNode;
	}
}
