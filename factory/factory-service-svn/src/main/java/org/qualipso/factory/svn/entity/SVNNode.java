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
package org.qualipso.factory.svn.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;

@Entity
@XmlType(name = SVNNode.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE + SVNNode.RESOURCE_NAME, propOrder =  {
	    "resourceName", "resourcePath"}
	)
@SuppressWarnings("serial")
public class SVNNode extends FactoryResource {

	/**
	 * RESOURCE_NAME
	 */
	public static final String RESOURCE_NAME = "svn-resource";
	
	@Id
	private String id;
	
	/**
	 * @return the id
	 */
	@XmlAttribute(name = "id", required = true)
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
	 * resourcePath
	 */
	private String resourcePath;
	
	/**
	 * FactoryResourceIdentifier
	 */
	private FactoryResourceIdentifier factoryResourceIdentifier;

	
	/**
	 * @param resourcePath the resourcePath to set
	 */
	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}



	/**
	 * Constructor
	 */
	public SVNNode() {
	}


	
	/**
	 * @param factoryResourceIdentifier the factoryResourceIdentifier to set
	 */
	public void setFactoryResourceIdentifier(
			FactoryResourceIdentifier factoryResourceIdentifier) {
		this.factoryResourceIdentifier = factoryResourceIdentifier;
	}


	/* (non-Javadoc)
	 * @see org.qualipso.factory.FactoryResource#getFactoryResourceIdentifier()
	 */
	@Override
	public FactoryResourceIdentifier getFactoryResourceIdentifier() {
		return factoryResourceIdentifier;
	}

	/* (non-Javadoc)
	 * @see org.qualipso.factory.FactoryResource#getResourceName()
	 */
	@XmlAttribute(name = "resourceName", required = true)
	@Override
	public String getResourceName() {
		return RESOURCE_NAME;
	}


	/* (non-Javadoc)
	 * @see org.qualipso.factory.FactoryResource#getResourcePath()
	 */
	@XmlAttribute(name = "resourcePath", required = true)
	@Override
	public String getResourcePath() {
		return resourcePath;
	}
}
