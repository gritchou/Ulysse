package org.qualipso.factory.collaboration.ws.beans;

import java.util.HashMap;

import org.qualipso.factory.collaboration.ws.CollaborationWSUtils;

public class ForumDTO {
    private String id;
    private String path;
    private String name;
    // We don't perist the following
    private String date;
    private String status = CollaborationWSUtils.FORUM_STATUS_ACTIVE;
    private HashMap<String, MessageDTO> messages;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String value) {
	this.name = value;
    }

    public String toString() {
	StringBuffer sb = new StringBuffer("Forum. Path " + this.path);
	sb.append("\nID " + this.id);
	sb.append("\nname " + this.name);
	sb.append("\ndate " + this.date);
	sb.append("\nstatus " + this.status);
	return sb.toString();
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    public HashMap<String, MessageDTO> getMessages() {
	return messages;
    }

    public void setMessages(HashMap<String, MessageDTO> messages) {
	this.messages = messages;
    }
}
