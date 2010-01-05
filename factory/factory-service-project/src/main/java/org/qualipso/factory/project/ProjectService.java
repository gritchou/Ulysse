package org.qualipso.factory.project;

/**
 * 
 * Copyright (C) 2006-2010 THALES
 * http://www.thalesgroup.fr - thierry.deroff@thalesgroup.com
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the license LGPL.
 *
 * Initial author :
 * Thierry Deroff from Thales Service, THERESIS Competence Center Open Source Software
 *
 */

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
import org.qualipso.factory.project.entity.Project;
import org.qualipso.factory.security.pep.AccessDeniedException;

@Remote
@WebService(name = ProjectService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + ProjectService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ProjectService extends FactoryService {
	
	public static final String SERVICE_NAME = "project";
	public static final String[] RESOURCE_TYPE_LIST = new String[] { Project.RESOURCE_NAME };
	
	@WebMethod
	public void createProject(String path, String name, String summary, String licence) throws ProjectServiceException, AccessDeniedException, InvalidPathException, PathAlreadyBoundException;

	@WebMethod
	public void updateProject(String path, String name, String status, String summary, String licence) throws ProjectServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

	@WebMethod
	public void updateTagsProject(String path, String[] os, String[] topics, String[] language, String[] programming_language, String[] intended_audience) throws ProjectServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

	@WebMethod
	@WebResult(name = "project")
	public Project readProject(String path) throws ProjectServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

	@WebMethod
	public void deleteProject(String path) throws ProjectServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException, PathNotEmptyException;
}
