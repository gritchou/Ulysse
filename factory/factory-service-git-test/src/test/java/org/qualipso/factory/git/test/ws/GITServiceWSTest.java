package org.qualipso.factory.git.test.ws;

import static org.junit.Assert.fail;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ws.core.StubExt;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.git.client.ws.BootstrapService;
import org.qualipso.factory.git.client.ws.BootstrapServiceException_Exception;
import org.qualipso.factory.git.client.ws.BootstrapService_Service;
import org.qualipso.factory.git.client.ws.CoreService;
import org.qualipso.factory.git.client.ws.CoreServiceException_Exception;
import org.qualipso.factory.git.client.ws.CoreService_Service;
import org.qualipso.factory.git.client.ws.GITService;
import org.qualipso.factory.git.client.ws.GITService_Service;
import org.qualipso.factory.git.test.AllTests;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 September 2009
 */
public class GITServiceWSTest {
	
	private static Log logger = LogFactory.getLog(GITServiceWSTest.class);
	
	private CoreService core;
	
	@BeforeClass
	public static void init() {
		try {
			BootstrapService port = new BootstrapService_Service().getBootstrapServicePort(); 
			((StubExt) port).setConfigName("Standard WSSecurity Client");
			port.bootstrap();
		} catch (BootstrapServiceException_Exception e) {
			logger.error("unable to bootstrap factory", e);
		}
	}
	
	public GITServiceWSTest() {
		CoreService_Service coreService = new CoreService_Service();
        core = coreService.getCoreServicePort();
	}
	
	@Before
	public void setup() throws CoreServiceException_Exception {
		((StubExt) core).setConfigName("Standard WSSecurity Client");
        Map<String, Object> reqContext = ((BindingProvider) core).getRequestContext();
        reqContext.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
        reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
        reqContext.put(BindingProvider.PASSWORD_PROPERTY, AllTests.ROOT_ACCOUNT_PASS);
        
		core.createFolder("/testrepos", "GITRepositories", "All the git repositories");
	}
	
	@After
	public void teardown() throws CoreServiceException_Exception {
		((StubExt) core).setConfigName("Standard WSSecurity Client");
        Map<String, Object> reqContext = ((BindingProvider) core).getRequestContext();
        reqContext.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
        reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
        reqContext.put(BindingProvider.PASSWORD_PROPERTY, AllTests.ROOT_ACCOUNT_PASS);
        
		core.deleteFolder("/testrepos");
	}
	
    @Test
    public void testCreateRepository() {
        try {
            GITService_Service service = new GITService_Service();
            GITService port = service.getGITServicePort();

            ((StubExt) port).setConfigName("Standard WSSecurity Client");

            Map<String, Object> reqContext = ((BindingProvider) port).getRequestContext();
            reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
            reqContext.put(BindingProvider.PASSWORD_PROPERTY, AllTests.ROOT_ACCOUNT_PASS);
            
            port.createGITRepository("/testrepos/repo1", "MyFirstREpo", "Whaou");
            
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
