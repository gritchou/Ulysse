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
 * UserDto
 *
 */
public class ProfileDto implements Serializable{


	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7034531019053672204L;


	/**
	 * id of the profile
	 */
	private String id;
	

	/**
	 * Project name
	 */
	private String fullname;
	
	
	

		
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}





	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}





	/**
	 * @return the fullname
	 */
	public String getFullname() {
		return fullname;
	}





	/**
	 * @param fullname the fullname to set
	 */
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}





	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id=");
		sb.append(this.id);
		sb.append("; fullname=");
		sb.append(this.fullname);
		return sb.toString();
	}
}
