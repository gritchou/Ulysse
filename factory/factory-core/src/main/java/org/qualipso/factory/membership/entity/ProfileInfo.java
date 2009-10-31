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
package org.qualipso.factory.membership.entity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 8 june 2009
 */

@XmlType(name = "ProfileInfo", namespace = "http://org.qualipso.factory.ws/resource/profile-info", propOrder =  {
	"name", "value"}
)
@SuppressWarnings("serial")
public class ProfileInfo implements Serializable {
	
	private String name;
	private String value;
	
	public ProfileInfo() {
	}
	
	@XmlElement(name="name", required=true)
    public String getName() {
    	return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    @XmlElement(name="value", required=true)
    public String getValue() {
    	return value;
    }
    
    public void setValue (String value) {
    	this.value = value;
    }
    
}
