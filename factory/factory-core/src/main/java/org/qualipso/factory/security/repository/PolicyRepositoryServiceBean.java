package org.qualipso.factory.security.repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.qualipso.factory.FactoryNamingConvention;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 1 September 2009
 */
//TODO Check that repository is not initialized each time a session bean instance is created... PolicyRepository should be a singleton.
//TODO Maybe add a purge method to ensure bootstrap is performed in an empty repository
@Stateless(name = PolicyRepositoryService.SERVICE_NAME, mappedName = FactoryNamingConvention.LOCAL_SERVICE_PREFIX + PolicyRepositoryService.SERVICE_NAME)
@SecurityDomain(value = "JBossWSDigest")
public class PolicyRepositoryServiceBean implements PolicyRepositoryService{
	
	private static Log logger = LogFactory.getLog(PolicyRepositoryServiceBean.class);
	private PolicyRepository repository;
	
	public PolicyRepositoryServiceBean() {
		repository = new FilePolicyRepository();
	}
	
	@PostConstruct
	public void init() throws PolicyRepositoryException {
		repository.init();
	}
	
	@PreDestroy
	public void destroy() throws PolicyRepositoryException {
		repository.shutdown();
	}

	@Override
	public PolicyRepository getPolicyRepository() {
		return repository;
	}

}
