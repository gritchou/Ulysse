package org.qualipso.factory.test.qualipsoServlet.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Hashtable;
import java.util.Vector;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.junit.Test;
import org.qualipso.factory.qualipsoServlet.impl.factory.CTableRequestProcessor;

public class CTableTest{
	
	private static Log logger = LogFactory.getLog(CTableTest.class);
	
	public CTableTest(){}
	
	/*
	 * JUnit for different arguments
	 * ArrayList arguments, message skeleton of the SOAPElement:
	 * 	<arg0>
	 * 		<item>1</item>
	 *		<item>toto</item>
	 * 		<item>titi</item>
	 *	 </arg0>
	 */
	@Test
	public void testCTableRequest()
	{
		Hashtable <String,Vector<String>> HtTable = new Hashtable<String,Vector<String>>();
		// fill the Hastable with simple arguments :
		Vector<String> vector1 = new Vector<String>();
		vector1.add("1");
		vector1.add("toto");
		vector1.add("titi");
		HtTable.put("arg0", vector1);		
		
		// test the object with soap generation:	
		CTableRequestProcessor message = new CTableRequestProcessor(HtTable);

		try{
			SOAPElement aTestSoap = message.generateSOAP();
			
			assertNotNull(aTestSoap);
			assertEquals(3 , aTestSoap.getChildNodes().getLength());
			
			assertNotNull(aTestSoap.getElementsByTagName("item"));
			assertNotNull(aTestSoap.getElementsByTagName("arg0"));
			
			assertEquals("item",aTestSoap.getChildNodes().item(0).getNodeName());
			assertEquals("1",aTestSoap.getChildNodes().item(0).getFirstChild().getNodeValue());
			assertEquals("toto",aTestSoap.getChildNodes().item(1).getFirstChild().getNodeValue());
			assertEquals("titi",aTestSoap.getChildNodes().item(2).getFirstChild().getNodeValue());
		}
		catch (SOAPException exception)
		{
			fail("Test arraylist argument : "+exception.getMessage()+"\n");
		}
	}
}
