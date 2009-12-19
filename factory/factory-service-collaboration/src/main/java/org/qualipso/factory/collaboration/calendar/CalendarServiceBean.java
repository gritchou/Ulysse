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
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathAlreadyBoundException;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.browser.BrowserService;
import org.qualipso.factory.collaboration.beans.AttachmentDetails;
import org.qualipso.factory.collaboration.beans.CalendarDetails;
import org.qualipso.factory.collaboration.beans.CalendarEvent;
import org.qualipso.factory.collaboration.beans.CalendarExtra;
import org.qualipso.factory.collaboration.beans.CalendarListItem;
import org.qualipso.factory.collaboration.beans.ParticipantDetails;
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
import org.qualipso.factory.core.entity.Link;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.membership.entity.Group;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.AccessDeniedException;
import org.qualipso.factory.security.pep.PEPService;

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

    private static Log logger = LogFactory.getLog(CalendarServiceBean.class);

    private BindingService binding;
    private PEPService pep;
    private PAPService pap;
    private NotificationService notification;
    private MembershipService membership;
    private BrowserService browser;
    private CoreService core;
    private SessionContext ctx;

    private EntityManager em;

    private CalendarWService calendarWS;
    private DocumentService documentService;
    private ForumService forumService;

    public CalendarServiceBean() {

    }

    @SuppressWarnings("unchecked")
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String[] createEvent(String calendarPath,
	    CalendarDetails calendarDetails) throws CalendarServiceException {
	return createEventWithAttachments(calendarPath, calendarDetails, null,
		null);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String[] createEventWithAttachments(String calendarPath,
	    CalendarDetails calendarDetails, String[] documentPaths,
	    String forumPath) throws CalendarServiceException {
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
	    AttachmentDetails forumAtt = null;
	    AttachmentDetails[] attdetArray = null;
	    // Get id(s) of documents binded with given paths
	    if (documentPaths != null && documentPaths.length > 0) {
		HashMap<String, String> attachments = new HashMap<String, String>();
		attdetArray = new AttachmentDetails[documentPaths.length];
		for (int i = 0; i < documentPaths.length; i++) {
		    if (documentPaths[i] != null
			    && !documentPaths[i].equals("")) {
			Document doc = documentService
				.readDocumentProperties(documentPaths[i]);
			if (doc != null) {
			    attachments.put(doc.getId(), doc.getResourceId());
			    AttachmentDetails attachment = new AttachmentDetails();
			    attachment.setId(doc.getId());
			    attachment.setName(doc.getName());
			    attachment.setPath(documentPaths[i]);
			    attachment.setResourceId(doc.getResourceId());
			    attdetArray[i] = attachment;
			} else {
			    throw new CalendarServiceException(
				    "Cannot read document from given path "
					    + documentPaths[i]);
			}
		    } else {
			throw new CalendarServiceException(
				"Invalid document path to attach.");
		    }
		}
		dto.setAttachments(attachments);
	    }

	    if (forumPath != null) {
		Forum forum = forumService.readForumProperties(forumPath);
		if (forum != null) {
		    forumAtt = new AttachmentDetails();
		    forumAtt.setId(forum.getId());
		    forumAtt.setName(forum.getName());
		    forumAtt.setType("forum");
		    forumAtt.setPath(forumPath);
		    //
		    dto.setForum(forum.getId());
		} else {
		    throw new CalendarServiceException(
			    "Cannot read forum from given path " + forumPath);
		}
	    }
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
		    // attachment handling
		    if (attdetArray != null && attdetArray.length > 0) {
			event.setAttachments(attdetArray);
		    }
		    if (forumAtt != null) {
			event.setForum(forumAtt);
		    }
		    // Set as participant the creator
		    ParticipantDetails[] partDetails = new ParticipantDetails[1];
		    partDetails[0] = new ParticipantDetails();
		    partDetails[0].setPath(ocPath);
		    partDetails[0].setProfile(caller);
		    event.setParticipants(partDetails);
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
		// Set as participant the creator
		ParticipantDetails[] partDetails = new ParticipantDetails[1];
		partDetails[0] = new ParticipantDetails();
		partDetails[0].setPath(path);
		partDetails[0].setProfile(caller);
		event.setParticipants(partDetails);
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
	    // TODO Call the email service to notify all invited users.
	    return paths;
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to create the event at path " + calendarPath
		    + ". Reason: " + e.getMessage(), e);
	    throw new CalendarServiceException(
		    "Unable to create the event at path " + calendarPath
			    + ". Reason: " + e.getMessage(), e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public CalendarItem readEvent(String path) throws CalendarServiceException {
	CalendarItem event = null;
	logIt("Read event " + path);
	try {
	    if (path != null) {
		// Security check
		String caller = membership
			.getProfilePathForConnectedIdentifier();
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
			    "unable to find a event for id "
				    + identifier.getId());
		}
		logIt("Found entity with id" + event.getId() + " Series id "
			+ event.getSeriesId());
		// Call the WS to retrieve values that are not stored in
		HashMap<String, Object> values = calendarWS.readEvent(event
			.getId(), event.getSeriesId(), "occurence");
		if (values != null && !values.isEmpty()) {
		    String code = (String) values.get("statusCode");
		    String msg = (String) values.get("statusMessage");
		    logIt("Message Code:" + code + " Message: " + msg);
		    if (!code.equals(CollaborationUtils.SUCCESS_CODE)
			    || values.get("calendar-item") == null) {
			throw new CalendarServiceException(
				"Error code recieved from the WS." + " Code"
					+ code + " Message:" + msg);
		    }
		    CalendarDTO eventDTO = (CalendarDTO) values
			    .get("calendar-item");
		    if (eventDTO != null) {
			// we set the values not persisted in the factory
			// We don't persist ocIds,location,start/end
			// time,contact
			// details
			event.setOccurenceIds(eventDTO.getOccurenceIds());
			event.setLocation(eventDTO.getLocation());
			event.setStartTime(eventDTO.getStartTime());
			event.setEndTime(eventDTO.getEndTime());
			event.setContactName(eventDTO.getContactName());
			event.setContactEmail(eventDTO.getContactEmail());
			event.setContactPhone(eventDTO.getContactPhone());
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
	    } else {
		throw new CalendarServiceException("No valid path for event.");
	    }
	    return event;
	} catch (Exception e) {
	    logger.error("Unable to read the event at path " + path, e);
	    throw new CalendarServiceException(
		    "Unable to read the event at path " + path, e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String updateEvent(String path, CalendarEvent calendarEvent)
	    throws CalendarServiceException {
	String updatedPath = path;
	logIt("updateEvent(...) called");
	logIt("params : path=" + path);
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
		    .checkCreateEventValues(calendarEvent.getName(),
			    calendarEvent.getLocation(), calendarEvent
				    .getDate(), calendarEvent.getStartTime(),
			    calendarEvent.getEndTime(), calendarEvent
				    .getContactName(), calendarEvent
				    .getContactEmail(), calendarEvent
				    .getContactPhone());
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
	    if (!calendarEvent.getDate().equals(event.getDate())) {
		changedDate = true;
	    }
	    // Call the WebService
	    HashMap<String, String> values = calendarWS
		    .updateEvent(event.getId(), event.getSeriesId(),
			    calendarEvent.getName(), calendarEvent
				    .getLocation(), calendarEvent.getDate(),
			    calendarEvent.getStartTime(), calendarEvent
				    .getEndTime(), calendarEvent
				    .getContactName(), calendarEvent
				    .getContactEmail(), calendarEvent
				    .getContactPhone());
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
		event.setName(calendarEvent.getName());
		event.setDate(calendarEvent.getDate());
		// If changed date we need to set the path accordignly and
		// unbind old one.
		if (changedDate) {
		    String tempPath = switchDatePath(calendarEvent.getDate(),
			    oldDate, path);
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
		event.setDate(date);
		String newPath = updateEvent(path, convert2CalendarEvent(event));
		paths = new String[0];
		paths[0] = newPath;
	    } else if (changedRec || changedTimes) {
		String calendarPath = getRootPath(path, event.getDate());
		CalendarDetails calDt = new CalendarDetails();
		calDt.setName(event.getName());
		calDt.setLocation(event.getLocation());
		calDt.setDate(date);
		calDt.setStartTime(event.getStartTime());
		calDt.setEndTime(event.getEndTime());
		calDt.setContactName(event.getContactName());
		calDt.setContactEmail(event.getContactEmail());
		calDt.setContactPhone(event.getContactPhone());
		calDt.setRecurrence(recurrence);
		calDt.setTimes(times);
		paths = createEvent(calendarPath, calDt);
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
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String updateEventExtra(String path, CalendarEvent calendarEvent,
	    CalendarExtra extraDetails) throws CalendarServiceException {
	String updatedPath = path;
	logIt("updateEventExtra(...) called");
	logIt("params : path=" + path);
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
		    .checkCreateEventValues(calendarEvent.getName(),
			    calendarEvent.getLocation(), calendarEvent
				    .getDate(), calendarEvent.getStartTime(),
			    calendarEvent.getEndTime(), calendarEvent
				    .getContactName(), calendarEvent
				    .getContactEmail(), calendarEvent
				    .getContactPhone());
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
	    if (!calendarEvent.getDate().equals(event.getDate())) {
		changedDate = true;
	    }
	    //
	    HashMap<String, String> attachments = null;
	    AttachmentDetails[] documentsAtt = null;
	    String forumId = null;
	    AttachmentDetails forumAtt = null;
	    ParticipantDetails[] partDetails = null;
	    Group[] groups = null;
	    if (extraDetails != null) {
		if (extraDetails.getAttachments() != null) {

		    String[] documentPaths = extraDetails.getAttachments();
		    documentPaths = removeDuplicates(documentPaths);
		    attachments = new HashMap<String, String>();
		    documentsAtt = new AttachmentDetails[documentPaths.length];
		    for (int i = 0; i < documentPaths.length; i++) {
			if (documentPaths[i] != null
				&& !documentPaths[i].equals("")) {
			    Document doc = documentService
				    .readDocumentProperties(documentPaths[i]);
			    if (doc != null) {
				attachments.put(doc.getId(), doc
					.getResourceId());
				AttachmentDetails attachment = new AttachmentDetails();
				attachment.setId(doc.getId());
				attachment.setName(doc.getName());
				attachment.setPath(documentPaths[i]);
				attachment.setResourceId(doc.getResourceId());
				documentsAtt[i] = attachment;
			    } else {
				throw new CalendarServiceException(
					"Cannot read document from given path "
						+ documentPaths[i]);
			    }
			} else {
			    throw new CalendarServiceException(
				    "Invalid document path to attach.");
			}
		    }
		}
		if (extraDetails.getForum() != null) {
		    Forum forum = forumService.readForumProperties(extraDetails
			    .getForum());
		    if (forum != null) {
			forumId = forum.getId();
			forumAtt = new AttachmentDetails();
			forumAtt.setId(forum.getId());
			forumAtt.setName(forum.getName());
			forumAtt.setType("forum");
			forumAtt.setPath(extraDetails.getForum());

		    } else {
			throw new CalendarServiceException(
				"Cannot read forum from given path "
					+ extraDetails.getForum());
		    }
		}
		if (extraDetails.getProfiles() != null) {
		    String[] profilePaths = extraDetails.getProfiles();
		    if (profilePaths.length > 0) {
			profilePaths = removeDuplicates(profilePaths);
			//TODO check if it is a valid profile path
			partDetails = new ParticipantDetails[profilePaths.length];
			for (int i = 0; i < profilePaths.length; i++) {
			    partDetails[i] = new ParticipantDetails();
			    partDetails[i].setPath(path);
			    partDetails[i].setProfile(profilePaths[i]);
			}

		    }
		}
		if (extraDetails.getGroups() != null) {
		    String[] groupPaths = extraDetails.getGroups();
		    if (groupPaths.length > 0) {
			String[] newGroupPaths = removeDuplicates(groupPaths);
			// TODO get group. get members and add them
			if (newGroupPaths != null && newGroupPaths.length > 0) {
			    groups = new Group[newGroupPaths.length];
			    for (int i = 0; i < newGroupPaths.length; i++) {
				Group gi = membership
					.readGroup(newGroupPaths[i]);
				if (gi != null) {
				    groups[i] = gi;
				} else {
				    throw new CalendarServiceException(
					    "Cannot read group from given path "
						    + newGroupPaths[i]);
				}
			    }
			}
		    }
		}
	    }

	    // Call the WebService
	    HashMap<String, String> values = calendarWS.updateEventExtra(event
		    .getId(), event.getSeriesId(), calendarEvent.getName(),
		    calendarEvent.getLocation(), calendarEvent.getDate(),
		    calendarEvent.getStartTime(), calendarEvent.getEndTime(),
		    calendarEvent.getContactName(), calendarEvent
			    .getContactEmail(),
		    calendarEvent.getContactPhone(), attachments, forumId);
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
		event.setName(calendarEvent.getName());
		event.setDate(calendarEvent.getDate());
		// set extra details (optional)
		if (forumAtt != null) {
		    event.setForum(forumAtt);
		}
		if (documentsAtt != null) {
		    event.setAttachments(documentsAtt);
		}
		if (partDetails != null) {
		    event.setParticipants(partDetails);
		}
		if (groups != null) {
		    event.setGroups(groups);
		}
		//
		// If changed date we need to set the path accordignly and
		// unbind old one.
		if (changedDate) {
		    String tempPath = switchDatePath(calendarEvent.getDate(),
			    oldDate, path);
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

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String updateEventAdvanced(String path, CalendarEvent calendarEvent,
	    String[] documentPaths, String forumPath, String[] profilePaths,
	    String[] groupPaths) throws CalendarServiceException {
	String updatedPath = path;
	logIt("updateEventExtra(...) called");
	logIt("params : path=" + path);
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
		    .checkCreateEventValues(calendarEvent.getName(),
			    calendarEvent.getLocation(), calendarEvent
				    .getDate(), calendarEvent.getStartTime(),
			    calendarEvent.getEndTime(), calendarEvent
				    .getContactName(), calendarEvent
				    .getContactEmail(), calendarEvent
				    .getContactPhone());
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
	    if (!calendarEvent.getDate().equals(event.getDate())) {
		changedDate = true;
	    }
	    //
	    HashMap<String, String> attachments = null;
	    AttachmentDetails[] documentsAtt = null;
	    String forumId = null;
	    AttachmentDetails forumAtt = null;
	    ParticipantDetails[] partDetails = null;
	    Group[] groups = null;
	    if (documentPaths != null && documentPaths.length > 0) {

		documentPaths = removeDuplicates(documentPaths);
		attachments = new HashMap<String, String>();
		documentsAtt = new AttachmentDetails[documentPaths.length];
		for (int i = 0; i < documentPaths.length; i++) {
		    if (documentPaths[i] != null
			    && !documentPaths[i].equals("")) {
			Document doc = documentService
				.readDocumentProperties(documentPaths[i]);
			if (doc != null) {
			    attachments.put(doc.getId(), doc.getResourceId());
			    AttachmentDetails attachment = new AttachmentDetails();
			    attachment.setId(doc.getId());
			    attachment.setName(doc.getName());
			    attachment.setPath(documentPaths[i]);
			    attachment.setResourceId(doc.getResourceId());
			    documentsAtt[i] = attachment;
			} else {
			    throw new CalendarServiceException(
				    "Cannot read document from given path "
					    + documentPaths[i]);
			}
		    } else {
			throw new CalendarServiceException(
				"Invalid document path to attach.");
		    }
		}
	    }
	    if (forumPath != null && !forumPath.equals("")) {
		Forum forum = forumService.readForumProperties(forumPath);
		if (forum != null) {
		    forumId = forum.getId();
		    forumAtt = new AttachmentDetails();
		    forumAtt.setId(forum.getId());
		    forumAtt.setName(forum.getName());
		    forumAtt.setType("forum");
		    forumAtt.setPath(forumPath);

		} else {
		    throw new CalendarServiceException(
			    "Cannot read forum from given path " + forumPath);
		}
	    }
	    if (profilePaths != null && profilePaths.length > 0) {
		profilePaths = removeDuplicates(profilePaths);
		// TODO check if it is a valid profile path
		partDetails = new ParticipantDetails[profilePaths.length];
		for (int i = 0; i < profilePaths.length; i++) {
		    partDetails[i] = new ParticipantDetails();
		    partDetails[i].setPath(path);
		    partDetails[i].setProfile(profilePaths[i]);
		}
	    }
	    if (groupPaths != null && groupPaths.length > 0) {
		String[] newGroupPaths = removeDuplicates(groupPaths);
		// TODO get group. get members and add them
		if (newGroupPaths != null && newGroupPaths.length > 0) {
		    groups = new Group[newGroupPaths.length];
		    for (int i = 0; i < newGroupPaths.length; i++) {
			Group gi = membership.readGroup(newGroupPaths[i]);
			if (gi != null) {
			    groups[i] = gi;
			} else {
			    throw new CalendarServiceException(
				    "Cannot read group from given path "
					    + newGroupPaths[i]);
			}
		    }
		}
	    }
	    // Call the WebService
	    HashMap<String, String> values = calendarWS.updateEventExtra(event
		    .getId(), event.getSeriesId(), calendarEvent.getName(),
		    calendarEvent.getLocation(), calendarEvent.getDate(),
		    calendarEvent.getStartTime(), calendarEvent.getEndTime(),
		    calendarEvent.getContactName(), calendarEvent
			    .getContactEmail(),
		    calendarEvent.getContactPhone(), attachments, forumId);
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
		event.setName(calendarEvent.getName());
		event.setDate(calendarEvent.getDate());
		// set extra details (optional)
		if (forumAtt != null) {
		    event.setForum(forumAtt);
		}
		if (documentsAtt != null) {
		    event.setAttachments(documentsAtt);
		}
		if (partDetails != null) {
		    event.setParticipants(partDetails);
		}
		if (groups != null) {
		    event.setGroups(groups);
		}
		//
		// If changed date we need to set the path accordignly and
		// unbind old one.
		if (changedDate) {
		    String tempPath = switchDatePath(calendarEvent.getDate(),
			    oldDate, path);
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

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String updateEventWithAttachments(String path,
	    CalendarEvent calendarEvent, String[] documentPaths,
	    String forumPath)
	    throws CalendarServiceException {
	String updatedPath = path;
	logIt("updateEventWithAttachments(...) called");
	logIt("params : path=" + path);
	try {
	    updatedPath = updateEvent(path, calendarEvent);
	    if (documentPaths != null && documentPaths.length > 0) {
		atttachDocumentToEvent(path, documentPaths);
	    }
	    if (forumPath != null && !forumPath.equals("")) {
		attachForumToEvent(path, forumPath);
	    }
//	    if (profilePaths != null && profilePaths.length > 0) {
//		addEventParticipants(path, profilePaths);
//	    }
	    return updatedPath;
	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to update event (with attachmetns) at path "
		    + path, e);
	    throw new CalendarServiceException(
		    "Unable to update event (with attachmetns) at path " + path,
		    e);
	}
    }

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
		Folder yearFolder = null;
		try {
		    yearFolder = core.readFolder(yearPath);
		} catch (Exception e) {
		    logger.warn(e.getMessage(), e);
		}
		if (yearFolder != null) {
		    String monthPath = yearPath + "/" + month;
		    Folder monthFolder = null;
		    try {
			monthFolder = core.readFolder(monthPath);
		    } catch (Exception e) {
			logger.warn(e.getMessage(), e);
		    }
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

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public CalendarListItem[] getMonthlyCalendar(String path, String year,
	    String month) throws CalendarServiceException {
	CalendarListItem[] items = null;
	logIt("get getMonthlyCalendar " + path);
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
		Folder yearFolder = null;
		try {
		    yearFolder = core.readFolder(yearPath);
		} catch (Exception e) {
		    logger.warn(e.getMessage(), e);
		}
		if (yearFolder != null) {
		    String monthPath = yearPath + "/" + month;
		    Folder monthFolder = null;
		    try {
			monthFolder = core.readFolder(monthPath);
		    } catch (Exception e) {
			logger.warn(e.getMessage(), e);
		    }
		    if (monthFolder != null) {
			// Retrieve month subfolders e.g. days.
			String servicePattern = CoreService.SERVICE_NAME;
			String typePattern = Folder.RESOURCE_NAME;
			String[] itemsArray = browser.listChildrenOfType(
				monthPath, servicePattern, typePattern);
			if (itemsArray != null && itemsArray.length > 0) {
			    Vector<CalendarListItem> cItemVector = new Vector<CalendarListItem>();
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
					// cItemVector.add(readEvent(eventsArray[j]));
					// No need to call the readEvent, since
					// we need only name and path
					identifier = binding
						.lookup(eventsArray[j]);
					checkResourceType(identifier,
						CalendarItem.RESOURCE_NAME);
					// Find the entity
					CalendarItem event = em.find(
						CalendarItem.class, identifier
							.getId());
					if (event == null) {
					    throw new CalendarServiceException(
						    "unable to find a event for id "
							    + identifier
								    .getId());
					}
					logIt("Found entity with id"
						+ event.getId() + " Series id "
						+ event.getSeriesId());
					CalendarListItem item = new CalendarListItem(
						eventsArray[j],
						event.getName(), event
							.getDate());
					cItemVector.add(item);
				    }
				}
			    }
			    if (cItemVector != null && cItemVector.size() > 0) {
				items = cItemVector
					.toArray(new CalendarListItem[cItemVector
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

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public CalendarListItem[] getMonthlyCalendarForUser(String year,
	    String month) throws CalendarServiceException {
	CalendarListItem[] listItems = null;
	logIt("get calendar for connected user for " + year + "/" + month);
	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new CalendarServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    //
	    int givenYear = Integer.parseInt(year);
	    int givenMonth = Integer.parseInt(month);
	    String[] eventPaths = getUserParticipations();
	    if (eventPaths != null && eventPaths.length > 0) {
		Vector<CalendarListItem> cItemVector = new Vector<CalendarListItem>();
		for (int i = 0; i < eventPaths.length; i++) {
		    // No need to call the readEvent, since
		    // we only need name,path and date
		    FactoryResourceIdentifier identifier = binding
			    .lookup(eventPaths[i]);
		    checkResourceType(identifier, CalendarItem.RESOURCE_NAME);
		    // Find the entity
		    CalendarItem event = em.find(CalendarItem.class, identifier
			    .getId());
		    if (event == null) {
			throw new CalendarServiceException(
				"unable to find a event for id "
					+ identifier.getId());
		    }
		    int[] dArray = parseDate(event.getDate());
		    int eYear = dArray[0];
		    int eMonth = dArray[1] + 1;
		    if (eYear == givenYear && eMonth == givenMonth) {
			CalendarListItem item = new CalendarListItem(
				eventPaths[i], event.getName(), event.getDate());
			cItemVector.add(item);
		    }
		}
		if (cItemVector != null && cItemVector.size() > 0) {
		    listItems = cItemVector
			    .toArray(new CalendarListItem[cItemVector.size()]);
		}
	    }

	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable to retrieve the month's events", e);
	    throw new CalendarServiceException(
		    "Unable to retrieve the month's  events", e);
	}
	return listItems;
    }

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
		// Get id(s) of documents binded with given paths
		HashMap<String, String> attachments = new HashMap<String, String>();
		AttachmentDetails[] attdetArray = new AttachmentDetails[documentPaths.length];
		for (int i = 0; i < documentPaths.length; i++) {
		    if (documentPaths[i] != null
			    && !documentPaths[i].equals("")) {
			Document doc = documentService
				.readDocumentProperties(documentPaths[i]);
			if (doc != null) {
			    attachments.put(doc.getId(), doc.getResourceId());
			    AttachmentDetails attachment = new AttachmentDetails();
			    attachment.setId(doc.getId());
			    attachment.setName(doc.getName());
			    attachment.setPath(documentPaths[i]);
			    attachment.setResourceId(doc.getResourceId());
			    attdetArray[i] = attachment;
			} else {
			    throw new CalendarServiceException(
				    "Cannot read document from given path "
					    + documentPaths[i]);
			}
		    } else {
			throw new CalendarServiceException(
				"Invalid document path to attach.");
		    }
		}
		// Call the mermigWS;
		HashMap<String, String> values = calendarWS
			.atttachDocumentToEvent(event.getId(), event
				.getSeriesId(),
				CollaborationUtils.CALENDAR_MODIFY_OC,
				attachments);
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
		    event.setAttachments(attdetArray);
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
		Forum forum = forumService.readForumProperties(forumPath);
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
			AttachmentDetails forumAtt = new AttachmentDetails();
			forumAtt.setId(forum.getId());
			forumAtt.setName(forum.getName());
			forumAtt.setType("forum");
			forumAtt.setPath(forumPath);
			event.setForum(forumAtt);
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

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void addEventParticipants(String path, String[] profilePaths)
	    throws CalendarServiceException {
	logIt("Add participants to event " + path);
	try {
	    if (path != null && profilePaths != null && profilePaths.length > 0) {
		// Security check
		String caller = membership
			.getProfilePathForConnectedIdentifier();
		if (caller == null) {
		    throw new CalendarServiceException(
			    "Could not get connected profile");
		}
		logIt("caller: " + caller);
		pep.checkSecurity(caller, path, "update");
		// Look up given path and check resource type
		FactoryResourceIdentifier identifier = binding.lookup(path);
		checkResourceType(identifier, CalendarItem.RESOURCE_NAME);
		// Find the entity
		CalendarItem event = em.find(CalendarItem.class, identifier
			.getId());
		if (event == null) {
		    throw new CalendarServiceException(
			    "Unable to find a event for id "
				    + identifier.getId());
		}
		// TODO check if profile paths are valid
		ParticipantDetails[] partDetails = new ParticipantDetails[profilePaths.length];
		for (int i = 0; i < profilePaths.length; i++) {
		    partDetails[i] = new ParticipantDetails();
		    partDetails[i].setPath(path);
		    partDetails[i].setProfile(profilePaths[i]);
		}
		event.setParticipants(partDetails);
		em.merge(event);
		binding.setProperty(path,
			FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System
				.currentTimeMillis()
				+ "");
		notification.throwEvent(new Event(path, caller,
			CalendarItem.RESOURCE_NAME, Event.buildEventType(
				CalendarService.SERVICE_NAME,
				CalendarItem.RESOURCE_NAME, "update"), ""));
		logIt(event.toString());
	    } else {
		throw new CalendarServiceException(
			"Invalid input.Please give valid event path and list of profile paths");
	    }

	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger
		    .error("Unable to add participant(s) to the event " + path,
			    e);
	    throw new CalendarServiceException(
		    "Unable to add participant(s) to the event " + path, e);
	}
    }

    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void handleInvitation(String path, String decision)
	    throws CalendarServiceException {
	logIt("Handle invitation to event " + path);
	try {
	    if (path != null && decision != null) {
		// Security check
		String caller = membership
			.getProfilePathForConnectedIdentifier();
		if (caller == null) {
		    throw new CalendarServiceException(
			    "Could not get connected profile");
		}
		logIt("caller: " + caller);
		pep.checkSecurity(caller, path, "update");
		// Look up given path and check resource type
		FactoryResourceIdentifier identifier = binding.lookup(path);
		checkResourceType(identifier, CalendarItem.RESOURCE_NAME);
		// Find the entity
		CalendarItem event = em.find(CalendarItem.class, identifier
			.getId());
		if (event == null) {
		    throw new CalendarServiceException(
			    "Unable to find a event for id "
				    + identifier.getId());
		}
		ParticipantDetails[] partDetails = event.getParticipants();
		String confirmedParticipants = event.getConfirmedParticipants();
		if (partDetails != null) {

		    boolean participantFound = false;
		    for (int i = 0; i < partDetails.length; i++) {
			if (partDetails[i].getProfile().equals(caller)) {
			    partDetails[i].setDecision(decision);
			    participantFound = true;
			    break;
			}
		    }
		    if (participantFound) {
			if (decision.toLowerCase().equals("accept")) {
			    //
			    confirmedParticipants = handleCSVString(caller,
				    confirmedParticipants);
			    event
				    .setConfirmedParticipants(confirmedParticipants);
			    // create link to event under his profile.
			    // we assume that /profiles/xxx/calendar exists.
			    String userCalendarPath = PathHelper
				    .normalize(caller + "/calendar");
			    // String datePath = createDatePath(event.getDate(),
			    // userCalendarPath);
			    // String linkPath = PathHelper.normalize(datePath+
			    // "/" + event.getId());
			    String linkPath = PathHelper
				    .normalize(userCalendarPath + "/"
					    + event.getId());
			    core.createLink(linkPath, path);
			    logIt("Created link " + linkPath);
			}
			event.setParticipants(partDetails);
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
			throw new CalendarServiceException("Given profile "
				+ caller
				+ " is not in the list of particiapnts of "
				+ path);
		    }
		}
		logIt(event.toString());
	    } else {
		throw new CalendarServiceException(
			"Invalid input.Please give valid event path and list of profile paths");
	    }

	} catch (Exception e) {
	    // ctx.setRollbackOnly();
	    logger.error("Unable handle invitation for " + path, e);
	    throw new CalendarServiceException(
		    "Unable to handle invitation for " + path, e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public ParticipantDetails[] getEventParticipants(String path)
	    throws CalendarServiceException {
	ParticipantDetails[] participants = null;
	try {
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new CalendarServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "update");
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
	    participants = event.getParticipants();
	} catch (Exception e) {
	    logger
		    .error("Unable to add participant(s) to the event " + path,
			    e);
	    throw new CalendarServiceException(
		    "Unable to add participant(s) to the event " + path, e);
	}
	return participants;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String[] getUserParticipations() throws CalendarServiceException {
	logIt("Get user confirmed participations to events.");
	String[] eventPaths = null;
	try {
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		throw new CalendarServiceException(
			"Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    // We assume that his confirmed events are Link entitites under
	    // profile/calendar
	    Folder calFold = null;
	    String calPath = PathHelper.normalize(caller + "/calendar");
	    try {
		calFold = core.readFolder(calPath);
	    } catch (Exception e) {
		logger.warn("No calendar folder under " + caller);
	    }
	    if (calFold != null) {
		String servicePattern = CoreService.SERVICE_NAME;
		String typePattern = Link.RESOURCE_NAME;
		String[] itemsArray = browser.listChildrenOfType(calPath,
			servicePattern, typePattern);
		if (itemsArray != null && itemsArray.length > 0) {
		    logIt("Number of links found:" + itemsArray.length);
		    eventPaths = new String[itemsArray.length];
		    for (int i = 0; i < itemsArray.length; i++) {
			Link link = core.readLink(itemsArray[i]);
			eventPaths[i] = link.getLink();
		    }
		} else {
		    logger.warn("No linked events under " + calPath);
		}
	    }
	} catch (Exception e) {
	    logger.error(
		    "Unable to retrieve participation info of connected user.",
		    e);
	    throw new CalendarServiceException(
		    "Unable to retrieve participation info of  of connected user.",
		    e);
	}
	return eventPaths;
    }

    public EntityManager getEntityManager() {
	return this.em;
    }

    @PersistenceContext(unitName = "CalendarServiceBean")
    public void setEntityManager(EntityManager em) {
	this.em = em;
    }

    public SessionContext getSessionContext() {
	return this.ctx;
    }

    @Resource
    public void setSessionContext(SessionContext ctx) {
	this.ctx = ctx;
    }

    @EJB
    public void setBindingService(BindingService binding) {
	this.binding = binding;
    }

    public BindingService getBindingService() {
	return this.binding;
    }

    public PEPService getPEPService() {
	return this.pep;
    }

    @EJB
    public void setPEPService(PEPService pep) {
	this.pep = pep;
    }

    public PAPService getPAPService() {
	return this.pap;
    }

    @EJB
    public void setPAPService(PAPService pap) {
	this.pap = pap;
    }

    public NotificationService getNotificationService() {
	return this.notification;
    }

    @EJB
    public void setNotificationService(NotificationService notification) {
	this.notification = notification;
    }

    public MembershipService getMembershipService() {
	return this.membership;
    }

    @EJB
    public void setMembershipService(MembershipService membership) {
	this.membership = membership;
    }

    public BrowserService getBrowser() {
	return browser;
    }

    @EJB
    public void setBrowser(BrowserService browser) {
	this.browser = browser;
    }

    public CoreService getCore() {
	return core;
    }

    @EJB
    public void setCore(CoreService core) {
	this.core = core;
    }

    public CalendarWService getCalendarWS() {
	return calendarWS;
    }

    @EJB(name = "CalendarWService")
    public void setCalendarWS(CalendarWService calendarWS) {
	this.calendarWS = calendarWS;
    }

    public DocumentService getDocumentService() {
	return documentService;
    }

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

    @Override
    public String[] getResourceTypeList() {
	return RESOURCE_TYPE_LIST;
    }

    @Override
    public String getServiceName() {
	return SERVICE_NAME;
    }

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

    private void logIt(String message) {
	if (logger.isInfoEnabled()) {
	    logger.info(message);
	}
    }

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

    private void saveFolderPath(String path, String name, String description)
	    throws CalendarServiceException {
	try {
	    core.readFolder(path);
	    logIt("Path " + path + " allready exitst");
	  
	} catch (AccessDeniedException e) {
	    throw new CalendarServiceException(e.getMessage(),e);
	} catch (InvalidPathException e) {
	    throw new CalendarServiceException(e.getMessage(),e);
	} catch (PathNotFoundException ex) {
	    try {
		core.createFolder(path, name, description);
		logIt("Crated Path " + path + " succesfully");
	    } catch (CoreServiceException e) {
		throw new CalendarServiceException(
			"Unable to create date path." + path);
	    } catch (AccessDeniedException e) {
		throw new CalendarServiceException(e.getMessage(),e);
	    } catch (InvalidPathException e) {
		throw new CalendarServiceException(e.getMessage(),e);
	    } catch (PathAlreadyBoundException e) {
		throw new CalendarServiceException(e.getMessage(),e);
	    }
	} catch (CoreServiceException e) {
	    throw new CalendarServiceException(e.getMessage(),e);
	}

    }

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

    private String switchDatePath(String date, String oldDate, String eventPath)
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

    private boolean isMemberOf(String part, String list) {
	if (list.indexOf(part) != -1) {
	    return true;
	}
	return false;
    }

    private String handleCSVString(String value, String csvString)
	    throws Exception {
	String toReturn = null;
	if (csvString == null || csvString.equals("")) {
	    toReturn = value;
	} else {
	    if (!isMemberOf(value, csvString)) {
		toReturn = (csvString + "," + value);
	    }
	}
	return toReturn;
    }

    private String[] removeDuplicates(String[] valuesArray) {
	// make sure we don't have duplicated values
	Vector<String> vs = new Vector<String>();
	for (int i = 0; i < valuesArray.length; i++) {
	    if (i == 0) {
		vs.add(valuesArray[i]);
	    } else if (!vs.contains(valuesArray[i])) {
		vs.add(valuesArray[i]);
	    }
	}
	if (vs != null && vs.size() > 0) {
	    valuesArray = vs.toArray(new String[vs.size()]);
	}
	return valuesArray;
    }

    private CalendarEvent convert2CalendarEvent(CalendarItem cItem) {
	CalendarEvent ce = null;
	if (cItem != null) {
	    ce = new CalendarEvent(cItem.getName(), cItem.getLocation(), cItem
		    .getDate(), cItem.getStartTime(), cItem.getEndTime(), cItem
		    .getContactName(), cItem.getContactEmail(), cItem
		    .getContactPhone());
	}
	return ce;
    }

}
