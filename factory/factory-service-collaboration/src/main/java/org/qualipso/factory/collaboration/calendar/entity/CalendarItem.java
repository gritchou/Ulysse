package org.qualipso.factory.collaboration.calendar.entity;

import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.collaboration.utils.CollaborationUtils;

@Entity
@XmlType(name = "CalendarItem", namespace = "http://org.qualipso.factory.ws/resource/calendar-item", propOrder = {
	"seriesId", "name", "location", "date", "startTime", "endTime", "contactName", "contactEmail", "contactPhone",
	"recurrence","times","occurencePaths",
	"type" })
@SuppressWarnings("serial")	
public class CalendarItem extends FactoryResource
{

    private static final long serialVersionUID = 5033298554422503678L;

    public CalendarItem()
    {

    }
    
    @Id
    private String id;
    // We persist  id,name,path,seriesId,date,recurrence,times,ocpaths and type
    private String path;
    private String name;
    private String seriesId;
    private String date = "";//yyyy-mm-dd
    private String recurrence = CollaborationUtils.REC_0;
    private long times = 1;
    private String[] occurencePaths;
    private String type = CollaborationUtils.CALENDAR_EVENT;
    // We don't persist ocIds,location,start/end time,contact details
    private HashMap<String, String> occurenceIds;
    private String location = "";
    private String startTime = "";
    private String endTime = "";
    private String contactName = "";
    private String contactEmail = "";
    private String contactPhone = "";
    
    
    @XmlAttribute(name = "id", required = true)
    public String getId()
    {
	return id;
    }

    public void setId(String id)
    {
	this.id = id;
    }

    @XmlAttribute(name = "path", required = true)
    @Transient
    @Override
    public String getResourcePath()
    {
	return path;
    }

    public void setResourcePath(String path)
    {
	this.path = path;
    }

    @XmlElement(name = "name", required = true)
    public String getName()
    {
	return name;
    }
    
    public void setName(String value)
    {
	this.name = value;
    }
    
    @XmlAttribute(name = "seriesId", required = true)
    public String getSeriesId()
    {
	return seriesId;
    }

    public void setSeriesId(String seriesId)
    {
	this.seriesId = seriesId;
    }
    
    @XmlElement(name = "date", required = true)
    public String getDate()
    {
	return date;
    }

    public void setDate(String date)
    {
	this.date = date;
    }
    
    @XmlElement(name = "type", required = false)
    public String getType()
    {
	return type;
    }

    public void setType(String type)
    {
	this.type = type;
    }
    
    @XmlElement(name = "recurrence", required = false)
    public String getRecurrence()
    {
        return recurrence;
    }

    
    public void setRecurrence(String recurrence)
    {
        this.recurrence = recurrence;
    }
    
    @XmlElement(name = "times", required = false)
    public long getTimes()
    {
        return times;
    }

    public void setTimes(long times)
    {
        this.times = times;
    }
    
    @XmlElement(name = "occurencePaths", required = false)
    public String[] getOccurencePaths()
    {
        return occurencePaths;
    }

    public void setOccurencePaths(String[] occurencePaths)
    {
        this.occurencePaths = occurencePaths;
    }
    
    
    @XmlTransient
    public HashMap<String, String> getOccurenceIds()
    {
	return occurenceIds;
    }

    public void setOccurenceIds(HashMap<String, String> occurenceIds)
    {
	this.occurenceIds = occurenceIds;
    }

    @Override
    @XmlTransient
    public FactoryResourceIdentifier getFactoryResourceIdentifier()
    {
	return new FactoryResourceIdentifier("CalendarService", "CalendarItem", getId());
    }

    @Override
    @XmlTransient
    public String getResourceName()
    {
	return getName();
    }
    
    @Override
    public String toString(){
	StringBuffer sb = new StringBuffer("CalendarItem. Path "+this.path);
	sb.append("\nID "+this.id);
	sb.append("\nname "+this.name);
	sb.append("\nlocation "+this.location);
	sb.append("\ndate "+this.date);
	sb.append("\nstartTime "+this.startTime);
	sb.append("\nendTime "+this.endTime);
	sb.append("\ncontactName "+this.contactName);
	sb.append("\ncontactEmail "+this.contactEmail);
	sb.append("\ncontactPhone "+this.contactPhone);
	sb.append("\nseriesId "+this.seriesId);
	sb.append("\nrecurrence "+this.recurrence);
	sb.append("\ntimes "+this.times);
	sb.append("\ntype "+this.type);
	return sb.toString();
    }


    @Transient
    public String getLocation()
    {
	return location;
    }

    public void setLocation(String location)
    {
	this.location = location;
    }
    @Transient
    public String getStartTime()
    {
	return startTime;
    }

    public void setStartTime(String startTime)
    {
	this.startTime = startTime;
    }
    @Transient
    public String getEndTime()
    {
	return endTime;
    }

    public void setEndTime(String endTime)
    {
	this.endTime = endTime;
    }
    @Transient
    public String getContactName()
    {
	return contactName;
    }

    public void setContactName(String contactName)
    {
	this.contactName = contactName;
    }
    @Transient
    public String getContactEmail()
    {
	return contactEmail;
    }

    public void setContactEmail(String contactEmail)
    {
	this.contactEmail = contactEmail;
    }
    @Transient
    public String getContactPhone()
    {
	return contactPhone;
    }

    public void setContactPhone(String contactPhone)
    {
	this.contactPhone = contactPhone;
    }

}
