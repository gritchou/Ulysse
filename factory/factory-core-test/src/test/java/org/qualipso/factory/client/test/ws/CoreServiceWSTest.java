package org.qualipso.factory.client.test.ws;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ws.core.StubExt;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.client.test.AllTests;
import org.qualipso.factory.client.ws.Bootstrap;
import org.qualipso.factory.client.ws.BootstrapServiceException_Exception;
import org.qualipso.factory.client.ws.Bootstrap_Service;
import org.qualipso.factory.client.ws.Core;
import org.qualipso.factory.client.ws.Core_Service;
import org.qualipso.factory.client.ws.File;
import org.qualipso.factory.client.ws.FileData;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 august 2009
 */
public class CoreServiceWSTest {
	
	private static Log logger = LogFactory.getLog(CoreServiceWSTest.class);
	
	private Core core;
	
	public CoreServiceWSTest() {
		core = new Core_Service().getCoreServiceBeanPort();
	}
	
	@BeforeClass
	public static void init() {
		try {
			Bootstrap bootstrap = new Bootstrap_Service().getBootstrapServiceBeanPort(); 
			((StubExt) bootstrap).setConfigName("Standard WSSecurity Client");
			bootstrap.bootstrap();
		} catch (BootstrapServiceException_Exception e) {
			logger.error("unable to bootstrap factory", e);
		}
	}
	
	@Test
    public void testCRUDFile() {
        try {
            ((StubExt) core).setConfigName("Standard WSSecurity Client");
            Map<String, Object> reqContext = ((BindingProvider) core).getRequestContext();
            reqContext.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
            reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
            reqContext.put(BindingProvider.PASSWORD_PROPERTY, AllTests.ROOT_ACCOUNT_PASS);
            
            java.io.File f = new java.io.File(ClassLoader.getSystemResource("test.txt").getPath());
            FileData data = new FileData();
            data.setData(new DataHandler(new FileDataSource(f)));
            
            java.io.File f2 = new java.io.File(ClassLoader.getSystemResource("test2.txt").getPath());
            FileData data2 = new FileData();
            data2.setData(new DataHandler(new FileDataSource(f2)));
                        
            core.createFile("/testfile.txt", "testfile.txt", "text/plain", "a  simple file", data );
            
            core.updateFile("/testfile.txt", "testfile.txt", "text/plain", "a  simple file", data2 );
            
            File readfile = core.readFile("/testfile.txt");
            assertTrue(readfile.getSize() == 36d);
            
            FileData readdata = core.getFileData("/testfile.txt");
            assertTrue(((String)readdata.getData().getContent()).contains("This is a smaller file."));
            
            core.deleteFile("/testfile.txt");
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
