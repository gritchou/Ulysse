package org.qualipso.factory.client.test.ws;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ws.core.StubExt;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.client.ws.BootstrapService;
import org.qualipso.factory.client.ws.BootstrapServiceException_Exception;
import org.qualipso.factory.client.ws.BootstrapService_Service;
import org.qualipso.factory.client.ws.MembershipService;
import org.qualipso.factory.client.ws.MembershipService_Service;
import org.qualipso.factory.client.ws.Profile;

public class MembershipServiceWSTest {

	private static Log logger = LogFactory.getLog(MembershipServiceWSTest.class);

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
    public void testGetConnectedProfilePath() {
        try {
            MembershipService_Service service = new MembershipService_Service();
            MembershipService port = service.getMembershipServicePort();

            ((StubExt) port).setConfigName("Standard WSSecurity Client");

            String path = port.getProfilePathForConnectedIdentifier();
            logger.debug("connected profile path : " + path);
            assertTrue(path.equals("/profiles/guest"));
            
            Map<String, Object> reqContext = ((BindingProvider) port).getRequestContext();
            reqContext.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
            reqContext.put(BindingProvider.USERNAME_PROPERTY, "kermit");
            reqContext.put(BindingProvider.PASSWORD_PROPERTY, "thefrog");
            
            path = port.getProfilePathForConnectedIdentifier();
            logger.debug("connected profile path : " + path);
            assertTrue(path.equals("/profiles/kermit"));
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testCreateProfile() {
        try {
            MembershipService_Service service = new MembershipService_Service();
            MembershipService port = service.getMembershipServicePort();

            ((StubExt) port).setConfigName("Standard WSSecurity Client");

            String path = port.getProfilePathForConnectedIdentifier();
            logger.debug("connected profile path : " + path);
            assertTrue(path.equals("/profiles/guest"));
            
            port.createProfile("kermit", "TOTO", "titi", 0);
            
            Map<String, Object> reqContext = ((BindingProvider) port).getRequestContext();
            reqContext.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
            reqContext.put(BindingProvider.USERNAME_PROPERTY, "kermit");
            reqContext.put(BindingProvider.PASSWORD_PROPERTY, "thefrog");
            
            Profile profile = port.readProfile("/profiles/kermit");
            
            assertTrue(profile.getFullname().equals("TOTO"));
            
            port.deleteProfile("/profiles/kermit");
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
