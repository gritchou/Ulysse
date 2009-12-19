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

import org.qualipso.factory.svn.exception.SVNServiceException;
import org.qualipso.factory.svn.ssh.command.filter.component.SVNResourceType;

/**
 * Executor of the method read of the abstract service
 */
public class AbstractServiceExecutorRead extends AbstractServiceExecutor {

	/**
	 * instance
	 */
	protected static AbstractServiceExecutor INSTANCE;
	
	/**
	 * ResourceType accepted for read operation
	 */
	private static List<SVNResourceType> resourcesTypeAccepted = new ArrayList<SVNResourceType> ();
	
	/**
	 * Constructor
	 */
	private AbstractServiceExecutorRead() {
		super();
	}


	/* (non-Javadoc)
	 * @see org.qualipso.factory.svn.ssh.command.filter.AbstractServiceExecutor#exec(org.qualipso.factory.svn.ssh.command.filter.component.SVNResourceType, java.util.List)
	 */
	@Override
	public void exec(SVNResourceType type, List<String> svnPathParts)  throws SVNServiceException {
		this.serviceLocal.readFromRepository(type, svnPathParts);
	}

	
	/**
	 * getInstance
	 * @return AbstractServiceExecutor
	 */ 
	public static AbstractServiceExecutor getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AbstractServiceExecutorRead();
			
			resourcesTypeAccepted.add(SVNResourceType.ADD_DIR);
			resourcesTypeAccepted.add(SVNResourceType.ADD_FILE);
		}
		
		return INSTANCE;
	}


	/* (non-Javadoc)
	 * @see org.qualipso.factory.svn.ssh.command.filter.AbstractServiceExecutor#getSVNResourceTypeSupported()
	 */
	@Override
	protected List<SVNResourceType> getSVNResourceTypeSupported() {
		return resourcesTypeAccepted;
	}
}
