package org.qualipso.factory.collaboration.beans;

import java.io.Serializable;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;

/**
 * The Class CalendarListItem.
 */
@XmlType(name = ListItem.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ ListItem.RESOURCE_NAME, propOrder = { "path", "name", "type","date" })
@SuppressWarnings("serial")
public class CalendarListItem implements Serializable {
    
    /** The Constant RESOURCE_NAME. */
    public static final String RESOURCE_NAME = "calendar-list-item";
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The path. */
    private String path;
    
    /** The name. */
    private String name;
    
    /** The type. */
    private String type = "event";
    
    /** The date. */
    private String date;

    /**
     * Instantiates a new calendar list item.
     */
    public CalendarListItem() {

    }

    /**
     * Instantiates a new calendar list item.
     * 
     * @param path the path
     * @param name the name
     * @param date the date
     */
    public CalendarListItem(String path,String name, String date) {
	this.name = name;
	this.path = path;
	this.date = date;
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("List item. Path " + this.path);
	sb.append("\nname " + this.name);
	sb.append("\ntype " + this.type);
	sb.append("\ndate " + this.date);
	return sb.toString();
    }

}
