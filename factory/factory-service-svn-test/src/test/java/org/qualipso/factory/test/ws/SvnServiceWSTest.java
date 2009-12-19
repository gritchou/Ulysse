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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.ws.core.StubExt;
import org.junit.BeforeClass;
import org.qualipso.factory.svn.client.ws.Browser;
import org.qualipso.factory.svn.client.ws.Browser_Service;
import org.qualipso.factory.svn.client.ws.Project;
import org.qualipso.factory.svn.client.ws.ProjectServiceException_Exception;
import org.qualipso.factory.svn.client.ws.Project_Service;
import org.qualipso.factory.svn.client.ws.Project_Type;
import org.qualipso.factory.svn.client.ws.SVNServiceException_Exception;
import org.qualipso.factory.svn.client.ws.Svn;
import org.qualipso.factory.svn.client.ws.SvnRepository;
import org.qualipso.factory.svn.client.ws.Svn_Service;
import org.qualipso.factory.svn.utils.FilterUtils;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNCommitPacket;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNMoveClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * 
 * BugTracker WS test
 *
 */
public class SvnServiceWSTest extends TestCase{
	
	
	/**
	 * project name
	 */
	private static final String PROJECT_NAME = "junitproject_ws";
	
	/**
	 * project path
	 */
	private static final String PROJECT_PATH = "/profiles/guest/" + PROJECT_NAME;
	
	
	/**
	 * Path of test repository
	 */
	private static final String PATH_REPOS = PROJECT_PATH + "/" + "repostest";
	
	//************************
	private static final String SSHD_HOSTNAME = "127.0.0.1";
	private static final int SSHD_PORT = 3333;
	private static final String USERNAME = "guest";
	private static final String PASSWD = "guest";
	private File workingDir;
	//************************
	
	/**
	 * ProjectService
	 */
	private Project projectService;
	
	/**
	 * SvnService
	 */
	private Svn svnService;
	
	/**
	 * BrowserService
	 */
	private Browser browserService;
	
	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(SvnServiceWSTest.class);
	
	/**
	 * Constructor
	 */
	public SvnServiceWSTest() {
		Svn_Service serviceSvn = new Svn_Service();
		svnService = serviceSvn.getSVNServiceBeanPort();
		((StubExt) this.svnService).setConfigName("Standard WSSecurity Client");
    	
    	Project_Service serviceProject = new Project_Service();
    	this.projectService = serviceProject.getProjectServiceBeanPort();
    	((StubExt) this.projectService).setConfigName("Standard WSSecurity Client");
    	
    	Browser_Service serviceBrowser = new Browser_Service();
    	this.browserService = serviceBrowser.getBrowserServiceBeanPort();
    	((StubExt) this.browserService).setConfigName("Standard WSSecurity Client");
	}
	
	/**
	 * Init services
	 */
	@BeforeClass
	public static void init() {
		/*try {
			Bootstrap port = new Bootstrap_Service().getBootstrapServiceBeanPort(); 
			((StubExt) port).setConfigName("Standard WSSecurity Client");
			port.bootstrap();
		} catch (BootstrapServiceException_Exception e) {
			logger.error("unable to bootstrap factory", e);
		}*/
	}
	
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() throws ProjectServiceException_Exception {
		logger.info("SETUP - begin");
    	// create a working directory
		workingDir = new File("target/local");
		workingDir.mkdirs();
		init();
		
        //Test if project exist
        try {
        	Project_Type project = projectService.getProject(PROJECT_PATH);
        	logger.debug("setup - Project " + project.getName() + "  exists");
        	try {
        		svnService.deleteSVNRepository(PATH_REPOS);
        	}
        	catch (SVNServiceException_Exception e) {
        		//OK
        	}
        }
        catch (ProjectServiceException_Exception e) {
        	// Create Project
        	logger.debug("setup - Project " + PROJECT_PATH + "  doesn't exist -> creation");
        	projectService.createProject(PROJECT_PATH, PROJECT_NAME, "description projet", "license projet");
        }
        logger.info("SETUP - end");
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() throws ProjectServiceException_Exception {
		logger.info("TEARDOWN - begin");
		//Delete project
		Project_Type project = null;
        try {
        	project = projectService.getProject(PROJECT_PATH);
        }
        catch (ProjectServiceException_Exception e) {
        	logger.debug("teardown - project " + PROJECT_PATH + " doesn't exist");
        }
        
        //Delete workfolder
        if (workingDir.exists()) {
        	deleteFolderRecursively(workingDir);
        }
        
        if (project != null) {
        	try {
        		//svnService.readSVNRepository(PATH_REPOS);
        		//logger.debug("TEARDOWN 5");
        		svnService.deleteSVNRepository(PATH_REPOS);
        	}
        	catch (SVNServiceException_Exception e) {
        		//OK
        	}
        	
        	logger.debug("teardown - delete project " + PROJECT_PATH);
    		projectService.deleteProject(PROJECT_PATH);
        }
        logger.info("TEARDOWN - end");
	}
	
	
	/**
	 * Test CRUD for SVNRepository
	 * @throws Exception if an error occurred
	 */
	public void testCRUDRepository() throws Exception {
		logger.info("testCRUDRepository");
		/*
		 * Create repository
		 */
		logger.debug("Create repository");
		String idRepos = svnService.createSVNRepository(PATH_REPOS, "reposName", "description repo");
		assertNotNull(idRepos);
		
		/*
		 * Read repository
		 */
		logger.debug("Read repository");
		SvnRepository repos = svnService.readSVNRepository(PATH_REPOS);
		assertEquals(idRepos, repos.getId());
		assertEquals("reposName", repos.getName());
		assertEquals("description repo", repos.getDescription());
		assertEquals(PATH_REPOS, repos.getPath());
		
		/*
		 * Update repository
		 */
		logger.debug("Update repository");
		svnService.updateSVNRepository(PATH_REPOS, "reposName2", "description repo2");
		repos = svnService.readSVNRepository(PATH_REPOS);
		assertEquals(idRepos, repos.getId());
		assertEquals("reposName2", repos.getName());
		assertEquals("description repo2", repos.getDescription());
		assertEquals(PATH_REPOS, repos.getPath());
		
		/*
		 * Delete repository
		 */
		logger.debug("Delete repository");
		svnService.deleteSVNRepository(PATH_REPOS);
		try {
			repos = svnService.readSVNRepository(PATH_REPOS);
			fail("SVNServiceException_Exception should be occurred");
		}
		catch (SVNServiceException_Exception e) {
			//OK
		}
	}
    
    public void testSVNOperation() throws Exception {
    	
    	/*
    	 * Test create a repository
    	 */
    	String idRepos = svnService.createSVNRepository(PATH_REPOS, "reposName", "description repo");
    	//check result
    	assertFalse(StringUtils.isEmpty(idRepos));
    	
		logger.debug("#### IMPORT ####");
		// Create a local svn workspace
		File workFolder1 = new File(workingDir, "testdir");
		workFolder1.mkdir();

		// Create some files and commit locally
		File file11 = new File(workFolder1, "testfile1.txt");
		FileOutputStream fos11 = new FileOutputStream(file11);
		fos11.write("Youhou".getBytes());
		fos11.flush();
		fos11.close();
		
		
		File subFolder = new File(workFolder1, "subdir");
		subFolder.mkdir();
		
		File file12 = new File(subFolder, "testfile2.txt");
		FileOutputStream fos12 = new FileOutputStream(file12);
		fos12.write("Youhou2".getBytes());
		fos12.flush();
		fos12.close();
		
		File subFolder2 = new File(subFolder, "subdir2");
		subFolder2.mkdir();
		
		File file3 = new File(subFolder2, "testfile3.txt");
		FileOutputStream fos3 = new FileOutputStream(file3);
		fos3.write("Youhou3".getBytes());
		fos3.flush();
		fos3.close();
		

		SVNRepositoryFactoryImpl.setup();
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		ISVNAuthenticationManager authManager = new BasicAuthenticationManager(USERNAME, PASSWD);
		SVNClientManager svnClientManager = SVNClientManager.newInstance(options, authManager);

		SVNURL dstURL = SVNURL.parseURIDecoded("svn+ssh://" + USERNAME + "@" + SSHD_HOSTNAME + ":" + SSHD_PORT
				+ "/" + idRepos);
		
		
		SVNCommitClient clientCommit = svnClientManager.getCommitClient();
		SVNCommitInfo infos = clientCommit.doImport(workFolder1, dstURL, "first import", null, false, false,
				SVNDepth.INFINITY);

		assertTrue("HEAD revision should be 1 but is " + infos.getNewRevision(), infos.getNewRevision() == 1);

		//Check resource
		checkFactoryResourcesExist("testfile1.txt", 0);
		checkFactoryResourcesExist("subdir", 2);
		checkFactoryResourcesExist("subdir/testfile2.txt", 0);
		
		//SVNResource depth = 2 -> testfile3.txt does't exist
		checkFactoryResourcesExist("subdir/subdir2", 0);
		
		logger.debug("#### CHECKOUT 1 ####");
		
		SVNUpdateClient clientUpdate = svnClientManager.getUpdateClient();
		
		
		File workFolderCheckOut1 = new File(workingDir, "testdirCO1");
		workFolderCheckOut1.mkdir();
		clientUpdate.doCheckout(dstURL, workFolderCheckOut1, SVNRevision.create(0), SVNRevision.create(1), null, true);
		
		//check
		//check
		checkFileExist(workFolderCheckOut1, "testfile1.txt", false);
		checkFileExist(workFolderCheckOut1, "subdir", true);
		checkFileExist(workFolderCheckOut1, "subdir/testfile2.txt", false);
		checkFileExist(workFolderCheckOut1, "subdir/subdir2", true);
		checkFileExist(workFolderCheckOut1, "subdir/subdir2/testfile3.txt", false);
		
		
		
		logger.debug("#### CHECKOUT 2 ####");
		
		File workFolderCheckOut2 = new File(workingDir, "testdirCO2");
		workFolderCheckOut2.mkdir();
		clientUpdate.doCheckout(dstURL, workFolderCheckOut2, SVNRevision.create(0), SVNRevision.create(1), null, true);
		
		//check
		checkFileExist(workFolderCheckOut2, "testfile1.txt", false);
		checkFileExist(workFolderCheckOut2, "subdir", true);
		checkFileExist(workFolderCheckOut2, "subdir/testfile2.txt", false);
		checkFileExist(workFolderCheckOut2, "subdir/subdir2", true);
		checkFileExist(workFolderCheckOut2, "subdir/subdir2/testfile3.txt", false);

		
		logger.debug("#### MODIFY subdir/testfile2.txt CO1 ####");
		
		// modify the first checkout
		File file = new File(workFolderCheckOut1 , "subdir/testfile2.txt");
		FileOutputStream fos = new FileOutputStream(file);
		fos.write("Youhou2 modif".getBytes());
		fos.flush();
		fos.close();
		
		//commit the first checkout
		logger.debug("#### COMMIT subdir/testfile2.txt CO1 ####");
		File[] files = new File[1];
		File fileToCommit = new File(workFolderCheckOut1 , "subdir/testfile2.txt");
		files[0] = fileToCommit;
		SVNCommitPacket packet = clientCommit.doCollectCommitItems(files, false, true, SVNDepth.INFINITY, null);
		clientCommit.doCommit(packet, false, "first commit");
		
		
		
		
		
		logger.debug("#### DIFF CO2 ####");
		SVNDiffClient clientDiff = svnClientManager.getDiffClient();
		OutputStream result = new ByteArrayOutputStream();
		Collection col = new ArrayList();
		clientDiff.doDiff(dstURL, SVNRevision.create(1), workFolderCheckOut2, SVNRevision.create(0), SVNDepth.INFINITY, true, result, col);
		
		
		logger.debug("#### UPDATE CO2####");
		clientUpdate.doUpdate(workFolderCheckOut2, SVNRevision.create(0), SVNDepth.INFINITY, true, false);
		
		
		logger.debug("#### MOVE ####");
		SVNMoveClient clientMove = svnClientManager.getMoveClient();
		File fileOrig = new File(workFolderCheckOut1, "testfile1.txt");
		File fileDest = new File(workFolderCheckOut1, "testfile1bis.txt");
		clientMove.doMove(fileOrig, fileDest);
		files = new File[1];
		files[0] = workFolderCheckOut1;
		packet = clientCommit.doCollectCommitItems(files, false, true, SVNDepth.INFINITY, null);
		SVNCommitInfo info = clientCommit.doCommit(packet, false, "second commit");
		//Check checkout
		checkFileExist(workFolderCheckOut1, "testfile1bis.txt", false);
		checkFileExist(workFolderCheckOut1, "subdir", true);
		checkFileExist(workFolderCheckOut1, "subdir/testfile2.txt", false);
		checkFileExist(workFolderCheckOut1, "subdir/subdir2", true);
		checkFileExist(workFolderCheckOut1, "subdir/subdir2/testfile3.txt", false);
		
		//Check resource
		checkFactoryResourcesExist("testfile1bis.txt", 0);
		checkFactoryResourcesExist("subdir", 2);
		checkFactoryResourcesExist("subdir/testfile2.txt", 0);
		//SVNResource depth = 2 -> testfile3.txt does't exist
		checkFactoryResourcesExist("subdir/subdir2", 0);
		
		/*
		logger.debug("#### DELETE ####");
		SVNURL dstURLToDelete = SVNURL.parseURIDecoded("svn+ssh://" + USERNAME + "@" + SSHD_HOSTNAME + ":" + SSHD_PORT
				+ "/" + idRepos + "/subdir/subdir2/testfile3.txt");

		SVNURL[] urls = new SVNURL[] {
				dstURLToDelete
		};
		
		info = clientCommit.doDelete(urls, "commitDelete", null);
		
		//Check resource
		checkFactoryResourcesExist("testfile1bis.txt", 0);
		checkFactoryResourcesExist("subdir", 2);
		checkFactoryResourcesExist("subdir/testfile2.txt", 0);
		checkFactoryResourcesExist("subdir/subdir2", 0);
		
		logger.debug("#### UPDATE CO2####");
		clientUpdate.doUpdate(workFolderCheckOut2, SVNRevision.create(info.getNewRevision()), SVNDepth.INFINITY, true, false);
		
		
		checkFileExist(workFolderCheckOut2, "testfile1bis.txt", false);
		checkFileExist(workFolderCheckOut2, "subdir", true);
		checkFileExist(workFolderCheckOut2, "subdir/testfile2.txt", false);
		checkFileExist(workFolderCheckOut2, "subdir/subdir2", true);
		checkFileNotExist(workFolderCheckOut2, "subdir/subdir2/testfile3.txt");
		
		
		logger.debug("#### DELETE 2 ####");
		logger.debug("#### ERROR 2 ####");
		dstURLToDelete = SVNURL.parseURIDecoded("svn+ssh://" + "root" + "@" + SSHD_HOSTNAME + ":" + SSHD_PORT
				+ "/" + idRepos + "/subdir/subdir2");

		authManager = new BasicAuthenticationManager("kermit", "thefrog");
		svnClientManager = SVNClientManager.newInstance(options, authManager);
		clientCommit = svnClientManager.getCommitClient();
		
		urls = new SVNURL[] {
				dstURLToDelete
		};
		
		info = clientCommit.doDelete(urls, "commitDelete2");
		
		System.out.println("getErrorMessage=" + info.getErrorMessage());
		
		//Check resource (no change, /subdir/subdir2/testfile3.txt not bound in the factory)
		checkFactoryResourcesExist("testfile1bis.txt", 0);
		
		logger.debug("#### UPDATE CO2####");
		clientUpdate.doUpdate(workFolderCheckOut2, SVNRevision.create(info.getNewRevision()), SVNDepth.INFINITY, true, false);
		
		checkFileExist(workFolderCheckOut2, "testfile1bis.txt", false);
		checkFileNotExist(workFolderCheckOut2, "subdir");
		checkFileNotExist(workFolderCheckOut2, "subdir/testfile2.txt");
		checkFileNotExist(workFolderCheckOut2, "subdir/subdir2");
		checkFileNotExist(workFolderCheckOut2, "subdir/subdir2/testfile3.txt");
		
		
		logger.debug("#### ERROR 2 ####");
		*/
    }
    
	private void deleteFolderRecursively(File folder) {
		if (folder != null) {
			for (File file : folder.listFiles()) {
				if (file.isDirectory()) {
					deleteFolderRecursively(file);
				} else {
					file.delete();
				}
			}
			folder.delete();
		}
	}
	
	private void checkFactoryResourcesExist(String factoryResourcesInFactory, int nbChildren) throws Exception{
		String[] partsPath = FilterUtils.splitPath(factoryResourcesInFactory);
		
		StringBuilder relatifPath = new StringBuilder();
		for (String part : partsPath) {
			relatifPath.append("/");
			relatifPath.append(part.hashCode());
		}
		
		assertEquals(factoryResourcesInFactory, nbChildren, browserService.listChildren(PATH_REPOS + relatifPath.toString()).getItem().size());
		
	}
	
	
	private void checkFileExist(File parentPath, String filename, boolean isDirectory) {
		File f = new File(parentPath, filename);
		assertTrue(f.exists());
		
		if (isDirectory) {
			assertTrue(f.isDirectory());
		}
		else {
			assertTrue(f.isFile());
		}
	}
	
	private void checkFileNotExist(File parentPath, String filename) {
		File f = new File(parentPath, filename);
		assertFalse(f.exists());
	}
}
