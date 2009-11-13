package org.qualipso.factory.test.qualipsoServlet.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SAAJMetaFactory;
import javax.xml.soap.SOAPBody ;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ws.core.soap.SOAPBodyImpl;

import org.junit.Test;
import org.qualipso.factory.qualipsoServlet.api.factory.IRequestProcessor;
import org.qualipso.factory.qualipsoServlet.api.factory.IRequestProcessorFactory;
import org.qualipso.factory.qualipsoServlet.impl.factory.CRequestProcessorFactoryImpl;
import org.qualipso.factory.qualipsoServlet.impl.factory.CSimpleRequestProcessor;


public class CRSimpleTest{
	
	private static Log logger = LogFactory.getLog(CRSimpleTest.class);
	
	public CRSimpleTest(){}
	
	/*
	 * JUnit for different arguments
	 * Simple arguments, message skeleton of the SOAPElement: 
	 * <body>
	 * 	<arg0>1</arg0>
	 * 	<arg1>toto</arg1>
	 * 	<arg2>titi</arg2>
	 * </body>
	 * This test verify simple arguments in soap request
	 */
	
	@Test
	public void testCSimpleRequest()
	{
		IRequestProcessorFactory factory= new CRequestProcessorFactoryImpl();
		Hashtable <String,Vector<String>> HtSimple1 = new Hashtable<String,Vector<String>>();
		Hashtable <String,Vector<String>> HtSimple2 = new Hashtable<String,Vector<String>>();
		Hashtable <String,Vector<String>> HtSimple3 = new Hashtable<String,Vector<String>>();
		
		SOAPBody bodySOAP = new SOAPBodyImpl("env","http://schemas.xmlsoap.org/soap/body");
		
		// fill the Hastable with simple arguments :
		Vector<String> vector1 = new Vector<String>();
		vector1.add("1");
		HtSimple1.put("arg0", vector1);		
		
		Vector<String> vector2 = new Vector<String>();
		vector2.add("toto");
		HtSimple2.put("arg1", vector2);
		
		Vector<String> vector3 = new Vector<String>();
		vector3.add("titi");
		HtSimple3.put("arg2", vector3);
		
		// test the object with soap generation:	
		
		try{
			// generation of the soapbody :			
			bodySOAP.addChildElement(factory.discriminant(HtSimple1).generateSOAP());
			bodySOAP.addChildElement(factory.discriminant(HtSimple2).generateSOAP());
			bodySOAP.addChildElement(factory.discriminant(HtSimple3).generateSOAP());
			
			// read value and attributes
			assertNotNull(bodySOAP);
			
			assertNotNull(bodySOAP.getElementsByTagName("arg0"));
			assertNotNull(bodySOAP.getElementsByTagName("arg1"));
			assertNotNull(bodySOAP.getElementsByTagName("arg2"));
			
			assertEquals(3,bodySOAP.getChildNodes().getLength());
			assertEquals("arg0",bodySOAP.getChildNodes().item(0).getNodeName());
			assertEquals("1",bodySOAP.getChildNodes().item(0).getFirstChild().getNodeValue());
			
			assertEquals("arg1",bodySOAP.getChildNodes().item(1).getNodeName());
			assertEquals("toto",bodySOAP.getChildNodes().item(1).getFirstChild().getNodeValue());
			
			assertEquals("arg2",bodySOAP.getChildNodes().item(2).getNodeName());
			assertEquals("titi",bodySOAP.getChildNodes().item(2).getFirstChild().getNodeValue());
		
		}
		catch (SOAPException exception)
		{
			fail("Test simple argument : "+exception.getMessage()+"\n");
		}
	}
}
