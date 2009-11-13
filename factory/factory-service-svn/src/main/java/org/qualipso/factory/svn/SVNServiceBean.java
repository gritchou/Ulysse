package org.qualipso.factory.svn;

import java.io.File;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.ssh.SSHService;
import org.qualipso.factory.svn.entity.SVNRepository;
import org.qualipso.factory.svn.utils.SubversionResources;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

/**
 * @author Gerald  Oster (oster@loria.fr)
 * @date 9 October 2009
 */
@Stateless(name = SVNService.SERVICE_NAME, mappedName = FactoryNamingConvention.SERVICE_PREFIX + SVNService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.svn.SVNService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + SVNService.SERVICE_NAME, serviceName = SVNService.SERVICE_NAME)
@WebContext(contextRoot = FactoryNamingConvention.WEB_SERVICE_ROOT_CONTEXT + "-" + SVNService.SERVICE_NAME, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX +  SVNService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class SVNServiceBean implements SVNService {	
	private static final String SERVICE_NAME = "SVNService";
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
		String repositoriesDir = SubversionResources.getInstance().getRootDirRepositories();
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
	public void createSVNRepository(String path, String name, String description) throws SVNServiceException {
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
			
			// FIXME: svnadmin command path should be provided in a configuration, isn't it?
			//ProcessBuilder pb = new ProcessBuilder("svnadmin", "create", repoPath);
			//Map<String, String> env = pb.environment();
			//pb.directory(repositoryFolder);
			//Process p = pb.start();
			//if (p.waitFor() != 0) {
			//	logger.error("unable to initialize the svn-repository directory at path " + repoPath);
			//	throw new SVNServiceException("unable to initialize the svn-repository directory at path " + repoPath);								
			//}	
			
			// FIXME: this should be equivalent but using svnkit
			try {
				SVNURL localURL = SVNRepositoryFactory.createLocalRepository(repositoryFolder, true, false);
			} catch (SVNException e) {
				logger.error("unable to create local repository for test in path '"+repoPath+"'", e);
				throw new SVNServiceException("unable to initialize the svn-repository directory at path " + repoPath, e);								
			}
			
			svnRepository.setFolder(repoPath);			
			em.persist(svnRepository);
			
			binding.bind(svnRepository.getFactoryResourceIdentifier(), path);
			
			binding.setProperty(path, FactoryResourceProperty.CREATION_TIMESTAMP, "" + System.currentTimeMillis());
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, "" + System.currentTimeMillis());
			binding.setProperty(path, FactoryResourceProperty.AUTHOR, caller);
			
			String policyId = UUID.randomUUID().toString();
			pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, caller, path));
			
			binding.setProperty(path, FactoryResourceProperty.OWNER, caller);
			binding.setProperty(path, FactoryResourceProperty.POLICY_ID, policyId);
			
			notification.throwEvent(new Event(path, caller, "SVNRepository", "svn.svn-repository.create", ""));
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
			
			checkResourceType(identifier, "SVNRepository");
		
			SVNRepository repository = em.find(SVNRepository.class, identifier.getId());
			if ( repository == null ) {
				throw new SVNServiceException("unable to find a svn repository for id " + identifier.getId());
			}
			repository.setResourcePath(path);
			
			notification.throwEvent(new Event(path, caller, "SVNRepository", "svn.svn-repository.read", ""));
			
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
			
			checkResourceType(identifier, "SVNRepository");
		
			SVNRepository repository = em.find(SVNRepository.class, identifier.getId());
			if ( repository == null ) {
				throw new SVNServiceException("unable to find a svn repository for id " + identifier.getId());
			}
			repository.setName(name);
			repository.setDescription(description);
			em.merge(repository);
			
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			
			notification.throwEvent(new Event(path, caller, "SVNRepository", "svn.svn-repository.update", ""));
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
			
			checkResourceType(identifier, "SVNRepository");
		
			SVNRepository repository = em.find(SVNRepository.class, identifier.getId());
			if ( repository == null ) {
				throw new SVNServiceException("unable to find a svn repository for id " + identifier.getId());
			}
			deleteFolderRecursively(new File(repository.getFolder()));
			em.remove(repository);
			
			String policyId = binding.getProperty(path, FactoryResourceProperty.POLICY_ID, false);
			pap.deletePolicy(policyId);

			binding.unbind(path);
			
			notification.throwEvent(new Event(path, caller, "SVNRepository", "svn.svn-repository.delete", ""));
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
			
			if ( identifier.getType().equals("SVNRepository") ) {
				return readSVNRepository(path);
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
	
	private void checkResourceType(FactoryResourceIdentifier identifier, String resourceType) throws MembershipServiceException {
		if ( !identifier.getService().equals(getServiceName()) ) {
			throw new MembershipServiceException("resource identifier " + identifier + " does not refer to service " + getServiceName());
		}
		if ( !identifier.getType().equals(resourceType) ) {
			throw new MembershipServiceException("resource identifier " + identifier + " does not refer to a resource of type " + resourceType);
		}
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

}
