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
import org.qualipso.factory.collaboration.beans.ListItem;
import org.qualipso.factory.collaboration.document.DocumentService;

/**
 * The Class CollaborationFolder.
 */
@Entity
@XmlType(name = CollaborationFolder.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ CollaborationFolder.RESOURCE_NAME, propOrder = { "name",
	"parentFolderId", "description", "date", "content" })
@SuppressWarnings("serial")
public class CollaborationFolder extends FactoryResource {
    
    /** The Constant RESOURCE_NAME. */
    public static final String RESOURCE_NAME = "collaboration-folder";
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 677883093059225890L;

    /** The id. */
    @Id
    private String id;
    // we persist id,name,path and parent id
    /** The path. */
    private String path;
    
    /** The name. */
    private String name;
    
    /** The parent folder id. */
    private String parentFolderId = "";
    // We don't persist description,date and content
    /** The description. */
    private String description;
    
    /** The date. */
    private String date = "";
    
    /** The content. */
    private ListItem[] content;

    /**
     * Instantiates a new collaboration folder.
     */
    public CollaborationFolder() {
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    @XmlAttribute(name = "id", required = true)
    public String getId() {
	return id;
    }

    /**
     * Sets the id.
     * 
     * @param id the new id
     */
    public void setId(String id) {
	this.id = id;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.FactoryResource#getResourcePath()
     */
    @XmlAttribute(name = "path", required = true)
    @Transient
    @Override
    public String getResourcePath() {
	return path;
    }

    /**
     * Sets the resource path.
     * 
     * @param path the new resource path
     */
    public void setResourcePath(String path) {
	this.path = path;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    @XmlElement(name = "name", required = true)
    public String getName() {
	return name;
    }

    /**
     * Sets the name.
     * 
     * @param value the new name
     */
    public void setName(String value) {
	this.name = value;
    }

    /**
     * Gets the parent folder id.
     * 
     * @return the parent folder id
     */
    @XmlElement(name = "parentFolderId", required = false)
    public String getParentFolderId() {
	return parentFolderId;
    }

    /**
     * Sets the parent folder id.
     * 
     * @param parentFolderId the new parent folder id
     */
    public void setParentFolderId(String parentFolderId) {
	this.parentFolderId = parentFolderId;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.FactoryResource#getFactoryResourceIdentifier()
     */
    @Override
    @XmlTransient
    public FactoryResourceIdentifier getFactoryResourceIdentifier() {
	return new FactoryResourceIdentifier(DocumentService.SERVICE_NAME,
		CollaborationFolder.RESOURCE_NAME, getId());
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.FactoryResource#getResourceName()
     */
    @Override
    @XmlTransient
    public String getResourceName() {
	return getName();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("Folder. Path " + getResourcePath());
	sb.append("\nID " + this.id);
	sb.append("\nname " + this.name);
	sb.append("\ndate " + this.date);
	sb.append("\nparentFolderId " + this.parentFolderId);
	sb.append("\nsdescription " + this.description);
	if (this.content != null) {
	    sb.append("\nsChildren " + this.content.length);
	}

	return sb.toString();
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    @Transient
    public String getDescription() {
	return description;
    }

    /**
     * Sets the description.
     * 
     * @param description the new description
     */
    public void setDescription(String description) {
	this.description = description;
    }

    /**
     * Gets the date.
     * 
     * @return the date
     */
    @Transient
    public String getDate() {
	return date;
    }

    /**
     * Sets the date.
     * 
     * @param date the new date
     */
    public void setDate(String date) {
	this.date = date;
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    @Transient
    public ListItem[] getContent() {
	return content;
    }

    /**
     * Sets the content.
     * 
     * @param content the new content
     */
    public void setContent(ListItem[] content) {
	this.content = content;
    }

}
