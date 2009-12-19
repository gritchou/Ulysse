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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.ejb3.annotation.SecurityDomain;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.security.repository.PolicyRepositoryException;
import org.qualipso.factory.security.repository.PolicyRepositoryService;

import javax.annotation.Resource;

import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;


/**
 * Implementation of the PAP Service.<br/>
 * <br/>
 * Implementation is based on a EJB 3.0 Stateless Session Bean. Because internal visibility only, this bean does not implement
 * Remote interface but only Local one.
 * Bean name follow naming conventions of the factory and use the specific local service prefix.<br/>
 * <br/>
 * Bean security is configured for JBoss AS 5 and rely on JAAS to ensure Authentication and Authorization of user.<br/>
 * <br/>
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 16 june 2009
 */
@Stateless(name = PAPService.SERVICE_NAME, mappedName = FactoryNamingConvention.LOCAL_SERVICE_PREFIX + PAPService.SERVICE_NAME)
@SecurityDomain(value = "JBossWSDigest")
public class PAPServiceBean implements PAPService {
    private static Log logger = LogFactory.getLog(PAPServiceBean.class);
    private PolicyRepositoryService repositoryService;
    private SessionContext ctx;

    public PAPServiceBean() {
    }

    @Resource
    public void setSessionContext(SessionContext ctx) {
        this.ctx = ctx;
    }

    public SessionContext getSessionContext() {
        return this.ctx;
    }

    @EJB
    public void setPolicyRepositoryService(PolicyRepositoryService repository) {
        this.repositoryService = repository;
    }

    public PolicyRepositoryService getPolicyRepositoryService() {
        return repositoryService;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createPolicy(String id, String policy)
        throws PAPServiceException {
        logger.debug("createPolicy(...) called");
        logger.debug("params : id=" + id + ", policy=\r\n" + policy);

        try {
            repositoryService.getPolicyRepository().addPolicy(id, policy.getBytes());
        } catch (PolicyRepositoryException e) {
            ctx.setRollbackOnly();
            throw new PAPServiceException(e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deletePolicy(String id) throws PAPServiceException {
        logger.debug("deletePolicy(...) called");
        logger.debug("params : id=" + id);

        try {
            repositoryService.getPolicyRepository().deletePolicy(id);
        } catch (PolicyRepositoryException e) {
            ctx.setRollbackOnly();
            throw new PAPServiceException(e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String getPolicy(String id) throws PAPServiceException {
        logger.debug("getPolicy(...) called");
        logger.debug("params : id=" + id);

        try {
            return new String(repositoryService.getPolicyRepository().getPolicy(id));
        } catch (PolicyRepositoryException e) {
            throw new PAPServiceException(e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updatePolicy(String id, String policy)
        throws PAPServiceException {
        logger.debug("updatePolicy(...) called");
        logger.debug("params : id=" + id + ", policy=\r\n" + policy);

        try {
            repositoryService.getPolicyRepository().updatePolicy(id, policy.getBytes());
        } catch (PolicyRepositoryException e) {
            ctx.setRollbackOnly();
            throw new PAPServiceException(e);
        }
    }
}
