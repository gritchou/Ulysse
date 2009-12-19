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
package org.qualipso.factory.svn.ssh.command.filter.component;

import org.qualipso.factory.svn.exception.SVNServiceException;
import org.qualipso.factory.svn.ssh.command.TaskType;
import org.qualipso.factory.svn.ssh.command.filter.RequestFilter;

/**
 * Component of the request
 */
public abstract class RequestComponent {

	/**
	 * Request orig of the component
	 */
	protected String requestOrig;
	
	
	
	/**
	 * Constructor
	 * @param requestOrig Request orig of the component
	 */
	public RequestComponent(String requestOrig) {
		super();
		this.requestOrig = requestOrig;
	}

	/**
	 * Execure check with the filter
	 * @param requestFilter RequestFilter
	 * @param taskType TaskType
	 * @throws SVNServiceException if an error occurred
	 */
	public abstract void execInFilter(RequestFilter requestFilter, TaskType taskType) throws SVNServiceException;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public abstract String toString();

	/**
	 * @return the requestOrig
	 */
	public String getRequestOrig() {
		return requestOrig;
	}
	
	
}
