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
package org.qualipso.factory.client.test;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.qualipso.factory.client.test.sb.AllSBTests;
import org.qualipso.factory.client.test.ws.AllWSTests;
import org.qualipso.factory.client.test.performance.AllPerfTest;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@RunWith(Suite.class)
@SuiteClasses(value =  {
    AllSBTests.class, AllWSTests.class, AllPerfTest.class}
)
public class AllTests {
    public static final String ROOT_ACCOUNT_PASS = "root";
}
