package org.qualipso.factory.collaboration.calendar.entity;

import java.util.HashMap;
import java.util.List;

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
import org.qualipso.factory.collaboration.beans.ParticipantDetails;
import org.qualipso.factory.collaboration.calendar.CalendarService;
import org.qualipso.factory.collaboration.utils.CollaborationUtils;
import org.qualipso.factory.membership.entity.Group;

/**
 * The Class CalendarItem.
 */
@Entity
@XmlType(name = CalendarItem.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ CalendarItem.RESOURCE_NAME, propOrder = { "seriesId", "name",
	"location", "date", "startTime", "endTime", "contactName",
	"contactEmail", "contactPhone", "recurrence", "times",
	"occurencePaths", "type","forum","attachments","participants","groups","confirmedParticipants"})
@SuppressWarnings("serial")
public class CalendarItem extends FactoryResource {
    
    /** The Constant RESOURCE_NAME. */
    public static final String RESOURCE_NAME = "calendar-item";
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5033298554422503678L;

    /**
     * Instantiates a new calendar item.
     */
    public CalendarItem() {

    }

    /** The id. */
    @Id
    private String id;
    // We persist id,name,path,seriesId,date,recurrence,times,ocpaths,forum and document attachments, and type
    /** The path. */
    private String path;
    
    /** The name. */
    private String name;
    
    /** The series id. */
    private String seriesId;
    
    /** The date. */
    private String date = "";// yyyy-mm-dd
    
    /** The recurrence. */
    private String recurrence = CollaborationUtils.REC_0;
    
    /** The times. */
    private long times = 1;
    
    /** The occurence paths. */
    private String[] occurencePaths;
    
    /** The forum. */
    private AttachmentDetails forum;
    
    /** The attachments. */
    private AttachmentDetails[] attachments;
    
    private ParticipantDetails[] participants;
    
    private Group[] groups;
    
    private String confirmedParticipants;
    
    /** The type. */
    private String type = CollaborationUtils.CALENDAR_EVENT;
    
    // We don't persist ocIds,location,start/end time,contact details
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
     * Gets the series id.
     * 
     * @return the series id
     */
    @XmlAttribute(name = "seriesId", required = true)
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
    @XmlElement(name = "date", required = true)
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
    @XmlElement(name = "type", required = false)
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
    @XmlElement(name = "recurrence", required = false)
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
    @XmlElement(name = "times", required = false)
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
    @XmlElement(name = "occurencePaths", required = false)
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
     * Gets the forum.
     * 
     * @return the forum
     */
    @XmlElement(name = "forum", required = false)
    public AttachmentDetails getForum() {
        return forum;
    }

    /**
     * Sets the forum.
     * 
     * @param forum the new forum
     */
    public void setForum(AttachmentDetails forum) {
        this.forum = forum;
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
    
    @XmlElement(name = "participants", required = false)
    public ParticipantDetails[] getParticipants() {
        return participants;
    }

    public void setParticipants(ParticipantDetails[] participants) {
        this.participants = participants;
    }

    @XmlElement(name = "groups", required = false)
    public Group[] getGroups() {
        return groups;
    }

    public void setGroups(Group[] groups) {
        this.groups = groups;
    }

    @XmlElement(name = "confirmedParticipants", required = false)
    public String getConfirmedParticipants() {
        return confirmedParticipants;
    }

    public void setConfirmedParticipants(String confirmedParticipants) {
        this.confirmedParticipants = confirmedParticipants;
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.FactoryResource#getFactoryResourceIdentifier()
     */
    @Override
    @XmlTransient
    public FactoryResourceIdentifier getFactoryResourceIdentifier() {
	return new FactoryResourceIdentifier(CalendarService.SERVICE_NAME,
		CalendarItem.RESOURCE_NAME, getId());
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
	StringBuffer sb = new StringBuffer("CalendarItem. Path " + this.path);
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
	sb.append("\ntype " + this.type);
	sb.append("\nforum " + this.forum);
	if(this.attachments!=null && this.attachments.length>0){
	    sb.append("\nattached documents: "+this.attachments.length);
	    for (int i = 0; i < this.attachments.length; i++) {
		sb.append("\ndoc " + this.attachments[i].getId() +" " + this.attachments[i].getName());
	    }
	}
	if(this.participants!=null && this.participants.length>0){
	    sb.append("\naparticipants: "+this.participants.length);
	    for (int i = 0; i < this.participants.length; i++) {
		sb.append("\n " + this.participants[i].getPath() +" "+this.participants[i].getDecision());
	    }
	}
	sb.append("\nconfirmed participants: "+this.confirmedParticipants);
	return sb.toString();
    }


    /**
     * Gets the occurence ids.
     * 
     * @return the occurence ids
     */
    @XmlTransient
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
    
    /**
     * Gets the location.
     * 
     * @return the location
     */
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
     * Gets the start time.
     * 
     * @return the start time
     */
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
}
