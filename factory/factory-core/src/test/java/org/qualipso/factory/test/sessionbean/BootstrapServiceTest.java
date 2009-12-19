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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.qualipso.factory.binding.entity.Node;
import org.qualipso.factory.bootstrap.BootstrapServiceBean;
import org.qualipso.factory.core.entity.File;
import org.qualipso.factory.core.entity.Folder;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.ssh.SSHService;

import com.bm.testsuite.BaseSessionBeanFixture;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 4 September 2009
 */
public class BootstrapServiceTest extends BaseSessionBeanFixture<BootstrapServiceBean> {
    private static Log logger = LogFactory.getLog(BootstrapServiceTest.class);
    @SuppressWarnings("unchecked")
    private static final Class[] usedBeans = { Folder.class, File.class, Profile.class, Node.class };

    private Mockery mockery;
    private SSHService ssh;
    
    public BootstrapServiceTest() {
        super(BootstrapServiceBean.class, usedBeans);
    }
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        logger.debug("injecting mock partners session beans DTC");
        mockery = new Mockery();
        ssh = mockery.mock(SSHService.class);
        getBeanToTest().setSSHService(ssh);
    }

    @Test
    public void testBootstrap() {
        logger.debug("Bootstrap Service Version : " + BootstrapServiceBean.VERSION);
    }
}
