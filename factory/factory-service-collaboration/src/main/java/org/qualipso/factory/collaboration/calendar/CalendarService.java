package org.qualipso.factory.collaboration.calendar;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.collaboration.calendar.entity.CalendarDetails;
import org.qualipso.factory.collaboration.calendar.entity.CalendarItem;
import org.qualipso.factory.collaboration.document.entity.Document;

/**
 * Calendar Service.
 * 
 * @author gstro
 */
@Remote
@WebService(name = CalendarService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE
	+ CalendarService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface CalendarService extends FactoryService {

    /** The service name. */
    String SERVICE_NAME = "calendar";

    /** The array of available resources. */
    String[] RESOURCE_TYPE_LIST = new String[] { CalendarItem.RESOURCE_NAME };

    /**
     * Create event based on given details.
     * 
     * @param path
     *            the path
     * @param name
     *            the name
     * @param location
     *            the location
     * @param date
     *            the date
     * @param startTime
     *            the start time
     * @param endTime
     *            the end time
     * @param contactName
     *            the contact name
     * @param contactEmail
     *            the contact email
     * @param contactPhone
     *            the contact phone
     * @param reccurence
     *            the reccurence
     * @param times
     *            the times
     * @return the Array of paths of the created event together with its
     *         occurrences
     * @throws CalendarServiceException
     *             the calendar service
     */
    @WebMethod
    String[] createEvent(@WebParam(name = "path") String path,
	    @WebParam(name = "name") String name,
	    @WebParam(name = "location") String location,
	    @WebParam(name = "date") String date,
	    @WebParam(name = "startTime") String startTime,
	    @WebParam(name = "endTime") String endTime,
	    @WebParam(name = "contactName") String contactName,
	    @WebParam(name = "contactEmail") String contactEmail,
	    @WebParam(name = "contactPhone") String contactPhone,
	    @WebParam(name = "reccurence") String reccurence,
	    @WebParam(name = "times") long times)
	    throws CalendarServiceException;

    /**
     * Creates the calendar item.
     * 
     * @param path
     *            the path the parent path
     * @param calendarDetails
     *            the calendar details
     * 
     * @return the array of paths of the created event together with its
     *         occurrences
     * 
     * @throws CalendarServiceException
     *             the calendar service exception
     */
    @WebMethod
    String[] createCalendarItem(@WebParam(name = "path") String path,
	    @WebParam(name = "calendar-details") CalendarDetails calendarDetails)
	    throws CalendarServiceException;

    /**
     * Read event details based on path.
     * 
     * @param path
     *            the path
     * @return the calendar item
     * @throws CalendarServiceException
     *             the calendar service exception
     */
    @WebMethod
    @WebResult(name = "calendar-item")
    CalendarItem readEvent(@WebParam(name = "path") String path)
	    throws CalendarServiceException;

    /**
     * Update event details based on given input.
     * 
     * @param path
     *            the path
     * @param name
     *            the name
     * @param location
     *            the location
     * @param date
     *            the date
     * @param startTime
     *            the start time
     * @param endTime
     *            the end time
     * @param contactName
     *            the contact name
     * @param contactEmail
     *            the contact email
     * @param contactPhone
     *            the contact phone
     * @return the value of the path, which is the same if date has not been
     *         changed.
     * @throws CalendarServiceException
     *             the calendar exception
     */
    @WebMethod
    String updateEvent(@WebParam(name = "path") String path,
	    @WebParam(name = "name") String name,
	    @WebParam(name = "location") String location,
	    @WebParam(name = "date") String date,
	    @WebParam(name = "startTime") String startTime,
	    @WebParam(name = "endTime") String endTime,
	    @WebParam(name = "contactName") String contactName,
	    @WebParam(name = "contactEmail") String contactEmail,
	    @WebParam(name = "contactPhone") String contactPhone)
	    throws CalendarServiceException;

    /**
     * Update event's occurence. If we change date only then call the normal
     * update. If we changed recurrence&times then we delete old and create new
     * ones based on the given event(path)values.
     * 
     * @param path
     *            the path
     * @param date
     *            the date
     * @param recurrence
     *            the recurrence
     * @param times
     *            the times
     * @return the Array of paths of the created event together with its
     *         occurrences
     * @throws CalendarServiceException
     *             the calendar exception
     */
    @WebMethod
    String[] updateRecurrence(@WebParam(name = "path") String path,
	    @WebParam(name = "date") String date,
	    @WebParam(name = "recurrence") String recurrence,
	    @WebParam(name = "times") long times)
	    throws CalendarServiceException;

    /**
     * Atttach document(s) to event.
     * 
     * @param path
     *            the path
     * @param documentPaths
     *            array of documents to attach (maximum 10)
     * @throws CalendarServiceException
     *             the calendar exception
     */
    @WebMethod
    void atttachDocumentToEvent(@WebParam(name = "path") String path,
	    @WebParam(name = "documentPath") String[] documentPaths)
	    throws CalendarServiceException;

    /**
     * Attach forum to event.
     * 
     * @param path
     *            the path
     * @param forumPath
     *            the forum path
     * 
     * @throws CalendarServiceException
     *             the calendar service exception
     */
    void attachForumToEvent(@WebParam(name = "path") String path,
	    @WebParam(name = "forumPath") String forumPath)
	    throws CalendarServiceException;

    /**
     * Delete event.
     * 
     * @param path
     *            the path
     * @throws CalendarServiceException
     *             the calendar exception
     */
    @WebMethod
    void deleteEvent(@WebParam(name = "path") String path)
	    throws CalendarServiceException;

    /**
     * Gets the calendar items(events) from a specified path.
     * 
     * @param path
     *            the path
     * @return the calendar items
     * @throws CalendarServiceException
     *             the calendar exception
     */
    @WebMethod
    CalendarItem[] getCalendarItems(@WebParam(name = "path") String path)
	    throws CalendarServiceException;

    /**
     * Gets the calendar items for month.
     * 
     * @param path
     *            the path
     * @param year
     *            the year
     * @param month
     *            the month
     * @return the calendar items under the given path for the given month and
     *         year
     * @throws CalendarServiceException
     *             the calendar service exception
     */
    @WebMethod
    CalendarItem[] getCalendarItemsForMonth(
	    @WebParam(name = "path") String path,
	    @WebParam(name = "year") String year,
	    @WebParam(name = "month") String month)
	    throws CalendarServiceException;

    /**
     * Gets the event attachments.
     * 
     * @param path
     *            the path
     * @return the event attachments
     * @throws CalendarServiceException
     *             the calendar service exception
     */
    @WebMethod
    Document[] getEventAttachments(@WebParam(name = "path") String path)
	    throws CalendarServiceException;
}
