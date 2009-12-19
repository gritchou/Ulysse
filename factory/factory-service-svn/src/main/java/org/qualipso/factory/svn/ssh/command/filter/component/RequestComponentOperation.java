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
import org.qualipso.factory.svn.ssh.command.filter.SVNOperationType;
import org.qualipso.factory.svn.ssh.command.filter.RequestFilter;

/**
 * Component type operation
 */
public class RequestComponentOperation extends RequestComponent {

	/**
	 * SVNOperationType
	 */
	private SVNOperationType operationTypeEnum;

	/**
	 * Constructor
	 * @param requestOrig Request orig of the component
	 * @param operationTypeEnum SVNOperationType
	 */
	public RequestComponentOperation(String requestOrig, SVNOperationType operationTypeEnum) {
		super(requestOrig);
		this.operationTypeEnum = operationTypeEnum;
	}

	/* (non-Javadoc)
	 * @see org.qualipso.factory.svn.ssh.command.filter.component.RequestComponent#execInFilter(org.qualipso.factory.svn.ssh.command.filter.RequestFilter, org.qualipso.factory.svn.ssh.command.TaskType)
	 */
	@Override
	public void execInFilter(RequestFilter queryAnalyser, TaskType taskType){
		queryAnalyser.defineSVNOperation(operationTypeEnum);
		
	}

	/* (non-Javadoc)
	 * @see org.qualipso.factory.svn.ssh.command.filter.component.RequestComponent#toString()
	 */
	@Override
	public String toString() {
		return "SVNOperationType=" + operationTypeEnum.toString();
	}
}
