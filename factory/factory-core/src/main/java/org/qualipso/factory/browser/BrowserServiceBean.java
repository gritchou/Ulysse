package org.qualipso.factory.browser;

import java.util.Vector;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.naming.InitialContext;
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
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 4 august 2009
 */
@Stateless(name = "Browser", mappedName = FactoryNamingConvention.JNDI_SERVICE_PREFIX + "BrowserService")
@WebService(endpointInterface = "org.qualipso.factory.browser.BrowserService", targetNamespace = "http://org.qualipso.factory.ws/service/browser", serviceName = "BrowserService", portName = "BrowserServicePort")
@WebContext(contextRoot = "/factory-core", urlPattern = "/browser")
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class BrowserServiceBean implements BrowserService {
	
	private static final String SERVICE_NAME = "BrowserService";
	private static final String[] RESOURCE_TYPE_LIST = new String[] {};
	private static Log logger = LogFactory.getLog(BrowserServiceBean.class);
	
	private BindingService binding;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	private MembershipService membership;
	private SessionContext ctx;
	private EntityManager em;
	
	public BrowserServiceBean() {
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
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public FactoryResource findResource(String path) throws BrowserServiceException {
		logger.info("findResource(...) called");
		logger.debug("params : path=" + path);
		
		try {
			PathHelper.valid(path);
			String npath = PathHelper.normalize(path);
			
			String caller = membership.getProfilePathForConnectedIdentifier();
			
			FactoryResourceIdentifier identifier = binding.lookup(npath);
			
			InitialContext ctx = new InitialContext();
			FactoryService service = (FactoryService) ctx.lookup(FactoryNamingConvention.getJNDINameForService(identifier.getService()));
			
			notification.throwEvent(new Event(npath, caller, "Resource", "browser.resource.find", ""));
			
			return service.findResource(npath);			
		} catch ( Exception e ) {
			throw new BrowserServiceException("unable to find a resource for path " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean hasChildren(String path) throws BrowserServiceException {
		logger.info("hasChildren(...) called");
		logger.debug("params : path=" + path);
		
		try {
			PathHelper.valid(path);
			String npath = PathHelper.normalize(path);
			
			String caller = membership.getProfilePathForConnectedIdentifier();
			
			pep.checkSecurity(caller, npath, "read");

			String[] children = binding.list(npath);
			
			notification.throwEvent(new Event(npath, caller, "Resource", "browser.resource.has-children", ""));
			
			if ( children.length > 0 ) {
				return true;
			} else {
				return false;
			}
		} catch ( Exception e ) {
			throw new BrowserServiceException("unable to test if path " + path + " has children", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String[] listChildren(String path) throws BrowserServiceException {
		logger.info("listChildren(...) called");
		logger.debug("params : path=" + path);
		
		try {
			PathHelper.valid(path);
			String npath = PathHelper.normalize(path);
			
			String caller = membership.getProfilePathForConnectedIdentifier();
			
			pep.checkSecurity(caller, npath, "read");

			String[] children = binding.list(npath);
			
			notification.throwEvent(new Event(npath, caller, "Resource", "browser.resource.list-children", ""));
			
			return children;
		} catch ( Exception e ) {
			throw new BrowserServiceException("unable to list children for path " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String[] listChildrenOfType(String path, String servicePattern, String typePattern) throws BrowserServiceException {
		logger.info("listChildrenOfType(...) called");
		logger.debug("params : path=" + path + ", typePattern=" + typePattern);
		
		try {
			PathHelper.valid(path);
			String npath = PathHelper.normalize(path);
			
			String caller = membership.getProfilePathForConnectedIdentifier();
			
			pep.checkSecurity(caller, npath, "read");

			String[] children = binding.list(npath);
			Vector<String> matchingChilds = new Vector<String> ();
			
			for (String child : children) {
				FactoryResourceIdentifier identifier = binding.lookup(child);
				logger.debug(identifier.getService() + ".matches(" + servicePattern + ") && " + identifier.getType() + ".matches(" + typePattern + ")");
				if ( identifier.getService().matches(servicePattern) && identifier.getType().matches(typePattern) ) {
					matchingChilds.add(child);
				}
			}
			
			notification.throwEvent(new Event(npath, caller, "Resource", "browser.resource.list-children-of-type", "type-pattern:" + typePattern));
			
			return matchingChilds.toArray(new String[matchingChilds.size()]);
		} catch ( Exception e ) {
			throw new BrowserServiceException("unable to list children of type for path " + path, e);
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
}
