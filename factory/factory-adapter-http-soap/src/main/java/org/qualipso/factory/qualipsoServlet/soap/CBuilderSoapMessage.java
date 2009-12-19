package org.qualipso.factory.qualipsoServlet.soap;

import java.io.IOException;
import java.net.URL;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.qualipso.factory.qualipsoServlet.api.manager.CRequestManager;
import org.qualipso.factory.qualipsoServlet.auth.soapHandler.QualipsoHandlerHeader;

/**
 * 
 * @author Emmanuel Meier from Thales Services
 *
 */

public class CBuilderSoapMessage 
{
	private static final String NAMESPACE_DEFAULT_AXIS2 = "http://ws.apache.org/axis2";
	private static final String XML_SOAP_ENVELOPE = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String XML_SOAP_ENCODING = "http://schemas.xmlsoap.org/soap/encoding/";
	private static final String XML_SOAP_MIME = "http://schemas.xmlsoap.org/wsdl/mime/";
	private static final String XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
	private static final String XML_SCHEMA_XSD = "http://www.w3.org/2001/XMLSchema";
	
	private SOAPConnectionFactory soapFactory;
	
	private static Log logger = LogFactory.getLog(CBuilderSoapMessage.class);
	
	public CBuilderSoapMessage()
	{
		try{
			soapFactory = SOAPConnectionFactory.newInstance();
		}
		catch (UnsupportedOperationException exception)
		{
			logger.error("Error, none operation as SOAPConnection can not be instantiated : " + exception.getMessage());
		}
		catch (SOAPException soapExcept)
		{
			logger.error("Error in soap object !" + soapExcept.getMessage());
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
 	 * @return SOAPEnvelope the soap response of the web service through axis2
 	 */
 	public SOAPMessage SendAndLoadServiceWeb(HttpServletRequest request)
 		throws SOAPException
 	{
 		// build the envelope of the soap message :
 		SOAPMessage aSOAPresult = null;
 		SOAPConnection conn;
 		if(soapFactory != null)
 			conn = soapFactory.createConnection();
 		else
 			conn = SOAPConnectionFactory.newInstance().createConnection();
 		
 		String aMsgInUrlWS = request.getParameter("FullPathURL");
 		if(aMsgInUrlWS == null)
 			throw new SOAPException ("Missing the EndPoint Reference of the service !");
 		
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
 			logger.error(e.getMessage());
 			conn.close();
 			throw new SOAPException(e.getMessage());
 		}
 		catch(IOException except)
 		{
 			conn.close();
 		}
 		finally
 		{
 			conn.close();
 		}
 		
 		// return soap response of the Web Service
 		return aSOAPresult;
 	}
 	
 	/**
 	 * {@literal This method built the soap message with OMElement Objects !}
 	 * @param request is the HttpServletRequest from the client
 	 * @param aMethod is the function name used in web service
 	 * @return is a Soap Message request
 	 */
 	private SOAPMessage generateSOAP(HttpServletRequest request, String aMethod) 
 		throws SOAPException
 	{
 		SOAPMessage MessageSoap = MessageFactory.newInstance().createMessage();
 		SOAPEnvelope envelope = MessageSoap.getSOAPPart().getEnvelope();
 		
 		
 		// read authentication information and set the handler for WS-security
 		try{
 			logger.info("Enter in wsse part :");
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
 			aNameSpaceSoap = CBuilderSoapMessage.NAMESPACE_DEFAULT_AXIS2;
 		}
 		else
 		{
 			if(! (aNameSpaceSoap.length() > 1))
 			{
 				aNameSpaceSoap = CBuilderSoapMessage.NAMESPACE_DEFAULT_AXIS2;
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
 				aNameSpaceXsdSoap = CBuilderSoapMessage.XML_SCHEMA_XSD;
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
 		
 		envelope.addNamespaceDeclaration("xsd", omNSComplex);
 		envelope.addNamespaceDeclaration("xsi",CBuilderSoapMessage.XML_SCHEMA_INSTANCE);
 		envelope.addNamespaceDeclaration("enc",CBuilderSoapMessage.XML_SOAP_ENCODING);
 		envelope.addNamespaceDeclaration("env",CBuilderSoapMessage.XML_SOAP_ENVELOPE);
 		envelope.addNamespaceDeclaration("mime",CBuilderSoapMessage.XML_SOAP_MIME);
 		envelope.setEncodingStyle(CBuilderSoapMessage.XML_SOAP_ENCODING);

 		SOAPBody body = envelope.getBody();
 		SOAPElement operation,aBodyElement;
 		operation = envelope.addChildElement(aMethod,"ns");
 		aBodyElement = body.addChildElement(operation);
 		
 		// build the soap body request
 		CRequestManager manager= new CRequestManager();
 		
 		// define the type of http content
 		logger.info("Will manage the attachment or nor....");
 		try{
 			//MessageSoap.saveChanges();
 			logger.info("Soap attachment before : "+MessageSoap.toString());
 			manager.processRequestWithAttach(request, MessageSoap);
 			logger.info("After attachment : "+ MessageSoap.toString());
 		}
 		catch(SOAPException exception)
 		{
 			logger.debug("No attachment in soap message");
 			manager.processRequest(request, aBodyElement);
 			logger.debug("body soap = "+MessageSoap.toString());
 		}
 		
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
 				aLogin = "guest";
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
 			aPasswd = "guest";
 		
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
 			logger.info("Build the header wsse : ");
 			QualipsoHandlerHeader aHandler = new QualipsoHandlerHeader();
 			aHandler.setCredentials(aLogin, aPasswd);
 			
 			try{
 				logger.info("Hander Header = " + aHandler.toString());
 				aHandler.insertWSSEheader(envelope);
 				// write the envelope to verify header :
 				System.out.println("soap envelope : "+envelope);
 			}
 			catch(Exception except)
 			{
 				logger.info("Erreur in header saop wsse : "+except);
 				//except.printStackTrace();
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
