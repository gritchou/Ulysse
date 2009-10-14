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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.qualipso.factory.qualipsoServlet.api.factory.IRequestProcessor;
import org.qualipso.factory.qualipsoServlet.api.factory.IRequestProcessorFactory;

/**
 * 
 * {@literal The class implent the interface IRequestProcessFactory.}
 * @author Thierry Deroff from Thales
 *
 */
public class CRequestProcessorFactoryImpl implements IRequestProcessorFactory 
{

	/**
	 * {@literal This method allow to build the nodes for the xml and complex types.
	 * It uses recursive method...
	 * The first parameter name and its value were saved in hashtable. }
	 * @param request is the HttpRequest containing all values and parameters
	 * @param aParamName is the root name of the parameter which is the name of the complex type.
	 * @return IrequestProcessor object
	 */
	public CComplexRequestProcessor discriminant(HttpServletRequest request,String aParamName)
	{
		Hashtable<String, Vector<String>> argVal = new Hashtable<String, Vector<String>>();
		CComplexRequestProcessor aReturnObject = new CComplexRequestProcessor(argVal);
		
		//set complex Name
		aReturnObject.setAComplexTypeName(request.getParameter(aParamName+"0"));		
		int i = 1;
		
		// build the hashtable :
		String aParamValue = request.getParameter(aParamName+i);
		
		// input values in the hashtable (or create children Complex):
		while(aParamValue != null)
		{				
			Vector<String> aTmpVector = new Vector<String>();
			
			//  recursive method :			
			if (request.getParameter(aParamName+i+"Value0") != null) // children complex types
			{			
				// the heart of the recursive :
				IRequestProcessor aTmpObject = this.discriminant(request, aParamName+i+"Value");				
				aReturnObject.addChildrenCComplex(aTmpObject);
			}
			// fill the hashtable :
			else 
			{
				String aParamValueValue = request.getParameter(aParamName+i+"Value");
			
				if (aParamValueValue == null)
					aParamValueValue = "";
				
				aTmpVector.add(aParamValueValue);
				argVal.put(aParamValue, aTmpVector); 
			}
			
			i++;		
			
			aParamValue = request.getParameter(aParamName+i);
			
			if(aParamValue == null)
				aParamValue = request.getParameter(aParamName+i+"Value0");
		} // end of while(aParamValue)
		
		return aReturnObject;
	}
	
	
	



	/**
	 * {@literal This method defines the type of the soap request : simple, complex, array}
	 * @param Hashtable<String,Vector<String>> that hashmap contains attributes of the soap request.
	 * if the size of the hashtable is different with 1 then the soap request is defined for a complex type.
	 * Otherwise according the size of the vector it is a simple or array soap request.
	 * @return IRequestProcessor object 
	 */
	public IRequestProcessor discriminant(Hashtable<String, Vector<String>> argVal) 
	{
		if(argVal.size()==1)
		{
			if(argVal.get((argVal.keys().nextElement())).size()==1)
				return (new CSimpleRequestProcessor(argVal));
			else
				return(new CTableRequestProcessor(argVal));
		}
		else 
		{
			return(new CComplexRequestProcessor(argVal));
 		}
	}

}
