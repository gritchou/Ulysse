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

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.security.pep.AccessDeniedException;


/**
 * Security Service allow to manage access control on factory resources.<br/>
 * <br/>
 * Access control relies on the XACML sub system. This service contains useful methods to abstract the usage of XACML.
 * Increasing the level of implementation of this interface should allow to make transparent modifications on the
 * security model used in the underlying XACML sub system.<br/>
 * <br/>
 * This external service is visible for factory users as a WebService or using RMI.<br/>
 * <br/>
 * Resource owner has all permissions and it's not allowed to remove owner rule.<br/>
 * <br/>
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 23 june 2009
 */
@Remote
@WebService(name = SecurityService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + SecurityService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface SecurityService extends FactoryService {
    public static final String SERVICE_NAME = "security";
    public static final String[] RESOURCE_TYPE_LIST = new String[] {  };

    /**
     * Retrieve the XACML Security Policy associated with this path.<br/>
     * <br/>
     * Anybody which has the 'read' permission shoudl be able to read its security policy.
     *
     * @param path the path to retrieve the security policy from.
     * @return a String representation of the XACML policy associated with this path.
     * @throws SecurityServiceException
     */
    @WebMethod
    @WebResult(name = "security-policy")
    public String getSecurityPolicy(String path) throws SecurityServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Add a security rule to an existing XACML policy. Rule is generated using underlying implementation according
     * to security model.<br/>
     * <br/>
     * The subject should be anything ; it's a path pointing to the resource you want to set the security rule on. 
     * Depending on the implementation, the subject can be a Profile, a Group, but also any other
     * resource that is capable of grouping Profiles. For example we could imagine a Department resource containing groups containing
     * users.<br/>
     * <br/>
     * Only owner should be able to modify security rules<br/>
     * <br/>
     * For example, addSecurity("/projects/project1", "/profiles/jayblanc", "read,update") 
     * will add the 'read' and 'update' permissions to profile jayblanc on resource /projects/project1
     * <br/>
     * @param path the path of the resource on which you want to add a security rule
     * @param subject the subject for this new security rule
     * @param permissions the permissions list to give to this subject, comma separated.
     * @throws SecurityServiceException
     */
    @WebMethod
    public void addSecurityRule(String path, String subject, String permissions)
        throws SecurityServiceException, OwnerException, SubjectException, InvalidPathException, PathNotFoundException;

    /**
     * Modify an existing security rule of an existing security policy.<br/>
     * <br/>
     * If the rule already exists, it is replace with this one according to the permissions given.<br/>
     * <br/>
     * Only owner should be able to modify security rules<br/>
     * <br/>
     * @param path the path of the resource to edit security rule from.
     * @param subject the subject of this rule.
     * @param permissions the new permissions list comma separated.
     * @throws SecurityServiceException
     */
    @WebMethod
    public void editSecurityRule(String path, String subject, String permissions)
        throws SecurityServiceException, OwnerException, SubjectException, InvalidPathException, PathNotFoundException;

    /**
     * Remove an existing security rule of an existing security policy.<br/>
     * <br/>
     * Only owner should be able to modify security rules<br/>
     * <br/>
     * @param path the path of the resource to remove security rule from.
     * @param subject the subject of the rule to remove
     * @throws SecurityServiceException
     */
    @WebMethod
    public void removeSecurityRule(String path, String subject)
        throws SecurityServiceException, OwnerException, SubjectException, InvalidPathException, PathNotFoundException;

    /**
     * Change the owner of this resource.<br/>
     * <br/>
     * Only owner should be able to modify security rules<br/>
     * <br/>
     * @param path the path of the owner resource.
     * @param newOwner the new owner resource.
     * @throws SecurityServiceException
     */
    @WebMethod
    public void changeOwner(String path, String newOwner)
        throws SecurityServiceException, OwnerException, SubjectException, InvalidPathException, PathNotFoundException;
}
