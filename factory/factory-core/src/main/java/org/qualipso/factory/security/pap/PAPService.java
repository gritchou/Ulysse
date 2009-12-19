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
package org.qualipso.factory.security.pap;

import javax.ejb.Local;


/**
 * PAP Service allows to manage the internal XACML policy repository.<br/>
 * <br/>
 * This internal service is not visible remotely and should only be used by trusted services. <br/>
 * <br/>
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@Local
public interface PAPService {
    public static final String SERVICE_NAME = "pap";

    /**
     * Create a policy in the repository.
     *
     * @param policyId the id of the policy to create.
     * @param policy a String representation of the policy.
     * @throws PAPServiceException
     */
    public void createPolicy(String policyId, String policy)
        throws PAPServiceException;

    /**
     * Update an existing policy of the repository.
     *
     * @param policyId the id of the policy to update.
     * @param policy a String representation of the new policy.
     * @throws PAPServiceException
     */
    public void updatePolicy(String policyId, String policy)
        throws PAPServiceException;

    /**
     * Delete an existing policy from the repository.
     *
     * @param policyId the id of the policy to update.
     * @throws PAPServiceException
     */
    public void deletePolicy(String policyId) throws PAPServiceException;

    /**
     * Retrieve a policy from the repository
     *
     * @param policyId the id of the policy to update.
     * @return a String representation of the policy.
     * @throws PAPServiceException
     */
    public String getPolicy(String policyId) throws PAPServiceException;
}
