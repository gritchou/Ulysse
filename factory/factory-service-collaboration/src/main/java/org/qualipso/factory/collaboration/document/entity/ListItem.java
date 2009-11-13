package org.qualipso.factory.collaboration.document.entity;

import java.io.Serializable;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;

@XmlType(name = ListItem.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ ListItem.RESOURCE_NAME, propOrder = { "id", "path", "name", "type" })
@SuppressWarnings("serial")
public class ListItem implements Serializable {
    public static final String RESOURCE_NAME = "list-item";
    private static final long serialVersionUID = 1L;
    private String id;
    private String path;
    private String name;
    private String type = "folder";

    public ListItem() {

    }

    public ListItem(String id, String name, String path, String type) {
	this.id = id;
	this.name = name;
	this.path = path;
	this.type = type;
    }

    @XmlAttribute(name = "id", required = true)
    @Transient
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    @XmlAttribute(name = "path", required = true)
    @Transient
    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    @Transient
    public String getName() {
	return name;
    }

    public void setName(String value) {
	this.name = value;
    }

    @Transient
    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("List item. Path " + this.path);
	sb.append("\nID " + this.id);
	sb.append("\nname " + this.name);
	sb.append("\ntype " + this.type);
	return sb.toString();
    }

}
