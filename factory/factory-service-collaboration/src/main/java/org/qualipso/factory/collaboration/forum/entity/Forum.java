package org.qualipso.factory.collaboration.forum.entity;

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
import org.qualipso.factory.collaboration.beans.AttachmentDetails;
import org.qualipso.factory.collaboration.forum.ForumService;
import org.qualipso.factory.collaboration.utils.CollaborationUtils;

/**
 * The Class Forum.
 */
@Entity
@XmlType(name = Forum.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ Forum.RESOURCE_NAME, propOrder = { "name", "date", "status",
	"messages","attachments" })
@SuppressWarnings("serial")
public class Forum extends FactoryResource {
    
    /** The Constant RESOURCE_NAME. */
    public static final String RESOURCE_NAME = "forum";
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4023667551663471511L;

    /** The id. */
    @Id
    private String id;
    
    /** The path. */
    private String path;
    
    /** The name. */
    private String name;
    
    /** The status. */
    private String status = CollaborationUtils.FORUM_STATUS_ACTIVE;
    
    /** The attachments. */
    private AttachmentDetails[] attachments;
    // We don't perist the date and messages
    /** The date. */
    private String date;
    
    /** The messages. */
    private ThreadMessage[] messages ;

    /**
     * Instantiates a new forum.
     */
    public Forum() {

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
     * Gets the status.
     * 
     * @return the status
     */
    @XmlElement(name = "status", required = true)
    public String getStatus() {
	return status;
    }

    /**
     * Sets the status.
     * 
     * @param status the new status
     */
    public void setStatus(String status) {
	this.status = status;
    }
    
    /**
     * Gets the attachments.
     * 
     * @return the attachments
     */
    @XmlElement(name = "attachments", required = false)
    public AttachmentDetails[] getAttachments() {
        return attachments;
    }

    /**
     * Sets the attachments.
     * 
     * @param attachments the new attachments
     */
    public void setAttachments(AttachmentDetails[] attachments) {
        this.attachments = attachments;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.FactoryResource#getFactoryResourceIdentifier()
     */
    @Override
    @XmlTransient
    public FactoryResourceIdentifier getFactoryResourceIdentifier() {
	return new FactoryResourceIdentifier(ForumService.SERVICE_NAME,
		Forum.RESOURCE_NAME, getId());
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
	StringBuffer sb = new StringBuffer("Forum. Path " + this.path);
	sb.append("\nID " + this.id);
	sb.append("\nname " + this.name);
	sb.append("\ndate " + this.date);
	sb.append("\nstatus " + this.status);
	if(this.attachments!=null && this.attachments.length>0){
	    sb.append("\nattached documents: "+this.attachments.length);
	    for (int i = 0; i < this.attachments.length; i++) {
		sb.append("\ndoc " + this.attachments[i].getId() +" " + this.attachments[i].getName());
	    }
	}
	return sb.toString();
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
     * Gets the messages.
     * 
     * @return the messages
     */
    @Transient
    public ThreadMessage[] getMessages() {
	return messages;
    }

    /**
     * Sets the messages.
     * 
     * @param messages the new messages
     */
    public void setMessages(ThreadMessage[] messages) {
	this.messages = messages;
    }

}
