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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.svn.exception.SVNServiceException;
import org.qualipso.factory.svn.ssh.command.TaskType;
import org.qualipso.factory.svn.ssh.command.filter.RequestFilter;

/**
 * 
 * Component type SVN Resource
 *
 */
public class RequestComponentResource extends RequestComponent {

	/**
	 * LogFactory
	 */
	private static final Log logger = LogFactory.getLog(RequestComponentResource.class);
	
	/**
	 * SVNResource
	 */
	private SVNResource resource;

	/**
	 * Constructor
	 * @param requestOrig Request orig of the component
	 * @param resource SVNResource
	 */
	public RequestComponentResource(String requestOrig, SVNResource resource) {
		super(requestOrig);
		this.resource = resource;
	}

	@Override
	public void execInFilter(RequestFilter queryAnalyser, TaskType taskType) throws SVNServiceException {
		logger.debug("execresource - request orig = " + requestOrig);
		queryAnalyser.checkResource(this.resource, taskType);
	}

	@Override
	public String toString() {
		return "SVNResource=" + resource.toString();
	}

}
