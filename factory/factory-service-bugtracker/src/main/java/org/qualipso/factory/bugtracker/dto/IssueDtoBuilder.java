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

import java.util.Map;

import org.mantisbt.connect.model.IIssue;
import org.mantisbt.connect.model.IMCAttribute;
import org.mantisbt.connect.model.Issue;
import org.mantisbt.connect.model.MCAttribute;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.bugtracker.utils.Utils;

/**
 * IssueDto builder
 *
 */
public class IssueDtoBuilder {

	/**
	 * Create a IssueDto with IIssue
	 * @param issue IIssue
	 * @param projectPath project path factory
	 * @return IssueDto
	 * @throws InvalidPathException if path is not valid
	 */
	public static IssueDto create(IIssue issue, String projectPath) throws InvalidPathException {
		IssueDto dto = null;
		
		if (issue != null) {
			dto = new IssueDto();
			//Transformation of the id bugtracker in path factory
			String path = Utils.generatePathIssueFactory(projectPath, issue.getId());
			dto.setPath(path);
			if (issue.getHandler() != null) {
				dto.setAssignee(issue.getHandler().getName());
			}
			
			if (issue.getReporter() != null) {
				dto.setReporter(issue.getReporter().getName());
			}
			dto.setPriority(ConfDataDtoBuilder.create(issue.getPriority()));
			dto.setResolution(ConfDataDtoBuilder.create(issue.getResolution()));
			dto.setSeverity(ConfDataDtoBuilder.create(issue.getSeverity()));
			dto.setStatus(ConfDataDtoBuilder.create(issue.getStatus()));
			dto.setSummary(issue.getSummary());
			dto.setDescription(issue.getDescription());
			
			if (issue.getDateLastUpdated() != null) {
				dto.setDateLastUpdate(issue.getDateLastUpdated().getTime());
			}
			
		}
		return dto;
	}
	
	
	/**
	 * Create a IssueDto with IIssue
	 * @param dto IssueDto
	 * @param projectIdBugTracker project Id
	 * @param priorities Mantis priorities
	 * @param resolutions Mantis resolutions
	 * @param severities Mantis severities
	 * @param status Mantis status
	 * @return IIssue
	 */
	public static IIssue create(IssueDto dto, 
			long projectIdBugTracker,
			Map<String, IMCAttribute> priorities,
			Map<String, IMCAttribute> resolutions,
			Map<String, IMCAttribute> severities,
			Map<String, IMCAttribute> status) {
		
		IIssue issue = null;
		
		if (dto != null) {
			issue = new Issue();
			
			if (dto.getPriority() != null) {
				issue.setPriority(priorities.get(dto.getPriority().getId()));
			}
			
			if (dto.getResolution() != null) {
				issue.setResolution(resolutions.get(dto.getResolution().getId()));
			}
			
			if (dto.getSeverity() != null) {
				issue.setSeverity(severities.get(dto.getSeverity().getId()));
			}
			
			if (dto.getStatus() != null) {
				issue.setStatus(status.get(dto.getStatus().getId()));
			}
			
			issue.setSummary(dto.getSummary());
			issue.setDescription(dto.getDescription());
			
			IMCAttribute project = new MCAttribute(projectIdBugTracker, "");
			issue.setProject(project);
			
		}
		return issue;
	}
	
	/**
	 * Create a IssueDto with IIssue
	 * @param dto IssueDto
	 * @param issue IIssue
	 * @return IssueDto
	 */
	
	/**
	 * Update an IIssue with a IssueDto
	 * @param dto IssueDto
	 * @param iIssue IIssue
	 * @param priorities Mantis priorities
	 * @param resolutions Mantis resolutions
	 * @param severities Mantis severities
	 * @param status Mantis status
	 */
	public static void update(IssueDto dto, 
			IIssue iIssue,
			Map<String, IMCAttribute> priorities,
			Map<String, IMCAttribute> resolutions,
			Map<String, IMCAttribute> severities,
			Map<String, IMCAttribute> status) {
		
		if (dto != null && iIssue != null) {
			if (dto.getPriority() != null) {
				iIssue.setPriority(priorities.get(dto.getPriority().getId()));
			}
			
			//issue.setReporter(reporter);
			if (dto.getResolution() != null) {
				iIssue.setResolution(resolutions.get(dto.getResolution().getId()));
			}
			
			if (dto.getSeverity() != null) {
				iIssue.setSeverity(severities.get(dto.getSeverity().getId()));
			}
			
			if (dto.getStatus() != null) {
				iIssue.setStatus(status.get(dto.getStatus().getId()));
			}
			iIssue.setSummary(dto.getSummary());
			iIssue.setDescription(dto.getDescription());
		}
	}
	
	/**
	 * Create a IssueDto[] with IIssue[]
	 * @param issues IIssue[]
	 * @param projectPath project path factory
	 * @return IssueDto[]
	 * @throws InvalidPathException if path is not valid
	 */
	public static IssueDto[] create(IIssue[] issues, String projectPath) throws InvalidPathException {
		IssueDto[] dtos = new IssueDto[0];
		
		if (issues != null) {
			dtos = new IssueDto[issues.length];
			int i = 0;
			for (IIssue issue : issues) {
				dtos[i] = create(issue, projectPath);
				i++;
			}
		}
		return dtos;
	}
}
