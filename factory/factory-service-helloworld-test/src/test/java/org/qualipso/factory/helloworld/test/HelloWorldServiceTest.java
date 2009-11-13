package org.qualipso.factory.helloworld.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ws.core.StubExt;
import org.junit.Test;
import org.qualipso.factory.helloworld.client.ws.Helloworld;
import org.qualipso.factory.helloworld.client.ws.Helloworld_Service;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 june 2009
 */
public class HelloWorldServiceTest {
	private static Log logger = LogFactory.getLog(HelloWorldServiceTest.class);

    @Test
    public void testSayHelloWorld() {
        try {
            Helloworld port = new Helloworld_Service().getHelloWorldServiceBeanPort();

            ((StubExt) port).setConfigName("Standard WSSecurity Client");

            String message = port.sayHelloWorld();
            logger.info("Message : " + message);
            assertTrue(message.equals("/profiles/guest says : Hello World !!"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        try {
        	Helloworld port = new Helloworld_Service().getHelloWorldServiceBeanPort();
            
            ((StubExt) port).setConfigName("Standard WSSecurity Client");

            Map<String, Object> reqContext = ((BindingProvider) port).getRequestContext();
            reqContext.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
            reqContext.put(BindingProvider.USERNAME_PROPERTY, "kermit");
            reqContext.put(BindingProvider.PASSWORD_PROPERTY, "thefrog");

            String message = port.sayHelloWorld();
            logger.info("Message : " + message);
            assertTrue(message.equals("/profiles/kermit says : Hello World !!"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}

