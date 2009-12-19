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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import org.qualipso.factory.security.pep.PEPServiceException;
import org.qualipso.factory.security.pep.PEPServiceHelper;
import org.qualipso.factory.security.pep.XACMLResponseStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 28 august 2009
 */
public class PEPServiceHelperTest {
    @Test
    public void testBuildRequest() {
        try {
            String expectedRequest = readRequest("request-04");
            String builtRequest = PEPServiceHelper.buildRequest("/node", "/subject", "read");
            System.out.println(builtRequest);
            assertEquals(expectedRequest, builtRequest);
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (PEPServiceException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testBuildRequestWithGroups() {
        try {
            String expectedRequest = readRequest("request-05");
            String builtRequest = PEPServiceHelper.buildRequest(new String[] { "/node", "/group1", "/group2" }, "/subject", "read");
            System.out.println(builtRequest);
            assertEquals(expectedRequest, builtRequest);
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (PEPServiceException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseResponse() {
        String response = "<Response><Result ResourceId=\"/\"><Decision>NotApplicable</Decision><Status><StatusCode Value=\"urn:oasis:names:tc:xacml:1.0:status:ok\"/></Status></Result></Response>";

        try {
            assertTrue(PEPServiceHelper.getResponseStatus(response).equals(XACMLResponseStatus.NOTAPPLICABLE));
        } catch (PEPServiceException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private String readRequest(String name) throws IOException {
        InputStream is = ClassLoader.getSystemResourceAsStream("requests/" + name + ".xml");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int nbRead = 0;

        while ((nbRead = is.read(buffer)) > -1) {
            baos.write(buffer, 0, nbRead);
        }

        return new String(baos.toByteArray());
    }
}
