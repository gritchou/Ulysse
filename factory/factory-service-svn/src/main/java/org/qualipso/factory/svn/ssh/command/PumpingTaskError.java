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
package org.qualipso.factory.svn.ssh.command;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.qualipso.factory.svn.exception.SVNServiceException;
import org.qualipso.factory.svn.ssh.command.filter.component.RequestComponent;

/**
 * PumpingTaskError
 */
public class PumpingTaskError extends PumpingTask {

	/**
	 * Constructor
	 * @param in InputStream
	 * @param out OutputStream
	 * @param command SVNServeSSHCommand
	 */
	public PumpingTaskError(InputStream in, OutputStream out, SVNServeSSHCommand command) {
		super(in, out, TaskType.ERROR, command);
	}

	
	/* (non-Javadoc)
	 * @see org.qualipso.factory.svn.ssh.command.PumpingTask#filter(java.lang.String, java.util.Map, org.qualipso.factory.svn.ssh.command.TaskType)
	 */
	@Override
	protected void filter(String request,
			Map<Integer, RequestComponent> requestComponents, TaskType type)
			throws SVNServiceException {
		//no filter
		
	}
}
