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


/**
 * Issue
 */
public class IssueComplexe {

	/**
	 * Issue
	 */
	private IssueExternal issue;
	
	/**
	 * Project path of the issue
	 */
	private String projectPath;
	
	/**
	 * Issue path 
	 */
	private String issuePath;

	/**
	 * Fullname of the reporter
	 */
	private String reporterFullName;
	
	
	/**
	 * Fullname of the assigned user
	 */
	private String assignedFullName;
	
	
	/**
	 * @return the issue
	 */
	public IssueExternal getIssue() {
		return issue;
	}

	/**
	 * @param issue the issue to set
	 */
	public void setIssue(IssueExternal issue) {
		this.issue = issue;
	}

	/**
	 * @return the reporterFullName
	 */
	public String getReporterFullName() {
		return reporterFullName;
	}

	/**
	 * @param reporterFullName the reporterFullName to set
	 */
	public void setReporterFullName(String reporterFullName) {
		this.reporterFullName = reporterFullName;
	}

	/**
	 * @return the assignedFullName
	 */
	public String getAssignedFullName() {
		return assignedFullName;
	}

	/**
	 * @param assignedFullName the assignedFullName to set
	 */
	public void setAssignedFullName(String assignedFullName) {
		this.assignedFullName = assignedFullName;
	}

	/**
	 * @return the projectPath
	 */
	public String getProjectPath() {
		return projectPath;
	}

	/**
	 * @param projectPath the projectPath to set
	 */
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	/**
	 * @return the issuePath
	 */
	public String getIssuePath() {
		return issuePath;
	}

	/**
	 * @param issuePath the issuePath to set
	 */
	public void setIssuePath(String issuePath) {
		this.issuePath = issuePath;
	}
	
	
}
