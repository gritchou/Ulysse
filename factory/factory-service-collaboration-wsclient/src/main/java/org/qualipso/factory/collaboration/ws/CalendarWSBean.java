package org.qualipso.factory.collaboration.ws;

import java.util.HashMap;
import java.util.Iterator;

import javax.ejb.Stateless;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.collaboration.ws.beans.CalendarDTO;

/**
 * The Class CalendarWSBean.
 */
@Stateless(name = "CalendarWSBean", mappedName = "CalendarWService")
public class CalendarWSBean extends CollaborationWSUtils implements
	CalendarWService {

    /**
     * Instantiates a new calendar ws bean.
     */
    public CalendarWSBean() {

    }

    /** The target epr. */
    private EndpointReference targetEPR = new EndpointReference(
	    CollaborationProperties.getInstance().MERMIG_ENDPOINT);

    /** The fac. */
    private static OMFactory fac = OMAbstractFactory.getOMFactory();

    /** The om ns. */
    private static OMNamespace omNs = fac.createOMNamespace(
	    CollaborationWSUtils.NAME_SPACE, "ns");

    /** The logger. */
    private static Log logger = LogFactory.getLog(CalendarWSBean.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.CalendarWService#createEvent(org
     * .qualipso.factory.collaboration.ws.beans.CalendarDTO)
     */
    @Override
    public HashMap<String, Object> createEvent(CalendarDTO calendarDTO)
	    throws Exception {
	return createEvent(calendarDTO.getName(), calendarDTO.getLocation(),
		calendarDTO.getDate(), calendarDTO.getStartTime(), calendarDTO
			.getEndTime(), calendarDTO.getContactName(),
		calendarDTO.getContactEmail(), calendarDTO.getContactPhone(),
		calendarDTO.getRecurrence(), calendarDTO.getTimes());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.CalendarWService#createEvent(java
     * .lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, long)
     */
    @Override
    public HashMap<String, Object> createEvent(String name, String location,
	    String date, String startTime, String endTime, String contactName,
	    String contactEmail, String contactPhone, String recurrence,
	    long times) throws Exception {
	HashMap<String, Object> resultMap = new HashMap<String, Object>(1);
	OMElement payload = getCreateEventPayLoad(name, location, date,
		startTime, endTime, contactName, contactEmail, contactPhone,
		recurrence, times);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:createEvent");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	resultMap.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	resultMap.put("statusMessage", omStatusMsg.getText());
	//
	if (omStatus.getText().equals(SUCCESS_CODE)) {
	    OMElement omSeriesID = result.getFirstChildWithName(OMEUtils
		    .getQName("seriesID"));
	    resultMap.put("seriesID", omSeriesID.getText());
	    Iterator<OMElement> iter = result.getChildrenWithName(OMEUtils
		    .getQName("occurenceId"));
	    if (iter != null) {
		HashMap<String, String> ocIds = new HashMap<String, String>();
		while (iter.hasNext()) {
		    OMElement ocElement = (OMElement) iter.next();
		    ocIds.put(ocElement.getText(), ocElement.getText());
		}
		resultMap.put("occurenceIds", ocIds);
	    }
	}
	return resultMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.CalendarWService#readEvent(java
     * .lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public HashMap<String, Object> readEvent(String itemId, String seriesId,
	    String view) throws Exception {

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
	OMElement omStatus = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	resultMap.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	resultMap.put("statusMessage", omStatusMsg.getText());
	if (omStatus.getText().equals(SUCCESS_CODE)) {
	    OMElement omCalendarItem = result.getFirstChildWithName(OMEUtils
		    .getQName("calendarItem"));
	    if (omCalendarItem != null) {
		CalendarDTO cItem = new CalendarDTO();
		// Basically we only need the values not peristed on factory.
		// OMElement omItemID =
		// omCalendarItem.getFirstChildWithName(OMEUtils.getQName("itemID"));
		// OMElement omSeriesID =
		// omCalendarItem.getFirstChildWithName(OMEUtils.getQName("seriesID"));
		// OMElement omSubject =
		// omCalendarItem.getFirstChildWithName(OMEUtils.getQName("subject"));
		// cItem.setName(omSubject.getText());
		OMElement omLocation = omCalendarItem
			.getFirstChildWithName(OMEUtils.getQName("location"));
		cItem.setLocation(omLocation.getText());
		OMElement omDate = omCalendarItem
			.getFirstChildWithName(OMEUtils.getQName("date"));
		cItem.setDate(omDate.getText());
		OMElement omStartTime = omCalendarItem
			.getFirstChildWithName(OMEUtils.getQName("startTime"));
		cItem.setStartTime(omStartTime.getText());
		OMElement omEndTime = omCalendarItem
			.getFirstChildWithName(OMEUtils.getQName("endTime"));
		cItem.setEndTime(omEndTime.getText());
		OMElement omOccurence = omCalendarItem
			.getFirstChildWithName(OMEUtils.getQName("occurence"));
		if (omOccurence != null) {
		    OMElement omRecurrence = omOccurence
			    .getFirstChildWithName(OMEUtils
				    .getQName("recurrence"));
		    cItem.setRecurrence(omRecurrence.getText());
		    OMElement omTimes = omOccurence
			    .getFirstChildWithName(OMEUtils.getQName("times"));
		    cItem.setTimes(Long.valueOf(omTimes.getText()).longValue());
		}
		OMElement omContactPerson = omCalendarItem
			.getFirstChildWithName(OMEUtils
				.getQName("contactPerson"));
		if (omContactPerson != null) {
		    OMElement omName = omContactPerson
			    .getFirstChildWithName(OMEUtils.getQName("name"));
		    cItem.setContactName(omName.getText());
		    OMElement omEmail = omContactPerson
			    .getFirstChildWithName(OMEUtils.getQName("email"));
		    cItem.setContactEmail(omEmail.getText());
		    OMElement omPhone = omContactPerson
			    .getFirstChildWithName(OMEUtils.getQName("phone"));
		    cItem.setContactPhone(omPhone.getText());
		}
		// attachments
		OMElement omAttachments = omCalendarItem
			.getFirstChildWithName(OMEUtils.getQName("attachments"));
		if (omAttachments != null) {
		    HashMap<String, String> attachments = new HashMap<String, String>();
		    Iterator attIterator = omAttachments
			    .getChildrenWithName(OMEUtils
				    .getQName("attachment"));
		    if (attIterator != null) {
			while (attIterator.hasNext()) {
			    OMElement attEl = (OMElement) attIterator.next();
			    if (attEl != null) {
				OMElement omDocId = attEl
					.getFirstChildWithName(OMEUtils
						.getQName("documentID"));
				OMElement omDocName = attEl
					.getFirstChildWithName(OMEUtils
						.getQName("documentName"));
				if (omDocId != null
					&& omDocId.getText() != null
					&& omDocName != null
					&& omDocName.getText() != null) {
				    attachments.put(omDocId.getText(),
					    omDocName.getText());
				}
			    }
			}
		    }
		    cItem.setAttachments(attachments);
		}
		// forum
		OMElement omForumList = omCalendarItem
			.getFirstChildWithName(OMEUtils.getQName("forumList"));
		if (omForumList != null) {
		    Iterator forumIterator = omForumList
			    .getChildrenWithName(OMEUtils
				    .getQName("forumItem"));
		    if (forumIterator != null) {
			while (forumIterator.hasNext()) {
			    OMElement foElement = (OMElement) forumIterator
				    .next();
			    if (foElement != null) {
				OMElement omFId = foElement
					.getFirstChildWithName(OMEUtils
						.getQName("id"));
				// OMElement omFName =
				// foElement.getFirstChildWithName(OMEUtils.getQName("name"));
				if (omFId != null && omFId.getText() != null) {
				    cItem.setForum(omFId.getText());
				}
			    }
			}
		    }
		}
		//
		logger.debug(cItem.toString());
		resultMap.put("calendar-item", cItem);
	    }

	}
	return resultMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.CalendarWService#updateEvent(java
     * .lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public HashMap<String, String> updateEvent(String itemId, String seriesId,
	    String name, String location, String date, String startTime,
	    String endTime, String contactName, String contactEmail,
	    String contactPhone) throws Exception {
	HashMap<String, String> values = new HashMap<String, String>();
	OMElement payload = getCreateUpadeEventPayLoad(itemId, seriesId,
		CollaborationWSUtils.CALENDAR_MODIFY_OC, name, location, date,
		startTime, endTime, contactName, contactEmail, contactPhone,
		null, 1, true);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:updateEvent");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	values.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	return values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.CalendarWService#updateRecurrence
     * (org.qualipso.factory.collaboration.ws.beans.CalendarDTO,
     * java.lang.String, java.lang.String, long)
     */
    @Override
    public HashMap<String, String> updateRecurrence(CalendarDTO event,
	    String date, String recurrence, long times) throws Exception {
	HashMap<String, String> values = new HashMap<String, String>();
	OMElement payload = getCreateUpadeEventPayLoad(event.getId(), event
		.getSeriesId(), CollaborationWSUtils.CALENDAR_MODIFY_SE, event
		.getName(), event.getLocation(), date, event.getStartTime(),
		event.getEndTime(), event.getContactName(), event
			.getContactEmail(), event.getContactPhone(),
		recurrence, times, true);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:updateEvent");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	values.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	return values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.CalendarWService#deleteEvent(java
     * .lang.String)
     */
    @Override
    public HashMap<String, String> deleteEvent(String itemId) throws Exception {
	HashMap<String, String> values = new HashMap<String, String>();
	OMElement payload = getDeleteEventPayLoad(itemId);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:deleteCalendarItem");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	values.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	values.put("statusMessage", omStatusMsg.getText());
	//
	return values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.CalendarWService#atttachDocumentToEvent
     * (java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String[])
     */
    @Override
    public HashMap<String, String> atttachDocumentToEvent(String itemId,
	    String seriesId, String modify, String[] documents)
	    throws Exception {
	HashMap<String, String> resultMap = new HashMap<String, String>(1);
	OMElement payload = createAttachmentPayload(itemId, seriesId, modify,
		documents, null);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:setAttachmentsForCalendarItem");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	resultMap.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	resultMap.put("statusMessage", omStatusMsg.getText());
	return resultMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.qualipso.factory.collaboration.ws.CalendarWService#atttachForumToEvent
     * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public HashMap<String, String> atttachForumToEvent(String itemId,
	    String seriesId, String modify, String forumId) throws Exception {
	HashMap<String, String> resultMap = new HashMap<String, String>(1);
	OMElement payload = createAttachmentPayload(itemId, seriesId, modify,
		null, forumId);
	logger.info(payload.toString());
	Options options = new Options();
	options.setTo(targetEPR);
	options.setAction("urn:setAttachmentsForCalendarItem");
	// Blocking invocation
	ServiceClient sender = new ServiceClient();
	sender.setOptions(options);
	OMElement result = sender.sendReceive(payload);
	logger.info(result.toString());
	OMElement omStatus = result.getFirstChildWithName(OMEUtils
		.getQName("statusCode"));
	resultMap.put("statusCode", omStatus.getText());
	OMElement omStatusMsg = result.getFirstChildWithName(OMEUtils
		.getQName("statusMessage"));
	resultMap.put("statusMessage", omStatusMsg.getText());
	return resultMap;
    }

    private static OMElement createAttachmentPayload(String itemId,
	    String seriesId, String modify, String[] documents, String forumId) {
	OMElement method = fac.createOMElement("setAttachmentsForCalendarItem",
		omNs);
	OMElement userElement = fac.createOMElement("username", omNs);
	userElement.addChild(fac.createOMText(userElement, USER_NAME));
	OMElement pwdElement = fac.createOMElement("password", omNs);
	pwdElement.addChild(fac.createOMText(pwdElement, USER_PWD));
	OMElement idElement = fac.createOMElement("itemID", omNs);
	idElement.addChild(fac.createOMText(idElement, itemId));
	OMElement sIdElement = fac.createOMElement("seriesID", omNs);
	sIdElement.addChild(fac.createOMText(sIdElement, seriesId));
	OMElement modifyElement = fac.createOMElement("modify", omNs);
	modifyElement.addChild(fac.createOMText(modifyElement, modify));
	method.addChild(userElement);
	method.addChild(pwdElement);
	method.addChild(idElement);
	method.addChild(sIdElement);
	method.addChild(modifyElement);
	if (documents != null && documents.length > 0) {
	    for (int j = 0; j < documents.length; j++) {
		OMElement attachmentElement = fac.createOMElement("attachment",
			omNs);
		attachmentElement.addChild(fac.createOMText(attachmentElement,
			documents[j]));
		method.addChild(attachmentElement);
	    }
	}
	if (forumId != null) {
	    OMElement forumElement = fac.createOMElement("forumItem", omNs);
	    forumElement.addChild(fac.createOMText(forumElement, forumId));
	    method.addChild(forumElement);
	}
	return method;
    }

    /**
     * Creates the read event pay load.
     * 
     * @param eventId
     *            the event id
     * @param seriesId
     *            the series id
     * @param view
     *            the view
     * 
     * @return the oM element
     */
    private static OMElement createReadEventPayLoad(String eventId,
	    String seriesId, String view) {
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

    /**
     * Gets the creates the event pay load.
     * 
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
     * @param recurrence
     *            the recurrence
     * @param times
     *            the times
     * 
     * @return the creates the event pay load
     */
    private static OMElement getCreateEventPayLoad(String name,
	    String location, String date, String startTime, String endTime,
	    String contactName, String contactEmail, String contactPhone,
	    String recurrence, long times) {
	return getCreateUpadeEventPayLoad(null, null, null, name, location,
		date, startTime, endTime, contactName, contactEmail,
		contactPhone, recurrence, times, false);
    }

    /**
     * Gets the creates the upade event pay load.
     * 
     * @param id
     *            the id
     * @param seriesId
     *            the series id
     * @param modify
     *            the modify
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
     * @param recurrence
     *            the recurrence
     * @param times
     *            the times
     * @param isUpdate
     *            the is update
     * 
     * @return the creates the upade event pay load
     */
    private static OMElement getCreateUpadeEventPayLoad(String id,
	    String seriesId, String modify, String name, String location,
	    String date, String startTime, String endTime, String contactName,
	    String contactEmail, String contactPhone, String recurrence,
	    long times, boolean isUpdate) {
	OMElement method;
	if (!isUpdate) {
	    method = fac.createOMElement("createEvent", omNs);
	} else {
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
	startTimeElement
		.addChild(fac.createOMText(startTimeElement, startTime));
	OMElement endTimeElement = fac.createOMElement("endTime", omNs);
	endTimeElement.addChild(fac.createOMText(endTimeElement, endTime));
	OMElement participantElement = fac.createOMElement("participant", omNs);
	participantElement.addChild(fac.createOMText(participantElement,
		USER_NAME));

	//
	OMElement contactPersonElement = fac.createOMElement("contactPerson",
		omNs);
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
	if (!isUpdate) {
	    OMElement idElement = fac.createOMElement("workspaceID", omNs);
	    idElement.addChild(fac.createOMText(idElement,
		    DEFAULT_WORKSPACE_STR));
	    method.addChild(idElement);
	} else {
	    OMElement idElement = fac.createOMElement("itemID", omNs);
	    idElement.addChild(fac.createOMText(idElement, id));
	    OMElement seriesIDElement = fac.createOMElement("seriesID", omNs);
	    seriesIDElement.addChild(fac
		    .createOMText(seriesIDElement, seriesId));
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
	if (recurrence != null) {
	    if (!(!isUpdate && recurrence.equals(REC_0))) {
		OMElement occurenceElement = fac.createOMElement("occurence",
			omNs);
		OMElement recurrenceElement = fac.createOMElement("recurrence",
			omNs);
		recurrenceElement.addChild(fac.createOMText(recurrenceElement,
			recurrence));
		OMElement timesElement = fac.createOMElement("times", omNs);
		timesElement.addChild(fac.createOMText(timesElement, Long
			.valueOf(times).toString()));
		occurenceElement.addChild(recurrenceElement);
		occurenceElement.addChild(timesElement);
		method.addChild(occurenceElement);
	    }
	}
	method.addChild(contactPersonElement);
	method.addChild(participantElement);
	return method;
    }

    /**
     * Gets the delete event pay load.
     * 
     * @param id
     *            the id
     * 
     * @return the delete event pay load
     */
    private static OMElement getDeleteEventPayLoad(String id) {
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
