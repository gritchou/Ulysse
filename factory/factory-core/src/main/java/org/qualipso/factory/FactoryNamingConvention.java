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
package org.qualipso.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Factory naming conventions class.<br/>
 * This class holds all conventions name for naming factory components ; this includes jndi prefix, web services context and urls, namespaces, etc...<br/>
 * Most of this names are used in component annotations to ensure uniform component names in the application server.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
public class FactoryNamingConvention {
    private static Log logger = LogFactory.getLog(FactoryNamingConvention.class);
    public static final String SERVICE_PREFIX = "factory-service-";
    public static final String LOCAL_SERVICE_PREFIX = "factory-local-service-";
    public static final String WEB_SERVICE_ROOT_MODULE_CONTEXT = "/factory";
    public static final String WEB_SERVICE_CORE_MODULE_CONTEXT = WEB_SERVICE_ROOT_MODULE_CONTEXT + "-core";
    public static final String WEB_SERVICE_URL_PATTERN_PREFIX = "/";
    public static final String SERVICE_NAMESPACE = "http://org.qualipso.factory.ws/service/";
    public static final String RESOURCE_NAMESPACE = "http://org.qualipso.factory.ws/resource/";
    public static final String SEARCH_NAMESPACE = "http://org.qualipso.factory.ws/search/";
    public static final String EVENT_NAMESPACE = "http://org.qualipso.factory.ws/event/";
    

    /**
     * @param the service name
     * @return a String representing the jndi name of this service
     */
    public static String getJNDINameForService(String service) {
        if (!service.toLowerCase().equals(service)) {
            logger.warn("service names should not contains caps");
        }

        return SERVICE_PREFIX + service;
    }

    /**
     * @param the service name
     * @return a String representing the local jndi name of this service
     */
    public static String getJNDINameForLocalService(String service) {
        if (!service.toLowerCase().equals(service)) {
            logger.warn("service names should not contains caps");
        }

        return LOCAL_SERVICE_PREFIX + service;
    }

    /**
     * @param the jndi name of a service
     * @return a String representing the name of this service
     */
    public static String getServiceNameFromJNDI(String jndiName) {
        return jndiName.substring(SERVICE_PREFIX.length());
    }
}
