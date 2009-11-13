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

import org.qualipso.factory.membership.entity.Profile;

/**
 * ProfileDto builder
 *
 */
public class ProfileDtoBuilder {

	
	/**
	 * Create a ProjectDto with IProject
	 * @param iproject IProject
	 * @return ProjectDto
	 */
	public static ProfileDto create(Profile profile) {
		ProfileDto dto = null;
		if (profile != null) {
			dto = new ProfileDto();
			dto.setId(profile.getId());
			dto.setFullname(profile.getFullname());
		}
		return dto;
	}

}
