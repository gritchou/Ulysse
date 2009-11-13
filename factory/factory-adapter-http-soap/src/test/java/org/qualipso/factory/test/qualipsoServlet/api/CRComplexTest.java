package org.qualipso.factory.test.qualipsoServlet.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

import org.jboss.ws.core.soap.SOAPBodyImpl;
import org.junit.Test;
import org.qualipso.factory.qualipsoServlet.api.factory.IRequestProcessor;
import org.qualipso.factory.qualipsoServlet.api.factory.IRequestProcessorFactory;
import org.qualipso.factory.qualipsoServlet.impl.factory.CComplexRequestProcessor;
import org.qualipso.factory.qualipsoServlet.impl.factory.CRequestProcessorFactoryImpl;
import org.qualipso.factory.qualipsoServlet.impl.factory.CSimpleRequestProcessor;
import org.qualipso.factory.qualipsoServlet.impl.factory.CTableRequestProcessor;

public class CRComplexTest{
	
	private static Log logger = LogFactory.getLog(CRComplexTest.class);
	
	public CRComplexTest()
	{
		
	}
	
	/*
	 * JUnit for different arguments
	 * Complex type arguments, message skeleton of the SOAPElement:
	 * <body>
	 * 	<arg0>test</arg0>
	 * 	<complex>
	 * 		<SubArg1>toto</SubArg1>
	 * 		<SubArg2>titi</SubArg2>
	 * 		<SubArg3>tutu</SubArg3>
	 * 	</complex>
	 *	<arg2>mimi</arg2>
	 * </body>
	 */
	@Test
	public void testCComplexRequest()
	{
		IRequestProcessorFactory factory= new CRequestProcessorFactoryImpl();
		Hashtable <String,Vector<String>> HtSimple1 = new Hashtable<String,Vector<String>>();
		Hashtable <String,Vector<String>> HtComplex2 = new Hashtable<String,Vector<String>>();
		Hashtable <String,Vector<String>> HtSimple3 = new Hashtable<String,Vector<String>>();
		CComplexRequestProcessor aComplexRequest;
		
		SOAPBody bodySOAP = new SOAPBodyImpl("env","http://schemas.xmlsoap.org/soap/body");
		
		// fill the Hastable with simple arguments :
		Vector<String> vector1 = new Vector<String>();
		vector1.add("test");
		HtSimple1.put("arg0", vector1);		
		
		Vector<String> vector2complex1 = new Vector<String>();
		vector2complex1.add("toto");
		Vector<String> vector2complex2 = new Vector<String>();
		vector2complex2.add("titi");
		Vector<String> vector2complex3 = new Vector<String>();
		vector2complex3.add("tutu");
		
		HtComplex2.put("SubArg1",vector2complex1);
		HtComplex2.put("SubArg2",vector2complex2);
		HtComplex2.put("SubArg3",vector2complex3);
		
		aComplexRequest = new CComplexRequestProcessor(HtComplex2);
		
		Vector<String> vector3 = new Vector<String>();
		vector3.add("mimi");
		HtSimple3.put("arg2", vector3);
		
		// test the object with soap generation:	
		
		try{
			// generation of the soapbody :			
			bodySOAP.addChildElement(factory.discriminant(HtSimple1).generateSOAP());
			bodySOAP.addChildElement(factory.discriminant(HtComplex2).generateSOAP());
			bodySOAP.addChildElement(factory.discriminant(HtSimple3).generateSOAP());
			
			// read value and attributes
			assertNotNull(bodySOAP);
			
			assertNotNull(bodySOAP.getElementsByTagName("arg0"));
			assertNotNull(bodySOAP.getElementsByTagName("arg1"));
			assertNotNull(bodySOAP.getElementsByTagName("arg2"));
			
			assertEquals(3,bodySOAP.getChildNodes().getLength());
			assertEquals("arg0",bodySOAP.getChildNodes().item(0).getNodeName());
			assertEquals("test",bodySOAP.getChildNodes().item(0).getFirstChild().getNodeValue());
			
			assertEquals("complex",bodySOAP.getChildNodes().item(1).getNodeName());
			
			assertEquals(3,bodySOAP.getChildNodes().item(1).getChildNodes().getLength());
			assertEquals("SubArg3",bodySOAP.getChildNodes().item(1).getChildNodes().item(0).getNodeName());
			assertEquals("SubArg2",bodySOAP.getChildNodes().item(1).getChildNodes().item(1).getNodeName());
			assertEquals("SubArg1",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getNodeName());
			assertEquals("tutu",bodySOAP.getChildNodes().item(1).getChildNodes().item(0).getFirstChild().getNodeValue());
			assertEquals("titi",bodySOAP.getChildNodes().item(1).getChildNodes().item(1).getFirstChild().getNodeValue());
			assertEquals("toto",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getFirstChild().getNodeValue());
						
			assertEquals("arg2",bodySOAP.getChildNodes().item(2).getNodeName());
			assertEquals("mimi",bodySOAP.getChildNodes().item(2).getFirstChild().getNodeValue());
		
		}
		catch (SOAPException exception)
		{
			fail("Test simple argument : "+exception.getMessage()+"\n");
		}
	}
	
	/*
	 * JUnit for different arguments
	 * subComplex type arguments, message skeleton of the SOAPElement:
	 * <arg0>test</arg0>
	 * <arg1>
	 * 		<SubArg1>toto</SubArg1>
	 * 		<SubArg2>
	 * 			<SSub1>vivi</SSub1>
	 * 			<SSub2>nini</SSub2>
	 *  	</SubArg2>
	 * 		<SubArg3>tutu</SubArg3>
	 * </arg1>
	 * <arg2>mimi</arg2>
	 */
	@Test
	public void testCsubComplex1Request()
	{
		IRequestProcessorFactory factory= new CRequestProcessorFactoryImpl();
		Hashtable <String,Vector<String>> HtSimple1 = new Hashtable<String,Vector<String>>();
		Hashtable <String,Vector<String>> HtComplex2 = new Hashtable<String,Vector<String>>();
		Hashtable <String,Vector<String>> HtSubComplex = new Hashtable<String,Vector<String>>();
		Hashtable <String,Vector<String>> HtSimple3 = new Hashtable<String,Vector<String>>();
		CComplexRequestProcessor aComplexRequest,aSubComplexObject;
		
		SOAPBody bodySOAP = new SOAPBodyImpl("env","http://schemas.xmlsoap.org/soap/body");
		
		// fill the Hastable with simple arguments :
		Vector<String> vector1 = new Vector<String>();
		vector1.add("test");
		HtSimple1.put("arg0", vector1);		
		
		Vector<String> vector2complex1 = new Vector<String>();
		vector2complex1.add("toto");
		
		Vector<String> vector2subcomplex1 = new Vector<String>();
		vector2subcomplex1.add("vivi");
		Vector<String> vector2subcomplex2 = new Vector<String>();
		vector2subcomplex2.add("nini");
		
		HtSubComplex.put("SSub1", vector2subcomplex1);
		HtSubComplex.put("SSub2", vector2subcomplex2);
		
		aSubComplexObject = new CComplexRequestProcessor(HtSubComplex);
		aSubComplexObject.setAComplexTypeName("SubArg2");
		
		Vector<String> vector2complex3 = new Vector<String>();
		vector2complex3.add("tutu");
		
		HtComplex2.put("SubArg1",vector2complex1);
		HtComplex2.put("SubArg3",vector2complex3);
		
		aComplexRequest = new CComplexRequestProcessor(HtComplex2);
		aComplexRequest.setAComplexTypeName("arg1");
		aComplexRequest.addChildrenCComplex(aSubComplexObject);
		
		Vector<String> vector3 = new Vector<String>();
		vector3.add("mimi");
		HtSimple3.put("arg2", vector3);
		
		// test the object with soap generation:	
		
		try{
			// generation of the soapbody :			
			bodySOAP.addChildElement(factory.discriminant(HtSimple1).generateSOAP());
			bodySOAP.addChildElement(aComplexRequest.generateSOAP());
			bodySOAP.addChildElement(factory.discriminant(HtSimple3).generateSOAP());
			
			// read value and attributes
			assertNotNull(bodySOAP);
			
			assertNotNull(bodySOAP.getElementsByTagName("arg0"));
			assertNotNull(bodySOAP.getElementsByTagName("arg1"));
			assertNotNull(bodySOAP.getElementsByTagName("arg2"));
			
			assertEquals(3,bodySOAP.getChildNodes().getLength());
			assertEquals("arg0",bodySOAP.getChildNodes().item(0).getNodeName());
			assertEquals("test",bodySOAP.getChildNodes().item(0).getFirstChild().getNodeValue());
			
			assertEquals("arg1",bodySOAP.getChildNodes().item(1).getNodeName());
			assertEquals(3,bodySOAP.getChildNodes().item(1).getChildNodes().getLength());
			
			assertEquals("SubArg3",bodySOAP.getChildNodes().item(1).getChildNodes().item(0).getNodeName());
			assertEquals("SubArg1",bodySOAP.getChildNodes().item(1).getChildNodes().item(1).getNodeName());
			assertEquals("SubArg2",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getNodeName());
			assertEquals("tutu",bodySOAP.getChildNodes().item(1).getChildNodes().item(0).getFirstChild().getNodeValue());
			assertEquals("toto",bodySOAP.getChildNodes().item(1).getChildNodes().item(1).getFirstChild().getNodeValue());
			
			assertEquals("SSub2",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(0).getNodeName());
			assertEquals("SSub1",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(1).getNodeName());
			assertEquals("nini",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(0).getFirstChild().getNodeValue());
			assertEquals("vivi",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(1).getFirstChild().getNodeValue());			
			
			assertEquals("arg2",bodySOAP.getChildNodes().item(2).getNodeName());
			assertEquals("mimi",bodySOAP.getChildNodes().item(2).getFirstChild().getNodeValue());
		
		}
		catch (SOAPException exception)
		{
			fail("Test simple argument : "+exception.getMessage()+"\n");
		}
	}
	
	/*
	 * JUnit for different arguments
	 * subsubsub complex type arguments, message skeleton of the SOAPElement:
	 * <arg0>test</arg0>
	 * <arg1>
	 * 	 <SubArg1>toto</SubArg1>
	 * 	 <SubArg2>
	 * 		<SSub1>
	 * 			<SSSub1>wiwi</SSSub1>
	 * 			<SSSub2>wowo</SSSub2>
	 * 		</SSub1>
	 * 		<SSub2>nini</SSub2>
	 * 	 <SubArg3>tutu</SubArg3>
	 * </arg1>
	 * <arg2>mimi</arg2>
	 */
	@Test
	public void testCsubComplex2Request()
	{
		IRequestProcessorFactory factory= new CRequestProcessorFactoryImpl();
		Hashtable <String,Vector<String>> HtSimple1 = new Hashtable<String,Vector<String>>();
		Hashtable <String,Vector<String>> HtComplex2 = new Hashtable<String,Vector<String>>();
		//Hashtable <String,Vector<String>> HtSubComplex = new Hashtable<String,Vector<String>>();
		Hashtable <String,Vector<String>> HtSSubComplex = new Hashtable<String,Vector<String>>();
		Hashtable <String,Vector<String>> HtSSSubComplex = new Hashtable<String,Vector<String>>();
		Hashtable <String,Vector<String>> HtSimple3 = new Hashtable<String,Vector<String>>();
		CComplexRequestProcessor aComplexRequest,aSubComplexObject;
		CComplexRequestProcessor aSSubComplexObject,aSSSubComplexObject;
		
		SOAPBody bodySOAP = new SOAPBodyImpl("env","http://schemas.xmlsoap.org/soap/body");
		
		// fill the Hastable with simple arguments :
		Vector<String> vector1 = new Vector<String>();
		vector1.add("test");
		HtSimple1.put("arg0", vector1);		
		
		Vector<String> vector2complex1 = new Vector<String>();
		vector2complex1.add("toto");
		Vector<String> vector2complex3 = new Vector<String>();
		vector2complex3.add("tutu");
		
		HtComplex2.put("SubArg1",vector2complex1);
		HtComplex2.put("SubArg3",vector2complex3);
		
		Vector<String> vector2subcomplex1 = new Vector<String>();
		vector2subcomplex1.add("wiwi");
		Vector<String> vector2subcomplex2 = new Vector<String>();
		vector2subcomplex2.add("wowo");
		
		HtSSSubComplex.put("SSSub1",vector2subcomplex1);
		HtSSSubComplex.put("SSSub2",vector2subcomplex2);
		aSSSubComplexObject = new CComplexRequestProcessor(HtSSSubComplex);
		aSSSubComplexObject.setAComplexTypeName("SSub1");
		
		Vector<String> vector2subcomplex3 = new Vector<String>();
		vector2subcomplex3.add("nini");
		HtSSubComplex.put("SSub2",vector2subcomplex3);		
		
		aSSubComplexObject = new CComplexRequestProcessor(HtSSubComplex);
		aSSubComplexObject.setAComplexTypeName("SubArg2");
		aSSubComplexObject.addChildrenCComplex(aSSSubComplexObject);
		
		aSubComplexObject = new CComplexRequestProcessor(HtComplex2);
		aSubComplexObject.setAComplexTypeName("arg1");
		aSubComplexObject.addChildrenCComplex(aSSubComplexObject);
		
		/*
		aComplexRequest = new CComplexRequestProcessor(HtComplex2);
		aComplexRequest.setAComplexTypeName("arg1");
		aComplexRequest.addChildrenCComplex(aSubComplexObject);
		*/
		
		Vector<String> vector3 = new Vector<String>();
		vector3.add("mimi");
		HtSimple3.put("arg2", vector3);
		
		// test the object with soap generation:	
		
		try{
			// generation of the soapbody :			
			bodySOAP.addChildElement(factory.discriminant(HtSimple1).generateSOAP());
			bodySOAP.addChildElement(aSubComplexObject.generateSOAP());
			bodySOAP.addChildElement(factory.discriminant(HtSimple3).generateSOAP());
			
			// read value and attributes
			assertNotNull(bodySOAP);
			
			assertNotNull(bodySOAP.getElementsByTagName("arg0"));
			assertNotNull(bodySOAP.getElementsByTagName("arg1"));
			assertNotNull(bodySOAP.getElementsByTagName("arg2"));
			
			assertEquals(3,bodySOAP.getChildNodes().getLength());
			assertEquals("arg0",bodySOAP.getChildNodes().item(0).getNodeName());
			assertEquals("test",bodySOAP.getChildNodes().item(0).getFirstChild().getNodeValue());
			assertEquals("arg2",bodySOAP.getChildNodes().item(2).getNodeName());
			assertEquals("mimi",bodySOAP.getChildNodes().item(2).getFirstChild().getNodeValue());
			
			assertEquals("arg1",bodySOAP.getChildNodes().item(1).getNodeName());
			
			assertEquals("SubArg3",bodySOAP.getChildNodes().item(1).getChildNodes().item(0).getNodeName());
			assertEquals("SubArg1",bodySOAP.getChildNodes().item(1).getChildNodes().item(1).getNodeName());
			assertEquals("SubArg2",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getNodeName());
			
			assertEquals("tutu",bodySOAP.getChildNodes().item(1).getChildNodes().item(0).getFirstChild().getNodeValue());
			assertEquals("toto",bodySOAP.getChildNodes().item(1).getChildNodes().item(1).getFirstChild().getNodeValue());
			
			assertEquals("SSub2",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(0).getNodeName());
			assertEquals("SSub1",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(1).getNodeName());
			assertEquals("nini",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(0).getFirstChild().getNodeValue());
			
			assertEquals("SSSub1",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(1).getChildNodes().item(0).getNodeName());
			assertEquals("SSSub2",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(1).getChildNodes().item(1).getNodeName());
			assertEquals("wiwi",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(1).getChildNodes().item(0).getFirstChild().getNodeValue());			
			assertEquals("wowo",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(1).getChildNodes().item(1).getFirstChild().getNodeValue());
			
		}
		catch (SOAPException exception)
		{
			fail("Test complex argument level 2 : "+exception.getMessage()+"\n");
		}
		catch(Exception e)
		{
			System.err.println("Error in "+e.getMessage());
		}
	}
	
	/*
	 * JUnit for different arguments
	 * complex type with arraylist arguments, message skeleton of the SOAPElement:
	 * <arg0>test</arg0>
	 * <arg1>
	 * 	<SubArg1>toto</SubArg1>
	 * 	<SubArg2>
	 * 		<SSub1>
	 * 			<item>wiwi</item>
	 * 			<item>wowo</item>
	 * 		</SSub1>
	 * 		<SSub2>nini</SSub2>
	 * 	</SubArg2>
	 * 	<SubArg3>tutu</SubArg3>
	 * </arg1>
	 * <arg2>mimi</arg2>
	 */
	@Test
	public void testCsubComplex3Request()
	{
		IRequestProcessorFactory factory= new CRequestProcessorFactoryImpl();
		Hashtable <String,Vector<String>> HtSimple1 = new Hashtable<String,Vector<String>>();
		Hashtable <String,Vector<String>> HtComplex2 = new Hashtable<String,Vector<String>>();
		Hashtable <String,Vector<String>> HtSubComplex = new Hashtable<String,Vector<String>>();
		Hashtable <String,Vector<String>> HtTable = new Hashtable<String,Vector<String>>();
		Hashtable <String,Vector<String>> HtSimple3 = new Hashtable<String,Vector<String>>();
		CComplexRequestProcessor aComplexRequest,aSubComplexObject;
		CTableRequestProcessor aTComplexObject;
		
		SOAPBody bodySOAP = new SOAPBodyImpl("env","http://schemas.xmlsoap.org/soap/body");
		
		// fill the Hastable with simple arguments :
		Vector<String> vector1 = new Vector<String>();
		vector1.add("test");
		HtSimple1.put("arg0", vector1);		
		
		Vector<String> vector2complex1 = new Vector<String>();
		vector2complex1.add("toto");
		Vector<String> vector2complex3 = new Vector<String>();
		vector2complex3.add("tutu");
		
		Vector<String> vector2subcomplex1 = new Vector<String>();
		vector2subcomplex1.add("wiwi");
		vector2subcomplex1.add("wowo");
		
		Vector<String> vector2subcomplex2 = new Vector<String>();
		vector2subcomplex2.add("nini");
		
		HtSubComplex.put("SSub2",vector2subcomplex2);
		
		HtTable.put("SSub1", vector2subcomplex1);
		aTComplexObject = new CTableRequestProcessor(HtTable);
		
		aSubComplexObject = new CComplexRequestProcessor(HtSubComplex);
		aSubComplexObject.setAComplexTypeName("SubArg2");
		aSubComplexObject.addChildrenCComplex(aTComplexObject);
		
		
		HtComplex2.put("SubArg1",vector2complex1);
		HtComplex2.put("SubArg3",vector2complex3);
		
		aComplexRequest = new CComplexRequestProcessor(HtComplex2);
		aComplexRequest.setAComplexTypeName("arg1");
		aComplexRequest.addChildrenCComplex(aSubComplexObject);
		
		Vector<String> vector3 = new Vector<String>();
		vector3.add("mimi");
		HtSimple3.put("arg2", vector3);
		
		// test the object with soap generation:	
		
		try{
			// generation of the soapbody :			
			bodySOAP.addChildElement(factory.discriminant(HtSimple1).generateSOAP());
			bodySOAP.addChildElement(aComplexRequest.generateSOAP());
			bodySOAP.addChildElement(factory.discriminant(HtSimple3).generateSOAP());
			
			// read value and attributes
			assertNotNull(bodySOAP);
			
			assertNotNull(bodySOAP.getElementsByTagName("arg0"));
			assertNotNull(bodySOAP.getElementsByTagName("arg1"));
			assertNotNull(bodySOAP.getElementsByTagName("arg2"));
			
			assertEquals(3,bodySOAP.getChildNodes().getLength());
			assertEquals("arg0",bodySOAP.getChildNodes().item(0).getNodeName());
			assertEquals("test",bodySOAP.getChildNodes().item(0).getFirstChild().getNodeValue());
			
			assertEquals("arg1",bodySOAP.getChildNodes().item(1).getNodeName());
			assertEquals(3,bodySOAP.getChildNodes().item(1).getChildNodes().getLength());
			
			assertEquals("SubArg3",bodySOAP.getChildNodes().item(1).getChildNodes().item(0).getNodeName());
			assertEquals("SubArg1",bodySOAP.getChildNodes().item(1).getChildNodes().item(1).getNodeName());
			assertEquals("SubArg2",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getNodeName());
			assertEquals("tutu",bodySOAP.getChildNodes().item(1).getChildNodes().item(0).getFirstChild().getNodeValue());
			assertEquals("toto",bodySOAP.getChildNodes().item(1).getChildNodes().item(1).getFirstChild().getNodeValue());
			
			assertEquals("SSub2",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(0).getNodeName());
			assertEquals("SSub1",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(1).getNodeName());
			assertEquals("nini",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(0).getFirstChild().getNodeValue());
			
			assertEquals("item",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(1).getChildNodes().item(0).getNodeName());
			assertEquals("wiwi",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(1).getChildNodes().item(0).getFirstChild().getNodeValue());			
			assertEquals("wowo",bodySOAP.getChildNodes().item(1).getChildNodes().item(2).getChildNodes().item(1).getChildNodes().item(1).getFirstChild().getNodeValue());
			
			assertEquals("arg2",bodySOAP.getChildNodes().item(2).getNodeName());
			assertEquals("mimi",bodySOAP.getChildNodes().item(2).getFirstChild().getNodeValue());
		
		}
		catch (SOAPException exception)
		{
			fail("Test simple argument : "+exception.getMessage()+"\n");
		}
	}
}
