package org.qualipso.factory.collaboration.calendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.browser.BrowserService;
import org.qualipso.factory.collaboration.calendar.entity.CalendarDetails;
import org.qualipso.factory.collaboration.calendar.entity.CalendarItem;
import org.qualipso.factory.collaboration.document.DocumentService;
import org.qualipso.factory.collaboration.document.DocumentServiceException;
import org.qualipso.factory.collaboration.document.entity.Document;
import org.qualipso.factory.collaboration.forum.ForumService;
import org.qualipso.factory.collaboration.forum.entity.Forum;
import org.qualipso.factory.collaboration.utils.CollaborationUtils;
import org.qualipso.factory.collaboration.ws.CalendarWService;
import org.qualipso.factory.collaboration.ws.beans.CalendarDTO;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.core.entity.Folder;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;

// TODO: Auto-generated Javadoc
/**
 * The Class implements the CalendarService.
 * 
 * @author gstro
 */
@Stateless(name = "CalendarServiceBean", mappedName = FactoryNamingConvention.SERVICE_PREFIX
	+ CalendarService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.collaboration.calendar.CalendarService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE
	+ CalendarService.SERVICE_NAME, serviceName = CalendarService.SERVICE_NAME, portName = CalendarService.SERVICE_NAME
	+ "Port")
@WebContext(contextRoot = CollaborationUtils.COLLABORATION_SERVICE_PREFIX, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX
	+ CalendarService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class CalendarServiceBean implements CalendarService {

    /** The logger. */
    private static Log logger = LogFactory.getLog(CalendarServiceBean.class);

    /** The binding. */
    private BindingService binding;

    /** The pep. */
    private PEPService pep;

    /** The pap. */
    private PAPService pap;

    /** The notification. */
    private NotificationService notification;

    /** The membership. */
    private MembershipService membership;
    //
    /** The browser. */
    private BrowserService browser;

    /** The core. */
    private CoreService core;
    //
    /** The ctx. */
    private SessionContext ctx;

    /** The em. */
    private EntityManager em;
    //
    /** The calendar ws. */
    private CalendarWService calendarWS;

    /** The document service. */
    private DocumentService documentService;

    /** The forum service. */
    private ForumService forumService;

    //
    /**
     * Instantiates a new calendar service bean.
     */
    public CalendarServiceBean() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.calendar.CalendarService#createEvent
     * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String, long)
     */
    @SuppressWarnings("unchecked")
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String[] createEvent(String calendarPath, String name,
	    String location, String date, String startTime, String endTime,
	    String contactName, String contactEmail, String contactPhone,
	    String recurrence, long times) throws CalendarServiceException {
	String[] paths = null;
	logIt("Create event under " + calendarPath);
	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new CalendarServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    // Check the values
	    CollaborationUtils
		    .checkCreateEventValues(name, location, date, startTime,
			    endTime, contactName, contactEmail, contactPhone);
	    // Set path for the event. We assume that all calendar items are
	    // under a defined calendar path. For example
	    // /any-folder/calendar
	    // The path wil be /path/yyyy/M/dd f.e. /calendar/2009/9/16/UID
	    pep.checkSecurity(caller, calendarPath, "create");
	    // Call the mermig WS to create the event
	    HashMap resultMap = calendarWS.createEvent(name, location, date,
		    startTime, endTime, contactName, contactEmail,
		    contactPhone, recurrence, times);
	    logIt("Message Code:" + resultMap.get("statusCode") + " Message: "
		    + resultMap.get("statusMessage"));
	    if (!resultMap.get("statusCode").equals(
		    CollaborationUtils.SUCCESS_CODE)) {
		throw new CalendarServiceException(
			"Error code recieved from the WS." + " Code"
				+ resultMap.get("statusCode") + " Message:"
				+ resultMap.get("statusMessage"));
	    }
	    String seriesID = (String) resultMap.get("seriesID");
	    HashMap<String, String> occurences = (HashMap<String, String>) resultMap
		    .get("occurenceIds");
	    // We save the series and the occurences
	    if (occurences != null && occurences.size() > 1) {
		// Calculate paths first becuase we need to save them on every
		// occurence
		paths = createOcPaths(occurences, date, calendarPath);
		logIt("Need to save the series and the occurences "
			+ occurences.size());
		Iterator iterator = occurences.entrySet().iterator();
		int cnt = 0;
		while (iterator.hasNext()) {
		    String newDate = getNextDay(date, cnt);
		    Map.Entry entry = (Map.Entry) iterator.next();
		    String ocID = (String) entry.getValue();
		    String ocPath = PathHelper.normalize(paths[cnt]);
		    logIt("Save occurence " + ocID + " at path " + ocPath);
		    // Save the entity (set values only to ones we persist)
		    // We persist
		    // id,name,path,seriesId,date,recurrence,times,ocpaths and
		    // type
		    CalendarItem event = new CalendarItem();
		    event.setId(ocID);
		    event.setName(name);
		    event.setResourcePath(ocPath);
		    event.setSeriesId(seriesID);
		    event.setDate(newDate);
		    event.setRecurrence(recurrence);
		    event.setTimes(times);
		    event.setOccurencePaths(paths);
		    event.setType(CollaborationUtils.CALENDAR_EVENT);
		    // save it
		    em.persist(event);
		    // Bind the event with the path
		    binding.bind(event.getFactoryResourceIdentifier(), ocPath);
		    binding.setProperty(ocPath,
			    FactoryResourceProperty.CREATION_TIMESTAMP, ""
				    + System.currentTimeMillis());
		    binding.setProperty(ocPath,
			    FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, ""
				    + System.currentTimeMillis());
		    binding.setProperty(ocPath, FactoryResourceProperty.AUTHOR,
			    caller);

		    // Create policy (owner)
		    String policyId = UUID.randomUUID().toString();
		    pap.createPolicy(policyId, PAPServiceHelper
			    .buildOwnerPolicy(policyId, caller, ocPath));
		    // Setting security properties on the node :
		    binding.setProperty(ocPath, FactoryResourceProperty.OWNER,
			    caller);
		    binding.setProperty(ocPath,
			    FactoryResourceProperty.POLICY_ID, policyId);
		    // Notify for the creation
		    notification.throwEvent(new Event(ocPath, caller,
			    CalendarItem.RESOURCE_NAME, Event.buildEventType(
				    CalendarService.SERVICE_NAME,
				    CalendarItem.RESOURCE_NAME, "create"), ""));
		    logIt(event.toString());
		    cnt++;
		}
	    } else {
		String datePath = createDatePath(date, calendarPath);
		String path = calendarPath;
		if (datePath != null) {
		    path += "/" + datePath;
		}
		// path += "/" + CollaborationUtils.normalizeForPath(name);
		String firstOcID = CollaborationUtils
			.getFirstElement(occurences);
		path += "/" + firstOcID;
		path = PathHelper.normalize(path);
		logIt("Path " + path);
		// Save the entity
		CalendarItem event = new CalendarItem();
		event.setId(firstOcID);
		event.setSeriesId(seriesID);
		event.setName(name);
		event.setDate(date);
		event.setType(CollaborationUtils.CALENDAR_EVENT);
		event.setResourcePath(path);
		event.setRecurrence(recurrence);
		event.setTimes(times);
		// save it
		em.persist(event);
		// Bind the event with the path
		binding.bind(event.getFactoryResourceIdentifier(), path);
		binding.setProperty(path,
			FactoryResourceProperty.CREATION_TIMESTAMP, ""
				+ System.currentTimeMillis());
		binding.setProperty(path,
			FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, ""
				+ System.currentTimeMillis());
		binding.setProperty(path, FactoryResourceProperty.AUTHOR,
			caller);
		// Create policy (owner)
		String policyId = UUID.randomUUID().toString();
		pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(
			policyId, caller, path));
		binding
			.setProperty(path, FactoryResourceProperty.OWNER,
				caller);
		binding.setProperty(path, FactoryResourceProperty.POLICY_ID,
			policyId);
		// Notify for the creation
		notification.throwEvent(new Event(path, caller,
			CalendarItem.RESOURCE_NAME, Event.buildEventType(
				CalendarService.SERVICE_NAME,
				CalendarItem.RESOURCE_NAME, "create"), ""));
		logIt(event.toString());
		paths = new String[1];
		paths[0] = path;
	    }
	    return paths;
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to create the event at path " + calendarPath,
		    e);
	    throw new CalendarServiceException(
		    "Unable to create the event at path " + calendarPath, e);
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.qualipso.factory.collaboration.calendar.CalendarService#
     * createCalendarItem(java.lang.String,
     * org.qualipso.factory.collaboration.calendar.entity.CalendarDetails)
     */
    @SuppressWarnings("unchecked")
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String[] createCalendarItem(String calendarPath,
	    CalendarDetails calendarDetails) throws CalendarServiceException {
	String[] paths = null;
	logIt("Create event under " + calendarPath);
	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new CalendarServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    // Check the values
	    CollaborationUtils.checkCreateEventValues(
		    calendarDetails.getName(), calendarDetails.getLocation(),
		    calendarDetails.getDate(), calendarDetails.getStartTime(),
		    calendarDetails.getEndTime(), calendarDetails
			    .getContactName(), calendarDetails
			    .getContactEmail(), calendarDetails
			    .getContactPhone());
	    // Set path for the event. We assume that all calendar items are
	    // under a defined calendar path. For example
	    // /any-folder/calendar
	    // The path wil be /path/yyyy/M/dd f.e. /calendar/2009/9/16/UID
	    pep.checkSecurity(caller, calendarPath, "create");
	    // Call the mermig WS to create the event
	    CalendarDTO dto = convert2DTO(calendarDetails);
	    HashMap resultMap = calendarWS.createEvent(dto);
	    logIt("Message Code:" + resultMap.get("statusCode") + " Message: "
		    + resultMap.get("statusMessage"));
	    if (!resultMap.get("statusCode").equals(
		    CollaborationUtils.SUCCESS_CODE)) {
		throw new CalendarServiceException(
			"Error code recieved from the WS." + " Code"
				+ resultMap.get("statusCode") + " Message:"
				+ resultMap.get("statusMessage"));
	    }
	    String seriesID = (String) resultMap.get("seriesID");
	    HashMap<String, String> occurences = (HashMap<String, String>) resultMap
		    .get("occurenceIds");
	    // We save the series and the occurences
	    if (occurences != null && occurences.size() > 1) {
		// Calculate paths first becuase we need to save them on every
		// occurence
		paths = createOcPaths(occurences, calendarDetails.getDate(),
			calendarPath);
		logIt("Need to save the series and the occurences "
			+ occurences.size());
		Iterator iterator = occurences.entrySet().iterator();
		int cnt = 0;
		while (iterator.hasNext()) {
		    String newDate = getNextDay(calendarDetails.getDate(), cnt);
		    Map.Entry entry = (Map.Entry) iterator.next();
		    String ocID = (String) entry.getValue();
		    String ocPath = PathHelper.normalize(paths[cnt]);
		    logIt("Save occurence " + ocID + " at path " + ocPath);
		    // Save the entity (set values only to ones we persist)
		    // We persist
		    // id,name,path,seriesId,date,recurrence,times,ocpaths and
		    // type
		    CalendarItem event = new CalendarItem();
		    event.setId(ocID);
		    event.setName(calendarDetails.getName());
		    event.setResourcePath(ocPath);
		    event.setSeriesId(seriesID);
		    event.setDate(newDate);
		    event.setRecurrence(calendarDetails.getRecurrence());
		    event.setTimes(calendarDetails.getTimes());
		    event.setOccurencePaths(paths);
		    event.setType(CollaborationUtils.CALENDAR_EVENT);
		    // save it
		    em.persist(event);
		    // Bind the event with the path
		    binding.bind(event.getFactoryResourceIdentifier(), ocPath);
		    binding.setProperty(ocPath,
			    FactoryResourceProperty.CREATION_TIMESTAMP, ""
				    + System.currentTimeMillis());
		    binding.setProperty(ocPath,
			    FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, ""
				    + System.currentTimeMillis());
		    binding.setProperty(ocPath, FactoryResourceProperty.AUTHOR,
			    caller);

		    // Create policy (owner)
		    String policyId = UUID.randomUUID().toString();
		    pap.createPolicy(policyId, PAPServiceHelper
			    .buildOwnerPolicy(policyId, caller, ocPath));
		    // Setting security properties on the node :
		    binding.setProperty(ocPath, FactoryResourceProperty.OWNER,
			    caller);
		    binding.setProperty(ocPath,
			    FactoryResourceProperty.POLICY_ID, policyId);
		    // Notify for the creation
		    notification.throwEvent(new Event(ocPath, caller,
			    CalendarItem.RESOURCE_NAME, Event.buildEventType(
				    CalendarService.SERVICE_NAME,
				    CalendarItem.RESOURCE_NAME, "create"), ""));
		    logIt(event.toString());
		    cnt++;
		}
	    } else {
		String datePath = createDatePath(calendarDetails.getDate(),
			calendarPath);
		String path = calendarPath;
		if (datePath != null) {
		    path += "/" + datePath;
		}
		// path += "/" + CollaborationUtils.normalizeForPath(name);
		String firstOcID = CollaborationUtils
			.getFirstElement(occurences);
		path += "/" + firstOcID;
		path = PathHelper.normalize(path);
		logIt("Path " + path);
		// Save the entity
		CalendarItem event = new CalendarItem();
		event.setId(firstOcID);
		event.setSeriesId(seriesID);
		event.setName(calendarDetails.getName());
		event.setDate(calendarDetails.getDate());
		event.setType(CollaborationUtils.CALENDAR_EVENT);
		event.setResourcePath(path);
		event.setRecurrence(calendarDetails.getRecurrence());
		event.setTimes(calendarDetails.getTimes());
		// save it
		em.persist(event);
		// Bind the event with the path
		binding.bind(event.getFactoryResourceIdentifier(), path);
		binding.setProperty(path,
			FactoryResourceProperty.CREATION_TIMESTAMP, ""
				+ System.currentTimeMillis());
		binding.setProperty(path,
			FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, ""
				+ System.currentTimeMillis());
		binding.setProperty(path, FactoryResourceProperty.AUTHOR,
			caller);
		// Create policy (owner)
		String policyId = UUID.randomUUID().toString();
		pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(
			policyId, caller, path));
		binding
			.setProperty(path, FactoryResourceProperty.OWNER,
				caller);
		binding.setProperty(path, FactoryResourceProperty.POLICY_ID,
			policyId);
		// Notify for the creation
		notification.throwEvent(new Event(path, caller,
			CalendarItem.RESOURCE_NAME, Event.buildEventType(
				CalendarService.SERVICE_NAME,
				CalendarItem.RESOURCE_NAME, "create"), ""));
		logIt(event.toString());
		paths = new String[1];
		paths[0] = path;
	    }
	    return paths;
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to create the event at path " + calendarPath,
		    e);
	    throw new CalendarServiceException(
		    "Unable to create the event at path " + calendarPath, e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.calendar.CalendarService#readEvent
     * (java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public CalendarItem readEvent(String path) throws CalendarServiceException {
	CalendarItem event = null;
	logIt("Read event " + path);
	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new CalendarServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");

	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, CalendarItem.RESOURCE_NAME);
	    // Find the entity
	    event = em.find(CalendarItem.class, identifier.getId());
	    if (event == null) {
		throw new CalendarServiceException(
			"unable to find a event for id " + identifier.getId());
	    }
	    logIt("Found entity with id" + event.getId() + " Series id "
		    + event.getSeriesId());
	    // Call the WS to retrieve values that are not stored in
	    HashMap<String, Object> values = calendarWS.readEvent(
		    event.getId(), event.getSeriesId(), "occurence");
	    if (values != null && !values.isEmpty()) {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)
			|| values.get("calendar-item") == null) {
		    throw new CalendarServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}
		CalendarDTO eventDTO = (CalendarDTO) values
			.get("calendar-item");
		if (eventDTO != null) {
		    // we set the values not persisted in the factory
		    // We don't persist ocIds,location,start/end time,contact
		    // details
		    event.setOccurenceIds(eventDTO.getOccurenceIds());
		    event.setLocation(eventDTO.getLocation());
		    event.setStartTime(eventDTO.getStartTime());
		    event.setEndTime(eventDTO.getEndTime());
		    event.setContactName(eventDTO.getContactName());
		    event.setContactEmail(eventDTO.getContactEmail());
		    event.setContactPhone(eventDTO.getContactPhone());
		    if(eventDTO.getAttachments()!=null){
			event.setDocumentIds(eventDTO.getAttachments());
		    }
		    if(eventDTO.getForum()!=null){
			event.setForumId(eventDTO.getForum());
		    }
		}
	    } else {
		throw new CalendarServiceException(
			"Wrong response recieved from the WS.Check Logs");
	    }
	    // Notify
	    notification.throwEvent(new Event(path, caller,
		    CalendarItem.RESOURCE_NAME, Event.buildEventType(
			    CalendarService.SERVICE_NAME,
			    CalendarItem.RESOURCE_NAME, "read"), ""));
	    logIt(event.toString());
	    return event;

	} catch (Exception e) {
	    logger.error("Unable to read the event at path " + path, e);
	    throw new CalendarServiceException(
		    "Unable to read the event at path " + path, e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.calendar.CalendarService#updateEvent
     * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String updateEvent(String path, String name, String location,
	    String date, String startTime, String endTime, String contactName,
	    String contactEmail, String contactPhone)
	    throws CalendarServiceException {
	String updatedPath = path;
	logIt("updateEvent(...) called");
	logIt("params : path=" + path + ", name=" + name);
	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new CalendarServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "update");
	    // Check supplied values
	    CollaborationUtils
		    .checkCreateEventValues(name, location, date, startTime,
			    endTime, contactName, contactEmail, contactPhone);
	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, CalendarItem.RESOURCE_NAME);
	    // Find the entity
	    CalendarItem event = em
		    .find(CalendarItem.class, identifier.getId());
	    if (event == null) {
		throw new CalendarServiceException(
			"Unable to find a event for id " + identifier.getId());
	    }
	    boolean changedDate = false;
	    String oldPath = path;
	    String oldDate = event.getDate();
	    if (!date.equals(event.getDate())) {
		changedDate = true;
	    }
	    // Call the WebService
	    HashMap<String, String> values = calendarWS
		    .updateEvent(event.getId(), event.getSeriesId(), name,
			    location, date, startTime, endTime, contactName,
			    contactEmail, contactPhone);
	    if (values != null && !values.isEmpty()) {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
		    throw new CalendarServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}

		// Save the entity in the factory
		event.setName(name);
		event.setDate(date);
		// If changed date we need to set the path accordignly and
		// unbind old one.
		if (changedDate) {
		    String tempPath = swichDatePath(date, oldDate, path);
		    if (tempPath != null) {
			path = tempPath + "/" + event.getId();
			logIt("Switch from " + oldPath + " TO " + path);
		    }
		    logIt("Path: " + path);
		    event.setResourcePath(path);
		    em.merge(event);
		    updatedPath = path;
		    // Delete previous policy and unbind old path
		    String policyId = binding.getProperty(oldPath,
			    FactoryResourceProperty.POLICY_ID, false);
		    pap.deletePolicy(policyId);
		    binding.unbind(oldPath);
		    // Bind the event with the new path based on the new date
		    binding.bind(event.getFactoryResourceIdentifier(), path);
		    binding.setProperty(path,
			    FactoryResourceProperty.CREATION_TIMESTAMP, ""
				    + System.currentTimeMillis());
		    binding.setProperty(path,
			    FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, ""
				    + System.currentTimeMillis());
		    binding.setProperty(path, FactoryResourceProperty.AUTHOR,
			    caller);
		    // Create policy (owner)
		    policyId = UUID.randomUUID().toString();
		    pap.createPolicy(policyId, PAPServiceHelper
			    .buildOwnerPolicy(policyId, caller, path));
		    binding.setProperty(path, FactoryResourceProperty.OWNER,
			    caller);
		    binding.setProperty(path,
			    FactoryResourceProperty.POLICY_ID, policyId);
		    // Notify
		    notification.throwEvent(new Event(path, caller,
			    CalendarItem.RESOURCE_NAME, Event.buildEventType(
				    CalendarService.SERVICE_NAME,
				    CalendarItem.RESOURCE_NAME, "update"), ""));
		} else {
		    event.setResourcePath(path);
		    em.merge(event);
		    binding.setProperty(path,
			    FactoryResourceProperty.LAST_UPDATE_TIMESTAMP,
			    System.currentTimeMillis() + "");
		    notification.throwEvent(new Event(path, caller,
			    CalendarItem.RESOURCE_NAME, Event.buildEventType(
				    CalendarService.SERVICE_NAME,
				    CalendarItem.RESOURCE_NAME, "update"), ""));
		}
		logIt(event.toString());
	    } else {
		throw new CalendarServiceException(
			"No valid answer from the WS.Check logs.");
	    }
	    return updatedPath;
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to update the event at path " + path, e);
	    throw new CalendarServiceException(
		    "Unable to update the event at path " + path, e);
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.calendar.CalendarService#updateRecurrence
     * (java.lang.String, java.lang.String, java.lang.String, long)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String[] updateRecurrence(String path, String date,
	    String recurrence, long times) throws CalendarServiceException {
	String[] paths = null;
	logIt("updateEvent(...) called");
	logIt("params : path=" + path + ", reccurence=" + recurrence
		+ " times " + times);
	try {
	    // Check supplied values
	    if (!recurrence.equals(CollaborationUtils.REC_0)
		    && !recurrence.equals(CollaborationUtils.REC_1)) {
		recurrence = CollaborationUtils.REC_0;
		times = 1;
	    }
	    if (date == null || date == "") {
		throw new CalendarServiceException("Event's date is mandatory");
	    }
	    CalendarItem event = readEvent(path);
	    if (event == null) {
		throw new CalendarServiceException(
			"Unable to find a event for path " + path);
	    }
	    boolean changedDate = false;
	    boolean changedRec = false;
	    boolean changedTimes = false;
	    String oldRec = event.getRecurrence();
	    long oldTimes = event.getTimes();
	    if (!date.equals(event.getDate())) {
		changedDate = true;
	    }
	    if (oldTimes != times) {
		changedTimes = true;
	    }
	    if (!recurrence.equals(oldRec)) {
		changedRec = true;
	    }
	    // If we changed date only then call simpleUpdate.
	    // If we changed recurrence&times then we delete old and
	    // create new ones based on the given event(path)values
	    if (changedDate && !changedRec && event.getTimes() == 1) {
		logIt("No need to update the series. Proceed with simple update");

		String newPath = updateEvent(path, event.getName(), event
			.getLocation(), date, event.getStartTime(), event
			.getEndTime(), event.getContactName(), event
			.getContactEmail(), event.getContactPhone());
		paths = new String[0];
		paths[0] = newPath;
	    } else if (changedRec || changedTimes) {
		String calendarPath = getRootPath(path, event.getDate());

		paths = createEvent(calendarPath, event.getName(), event
			.getLocation(), date, event.getStartTime(), event
			.getEndTime(), event.getContactName(), event
			.getContactEmail(), event.getContactPhone(),
			recurrence, times);
		if (event.getOccurencePaths() != null) {
		    for (int i = 0; i < event.getOccurencePaths().length; i++) {
			deleteEvent(event.getOccurencePaths()[i]);
		    }
		} else {
		    deleteEvent(path);
		}
	    }
	    return paths;
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to update recurrence/date for event at path "
		    + path, e);
	    throw new CalendarServiceException(
		    "Unable to update recurrence/date for event at path "
			    + path, e);
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.qualipso.factory.collaboration.calendar.CalendarService#
     * atttachDocumentToEvent(java.lang.String, java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void atttachDocumentToEvent(String path, String[] documentPaths)
	    throws CalendarServiceException {
	logIt("attach document at " + path);
	try {
	    // Get event
	    CalendarItem event = readEvent(path);
	    if (event != null && documentPaths != null
		    && documentPaths.length > 0) {
		// Security check
		String caller = membership
			.getProfilePathForConnectedIdentifier();
		if (caller == null) {
		    throw new CalendarServiceException(
			    "Could not get connected profile");
		}
		logIt("caller: " + caller);
		pep.checkSecurity(caller, path, "update");
		// Get id(s) from provided paths
		String[] documentIds = new String[documentPaths.length];
		for (int i = 0; i < documentPaths.length; i++) {
		    Document doc = documentService
			    .readDocument(documentPaths[i]);
		    if (doc != null) {
			documentIds[i] = doc.getId();
		    } else {
			throw new CalendarServiceException(
				"Cannot read document from given path "
					+ documentPaths[i]);
		    }
		}
		// Call the mermigWS;
		HashMap<String, String> values = calendarWS
			.atttachDocumentToEvent(event.getId(), event
				.getSeriesId(),
				CollaborationUtils.CALENDAR_MODIFY_OC,
				documentIds);
		if (values != null && !values.isEmpty()) {
		    String code = (String) values.get("statusCode");
		    String msg = (String) values.get("statusMessage");
		    logIt("Message Code:" + code + " Message: " + msg);
		    if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
			throw new CalendarServiceException(
				"Error code recieved from the WS." + " Code"
					+ code + " Message:" + msg);
		    }
		    // Now update the event to persist the attachments
		    event.setDocumentPaths(documentPaths);
		    em.merge(event);
		    binding.setProperty(path,
			    FactoryResourceProperty.LAST_UPDATE_TIMESTAMP,
			    System.currentTimeMillis() + "");
		    notification.throwEvent(new Event(path, caller,
			    CalendarItem.RESOURCE_NAME, Event.buildEventType(
				    CalendarService.SERVICE_NAME,
				    CalendarItem.RESOURCE_NAME, "update"), ""));
		} else {
		    throw new CalendarServiceException(
			    "No valid answer from the WS.Check logs.");
		}
	    } else {
		throw new CalendarServiceException(
			"Cannot read event from given path " + path);
	    }

	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to attach document for the event " + path, e);
	    throw new CalendarServiceException(
		    "Unable to attach document for the event " + path, e);
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.qualipso.factory.collaboration.calendar.CalendarService#
     * attachForumToEvent(java.lang.String, java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void attachForumToEvent(String path, String forumPath)
	    throws CalendarServiceException {
	logIt("attach forum " + forumPath + " at " + path);
	try {
	    // Get event
	    CalendarItem event = readEvent(path);
	    if (event != null) {
		// Security check
		String caller = membership
			.getProfilePathForConnectedIdentifier();
		if (caller == null) {
		    throw new CalendarServiceException(
			    "Could not get connected profile");
		}
		logIt("caller: " + caller);
		pep.checkSecurity(caller, path, "update");
		Forum forum = forumService.readForum(forumPath);
		if (forum != null) {
		    // Call the mermigWS;
		    HashMap<String, String> values = calendarWS
			    .atttachForumToEvent(event.getId(), event
				    .getSeriesId(),
				    CollaborationUtils.CALENDAR_MODIFY_OC,
				    forum.getId());
		    if (values != null && !values.isEmpty()) {
			String code = (String) values.get("statusCode");
			String msg = (String) values.get("statusMessage");
			logIt("Message Code:" + code + " Message: " + msg);
			if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
			    throw new CalendarServiceException(
				    "Error code recieved from the WS."
					    + " Code" + code + " Message:"
					    + msg);
			}
			// Now update the event to persist the attachments
			event.setForum(forumPath);
			em.merge(event);
			binding.setProperty(path,
				FactoryResourceProperty.LAST_UPDATE_TIMESTAMP,
				System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller,
				CalendarItem.RESOURCE_NAME, Event
					.buildEventType(
						CalendarService.SERVICE_NAME,
						CalendarItem.RESOURCE_NAME,
						"update"), ""));
		    } else {
			throw new CalendarServiceException(
				"No valid answer from the WS.Check logs.");
		    }
		} else {
		    throw new CalendarServiceException(
			    "Cannot read forum from given path " + forumPath);
		}
	    } else {
		throw new CalendarServiceException(
			"Cannot read event from given path " + path);
	    }

	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to attach document for the event " + path, e);
	    throw new CalendarServiceException(
		    "Unable to attach document for the event " + path, e);
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.calendar.CalendarService#deleteEvent
     * (java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteEvent(String path) throws CalendarServiceException {
	logIt("delete event " + path);
	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new CalendarServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "delete");
	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, CalendarItem.RESOURCE_NAME);
	    // Find the entity
	    CalendarItem event = em
		    .find(CalendarItem.class, identifier.getId());
	    if (event == null) {
		throw new DocumentServiceException(
			"unable to find a event for id " + identifier.getId());
	    }
	    logIt("Call WS to delete event with id: " + event.getId());
	    HashMap<String, String> values = calendarWS.deleteEvent(event
		    .getId());
	    if (values != null && !values.isEmpty()) {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE)) {
		    throw new CalendarServiceException(
			    "Error code recieved from the WS." + " Code" + code
				    + " Message:" + msg);
		}
		em.remove(event);
		// Delete the policy and unbind the resource from this path
		String policyId = binding.getProperty(path,
			FactoryResourceProperty.POLICY_ID, false);
		pap.deletePolicy(policyId);
		binding.unbind(path);
		// notify
		notification.throwEvent(new Event(path, caller,
			CalendarItem.RESOURCE_NAME, Event.buildEventType(
				CalendarService.SERVICE_NAME,
				CalendarItem.RESOURCE_NAME, "delete"), ""));
	    } else {
		throw new CalendarServiceException(
			"No valid answer from the WS.Check logs.");
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to delete the event at path " + path, e);
	    throw new CalendarServiceException(
		    "Unable to delete the event at path " + path, e);
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.calendar.CalendarService#getCalendarItems
     * (java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public CalendarItem[] getCalendarItems(String path)
	    throws CalendarServiceException {
	CalendarItem[] items = null;
	logIt("get getCalendarItems " + path);
	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new CalendarServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    if (identifier != null) {
		String servicePattern = CalendarService.SERVICE_NAME;
		String typePattern = CalendarItem.RESOURCE_NAME;
		String[] itemsArray = browser.listChildrenOfType(path,
			servicePattern, typePattern);
		if (itemsArray != null && itemsArray.length > 0) {
		    items = new CalendarItem[itemsArray.length];
		    for (int i = 0; i < itemsArray.length; i++) {
			logIt("child #" + i + ". " + itemsArray[i]);
			items[i] = readEvent(itemsArray[i]);
			logIt(items[i].toString());
		    }
		}
	    } else {
		throw new CalendarServiceException("Invalid path " + path);
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to retrieve the events at path " + path, e);
	    throw new CalendarServiceException(
		    "Unable to retrieve the events at path " + path, e);
	}
	return items;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.qualipso.factory.collaboration.calendar.CalendarService#
     * getCalendarItemsForMonth(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public CalendarItem[] getCalendarItemsForMonth(String path, String year,
	    String month) throws CalendarServiceException {
	CalendarItem[] items = null;
	logIt("get getCalendarItemsForMonth " + path);
	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new CalendarServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    // look up to check if path is invalid
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    if (identifier != null) {
		// We save calendar times in a folder structure like
		// <path>/year/month/day/id
		// Check that year exists.
		String yearPath = path + "/" + year;
		Folder yearFolder = core.readFolder(yearPath);
		if (yearFolder != null) {
		    String monthPath = yearPath + "/" + month;
		    Folder monthFolder = core.readFolder(monthPath);
		    if (monthFolder != null) {
			// Retrieve month subfolders e.g. days.
			String servicePattern = CoreService.SERVICE_NAME;
			String typePattern = Folder.RESOURCE_NAME;
			String[] itemsArray = browser.listChildrenOfType(
				monthPath, servicePattern, typePattern);
			if (itemsArray != null && itemsArray.length > 0) {
			    Vector<CalendarItem> cItemVector = new Vector<CalendarItem>();
			    for (int i = 0; i < itemsArray.length; i++) {
				logIt("get events of day #" + i + " "
					+ itemsArray[i]);
				servicePattern = CalendarService.SERVICE_NAME;
				typePattern = CalendarItem.RESOURCE_NAME;
				String[] eventsArray = browser
					.listChildrenOfType(itemsArray[i],
						servicePattern, typePattern);
				if (eventsArray != null
					&& eventsArray.length > 0) {
				    for (int j = 0; j < eventsArray.length; j++) {
					logIt("Found " + eventsArray[j]);
					cItemVector
						.add(readEvent(eventsArray[j]));
				    }
				}
			    }
			    if (cItemVector != null && cItemVector.size() > 0) {
				items = cItemVector
					.toArray(new CalendarItem[cItemVector
						.size()]);
			    }
			} else {
			    logIt("No events found for given " + monthPath);
			}
		    } else {
			logIt("No events found for given " + monthPath);
		    }
		} else {
		    logIt("No events found for given " + yearPath);
		}
	    } else {
		throw new CalendarServiceException("Invalid path " + path);
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to retrieve the month's events at path "
		    + path, e);
	    throw new CalendarServiceException(
		    "Unable to retrieve the month's  events at path " + path, e);
	}
	return items;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.qualipso.factory.collaboration.calendar.CalendarService#
     * getEventAttachments(java.lang.String)
     */
    @Override
    public Document[] getEventAttachments(String path)
	    throws CalendarServiceException {
	logIt("get attached documents " + path);
	Document[] listDoc = null;
	try {
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new CalendarServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    // Check if given path is valid CalendarItem
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, CalendarItem.RESOURCE_NAME);
	    // Get attachments
	    String servicePattern = DocumentService.SERVICE_NAME;
	    String typePattern = Document.RESOURCE_NAME;
	    String[] docsArray = browser.listChildrenOfType(path,
		    servicePattern, typePattern);
	    if (docsArray != null && docsArray.length > 0) {
		Vector<Document> docVector = new Vector<Document>();

		for (int i = 0; i < docsArray.length; i++) {

		    logIt("child #" + i + ". " + docsArray[i]);
		    Document doc = documentService
			    .readDocumentProperties(docsArray[i]);
		    if (doc != null) {
			docVector.add(doc);
		    }
		}
		listDoc = docVector.toArray(new Document[docVector.size()]);
	    }
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error(
		    "Unable to retrieve attached documents for the event at path "
			    + path, e);
	    throw new CalendarServiceException(
		    "Unable to retrieve documents for the event at path "
			    + path, e);
	}
	return listDoc;
    }

    /**
     * Gets the entity manager.
     * 
     * @return the entity manager
     */
    public EntityManager getEntityManager() {
	return this.em;
    }

    /**
     * Sets the entity manager.
     * 
     * @param em
     *            the new entity manager
     */
    @PersistenceContext(unitName = "CalendarServiceBean")
    public void setEntityManager(EntityManager em) {
	this.em = em;
    }

    /**
     * Gets the session context.
     * 
     * @return the session context
     */
    public SessionContext getSessionContext() {
	return this.ctx;
    }

    /**
     * Sets the session context.
     * 
     * @param ctx
     *            the new session context
     */
    @Resource
    public void setSessionContext(SessionContext ctx) {
	this.ctx = ctx;
    }

    // @EJB(name = "BindingService")
    /**
     * Sets the binding service.
     * 
     * @param binding
     *            the new binding service
     */
    @EJB
    public void setBindingService(BindingService binding) {
	this.binding = binding;
    }

    /**
     * Gets the binding service.
     * 
     * @return the binding service
     */
    public BindingService getBindingService() {
	return this.binding;
    }

    /**
     * Gets the pEP service.
     * 
     * @return the pEP service
     */
    public PEPService getPEPService() {
	return this.pep;
    }

    // @EJB(name = "PEPService")
    /**
     * Sets the pEP service.
     * 
     * @param pep
     *            the new pEP service
     */
    @EJB
    public void setPEPService(PEPService pep) {
	this.pep = pep;
    }

    /**
     * Gets the pAP service.
     * 
     * @return the pAP service
     */
    public PAPService getPAPService() {
	return this.pap;
    }

    // @EJB(name = "PAPService")
    /**
     * Sets the pAP service.
     * 
     * @param pap
     *            the new pAP service
     */
    @EJB
    public void setPAPService(PAPService pap) {
	this.pap = pap;
    }

    /**
     * Gets the notification service.
     * 
     * @return the notification service
     */
    public NotificationService getNotificationService() {
	return this.notification;
    }

    // @EJB(name = "NotificationService")
    /**
     * Sets the notification service.
     * 
     * @param notification
     *            the new notification service
     */
    @EJB
    public void setNotificationService(NotificationService notification) {
	this.notification = notification;
    }

    /**
     * Gets the membership service.
     * 
     * @return the membership service
     */
    public MembershipService getMembershipService() {
	return this.membership;
    }

    // @EJB(name = "MembershipService")
    /**
     * Sets the membership service.
     * 
     * @param membership
     *            the new membership service
     */
    @EJB
    public void setMembershipService(MembershipService membership) {
	this.membership = membership;
    }

    /**
     * Gets the browser.
     * 
     * @return the browser
     */
    public BrowserService getBrowser() {
	return browser;
    }

    // @EJB(name = "BrowserService")
    /**
     * Sets the browser.
     * 
     * @param browser
     *            the new browser
     */
    @EJB
    public void setBrowser(BrowserService browser) {
	this.browser = browser;
    }

    /**
     * Gets the core.
     * 
     * @return the core
     */
    public CoreService getCore() {
	return core;
    }

    // @EJB(name = "CoreService")
    /**
     * Sets the core.
     * 
     * @param core
     *            the new core
     */
    @EJB
    public void setCore(CoreService core) {
	this.core = core;
    }

    /**
     * Gets the calendar ws.
     * 
     * @return the calendar ws
     */
    public CalendarWService getCalendarWS() {
	return calendarWS;
    }

    /**
     * Sets the calendar ws.
     * 
     * @param calendarWS
     *            the new calendar ws
     */
    @EJB(name = "CalendarWService")
    public void setCalendarWS(CalendarWService calendarWS) {
	this.calendarWS = calendarWS;
    }

    /**
     * Gets the document service.
     * 
     * @return the document service
     */
    public DocumentService getDocumentService() {
	return documentService;
    }

    // @EJB(name = "DocumentService")
    /**
     * Sets the document service.
     * 
     * @param documentService
     *            the new document service
     */
    @EJB
    public void setDocumentService(DocumentService documentService) {
	this.documentService = documentService;
    }

    public ForumService getForumService() {
	return forumService;
    }

    @EJB
    public void setForumService(ForumService forumService) {
	this.forumService = forumService;
    }

    /**
     * Check resource type.
     * 
     * @param identifier
     *            the identifier
     * @param resourceType
     *            the resource type
     * 
     * @throws MembershipServiceException
     *             the membership service exception
     */
    private void checkResourceType(FactoryResourceIdentifier identifier,
	    String resourceType) throws MembershipServiceException {

	if (!identifier.getService().equals(getServiceName())) {
	    throw new MembershipServiceException("resource identifier "
		    + identifier + " does not refer to service "
		    + getServiceName());
	}
	if (!identifier.getType().equals(resourceType)) {
	    throw new MembershipServiceException("resource identifier "
		    + identifier + " does not refer to a resource of type "
		    + resourceType);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.qualipso.factory.FactoryService#getResourceTypeList()
     */
    @Override
    public String[] getResourceTypeList() {
	return RESOURCE_TYPE_LIST;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.qualipso.factory.FactoryService#getServiceName()
     */
    @Override
    public String getServiceName() {
	return SERVICE_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.qualipso.factory.FactoryService#findResource(java.lang.String)
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FactoryResource findResource(String path) throws FactoryException {
	logIt("findResource(...) called");
	logIt("params : path=" + path);

	try {
	    FactoryResourceIdentifier identifier = binding.lookup(path);

	    if (!identifier.getService().equals(SERVICE_NAME)) {
		throw new CoreServiceException("Resource " + identifier
			+ " is not managed by " + SERVICE_NAME);
	    }

	    if (identifier.getType().equals(CalendarItem.RESOURCE_NAME)) {
		return readEvent(path);
	    }

	    throw new CoreServiceException("Resource " + identifier
		    + " is not managed by " + SERVICE_NAME);

	} catch (Exception e) {
	    logger.error("unable to find the resource at path " + path, e);
	    throw new CoreServiceException(
		    "unable to find the resource at path " + path, e);
	}
    }

    /**
     * Log it.
     * 
     * @param message
     *            the message
     */
    private void logIt(String message) {
	if (logger.isInfoEnabled()) {
	    logger.info(message);
	}
    }

    /**
     * Gets the next day.
     * 
     * @param date
     *            the date
     * @param index
     *            the index
     * 
     * @return the next day
     * 
     * @throws CalendarServiceException
     *             the calendar service exception
     */
    private String getNextDay(String date, int index)
	    throws CalendarServiceException {
	String newDate = date;
	try {
	    if (index > 0) {
		// FIXME It only works with "daily" reccurence
		int MILLIS = 1000 * 60 * 60 * 24 * index;
		DateFormat wsDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date eventDate = wsDateFormat.parse(date);
		newDate = wsDateFormat.format(eventDate.getTime() + MILLIS);
		logIt("Next day " + newDate);
	    }
	} catch (Exception e) {
	    throw new CalendarServiceException(
		    "Error in retrieving the next day. Reason: "
			    + e.getMessage());
	}
	return newDate;
    }

    /**
     * Save folder path.
     * 
     * @param path
     *            the path
     * @param name
     *            the name
     * @param description
     *            the description
     * 
     * @throws CalendarServiceException
     *             the calendar service exception
     */
    private void saveFolderPath(String path, String name, String description)
	    throws CalendarServiceException {

	try {
	    core.readFolder(path);
	    logIt("Path " + path + " allready exitst");
	} catch (CoreServiceException e) {
	    try {
		core.createFolder(path, name, description);
		logIt("Crated Path " + path + " succesfully");
	    } catch (CoreServiceException e2) {
		throw new CalendarServiceException(
			"Unable to create date path." + path);
	    }
	}

    }

    /**
     * Creates the oc paths.
     * 
     * @param occurences
     *            the occurences
     * @param date
     *            the date
     * @param calendarPath
     *            the calendar path
     * 
     * @return the string[]
     * 
     * @throws CalendarServiceException
     *             the calendar service exception
     */
    private String[] createOcPaths(HashMap<String, String> occurences,
	    String date, String calendarPath) throws CalendarServiceException {
	String[] occurencePaths = new String[occurences.size()];
	Iterator iterator = occurences.entrySet().iterator();
	int cnt = 0;
	while (iterator.hasNext()) {
	    String newDate = getNextDay(date, cnt);
	    logIt("Next day " + newDate);
	    String datePath = createDatePath(newDate, calendarPath);
	    String path = calendarPath;
	    if (datePath != null) {
		path += "/" + datePath;
	    }
	    Map.Entry entry = (Map.Entry) iterator.next();
	    String ocID = (String) entry.getValue();
	    String ocPath = path + "/" + ocID;
	    occurencePaths[cnt] = ocPath;
	    cnt++;
	}
	return occurencePaths;
    }

    /**
     * Creates the date path.
     * 
     * @param date
     *            the date
     * @param calendarPath
     *            the calendar path
     * 
     * @return the string
     * 
     * @throws CalendarServiceException
     *             the calendar service exception
     */
    private String createDatePath(String date, String calendarPath)
	    throws CalendarServiceException {
	String fdatePath = null;
	logIt("Parse date " + date + " as yyyy-MM-dd fortmat");
	int[] dArray = parseDate(date);
	int year = dArray[0];
	int month = dArray[1] + 1;
	int day = dArray[2];

	// skip the 'root' path We "assume" it is there.It should be. :)
	// saveFolderPath(calendarPath, "calendar", "calendar folder");
	//
	saveFolderPath(calendarPath + "/" + year, String.valueOf(year),
		"calendar folder for year " + year);
	saveFolderPath(calendarPath + "/" + year + "/" + month, String
		.valueOf(month), "calendar folder for year " + year
		+ " and month" + month);
	saveFolderPath(calendarPath + "/" + year + "/" + month + "/" + day,
		String.valueOf(day), "calendar folder for year " + year
			+ " and month" + month + " and day " + day);
	fdatePath = new String(year + "/" + month + "/" + day);
	return fdatePath;
    }

    /**
     * Gets the root path.
     * 
     * @param path
     *            the path
     * @param date
     *            the date
     * 
     * @return the root path
     * 
     * @throws CalendarServiceException
     *             the calendar service exception
     */
    private String getRootPath(String path, String date)
	    throws CalendarServiceException {
	int[] dArray = parseDate(date);
	String tempPath = new String("/" + dArray[0] + "/" + (dArray[1] + 1)
		+ "/" + dArray[2]);
	int index = path.indexOf(tempPath);
	String parentPath = "";
	if (index > 0) {
	    parentPath = path.substring(0, index);
	}

	return parentPath;
    }

    /**
     * Swich date path.
     * 
     * @param date
     *            the date
     * @param oldDate
     *            the old date
     * @param eventPath
     *            the event path
     * 
     * @return the string
     * 
     * @throws CalendarServiceException
     *             the calendar service exception
     */
    private String swichDatePath(String date, String oldDate, String eventPath)
	    throws CalendarServiceException {
	String fdatePath = null;

	int[] dArray = parseDate(date);
	int year = dArray[0];
	int month = dArray[1] + 1;
	int day = dArray[2];
	String parentPath = getRootPath(eventPath, oldDate);

	if (!parentPath.equals("")) {
	    saveFolderPath(parentPath, "calendar", "calendar folder");
	    saveFolderPath(parentPath + "/" + year, String.valueOf(year),
		    "calendar folder for year " + year);
	    saveFolderPath(parentPath + "/" + year + "/" + month, String
		    .valueOf(month), "calendar folder for year " + year
		    + " and month" + month);
	    saveFolderPath(parentPath + "/" + year + "/" + month + "/" + day,
		    String.valueOf(day), "calendar folder for year " + year
			    + " and month" + month + " and day " + day);
	    fdatePath = parentPath + "/" + year + "/" + month + "/" + day;
	} else {
	    throw new CalendarServiceException("Error in switching dates.");
	}

	return fdatePath;
    }

    /**
     * Parses the date.
     * 
     * @param date
     *            the date
     * 
     * @return the int[]
     * 
     * @throws CalendarServiceException
     *             the calendar service exception
     */
    private int[] parseDate(String date) throws CalendarServiceException {
	int[] dArray = new int[3];
	try {
	    logIt("Parse date " + date + " as yyyy-MM-dd format");
	    DateFormat wsDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    Date eventDate = wsDateFormat.parse(date);
	    Calendar eventCalendar = new GregorianCalendar();
	    eventCalendar.setTime(eventDate);
	    dArray[0] = eventCalendar.get(Calendar.YEAR);
	    dArray[1] = eventCalendar.get(Calendar.MONTH);
	    dArray[2] = eventCalendar.get(Calendar.DATE);
	} catch (ParseException e) {
	    logger
		    .warn("Given date "
			    + date
			    + " is not on yyyy-MM-dd fortmat. Trying to parse as dd/MM/yyyy");
	    try {
		DateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date eventDate = simpleDateFormat.parse(date);
		Calendar eventCalendar = new GregorianCalendar();
		eventCalendar.setTime(eventDate);
		dArray[0] = eventCalendar.get(Calendar.YEAR);
		dArray[1] = eventCalendar.get(Calendar.MONTH) + 1;
		dArray[2] = eventCalendar.get(Calendar.DATE);
	    } catch (Exception e2) {
		throw new CalendarServiceException("Date format exception "
			+ date);
	    }
	}
	return dArray;
    }

    /**
     * Convert2 dto.
     * 
     * @param cDetails
     *            the c details
     * 
     * @return the calendar dto
     */
    private CalendarDTO convert2DTO(CalendarDetails cDetails) {
	CalendarDTO dto = null;
	if (cDetails != null) {
	    dto = new CalendarDTO();
	    dto.setName(cDetails.getName());
	    dto.setLocation(cDetails.getLocation());
	    dto.setDate(cDetails.getDate());
	    dto.setStartTime(cDetails.getStartTime());
	    dto.setEndTime(cDetails.getEndTime());
	    //
	    dto.setContactEmail(cDetails.getContactEmail());
	    dto.setContactName(cDetails.getContactName());
	    dto.setContactPhone(cDetails.getContactPhone());
	    //
	    dto.setRecurrence(cDetails.getRecurrence());
	    dto.setTimes(cDetails.getTimes());
	}
	return dto;
    }

}
