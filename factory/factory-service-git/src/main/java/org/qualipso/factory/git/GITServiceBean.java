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
import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.LocalBinding;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathAlreadyBoundException;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.binding.PathNotEmptyException;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.git.entity.GITRepository;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.AccessDeniedException;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.ssh.SSHService;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.UploadPack;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import com.healthmarketscience.rmiio.RemoteOutputStream;
import com.healthmarketscience.rmiio.RemoteOutputStreamClient;

@Stateless(name = GITService.SERVICE_NAME, mappedName = FactoryNamingConvention.SERVICE_PREFIX + GITService.SERVICE_NAME)
@LocalBinding(jndiBinding = FactoryNamingConvention.LOCAL_SERVICE_PREFIX + GITService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.git.GITService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + GITService.SERVICE_NAME, serviceName = GITService.SERVICE_NAME)
@WebContext(contextRoot = FactoryNamingConvention.WEB_SERVICE_ROOT_MODULE_CONTEXT + "-" + GITService.SERVICE_NAME, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX
		+ GITService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class GITServiceBean implements GITService, GITServiceLocal {

	private static final String REPOSITORIES_FOLDER = "data/git-repositories";
	private static Log logger = LogFactory.getLog(GITServiceBean.class);
	private static boolean registrationOk = false;

	private BindingService binding;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	private MembershipService membership;
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

	@PostConstruct
	public void init() {
		if ( !registrationOk ) {
			logger.debug("registering commands");
			try {
				ObjectName objectName = new ObjectName("service.qualipso:service=SSHServer");
				MBeanServer server = MBeanServerLocator.locateJBoss();
				SSHService mbean = (SSHService) MBeanServerInvocationHandler.newProxyInstance(server, objectName, SSHService.class, false);
				
				mbean.registerCommand("git-upload-pack", "org.qualipso.factory.git.ssh.command.UploadPackSSHCommand");
				mbean.registerCommand("git-receive-pack", "org.qualipso.factory.git.ssh.command.ReceivePackSSHCommand");
				
				registrationOk = true;
			} catch (Exception e) {
				logger.warn("unable to register commands in SSHService", e);
			}
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createGITRepository(String path, String name, String description) throws GITServiceException, AccessDeniedException, InvalidPathException, PathAlreadyBoundException {
		logger.info("createGITRepository(...) called");
		logger.debug("params : path=" + path + ", name=" + name + ", description=" + description);

		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(membership.getConnectedIdentifierSubjects(), PathHelper.getParentPath(path), "create");

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

			notification.throwEvent(new Event(path, caller, GITRepository.RESOURCE_NAME, Event.buildEventType(GITService.SERVICE_NAME,
					GITRepository.RESOURCE_NAME, "create"), ""));
		} catch (AccessDeniedException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (InvalidPathException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathAlreadyBoundException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			ctx.setRollbackOnly();
			throw new GITServiceException("unable to create the git-repository at path: " + path, e);
		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public GITRepository readGITRepository(String path) throws GITServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.info("readGITRepository(...) called");
		logger.debug("params : path=" + path);

		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(membership.getConnectedIdentifierSubjects(), path, "read");

			FactoryResourceIdentifier identifier = binding.lookup(path);

			checkResourceType(identifier, GITRepository.RESOURCE_NAME);

			GITRepository repository = em.find(GITRepository.class, identifier.getId());
			if (repository == null) {
				throw new GITServiceException("unable to find a git repository for id " + identifier.getId());
			}
			repository.setResourcePath(path);

			notification.throwEvent(new Event(path, caller, GITRepository.RESOURCE_NAME, Event.buildEventType(GITService.SERVICE_NAME,
					GITRepository.RESOURCE_NAME, "read"), ""));

			return repository;
		} catch (AccessDeniedException e) {
			throw e;
		} catch (InvalidPathException e) {
			throw e;
		} catch (PathNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new GITServiceException("unable to read the git repository at path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateGITRepository(String path, String name, String description) throws GITServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.info("updateGITRepository(...) called");
		logger.debug("params : path=" + path + ", name=" + name + ", description=" + description);

		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(membership.getConnectedIdentifierSubjects(), path, "update");

			FactoryResourceIdentifier identifier = binding.lookup(path);

			checkResourceType(identifier, GITRepository.RESOURCE_NAME);

			GITRepository repository = em.find(GITRepository.class, identifier.getId());
			if (repository == null) {
				throw new GITServiceException("unable to find a git repository for id " + identifier.getId());
			}
			repository.setName(name);
			repository.setDescription(description);
			em.merge(repository);

			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");

			notification.throwEvent(new Event(path, caller, GITRepository.RESOURCE_NAME, Event.buildEventType(GITService.SERVICE_NAME,
					GITRepository.RESOURCE_NAME, "update"), ""));
		} catch (AccessDeniedException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (InvalidPathException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathNotFoundException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			ctx.setRollbackOnly();
			throw new GITServiceException("unable to update the git repository at path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteGITRepository(String path) throws GITServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException, PathNotEmptyException {
		logger.info("deleteGITRepository(...) called");
		logger.debug("params : path=" + path);

		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(membership.getConnectedIdentifierSubjects(), path, "delete");

			FactoryResourceIdentifier identifier = binding.lookup(path);

			checkResourceType(identifier, GITRepository.RESOURCE_NAME);

			GITRepository repository = em.find(GITRepository.class, identifier.getId());
			if (repository == null) {
				throw new GITServiceException("unable to find a git repository for id " + identifier.getId());
			}
			deleteFolderRecursively(new File(repository.getFolder()));
			em.remove(repository);

			String policyId = binding.getProperty(path, FactoryResourceProperty.POLICY_ID, false);
			pap.deletePolicy(policyId);

			binding.unbind(path);

			notification.throwEvent(new Event(path, caller, GITRepository.RESOURCE_NAME, Event.buildEventType(GITService.SERVICE_NAME,
					GITRepository.RESOURCE_NAME, "delete"), ""));
		} catch (AccessDeniedException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (InvalidPathException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathNotFoundException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathNotEmptyException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			ctx.setRollbackOnly();
			throw new GITServiceException("unable to delete the git repository at path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remotePullFromGITRepository(String path, RemoteInputStream in, RemoteOutputStream out, RemoteOutputStream messages) throws GITServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		try {
			this.pullFromGITRepository(path, RemoteInputStreamClient.wrap(in), RemoteOutputStreamClient.wrap(out), RemoteOutputStreamClient.wrap(messages));
		} catch (IOException e) {
			throw new GITServiceException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remotePushToGITRepository(String path, RemoteInputStream in, RemoteOutputStream out, RemoteOutputStream messages) throws GITServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		try {
			this.pushToGITRepository(path, RemoteInputStreamClient.wrap(in), RemoteOutputStreamClient.wrap(out), RemoteOutputStreamClient.wrap(messages));
		} catch (IOException e) {
			throw new GITServiceException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void pullFromGITRepository(String path, InputStream in, OutputStream out, OutputStream messages) throws GITServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.info("pullFromGITRepository(...) called");
		logger.debug("params : path=" + path);

		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(membership.getConnectedIdentifierSubjects(), path, "pull");

			FactoryResourceIdentifier identifier = binding.lookup(path);

			checkResourceType(identifier, GITRepository.RESOURCE_NAME);

			GITRepository gitRepository = em.find(GITRepository.class, identifier.getId());
			if (gitRepository == null) {
				throw new GITServiceException("unable to find a git repository for id " + identifier.getId());
			}

			Repository repository = new Repository(new File(gitRepository.getFolder()));
			UploadPack uploadPack = new UploadPack(repository);
			uploadPack.upload(in, out, messages);
			repository.close();

			notification.throwEvent(new Event(path, caller, GITRepository.RESOURCE_NAME, Event.buildEventType(GITService.SERVICE_NAME,
					GITRepository.RESOURCE_NAME, "pull"), ""));
		} catch (AccessDeniedException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (InvalidPathException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathNotFoundException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			ctx.setRollbackOnly();
			throw new GITServiceException("unable to pull from the git repository at path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void pushToGITRepository(String path, InputStream in, OutputStream out, OutputStream messages) throws GITServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.info("pushToGITRepository(...) called");
		logger.debug("params : path=" + path);

		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(membership.getConnectedIdentifierSubjects(), path, "push");

			FactoryResourceIdentifier identifier = binding.lookup(path);

			checkResourceType(identifier, GITRepository.RESOURCE_NAME);

			GITRepository gitRepository = em.find(GITRepository.class, identifier.getId());
			if (gitRepository == null) {
				throw new GITServiceException("unable to find a git repository for id " + identifier.getId());
			}

			Repository repository = new Repository(new File(gitRepository.getFolder()));
			ReceivePack receivePack = new ReceivePack(repository);
			receivePack.receive(in, out, messages);
			repository.close();

			notification.throwEvent(new Event(path, caller, GITRepository.RESOURCE_NAME, Event.buildEventType(GITService.SERVICE_NAME,
					GITRepository.RESOURCE_NAME, "push"), ""));
		} catch (AccessDeniedException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (InvalidPathException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathNotFoundException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			ctx.setRollbackOnly();
			throw new GITServiceException("unable to push to the git repository at path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public FactoryResource findResource(String path) throws FactoryException, InvalidPathException, PathNotFoundException, AccessDeniedException {
		logger.info("findResource(...) called");
		logger.debug("params : path=" + path);

		FactoryResourceIdentifier identifier = binding.lookup(path);

		if (!identifier.getService().equals(GITService.SERVICE_NAME)) {
			throw new GITServiceException("resource " + identifier + " is not managed by service " + GITService.SERVICE_NAME);
		}

		if (identifier.getType().equals(GITRepository.RESOURCE_NAME)) {
			return readGITRepository(path);
		}

		throw new GITServiceException("resource " + identifier + " is not managed by service " + GITService.SERVICE_NAME);
	}

	@Override
	public String[] getResourceTypeList() {
		return GITService.RESOURCE_TYPE_LIST;
	}

	@Override
	public String getServiceName() {
		return GITService.SERVICE_NAME;
	}

	private void checkResourceType(FactoryResourceIdentifier identifier, String resourceType) throws GITServiceException {
		if (!identifier.getService().equals(GITService.SERVICE_NAME)) {
			throw new GITServiceException("resource identifier " + identifier + " does not refer to service " + GITService.SERVICE_NAME);
		}
		if (!identifier.getType().equals(resourceType)) {
			throw new GITServiceException("resource identifier " + identifier + " does not refer to a resource of type " + resourceType);
		}
	}

	private void deleteFolderRecursively(File folder) {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				deleteFolderRecursively(file);
			} else {
				file.delete();
			}
		}
		folder.delete();
	}

}
