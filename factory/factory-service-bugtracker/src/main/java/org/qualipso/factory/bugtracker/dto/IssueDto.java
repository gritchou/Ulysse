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
package org.qualipso.factory.bugtracker.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Issue classes
 */
public class IssueDto implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7930669178655297625L;

	/**
	 * projectPath
	 */
	private String projectPath;
	
	/**
	 * Number of the issue
	 */
	private String num;

	/**
	 * issuePath
	 */
	private String issuePath;
	
	/**
	 * Issue summary
	 */
	private String summary;
	
	/**
	 * Issue description
	 */
	private String description;

	/**
	 * Issue Status
	 */
	private ConfDataDto status;

	/**
	 * Reporter of the Issue
	 */
	private String reporter;

	/**
	 * Person
	 */
	private String assignee;

	/**
	 * Issue priority
	 */
	private ConfDataDto priority;

	/**
	 * Issue severity
	 */
	private ConfDataDto severity;

	/**
	 * Issue resolution
	 */
	private ConfDataDto resolution;
	

	/**
	 * last date of issue update
	 */
	private long dateLastUpdate;

	/**
	 * Creation date of the issue
	 */
	private Date dateCreation;
	
	/**
	 * date of the last modification
	 */
	private Date dateModification;
	
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
	 * @return the num
	 */
	public String getNum() {
		return num;
	}

	/**
	 * @param num the num to set
	 */
	public void setNum(String num) {
		this.num = num;
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

	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary
	 *            the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @return the status
	 */
	public ConfDataDto getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(ConfDataDto status) {
		this.status = status;
	}

	/**
	 * @return the reporter
	 */
	public String getReporter() {
		return reporter;
	}

	/**
	 * @param reporter
	 *            the reporter to set
	 */
	public void setReporter(String reporter) {
		this.reporter = reporter;
	}

	/**
	 * @return the assignee
	 */
	public String getAssignee() {
		return assignee;
	}

	/**
	 * @param assignee
	 *            the assignee to set
	 */
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	/**
	 * @return the priority
	 */
	public ConfDataDto getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(ConfDataDto priority) {
		this.priority = priority;
	}

	/**
	 * @return the severity
	 */
	public ConfDataDto getSeverity() {
		return severity;
	}

	/**
	 * @param severity
	 *            the severity to set
	 */
	public void setSeverity(ConfDataDto severity) {
		this.severity = severity;
	}

	/**
	 * @return the resolution
	 */
	public ConfDataDto getResolution() {
		return resolution;
	}

	/**
	 * @param resolution
	 *            the resolution to set
	 */
	public void setResolution(ConfDataDto resolution) {
		this.resolution = resolution;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	

	/**
	 * @return the dateLastUpdate
	 */
	public long getDateLastUpdate() {
		return dateLastUpdate;
	}

	/**
	 * @param dateLastUpdate the dateLastUpdate to set
	 */
	public void setDateLastUpdate(long dateLastUpdate) {
		this.dateLastUpdate = dateLastUpdate;
	}
	
	

	/**
	 * @return the dateCreation
	 */
	public Date getDateCreation() {
		return dateCreation;
	}

	/**
	 * @param dateCreation the dateCreation to set
	 */
	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	/**
	 * @return the dateModification
	 */
	public Date getDateModification() {
		return dateModification;
	}

	/**
	 * @param dateLastModification the dateLastModification to set
	 */
	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("projectPath=");
		sb.append(this.projectPath);
		sb.append("num=");
		sb.append(this.num);
		sb.append("; summary=");
		sb.append(this.summary);
		sb.append("; description=");
		sb.append(this.description);
		sb.append("; status=");
		sb.append(this.status);
		sb.append("; assignee=");
		sb.append(this.assignee);
		sb.append("; priority=");
		sb.append(this.priority);
		sb.append("; reporter=");
		sb.append(this.reporter);
		sb.append("; resolution=");
		sb.append(this.resolution);
		sb.append("; severity=");
		sb.append(this.severity);
		sb.append("; dateLastUpdate=");
		sb.append(new Date(this.dateLastUpdate));
		sb.append("; dateCreation=");
		sb.append(dateCreation);
		sb.append("; dateModification=");
		sb.append(dateModification);
		
		return sb.toString();
	}
	
}
