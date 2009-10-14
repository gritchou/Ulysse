package org.qualipso.factory.subversion;

import java.util.ArrayList;

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
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.subversion.entity.Repository;
import org.qualipso.factory.subversion.entity.SVNLogEntry;
import org.qualipso.factory.subversion.utils.Utils;
import org.qualipso.factory.subversion.wsclient.SCMServiceStub;
import org.qualipso.factory.subversion.wsclient.SCMServiceStub.FindRepositoryByNameResponse;

/**
 * @author Emmanuel Rias (emmanuel.rias@bull.net)
 * @date 11 june 2009
 */
@Stateless(name = "Subversion", mappedName = FactoryNamingConvention.JNDI_SERVICE_PREFIX + "SubversionService")
@WebService(endpointInterface = "org.qualipso.factory.subversion.SubversionService", targetNamespace = "http://org.qualipso.factory.ws/service/subversion", serviceName = "SubversionService", portName = "SubversionServicePort")
@WebContext(contextRoot = "/factory-service-subversion", urlPattern = "/subversion")
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class SubversionServiceBean implements SubversionService {

	private static final String SERVICE_NAME = "SubversionService";
	private static final String[] RESOURCE_TYPE_LIST = new String[] { "Repository", "Acl", "SVNLogEntry", "SVNLogEntryPath" };
	private static Log logger = LogFactory.getLog(SubversionServiceBean.class);

	private BindingService binding;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	private MembershipService membership;
	private SessionContext ctx;
	private EntityManager em;

	public SubversionServiceBean() {
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

	@EJB(name = "BindingService")
	public void setBindingService(BindingService binding) {
		this.binding = binding;
	}

	public BindingService getBindingService() {
		return this.binding;
	}

	@EJB(name = "PEPService")
	public void setPEPService(PEPService pep) {
		this.pep = pep;
	}

	public PEPService getPEPService() {
		return this.pep;
	}

	@EJB(name = "PAPService")
	public void setPAPService(PAPService pap) {
		this.pap = pap;
	}

	public PAPService getPAPService() {
		return this.pap;
	}

	@EJB(name = "NotificationService")
	public void setNotificationService(NotificationService notification) {
		this.notification = notification;
	}

	public NotificationService getNotificationService() {
		return this.notification;
	}

	@EJB(name = "MembershipService")
	public void setMembershipService(MembershipService membership) {
		this.membership = membership;
	}

	public MembershipService getMembershipService() {
		return this.membership;
	}

	private void checkResourceType(FactoryResourceIdentifier identifier,
			String resourceType) throws MembershipServiceException {
		if (!identifier.getService().equals(getServiceName())) {
			throw new MembershipServiceException("resource identifier "
					+ identifier + " does not refer to service "
					+ getServiceName());
		}
		if (!identifier.getType().equals(resourceType)) {
			throw new MembershipServiceException("resource identifier "
					+ identifier + " does not refer to a resource of type "
					+ resourceType);
		}
	}

	@Override
	public String[] getResourceTypeList() {
		return RESOURCE_TYPE_LIST;
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Repository findRepositoryByName(String path, String name)
			throws SubversionServiceException {
		logger.info("findProjectByName(...) called");
		logger.debug("params : path=" + path + "/" + name);

		try {
			// Checking if the connected user has the permission to getAllBugs
			// the resource giving pep :
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "read");

			// Performing a lookup in the naming to recover the Resource
			// Identifier
			//FactoryResourceIdentifier identifier = binding.lookup(path);

			// Checking if this resource identifier is really a resource managed
			// by this service (a Project resource)
			// checkResourceType(identifier, "Repository");

			// STARTING SPECIFIC EXTERNAL SERVICE RESOURCE LOADING OR METHOD
			// CALLS
			// TODO better to use business delegate
			SCMServiceStub.FindRepositoryByName findRepositoryByName = (SCMServiceStub.FindRepositoryByName) getTestObject(SCMServiceStub.FindRepositoryByName.class);
			SCMServiceStub.SCMRepository scmRepo = new SCMServiceStub.SCMRepository();
			scmRepo.setName(name);
			findRepositoryByName.setRepository(scmRepo);
			SCMServiceStub stub = new SCMServiceStub();
			FindRepositoryByNameResponse fpr = stub
					.findRepositoryByName(findRepositoryByName);
			SCMServiceStub.SCMRepository repositoryResponse = fpr.get_return();

			if (repositoryResponse == null) {
				throw new SubversionServiceException(
						"unable to find a repository for name " + name);
			}
			Repository repository = Utils.toRepository(repositoryResponse);
			// END OF EXTERNAL SERVICE INVOCATION

			// Using the notification service to throw an event :
			notification.throwEvent(new Event(path + "/" + name, caller,
					"Repository", "repository.findRepositoryByName", ""));
			return repository;
		} catch (Exception e) {
			// ctx.setRollbackOnly();
			logger.error("unable to find repository to the name at path "
					+ path + "/" + name, e);
			throw new SubversionServiceException(
					"unable to find repository to the name at path " + path
							+ "/" + name, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public FactoryResource findResource(String path) throws FactoryException {
		logger.info("findResource(...) called");
		logger.debug("params : path=" + path);

		throw new CoreServiceException(
				"No Resource are managed by Subversion Service");
	}

	public org.apache.axis2.databinding.ADBBean getTestObject(
			java.lang.Class type) throws java.lang.Exception {
		return (org.apache.axis2.databinding.ADBBean) type.newInstance();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void createRepository(String path, String name)
			throws SubversionServiceException {
		logger.info("createRepository(...) called");
		logger.debug("params : path=" + path + "/" + name);

		try {
			// Checking if the connected user has the permission to getAllBugs
			// the resource giving pep :
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "write");

			// Performing a lookup in the naming to recover the Resource
			// Identifier
			//FactoryResourceIdentifier identifier = binding.lookup(path);

			// Checking if this resource identifier is really a resource managed
			// by this service (a Project resource)
			// checkResourceType(identifier, "Project");

			// STARTING SPECIFIC EXTERNAL SERVICE RESOURCE LOADING OR METHOD
			// CALLS
			// TODO better to use business delegate
			SCMServiceStub.CreateRepository createRepository = (SCMServiceStub.CreateRepository) getTestObject(SCMServiceStub.CreateRepository.class);
			SCMServiceStub.SCMRepository scmRepo = new SCMServiceStub.SCMRepository();
			scmRepo.setName(name);
			createRepository.setRepository(scmRepo);
			SCMServiceStub stub = new SCMServiceStub();
			stub.createRepository(createRepository);

			// END OF EXTERNAL SERVICE INVOCATION

			// Using the notification service to throw an event :
			notification.throwEvent(new Event(path + "/" + name, caller,
					"Repository", "repository.createRepository", ""));
		} catch (Exception e) {
			// ctx.setRollbackOnly();
			logger.error("unable to create repository to the name at path "
					+ path + "/" + name, e);
			throw new SubversionServiceException(
					"unable to create repository to the name at path " + path
							+ "/" + name, e);
		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void deleteRepository(String path, String name)
			throws SubversionServiceException {
		logger.info("deleteRepository(...) called");
		logger.debug("params : path=" + path + "/" + name);

		try {
			// Checking if the connected user has the permission to getAllBugs
			// the resource giving pep :
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "write");

			// Performing a lookup in the naming to recover the Resource
			// Identifier
			//FactoryResourceIdentifier identifier = binding.lookup(path);

			// Checking if this resource identifier is really a resource managed
			// by this service (a Project resource)
			// checkResourceType(identifier, "Project");

			// STARTING SPECIFIC EXTERNAL SERVICE RESOURCE LOADING OR METHOD
			// CALLS
			// TODO better to use business delegate
			SCMServiceStub.DeleteRepository deleteRepository = (SCMServiceStub.DeleteRepository) getTestObject(SCMServiceStub.DeleteRepository.class);
			SCMServiceStub.SCMRepository scmRepo = new SCMServiceStub.SCMRepository();
			scmRepo.setName(name);
			deleteRepository.setRepository(scmRepo);
			SCMServiceStub stub = new SCMServiceStub();
			stub.deleteRepository(deleteRepository);

			// END OF EXTERNAL SERVICE INVOCATION

			// Using the notification service to throw an event :
			notification.throwEvent(new Event(path + "/" + name, caller,
					"Repository", "repository.deleteRepository", ""));
		} catch (Exception e) {
			// ctx.setRollbackOnly();
			logger.error("unable to delete repository to the name at path "
					+ path + "/" + name, e);
			throw new SubversionServiceException(
					"unable to delete repository to the name at path " + path
							+ "/" + name, e);
		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean removeUsers(ArrayList<Profile> users, Repository repository)
			throws SubversionServiceException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean setUserRole(String path, String repositoryName, String role, String userName)
			throws SubversionServiceException {
		logger.info("setUserRoles(...) called");
		logger.debug("params : path=" + path + "/" + ", Repository name =" + repositoryName + ", Role = " + role);

		try {
			// Checking if the connected user has the permission to getAllBugs
			// the resource giving pep :
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "write");

			// Performing a lookup in the naming to recover the Resource
			// Identifier
			//FactoryResourceIdentifier identifier = binding.lookup(path);

			// Checking if this resource identifier is really a resource managed
			// by this service (a Project resource)
			// checkResourceType(identifier, "Project");

			// STARTING SPECIFIC EXTERNAL SERVICE RESOURCE LOADING OR METHOD
			// CALLS
			// TODO better to use business delegate
			SCMServiceStub.SetUserRoles setUserRole = (SCMServiceStub.SetUserRoles) getTestObject(SCMServiceStub.SetUserRoles.class);
			SCMServiceStub.SCMRepository scmRepo = new SCMServiceStub.SCMRepository();
			scmRepo.setName(repositoryName);
			setUserRole.setRepository(scmRepo);
			SCMServiceStub.SCMAcl acl = new SCMServiceStub.SCMAcl();
			acl.setRole(role);
			SCMServiceStub.SCMUser user = new SCMServiceStub.SCMUser();
			user.setUserName(userName);
			user.setPassword(userName);
			acl.setUser(user);
			SCMServiceStub.SCMAcl[] acls = {acl};
			SCMServiceStub stub = new SCMServiceStub();
			setUserRole.setAcls(acls);
			stub.setUserRoles(setUserRole);

			// END OF EXTERNAL SERVICE INVOCATION

			// Using the notification service to throw an event :
			notification.throwEvent(new Event(path + "/" + repositoryName, caller,
					"Role", "repository.setUserRole", ""));
			return true;
		} catch (Exception e) {
			// ctx.setRollbackOnly();
			logger.error("unable to set Users Roles to the name at path "
					+ path + "/" + repositoryName, e);
			throw new SubversionServiceException(
					"unable to set Users Roles  to the name at path " + path
							+ "/" + repositoryName, e);
		}
	}


	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public SVNLogEntry[] getCommitsInfo(String path, Repository repository)
			throws SubversionServiceException {
		return getCommitsInfoFromRevTo(path, repository, "0", "-1");
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public SVNLogEntry[] getCommitsInfoFromRev(String path, Repository repository, String revision)
			throws SubversionServiceException {		
		return getCommitsInfoFromRevTo(path, repository, revision, "-1");
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public SVNLogEntry[] getCommitsInfoFromRevTo(String path, Repository repository, String revisionFrom,
			String revisionTo) throws SubversionServiceException {
		logger.info("getCommitsInfo(...) called");
		logger.debug("params : path=" + path + "/" + repository.getName());

		try {
			// Checking if the connected user has the permission to getAllBugs
			// the resource giving pep :
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "read");

			// Performing a lookup in the naming to recover the Resource
			// Identifier
			//FactoryResourceIdentifier identifier = binding.lookup(path);

			// Checking if this resource identifier is really a resource managed
			// by this service (a Project resource)
			// checkResourceType(identifier, "Project");

			// STARTING SPECIFIC EXTERNAL SERVICE RESOURCE LOADING OR METHOD
			// CALLS
			// TODO better to use business delegate
			SCMServiceStub.GetCommitsInfoWithFromRevTo commitsInfos = (SCMServiceStub.GetCommitsInfoWithFromRevTo) getTestObject(SCMServiceStub.GetCommitsInfoWithFromRevTo.class);
			SCMServiceStub.SCMRepository scmRepo = new SCMServiceStub.SCMRepository();
			scmRepo.setName(repository.getName());
			commitsInfos.setRepository(scmRepo);
			commitsInfos.setRevisionFrom(revisionFrom);
			commitsInfos.setRevisionTo(revisionTo);
			SCMServiceStub stub = new SCMServiceStub();
			
			// END OF EXTERNAL SERVICE INVOCATION

			// Using the notification service to throw an event :
			notification.throwEvent(new Event(path + "/" + repository.getName(), caller,
					"CommitsInfos", "repository.getCommitsInfos", ""));
			if (stub.getCommitsInfoWithFromRevTo(commitsInfos) != null) {
				return Utils.toSVNLogEntry(stub.getCommitsInfoWithFromRevTo(commitsInfos).get_return());
			} else {
				return null;
			}
		} catch (Exception e) {
			// ctx.setRollbackOnly();
			logger.error("unable to getCommitsInfos to the name at path "
					+ path + "/" + repository.getName(), e);
			throw new SubversionServiceException(
					"unable to getCommitsInfos to the name at path " + path
							+ "/" + repository.getName(), e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void getModifiedLines(Repository repository, String fileName)
			throws SubversionServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void getModifiedLinesFromRev(Repository repository, String fileName,
			String revision) throws SubversionServiceException {
		// TODO Auto-generated method stub
		
	}
}
