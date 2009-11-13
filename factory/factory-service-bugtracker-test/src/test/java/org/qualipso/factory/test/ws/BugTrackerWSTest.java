package org.qualipso.factory.test.ws;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import org.jboss.ws.core.StubExt;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.bugtracker.client.ws.Bootstrap;
import org.qualipso.factory.bugtracker.client.ws.BootstrapServiceException_Exception;
import org.qualipso.factory.bugtracker.client.ws.Bootstrap_Service;
import org.qualipso.factory.bugtracker.client.ws.BugTrackerServiceException_Exception;
import org.qualipso.factory.bugtracker.client.ws.Bugtracker;
import org.qualipso.factory.bugtracker.client.ws.Bugtracker_Service;
import org.qualipso.factory.bugtracker.client.ws.IssueAttributesDto;
import org.qualipso.factory.bugtracker.client.ws.IssueDto;
import org.qualipso.factory.bugtracker.client.ws.IssueDtoArray;
import org.qualipso.factory.bugtracker.client.ws.Project;
import org.qualipso.factory.bugtracker.client.ws.ProjectException_Exception;
import org.qualipso.factory.bugtracker.client.ws.ProjectService;
import org.qualipso.factory.bugtracker.client.ws.Project_Type;
import org.qualipso.factory.bugtracker.utils.Utils;

/**
 * 
 * BugTracker WS test
 *
 */
public class BugTrackerWSTest {
	
	
	/**
	 * project name
	 */
	private static final String PROJECT_NAME = "junitproject_ws";
	
	/**
	 * project path
	 */
	private static final String PROJECT_PATH = "/" + PROJECT_NAME;
	
	
	/**
	 * User guest
	 */
	private static final String USER_NAME_GUEST = "Anonymous";
	
	/**
	 * ProjectService
	 */
	private Project projectService;
	
	/**
	 * BugTrackerService
	 */
	private Bugtracker bugTrackerService;
	
	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(BugTrackerWSTest.class);
	
	/**
	 * Constructor
	 */
	public BugTrackerWSTest() {
		Bugtracker_Service serviceBugTracker = new Bugtracker_Service();
		bugTrackerService = serviceBugTracker.getBugTrackerServiceBeanPort();
		((StubExt) this.bugTrackerService).setConfigName("Standard WSSecurity Client");
    	
    	ProjectService serviceProject = new ProjectService();
    	this.projectService = serviceProject.getProjectService();
    	((StubExt) this.projectService).setConfigName("Standard WSSecurity Client");
	}
	
	@BeforeClass
	public static void init() {
		try {
			Bootstrap port = new Bootstrap_Service().getBootstrapServiceBeanPort(); 
			((StubExt) port).setConfigName("Standard WSSecurity Client");
			port.bootstrap();
		} catch (BootstrapServiceException_Exception e) {
			logger.error("unable to bootstrap factory", e);
		}
	}
	
	@Before
	public void setup() throws ProjectException_Exception {
        //Test if project exist
        try {
        	Project_Type project = projectService.getProject(PROJECT_PATH);
        	logger.debug("setup - Project " + project.getName() + "  exists");
        }
        catch (ProjectException_Exception e) {
        	// Create Project
        	logger.debug("setup - Project " + PROJECT_PATH + "  doesn't exist -> creation");
        	projectService.createProject(PROJECT_PATH, PROJECT_NAME, "description projet", "license projet");
        }
	}
	
	@After
	public void teardown() throws ProjectException_Exception, BugTrackerServiceException_Exception {
		
		//Delete issues
		IssueDtoArray issues = bugTrackerService.getAllIssues(PROJECT_PATH);
		logger.debug("teardown - delete " + issues.getItem().size() + " issues");
		for (IssueDto issue : issues.getItem()) {
			logger.debug("teardown -  delete issue " + issue.getNum() + " issues");
			bugTrackerService.deleteIssue(PROJECT_PATH, issue.getNum());
		}
		
		//Delete project
		Project_Type project = null;
        try {
        	project = projectService.getProject(PROJECT_PATH);
        }
        catch (ProjectException_Exception e) {
        	logger.debug("teardown - project " + PROJECT_PATH + " doesn't exist");
        }
        
        if (project != null) {
        	logger.debug("teardown - delete project " + PROJECT_PATH);
    		projectService.deleteProject(PROJECT_PATH);
        }
	}

    @Test
    public void testCRUD() {
        try {
            
            /*
             * Test getAllIssue no result
             */
            IssueDtoArray issues = bugTrackerService.getAllIssues(PROJECT_PATH);
            assertTrue(issues.getItem().isEmpty());
            
            /*
             * Test getIssue no result
             */
            IssueDto issue = null;
            try {
            	issue = bugTrackerService.getIssue(PROJECT_PATH, "1");
            	fail("An error should be occurred");
            }
            catch (BugTrackerServiceException_Exception e) {
            	//OK
            }
            
            /*
             * Create an issue
             */
            String idIssue = bugTrackerService.createIssue(PROJECT_PATH, createIssueDto("summary_test", "description_test"));
            
            assertTrue(idIssue.length() > 0);
            
            /*
             * Get An issue
             */
            issue = bugTrackerService.getIssue(PROJECT_PATH, idIssue);
            
            //Check result
            assertEquals(idIssue, issue.getNum());
            assertEquals(PROJECT_PATH, issue.getProjectPath());
            assertEquals(Utils.generatePathIssueFactory(PROJECT_PATH, idIssue), issue.getIssuePath());
            assertEquals("summary_test", issue.getSummary());
            assertEquals("description_test", issue.getDescription());
            long dateCreation = issue.getDateLastUpdate();
            assertTrue(issue.getDateLastUpdate() > 0);
            assertNotNull(issue.getPriority());
            assertNotNull(issue.getResolution());
            assertNotNull(issue.getSeverity());
            assertNotNull(issue.getStatus());
            assertEquals(USER_NAME_GUEST, issue.getReporter());
            assertNull(issue.getAssignee());
            assertNotNull(issue.getDateCreation());
            assertNotNull(issue.getDateModification());
            assertEquals(issue.getDateCreation(), issue.getDateModification());
            assertTrue(issue.getDateLastUpdate() > 0);
            
            /*
             * Get AllIssues (1 result)
             */
            issues = bugTrackerService.getAllIssues(PROJECT_PATH);
            assertEquals(1, issues.getItem().size());
            issue = issues.getItem().get(0);
            
            //Check result
            assertEquals(idIssue, issue.getNum());
            assertEquals(PROJECT_PATH, issue.getProjectPath());
            assertEquals(Utils.generatePathIssueFactory(PROJECT_PATH, idIssue), issue.getIssuePath());
            assertEquals("summary_test", issue.getSummary());
            assertEquals("description_test", issue.getDescription());
            assertTrue(issue.getDateLastUpdate() > 0);
            assertEquals(dateCreation, issue.getDateLastUpdate());
            assertNotNull(issue.getPriority());
            assertNotNull(issue.getResolution());
            assertNotNull(issue.getSeverity());
            assertNotNull(issue.getStatus());
            assertEquals(USER_NAME_GUEST, issue.getReporter());
            assertNull(issue.getAssignee());
            assertNotNull(issue.getDateCreation());
            assertNotNull(issue.getDateModification());
            assertTrue(issue.getDateLastUpdate() > 0);
            
            /*
             * Update Issue
             */
            issue.setDescription("description_test_modif");
            issue.setSummary("summary_test_modif");
            
            bugTrackerService.updateIssue(issue);
            
            //Check result
            issue = bugTrackerService.getIssue(PROJECT_PATH, idIssue);
            
            assertEquals(idIssue, issue.getNum());
            assertEquals(PROJECT_PATH, issue.getProjectPath());
            assertEquals(Utils.generatePathIssueFactory(PROJECT_PATH, idIssue), issue.getIssuePath());
            assertEquals("summary_test_modif", issue.getSummary());
            assertEquals("description_test_modif", issue.getDescription());
            assertTrue(issue.getDateLastUpdate() > dateCreation);
            assertNotNull(issue.getPriority());
            assertNotNull(issue.getResolution());
            assertNotNull(issue.getSeverity());
            assertNotNull(issue.getStatus());
            assertEquals(USER_NAME_GUEST, issue.getReporter());
            assertNull(issue.getAssignee());
            assertNotNull(issue.getDateCreation());
            assertNotNull(issue.getDateModification());
            assertTrue(issue.getDateCreation().compare(issue.getDateModification()) < 0);
            assertTrue(issue.getDateLastUpdate() > 0);
            
            /*
             * Delete issue
             */
            bugTrackerService.deleteIssue(PROJECT_PATH, idIssue);
            
            //Check result
            issues = bugTrackerService.getAllIssues(PROJECT_PATH);
            assertTrue(issues.getItem().isEmpty());
            
            /*
             * Get AllIssues (3 result)
             */
            Set<String> idsIssuesExpected = new HashSet<String>();
            String idIssue1 = bugTrackerService.createIssue(PROJECT_PATH, createIssueDto("summary_test_1", "description_test_1"));
            idsIssuesExpected.add(idIssue1);
            String idIssue2 = bugTrackerService.createIssue(PROJECT_PATH, createIssueDto("summary_test_2", "description_test_2"));
            idsIssuesExpected.add(idIssue2);
            String idIssue3 = bugTrackerService.createIssue(PROJECT_PATH, createIssueDto("summary_test_3", "description_test_3"));
            idsIssuesExpected.add(idIssue3);
            assertEquals(3, idsIssuesExpected.size());
            
            issues = bugTrackerService.getAllIssues(PROJECT_PATH);
            //Check results
            assertEquals(idsIssuesExpected.size(), issues.getItem().size());
            
            for (IssueDto issueDto : issues.getItem()) {
				assertTrue(idsIssuesExpected.contains(issueDto.getNum()));
			}
            
            /*
             * Get new Issues
             */
            IssueDto dto = bugTrackerService.getIssue(PROJECT_PATH, idIssue2);
            XMLGregorianCalendar dateCreationIssue2 = dto.getDateCreation();
            List<String> idsExpected = new ArrayList<String>();
            idsExpected.add(idIssue2);
            idsExpected.add(idIssue3);
            
            IssueDtoArray newIssues = bugTrackerService.getNewIssues(PROJECT_PATH, dateCreationIssue2);
            assertEquals(idsExpected.size(), newIssues.getItem().size());
            for (IssueDto issueDto : newIssues.getItem()) {
				assertTrue(idsExpected.contains(issueDto.getNum()));
			}
            
            /*
             * Get Modified Issue
             */
            //Update Issue1
            dto = bugTrackerService.getIssue(PROJECT_PATH, idIssue1);
            bugTrackerService.updateIssue(dto);
            
            //Date modification issue 3
            dto = bugTrackerService.getIssue(PROJECT_PATH, idIssue3);
            XMLGregorianCalendar dateModificationIssue3 = dto.getDateModification();
            idsExpected = new ArrayList<String>();
            idsExpected.add(idIssue1);
            idsExpected.add(idIssue3);
            
            IssueDtoArray modifiedIssues = bugTrackerService.getModifiedIssues(PROJECT_PATH, dateModificationIssue3);
            assertEquals(idsExpected.size(), modifiedIssues.getItem().size());
            for (IssueDto issueDto : modifiedIssues.getItem()) {
				assertTrue(idsExpected.contains(issueDto.getNum()));
			}
            
            
            /*
             * test getIssueAttributes
             */
            IssueAttributesDto attributes = bugTrackerService.getIssueAttributes();
        	
        	assertEquals(6, attributes.getPriorities().size());
        	assertEquals(9, attributes.getResolutions().size());
        	assertEquals(8, attributes.getSeverities().size());
        	assertEquals(7, attributes.getStatus().size());
            
        } catch (Exception e) {
        	logger.error("Error", e);
            fail(e.getMessage());
        }

    }
    
    /**
     * Create an issue
     * @param summary of the issue
     * @param description of the issue
     * @return
     */
    private IssueDto createIssueDto(String summary, String description) {
    	IssueDto dto = new IssueDto();
    	dto.setSummary(summary);
    	dto.setDescription(description);
    	return dto;
    }

}
