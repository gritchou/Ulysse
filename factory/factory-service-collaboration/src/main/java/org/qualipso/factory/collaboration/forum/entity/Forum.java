package org.qualipso.factory.collaboration.forum.entity;

import java.util.HashMap;

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
import org.qualipso.factory.collaboration.forum.ForumService;
import org.qualipso.factory.collaboration.utils.CollaborationUtils;

@Entity
@XmlType(name = Forum.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ Forum.RESOURCE_NAME, propOrder = { "name", "date", "status",
	"messages" })
@SuppressWarnings("serial")
public class Forum extends FactoryResource {
    public static final String RESOURCE_NAME = "forum";
    private static final long serialVersionUID = 4023667551663471511L;

    @Id
    private String id;
    private String path;
    private String name;
    private String status = CollaborationUtils.FORUM_STATUS_ACTIVE;
    // We don't perist the date and messages
    private String date;
    private HashMap<String, ThreadMessage> messages;

    public Forum() {

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

    @XmlElement(name = "status", required = true)
    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    @Override
    @XmlTransient
    public FactoryResourceIdentifier getFactoryResourceIdentifier() {
	return new FactoryResourceIdentifier(ForumService.SERVICE_NAME,
		Forum.RESOURCE_NAME, getId());
    }

    @Override
    @XmlTransient
    public String getResourceName() {
	return getName();
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("Forum. Path " + this.path);
	sb.append("\nID " + this.id);
	sb.append("\nname " + this.name);
	sb.append("\ndate " + this.date);
	sb.append("\nstatus " + this.status);
	return sb.toString();
    }

    @Transient
    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    @Transient
    public HashMap<String, ThreadMessage> getMessages() {
	return messages;
    }

    public void setMessages(HashMap<String, ThreadMessage> messages) {
	this.messages = messages;
    }

}
