package org.qualipso.factory.collaboration.beans;

import java.io.Serializable;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;

@XmlType(name = CalendarExtra.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ CalendarExtra.RESOURCE_NAME, propOrder = { "attachments", "forum",
	"profiles", "groups" })
@SuppressWarnings("serial")
public class CalendarExtra implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String RESOURCE_NAME = "extra-details";

    String[] attachments;
    String forum;
    String[] profiles;
    String[] groups;

    public CalendarExtra() {

    }

    public CalendarExtra(String[] attachments, String forum) {
	setAttachments(attachments);
	setForum(forum);
    }

    public CalendarExtra(String[] attachments, String forum, String[] profiles,
	    String[] groups) {
	setAttachments(attachments);
	setForum(forum);
	setProfiles(profiles);
	setGroups(groups);
    }

    @XmlElement(name = "attachments", required = false)
    @Transient
    public String[] getAttachments() {
	return attachments;
    }

    public void setAttachments(String[] attachments) {
	this.attachments = attachments;
    }

    @XmlElement(name = "forum", required = false)
    @Transient
    public String getForum() {
	return forum;
    }

    public void setForum(String forum) {
	this.forum = forum;
    }

    @XmlElement(name = "profiles", required = false)
    @Transient
    public String[] getProfiles() {
	return profiles;
    }

    public void setProfiles(String[] profiles) {
	this.profiles = profiles;
    }

    @XmlElement(name = "groups", required = false)
    @Transient
    public String[] getGroups() {
	return groups;
    }

    public void setGroups(String[] groups) {
	this.groups = groups;
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("Calendar item extra details:");
	sb.append("\neforum " + this.forum);
	if (this.attachments != null && this.attachments.length > 0) {
	    for (int i = 0; i < this.attachments.length; i++) {
		sb.append("\n att: " + this.attachments[i]);
	    }
	}
	if (this.attachments != null && this.attachments.length > 0) {
	    sb.append("\n attachments:");
	    for (int i = 0; i < this.attachments.length; i++) {
		sb.append("\n " + this.attachments[i]);
	    }
	}
	if (this.profiles != null && this.profiles.length > 0) {
	    sb.append("\n profiles:");
	    for (int i = 0; i < this.profiles.length; i++) {
		sb.append("\n " + this.profiles[i]);
	    }
	}
	if (this.groups != null && this.groups.length > 0) {
	    sb.append("\n groups:");
	    for (int i = 0; i < this.groups.length; i++) {
		sb.append("\n " + this.groups[i]);
	    }
	}
	return sb.toString();
    }

}
