package org.qualipso.factory.jabuti.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ws.core.StubExt;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.jabuti.client.ws.Bootstrap;
import org.qualipso.factory.jabuti.client.ws.BootstrapServiceException_Exception;
import org.qualipso.factory.jabuti.client.ws.Bootstrap_Service;
import org.qualipso.factory.jabuti.client.ws.CoverageCriterionDetailsArray;
import org.qualipso.factory.jabuti.client.ws.InstrumentedProjectDetails;
import org.qualipso.factory.jabuti.client.ws.Jabuti;
import org.qualipso.factory.jabuti.client.ws.Jabuti_Service;
import org.qualipso.factory.jabuti.client.ws.RequiredElementsDetailsArray;



/**
 * @author Rafael M. Martins
 * @date Sometime in 2009
 */
public class JabutiServiceTest {
	
	private static Log logger = LogFactory.getLog(JabutiServiceTest.class);
	
	private static String projectId;
	
	private Jabuti port;

	@BeforeClass
	public static void beforeClass() {
		try {
			Bootstrap port = new Bootstrap_Service().getBootstrapServiceBeanPort();
			((StubExt) port).setConfigName("Standard WSSecurity Client");
			port.bootstrap();
		} catch (BootstrapServiceException_Exception e) {
			logger.error("unable to bootstrap factory", e);
		}
	}
	
	@Before
	public void before() {
		port = new Jabuti_Service().getJabutiServiceBeanPort();
		
//		----------------- Authentication		

		((StubExt) port).setConfigName("Standard WSSecurity Client");
//
//        Map<String, Object> reqContext = 
//        		((BindingProvider) port).getRequestContext();
//        reqContext.put(StubExt.PROPERTY_AUTH_TYPE, 
//        		StubExt.PROPERTY_AUTH_TYPE_WSSE);
//        reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
//        reqContext.put(BindingProvider.PASSWORD_PROPERTY, "root");

//			-----------------
	}

	@Test
	public void testCreateProject() {
		logger.info("testCreateProject()");
		try {           
			String user = "SA";
			String projectName = "Vending";
			File file = new File("src/test/resources/vending.jar");
			
			byte[] fileBytes = new byte[(int)file.length()];
			new FileInputStream(file).read(fileBytes);
			
			projectId = port.createProject(user, projectName, fileBytes);		
			logger.info("projectId = " + projectId);
			
			assertNotNull(projectId);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSelectClasses() {
		logger.info("testSelectClassesAndGetRequiredElements()");
		try {			
			String user = "SA";
			MyStringArray classes = 
					new MyStringArray(new String[]{"*"});
			
			logger.info("projectId = " + projectId);
			
			String message = 
				port.selectClassesToInstrument(user, projectId, classes);

			assertEquals(message, "The classes were instrumented.");
		} 
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetRequiredElements() {
		logger.info("testSelectClassesAndGetRequiredElements()");
		try {			
			String user = "SA";
			RequiredElementsDetailsArray details = 
				port.getAllRequiredElements(user, projectId);
			
			assertNotNull(details);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testAddTestCases() {
		logger.info("testAddTestCases()");
		try {			
			String user = "SA";
			File file = new File("src/test/resources/vending_test.jar");
			String testSuiteClass = "vending.DispenserTestCase";
			
			byte[] fileBytes = new byte[(int)file.length()];
			new FileInputStream(file).read(fileBytes);
			
			String message = 
				port.addTestCases(user, projectId, testSuiteClass, fileBytes);
			
			assertEquals(message, "Test Case file was added.");
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetInstrumentedProject() {
		logger.info("testGetInstrumentedProject()");
		try {			
			String user = "SA";
			
			InstrumentedProjectDetails project = 
				port.getInstrumentedProject(user, projectId);
			
			assertNotNull(project);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSendTraceFile() {
		logger.info("testSendTraceFile()");
    	try {    		
    		File file = new File("src/test/resources/vending.trc");
    		byte[] fileBytes = new byte[(int)file.length()];
            new FileInputStream(file).read(fileBytes);
    		String message = port.sendTraceFile("SA", projectId, fileBytes);
    		
    		assertEquals(message, "ok");
    	} catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
	}
	
	@Test
    public void testGetCoverageByCriteria() {
    	logger.info("testGetCoverageByCriteria()");
    	try {    		
    		CoverageCriterionDetailsArray details =
    				port.getCoverageByCriteria("SA", projectId);
    		assertNotNull(details);			
    	} catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

	@AfterClass
	public static void cleanUp() {
		// TODO Delete all temporary project folders
	}
	
}


