package org.qualipso.factory.git.entity;

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
import org.qualipso.factory.git.GITService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 21 september 2009
 */
@Entity
@XmlType(name = GITRepository.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE + GITRepository.RESOURCE_NAME, propOrder =  {
    "name", "description"}
)
@SuppressWarnings("serial")
public class GITRepository extends FactoryResource {
	
	public static final String RESOURCE_NAME = "git-repository";
	
	@Id
	private String id;
	private String name;
	private String description;
	private String folder;
	private String path;
	
	public GITRepository() {
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
		return new FactoryResourceIdentifier(GITService.SERVICE_NAME, GITRepository.RESOURCE_NAME, getId());
	}

	@Override
	@XmlTransient
	public String getResourceName() {
		return getName();
	}
}
