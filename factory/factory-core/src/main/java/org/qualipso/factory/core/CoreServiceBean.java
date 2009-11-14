package org.qualipso.factory.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;

import javax.activation.DataHandler;
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
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.IgnoreDependency;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.core.entity.File;
import org.qualipso.factory.core.entity.FileData;
import org.qualipso.factory.core.entity.FileDataSource;
import org.qualipso.factory.core.entity.Folder;
import org.qualipso.factory.core.entity.Link;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;

import eu.medsea.mimeutil.MimeUtil;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 july 2009
 */
@Stateless(name = CoreService.SERVICE_NAME, mappedName = FactoryNamingConvention.SERVICE_PREFIX + CoreService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.core.CoreService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + CoreService.SERVICE_NAME, serviceName = CoreService.SERVICE_NAME)
@WebContext(contextRoot = FactoryNamingConvention.WEB_SERVICE_CORE_MODULE_CONTEXT, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX + CoreService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class CoreServiceBean implements CoreService {
	
	private static Log logger = LogFactory.getLog(CoreServiceBean.class);
	
	private BindingService binding;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	private MembershipService membership;
	private SessionContext ctx;
	private EntityManager em;
	
	public CoreServiceBean() {
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
	@IgnoreDependency
	public void setMembershipService(MembershipService membership) {
		this.membership = membership;
	}

	public MembershipService getMembershipService() {
		return this.membership;
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createLink(String path, String toPath) throws CoreServiceException {
		logger.info("createLink(...) called");
		logger.debug("params : path=" + path + ", toPath=" + toPath);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, PathHelper.getParentPath(path), "create");
			
			Link link = new Link();
			link.setId(UUID.randomUUID().toString());
			link.setLink(toPath);
			em.persist(link);
			
			binding.bind(link.getFactoryResourceIdentifier(), path);
			
			binding.setProperty(path, FactoryResourceProperty.CREATION_TIMESTAMP, "" + System.currentTimeMillis());
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, "" + System.currentTimeMillis());
			binding.setProperty(path, FactoryResourceProperty.AUTHOR, caller);
			
			String policyId = UUID.randomUUID().toString();
			pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, caller, path));
			
			binding.setProperty(path, FactoryResourceProperty.OWNER, caller);
			binding.setProperty(path, FactoryResourceProperty.POLICY_ID, policyId);
			
			notification.throwEvent(new Event(path, caller, Link.RESOURCE_NAME, Event.buildEventType(CoreService.SERVICE_NAME, Link.RESOURCE_NAME, "create"), ""));
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to create the link at path " + path, e);
			throw new CoreServiceException("unable to create the link at path " + path, e);
		}
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Link readLink(String path) throws CoreServiceException {
		logger.info("readResourceLink(...) called");
		logger.debug("params : path=" + path);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
		
			pep.checkSecurity(caller, path, "read");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			checkResourceType(identifier, Link.RESOURCE_NAME);
		
			Link link = em.find(Link.class, identifier.getId());
			if ( link == null ) {
				throw new CoreServiceException("unable to find a link for id " + identifier.getId());
			}
			link.setResourcePath(path);
			
			notification.throwEvent(new Event(path, caller, Link.RESOURCE_NAME, Event.buildEventType(CoreService.SERVICE_NAME, Link.RESOURCE_NAME, "read"), ""));
			
			return link;
		} catch ( Exception e ) {
			logger.error("unable to read the link at path " + path, e);
			throw new CoreServiceException("unable to read the link at path " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateLink(String path, String toPath) throws CoreServiceException {
		logger.info("updateLink(...) called");
		logger.debug("params : path=" + path + ", toPath=" + toPath);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "update");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			checkResourceType(identifier, Link.RESOURCE_NAME);
		
			Link link = em.find(Link.class, identifier.getId());
			if ( link == null ) {
				throw new CoreServiceException("unable to find a link for id " + identifier.getId());
			}
			link.setLink(toPath);
			em.merge(link);
			
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			
			notification.throwEvent(new Event(path, caller, Link.RESOURCE_NAME, Event.buildEventType(CoreService.SERVICE_NAME, Link.RESOURCE_NAME, "update"), ""));
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to update the link at path " + path, e);
			throw new CoreServiceException("unable to update the link at path " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteLink(String path) throws CoreServiceException {
		logger.info("deleteLink(...) called");
		logger.debug("params : path=" + path);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "delete");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			checkResourceType(identifier, Link.RESOURCE_NAME);
		
			Link link = em.find(Link.class, identifier.getId());
			if ( link == null ) {
				throw new CoreServiceException("unable to find a link for id " + identifier.getId());
			}
			em.remove(link);
			
			binding.unbind(path);
			
			notification.throwEvent(new Event(path, caller, Link.RESOURCE_NAME, Event.buildEventType(CoreService.SERVICE_NAME, Link.RESOURCE_NAME, "delete"), ""));
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to delete the link at path " + path, e);
			throw new CoreServiceException("unable to delete the link at path " + path, e);
		}
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createFolder(String path, String name, String description) throws CoreServiceException {
		logger.info("createFolder(...) called");
		logger.debug("params : path=" + path + ", name=" + name + ", description=" + description);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, PathHelper.getParentPath(path), "create");
			
			Folder folder = new Folder();
			folder.setId(UUID.randomUUID().toString());
			folder.setName(name);
			folder.setDescription(description);
			em.persist(folder);
			
			binding.bind(folder.getFactoryResourceIdentifier(), path);
			
			binding.setProperty(path, FactoryResourceProperty.CREATION_TIMESTAMP, "" + System.currentTimeMillis());
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, "" + System.currentTimeMillis());
			binding.setProperty(path, FactoryResourceProperty.AUTHOR, caller);
			
			String policyId = UUID.randomUUID().toString();
			pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, caller, path));
			
			binding.setProperty(path, FactoryResourceProperty.OWNER, caller);
			binding.setProperty(path, FactoryResourceProperty.POLICY_ID, policyId);
			
			notification.throwEvent(new Event(path, caller, Folder.RESOURCE_NAME, Event.buildEventType(CoreService.SERVICE_NAME, Folder.RESOURCE_NAME, "create"), ""));
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to create the folder at path " + path, e);
			throw new CoreServiceException("unable to create the folder at path " + path, e);
		}
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Folder readFolder(String path) throws CoreServiceException {
		logger.info("readFolder(...) called");
		logger.debug("params : path=" + path);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "read");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			checkResourceType(identifier, Folder.RESOURCE_NAME);
		
			Folder folder = em.find(Folder.class, identifier.getId());
			if ( folder == null ) {
				throw new CoreServiceException("unable to find a folder for id " + identifier.getId());
			}
			folder.setResourcePath(path);
			
			notification.throwEvent(new Event(path, caller, Folder.RESOURCE_NAME, Event.buildEventType(CoreService.SERVICE_NAME, Folder.RESOURCE_NAME, "read"), ""));
			
			return folder;
		} catch ( Exception e ) {
			logger.error("unable to read the folder at path " + path, e);
			throw new CoreServiceException("unable to read the folder at path " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateFolder(String path, String name, String description) throws CoreServiceException {
		logger.info("updateFolder(...) called");
		logger.debug("params : path=" + path + ", name=" + name + ", description=" + description);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "update");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			checkResourceType(identifier, Folder.RESOURCE_NAME);
		
			Folder folder = em.find(Folder.class, identifier.getId());
			if ( folder == null ) {
				throw new CoreServiceException("unable to find a folder for id " + identifier.getId());
			}
			folder.setName(name);
			folder.setDescription(description);
			em.merge(folder);
			
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			
			notification.throwEvent(new Event(path, caller, Folder.RESOURCE_NAME, Event.buildEventType(CoreService.SERVICE_NAME, Folder.RESOURCE_NAME, "update"), ""));
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to update the resource-folder at path " + path, e);
			throw new CoreServiceException("unable to update the resource-folder at path " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteFolder(String path) throws CoreServiceException {
		logger.info("deleteFolder(...) called");
		logger.debug("params : path=" + path);
		
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "delete");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			checkResourceType(identifier, Folder.RESOURCE_NAME);
		
			Folder folder = em.find(Folder.class, identifier.getId());
			if ( folder == null ) {
				throw new CoreServiceException("unable to find a folder for id " + identifier.getId());
			}
			em.remove(folder);
			
			binding.unbind(path);
			
			notification.throwEvent(new Event(path, caller, Folder.RESOURCE_NAME, Event.buildEventType(CoreService.SERVICE_NAME, Folder.RESOURCE_NAME, "delete"), ""));
		} catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to delete the folder at path " + path, e);
			throw new CoreServiceException("unable to delete the folder at path " + path, e);
		}
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createFile(String path, String filename, String contenttype, String description, FileData filedata) throws CoreServiceException {
		try {
			createFile(path, filename, contenttype, description, filedata.getData().getInputStream());
		} catch (IOException e) {
			logger.error("unable to create file", e);
			throw new CoreServiceException("unable to create file", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createFile(String path, String filename, String contenttype, String description, java.io.File data) throws CoreServiceException {
		try {
			createFile(path, filename, contenttype, description, new FileInputStream(data));
		} catch (IOException e) {
			logger.error("unable to create file", e);
			throw new CoreServiceException("unable to create file", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createFile(String path, String filename, String contenttype, String description, InputStream data) throws CoreServiceException {
		logger.info("createFile(...) called");
		logger.debug("params : path=" + path + ", filename=" + filename + ", contenttype=" + contenttype + ", description=" + description);

		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, PathHelper.getParentPath(path), "create");

			File file = new File();
			file.setId(UUID.randomUUID().toString());
			file.setName(filename);
			if ( contenttype != null && contenttype.length() > 0 ) {
				file.setContentType(contenttype);
			} else {
				logger.info("no content type, trying to detect...");
				Collection<?> mimetypes = MimeUtil.getMimeTypes(data);
				logger.debug("detected mimetypes : " + mimetypes);
				if ( mimetypes.size() > 0 && !mimetypes.contains("UNKNOWN_MIME_TYPE")) {
					String mimetype = (String) mimetypes.iterator().next();
					logger.debug("choosed mimetype : " + mimetype);
					file.setContentType(mimetype);
				} else {
					logger.debug("unable to determine mimetype, setting it to application/octet-stream");
					file.setContentType("application/octet-stream");
				}
				file.setContentType("application/octet-stream");
			} 
			file.setDescription(description);
			file.setNbReads(0);
			file.setBlob(Hibernate.createBlob(data));
			file.setSize(file.getBlob().length());
			em.persist(file);

			binding.bind(file.getFactoryResourceIdentifier(), path);

			binding.setProperty(path, FactoryResourceProperty.CREATION_TIMESTAMP, "" + System.currentTimeMillis());
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, "" + System.currentTimeMillis());
			binding.setProperty(path, FactoryResourceProperty.AUTHOR, caller);

			String policyId = UUID.randomUUID().toString();
			pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, caller, path));

			binding.setProperty(path, FactoryResourceProperty.OWNER, caller);
			binding.setProperty(path, FactoryResourceProperty.POLICY_ID, policyId);

			notification.throwEvent(new Event(path, caller, File.RESOURCE_NAME, Event.buildEventType(CoreService.SERVICE_NAME, File.RESOURCE_NAME, "create"), ""));
		} catch (Exception e) {
			ctx.setRollbackOnly();
			logger.error("unable to create the file at path " + path, e);
			throw new CoreServiceException("unable to create the file at path " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public File readFile(String path) throws CoreServiceException {
		logger.info("readFile(...) called");
		logger.debug("params : path=" + path);

		try {
			String caller = membership.getProfilePathForConnectedIdentifier();

			pep.checkSecurity(caller, path, "read");

			FactoryResourceIdentifier identifier = binding.lookup(path);

			checkResourceType(identifier, File.RESOURCE_NAME);

			File file = em.find(File.class, identifier.getId());
			if (file == null) {
				throw new CoreServiceException("unable to find a file for id " + identifier.getId());
			}
			file.setResourcePath(path);

			notification.throwEvent(new Event(path, caller, File.RESOURCE_NAME, Event.buildEventType(CoreService.SERVICE_NAME, File.RESOURCE_NAME, "read"), ""));

			return file;
		} catch (Exception e) {
			logger.error("unable to read the file at path " + path, e);
			throw new CoreServiceException("unable to read the file at path " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateFile(String path, String filename, String contenttype, String description, FileData filedata) throws CoreServiceException {
		try {
			updateFile(path, filename, contenttype, description, filedata.getData().getInputStream());
		} catch (IOException e) {
			logger.error("unable to update file", e);
			throw new CoreServiceException("unable to update file", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateFile(String path, String filename, String contenttype, String description, java.io.File data) throws CoreServiceException {
		try {
			updateFile(path, filename, contenttype, description, new FileInputStream(data));
		} catch (IOException e) {
			logger.error("unable to update file", e);
			throw new CoreServiceException("unable to update file", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateFile(String path, String filename, String contenttype, String description, InputStream data) throws CoreServiceException {
		logger.info("updateFile(...) called");
		logger.debug("params : path=" + path + ", filename=" + filename + ", contenttype=" + contenttype + ", description=" + description);

		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, PathHelper.getParentPath(path), "update");

			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			checkResourceType(identifier, File.RESOURCE_NAME);

			File file = em.find(File.class, identifier.getId());
			if (file == null) {
				throw new CoreServiceException("unable to find a file for id " + identifier.getId());
			}
			file.setName(filename);
			if ( contenttype != null && contenttype.length() > 0 ) {
				file.setContentType(contenttype);
			} else {
				file.setContentType("application/octet-stream");
			} 
			file.setDescription(description);
			if ( data != null ) {
				file.setBlob(Hibernate.createBlob(data));
				file.setNbReads(0);
				file.setSize(file.getBlob().length());
			}
			em.merge(file);

			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, "" + System.currentTimeMillis());
			
			notification.throwEvent(new Event(path, caller, File.RESOURCE_NAME, Event.buildEventType(CoreService.SERVICE_NAME, File.RESOURCE_NAME, "update"), ""));
		} catch (Exception e) {
			ctx.setRollbackOnly();
			logger.error("unable to update the file at path " + path, e);
			throw new CoreServiceException("unable to update the file at path " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteFile(String path) throws CoreServiceException {
		logger.info("deleteFile(...) called");
		logger.debug("params : path=" + path);

		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "delete");

			FactoryResourceIdentifier identifier = binding.lookup(path);

			checkResourceType(identifier, File.RESOURCE_NAME);

			File file = em.find(File.class, identifier.getId());
			if (file == null) {
				throw new CoreServiceException("unable to find a file for id " + identifier.getId());
			}
			em.remove(file);

			binding.unbind(path);

			notification.throwEvent(new Event(path, caller, File.RESOURCE_NAME, Event.buildEventType(CoreService.SERVICE_NAME, File.RESOURCE_NAME, "delete"), ""));
		} catch (Exception e) {
			ctx.setRollbackOnly();
			logger.error("unable to delete the file at path " + path, e);
			throw new CoreServiceException("unable to delete the file at path " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public FileData getFileData(String path) throws CoreServiceException {
		logger.info("getFileData(...) called");
		logger.debug("params : path=" + path);

		try {
			String caller = membership.getProfilePathForConnectedIdentifier();

			pep.checkSecurity(caller, path, "read");

			FactoryResourceIdentifier identifier = binding.lookup(path);

			checkResourceType(identifier, File.RESOURCE_NAME);

			File file = em.find(File.class, identifier.getId());
			if (file == null) {
				throw new CoreServiceException("unable to find a file for id " + identifier.getId());
			}
			file.setNbReads(file.getNbReads()+1);
			em.merge(file);

			FileData data = new FileData(new DataHandler(new FileDataSource(file)));

			notification.throwEvent(new Event(path, caller, File.RESOURCE_NAME, Event.buildEventType(CoreService.SERVICE_NAME, File.RESOURCE_NAME, "read-data"), ""));

			return data;
		} catch (Exception e) {
			logger.error("unable to read data for the file at path " + path, e);
			throw new CoreServiceException("unable to read data for the file at path " + path, e);
		}
	}
	
	private void checkResourceType(FactoryResourceIdentifier identifier, String resourceType) throws CoreServiceException {
		if ( !identifier.getService().equals(getServiceName()) ) {
			throw new CoreServiceException("resource identifier " + identifier + " does not refer to service " + getServiceName());
		}
		if ( !identifier.getType().equals(resourceType) ) {
			throw new CoreServiceException("resource identifier " + identifier + " does not refer to a resource of type " + resourceType);
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
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public FactoryResource findResource(String path) throws FactoryException {
		logger.info("findResource(...) called");
		logger.debug("params : path=" + path);
		
		try {
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			if ( !identifier.getService().equals(CoreService.SERVICE_NAME) ) {
				throw new CoreServiceException("Resource " + identifier + " is not managed by Core Service");
			}
			
			if ( identifier.getType().equals(Folder.RESOURCE_NAME) ) {
				return readFolder(path);
			} 
			
			if ( identifier.getType().equals(File.RESOURCE_NAME) ) {
				return readFile(path);
			}
			
			if ( identifier.getType().equals(Link.RESOURCE_NAME) ) {
				return readLink(path);
			}
			
			throw new CoreServiceException("Resource " + identifier + " is not managed by Core Service");
			
		} catch (Exception e) {
			logger.error("unable to find the resource at path " + path, e);
			throw new CoreServiceException("unable to find the resource at path " + path, e);
		}
	}

}
