package org.qualipso.factory.collaboration.ws;

import java.util.HashMap;
import java.util.Iterator;

import javax.ejb.Stateless;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.collaboration.ws.beans.CalendarDTO;

@Stateless (name = "CalendarWSBean", mappedName = "CalendarWService")
public class CalendarWSBean extends CollaborationWSUtils implements CalendarWService
{
    public CalendarWSBean(){
	
    }
    private static Log logger = LogFactory.getLog(CalendarWSBean.class);
    
    @Override
    public HashMap<String, Object> createEvent(String name, String location, String date, String startTime,
	    String endTime, String contactName, String contactEmail, String contactPhone, String recurrence, long times)
	    throws Exception
    {
	HashMap<String, Object> resultMap = new HashMap<String, Object>(1);
	OMElement payload = getCreateEventPayLoad(name, location, date, startTime, endTime, contactName, contactEmail,
		contactPhone, recurrence, times);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:createEvent");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(getQName("statusCode"));
	resultMap.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(getQName("statusMessage"));
	resultMap.put("statusMessage", omStatusMsg.getText());
	//
	if (omStatus.getText().equals(SUCCESS_CODE))
	{
	    OMElement omSeriesID = result.getFirstChildWithName(getQName("seriesID"));
	    resultMap.put("seriesID", omSeriesID.getText());
	    Iterator iter = result.getChildrenWithName(getQName("occurenceId"));
	    if (iter != null)
	    {
		HashMap ocIds = new HashMap();
		while (iter.hasNext())
		{
		    OMElement ocElement = (OMElement) iter.next();
		    ocIds.put(ocElement.getText(), ocElement.getText());
		}
		resultMap.put("occurenceIds", ocIds);
	    }
	}
	return resultMap;
    }
    
    @Override
    public HashMap<String, Object> readEvent(String itemId, String seriesId, String view) throws Exception
    {

	HashMap<String, Object> resultMap = new HashMap<String, Object>(1);
	OMElement payload = createReadEventPayLoad(itemId, seriesId, view);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:getCalendarItem");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(getQName("statusCode"));
	resultMap.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(getQName("statusMessage"));
	resultMap.put("statusMessage", omStatusMsg.getText());
	if (omStatus.getText().equals(SUCCESS_CODE))
	{
	    OMElement omCalendarItem = result.getFirstChildWithName(getQName("calendarItem"));
	    if (omCalendarItem != null)
	    {
		CalendarDTO cItem = new CalendarDTO();
		// Basically we only need the values not peristed on factory.
		// OMElement omItemID =
		// omCalendarItem.getFirstChildWithName(getQName("itemID"));
		// OMElement omSeriesID =
		// omCalendarItem.getFirstChildWithName(getQName("seriesID"));
		// OMElement omSubject =
		// omCalendarItem.getFirstChildWithName(getQName("subject"));
		// cItem.setName(omSubject.getText());
		OMElement omLocation = omCalendarItem.getFirstChildWithName(getQName("location"));
		cItem.setLocation(omLocation.getText());
		OMElement omDate = omCalendarItem.getFirstChildWithName(getQName("date"));
		cItem.setDate(omDate.getText());
		OMElement omStartTime = omCalendarItem.getFirstChildWithName(getQName("startTime"));
		cItem.setStartTime(omStartTime.getText());
		OMElement omEndTime = omCalendarItem.getFirstChildWithName(getQName("endTime"));
		cItem.setEndTime(omEndTime.getText());
		OMElement omOccurence = omCalendarItem.getFirstChildWithName(getQName("occurence"));
		if (omOccurence != null)
		{
		    OMElement omRecurrence = omOccurence.getFirstChildWithName(getQName("recurrence"));
		    cItem.setRecurrence(omRecurrence.getText());
		    OMElement omTimes = omOccurence.getFirstChildWithName(getQName("times"));
		    cItem.setTimes(Long.valueOf(omTimes.getText()).longValue());
		}
		OMElement omContactPerson = omCalendarItem.getFirstChildWithName(getQName("contactPerson"));
		if (omContactPerson != null)
		{
		    OMElement omName = omContactPerson.getFirstChildWithName(getQName("name"));
		    cItem.setContactName(omName.getText());
		    OMElement omEmail = omContactPerson.getFirstChildWithName(getQName("email"));
		    cItem.setContactEmail(omEmail.getText());
		    OMElement omPhone = omContactPerson.getFirstChildWithName(getQName("phone"));
		    cItem.setContactPhone(omPhone.getText());
		}
		resultMap.put("calendar-item", cItem);
	    }

	}
	return resultMap;
    }

    @Override
    public HashMap<String, String> updateEvent(String id, String seriesId, String name, String location,
	    String date, String startTime, String endTime, String contactName, String contactEmail, String contactPhone)
	    throws Exception
    {
	HashMap<String, String> values = new HashMap<String, String>();
	OMElement payload = getCreateUpadeEventPayLoad(id, seriesId, CollaborationWSUtils.CALENDAR_MODIFY_OC, name,
		location, date, startTime, endTime, contactName, contactEmail, contactPhone, null, 1, true);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:updateEvent");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(getQName("statusCode"));
	values.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	return values;
    }

    @Override
    public HashMap<String, String> updateRecurrence(CalendarDTO event, String date, String recurrence,
	    long times) throws Exception
    {
	HashMap<String, String> values = new HashMap<String, String>();
	OMElement payload = getCreateUpadeEventPayLoad(event.getId(), event.getSeriesId(),
		CollaborationWSUtils.CALENDAR_MODIFY_SE, event.getName(), event.getLocation(), date,
		event.getStartTime(), event.getEndTime(), event.getContactName(), event.getContactEmail(), event
			.getContactPhone(), recurrence, times, true);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:updateEvent");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(getQName("statusCode"));
	values.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	return values;
    }

    @Override
    public HashMap<String, String> deleteEvent(String id) throws Exception
    {
	HashMap<String, String> values = new HashMap<String, String>();
	OMElement payload = getDeleteEventPayLoad(id);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:deleteCalendarItem");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(getQName("statusCode"));
	values.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	//
	return values;
    }

    private static OMElement createReadEventPayLoad(String eventId, String seriesId, String view)
    {
	OMElement method = fac.createOMElement("getCalendarItem", omNs);
	OMElement userElement = fac.createOMElement("username", omNs);
	userElement.addChild(fac.createOMText(userElement, USER_NAME));
	OMElement pwdElement = fac.createOMElement("password", omNs);
	pwdElement.addChild(fac.createOMText(pwdElement, USER_PWD));
	OMElement idElement = fac.createOMElement("itemID", omNs);
	idElement.addChild(fac.createOMText(idElement, eventId));
	OMElement sIdElement = fac.createOMElement("seriesID", omNs);
	sIdElement.addChild(fac.createOMText(sIdElement, seriesId));
	OMElement viewElement = fac.createOMElement("view", omNs);
	viewElement.addChild(fac.createOMText(viewElement, view));
	method.addChild(userElement);
	method.addChild(pwdElement);
	method.addChild(idElement);
	method.addChild(sIdElement);
	method.addChild(viewElement);
	return method;
    }

    private static OMElement getCreateEventPayLoad(String name, String location, String date, String startTime,
	    String endTime, String contactName, String contactEmail, String contactPhone, String recurrence, long times)
    {
	return getCreateUpadeEventPayLoad(null, null, null, name, location, date, startTime, endTime, contactName,
		contactEmail, contactPhone, recurrence, times, false);
    }

    private static OMElement getCreateUpadeEventPayLoad(String id, String seriesId, String modify, String name,
	    String location, String date, String startTime, String endTime, String contactName, String contactEmail,
	    String contactPhone, String recurrence, long times, boolean isUpdate)
    {
	OMElement method;
	if (!isUpdate)
	{
	    method = fac.createOMElement("createEvent", omNs);
	} else
	{
	    method = fac.createOMElement("updateEvent", omNs);
	}
	OMElement userElement = fac.createOMElement("username", omNs);
	userElement.addChild(fac.createOMText(userElement, USER_NAME));
	OMElement pwdElement = fac.createOMElement("password", omNs);
	pwdElement.addChild(fac.createOMText(pwdElement, USER_PWD));
	//
	OMElement subjectElement = fac.createOMElement("subject", omNs);
	subjectElement.addChild(fac.createOMText(subjectElement, name));
	OMElement locationElement = fac.createOMElement("location", omNs);
	locationElement.addChild(fac.createOMText(locationElement, location));
	OMElement dateElement = fac.createOMElement("date", omNs);
	dateElement.addChild(fac.createOMText(dateElement, date));
	OMElement startTimeElement = fac.createOMElement("startTime", omNs);
	startTimeElement.addChild(fac.createOMText(startTimeElement, startTime));
	OMElement endTimeElement = fac.createOMElement("endTime", omNs);
	endTimeElement.addChild(fac.createOMText(endTimeElement, endTime));
	OMElement participantElement = fac.createOMElement("participant", omNs);
	participantElement.addChild(fac.createOMText(participantElement, USER_NAME));

	//
	OMElement contactPersonElement = fac.createOMElement("contactPerson", omNs);
	OMElement nameElement = fac.createOMElement("name", omNs);
	nameElement.addChild(fac.createOMText(nameElement, contactName));
	OMElement emailElement = fac.createOMElement("email", omNs);
	emailElement.addChild(fac.createOMText(emailElement, contactEmail));
	OMElement phoneElement = fac.createOMElement("phone", omNs);
	phoneElement.addChild(fac.createOMText(phoneElement, contactPhone));
	contactPersonElement.addChild(nameElement);
	contactPersonElement.addChild(emailElement);
	contactPersonElement.addChild(phoneElement);
	//
	method.addChild(userElement);
	method.addChild(pwdElement);
	if (!isUpdate)
	{
	    OMElement idElement = fac.createOMElement("workspaceID", omNs);
	    idElement.addChild(fac.createOMText(idElement, workspaceIDStr));
	    method.addChild(idElement);
	} else
	{
	    OMElement idElement = fac.createOMElement("itemID", omNs);
	    idElement.addChild(fac.createOMText(idElement, id));
	    OMElement seriesIDElement = fac.createOMElement("seriesID", omNs);
	    seriesIDElement.addChild(fac.createOMText(seriesIDElement, seriesId));
	    OMElement modifyElement = fac.createOMElement("modify", omNs);
	    modifyElement.addChild(fac.createOMText(modifyElement, modify));
	    method.addChild(idElement);
	    method.addChild(seriesIDElement);
	    method.addChild(modifyElement);
	}

	method.addChild(subjectElement);
	method.addChild(locationElement);
	method.addChild(dateElement);
	method.addChild(startTimeElement);
	method.addChild(endTimeElement);
	// Occrurence
	if (recurrence != null)
	{
	    if (!(!isUpdate && recurrence.equals(REC_0)))
	    {
		OMElement occurenceElement = fac.createOMElement("occurence", omNs);
		OMElement recurrenceElement = fac.createOMElement("recurrence", omNs);
		recurrenceElement.addChild(fac.createOMText(recurrenceElement, recurrence));
		OMElement timesElement = fac.createOMElement("times", omNs);
		timesElement.addChild(fac.createOMText(timesElement, Long.valueOf(times).toString()));
		occurenceElement.addChild(recurrenceElement);
		occurenceElement.addChild(timesElement);
		method.addChild(occurenceElement);
	    }
	}
	method.addChild(contactPersonElement);
	method.addChild(participantElement);
	return method;
    }

    private static OMElement getDeleteEventPayLoad(String id)
    {
	OMElement method = fac.createOMElement("deleteCalendarItem", omNs);
	OMElement userElement = fac.createOMElement("username", omNs);
	userElement.addChild(fac.createOMText(userElement, USER_NAME));
	OMElement pwdElement = fac.createOMElement("password", omNs);
	pwdElement.addChild(fac.createOMText(pwdElement, USER_PWD));
	OMElement idElement = fac.createOMElement("itemID", omNs);
	idElement.addChild(fac.createOMText(idElement, id));
	method.addChild(userElement);
	method.addChild(pwdElement);
	method.addChild(idElement);

	return method;
    }

}
