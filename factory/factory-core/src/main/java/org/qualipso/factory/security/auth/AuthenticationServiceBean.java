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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.ejb3.annotation.SecurityDomain;

import org.qualipso.factory.FactoryNamingConvention;

import java.security.Principal;

import javax.annotation.Resource;

import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


/**
 * Implementation of the Authentication Service.<br/>
 * <br/>
 * Implementation is based on a EJB 3.0 Stateless Session Bean. Because external visibility, this bean implements
 * Remote interface and uses WebService annotations.
 * Bean name follow naming conventions of the factory and use the specific remote service prefix.<br/>
 * <br/>
 * Bean security is configured for JBoss AS 5 and rely on JAAS to ensure Authentication and Authorization of user.<br/>
 * <br/>
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@Stateless(name = AuthenticationService.SERVICE_NAME, mappedName = FactoryNamingConvention.LOCAL_SERVICE_PREFIX + AuthenticationService.SERVICE_NAME)
@SecurityDomain(value = "JBossWSDigest")
public class AuthenticationServiceBean implements AuthenticationService {
    private static Log logger = LogFactory.getLog(AuthenticationServiceBean.class);
    private SessionContext ctx;
    private EntityManager em;

    public AuthenticationServiceBean() {
    }

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEntityManager() {
        return this.em;
    }

    @Resource
    public void setSessionContext(SessionContext ctx) {
        this.ctx = ctx;
    }

    public SessionContext getSessionContext() {
        return this.ctx;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String getConnectedIdentifier() {
        logger.info("getConnectedIdentifier(...) called");

        Principal callerPrincipal = ctx.getCallerPrincipal();
        logger.debug("connected identifier : " + callerPrincipal.getName());

        return callerPrincipal.getName();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String[] listConnectedIdentifiers() throws AuthenticationServiceException {
        //TODO
        logger.warn("listConnectedIdentifiers(...) called ### NOT IMPLEMENTED");
        throw new AuthenticationServiceException("not implemented");
    }
}
