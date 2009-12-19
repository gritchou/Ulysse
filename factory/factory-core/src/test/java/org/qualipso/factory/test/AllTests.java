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
package org.qualipso.factory.test;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.qualipso.factory.test.entity.FileTest;
import org.qualipso.factory.test.entity.FolderTest;
import org.qualipso.factory.test.entity.GroupTest;
import org.qualipso.factory.test.entity.LinkTest;
import org.qualipso.factory.test.entity.NodeTest;
import org.qualipso.factory.test.entity.ProfileTest;
import org.qualipso.factory.test.sessionbean.AuthenticationServiceTest;
import org.qualipso.factory.test.sessionbean.BindingServiceTest;
import org.qualipso.factory.test.sessionbean.BootstrapServiceTest;
import org.qualipso.factory.test.sessionbean.BrowserServiceTest;
import org.qualipso.factory.test.sessionbean.CoreServiceTest;
import org.qualipso.factory.test.sessionbean.EventQueueRulesServiceTest;
import org.qualipso.factory.test.sessionbean.EventQueueServiceTest;
import org.qualipso.factory.test.sessionbean.IndexingServiceTest;
import org.qualipso.factory.test.sessionbean.MembershipServiceTest;
import org.qualipso.factory.test.sessionbean.NotificationServiceTest;
import org.qualipso.factory.test.sessionbean.PAPServiceTest;
import org.qualipso.factory.test.sessionbean.SecurityServiceTest;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@RunWith(Suite.class)
@SuiteClasses(value =  {
    PathHelperTest.class, PAPServiceHelperTest.class, PEPServiceHelperTest.class, FilePolicyRepositoryTest.class, NodeTest.class, ProfileTest.class, GroupTest.class, FolderTest.class, FileTest.class, LinkTest.class, BootstrapServiceTest.class, AuthenticationServiceTest.class, BindingServiceTest.class, PAPServiceTest.class, CoreServiceTest.class, BrowserServiceTest.class, MembershipServiceTest.class, SecurityServiceTest.class, IndexingServiceTest.class, EventQueueServiceTest.class, EventQueueRulesServiceTest.class, NotificationServiceTest.class}
)
public class AllTests {
}
