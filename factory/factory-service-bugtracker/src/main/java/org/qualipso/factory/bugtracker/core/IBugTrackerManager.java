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

import java.util.Date;
import java.util.List;

import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.bugtracker.dto.IssueAttributesDto;
import org.qualipso.factory.bugtracker.dto.IssueDto;
import org.qualipso.factory.bugtracker.exception.BugTrackerServiceException;

/**
 * 
 * BugTrackerManager interface
 *
 */
public interface IBugTrackerManager {

	/**
	 * Return issues for a project
	 * @param identifier of the project in the factory
	 * @param projectPath path of the project
	 * @param dateCreationMin of issues
	 * @param dateModificationMin of issues
	 * @return all issues for a project
	 * @throws BugTrackerServiceException if an error occurred
	 */
	List<IssueExternal> getIssues(
			FactoryResourceIdentifier identifier, 
			String projectPath, 
			Date dateCreationMin, 
			Date dateModificationMin) throws BugTrackerServiceException;
	
	/**
	 * Return an issue
	 * @param pathIssue of issue
	 * @return issue
	 * @throws BugTrackerServiceException if an error occurred
	 */
	IssueExternal getIssue(String issuePath) throws BugTrackerServiceException;
	
	/**
	 * Create a bug
	 * @param issue to create
	 * @param identifier of the project
	 * @param projectPath where issue is created
	 * @return id of the created issue
	 * @throws BugTrackerServiceException if an error occurred
	 */
	String createIssue(IssueDto issue, FactoryResourceIdentifier identifier, String projectPath) throws BugTrackerServiceException;
	
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
