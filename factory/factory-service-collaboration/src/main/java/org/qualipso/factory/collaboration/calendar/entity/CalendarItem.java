package org.qualipso.factory.collaboration.calendar.entity;

import java.util.HashMap;
import java.util.Iterator;

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
import org.qualipso.factory.collaboration.calendar.CalendarService;
import org.qualipso.factory.collaboration.utils.CollaborationUtils;

@Entity
@XmlType(name = CalendarItem.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ CalendarItem.RESOURCE_NAME, propOrder = { "seriesId", "name",
	"location", "date", "startTime", "endTime", "contactName",
	"contactEmail", "contactPhone", "recurrence", "times",
	"occurencePaths", "type","forum","documentPaths","forumId","documentIds" })
@SuppressWarnings("serial")
public class CalendarItem extends FactoryResource {
    public static final String RESOURCE_NAME = "calendar-item";
    private static final long serialVersionUID = 5033298554422503678L;

    public CalendarItem() {

    }

    @Id
    private String id;
    // We persist id,name,path,seriesId,date,recurrence,times,ocpaths and type
    private String path;
    private String name;
    private String seriesId;
    private String date = "";// yyyy-mm-dd
    private String recurrence = CollaborationUtils.REC_0;
    private long times = 1;
    private String[] occurencePaths;
    private String forum;
    private String[] documentPaths;
    private String type = CollaborationUtils.CALENDAR_EVENT;
    // We don't persist ocIds,location,start/end time,contact details
    private HashMap<String, String> occurenceIds;
    private String location = "";
    private String startTime = "";
    private String endTime = "";
    private String contactName = "";
    private String contactEmail = "";
    private String contactPhone = "";
    private String forumId;
    private HashMap<String, String> documentIds;

    @XmlAttribute(name = "id", required = true)
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    @XmlAttribute(name = "path", required = true)
    @Transient
    @Override
    public String getResourcePath() {
	return path;
    }

    public void setResourcePath(String path) {
	this.path = path;
    }

    @XmlElement(name = "name", required = true)
    public String getName() {
	return name;
    }

    public void setName(String value) {
	this.name = value;
    }

    @XmlAttribute(name = "seriesId", required = true)
    public String getSeriesId() {
	return seriesId;
    }

    public void setSeriesId(String seriesId) {
	this.seriesId = seriesId;
    }

    @XmlElement(name = "date", required = true)
    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    @XmlElement(name = "type", required = false)
    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    @XmlElement(name = "recurrence", required = false)
    public String getRecurrence() {
	return recurrence;
    }

    public void setRecurrence(String recurrence) {
	this.recurrence = recurrence;
    }

    @XmlElement(name = "times", required = false)
    public long getTimes() {
	return times;
    }

    public void setTimes(long times) {
	this.times = times;
    }

    @XmlElement(name = "occurencePaths", required = false)
    public String[] getOccurencePaths() {
	return occurencePaths;
    }

    public void setOccurencePaths(String[] occurencePaths) {
	this.occurencePaths = occurencePaths;
    }

    @XmlElement(name = "documentPaths", required = false)
    public String[] getDocumentPaths() {
        return documentPaths;
    }

    public void setDocumentPaths(String[] documentPaths) {
        this.documentPaths = documentPaths;
    }

    @XmlElement(name = "forum", required = false)
    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    @Override
    @XmlTransient
    public FactoryResourceIdentifier getFactoryResourceIdentifier() {
	return new FactoryResourceIdentifier(CalendarService.SERVICE_NAME,
		CalendarItem.RESOURCE_NAME, getId());
    }

    @Override
    @XmlTransient
    public String getResourceName() {
	return getName();
    }
    
    

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
	if(this.documentPaths!=null && this.documentPaths.length>0){
	    sb.append("\nattached documents: ");
	    for (int i = 0; i < this.documentPaths.length; i++) {
		sb.append("\ndoc " + this.documentPaths[i]);
	    }
	}
	sb.append("\nforum id " + this.forumId);
	if(this.documentIds!=null && this.documentIds.size()>0){
	    sb.append("\nEvent contains attachments "+this.documentIds.size());
	    Iterator<String> hashIterator = this.documentIds.keySet().iterator();
	    while(hashIterator.hasNext()) {
		String  docID = (String)hashIterator.next();
		sb.append("\ndocument " + docID);
	    }
	}
	
	return sb.toString();
    }


    @XmlTransient
    public HashMap<String, String> getOccurenceIds() {
	return occurenceIds;
    }

    public void setOccurenceIds(HashMap<String, String> occurenceIds) {
	this.occurenceIds = occurenceIds;
    }
    
    @Transient
    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    @Transient
    public String getStartTime() {
	return startTime;
    }

    public void setStartTime(String startTime) {
	this.startTime = startTime;
    }

    @Transient
    public String getEndTime() {
	return endTime;
    }

    public void setEndTime(String endTime) {
	this.endTime = endTime;
    }

    @Transient
    public String getContactName() {
	return contactName;
    }

    public void setContactName(String contactName) {
	this.contactName = contactName;
    }

    @Transient
    public String getContactEmail() {
	return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
	this.contactEmail = contactEmail;
    }

    @Transient
    public String getContactPhone() {
	return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
	this.contactPhone = contactPhone;
    }

    @Transient
    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    @Transient
    public HashMap<String, String> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(HashMap<String, String> documentIds) {
        this.documentIds = documentIds;
    }
    
    

}
