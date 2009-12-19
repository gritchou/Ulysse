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

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.security.pep.AccessDeniedException;

import javax.ejb.Remote;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * Browser Service is a central service giving the ability to navigate into the naming scheme of the factory.<br/>
 * <br/>
 * For exemple, you can list the children of the root node (/) to discover the content of the factory.
 * If you want to acces some content directly, you also benefits of generic finder method findResource() which will
 * give you convenient method to recover real resource data in one simple lookup. Underlying service which manage the binded
 * resource is called to load the resource dynamically.<br/>
 * <br/>
 * This external service is visible for factory users as a WebService or using RMI.<br/>
 * <br/>
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 16 june 2009
 */
@Remote
@WebService(name = BrowserService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + BrowserService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface BrowserService extends FactoryService {
    public static final String SERVICE_NAME = "browser";
    public static final String[] RESOURCE_TYPE_LIST = new String[] {  };

    /**
     * Find a resource in a generic way, relying on the concrete service which manage this type of resource.
     *
     * @param path the path of the resource you want to find
     * @return the FactoryResource
     * @throws BrowserServiceException if the resource cannot be found.
     */
    @WebMethod
    @WebResult(name = "resource")
    public FactoryResource findResource(@WebParam(name = "path")
    String path) throws BrowserServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Check if the given path contains children or not.
     *
     * @param path the path you want to check
     * @return true if there is some children, false otherwise
     * @throws BrowserServiceException if the path does not exists.
     */
    @WebMethod
    @WebResult(name = "has-children")
    public boolean hasChildren(@WebParam(name = "path")
    String path) throws BrowserServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * List the children of a given path.<br/>
     * <br/>
     * The full path of all children of the given path are returned as a String array.
     *
     * @param path the path you want to list
     * @return a String array containing the full path of each children
     * @throws BrowserServiceException if the path does not exists.
     */
    @WebMethod
    @WebResult(name = "children")
    public String[] listChildren(@WebParam(name = "path")
    String path) throws BrowserServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * List the children of a given path filtered using a service regular expression pattern and a resource type regular
     * expression pattern.<br/>
     * <br/>
     * The full path of all matching children of the given path are returned as a String array.<br/>
     * <br/>
     *          
     * @param path the path you want to list
     * @param servicePattern the pattern for the service name of children
     * @param typePattern the pattern for the resource type of children
     * @return a String array containing the full path of each matching children.
     * @throws BrowserServiceException if the path does not exists.
     */
    @WebMethod
    @WebResult(name = "children")
    public String[] listChildrenOfType(@WebParam(name = "path")
    String path, @WebParam(name = "service-pattern")
    String servicePattern, @WebParam(name = "type-pattern")
    String typePattern) throws BrowserServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;
    
    
//    /**
//     * Test if the specified path already exists in the naming tree<br/>
//     * <br/>
//     * To perform this action, you should have the 'read' permission on this path or on the parent path<br/>
//     * 
//     * @Param path the path you want to test
//     * @return true if the path exists, false otherwise
//     * @throws BrowserServiceException.
//     */
//    @WebMethod
//    @WebResult(name = "exists")
//    public boolean exists(@WebParam(name = "path") String path) throws BrowserServiceException; 
}
