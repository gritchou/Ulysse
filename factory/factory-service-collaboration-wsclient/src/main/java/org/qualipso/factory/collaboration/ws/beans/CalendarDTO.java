package org.qualipso.factory.collaboration.ws.beans;

import java.util.HashMap;

import org.qualipso.factory.collaboration.ws.CollaborationWSUtils;

public class CalendarDTO
{
    private String id;
    // We persist the following
    private String name;
    private String seriesId;
    private String date = "";//yyyy-mm-dd
    private String recurrence = CollaborationWSUtils.REC_0;
    private long times = 1;
    private String[] occurencePaths;
    private String type = CollaborationWSUtils.CALENDAR_EVENT;
    private HashMap<String, String> occurenceIds;
    private String location = "";
    private String startTime = "";
    private String endTime = "";
    private String contactName = "";
    private String contactEmail = "";
    private String contactPhone = "";
    
    
    public String getId()
    {
	return id;
    }

    public void setId(String id)
    {
	this.id = id;
    }

    public String getName()
    {
	return name;
    }
    
    public void setName(String value)
    {
	this.name = value;
    }
    
    public String getSeriesId()
    {
	return seriesId;
    }

    public void setSeriesId(String seriesId)
    {
	this.seriesId = seriesId;
    }
    
    public String getDate()
    {
	return date;
    }

    public void setDate(String date)
    {
	this.date = date;
    }
    
    public String getType()
    {
	return type;
    }

    public void setType(String type)
    {
	this.type = type;
    }
    
    public String getRecurrence()
    {
        return recurrence;
    }

    
    public void setRecurrence(String recurrence)
    {
        this.recurrence = recurrence;
    }
    
    public long getTimes()
    {
        return times;
    }

    public void setTimes(long times)
    {
        this.times = times;
    }
    
    public String[] getOccurencePaths()
    {
        return occurencePaths;
    }

    public void setOccurencePaths(String[] occurencePaths)
    {
        this.occurencePaths = occurencePaths;
    }
    
    
    public HashMap<String, String> getOccurenceIds()
    {
	return occurenceIds;
    }

    public void setOccurenceIds(HashMap<String, String> occurenceIds)
    {
	this.occurenceIds = occurenceIds;
    }
    
    @Override
    public String toString(){
	StringBuffer sb = new StringBuffer("CalendarItem.");
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

    public String getLocation()
    {
	return location;
    }

    public void setLocation(String location)
    {
	this.location = location;
    }

    public String getStartTime()
    {
	return startTime;
    }

    public void setStartTime(String startTime)
    {
	this.startTime = startTime;
    }

    public String getEndTime()
    {
	return endTime;
    }

    public void setEndTime(String endTime)
    {
	this.endTime = endTime;
    }

    public String getContactName()
    {
	return contactName;
    }

    public void setContactName(String contactName)
    {
	this.contactName = contactName;
    }

    public String getContactEmail()
    {
	return contactEmail;
    }

    public void setContactEmail(String contactEmail)
    {
	this.contactEmail = contactEmail;
    }
    
    public String getContactPhone()
    {
	return contactPhone;
    }

    public void setContactPhone(String contactPhone)
    {
	this.contactPhone = contactPhone;
    }

}
