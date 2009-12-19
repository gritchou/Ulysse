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
package org.qualipso.factory.security.pdp;

import javax.ejb.Local;


/**
 * PDP Service allow to query the internal policy repository in order to produce access control response.<br/>
 * <br/>
 * This internal service is not visible remotely and should only be used by trusted services. <br/>
 * <br/>
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@Local
public interface PDPService {
    public static final String SERVICE_NAME = "pdp";

    /**
     * Send an XACML request to the XACML engine.
     *
     * @param request a String representation of the XACML request
     * @return a String representation of the XACML response.
     * @throws PDPServiceException
     */
    public String query(String request) throws PDPServiceException;
}
