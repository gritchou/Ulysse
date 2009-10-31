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
 * Initial authors :
 *
 * Jérôme Blanchard / INRIA
 * Christophe Bouthier / INRIA
 * Pascal Molli / Nancy Université
 * Gérald Oster / Nancy Université
 */
package org.qualipso.factory.client.test.sb;

import static org.junit.Assert.*;
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
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
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


public class IndexingServiceSBTest{
	private static Log logger = LogFactory.getLog(IndexingServiceSBTest.class);
	private static Context context;
	private static LoginContext loginContext;
	private static IndexingService indexing;
	private static MembershipService membership;
	private static GreetingService greeting; 
	private static PAPServiceHelper paph;
	private static BindingService binding;
	private FactoryResourceIdentifier friB, friF, friT, friFB;

	static String id;
	private FactoryResourceIdentifier fri;


	/**
	 * Set up service for all tests.
	 * @throws MembershipServiceException 
	 * @throws GreetingServiceException 
	 */
	@BeforeClass
	public static void before() throws NamingException, LoginException, MembershipServiceException, GreetingServiceException {
		Properties properties = new Properties();
		properties.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
		properties.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
		properties.put("java.naming.provider.url", "localhost:1099");
		System.setProperty("java.security.auth.login.config", ClassLoader.getSystemResource("jaas.config").getPath());
		context = new InitialContext(properties);

		UsernamePasswordHandler uph = new UsernamePasswordHandler("kermit", "thefrog"); 
		loginContext = new LoginContext("tests", uph);
		loginContext.login();

		indexing = (IndexingService) context.lookup("IndexingService");
		binding = (BindingService) context.lookup("BindingService");
		paph = (PAPServiceHelper) context.lookup("PAPServiceHelper");
		greeting=(GreetingService) context.lookup("GreetingService");
		membership = (MembershipService) context.lookup("MembershipService");
		membership.createProfile("toto", "toto titi", "toto@gmail.com", 0);

	}


	@AfterClass
	public static void after() throws LoginException, NamingException, MembershipServiceException {
		membership.deleteProfile("toto");

		loginContext.logout();
		context.close();
	}

	@Before
	public void setUp() throws NamingException, FactoryException {
		greeting.createName("/profiles/kermit/bug", "bug");
		greeting.createName("/profiles/kermit/forge", "forge");
		greeting.createName("/profiles/kermit/tm", "tm");
		greeting.createName("/profiles/kermit/forge_bug", "forge_bug");
		friB =  binding.lookup("/profiles/kermit/bug");
		friF =  binding.lookup("/profiles/kermit/forge");
		friT =  binding.lookup("/profiles/kermit/tm");
		friFB = binding.lookup("/profiles/kermit/forge_bug");


	}
	@After
	public void tearDown() throws MembershipServiceException, GreetingServiceException{
		greeting.deleteName("/profiles/kermit/bug")   ;
		greeting.deleteName("/profiles/kermit/forge")   ;
		greeting.deleteName("/profiles/kermit/tm");
		greeting.deleteName("/profiles/kermit/forge_bug");   
	}


	
	/**
	 * Test if a resource can be found by the resource owner
	 */
	@Test
	public void testIndexingSearchOwnedResource() throws IndexingServiceException{
		logger.debug("testing indexing search a owned resource(...)");
		ArrayList<SearchResult> result = indexing.search("bug AND forge");
		boolean test = false;
		boolean test2 = false;
		boolean test3 = false;
		boolean test4 = false;
		for(int i =0; i<result.size();i++){
			if(result.get(i).getResourceIdentifier().toString().equals(friB)){
				test  = true;
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friF)){
				test2 = true; 
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friT)){
				test3 = true;
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friFB)){
				test4 = true;
			}

		}
		assertTrue(!test&&!test2&&!test3&&test4);
	}
	
	/**
	 * Test if a client who doesn't have the right to read the resource can't find it
	 * */
	@Test
	public void testIndexingSearchNotOwnedResource() throws InvalidPathException, PathNotFoundException, BindingServiceException, IndexingServiceException{
		logger.debug("testing indexing search a not owned resource(...)");
		String policy = PAPServiceHelper.buildPolicy("1", "/profiles/kermit", "/profiles/kermit/friF", new String[]{""});
		binding.setProperty("/profiles/resource",FactoryResourceProperty.POLICY_ID, policy);
		ArrayList<SearchResult> result = indexing.search("bug AND forge");
		boolean test = false;
		boolean test2 = false;
		boolean test3 = false;
		boolean test4 = false;
		for(int i =0; i<result.size();i++){
			if(result.get(i).getResourceIdentifier().toString().equals(friB)){
				test  = true;
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friF)){
				test2 = true; 
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friT)){
				test3 = true;
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friFB)){
				test4 = true;
			}
		}
		assertTrue(test&&!test2&&!test3&&!test4);

		binding.setProperty("/profiles/resource",FactoryResourceProperty.OWNER, "/profiles/kermit");
	}
	
	/**
	 * Test if a client who has the right to read the resource can find it. This method give the ownership to user toto,
	 *  and keep the read right for kermit.
	 * 
	 */
	@Test
	public void testIndexingSearchReadableResource() throws InvalidPathException, PathNotFoundException, BindingServiceException, IndexingServiceException{
		logger.debug("testing indexing search a readable resource(...)");
		String policy = PAPServiceHelper.buildPolicy("1", "/profiles/kermit", "/profiles/kermit/friF", new String[]{"read"});
		binding.setProperty("/profiles/kermit/friF",FactoryResourceProperty.POLICY_ID, policy);
		binding.setProperty("/profiles/kermit/friF",FactoryResourceProperty.OWNER, "/profiles/toto");
		ArrayList<SearchResult> result;
		result = indexing.search("bug AND forge");
		boolean test = false;
		boolean test2 = false;
		boolean test3 = false;
		boolean test4= false;
		for(int i =0; i<result.size();i++){
			if(result.get(i).getResourceIdentifier().toString().equals(friB)){
				test  = true;
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friF)){
				test2 = true; 
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friT)){
				test3 = true;
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friFB)){
				test4 = true;
			}
		}
		assertTrue(test&&test2&&!test3&&test4);
	}

	
	/**
	 * test if an inexistent resource can't be found by the caller
	 * @throws IndexingServiceException
	 */
	@Test
	public void testIndexingSearchInexistantResource() throws IndexingServiceException {
		logger.debug("testing indexing search an inexistant resource(...)");
		ArrayList<SearchResult> result = indexing.search("titi");
		assertEquals(result.size(),0);
	}
	
	/**
	 * Test if resource can be found with just a part of the content
	 * 
	 */
	@Test
	public void testIndexingSearchHalfContent() throws IndexingServiceException {
		logger.debug("testing indexing search a resource(...)");
		ArrayList<SearchResult> result = indexing.search("bug");
		boolean test = false;
		boolean test2 = false; 
		boolean test3 = false; 
		for(int i =0; i<result.size();i++){
			if(result.get(i).getResourceIdentifier().toString().equals(friB)){ 
				test = true;   
			}
			if (result.get(i).getResourceIdentifier().toString().equals(friF) || ( 
					result.get(i).getResourceIdentifier().toString().equals(friT))){
				test2= true;
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friFB)){
				test3 = true;
			}
		}
		assertTrue(test&&!test2&&test3);
	}

	
	/**
	 * Test if resource can be found with just a part or an other of the content
	 * 
	 */
	@Test
	public void testIndexingSearchOr() throws IndexingServiceException {
		logger.debug("testing indexing search with OR");
		ArrayList<SearchResult> result = indexing.search("bug OR forge");
		boolean test = false;
		boolean test2 = false;
		boolean test3 = false;
		boolean test4= false;
		for(int i =0; i<result.size();i++){
			if(result.get(i).getResourceIdentifier().toString().equals(friB)){
				test  = true;
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friF)){
				test2 = true; 
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friT)){
				test3 = true;
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friFB)){
				test4 = true;
			}
		}
		assertTrue(test&&test2&&!test3&&test4);
	}

	
	/**
	 * Test if resource can't be found with negation of the content
	 */
	@Test
	public void testIndexingNotContent() throws IndexingServiceException{
		logger.debug("testing indexing search an inexistant resource(...)");
		ArrayList<SearchResult> result = indexing.search("NOT bug");
		boolean test = false;
		boolean test2 = false;
		boolean test3 = false;
		boolean test4= false;
		for(int i =0; i<result.size();i++){
			if(result.get(i).getResourceIdentifier().toString().equals(friB)){
				test  = true;
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friF)){
				test2 = true; 
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friT)){
				test3 = true;
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friFB)){
				test4 = true;
			}
		}
		assertTrue(!test&&test2&&test3&&!test4);
	}
	
	
	/**
	 * Test update of index when update of resource occur
	 */
	@Test
	public void testUpdateindex() throws GreetingServiceException, IndexingServiceException{
		logger.debug("Testing update index");
		greeting.updateName("/profiles/kermit/forge", "egrof");
		ArrayList<SearchResult> result = indexing.search("egrof");
		boolean test = false;
		for(int i =0; i<result.size();i++){
			if(result.get(i).getResourceIdentifier().toString().equals(friF)){
				test  = true;
			}
		}
		assertTrue(test);
	}
	
	
	/**
	 * Test search deleted resource
	 * 
	 */
	@Test
	public void testDeletedSearch() throws GreetingServiceException, IndexingServiceException{
		logger.debug("Testing update index");
		greeting.deleteName("/profiles/kermit/forge");
		ArrayList<SearchResult> result = indexing.search("forge");
		boolean test = false;
		boolean test2 =false;
		for(int i =0; i<result.size();i++){
			if(result.get(i).getResourceIdentifier().toString().equals(friB)){
				test  = true;
			}
			if(result.get(i).getResourceIdentifier().toString().equals(friFB)){
				test2  = true;
			}
		}
		assertTrue(!test&&test2);
	}
	
	/**
	 * Test update an not indexed resource. Here we use a greetingService.createNameE
	 *  which act normal but use a reIndex operation instead of an index operation.
	 */
	@Test(expected=IndexingServiceException.class)
	public void testupdateNotIndexedResource() throws IndexingServiceException, GreetingServiceException{
		logger.debug("Testing update index");
		greeting.deleteName("/profiles/kermit/forge");
		

		greeting.createName("/profiles/kermit/frogeR", "forgeR");
		
	}
	
	
	
	/**
	 * Test a full disk issue. This test use  a spécific greetingService.createNamei. 
	 * This operation act like it might but don't add the new name to the entity manager
	 */
	@Test(expected=IndexingServiceException.class)
	public void testFullDisk() throws GreetingServiceException, IndexingServiceException{
		
		int i = 0;
		while(true){
			
			greeting.createName("/profiles/kermit/bug"+i, "bug"+i);
			i++;
		}
	}
	
}

