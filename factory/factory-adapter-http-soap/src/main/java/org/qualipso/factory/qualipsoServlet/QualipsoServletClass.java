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

package org.qualipso.factory.qualipsoServlet;

import java.io.IOException;
import java.io.PrintWriter;

import java.security.Principal;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.Service;

import org.jboss.ws.core.jaxws.handler.SOAPMessageContextJAXWS;
import org.qualipso.factory.qualipsoServlet.api.manager.CRequestManager;
import org.qualipso.factory.qualipsoServlet.auth.soapHandler.QualipsoHandlerHeader;
import org.qualipso.factory.qualipsoServlet.soap.CBuilderSoapMessage;

/**
 * 
 * Servlet translator from http to soap request.
 * @author Emmanuel Meier & Thierry Deroff from Thales
 * 
 */
public class QualipsoServletClass extends HttpServlet 
{
	static final long serialVersionUID = 8228144296520589103L;
	//private PrintWriter outputPageHtml;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QualipsoServletClass() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException 
	{
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException 
	{
		try 
		{
			//response.getOutputStream().print(makePageServlet(request).WRITE_XML_DECLARATION);
			response.setContentType("text/xml");
			makePageServlet(request).writeTo(response.getOutputStream());
		}
		catch (SOAPException e)
		{
			e.printStackTrace();
			throw new ServletException("Error in soap request : " + e.getMessage());
		}
		catch (ServletException exception)
		{
			exception.printStackTrace();
			throw new IOException("Error in method of the servlet : " + exception.getMessage());
		}
	}
	
 	/**
	 * {@literalThose methods make and manage soap message from request REST... }
	 * @param HttpServletRequest
	 * @return void
	 * @exception ServletException and IOException also
	 */
 	protected SOAPMessage makePageServlet(HttpServletRequest request) 
 		throws ServletException, IOException
 	{
 		// Send information to make the translation to soap message :
 		try{
 			SOAPMessage resultat = new CBuilderSoapMessage().SendAndLoadServiceWeb(request);
 			return resultat;
 		}
 		catch(SOAPException e )
 		{
 			e.printStackTrace();
 			throw new ServletException("Error in soap object : " + e.getMessage());
 		}
 		/*catch(Exception except)
 		{
 			except.printStackTrace();
 			throw new IOException("Exception error in iostream : " + except.getMessage());
 		}
 		*/
 	}
 	
 	
}
