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
package org.qualipso.factory.test.utils;

import org.apache.commons.lang.StringUtils;
import org.qualipso.factory.bugtracker.core.BugTrackerResources;

import junit.framework.TestCase;


/**
 * test BugTrackerResources class
 *
 */
public class BugTrackerResourcesTest extends TestCase {

	/**
	 * Test resources access
	 */
	public void testResourceAccess() {
		assertTrue(!StringUtils.isEmpty(BugTrackerResources.getInstance().getWSEndPoint()));
		assertTrue(!StringUtils.isEmpty(BugTrackerResources.getInstance().getWSUserName()));
		assertTrue(!StringUtils.isEmpty(BugTrackerResources.getInstance().getWSUserPassword()));
	}
}
