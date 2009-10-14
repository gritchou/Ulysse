package org.qualipso.factory.subversion.entity;

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
 * @author Emmanuel Rias (emmanuel.rias@bull.net)
 * @date 1 july 2009
 */
@Entity
@XmlType(name = "Acl", namespace = "http://org.qualipso.factory.ws/resource/acl/", propOrder = { "userId" , "role" })
@SuppressWarnings("serial")
public class Acl extends FactoryResource {

	@Id
	private String id;
    private String userId    = null;
    private String role        = null;

	private String path;

	public Acl() {
	}

	public Acl(String userId) {
		this.userId = userId;
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
				"Acl", getId());
	}

	@Override
	@XmlTransient
	public String getResourceName() {
		return getId();
	}

	@XmlElement(name = "userId", required = false)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@XmlElement(name = "role", required = false)
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}


}
