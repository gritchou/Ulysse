/*
 * Qualipso Funky Factory
 * Copyright (C) 2006-2010 INRIA
 * http://www.inria.fr - molli@loria.fr
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation. See the GNU
 * Lesser General Public License in LGPL.txt for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 */
package org.qualipso.factory.client.test.sb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.auth.callback.UsernamePasswordHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.client.test.AllTests;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.browser.BrowserService;
import org.qualipso.factory.browser.BrowserServiceException;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.BindingServiceException;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.indexing.IndexingService;
import org.qualipso.factory.indexing.IndexingServiceException;
import org.qualipso.factory.indexing.SearchResult;
import org.qualipso.factory.greeting.GreetingService;
import org.qualipso.factory.greeting.GreetingServiceException;
import org.qualipso.factory.bootstrap.BootstrapService;
import org.qualipso.factory.bootstrap.BootstrapServiceException;

/**
 * 
 * Functionnal tests for the indexing service bean
 * 
 * @author Benjamin Dreux / Nancy UHP
 * @author Anthony Claudot / Nancy UHP
 * @author Philippe Schmucker / Nancy UHP
 * @author cynthia FLORENTIN / Nancy UHP
 * 
 */
public class IndexingServiceSBTest{

	private static Log logger = LogFactory.getLog(IndexingServiceSBTest.class);
	private static Context context;
	private static LoginContext loginContext;
	private static IndexingService indexing;
	private static MembershipService membership;
	private static GreetingService greeting; 
	private static BindingService binding;
	private static BrowserService browser;
	private FactoryResourceIdentifier friB, friF, friFB;
    private String profilePath;


	/**
	 * Set up service for all tests and log in as kermit thefrog.
	 * 
	 * @throws NamingException exception thrown when a naming problem occurs
	 * @throws LoginException exception thrown when the login fails
	 * @throws MembershipServiceException When the creation of profile toto fails, a MembershipServiceException will be thrown
	 */
	@BeforeClass
	public static void before() throws NamingException, LoginException{
		try {
			logger.debug("jaas config file path : " + ClassLoader.getSystemResource("jaas.config").getPath());
			System.setProperty("java.security.auth.login.config", ClassLoader.getSystemResource("jaas.config").getPath());
		} catch (Exception e) {
			logger.error("unable to load local jaas.config file");
		}

		Properties properties = new Properties();
		properties.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
		properties.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
		properties.put("java.naming.provider.url", "localhost:1099");
		context = new InitialContext(properties);

		BootstrapService bootstrap = (BootstrapService) context.lookup(FactoryNamingConvention.getJNDINameForService("bootstrap"));
		try {
			bootstrap.bootstrap();
		} catch (BootstrapServiceException e) {
			logger.error(e);
		}

		UsernamePasswordHandler uph = new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS); 
		loginContext = new LoginContext("qualipso", uph);
        //we need to login in just to delete things created by the quest user
        loginContext.login();

		indexing = (IndexingService) context.lookup(FactoryNamingConvention.getJNDINameForService(IndexingService.SERVICE_NAME));
		//binding = (BindingService) context.lookup(BindingService.SERVICE_NAME);
		browser = (BrowserService) context.lookup(FactoryNamingConvention.getJNDINameForService(BrowserService.SERVICE_NAME));
		greeting=(GreetingService) context.lookup(FactoryNamingConvention.getJNDINameForService(GreetingService.SERVICE_NAME));
		membership = (MembershipService) context.lookup(FactoryNamingConvention.getJNDINameForService(MembershipService.SERVICE_NAME));
	

	}

	/**
	 * Log out, and close the context
	 * 
	 * @throws NamingException
	 * @throws LoginException exception thrown when the logout fails
	 * @throws MembershipServiceException When the deletion of profile toto fails, a MembershipServiceException will be thrown
	 */
	@AfterClass
	public static void after() throws LoginException, NamingException, MembershipServiceException {
		

		loginContext.logout();
		context.close();
	}

	/**
	 * Set up the context before each test. We create 4 resources Name and get their FactoryResourceIdentifier,
	 * and create profile toto.
	 * @throws NamingException
	 * @throws FactoryException
	 * @throws InterruptedException
	 */
	@Before
	public void setUp() throws NamingException, FactoryException, InterruptedException {
	    //TODO create toto profile with root user
		//membership.createProfile("toto", "toto titi", "toto@gmail.com", 0);

        profilePath = membership.getProfilePathForConnectedIdentifier()+"/";
		greeting.createName(profilePath+"bug", "bug");
		greeting.createName(profilePath+"forge", "forge");
		greeting.createName(profilePath+"tm", "tm");
		greeting.createName(profilePath+"forge_bug", "forge_bug");
		
		// Waiting 1 second for the asynchronous call of the indexation
		Thread.sleep(1000);
		friB =  browser.findResource(profilePath+"bug").getFactoryResourceIdentifier();
		friF =  browser.findResource(profilePath+"forge").getFactoryResourceIdentifier();
		friFB = browser.findResource(profilePath+"forge_bug").getFactoryResourceIdentifier();
	}
	
	/**
	 * After each test, we delete these 4 resources, and profile toto.
	 * 
	 * @throws MembershipServiceException
	 * @throws GreetingServiceException
	 */
	@After
	public void tearDown() throws MembershipServiceException, GreetingServiceException{

    	//membership.deleteProfile("toto");
    //name may have been deleted by the test
    try{ greeting.deleteName(profilePath+"bug");}catch(GreetingServiceException e){}
	try{ greeting.deleteName(profilePath+"forge");}catch(GreetingServiceException e){}
    try{ greeting.deleteName(profilePath+"tm");}catch(GreetingServiceException e){}
	try{ greeting.deleteName(profilePath+"forge_bug");}catch(GreetingServiceException e){}   


	}

/* ****************************** TESTS ****************************** */
	/**
	 * ==== RIGHT ====
	 * Test of simple case. 
	 * @throws IndexingServiceException 
	 */
	@Test
	public void testIndexingSearch() throws IndexingServiceException{
		logger.debug("Testing search of an owned resource");
		ArrayList<SearchResult> result = indexing.search("bug");
		
		assertEquals("The ArrayList should contain exactly one result", 1, result.size());
		assertEquals("The expected result should be the resource BUG ", friB, result.get(0).getResourceIdentifier());
	}
	
	/**
	 * ===== RIGHT =====
	 * Test if a resource can be found by the resource owner
	 * 
	 * @throws IndexingServiceException exception thrown when a problem occurs in the search method
	 */
	@Test
	public void testIndexingSearchOwnedResource() throws IndexingServiceException{
		logger.debug("Testing search of an owned resource");
		ArrayList<SearchResult> result = indexing.search("bug AND forge");
		
		assertEquals("The ArrayList should contain exactly one result", 1, result.size());
		assertEquals("The expected result should be the resource BUG and FORGE", friFB, result.get(0).getResourceIdentifier());
	}
	
	/**
	 * ===== BOUNDARIE =====
	 * Test if a client who doesn't have the right to read the resource can't find it
	 * 
	 * @throws InvalidPathException
	 * @throws PathNotFoundException
	 * @throws BindingServiceException
	 * @throws IndexingServiceException exception thrown when a problem occurs in the search method
	 */
	@Test
	public void testIndexingSearchReadNotAllowedResource() throws InvalidPathException, PathNotFoundException, BindingServiceException, IndexingServiceException{
		logger.debug("Testing search of a resource on which we don't have the right to read");
		String policy = PAPServiceHelper.buildPolicy("1", "/profiles/kermit", "/profiles/kermit/friFB", new String[]{""});
		//binding.setProperty("/profiles/kermit/friFB",FactoryResourceProperty.POLICY_ID, policy);
		ArrayList<SearchResult> result = indexing.search("bug AND forge");
		
		assertEquals("The ArrayList should be empty", 0, result.size());
		
		//binding.setProperty("/profiles/resource",FactoryResourceProperty.OWNER, "/profiles/kermit");
	}
	
	/**
	 * ===== BOUNDARIE =====
	 * Test if a client who has the right to read the resource can find it. This method give the ownership to user toto,
	 * and keep the read right for kermit.
	 * 
	 * @throws InvalidPathException
	 * @throws PathNotFoundException
	 * @throws BindingServiceException
	 * @throws IndexingServiceException exception thrown when a problem occurs in the search method
	 */
	@Test
	public void testIndexingSearchReadableResource() throws InvalidPathException, PathNotFoundException, BindingServiceException, IndexingServiceException{
		logger.debug("Testing search of a readable resource");
		String policy = PAPServiceHelper.buildPolicy("1", profilePath+"kermit", profilePath+"friFB", new String[]{"read"});
		//binding.setProperty("/profiles/kermit/friFB",FactoryResourceProperty.OWNER, "/profiles/toto");
		//binding.setProperty("/profiles/kermit/friFB",FactoryResourceProperty.POLICY_ID, policy);
		ArrayList<SearchResult> result = indexing.search("bug AND forge");
		
		assertEquals("The ArrayList should contain exactly one result", 1, result.size());
		assertEquals("The expected result should be the resource BUG and FORGE", friFB, result.get(0).getResourceIdentifier());
	}
	
	/**
	 * ===== BOUNDARIE =====
	 * test if an inexistent resource can't be found by the caller
	 * 
	 * @throws IndexingServiceException exception thrown when a problem occurs in the search method
	 */
	@Test
	public void testIndexingSearchInexistantResource() throws IndexingServiceException {
		logger.debug("Testing search of an inexistant resource");
		ArrayList<SearchResult> result = indexing.search("titi");
		
		assertEquals("The ArrayList should be empty", 0, result.size());
	}
	
	/**
	 * ===== BOUNDARIE =====
	 * Test if resource can be found with just a part of the content
	 * 
	 * @throws IndexingServiceException exception thrown when a problem occurs in the search method
	 */
	@Test
	public void testIndexingSearchHalfContent() throws IndexingServiceException {
		logger.debug("Testing search of a resource with one keyword");
		ArrayList<SearchResult> result = indexing.search("bug");
		// build the list of FactoryResourceIdentifier from the SearchResult list
		ArrayList<FactoryResourceIdentifier> resources = new ArrayList<FactoryResourceIdentifier>();
		for (SearchResult res : result){
			resources.add(res.getResourceIdentifier());
		}
		
		assertEquals("The ArrayList should contain exactly two results", 2, resources.size());
		assertTrue("The ArrayList should contain the resource BUG", resources.contains(friB));
		assertTrue("The ArrayList should contain the resource BUG and FORGE", resources.contains(friFB));
	}

	/**
	 * ====== RIGHT =====
	 * Test if resource can be found with just a part or an other of the content
	 * 
	 * @throws IndexingServiceException exception thrown when a problem occurs in the search method
	 */
	@Test
	public void testIndexingSearchOr() throws IndexingServiceException {
		logger.debug("Testing search with operator OR");
		ArrayList<SearchResult> result = indexing.search("bug OR forge");
		// build the list of FactoryResourceIdentifier from the SearchResult list
		ArrayList<FactoryResourceIdentifier> resources = new ArrayList<FactoryResourceIdentifier>();
		for (SearchResult res : result){
			resources.add(res.getResourceIdentifier());
		}
		
		assertEquals("The ArrayList should contains exactly three results", 3, resources.size());
		assertTrue("The ArrayList should contain the resource BUG", resources.contains(friB));
		assertTrue("The ArrayList should contain the resource FORGE", resources.contains(friF));
		assertTrue("The ArrayList should contain the resource BUG and FORGE", resources.contains(friFB));
	}

	/**
	 * ======= Right ======
	 * Test if resource can't be found with negation of the content
	 * 
	 * @throws IndexingServiceException exception thrown when a problem occurs in the search method
	 */
	@Test
	public void testIndexingNotContent() throws IndexingServiceException{
		logger.debug("Testing search with operator NOT");
		ArrayList<SearchResult> result = indexing.search("NOT bug");
		// build the list of FactoryResourceIdentifier from the SearchResult list
		ArrayList<FactoryResourceIdentifier> resources = new ArrayList<FactoryResourceIdentifier>();
		for (SearchResult res : result){
			resources.add(res.getResourceIdentifier());
		}
		
		assertEquals("The ArrayList should contains exactly three results", 2, resources.size());
		assertFalse("The ArrayList should not contain the resource BUG", resources.contains(friB));
		assertFalse("The ArrayList should not contain the resource BUG and FORGE", resources.contains(friFB));
	}
	
	/** 
	 * ====== RIGHT ======
	 * Test update of index when update of the resource occurs
	 * 
	 * @throws GreetingServiceException
	 * @throws IndexingServiceException exception thrown when a problem occurs in the search method
	 * @throws InterruptedException
	 */
	@Test
	public void testUpdateIndex() throws GreetingServiceException, IndexingServiceException, InterruptedException {
		logger.debug("Testing update index");
		greeting.updateName(profilePath+"forge", "egrof");
		// Waiting 1 second for the asynchronous call of the reindexation
		Thread.sleep(1000);
		ArrayList<SearchResult> result = indexing.search("egrof");
		
		assertEquals("The ArrayList should contains exactly one result", 1, result.size());
		assertEquals("The expected result should be the resource FORGE", friF, result.get(0).getResourceIdentifier());
	}
	
	/**
	 * ====== RIGHT =====
	 * Test search of deleted resource
	 * 
	 * @throws GreetingServiceException
	 * @throws IndexingServiceException exception thrown when a problem occurs in the search method
	 * @throws InterruptedException
	 */
	@Test
	public void testDeletedSearch() throws GreetingServiceException, IndexingServiceException, InterruptedException{
		logger.debug("Testing delete index");

		greeting.deleteName(profilePath+"forge");

		// Waiting 1 second for the asynchronous call of the deletion in index
		Thread.sleep(1000);
		ArrayList<SearchResult> result = indexing.search("forge");
        ArrayList<FactoryResourceIdentifier> resources = new ArrayList<FactoryResourceIdentifier>();
        for (SearchResult res : result){
            resources.add(res.getResourceIdentifier());
        }
		
		assertEquals("The ArrayList should contain exactly one result", 1, resources.size());
		assertFalse("The ArrayList should not contain the resource FORGE", resources.contains(friF));
        
	}
	
}

