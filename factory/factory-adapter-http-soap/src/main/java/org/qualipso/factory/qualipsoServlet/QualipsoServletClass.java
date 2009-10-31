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

	/*
 	 * Method builtHeaderHtml make html code page for servlet
 	 */
 	protected void builtHeaderHtml(PrintWriter aStreamPageHtml) 
 	{
 		aStreamPageHtml.println("<html><head>" +
 				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
 				"<title>");
 		aStreamPageHtml.println("Response page from Servlet translator");
 		aStreamPageHtml.println("</title></head>");
 		aStreamPageHtml.println("<body>");
 		aStreamPageHtml.println("<br><center><b>------------------------------------------------<br>");
 		aStreamPageHtml.println("Start response from servlet <br>");
 		aStreamPageHtml.println("------------------------------------------------</b></center>");
 	}
 	
 	/*
 	 * Method builtEndBodyPageHtml make end of page html code
 	 */
 	protected void builtEndBodyPageHtml(PrintWriter aStreamPageHtml) 
 	{
 		aStreamPageHtml.println("<br><br>");
 		aStreamPageHtml.println("<center>");
 		aStreamPageHtml.println("<b>------------------------------------------------</b><br>");
 		aStreamPageHtml.println("<b>End of response message page</b>");
 		aStreamPageHtml.println("</center>");
 		aStreamPageHtml.println("</body>");
 		aStreamPageHtml.println("</html>");
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
 			SOAPMessage resultat = SendAndLoadServiceWeb(request);
 			return resultat;
 		}
 		catch(SOAPException e )
 		{
 			e.printStackTrace();
 			throw new ServletException("Error in soap object : " + e.getMessage());
 		}
 		catch(Exception except)
 		{
 			except.printStackTrace();
 			throw new IOException("Exception error in iostream : " + except.getMessage());
 		}
 	}
 	
 	/**
 	 * {@literal Method SendAndLoadServiceWeb send and built request to the Web Service
 	 * return output data from web service in XML format included in OMElement object.
 	 * We call the service:
 	 * It is unexpected operation and it is IN/OUT mode
 	 * The first XML format is build in Payload
 	 * "result" is the result of communication }
 	 * @param HttpServletRequest from the http request of methods of the servlet
 	 * @return SOAPEnvelope the soap response of the web service trhough axis2
 	 */
 	protected SOAPMessage SendAndLoadServiceWeb(HttpServletRequest request)
 		throws SOAPException,Exception
 	{
 		// build the envelope of the soap message :
 		SOAPMessage aSOAPresult;
 		
 		SOAPConnectionFactory SOAPfactory = SOAPConnectionFactory.newInstance();
 		SOAPConnection conn = SOAPfactory.createConnection();
 		
 		String aMsgInUrlWS = request.getParameter("FullPathURL");
 		
 		// management of security regarding the abstract service of the factory :
 		
 		String methodWS = request.getParameter("MethodName");
 		
 		if (methodWS == null)
 			throw new SOAPException ("Missing the Method Name of the service !");
 		
 		try
 		{
 			//From the http request, we build the soap request :
 			SOAPMessage SOAPMessageRequest = generateSOAP(request,methodWS);
 			
 			if(SOAPMessageRequest == null)
 				throw new SOAPException("The soap request is null ! Error in getParameters of http request.\n");
 			
 			// verify the soap request before send it :
 			System.out.println("Soap request : \n" + SOAPMessageRequest);
 			SOAPMessageRequest.writeTo(System.out);
 			System.out.println();
 			
 			// do the soap request :
 			aSOAPresult = conn.call(SOAPMessageRequest, aMsgInUrlWS);
 			
 			if(aSOAPresult == null)
 				throw new SOAPException("The soap response is null ! Error in web service response. \n");

 			// verify the soap response : any treatments are used on the soap response
 			System.out.println("Message receive : ");
 			aSOAPresult.writeTo(System.out);
 			System.out.println();
 		}
 		catch (SOAPException e)
 		{
 			System.out.println(e.getMessage());
 			conn.close();
 			throw new SOAPException(e.getMessage());
 		}
 		catch (IOException except)
 		{
 			System.out.println(except.getMessage());
 			conn.close();
 			throw new Exception(except.getMessage());
 		}
 		
 		//close the connection before leaving the method
 		conn.close();
 		
 		// return soap response of the Web Service
 		return aSOAPresult;
 	}
 	
 	/**
 	 * {@literal This method built the soap message with OMElement Objects !}
 	 * @param request is the HttpServletRequest from the client
 	 * @param aMethod is the function name used in web service
 	 * @return is a Soap Message request
 	 */
 	private SOAPMessage generateSOAP(HttpServletRequest request, String aMethod) throws SOAPException
 	{
 		SOAPMessage MessageSoap = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage();
 		SOAPEnvelope envelope = MessageSoap.getSOAPPart().getEnvelope();
 		
 		
 		// read authentication information and set the handler for WS-security
 		try{
 			System.out.println("Enter in wsse part :");
 			envelope = this.moduleWSSE(request,envelope);
 		}
 		catch(SOAPException soapException)
 		{
 			throw new SOAPException("Error in read/write in the header : "+soapException.getMessage());
 		}
 		catch(ServletException except)
 		{
 			throw new SOAPException("The http request header does not fill credentials : " + except.getMessage());
 		}
 		catch(IOException e)
 		{
 			throw new SOAPException("Error in authentication process of the servlet : "+e.getMessage());
 		}
 		
 		// define name spaces :
		String aNameSpaceSoap = request.getParameter("aFullNSurl");
		String aNameSpaceXsdSoap = request.getParameter("aFullXsdUrl");
 		
		// verifying parameters :
 		if (aNameSpaceSoap == null )
 		{
 			// default value for name space:
 			aNameSpaceSoap = "http://ws.apache.org/axis2";
 		}
 		else
 		{
 			if(! (aNameSpaceSoap.length() > 1))
 			{
 				aNameSpaceSoap = "http://ws.apache.org/axis2";
 			}
 		}

 		if (aNameSpaceXsdSoap == null )
 		{
 			aNameSpaceXsdSoap = aNameSpaceSoap + "/xsd";
 		}
 		else
 		{
 			if(!(aNameSpaceXsdSoap.length() > 1))
 			{
 				aNameSpaceXsdSoap = aNameSpaceSoap + "/xsd";
 			}
 		}
 		//################################
 		// build nodes for soap body :
 		//################################
 		String omNS = aNameSpaceSoap;
 		String omNSComplex = aNameSpaceXsdSoap;
 		
 		envelope.addNamespaceDeclaration("ns", omNS);
 		
 		//MimeHeaders aMimeHeader = MessageSoap.getMimeHeaders();
 		//aMimeHeader.addHeader("SOAPAction", "urn:"+ aMethod);
 		
 		//System.out.println("Envelope test = " + envelope.getNamespaceURI());
 		
 		envelope.addNamespaceDeclaration("xsd", omNSComplex);
 		envelope.addNamespaceDeclaration("xsi","http://www.w3.org/2001/XMLSchema-instance");
 		envelope.addNamespaceDeclaration("enc","http://schemas.xmlsoap.org/soap/encoding/");
 		envelope.addNamespaceDeclaration("env","http://schemas.xmlsoap.org/soap/envelope/");
 		envelope.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");

 		SOAPBody body = envelope.getBody();
 		System.out.println("body test = " + body);

 		SOAPElement operation = envelope.addChildElement(aMethod,"ns");
 		SOAPElement aBodyElement = body.addChildElement(operation);
 		
 		// build the soap body request
 		CRequestManager manager= new CRequestManager();
 		manager.processRequest(request, aBodyElement);
 		/*
 		// data attachment
 		if (aTagAttachment == true)
 		{
 			MessageSoap.saveChanges();
 			
 			MessageSoap.createAttachmentPart(datahandler);
 		}
 		*/
 		MessageSoap.saveChanges();
 		
 		return MessageSoap;
 	} 	
 	
 	/**
 	 * Build the module WS-security in soap header
 	 * @param request is the http request from gadgets
 	 * @param envelope is the envolope of the soap message to send
 	 * @return a SOAPEnvelope object with wsse tags
 	 * @throws SOAPException is thrown when one node of the soap header is null
 	 * @throws ServletException is thrown when information missed in http header
 	 * @throws IOException is thrown in any case with file config...
 	 */
 	protected SOAPEnvelope moduleWSSE(HttpServletRequest request, SOAPEnvelope envelope) 
 		throws SOAPException, ServletException, IOException
 	{
 		//SOAPHeader theHeader = envelope.addHeader();
 		
 		// is the user authenticated ?
 		Principal aUserLogin = request.getUserPrincipal();
 		
 		String aLogin;
 		String aPasswd;
 		
 		if(aUserLogin == null)
 		{
 			// define the end point reference of the service, load the context service
 			aLogin = request.getHeader("Qualipsouser");
 			if(aLogin == null)
 			{
 				// only for the version beta ! Delete it after demo...
 				aLogin = "kermit";
 				// throw new ServletException("Error missing \"qualipsouser\" in http request !");
 			}
 		}
 		else
 		{
 			System.out.println("The username = "+aUserLogin.getName());
 			// input information in jaas module :
 			aLogin = aUserLogin.getName();
 		}
 		
 		aPasswd = request.getHeader("Password");
 		
 		// Only for the version beta to test wsse !! To delete afterward demo...
 		if(aPasswd == null)
 			aPasswd = "thefrog";
 		
 		if (aPasswd == null)
 			throw new ServletException("Error missing \"password\" in http request !");
 		
 		// build the header with wsse username passwordDigest token, nonce= true, timestamp=true
 		String EPR_wsdl = request.getParameter("FullPathURL");
 		if(EPR_wsdl.endsWith("?wsdl")==false)
 			EPR_wsdl += "?wsdl";
 		
 		String targetNS = request.getParameter("targetName");
 		String serviceName = request.getParameter("serviceName");
 		
 		if (targetNS==null || serviceName==null)
 		{
 			//use the HandlerHeader to build wsse
 			System.out.println("Build the header wsse : ");
 			QualipsoHandlerHeader aHandler = new QualipsoHandlerHeader();
 			aHandler.setCredentials(aLogin, aPasswd);
 			
 			try{
 				System.out.println("Hander Header = " + aHandler.toString());
 				aHandler.insertWSSEheader(envelope);
 				// write the envelope to verify header :
 				System.out.println("soap envelope : "+envelope);
 			}
 			catch(Exception except)
 			{
 				except.printStackTrace();
 			}
 		}
 		else
 		{
 			// use Jax-WS and jboss library to build wsse
 			QName qnService = new QName(targetNS,serviceName);
 			Service service = Service.create(new URL(EPR_wsdl),qnService);
 			// pb with stubs classes for each services 
 		}
 		
 		/*
 		WSSecurityHandlerClient aHandler = new WSSecurityHandlerClient();
 		aHandler.handleMessage(msgContext);
 		
 		
 		String serviceName = EPR_wsdl.;
 		
 		URL url = new URL(EPR_wsdl);
 		QName qn = new QName(request.getParameter("aTargetName"),request.getParameter("aServiceName"));
 		Service clientService = Service.create(url, qn);
 		clientService.
		((StubExt)port).setSecurityConfig(securityURL.toExternalForm());
		((StubExt)port).setConfigName("Standard WSSecurity Client");
		((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "kermit");
		((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "thefrog");
		*/
 		return envelope;
 	}
}
