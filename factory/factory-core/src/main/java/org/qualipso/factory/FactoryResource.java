/*
 *
 * Qualipso Factory
 * Copyright (C) 2006-2010 INRIA
 * http://www.inria.fr - molli@loria.fr
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of LGPL. See licenses details in LGPL.txt
 *
 * Initial authors :
 *
 * Jérôme Blanchard / INRIA
 * Pascal Molli / Nancy Université
 * Gérald Oster / Nancy Université
 *
 */
package org.qualipso.factory;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.qualipso.factory.core.entity.File;
import org.qualipso.factory.core.entity.Folder;
import org.qualipso.factory.core.entity.Link;
import org.qualipso.factory.membership.entity.Group;
import org.qualipso.factory.membership.entity.Profile;

/**
 * This is the abstract class for all factory resources. All resources extends this class.<br/>
 * <br/>
 * A resource in the factory is the smallest grain for a piece of data. This piece of data can be handled 
 * in a common way by the services of the factory (Security, Indexing, Notification, etc...). <br/>
 * <br/>
 * A resource have some global properties : 
 * <ul>
 * <li>a RESOURCE_NAME to define the type of all instances of this resource (folder, message, bug, repository, etc...), 
 * <li>a unique path to bind the resource instance in the factory naming scheme,
 * <li>a unique identifier @see FactoryResourceIdentifier to hold external identifier,
 * <li>a friendly name for display.
 * </ul>
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@SuppressWarnings("serial")
@XmlSeeAlso(value={File.class, Folder.class, Link.class, Group.class, Profile.class})
public abstract class FactoryResource implements Serializable {
	
	/**
	 * The name for this type of resource
	 */
	public static final String RESOURCE_NAME = "resource";
	
	 
	/**
	 * @return A String representation of the resource path in the factory naming scheme.
	 */
	abstract public String getResourcePath();
	
	 /**
	 * @return This resource FactoryIdentifier.
	 */
	abstract public FactoryResourceIdentifier getFactoryResourceIdentifier();

	/**
	 * @return This resource friendly name.
	 */
	abstract public String getResourceName();
}
