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
package org.qualipso.factory.svn.ssh.command.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.svn.exception.SVNServiceException;
import org.qualipso.factory.svn.exception.SVNTechnicalException;
import org.qualipso.factory.svn.ssh.command.TaskType;
import org.qualipso.factory.svn.ssh.command.filter.component.SVNResourceType;

/**
 * 
 * Behavior of a SVNOperation
 *
 */
public class SVNOperationBehavior {

	/**
	 * LogFactory
	 */
	private static final Log logger = LogFactory.getLog(SVNOperationBehavior.class);
	
	/**
	 * Task accepted for a SVNOperation
	 */
	private List<TaskType> tasksAccepted = new ArrayList<TaskType>();
	
	/**
	 * Executor
	 */
	private AbstractServiceExecutor executor;
	
	/**
	 * Constructor
	 * @param pExecutor AbstractServiceExecutor
	 */
	public SVNOperationBehavior(AbstractServiceExecutor pExecutor) {
		this.executor = pExecutor;
	}
	
	
	/**
	 * Configure by adding a task accepted
	 * @param task TaskType
	 */
	public void addTaskAccepted(TaskType task) {
		this.tasksAccepted.add(task);
	}
	
	
	/**
	 * check if the task is accepted
	 * @param task TaskType
	 * @return true if the task is accepted
	 */
	public boolean isTaskAccepted(TaskType task) {
		if (task == null) {
			logger.error("TaskType cannot be null for isTaskAccepted");
			throw new SVNTechnicalException("TaskType cannot be null for isTaskAccepted");
		}
		return this.tasksAccepted.contains(task);
	}

	/**
	 * Execute the service
	 * @param type of svn resource
	 * @param svnPathParts part of the svn url
	 */
	public void execute(SVNResourceType type, List<String> svnPathParts) throws SVNServiceException {
		executor.execute(type, svnPathParts);
	}
	
	
}
