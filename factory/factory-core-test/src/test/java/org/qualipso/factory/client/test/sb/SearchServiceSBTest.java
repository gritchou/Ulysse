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
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.qualipso.factory.client.test.matcher.SearchResultWithFactoryResourceIdentfier.searchResultWithFactoryResourceIdentifier;

import java.util.ArrayList;

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
import org.qualipso.factory.Factory;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.bootstrap.BootstrapService;
import org.qualipso.factory.bootstrap.BootstrapServiceException;
import org.qualipso.factory.browser.BrowserService;
import org.qualipso.factory.client.test.AllTests;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.greeting.GreetingServiceException;
import org.qualipso.factory.indexing.SearchResult;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.search.SearchService;
import org.qualipso.factory.search.SearchServiceException;
import org.qualipso.factory.security.SecurityService;
import org.qualipso.factory.security.SecurityServiceException;

/**
 * 
 * Functionnal tests for the search-indexing service bean
 * 
 * @author Benjamin Dreux / Nancy UHP
 * @author Anthony Claudot / Nancy UHP
 * @author Philippe Schmucker / Nancy UHP
 * @author Cynthia FLORENTIN / Nancy UHP
 * 
 */
public class SearchServiceSBTest {

    private static Log logger = LogFactory.getLog(SearchServiceSBTest.class);
    private static LoginContext loginContextRoot;
    private static LoginContext loginContextKermit;
    private static SearchService search;
    private static MembershipService membership;
    private static CoreService core;
    private static SecurityService security;
    private static BrowserService browser;
    private static FactoryResourceIdentifier friB, friF, friFB;
    private static int waitingTime = 2000;

    /**
     * Set up service for all tests and log in as kermit thefrog.
     * 
     * @throws NamingException
     *             exception thrown when a naming problem occurs
     * @throws LoginException
     *             exception thrown when the login fails
     * @throws MembershipServiceException
     *             When the creation of profile toto fails, a
     *             MembershipServiceException will be thrown
     * @throws SecurityServiceException
     */
    @BeforeClass
    public static void before() throws Exception {
        try {
            logger.debug("jaas config file path : " + ClassLoader.getSystemResource("jaas.config").getPath());
            System.setProperty("java.security.auth.login.config", ClassLoader.getSystemResource("jaas.config").getPath());
        } catch (Exception e) {
            logger.error("unable to load local jaas.config file");
        }

        BootstrapService bootstrap = (BootstrapService) Factory.findService(BootstrapService.SERVICE_NAME);
        try {
            bootstrap.bootstrap();
        } catch (BootstrapServiceException e) {
            logger.error(e);
        }

        UsernamePasswordHandler uphRoot = new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS);
        loginContextRoot = new LoginContext("qualipso", uphRoot);
        UsernamePasswordHandler uphKermit = new UsernamePasswordHandler("kermit", "thefrog");
        loginContextKermit = new LoginContext("qualipso", uphKermit);

        search = (SearchService) Factory.findService(SearchService.SERVICE_NAME);
        security = (SecurityService) Factory.findService(SecurityService.SERVICE_NAME);
        browser = (BrowserService) Factory.findService(BrowserService.SERVICE_NAME);
        core = (CoreService) Factory.findService(CoreService.SERVICE_NAME);
        membership = (MembershipService) Factory.findService(MembershipService.SERVICE_NAME);

        //Create a profile for user kermit
        membership.createProfile("kermit", "Kermit", "THE-FROG", 0);
        logger.debug("kermit profile created");
        
        //Create a folder for all test resources (as root)
        loginContextRoot.login();
        
        core.createFolder("/indexingtests", "Indexing Tests", "A folder to create dedicated resource in indexing test");
        security.addSecurityRule("/indexingtests", "/profiles/kermit", "create,read,update,delete");
        
        loginContextRoot.logout();

    }

    /**
     * Log out, and close the context
     * 
     * @throws NamingException
     * @throws SecurityServiceException
     * @throws LoginException
     *             exception thrown when the logout fails
     * @throws MembershipServiceException
     *             When the deletion of profile toto fails, a
     *             MembershipServiceException will be thrown
     */
    @AfterClass
    public static void after() throws Exception {
        loginContextRoot.login();
        
    	core.deleteFolder("/indexingtests");
        membership.deleteProfile("/profiles/kermit");
        
        loginContextRoot.logout();
    }
    
    @Before
    public void setup() throws Exception {
    	loginContextKermit.login();

    	core.createFolder("/indexingtests/bug", "bug", "a bug folder");
        core.createFolder("/indexingtests/forge", "forge", "a forge folder");
        core.createFolder("/indexingtests/forge_bug", "forge_bug", "a forge_bug folder");

        friB = browser.findResource("/indexingtests/bug").getFactoryResourceIdentifier();
        friF = browser.findResource("/indexingtests/forge").getFactoryResourceIdentifier();
        friFB = browser.findResource("/indexingtests/forge_bug").getFactoryResourceIdentifier();
        
        Thread.sleep(waitingTime);
        
        loginContextKermit.logout();
    }
    
    @After
    public void teardown() throws Exception {
    	loginContextRoot.login();
        
    	core.deleteFolder("/indexingtests/bug");
    	core.deleteFolder("/indexingtests/forge");
    	core.deleteFolder("/indexingtests/forge_bug");
    	
        loginContextRoot.logout();
    }

    /*   ****************************** TESTS ****************************** */
    /**
     * ==== RIGHT ==== Test of simple case.
     * 
     * @throws SearchServiceException
     */
    @Test
    public void testSearch() throws Exception {
        logger.info("Testing search of an owned resource");
        loginContextKermit.login();
        ArrayList<SearchResult> result = search.searchResource("bug");

        assertEquals("The ArrayList should contain exactly two results", 2, result.size());

        assertThat("The expected result should be the resource BUG", result, hasItem(searchResultWithFactoryResourceIdentifier(friB)));
        assertThat("The expected result should be the resource FORGE_BUG", result, hasItem(searchResultWithFactoryResourceIdentifier(friFB)));
        loginContextKermit.logout();
    }

    /**
     * ===== RIGHT ===== Test if a resource can be found by the resource owner
     * 
     * @throws SearchServiceException
     *             exception thrown when a problem occurs in the search method
     * @throws LoginException
     * @SecurityServiceException
     */
    @Test
    public void testSearchOwnedResource() throws Exception {
        logger.info("Testing search of an owned resource");
        loginContextKermit.login();

        ArrayList<SearchResult> result = search.searchResource("bug AND forge");

        assertEquals("The ArrayList should contain exactly one result", 1, result.size());
        assertThat("The expected result should be the resource FORGE_BUG", result, hasItem(searchResultWithFactoryResourceIdentifier(friFB)));

        loginContextKermit.logout();
    }

    /**
     * ===== BOUNDARIE ===== Test if a client who doesn't have the right to read
     * the resource can't find it
     * 
     * @throws SearchServiceException
     *             exception thrown when a problem occurs in the search method
     * @throws LoginException
     */
    @Test
    public void testSearchReadNotAllowedResource() throws Exception {
        logger.info("Testing search of a resource on which we don't have the right to read");

        ArrayList<SearchResult> result = search.searchResource("bug AND forge");

        assertEquals("The ArrayList should be empty", 0, result.size());

    }

    /**
     * ===== BOUNDARIE ===== Test if a client who has the right to read the
     * resource can find it. 
     * 
     * @throws LoginException
     * @throws SecurityServiceException
     * @throws SearchServiceException
     *             exception thrown when a problem occurs in the search method
     */
    @Test
    public void testSearchReadableResource() throws Exception {
        logger.info("Testing search of a readable resource");

        loginContextKermit.login();
        security.addSecurityRule("/indexingtests/forge_bug", "/profiles/guest", "read");
        loginContextKermit.logout();

        ArrayList<SearchResult> result = search.searchResource("bug AND forge");

        assertEquals("The ArrayList should contain exactly one result", 1, result.size());
        assertThat("The expected result should be the resource FORGE_BUG", result, hasItem(searchResultWithFactoryResourceIdentifier(friFB)));
    }

    /**
     * ===== BOUNDARIE ===== test if an inexistent resource can't be found by
     * the caller
     * 
     * @throws SearchServiceException
     *             exception thrown when a problem occurs in the search method
     */
    @Test
    public void testSearchInexistantResource() throws Exception {
        logger.info("Testing search of an inexistant resource");
        loginContextRoot.login();
        ArrayList<SearchResult> result = search.searchResource("titi");

        assertEquals("The ArrayList should be empty", 0, result.size());
        loginContextRoot.logout();
    }

    /**
     * ===== BOUNDARIE ===== Test if resource can be found with just a part of
     * the content
     * 
     * @throws SearchServiceException
     *             exception thrown when a problem occurs in the search method
     */
    @Test
    public void testSearchHalfContent() throws Exception {
        logger.info("Testing search of a resource with one keyword");
        loginContextRoot.login();
        ArrayList<SearchResult> result = search.searchResource("bug");

        assertEquals("The ArrayList should contain exactly two results", 2, result.size());
        assertThat("The expected result should be the resource BUG", result, hasItem(searchResultWithFactoryResourceIdentifier(friB)));
        assertThat("The expected result should be the resource FORGE_BUG", result, hasItem(searchResultWithFactoryResourceIdentifier(friFB)));
        loginContextRoot.logout();

    }

    /**
     * ====== RIGHT ===== Test if resource can be found with just a part or an
     * other of the content
     * 
     * @throws SearchServiceException
     *             exception thrown when a problem occurs in the search method
     */
    @Test
    public void testSearchOr() throws Exception {
        logger.info("Testing search with operator OR");
        loginContextRoot.login();
        ArrayList<SearchResult> result = search.searchResource("bug OR forge");

        assertEquals("The ArrayList should contains exactly three results", 3, result.size());
        assertThat("The expected result should be the resource FORGE_BUG", result, hasItem(searchResultWithFactoryResourceIdentifier(friFB)));
        assertThat("The expected result should be the resource FORGE", result, hasItem(searchResultWithFactoryResourceIdentifier(friF)));
        assertThat("The expected result should be the resource BUG", result, hasItem(searchResultWithFactoryResourceIdentifier(friB)));
        loginContextRoot.logout();
    }

    /**
     * ======= Right ====== Test if resource can't be found with negation of the
     * content
     * 
     * @throws SearchServiceException
     *             exception thrown when a problem occurs in the search method
     */
    @Test
    public void testSearchNotContent() throws Exception {
        logger.info("Testing search with operator NOT");
        loginContextRoot.login();
        ArrayList<SearchResult> result = search.searchResource("forge NOT bug ");

        assertEquals("The ArrayList should contains exactly one results", 1, result.size());
        assertThat("The expected result should be the resource FORGE", result, hasItem(searchResultWithFactoryResourceIdentifier(friF)));
        loginContextRoot.logout();
    }

    /**
     * ====== RIGHT ====== Test update of index when update of the resource
     * occurs
     * 
     * @throws GreetingServiceException
     * @throws SearchServiceException
     *             exception thrown when a problem occurs in the search method
     * @throws InterruptedException
     */
    @Test
    public void testUpdateIndex() throws Exception {
        logger.info("Testing update index");
        loginContextRoot.login();
        core.updateFolder("/indexingtests/forge", "egrof", "a egrof folder");
        
        // Waiting 1 second for the asynchronous call of the reindexation
        Thread.sleep(waitingTime);
        ArrayList<SearchResult> result = search.searchResource("egrof");

        assertEquals("The ArrayList should contains exactly one result", 1, result.size());
        assertThat("The expected result should be the resource FORGE", result, hasItem(searchResultWithFactoryResourceIdentifier(friF)));
        loginContextRoot.logout();
    }

    /**
     * ====== RIGHT ===== Test search of deleted resource
     * 
     * @throws GreetingServiceException
     * @throws SearchServiceException
     *             exception thrown when a problem occurs in the search method
     * @throws InterruptedException
     */
    @Test
    public void testDeletedSearch() throws Exception {
        logger.info("Testing delete from index");
        loginContextRoot.login();
        
        core.createFolder("/indexingtests/forge2", "forge", "a forge folder description");
        FactoryResourceIdentifier friF2 = browser.findResource("/indexingtests/forge2").getFactoryResourceIdentifier();
        Thread.sleep(waitingTime);
        
        //First search should return 3 results
        ArrayList<SearchResult> result = search.searchResource("forge");
        assertEquals("The ArrayList should contain exactly three results", 3, result.size());
        assertThat("The expected result should be the resource FORGE", result, hasItem(searchResultWithFactoryResourceIdentifier(friF)));
        assertThat("The expected result should be the resource FORGE2", result, hasItem(searchResultWithFactoryResourceIdentifier(friF2)));
        assertThat("The expected result should be the resource BUG_FORGE", result, hasItem(searchResultWithFactoryResourceIdentifier(friFB)));

        //After a delete, search results should change
        core.deleteFolder("/indexingtests/forge2");
        Thread.sleep(waitingTime);
        ArrayList<SearchResult> newresult = search.searchResource("forge");

        assertEquals("The ArrayList should contain exactly two result", 2, newresult.size());
        assertThat("The expected result should be the resource FORGE", result, hasItem(searchResultWithFactoryResourceIdentifier(friF)));
        assertThat("The expected result should be the resource BUG_FORGE", newresult, hasItem(searchResultWithFactoryResourceIdentifier(friFB)));

        loginContextRoot.logout();
    }

}
