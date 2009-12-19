package org.qualipso.factory.svn.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.svn.utils.SVNConstants;

/**
 * @author Gerald Oster (oster@loria.fr)
 * @date 9 October 2009
 */
@Entity
@XmlType(name = SVNRepository.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE + SVNRepository.RESOURCE_NAME, propOrder =  {
    "name", "description"}
)
@SuppressWarnings("serial")
public class SVNRepository extends FactoryResource {

	public static final String RESOURCE_NAME = "svn-repository";
	
	@Id
	private String id;
	private String name;
	private String description;
	private String folder;
	private String path;
	
	public SVNRepository() {
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
	
	public void setResourcePath(String resourcePath) {
		this.path = resourcePath;
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

	@XmlTransient
	public String getFolder() {
		return folder;
	}
	
	public void setFolder(String folder) {
		this.folder = folder;
	}

	@Override
	@XmlTransient
	public FactoryResourceIdentifier getFactoryResourceIdentifier() {
		return new FactoryResourceIdentifier(SVNConstants.SVN_SERVICE_NAME, RESOURCE_NAME, getId());
	}

	@Override
	@XmlTransient
	public String getResourceName() {
		return getName();
	}
}
