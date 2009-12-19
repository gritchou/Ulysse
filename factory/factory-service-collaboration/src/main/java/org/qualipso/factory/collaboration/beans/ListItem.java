package org.qualipso.factory.collaboration.beans;

import java.io.Serializable;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;

/**
 * The Class ListItem.
 */
@XmlType(name = ListItem.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ ListItem.RESOURCE_NAME, propOrder = { "id", "path", "name", "type" })
@SuppressWarnings("serial")
public class ListItem implements Serializable {
    
    /** The Constant RESOURCE_NAME. */
    public static final String RESOURCE_NAME = "list-item";
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The id. */
    private String id;
    
    /** The path. */
    private String path;
    
    /** The name. */
    private String name;
    
    /** The type. */
    private String type = "folder";

    /**
     * Instantiates a new list item.
     */
    public ListItem() {

    }

    /**
     * Instantiates a new list item.
     * 
     * @param id the id
     * @param name the name
     * @param path the path
     * @param type the type
     */
    public ListItem(String id, String name, String path, String type) {
	this.id = id;
	this.name = name;
	this.path = path;
	this.type = type;
    }

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
     * Gets the name.
     * 
     * @return the name
     */
    @Transient
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
     * Gets the type.
     * 
     * @return the type
     */
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("List item. Path " + this.path);
	sb.append("\nID " + this.id);
	sb.append("\nname " + this.name);
	sb.append("\ntype " + this.type);
	return sb.toString();
    }

}
