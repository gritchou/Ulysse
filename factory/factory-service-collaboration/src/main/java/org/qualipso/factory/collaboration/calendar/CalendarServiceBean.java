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
import org.qualipso.factory.browser.BrowserService;
import org.qualipso.factory.collaboration.calendar.entity.CalendarItem;
import org.qualipso.factory.collaboration.document.DocumentServiceException;
import org.qualipso.factory.collaboration.forum.entity.ThreadMessage;
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

@Stateless(name = "CalendarServiceBean", mappedName = FactoryNamingConvention.JNDI_SERVICE_PREFIX + "CalendarService")
@WebService(endpointInterface = "org.qualipso.factory.collaboration.calendar.CalendarService", targetNamespace = "http://org.qualipso.factory.ws/service/calendar", serviceName = "CalendarService", portName = "CalendarServicePort")
@WebContext(contextRoot = "/factory-service-collaboration", urlPattern = "/calendar")
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class CalendarServiceBean implements CalendarService
{
    private static Log logger = LogFactory.getLog(CalendarServiceBean.class);
    private static final String SERVICE_NAME = "CalendarService";
    private static final String[] RESOURCE_TYPE_LIST = new String[] { "CalendarItem" };

    private BindingService binding;
    private PEPService pep;
    private PAPService pap;
    private NotificationService notification;
    private MembershipService membership;
    //
    private BrowserService browser;
    private CoreService core;
    //
    private SessionContext ctx;
    private EntityManager em;
    //
    private CalendarWService calendarWS;

    //
    public CalendarServiceBean()
    {

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String[] createEvent(String calendarPath, String name, String location, String date, String startTime,
	    String endTime, String contactName, String contactEmail, String contactPhone, String recurrence, long times)
	    throws CalendarServiceException
    {
	String[] paths = null;
	logIt("Create event under " + calendarPath);
	try
	{
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null)
	    {
		throw new CalendarServiceException("Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    // Check the values
	    CollaborationUtils.checkCreateEventValues(name, location, date, startTime, endTime, contactName,
		    contactEmail, contactPhone);
	    // Set path for the event. We assume that all calendar items are
	    // under a defined calendar path. For example
	    // /any-folder/calendar
	    // The path wil be /path/yyyy/M/dd f.e. /calendar/2009/9/16/UID
	    pep.checkSecurity(caller, calendarPath, "create");
	    // Call the mermig WS to create the event
	    HashMap resultMap = calendarWS.createEvent(name, location, date, startTime, endTime, contactName,
		    contactEmail, contactPhone, recurrence, times);
	    logIt("Message Code:" + resultMap.get("statusCode") + " Message: " + resultMap.get("statusMessage"));
	    if (!resultMap.get("statusCode").equals(CollaborationUtils.SUCCESS_CODE))
	    {
		throw new CalendarServiceException("Error code recieved from the WS." + " Code"
			+ resultMap.get("statusCode") + " Message:" + resultMap.get("statusMessage"));
	    }
	    String seriesID = (String) resultMap.get("seriesID");
	    HashMap<String, String> occurences = (HashMap<String, String>) resultMap.get("occurenceIds");
	    // We save the series and the occurences
	    if (occurences != null && occurences.size() > 1)
	    {
		// Calculate paths first becuase we need to save them on every
		// occurence
		paths = createOcPaths(occurences, date, calendarPath);
		logIt("Need to save the series and the occurences " + occurences.size());
		Iterator iterator = occurences.entrySet().iterator();
		int cnt = 0;
		while (iterator.hasNext())
		{
		    String newDate = getNextDay(date, cnt);
		    Map.Entry entry = (Map.Entry) iterator.next();
		    String ocID = (String) entry.getValue();
		    String ocPath = paths[cnt];
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
		    binding.setProperty(ocPath, FactoryResourceProperty.CREATION_TIMESTAMP, ""
			    + System.currentTimeMillis());
		    binding.setProperty(ocPath, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, ""
			    + System.currentTimeMillis());
		    binding.setProperty(ocPath, FactoryResourceProperty.AUTHOR, caller);

		    // Create policy (owner)
		    String policyId = UUID.randomUUID().toString();
		    pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, caller, ocPath));
		    // Setting security properties on the node :
		    binding.setProperty(ocPath, FactoryResourceProperty.OWNER, caller);
		    binding.setProperty(ocPath, FactoryResourceProperty.POLICY_ID, policyId);
		    // Notify for the creation
		    notification.throwEvent(new Event(ocPath, caller, "CalendarItem", "collaboration.calendar.create",
			    ""));
		    logIt(event.toString());
		    cnt++;
		}
	    } else
	    {
		String datePath = createDatePath(date, calendarPath);
		String path = calendarPath;
		if (datePath != null)
		{
		    path += "/" + datePath;
		}
		// path += "/" + CollaborationUtils.normalizeForPath(name);
		String firstOcID = CollaborationUtils.getFirstElement(occurences);
		path += "/" + firstOcID;
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
		binding.setProperty(path, FactoryResourceProperty.CREATION_TIMESTAMP, "" + System.currentTimeMillis());
		binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, ""
			+ System.currentTimeMillis());
		binding.setProperty(path, FactoryResourceProperty.AUTHOR, caller);
		// Create policy (owner)
		String policyId = UUID.randomUUID().toString();
		pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, caller, path));
		binding.setProperty(path, FactoryResourceProperty.OWNER, caller);
		binding.setProperty(path, FactoryResourceProperty.POLICY_ID, policyId);
		// Notify for the creation
		notification.throwEvent(new Event(path, caller, "CalendarItem", "collaboration.calendar.create", ""));
		logIt(event.toString());
		paths = new String[1];
		paths[0] = path;
	    }
	    return paths;
	} catch (Exception e)
	{
	    // ctx.setRollbackOnly();
	    logger.error("Unable to create the event at path " + calendarPath, e);
	    throw new CalendarServiceException("Unable to create the event at path " + calendarPath, e);
	}

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public CalendarItem readEvent(String path) throws CalendarServiceException
    {
	CalendarItem event = null;
	logIt("Read event " + path);
	try
	{
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null)
	    {
		throw new CalendarServiceException("Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");

	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, "CalendarItem");
	    // Find the entity
	    event = em.find(CalendarItem.class, identifier.getId());
	    if (event == null)
	    {
		throw new CalendarServiceException("unable to find a event for id " + identifier.getId());
	    }
	    logIt("Found entity with id" + event.getId() + " Series id " + event.getSeriesId());
	    // Call the WS to retrieve values that are not stored in
	    HashMap<String, Object> values = calendarWS.readEvent(event.getId(), event.getSeriesId(), "occurence");
	    if (values != null && !values.isEmpty())
	    {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE) || values.get("calendar-item") == null)
		{
		    throw new CalendarServiceException("Error code recieved from the WS." + " Code" + code
			    + " Message:" + msg);
		}
		CalendarDTO eventDTO = (CalendarDTO) values.get("calendar-item");
		if (eventDTO != null)
		{
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
		}
	    } else
	    {
		throw new CalendarServiceException("Wrong response recieved from the WS.Check Logs");
	    }

	    // Notify
	    notification.throwEvent(new Event(path, caller, "CalendarItem", "collaboration.calendar.read", ""));
	    //
	    logIt(event.toString());
	    return event;

	} catch (Exception e)
	{
	    logger.error("Unable to read the event at path " + path, e);
	    throw new CalendarServiceException("Unable to read the event at path " + path, e);
	}
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String updateEvent(String path, String name, String location, String date, String startTime, String endTime,
	    String contactName, String contactEmail, String contactPhone) throws CalendarServiceException
    {
	String updatedPath = path;
	logIt("updateEvent(...) called");
	logIt("params : path=" + path + ", name=" + name);
	try
	{
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null)
	    {
		throw new CalendarServiceException("Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "update");
	    // Check supplied values
	    CollaborationUtils.checkCreateEventValues(name, location, date, startTime, endTime, contactName,
		    contactEmail, contactPhone);
	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, "CalendarItem");
	    // Find the entity
	    CalendarItem event = em.find(CalendarItem.class, identifier.getId());
	    if (event == null)
	    {
		throw new CalendarServiceException("Unable to find a event for id " + identifier.getId());
	    }
	    boolean changedDate = false;
	    String oldPath = path;
	    String oldDate = event.getDate();
	    if (!date.equals(event.getDate()))
	    {
		changedDate = true;
	    }
	    // Call the WebService
	    HashMap<String, String> values = calendarWS.updateEvent(event.getId(), event.getSeriesId(), name, location,
		    date, startTime, endTime, contactName, contactEmail, contactPhone);
	    if (values != null && !values.isEmpty())
	    {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE))
		{
		    throw new CalendarServiceException("Error code recieved from the WS." + " Code" + code
			    + " Message:" + msg);
		}

		// Save the entity in the factory
		event.setName(name);
		event.setDate(date);
		// If changed date we need to set the path accordignly and
		// unbind old one.
		if (changedDate)
		{
		    String tempPath = swichDatePath(date, oldDate, path);
		    if (tempPath != null)
		    {
			path = tempPath + "/" + event.getId();
			logIt("Switch from " + oldPath + " TO " + path);
		    }
		    logIt("Path: " + path);
		    event.setResourcePath(path);
		    em.merge(event);
		    updatedPath = path;
		    // Delete previous policy and unbind old path
		    String policyId = binding.getProperty(oldPath, FactoryResourceProperty.POLICY_ID, false);
		    pap.deletePolicy(policyId);
		    binding.unbind(oldPath);
		    // Bind the event with the new path based on the new date
		    binding.bind(event.getFactoryResourceIdentifier(), path);
		    binding.setProperty(path, FactoryResourceProperty.CREATION_TIMESTAMP, ""
			    + System.currentTimeMillis());
		    binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, ""
			    + System.currentTimeMillis());
		    binding.setProperty(path, FactoryResourceProperty.AUTHOR, caller);
		    // Create policy (owner)
		    policyId = UUID.randomUUID().toString();
		    pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, caller, path));
		    binding.setProperty(path, FactoryResourceProperty.OWNER, caller);
		    binding.setProperty(path, FactoryResourceProperty.POLICY_ID, policyId);
		    // Notify
		    notification
			    .throwEvent(new Event(path, caller, "CalendarItem", "collaboration.calendar.update", ""));
		} else
		{
		    event.setResourcePath(path);
		    em.merge(event);
		    binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis()
			    + "");
		    notification
			    .throwEvent(new Event(path, caller, "CalendarItem", "collaboration.calendar.update", ""));
		}
		logIt(event.toString());
	    } else
	    {
		throw new CalendarServiceException("No valid answer from the WS.Check logs.");
	    }
	    return updatedPath;
	} catch (Exception e)
	{
	    // ctx.setRollbackOnly();
	    logger.error("Unable to update the event at path " + path, e);
	    throw new CalendarServiceException("Unable to update the event at path " + path, e);
	}

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String[] updateRecurrence(String path, String date, String recurrence, long times)
	    throws CalendarServiceException
    {
	String[] paths = null;
	logIt("updateEvent(...) called");
	logIt("params : path=" + path + ", reccurence=" + recurrence + " times " + times);
	try
	{
	    // Check supplied values
	    if (!recurrence.equals(CollaborationUtils.REC_0) && !recurrence.equals(CollaborationUtils.REC_1))
	    {
		recurrence = CollaborationUtils.REC_0;
		times = 1;
	    }
	    if (date == null || date == "")
	    {
		throw new CalendarServiceException("Event's date is mandatory");
	    }
	    CalendarItem event = readEvent(path);
	    if (event == null)
	    {
		throw new CalendarServiceException("Unable to find a event for path " + path);
	    }
	    boolean changedDate = false;
	    boolean changedRec = false;
	    boolean changedTimes = false;
	    String oldRec = event.getRecurrence();
	    long oldTimes = event.getTimes();
	    if (!date.equals(event.getDate()))
	    {
		changedDate = true;
	    }
	    if (oldTimes != times)
	    {
		changedTimes = true;
	    }
	    if (!recurrence.equals(oldRec))
	    {
		changedRec = true;
	    }
	    // If we changed date only then call simpleUpdate.If changed we
	    // delete old and create new ones based on the given
	    // event(path)values
	    if (changedDate && !changedRec && event.getTimes() == 1)
	    {
		logIt("No need to update the series. Proceed with simple update");

		String newPath = updateEvent(path, event.getName(), event.getLocation(), date, event.getStartTime(),
			event.getEndTime(), event.getContactName(), event.getContactEmail(), event.getContactPhone());
		paths = new String[0];
		paths[0] = newPath;
	    } else if (changedRec || changedTimes)
	    {
		String calendarPath = getRootPath(path, event.getDate());

		paths = createEvent(calendarPath, event.getName(), event.getLocation(), date, event.getStartTime(),
			event.getEndTime(), event.getContactName(), event.getContactEmail(), event.getContactPhone(),
			recurrence, times);
		if (event.getOccurencePaths() != null)
		{
		    for (int i = 0; i < event.getOccurencePaths().length; i++)
		    {
			deleteEvent(event.getOccurencePaths()[i]);
		    }
		} else
		{
		    deleteEvent(path);
		}
	    }
	    return paths;
	} catch (Exception e)
	{
	    // ctx.setRollbackOnly();
	    logger.error("Unable to update recurrence/date for event at path " + path, e);
	    throw new CalendarServiceException("Unable to update recurrence/date for event at path " + path, e);
	}

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteEvent(String path) throws CalendarServiceException
    {
	logIt("delete event " + path);
	try
	{
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null)
	    {
		throw new CalendarServiceException("Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "delete");
	    // Look up given path and check resource type
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    checkResourceType(identifier, "CalendarItem");
	    // Find the entity
	    CalendarItem event = em.find(CalendarItem.class, identifier.getId());
	    if (event == null)
	    {
		throw new DocumentServiceException("unable to find a event for id " + identifier.getId());
	    }
	    logIt("Call WS to delete event with id: " + event.getId());
	    HashMap<String, String> values = calendarWS.deleteEvent(event.getId());
	    if (values != null && !values.isEmpty())
	    {
		String code = (String) values.get("statusCode");
		String msg = (String) values.get("statusMessage");
		logIt("Message Code:" + code + " Message: " + msg);
		if (!code.equals(CollaborationUtils.SUCCESS_CODE))
		{
		    throw new CalendarServiceException("Error code recieved from the WS." + " Code" + code
			    + " Message:" + msg);
		}
		em.remove(event);
		// Delete the policy and unbind the resource from this path
		String policyId = binding.getProperty(path, FactoryResourceProperty.POLICY_ID, false);
		pap.deletePolicy(policyId);
		binding.unbind(path);
		// notify
		notification.throwEvent(new Event(path, caller, "CalendarItem", "collaboration.calendar.delete", ""));
	    } else
	    {
		throw new CalendarServiceException("No valid answer from the WS.Check logs.");
	    }
	} catch (Exception e)
	{
	    // ctx.setRollbackOnly();
	    logger.error("Unable to delete the event at path " + path, e);
	    throw new CalendarServiceException("Unable to delete the event at path " + path, e);
	}

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public CalendarItem[] getCalendarItems(String path) throws CalendarServiceException
    {
	CalendarItem[] items = null;
	logIt("get getCalendarItems " + path);
	try
	{
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null)
	    {
		throw new CalendarServiceException("Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    FactoryResourceIdentifier identifier = binding.lookup(path);

	    String servicePattern = "CalendarService";
	    String typePattern = "CalendarItem";
	    String[] itemsArray = browser.listChildrenOfType(path, servicePattern, typePattern);
	    if (itemsArray != null && itemsArray.length > 0)
	    {
		items = new CalendarItem[itemsArray.length];
		for (int i = 0; i < itemsArray.length; i++)
		{
		    logIt("child #" + i + ". " + itemsArray[i]);
		    items[i] = readEvent(itemsArray[i]);
		    logIt(items[i].toString());
		}
	    }
	} catch (Exception e)
	{
	    // ctx.setRollbackOnly();
	    logger.error("Unable to retrieve the events at path " + path, e);
	    throw new CalendarServiceException("Unable to retrieve the events at path " + path, e);
	}
	return items;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public CalendarItem[] getCalendarItemsForMonth(String path, String year, String month)
	    throws CalendarServiceException
    {
	CalendarItem[] items = null;
	logIt("get getCalendarItemsForMonth " + path);
	try
	{
	    // Security check
	    String caller = membership.getProfilePathForConnectedIdentifier();
	    if (caller == null)
	    {
		throw new CalendarServiceException("Could not get connected profile");
	    }
	    logIt("caller: " + caller);
	    pep.checkSecurity(caller, path, "read");
	    // look up to check if path is invalid
	    FactoryResourceIdentifier identifier = binding.lookup(path);
	    // We save calendar times in a folder structure like
	    // <path>/year/month/day/id
	    // Check that year exists.
	    String yearPath = path + "/" + year;
	    Folder yearFolder = core.readFolder(yearPath);
	    if (yearFolder != null)
	    {
		String monthPath = yearPath + "/" + month;
		Folder monthFolder = core.readFolder(monthPath);
		if (monthFolder != null)
		{
		    // Retrieve month subfolders e.g. days.
		    String servicePattern = "CoreService";
		    String typePattern = "Folder";
		    String[] itemsArray = browser.listChildrenOfType(monthPath, servicePattern, typePattern);
		    if (itemsArray != null && itemsArray.length > 0)
		    {
			Vector<CalendarItem> cItemVector = new Vector<CalendarItem>();
			for (int i = 0; i < itemsArray.length; i++)
			{
			    logIt("get events of day #" + i + " " + itemsArray[i]);
			    servicePattern = "CalendarService";
			    typePattern = "CalendarItem";
			    String[] eventsArray = browser.listChildrenOfType(itemsArray[i], servicePattern, typePattern);
			    if (eventsArray != null && eventsArray.length > 0)
			    {
				for (int j = 0; j < eventsArray.length; j++)
				{
				    logIt("Found "+eventsArray[j]);
				    cItemVector.add(readEvent(eventsArray[j]));
				}
			    }
			}
			if(cItemVector!=null && cItemVector.size()>0)
			{
			    items = cItemVector.toArray(new CalendarItem[cItemVector.size()]);
			}
		    }else{
			logIt("No events found for given " + monthPath);
		    }
		}else{
		    logIt("No events found for given " + monthPath);
		}
	    } else
	    {
		logIt("No events found for given " + yearPath);
	    }
	} catch (Exception e)
	{
	    // ctx.setRollbackOnly();
	    logger.error("Unable to retrieve the month's events at path " + path, e);
	    throw new CalendarServiceException("Unable to retrieve the month's  events at path " + path, e);
	}
	return items;
    }

    @PersistenceContext(unitName = "CalendarServiceBean")
    public void setEntityManager(EntityManager em)
    {
	this.em = em;
    }

    public EntityManager getEntityManager()
    {
	return this.em;
    }

    @Resource
    public void setSessionContext(SessionContext ctx)
    {
	this.ctx = ctx;
    }

    public SessionContext getSessionContext()
    {
	return this.ctx;
    }

    @EJB(name = "BindingService")
    public void setBindingService(BindingService binding)
    {
	this.binding = binding;
    }

    public BindingService getBindingService()
    {
	return this.binding;
    }

    @EJB(name = "PEPService")
    public void setPEPService(PEPService pep)
    {
	this.pep = pep;
    }

    public PEPService getPEPService()
    {
	return this.pep;
    }

    @EJB(name = "PAPService")
    public void setPAPService(PAPService pap)
    {
	this.pap = pap;
    }

    public PAPService getPAPService()
    {
	return this.pap;
    }

    @EJB(name = "NotificationService")
    public void setNotificationService(NotificationService notification)
    {
	this.notification = notification;
    }

    public NotificationService getNotificationService()
    {
	return this.notification;
    }

    @EJB(name = "MembershipService")
    public void setMembershipService(MembershipService membership)
    {
	this.membership = membership;
    }

    public MembershipService getMembershipService()
    {
	return this.membership;
    }

    public BrowserService getBrowser()
    {
	return browser;
    }

    @EJB(name = "BrowserService")
    public void setBrowser(BrowserService browser)
    {
	this.browser = browser;
    }

    public CoreService getCore()
    {
	return core;
    }

    @EJB(name = "CoreService")
    public void setCore(CoreService core)
    {
	this.core = core;
    }

    public CalendarWService getCalendarWS()
    {
	return calendarWS;
    }

    @EJB(name = "CalendarWService")
    public void setCalendarWS(CalendarWService calendarWS)
    {
	this.calendarWS = calendarWS;
    }

    private void checkResourceType(FactoryResourceIdentifier identifier, String resourceType)
	    throws MembershipServiceException
    {

	if (!identifier.getService().equals(getServiceName()))
	{
	    throw new MembershipServiceException("resource identifier " + identifier + " does not refer to service "
		    + getServiceName());
	}
	if (!identifier.getType().equals(resourceType))
	{
	    throw new MembershipServiceException("resource identifier " + identifier
		    + " does not refer to a resource of type " + resourceType);
	}
    }

    @Override
    public String[] getResourceTypeList()
    {
	return RESOURCE_TYPE_LIST;
    }

    @Override
    public String getServiceName()
    {
	return SERVICE_NAME;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FactoryResource findResource(String path) throws FactoryException
    {
	logIt("findResource(...) called");
	logIt("params : path=" + path);

	try
	{
	    FactoryResourceIdentifier identifier = binding.lookup(path);

	    if (!identifier.getService().equals(SERVICE_NAME))
	    {
		throw new CoreServiceException("Resource " + identifier + " is not managed by " + SERVICE_NAME);
	    }

	    if (identifier.getType().equals("CalendarItem"))
	    {
		return readEvent(path);
	    }

	    throw new CoreServiceException("Resource " + identifier + " is not managed by Greeting Service");

	} catch (Exception e)
	{
	    logger.error("unable to find the resource at path " + path, e);
	    throw new CoreServiceException("unable to find the resource at path " + path, e);
	}
    }

    private void logIt(String message)
    {
	if (logger.isInfoEnabled())
	{
	    logger.info(message);
	}
    }

    private String getNextDay(String date, int index) throws CalendarServiceException
    {
	String newDate = date;
	try
	{
	    if (index > 0)
	    {
		// FIXME It only works with "daily" reccurence
		int MILLIS = 1000 * 60 * 60 * 24 * index;
		DateFormat wsDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date eventDate = wsDateFormat.parse(date);
		newDate = wsDateFormat.format(eventDate.getTime() + MILLIS);
		logIt("Next day " + newDate);
	    }
	} catch (Exception e)
	{
	    throw new CalendarServiceException("Error in retrieving the next day. Reason: " + e.getMessage());
	}
	return newDate;
    }

    private void saveFolderPath(String path, String name, String description) throws CalendarServiceException
    {

	try
	{
	    Folder cf = core.readFolder(path);
	    logIt("Path " + path + " allready exitst");
	} catch (CoreServiceException e)
	{
	    try
	    {
		core.createFolder(path, name, description);
		logIt("Crated Path " + path + " succesfully");
	    } catch (CoreServiceException e2)
	    {
		throw new CalendarServiceException("Unable to create date path." + path);
	    }
	}

    }

    private String[] createOcPaths(HashMap<String, String> occurences, String date, String calendarPath)
	    throws CalendarServiceException
    {
	String[] occurencePaths = new String[occurences.size()];
	Iterator iterator = occurences.entrySet().iterator();
	int cnt = 0;
	while (iterator.hasNext())
	{
	    String newDate = getNextDay(date, cnt);
	    logIt("Next day " + newDate);
	    String datePath = createDatePath(newDate, calendarPath);
	    String path = calendarPath;
	    if (datePath != null)
	    {
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

    private String createDatePath(String date, String calendarPath) throws CalendarServiceException
    {
	String fdatePath = null;
	logIt("Parse date " + date + " as yyyy-MM-dd fortmat");
	int[] dArray = parseDate(date);
	int year = dArray[0];
	int month = dArray[1] + 1;
	int day = dArray[2];

	// skip the 'root' path We "assume" it is there.It should be. :)
	// saveFolderPath(calendarPath, "calendar", "calendar folder");
	//
	saveFolderPath(calendarPath + "/" + year, String.valueOf(year), "calendar folder for year " + year);
	saveFolderPath(calendarPath + "/" + year + "/" + month, String.valueOf(month), "calendar folder for year "
		+ year + " and month" + month);
	saveFolderPath(calendarPath + "/" + year + "/" + month + "/" + day, String.valueOf(day),
		"calendar folder for year " + year + " and month" + month + " and day " + day);
	fdatePath = new String(year + "/" + month + "/" + day);
	return fdatePath;
    }

    private String getRootPath(String path, String date) throws CalendarServiceException
    {
	int[] dArray = parseDate(date);
	String tempPath = new String("/" + dArray[0] + "/" + (dArray[1] + 1) + "/" + dArray[2]);
	int index = path.indexOf(tempPath);
	String parentPath = "";
	if (index > 0)
	{
	    parentPath = path.substring(0, index);
	}

	return parentPath;
    }

    private String swichDatePath(String date, String oldDate, String eventPath) throws CalendarServiceException
    {
	String fdatePath = null;

	int[] dArray = parseDate(date);
	int year = dArray[0];
	int month = dArray[1] + 1;
	int day = dArray[2];
	String parentPath = getRootPath(eventPath, oldDate);

	if (!parentPath.equals(""))
	{
	    saveFolderPath(parentPath, "calendar", "calendar folder");
	    saveFolderPath(parentPath + "/" + year, String.valueOf(year), "calendar folder for year " + year);
	    saveFolderPath(parentPath + "/" + year + "/" + month, String.valueOf(month), "calendar folder for year "
		    + year + " and month" + month);
	    saveFolderPath(parentPath + "/" + year + "/" + month + "/" + day, String.valueOf(day),
		    "calendar folder for year " + year + " and month" + month + " and day " + day);
	    fdatePath = parentPath + "/" + year + "/" + month + "/" + day;
	} else
	{
	    throw new CalendarServiceException("Error in switching dates.");
	}

	return fdatePath;
    }

    private int[] parseDate(String date) throws CalendarServiceException
    {
	int[] dArray = new int[3];
	try
	{
	    logIt("Parse date " + date + " as yyyy-MM-dd format");
	    DateFormat wsDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    Date eventDate = wsDateFormat.parse(date);
	    Calendar eventCalendar = new GregorianCalendar();
	    eventCalendar.setTime(eventDate);
	    dArray[0] = eventCalendar.get(Calendar.YEAR);
	    dArray[1] = eventCalendar.get(Calendar.MONTH);
	    dArray[2] = eventCalendar.get(Calendar.DATE);
	} catch (ParseException e)
	{
	    logger.warn("Given date " + date + " is not on yyyy-MM-dd fortmat. Trying to parse as dd/MM/yyyy");
	    try
	    {
		DateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date eventDate = simpleDateFormat.parse(date);
		Calendar eventCalendar = new GregorianCalendar();
		eventCalendar.setTime(eventDate);
		dArray[0] = eventCalendar.get(Calendar.YEAR);
		dArray[1] = eventCalendar.get(Calendar.MONTH) + 1;
		dArray[2] = eventCalendar.get(Calendar.DATE);
	    } catch (Exception e2)
	    {
		throw new CalendarServiceException("Date format exception " + date);
	    }
	}
	return dArray;
    }

}
