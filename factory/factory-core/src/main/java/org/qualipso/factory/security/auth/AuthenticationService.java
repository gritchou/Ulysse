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
package org.qualipso.factory.security.auth;

import javax.ejb.Local;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@Local
public interface AuthenticationService {
	
	public static final String SERVICE_NAME = "authentication";
	
	public String getConnectedIdentifier() throws AuthenticationServiceException;

    public String[] listConnectedIdentifiers() throws AuthenticationServiceException;
}
