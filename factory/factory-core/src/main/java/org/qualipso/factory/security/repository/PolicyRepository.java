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
package org.qualipso.factory.security.repository;

import com.sun.xacml.EvaluationCtx;

import java.util.List;
import java.util.Map;


/**
 * A PolicyRepository is a kind of data Store but a little more specific to XACML policies.
 * It allows basic CRUD operations but is also capable of
 * getting policies depending on the evaluation context of an XACML request in order to optimize relevant
 * policies retrieving.<br/>
 * <br/>
 * Using this interface, policy repository could be located on the file system, in an XACML database or in an existing XACML
 * policy store.<br/>
 * <br/>
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 28 august 2009
 */
public abstract class PolicyRepository {
    /**
     * Override this method to produce specific repository initialization.
     *
     * @throws PolicyRepositoryException
     */
    public void init() throws PolicyRepositoryException {
    }

    /**
     * Override this method to produce specific repository shutdown phase.
     *
     * @throws PolicyRepositoryException
     */
    public void shutdown() throws PolicyRepositoryException {
    }

    /**
     * Add a policy in the repository.
     *
     * @param id the policy id,
     * @param policy a byte array representing the policy.
     * @throws PolicyRepositoryException
     */
    abstract public void addPolicy(String id, byte[] policy)
        throws PolicyRepositoryException, PolicyAlreadyExistsException;

    /**
     * Delete a policy in the repository.
     *
     * @param id the policy id,
     * @throws PolicyRepositoryException
     */
    abstract public void deletePolicy(String id) throws PolicyRepositoryException, PolicyNotFoundException;

    /**
     * Update a policy in the repository.
     *
     * @param id the policy id,
     * @param policy a byte array representing the new policy.
     * @throws PolicyRepositoryException
     */
    abstract public void updatePolicy(String id, byte[] policy)
        throws PolicyRepositoryException, PolicyNotFoundException;

    /**
     * Get a policy from the repository.
     *
     * @param id the policy id to retrieve.
     * @return a byte array representing the policy.
     * @throws PolicyRepositoryException
     */
    abstract public byte[] getPolicy(String id) throws PolicyRepositoryException, PolicyNotFoundException;

    /**
     * List all the policies ids of this repository.
     *
     * @return a List of policy id
     * @throws PolicyRepositoryException
     */
    abstract public List<String> listPolicies() throws PolicyRepositoryException;

    /**
     * Get all policies relevant to this EvaluationContext.<br/>
     * <br/>
     * A good implementation should use this context to produce a pre filtered set of policies. The XACML engine should
     * respond faster in this way. XMLDB based implementation should also be able to retrieve relevant policies using this
     * evaluation context.
     *
     * @param eval the evaluation context of a request.
     * @return a Map of policy id and policy content.
     * @throws PolicyRepositoryException
     */
    abstract public Map<String, byte[]> getPolicies(EvaluationCtx eval)
        throws PolicyRepositoryException;
    
    abstract public void purge() throws PolicyRepositoryException;
}
