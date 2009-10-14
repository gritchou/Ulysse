package org.qualipso.factory.core.entity;

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
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 july 2009
 */
@Entity
@XmlRootElement
@XmlType(name = "Folder", namespace = "http://org.qualipso.factory.ws/resource/folder", propOrder =  {
    "name", "description"}
)
@SuppressWarnings("serial")
public class Folder extends FactoryResource {
	
	@Id
	private String id;
	private String name;
	private String description;
	private String path;
	
	public Folder() {
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
	
	@XmlElement(name = "name", required = true)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "description", required = true)
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	@XmlTransient
	public FactoryResourceIdentifier getFactoryResourceIdentifier() {
		return new FactoryResourceIdentifier("CoreService", "Folder", getId());
	}

	@Override
	@XmlTransient
	public String getResourceName() {
		return name;
	}

}