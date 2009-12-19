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
package org.qualipso.factory.security;

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
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.BindingServiceException;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.binding.PropertyNotFoundException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.membership.entity.Group;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.AccessDeniedException;
import org.qualipso.factory.security.pep.PEPService;

/**
 * Implementation of the Security Service.<br/>
 * <br/>
 * This implementation relies on an XACML sub system allowing very flexible modification of security model (TBAC, RBAC, etc...). Actual Access Control model is
 * DAC and more specifically ACL Based.<br/>
 * <br/>
 * Implementation is based on a EJB 3.0 Stateless Session Bean. Because external visibility, this bean implements Remote interface and uses WebService
 * annotations. Bean name follow naming conventions of the factory and use the specific remote service prefix.<br/>
 * <br/>
 * Bean security is configured for JBoss AS 5 and rely on JAAS to ensure Authentication and Authorization of user. <br/>
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 september 2009
 */
@Stateless(name = SecurityService.SERVICE_NAME, mappedName = FactoryNamingConvention.SERVICE_PREFIX + SecurityService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.security.SecurityService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE
		+ SecurityService.SERVICE_NAME, serviceName = SecurityService.SERVICE_NAME)
@WebContext(contextRoot = FactoryNamingConvention.WEB_SERVICE_CORE_MODULE_CONTEXT, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX
		+ SecurityService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class SecurityServiceBean implements SecurityService {
	private static Log logger = LogFactory.getLog(SecurityServiceBean.class);
	private BindingService binding;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	private MembershipService membership;
	private SessionContext ctx;
	private EntityManager em;

	public SecurityServiceBean() {
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
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String getSecurityPolicy(String path) throws SecurityServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.info("getSecurityPolicy(...) called");
		logger.debug("params : path=" + path);

		try {
			PathHelper.valid(path);

			String npath = PathHelper.normalize(path);

			String caller = membership.getProfilePathForConnectedIdentifier();
			String[] subjects = membership.getConnectedIdentifierSubjects();

			pep.checkSecurity(subjects, npath, "read");

			binding.lookup(npath);

			String policy = pap.getPolicy(binding.getProperty(npath, FactoryResourceProperty.POLICY_ID, false));

			notification.throwEvent(new Event(npath, caller, FactoryResource.RESOURCE_NAME, Event.buildEventType(SecurityService.SERVICE_NAME,
					FactoryResource.RESOURCE_NAME, "get-security-policy"), ""));

			return policy;
		} catch (InvalidPathException e) {
			throw e;
		} catch (AccessDeniedException e) {
			throw e;
		} catch (PathNotFoundException e) {
			throw e;
		} catch (FactoryException e) {
			throw new SecurityServiceException("unable to get the security policy for path: " + path);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void addSecurityRule(String path, String subject, String permissions) throws SecurityServiceException, OwnerException, SubjectException, InvalidPathException, PathNotFoundException {
		logger.info("addSecurityRule(...) called");
		logger.debug("params : path=" + path + ", subject=" + subject + ", permissions=" + permissions);

		try {
			PathHelper.valid(path);

			String npath = PathHelper.normalize(path);

			String caller = membership.getProfilePathForConnectedIdentifier();

			// just to check if path exists
			binding.lookup(npath);

			if (!isOwner(caller, npath)) {
				throw new OwnerException("caller: " + caller + " is not owner of path: " + path);
			}

			String owner = binding.getProperty(npath, FactoryResourceProperty.OWNER, false);

			if (subject.equals(owner)) {
				throw new SubjectException("subject is the owner, it already have all permissions");
			}

			FactoryResourceIdentifier subjectIdentifier = binding.lookup(subject);

			if (!subjectIdentifier.getService().equals(MembershipService.SERVICE_NAME)
					|| (!subjectIdentifier.getType().equals(Profile.RESOURCE_NAME) && !subjectIdentifier.getType().equals(Group.RESOURCE_NAME))) {
				throw new SubjectException("subject need to be neither a " + Profile.RESOURCE_NAME + " nor a " + Group.RESOURCE_NAME + " resource type");
			}

			String policyId = binding.getProperty(npath, FactoryResourceProperty.POLICY_ID, false);
			String policy = pap.getPolicy(policyId);
			String newPolicy = PAPServiceHelper.addRuleToPolicy(policy, subject, permissions.split(","));
			pap.updatePolicy(policyId, newPolicy);

			notification.throwEvent(new Event(npath, caller, FactoryResource.RESOURCE_NAME, Event.buildEventType(SecurityService.SERVICE_NAME,
					FactoryResource.RESOURCE_NAME, "add-security-rule"), ""));
		} catch (OwnerException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (SubjectException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (InvalidPathException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathNotFoundException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (FactoryException e) {
			ctx.setRollbackOnly();
			throw new SecurityServiceException("unable to add a security rule at path: " + path, e);
		} 
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void editSecurityRule(String path, String subject, String permissions) throws SecurityServiceException, OwnerException, SubjectException, InvalidPathException, PathNotFoundException {
		logger.info("editSecurityRule(...) called");
		logger.debug("params : path=" + path);

		try {
			PathHelper.valid(path);

			String npath = PathHelper.normalize(path);

			String caller = membership.getProfilePathForConnectedIdentifier();

			// just to check if path exists
			binding.lookup(npath);

			if (!isOwner(caller, npath)) {
				throw new OwnerException("caller: " + caller + " is not owner of path: " + path);
			}

			String owner = binding.getProperty(npath, FactoryResourceProperty.OWNER, false);

			if (subject.equals(owner)) {
				throw new SubjectException("subject is the owner, it already have all permissions");
			}

			String policyId = binding.getProperty(npath, FactoryResourceProperty.POLICY_ID, false);
			String policy = pap.getPolicy(policyId);
			String newPolicy = PAPServiceHelper.addRuleToPolicy(PAPServiceHelper.removeRuleFromPolicy(policy, subject), subject, permissions.split(","));
			pap.updatePolicy(policyId, newPolicy);

			notification.throwEvent(new Event(npath, caller, FactoryResource.RESOURCE_NAME, Event.buildEventType(SecurityService.SERVICE_NAME,
					FactoryResource.RESOURCE_NAME, "edit-security-rule"), ""));
		} catch (OwnerException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (SubjectException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (InvalidPathException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathNotFoundException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (FactoryException e) {
			ctx.setRollbackOnly();
			throw new SecurityServiceException("unable to edit a security rule at path: " + path, e);
		} 
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removeSecurityRule(String path, String subject) throws SecurityServiceException, OwnerException, SubjectException, InvalidPathException, PathNotFoundException {
		logger.info("removeSecurityRule(...) called");
		logger.debug("params : path=" + path);

		try {
			PathHelper.valid(path);

			String npath = PathHelper.normalize(path);

			String caller = membership.getProfilePathForConnectedIdentifier();

			// just to check if path exists
			binding.lookup(npath);

			if (!isOwner(caller, npath)) {
				throw new OwnerException("caller: " + caller + " is not owner of path: " + path);
			}

			String owner = binding.getProperty(npath, FactoryResourceProperty.OWNER, false);

			if (subject.equals(owner)) {
				throw new SubjectException("subject is the owner, can't remove owner security rule");
			}

			String policyId = binding.getProperty(npath, FactoryResourceProperty.POLICY_ID, false);
			String policy = pap.getPolicy(policyId);
			String newPolicy = PAPServiceHelper.removeRuleFromPolicy(policy, subject);
			pap.updatePolicy(policyId, newPolicy);

			notification.throwEvent(new Event(npath, caller, FactoryResource.RESOURCE_NAME, Event.buildEventType(SecurityService.SERVICE_NAME,
					FactoryResource.RESOURCE_NAME, "remove-security-rule"), ""));
		} catch (OwnerException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (SubjectException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (InvalidPathException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathNotFoundException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (FactoryException e) {
			ctx.setRollbackOnly();
			throw new SecurityServiceException("unable to remove a security rule at path: " + path, e);
		} 
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void changeOwner(String path, String subject) throws SecurityServiceException, OwnerException, SubjectException, InvalidPathException, PathNotFoundException {
		logger.info("changeOwner(...) called");
		logger.debug("params : path=" + path);

		try {
			PathHelper.valid(path);

			String npath = PathHelper.normalize(path);

			PathHelper.valid(subject);

			String newOwnerPath = PathHelper.normalize(subject);

			String caller = membership.getProfilePathForConnectedIdentifier();

			// just to check if path exists
			binding.lookup(npath);

			if (!isOwner(caller, npath)) {
				throw new OwnerException("caller: " + caller + " is not owner of path: " + path);
			}

			FactoryResourceIdentifier newOwnerIdentifier = binding.lookup(subject);

			if (!newOwnerIdentifier.getService().equals(MembershipService.SERVICE_NAME)
					|| (!newOwnerIdentifier.getType().equals(Profile.RESOURCE_NAME) && !newOwnerIdentifier.getType().equals(Profile.RESOURCE_NAME))) {
				throw new SubjectException("subject need to be neither a " + Profile.RESOURCE_NAME + " nor a " + Group.RESOURCE_NAME + " resource type");
			}

			String ownerPath = binding.getProperty(npath, FactoryResourceProperty.OWNER, false);
			String policyId = binding.getProperty(npath, FactoryResourceProperty.POLICY_ID, false);
			String policy = pap.getPolicy(policyId);
			String newPolicy = PAPServiceHelper.addRuleToPolicy(PAPServiceHelper.removeRuleFromPolicy(policy, ownerPath), newOwnerPath, new String[0]);
			System.out.println(newPolicy);
			pap.updatePolicy(policyId, newPolicy);

			binding.setProperty(npath, FactoryResourceProperty.OWNER, newOwnerPath);

			notification.throwEvent(new Event(npath, caller, FactoryResource.RESOURCE_NAME, Event.buildEventType(SecurityService.SERVICE_NAME,
					FactoryResource.RESOURCE_NAME, "change-owner"), "new-owner=" + newOwnerPath));
		} catch (OwnerException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (SubjectException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (InvalidPathException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathNotFoundException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (FactoryException e) {
			ctx.setRollbackOnly();
			throw new SecurityServiceException("unable to change owner of path: " + path, e);
		} 
	}

	@Override
	public FactoryResource findResource(String path) throws FactoryException {
		throw new SecurityServiceException("this service doesn't manage any resource");
	}

	@Override
	public String[] getResourceTypeList() {
		return RESOURCE_TYPE_LIST;
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

	// Service methods
	private boolean isOwner(String user, String path) throws MembershipServiceException, InvalidPathException, PathNotFoundException, PropertyNotFoundException, BindingServiceException, AccessDeniedException {
		String ownerPath = binding.getProperty(path, FactoryResourceProperty.OWNER, false);
		FactoryResourceIdentifier owner = binding.lookup(ownerPath);

		if (!user.equals(ownerPath)) {
			// caller is not the owner, maybe owner is a group
			if (owner.getType().equals("Group")) {
				// owner is a group, checking if caller is member of this group
				if (!membership.isMember(ownerPath, user)) {
					logger.debug("user:" + user + " is NOT owner");
					return false;
				}
			} else {
				logger.debug("user:" + user + " is NOT owner");
				return false;
			}
		}

		logger.debug("user:" + user + " is owner");
		return true;
	}
}
