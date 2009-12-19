package org.qualipso.factory.collaboration.beans;

import java.io.Serializable;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;

/**
 * The Class AttachmentDetails.
 */
@XmlType(name = AttachmentDetails.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ AttachmentDetails.RESOURCE_NAME, propOrder = { "id","path", "name","resourceId","type" })
@SuppressWarnings("serial")
public class AttachmentDetails implements Serializable  {
    
    /**
     * Instantiates a new attachment details.
     */
    public AttachmentDetails(){
	
    }
    
    /** The Constant RESOURCE_NAME. */
    public static final String RESOURCE_NAME = "attachment-details";
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The id. */
    private String id;
    
    /** The path. */
    private String path;
    
    /** The resource id. */
    private String resourceId;
    
    /** The type. */
    private String type;//can be either document or forum
    
    /** The name. */
    private String name;
    
    /**
     * Gets the id.
     * 
     * @return the id
     */
    @XmlAttribute(name = "id", required = true)
    @Transient
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
    
    /**
     * Gets the path.
     * 
     * @return the path
     */
    @XmlAttribute(name = "path", required = true)
    @Transient
    public String getPath() {
        return path;
    }
    
    /**
     * Sets the path.
     * 
     * @param path the new path
     */
    public void setPath(String path) {
        this.path = path;
    }
    
    /**
     * Gets the resource id.
     * 
     * @return the resource id
     */
    @XmlAttribute(name = "resourceId", required = false)
    @Transient
    public String getResourceId() {
        return resourceId;
    }
    
    /**
     * Sets the resource id.
     * 
     * @param resourceId the new resource id
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
    
    /**
     * Gets the type.
     * 
     * @return the type
     */
    @XmlElement(name = "type", required = false)
    @Transient
    public String getType() {
        return type;
    }
    
    /**
     * Sets the type.
     * 
     * @param type the new type
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Gets the name.
     * 
     * @return the name
     */
    @XmlElement(name = "name", required = true)
    @Transient
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    

    
    

}
