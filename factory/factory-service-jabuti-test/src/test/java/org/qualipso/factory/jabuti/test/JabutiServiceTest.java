package org.qualipso.factory.jabuti.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ws.core.StubExt;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.jabuti.client.ws.BootstrapService;
import org.qualipso.factory.jabuti.client.ws.BootstrapServiceException_Exception;
import org.qualipso.factory.jabuti.client.ws.BootstrapService_Service;
import org.qualipso.factory.jabuti.client.ws.JabutiService;
import org.qualipso.factory.jabuti.client.ws.JabutiService_Service;



/**
 * @author 
 * @date 
 */
public class JabutiServiceTest {
	private static Log logger = LogFactory.getLog(JabutiServiceTest.class);
	
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

    @Test
    public void testSayJabuti() {
//        try {
//            JabutiService_Service service = new JabutiService_Service();
//            JabutiService port = service.getJabutiServicePort();
//
//            ((StubExt) port).setConfigName("Standard WSSecurity Client");
//
//            String message = port.sayJabuti();
//            logger.info("Message : " + message);
//            assertTrue(message.equals("/profiles/guest says : Hello World !!"));
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail(e.getMessage());
//        }

        try {
        	JabutiService_Service service = new JabutiService_Service();
            JabutiService port = service.getJabutiServicePort();
            
            ((StubExt) port).setConfigName("Standard WSSecurity Client");

            Map<String, Object> reqContext = ((BindingProvider) port).getRequestContext();
            reqContext.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
            reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
            reqContext.put(BindingProvider.PASSWORD_PROPERTY, "root");
                       
//            logger.info(reqContext.get(BindingProvider.USERNAME_PROPERTY));

            String message = port.sayJabuti();
            logger.info("Message : " + message);
            assertTrue(message.equals("/profiles/kermit says : Hello World !!"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}

