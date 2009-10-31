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
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@SuppressWarnings("serial")
@XmlSeeAlso(value={File.class, Folder.class, Link.class, Group.class, Profile.class})
public abstract class FactoryResource implements Serializable {
	
	abstract public String getResourcePath();
	
	abstract public FactoryResourceIdentifier getFactoryResourceIdentifier();

	abstract public String getResourceName();
}
