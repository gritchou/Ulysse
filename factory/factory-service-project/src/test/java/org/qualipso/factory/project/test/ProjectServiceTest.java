package org.qualipso.factory.project.test;

/**
 * 
 * Copyright (C) 2006-2010 THALES
 * http://www.thalesgroup.fr - thierry.deroff@thalesgroup.com
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the license LGPL.
 *
 * Initial author :
 * Thierry Deroff from Thales Service, THERESIS Competence Center Open Source Software
 *
 */

import static org.hamcrest.text.StringContains.containsString;
import static org.qualipso.factory.project.test.jmock.action.SaveParamsAction.saveParams;
import static org.qualipso.factory.project.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;



import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.project.ProjectServiceBean;
import org.qualipso.factory.project.entity.Project;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;

import com.bm.testsuite.BaseSessionBeanFixture;


public class ProjectServiceTest extends BaseSessionBeanFixture<ProjectServiceBean> {
	
	private static Log logger = LogFactory.getLog(ProjectServiceTest.class);
	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = { Project.class };
	
	private Mockery mockery;
	private BindingService binding;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	private MembershipService membership;
	
	public ProjectServiceTest() {
		super(ProjectServiceBean.class, usedBeans);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		logger.debug("injecting mock partners session beans");
		mockery = new Mockery();
		binding = mockery.mock(BindingService.class);
		pep = mockery.mock(PEPService.class);
		pap= mockery.mock(PAPService.class);
		notification = mockery.mock(NotificationService.class);
		membership = mockery.mock(MembershipService.class);
		

		getBeanToTest().setNotificationService(notification);
		getBeanToTest().setBindingService(binding);
		getBeanToTest().setPEPService(pep);
		getBeanToTest().setPAPService(pap);
		getBeanToTest().setMembershipService(membership);
		
	}
	
	public void testCreateProject() {
		logger.debug("testing createProject(...)");
		
		final Sequence sequence1 = mockery.sequence("sequence1");
		
		try {
			mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/toto")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/toto")), with(equal("/projects")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/projects/test"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/toto"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/profiles/toto"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/toto"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.project.create"))); inSequence(sequence1);
				
				
				}
			});
			
			getBeanToTest().getEntityManager().getTransaction().begin();
			getBeanToTest().createProject("/projects/test", "projectTest", "this is a test", "GPL");
			getBeanToTest().getEntityManager().getTransaction().commit();	
			mockery.assertIsSatisfied();
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}	
	}
	
	public void testGetProject() {
		logger.debug("testing getProject(...)");
		
		final Sequence sequence1 = mockery.sequence("sequence1");
		final Vector<Object> params = new Vector<Object>();
		
		try {
			//create Project
			mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/toto")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/toto")), with(equal("/projects")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/projects/test"))); will(saveParams(params)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/toto"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/profiles/toto"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/toto"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.project.create"))); inSequence(sequence1);
				
				
				}
			});
			
			getBeanToTest().getEntityManager().getTransaction().begin();
			getBeanToTest().createProject("/projects/test", "projectTest", "this is a test", "GPL");
			getBeanToTest().getEntityManager().getTransaction().commit();	
			mockery.assertIsSatisfied();
			
			//get project and check parameters
			//---------------------------------------
			mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/toto")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/toto")), with(equal("/projects/test")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/projects/test"))); will(returnValue(params.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.project.read"))); inSequence(sequence1);
				}
			});
			
			getBeanToTest().getEntityManager().getTransaction().begin();
			Project project = getBeanToTest().getProject("/projects/test");
			assertTrue(project.getResourcePath().equals("/projects/test"));
			assertTrue(project.getName().equals("projectTest"));
			assertTrue(project.getSummary().equals("this is a test"));
			assertTrue(project.getLicence().equals("GPL"));
			getBeanToTest().getEntityManager().getTransaction().commit();
			mockery.assertIsSatisfied();
			//---------------------------------------
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}	
	}
	
	public void testUpdateProject(){
		logger.debug("testing updateProject(...)");
		
		final Sequence sequence1 = mockery.sequence("sequence1");
		final Vector<Object> params = new Vector<Object>();
		
		try {
			//create Project
			mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/toto")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/toto")), with(equal("/projects")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/projects/test"))); will(saveParams(params)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/toto"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/profiles/toto"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/toto"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.project.create"))); inSequence(sequence1);
				
				
				}
			});
			
			getBeanToTest().getEntityManager().getTransaction().begin();
			getBeanToTest().createProject("/projects/test", "projectTest", "this is a test", "GPL");
			getBeanToTest().getEntityManager().getTransaction().commit();	
			mockery.assertIsSatisfied();
			
			//update project
			mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/toto")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/toto")), with(equal("/projects/test")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/projects/test"))); will(returnValue(params.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.project.update"))); inSequence(sequence1);
				}
			});
			
			getBeanToTest().getEntityManager().getTransaction().begin();
			getBeanToTest().updateProject("/projects/test", "projectTestUpdate", "alpha","this is a test update", "GPL update");
			getBeanToTest().getEntityManager().getTransaction().commit();	
			mockery.assertIsSatisfied();
			
			//check update
			mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/toto")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/toto")), with(equal("/projects/test")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/projects/test"))); will(returnValue(params.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.project.read"))); inSequence(sequence1);
				}
			});
			
			getBeanToTest().getEntityManager().getTransaction().begin();
			Project project = getBeanToTest().getProject("/projects/test");
			assertTrue(project.getResourcePath().equals("/projects/test"));
			assertTrue(project.getName().equals("projectTestUpdate"));
			assertTrue(project.getSummary().equals("this is a test update"));
			assertTrue(project.getLicence().equals("GPL update"));
			assertTrue(project.getDev_status().equals("alpha"));
			getBeanToTest().getEntityManager().getTransaction().commit();
			mockery.assertIsSatisfied();
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}	
	}
	
	
	public void testUpdateTagsProject(){
		logger.debug("testing updateTagsProject(...)");
		
		final Sequence sequence1 = mockery.sequence("sequence1");
		final Vector<Object> params = new Vector<Object>();
		
		try {
			//create Project
			mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/toto")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/toto")), with(equal("/projects")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/projects/test"))); will(saveParams(params)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/toto"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/profiles/toto"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/toto"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.project.create"))); inSequence(sequence1);
				
				
				}
			});
			
			getBeanToTest().getEntityManager().getTransaction().begin();
			getBeanToTest().createProject("/projects/test", "projectTest", "this is a test", "GPL");
			getBeanToTest().getEntityManager().getTransaction().commit();	
			mockery.assertIsSatisfied();
			
			//update project
			mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/toto")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/toto")), with(equal("/projects/test")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/projects/test"))); will(returnValue(params.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/projects/test")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.project.update"))); inSequence(sequence1);
				}
			});
			
			//updateTagsProject(String path, String[] os, String[] topics,
			//String[] language, String[] programming_language,
			//String[] intended_audience)
			
			getBeanToTest().getEntityManager().getTransaction().begin();
			getBeanToTest().updateTagsProject("/projects/test", new String[] {"windows NT", "GPL/Linux"}, new String[] {"Software Development/", "Internet/"},new String[] {"English"}, new String[] {"Java", "Caml"}, new String[] {"Developers" ,"End Users"});
			getBeanToTest().getEntityManager().getTransaction().commit();	
			mockery.assertIsSatisfied();
			
			//check update
			mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/toto")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/toto")), with(equal("/projects/test")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/projects/test"))); will(returnValue(params.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.project.read"))); inSequence(sequence1);
				}
			});
			
			getBeanToTest().getEntityManager().getTransaction().begin();
			Project project = getBeanToTest().getProject("/projects/test");
			assertTrue(project.getResourcePath().equals("/projects/test"));
			assertTrue(project.getName().equals("projectTest"));
			assertTrue(project.getSummary().equals("this is a test"));
			assertTrue(project.getLicence().equals("GPL"));
			assertNull(project.getDev_status());
			assertTrue(java.util.Arrays.equals(project.getOs(), new String[] {"windows NT", "GPL/Linux"}));
			assertTrue(java.util.Arrays.equals(project.getTopics(),new String[] {"Software Development/", "Internet/"}));
			assertTrue(java.util.Arrays.equals(project.getSpoken_language(),new String[] {"English"}));
			assertTrue(java.util.Arrays.equals(project.getProgramming_language(),new String[] {"Java", "Caml"}));
			assertTrue(java.util.Arrays.equals(project.getIntended_audience(),new String[] {"Developers" ,"End Users"}));
			getBeanToTest().getEntityManager().getTransaction().commit();
			mockery.assertIsSatisfied();
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}	
	}
	
}
