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
package org.qualipso.factory.client.test.sb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.security.auth.callback.UsernamePasswordHandler;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.bootstrap.BootstrapService;
import org.qualipso.factory.bootstrap.BootstrapServiceException;
import org.qualipso.factory.client.test.AllTests;
import org.qualipso.factory.membership.MembershipService;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.security.auth.login.LoginContext;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
public class MembershipServiceSBTest {
    private static Log logger = LogFactory.getLog(MembershipServiceSBTest.class);
    private static Context ctx;
    private MembershipService service;

    public void setMembershipService(MembershipService service) {
        this.service = service;
    }

    public MembershipService getMembershipService() {
        return service;
    }

    @BeforeClass
    public static void before() throws NamingException {
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
        ctx = new InitialContext(properties);

        BootstrapService bootstrap = (BootstrapService) ctx.lookup(FactoryNamingConvention.getJNDINameForService("bootstrap"));

        try {
            bootstrap.bootstrap();
        } catch (BootstrapServiceException e) {
            logger.error(e);
        }
    }

    @Test
    public void testGetConnectedUserIdentifier() {
        logger.debug("testing get connected user identifier");

        try {
            MembershipService service = (MembershipService) ctx.lookup(FactoryNamingConvention.getJNDINameForService("membership"));
            this.setMembershipService(service);

            String connectedProfile = getMembershipService().getProfilePathForConnectedIdentifier();
            logger.debug("connected user : " + connectedProfile);
            assertTrue(connectedProfile.equals("/profiles/guest"));

            LoginContext lc = new LoginContext("qualipso", new UsernamePasswordHandler("root", AllTests.ROOT_ACCOUNT_PASS));
            lc.login();

            connectedProfile = getMembershipService().getProfilePathForConnectedIdentifier();
            logger.info("connected user : " + connectedProfile);
            assertTrue(connectedProfile.equals("/profiles/root"));

            lc.logout();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }
}
