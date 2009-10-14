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
@XmlType(name = "SVNLogEntryPath", namespace = "http://org.qualipso.factory.ws/resource/svnlogentrypath", propOrder = { "copyPath","copyRevision","oriPath","type" })
@SuppressWarnings("serial")
public class SVNLogEntryPath extends FactoryResource {

	@Id
	private String id;
	private String type;
	private String oriPath;
	private String copyPath;
	private String copyRevision;

	private String path;

	public SVNLogEntryPath() {
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
				"SVNLogEntryPath", getId());
	}

	@Override
	@XmlTransient
	public String getResourceName() {
		return getId();
	}


	@XmlElement(name = "type", required = false)
	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	@XmlElement(name = "oriPath", required = false)
	public String getOriPath() {
		return oriPath;
	}


	public void setOriPath(String oriPath) {
		this.oriPath = oriPath;
	}

	@XmlElement(name = "copyPath", required = false)
	public String getCopyPath() {
		return copyPath;
	}


	public void setCopyPath(String copyPath) {
		this.copyPath = copyPath;
	}

	@XmlElement(name = "copyRevision", required = false)
	public String getCopyRevision() {
		return copyRevision;
	}


	public void setCopyRevision(String copyRevision) {
		this.copyRevision = copyRevision;
	}


}
