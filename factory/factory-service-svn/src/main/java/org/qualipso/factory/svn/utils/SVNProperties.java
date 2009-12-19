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

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.svn.exception.SVNTechnicalException;

/**
 * Access class to the properties file 
 */
public class SVNProperties {

	/**
	 * No limit for check right
	 */
	public static final int DEPTH_CHECK_RIGHT_NO_LIMIT = -1;
	
	/**
	 * Resource file name
	 */
	private static final String RESOURCE_FILE_NAME = "subversion-service";
	
	/**
	 * Separator for urls il property file
	 */
	private static final String URLS_PROP_SEPARATOR = ",";
	
	
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
	    * Depth check right
	    * -1 -> limitless (all resources are checked)
	    * 0 -> Only repository
	    * 1 -> Access of the first folder in the repository
	    */
	private int depthCheckRights;
	
   /**
    * Urls to access to ssh server
    */
	private List<String> urlsAccess;
	
	
   /**
    * SubversionResources
    */
   private static SVNProperties subversionResource;

   /**
    * Logger
    */
   private static Log logger = LogFactory.getLog(SVNProperties.class);

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
    * Depth check right
    * -1 -> limitless (all resources are checked)
    * 0 -> Only repository
    * 1 -> Access of the first folder in the repository
    */
   private final static String PROP_DEPTH_CHECK_RIGHTS = "subversion.depth.check.rights";
   
   
   /**
    * Urls to access to ssh server
    * List of urls (separator ,)
    */
   private final static String PROP_SSH_ACCESS_URLS = "subversion.ssh.access.urls";
   
   /**
    * private constructor for singleton
    * @param bundle
    */
   private SVNProperties() {
	   ResourceBundle resource = ResourceBundle.getBundle(RESOURCE_FILE_NAME);
	   
	   if (resource == null) {
		   throw new SVNTechnicalException(RESOURCE_FILE_NAME + " not found in classpath");
	   }
	   logger.debug(RESOURCE_FILE_NAME + ".properties Loaded");
	   
	   //svnserve
	   this.cmdSvnServe = resource.getString(PROP_CMD_SVNSERVE);
	   checkProperty(PROP_CMD_SVNSERVE, this.cmdSvnServe);
	   
	   //roorDir
	   this.rootDirRepositories = resource.getString(PROP_ROOT_DIR_REPOSITORIES);
	   checkProperty(PROP_ROOT_DIR_REPOSITORIES, this.rootDirRepositories);
	   
	   //depth
	   final String sDepthCheckRights = resource.getString(PROP_DEPTH_CHECK_RIGHTS);
	   checkIntProperty(PROP_DEPTH_CHECK_RIGHTS, sDepthCheckRights);
	   this.depthCheckRights = Integer.parseInt(sDepthCheckRights);
	   
	   //url
	   final String sUrls = resource.getString(PROP_SSH_ACCESS_URLS);
	   this.urlsAccess = extractUrls(sUrls);
	   checkPropertyList(PROP_SSH_ACCESS_URLS, this.urlsAccess);
	   
   }

   public static SVNProperties getInstance() {

	   if (subversionResource == null) {
		   subversionResource = new SVNProperties();
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
	 * @return the depthCheckRights
	 */
	public int getDepthCheckRights() {
		return depthCheckRights;
	}
	
	

	/**
	 * @return the urlsAccess
	 */
	public List<String> getUrlsAccess() {
		return urlsAccess;
	}

	/**
    * Check if the property is not empty. Throw SubversionTechnicalException if value of the property is empty
    * @param property to check
    * @param value of the property which is checked
    */
   private void checkProperty(String property, String value) {
	   if (StringUtils.isEmpty(value)) {
		   throw new SVNTechnicalException("property " + property + " not found in " + RESOURCE_FILE_NAME);
	   }
	   logger.debug(property + "=" + value);
   }
   
	/**
    * Check if the property is not empty and is numeric. Throw SubversionTechnicalException if value of the property is empty or not numeric
    * @param property to check
    * @param value of the property which is checked
    */
   private void checkIntProperty(String property, String value) {
	   checkProperty(property, value);
	   
	   if (!StringUtils.isNumeric(value)) {
		   throw new SVNTechnicalException("Value + [" + value + "] for property " + property + " " +
		   		"in properties file " + RESOURCE_FILE_NAME + " + must be numeric");
	   }
   }
   
	/**
    * Check if the property is not empty. Throw SubversionTechnicalException if value of the property is empty
    * @param property to check
    * @param value of the property which is checked
    */
   private void checkPropertyList(String property, List<String> value) {
	   if (value == null || value.isEmpty()) {
		   throw new SVNTechnicalException("property " + property + " not found in " + RESOURCE_FILE_NAME);
	   }
	   logger.debug(property + "=" + value);
   }
   
   /**
    * Extract url from the string value
    * @param value of the property
    * @return list of url
    */
   private List<String> extractUrls(String value) {
	   List<String> urls = new ArrayList<String>();
	   
	   if (!StringUtils.isEmpty(value)) {
		   String[] urlsArray = value.split(URLS_PROP_SEPARATOR);
		   
		   if (urlsArray != null) {
			   for (String url : urlsArray) {
				   if (url != null) {
					   String urlToAdd = url.trim();
					   if (!StringUtils.isEmpty(urlToAdd)) {
						   urls.add(urlToAdd);
					   }
				   }
			   }
		   }
	   }
	   return urls;
   }
}
