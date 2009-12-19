package org.qualipso.factory.collaboration.ws.beans;

import java.io.Serializable;
import java.util.HashMap;

import org.qualipso.factory.collaboration.ws.CollaborationWSUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class ForumDTO.
 */
public class ForumDTO implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8496688432864373240L;

    /** The id. */
    private String id;

    /** The path. */
    private String path;

    /** The name. */
    private String name;

    /** The date. */
    private String date;

    /** The status. */
    private String status = CollaborationWSUtils.FORUM_STATUS_ACTIVE;

    /** The messages. */
    private HashMap<String, MessageDTO> messages;

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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
	StringBuffer sb = new StringBuffer("Forum. Path " + this.path);
	sb.append("\nID " + this.id);
	sb.append("\nname " + this.name);
	sb.append("\ndate " + this.date);
	sb.append("\nstatus " + this.status);
	return sb.toString();
    }

    /**
     * Gets the status.
     * 
     * @return the status
     */
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
     * Gets the date.
     * 
     * @return the date
     */
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
    public HashMap<String, MessageDTO> getMessages() {
	return messages;
    }

    /**
     * Sets the messages.
     * 
     * @param messages the messages
     */
    public void setMessages(HashMap<String, MessageDTO> messages) {
	this.messages = messages;
    }
}
