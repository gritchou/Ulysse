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
package org.qualipso.factory.svn.ssh.command.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.svn.exception.SVNServiceException;
import org.qualipso.factory.svn.exception.SVNTechnicalException;
import org.qualipso.factory.svn.ssh.command.TaskType;
import org.qualipso.factory.svn.ssh.command.filter.component.RequestComponent;
import org.qualipso.factory.svn.ssh.command.filter.component.SVNResource;
import org.qualipso.factory.svn.ssh.command.filter.component.SVNResourceType;
import org.qualipso.factory.svn.utils.FilterUtils;
import org.qualipso.factory.svn.utils.SVNProperties;

/**
 * 
 * Filter of a request svn
 *
 */
public class RequestFilter {

	/**
	 * LogFactory
	 */
	private static final Log logger = LogFactory.getLog(RequestFilter.class);

	
	/**
	 * id of the repository in the url
	 */
	private String idRepository;
	
	/**
	 * SVN operation of the request
	 */
	private SVNOperationType svnOperation;
	
	/**
	 * SVN resources treated by the request
	 */
	private List<ExecutedResource> resources = new ArrayList<ExecutedResource>();
	
	/**
	 * Path checked in request
	 */
	protected List<String> pathsChecked = new ArrayList<String>();
	
	/**
	 * Map to know which task and service must be necessary to observe to extract SVNNode
	 */
	private static Map<SVNOperationType, SVNOperationBehavior> operationsBehavior = new HashMap<SVNOperationType, SVNOperationBehavior>();
	
	static {
		//commit
		SVNOperationBehavior commitBehavior = new SVNOperationBehavior(AbstractServiceExecutorWrite.getInstance());
		commitBehavior.addTaskAccepted(TaskType.C2S);
		operationsBehavior.put(SVNOperationType.COMMIT, commitBehavior);
		
		//update
		SVNOperationBehavior updateBehavior = new SVNOperationBehavior(AbstractServiceExecutorRead.getInstance());
		updateBehavior.addTaskAccepted(TaskType.S2C);
		operationsBehavior.put(SVNOperationType.UPDATE, updateBehavior);
		
		//diff
		SVNOperationBehavior diffBehavior = new SVNOperationBehavior(AbstractServiceExecutorRead.getInstance());
		diffBehavior.addTaskAccepted(TaskType.S2C);
		operationsBehavior.put(SVNOperationType.DIFF, diffBehavior);
	}
	
	
	/**
	 * Filter a request
	 * @param request to filter
	 * @param requestComponents extract from the request
	 * @param type of task
	 * @throws SVNServiceException if an error occurred
	 */
	public synchronized void  filter(String request, Map<Integer, RequestComponent> requestComponents, TaskType type) throws SVNServiceException {
		if (requestComponents == null) {
			logger.error("requestComponents cannot be null");
			throw new SVNTechnicalException("requestComponents cannot be null");
		}
		
		logger.debug("request to filter = " + request);
		
		/*
		 * Extract needed informations
		 */
		//URL
		FilterUtils.extractURL(request, 0, requestComponents);
		//commit operation
		FilterUtils.extractSVNOperation(request, 0, SVNOperationType.COMMIT, requestComponents);
		//update operation
		FilterUtils.extractSVNOperation(request, 0, SVNOperationType.UPDATE, requestComponents);
		//diff operation
		FilterUtils.extractSVNOperation(request, 0, SVNOperationType.DIFF, requestComponents);
		
		//SVNNode
		//add-dir resource
		FilterUtils.extractSVNResource(request, 0, SVNResourceType.ADD_DIR, requestComponents);
		//add-file
		FilterUtils.extractSVNResource(request, 0, SVNResourceType.ADD_FILE, requestComponents);
		//delete
		FilterUtils.extractSVNResourceDelete(request, 0, requestComponents);
		
		//check-path
		FilterUtils.extractSVNCheckPath(request, 0, this.pathsChecked);
		
		//Sort the components
		Set<Integer> indexes = requestComponents.keySet();
		List<Integer> listIndexes = new ArrayList<Integer>();
		listIndexes.addAll(indexes);
		Collections.sort(listIndexes);
		
		//Execute check with the factory
		for (Integer index : listIndexes) {
			RequestComponent requestComponent = requestComponents.get(index);
			requestComponent.execInFilter(this, type);
			
		}
	}
	
	/**
	 * Define the pIdRepository
	 * @param pIdRepository to define
	 */
	public void defineIdRepository(String pIdRepository) {
		logger.trace("defineIdRepository called...");
		logger.trace("pIdRepsoitory=" + pIdRepository);
		logger.trace("existing id repo=" + this.idRepository);

		if (!StringUtils.isEmpty(pIdRepository) && !pIdRepository.equals(this.idRepository)) {
			this.idRepository = pIdRepository;
			logger.debug("define new id repository " + this.idRepository);
		}
	}
	
	/**
	 * Define the SVN operation type of the request
	 * @param pOperation to define
	 */
	public void defineSVNOperation(SVNOperationType pOperation) {
		logger.trace("defineSVNOperation called...");
		if (pOperation == null) {
			logger.error("SVNOperationType cannot be null");
			throw new SVNTechnicalException("");
		}
		logger.debug("pOperation=" + pOperation.toString());
		
		if (this.svnOperation != null && !this.svnOperation.toString().equals(pOperation.toString())) {
			logger.debug("previous svnOperation=" + this.svnOperation.toString());
			logger.debug("Clear operations");
			//Clear resource
			resources.clear();
		}
		this.svnOperation = pOperation;
	}
	
	/**
	 * Check the resource with the factory
	 * @param pResource resource to check
	 * @param taskType type of the task
	 * @throws SVNServiceException  if the SVN operation is not define
	 */
	public void checkResource(SVNResource pResource, TaskType taskType) throws SVNServiceException {
		logger.trace("checkResource called...");
		if (pResource == null) {
			logger.error("SVNResource cannot be null");
			throw new SVNTechnicalException("SVNResource cannot be null");
		}
		
		if (logger.isTraceEnabled()) {
			logger.trace("pResource=" + pResource.toString());
			logger.trace("pathsChecked=" + this.pathsChecked.toString());
		}
		
		
		
		if (this.svnOperation == null) {
			logger.warn("svnOperation null, cannot execute " + pResource.toString());
			return;
		}
		if (this.idRepository == null) {
			logger.warn("idRepository null, cannot execute " + pResource.toString());
			throw new SVNServiceException("idRepository null, cannot execute " + pResource.toString());
		}
		
		logger.trace("svnOperation=" + svnOperation.toString());
		
		//Getting the appropriate type of task fot the svn operation
		SVNOperationBehavior behavior = operationsBehavior.get(this.svnOperation);
		
		if (behavior == null) {
			logger.error("behavior cannot be null");
			throw new SVNTechnicalException("SVN Operation" + this.svnOperation.toString() + "cannot be treated");
		}
		
		
		
		// Check if task type is accepted for this svn operation and if the resource is not already treated
		ExecutedResource resourceToExecutedResource = new ExecutedResource(pResource, this.idRepository);
		
		if (behavior.isTaskAccepted(taskType) && !this.resources.contains(resourceToExecutedResource)) {
			this.resources.add(resourceToExecutedResource);
			
			if (logger.isDebugEnabled()) {
				logger.debug("check " + pResource.toString());
				logger.debug("resources size=" +this.resources.size());
				logger.debug("---- BEGIN CALL FACTORY ----");
				logger.debug("resource=" + resourceToExecutedResource);
				logger.debug("taskType=" + taskType);
				logger.debug(this.toString());
				logger.debug("---- END CALL FACTORY ----");
			}
			
			behavior.execute(resourceToExecutedResource.getType(), resourceToExecutedResource.getPathParts());
		}
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("idRepository=");
		sb.append(this.idRepository);
		sb.append("\n");
		sb.append("svnOperation=");
		sb.append(this.svnOperation);
		sb.append("\n");
		sb.append("resources=");
		if (this.resources != null) {
			sb.append(this.resources.toString());
		}
		else {
			sb.append("null");
		}
		
		return sb.toString();
	}
	
	
	
	/**
	 * 
	 * Resource executed
	 *
	 */
	private class ExecutedResource {
		/**
		 * Type of the resource
		 */
		private SVNResourceType type;
		
		/**
		 * Path of the resource
		 */
		private List<String> pathParts;
		
		/**
		 * toString value
		 */
		private String toString;
		
		
		/**
		 * Constructor
		 * @param type of the resource
		 * @param path of the resource
		 */
		public ExecutedResource(SVNResource resource, String path) {
			this.type = resource.getType();
			
			// Check the depth
			int depthCheck = SVNProperties.getInstance().getDepthCheckRights();
			
			//Check in the checked path
			String pathToExec = FilterUtils.normalizePath(resource.getPath());
			for (String pathChecked : pathsChecked) {
				if (pathChecked.endsWith(pathToExec)) {
					pathToExec = pathChecked;
					break;
				}
			}
			
			List<String> completePath = FilterUtils.generateResourcePath(path, pathToExec);
			
			if (depthCheck == SVNProperties.DEPTH_CHECK_RIGHT_NO_LIMIT) {
				this.pathParts = completePath;
			}
			else if (completePath.size() <= (depthCheck + 1)) {
				this.pathParts = completePath;
			}
			else {
				//Filter part
				this.pathParts = completePath.subList(0, depthCheck);
				this.type = resource.getTypeForTruncatedPath();
			}
			
			//Construct toString
			StringBuffer sb = new StringBuffer();
			sb.append("type=[");
			sb.append(this.type.toString());
			sb.append("], path=[");
			for (String part : pathParts) {
				sb.append(part);
				sb.append("/");
			}
			sb.append("]");
			this.toString = sb.toString();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return this.toString().hashCode();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return this.toString;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ExecutedResource) {
				ExecutedResource resource2 = (ExecutedResource) obj;
				
				return (this.hashCode() == resource2.hashCode());
			}
			else {
				return false;
			}
		}

		/**
		 * @return the type
		 */
		public SVNResourceType getType() {
			return type;
		}

		/**
		 * @return the pathParts
		 */
		public List<String> getPathParts() {
			return this.pathParts;
		}
		
		
	}
}
