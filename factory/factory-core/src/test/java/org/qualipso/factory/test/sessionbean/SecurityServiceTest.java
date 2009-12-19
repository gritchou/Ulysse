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
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.SecurityService;
import org.qualipso.factory.security.SecurityServiceBean;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;
import static org.qualipso.factory.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 Septembre 2009
 */
public class SecurityServiceTest extends BaseSessionBeanFixture<SecurityServiceBean> {
    private static Log logger = LogFactory.getLog(SecurityServiceTest.class);
    @SuppressWarnings("unchecked")
    private static final Class[] usedBeans = {  };
    private Mockery mockery;
    private BindingService binding;
    private MembershipService membership;
    private PEPService pep;
    private PAPService pap;
    private NotificationService notification;

    public SecurityServiceTest() {
        super(SecurityServiceBean.class, usedBeans);
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

    public void testGetSecurityPolicy() {
        logger.debug("testing testGetSecurityPolicy(...)");

        final Sequence sequence1 = mockery.sequence("sequence1");

        try {
            final String expectedPolicy = readPolicy("policy-15");

            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(membership).getConnectedIdentifierSubjects();
                        will(returnValue(new String[] { "/profiles/jayblanc" }));
                        inSequence(sequence1);
                        oneOf(pep).checkSecurity(with(equal(new String[] { "/profiles/jayblanc" })), with(equal("/testnode")), with(equal("read")));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/testnode")));
                        will(returnValue(new FactoryResourceIdentifier("FakeService", "FakeResource", "FakeID")));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/testnode")), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false)));
                        will(returnValue("aFakePolicyID"));
                        inSequence(sequence1);
                        oneOf(pap).getPolicy(with(equal("aFakePolicyID")));
                        will(returnValue(expectedPolicy));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("security.resource.get-security-policy")));
                        inSequence(sequence1);
                    }
                });

            SecurityService service = getBeanToTest();
            String policy = service.getSecurityPolicy("/testnode");

            assertEquals(expectedPolicy, policy);

            mockery.assertIsSatisfied();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testAddSecurityRule() {
        logger.debug("testing testAddSecurityRule(...)");

        final Sequence sequence1 = mockery.sequence("sequence1");

        try {
            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/projects/project1")));
                        will(returnValue(new FactoryResourceIdentifier("FakeService", "FakeResource", "FakeID")));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.OWNER)), with(equal(false)));
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/profiles/jayblanc")));
                        will(returnValue(new FactoryResourceIdentifier(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "ProfileID")));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.OWNER)), with(equal(false)));
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                    }
                });

            SecurityService service = getBeanToTest();
            service.addSecurityRule("/projects/project1", "/profiles/jayblanc", "read,update");

            fail("This should be forbidden to add a rule for the owner because he already has a dedicated rule");
            mockery.assertIsSatisfied();
        } catch (Exception e) {
            //
        }

        try {
            final String policy15 = readPolicy("policy-15");
            final String expectedPolicy = readPolicy("policy-17");

            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/projects/project1")));
                        will(returnValue(new FactoryResourceIdentifier("FakeService", "FakeResource", "FakeID")));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.OWNER)), with(equal(false)));
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/profiles/jayblanc")));
                        will(returnValue(new FactoryResourceIdentifier(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "ProfileID")));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.OWNER)), with(equal(false)));
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/profiles/user1")));
                        will(returnValue(new FactoryResourceIdentifier(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "ProfileID")));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false)));
                        will(returnValue("aFakePolicyID"));
                        inSequence(sequence1);
                        oneOf(pap).getPolicy(with(equal("aFakePolicyID")));
                        will(returnValue(policy15));
                        inSequence(sequence1);
                        oneOf(pap).updatePolicy(with(equal("aFakePolicyID")), with(equal(expectedPolicy)));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("security.resource.add-security-rule")));
                        inSequence(sequence1);
                    }
                });

            SecurityService service = getBeanToTest();
            service.addSecurityRule("/projects/project1", "/profiles/user1", "read,update");

            mockery.assertIsSatisfied();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testEditSecurityRule() {
        logger.debug("testing testEditSecurityRule(...)");

        final Sequence sequence1 = mockery.sequence("sequence1");

        try {
            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/projects/project1")));
                        will(returnValue(new FactoryResourceIdentifier("FakeService", "FakeResource", "FakeID")));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.OWNER)), with(equal(false)));
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/profiles/jayblanc")));
                        will(returnValue(new FactoryResourceIdentifier(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "ProfileID")));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.OWNER)), with(equal(false)));
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                    }
                });

            SecurityService service = getBeanToTest();
            service.editSecurityRule("/projects/project1", "/profiles/jayblanc", "read");

            fail("This should be forbidden to edit the owner rule because he already has a dedicated rule");
            mockery.assertIsSatisfied();
        } catch (Exception e) {
            //
        }

        try {
            final String policy17 = readPolicy("policy-17");
            final String expectedPolicy = readPolicy("policy-19");

            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/projects/project1")));
                        will(returnValue(new FactoryResourceIdentifier("FakeService", "FakeResource", "FakeID")));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.OWNER)), with(equal(false)));
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/profiles/jayblanc")));
                        will(returnValue(new FactoryResourceIdentifier(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "ProfileID")));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.OWNER)), with(equal(false)));
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false)));
                        will(returnValue("aFakePolicyID"));
                        inSequence(sequence1);
                        oneOf(pap).getPolicy(with(equal("aFakePolicyID")));
                        will(returnValue(policy17));
                        inSequence(sequence1);
                        oneOf(pap).updatePolicy(with(equal("aFakePolicyID")), with(equal(expectedPolicy)));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("security.resource.edit-security-rule")));
                        inSequence(sequence1);
                    }
                });

            SecurityService service = getBeanToTest();
            service.editSecurityRule("/projects/project1", "/profiles/user1", "read");

            mockery.assertIsSatisfied();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testRemoveSecurityRule() {
        logger.debug("testing testRemoveSecurityRule(...)");

        final Sequence sequence1 = mockery.sequence("sequence1");

        try {
            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/projects/project1")));
                        will(returnValue(new FactoryResourceIdentifier("FakeService", "FakeResource", "FakeID")));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.OWNER)), with(equal(false)));
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/profiles/jayblanc")));
                        will(returnValue(new FactoryResourceIdentifier(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "ProfileID")));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.OWNER)), with(equal(false)));
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                    }
                });

            SecurityService service = getBeanToTest();
            service.removeSecurityRule("/projects/project1", "/profiles/jayblanc");

            fail("This should be forbidden to remove the owner rule");
            mockery.assertIsSatisfied();
        } catch (Exception e) {
            //
        }

        try {
            final String policy17 = readPolicy("policy-17");
            final String expectedPolicy = readPolicy("policy-20");

            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/projects/project1")));
                        will(returnValue(new FactoryResourceIdentifier("FakeService", "FakeResource", "FakeID")));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.OWNER)), with(equal(false)));
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/profiles/jayblanc")));
                        will(returnValue(new FactoryResourceIdentifier(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "ProfileID")));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.OWNER)), with(equal(false)));
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false)));
                        will(returnValue("aFakePolicyID"));
                        inSequence(sequence1);
                        oneOf(pap).getPolicy(with(equal("aFakePolicyID")));
                        will(returnValue(policy17));
                        inSequence(sequence1);
                        oneOf(pap).updatePolicy(with(equal("aFakePolicyID")), with(equal(expectedPolicy)));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("security.resource.remove-security-rule")));
                        inSequence(sequence1);
                    }
                });

            SecurityService service = getBeanToTest();
            service.removeSecurityRule("/projects/project1", "/profiles/user1");

            mockery.assertIsSatisfied();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    public void testChangeOwner() {
        logger.debug("testing testChangeOwner(...)");

        final Sequence sequence1 = mockery.sequence("sequence1");

        try {
            final String policy17 = readPolicy("policy-17");
            final String expectedPolicy = readPolicy("policy-21");

            mockery.checking(new Expectations() {

                    {
                        oneOf(membership).getProfilePathForConnectedIdentifier();
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/projects/project1")));
                        will(returnValue(new FactoryResourceIdentifier("FakeService", "FakeResource", "FakeID")));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.OWNER)), with(equal(false)));
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/profiles/jayblanc")));
                        will(returnValue(new FactoryResourceIdentifier(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "ProfileID")));
                        inSequence(sequence1);
                        oneOf(binding).lookup(with(equal("/profiles/userlambda")));
                        will(returnValue(new FactoryResourceIdentifier(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "ProfileIDLambda")));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.OWNER)), with(equal(false)));
                        will(returnValue("/profiles/jayblanc"));
                        inSequence(sequence1);
                        oneOf(binding).getProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false)));
                        will(returnValue("aFakePolicyID"));
                        inSequence(sequence1);
                        oneOf(pap).getPolicy(with(equal("aFakePolicyID")));
                        will(returnValue(policy17));
                        inSequence(sequence1);
                        oneOf(pap).updatePolicy(with(equal("aFakePolicyID")), with(equal(expectedPolicy)));
                        inSequence(sequence1);
                        oneOf(binding)
                            .setProperty(with(equal("/projects/project1")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/userlambda")));
                        inSequence(sequence1);
                        oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("security.resource.change-owner")));
                        inSequence(sequence1);
                    }
                });

            SecurityService service = getBeanToTest();
            service.changeOwner("/projects/project1", "/profiles/userlambda");

            mockery.assertIsSatisfied();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    private String readPolicy(String name) throws IOException {
        InputStream is = ClassLoader.getSystemResourceAsStream("policies/" + name + ".xml");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int nbRead = 0;

        while ((nbRead = is.read(buffer)) > -1) {
            baos.write(buffer, 0, nbRead);
        }

        return new String(baos.toByteArray());
    }
}
