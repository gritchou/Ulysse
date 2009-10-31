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

import org.qualipso.factory.FactoryService;
import org.qualipso.factory.project.entity.Project;

@Remote
@WebService(name = "ProjectService", targetNamespace = "http://org.qualipso.factory.ws/service/project")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ProjectService extends FactoryService{
	
	@WebMethod
	public void createProject(String path, String name, String summary, String licence) throws ProjectException;

	@WebMethod
	public void updateProject(String path, String name, String status, String summary,
			String licence) throws ProjectException;

	@WebMethod
	public void updateTagsProject(String path, String[] os, String[] topics,
			String[] language, String[] programming_language,
			String[] intended_audience) throws ProjectException;

	@WebMethod
	@WebResult(name = "project")
	public Project getProject(String path) throws ProjectException;

	@WebMethod
	public void deleteProject(String path)
			throws ProjectException;
}
