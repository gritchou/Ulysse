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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.svn.ssh.command.TaskType;
import org.qualipso.factory.svn.ssh.command.filter.RequestFilter;
import org.qualipso.factory.svn.utils.FilterUtils;

/**
 * Component type url
 */
public class RequestComponentUrl extends RequestComponent {

	/**
	 * LogFactory
	 */
	private static final Log logger = LogFactory.getLog(RequestComponentUrl.class);
	
	/**
	 * Url
	 */
	private String url;

	
	/**
	 * Constructor
	 * @param requestOrig Request orig of the component
	 * @param url url
	 */
	public RequestComponentUrl(String requestOrig, String url) {
		super(requestOrig);
		this.url = url;
	}

	/* (non-Javadoc)
	 * @see org.qualipso.factory.svn.ssh.command.filter.component.RequestComponent#execInFilter(org.qualipso.factory.svn.ssh.command.filter.RequestFilter, org.qualipso.factory.svn.ssh.command.TaskType)
	 */
	@Override
	public void execInFilter(RequestFilter queryAnalyser, TaskType taskType) {
		logger.debug("execurl - request orig = " + requestOrig);
		
		//Extract id repository
		String idRepository = FilterUtils.extractIdRepositoryFromUrl(this.url);
		
		if (!StringUtils.isEmpty(idRepository)) {
			queryAnalyser.defineIdRepository(idRepository);
		}
	}

	/* (non-Javadoc)
	 * @see org.qualipso.factory.svn.ssh.command.filter.component.RequestComponent#toString()
	 */
	@Override
	public String toString() {
		return "url=" + this.url;
	}
}
