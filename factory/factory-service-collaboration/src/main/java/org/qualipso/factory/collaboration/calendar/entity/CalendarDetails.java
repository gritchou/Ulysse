package org.qualipso.factory.collaboration.calendar.entity;

import java.io.Serializable;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;

@XmlType(name = CalendarDetails.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ CalendarDetails.RESOURCE_NAME, propOrder = { "path", "name",
	"location", "date", "startTime", "endTime", "contactName",
	"contactEmail", "contactPhone", "recurrence", "times" })
@SuppressWarnings("serial")
public class CalendarDetails implements Serializable {
    public CalendarDetails() {
    }

    public static final String RESOURCE_NAME = "calendar-details";
    private static final long serialVersionUID = 1L;

    private String path;
    private String name;
    private String location;
    private String date;
    private String startTime;
    private String endTime;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String recurrence;
    private long times;

    @XmlAttribute(name = "path", required = true)
    @Transient
    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    @XmlAttribute(name = "name", required = true)
    @Transient
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @XmlAttribute(name = "location", required = true)
    @Transient
    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    @XmlAttribute(name = "date", required = true)
    @Transient
    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    @XmlAttribute(name = "startTime", required = true)
    @Transient
    public String getStartTime() {
	return startTime;
    }

    public void setStartTime(String startTime) {
	this.startTime = startTime;
    }

    @XmlAttribute(name = "endTime", required = true)
    @Transient
    public String getEndTime() {
	return endTime;
    }

    public void setEndTime(String endTime) {
	this.endTime = endTime;
    }

    @XmlAttribute(name = "contactName", required = true)
    @Transient
    public String getContactName() {
	return contactName;
    }

    public void setContactName(String contactName) {
	this.contactName = contactName;
    }

    @XmlAttribute(name = "contactEmail", required = true)
    @Transient
    public String getContactEmail() {
	return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
	this.contactEmail = contactEmail;
    }

    @XmlAttribute(name = "contactPhone", required = true)
    @Transient
    public String getContactPhone() {
	return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
	this.contactPhone = contactPhone;
    }

    @XmlAttribute(name = "recurrence", required = true)
    @Transient
    public String getRecurrence() {
	return recurrence;
    }

    public void setRecurrence(String recurrence) {
	this.recurrence = recurrence;
    }

    @XmlAttribute(name = "times", required = true)
    @Transient
    public long getTimes() {
	return times;
    }

    public void setTimes(long times) {
	this.times = times;
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("Calendar item details:Path "
		+ this.path);
	sb.append("\nname " + this.name);
	sb.append("\ndatee " + this.date);
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
