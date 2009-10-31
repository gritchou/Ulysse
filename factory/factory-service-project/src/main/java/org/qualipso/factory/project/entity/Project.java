package org.qualipso.factory.project.entity;

/**
 * 
 * Copyright (C) 2006-2010 THALES
 * http://www.thalesgroup.fr - thierry.deroff@thalesgroup.com
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the license LGPL.
 *
 * Initial author :
 * Thierry Deroff from Thales Service, THERESIS Competence Center Open Source Software
 *
 */

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;

@Entity
@XmlType(name = "Project", namespace = "http://org.qualipso.factory.ws/resource/project", propOrder =  {
    "name", "licence", "summary", "os", "spoken_language", "topics", "intended_audience", "dev_status"}
)
@SuppressWarnings("serial")
public class Project extends FactoryResource {
	
	@Id
	private String id;
	private String path;
	private String name;
	private String licence;
	private String summary;
	private String[] os;
	private String[] spoken_language;
	private String[] programming_language;
	private String[] topics;
	private String[] intended_audience;
	private String dev_status;
	
	public Project(){	
	}
	
	@Override
	@XmlTransient
	public FactoryResourceIdentifier getFactoryResourceIdentifier() {
		return new FactoryResourceIdentifier("ProjectService", "Project", getId());
	}

	@Override
	@XmlTransient
	public String getResourceName() {
		return name;
	}

	@Override
	@XmlAttribute(name = "path", required = true)
	@Transient
	public String getResourcePath() {
		return path;
	}

	@XmlAttribute(name = "id", required = true)
	public String getId() {
		return id;
	}

	@XmlElement(name = "name", required = true)
	public String getName() {
		return name;
	}

	@XmlAttribute(name = "licence", required = true)
	public String getLicence() {
		return licence;
	}

	@XmlAttribute(name = "summary", required = true)
	public String getSummary() {
		return summary;
	}


	@XmlAttribute(name = "os", required = false)
	public String[] getOs() {
		return os;
	}

	@XmlAttribute(name = "spoken_language", required = false)
	public String[] getSpoken_language() {
		return spoken_language;
	}

	@XmlAttribute(name = "programming_language", required = false)
	public String[] getProgramming_language() {
		return programming_language;
	}

	@XmlAttribute(name = "topics", required = false)
	public String[] getTopics() {
		return topics;
	}

	@XmlAttribute(name = "intended_audience", required = false)
	public String[] getIntended_audience() {
		return intended_audience;
	}

	@XmlAttribute(name = "dev_status", required = false)
	public String getDev_status() {
		return dev_status;
	}


	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setResourcePath(String path) {
		this.path = path;
	}

	public void setLicence(String licence) {
		this.licence = licence;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setOs(String[] os) {
		this.os = os;
	}

	public void setSpoken_language(String[] spoken_language) {
		this.spoken_language = spoken_language;
	}

	public void setProgramming_language(String[] programming_language) {
		this.programming_language = programming_language;
	}

	public void setTopics(String[] topics) {
		this.topics = topics;
	}

	public void setIntended_audience(String[] intended_audience) {
		this.intended_audience = intended_audience;
	}

	public void setDev_status(String dev_status) {
		this.dev_status = dev_status;
	}


}
