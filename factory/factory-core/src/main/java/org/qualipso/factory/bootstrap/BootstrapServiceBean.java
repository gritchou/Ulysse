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
package org.qualipso.factory.bootstrap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
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
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.BindingServiceException;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.core.entity.File;
import org.qualipso.factory.core.entity.Folder;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceBean;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.repository.PolicyRepositoryService;
import org.qualipso.factory.ssh.SSHService;
import org.qualipso.factory.ssh.SSHServiceException;


/**
 * Implementation fo the BootstrapService.<br/>
 * <br/>
 * Implementation is based on a EJB 3.0 Stateless Session Bean. Because external visibility, this bean implements
 * Remote interface and uses WebService annotations.
 * Bean name follow naming conventions of the factory and use the specific remote service prefix.<br/>
 * <br/>
 * Bean security is configured for JBoss AS 5 and rely on JAAS to ensure Authentication and Autorization of user.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 4 September 2009
 */
@Stateless(name = BootstrapService.SERVICE_NAME, mappedName = FactoryNamingConvention.SERVICE_PREFIX + BootstrapService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.bootstrap.BootstrapService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE +
BootstrapService.SERVICE_NAME, serviceName = BootstrapService.SERVICE_NAME)
@WebContext(contextRoot = FactoryNamingConvention.WEB_SERVICE_CORE_MODULE_CONTEXT, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX +
BootstrapService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class BootstrapServiceBean implements BootstrapService {
    private static Log logger = LogFactory.getLog(BootstrapServiceBean.class);
    private PAPService pap;
    private SessionContext ctx;
    private EntityManager em;
    private BindingService binding;
    private NotificationService notification;
    private SSHService sshd;
    private MembershipService membership;
	private PolicyRepositoryService repositoryService;

    public BootstrapServiceBean() {
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
    public void setPAPService(PAPService pap) {
        this.pap = pap;
    }

    public PAPService getPAPService() {
        return pap;
    }

    @EJB
    public void setBindingService(BindingService binding) {
        this.binding = binding;
    }

    public BindingService getBindingService() {
        return binding;
    }

    @EJB
    public void setMembershipService(MembershipService membership) {
        this.membership = membership;
    }

    public MembershipService getMembershipService() {
        return membership;
    }

    @EJB
    public void setNotificationService(NotificationService notification) {
        this.notification = notification;
    }

    public NotificationService getNotificationService() {
        return notification;
    }

    @EJB
    public void setSSHService(SSHService sshd) {
        this.sshd = sshd;
    }

    public SSHService getSSHService() {
        return sshd;
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
    public void bootstrap() throws BootstrapServiceException {
    	//TODO remove this to a more convenient way of starting ssh server...
    	try {
    		sshd.importShellCommands("org.qualipso.factory.ssh.shell.command");
    	} catch ( SSHServiceException e ) {
    		logger.warn("error in importing ssh commands", e);
    	}
        try {
            binding.lookup(BootstrapServiceBean.BOOTSTRAP_FILE_PATH);
        } catch (InvalidPathException e) {
            throw new BootstrapServiceException("error during bootstrap", e);
        } catch (PathNotFoundException pnfe) {
            logger.info("starting factory bootstrap");

            try {
            	logger.debug("purging policy store");
            	repositoryService.getPolicyRepository().purge();
            	
                logger.debug("creating the profile folder");

                String suPath = membership.getProfilePathForIdentifier(MembershipService.SUPERUSER_IDENTIFIER);

                Folder folder = new Folder();
                folder.setId(UUID.randomUUID().toString());
                folder.setName("All Profiles");
                em.persist(folder);

                binding.bind(folder.getFactoryResourceIdentifier(), MembershipService.PROFILES_PATH);

                binding.setProperty(MembershipService.PROFILES_PATH, FactoryResourceProperty.CREATION_TIMESTAMP, "" + System.currentTimeMillis());
                binding.setProperty(MembershipService.PROFILES_PATH, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, "" + System.currentTimeMillis());
                binding.setProperty(MembershipService.PROFILES_PATH, FactoryResourceProperty.AUTHOR, suPath);

                String policyId = UUID.randomUUID().toString();
                pap.createPolicy(policyId,
                    PAPServiceHelper.addRuleToPolicy(PAPServiceHelper.buildOwnerPolicy(policyId, suPath, MembershipServiceBean.PROFILES_PATH), "",
                        new String[] { "create" }));

                binding.setProperty(MembershipService.PROFILES_PATH, FactoryResourceProperty.OWNER, suPath);
                binding.setProperty(MembershipService.PROFILES_PATH, FactoryResourceProperty.POLICY_ID, policyId);

                notification.throwEvent(new Event(MembershipServiceBean.PROFILES_PATH, suPath, Folder.RESOURCE_NAME,
                        Event.buildEventType(CoreService.SERVICE_NAME, Folder.RESOURCE_NAME, "create"), ""));

                logger.debug("creating the root profile");

                Profile suProfile = new Profile();
                suProfile.setId(UUID.randomUUID().toString());
                suProfile.setFullname("Administrator");
                suProfile.setEmail("");
                suProfile.setAccountStatus(Profile.ACTIVATED);
                suProfile.setOnlineStatus(Profile.OFFLINE);
                suProfile.setLastLoginDate(new Date(0));
                em.persist(suProfile);

                binding.bind(suProfile.getFactoryResourceIdentifier(), suPath);
                binding.setProperty(suPath, FactoryResourceProperty.CREATION_TIMESTAMP, System.currentTimeMillis() + "");
                binding.setProperty(suPath, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
                binding.setProperty(suPath, FactoryResourceProperty.AUTHOR, suPath);

                policyId = UUID.randomUUID().toString();
                pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, suPath, suPath));

                binding.setProperty(suPath, FactoryResourceProperty.OWNER, suPath);
                binding.setProperty(suPath, FactoryResourceProperty.POLICY_ID, policyId);

                notification.throwEvent(new Event(suPath, suPath, Profile.RESOURCE_NAME,
                        Event.buildEventType(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "create"), ""));

                logger.debug("creating the root security rule");

                policyId = UUID.randomUUID().toString();
                pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, suPath, "/"));

                logger.debug("creating the guest profile");

                String anoPath = membership.getProfilePathForIdentifier(MembershipService.UNAUTHENTIFIED_IDENTIFIER);

                Profile anoProfile = new Profile();
                anoProfile.setId(UUID.randomUUID().toString());
                anoProfile.setFullname("Anonymous");
                anoProfile.setEmail("");
                anoProfile.setAccountStatus(Profile.ACTIVATED);
                anoProfile.setOnlineStatus(Profile.OFFLINE);
                anoProfile.setLastLoginDate(new Date(0));
                em.persist(anoProfile);

                binding.bind(anoProfile.getFactoryResourceIdentifier(), anoPath);
                binding.setProperty(anoPath, FactoryResourceProperty.CREATION_TIMESTAMP, System.currentTimeMillis() + "");
                binding.setProperty(anoPath, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
                binding.setProperty(anoPath, FactoryResourceProperty.AUTHOR, suPath);

                String policyId2 = UUID.randomUUID().toString();
                pap.createPolicy(policyId2, PAPServiceHelper.buildOwnerPolicy(policyId2, suPath, anoPath));

                binding.setProperty(anoPath, FactoryResourceProperty.OWNER, suPath);
                binding.setProperty(anoPath, FactoryResourceProperty.POLICY_ID, policyId2);

                notification.throwEvent(new Event(anoPath, suPath, Profile.RESOURCE_NAME,
                        Event.buildEventType(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, "create"), ""));

                logger.debug("allowing guest to create resource in the profile folder");

                pap.updatePolicy(policyId2, PAPServiceHelper.addRuleToPolicy(pap.getPolicy(policyId2), anoPath, new String[] { "create" }));

                logger.debug("adding global security rules");

                InputStream is = this.getClass().getClassLoader().getResourceAsStream("config/root.policy");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int nbRead = 0;

                while ((nbRead = is.read(buffer)) > -1) {
                    baos.write(buffer, 0, nbRead);
                }

                pap.createPolicy("global.root.policy", baos.toString());
                baos.close();

                Properties props = new Properties();
                props.setProperty("bootstrap.status", "done");
                props.setProperty("bootstrap.timestamp", System.currentTimeMillis() + "");
                props.setProperty("bootstrap.version", BootstrapService.VERSION);

                File file = new File();
                file.setId(UUID.randomUUID().toString());
                file.setName("bootstrap.properties");
                file.setContentType("text/plain");
                file.setDescription("bootstrap properties");
                file.setNbReads(0);
                file.setBlob(Hibernate.createBlob(new ByteArrayInputStream(props.toString().getBytes())));
                file.setSize(file.getBlob().length());
                em.persist(file);

                binding.bind(file.getFactoryResourceIdentifier(), BootstrapServiceBean.BOOTSTRAP_FILE_PATH);

                binding.setProperty(BootstrapServiceBean.BOOTSTRAP_FILE_PATH, FactoryResourceProperty.CREATION_TIMESTAMP, "" + System.currentTimeMillis());
                binding.setProperty(BootstrapServiceBean.BOOTSTRAP_FILE_PATH, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, "" + System.currentTimeMillis());
                binding.setProperty(BootstrapServiceBean.BOOTSTRAP_FILE_PATH, FactoryResourceProperty.AUTHOR, suPath);

                policyId = UUID.randomUUID().toString();
                pap.createPolicy(policyId, PAPServiceHelper.addRuleToPolicy(PAPServiceHelper.buildOwnerPolicy(policyId, suPath, BootstrapServiceBean.BOOTSTRAP_FILE_PATH), "/profiles/guest" , new String[] {"read"}));

                binding.setProperty(BootstrapServiceBean.BOOTSTRAP_FILE_PATH, FactoryResourceProperty.OWNER, suPath);
                binding.setProperty(BootstrapServiceBean.BOOTSTRAP_FILE_PATH, FactoryResourceProperty.POLICY_ID, policyId);

                notification.throwEvent(new Event(BootstrapServiceBean.BOOTSTRAP_FILE_PATH, suPath, File.RESOURCE_NAME,
                        Event.buildEventType(CoreService.SERVICE_NAME, File.RESOURCE_NAME, "create"), ""));
            } catch (Exception e) {
                ctx.setRollbackOnly();
                throw new BootstrapServiceException("error during bootstrap", e);
            }

            logger.info("factory bootstrap done.");
        } catch (BindingServiceException e) {
            throw new BootstrapServiceException("error during bootstrap", e);
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

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FactoryResource findResource(String path) throws FactoryException {
        logger.info("findResource(...) called");
        logger.debug("params : path=" + path);

        throw new BootstrapServiceException("Bootstrap service does not manage any resource");
    }
}
