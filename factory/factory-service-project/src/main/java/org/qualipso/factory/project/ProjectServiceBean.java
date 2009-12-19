package org.qualipso.factory.project;

/**
 * 
 * Copyright (C) 2006-2010 THALES
 * http://www.thalesgroup.fr - thierry.deroff@thalesgroup.com
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the license LGPL.
 *
 * Initial author :
 * Thierry Deroff from Thales Service, THERESIS Competence Center Open Source Software
 *
 */

import java.util.UUID;

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
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.project.entity.Project;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;

@Stateless(name = ProjectService.SERVICE_NAME, mappedName = FactoryNamingConvention.SERVICE_PREFIX + ProjectService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.project.ProjectService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + ProjectService.SERVICE_NAME, serviceName = ProjectService.SERVICE_NAME)
@WebContext(contextRoot = FactoryNamingConvention.WEB_SERVICE_ROOT_MODULE_CONTEXT + "-" + ProjectService.SERVICE_NAME, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX + ProjectService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class ProjectServiceBean implements ProjectService {

	private static Log logger = LogFactory.getLog(ProjectServiceBean.class);

	private BindingService binding;
	private MembershipService membership;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	private SessionContext ctx;
	private EntityManager em;

	public ProjectServiceBean() {
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

	@EJB
	public void setPAPService(PAPService pap) {
		this.pap = pap;
	}

	public PAPService getPAPService() {
		return this.pap;
	}

	/**
	 * @param name
	 *            the name of the project
	 * @param summary
	 *            the description of the project
	 * @param path
	 *            the target location of the new resource
	 * @param licence
	 * 
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createProject(String path, String name, String summary, String licence) throws ProjectServiceException {
		logger.debug("starting project creation");
		try {

			if (name == null || name == "")
				throw new ProjectServiceException("your must specify a name for your project");
			if (summary.length() < 10)
				throw new ProjectServiceException("describe in a more comprehensive manner your project");
			if (summary.length() > 255)
				throw new ProjectServiceException("Your project description is too long. Please make it smaller than 256 bytes.");

			String caller = membership.getProfilePathForConnectedIdentifier();
			// create entity object
			Project project = new Project();
			project.setName(name);
			project.setSummary(summary);
			project.setLicence(licence);
			project.setId(UUID.randomUUID().toString());
			em.persist(project);
			// service orchestration
			pep.checkSecurity(membership.getConnectedIdentifierSubjects(), PathHelper.getParentPath(path), "create");

			binding.bind(project.getFactoryResourceIdentifier(), path);
			binding.setProperty(path, FactoryResourceProperty.CREATION_TIMESTAMP, System.currentTimeMillis() + "");
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			binding.setProperty(path, FactoryResourceProperty.AUTHOR, caller);

			// create default policy
			String policyId = UUID.randomUUID().toString();
			pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, caller, path));
			binding.setProperty(path, FactoryResourceProperty.OWNER, caller);
			binding.setProperty(path, FactoryResourceProperty.POLICY_ID, policyId);

			notification.throwEvent(new Event(path, caller, ProjectService.SERVICE_NAME, Event.buildEventType(ProjectService.SERVICE_NAME, Project.RESOURCE_NAME, "create"), ""));

		} catch (Exception e) {

			ctx.setRollbackOnly();
			throw new ProjectServiceException(e);
		}
	}

	/**
	 * @param path
	 *            location of the project in the resource tree
	 * 
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteProject(String path) throws ProjectServiceException {
		try {

			String caller = membership.getProfilePathForConnectedIdentifier();
			
			pep.checkSecurity(membership.getConnectedIdentifierSubjects(), path, "delete");

			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, Project.RESOURCE_NAME);
			Project project = em.find(Project.class, identifier.getId());

			if (project == null) {
				throw new ProjectServiceException("unable to find a project for id " + identifier.getId());
			}
			em.remove(project);
			
			String policyId = binding.getProperty(path, FactoryResourceProperty.POLICY_ID, false);
			pap.deletePolicy(policyId);
			
			binding.unbind(path);
			notification.throwEvent(new Event(path, caller, ProjectService.SERVICE_NAME, Event.buildEventType(ProjectService.SERVICE_NAME, Project.RESOURCE_NAME, "delete"), ""));

		} catch (Exception e) {
			ctx.setRollbackOnly();
			throw new ProjectServiceException("unable to delete the project at path " + path);
		}
	}

	/**
	 * @param path
	 *            the location of the project
	 * 
	 * @return a Project entity
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Project getProject(String path) throws ProjectServiceException {
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();
			pep.checkSecurity(membership.getConnectedIdentifierSubjects(), path, "read");

			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, Project.RESOURCE_NAME);

			Project project = em.find(Project.class, identifier.getId());
			if (project == null) {
				throw new ProjectServiceException("unable to find a project for id " + identifier.getId());
			}

			project.setResourcePath(path);
			notification.throwEvent(new Event(path, caller, ProjectService.SERVICE_NAME, Event.buildEventType(ProjectService.SERVICE_NAME, Project.RESOURCE_NAME, "read"), ""));

			return project;

		} catch (Exception e) {
			throw new ProjectServiceException(e);
		}
	}

	/**
	 * 
	 * 
	 * @param path
	 *            the location of the project in the resource tree
	 * @param name
	 * 
	 * 
	 * @param status
	 *            if the project is on alpha, beta or release state
	 * 
	 * @param summary
	 * 
	 * @param licence
	 */

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateProject(String path, String name, String status, String summary, String licence) throws ProjectServiceException {
		try {
			if (name == null || name == "")
				throw new ProjectServiceException("your must specify a name for your project");
			if (summary.length() < 10)
				throw new ProjectServiceException("describe in a more comprehensive manner your project");
			if (summary.length() > 255)
				throw new ProjectServiceException("Your project description is too long. Please make it smaller than 256 bytes.");

			String caller = membership.getProfilePathForConnectedIdentifier();

			pep.checkSecurity(membership.getConnectedIdentifierSubjects(), path, "update");
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, Project.RESOURCE_NAME);

			Project project = em.find(Project.class, identifier.getId());
			if (project == null) {
				throw new ProjectServiceException("unable to find a project for id " + identifier.getId());
			}
			project.setName(name);
			project.setDev_status(status);
			project.setSummary(summary);
			project.setLicence(licence);
			em.merge(project);

			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller, ProjectService.SERVICE_NAME, Event.buildEventType(ProjectService.SERVICE_NAME, Project.RESOURCE_NAME, "update"), ""));

		} catch (Exception e) {
			ctx.setRollbackOnly();
			throw new ProjectServiceException(e);

		}

	}

	/**
	 * update meta information about the project
	 * 
	 * @param path
	 *            the location of the project in the resource tree
	 * 
	 * @param os
	 *            the operating system used by this project
	 * 
	 * @param topics
	 * 
	 * @param language
	 *            natural language used by this project
	 * 
	 * @param programming_language
	 *            programming language used by this project
	 * 
	 * @param intended_audience
	 * 
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateTagsProject(String path, String[] os, String[] topics, String[] language, String[] programming_language, String[] intended_audience)
			throws ProjectServiceException {
		try {
			String caller = membership.getProfilePathForConnectedIdentifier();

			pep.checkSecurity(membership.getConnectedIdentifierSubjects(), path, "update");
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, Project.RESOURCE_NAME);

			Project project = em.find(Project.class, identifier.getId());
			if (project == null) {
				throw new ProjectServiceException("unable to find a project for id " + identifier.getId());
			}
			project.setOs(os);
			project.setTopics(topics);
			project.setSpoken_language(language);
			project.setProgramming_language(programming_language);
			project.setIntended_audience(intended_audience);
			em.merge(project);

			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller, ProjectService.SERVICE_NAME, Event.buildEventType(ProjectService.SERVICE_NAME, Project.RESOURCE_NAME, "update"), ""));

		} catch (Exception e) {
			ctx.setRollbackOnly();
			throw new ProjectServiceException(e);

		}

	}

	@Override
	public String[] getResourceTypeList() {
		return ProjectService.RESOURCE_TYPE_LIST;
	}

	@Override
	public String getServiceName() {
		return ProjectService.SERVICE_NAME;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public FactoryResource findResource(String path) throws FactoryException {
		try {
			FactoryResourceIdentifier identifier = binding.lookup(path);

			if (!identifier.getService().equals(ProjectService.SERVICE_NAME)) {
				throw new CoreServiceException("Resource " + identifier + " is not managed by " + ProjectService.SERVICE_NAME);
			}

			if (identifier.getType().equals(Project.RESOURCE_NAME)) {
				return getProject(path);
			}

			throw new CoreServiceException("Resource " + identifier + " is not managed by " + ProjectService.SERVICE_NAME);

		} catch (Exception e) {
			throw new CoreServiceException("unable to find the resource at path " + path, e);
		}
	}

	private void checkResourceType(FactoryResourceIdentifier identifier, String resourceType) throws ProjectServiceException {
		if (!identifier.getService().equals(getServiceName())) {
			throw new ProjectServiceException("resource identifier " + identifier + " does not refer to service " + getServiceName());
		}
		if (!identifier.getType().equals(resourceType)) {
			throw new ProjectServiceException("resource identifier " + identifier + " does not refer to a resource of type " + resourceType);
		}
	}
}
