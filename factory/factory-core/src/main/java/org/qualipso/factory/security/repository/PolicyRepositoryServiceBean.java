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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.ejb3.annotation.SecurityDomain;

import org.qualipso.factory.FactoryNamingConvention;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import javax.ejb.Stateless;


/**
 * Implementation of the PolicyRepsoitoryService.<br/>
 * <br/>
 * Implementation is based on a EJB 3.0 Stateless Session Bean. Because internal visibility only, this bean does not implement
 * Remote interface but only Local one.
 * Bean name follow naming conventions of the factory and use the specific local service prefix.<br/>
 * <br/>
 * Bean security is configured for JBoss AS 5 and rely on JAAS to ensure Authentication and Authorization of user.<br/>
 * <br/>
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 1 September 2009
 */

@Stateless(name = PolicyRepositoryService.SERVICE_NAME, mappedName = FactoryNamingConvention.LOCAL_SERVICE_PREFIX + PolicyRepositoryService.SERVICE_NAME)
@SecurityDomain(value = "JBossWSDigest")
public class PolicyRepositoryServiceBean implements PolicyRepositoryService {
    private static Log logger = LogFactory.getLog(PolicyRepositoryServiceBean.class);
    private static PolicyRepository repository;

    public PolicyRepositoryServiceBean() {
    }

    @PostConstruct
    public void init() throws PolicyRepositoryException {
    	if ( repository == null ) {
    		repository = new FilePolicyRepository();
    		repository.init();
    	}
    }

    @PreDestroy
    public void destroy() throws PolicyRepositoryException {
    	if ( repository != null ) {
    		repository.shutdown();
    	}
    }

    @Override
    public PolicyRepository getPolicyRepository() {
        return repository;
    }
}
