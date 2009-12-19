package org.qualipso.factory.svn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.LocalBinding;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.ssh.SSHService;
import org.qualipso.factory.svn.entity.SVNNode;
import org.qualipso.factory.svn.entity.SVNRepository;
import org.qualipso.factory.svn.exception.SVNServiceException;
import org.qualipso.factory.svn.exception.SVNTechnicalException;
import org.qualipso.factory.svn.ssh.command.filter.component.SVNResourceType;
import org.qualipso.factory.svn.utils.SVNProperties;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

/**
 * @author Gerald  Oster (oster@loria.fr)
 * @date 9 October 2009
 */
@Stateless(name = SVNService.SERVICE_NAME, mappedName = FactoryNamingConvention.SERVICE_PREFIX + SVNService.SERVICE_NAME)
@LocalBinding(jndiBinding = FactoryNamingConvention.LOCAL_SERVICE_PREFIX + SVNService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.svn.SVNService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + SVNService.SERVICE_NAME, serviceName = SVNService.SERVICE_NAME)
@WebContext(contextRoot = FactoryNamingConvention.WEB_SERVICE_ROOT_MODULE_CONTEXT + "-" + SVNService.SERVICE_NAME, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX +  SVNService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class SVNServiceBean implements SVNService, SVNServiceLocal {	
	private static Log logger = LogFactory.getLog(SVNServiceBean.class);
	
	private BindingService binding;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	private MembershipService membership;
	private SSHService ssh;
	private File repositoriesFolder;
	private SessionContext ctx;
	private EntityManager em;
	
	
	public SVNServiceBean() {
		String repositoriesDir = SVNProperties.getInstance().getRootDirRepositories();
		repositoriesFolder = new File(repositoriesDir);
		if (!repositoriesFolder.exists()) {
			repositoriesFolder.mkdirs();
		}
		logger.debug("repositories folder set to " + repositoriesFolder.getAbsolutePath());
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
	
	@EJB
	public void setSSHService(SSHService ssh) {
		this.ssh = ssh;
	}

	public SSHService getSSHService() {
		return this.ssh;
	}
	
	@PostConstruct
	public void init() {
		logger.debug("post construct called, registering commands");
		try {
			if (ssh != null)
				ssh.registerCommand("svnserve", "org.qualipso.factory.svn.ssh.command.SVNServeSSHCommand");
			else {
				logger.warn("unable to register commands in SSHService");
			}
		} catch (Exception e) {
			logger.warn("unable to register commands in SSHService", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String createSVNRepository(String path, String name, String description) throws SVNServiceException {
		logger.info("createSVNRepository(...) called");
		logger.debug("params : path=" + path + ", name=" + name + ", description=" + description);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, PathHelper.getParentPath(path), "create");
			
			SVNRepository svnRepository = new SVNRepository();
			svnRepository.setId(UUID.randomUUID().toString());
			svnRepository.setName(name);
			svnRepository.setDescription(description);
			
			File repositoryFolder = new File(repositoriesFolder, svnRepository.getId());
			String repoPath = repositoryFolder.getAbsolutePath();
		
			if (! repositoryFolder.mkdirs()) {
				logger.error("unable to create the svn-repository directory at filepath " + repoPath);
				throw new SVNServiceException("unable to create the svn-repository directory at filepath " + repoPath);				
			}
			
			try {
				logger.debug("create local repository at path '"+repoPath+"'");
				SVNRepositoryFactory.createLocalRepository(repositoryFolder, null, true, false, true);
			} catch (SVNException e) {
				logger.error("unable to create local repository for test in path '"+repoPath+"'", e);
				throw new SVNServiceException("unable to initialize the svn-repository directory at path " + repoPath, e);								
			}
			
			svnRepository.setFolder(repoPath);			
			svnRepository.setResourcePath(path);
			em.persist(svnRepository);
			
			binding.bind(svnRepository.getFactoryResourceIdentifier(), path);
			
			binding.setProperty(path, FactoryResourceProperty.CREATION_TIMESTAMP, "" + System.currentTimeMillis());
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, "" + System.currentTimeMillis());
			binding.setProperty(path, FactoryResourceProperty.AUTHOR, caller);
			
			String policyId = UUID.randomUUID().toString();
			pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, caller, path));
			
			binding.setProperty(path, FactoryResourceProperty.OWNER, caller);
			binding.setProperty(path, FactoryResourceProperty.POLICY_ID, policyId);
			
			notification.throwEvent(new Event(path, caller, SVNRepository.RESOURCE_NAME, "svn.svn-repository.create", ""));
			
			return svnRepository.getId();
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to create the svn-repository at path " + path, e);
			throw new SVNServiceException("unable to create the svn-repository at path " + path, e);
		}
		
	}
	
	

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public SVNRepository readSVNRepository(String path) throws SVNServiceException {
		logger.info("readSVNRepository(...) called");
		logger.debug("params : path=" + path);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "read");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			checkResourceTypeRepository(identifier);
		
			SVNRepository repository = em.find(SVNRepository.class, identifier.getId());
			if ( repository == null ) {
				throw new SVNServiceException("unable to find a svn repository for id " + identifier.getId());
			}
			repository.setResourcePath(path);
			
			notification.throwEvent(new Event(path, caller, SVNRepository.RESOURCE_NAME, "svn.svn-repository.read", ""));
			
			return repository;
		} catch ( Exception e ) {
			logger.error("unable to read the svn repository at path " + path, e);
			throw new SVNServiceException("unable to read the svn repository at path " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateSVNRepository(String path, String name, String description) throws SVNServiceException {
		logger.info("updateSVNRepository(...) called");
		logger.debug("params : path=" + path + ", name=" + name + ", description=" + description);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "update");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			checkResourceTypeRepository(identifier);
		
			SVNRepository repository = em.find(SVNRepository.class, identifier.getId());
			if ( repository == null ) {
				throw new SVNServiceException("unable to find a svn repository for id " + identifier.getId());
			}
			repository.setName(name);
			repository.setDescription(description);
			em.merge(repository);
			
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			
			notification.throwEvent(new Event(path, caller, SVNRepository.RESOURCE_NAME, "svn.svn-repository.update", ""));
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to update the svn repository at path " + path, e);
			throw new SVNServiceException("unable to update the svn repository at path " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteSVNRepository(String path) throws SVNServiceException {
		logger.info("deleteSVNRepository(...) called");
		logger.debug("params : path=" + path);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "delete");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			checkResourceTypeRepository(identifier);
		
			SVNRepository repository = em.find(SVNRepository.class, identifier.getId());
			if ( repository == null ) {
				throw new SVNServiceException("unable to find a svn repository for id " + identifier.getId());
			}
			deleteFolderRecursively(new File(repository.getFolder()));
			em.remove(repository);
			
			deleteRecursively(path, caller);
			
			notification.throwEvent(new Event(path, caller, SVNRepository.RESOURCE_NAME, "svn.svn-repository.delete", ""));
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to delete the svn repository at path " + path, e);
			throw new SVNServiceException("unable to delete the svn repository at path " + path, e);
		}
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public FactoryResource findResource(String path) throws SVNServiceException {
		logger.info("findResource(...) called");
		logger.debug("params : path=" + path);
		
		try {
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			if ( !identifier.getService().equals(SERVICE_NAME) ) {
				throw new SVNServiceException("Resource " + identifier + " is not managed by " + SERVICE_NAME);
			}
			
			if ( identifier.getType().equals(SVNRepository.RESOURCE_NAME) ) {
				return readSVNRepository(path);
			} 
			else if ( identifier.getType().equals(SVNNode.RESOURCE_NAME) ) {
				SVNNode node = new SVNNode();
				node.setId(identifier.getId());
				node.setFactoryResourceIdentifier(identifier);
				node.setResourcePath(path);
				return node;
			} 
			
			throw new SVNServiceException("Resource " + identifier + " is not managed by SVN Service");
			
		} catch (Exception e) {
			logger.error("unable to find the resource at path " + path, e);
			throw new SVNServiceException("unable to find the resource at path " + path, e);
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
	
	private void checkResourceType(FactoryResourceIdentifier identifier, List<String> resourceTypeList) throws MembershipServiceException {
		if ( !identifier.getService().equals(getServiceName()) ) {
			throw new MembershipServiceException("resource identifier " + identifier + " does not refer to service " + getServiceName());
		}
		if ( !resourceTypeList.contains(identifier.getType()) ) {
			//FIXME tester l'ecriture de la liste
			throw new MembershipServiceException("resource identifier " + identifier + " does not refer to a resource of type " + resourceTypeList);
		}
	}
	
	private void checkResourceTypeRepository(FactoryResourceIdentifier identifier) throws MembershipServiceException {
		List<String> resourceTypeList = new ArrayList<String>();
		resourceTypeList.add(SVNRepository.RESOURCE_NAME);
		
		checkResourceType(identifier, resourceTypeList);
	}
	
	private void deleteFolderRecursively(File folder) {
		for (File file : folder.listFiles()) {
			if ( file.isDirectory() ) {
				deleteFolderRecursively(file);
			} else {
				file.delete();
			}
		}
		folder.delete();
	}

	
		@Override
	public void writeInRepository(SVNResourceType type, List<String> svnPathParts) throws SVNServiceException {
		
		logger.info("writeInRepository(...) called");
		if (type == null) {
			throw new SVNTechnicalException("type cannot be null");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("params : type=" + type.toString());
		}
		
		if (svnPathParts == null || svnPathParts.isEmpty()) {
			throw new SVNTechnicalException("svnPathParts cannot be null");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("params : svnPathParts=" + svnPathParts);
		}
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			
			String path = generateResourcePath(svnPathParts);
			
			//Check if the resource exist
			try {
				FactoryResourceIdentifier identifier = binding.lookup(path);
				
				List<String> resourceTypeList = new ArrayList<String>();
				resourceTypeList.add(SVNNode.RESOURCE_NAME);
				checkResourceType(identifier, resourceTypeList);
				if (type.equals(SVNResourceType.ADD_FILE)
						|| type.equals(SVNResourceType.ADD_DIR)) {
					//Update resource
					pep.checkSecurity(caller, path, "update");
					
					binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
					notification.throwEvent(new Event(path, caller, SVNNode.RESOURCE_NAME, "svn." + SVNNode.RESOURCE_NAME + ".update", ""));
				}
				else if (type.equals(SVNResourceType.DELETE_ENTRY)) {
					deleteRecursively(path, caller);
					
					//Using the notification service to throw an event : 
					notification.throwEvent(new Event(path, caller, SVNNode.RESOURCE_NAME, "svn." + SVNNode.RESOURCE_NAME + ".delete", ""));
				}
				else if (type.equals(SVNResourceType.CHECK_DELETE_ENTRY)) {
					pep.checkSecurity(caller, path, "delete");
					
					//Using the notification service to throw an event : 
					notification.throwEvent(new Event(path, caller, SVNNode.RESOURCE_NAME, "svn." + SVNNode.RESOURCE_NAME + ".delete", ""));
				}
				else {
					logger.error("Creating SVN resource is not possible for " + path);
					throw new SVNTechnicalException("Creating SVN resource is not possible for " + path);
				}
			}
			catch (PathNotFoundException e) {
				
				if (type.equals(SVNResourceType.DELETE_ENTRY) || type.equals(SVNResourceType.CHECK_DELETE_ENTRY)) {
					logger.error("Deleting SVN resource is not possible for " + path, e);
					throw new SVNServiceException(e);
				}
				
				//create resource
				FactoryResourceIdentifier identifierParent = binding.lookup(PathHelper.getParentPath(path));
				
				pep.checkSecurity(caller, PathHelper.getParentPath(path), "create");
				
				//parent must be a resource or a repository
				List<String> resourceTypeList = new ArrayList<String>();
				resourceTypeList.add(SVNRepository.RESOURCE_NAME);
				resourceTypeList.add(SVNNode.RESOURCE_NAME);
				checkResourceType(identifierParent, resourceTypeList);
				
				FactoryResourceIdentifier resourceIdentifier = generateIdentifierForResourceInRepository();

				binding.bind(resourceIdentifier, path);
				
				binding.setProperty(path, FactoryResourceProperty.CREATION_TIMESTAMP, "" + System.currentTimeMillis());
				binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, "" + System.currentTimeMillis());
				binding.setProperty(path, FactoryResourceProperty.AUTHOR, caller);
				
				String policyId = UUID.randomUUID().toString();
				pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, caller, path));
				
				binding.setProperty(path, FactoryResourceProperty.OWNER, caller);
				binding.setProperty(path, FactoryResourceProperty.POLICY_ID, policyId);
				
				notification.throwEvent(new Event(path, caller, SVNNode.RESOURCE_NAME, "svn." + SVNNode.RESOURCE_NAME + ".create", ""));
			}
		} 
		catch ( SVNServiceException e1 ) {
			logger.error("unable to write the svn resource " + type.toString() + "  in " + svnPathParts, e1);
			throw e1;
		} 
		catch ( Exception e ) {
			logger.error("unable to write the svn resource " + type.toString() + "  in " + svnPathParts, e);
			throw new SVNServiceException("unable to write the svn resource " + type.toString() + "  in " + svnPathParts, e);
		}
		
	}
	
	/**
	 * Delete recursively the resource
	 * @param path of the svnresource
	 * @param caller of the delete
	 * @throws Exception if an error occurred
	 */
	private void deleteRecursively(String path, String caller) throws Exception {
		String[] children = binding.list(path);
		if (children != null) {
			for (String child : children) {
				deleteRecursively(child, caller);
			}
		}
		
		//Delete resource
		pep.checkSecurity(caller, path, "delete");
		
		String policyId = binding.getProperty(path, FactoryResourceProperty.POLICY_ID, false);
		if (!StringUtils.isEmpty(policyId)) {
			pap.deletePolicy(policyId);
		}
		
		binding.unbind(path);
	}

	
	@Override
	public void readFromRepository(SVNResourceType type, List<String> svnPathParts)
			throws SVNServiceException {
		// TODO Auto-generated method stub
		logger.info("readFromRepository(...) called");
		if (type == null) {
			throw new SVNTechnicalException("type cannot be null");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("params : type=" + type.toString());
		}
		
		if (svnPathParts == null || svnPathParts.isEmpty()) {
			throw new SVNTechnicalException("svnPathParts cannot be null");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("params : svnPathParts=" + svnPathParts);
		}
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			
			String path = generateResourcePath(svnPathParts);
			
			try {
				FactoryResourceIdentifier identifier = binding.lookup(path);
				
				pep.checkSecurity(caller, path, "read");
				
				//parent must be a resource or a repository
				List<String> resourceTypeList = new ArrayList<String>();
				resourceTypeList.add(SVNRepository.RESOURCE_NAME);
				resourceTypeList.add(SVNNode.RESOURCE_NAME);
				checkResourceType(identifier, resourceTypeList);
			
				notification.throwEvent(new Event(path, caller, identifier.getType(), "svn." + identifier.getType() + ".read", ""));
			}
			catch (PathNotFoundException e) {
				logger.debug(path + " not found in the factory");
			}
		} 
		catch ( SVNServiceException e1 ) {
			logger.error("unable to read the svn resource " + type.toString() + "  in " + svnPathParts, e1);
			throw e1;
		} 
		catch ( Exception e ) {
			logger.error("unable to read the svn resource " + type.toString() + "  in " + svnPathParts, e);
			throw new SVNServiceException("unable to read the svn resource " + type.toString() + "  in " + svnPathParts, e);
		}
	}

	/**
	 * Generate the resource path in the factory
	 * @param svnPathParts of the resource
	 * @return Resource path in the factory
	 */
	private String generateResourcePath(List<String> svnPathParts) throws SVNServiceException {
		String resourcePath = "";
		
		//Get idRepository
		if (svnPathParts == null || svnPathParts.isEmpty()) {
			logger.error("svnPathParts cannot be null");
			throw new SVNTechnicalException("svnPathParts cannot be null");
		}
		
		
		String idRepository = svnPathParts.get(0);
		
		SVNRepository repository = em.find(SVNRepository.class, idRepository);
		
		if (repository == null) {
			logger.error("unable to find the repository for id [" + idRepository + "]");
			throw new SVNServiceException("unable to find the repository for id [" + idRepository + "]");
		}
		
		resourcePath = repository.getResourcePath();
		
		//Add svnPath
		if (svnPathParts.size() > 1) {
			for (int i = 1; i < svnPathParts.size(); i++) {
				resourcePath = resourcePath + "/" + svnPathParts.get(i).hashCode();
			}
		}
		return resourcePath;
	}
	
	/**
	 * Generate a FactoryResourceIdentifier for a resource in a repository
	 * @return FactoryResourceIdentifier
	 */
	public FactoryResourceIdentifier generateIdentifierForResourceInRepository() {
		return new FactoryResourceIdentifier(SERVICE_NAME, SVNNode.RESOURCE_NAME, UUID.randomUUID().toString());
	}

	@Override
	public SVNNode readNode(String path) {
		// TODO Auto-generated method stub
		return null;
	}
}
