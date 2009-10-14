package org.qualipso.factory.collaboration.ws;

import java.util.HashMap;

import javax.ejb.Remote;

import org.qualipso.factory.collaboration.ws.beans.CalendarDTO;

@Remote
public interface CalendarWService
{
    public HashMap<String, Object> readEvent(String itemId, String seriesId, String view) throws Exception;

    public HashMap<String, Object> createEvent(String name, String location, String date, String startTime,
	    String endTime, String contactName, String contactEmail, String contactPhone, String recurrence, long times)
	    throws Exception;
    
    public HashMap<String, String> updateEvent(String id, String seriesId, String name, String location,
	    String date, String startTime, String endTime, String contactName, String contactEmail, String contactPhone)
	    throws Exception;
    
    public HashMap<String, String> updateRecurrence(CalendarDTO event, String date, String recurrence,
	    long times) throws Exception;
    
    public HashMap<String, String> deleteEvent(String id) throws Exception;
}
