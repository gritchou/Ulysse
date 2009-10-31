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

import java.io.Serializable;

/**
 * ProjectDto
 *
 */
public class ProjectDto implements Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7754235859131520623L;

	/**
	 * Project id mantis
	 */
	private long id;
	

	/**
	 * Project name
	 */
	private String name;
	
	/**
	 * Project description
	 */
	private String description;
	
		
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

		
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id=");
		sb.append(this.id);
		sb.append("; name=");
		sb.append(this.name);
		sb.append("; description=");
		sb.append(this.description);
		return sb.toString();
	}
}
