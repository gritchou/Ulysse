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
import java.util.ArrayList;
import java.util.List;


/**
 * Attributes to create a bug
 */
public class IssueAttributesDto implements Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4675259930078574884L;

	/**
	 * Priorities
	 */
	private List<ConfDataDto> priorities = new ArrayList<ConfDataDto>();
	
	/**
	 * Resolutions
	 */
	private List<ConfDataDto> resolutions = new ArrayList<ConfDataDto>();
	
	/**
	 * Severities
	 */
	private List<ConfDataDto> severities = new ArrayList<ConfDataDto>();
	
	/**
	 * Status
	 */
	private List<ConfDataDto> status = new ArrayList<ConfDataDto>();

	/**
	 * @return the priorities
	 */
	public List<ConfDataDto> getPriorities() {
		return priorities;
	}

	/**
	 * @param priorities the priorities to set
	 */
	public void setPriorities(List<ConfDataDto> priorities) {
		this.priorities = priorities;
	}

	/**
	 * @return the resolutions
	 */
	public List<ConfDataDto> getResolutions() {
		return resolutions;
	}

	/**
	 * @param resolutions the resolutions to set
	 */
	public void setResolutions(List<ConfDataDto> resolutions) {
		this.resolutions = resolutions;
	}

	/**
	 * @return the severities
	 */
	public List<ConfDataDto> getSeverities() {
		return severities;
	}

	/**
	 * @param severities the severities to set
	 */
	public void setSeverities(List<ConfDataDto> severities) {
		this.severities = severities;
	}

	/**
	 * @return the status
	 */
	public List<ConfDataDto> getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(List<ConfDataDto> status) {
		this.status = status;
	}
	
}
