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

import org.junit.Test;

import org.qualipso.factory.security.pap.PAPServiceBean;
import org.qualipso.factory.security.pap.PAPServiceException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.util.UUID;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 2 September 2009
 */
public class PAPServiceTest extends BaseSessionBeanFixture<PAPServiceBean> {
    private static Log logger = LogFactory.getLog(PAPServiceTest.class);
    @SuppressWarnings("unchecked")
    private static final Class[] usedBeans = {  };

    public PAPServiceTest() {
        super(PAPServiceBean.class, usedBeans);
    }

    public void setUp() throws Exception {
        super.setUp();
        logger.debug("setting up pap service");
    }

    @Test
    public void testGetPolicyError() {
        try {
            getBeanToTest().getPolicy("unexistingPolicyID");
            fail("this policy should not exists");
        } catch (PAPServiceException pre) {
            //
        }
    }

    @Test
    public void testAddPolicy() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-12.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            String id = UUID.randomUUID().toString();
            getBeanToTest().createPolicy(id, baos.toString());

            try {
                getBeanToTest().getPolicy(id);
            } catch (PAPServiceException pse) {
                fail("unable to get the created policy : " + pse.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdatePolicy() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-12.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            String id = UUID.randomUUID().toString();
            getBeanToTest().createPolicy(id, baos.toString());

            is = ClassLoader.getSystemResourceAsStream("policies/policy-13.xml");
            baos = new ByteArrayOutputStream();
            buffer = new byte[1024];
            nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            getBeanToTest().createPolicy(id, baos.toString());

            try {
                String policy = new String(getBeanToTest().getPolicy(id));
                assertTrue(policy.indexOf("policy-02") != -1);
            } catch (PAPServiceException pse) {
                fail("unable to get the updated policy : " + pse.getMessage());
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDeletePolicy() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("policies/policy-12.xml");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nbRead = 0;

            while ((nbRead = is.read(buffer)) > -1) {
                baos.write(buffer, 0, nbRead);
            }

            String id = UUID.randomUUID().toString();
            getBeanToTest().createPolicy(id, baos.toString());

            try {
                getBeanToTest().getPolicy(id);
            } catch (PAPServiceException pse) {
                fail("unable to get the created policy : " + pse.getMessage());
            }

            getBeanToTest().deletePolicy(id);

            try {
                getBeanToTest().getPolicy(id);
                fail("policy should not exists after deletion.");
            } catch (PAPServiceException pse) {
                //
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
