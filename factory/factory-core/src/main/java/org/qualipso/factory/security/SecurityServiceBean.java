package org.qualipso.factory.security;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.binding.PropertyNotFoundException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceBean;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 september 2009
 */
@Stateless(name = "Security", mappedName = FactoryNamingConvention.JNDI_SERVICE_PREFIX + "SecurityService")
@WebService(endpointInterface = "org.qualipso.factory.security.SecurityService", targetNamespace = "http://org.qualipso.factory.ws/service/security", serviceName = "SecurityService", portName = "SecurityServicePort")
@WebContext(contextRoot = "/factory-core", urlPattern = "/security")
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class SecurityServiceBean implements SecurityService {
	
	private static final String SERVICE_NAME = "SecurityService";
	private static final String[] RESOURCE_TYPE_LIST = new String[] {};
	private static Log logger = LogFactory.getLog(SecurityServiceBean.class);
	
	private BindingService binding;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	private MembershipService membership;
	private SessionContext ctx;
	private EntityManager em;
	
	public SecurityServiceBean() {
	}
	
	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public EntityManager getEntityManager() {
		return this.em;
	}

	@Resource
	public void setSessionContext(SessionContext ctx) {
		this.ctx = ctx;
	}

	public SessionContext getSessionContext() {
		return this.ctx;
	}

	@EJB
	public void setBindingService(BindingService binding) {
		this.binding = binding;
	}

	public BindingService getBindingService() {
		return this.binding;
	}

	@EJB
	public void setPEPService(PEPService pep) {
		this.pep = pep;
	}

	public PEPService getPEPService() {
		return this.pep;
	}

	@EJB
	public void setPAPService(PAPService pap) {
		this.pap = pap;
	}

	public PAPService getPAPService() {
		return this.pap;
	}

	@EJB
	public void setNotificationService(NotificationService notification) {
		this.notification = notification;
	}

	public NotificationService getNotificationService() {
		return this.notification;
	}

	@EJB
	public void setMembershipService(MembershipService membership) {
		this.membership = membership;
	}

	public MembershipService getMembershipService() {
		return this.membership;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String getSecurityPolicy(String path) throws SecurityServiceException {
		logger.info("getSecurityPolicy(...) called");
		logger.debug("params : path=" + path);
		
		try {
			PathHelper.valid(path);
			String npath = PathHelper.normalize(path);
			
			String caller = membership.getProfilePathForConnectedIdentifier();
			
			pep.checkSecurity(caller, npath, "read");
			
			binding.lookup(npath);
			
			String policy = pap.getPolicy(binding.getProperty(npath, FactoryResourceProperty.POLICY_ID, false));
			
			notification.throwEvent(new Event(npath, caller, "Resource", "security.resource.get-security-policy", ""));
			
			return policy;
		} catch ( Exception e ) {
			throw new SecurityServiceException("error in getting security : " + e.getMessage(), e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void addSecurityRule(String path, String subject, String permissions) throws SecurityServiceException {
		logger.info("addSecurityRule(...) called");
		logger.debug("params : path=" + path);
		
		try {
			PathHelper.valid(path);
			String npath = PathHelper.normalize(path);
			
			String caller = membership.getProfilePathForConnectedIdentifier();
			
			//just to check if path exists
			binding.lookup(npath);
			
			if ( !isOwner(caller, npath) ) {
				throw new SecurityServiceException("only owner can modify security");
			}
			
			String owner = binding.getProperty(npath, FactoryResourceProperty.OWNER, false);
			
			if ( subject.equals(owner) ) {
				throw new SecurityServiceException("forbidden to add a rule for owner");
			}
			
			String policyId = binding.getProperty(npath, FactoryResourceProperty.POLICY_ID, false);
			String policy = pap.getPolicy(policyId);
			String newPolicy = PAPServiceHelper.addRuleToPolicy(policy, subject, permissions.split(","));
			pap.updatePolicy(policyId, newPolicy);
			
			notification.throwEvent(new Event(npath, caller, "Resource", "security.resource.add-security-rule", ""));
		} catch ( Exception e ) {
			e.printStackTrace();
			ctx.setRollbackOnly();
			throw new SecurityServiceException("error in adding security rule : " + e.getMessage(), e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void editSecurityRule(String path, String subject, String permissions) throws SecurityServiceException {
		logger.info("editSecurityRule(...) called");
		logger.debug("params : path=" + path);
		
		try {
			PathHelper.valid(path);
			String npath = PathHelper.normalize(path);
			
			String caller = membership.getProfilePathForConnectedIdentifier();
			
			//just to check if path exists
			binding.lookup(npath);
			
			if ( !isOwner(caller, npath) ) {
				throw new SecurityServiceException("only owner can modify security");
			}
			
			String owner = binding.getProperty(npath, FactoryResourceProperty.OWNER, false);
			
			if ( subject.equals(owner) ) {
				throw new SecurityServiceException("forbidden to edit owner rule");
			}
			
			String policyId = binding.getProperty(npath, FactoryResourceProperty.POLICY_ID, false);
			String policy = pap.getPolicy(policyId);
			String newPolicy = PAPServiceHelper.addRuleToPolicy(PAPServiceHelper.removeRuleFromPolicy(policy, subject), subject, permissions.split(","));
			pap.updatePolicy(policyId, newPolicy);
			
			notification.throwEvent(new Event(npath, caller, "Resource", "security.resource.edit-security-rule", ""));
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			throw new SecurityServiceException("error in editing security rule : " + e.getMessage(), e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removeSecurityRule(String path, String subject) throws SecurityServiceException {
		logger.info("removeSecurityRule(...) called");
		logger.debug("params : path=" + path);
		
		try {
			PathHelper.valid(path);
			String npath = PathHelper.normalize(path);
			
			String caller = membership.getProfilePathForConnectedIdentifier();
			
			//just to check if path exists
			binding.lookup(npath);
			
			if ( !isOwner(caller, npath) ) {
				throw new SecurityServiceException("only owner can modify security");
			}
			
			String owner = binding.getProperty(npath, FactoryResourceProperty.OWNER, false);
			
			if ( subject.equals(owner) ) {
				throw new SecurityServiceException("forbidden to remove owner rule");
			}
			
			String policyId = binding.getProperty(npath, FactoryResourceProperty.POLICY_ID, false);
			String policy = pap.getPolicy(policyId);
			String newPolicy = PAPServiceHelper.removeRuleFromPolicy(policy, subject);
			pap.updatePolicy(policyId, newPolicy);
			
			notification.throwEvent(new Event(npath, caller, "Resource", "security.resource.remove-security-rule", ""));
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			throw new SecurityServiceException("error in removing security rule : " + e.getMessage(), e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void changeOwner(String path, String newOwner) throws SecurityServiceException {
		logger.info("changeOwner(...) called");
		logger.debug("params : path=" + path);
		
		try {
			PathHelper.valid(path);
			String npath = PathHelper.normalize(path);
			
			PathHelper.valid(newOwner);
			String newOwnerPath = PathHelper.normalize(newOwner);
			
			String caller = membership.getProfilePathForConnectedIdentifier();
			
			//just to check if path exists
			binding.lookup(npath);
			
			if ( !isOwner(caller, npath) ) {
				throw new SecurityServiceException("only owner can modify security");
			}
			
			FactoryResourceIdentifier newOwnerIdentifier = binding.lookup(newOwner);
			if ( !newOwnerIdentifier.getService().equals(membership.getServiceName()) && (!newOwnerIdentifier.getType().equals("Profile") || !newOwnerIdentifier.getType().equals("Group")) ) {
				throw new SecurityException("new owner must be a Profile or a Group Resource");
			}
			
			String ownerPath = binding.getProperty(npath, FactoryResourceProperty.OWNER, false);
			String policyId = binding.getProperty(npath, FactoryResourceProperty.POLICY_ID, false);
			String policy = pap.getPolicy(policyId);
			String newPolicy = PAPServiceHelper.addRuleToPolicy(PAPServiceHelper.removeRuleFromPolicy(policy, ownerPath), newOwnerPath, new String[0]);
			pap.updatePolicy(policyId, newPolicy);
			
			binding.setProperty(npath, FactoryResourceProperty.OWNER, newOwnerPath);
			
			notification.throwEvent(new Event(npath, caller, "Resource", "security.resource.change-owner", "new-owner=" + newOwnerPath));
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			throw new SecurityServiceException("error in changing owner : " + e.getMessage(), e);
		}
	}
	
	@Override
	public FactoryResource findResource(String path) throws FactoryException {
		throw new SecurityServiceException("This service doesn't manage any resource");
	}

	@Override
	public String[] getResourceTypeList() {
		return RESOURCE_TYPE_LIST;
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	
	//Service methods
	private boolean isOwner(String user, String path) throws MembershipServiceException, InvalidPathException, PathNotFoundException, PropertyNotFoundException {
		String ownerPath = binding.getProperty(path, FactoryResourceProperty.OWNER, false);
		FactoryResourceIdentifier owner = binding.lookup(ownerPath);
		
		if ( !user.equals(ownerPath) ) {
			//caller is not the owner, maybe owner is a group
			if ( owner.getType().equals("Group") ) {
				//owner is a group, checking if caller is member of this group
				if ( !membership.isMember(ownerPath, user) ) {
					logger.debug("user:" + user + " is NOT owner");
					return false;
				} 
			} else {
				logger.debug("user:" + user + " is NOT owner");
				return false;
			}
		}
		logger.debug("user:" + user + " is owner");
		return true;
	}

}
