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
package org.qualipso.factory.bugtracker.core;

import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.qualipso.factory.bugtracker.exception.BugTrackerTechnicalException;

/**
 * Access class to the properties file 
 */
public class BugTrackerResources {

	/**
	 * Resource file name
	 */
	private static final String RESOURCE_FILE_NAME = "bugtracker-service";
	
	
	/**
	 * WS end point for the bugtracker software
	 */
	private String wSEndPoint;
	
	/**
	 * WS user name for the bugtracker software
	 */
	private String wSUserName;
	
	/**
	 * WS user password name for the bugtracker software
	 */
	private String wSUserPassword;
	
   
   /**
    * BugTrackerResource
    */
   private static BugTrackerResources bugTrackerResource;

   /**
    * Logger
    */
   private static Logger logger = Logger.getLogger(BugTrackerResources.class);

   /**
    * WS End point propertie name
    */
   private final static String PROP_WS_END_POINT = "bugtracker.ws.endpoint";
   
   /**
    * WS user name propertie name
    */
   private final static String PROP_WS_USER_NAME = "bugtracker.ws.user.name";
   
   /**
    * WS user password propertie name
    */
   private final static String PROP_WS_USER_PWD = "bugtracker.ws.user.password";

   
   /**
    * private constructor for singleton
    * @param bundle
    */
   private BugTrackerResources() {
	   ResourceBundle resource = ResourceBundle.getBundle(RESOURCE_FILE_NAME);
	   
	   if (resource == null) {
		   throw new BugTrackerTechnicalException(RESOURCE_FILE_NAME + " not found in classpath");
	   }
	   logger.debug(RESOURCE_FILE_NAME + ".properties Loaded");
	   
	   //WSEndPoint
	   this.wSEndPoint = resource.getString(PROP_WS_END_POINT);
	   checkProperty(PROP_WS_END_POINT, this.wSEndPoint);
	   
	 //WSUserName
	   this.wSUserName = resource.getString(PROP_WS_USER_NAME);
	   checkProperty(PROP_WS_USER_NAME, this.wSUserName);
	   
	 //WSUserPassword
	   this.wSUserPassword = resource.getString(PROP_WS_USER_PWD);
	   checkProperty(PROP_WS_USER_PWD, this.wSUserPassword);
	   
   }

   public static BugTrackerResources getInstance() {

	   if (bugTrackerResource == null) {
		   bugTrackerResource = new BugTrackerResources();
	   }
	   return bugTrackerResource;
   }


   /**
    * get the WS End point
    * @return EndPoint
    */
   public final String getWSEndPoint() {
	   return this.wSEndPoint;
   }
   
   /**
    * get the WS user name
    * @return user name
    */
   public final String getWSUserName() {
	   return this.wSUserName;
   }
   
   /**
    * get the WS user password
    * @return password
    */
   public final String getWSUserPassword() {
	   return this.wSUserPassword;
   }
   
   /**
    * Check if the property is not empty. Throw BugTrackerTechnicalException if value of the property is empty
    * @param property to check
    * @param value of the property which is checked
    */
   private void checkProperty(String property, String value) {
	   if (StringUtils.isEmpty(value)) {
		   throw new BugTrackerTechnicalException("propertiy " + property + " not found in " + RESOURCE_FILE_NAME);
	   }
   }
}
