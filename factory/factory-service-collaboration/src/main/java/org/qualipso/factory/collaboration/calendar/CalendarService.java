package org.qualipso.factory.collaboration.calendar;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;
import org.qualipso.factory.collaboration.calendar.entity.CalendarItem;
@Remote
@WebService(name = "CalendarService", targetNamespace = "http://org.qualipso.factory.ws/service/calendar")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface CalendarService extends FactoryService
{

    @WebMethod
    public String[] createEvent(String path, String name, String location, String date, String startTime, String endTime,
	    String contactName, String contactEmail, String contactPhone, String reccurence, long times) throws CalendarServiceException;

    @WebMethod
    @WebResult(name = "calendar-item")
    public CalendarItem readEvent(String path) throws CalendarServiceException;

    @WebMethod
    public String updateEvent(String path, String name, String location, String date, String startTime, String endTime,
	    String contactName, String contactEmail, String contactPhone) throws CalendarServiceException;
    
    @WebMethod
    public String[] updateRecurrence(String path, String date, String recurrence, long times ) throws CalendarServiceException;

    @WebMethod
    public void deleteEvent(String path) throws CalendarServiceException;
    
    @WebMethod
    public CalendarItem[]  getCalendarItems(String path) throws CalendarServiceException;
    
    @WebMethod
    public CalendarItem[]  getCalendarItemsForMonth(String path,String year, String month) throws CalendarServiceException;
}
