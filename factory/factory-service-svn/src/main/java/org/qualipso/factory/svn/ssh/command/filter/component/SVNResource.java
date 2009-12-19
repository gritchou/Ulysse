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
package org.qualipso.factory.svn.ssh.command.filter.component;

/**
 * 
 * SVN resource
 *
 */
public class SVNResource {

	/**
	 * Type of the resource
	 */
	private SVNResourceType type;
	
	/**
	 * Path of the resource
	 */
	private String path;
	
	
	/**
	 * Constructor
	 * @param type of the resource
	 * @param path of the resource
	 */
	public SVNResource(SVNResourceType type, String path) {
		super();
		this.type = type;
		this.path = path;
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
		return "[" + this.type.toString() + ", " + this.path + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SVNResource) {
			SVNResource resource2 = (SVNResource) obj;
			
			return (this.type.toString().equals(resource2.type.toString()) && this.path.equals(resource2.path));
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
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	
	/**
	 * Return the SVNResourceType for a truncated path
	 * @return CHECK_DELETE_ENTRY if SVNResourceType=DELETE_ENTRY, else original SVNResourceType
	 */
	public SVNResourceType getTypeForTruncatedPath() {
		if (SVNResourceType.DELETE_ENTRY.equals(this.type)) {
			return SVNResourceType.CHECK_DELETE_ENTRY;
		}
		return this.type;
	}
	
}
