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

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.svn.SVNServiceLocal;
import org.qualipso.factory.svn.exception.SVNServiceException;
import org.qualipso.factory.svn.exception.SVNTechnicalException;
import org.qualipso.factory.svn.ssh.command.filter.component.SVNResourceType;
import org.qualipso.factory.svn.utils.SVNConstants;

/**
 * Executor for Abstract service
 */
public abstract class AbstractServiceExecutor {

	
	/**
	 * LogFactory
	 */
	private static final Log logger = LogFactory.getLog(AbstractServiceExecutor.class);
	
	/**
	 * SVNServiceLocal
	 */
	protected SVNServiceLocal serviceLocal;

	protected abstract List<SVNResourceType> getSVNResourceTypeSupported();
	
	/**
	 * Constructor
	 */
	protected AbstractServiceExecutor() {
		super();
		try {
			Context ctx = new InitialContext();
			this.serviceLocal = (SVNServiceLocal) ctx.lookup(FactoryNamingConvention.getLocalJNDINameForService(SVNConstants.SVN_SERVICE_NAME));
		}
		catch (NamingException e) {
			logger.error("NamingException", e);
			throw new SVNTechnicalException(e);
		}
	}

	/**
	 * Execute abstract service
	 * @param type of svn resource
	 * @param svnPathParts part of the svn url
	 * @throws SVNServiceException if an error occurred
	 */
	public final void execute(SVNResourceType type, List<String> svnPathParts) throws SVNServiceException {
		if (getSVNResourceTypeSupported().contains(type)) {
			exec(type, svnPathParts);
		}
	}
	
	/**
	 * Execute abstract service
	 * @param type of svn resource
	 * @param svnPathParts part of the svn url
	 * @throws SVNServiceException if an error occurred
	 */
	public abstract void exec(SVNResourceType type, List<String> svnPathParts) throws SVNServiceException;
}
