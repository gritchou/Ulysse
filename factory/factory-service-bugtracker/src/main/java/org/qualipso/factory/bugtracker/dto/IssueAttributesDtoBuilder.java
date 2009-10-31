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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.mantisbt.connect.model.IMCAttribute;
import org.qualipso.factory.bugtracker.utils.ConfDataDtoIdComparator;

/**
 * IssueAttributesDto builder
 *
 */
public class IssueAttributesDtoBuilder {

	/**
	 * Create a IssueAttributesDto
	 * @param priorities Bugtracker priorities
	 * @param resolutions Bugtracker resolutions
	 * @param severities Bugtracker severities
	 * @param status Bugtracker status
	 * @return IssueAttributesDto
	 */
	public static IssueAttributesDto create(Map<String, IMCAttribute> priorities,
			Map<String, IMCAttribute> resolutions,
			Map<String, IMCAttribute> severities,
			Map<String, IMCAttribute> status) {
		IssueAttributesDto dto = new IssueAttributesDto();
		dto.setPriorities(convertAndSortMapValues(priorities));
		dto.setResolutions(convertAndSortMapValues(resolutions));
		dto.setSeverities(convertAndSortMapValues(severities));
		dto.setStatus(convertAndSortMapValues(status));

		return dto;
	}
	
	 
	/**
	 * Convert and sort the map values
	 * @param map for converting
	 * @return sorted list
	 */
	private static List<ConfDataDto> convertAndSortMapValues(Map<String, IMCAttribute> map) {
		List<ConfDataDto> dtos = null;
		if (map != null) {
			dtos = new ArrayList<ConfDataDto>();
			
			//Convert
			for(IMCAttribute iMCAttribute : map.values()) {
				dtos.add(ConfDataDtoBuilder.create(iMCAttribute));
			}
			//Sort
			Collections.sort(dtos, new ConfDataDtoIdComparator());
		}
		return dtos;
	}
}
