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
package org.qualipso.factory.membership;

import java.util.Date;
import java.util.UUID;
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
import org.jboss.ejb3.annotation.LocalBinding;
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
import org.qualipso.factory.binding.PathAlreadyBoundException;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.binding.PathNotEmptyException;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.indexing.IndexableContent;
import org.qualipso.factory.indexing.IndexableService;
import org.qualipso.factory.indexing.IndexingService;
import org.qualipso.factory.membership.entity.Group;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.membership.entity.ProfileInfo;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.auth.AuthenticationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.AccessDeniedException;
import org.qualipso.factory.security.pep.PEPService;

/**
 * Implementation fo the MembershipService.<br/>
 * <br/>
 * Implementation is based on a EJB 3.0 Stateless Session Bean. Because external visibility, this bean implements Remote interface and uses WebService
 * annotations. Bean name follow naming conventions of the factory and use the specific remote service prefix.<br/>
 * <br/>
 * Bean security is configured for JBoss AS 5 and rely on JAAS to ensure Authentication and Autorization of user.
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 8 june 2009
 */
@Stateless(name = MembershipService.SERVICE_NAME, mappedName = FactoryNamingConvention.SERVICE_PREFIX + MembershipService.SERVICE_NAME)
@LocalBinding(jndiBinding = FactoryNamingConvention.LOCAL_SERVICE_PREFIX + MembershipService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.membership.MembershipService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + MembershipService.SERVICE_NAME, serviceName = MembershipService.SERVICE_NAME)
@WebContext(contextRoot = FactoryNamingConvention.WEB_SERVICE_CORE_MODULE_CONTEXT, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX + MembershipService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class MembershipServiceBean implements MembershipService, IndexableService {
	private static Log logger = LogFactory.getLog(MembershipServiceBean.class);
	private PEPService pep;
	private PAPService pap;
	private BindingService binding;
	private NotificationService notification;
	private IndexingService indexing;
	private AuthenticationService authentication;
	private SessionContext ctx;
	private EntityManager em;

	public MembershipServiceBean() {
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
	public void setIndexingService(IndexingService indexing) {
		this.indexing = indexing;
	}

	public IndexingService getIndexingService() {
		return this.indexing;
	}

	@EJB
	public void setAuthenticationService(AuthenticationService authentication) {
		this.authentication = authentication;
	}

	public AuthenticationService getAuthenticationService() {
		return this.authentication;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String getProfilesPath() {
		return PROFILES_PATH;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String getProfilePathForConnectedIdentifier() {
		logger.info("getProfilePathForConnectedIdentifier(...) called");

		return PROFILES_PATH + "/" + authentication.getConnectedIdentifier();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String getProfilePathForIdentifier(String identifier) {
		logger.info("getProfilePathForIdentifier(...) called");

		return PROFILES_PATH + "/" + identifier;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String[] getConnectedIdentifierSubjects() throws MembershipServiceException, InvalidPathException, PathNotFoundException {
		logger.info("getConnectedIdentifierSubjects(...) called");

		try {
			String caller = getProfilePathForConnectedIdentifier();
	
			FactoryResourceIdentifier identifier = binding.lookup(caller);
	
			Profile profile = em.find(Profile.class, identifier.getId());
	
			if (profile == null) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}
	
			String[] groups = profile.getGroups();
			String[] subjects = new String[groups.length + 1];
			subjects[0] = caller;
	
			for (int i = 0; i < groups.length; i++) {
				subjects[i + 1] = groups[i];
			}
	
			return subjects;
		} catch ( BindingServiceException e ) {
			throw new MembershipServiceException("unable to get connected identifier subjects", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createProfile(String identifier, String fullname, String email, int accountStatus) throws MembershipServiceException, AccessDeniedException,
			InvalidPathException, PathAlreadyBoundException {
		logger.info("createProfile(...) called");
		logger.debug("params : identifier=" + identifier + ", fullname=" + fullname + ", email=" + email + ", accountStatus=" + accountStatus);

		String path = getProfilePathForIdentifier(identifier);
		logger.debug("generated profile path : " + path);

		try {
			String caller = getProfilePathForConnectedIdentifier();

			pep.checkSecurity(getConnectedIdentifierSubjects(), PathHelper.getParentPath(path), "create");

			Profile profile = new Profile();
			profile.setId(UUID.randomUUID().toString());
			profile.setFullname(fullname);
			profile.setEmail(email);
			profile.setAccountStatus(accountStatus);
			profile.setOnlineStatus(Profile.OFFLINE);
			profile.setLastLoginDate(new Date(0));
			em.persist(profile);

			binding.bind(profile.getFactoryResourceIdentifier(), path);
			binding.setProperty(path, FactoryResourceProperty.CREATION_TIMESTAMP, System.currentTimeMillis() + "");
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			binding.setProperty(path, FactoryResourceProperty.AUTHOR, caller);

			String policyId = UUID.randomUUID().toString();
			pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, path, path));

			binding.setProperty(path, FactoryResourceProperty.OWNER, path);
			binding.setProperty(path, FactoryResourceProperty.POLICY_ID, policyId);

			notification.throwEvent(new Event(path, caller, Profile.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "create"), ""));

			indexing.index(path);
		} catch (AccessDeniedException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (InvalidPathException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathAlreadyBoundException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (FactoryException e) {
			ctx.setRollbackOnly();
			throw new MembershipServiceException("unable to create a profile", e);
		}

	}
	

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Profile readProfile(String path) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		return readProfile(path, false);
	}
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private Profile readProfile(String path, boolean bypassSecurity) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.info("readProfile(...) called");
		logger.debug("params : path=" + path);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			
			if (!bypassSecurity) {
				pep.checkSecurity(getConnectedIdentifierSubjects(), path, "read");
			}

			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, Profile.RESOURCE_NAME);

			Profile profile = em.find(Profile.class, identifier.getId());

			if (profile == null) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}

			profile.setResourcePath(path);

			notification.throwEvent(new Event(path, caller, Profile.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "read"), ""));

			return profile;
		} catch (AccessDeniedException e) {
			throw e;
		} catch (InvalidPathException e) {
			throw e;
		} catch (PathNotFoundException e) {
			throw e;
		} catch (FactoryException e) {
			throw new MembershipServiceException("unable to read the profile at path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateProfile(String path, String fullname, String email, int accountStatus) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.info("updateProfile(...) called");
		logger.debug("params : path=" + path + ", fullname=" + fullname + ", email=" + email + ", accountStatus=" + accountStatus);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(getConnectedIdentifierSubjects(), path, "update");

			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, Profile.RESOURCE_NAME);

			Profile profile = em.find(Profile.class, identifier.getId());

			if (profile == null) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}

			profile.setFullname(fullname);
			profile.setEmail(email);
			profile.setAccountStatus(accountStatus);
			em.merge(profile);

			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller, Profile.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "update"), ""));
			
			indexing.reindex(path);
		} catch (AccessDeniedException e) {
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
			throw new MembershipServiceException("unable to update the profile at path: " + path, e);
		}
		
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateProfileLastLoginDate(String path) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.info("updateProfileLastLoginDate(...) called");
		logger.debug("params : path=" + path);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(getConnectedIdentifierSubjects(), path, "update");

			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, Profile.RESOURCE_NAME);

			Profile profile = em.find(Profile.class, identifier.getId());

			if (profile == null) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}

			profile.setLastLoginDate(new Date());
			em.merge(profile);

			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller, Profile.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "update"), ""));
			
			indexing.reindex(path);
		} catch (AccessDeniedException e) {
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
			throw new MembershipServiceException("unable to update last login date of the profile at path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateProfileOnlineStatus(String path, int onlineStatus) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.info("updateProfileOnlineStatus(...) called");
		logger.debug("params : path=" + path + ", onlineStatus=" + onlineStatus);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(getConnectedIdentifierSubjects(), path, "update");

			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, Profile.RESOURCE_NAME);

			Profile profile = em.find(Profile.class, identifier.getId());

			if (profile == null) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}

			profile.setOnlineStatus(onlineStatus);
			em.merge(profile);

			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller, Profile.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "update"), ""));
			
			indexing.reindex(path);
		} catch (AccessDeniedException e) {
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
			throw new MembershipServiceException("unable to update online status of the profile at path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteProfile(String path) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException, PathNotEmptyException {
		logger.info("deleteProfile(...) called");
		logger.debug("params : path=" + path);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(getConnectedIdentifierSubjects(), path, "delete");

			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, Profile.RESOURCE_NAME);

			Profile profile = em.find(Profile.class, identifier.getId());
			if (profile == null) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}
			em.remove(profile);

			String policyId = binding.getProperty(path, FactoryResourceProperty.POLICY_ID, false);
			pap.deletePolicy(policyId);
			
			binding.unbind(path);
			
			notification.throwEvent(new Event(path, caller, Profile.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "delete"), ""));
			
			indexing.remove(path);
		} catch (AccessDeniedException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (InvalidPathException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathNotFoundException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathNotEmptyException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (FactoryException e) {
			ctx.setRollbackOnly();
			throw new MembershipServiceException("unable to delete profile at path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void setProfileInfo(String path, String name, String value) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.info("setProfileInfo(...) called");
		logger.debug("params : path=" + path + ", name=" + name + ", value=" + value);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(getConnectedIdentifierSubjects(), path, "update");

			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, Profile.RESOURCE_NAME);

			Profile profile = em.find(Profile.class, identifier.getId());

			if (profile == null) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}

			profile.setProfileInfo(name, value);
			em.merge(profile);

			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller, Profile.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME,
					"set-info"), "name:" + name + ", value:" + value));
			
			indexing.reindex(path);
		} catch (AccessDeniedException e) {
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
			throw new MembershipServiceException("unable to set info for profile at path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ProfileInfo getProfileInfo(String path, String name) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.info("getProfileInfo(...) called");
		logger.debug("params : path=" + path + ", name=" + name);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			
			pep.checkSecurity(getConnectedIdentifierSubjects(), path, "read");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, Profile.RESOURCE_NAME);

			Profile profile = em.find(Profile.class, identifier.getId());

			if (profile == null) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}

			ProfileInfo pinfo = new ProfileInfo();
			pinfo.setName(name);
			pinfo.setValue(profile.getProfileInfo(name));

			notification.throwEvent(new Event(path, caller, Profile.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME,
					"get-info"), "name:" + name));

			return pinfo;
		} catch (AccessDeniedException e) {
			throw e;
		} catch (InvalidPathException e) {
			throw e;
		} catch (PathNotFoundException e) {
			throw e;
		} catch (FactoryException e) {
			throw new MembershipServiceException("unable to get info for profile at path: " + path, e);
		}
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ProfileInfo[] listProfileInfos(String path) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.info("listProfileInfos(...) called");
		logger.debug("params : path=" + path);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			
			pep.checkSecurity(getConnectedIdentifierSubjects(), path, "read");

			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, Profile.RESOURCE_NAME);

			Profile profile = em.find(Profile.class, identifier.getId());

			if (profile == null) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}

			Vector<ProfileInfo> pinfos = new Vector<ProfileInfo>();

			for (String pinfokey : profile.getProfileInfos().keySet()) {
				ProfileInfo pinfo = new ProfileInfo();
				pinfo.setName(pinfokey);
				pinfo.setValue(profile.getProfileInfo(pinfokey));
				pinfos.add(pinfo);
			}

			notification.throwEvent(new Event(path, caller, Profile.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME,
					"list-info"), ""));

			ProfileInfo[] infos = new ProfileInfo[0];

			return pinfos.toArray(infos);
		} catch (AccessDeniedException e) {
			throw e;
		} catch (InvalidPathException e) {
			throw e;
		} catch (PathNotFoundException e) {
			throw e;
		} catch (FactoryException e) {
			throw new MembershipServiceException("unable to list infos for profile at path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createGroup(String path, String name, String description) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathAlreadyBoundException {
		logger.warn("createGroup(...) called");
		logger.debug("params : path=" + path + ", name=" + name + ", description=" + description);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(getConnectedIdentifierSubjects(), PathHelper.getParentPath(path), "create");

			Group group = new Group();
			group.setId(UUID.randomUUID().toString());
			group.setName(name);
			group.setDescription(description);
			em.persist(group);

			binding.bind(group.getFactoryResourceIdentifier(), path);
			binding.setProperty(path, FactoryResourceProperty.CREATION_TIMESTAMP, System.currentTimeMillis() + "");
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			binding.setProperty(path, FactoryResourceProperty.AUTHOR, caller);

			String policyId = UUID.randomUUID().toString();
			pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, path, path));

			binding.setProperty(path, FactoryResourceProperty.OWNER, caller);
			binding.setProperty(path, FactoryResourceProperty.POLICY_ID, policyId);

			notification.throwEvent(new Event(path, caller, Group.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Group.RESOURCE_NAME,
					"create"), ""));
			
			indexing.index(path);
		} catch (AccessDeniedException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (InvalidPathException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathAlreadyBoundException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (FactoryException e) {
			ctx.setRollbackOnly();
			throw new MembershipServiceException("unable to create group at path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Group readGroup(String path) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		return readGroup(path, false);
	}
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private Group readGroup(String path, boolean bypassSecurity) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.warn("readGroup(...) called");
		logger.debug("params : path=" + path);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			
			if (!bypassSecurity) {
				pep.checkSecurity(getConnectedIdentifierSubjects(), path, "read");
			}

			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, Group.RESOURCE_NAME);

			Group group = em.find(Group.class, identifier.getId());

			if (group == null) {
				throw new MembershipServiceException("unable to find a group for id " + identifier.getId());
			}

			group.setResourcePath(path);

			notification.throwEvent(new Event(path, caller, Group.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Group.RESOURCE_NAME,
					"read"), ""));

			return group;
		} catch (AccessDeniedException e) {
			throw e;
		} catch (InvalidPathException e) {
			throw e;
		} catch (PathNotFoundException e) {
			throw e;
		} catch (FactoryException e) {
			throw new MembershipServiceException("unable to read group at path", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateGroup(String path, String name, String description) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.warn("updateGroup(...) called");
		logger.debug("params : path=" + path + ", name=" + name + ", description=" + description);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(getConnectedIdentifierSubjects(), path, "update");

			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, Group.RESOURCE_NAME);

			Group group = em.find(Group.class, identifier.getId());

			if (group == null) {
				throw new MembershipServiceException("unable to find a group for id " + identifier.getId());
			}

			group.setName(name);
			group.setDescription(description);
			em.merge(group);

			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller, Group.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Group.RESOURCE_NAME,
					"update"), ""));
			
			indexing.reindex(path);
		} catch (AccessDeniedException e) {
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
			throw new MembershipServiceException("unable to update group at path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteGroup(String path) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException, PathNotEmptyException {
		logger.warn("deleteGroup(...) called");
		logger.debug("params : path=" + path);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(getConnectedIdentifierSubjects(), path, "delete");

			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, Group.RESOURCE_NAME);

			Group group = em.find(Group.class, identifier.getId());
			if (group == null) {
				throw new MembershipServiceException("unable to find a group for id " + identifier.getId());
			}
			em.remove(group);
			
			String policyId = binding.getProperty(path, FactoryResourceProperty.POLICY_ID, false);
			pap.deletePolicy(policyId);

			binding.unbind(path);
			
			notification.throwEvent(new Event(path, caller, Group.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Group.RESOURCE_NAME,
					"delete"), ""));
			
			indexing.remove(path);
		} catch (AccessDeniedException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (InvalidPathException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathNotFoundException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathNotEmptyException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (FactoryException e) {
			ctx.setRollbackOnly();
			throw new MembershipServiceException("unable to delete group at path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void addMemberInGroup(String path, String member) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.warn("addMemberInGroup(...) called");
		logger.debug("params : path=" + path + ", member=" + member);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(getConnectedIdentifierSubjects(), path, "update");

			FactoryResourceIdentifier groupIdentifier = binding.lookup(path);
			checkResourceType(groupIdentifier, Group.RESOURCE_NAME);

			FactoryResourceIdentifier memberIdentifier = binding.lookup(member);
			checkResourceType(memberIdentifier, Profile.RESOURCE_NAME);

			Group group = em.find(Group.class, groupIdentifier.getId());

			if (group == null) {
				throw new MembershipServiceException("unable to find a group for id " + groupIdentifier.getId());
			}

			Profile profile = em.find(Profile.class, memberIdentifier.getId());

			if (profile == null) {
				throw new MembershipServiceException("unable to find a profile for id " + memberIdentifier.getId());
			}

			group.addMember(member);
			profile.addGroup(path);

			em.merge(profile);
			em.merge(group);

			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			binding.setProperty(member, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller, Group.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Group.RESOURCE_NAME,
					"add-member"), ""));
			
			indexing.reindex(path);
			indexing.reindex(member);
		} catch (AccessDeniedException e) {
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
			throw new MembershipServiceException("unable to add member for group at path", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removeMemberFromGroup(String path, String member) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.warn("removeMemberFromGroup(...) called");
		logger.debug("params : path=" + path + ", member=" + member);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(getConnectedIdentifierSubjects(), path, "update");

			FactoryResourceIdentifier groupIdentifier = binding.lookup(path);
			checkResourceType(groupIdentifier, Group.RESOURCE_NAME);

			FactoryResourceIdentifier memberIdentifier = binding.lookup(member);
			checkResourceType(memberIdentifier, Profile.RESOURCE_NAME);

			Group group = em.find(Group.class, groupIdentifier.getId());

			if (group == null) {
				throw new MembershipServiceException("unable to find a group for id " + groupIdentifier.getId());
			}

			Profile profile = em.find(Profile.class, memberIdentifier.getId());

			if (profile == null) {
				throw new MembershipServiceException("unable to find a profile for id " + memberIdentifier.getId());
			}

			group.removeMember(member);
			profile.removeGroup(path);

			em.merge(profile);
			em.merge(group);

			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			binding.setProperty(member, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller, Group.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Group.RESOURCE_NAME,
					"remove-member"), ""));
			
			indexing.reindex(path);
			indexing.reindex(member);
		} catch (AccessDeniedException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (InvalidPathException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (PathNotFoundException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			ctx.setRollbackOnly();
			throw new MembershipServiceException("unable to remove member for group at path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isMember(String path, String member) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.warn("isMember(...) called");
		logger.debug("params : path=" + path + ", member=" + member);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(getConnectedIdentifierSubjects(), path, "read");

			FactoryResourceIdentifier groupIdentifier = binding.lookup(path);
			checkResourceType(groupIdentifier, Group.RESOURCE_NAME);

			FactoryResourceIdentifier memberIdentifier = binding.lookup(member);
			checkResourceType(memberIdentifier, Profile.RESOURCE_NAME);

			Group group = em.find(Group.class, groupIdentifier.getId());

			boolean isMember = false;

			if (group.isMember(member)) {
				isMember = true;
			}

			notification.throwEvent(new Event(path, caller, Group.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Group.RESOURCE_NAME,
					"is-member"), ""));

			return isMember;
		} catch (AccessDeniedException e) {
			throw e;
		} catch (InvalidPathException e) {
			throw e;
		} catch (PathNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new MembershipServiceException("unable to check if is member of group ath path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String[] listMembers(String path) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.warn("listMembers(...)");
		logger.debug("params : path=" + path);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(getConnectedIdentifierSubjects(), path, "read");

			FactoryResourceIdentifier groupIdentifier = binding.lookup(path);
			checkResourceType(groupIdentifier, Group.RESOURCE_NAME);

			Group group = em.find(Group.class, groupIdentifier.getId());

			String[] members = group.getMembers();

			notification.throwEvent(new Event(path, caller, Group.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Group.RESOURCE_NAME,
					"list-members"), ""));

			return members;
		} catch (AccessDeniedException e) {
			throw e;
		} catch (InvalidPathException e) {
			throw e;
		} catch (PathNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new MembershipServiceException("unable to list members of group at path: " + path, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String[] listGroups(String path) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.warn("listGroups(...)");
		logger.debug("params : path=" + path);

		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(getConnectedIdentifierSubjects(), path, "read");

			FactoryResourceIdentifier profileIdentifier = binding.lookup(path);
			checkResourceType(profileIdentifier, Profile.RESOURCE_NAME);

			Profile profile = em.find(Profile.class, profileIdentifier.getId());

			String[] groups = profile.getGroups();

			notification.throwEvent(new Event(path, caller, Profile.RESOURCE_NAME, Event.buildEventType(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME,
					"list-groups"), ""));

			return groups;
		} catch (AccessDeniedException e) {
			throw e;
		} catch (InvalidPathException e) {
			throw e;
		} catch (PathNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new MembershipServiceException("unable to list groups of profile at path: " + path, e);
		}
	}

	// private internal methods
	private void checkResourceType(FactoryResourceIdentifier identifier, String resourceType) throws MembershipServiceException {
		Vector<String> types = new Vector<String>();
		types.add(resourceType);
		this.checkResourceType(identifier, types);
	}

	private void checkResourceType(FactoryResourceIdentifier identifier, Vector<String> resourceTypes) throws MembershipServiceException {
		if (!identifier.getService().equals(getServiceName())) {
			throw new MembershipServiceException("resource identifier " + identifier + " does not refer to service " + getServiceName());
		}

		if (!resourceTypes.contains(identifier.getType())) {
			throw new MembershipServiceException("resource identifier " + identifier + " does not refer to one of the desired resource types");
		}
	}

	// Factory Service Methods
	@Override
	public String[] getResourceTypeList() {
		return RESOURCE_TYPE_LIST;
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public FactoryResource findResource(String path) throws FactoryException, AccessDeniedException, InvalidPathException, PathNotFoundException {
		logger.info("findResource(...) called");
		logger.debug("params : path=" + path);

		FactoryResourceIdentifier identifier = binding.lookup(path);

		if (!identifier.getService().equals(MembershipService.SERVICE_NAME)) {
			throw new MembershipServiceException("resource " + identifier + " is not managed by service " + MembershipService.SERVICE_NAME);
		}

		if (identifier.getType().equals(Group.RESOURCE_NAME)) {
			return readGroup(path);
		}

		if (identifier.getType().equals(Profile.RESOURCE_NAME)) {
			return readProfile(path);
		}

		throw new MembershipServiceException("resource " + identifier + " is not managed by service " + MembershipService.SERVICE_NAME);
	}
	
	@Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public IndexableContent getIndexableContent(String path) throws FactoryException {
        logger.info("getIndexableContent(...) called");
        logger.debug("params : path=" + path);
        
        FactoryResourceIdentifier identifier = binding.lookup(path);
        
        if (!identifier.getService().equals(MembershipService.SERVICE_NAME)) {
			throw new MembershipServiceException("resource " + identifier + " is not managed by service " + MembershipService.SERVICE_NAME);
		}

        IndexableContent content = new IndexableContent();
        
        if (identifier.getType().equals(Group.RESOURCE_NAME)) {
			Group group = readGroup(path, true);
			content.addContentPart(group.getName());
			content.addContentPart(group.getDescription());
			content.addContentPart(group.getMembersList());
			return content;
		}

		if (identifier.getType().equals(Profile.RESOURCE_NAME)) {
			Profile profile = readProfile(path, true);
			content.addContentPart(profile.getFullname());
			content.addContentPart(profile.getEmail());
			content.addContentPart(profile.getGroupsList());
			for ( String key : profile.getProfileInfos().keySet() ) {
				content.addContentPart(key + ":" + profile.getProfileInfo(key));
			}
			return content;
		}

		throw new MembershipServiceException("resource " + identifier + " is not managed by service " + MembershipService.SERVICE_NAME);
    }
}
