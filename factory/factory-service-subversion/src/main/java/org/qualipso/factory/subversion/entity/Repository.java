package org.qualipso.factory.subversion.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;

/**
 * @author Emmanuel Rias (emmanuel.rias@bull.net)
 * @date 1 july 2009
 */
@Entity
@XmlRootElement
@XmlType(name = "Repository", namespace = "http://org.qualipso.factory.ws/resource/repository/", propOrder = { "name" ,"summary" })
@SuppressWarnings("serial")
public class Repository extends FactoryResource {

	@Id
	private String id;
	private String name;
	private String summary;

	private String path;

	public Repository() {
	}

	public Repository(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "id", required = true)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(name = "path", required = true)
	@Transient
	@Override
	public String getResourcePath() {
		return path;
	}

	public void setResourcePath(String path) {
		this.path = path;
	}

	@Override
	@XmlTransient
	public FactoryResourceIdentifier getFactoryResourceIdentifier() {
		return new FactoryResourceIdentifier("SubversionService",
				"Repository", getId());
	}

	@Override
	@XmlTransient
	public String getResourceName() {
		return getId();
	}

	@XmlElement(name = "summary", required = false)
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@XmlElement(name = "name", required = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
