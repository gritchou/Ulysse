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

import static org.hamcrest.text.StringContains.containsString;
import static org.qualipso.factory.test.jmock.action.SaveParamsAction.saveParams;
import static org.qualipso.factory.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.entity.Node;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.core.CoreServiceBean;
import org.qualipso.factory.core.entity.Folder;
import org.qualipso.factory.core.entity.Link;
import org.qualipso.factory.indexing.IndexingService;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;

import com.bm.testsuite.BaseSessionBeanFixture;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 july 2009
 */
public class CoreServiceTest extends BaseSessionBeanFixture<CoreServiceBean> {
    private static Log logger = LogFactory.getLog(CoreServiceTest.class);
    @SuppressWarnings("unchecked")
    private static final Class[] usedBeans = { Link.class, Folder.class, Node.class, Profile.class };
    private Mockery mockery;
    private BindingService binding;
    private MembershipService membership;
    private PEPService pep;
    private PAPService pap;
    private NotificationService notification;
    private IndexingService indexing;

    public CoreServiceTest() {
        super(CoreServiceBean.class, usedBeans);
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
        indexing = mockery.mock(IndexingService.class);
        getBeanToTest().setMembershipService(membership);
        getBeanToTest().setNotificationService(notification);
        getBeanToTest().setIndexingService(indexing);
        getBeanToTest().setBindingService(binding);
        getBeanToTest().setPEPService(pep);
        getBeanToTest().setPAPService(pap);
    }

    public void testCRUDFolder() {
        logger.debug("testing testCRUDFolder(...)");

        final Sequence sequence1 = mockery.sequence("sequence1");
        final Vector<Object> params1 = new Vector<Object>();

        try {
            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/")), with(equal("create")));
                        inSequence(sequence1);
                        oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/folder1")));
                        will(saveParams(params1));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/folder1")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class)));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/folder1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class)));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/folder1")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/jayblanc")));
                        inSequence(sequence1);
                        oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/folder1")));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/folder1")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc")));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/folder1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class)));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("core.folder.create")));
                        inSequence(sequence1);
                        oneOf(indexing).index(with(equal("/folder1")));
                        inSequence(sequence1);
                    }
                });

            CoreService service = getBeanToTest();
            service.createFolder("/folder1", "My Folder 1", "A super folder");

            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/folder1")), with(equal("read")));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/folder1")));
                        will(returnValue(params1.get(0)));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("core.folder.read")));
                        inSequence(sequence1);
                    }
                });

            assertTrue(service.readFolder("/folder1").getName().equals("My Folder 1"));

            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/folder1")), with(equal("update")));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/folder1")));
                        will(returnValue(params1.get(0)));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/folder1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class)));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("core.folder.update")));
                        inSequence(sequence1);
                        oneOf(indexing).reindex(with(equal("/folder1")));
                        inSequence(sequence1);
                    }
                });

            service.updateFolder("/folder1", "MyFolder", "a folder");

            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/folder1")), with(equal("read")));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/folder1")));
                        will(returnValue(params1.get(0)));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("core.folder.read")));
                        inSequence(sequence1);
                    }
                });

            Folder rf = service.readFolder("/folder1");
            assertTrue(rf.getName().equals("MyFolder"));
            assertTrue(rf.getDescription().equals("a folder"));

            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/folder1")), with(equal("delete")));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/folder1")));
                        will(returnValue(params1.get(0)));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/folder1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); 
                        will(returnValue("aFakePolicyID"));  
                        inSequence(sequence1);
    					oneOf(pap).deletePolicy("aFakePolicyID"); 
    					inSequence(sequence1);
                        oneOf(binding).unbind(with(equal("/folder1")));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("core.folder.delete")));
                        inSequence(sequence1);
                        oneOf(indexing).remove(with(equal("/folder1")));
                        inSequence(sequence1);
                    }
                });

            service.deleteFolder("/folder1");

            mockery.assertIsSatisfied();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testCRUDLink() {
        logger.debug("testing CRUDLink(...)");

        final Sequence sequence1 = mockery.sequence("sequence1");
        final Vector<Object> params11 = new Vector<Object>();
        final Vector<Object> params12 = new Vector<Object>();
        final Vector<Object> params2 = new Vector<Object>();

        try {
            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/")), with(equal("create")));
                        inSequence(sequence1);
                        oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/folder1")));
                        will(saveParams(params11));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/folder1")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class)));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/folder1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class)));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/folder1")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/jayblanc")));
                        inSequence(sequence1);
                        oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/folder1")));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/folder1")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc")));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/folder1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class)));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("core.folder.create")));
                        inSequence(sequence1);
                        oneOf(indexing).index(with(equal("/folder1")));
                        inSequence(sequence1);

                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/")), with(equal("create")));
                        inSequence(sequence1);
                        oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/folder2")));
                        will(saveParams(params12));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/folder2")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class)));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/folder2")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class)));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/folder2")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/jayblanc")));
                        inSequence(sequence1);
                        oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/folder2")));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/folder2")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc")));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/folder2")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class)));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("core.folder.create")));
                        inSequence(sequence1);
                        oneOf(indexing).index(with(equal("/folder2")));
                        inSequence(sequence1);

                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/")), with(equal("create")));
                        inSequence(sequence1);
                        oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/link1")));
                        will(saveParams(params2));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/link1")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class)));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/link1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class)));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/link1")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/jayblanc")));
                        inSequence(sequence1);
                        oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/link1")));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/link1")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc")));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/link1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class)));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("core.link.create")));
                        inSequence(sequence1);
                        oneOf(indexing).index(with(equal("/link1")));
                        inSequence(sequence1);
                    }
                });

            CoreService service = getBeanToTest();
            service.createFolder("/folder1", "My Folder 1", "A super folder");
            service.createFolder("/folder2", "My Folder 2", "Another super folder");
            service.createLink("/link1", "/folder1");

            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/link1")), with(equal("read")));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/link1")));
                        will(returnValue(params2.get(0)));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("core.link.read")));
                        inSequence(sequence1);
                    }
                });

            assertTrue(service.readLink("/link1").getLink().equals("/folder1"));

            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/link1")), with(equal("update")));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/link1")));
                        will(returnValue(params2.get(0)));
                        inSequence(sequence1);
                        oneOf(binding).setProperty(with(equal("/link1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class)));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("core.link.update")));
                        inSequence(sequence1);
                        oneOf(indexing).reindex(with(equal("/link1")));
                        inSequence(sequence1);
                    }
                });

            service.updateLink("/link1", "/folder2");

            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/link1")), with(equal("read")));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/link1")));
                        will(returnValue(params2.get(0)));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("core.link.read")));
                        inSequence(sequence1);
                    }
                });

            assertTrue(service.readLink("/link1").getLink().equals("/folder2"));

            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/link1")), with(equal("delete")));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/link1")));
                        will(returnValue(params2.get(0)));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/link1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); 
                        will(returnValue("aFakePolicyID"));  
                        inSequence(sequence1);
    					oneOf(pap).deletePolicy("aFakePolicyID"); 
    					inSequence(sequence1);
                        oneOf(binding).unbind(with(equal("/link1")));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("core.link.delete")));
                        inSequence(sequence1);
                        oneOf(indexing).remove(with(equal("/link1")));
                        inSequence(sequence1);
                    }
                });

            service.deleteLink("/link1");

            mockery.assertIsSatisfied();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }
}
