package org.qualipso.factory.collaboration.beans;

import java.io.Serializable;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;

/**
 * The Class ThreadMessageDetails.
 */
@XmlType(name = ThreadMessageDetails.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ ThreadMessageDetails.RESOURCE_NAME, propOrder = { "name", "forumId",
	"author", "messageBody", "datePosted", "isReply" })
public class ThreadMessageDetails implements Serializable {

    /**
     * Instantiates a new thread message details.
     */
    public ThreadMessageDetails() {

    }
    
    /**
     * Instantiates a new thread message details.
     * 
     * @param name the name
     * @param forumId the forum id
     * @param messageBody the message body
     * @param datePosted the date posted
     * @param isReply the is reply
     */
    public ThreadMessageDetails(String name, String forumId,
	    String messageBody, String datePosted, boolean isReply) {
	setName(name);
	setForumId(forumId);
	setMessageBody(messageBody);
	setDatePosted(datePosted);
	setReply(isReply);
    }

    /** The Constant RESOURCE_NAME. */
    public static final String RESOURCE_NAME = "thread-details";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The name. */
    private String name;

    /** The forum id. */
    private String forumId;

    /** The author. */
    private String author;

    /** The message body. */
    private String messageBody;

    /** The date posted. */
    private String datePosted;

    /** The is reply. */
    private boolean isReply;

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

    /**
     * Gets the forum id.
     * 
     * @return the forum id
     */
    @XmlElement(name = "forumId", required = true)
    @Transient
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
     * Gets the author.
     * 
     * @return the author
     */
    @XmlElement(name = "author", required = true)
    @Transient
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
     * Gets the message body.
     * 
     * @return the message body
     */
    @XmlElement(name = "messageBody", required = true)
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
    @XmlElement(name = "datePosted", required = true)
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
     * Gets the checks if is reply.
     * 
     * @return the checks if is reply
     */
    @XmlElement(name = "isReply", required = true)
    @Transient
    public boolean getIsReply() {
	return isReply;
    }

    /**
     * Sets the reply.
     * 
     * @param isReply the new reply
     */
    public void setReply(boolean isReply) {
	this.isReply = isReply;
    }

}
