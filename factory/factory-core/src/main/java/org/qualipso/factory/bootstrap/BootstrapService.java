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

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;

import javax.ejb.Remote;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * Bootstrap Service provide a way to create all initial resources, security rules, needed to have a useable system.<br/>
 * <br/>
 * The bootstrap() method of this service should be called preliminary to any operation on the factory.
 * You can ensure that this method has been called testing if the /bootstrap node exists or not<br/>
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 4 September 2009
 */
@Remote
@WebService(name = BootstrapService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + BootstrapService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface BootstrapService extends FactoryService {
    public static final String SERVICE_NAME = "bootstrap";
    public static final String[] RESOURCE_TYPE_LIST = new String[] {  };
    public static final String VERSION = "1.2";
    public static final String BOOTSTRAP_FILE_PATH = "/bootstrap";

    public void bootstrap() throws BootstrapServiceException;
}
