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

import java.util.List;

import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.bugtracker.utils.Utils;

import junit.framework.TestCase;

/**
 * Test Utils class
 *
 */
public class UtilsTest extends TestCase {

	/**
	 * test generatePathIssueFactory method
	 */
	public void testGeneratePathIssueFactory() throws Exception {
		
		/*
		 * Test OK
		 */
		assertEquals("/toto/issue_10", Utils.generatePathIssueFactory("/toto", "10"));
		
		assertEquals("/toto/titi/issue_10", Utils.generatePathIssueFactory("/toto/titi", "10"));
		
		/*
		 * Exception cases
		 */
		//Invalid projectPath
		try {
			Utils.generatePathIssueFactory("toto", "10");
			fail("toto is not a valid path");
		}
		catch (InvalidPathException e) {
			//OK
		}
		//Invalid projectPath
		try {
			Utils.generatePathIssueFactory("/toto/", "10");
			fail("/toto/ is not a valid path");
		}
		catch (InvalidPathException e) {
			//OK
		}
		//Invalid projectPath
		try {
			Utils.generatePathIssueFactory("", "10");
			fail("'' is not a valid path");
		}
		catch (InvalidPathException e) {
			//OK
		}
		//Invalid projectPath
		try {
			Utils.generatePathIssueFactory(null, "10");
			fail("null is not a valid path");
		}
		catch (InvalidPathException e) {
			//OK
		}
	}
	
	/**
	 * Test getIdBugTracker
	 * @throws Exception Exception
	 */
	public void testGetIdBugTracker() throws Exception {
		/*
		 * Test OK
		 */
		assertEquals(10, Utils.getIdBugTracker("/toto/issue_10"));
		assertEquals(10, Utils.getIdBugTracker("/toto/titi/issue_10"));
		assertEquals(10, Utils.getIdBugTracker("/issue_10"));
		
		/*
		 * Exception cases
		 */
		//Invalid issue id
		try {
			Utils.getIdBugTracker("/toto/10");
			fail("10 is not a valid id");
		}
		catch (InvalidPathException e) {
			//OK
		}
		
		//Invalid issue id
		try {
			Utils.getIdBugTracker("toto/10");
			fail("toto is not a valid projectpath");
		}
		catch (InvalidPathException e) {
			//OK
		}
		//Invalid issue id
		try {
			Utils.getIdBugTracker(null);
			fail("null is not a valid issuePath");
		}
		catch (InvalidPathException e) {
			//OK
		}
		//Invalid issue id
		try {
			Utils.getIdBugTracker("");
			fail("vide is not a valid issuePath");
		}
		catch (InvalidPathException e) {
			//OK
		}
		
		//Invalid issue id (not long)
		try {
			Utils.getIdBugTracker("/toto/issue_dix");
			fail("vide is not a valid issuePath (not long)");
		}
		catch (InvalidPathException e) {
			//OK
		}
	}
	
	public void testCopyToList() {
		
		/*
		 * Array not null 
		 */
		String[] tab = new String[3];
		tab[0] = "A";
		tab[1] = "B";
		tab[2] = "C";
		
		List<String> list = Utils.copyToList(tab);
		assertEquals(tab.length, list.size());
		
		for (int i = 0; i < tab.length; i++) {
			assertEquals(tab[i], list.get(i));
		}
		
		//If I remove on the list, the array does't change
		list.remove("B");
		
		//Check
		assertEquals(3, tab.length);
		assertEquals(2, list.size());
		assertEquals("A", list.get(0));
		assertEquals("C", list.get(1));
		
		/*
		 * Array null
		 */
		list = Utils.copyToList(null);
		assertTrue(list.isEmpty());
	}
}
