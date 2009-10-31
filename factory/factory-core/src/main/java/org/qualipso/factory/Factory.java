package org.qualipso.factory;

import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Factory {
	
	private static Log logger = LogFactory.getLog(Factory.class);
	private static InitialContext jndi;
	
	private static synchronized InitialContext getJndiContext() throws FactoryException {
		try {
	        if (jndi == null) {
	            jndi = new InitialContext();
	        }
	        return jndi;
		} catch ( Exception e ) {
			throw new FactoryException(e);
		}
    }
	
	public static String[] listServices() throws FactoryException {
		try {
			NamingEnumeration<NameClassPair> enumeration = getJndiContext().list("");
			Vector<String> result = new Vector<String> ();
			
			while ( enumeration.hasMoreElements() ) {
				String name = ((NameClassPair)enumeration.next()).getName();
				if ( name.startsWith(FactoryNamingConvention.JNDI_SERVICE_PREFIX) ) {
					logger.debug("jndi service name found : " + name);
					result.add(FactoryNamingConvention.getServiceNameFromJNDI(name));
				}
			}
			
			return result.toArray(new String[result.size()]);
		} catch ( Exception  e ) {
			throw new FactoryException(e);
		}
	}
	

}
