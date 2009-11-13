package org.qualipso.factory.collaboration.document.entity;

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
import org.qualipso.factory.collaboration.document.DocumentService;

@Entity
@XmlType(name = CollaborationFolder.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ CollaborationFolder.RESOURCE_NAME, propOrder = { "name",
	"parentFolderId", "description", "date", "content" })
@SuppressWarnings("serial")
public class CollaborationFolder extends FactoryResource {
    public static final String RESOURCE_NAME = "collaboration-folder";
    private static final long serialVersionUID = 677883093059225890L;

    @Id
    private String id;
    // we persist id,name,path and parent id
    private String path;
    private String name;
    private String parentFolderId = "";
    // We don't persist description,date and content
    private String description;
    private String date = "";
    private ListItem[] content;

    public CollaborationFolder() {
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

    public void setName(String value) {
	this.name = value;
    }

    @XmlElement(name = "parentFolderId", required = false)
    public String getParentFolderId() {
	return parentFolderId;
    }

    public void setParentFolderId(String parentFolderId) {
	this.parentFolderId = parentFolderId;
    }

    @Override
    @XmlTransient
    public FactoryResourceIdentifier getFactoryResourceIdentifier() {
	return new FactoryResourceIdentifier(DocumentService.SERVICE_NAME,
		CollaborationFolder.RESOURCE_NAME, getId());
    }

    @Override
    @XmlTransient
    public String getResourceName() {
	return getName();
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("Folder. Path " + getResourcePath());
	sb.append("\nID " + this.id);
	sb.append("\nname " + this.name);
	sb.append("\ndate " + this.date);
	sb.append("\nparentFolderID " + this.parentFolderId);
	sb.append("\nsdescription " + this.description);
	if (this.content != null) {
	    sb.append("\nsChildren " + this.content.length);
	}

	return sb.toString();
    }

    @Transient
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Transient
    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    @Transient
    public ListItem[] getContent() {
	return content;
    }

    public void setContent(ListItem[] content) {
	this.content = content;
    }

}
