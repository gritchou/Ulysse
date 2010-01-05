package org.qualipso.factory.greeting.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ws.core.StubExt;
import org.junit.Test;
import org.qualipso.factory.greeting.client.ws.Greeting;
import org.qualipso.factory.greeting.client.ws.Greeting_Service;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 22 june 2009
 */
public class GreetingServiceTest {
	
	private static Log logger = LogFactory.getLog(GreetingServiceTest.class);

    @Test
    public void testHello() {
        try {
            Greeting port = new Greeting_Service().getGreetingServiceBeanPort();

            ((StubExt) port).setConfigName("Standard WSSecurity Client");

            Map<String, Object> reqContext = ((BindingProvider) port).getRequestContext();
            reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
            reqContext.put(BindingProvider.PASSWORD_PROPERTY, "root");
            
            port.createName("/sheldon", "Sheldon Cooper");
            port.createName("/howard", "Howard Wolowitz");
            
            String message = port.sayHello("/sheldon");
            logger.debug("message : " + message);
            assertTrue(message.equals("Hello dear Sheldon Cooper !!"));
            
            message = port.sayHello("/sheldon");
            logger.debug("message : " + message);
            assertTrue(message.equals("Hello dear Howard Wolowitz !!"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
