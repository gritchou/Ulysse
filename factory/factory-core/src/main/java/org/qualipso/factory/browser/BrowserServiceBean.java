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
package org.qualipso.factory.browser;

import java.util.Vector;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.Factory;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.AccessDeniedException;
import org.qualipso.factory.security.pep.PEPService;


/**
 * Implementation fo the BrowserService.<br/>
 * <br/>
 * Implementation is based on a EJB 3.0 Stateless Session Bean. Because external visibility, this bean implements
 * Remote interface and uses WebService annotations.
 * Bean name follow naming conventions of the factory and use the specific remote service prefix.<br/>
 * <br/>
 * Bean security is configured for JBoss AS 5 and rely on JAAS to ensure Authentication and Autorization of user.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 4 august 2009
 */
@Stateless(name = BrowserService.SERVICE_NAME, mappedName = FactoryNamingConvention.SERVICE_PREFIX + BrowserService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.browser.BrowserService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE +
BrowserServiceBean.SERVICE_NAME, serviceName = BrowserService.SERVICE_NAME)
@WebContext(contextRoot = FactoryNamingConvention.WEB_SERVICE_CORE_MODULE_CONTEXT, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX +
BrowserService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class BrowserServiceBean implements BrowserService {
    private static Log logger = LogFactory.getLog(BrowserServiceBean.class);
    private BindingService binding;
    private PEPService pep;
    private PAPService pap;
    private NotificationService notification;
    private MembershipService membership;
    private SessionContext ctx;
    private EntityManager em;

    public BrowserServiceBean() {
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

    @EJB
    public void setBindingService(BindingService binding) {
        this.binding = binding;
    }

    public BindingService getBindingService() {
        return this.binding;
    }

    @EJB
    public void setPEPService(PEPService pep) {
        this.pep = pep;
    }

    public PEPService getPEPService() {
        return this.pep;
    }

    @EJB
    public void setPAPService(PAPService pap) {
        this.pap = pap;
    }

    public PAPService getPAPService() {
        return this.pap;
    }

    @EJB
    public void setNotificationService(NotificationService notification) {
        this.notification = notification;
    }

    public NotificationService getNotificationService() {
        return this.notification;
    }

    @EJB
    public void setMembershipService(MembershipService membership) {
        this.membership = membership;
    }

    public MembershipService getMembershipService() {
        return this.membership;
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FactoryResource findResource(String path) throws BrowserServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
        logger.info("findResource(...) called");
        logger.debug("params : path=" + path);

        try {
            PathHelper.valid(path);

            String npath = PathHelper.normalize(path);

            String caller = membership.getProfilePathForConnectedIdentifier();

            FactoryResourceIdentifier identifier = binding.lookup(npath);

            FactoryService service = Factory.findService(identifier.getService());

            notification.throwEvent(new Event(npath, caller, FactoryResource.RESOURCE_NAME,
                    Event.buildEventType(BrowserService.SERVICE_NAME, FactoryResource.RESOURCE_NAME, "find"), ""));

            return service.findResource(npath);
        } catch (AccessDeniedException e) {
        	throw e;
        } catch (InvalidPathException e) {
        	throw e;
        } catch (PathNotFoundException e) {
        	throw e;
        } catch (FactoryException e) {
            throw new BrowserServiceException("unable to find a resource for path: " + path, e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public boolean hasChildren(String path) throws BrowserServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
        logger.info("hasChildren(...) called");
        logger.debug("params : path=" + path);

        try {
            PathHelper.valid(path);

            String npath = PathHelper.normalize(path);

            String caller = membership.getProfilePathForConnectedIdentifier();

            pep.checkSecurity(membership.getConnectedIdentifierSubjects(), npath, "read");

            String[] children = binding.list(npath);

            notification.throwEvent(new Event(npath, caller, FactoryResource.RESOURCE_NAME,
                    Event.buildEventType(BrowserService.SERVICE_NAME, FactoryResource.RESOURCE_NAME, "has-children"), ""));

            if (children.length > 0) {
                return true;
            } else {
                return false;
            }
        } catch (AccessDeniedException e) {
        	throw e;
        } catch (InvalidPathException e) {
        	throw e;
        } catch (PathNotFoundException e) {
        	throw e;
        } catch (FactoryException e) {
            throw new BrowserServiceException("unable to test if path: " + path + " has children", e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String[] listChildren(String path) throws BrowserServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
        logger.info("listChildren(...) called");
        logger.debug("params : path=" + path);

        try {
            PathHelper.valid(path);

            String npath = PathHelper.normalize(path);

            String caller = membership.getProfilePathForConnectedIdentifier();

            pep.checkSecurity(membership.getConnectedIdentifierSubjects(), npath, "read");

            String[] children = binding.list(npath);

            notification.throwEvent(new Event(npath, caller, FactoryResource.RESOURCE_NAME,
                    Event.buildEventType(BrowserService.SERVICE_NAME, FactoryResource.RESOURCE_NAME, "list-children"), ""));

            return children;
        } catch (AccessDeniedException e) {
        	throw e;
        } catch (InvalidPathException e) {
        	throw e;
        } catch (PathNotFoundException e) {
        	throw e;
        } catch (FactoryException e) {
            throw new BrowserServiceException("unable to list children for path: " + path, e);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String[] listChildrenOfType(String path, String servicePattern, String typePattern)
        throws BrowserServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
        logger.info("listChildrenOfType(...) called");
        logger.debug("params : path=" + path + ", typePattern=" + typePattern);

        try {
            PathHelper.valid(path);

            String npath = PathHelper.normalize(path);

            String caller = membership.getProfilePathForConnectedIdentifier();

            pep.checkSecurity(membership.getConnectedIdentifierSubjects(), npath, "read");

            String[] children = binding.list(npath);
            Vector<String> matchingChilds = new Vector<String>();

            for (String child : children) {
                FactoryResourceIdentifier identifier = binding.lookup(child);
                logger.debug(identifier.getService() + ".matches(" + servicePattern + ") && " + identifier.getType() + ".matches(" + typePattern + ")");

                if (identifier.getService().matches(servicePattern) && identifier.getType().matches(typePattern)) {
                    matchingChilds.add(child);
                }
            }

            notification.throwEvent(new Event(npath, caller, FactoryResource.RESOURCE_NAME,
                    Event.buildEventType(BrowserService.SERVICE_NAME, FactoryResource.RESOURCE_NAME, "list-children-of-type"), "type-pattern:" + typePattern));

            return matchingChilds.toArray(new String[matchingChilds.size()]);
        } catch (AccessDeniedException e) {
        	throw e;
        } catch (InvalidPathException e) {
        	throw e;
        } catch (PathNotFoundException e) {
        	throw e;
        } catch (FactoryException e) {
            throw new BrowserServiceException("unable to list children of type for path: " + path, e);
        }
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean exists(String path) throws BrowserServiceException, AccessDeniedException, InvalidPathException {
    	logger.info("exists(...) called");
        logger.debug("params : path=" + path);
    	try {
    		PathHelper.valid(path);

            String npath = PathHelper.normalize(path);
            String ppath = npath;
            if ( !PathHelper.isRoot(npath) ) {
            	ppath = PathHelper.getParentPath(npath);
            }

            String caller = membership.getProfilePathForConnectedIdentifier();

            boolean readAccessOnPath = false;
            boolean readAccessOnParent = false;
            
            try {
            	pep.checkSecurity(membership.getConnectedIdentifierSubjects(), npath, "read");
            	readAccessOnPath = true;
            } catch (AccessDeniedException e) {
            	//
            }
            
            try {
            	pep.checkSecurity(membership.getConnectedIdentifierSubjects(), ppath, "read");
            	readAccessOnParent = true;
            } catch (AccessDeniedException e) {
            	//
            }
            
            if ( !readAccessOnParent && !readAccessOnPath ) {
            	throw new AccessDeniedException("not allowed to check if this path exists");
            }
            
            try {
            	binding.lookup(npath);
            } catch ( PathNotFoundException e ) {
            	return false;
            }
            
            notification.throwEvent(new Event(npath, caller, FactoryResource.RESOURCE_NAME,
                    Event.buildEventType(BrowserService.SERVICE_NAME, FactoryResource.RESOURCE_NAME, "exists"), ""));
            
            return true;
    	} catch (AccessDeniedException e) {
        	throw e;
        } catch (InvalidPathException e) {
        	throw e;
        } catch (FactoryException e) {
            throw new BrowserServiceException("unable to check existence of path: " + path, e);
        }
	}
    
    @Override
    public String[] getResourceTypeList() {
        return RESOURCE_TYPE_LIST;
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }
}
