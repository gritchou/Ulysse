package org.qualipso.factory.security.repository;

import javax.ejb.Local;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 1 September 2009
 */
@Local 
public interface PolicyRepositoryService {
	
	public static final String SERVICE_NAME = "policy-repository";
	
	public PolicyRepository getPolicyRepository();

}
