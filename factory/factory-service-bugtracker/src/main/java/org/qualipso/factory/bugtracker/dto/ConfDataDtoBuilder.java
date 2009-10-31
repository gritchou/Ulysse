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

import org.mantisbt.connect.model.IMCAttribute;

/**
 * ConfDataDto builder
 *
 */
public class ConfDataDtoBuilder {

	/**
	 * Create a ConfDataDto with IMCAttribute
	 * @param attribute IMCAttribute
	 * @return ConfDataDto
	 */
	public static ConfDataDto create(IMCAttribute attribute) {
		ConfDataDto dto = null;
		
		if (attribute != null) {
			dto = new ConfDataDto();
			dto.setId(String.valueOf(attribute.getId()));
			dto.setName(attribute.getName());
		}
		return dto;
	}
	
}
