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

import java.util.Date;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.bugtracker.dto.IssueAttributesDto;
import org.qualipso.factory.bugtracker.dto.IssueDto;
import org.qualipso.factory.bugtracker.entity.Issue;
import org.qualipso.factory.bugtracker.exception.BugTrackerServiceException;


@Remote
@WebService(name = BugTrackerService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + BugTrackerService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface BugTrackerService extends FactoryService {
	
	/**
	 * Service name
	 */
	public static final String SERVICE_NAME = "bugtracker";
	
	/**
	 * Resource type list
	 */
	public static final String[] RESOURCE_TYPE_LIST = new String[] { Issue.RESOURCE_NAME };
	
	/**
	 * Return all issues of a project
	 * @param projectPath path of the project
	 * @return all issues of the project
	 * @throws BugTrackerServiceException if an exception occurred
	 */
	@WebMethod
	@WebResult(name = "issue")
	public IssueDto[] getAllIssues(String projectPath) throws BugTrackerServiceException;
	
	/**
	 * Return an issue
	 * @param projectPath of the issue
	 * @param numIssue number of the issue
	 * @return an issue
	 * @throws BugTrackerServiceException if an exception occurred
	 */
	@WebMethod
	@WebResult(name = "issue")
	public IssueDto getIssue(String projectPath, String numIssue) throws BugTrackerServiceException;
	
	/**
	 * Create an issue in the project
	 * @param projectPath path of the project
	 * @param issueDto issue
	 * @return number of the issue
	 * @throws BugTrackerServiceException if an exception occurred
	 */
	@WebMethod
	@WebResult(name = "path")
	public String createIssue(String projectPath, IssueDto issueDto) throws BugTrackerServiceException;
	
	/**
	 * Update an issue
	 * @param issueDto issue to update
	 * @throws BugTrackerServiceException if an exception occurred
	 */
	@WebMethod
	public void updateIssue(IssueDto issueDto) throws BugTrackerServiceException;
	
	/**
	 * Delete an issue
	 * @param projectPath of the project
	 * @param num of the issue
	 * @throws BugTrackerServiceException if an exception occurred
	 */
	@WebMethod
	public void deleteIssue(String projectPath, String numIssue) throws BugTrackerServiceException;
	
	/**
	 * Return all attributes for issue
	 * @return attributes
	 * @throws BugTrackerServiceException if an exception occurred
	 */
	@WebMethod
	@WebResult(name = "issueAttributes")
	public IssueAttributesDto getIssueAttributes() throws BugTrackerServiceException;
	
	/**
	 * Return all new issues of a project since a date
	 * @param projectPath path of the project
	 * @param dateCreation date of creation
	 * @return all new issues of the project since a creation date
	 * @throws BugTrackerServiceException if an exception occurred
	 */
	@WebMethod
	@WebResult(name = "issue")
	public IssueDto[] getNewIssues(String projectPath, Date dateCreation) throws BugTrackerServiceException;
	
	/**
	 * Return all modified issues of a project since a date
	 * @param projectPath path of the project
	 * @param dateCreation date of creation
	 * @return all new issues of the project since a creation date
	 * @throws BugTrackerServiceException if an exception occurred
	 */
	@WebMethod
	@WebResult(name = "issue")
	public IssueDto[] getModifiedIssues(String projectPath, Date dateModification) throws BugTrackerServiceException;
}
