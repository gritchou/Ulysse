package org.qualipso.factory.security.pap;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.security.repository.PolicyRepositoryException;
import org.qualipso.factory.security.repository.PolicyRepositoryService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 16 june 2009
 */
@Stateless(name = PAPService.SERVICE_NAME, mappedName = FactoryNamingConvention.LOCAL_SERVICE_PREFIX + PAPService.SERVICE_NAME)
@SecurityDomain(value = "JBossWSDigest")
public class PAPServiceBean implements PAPService {
	
	private static Log logger = LogFactory.getLog(PAPServiceBean.class);
	
	private PolicyRepositoryService repositoryService;
    private SessionContext ctx;
	
    public PAPServiceBean() {
	}
    
    @Resource
	public void setSessionContext(SessionContext ctx) {
		this.ctx = ctx;
	}

	public SessionContext getSessionContext() {
		return this.ctx;
	}

	@EJB
    public void setPolicyRepositoryService(PolicyRepositoryService repository) {
		this.repositoryService = repository;
	}

	public PolicyRepositoryService getPolicyRepositoryService() {
		return repositoryService;
	}  
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createPolicy(String id, String policy) throws PAPServiceException {
		logger.warn("createPolicy(...) called");
		logger.debug("params : id=" + id + ", policy=\r\n" + policy);
		
		try {
			repositoryService.getPolicyRepository().addPolicy(id, policy.getBytes());
		} catch (PolicyRepositoryException e) {
			ctx.setRollbackOnly();
			throw new PAPServiceException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deletePolicy(String id) throws PAPServiceException {
		logger.warn("deletePolicy(...) called");
		logger.debug("params : id=" + id);
		
		try {
			repositoryService.getPolicyRepository().deletePolicy(id);
		} catch (PolicyRepositoryException e) {
			ctx.setRollbackOnly();
			throw new PAPServiceException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String getPolicy(String id) throws PAPServiceException {
		logger.warn("getPolicy(...) called");
		logger.debug("params : id=" + id);
		
		try {
			return new String(repositoryService.getPolicyRepository().getPolicy(id));
		} catch (PolicyRepositoryException e) {
			throw new PAPServiceException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updatePolicy(String id, String policy) throws PAPServiceException {
		logger.warn("updatePolicy(...) called");
		logger.debug("params : id=" + id + ", policy=\r\n" + policy);
		
		try {
			repositoryService.getPolicyRepository().updatePolicy(id, policy.getBytes());
		} catch (PolicyRepositoryException e) {
			ctx.setRollbackOnly();
			throw new PAPServiceException(e);
		}
	}
	
}
