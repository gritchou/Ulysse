/**
 * 
 * Copyright (C) 2006-2010 THALES
 * http://www.thalesgroup.fr - gregory.cunha@thalesgroup.com
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the license LGPL.
 *
 * Initial author :
 * Gregory Cunha from Thales Service, THERESIS Competence Center Open Source Software
 *
 */
package org.qualipso.factory.bugtracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
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
import org.qualipso.factory.binding.BindingServiceException;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.bugtracker.core.BugTrackerManager;
import org.qualipso.factory.bugtracker.core.IBugTrackerManager;
import org.qualipso.factory.bugtracker.core.IssueComplexe;
import org.qualipso.factory.bugtracker.core.IssueExternal;
import org.qualipso.factory.bugtracker.dto.IssueAttributesDto;
import org.qualipso.factory.bugtracker.dto.IssueDto;
import org.qualipso.factory.bugtracker.dto.IssueDtoBuilder;
import org.qualipso.factory.bugtracker.entity.Issue;
import org.qualipso.factory.bugtracker.exception.BugTrackerServiceException;
import org.qualipso.factory.bugtracker.utils.Utils;
import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;

@Stateless(name = BugTrackerService.SERVICE_NAME, mappedName = FactoryNamingConvention.SERVICE_PREFIX + BugTrackerService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.bugtracker.BugTrackerService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + BugTrackerService.SERVICE_NAME, serviceName = BugTrackerService.SERVICE_NAME)
@WebContext(contextRoot = FactoryNamingConvention.WEB_SERVICE_ROOT_MODULE_CONTEXT + "-" + BugTrackerService.SERVICE_NAME, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX + BugTrackerService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class BugTrackerServiceBean implements BugTrackerService {

	/**
	 * Logger
	 */
	private static Log logger = LogFactory.getLog(BugTrackerServiceBean.class);

	private BindingService binding;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	private MembershipService membership;
	private SessionContext ctx;
	private EntityManager em;
	private IBugTrackerManager  bugTrackerManager;
	
	
//	private ForumService forumService;
//	private DocumentService documentService;
//	
//	/**
//	 * name of the document folder created by default
//	 */
//	private static final String DOCUMENT_FOLDER_NAME = "issue_document_folder";
//	
//	/**
//	 * name of the forum created by default
//	 */
//	private static final String FORUM_NAME = "issue_forum";



	public BugTrackerServiceBean() throws Exception {
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
	
//	public ForumService getForumService() {
//		return forumService;
//	}
//
//	@EJB(name = "ForumService")
//	public void setForumService(ForumService forumService) {
//		this.forumService = forumService;
//	}
//
//
//	public DocumentService getDocumentService() {
//		return documentService;
//	}
//
//	@EJB(name = "DocumentService")
//	public void setDocumentService(DocumentService documentService) {
//		this.documentService = documentService;
//	}

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
		if (!identifier.getType().equals(Issue.RESOURCE_NAME)) {
			throw new MembershipServiceException("resource identifier "
					+ identifier + " does not refer to a resource of type "
					+ Issue.RESOURCE_NAME);
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
	public IssueDto[] getAllIssues(String projectPath)
			throws BugTrackerServiceException {
		logger.info("getAllIssues(...) called");
		logger.debug("params : path=" + projectPath);
		if (projectPath == null) {
			throw new BugTrackerServiceException("getAllIssues: arg path cannot be null");
		}

		try {
			IssueDto[] bugs = new IssueDto[0];
			
			// Checking if the connected user has the permission to getAllIssues
			// the resource giving pep :
			String caller = membership.getProfilePathForConnectedIdentifier();
			String[] subjects = membership.getConnectedIdentifierSubjects();
			pep.checkSecurity(subjects, projectPath, "getAllIssues");

			
			//Search the project
			// Performing a lookup in the naming to recover the Resource
			// Identifier
			FactoryResourceIdentifier identifier = binding.lookup(projectPath);
			// STARTING SPECIFIC EXTERNAL SERVICE RESOURCE LOADING OR METHOD
			// CALLS
			List<IssueExternal> issueExternals = getBugTrackerManager().getIssues(identifier, projectPath, null, null);
			//END OF EXTERNAL INVOCATION
			
			bugs = constructIssueDtoArray(subjects, projectPath, issueExternals);
			
			Event.buildEventType(BugTrackerService.SERVICE_NAME,
					Issue.RESOURCE_NAME, "read");
					
			// Using the notification service to throw an event :
			notification.throwEvent(new Event(projectPath, caller, Issue.RESOURCE_NAME, Event.buildEventType(BugTrackerService.SERVICE_NAME,
					Issue.RESOURCE_NAME, "read"), ""));
			return bugs;
		} 
		catch (FactoryException fe) {
			throw new BugTrackerServiceException(fe);
		}
		catch (Exception e) {
			logger.error("unable to get all issues at path " + projectPath, e);
			throw new BugTrackerServiceException(
					"unable to get all issues at path " + projectPath, e);
		}
	}


	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.BugTrackerService#getModifiedIssues(java.lang.String, java.util.Date)
	 */
	@Override
	public IssueDto[] getModifiedIssues(String projectPath,
			Date dateModification) throws BugTrackerServiceException {
		logger.info("getModifiedIssues(...) called");
		logger.debug("params : path=" + projectPath);
		logger.debug("params : dateModification=" + dateModification);
		if (projectPath == null) {
			throw new BugTrackerServiceException("getModifiedIssues: arg path cannot be null");
		}

		try {
			IssueDto[] bugs = new IssueDto[0];
			
			// Checking if the connected user has the permission to getAllIssues
			// the resource giving pep :
			String caller = membership.getProfilePathForConnectedIdentifier();
			String[] subjects = membership.getConnectedIdentifierSubjects();
			pep.checkSecurity(subjects, projectPath, "getModifiedIssues");

			
			//Search the project
			// Performing a lookup in the naming to recover the Resource
			// Identifier
			FactoryResourceIdentifier identifier = binding.lookup(projectPath);
			// STARTING SPECIFIC EXTERNAL SERVICE RESOURCE LOADING OR METHOD
			// CALLS
			List<IssueExternal> issueExternals = getBugTrackerManager().getIssues(identifier, projectPath, null, dateModification);
			//END OF EXTERNAL INVOCATION
			
			
			
			bugs = constructIssueDtoArray(subjects, projectPath, issueExternals);
			
			// Using the notification service to throw an event :
			notification.throwEvent(new Event(projectPath, caller, Issue.RESOURCE_NAME, Event.buildEventType(BugTrackerService.SERVICE_NAME,
					Issue.RESOURCE_NAME, "read"), ""));
			return bugs;
		} 
		catch (FactoryException fe) {
			throw new BugTrackerServiceException(fe);
		}
		catch (Exception e) {
			logger.error("unable to get modified issues at path " + projectPath + " since " + dateModification, e);
			throw new BugTrackerServiceException(
					"unable to get modified issues at path " + projectPath + " since " + dateModification, e);
		}
	}

	
	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.BugTrackerService#getNewIssues(java.lang.String, java.util.Date)
	 */
	@Override
	public IssueDto[] getNewIssues(String projectPath, Date dateCreation)
			throws BugTrackerServiceException {
		
		logger.info("getNewIssues(...) called");
		logger.debug("params : path=" + projectPath);
		logger.debug("params : dateCreation=" + dateCreation);
		if (projectPath == null) {
			throw new BugTrackerServiceException("getNewIssues: arg path cannot be null");
		}

		try {
			IssueDto[] bugs = new IssueDto[0];
			
			// Checking if the connected user has the permission to getAllIssues
			// the resource giving pep :
			String caller = membership.getProfilePathForConnectedIdentifier();
			String[] subjects = membership.getConnectedIdentifierSubjects();
			pep.checkSecurity(subjects, projectPath, "getNewIssues");

			
			//Search the project
			// Performing a lookup in the naming to recover the Resource
			// Identifier
			FactoryResourceIdentifier identifier = binding.lookup(projectPath);
			// STARTING SPECIFIC EXTERNAL SERVICE RESOURCE LOADING OR METHOD
			// CALLS
			List<IssueExternal> issueExternals = getBugTrackerManager().getIssues(identifier, projectPath, dateCreation, null);
			//END OF EXTERNAL INVOCATION
			
			
			
			bugs = constructIssueDtoArray(subjects, projectPath, issueExternals);
			
			// Using the notification service to throw an event :
			notification.throwEvent(new Event(projectPath, caller, Issue.RESOURCE_NAME, Event.buildEventType(BugTrackerService.SERVICE_NAME,
					Issue.RESOURCE_NAME, "read"), ""));
			return bugs;
		} 
		catch (FactoryException fe) {
			throw new BugTrackerServiceException(fe);
		}
		catch (Exception e) {
			logger.error("unable to get new issues at path " + projectPath + " since " + dateCreation, e);
			throw new BugTrackerServiceException(
					"unable to get new issues at path " + projectPath + " since " + dateCreation, e);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.BugTrackerService#getIssue(java.lang.String, java.lang.String)
	 */
	@Override
	public IssueDto getIssue(String projectPath, String numIssue)
			throws BugTrackerServiceException {
		logger.info("getIssue(...) called");
		logger.debug("params : projectPath=" + projectPath);
		logger.debug("params : numIssue=" + numIssue);

		if (StringUtils.isEmpty(projectPath)) {
			throw new BugTrackerServiceException("getIssue: arg projectPath cannot be null");
		}
		if (StringUtils.isEmpty(numIssue)) {
			throw new BugTrackerServiceException("getIssue: arg numIssue cannot be null");
		}
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			
			//Path of the issue
			final String path = Utils.generatePathIssueFactory(projectPath, numIssue);
			final Issue issueInternal = getIssueInternal(path, membership.getConnectedIdentifierSubjects());
			
			if (issueInternal == null) {
				throw new BugTrackerServiceException(
						"Issue doesn't exist in factory at path " + path);
			}
			
			IssueDto bug = null;
			
			// STARTING SPECIFIC EXTERNAL SERVICE RESOURCE LOADING OR METHOD
			// CALLS
			IssueExternal issueExternal = getBugTrackerManager().getIssue(path);
			
			if (issueExternal == null) {
				throw new BugTrackerServiceException(
						"Issue doesn't exist at path " + path);
			}
			//END OF EXTERNAL INVOCATION
			
			bug = convert(issueExternal, issueInternal, PathHelper.getParentPath(path));
			
			
			// Using the notification service to throw an event :
			notification.throwEvent(new Event(path, caller, Issue.RESOURCE_NAME, Event.buildEventType(BugTrackerService.SERVICE_NAME,
					Issue.RESOURCE_NAME, "read"), ""));
			
			return bug;
		} 
		catch (FactoryException fe) {
			throw new BugTrackerServiceException(fe);
		}
		catch (Exception e) {
		logger.error("unable to get issue [" + projectPath + ", " + numIssue + "]", e);
		throw new BugTrackerServiceException(
				"unable to get issue [" + projectPath + ", " + numIssue + "]", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.BugTrackerService#createIssue(java.lang.String, org.qualipso.factory.bugtracker.dto.IssueDto)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public String createIssue(String projectPath, IssueDto issueDto)
			throws BugTrackerServiceException {
		logger.info("createIssue(...) called");
		if (logger.isDebugEnabled()) {
			if (issueDto != null) {
				logger.debug("params : projectPath=" + projectPath + ", issueDto=[" + issueDto.toString() + "]");
			}
			else {
				logger.debug("params : projectPath=" + projectPath + ", issueDto=[null]");
			}
		}
		
		
		if (StringUtils.isEmpty(projectPath)) {
			throw new BugTrackerServiceException("createIssue: arg path cannot be null");
		}
		
		checkFieldMandatoryCreate(issueDto, "createIssue");

		try {
			//Checking if the connected user has the permission to create a resource giving pep : 
			//  - the profile path of the connected user (caller)
			//  - the parent of the path (we check the 'create' permission on the parent of the given path)
			//  - the name of the permission to check ('create')
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(membership.getConnectedIdentifierSubjects(), projectPath, "create");
			
			
			//Search the project
			FactoryResourceIdentifier identifier = binding.lookup(projectPath);
			
			
			//STARTING SPECIFIC EXTERNAL SERVICE RESOURCE CREATION OR METHOD CALL
			String idIssue = getBugTrackerManager().createIssue(issueDto, identifier, projectPath);
			//END OF EXTERNAL INVOCATION
			
			// persist issue
			Issue issue = new Issue(idIssue,
					caller,
					issueDto.getAssignee()); //FIXME user assigned
			
			em.persist(issue);
			
			
			//Binding the external resource in the naming using the generated resource ID : 
			final String pathIssue  = Utils.generatePathIssueFactory(projectPath, idIssue);
			
			binding.bind(issue.getFactoryResourceIdentifier(), pathIssue);
			
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
			
			
//			//Create forum
//			forumService.createForum(pathIssue, FORUM_NAME);
//			
//			//Create document folder
//			documentService.createFolder(pathIssue, DOCUMENT_FOLDER_NAME, "document folder for the issue");
//			
			
			//Using the notification service to throw an event : 
			notification.throwEvent(new Event(pathIssue, caller, Issue.RESOURCE_NAME, Event.buildEventType(BugTrackerService.SERVICE_NAME,
					Issue.RESOURCE_NAME, "create"), ""));
			
			return idIssue;
		} 
		catch (FactoryException fe) {
			ctx.setRollbackOnly();
			throw new BugTrackerServiceException(fe);
		}
		catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to create the project at path " + projectPath, e);
			throw new BugTrackerServiceException("unable to create the name at path " + projectPath, e);
		}
	}
	

	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.BugTrackerService#updateIssue(org.qualipso.factory.bugtracker.dto.IssueDto)
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void updateIssue(IssueDto issueDto)
			throws BugTrackerServiceException {
		logger.info("updateIssue(...) called");	
		checkFieldMandatoryUpdate(issueDto, "updateIssue");
		
		final String path = issueDto.getIssuePath();
		try {
			//Checking if the connected user has the permission to create a resource giving pep : 
			//  - the profile path of the connected user (caller)
			//  - the parent of the path (we check the 'create' permission on the parent of the given path)
			//  - the name of the permission to check ('create')
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(membership.getConnectedIdentifierSubjects(), path, "update");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceTypeIssue(identifier);
			
			// internal issue
			Issue issue = em.find(Issue.class, issueDto.getNum());
			if (issue == null) {
				throw new BugTrackerServiceException("unable to find an issue for id " + issueDto.getNum());
			}
			issue.setAssigned(issueDto.getAssignee());
			
			//STARTING SPECIFIC EXTERNAL SERVICE RESOURCE CREATION OR METHOD CALL
			getBugTrackerManager().updateIssue(issueDto, path);
			//END OF EXTERNAL INVOCATION
			
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			
			//Using the notification service to throw an event : 
			notification.throwEvent(new Event(path, caller, Issue.RESOURCE_NAME, Event.buildEventType(BugTrackerService.SERVICE_NAME,
					Issue.RESOURCE_NAME, "update"), ""));
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
	public void deleteIssue(String projectPath, String numIssue)
			throws BugTrackerServiceException {
		logger.info("deleteIssue(...) called");
		logger.debug("params : projectPath=" + projectPath);
		logger.debug("params : numIssue=" + numIssue);
		if (StringUtils.isEmpty(projectPath)) {
			throw new BugTrackerServiceException("deleteIssue: arg projectPath cannot be null");
		}
		if (StringUtils.isEmpty(numIssue)) {
			throw new BugTrackerServiceException("deleteIssue: arg numIssue cannot be null");
		}
		try {
			final String path = Utils.generatePathIssueFactory(projectPath, numIssue);
			//Checking if the connected user has the permission to create a resource giving pep : 
			//  - the profile path of the connected user (caller)
			//  - the parent of the path (we check the 'create' permission on the parent of the given path)
			//  - the name of the permission to check ('create')
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(membership.getConnectedIdentifierSubjects(), path, "delete");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceTypeIssue(identifier);
			
			
			//STARTING SPECIFIC EXTERNAL SERVICE RESOURCE CREATION OR METHOD CALL
			getBugTrackerManager().deleteIssue(path);
			//END OF EXTERNAL INVOCATION
			
			//delete internal issue
			Issue issue = em.find(Issue.class, numIssue);
			if (issue != null) {
				em.remove(issue);
			}

			
			String policyId = binding.getProperty(path, FactoryResourceProperty.POLICY_ID, false);
			pap.deletePolicy(policyId);
			
			binding.unbind(path);
			
			//Using the notification service to throw an event : 
			notification.throwEvent(new Event(path, caller, Issue.RESOURCE_NAME, Event.buildEventType(BugTrackerService.SERVICE_NAME,
					Issue.RESOURCE_NAME, "delete"), ""));
		} 
		catch (FactoryException fe) {
			ctx.setRollbackOnly();
			throw new BugTrackerServiceException(fe);
		}
		catch ( Exception e ) {
			ctx.setRollbackOnly();
			logger.error("unable to delete issue [" + projectPath + ", " + numIssue + "]", e);
			throw new BugTrackerServiceException("unable to delete issue [" + projectPath + ", " + numIssue + "]", e);
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
	private void checkFieldMandatoryCreate(IssueDto issueDto, String method) throws BugTrackerServiceException {
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
	
	/**
	 * Check if mandatory fields are missing
	 * Mandatory fields are summary and description
	 * @param issueDto
	 * @throws BugTrackerServiceException if mandatory field is missing
	 */
	private void checkFieldMandatoryUpdate(IssueDto issueDto, String method) throws BugTrackerServiceException {
		checkFieldMandatoryCreate(issueDto, method);
		if (StringUtils.isEmpty(issueDto.getSummary())) {
			logger.error(method + ": Mandatory field 'issueDto.summary' is missing");
			throw new BugTrackerServiceException("Mandatory field 'summary' is missing");
		}
		if (StringUtils.isEmpty(issueDto.getDescription())) {
			logger.error(method + ": Mandatory field 'issueDto.description' is missing");
			throw new BugTrackerServiceException("Mandatory field 'description' is missing");
		}
	}


	/**
	 * Return an issue
	 * @param path of the issue
	 * @param subjects for checkSecurity
	 * @return an Issue (Factory)
	 * @throws PathNotFoundException 
	 * @throws InvalidPathException 
	 * @throws BindingServiceException 
	 * @throws MembershipServiceException 
	 * @throws Exception if an error occurred
	 */
	private Issue getIssueInternal(String path, String[] subjects) throws FactoryException {
		if (StringUtils.isEmpty(path)) {
			throw new BugTrackerServiceException("getIssue: arg path cannot be null");
		}
		Issue issueInternal = null;
		//Search the issue
		// Performing a lookup in the naming to recover the Resource
		// Identifier
		FactoryResourceIdentifier identifier = binding.lookup(path);
		checkResourceTypeIssue(identifier);
		
		// Checking if the connected user has the permission to getIssue
		// the resource giving pep :
		pep.checkSecurity(subjects, path, "read");
		
		String id = Utils.getIdIssue(path);
		//STARTING INTERNAL INVOCATION
		issueInternal = em.find(Issue.class, id);
		issueInternal.setResourcePath(path);
		//END OF INTERNAL INVOCATION
			
		return issueInternal;
	}
	
	/**
	 * Construct an array of issueDto
	 * @param subjects for checkSecurity
	 * @param projectPath of the IssueExternal[]
	 * @param issueExternals of the projectPath
	 * @return IssueDto[]
	 * @throws Exception if an error occurred
	 */
	private IssueDto[] constructIssueDtoArray(String[] subjects, String projectPath, List<IssueExternal> issueExternals) {
		List<IssueDto> bugsList = new ArrayList<IssueDto>();
		if (issueExternals != null && !issueExternals.isEmpty()) {
			for (IssueExternal issueExternal : issueExternals) {
				//STARTING INTERNAL INVOCATION
				try {
					final String issuePath = Utils.generatePathIssueFactory(projectPath, issueExternal.getId());
					final Issue issueInternal = getIssueInternal(issuePath, subjects);
					if (issueInternal != null) {
						final IssueDto dto = convert(issueExternal, issueInternal, projectPath);
						bugsList.add(dto);
					}
				}
				catch (FactoryException e){
					logger.warn("Error on issue " + issueExternal.getId(), e);
				}
				//END OF INTERNAL INVOCATION
				
				
			}
		}
		final IssueDto[] bugs = bugsList.toArray(new IssueDto[bugsList.size()]);
		return bugs;
	}
	
	/* (non-Javadoc)
	 * @see org.qualipso.factory.FactoryService#findResource(java.lang.String)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public FactoryResource findResource(String path) throws FactoryException {
		logger.info("findResource(...) called");
		logger.debug("params : path=" + path);
		try {
			String[] subjects = membership.getConnectedIdentifierSubjects();
			final Issue issueInternal = getIssueInternal(path, subjects);
			if (issueInternal == null) {
				throw new BugTrackerServiceException(
						"Issue doesn't exist in factory with id " + path);
			}
			return issueInternal;

		} catch (Exception e) {
			throw new CoreServiceException("unable to find the resource at path " + path, e);
		}
	}
	
	/**
	 * Convert to IssueDto
	 * @param issueExternal IssueExternal
	 * @param issueInternal Issue
	 * @param projectPath of the issue
	 * @return
	 * @throws FactoryException
	 */
	private IssueDto convert(IssueExternal issueExternal, Issue issueInternal, String projectPath) throws FactoryException {
		IssueComplexe issueComplexe = new IssueComplexe();
		issueComplexe.setIssue(issueExternal);
		issueComplexe.setProjectPath(projectPath);
		//Reporter
		if (!StringUtils.isEmpty(issueInternal.getReporter())) {
			FactoryResourceIdentifier identifierReporter = binding.lookup(issueInternal.getReporter());
			if (identifierReporter == null || !Profile.RESOURCE_NAME.equals(identifierReporter.getType())) {
				throw new BugTrackerServiceException("Reporter " + issueInternal.getReporter() + " is not a path for a profile");
			}
			issueComplexe.setReporterFullName(issueInternal.getReporter());
			/* TODO use membership service
			 Profile reporter = membership.readProfile(issueInternal.getReporter());
			if (reporter != null) {
				issueComplexe.setReporterFullName(reporter.getFullname());
			}*/
			
		}
		//Assigned
		if (!StringUtils.isEmpty(issueInternal.getAssigned())) {
			FactoryResourceIdentifier identifierAssigned = binding.lookup(issueInternal.getAssigned());
			if (identifierAssigned == null || !Profile.RESOURCE_NAME.equals(identifierAssigned.getType())) {
				throw new BugTrackerServiceException("Assigned " + issueInternal.getAssigned() + " is not a path for a profile");
			}
			issueComplexe.setAssignedFullName(issueInternal.getAssigned());
			/* TODO use membership service
			Profile assigned = membership.readProfile(issueInternal.getAssigned());
			
			if (assigned != null) {
				issueComplexe.setAssignedFullName(assigned.getFullname());
			}
			*/
		}
		issueComplexe.setIssuePath(issueInternal.getResourcePath());
		
		return IssueDtoBuilder.create(issueComplexe);
	}
}
