package org.qualipso.factory.bugtracker;

import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.bugtracker.core.BugTrackerManager;
import org.qualipso.factory.bugtracker.core.IBugTrackerManager;
import org.qualipso.factory.bugtracker.dto.IssueAttributesDto;
import org.qualipso.factory.bugtracker.dto.IssueDto;
import org.qualipso.factory.bugtracker.dto.ProjectDto;
import org.qualipso.factory.bugtracker.dto.ProjectDtoBuilder;
import org.qualipso.factory.bugtracker.exception.BugTrackerServiceException;
import org.qualipso.factory.bugtracker.utils.Utils;
import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 june 2009
 */
@Stateless(name = "BugTracker", mappedName = "BugTrackerService")
@WebService(endpointInterface = "org.qualipso.factory.bugtracker.BugTrackerService", targetNamespace = "http://org.qualipso.factory.ws/service/bugtracker", serviceName = "BugTrackerService", portName = "BugTrackerServicePort")
@WebContext(contextRoot = "/factory-service-bugtracker", urlPattern = "/bugtracker")
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class BugTrackerServiceBean implements BugTrackerService {

	private static final String SERVICE_NAME = "BugTrackerService";
	private static final String RESOURCE_TYPE_ISSUE = "Issue";
	private static final String[] RESOURCE_TYPE_LIST = new String[] { RESOURCE_TYPE_ISSUE };
	private static Log logger = LogFactory.getLog(BugTrackerServiceBean.class);

	private BindingService binding;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	private MembershipService membership;
	private SessionContext ctx;
	private IBugTrackerManager  bugTrackerManager;
	



	public BugTrackerServiceBean() throws Exception {
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
	
	/**
	 * @return the bugTrackerManager
	 * @throws BugTrackerServiceException if an error occurred
	 */
	public IBugTrackerManager getBugTrackerManager() throws BugTrackerServiceException {
		if (this.bugTrackerManager == null) {
			this.bugTrackerManager = BugTrackerManager.getInstance();
		}
		return this.bugTrackerManager;
	}


	/**
	 * @param bugTrackerManager the bugTrackerManager to set
	 */
	public void setBugTrackerManager(IBugTrackerManager bugTrackerManager) {
		this.bugTrackerManager = bugTrackerManager;
	}

	/**
	 * Check if the resource type is from BugTRacker
	 * @param identifier
	 * @throws MembershipServiceException
	 */
	private void checkResourceTypeIssue(FactoryResourceIdentifier identifier) throws MembershipServiceException {
		if (!identifier.getService().equals(getServiceName())) {
			throw new MembershipServiceException("resource identifier "
					+ identifier + " does not refer to service "
					+ getServiceName());
		}
		if (!identifier.getType().equals(RESOURCE_TYPE_ISSUE)) {
			throw new MembershipServiceException("resource identifier "
					+ identifier + " does not refer to a resource of type "
					+ RESOURCE_TYPE_ISSUE);
		}
	}

	public String[] getResourceTypeList() {
		return RESOURCE_TYPE_LIST;
	}

	public String getServiceName() {
		return SERVICE_NAME;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public IssueDto[] getAllIssues(String path)
			throws BugTrackerServiceException {
		logger.info("getAllIssues(...) called");
		logger.debug("params : path=" + path);
		if (path == null) {
			throw new BugTrackerServiceException("getAllIssues: arg path cannot be null");
		}

		try {
			IssueDto[] bugs = new IssueDto[0];
			
			// Checking if the connected user has the permission to getAllIssues
			// the resource giving pep :
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "getAllIssues");

			//Search the project
			// Performing a lookup in the naming to recover the Resource
			// Identifier
			FactoryResourceIdentifier identifier = binding.lookup(path);
			// STARTING SPECIFIC EXTERNAL SERVICE RESOURCE LOADING OR METHOD
			// CALLS
			bugs = getBugTrackerManager().getAllIssues(identifier, path);
			//END OF EXTERNAL INVOCATION
			
			// Checking if the connected user has the permission to see each issue
			// the resource giving pep :
			if (bugs != null) {
				for (IssueDto issueDto : bugs) {
					pep.checkSecurity(caller, issueDto.getPath(), "issue.read");
				}
			}
			
			
			// Using the notification service to throw an event :
			notification.throwEvent(new Event(path, caller, "Issue", "project.allIssues", path));
			return bugs;
		} 
		catch (FactoryException fe) {
			throw new BugTrackerServiceException(fe);
		}
		catch (Exception e) {
			logger.error("unable to get all issues at path " + path, e);
			throw new BugTrackerServiceException(
					"unable to get all issues at path " + path, e);
		}
	}

	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.BugTrackerService#getIssue(java.lang.String)
	 */
	@Override
	public IssueDto getIssue(String path)
			throws BugTrackerServiceException {
		logger.info("getIssue(...) called");
		logger.debug("params : path=" + path);

		if (path == null) {
			throw new BugTrackerServiceException("getIssue: arg path cannot be null");
		}
		try {
			IssueDto bug = null;
			
			// Checking if the connected user has the permission to getAllIssues
			// the resource giving pep :
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "read");

			//Search the issue
			// Performing a lookup in the naming to recover the Resource
			// Identifier
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceTypeIssue(identifier);
			
			// STARTING SPECIFIC EXTERNAL SERVICE RESOURCE LOADING OR METHOD
			// CALLS
			bug = getBugTrackerManager().getIssue(path);
			//END OF EXTERNAL INVOCATION
			
			// Using the notification service to throw an event :
			notification.throwEvent(new Event(path, caller, "Issue", "issue.read", path));
			return bug;
		} 
		catch (FactoryException fe) {
			throw new BugTrackerServiceException(fe);
		}
		catch (Exception e) {
		logger.error("unable to get issue at path " + path, e);
		throw new BugTrackerServiceException(
				"unable to get issue at path " + path, e);
		}
	}

	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.BugTrackerService#createIssue(java.lang.String, org.qualipso.factory.bugtracker.dto.IssueDto)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public String createIssue(String path, IssueDto issueDto)
			throws BugTrackerServiceException {
		logger.info("createIssue(...) called");
		if (logger.isDebugEnabled()) {
			if (issueDto != null) {
				logger.debug("params : path=" + path + ", issueDto=[" + issueDto.toString() + "]");
			}
			else {
				logger.debug("params : path=" + path + ", issueDto=[null]");
			}
		}
		
		
		if (path == null) {
			throw new BugTrackerServiceException("createIssue: arg path cannot be null");
		}
		
		checkFieldMandatory(issueDto, "createIssue");

		try {
			//Checking if the connected user has the permission to create a resource giving pep : 
			//  - the profile path of the connected user (caller)
			//  - the parent of the path (we check the 'create' permission on the parent of the given path)
			//  - the name of the permission to check ('create')
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "create");
			
			
			//Search the project
			long idProject = 0;
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			ProjectDto project = getBugTrackerManager().findProjectByName(identifier.getId());
			if (project == null) {
				ProjectDto dto = ProjectDtoBuilder.createDefault(identifier.getId(), path);
				idProject = getBugTrackerManager().createProject(dto);
			}
			else {
				idProject = project.getId();
			}
			
			//STARTING SPECIFIC EXTERNAL SERVICE RESOURCE CREATION OR METHOD CALL
			long idIssue = getBugTrackerManager().createIssue(issueDto, idProject);
			//END OF EXTERNAL INVOCATION
			
			//Binding the external resource in the naming using the generated resource ID : 
			FactoryResourceIdentifier resourceIdentifier = new FactoryResourceIdentifier(SERVICE_NAME, RESOURCE_TYPE_ISSUE, String.valueOf(idIssue));
			
			final String pathIssue  = Utils.generatePathIssueFactory(path, idIssue);
			
			binding.bind(resourceIdentifier, pathIssue);
			
			//Need to set some properties on the node : 
			binding.setProperty(pathIssue, FactoryResourceProperty.CREATION_TIMESTAMP, "" + System.currentTimeMillis());
			binding.setProperty(pathIssue, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, "" + System.currentTimeMillis());
			binding.setProperty(pathIssue, FactoryResourceProperty.AUTHOR, caller);
			
			//Need to create a new security policy for this resource : 
			//Giving the caller the Owner permission (aka all permissions)
			String policyId = UUID.randomUUID().toString();
			pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, caller, pathIssue));
			
			//Setting security properties on the node : 
			binding.setProperty(pathIssue, FactoryResourceProperty.OWNER, caller);
			binding.setProperty(pathIssue, FactoryResourceProperty.POLICY_ID, policyId);
			
			//Using the notification service to throw an event : 
			notification.throwEvent(new Event(pathIssue, caller, "Issue", "issue.create", String.valueOf(idIssue)));
			
			return pathIssue;
		} 
		catch (FactoryException fe) {
			ctx.setRollbackOnly();
			throw new BugTrackerServiceException(fe);
		}
		catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to create the project at path " + path, e);
			throw new BugTrackerServiceException("unable to create the name at path " + path, e);
		}
	}
	

	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.BugTrackerService#updateIssue(java.lang.String, org.qualipso.factory.bugtracker.dto.IssueDto)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void updateIssue(String path, IssueDto issueDto)
			throws BugTrackerServiceException {
		logger.info("updateIssue(...) called");
		
		if (logger.isDebugEnabled()) {
			if (issueDto != null) {
				logger.debug("params : path=" + path + ", issueDto=[" + issueDto.toString() + "]");
			}
			else {
				logger.debug("params : path=" + path + ", issueDto=[null]");
			}
		}
		
		if (path == null) {
			throw new BugTrackerServiceException("updateIssue: arg path cannot be null");
		}
		checkFieldMandatory(issueDto, "updateIssue");
		
		try {
			//Checking if the connected user has the permission to create a resource giving pep : 
			//  - the profile path of the connected user (caller)
			//  - the parent of the path (we check the 'create' permission on the parent of the given path)
			//  - the name of the permission to check ('create')
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "update");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceTypeIssue(identifier);
			
			
			//STARTING SPECIFIC EXTERNAL SERVICE RESOURCE CREATION OR METHOD CALL
			getBugTrackerManager().updateIssue(issueDto, path);
			//END OF EXTERNAL INVOCATION
			
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			
			//Using the notification service to throw an event : 
			notification.throwEvent(new Event(path, caller, "Issue", "issue.update", path));
		} 
		catch (FactoryException fe) {
			ctx.setRollbackOnly();
			throw new BugTrackerServiceException(fe);
		}
		catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to update the issue at path " + path, e);
			throw new BugTrackerServiceException("unable to update the issue at path " + path, e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.BugTrackerService#deleteIssue(java.lang.String, java.lang.String)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void deleteIssue(String path)
			throws BugTrackerServiceException {
		logger.info("deleteIssue(...) called");
		logger.debug("params : path=" + path);
		if (path == null) {
			throw new BugTrackerServiceException("deleteIssue: arg path cannot be null");
		}
		try {
			//Checking if the connected user has the permission to create a resource giving pep : 
			//  - the profile path of the connected user (caller)
			//  - the parent of the path (we check the 'create' permission on the parent of the given path)
			//  - the name of the permission to check ('create')
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "delete");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceTypeIssue(identifier);
			
			
			//STARTING SPECIFIC EXTERNAL SERVICE RESOURCE CREATION OR METHOD CALL
			getBugTrackerManager().deleteIssue(path);
			//END OF EXTERNAL INVOCATION
			
			String policyId = binding.getProperty(path, FactoryResourceProperty.POLICY_ID, false);
			pap.deletePolicy(policyId);
			
			binding.unbind(path);
			
			//Using the notification service to throw an event : 
			notification.throwEvent(new Event(path, caller, "Issue", "issue.delete", path));
		} 
		catch (FactoryException fe) {
			ctx.setRollbackOnly();
			throw new BugTrackerServiceException(fe);
		}
		catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to delete the project at path " + path, e);
			throw new BugTrackerServiceException("unable to delete the name at path " + path, e);
		}
	}

	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.BugTrackerService#getIssueAttributes()
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public IssueAttributesDto getIssueAttributes() throws BugTrackerServiceException {
		logger.info("getIssueAttributes(...) called");

		try {
		
			IssueAttributesDto dto = getBugTrackerManager().getIssueAttributesDto();
			return dto;
		} 
		catch (Exception e) {
		logger.error("unable to get issue attributes", e);
		throw new BugTrackerServiceException(
				"unable to get issue attributes", e);
		}
	}

	
	/**
	 * Check if mandatory fields are missing
	 * Mandatory fields are summary and description
	 * @param issueDto
	 * @throws BugTrackerServiceException if mandatory field is missing
	 */
	private void checkFieldMandatory(IssueDto issueDto, String method) throws BugTrackerServiceException {
		if (issueDto == null) {
			logger.error(method + ": arg issueDto cannot be null");
			throw new BugTrackerServiceException(method + ": arg issueDto cannot be null");
		}
		if (StringUtils.isEmpty(issueDto.getSummary())) {
			logger.error(method + ": Mandatory field 'issueDto.summary' is missing");
			throw new BugTrackerServiceException("Mandatory field 'summary' is missing");
		}
		if (StringUtils.isEmpty(issueDto.getDescription())) {
			logger.error(method + ": Mandatory field 'issueDto.description' is missing");
			throw new BugTrackerServiceException("Mandatory field 'description' is missing");
		}
	}


	/* (non-Javadoc)
	 * @see org.qualipso.factory.FactoryService#findResource(java.lang.String)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public FactoryResource findResource(String path) throws FactoryException {
		logger.info("findResource(...) called");
		logger.debug("params : path=" + path);
		
		throw new CoreServiceException("No Resource are managed by BugTracker Service");
	}
}
