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

import java.util.Date;

/**
 * Issue of the external service
 */
public class IssueExternal {

	/**
	 * Id of the issue
	 */
	private String id;
	
	
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
	private ConfDataExternal status;

	/**
	 * Issue priority
	 */
	private ConfDataExternal priority;

	/**
	 * Issue severity
	 */
	private ConfDataExternal severity;

	/**
	 * Issue resolution
	 */
	private ConfDataExternal resolution;
	

	/**
	 * last date of issue update
	 */
	private Date dateLastUpdate;

	/**
	 * Creation date of the issue
	 */
	private Date dateCreation;

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
	 * @return the id
	 */
	public String getId() {
		return id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}



	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}



	/**
	 * @param summary the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
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
	 * @return the status
	 */
	public ConfDataExternal getStatus() {
		return status;
	}



	/**
	 * @param status the status to set
	 */
	public void setStatus(ConfDataExternal status) {
		this.status = status;
	}



	/**
	 * @return the priority
	 */
	public ConfDataExternal getPriority() {
		return priority;
	}



	/**
	 * @param priority the priority to set
	 */
	public void setPriority(ConfDataExternal priority) {
		this.priority = priority;
	}



	/**
	 * @return the severity
	 */
	public ConfDataExternal getSeverity() {
		return severity;
	}



	/**
	 * @param severity the severity to set
	 */
	public void setSeverity(ConfDataExternal severity) {
		this.severity = severity;
	}



	/**
	 * @return the resolution
	 */
	public ConfDataExternal getResolution() {
		return resolution;
	}



	/**
	 * @param resolution the resolution to set
	 */
	public void setResolution(ConfDataExternal resolution) {
		this.resolution = resolution;
	}



	/**
	 * @return the dateLastUpdate
	 */
	public Date getDateLastUpdate() {
		return dateLastUpdate;
	}



	/**
	 * @param dateLastUpdate the dateLastUpdate to set
	 */
	public void setDateLastUpdate(Date dateLastUpdate) {
		this.dateLastUpdate = dateLastUpdate;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id=");
		sb.append(this.id);
		sb.append("; summary=");
		sb.append(this.summary);
		sb.append("; description=");
		sb.append(this.description);
		sb.append("; status=");
		sb.append(this.status);
		sb.append("; priority=");
		sb.append(this.priority);
		sb.append("; resolution=");
		sb.append(this.resolution);
		sb.append("; severity=");
		sb.append(this.severity);
		sb.append("; dateLastUpdate=");
		sb.append(this.dateLastUpdate);
		
		return sb.toString();
	}
	
}
