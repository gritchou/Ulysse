package org.qualipso.factory.bugtracker.core;

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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mantisbt.connect.Enumeration;
import org.mantisbt.connect.IMCSession;
import org.mantisbt.connect.MCException;
import org.mantisbt.connect.axis.MCSession;
import org.mantisbt.connect.model.IIssue;
import org.mantisbt.connect.model.IMCAttribute;
import org.mantisbt.connect.model.IProject;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.bugtracker.dto.ConfDataDto;
import org.qualipso.factory.bugtracker.dto.ConfDataDtoBuilder;
import org.qualipso.factory.bugtracker.dto.IssueAttributesDto;
import org.qualipso.factory.bugtracker.dto.IssueAttributesDtoBuilder;
import org.qualipso.factory.bugtracker.dto.IssueDto;
import org.qualipso.factory.bugtracker.dto.IssueDtoBuilder;
import org.qualipso.factory.bugtracker.dto.ProjectDto;
import org.qualipso.factory.bugtracker.dto.ProjectDtoBuilder;
import org.qualipso.factory.bugtracker.exception.BugTrackerServiceException;
import org.qualipso.factory.bugtracker.utils.Utils;

/**
 * BugTrackerManager
 */
public class BugTrackerManager implements IBugTrackerManager{

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(BugTrackerManager.class);
	
	/**
	 * Instance (Singleton)
	 */
	private static BugTrackerManager instance = null;
	
	
	/**
	 * Priorities
	 */
	private Map<String, IMCAttribute> priorities = null;
	
	

	/**
	 * Resolutions
	 */
	private Map<String, IMCAttribute> resolutions = null;
	
	
	/**
	 * Severities
	 */
	private Map<String, IMCAttribute> severities = null;
	
	
	/**
	 * Status
	 */
	private Map<String, IMCAttribute> status = null;
	
	
	/**
	 * Issue attributes Dto
	 */
	private IssueAttributesDto issueAttributesDto = null;
	
	
	/**
	 * private constructor
	 * initEnum
	 * @throws BugTrackerServiceException if an error occurred
	 */
	protected BugTrackerManager() throws BugTrackerServiceException {
		initEnum();
	}
	
	/**
	 * Construct object
	 * @return BugTrackerManager
	 * @throws BugTrackerServiceException if an exception occurred
	 */
	public static BugTrackerManager getInstance() throws BugTrackerServiceException {
		if (instance == null) {
			instance = new BugTrackerManager();
		}
		return instance;
	}
	
	
	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.core.IBugTrackerManager#getAllIssues(org.qualipso.factory.FactoryResourceIdentifier, java.lang.String)
	 */
	@Override
	public IssueDto[] getAllIssues(FactoryResourceIdentifier identifier, String projectPath) throws BugTrackerServiceException {
		try {
			long idProjectBugTracker = 0;
			IssueDto[] dtos = new IssueDto[0];
			
			ProjectDto project = findProjectByName(identifier.getId());
			if (project != null) {
				idProjectBugTracker = project.getId();
				IMCSession session = createWSSession();
				IIssue[] iIssues = session.getProjectIssues(idProjectBugTracker);
				dtos = IssueDtoBuilder.create(iIssues, projectPath);
			}
			
			return dtos;
		} 
		catch (MCException e) {
			logger.error("unable to getAllIssues " + projectPath, e);
			throw new BugTrackerServiceException(
					"unable to getAllIssues " + projectPath, e);
		} 
		catch (InvalidPathException e) {
			logger.error("unable to getAllIssues " + projectPath, e);
			throw new BugTrackerServiceException(
					"unable to getAllIssues " + projectPath, e);
		}
		
	}
	
	
	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.core.IBugTrackerManager#getIssue(java.lang.String)
	 */
	@Override
	public IssueDto getIssue(String issuePath) throws BugTrackerServiceException {
		try {
			IMCSession session = createWSSession();
			IIssue iIssue = session.getIssue(Utils.getIdBugTracker(issuePath));

			
			IssueDto dto = IssueDtoBuilder.create(iIssue, PathHelper.getParentPath(issuePath));
			return dto;
		} 
		catch (MCException e) {
			logger.error("unable to getIssue " + issuePath, e);
			throw new BugTrackerServiceException(
					"unable to getIssue " + issuePath, e);
		} 
		catch (InvalidPathException e) {
			logger.error("unable to getIssue " + issuePath, e);
			throw new BugTrackerServiceException(
					"unable to getIssue " + issuePath, e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.core.IBugTrackerManager#createProject(org.qualipso.factory.bugtracker.dto.ProjectDto)
	 */
	@Override
	public long createProject(ProjectDto project) throws BugTrackerServiceException {
		try {
			IMCSession session = createWSSession();
			
			IProject iProject = ProjectDtoBuilder.create(project);
			long projectId = session.addProject(iProject);
			return projectId;
		} 
		catch (MCException e) {
			logger.error("unable to create the project " + project.toString(), e);
			throw new BugTrackerServiceException(
					"unable to create the project " + project.toString(), e);
		} 
	}
	
	
	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.core.IBugTrackerManager#findProjectByName(java.lang.String)
	 */
	@Override
	public ProjectDto findProjectByName(String name) throws BugTrackerServiceException {
		try {
			IMCSession session = createWSSession();
			IProject iproject = session.getProject(name);

			return ProjectDtoBuilder.create(iproject);
		} 
		catch (MCException e) {
			logger.error("unable to find project to the name " + name, e);
			throw new BugTrackerServiceException(
					"unable to find project to the name " + name, e);
		} 
	}
	
	
	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.core.IBugTrackerManager#createIssue(org.qualipso.factory.bugtracker.dto.IssueDto, long)
	 */
	@Override
	public long createIssue(IssueDto issue, long projectId) throws BugTrackerServiceException {
		try {
			IMCSession session = createWSSession();
			
			IIssue iIssue = IssueDtoBuilder.create(issue, 
					projectId,
					priorities, 
					resolutions,
					severities,
					status
					);
			long issueId = session.addIssue(iIssue);
			return issueId;
		} 
		catch (MCException e) {
			logger.error("unable to create the issue " + issue.toString(), e);
			throw new BugTrackerServiceException(
					"unable to create the issue " + issue.toString(), e);
		} 
	}
	
	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.core.IBugTrackerManager#updateIssue(org.qualipso.factory.bugtracker.dto.IssueDto, java.lang.String)
	 */
	@Override
	public void updateIssue(IssueDto issue, String issuePath) throws BugTrackerServiceException {
		try {
			IMCSession session = createWSSession();
			
			//Search issue
			IIssue iIssue = session.getIssue(Utils.getIdBugTracker(issuePath));
			
			//Check the last date update
			if (iIssue.getDateLastUpdated() != null
					&& iIssue.getDateLastUpdated().getTime() != issue.getDateLastUpdate()) {
				logger.error("unable to update the issue. " +
						"Issue is not up to date [" + iIssue.getDateLastUpdated().getTime() + " != " + issue.getDateLastUpdate() + "]");
				throw new BugTrackerServiceException(
						"unable to update the issue. Issue is not up to date");
			}
			
			IssueDtoBuilder.update(issue, 
					iIssue, 
					priorities, 
					resolutions, 
					severities, 
					status);
			session.updateIssue(iIssue);
		} 
		catch (MCException e) {
			logger.error("unable to update the issue " + issue.toString(), e);
			throw new BugTrackerServiceException(
					"unable to update the issue " + issue.toString(), e);
		}
		catch (InvalidPathException e) {
			logger.error("unable to update the issue " + issue.toString(), e);
			throw new BugTrackerServiceException(
					"unable to update the issue " + issue.toString(), e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.core.IBugTrackerManager#deleteIssue(java.lang.String)
	 */
	@Override
	public boolean deleteIssue(String issuePath) throws BugTrackerServiceException {
		try {
			IMCSession session = createWSSession();
			
			//Search issue
			boolean deleted = session.deleteIssue(Utils.getIdBugTracker(issuePath));
			return deleted;
		}
		catch (MCException e) {
			logger.error("unable to delete the issue " + issuePath, e);
			throw new BugTrackerServiceException(
					"unable to update the issue " + issuePath, e);
		}
		catch (InvalidPathException e) {
			logger.error("unable to delete the issue " + issuePath, e);
			throw new BugTrackerServiceException(
					"unable to update the issue " + issuePath, e);
		}

	}
	
	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.core.IBugTrackerManager#getIssueAttributesDto()
	 */
	@Override
	public IssueAttributesDto getIssueAttributesDto() {
		return issueAttributesDto;
	}
	
	
	/**
	 * Create a Web Service session
	 * @return IMCSession
	 * @throws BugTrackerServiceException if an error occurred while accessing to WS
	 */
	protected IMCSession createWSSession() throws BugTrackerServiceException {
		BugTrackerResources btr = BugTrackerResources.getInstance();
		IMCSession session;
		try {
			session = new MCSession(new URL(btr.getWSEndPoint()), btr.getWSUserName(), btr.getWSUserPassword());
		} 
		catch (MalformedURLException e) {
			logger.error("URL mal formed: " + btr.getWSEndPoint(), e);
			throw new BugTrackerServiceException(
					"URL mal formed: " + btr.getWSEndPoint(), e);
		} 
		catch (MCException e) {
			logger.error("Mantis connect exception", e);
			throw new BugTrackerServiceException(
					"Mantis connect exception", e);
		}
		return session;
	}
	
	/**
	 * Init Enum
	 * @throws BugTrackerServiceException if an error occurred while accessing to WS
	 */
	private void initEnum() throws BugTrackerServiceException {
		logger.debug("initEnum");
		IMCSession session;
		try {
			session = createWSSession();
		
			IMCAttribute[] prioritiesTab = session.getEnum(Enumeration.PRIORITIES);
			this.priorities = createMap(prioritiesTab);
			logger.debug("priorities size=" + this.priorities.size());
			
			IMCAttribute[] resolutionsTab = session.getEnum(Enumeration.RESOLUTIONS);
			this.resolutions = createMap(resolutionsTab);
			logger.debug("resolutions size=" + this.resolutions.size());
			
			IMCAttribute[] severitiesTab = session.getEnum(Enumeration.SEVERITIES);
			this.severities = createMap(severitiesTab);
			logger.debug("severities size=" + this.severities.size());
			
			IMCAttribute[] statusTab = session.getEnum(Enumeration.STATUS);
			this.status = createMap(statusTab);
			logger.debug("status size=" + this.status.size());
		
			
			issueAttributesDto = IssueAttributesDtoBuilder.create(
					priorities, 
					resolutions, 
					severities, 
					status);
		} 
		catch (MCException e) {
			logger.error("Mantis connect exception", e);
			throw new BugTrackerServiceException(
					"Mantis connect exception", e);
		}
	}
	
	
	/**
	 * Create a map with a tab
	 * @param tab for convert
	 * @return map created
	 */
	private static Map<String, IMCAttribute> createMap(IMCAttribute[] tab) {
		Map<String, IMCAttribute> map = new HashMap<String, IMCAttribute>();
		
		if (tab != null) {
			for (IMCAttribute el : tab) {
				ConfDataDto dto = ConfDataDtoBuilder.create(el);
				map.put(dto.getId(), el);
			}
		}
		return map;
	}
}
