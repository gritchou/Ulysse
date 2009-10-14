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
 *
 */
package org.qualipso.factory.test.sessionbean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.security.auth.AuthenticationService;
import org.qualipso.factory.security.auth.AuthenticationServiceBean;

import com.bm.testsuite.BaseSessionBeanFixture;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
public class AuthenticationServiceTest extends BaseSessionBeanFixture<AuthenticationServiceBean> {
    
	private static Log logger = LogFactory.getLog(AuthenticationServiceTest.class);
    
	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = { };
    
    public AuthenticationServiceTest() {
    	super(AuthenticationServiceBean.class, usedBeans);
    }
    
    public void testGetConnectedUserIdentifier() {
        logger.debug("testing getConnectedIdentifier()");

        try {
            AuthenticationService service = getBeanToTest();
            logger.info("connected user : " + service.getConnectedIdentifier());
            assertTrue(service.getConnectedIdentifier().equals("anonymouse"));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //fail(e.getMessage());
        }
    }

    public void testListConnectedUsersIdentifier() {
        logger.debug("testing list connected users identifier");

        try {
            AuthenticationService service = getBeanToTest();
            logger.info("connected users : ");

            for (String user : service.listConnectedIdentifiers()) {
                logger.info(" - user : " + user);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //fail(e.getMessage());
        }
    }
}
