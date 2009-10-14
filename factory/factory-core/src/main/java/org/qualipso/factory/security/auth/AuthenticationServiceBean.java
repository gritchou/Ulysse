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

import java.security.Principal;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@Stateless(name = "Authentication", mappedName = "AuthenticationService")
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
    public String getConnectedIdentifier() throws AuthenticationServiceException {
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
