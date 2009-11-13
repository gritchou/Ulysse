/**
 * 
 * Copyright (C) 2006-2010 THALES
 * http://www.thalesgroup.fr - gregory.cunha@thalesgroup.com
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the license LGPL.
 *
 * Initial author :
 * Gregory Cunha from Thales Service, THERESIS Competence Center Open Source Software
 *
 */
package org.qualipso.factory.svn.utils;

import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.svn.exception.SubversionTechnicalException;

/**
 * Access class to the properties file 
 */
public class SubversionResources {

	/**
	 * Resource file name
	 */
	private static final String RESOURCE_FILE_NAME = "subversion-service";
	
	
	/**
	 * svnserve command on the server
	 * For exemple /usr/bin/svnserve
	 */
	private String cmdSvnServe;
	
	/**
	 * Root Directory for the created repositories
	 * For exemple /data/svn-repositories
	 */
	private String rootDirRepositories;
	
   
   /**
    * SubversionResources
    */
   private static SubversionResources subversionResource;

   /**
    * Logger
    */
   private static Log logger = LogFactory.getLog(SubversionResources.class);

   /**
    * svnserve command on the server
    * For exemple /usr/bin/svnserve
    */
   private final static String PROP_CMD_SVNSERVE = "subversion.cmd.svnserve";
   
   /**
    * Root Directory for the created repositories
    * For exemple /data/svn-repositories
    */
   private final static String PROP_ROOT_DIR_REPOSITORIES = "subversion.root.dir.repositories";
   
   
   /**
    * private constructor for singleton
    * @param bundle
    */
   private SubversionResources() {
	   ResourceBundle resource = ResourceBundle.getBundle(RESOURCE_FILE_NAME);
	   
	   if (resource == null) {
		   throw new SubversionTechnicalException(RESOURCE_FILE_NAME + " not found in classpath");
	   }
	   logger.debug(RESOURCE_FILE_NAME + ".properties Loaded");
	   
	   //WSEndPoint
	   this.cmdSvnServe = resource.getString(PROP_CMD_SVNSERVE);
	   checkProperty(PROP_CMD_SVNSERVE, this.cmdSvnServe);
	   
	 //WSUserName
	   this.rootDirRepositories = resource.getString(PROP_ROOT_DIR_REPOSITORIES);
	   checkProperty(PROP_ROOT_DIR_REPOSITORIES, this.rootDirRepositories);
	   
   }

   public static SubversionResources getInstance() {

	   if (subversionResource == null) {
		   subversionResource = new SubversionResources();
	   }
	   return subversionResource;
   }

   
   
   /**
 * @return the cmdSvnServe
 */
public String getCmdSvnServe() {
	return cmdSvnServe;
}

/**
 * @return the rootDirRepositories
 */
public String getRootDirRepositories() {
	return rootDirRepositories;
}

/**
    * Check if the property is not empty. Throw BugTrackerTechnicalException if value of the property is empty
    * @param property to check
    * @param value of the property which is checked
    */
   private void checkProperty(String property, String value) {
	   if (StringUtils.isEmpty(value)) {
		   throw new SubversionTechnicalException("propertiy " + property + " not found in " + RESOURCE_FILE_NAME);
	   }
   }
}
