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

import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.bugtracker.dto.IssueAttributesDto;
import org.qualipso.factory.bugtracker.dto.IssueDto;
import org.qualipso.factory.bugtracker.dto.ProjectDto;
import org.qualipso.factory.bugtracker.exception.BugTrackerServiceException;

/**
 * 
 * BugTrackerManager interface
 *
 */
public interface IBugTrackerManager {

	/**
	 * Return all issues for a project
	 * @param idProjectFactory id of the project in the factory
	 * @param projectPath path of the project
	 * @return all issues for a project
	 * @throws BugTrackerServiceException if an error occurred
	 */
	IssueDto[] getAllIssues(FactoryResourceIdentifier identifier, String projectPath) throws BugTrackerServiceException;
	
	/**
	 * Return an issue
	 * @param pathIssue of issue
	 * @return issue
	 * @throws BugTrackerServiceException if an error occurred
	 */
	IssueDto getIssue(String issuePath) throws BugTrackerServiceException;
	
	/**
	 * Create a project
	 * @param project to create
	 * @return id of the created project
	 * @throws BugTrackerServiceException if an error occurred
	 */
	long createProject(ProjectDto project) throws BugTrackerServiceException;
	
	/**
	 * find a project by name
	 * @param name of the project
	 * @return ProjectDto
	 * @throws BugTrackerServiceException if an error occurred
	 */
	ProjectDto findProjectByName(String name) throws BugTrackerServiceException;
	
	/**
	 * Create a bug
	 * @param issue to create
	 * @return id of the created issue
	 * @throws BugTrackerServiceException if an error occurred
	 */
	long createIssue(IssueDto issue, long projectId) throws BugTrackerServiceException;
	
	/**
	 * Update an issue
	 * @param issue to update
	 * @param issuePath to update
	 * @throws BugTrackerServiceException if an error occurred
	 */
	void updateIssue(IssueDto issue, String issuePath) throws BugTrackerServiceException;
	
	/**
	 * delete an issue
	 * @param issuePath to delete
	 * @return true if issue is deleted
	 * @throws BugTrackerServiceException if an error occurred
	 */
	boolean deleteIssue(String issuePath) throws BugTrackerServiceException;
	
	/**
	 * @return the issueAttributesDto
	 */
	IssueAttributesDto getIssueAttributesDto();
}
