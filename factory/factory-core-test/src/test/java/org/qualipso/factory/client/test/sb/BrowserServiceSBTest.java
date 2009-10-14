package org.qualipso.factory.client.test.sb;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.LoginContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.auth.callback.UsernamePasswordHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.bootstrap.BootstrapService;
import org.qualipso.factory.bootstrap.BootstrapServiceException;
import org.qualipso.factory.browser.BrowserService;
import org.qualipso.factory.client.test.AllTests;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.membership.MembershipService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 7 october 2009
 */
public class BrowserServiceSBTest {
	private static Log logger = LogFactory.getLog(BrowserServiceSBTest.class);
    private static Context ctx;
    
    private MembershipService membership;
    private BrowserService browser;
    private CoreService core;
    private LoginContext lc;
    
    @BeforeClass
	public static void beforeClass() throws NamingException {
		try {
			logger.debug("jaas config file path : " + ClassLoader.getSystemResource("jaas.config").getPath());
			System.setProperty("java.security.auth.login.config", ClassLoader.getSystemResource("jaas.config").getPath());
		} catch (Exception e) {
			logger.error("unable to load local jaas.config file");
		}
		
		Properties properties = new Properties();
		properties.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
		properties.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
		properties.put("java.naming.provider.url","localhost:1099");
		ctx = new InitialContext(properties);
		
		BootstrapService bootstrap = (BootstrapService) ctx.lookup(FactoryNamingConvention.JNDI_SERVICE_PREFIX + "BootstrapService");
		try {
			bootstrap.bootstrap();
		} catch (BootstrapServiceException e) {
			logger.error(e);
		}
	}
    
    @Before
    public void before() {
    	try {
    		membership = (MembershipService) ctx.lookup(FactoryNamingConvention.JNDI_SERVICE_PREFIX + "MembershipService");
			browser = (BrowserService) ctx.lookup(FactoryNamingConvention.JNDI_SERVICE_PREFIX + "BrowserService");
			core = (CoreService) ctx.lookup(FactoryNamingConvention.JNDI_SERVICE_PREFIX + "CoreService");
			
			LoginContext lc = new LoginContext("qualipso", new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS));
            lc.login();
            
            core.createFolder("/testfolder", "Test Folder", "A beautifull folder for testing a lots of things !!");
            core.createFolder("/testfolder/folder1", "Folder1", "Folder1");
            core.createFolder("/testfolder/folder2", "Folder2", "Folder2");
            core.createFolder("/testfolder/folder3", "Folder3", "Folder3");
            core.createFolder("/testfolder/folder4", "Folder4", "Folder4");
            core.createLink("/testfolder/link1", "/testfolder/folder1");
            core.createLink("/testfolder/link2", "/testfolder/folder2");
            core.createLink("/testfolder/link3", "/testfolder/folder3");
            core.createLink("/testfolder/link4", "/testfolder/folder4");
            
            lc.logout();
    	} catch (Exception e) {
			logger.error(e);
			fail(e.getMessage());
		}
    }
    
    @After
    public void after() {
    	try {
    		LoginContext lc = new LoginContext("qualipso", new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS));
            lc.login();
            
    		core.deleteLink("/testfolder/link1");
        	core.deleteLink("/testfolder/link2");
        	core.deleteLink("/testfolder/link3");
        	core.deleteLink("/testfolder/link4");
        	core.deleteFolder("/testfolder/folder1");
        	core.deleteFolder("/testfolder/folder2");
        	core.deleteFolder("/testfolder/folder3");
        	core.deleteFolder("/testfolder/folder4");
        	core.deleteFolder("/testfolder");
        	
        	lc.logout();
    	} catch (Exception e) {
			logger.error(e);
			fail(e.getMessage());
		}
    }
	
	@Test
    public void testListChildren() {
        logger.debug("testing list children");
        
        try {
        	LoginContext lc = new LoginContext("qualipso", new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS));
            lc.login();

            String[] childs = browser.listChildren("/testfolder");
            assertTrue(childs.length == 8);

            lc.logout();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }
	
	@Test
    public void testListChildrenOfType() {
        logger.debug("testing list children of type");
        
        try {
        	LoginContext lc = new LoginContext("qualipso", new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS));
            lc.login();

            String[] childs = browser.listChildrenOfType("/testfolder", "CoreService", "Folder");
            assertTrue(childs.length == 4);
            
            String[] childs2 = browser.listChildrenOfType("/testfolder", "CoreService", "Link");
            assertTrue(childs2.length == 4);
            
            String[] childs3 = browser.listChildrenOfType("/testfolder", "CoreService", ".*");
            assertTrue(childs3.length == 8);

            lc.logout();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

}
