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
package org.qualipso.factory.bugtracker.dto;

import org.mantisbt.connect.AccessLevel;
import org.mantisbt.connect.model.IProject;
import org.mantisbt.connect.model.Project;

/**
 * ProjetDto builder
 *
 */
public class ProjectDtoBuilder {

	/**
	 * Create a IProject with ProjectDto
	 * @param dto ProjectDto
	 * @return IProject
	 */
	public static IProject create(ProjectDto dto) {
		IProject project = null;
		if (dto != null) {
			project = new Project();
			project.setAccessLevelMin(AccessLevel.ANYBODY);
			project.setDesription(dto.getDescription());
			project.setName(dto.getName());
			project.setEnabled(true);
			project.setPrivate(false);
		}
		return project;
	}
	
	/**
	 * Create a ProjectDto with IProject
	 * @param iproject IProject
	 * @return ProjectDto
	 */
	public static ProjectDto create(IProject iproject) {
		ProjectDto dto = null;
		if (iproject != null) {
			dto = new ProjectDto();
			dto.setId(iproject.getId());
			dto.setDescription(iproject.getDescription());
			dto.setName(iproject.getName());
		}
		return dto;
	}
	
	/**
	 * Create a default project
	 * @param name of the project
	 * @return ProjectDto
	 */
	public static ProjectDto createDefault(String name, String description) {
		ProjectDto dto = new ProjectDto();
		dto.setDescription(description);
		dto.setName(name);
		return dto;
	}
}
