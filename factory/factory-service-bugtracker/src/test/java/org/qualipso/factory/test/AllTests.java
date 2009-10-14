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
package org.qualipso.factory.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.qualipso.factory.test.sessionbean.BugTrackerServiceTest;
import org.qualipso.factory.test.utils.BugTrackerResourcesTest;
import org.qualipso.factory.test.utils.ConfDataDtoIdComparatorTest;
import org.qualipso.factory.test.utils.UtilsTest;

/**
 * All Test
 */
@RunWith(Suite.class)
@SuiteClasses(value = { 
		BugTrackerServiceTest.class,
		BugTrackerResourcesTest.class,  
		ConfDataDtoIdComparatorTest.class, 
		UtilsTest.class})
public class AllTests {
	
	
}
