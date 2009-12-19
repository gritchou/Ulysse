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
package org.qualipso.factory.svn;

import java.util.List;

import javax.ejb.Local;

import org.qualipso.factory.svn.exception.SVNServiceException;
import org.qualipso.factory.svn.ssh.command.filter.component.SVNResourceType;

/**
 * Local interface for subversion service
 */
@Local
public interface SVNServiceLocal {
	
	/**
	 * Create a resource in subversion repository
	 * @param type of svn resource
	 * @param svnPathParts part of the svn url
	 * @throws SVNServiceException if an error occurred
	 */
	public void writeInRepository(SVNResourceType type, List<String> svnPathParts) throws SVNServiceException;
	
	/**
	 * Read a resource in subversion repository
	 * @param type of svn resource
	 * @param svnPathParts part of the svn url
	 * @throws SVNServiceException if an error occurred
	 */
	public void readFromRepository(SVNResourceType type, List<String> svnPathParts) throws SVNServiceException;



}
