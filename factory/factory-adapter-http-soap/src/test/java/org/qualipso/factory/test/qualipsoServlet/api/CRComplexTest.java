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

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

import org.junit.Test;
import org.qualipso.factory.qualipsoServlet.api.factory.IRequestProcessor;
import org.qualipso.factory.qualipsoServlet.impl.factory.CComplexRequestProcessor;
import org.qualipso.factory.qualipsoServlet.impl.factory.CSimpleRequestProcessor;

public class CRComplexTest{
	
	private static Log logger = LogFactory.getLog(CRComplexTest.class);
	
	public CRComplexTest()
	{
		
	}
	
	/*
	 * JUnit for different arguments
	 * Complex type arguments, message skeleton of the SOAPElement:
	 * <body>
	 * 	<arg0>1</arg0>
	 * 	<arg1>
	 * 		<SubArg1>toto</SubArg1>
	 * 		<SubArg2>titi</SubArg2>
	 * 		<SubArg3>tutu</SubArg3>
	 * 	</arg1>
	 *	<arg2>mimi</arg2>
	 * </body>
	 */
	@Test
	public void testCComplexRequest()
	{
		/*
		Hashtable <String,Vector<String>> HtComplex1,HtComplex2,HtComplex3,HtComplex;
		// fill the Hastable with simple arguments :
		Vector<String> vector1 = new Vector<String>();
		vector1.add("1");
		HtComplex1 = new Hashtable<String,Vector<String>>();
		HtComplex1.put("arg0", vector1);
		
		Vector<String> vector2 = new Vector<String>();
		vector2.add("test1");
		HtComplex2 = new Hashtable<String,Vector<String>>();
		HtComplex2.put("arg1", vector2);
		*/
		Hashtable <String,Vector<String>> HtComplex = new Hashtable<String,Vector<String>>();
		Vector<String> vector = new Vector<String>();
		vector.add("toto");
		HtComplex.put("SubArg1", vector);
		vector = new Vector<String>();
		vector.add("titi");
		HtComplex.put("SubArg2", vector);
		vector = new Vector<String>();
		vector.add("tutu");
		HtComplex.put("SubArg3", vector);
		CComplexRequestProcessor CComplexChildren = new CComplexRequestProcessor(HtComplex);
		/*
		Vector<String> vector3 = new Vector<String>();
		vector3.add("mimi");
		HtComplex3 = new Hashtable<String,Vector<String>>();
		HtComplex3.put("arg2", vector3);
		
		// preparing the environment of the unit test
		Vector<IRequestProcessor> requestProcessors = new Vector <IRequestProcessor>();
		
		// test the object with soap generation:

		try{
			CSimpleRequestProcessor aProc1 = new CSimpleRequestProcessor(HtComplex1);
			requestProcessors.add(aProc1);
			
			CComplexRequestProcessor aReturnObject = new CComplexRequestProcessor(HtComplex2);			
			aReturnObject.setAComplexTypeName(vector2.firstElement());
			aReturnObject.addChildrenCComplex(CComplexChildren);
			
			requestProcessors.add(aReturnObject);
			
			CSimpleRequestProcessor aProc3 = new CSimpleRequestProcessor(HtComplex3);
			requestProcessors.add(aProc3);
		}
		catch(Exception except)
		{
			fail("Error in test of complex arguments : building the vector requestProcessors.\n");
		}
		*/
		try{	
			// above is the code of the buildSoapMessage from the CRequestManager :
			/*
			Enumeration<IRequestProcessor> enumRequest = requestProcessors.elements();
			SOAPFactory factory=SOAPFactory.newInstance();
			
			SOAPElement aTestSoap = factory.createElement("body");
			
			while(enumRequest.hasMoreElements())
			{
				aTestSoap.addChildElement(enumRequest.nextElement().generateSOAP());
			}
			*/
			SOAPElement aTestSoap = CComplexChildren.generateSOAP();
			// read value and attributes
			assertNotNull(aTestSoap);
			assertNotNull(aTestSoap.getChildNodes());
			assertEquals(3 , aTestSoap.getChildNodes().getLength());
			/*
			assertNotNull(aTestSoap.getChildNodes().item(1));
			assertEquals("arg0",aTestSoap.getChildNodes().item(1).getNodeName());
			assertEquals("item",aTestSoap.getChildNodes().item(1).getChildNodes().item(1).getNodeName());
			assertEquals("item",aTestSoap.getChildNodes().item(1).getChildNodes().item(2).getNodeName());
			assertEquals("item",aTestSoap.getChildNodes().item(1).getChildNodes().item(3).getNodeName());
			assertEquals("1",aTestSoap.getChildNodes().item(1).getChildNodes().item(1).getNodeValue());
			assertEquals("toto",aTestSoap.getChildNodes().item(1).getChildNodes().item(2).getNodeValue());
			assertEquals("titi",aTestSoap.getChildNodes().item(1).getChildNodes().item(3).getNodeValue());
			*/
		}
		catch (SOAPException exception)
		{
			fail("Test arraylist argument : "+exception.getMessage()+"\n");
		}
	}
	
	/*
	 * JUnit for different arguments
	 * subComplex type arguments, message skeleton of the SOAPElement:
	 * <arg0>1</arg0>
	 * <arg1>
	 * 	<SubArg1>toto</SubArg1>
	 * 	<SubArg2>titi</SubArg2>
	 * 		<SSub1>vivi</SSub1>
	 * 		<SSub2>nini</SSub2>
	 * 	<SubArg3>tutu</SubArg3>
	 * </arg1>
	 * <arg2>mimi</arg2>
	 */
	
	public void testCsubComplex1Request()
	{
		Hashtable <String,Vector<String>> HtSubComplex1;
		// fill the Hastable with sub complex arguments :
		
		
		// test the object with soap generation:

	}
	
	/*
	 * JUnit for different arguments
	 * subsubsub complex type arguments, message skeleton of the SOAPElement:
	 * <arg0>1</arg0>
	 * <arg1>
	 * 	<SubArg1>toto</SubArg1>
	 * 	<SubArg2>titi</SubArg2>
	 * 		<SSub1>vivi</SSub1>
	 * 			<SSSub1>wiwi</SSSub1>
	 * 			<SSSub2>wowo</SSSub2>
	 * 		<SSub2>nini</SSub2>
	 * 	<SubArg3>tutu</SubArg3>
	 * </arg1>
	 * <arg2>mimi</arg2>
	 */
	
	public void testCsubComplex2Request()
	{
		Hashtable <String,Vector<String>> HtSubComplex2;
		// fill the Hastable with sub complex less 2 arguments :
		
		
		// test the object with soap generation:

	}
	
	/*
	 * JUnit for different arguments
	 * complex type with arraylist arguments, message skeleton of the SOAPElement:
	 * <arg0>1</arg0>
	 * <arg1>
	 * 	<SubArg1>toto</SubArg1>
	 * 	<SubArg2>titi</SubArg2>
	 * 		<SSub1>
	 * 			<item>wiwi</item>
	 * 			<item>wowo</item>
	 * 		</SSub1>
	 * 		<SSub2>nini</SSub2>
	 * 	<SubArg3>tutu</SubArg3>
	 * </arg1>
	 * <arg2>mimi</arg2>
	 */
	
	public void testCsubComplex3Request()
	{
		Hashtable <String,Vector<String>> HtSubComplex3;
		// fill the Hastable with sub complex less 3 arguments :
		
		
		// test the object with soap generation:

	}
}
