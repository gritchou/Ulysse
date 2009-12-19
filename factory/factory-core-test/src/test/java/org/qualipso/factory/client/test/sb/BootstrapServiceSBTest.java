package org.qualipso.factory.client.test.sb;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.qualipso.factory.Factory;
import org.qualipso.factory.bootstrap.BootstrapService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 30 november 2009
 */
public class BootstrapServiceSBTest {
	
	private static Log logger = LogFactory.getLog(BootstrapServiceSBTest.class);
    private static Context ctx;
    
    @Test
    public void callBootstrap() throws NamingException {
//        Properties properties = new Properties();
//        properties.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
//        properties.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
//        properties.put("java.naming.provider.url", "localhost:1099");
//        ctx = new InitialContext(properties);

        //BootstrapService bootstrap = (BootstrapService) ctx.lookup(FactoryNamingConvention.getJNDINameForService("bootstrap"));
        
        try {
        	BootstrapService bootstrap = (BootstrapService) Factory.findService("bootstrap");
        	bootstrap.bootstrap();
        } catch (Exception e) {
            logger.error(e);
        }
    }


}
