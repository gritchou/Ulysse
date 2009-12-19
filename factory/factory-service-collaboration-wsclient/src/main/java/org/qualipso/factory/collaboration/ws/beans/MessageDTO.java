package org.qualipso.factory.collaboration.ws.beans;

import java.io.Serializable;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * The Class MessageDTO.
 */
public class MessageDTO implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4675581061311251248L;

    /** The id. */
    private String id;

    /** The name. */
    private String name;

    /** The parent id. */
    private String parentId;

    /** The forum id. */
    private String forumId;

    /** The author. */
    private String author;

    /** The message body. */
    private String messageBody;

    /** The date posted. */
    private String datePosted;

    /** The num replies. */
    private String numReplies = "0";

    /** The is reply. */
    private boolean isReply;

    /** The message replies. */
    private HashMap<String, MessageDTO> messageReplies = null;

    /** The attachments. */
    private HashMap<String, String> attachments = null;

    /**
     * Gets the id.
     * 
     * @return the id
     */
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
     * Gets the name.
     * 
     * @return the name
     */
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
     * Gets the author.
     * 
     * @return the author
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("Thread.");
	sb.append("\nID " + this.id);
	sb.append("\nname " + this.name);
	sb.append("\ndatePosted " + this.datePosted);
	sb.append("\nforumId " + this.forumId);
	sb.append("\nmessageBody " + this.messageBody);
	sb.append("\nparentId " + this.parentId);
	return sb.toString();
    }

    /**
     * Gets the message body.
     * 
     * @return the message body
     */
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
     * Gets the num replies.
     * 
     * @return the num replies
     */
    public String getNumReplies() {
	return numReplies;
    }

    /**
     * Sets the num replies.
     * 
     * @param numReplies the new num replies
     */
    public void setNumReplies(String numReplies) {
	this.numReplies = numReplies;
    }

    /**
     * Checks if is reply.
     * 
     * @return true, if is reply
     */
    public boolean isReply() {
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

    /**
     * Gets the message replies.
     * 
     * @return the message replies
     */
    public HashMap<String, MessageDTO> getMessageReplies() {
	return messageReplies;
    }

    /**
     * Sets the message replies.
     * 
     * @param messageReplies the message replies
     */
    public void setMessageReplies(HashMap<String, MessageDTO> messageReplies) {
	this.messageReplies = messageReplies;
    }

    /**
     * Gets the attachments.
     * 
     * @return the attachments
     */
    public HashMap<String, String> getAttachments() {
	return attachments;
    }

    /**
     * Sets the attachments.
     * 
     * @param attachments the attachments
     */
    public void setAttachments(HashMap<String, String> attachments) {
	this.attachments = attachments;
    }
}
