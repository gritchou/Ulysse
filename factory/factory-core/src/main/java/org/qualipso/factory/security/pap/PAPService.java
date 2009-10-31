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
package org.qualipso.factory.security.pap;

import javax.ejb.Local;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@Local
public interface PAPService {
	public void createPolicy(String policyId, String policy) throws PAPServiceException;

	public void updatePolicy(String policyId, String policy) throws PAPServiceException;

	public void deletePolicy(String policyId) throws PAPServiceException;

	public String getPolicy(String policyId) throws PAPServiceException;
}
