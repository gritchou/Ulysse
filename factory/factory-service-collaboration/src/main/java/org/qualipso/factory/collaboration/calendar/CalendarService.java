package org.qualipso.factory.collaboration.calendar;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.collaboration.beans.CalendarDetails;
import org.qualipso.factory.collaboration.beans.CalendarEvent;
import org.qualipso.factory.collaboration.beans.CalendarExtra;
import org.qualipso.factory.collaboration.beans.CalendarListItem;
import org.qualipso.factory.collaboration.beans.ParticipantDetails;
import org.qualipso.factory.collaboration.calendar.entity.CalendarItem;

@Remote
@WebService(name = CalendarService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE
	+ CalendarService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface CalendarService extends FactoryService {

    String SERVICE_NAME = "calendar";

    String[] RESOURCE_TYPE_LIST = new String[] { CalendarItem.RESOURCE_NAME };

    @WebMethod
    @WebResult(name = "paths")
    String[] createEvent(@WebParam(name = "parent-path") String path,
	    @WebParam(name = "calendar-details") CalendarDetails calendarDetails)
	    throws CalendarServiceException;

    @WebMethod
    @WebResult(name = "paths")
    String[] createEventWithAttachments(
	    @WebParam(name = "parent-path") String path,
	    @WebParam(name = "calendar-details") CalendarDetails calendarDetails,
	    @WebParam(name = "attachments") String[] documentPaths,
	    @WebParam(name = "forumPath") String forumPath)
	    throws CalendarServiceException;
    
    @WebMethod
    @WebResult(name = "calendar-item")
    CalendarItem readEvent(@WebParam(name = "path") String path)
	    throws CalendarServiceException;

    @WebMethod
    @WebResult(name = "updated-path")
    String updateEvent(@WebParam(name = "path") String path,
	    @WebParam(name = "calendar-details") CalendarEvent calendarEvent)
	    throws CalendarServiceException;

    @WebMethod
    @WebResult(name = "updated-path")
    String updateEventWithAttachments(@WebParam(name = "path") String path,
	    @WebParam(name = "calendar-details") CalendarEvent calendarEvent,
	    @WebParam(name = "attachments") String[] documentPaths,
	    @WebParam(name = "forumPath") String forumPath)
	    throws CalendarServiceException;
    
    @WebMethod
    @WebResult(name = "updated-path")
    String updateEventExtra(
	    @WebParam(name = "path") String path,
	    @WebParam(name = "calendar-details") CalendarEvent calendarEvent,
	    @WebParam(name = "extra-details") CalendarExtra extraDetails)
	    throws CalendarServiceException;
    
    @WebMethod
    @WebResult(name = "path")
    String updateEventAdvanced(
	    @WebParam(name = "path") String path,
	    @WebParam(name = "calendar-details") CalendarEvent calendarEvent,
	    @WebParam(name = "attachments") String[] documentPaths,
	    @WebParam(name = "forumPath") String forumPath,
	    @WebParam(name = "profiles") String[] profilePaths,
	    @WebParam(name = "groups") String[] groupPaths)
	    throws CalendarServiceException;

    @WebMethod
    @WebResult(name = "paths")
    String[] updateRecurrence(@WebParam(name = "path") String path,
	    @WebParam(name = "date") String date,
	    @WebParam(name = "recurrence") String recurrence,
	    @WebParam(name = "times") long times)
	    throws CalendarServiceException;

    @WebMethod
    void deleteEvent(@WebParam(name = "path") String path)
	    throws CalendarServiceException;

    @WebMethod
    @WebResult(name = "calendar-items")
    CalendarItem[] getCalendarItems(@WebParam(name = "path") String path)
	    throws CalendarServiceException;

    @WebMethod
    @WebResult(name = "calendar-items")
    CalendarItem[] getCalendarItemsForMonth(
	    @WebParam(name = "path") String path,
	    @WebParam(name = "year") String year,
	    @WebParam(name = "month") String month)
	    throws CalendarServiceException;

    @WebMethod
    @WebResult(name = "calendar-items")
    CalendarListItem[] getMonthlyCalendar(@WebParam(name = "path") String path,
	    @WebParam(name = "year") String year,
	    @WebParam(name = "month") String month)
	    throws CalendarServiceException;

    @WebMethod
    @WebResult(name = "calendar-items")
    CalendarListItem[] getMonthlyCalendarForUser(
	    @WebParam(name = "year") String year,
	    @WebParam(name = "month") String month)
	    throws CalendarServiceException;

    @WebMethod
    void atttachDocumentToEvent(@WebParam(name = "path") String path,
	    @WebParam(name = "documentPath") String[] documentPaths)
	    throws CalendarServiceException;

    @WebMethod
    void attachForumToEvent(@WebParam(name = "path") String path,
	    @WebParam(name = "forumPath") String forumPath)
	    throws CalendarServiceException;

    @WebMethod
    void addEventParticipants(@WebParam(name = "path") String path,
	    @WebParam(name = "profile-path") String[] profilePaths)
	    throws CalendarServiceException;

    @WebMethod
    @WebResult(name = "participants")
    ParticipantDetails[] getEventParticipants(
	    @WebParam(name = "path") String path)
	    throws CalendarServiceException;

    @WebMethod
    void handleInvitation(@WebParam(name = "path") String path,
	    @WebParam(name = "decision") String decision)
	    throws CalendarServiceException;

    @WebMethod
    @WebResult(name = "events")
    String[] getUserParticipations() throws CalendarServiceException;

}
