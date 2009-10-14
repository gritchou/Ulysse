package org.qualipso.factory;

public class FactoryNamingConvention {
	
	public static final String JNDI_SERVICE_PREFIX = "QualipsoFactory-";
	
	public static String getJNDINameForService(String service) {
		return JNDI_SERVICE_PREFIX + service;
	}
	
	public static String getServiceNameFromJNDI(String jndiName) {
		return jndiName.substring(JNDI_SERVICE_PREFIX.length());
	}

}
