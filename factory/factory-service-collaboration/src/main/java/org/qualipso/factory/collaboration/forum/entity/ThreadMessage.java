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

/**
 * The Class ThreadMessage.
 */
@Entity
@XmlType(name = ThreadMessage.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ ThreadMessage.RESOURCE_NAME, propOrder = { "name", "author",
	"parentId", "forumId", "messageBody", "datePosted", "messages",
	"attachments","replies" })
@SuppressWarnings("serial")
public class ThreadMessage extends FactoryResource {

    /** The Constant RESOURCE_NAME. */
    public static final String RESOURCE_NAME = "thread-message";
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1482783023713344338L;

    /**
     * Instantiates a new thread message.
     */
    public ThreadMessage() {

    }

    /** The id. */
    @Id
    private String id;
    
    /** The path. */
    private String path;
    
    /** The name. */
    private String name;
    
    /** The parent id. */
    private String parentId;
    
    /** The forum id. */
    private String forumId;
    
    /** The author. */
    private String author;
    
    /** The attachments. */
    private AttachmentDetails[] attachments;
    // We don't persist the following
    // (messageBody,datePosted,messages,replies)
    /** The message body. */
    private String messageBody;
    
    /** The date posted. */
    private String datePosted;
    
    /** The messages. */
    private ThreadMessage[] messages;
    
    /** The replies. */
    private int replies;

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

    /* (non-Javadoc)
     * @see org.qualipso.factory.FactoryResource#getFactoryResourceIdentifier()
     */
    @Override
    @XmlTransient
    public FactoryResourceIdentifier getFactoryResourceIdentifier() {
	return new FactoryResourceIdentifier(ForumService.SERVICE_NAME,
		ThreadMessage.RESOURCE_NAME, getId());
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.FactoryResource#getResourceName()
     */
    @Override
    @XmlTransient
    public String getResourceName() {
	return getName();
    }

    /**
     * Gets the author.
     * 
     * @return the author
     */
    @XmlElement(name = "author", required = true)
    public String getAuthor() {
	return author;
    }

    /**
     * Sets the author.
     * 
     * @param author the new author
     */
    public void setAuthor(String author) {
	this.author = author;
    }

    /**
     * Gets the parent id.
     * 
     * @return the parent id
     */
    @XmlElement(name = "parentId", required = true)
    public String getParentId() {
	return parentId;
    }

    /**
     * Sets the parent id.
     * 
     * @param parentId the new parent id
     */
    public void setParentId(String parentId) {
	this.parentId = parentId;
    }

    /**
     * Gets the forum id.
     * 
     * @return the forum id
     */
    @XmlElement(name = "forumId", required = true)
    public String getForumId() {
	return forumId;
    }

    /**
     * Sets the forum id.
     * 
     * @param forumId the new forum id
     */
    public void setForumId(String forumId) {
	this.forumId = forumId;
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("Thread. Path " + this.path);
	sb.append("\nID " + this.id);
	sb.append("\nname " + this.name);
	sb.append("\ndatePosted " + this.datePosted);
	sb.append("\nforumId " + this.forumId);
	sb.append("\nmessageBody " + this.messageBody);
	sb.append("\nparentId " + this.parentId);
	if (this.messages != null && this.messages.length > 0) {
	    sb.append("\nnumber of replies " + this.messages.length);
	}
	return sb.toString();
    }

    /**
     * Gets the message body.
     * 
     * @return the message body
     */
    @Transient
    public String getMessageBody() {
	return messageBody;
    }

    /**
     * Sets the message body.
     * 
     * @param messageBody the new message body
     */
    public void setMessageBody(String messageBody) {
	this.messageBody = messageBody;
    }

    /**
     * Gets the date posted.
     * 
     * @return the date posted
     */
    @Transient
    public String getDatePosted() {
	return datePosted;
    }

    /**
     * Sets the date posted.
     * 
     * @param datePosted the new date posted
     */
    public void setDatePosted(String datePosted) {
	this.datePosted = datePosted;
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
	if(messages!=null && messages.length>0){
	    setReplies(messages.length);
	}
    }

    /**
     * Gets the replies.
     * 
     * @return the replies
     */
    @Transient
    public int getReplies() {
        return replies;
    }

    /**
     * Sets the replies.
     * 
     * @param replies the new replies
     */
    public void setReplies(int replies) {
        this.replies = replies;
    }
    

}
