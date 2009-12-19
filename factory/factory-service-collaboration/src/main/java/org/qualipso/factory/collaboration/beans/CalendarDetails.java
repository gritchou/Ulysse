package org.qualipso.factory.collaboration.beans;

import java.io.Serializable;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;

/**
 * The Class CalendarDetails.
 */
@XmlType(name = CalendarDetails.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ CalendarDetails.RESOURCE_NAME, propOrder = { "name",
	"location", "date", "startTime", "endTime", "contactName",
	"contactEmail", "contactPhone", "recurrence", "times" })
@SuppressWarnings("serial")
public class CalendarDetails implements Serializable {

    /**
     * Instantiates a new calendar details.
     */
    public CalendarDetails() {

    }

    /**
     * Instantiates a new calendar details.
     * 
     * @param name the name
     * @param location the location
     * @param date the date
     * @param startTime the start time
     * @param endTime the end time
     * @param contactName the contact name
     * @param contactEmail the contact email
     * @param contactPhone the contact phone
     * @param reccurence the reccurence
     * @param times the times
     */
    public CalendarDetails(String name, String location, String date,
	    String startTime, String endTime, String contactName,
	    String contactEmail, String contactPhone, String reccurence,
	    long times) {
	setName(name);
	setLocation(location);
	setDate(date);
	setStartTime(startTime);
	setEndTime(endTime);
	setContactName(contactName);
	setContactEmail(contactEmail);
	setContactPhone(contactPhone);
	setRecurrence(recurrence);
	setTimes(times);
    }

    /** The Constant RESOURCE_NAME. */
    public static final String RESOURCE_NAME = "calendar-details";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The name. */
    private String name;

    /** The location. */
    private String location;

    /** The date. */
    private String date;

    /** The start time. */
    private String startTime;

    /** The end time. */
    private String endTime;

    /** The contact name. */
    private String contactName;

    /** The contact email. */
    private String contactEmail;

    /** The contact phone. */
    private String contactPhone;

    /** The recurrence. */
    private String recurrence;

    /** The times. */
    private long times;

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
     * Gets the location.
     * 
     * @return the location
     */
    @XmlElement(name = "location", required = true)
    @Transient
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
     * Gets the date.
     * 
     * @return the date
     */
    @XmlElement(name = "date", required = true)
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
     * Gets the start time.
     * 
     * @return the start time
     */
    @XmlElement(name = "startTime", required = true)
    @Transient
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
    @XmlElement(name = "endTime", required = true)
    @Transient
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
    @XmlElement(name = "contactName", required = true)
    @Transient
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
    @XmlElement(name = "contactEmail", required = true)
    @Transient
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
    @XmlElement(name = "contactPhone", required = true)
    @Transient
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
     * Gets the recurrence.
     * 
     * @return the recurrence
     */
    @XmlElement(name = "recurrence", required = true)
    @Transient
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
    @XmlElement(name = "times", required = true)
    @Transient
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("Calendar item details:");
	sb.append("\nname " + this.name);
	sb.append("\ndate " + this.date);
	sb.append("\nlocation " + this.location);
	sb.append("\nstart " + this.startTime);
	sb.append("\nend " + this.endTime);
	sb.append("\ncontact " + this.contactName);
	sb.append("\nemail " + this.contactEmail);
	sb.append("\nphone " + this.contactPhone);
	sb.append("\nrecurrence " + this.recurrence);
	sb.append("\ntimes " + this.times);
	return sb.toString();
    }

}
