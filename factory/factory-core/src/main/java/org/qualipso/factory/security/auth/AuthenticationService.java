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
package org.qualipso.factory.security.auth;

import javax.ejb.Local;


/**
 * Authentication Service provides a way to know who is identify.<br/>
 * <br/>
 * The service should also be able to provide informations about active identified users in the system.<br/>
 * <br/>
 * This internal service is not visible remotely and should only be used by trusted services. <br/>
 * <br/>
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@Local
public interface AuthenticationService {
    public static final String SERVICE_NAME = "authentication";

    /**
     * Retrieve the connected identifier in the system for this call.
     *
     * @return a String representation of the connected identifier.
     * @throws AuthenticationServiceException
     */
    public String getConnectedIdentifier();

    /**
     * Retrieve the list of all active connected identifier on the system.<br/>
     * <br/>
     * As the whole system is stateless, connected identifiers list should change very quickly and is only a very
     * instant representation of connected identifiers.<br/<
     * <br/>
     * @return a String array of all the connected identifiers.
     * @throws AuthenticationServiceException
     */
    public String[] listConnectedIdentifiers() throws AuthenticationServiceException;
}
