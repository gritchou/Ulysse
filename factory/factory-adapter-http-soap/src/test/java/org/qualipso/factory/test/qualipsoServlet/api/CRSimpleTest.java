package org.qualipso.factory.test.qualipsoServlet.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Hashtable;
import java.util.Vector;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.qualipso.factory.qualipsoServlet.impl.factory.CSimpleRequestProcessor;


public class CRSimpleTest{
	
	private static Log logger = LogFactory.getLog(CRSimpleTest.class);
	
	public CRSimpleTest()
	{
		
	}
	
	/*
	 * JUnit for different arguments
	 * Simple arguments, message skeleton of the SOAPElement:
	 * <arg0>1</arg0>
	 * <arg1>toto</arg1>
	 * <arg2>titi</arg2>
	 */
	@Test
	public void testCSimpleRequest()
	{
		Hashtable <String,Vector<String>> HtSimple = new Hashtable<String,Vector<String>>();
		// fill the Hastable with simple arguments :
		Vector<String> vector = new Vector<String>();
		vector.add("1");
		HtSimple.put("arg0", vector);
		
		vector = new Vector<String>();
		vector.add("toto");
		HtSimple.put("arg1", vector);
		
		vector = new Vector<String>();
		vector.add("titi");
		HtSimple.put("arg2", vector);
		
		// test the object with soap generation:	

		try{
			//IRequestProcessor aProc = message.discriminant(HtSimple);
			SOAPElement aTestSoap = new CSimpleRequestProcessor(HtSimple).generateSOAP();
			
			// read value and attributes
			assertNotNull(aTestSoap);
			assertNotNull(aTestSoap.getChildNodes());
			assertEquals(1 , aTestSoap.getChildNodes().getLength());
			//assertNotNull(aTestSoap.getChildNodes().item(1));
			//assertNotNull(aTestSoap.getChildNodes().item(2));
			//assertNotNull(aTestSoap.getChildNodes().item(3));
			/*
			assertEquals("arg0",aTestSoap.getChildNodes().item(1).getNodeName());
			assertEquals("1",aTestSoap.getChildNodes().item(1).getNodeValue());
			assertEquals("arg1",aTestSoap.getChildNodes().item(2).getNodeName());
			assertEquals("toto",aTestSoap.getChildNodes().item(2).getNodeValue());
			assertEquals("arg2",aTestSoap.getChildNodes().item(3).getNodeName());
			assertEquals("titi",aTestSoap.getChildNodes().item(3).getNodeValue());
			*/
		}
		catch (SOAPException exception)
		{
			fail("Test simple argument : "+exception.getMessage()+"\n");
		}
	}
}
