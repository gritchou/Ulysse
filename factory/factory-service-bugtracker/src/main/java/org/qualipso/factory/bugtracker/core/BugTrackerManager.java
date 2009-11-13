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
package org.qualipso.factory.bugtracker.core;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mantisbt.connect.AccessLevel;
import org.mantisbt.connect.Enumeration;
import org.mantisbt.connect.IMCSession;
import org.mantisbt.connect.MCException;
import org.mantisbt.connect.axis.MCSession;
import org.mantisbt.connect.model.IIssue;
import org.mantisbt.connect.model.IMCAttribute;
import org.mantisbt.connect.model.IProject;
import org.mantisbt.connect.model.Project;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.bugtracker.dto.ConfDataDto;
import org.qualipso.factory.bugtracker.dto.ConfDataDtoBuilder;
import org.qualipso.factory.bugtracker.dto.IssueAttributesDto;
import org.qualipso.factory.bugtracker.dto.IssueAttributesDtoBuilder;
import org.qualipso.factory.bugtracker.dto.IssueDto;
import org.qualipso.factory.bugtracker.dto.IssueDtoBuilder;
import org.qualipso.factory.bugtracker.exception.BugTrackerServiceException;
import org.qualipso.factory.bugtracker.utils.Utils;

/**
 * BugTrackerManager
 */
/**
 * @author T0090864
 *
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
	 * @see org.qualipso.factory.bugtracker.core.IBugTrackerManager#getIssues(org.qualipso.factory.FactoryResourceIdentifier, java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<IssueExternal> getIssues(FactoryResourceIdentifier identifier,
			String projectPath, Date dateCreationMin, Date dateModificationMin)
			throws BugTrackerServiceException {
		try {
			long idProjectBugTracker = findIdProjectByName(identifier.getId());
			List<IssueExternal> issuesToReturn = new ArrayList<IssueExternal>();
			if (idProjectBugTracker != 0) {
				IMCSession session = createWSSession();
				IIssue[] iAllIssues = session.getProjectIssues(idProjectBugTracker);
				
				List<IIssue> allIssues = new ArrayList<IIssue>();
				
				if (iAllIssues != null) {
					allIssues = Utils.copyToList(iAllIssues);
					logger.debug("getIssues: size allIssues=" + allIssues.size());
				}
				
				//Filter with creation date
				if (dateCreationMin != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("dateCreationMin=" + dateCreationMin);
					}
					List<IIssue> issueToRemove = new ArrayList<IIssue>();
					for (IIssue issue : allIssues) {
						if (logger.isDebugEnabled()) {
							logger.debug("issue, id=" + issue.getId() + ", dateCreation=" + issue.getDateSubmitted());
						}
						if (issue.getDateSubmitted().before(dateCreationMin)) {
							logger.debug("issue, id=" + issue.getId() + " excluded");
							issueToRemove.add(issue);
						}
					}
					allIssues.removeAll(issueToRemove);
					logger.debug("getIssues: size Issues after filtering creation date=" + allIssues.size());
				}
				
				//Filter with modification date
				if (dateModificationMin != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("dateModificationMin=" + dateModificationMin);
					}
					List<IIssue> issueToRemove = new ArrayList<IIssue>();
					for (IIssue issue : allIssues) {
						if (logger.isDebugEnabled()) {
							logger.debug("issue, id=" + issue.getId() + ", dateModification=" + issue.getDateLastUpdated());
						}
						if (issue.getDateLastUpdated().before(dateModificationMin)) {
							logger.debug("issue, id=" + issue.getId() + " excluded");
							issueToRemove.add(issue);
						}
					}
					allIssues.removeAll(issueToRemove);
					logger.debug("getIssues: size Issues after filtering modification date=" + allIssues.size());
				}
				
				for (IIssue issue : allIssues) {
					issuesToReturn.add(convert(issue));
				}
			}
			
			return issuesToReturn;
		} 
		catch (MCException e) {
			logger.error("unable to getIssues " + projectPath + ", dateCreationMin " + dateCreationMin + ", dateModificationMin " + dateModificationMin, e);
			throw new BugTrackerServiceException(
					"unable to getIssues " + projectPath + ", dateCreationMin " + dateCreationMin + ", dateModificationMin " + dateModificationMin, e);
		} 
	}
	
	
	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.core.IBugTrackerManager#getIssue(java.lang.String)
	 */
	@Override
	public IssueExternal getIssue(String issuePath) throws BugTrackerServiceException {
		try {
			IMCSession session = createWSSession();
			IIssue iIssue = session.getIssue(Utils.getIdBugTracker(issuePath));
			return convert(iIssue);
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
	
	/**
	 * Create an external project
	 * @param name of the project
	 * @param description of the project
	 * @return
	 * @throws BugTrackerServiceException
	 */
	private long createExternalProject(String name, String description) throws BugTrackerServiceException {
		try {
			IMCSession session = createWSSession();
			
			IProject iProject = create(name, description);
			long projectId = session.addProject(iProject);
			return projectId;
		} 
		catch (MCException e) {
			logger.error("unable to create the project [" + name + ", " + description + "]", e);
			throw new BugTrackerServiceException(
					"unable to create the project [" + name + ", " + description + "]", e);
		} 
	}
	
	/**
	 * Find id project by name
	 * @param name
	 * @return
	 * @throws BugTrackerServiceException
	 */
	public long findIdProjectByName(String name) throws BugTrackerServiceException {
		try {
			IMCSession session = createWSSession();
			IProject iproject = session.getProject(name);
			long idProject = 0;
			
			if (iproject != null) {
				idProject = iproject.getId();
			}
			return idProject;
		} 
		catch (MCException e) {
			logger.error("unable to find project to the name " + name, e);
			throw new BugTrackerServiceException(
					"unable to find project to the name " + name, e);
		} 
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.core.IBugTrackerManager#createIssue(org.qualipso.factory.bugtracker.dto.IssueDto, org.qualipso.factory.FactoryResourceIdentifier, java.lang.String)
	 */
	@Override
	public String createIssue(IssueDto issue, FactoryResourceIdentifier identifier, String projectPath) throws BugTrackerServiceException {
		try {
			//check for project
			long idProject = findIdProjectByName(identifier.getId());
			if (idProject == 0) {
				idProject = createExternalProject(identifier.getId(), projectPath);
			}

			
			IMCSession session = createWSSession();
			
			IIssue iIssue = IssueDtoBuilder.create(issue, 
					idProject,
					priorities, 
					resolutions,
					severities,
					status
					);
			long issueId = session.addIssue(iIssue);
			return String.valueOf(issueId);
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
						"Issue is not up to date [" + iIssue.getDateLastUpdated() + "] != [" + new Date(issue.getDateLastUpdate()) + "]");
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
	
	/**
	 * Convert an IIssue to IssueExternal
	 * @param iIssue
	 * @return
	 */
	private IssueExternal convert(IIssue iIssue) {
		IssueExternal issue = null;
		
		if (iIssue != null) {
			issue = new IssueExternal();
			issue.setId(String.valueOf(iIssue.getId()));
			issue.setSummary(iIssue.getSummary());
			issue.setDescription(iIssue.getDescription());
			issue.setDateLastUpdate(iIssue.getDateLastUpdated());
			issue.setDateCreation(iIssue.getDateSubmitted());
			issue.setDateLastUpdate(iIssue.getDateLastUpdated());
			if (iIssue.getPriority() != null) {
				issue.setPriority(convert(iIssue.getPriority()));
			}
			if (iIssue.getResolution() != null) {
				issue.setResolution(convert(iIssue.getResolution()));
			}
			if (iIssue.getSeverity() != null) {
				issue.setSeverity(convert(iIssue.getSeverity()));
			}
			if (iIssue.getStatus() != null) {
				issue.setStatus(convert(iIssue.getStatus()));
			}
		}
		
		return issue;
	}
	
	/**
	 * Create a IProject
	 * @param dto ProjectDto
	 * @return IProject
	 */
	public IProject create(String name, String description) {
		final IProject project = new Project();
		project.setAccessLevelMin(AccessLevel.ANYBODY);
		project.setDesription(description);
		project.setName(name);
		project.setEnabled(true);
		project.setPrivate(false);
		return project;
	}
	
	/**
	 * Create a ConfDataExternal with an IMCAttribute
	 * @param attribute IMCAttribute
	 * @return ConfDataExternal
	 */
	public ConfDataExternal convert(IMCAttribute attribute) {
		ConfDataExternal confDataExternal = null;
		
		if (attribute != null) {
			confDataExternal = new ConfDataExternal();
			confDataExternal.setId(String.valueOf(attribute.getId()));
			confDataExternal.setName(attribute.getName());
		}
		return confDataExternal;
	}
}
