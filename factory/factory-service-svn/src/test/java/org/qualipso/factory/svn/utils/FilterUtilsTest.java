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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.qualipso.factory.svn.exception.SVNTechnicalException;
import org.qualipso.factory.svn.ssh.command.filter.SVNOperationType;
import org.qualipso.factory.svn.ssh.command.filter.component.RequestComponent;
import org.qualipso.factory.svn.ssh.command.filter.component.RequestComponentOperation;
import org.qualipso.factory.svn.ssh.command.filter.component.RequestComponentResource;
import org.qualipso.factory.svn.ssh.command.filter.component.RequestComponentUrl;
import org.qualipso.factory.svn.ssh.command.filter.component.SVNResource;
import org.qualipso.factory.svn.ssh.command.filter.component.SVNResourceType;

/**
 * Test of FilterUtils class
 *
 */
public class FilterUtilsTest extends TestCase {
	
	
	public void testSplitPath() {
		/*
		 * Complete path
		 */
		String[] partsExpected = new String[] {
				"toto", "titi"
		};
		
		String[] parts = FilterUtils.splitPath("/toto/titi/");
		assertEquals(parts, partsExpected);
		
		parts = FilterUtils.splitPath("toto/titi/");
		assertEquals(parts, partsExpected);
		
		parts = FilterUtils.splitPath("/toto/titi");
		assertEquals(parts, partsExpected);
		
		parts = FilterUtils.splitPath("toto/titi");
		assertEquals(parts, partsExpected);
		
		parts = FilterUtils.splitPath("toto/titi'");
		assertEquals(parts, partsExpected);
		
		parts = FilterUtils.splitPath("toto'/titi");
		assertEquals(parts, partsExpected);
		
		parts = FilterUtils.splitPath("'toto'/'titi'");
		assertEquals(parts, partsExpected);
		
		parts = FilterUtils.splitPath("'toto\"/'titi'");
		assertEquals(parts, partsExpected);
		
		/*
		 * minimal path
		 */
		partsExpected = new String[] {
				"toto"
		};
		
		parts = FilterUtils.splitPath("/toto/");
		assertEquals(parts, partsExpected);
		
		parts = FilterUtils.splitPath("toto");
		assertEquals(parts, partsExpected);
		
		parts = FilterUtils.splitPath("/toto");
		assertEquals(parts, partsExpected);
		
		parts = FilterUtils.splitPath("toto/");
		assertEquals(parts, partsExpected);
		
		/*
		 * Root
		 */
		partsExpected = new String[] {
				""
		};
		
		parts = FilterUtils.splitPath("/");
		assertEquals(parts, partsExpected);
		
		parts = FilterUtils.splitPath("");
		assertEquals(parts, partsExpected);
	}
	
	/**
	 * test FilterUtils.extractURL
	 * @throws Exception if an error occurred
	 */
    public void testExtractUrl() throws Exception {
    	/*
    	 * request null 
    	 */
    	String request = null;
    	Map<Integer, RequestComponent> requestComponents = new HashMap<Integer, RequestComponent>();
    	FilterUtils.extractURL(request, 0, requestComponents);
    	assertTrue(requestComponents.isEmpty());
    	
    	/*
    	 * request empty 
    	 */
    	request = "";
    	FilterUtils.extractURL(request, 0, requestComponents);
    	assertTrue(requestComponents.isEmpty());
    	
    	/*
    	 * requestComponents null
    	 */
    	requestComponents = null;
    	request = "a";
    	try {
    		FilterUtils.extractURL(request, 0, requestComponents);
    		fail("requestComponents cannot be null");
    	}
    	catch (SVNTechnicalException e) {
    		//test ok
    	}
    	
    	/*
    	 * Index > request.length
    	 */
    	requestComponents = new HashMap<Integer, RequestComponent>();
    	FilterUtils.extractURL(request, 2, requestComponents);
    	assertTrue(requestComponents.isEmpty());
    	
    	/*
    	 * Extract 1 url
    	 */
    	requestComponents = new HashMap<Integer, RequestComponent>();
    	request="( 2 ( edit-pipeline svndiff1 absent-entries depth mergeinfo log-revprops ) 43:svn+ssh://anonymous@localhost:8022/test-svn ) ";
    	FilterUtils.extractURL(request, 0, requestComponents);
    	assertEquals(1, requestComponents.size());
    	RequestComponent componentExpected = new RequestComponentUrl(request, "svn+ssh://anonymous@localhost:8022/test-svn");
    	assertTrue(requestComponents, 0, componentExpected);
    	
    	/*
    	 * Extract 2 url
    	 */
    	requestComponents = new HashMap<Integer, RequestComponent>();
    	request="( 2 ( edit-pipeline svndiff1 absent-entries depth mergeinfo log-revprops ) 43:svn+ssh://anonymous@localhost:8022/test-svn  log-revprops ) 43:svn+ssh://anonymous@localhost:8022/test-svn2 dsff) ";
    	FilterUtils.extractURL(request, 0, requestComponents);
    	assertEquals(2, requestComponents.size());
    	componentExpected = new RequestComponentUrl(request, "svn+ssh://anonymous@localhost:8022/test-svn");
    	assertTrue(requestComponents, 0, componentExpected);
    	componentExpected = new RequestComponentUrl(request, "svn+ssh://anonymous@localhost:8022/test-svn2");
    	assertTrue(requestComponents, 1, componentExpected);
    }
    
	/**
	 * test FilterUtils.extractURL
	 * @throws Exception if an error occurred
	 */
    public void testExtractSVNOperation() throws Exception {
    	/*
    	 * request null 
    	 */
    	String request = null;
    	Map<Integer, RequestComponent> requestComponents = new HashMap<Integer, RequestComponent>();
    	FilterUtils.extractSVNOperation(request, 0, SVNOperationType.COMMIT, requestComponents);
    	assertTrue(requestComponents.isEmpty());
    	
    	/*
    	 * request empty 
    	 */
    	request = "";
    	FilterUtils.extractSVNOperation(request, 0, SVNOperationType.COMMIT, requestComponents);
    	assertTrue(requestComponents.isEmpty());
    	
    	/*
    	 * requestComponents null
    	 */
    	requestComponents = null;
    	request = "a";
    	try {
    		FilterUtils.extractSVNOperation(request, 0, SVNOperationType.COMMIT, requestComponents);
    		fail("requestComponents cannot be null");
    	}
    	catch (SVNTechnicalException e) {
    		//test ok
    	}
    	
    	/*
    	 * Index > request.length
    	 */
    	requestComponents = new HashMap<Integer, RequestComponent>();
    	FilterUtils.extractSVNOperation(request, 2, SVNOperationType.COMMIT, requestComponents);
    	assertTrue(requestComponents.isEmpty());
    	
    	/*
    	 * Extract 1 commit
    	 */
    	requestComponents = new HashMap<Integer, RequestComponent>();
    	request="( commit ( 12:first import ( ) false ( ( 7:svn:log 12:first import ( update ( )  ) ) ) ";
    	FilterUtils.extractSVNOperation(request, 0, SVNOperationType.COMMIT, requestComponents);
    	assertEquals(1, requestComponents.size());
    	RequestComponent componentExpected = new RequestComponentOperation(request, SVNOperationType.COMMIT);
    	assertTrue(requestComponents, 0, componentExpected);
    	
    	/*
    	 * Extract 2 commit
    	 */
    	requestComponents = new HashMap<Integer, RequestComponent>();
    	request="( commit ( 12:first import ( ) false ( ( 7:svn:log ( commit ( 12:first import ( update ( )  ) ) ) ";
    	FilterUtils.extractSVNOperation(request, 0, SVNOperationType.COMMIT, requestComponents);
    	assertEquals(2, requestComponents.size());
    	componentExpected = new RequestComponentOperation(request, SVNOperationType.COMMIT);
    	assertTrue(requestComponents, 0, componentExpected);
    	componentExpected = new RequestComponentOperation(request, SVNOperationType.COMMIT);
    	assertTrue(requestComponents, 1, componentExpected);
    	
    	/*
    	 * Many operation
    	 */
    	requestComponents = new HashMap<Integer, RequestComponent>();
    	request="( commit ( 12:first ( diff (import ( ) false ( ( 7:svn:log ( commit ( 12:first import ( update ( )  ) ) ) ";
    	FilterUtils.extractSVNOperation(request, 0, SVNOperationType.COMMIT, requestComponents);
    	FilterUtils.extractSVNOperation(request, 0, SVNOperationType.UPDATE, requestComponents);
    	FilterUtils.extractSVNOperation(request, 0, SVNOperationType.DIFF, requestComponents);
    	
    	assertEquals(4, requestComponents.size());
    	componentExpected = new RequestComponentOperation(request, SVNOperationType.COMMIT);
    	assertTrue(requestComponents, 0, componentExpected);
    	componentExpected = new RequestComponentOperation(request, SVNOperationType.DIFF);
    	assertTrue(requestComponents, 1, componentExpected);
    	componentExpected = new RequestComponentOperation(request, SVNOperationType.COMMIT);
    	assertTrue(requestComponents, 2, componentExpected);
    	componentExpected = new RequestComponentOperation(request, SVNOperationType.UPDATE);
    	assertTrue(requestComponents, 3, componentExpected);
    }
    
    /**
	 * test FilterUtils.extractURL
	 * @throws Exception if an error occurred
	 */
    public void testExtractSVNResource() throws Exception {
    	/*
    	 * request null 
    	 */
    	String request = null;
    	Map<Integer, RequestComponent> requestComponents = new HashMap<Integer, RequestComponent>();
    	FilterUtils.extractSVNResource(request, 0, SVNResourceType.ADD_FILE, requestComponents);
    	assertTrue(requestComponents.isEmpty());
    	
    	/*
    	 * request empty 
    	 */
    	request = "";
    	FilterUtils.extractSVNResource(request, 0, SVNResourceType.ADD_FILE, requestComponents);
    	assertTrue(requestComponents.isEmpty());
    	
    	/*
    	 * requestComponents null
    	 */
    	requestComponents = null;
    	request = "a";
    	try {
    		FilterUtils.extractSVNResource(request, 0, SVNResourceType.ADD_FILE, requestComponents);
    		fail("requestComponents cannot be null");
    	}
    	catch (SVNTechnicalException e) {
    		//test ok
    	}
    	
    	/*
    	 * Index > request.length
    	 */
    	requestComponents = new HashMap<Integer, RequestComponent>();
    	FilterUtils.extractSVNResource(request, 2, SVNResourceType.ADD_FILE, requestComponents);
    	assertTrue(requestComponents.isEmpty());
    	
    	/*
    	 * Extract 1 resource
    	 */
    	requestComponents = new HashMap<Integer, RequestComponent>();
    	request="( add-file ( 13:testfile1.txt 2:d0 2:c1 ( ) ) ) ";
    	FilterUtils.extractSVNResource(request, 0, SVNResourceType.ADD_FILE, requestComponents);
    	assertEquals(1, requestComponents.size());
    	RequestComponent componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.ADD_FILE, "testfile1.txt"));
    	assertTrue(requestComponents, 0, componentExpected);
    	
    	/*
    	 * Extract 2 resources
    	 */
    	requestComponents = new HashMap<Integer, RequestComponent>();
    	request="( add-file ( 13:testfile1.txt 2:d0 2:c1 ( ) ) )  ( add-file ( 13:testfile2.txt 2:d0 2:c1 ( ) ) ) ";
    	FilterUtils.extractSVNResource(request, 0, SVNResourceType.ADD_FILE, requestComponents);
    	assertEquals(2, requestComponents.size());
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.ADD_FILE, "testfile1.txt"));
    	assertTrue(requestComponents, 0, componentExpected);
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.ADD_FILE, "testfile2.txt"));
    	assertTrue(requestComponents, 1, componentExpected);
    	
    	/*
    	 * Extract 2 resources
    	 */
    	requestComponents = new HashMap<Integer, RequestComponent>();
    	request="( add-dir ( 13:subdir1 2:d0 2:c1 ( ) ) )  " +
		"( delete-entry ( 13:testfile0.txt ( ) 2:d0 ) ) " +
		"( 57:svn+ssh://anonymous@localhost:8022/test-svn/testfile1.txt 1 ) ) )" +
		"( add-file ( 13:testfile1.txt 2:d0 2:c1 ( ) ) ) " +
		"( delete-entry ( 13:testfile1.txt ( 1 ) 2:d0 ) ) " +
		"( add-file ( 13:testfile2.txt 2:d0 2:c1 ( ) ) )  " +
		"( delete-entry ( 13:testfile2.tx ( 1 ) 2:d0 ) ) " +
		"( commit ( 12:first" +
		" ( diff (import ( ) false ( ( 7:svn:log" +
		" ( commit ( 12:first import" +
		" ( update ( )  ) ) ) " +
		"( add-dir ( 13:subdir2 2:d0 2:c1 ( ) ) ) ";
    	
    	FilterUtils.extractSVNResourceDelete(request, 0, requestComponents);
    	assertEquals(3, requestComponents.size());
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.DELETE_ENTRY, "testfile0.txt"));
    	assertTrue(requestComponents, 0, componentExpected);
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.DELETE_ENTRY, "testfile1.txt"));
    	assertTrue(requestComponents, 1, componentExpected);
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.DELETE_ENTRY, "testfile2.tx"));
    	assertTrue(requestComponents, 2, componentExpected);
    	
    	/*
    	 * Many resources
    	 */
    	requestComponents = new HashMap<Integer, RequestComponent>();
    	request="( add-dir ( 13:subdir1 2:d0 2:c1 ( ) ) )  " +
    			"( delete-entry ( 13:testfile0.txt ( ) 2:d0 ) ) " +
    			"( 57:svn+ssh://anonymous@localhost:8022/test-svn/testfile1.txt 1 ) ) )" +
    			"( add-file ( 13:testfile1.txt 2:d0 2:c1 ( ) ) ) " +
    			"( delete-entry ( 13:testfile1.txt ( 1 ) 2:d0 ) ) " +
    			"( add-file ( 13:testfile2.txt 2:d0 2:c1 ( ) ) )  " +
    			"( delete-entry ( 13:testfile2.tx ( 1 ) 2:d0 ) ) " +
    			"( commit ( 12:first" +
    			" ( diff (import ( ) false ( ( 7:svn:log" +
    			" ( commit ( 12:first import" +
    			" ( update ( )  ) ) ) " +
    			"( add-dir ( 13:subdir2 2:d0 2:c1 ( ) ) ) ";
    	
    	FilterUtils.extractSVNResource(request, 0, SVNResourceType.ADD_FILE, requestComponents);
    	FilterUtils.extractSVNResource(request, 0, SVNResourceType.ADD_DIR, requestComponents);
    	FilterUtils.extractSVNResourceDelete(request, 0, requestComponents);
    	
    	assertEquals(7, requestComponents.size());
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.ADD_DIR, "subdir1"));
    	assertTrue(requestComponents, 0, componentExpected);
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.DELETE_ENTRY, "testfile0.txt"));
    	assertTrue(requestComponents, 1, componentExpected);
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.ADD_FILE, "testfile1.txt"));
    	assertTrue(requestComponents, 2, componentExpected);
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.DELETE_ENTRY, "testfile1.txt"));
    	assertTrue(requestComponents, 3, componentExpected);
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.ADD_FILE, "testfile2.txt"));
    	assertTrue(requestComponents, 4, componentExpected);
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.DELETE_ENTRY, "testfile2.tx"));
    	assertTrue(requestComponents, 5, componentExpected);
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.ADD_DIR, "subdir2"));
    	assertTrue(requestComponents, 6, componentExpected);
    }
    
    
    /**
     * test with all extract
     */
    public void testExtractGeneral() {
    	Map<Integer, RequestComponent> requestComponents = new HashMap<Integer, RequestComponent>();
    	String request="( add-dir ( 13:subdir1 2:d0 2:c1 ( ) ) )  " +
    			"( delete-entry ( 13:testfile0.txt ( ) 2:d0 ) ) " +
    			"( 57:svn+ssh://anonymous@localhost:8022/test-svn/testfile1.txt 1 ) ) )" +
    			"( add-file ( 13:testfile1.txt 2:d0 2:c1 ( ) ) ) " +
    			"( delete-entry ( 13:testfile1.txt ( 1 ) 2:d0 ) ) " +
    			"( add-file ( 13:testfile2.txt 2:d0 2:c1 ( ) ) )  " +
    			"( delete-entry ( 13:testfile2.tx ( 1 ) 2:d0 ) ) " +
    			"( commit ( 12:first" +
    			" ( diff (import ( ) false ( ( 7:svn:log" +
    			" ( commit ( 12:first import" +
    			" ( update ( )  ) ) ) " +
    			"( add-dir ( 13:subdir2 2:d0 2:c1 ( ) ) ) ";
    	
    	FilterUtils.extractSVNResource(request, 0, SVNResourceType.ADD_FILE, requestComponents);
    	FilterUtils.extractSVNResource(request, 0, SVNResourceType.ADD_DIR, requestComponents);
    	FilterUtils.extractSVNResourceDelete(request, 0, requestComponents);
    	FilterUtils.extractURL(request, 0, requestComponents);
    	FilterUtils.extractSVNOperation(request, 0, SVNOperationType.COMMIT, requestComponents);
    	FilterUtils.extractSVNOperation(request, 0, SVNOperationType.UPDATE, requestComponents);
    	FilterUtils.extractSVNOperation(request, 0, SVNOperationType.DIFF, requestComponents);
    	
    	assertEquals(12, requestComponents.size());
    	RequestComponent componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.ADD_DIR, "subdir1"));
    	assertTrue(requestComponents, 0, componentExpected);
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.DELETE_ENTRY, "testfile0.txt"));
    	assertTrue(requestComponents, 1, componentExpected);
    	componentExpected = new RequestComponentUrl(request, "svn+ssh://anonymous@localhost:8022/test-svn/testfile1.txt");
    	assertTrue(requestComponents, 2, componentExpected);
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.ADD_FILE, "testfile1.txt"));
    	assertTrue(requestComponents, 3, componentExpected);
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.DELETE_ENTRY, "testfile1.txt"));
    	assertTrue(requestComponents, 4, componentExpected);
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.ADD_FILE, "testfile2.txt"));
    	assertTrue(requestComponents, 5, componentExpected);
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.DELETE_ENTRY, "testfile2.tx"));
    	assertTrue(requestComponents, 6, componentExpected);
    	componentExpected = new RequestComponentOperation(request, SVNOperationType.COMMIT);
    	assertTrue(requestComponents, 7, componentExpected);
    	componentExpected = new RequestComponentOperation(request, SVNOperationType.DIFF);
    	assertTrue(requestComponents, 8, componentExpected);
    	componentExpected = new RequestComponentOperation(request, SVNOperationType.COMMIT);
    	assertTrue(requestComponents, 9, componentExpected);
    	componentExpected = new RequestComponentOperation(request, SVNOperationType.UPDATE);
    	assertTrue(requestComponents, 10, componentExpected);
    	componentExpected = new RequestComponentResource(request, new SVNResource(SVNResourceType.ADD_DIR, "subdir2"));
    	assertTrue(requestComponents, 11, componentExpected);
    }
    
    /**
     * test FilterUtils.generateResourcePath
     */
    public void testGenerateResourcePath() {
    	/*
    	 * complete result
    	 */
    	List<String> resultExpected = Arrays.asList("repo1", "titi", "toto");
    	
    	List<String> result = FilterUtils.generateResourcePath("repo1", "titi/toto");
    	assertEquals(resultExpected, result);
    	
    	result = FilterUtils.generateResourcePath("/repo1/", "/titi/toto/");
    	assertEquals(resultExpected, result);
    	
    	result = FilterUtils.generateResourcePath("", "repo1/titi/toto/");
    	assertEquals(resultExpected, result);
    	
    	result = FilterUtils.generateResourcePath("repo1/titi/toto/", "");
    	assertEquals(resultExpected, result);
    	
    	/*
    	 * No result
    	 */
    	resultExpected = new ArrayList<String>();
    	
    	result = FilterUtils.generateResourcePath("", "");
    	assertEquals(resultExpected, result);
    	
    	result = FilterUtils.generateResourcePath(null, null);
    	assertEquals(resultExpected, result);
    }
    
    /**
     * test extractIdRepositoryFromUrl
     */
    public void testExtractIdRepositoryFromUrl() {
    	assertEquals("", FilterUtils.extractIdRepositoryFromUrl(""));
    	assertEquals("", FilterUtils.extractIdRepositoryFromUrl(null));
    	assertEquals("idRepo", FilterUtils.extractIdRepositoryFromUrl("svn+ssh//localhost:3333/idRepo"));
    	assertEquals("idRepo", FilterUtils.extractIdRepositoryFromUrl("svn+ssh//127.0.0.1:3333/idRepo"));
    	assertEquals("", FilterUtils.extractIdRepositoryFromUrl("svn+ssh//toto:3333/idRepo"));
    	assertEquals("", FilterUtils.extractIdRepositoryFromUrl("svn+ssh//localhost:3334/idRepo"));
    	assertEquals("idRepo", FilterUtils.extractIdRepositoryFromUrl("svn+ssh//localhost:3333/idRepo/"));
    	assertEquals("idRepo", FilterUtils.extractIdRepositoryFromUrl("svn+ssh//localhost:3333/idRepo/repo2"));
    }
    
    
    /**
     * Assert equals requestComponent of the map
     * @param requestComponents
     * @param numberComponent
     * @param componentExpected
     */
    private void assertTrue(Map<Integer, RequestComponent> requestComponents, int numberComponent, RequestComponent componentExpected) {
    	List<RequestComponent> list = getSortedList(requestComponents);
    	assertEquals(componentExpected.toString(), list.get(numberComponent).toString());
    }

    /**
     * Get list sorted by key of the map
     * @param requestComponents map
     * @return list sorted
     */
    private List<RequestComponent> getSortedList(Map<Integer, RequestComponent> requestComponents) {
    	List<RequestComponent> list = new ArrayList<RequestComponent>();
    	
    	Set<Integer> indexes = requestComponents.keySet();
		List<Integer> listIndexes = new ArrayList<Integer>();
		listIndexes.addAll(indexes);
		Collections.sort(listIndexes);
		
		for (Integer index : listIndexes) {
			RequestComponent requestComponent = requestComponents.get(index);
			list.add(requestComponent);
			
		}
    	return list;
    }
    
    private void assertEquals(String[] o1, String[] o2) {
    	assertEquals(o1.length, o2.length);
    	
    	for (int i = 0; i < o1.length; i++) {
			assertEquals(o1[i], o2[i]);
		}
    }
}
