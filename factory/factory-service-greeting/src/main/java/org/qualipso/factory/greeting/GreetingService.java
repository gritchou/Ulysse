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
package org.qualipso.factory.greeting;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathAlreadyBoundException;
import org.qualipso.factory.binding.PathNotEmptyException;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.greeting.entity.Name;
import org.qualipso.factory.security.pep.AccessDeniedException;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 june 2009
 */
@Remote
@WebService(name = GreetingService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + GreetingService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface GreetingService extends FactoryService {
    public static final String SERVICE_NAME = "greeting";
    public static final String[] RESOURCE_TYPE_LIST = new String[] { Name.RESOURCE_NAME };

    @WebMethod
    public void createName(String path, String name) throws GreetingServiceException, AccessDeniedException, InvalidPathException, PathAlreadyBoundException;

    @WebMethod
    @WebResult(name = "name")
    public Name readName(String path) throws GreetingServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    @WebMethod
    public void updateName(String path, String name) throws GreetingServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    @WebMethod
    public void deleteName(String path) throws GreetingServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException, PathNotEmptyException;

    @WebMethod
    @WebResult(name = "message")
    public String sayHello(String path) throws GreetingServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;
}
