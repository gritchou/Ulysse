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
package org.qualipso.factory.client.test.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.ws.core.StubExt;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import org.qualipso.factory.client.ws.Bootstrap;
import org.qualipso.factory.client.ws.BootstrapServiceException_Exception;
import org.qualipso.factory.client.ws.Bootstrap_Service;
import org.qualipso.factory.client.ws.Membership;
import org.qualipso.factory.client.ws.Membership_Service;
import org.qualipso.factory.client.ws.Profile;

import java.util.Map;

import javax.xml.ws.BindingProvider;


public class MembershipServiceWSTest {
    private static Log logger = LogFactory.getLog(MembershipServiceWSTest.class);
    private Membership membership;

    public MembershipServiceWSTest() {
        membership = new Membership_Service().getMembershipServiceBeanPort();
        ((StubExt) membership).setConfigName("Standard WSSecurity Client");
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

    @Test
    public void testGetConnectedProfilePath() {
        try {
            String path = membership.getProfilePathForConnectedIdentifier();
            logger.debug("connected profile path : " + path);
            assertTrue(path.equals("/profiles/guest"));

            Map<String, Object> reqContext = ((BindingProvider) membership).getRequestContext();
            reqContext.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
            reqContext.put(BindingProvider.USERNAME_PROPERTY, "kermit");
            reqContext.put(BindingProvider.PASSWORD_PROPERTY, "thefrog");

            path = membership.getProfilePathForConnectedIdentifier();
            logger.debug("connected profile path : " + path);
            assertTrue(path.equals("/profiles/kermit"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testCreateProfile() {
        try {
            String path = membership.getProfilePathForConnectedIdentifier();
            logger.debug("connected profile path : " + path);
            assertTrue(path.equals("/profiles/guest"));

            Map<String, Object> reqContext = ((BindingProvider) membership).getRequestContext();
            
            membership.createProfile("kermit", "Kermit", "THE-FROG", 0);

            reqContext.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
            reqContext.put(BindingProvider.USERNAME_PROPERTY, "kermit");
            reqContext.put(BindingProvider.PASSWORD_PROPERTY, "thefrog");

            Profile profile = membership.readProfile("/profiles/kermit");
            assertTrue(profile.getFullname().equals("Kermit"));
            assertTrue(profile.getEmail().equals("THE-FROG"));

            membership.deleteProfile("/profiles/kermit");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
