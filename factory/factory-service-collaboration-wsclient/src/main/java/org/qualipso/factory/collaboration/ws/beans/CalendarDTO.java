package org.qualipso.factory.collaboration.ws.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import org.qualipso.factory.collaboration.ws.CollaborationWSUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class CalendarDTO.
 */
public class CalendarDTO implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8029669733897223019L;

    /** The id. */
    private String id;
    // We persist the following
    /** The name. */
    private String name;

    /** The series id. */
    private String seriesId;

    /** The date in yyyy-mm-dd format. */
    private String date = "";

    /** The recurrence. */
    private String recurrence = CollaborationWSUtils.REC_0;

    /** The times. */
    private long times = 1;

    /** The occurence paths. */
    private String[] occurencePaths;

    /** The type. */
    private String type = CollaborationWSUtils.CALENDAR_EVENT;

    /** The occurence ids. */
    private HashMap<String, String> occurenceIds;

    /** The location. */
    private String location = "";

    /** The start time. */
    private String startTime = "";

    /** The end time. */
    private String endTime = "";

    /** The contact name. */
    private String contactName = "";

    /** The contact email. */
    private String contactEmail = "";

    /** The contact phone. */
    private String contactPhone = "";

    /** The attachments. */
    private HashMap<String, String> attachments;

    /** The forum. */
    private String forum;

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
     * Gets the series id.
     * 
     * @return the series id
     */
    public String getSeriesId() {
	return seriesId;
    }

    /**
     * Sets the series id.
     * 
     * @param seriesId the new series id
     */
    public void setSeriesId(String seriesId) {
	this.seriesId = seriesId;
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
     * Gets the type.
     * 
     * @return the type
     */
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
     * Gets the recurrence.
     * 
     * @return the recurrence
     */
    public String getRecurrence() {
	return recurrence;
    }

    /**
     * Sets the recurrence.
     * 
     * @param recurrence the new recurrence
     */
    public void setRecurrence(String recurrence) {
	this.recurrence = recurrence;
    }

    /**
     * Gets the times.
     * 
     * @return the times
     */
    public long getTimes() {
	return times;
    }

    /**
     * Sets the times.
     * 
     * @param times the new times
     */
    public void setTimes(long times) {
	this.times = times;
    }

    /**
     * Gets the occurence paths.
     * 
     * @return the occurence paths
     */
    public String[] getOccurencePaths() {
	return occurencePaths;
    }

    /**
     * Sets the occurence paths.
     * 
     * @param occurencePaths the new occurence paths
     */
    public void setOccurencePaths(String[] occurencePaths) {
	this.occurencePaths = occurencePaths;
    }

    /**
     * Gets the occurence ids.
     * 
     * @return the occurence ids
     */
    public HashMap<String, String> getOccurenceIds() {
	return occurenceIds;
    }

    /**
     * Sets the occurence ids.
     * 
     * @param occurenceIds the occurence ids
     */
    public void setOccurenceIds(HashMap<String, String> occurenceIds) {
	this.occurenceIds = occurenceIds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("CalendarDTO.");
	sb.append("\nID " + this.id);
	sb.append("\nname " + this.name);
	sb.append("\nlocation " + this.location);
	sb.append("\ndate " + this.date);
	sb.append("\nstartTime " + this.startTime);
	sb.append("\nendTime " + this.endTime);
	sb.append("\ncontactName " + this.contactName);
	sb.append("\ncontactEmail " + this.contactEmail);
	sb.append("\ncontactPhone " + this.contactPhone);
	sb.append("\nseriesId " + this.seriesId);
	sb.append("\nrecurrence " + this.recurrence);
	sb.append("\ntimes " + this.times);
	sb.append("\ntype " + this.type);
	sb.append("\nforum " + this.forum);
	if (this.attachments != null && this.attachments.size() > 0) {
	    sb
		    .append("\nEvent contains attachments "
			    + this.attachments.size());
	    Iterator<String> hashIterator = this.attachments.keySet()
		    .iterator();
	    while (hashIterator.hasNext()) {
		String docID = (String) hashIterator.next();
		sb.append("\ndocument " + docID);
	    }
	}
	return sb.toString();
    }

    /**
     * Gets the location.
     * 
     * @return the location
     */
    public String getLocation() {
	return location;
    }

    /**
     * Sets the location.
     * 
     * @param location the new location
     */
    public void setLocation(String location) {
	this.location = location;
    }

    /**
     * Gets the start time.
     * 
     * @return the start time
     */
    public String getStartTime() {
	return startTime;
    }

    /**
     * Sets the start time.
     * 
     * @param startTime the new start time
     */
    public void setStartTime(String startTime) {
	this.startTime = startTime;
    }

    /**
     * Gets the end time.
     * 
     * @return the end time
     */
    public String getEndTime() {
	return endTime;
    }

    /**
     * Sets the end time.
     * 
     * @param endTime the new end time
     */
    public void setEndTime(String endTime) {
	this.endTime = endTime;
    }

    /**
     * Gets the contact name.
     * 
     * @return the contact name
     */
    public String getContactName() {
	return contactName;
    }

    /**
     * Sets the contact name.
     * 
     * @param contactName the new contact name
     */
    public void setContactName(String contactName) {
	this.contactName = contactName;
    }

    /**
     * Gets the contact email.
     * 
     * @return the contact email
     */
    public String getContactEmail() {
	return contactEmail;
    }

    /**
     * Sets the contact email.
     * 
     * @param contactEmail the new contact email
     */
    public void setContactEmail(String contactEmail) {
	this.contactEmail = contactEmail;
    }

    /**
     * Gets the contact phone.
     * 
     * @return the contact phone
     */
    public String getContactPhone() {
	return contactPhone;
    }

    /**
     * Sets the contact phone.
     * 
     * @param contactPhone the new contact phone
     */
    public void setContactPhone(String contactPhone) {
	this.contactPhone = contactPhone;
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

    /**
     * Gets the forum.
     * 
     * @return the forum
     */
    public String getForum() {
	return forum;
    }

    /**
     * Sets the forum.
     * 
     * @param forum the new forum
     */
    public void setForum(String forum) {
	this.forum = forum;
    }
}
