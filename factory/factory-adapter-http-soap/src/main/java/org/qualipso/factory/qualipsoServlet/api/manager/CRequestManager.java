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
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.qualipso.factory.qualipsoServlet.api.factory.IRequestProcessorFactory;
import org.qualipso.factory.qualipsoServlet.api.factory.IRequestProcessor;
import org.qualipso.factory.qualipsoServlet.impl.factory.CRequestProcessorFactoryImpl;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


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
	 * {@literalThis method makes and manages soap message from request REST... \n
	 * All attached files are implemented only after parameters of the method.\n
	 * For example : method1(param1,param2,...,AttachmentFile1, AttachmentFile2,...) and so on...}
	 * @param HttpServletRequest is the http request from the browser
	 * @param SOAPElement is the first node of the soap message
	 * @return SOAPElement is the body part of the soap message
	 */
	public SOAPElement processRequest(HttpServletRequest request, SOAPElement rootSoap) 
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
			// build the HashTable for the soap message
			while(aParamName != null)
			{
				// valueTmp contains values of parameters
				Vector<String> valueTmp = new Vector<String>();
				
				// argVal contains the names and values (as a vector of values) of parameters
				// argVal is defined for each node of the soap message.
				Hashtable<String, Vector<String>> argVal = new Hashtable<String, Vector<String>>();
					
				String aTmpParamValue = request.getParameter("Param"+i+"Name"+j+"Value");
				
				while(aTmpParamValue != null)
				{
					if(j == 0) // the parameter is a complex type ! 
					{						
						IRequestProcessor processor = factory.discriminant(request,"Param"+i+"Name");					
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
			throw new SOAPException("Error : do not manage to Map parameters : " + e.getMessage());
		}
		
		return this.buildSoapMessage(rootSoap);
	}
	
	/**
	 * 
	 * @param request is the HttpRequest from browser
	 * @param root is the body node of the soap message
	 * @return SOAPElement content within soap body and soap message.
	 * @throws SOAPException is throwing only if no attachment data or no multipart form was not requested
	 */
	
	public void processRequestWithAttach(HttpServletRequest request, SOAPMessage root) 
		throws SOAPException
	{
		boolean attachmentTag = false;
		Iterator anIter = root.getSOAPBody().getChildElements();
		
		SOAPElement rootSoap = (SOAPElement) anIter.next();
		SOAPElement aSoapResult = null;
		int i = -1;
		
		try
		{
			if(request.getContentType().startsWith("multipart/form-data") == true)
			{
				// attachment with multi-part data in http request
				
				while ( (i<4) && (attachmentTag == false))
				{
					i++; // start to i=0
					if(request.getParameter("AttachmentFile"+i) != null)
					{
						attachmentTag = true;
						// manage first parameters:
						aSoapResult = this.processRequest(request, rootSoap);
					}
				}
			}
			else
			{
				throw new Exception("No multipart/form-data tag has found in request");
			}
		}
		catch (Exception e)
		{
			throw new SOAPException("No multipart format in http request ! build standard message soap: "+e.getMessage());
		}
				
		/*************
		 * Management of attached files
		 *************/
		
		if(attachmentTag)
			this.buildAttachedFile(request,root, aSoapResult,i);
		else
			throw new SOAPException("No attachment data in http request");
		
		// fill the soap message with attachment :
		root.saveChanges();
	}
	
	/** 
	 * Build the soap message and start from the root node.
	 * @Param SOAPElement is the root node of the soap message
	 * @return SOAPElement is the part of the main soap message 
	 */
	public SOAPElement buildSoapMessage(SOAPElement aRootNode) throws SOAPException
	{
		Enumeration<IRequestProcessor> enumRequest = requestProcessors.elements();
		
		while(enumRequest.hasMoreElements())
		{
			aRootNode.addChildElement(enumRequest.nextElement().generateSOAP());
		}
		
		return aRootNode;
	}
	
	protected void buildAttachedFile(HttpServletRequest request,SOAPMessage soapMessage, SOAPElement nodeSoapElement,int nbreMax) 
		throws SOAPException
	{
		// search in HttpRequest data for attachment file
			
		try{
			// convert binary data 
			ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
			List fileItemsList = servletFileUpload.parseRequest(request);
			
			for(int j = 0 ; j <= nbreMax ; j++)
			{
				Iterator it = fileItemsList.iterator();
			
				while (it.hasNext())
				{
					FileItem fileItemTemp = (FileItem) it.next();
			    
					if (fileItemTemp.getFieldName().equals("AttachmentFile"+j))
					{
						String optionalFileName = fileItemTemp.getString();
						long sizeFile = fileItemTemp.getSize();
			    	
						byte[] aDataBinary = optionalFileName.getBytes();
						DataHandler dataHandler = 
							new DataHandler(new ByteArrayDataSource(aDataBinary,fileItemTemp.getContentType()));
			    	
						// add in body :
						
						AttachmentPart soapPart = soapMessage.createAttachmentPart(dataHandler);
						String cidRefPart = soapPart.getContentId();
						
						nodeSoapElement.addChildElement("AttachmentFile"+j, "href", "cid:"+cidRefPart);
			    	
						// add in soap part element :
						soapMessage.addAttachmentPart(soapPart);
					}
				}
			}
			soapMessage.saveChanges();
		}
		catch(FileUploadException except)
		{
			throw new SOAPException("Error in CRequestManager.buildAttachedFile(),\n" +
					" ServletFileUpload.parseRequest method : "+except.getMessage());
		}
	}
}
