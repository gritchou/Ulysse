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
package org.qualipso.factory.bugtracker.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;

/**
 * Entity for an issue in the factory
 *
 */
@Entity
@XmlType(name = Issue.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE + Issue.RESOURCE_NAME, propOrder =  {
		"id", "reporter", "assigned"}
)
@SuppressWarnings("serial")
public class Issue extends FactoryResource {

	/**
	 * Resource name
	 */
	public static final String RESOURCE_NAME = "issue";
	
	/**
	 * id of issue
	 */
	@Id
	private String id;
	
	/**
	 * Id of the reporter of the issue
	 */
	private String reporter;
	
	/**
	 * Id of the Assigned user
	 */
	private String assigned;
	
	
	private String path;


	/**
	 * Constructor
	 */
	public Issue() {
		
	}
	
	
	
	/**
	 * Constructor
	 * @param id of the Issue
	 * @param reporter of the bug
	 * @param assigned of the bug (optional)
	 */
	public Issue(String id, String reporter,
			String assigned) {
		super();
		this.id = id;
		this.reporter = reporter;
		this.assigned = assigned;
	}




	/**
	 * getId
	 * @return the id
	 */
	@XmlAttribute(name = "id", required = true)
	public String getId() {
		return id;
	}



	/**
	 * setId
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * getReporter
	 * @return the reporter
	 */
	@XmlElement(name = "reporter", required = true)
	public String getReporter() {
		return reporter;
	}



	/**
	 * setReporter
	 * @param reporter the reporter to set
	 */
	public void setReporter(String reporter) {
		this.reporter = reporter;
	}



	/**
	 * getAssigned
	 * @return the assigned
	 */
	@XmlElement(name = "assigned", required = false)
	public String getAssigned() {
		return assigned;
	}



	/**
	 * setAssigned
	 * @param assigned the assigned to set
	 */
	public void setAssigned(String assigned) {
		this.assigned = assigned;
	}


	/* (non-Javadoc)
	 * @see org.qualipso.factory.FactoryResource#getFactoryResourceIdentifier()
	 */
	@Override
	public FactoryResourceIdentifier getFactoryResourceIdentifier() {
		return new FactoryResourceIdentifier("bugtracker", RESOURCE_NAME, getId());
	}

	/* (non-Javadoc)
	 * @see org.qualipso.factory.FactoryResource#getResourceName()
	 */
	@Override
	public String getResourceName() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see org.qualipso.factory.FactoryResource#getResourcePath()
	 */
	@Override
	public String getResourcePath() {
		return this.path;
	}
	
	/**
	 * Set the resource path
	 * @param path of the issue
	 */
	public void setResourcePath(String path) {
		this.path = path;
	}

}
