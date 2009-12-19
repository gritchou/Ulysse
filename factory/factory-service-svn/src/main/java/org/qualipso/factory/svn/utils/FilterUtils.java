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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.svn.exception.SVNTechnicalException;
import org.qualipso.factory.svn.ssh.command.filter.SVNOperationType;
import org.qualipso.factory.svn.ssh.command.filter.component.RequestComponent;
import org.qualipso.factory.svn.ssh.command.filter.component.RequestComponentOperation;
import org.qualipso.factory.svn.ssh.command.filter.component.RequestComponentResource;
import org.qualipso.factory.svn.ssh.command.filter.component.RequestComponentUrl;
import org.qualipso.factory.svn.ssh.command.filter.component.SVNResource;
import org.qualipso.factory.svn.ssh.command.filter.component.SVNResourceType;

/**
 * 
 * Filter utils
 *
 */
public class FilterUtils {

	/**
	 * LogFactory
	 */
	private static final Log logger = LogFactory.getLog(FilterUtils.class);
	
	/**
	 * Extract URL of the request
	 * @param request to analyze
	 * @param beginIndex of analyze
	 * @param requestComponents components of the request
	 */
	public static void extractURL(String request, int beginIndex, final Map<Integer, RequestComponent> requestComponents) {
		logger.debug("extractURL called...");
		logger.debug("request=" + request);
		logger.debug("beginIndex=" + beginIndex);
		
		if (StringUtils.isEmpty(request)) {
			return;
		}
		if (beginIndex >= request.length()) {
			return;
		}
		
		if (requestComponents == null) {
			logger.error("requestComponents cannot be null");
			throw new SVNTechnicalException("requestComponents cannot be null");
		}
		
		//Token to search
		final String urlToken = "svn+ssh";
		
		//String to extract url
		String temp = request.substring(beginIndex);
		logger.debug("extractURL in " + temp);

		if (temp.contains(urlToken)) {
			int indexCommand = temp.indexOf(urlToken);
			if (indexCommand >= 0) {
				temp = temp.substring(indexCommand);
				
				int index = temp.indexOf(" ");
				if (index > 1) {
					temp = temp.substring(0, index);
				}
				
				//Construct url
				RequestComponent component = new RequestComponentUrl(request, temp);
				
				requestComponents.put((indexCommand + beginIndex), component);
				if (logger.isDebugEnabled()) {
					logger.debug("extracted=" + component.toString());
				}
				extractURL(request, indexCommand + beginIndex + 1, requestComponents);
			}
		}
	}
	
	/**
	 * Extract svn operation
	 * @param request to analyze
	 * @param beginIndex of analyze
	 * @param operationType svn operation to extract
	 * @param requestComponents components of the request
	 */
	public static void extractSVNOperation(String request, int beginIndex, SVNOperationType operationType, final Map<Integer, RequestComponent> requestComponents) {
		logger.debug("extractSVNOperation called...");
		logger.debug("request=" + request);
		logger.debug("beginIndex=" + beginIndex);
		logger.debug("operationType=" + operationType);
		
		if (StringUtils.isEmpty(request)) {
			return;
		}
		if (beginIndex >= request.length()) {
			return;
		}
		
		if (requestComponents == null) {
			logger.error("requestComponents cannot be null");
			throw new SVNTechnicalException("requestComponents cannot be null");
		}
		
		//Token to search
		String searchString = "( "+ operationType.toString() + " (";
		
		String temp = request.substring(beginIndex);
		logger.debug("extractSVNOperation in " + temp);
		
		if (temp.contains(searchString)) {
			int indexCommand = temp.indexOf(searchString);
			
			RequestComponent component = new RequestComponentOperation(request, operationType);
			requestComponents.put((indexCommand + beginIndex), component);
			if (logger.isDebugEnabled()) {
				logger.debug("extracted=" + component.toString());
			}
			
			extractSVNOperation(request, indexCommand + beginIndex + 1, operationType, requestComponents);
		}
	}
	
	
	/**
	 * Extract SVNNode
	 * @param request to analyze
	 * @param beginIndex of analyze
	 * @param resourceType SVNResourceType
	 * @param requestComponents components of the request
	 */
	public static void extractSVNResource(String request, int beginIndex, SVNResourceType resourceType, final Map<Integer, RequestComponent> requestComponents) {
		logger.debug("extractSVNResource called...");
		logger.debug("request=" + request);
		logger.debug("beginIndex=" + beginIndex);
		logger.debug("SVNResourceType=" + resourceType.toString());
		
		if (StringUtils.isEmpty(request)) {
			return;
		}
		if (beginIndex >= request.length()) {
			return;
		}
		
		if (requestComponents == null) {
			logger.error("requestComponents cannot be null");
			throw new SVNTechnicalException("requestComponents cannot be null");
		}
		
		//Substring
		String temp = request.substring(beginIndex);
		logger.debug("extractSVNResource in " + temp);
		if (temp.contains(resourceType.toString())) {
			int indexCommand = temp.indexOf(resourceType.toString());
			if (indexCommand >= 0) {
				temp = temp.substring(indexCommand);
			}
			
			int index = temp.indexOf(":");
			if (index > 1) {
				temp = temp.substring(index + 1);
			}
			
			index = temp.indexOf(":");
			if (index > 1) {
				temp = temp.substring(0, index);
			}
			
			index = temp.lastIndexOf(" ");
			if (index > 1) {
				temp = temp.substring(0, index);
			}
			
			SVNResource queryResource = new SVNResource(resourceType, temp);
			RequestComponent component = new RequestComponentResource(request, queryResource);
			requestComponents.put((indexCommand + beginIndex), component);
			if (logger.isDebugEnabled()) {
				logger.debug("extracted=" + component.toString());
			}
			
			extractSVNResource(request, indexCommand + beginIndex + 1, resourceType, requestComponents);
		}
	}
	
	/**
	 * Extract SVNNode of delete type
	 * @param request to analyze
	 * @param beginIndex of analyze
	 * @param requestComponents components of the request
	 */
	public static void extractSVNResourceDelete(String request, int beginIndex, final Map<Integer, RequestComponent> requestComponents) {
		logger.debug("extractSVNResourceDelete called...");
		logger.debug("request=" + request);
		logger.debug("beginIndex=" + beginIndex);
		
		if (StringUtils.isEmpty(request)) {
			return;
		}
		if (beginIndex >= request.length()) {
			return;
		}
		
		if (requestComponents == null) {
			logger.error("requestComponents cannot be null");
			throw new SVNTechnicalException("requestComponents cannot be null");
		}
		
		//Substring
		String temp = request.substring(beginIndex);
		logger.debug("extractSVNResourceDelete in " + temp);
		if (temp.contains(SVNResourceType.DELETE_ENTRY.toString())) {
			int indexCommand = temp.indexOf(SVNResourceType.DELETE_ENTRY.toString());
			if (indexCommand > 0) {
				temp = temp.substring(indexCommand);
			}
			
			int index = temp.indexOf(":");
			if (index > 1) {
				temp = temp.substring(index+1);
			}
			
			index = temp.indexOf(":");
			if (index > 1) {
				temp = temp.substring(0, index);
			}
			
			index = temp.lastIndexOf(" ");
			if (index > 1) {
				temp = temp.substring(0, index);
			}
			
			//Extract string between ()
			index = temp.indexOf(" (");
			if (index >= 0) {
				temp = temp.substring(0, index);
				SVNResource queryResource = new SVNResource(SVNResourceType.DELETE_ENTRY, temp);
				RequestComponent component = new RequestComponentResource(request, queryResource);
				requestComponents.put((indexCommand + beginIndex), component);
				
				if (logger.isDebugEnabled()) {
					logger.debug("extracted=" + component.toString());
				}
			}
			extractSVNResourceDelete(request, indexCommand + beginIndex + 1, requestComponents);
		}
	}
	
	/**
	 * extract id repository from url
	 * @param pUrl to extract id repository
	 * @return id repository
	 */
	public static String extractIdRepositoryFromUrl(String pUrl) {
		String idRepository = "";
		
		if (!StringUtils.isEmpty(pUrl)) {
			for (String urltoken : SVNProperties.getInstance().getUrlsAccess()) {
				int index = pUrl.indexOf(urltoken);
				if (index >= 0) {
					String tmpUrl = pUrl.substring(index + urltoken.length());
					String[] parts = FilterUtils.splitPath(tmpUrl);
					
					if (parts != null && parts.length > 0) {
						String lIdRepo = parts[0];
						if (!StringUtils.isEmpty(lIdRepo)) {
							return lIdRepo;
						}
					}
				}
			}
		}
		
		return idRepository;
	}
	
	/**
	 * split path
	 * @param path to split
	 * @return array
	 */
	public static String[] splitPath(String path) {
		String[] parts = null;
		if (path != null) {
			String pathToSplit = path;
			//Delete parasite characters
			pathToSplit = pathToSplit.replaceAll("'", "");
			pathToSplit = pathToSplit.replaceAll("\"", "");
			if (pathToSplit.startsWith("/")) {
				pathToSplit = pathToSplit.substring(1);
			}
			if (pathToSplit.endsWith("/")) {
				pathToSplit = pathToSplit.substring(0, (pathToSplit.length() - 1));
			}
			parts = pathToSplit.split("/");
		}
		
		return parts;
	}
	
	/**
	 * Generate the resource path
	 * @param svnPath path svn
	 * @param relatifPath of the resource
	 * @return list sorted of the url part
	 */
	public static List<String> generateResourcePath(String svnPath, String relatifPath) {
		List<String> list = new ArrayList<String>();
		
		/*
		 * svnPath
		 */
		if (!StringUtils.isEmpty(svnPath)) {
			String[] paths = FilterUtils.splitPath(svnPath);
			
			if (paths != null) {
				for (String string : paths) {
					list.add(string);
				}
			}
		}
		
		
		/*
		 * relatifPath
		 */
		if (!StringUtils.isEmpty(relatifPath)) {
			String[] relatifPathArray = FilterUtils.splitPath(relatifPath);
			
			for (String string : relatifPathArray) {
				list.add(string);
			}
		}
		return list;
	}
}
