package org.qualipso.factory.core.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 july 2009
 */
@Entity
@XmlType(name = "Link", namespace = "http://org.qualipso.factory.ws/resource/link", propOrder =  {
    "link"}
)
@SuppressWarnings("serial")
public class Link extends FactoryResource {
	
	@Id
	private String id;
	private String link;
	private String path;
	
	public Link() {
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
	
	@XmlElement(name = "link", required = true)
	public String getLink() {
		return link;
	}
	
	public void setLink(String path) {
		this.link = path;
	}

	@Override
	@XmlTransient
	public FactoryResourceIdentifier getFactoryResourceIdentifier() {
		return new FactoryResourceIdentifier("CoreService", "Link", getId());
	}

	@Override
	@XmlTransient
	public String getResourceName() {
		// TODO maybe get the name of the linked resource...
		return link;
	}

}
