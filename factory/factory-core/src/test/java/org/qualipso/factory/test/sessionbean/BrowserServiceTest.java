/*
 *
 * Qualipso Factory
 * Copyright (C) 2006-2010 INRIA
 * http://www.inria.fr - molli@loria.fr
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of LGPL. See licenses details in LGPL.txt
 *
 * Initial authors :
 *
 * Jérôme Blanchard / INRIA
 * Pascal Molli / Nancy Université
 * Gérald Oster / Nancy Université
 * Christophe Bouthier / INRIA
 * 
 */
package org.qualipso.factory.test.sessionbean;

import com.bm.testsuite.BaseSessionBeanFixture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;

import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.browser.BrowserService;
import org.qualipso.factory.browser.BrowserServiceBean;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;
import static org.qualipso.factory.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 5 august 2009
 */
public class BrowserServiceTest extends BaseSessionBeanFixture<BrowserServiceBean> {
    private static Log logger = LogFactory.getLog(BrowserServiceTest.class);
    @SuppressWarnings("unchecked")
    private static final Class[] usedBeans = {  };
    private Mockery mockery;
    private BindingService binding;
    private MembershipService membership;
    private PEPService pep;
    private PAPService pap;
    private NotificationService notification;

    public BrowserServiceTest() {
        super(BrowserServiceBean.class, usedBeans);
    }

    public void setUp() throws Exception {
        super.setUp();
        logger.debug("injecting mock partners session beans");
        mockery = new Mockery();
        binding = mockery.mock(BindingService.class);
        membership = mockery.mock(MembershipService.class);
        pep = mockery.mock(PEPService.class);
        pap = mockery.mock(PAPService.class);
        notification = mockery.mock(NotificationService.class);
        getBeanToTest().setMembershipService(membership);
        getBeanToTest().setNotificationService(notification);
        getBeanToTest().setBindingService(binding);
        getBeanToTest().setPEPService(pep);
        getBeanToTest().setPAPService(pap);
    }
    
    public void testExists() {
        logger.debug("testing exists(...)");

        final Sequence sequence1 = mockery.sequence("sequence1");

        try {
            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/toto/titi")), with(equal("read")));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/toto")), with(equal("read")));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(any(String.class)));
                        will(returnValue(new FactoryResourceIdentifier("Servcie", "Type", "ID")));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("browser.resource.exists")));
                        inSequence(sequence1);
                        
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/")), with(equal("read")));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/")), with(equal("read")));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(any(String.class)));
                        will(returnValue(new FactoryResourceIdentifier("Servcie", "Type", "ID")));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("browser.resource.exists")));
                        inSequence(sequence1);
                    }
                });

            BrowserService service = getBeanToTest();
            assertTrue(service.exists("/toto/titi"));
            assertTrue(service.exists("/"));
            mockery.assertIsSatisfied();
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testHasChildren() {
        logger.debug("testing hasChildren(...)");

        final Sequence sequence1 = mockery.sequence("sequence1");

        try {
            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/")), with(equal("read")));
                        inSequence(sequence1);
                        oneOf(binding).list(with(any(String.class)));
                        will(returnValue(new String[] { "/test", "/test2" }));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("browser.resource.has-children")));
                        inSequence(sequence1);
                    }
                });

            BrowserService service = getBeanToTest();
            assertTrue(service.hasChildren("/"));
            mockery.assertIsSatisfied();
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testListChildren() {
        logger.debug("testing listChildren(...)");

        final Sequence sequence1 = mockery.sequence("sequence1");

        try {
            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/")), with(equal("read")));
                        inSequence(sequence1);
                        oneOf(binding).list(with(any(String.class)));
                        will(returnValue(new String[] { "/test", "/test2", "/test3" }));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("browser.resource.list-children")));
                        inSequence(sequence1);
                    }
                });

            BrowserService service = getBeanToTest();
            assertTrue(service.listChildren("/").length == 3);
            mockery.assertIsSatisfied();
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testListChildrenOfType() {
        logger.debug("testing listChildrenOfType(...)");

        final Sequence sequence1 = mockery.sequence("sequence1");

        try {
            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/")), with(equal("read")));
                        inSequence(sequence1);
                        oneOf(binding).list(with(any(String.class)));
                        will(returnValue(new String[] { "/test", "/test2", "/test3", "/test4" }));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/test")));
                        will(returnValue(new FactoryResourceIdentifier("ForumService", "Forum", "UUID-1")));
                        oneOf(binding).lookup(with(equal("/test2")));
                        will(returnValue(new FactoryResourceIdentifier("CoreService", "Folder", "UUID-2")));
                        oneOf(binding).lookup(with(equal("/test3")));
                        will(returnValue(new FactoryResourceIdentifier("CoreService", "Folder", "UUID-3")));
                        oneOf(binding).lookup(with(equal("/test4")));
                        will(returnValue(new FactoryResourceIdentifier("CoreService", "File", "UUID-4")));
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("browser.resource.list-children-of-type")));
                        inSequence(sequence1);

                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/")), with(equal("read")));
                        inSequence(sequence1);
                        oneOf(binding).list(with(any(String.class)));
                        will(returnValue(new String[] { "/test", "/test2", "/test3", "/test4" }));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/test")));
                        will(returnValue(new FactoryResourceIdentifier("ForumService", "Forum", "UUID-1")));
                        oneOf(binding).lookup(with(equal("/test2")));
                        will(returnValue(new FactoryResourceIdentifier("CoreService", "Folder", "UUID-2")));
                        oneOf(binding).lookup(with(equal("/test3")));
                        will(returnValue(new FactoryResourceIdentifier("CoreService", "Folder", "UUID-3")));
                        oneOf(binding).lookup(with(equal("/test4")));
                        will(returnValue(new FactoryResourceIdentifier("CoreService", "File", "UUID-4")));
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("browser.resource.list-children-of-type")));
                        inSequence(sequence1);

                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/")), with(equal("read")));
                        inSequence(sequence1);
                        oneOf(binding).list(with(any(String.class)));
                        will(returnValue(new String[] { "/test", "/test2", "/test3", "/test4" }));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/test")));
                        will(returnValue(new FactoryResourceIdentifier("ForumService", "Forum", "UUID-1")));
                        oneOf(binding).lookup(with(equal("/test2")));
                        will(returnValue(new FactoryResourceIdentifier("CoreService", "Folder", "UUID-2")));
                        oneOf(binding).lookup(with(equal("/test3")));
                        will(returnValue(new FactoryResourceIdentifier("CoreService", "Folder", "UUID-3")));
                        oneOf(binding).lookup(with(equal("/test4")));
                        will(returnValue(new FactoryResourceIdentifier("CoreService", "File", "UUID-4")));
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("browser.resource.list-children-of-type")));
                        inSequence(sequence1);
                    }
                });

            BrowserService service = getBeanToTest();

            assertTrue(service.listChildrenOfType("/", "ForumService", ".*").length == 1);
            assertTrue(service.listChildrenOfType("/", "CoreService", ".*").length == 3);
            assertTrue(service.listChildrenOfType("/", "CoreService", "Folder").length == 2);
            mockery.assertIsSatisfied();
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }
}
