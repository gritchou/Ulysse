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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.ejb3.annotation.SecurityDomain;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.security.pdp.PDPService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;


/**
 * PEP Service implementation.<br/>
 * <br/>
 * The implementation relies on a Helper class to generate requests and interpret responses.<br/>
 * <br/>
 * Implementation is based on a EJB 3.0 Stateless Session Bean. Because internal visibility only, this bean does not implement
 * Remote interface but only Local one.
 * Bean name follow naming conventions of the factory and use the specific local service prefix.<br/>
 * <br/>
 * Bean security is configured for JBoss AS 5 and rely on JAAS to ensure Authentication and Authorization of user.<br/>
 * <br/>
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 16 june 2009
 */
@Stateless(name = PEPService.SERVICE_NAME, mappedName = FactoryNamingConvention.LOCAL_SERVICE_PREFIX + PEPService.SERVICE_NAME)
@SecurityDomain(value = "JBossWSDigest")
public class PEPServiceBean implements PEPService {
    private static Log logger = LogFactory.getLog(PEPServiceBean.class);
    private PDPService pdp;

    public PEPServiceBean() {
    }

    @EJB
    public void setPDPService(PDPService pdp) {
        this.pdp = pdp;
    }

    public PDPService getPDPService() {
        return this.pdp;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void checkSecurity(String subject, String object, String action)
    	throws PEPServiceException, AccessDeniedException {
        logger.info("checkSecurity(...) called");
        logger.debug("params : subject=" + subject + ", object=" + object + ", action=" + action);

        String[] subjects = new String[] { subject };
        checkSecurity(subjects, object, action);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void checkSecurity(String[] subjects, String object, String action)
        throws PEPServiceException, AccessDeniedException {
        logger.info("checkSecurity(...) called");
        logger.debug("params : subjects=" + subjects + ", object=" + object + ", action=" + action);

        String request = PEPServiceHelper.buildRequest(subjects, object, action);
        logger.debug("request built : ");
        logger.debug(request);

        String response;

        try {
            response = pdp.query(request);
        } catch (Exception e) {
            throw new PEPServiceException(e);
        }

        logger.debug("response received : ");
        logger.debug(response);

        XACMLResponseStatus status = PEPServiceHelper.getResponseStatus(response);

        if (!status.equals(XACMLResponseStatus.PERMIT)) {
        	throw new AccessDeniedException("Response status is : " + status);
        }
    }
}
