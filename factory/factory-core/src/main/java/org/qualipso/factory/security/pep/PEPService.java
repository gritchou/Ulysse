package org.qualipso.factory.security.pep;

import javax.ejb.Local;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 26 May 2009
 */
@Local
public interface PEPService {

	public static final String SERVICE_NAME = "pep";
	
	public void checkSecurity(String subject, String object, String action) throws PEPServiceException;

}
