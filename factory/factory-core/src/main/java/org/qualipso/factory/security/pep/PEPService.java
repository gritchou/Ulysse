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
package org.qualipso.factory.security.pep;

import javax.ejb.Local;


/**
 * PEP Service allow to send queries to the PDP. It gives abstraction of underlying XACML request format and is capable
 * of interpreting XACML response to produce exception in case of lake of permission.<br/>
 * <br/>
 * This internal service is not visible remotely and should only be used by trusted services. <br/>
 * <br/>
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 26 May 2009
 */
@Local
public interface PEPService {
    public static final String SERVICE_NAME = "pep";

    /**
     * Send a query to the PDP.
     *
     * @param subject the subject
     * @param object the object
     * @param action the action
     * @throws PEPServiceException if action is not allowed
     */
    public void checkSecurity(String subject, String object, String action)
        throws PEPServiceException, AccessDeniedException;

    /**
     * Send a query to the PDP.
     *
     * @param subject a String array of multiple subjects (for users with groups for example)
     * @param object the object
     * @param action the action
     * @throws PEPServiceException if action is not allowed
     */
    public void checkSecurity(String[] subjects, String object, String action)
    	throws PEPServiceException, AccessDeniedException;
}
