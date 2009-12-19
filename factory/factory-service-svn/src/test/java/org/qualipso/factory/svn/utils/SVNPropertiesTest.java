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
package org.qualipso.factory.svn.utils;

import junit.framework.TestCase;


/**
 * test BugTrackerResources class
 *
 */
public class SVNPropertiesTest extends TestCase {

	/**
	 * Test resources access
	 */
	public void testResourceAccess() {
		assertEquals("/usr/bin/svnserve", SVNProperties.getInstance().getCmdSvnServe());
		
		assertEquals("data/svn-repositories", SVNProperties.getInstance().getRootDirRepositories());
		
		assertEquals(2, SVNProperties.getInstance().getDepthCheckRights());
		
		assertEquals(2, SVNProperties.getInstance().getUrlsAccess().size());
		assertTrue(SVNProperties.getInstance().getUrlsAccess().contains("127.0.0.1:3333"));
		assertTrue(SVNProperties.getInstance().getUrlsAccess().contains("localhost:3333"));
	}
}
