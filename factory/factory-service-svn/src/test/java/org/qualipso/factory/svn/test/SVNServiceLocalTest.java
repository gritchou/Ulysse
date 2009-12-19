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
package org.qualipso.factory.svn.test;

import static org.hamcrest.text.StringContains.containsString;
import static org.qualipso.factory.svn.test.jmock.action.SaveParamsAction.saveParams;
import static org.qualipso.factory.svn.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;

import java.util.Arrays;
import java.util.UUID;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.binding.entity.Node;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.ssh.SSHService;
import org.qualipso.factory.svn.SVNService;
import org.qualipso.factory.svn.SVNServiceBean;
import org.qualipso.factory.svn.entity.SVNRepository;
import org.qualipso.factory.svn.ssh.command.filter.component.SVNResourceType;

import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * 
 * Test of SVNServiceLocal
 *
 */
public class SVNServiceLocalTest extends BaseSessionBeanFixture<SVNServiceBean> {
    
	/**
	 * Logger
	 */
	private static Log logger = LogFactory.getLog(SVNServiceLocalTest.class);
    
	/**
	 * usedBeans
	 */
	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = { SVNRepository.class, Node.class, Profile.class };
    
	/**
	 * path of profile for test
	 */
	private static String PATH_PROFILE = "/profiles/usertest";
	
	/**
	 * Id of the test repository
	 */
	private String idRepository;
	
	/**
	 * Mockery
	 */
	private Mockery mockery;
	
	/**
	 * BindingService
	 */
	private BindingService binding;
	
	/**
	 * MembershipService
	 */
	private MembershipService membership;
	
	/**
	 * PEPService
	 */
	private PEPService pep;
	
	/**
	 * PAPService
	 */
	private PAPService pap;
	
	/**
	 * NotificationService
	 */
	private NotificationService notification;
	
	/**
	 * SSHService
	 */
	private SSHService ssh;
	
    public SVNServiceLocalTest() {
    	super(SVNServiceBean.class, usedBeans);
    }
    
    /**
     * SetUp
     * Executed before test running
     */
    public void setUp() throws Exception {
		super.setUp();
		logger.debug("injecting mock partners session beans");
		mockery = new Mockery();
		binding = mockery.mock(BindingService.class);
		membership = mockery.mock(MembershipService.class);
		pep = mockery.mock(PEPService.class);
		pap = mockery.mock(PAPService.class);
		notification = mockery.mock(NotificationService.class);
		ssh = mockery.mock(SSHService.class);
		getBeanToTest().setMembershipService(membership);
		getBeanToTest().setNotificationService(notification);
		getBeanToTest().setBindingService(binding);
		getBeanToTest().setPEPService(pep);
		getBeanToTest().setPAPService(pap);
		
		//Create a repository
        final Sequence sequence1 = mockery.sequence("sequence1");
        final Vector<Object> params1 = new Vector<Object>();
        
		mockery.checking(new Expectations() {
			{
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(PATH_PROFILE)); inSequence(sequence1);
				oneOf(pep).checkSecurity(with(equal(PATH_PROFILE)), with(equal("/")), with(equal("create"))); inSequence(sequence1);
				oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/repo1"))); will(saveParams(params1));  inSequence(sequence1);
				oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
				oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
				oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal(PATH_PROFILE))); inSequence(sequence1);
				oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/repo1"))); inSequence(sequence1);
				oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.OWNER)), with(equal(PATH_PROFILE))); inSequence(sequence1);
				oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("svn.svn-repository.create"))); inSequence(sequence1);
			}
		});
		
		SVNServiceBean service = getBeanToTest();
		idRepository = service.createSVNRepository("/repo1", "My SVN Repository 1", "A super svn repository");
	}
    
    /**
     * test writeInRepository to create resource
     * @throws Exception if an error occurred
     */
    public void testWriteInRepository_create() throws Exception{
		
        logger.debug("testing writeInRepository to create resource(...)");
        
        final Sequence sequence1 = mockery.sequence("sequence2");
        
		mockery.checking(new Expectations() {
			{
				String parentPath = "/repo1/" + "subdir".hashCode();
				String resourcePath = parentPath + "/" + "subdir2".hashCode();
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(PATH_PROFILE)); inSequence(sequence1);
				oneOf(binding).lookup(with(equal(resourcePath))); will(throwException(new PathNotFoundException(""))); inSequence(sequence1);
				oneOf(binding).lookup(with(equal(parentPath))); will(returnValue(FactoryResourceIdentifier.deserialize(SVNService.SERVICE_NAME + "/" + "svn-resource" + "/" + "1")));
				oneOf(pep).checkSecurity(with(equal(PATH_PROFILE)), with(equal(parentPath)), with(equal("create"))); inSequence(sequence1);
				oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(containsString(resourcePath))); inSequence(sequence1);
				
				oneOf(binding).setProperty(with(equal(resourcePath)), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
				oneOf(binding).setProperty(with(equal(resourcePath)), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
				oneOf(binding).setProperty(with(equal(resourcePath)), with(equal(FactoryResourceProperty.AUTHOR)), with(equal(PATH_PROFILE))); inSequence(sequence1);
				oneOf(pap).createPolicy(with(any(String.class)), with(containsString(resourcePath))); inSequence(sequence1);
				oneOf(binding).setProperty(with(equal(resourcePath)), with(equal(FactoryResourceProperty.OWNER)), with(equal(PATH_PROFILE))); inSequence(sequence1);
				oneOf(binding).setProperty(with(equal(resourcePath)), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
			
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("svn.svn-resource.create"))); inSequence(sequence1);
			}
		});
		
		SVNServiceBean service = getBeanToTest();
		service.writeInRepository(SVNResourceType.ADD_DIR, Arrays.asList(idRepository, "subdir", "subdir2"));
		
		mockery.assertIsSatisfied();
    }
    
    
    /**
     * test writeInRepository to update resource
     * @throws Exception if an error occurred
     */
    public void testWriteInRepository_update() throws Exception{
		
        logger.debug("testing writeInRepository to update resource(...)");
        
        final Sequence sequence1 = mockery.sequence("sequence2");
        
		mockery.checking(new Expectations() {
			{
				String resourcePath = "/repo1/" + "subdir".hashCode() + "/" + "subdir2".hashCode();
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(PATH_PROFILE)); inSequence(sequence1);
				oneOf(binding).lookup(with(equal(resourcePath))); will(returnValue(FactoryResourceIdentifier.deserialize(SVNService.SERVICE_NAME + "/" + "svn-resource" + "/" + UUID.randomUUID()))); inSequence(sequence1);
				oneOf(pep).checkSecurity(with(equal(PATH_PROFILE)), with(equal(resourcePath)), with(equal("update"))); inSequence(sequence1);
				
				oneOf(binding).setProperty(with(equal(resourcePath)), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("svn.svn-resource.update"))); inSequence(sequence1);
			}
		});
		
		SVNServiceBean service = getBeanToTest();
		service.writeInRepository(SVNResourceType.ADD_DIR, Arrays.asList(idRepository, "subdir", "subdir2"));
		
		mockery.assertIsSatisfied();
    }
    
    
    /**
     * test writeInRepository to delete resource
     * @throws Exception if an error occurred
     */
    public void testWriteInRepository_delete() throws Exception{
		
        logger.debug("testing writeInRepository to delete resource(...)");
        
        final Sequence sequence1 = mockery.sequence("sequence2");
        
		mockery.checking(new Expectations() {
			{
				String resourcePath = "/repo1/" + "subdir".hashCode() + "/" + "subdir2".hashCode();
				String [] children = new String[] {
						resourcePath + "/titi", resourcePath + "/toto"
				};
				String [] children2 = new String[] {
						resourcePath + "/toto/tutu"
				};
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(PATH_PROFILE)); inSequence(sequence1);
				oneOf(binding).lookup(with(equal(resourcePath))); will(returnValue(FactoryResourceIdentifier.deserialize(SVNService.SERVICE_NAME + "/" + "svn-resource" + "/" + UUID.randomUUID()))); inSequence(sequence1);
				//oneOf(pep).checkSecurity(with(equal(PATH_PROFILE)), with(equal(resourcePath)), with(equal("delete"))); inSequence(sequence1);
				
				oneOf(binding).list(with(equal(resourcePath)));will(returnValue(children)); inSequence(sequence1);
				
				oneOf(binding).list(with(equal(resourcePath + "/titi")));will(returnValue(null)); inSequence(sequence1);
				oneOf(pep).checkSecurity(with(equal(PATH_PROFILE)), with(equal(resourcePath + "/titi")), with(equal("delete"))); inSequence(sequence1);
				oneOf(binding).getProperty(with(equal(resourcePath + "/titi")), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); will(returnValue("policeId")); inSequence(sequence1);
				oneOf(pap).deletePolicy("policeId"); inSequence(sequence1);
				oneOf(binding).unbind(with(equal(resourcePath + "/titi")));
				
				oneOf(binding).list(with(equal(resourcePath + "/toto")));will(returnValue(children2)); inSequence(sequence1);
				oneOf(binding).list(with(equal(resourcePath + "/toto/tutu")));will(returnValue(null)); inSequence(sequence1);
				oneOf(pep).checkSecurity(with(equal(PATH_PROFILE)), with(equal(resourcePath + "/toto/tutu")), with(equal("delete"))); inSequence(sequence1);
				oneOf(binding).getProperty(with(equal(resourcePath + "/toto/tutu")), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); will(returnValue("policeId")); inSequence(sequence1);
				oneOf(pap).deletePolicy("policeId"); inSequence(sequence1);
				oneOf(binding).unbind(with(equal(resourcePath + "/toto/tutu")));
				
				oneOf(pep).checkSecurity(with(equal(PATH_PROFILE)), with(equal(resourcePath + "/toto")), with(equal("delete"))); inSequence(sequence1);
				oneOf(binding).getProperty(with(equal(resourcePath + "/toto")), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); will(returnValue("policeId")); inSequence(sequence1);
				oneOf(pap).deletePolicy("policeId"); inSequence(sequence1);
				oneOf(binding).unbind(with(equal(resourcePath + "/toto")));
				
				oneOf(pep).checkSecurity(with(equal(PATH_PROFILE)), with(equal(resourcePath)), with(equal("delete"))); inSequence(sequence1);
				oneOf(binding).getProperty(with(equal(resourcePath)), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); will(returnValue("policeId")); inSequence(sequence1);
				oneOf(pap).deletePolicy("policeId"); inSequence(sequence1);
				oneOf(binding).unbind(with(equal(resourcePath)));
				
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("svn.svn-resource.delete"))); inSequence(sequence1);
			}
		});
		
		SVNServiceBean service = getBeanToTest();
		service.writeInRepository(SVNResourceType.DELETE_ENTRY, Arrays.asList(idRepository, "subdir", "subdir2"));
		
		mockery.assertIsSatisfied();
    }
    
    /**
     * test readFromRepository a resource
     * @throws Exception if an error occurred
     */
    public void testReadFromRepository_resource() throws Exception{
		
        logger.debug("testing readFromRepository(...). Read a resource");
        
        final Sequence sequence1 = mockery.sequence("sequence2");
        
        mockery.checking(new Expectations() {
			{
				String resourcePath = "/repo1/" + "subdir".hashCode() + "/" + "subdir2".hashCode();
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(PATH_PROFILE)); inSequence(sequence1);
				oneOf(binding).lookup(with(equal(resourcePath))); will(returnValue(FactoryResourceIdentifier.deserialize(SVNService.SERVICE_NAME + "/" + "svn-resource" + "/" + UUID.randomUUID()))); inSequence(sequence1);
				oneOf(pep).checkSecurity(with(equal(PATH_PROFILE)), with(equal(resourcePath)), with(equal("read"))); inSequence(sequence1);
				
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("svn.svn-resource.read"))); inSequence(sequence1);
			}
		});
		
		SVNServiceBean service = getBeanToTest();
		service.readFromRepository(SVNResourceType.DELETE_ENTRY, Arrays.asList(idRepository, "subdir", "subdir2"));
		
		mockery.assertIsSatisfied();
    }
    
    /**
     * test readFromRepository a repository
     * @throws Exception if an error occurred
     */
    public void testReadFromRepository_repository() throws Exception{
		
        logger.debug("testing readFromRepository(...). Read a repository");
        
        final Sequence sequence1 = mockery.sequence("sequence2");
        
        mockery.checking(new Expectations() {
			{
				String resourcePath = "/repo1/" + "subdir".hashCode() + "/" + "subdir2".hashCode();
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(PATH_PROFILE)); inSequence(sequence1);
				oneOf(binding).lookup(with(equal(resourcePath))); will(returnValue(FactoryResourceIdentifier.deserialize(SVNService.SERVICE_NAME + "/" + SVNRepository.RESOURCE_NAME + "/" + UUID.randomUUID()))); inSequence(sequence1);
				oneOf(pep).checkSecurity(with(equal(PATH_PROFILE)), with(equal(resourcePath)), with(equal("read"))); inSequence(sequence1);
				
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("svn." + SVNRepository.RESOURCE_NAME + ".read"))); inSequence(sequence1);
			}
		});
		
		SVNServiceBean service = getBeanToTest();
		service.readFromRepository(SVNResourceType.DELETE_ENTRY, Arrays.asList(idRepository, "subdir", "subdir2"));
		
		mockery.assertIsSatisfied();
    }
}
