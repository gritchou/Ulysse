package org.qualipso.factory.git;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.qualipso.factory.git.entity.GITRepository;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.ssh.SSHService;
import org.spearce.jgit.lib.Repository;
import org.spearce.jgit.transport.ReceivePack;
import org.spearce.jgit.transport.UploadPack;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.RemoteOutputStream;
import com.healthmarketscience.rmiio.RemoteOutputStreamClient;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 22 september 2009
 */
@Stateless(name = "GIT", mappedName = FactoryNamingConvention.JNDI_SERVICE_PREFIX + "GITService")
@LocalBinding(jndiBinding=FactoryNamingConvention.JNDI_SERVICE_PREFIX + "GITService-local")
@WebService(endpointInterface = "org.qualipso.factory.git.GITService", targetNamespace = "http://org.qualipso.factory.ws/service/git", serviceName = "GITService", portName = "GITServicePort")
@WebContext(contextRoot = "/factory-service-git", urlPattern = "/git")
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class GITServiceBean implements GITService, GITServiceLocal {
	
	private static final String SERVICE_NAME = "GITService";
	private static final String[] RESOURCE_TYPE_LIST = new String[] {"GITRepository"};
	private static final String REPOSITORIES_FOLDER = "data/git-repositories";
	private static Log logger = LogFactory.getLog(GITServiceBean.class);
	
	private BindingService binding;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	private MembershipService membership;
	private SSHService ssh;
	private File repositoriesFolder;
	private SessionContext ctx;
	private EntityManager em;
	
	public GITServiceBean() {
		repositoriesFolder = new File(REPOSITORIES_FOLDER);
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
			ssh.registerCommand("git-upload-pack", "org.qualipso.factory.git.ssh.command.UploadPackSSHCommand");
			ssh.registerCommand("git-receive-pack", "org.qualipso.factory.git.ssh.command.ReceivePackSSHCommand");
		} catch (Exception e) {
			logger.warn("unable to register commands in SSHService", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createGITRepository(String path, String name, String description) throws GITServiceException {
		logger.info("createGITRepository(...) called");
		logger.debug("params : path=" + path + ", name=" + name + ", description=" + description);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, PathHelper.getParentPath(path), "create");
			
			GITRepository gitRepository = new GITRepository();
			gitRepository.setId(UUID.randomUUID().toString());
			gitRepository.setName(name);
			gitRepository.setDescription(description);
			
			File repositoryFolder = new File(repositoriesFolder, gitRepository.getId());
			Repository repository = new Repository(repositoryFolder);
			repository.create();
			
			gitRepository.setFolder(repositoryFolder.getAbsolutePath());
			
			em.persist(gitRepository);
			
			binding.bind(gitRepository.getFactoryResourceIdentifier(), path);
			
			binding.setProperty(path, FactoryResourceProperty.CREATION_TIMESTAMP, "" + System.currentTimeMillis());
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, "" + System.currentTimeMillis());
			binding.setProperty(path, FactoryResourceProperty.AUTHOR, caller);
			
			String policyId = UUID.randomUUID().toString();
			pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, caller, path));
			
			binding.setProperty(path, FactoryResourceProperty.OWNER, caller);
			binding.setProperty(path, FactoryResourceProperty.POLICY_ID, policyId);
			
			notification.throwEvent(new Event(path, caller, "GITRepository", "git.git-repository.create", ""));
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to create the git-repository at path " + path, e);
			throw new GITServiceException("unable to create the git-repository at path " + path, e);
		}
		
	}
	
	

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public GITRepository readGITRepository(String path) throws GITServiceException {
		logger.info("readGITRepository(...) called");
		logger.debug("params : path=" + path);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "read");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			checkResourceType(identifier, "GITRepository");
		
			GITRepository repository = em.find(GITRepository.class, identifier.getId());
			if ( repository == null ) {
				throw new GITServiceException("unable to find a git repository for id " + identifier.getId());
			}
			repository.setResourcePath(path);
			
			notification.throwEvent(new Event(path, caller, "GITRepository", "git.git-repository.read", ""));
			
			return repository;
		} catch ( Exception e ) {
			logger.error("unable to read the git repository at path " + path, e);
			throw new GITServiceException("unable to read the git repository at path " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateGITRepository(String path, String name, String description) throws GITServiceException {
		logger.info("updateGITRepository(...) called");
		logger.debug("params : path=" + path + ", name=" + name + ", description=" + description);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "update");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			checkResourceType(identifier, "GITRepository");
		
			GITRepository repository = em.find(GITRepository.class, identifier.getId());
			if ( repository == null ) {
				throw new GITServiceException("unable to find a git repository for id " + identifier.getId());
			}
			repository.setName(name);
			repository.setDescription(description);
			em.merge(repository);
			
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			
			notification.throwEvent(new Event(path, caller, "GITRepository", "git.git-repository.update", ""));
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to update the git repository at path " + path, e);
			throw new GITServiceException("unable to update the git repository at path " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteGITRepository(String path) throws GITServiceException {
		logger.info("deleteGITRepository(...) called");
		logger.debug("params : path=" + path);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "delete");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			checkResourceType(identifier, "GITRepository");
		
			GITRepository repository = em.find(GITRepository.class, identifier.getId());
			if ( repository == null ) {
				throw new GITServiceException("unable to find a git repository for id " + identifier.getId());
			}
			deleteFolderRecursively(new File(repository.getFolder()));
			em.remove(repository);
			
			String policyId = binding.getProperty(path, FactoryResourceProperty.POLICY_ID, false);
			pap.deletePolicy(policyId);

			binding.unbind(path);
			
			notification.throwEvent(new Event(path, caller, "GITRepository", "git.git-repository.delete", ""));
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to delete the git repository at path " + path, e);
			throw new GITServiceException("unable to delete the git repository at path " + path, e);
		}
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remotePullFromGITRepository(String path, RemoteInputStream in, RemoteOutputStream out, RemoteOutputStream messages) throws GITServiceException {
		try {
			this.pullFromGITRepository(path, RemoteInputStreamClient.wrap(in), RemoteOutputStreamClient.wrap(out), RemoteOutputStreamClient.wrap(messages));
		} catch ( IOException e ) {
			throw new GITServiceException(e);
		}
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remotePushToGITRepository(String path, RemoteInputStream in, RemoteOutputStream out, RemoteOutputStream messages) throws GITServiceException {
		try {
			this.pushToGITRepository(path, RemoteInputStreamClient.wrap(in), RemoteOutputStreamClient.wrap(out), RemoteOutputStreamClient.wrap(messages));
		} catch ( IOException e ) {
			throw new GITServiceException(e);
		}
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void pullFromGITRepository(String path, InputStream in, OutputStream out, OutputStream messages) throws GITServiceException {
		logger.info("pullFromGITRepository(...) called");
		logger.debug("params : path=" + path);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "pull");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			checkResourceType(identifier, "GITRepository");
		
			GITRepository gitRepository = em.find(GITRepository.class, identifier.getId());
			if ( gitRepository == null ) {
				throw new GITServiceException("unable to find a git repository for id " + identifier.getId());
			}
			
			Repository repository = new Repository(new File(gitRepository.getFolder()));
			UploadPack uploadPack = new UploadPack(repository);
			uploadPack.upload(in, out, messages);
			repository.close();
			
			notification.throwEvent(new Event(path, caller, "GITRepository", "git.git-repository.pull", ""));
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to pull from the git repository at path " + path, e);
			throw new GITServiceException("unable to pull from the git repository at path " + path, e);
		}
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void pushToGITRepository(String path, InputStream in, OutputStream out, OutputStream messages) throws GITServiceException {
		logger.info("pushToGITRepository(...) called");
		logger.debug("params : path=" + path);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "push");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			checkResourceType(identifier, "GITRepository");
		
			GITRepository gitRepository = em.find(GITRepository.class, identifier.getId());
			if ( gitRepository == null ) {
				throw new GITServiceException("unable to find a git repository for id " + identifier.getId());
			}
			
			Repository repository = new Repository(new File(gitRepository.getFolder()));
			ReceivePack receivePack = new ReceivePack(repository);
			receivePack.receive(in, out, messages);
			repository.close();
		
			notification.throwEvent(new Event(path, caller, "GITRepository", "git.git-repository.push", ""));
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to push to the git repository at path " + path, e);
			throw new GITServiceException("unable to push to the git repository at path " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public FactoryResource findResource(String path) throws GITServiceException {
		logger.info("findResource(...) called");
		logger.debug("params : path=" + path);
		
		try {
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			if ( !identifier.getService().equals(SERVICE_NAME) ) {
				throw new GITServiceException("Resource " + identifier + " is not managed by " + SERVICE_NAME);
			}
			
			if ( identifier.getType().equals("GITRepository") ) {
				return readGITRepository(path);
			} 
			
			throw new GITServiceException("Resource " + identifier + " is not managed by GIT Service");
			
		} catch (Exception e) {
			logger.error("unable to find the resource at path " + path, e);
			throw new GITServiceException("unable to find the resource at path " + path, e);
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
