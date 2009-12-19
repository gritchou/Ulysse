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

import java.util.Properties;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;


/**
 * This is a global class for Factory framework. <br/>
 * This class gives convenient static methods for global framework operations.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
public class Factory {
    private static Log logger = LogFactory.getLog(Factory.class);
    private static InitialContext jndi;

    private static synchronized InitialContext getJndiContext()
        throws FactoryException {
        try {
            if (jndi == null) {
                Properties properties = new Properties();
                properties.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
                properties.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
                properties.put("java.naming.provider.url", "localhost:1099");
                jndi = new InitialContext(properties);
            }

            return jndi;
        } catch (Exception e) {
            throw new FactoryException(e);
        }
    }

    /**
     *
     * @return A string array with all available factory services names.
     * @throws FactoryException
     */
    public static String[] listServices() throws FactoryException {
        try {
            NamingEnumeration<NameClassPair> enumeration = getJndiContext().list("");
            Vector<String> result = new Vector<String>();

            while (enumeration.hasMoreElements()) {
                String name = ((NameClassPair) enumeration.next()).getName();

                if (name.startsWith(FactoryNamingConvention.SERVICE_PREFIX)) {
                    logger.debug("jndi service name found : " + name);
                    result.add(FactoryNamingConvention.getServiceNameFromJNDI(name));
                }
            }

            return result.toArray(new String[result.size()]);
        } catch (Exception e) {
            throw new FactoryException(e);
        }
    }

    public static FactoryService findService(String serviceName)
        throws FactoryException {
        try {
            logger.debug("looking in jndi: " + FactoryNamingConvention.getJNDINameForService(serviceName));

            return (FactoryService) getJndiContext().lookup(FactoryNamingConvention.getJNDINameForService(serviceName));
        } catch (Exception e) {
            throw new FactoryException(e);
        }
    }
}
