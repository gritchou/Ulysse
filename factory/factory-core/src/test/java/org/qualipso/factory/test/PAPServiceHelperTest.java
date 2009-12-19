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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.qualipso.factory.security.pap.PAPServiceException;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.xml.sax.InputSource;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 28 august 2009
 */
public class PAPServiceHelperTest {
	@Test
	public void testBuildOwnerPolicy() {
		try {
			String expectedPolicy = readPolicy("policy-15");
			String builtPolicy = PAPServiceHelper.buildOwnerPolicy("1", "/profiles/jayblanc", "/projects/project1");
			assertEquals(expectedPolicy, builtPolicy);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testBuildPolicy() {
		try {
			String expectedPolicy = readPolicy("policy-16");
			String builtPolicy = PAPServiceHelper.buildPolicy("1", "/profiles/jayblanc", "/projects/project1", new String[] { "read", "update" });
			assertEquals(expectedPolicy, builtPolicy);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testBuildGroupPolicy() {
		try {
			String expectedPolicy = readPolicy("policy-22");
			String builtPolicy = PAPServiceHelper.buildPolicy("1", "/projects/project1/groups/developers", "/projects/project1", new String[] { "read",
					"update" });
			assertEquals(expectedPolicy, builtPolicy);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testAddRuleToPolicy() {
		try {
			String expectedPolicy = readPolicy("policy-17");
			String builtPolicy = PAPServiceHelper.addRuleToPolicy(PAPServiceHelper.buildOwnerPolicy("1", "/profiles/jayblanc", "/projects/project1"),
					"/profiles/user1", new String[] { "read", "update" });
			assertEquals(expectedPolicy, builtPolicy);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (PAPServiceException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testAddGroupRuleToPolicy() {
		try {
			String expectedPolicy = readPolicy("policy-23");
			String builtPolicy = PAPServiceHelper.addRuleToPolicy(PAPServiceHelper.buildOwnerPolicy("1", "/profiles/jayblanc", "/projects/project1"),
					"/projects/project1/groups/developers", new String[] { "read", "update" });
			assertEquals(expectedPolicy, builtPolicy);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (PAPServiceException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testRemoveRuleFromPolicy() {
		try {
			String expectedPolicy = readPolicy("policy-18");
			String builtPolicy = PAPServiceHelper.removeRuleFromPolicy(PAPServiceHelper.addRuleToPolicy(PAPServiceHelper.buildOwnerPolicy("1",
					"/profiles/jayblanc", "/projects/project1"), "/profiles/user1", new String[] { "read", "update" }), "/profiles/jayblanc");
			assertEquals(expectedPolicy, builtPolicy);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (PAPServiceException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testRemoveGroupRuleFromPolicy() {
		try {
			String expectedPolicy = readPolicy("policy-24");
			String builtPolicy = PAPServiceHelper.removeRuleFromPolicy(PAPServiceHelper.addRuleToPolicy(PAPServiceHelper.buildOwnerPolicy("1",
					"/profiles/jayblanc", "/projects/project1"), "/projects/project1/groups/developers", new String[] { "read", "update" }),
					"/projects/project1/groups/developers");
			assertEquals(expectedPolicy, builtPolicy);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (PAPServiceException e) {
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
