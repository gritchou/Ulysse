package org.qualipso.factory.collaboration.ws;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.addressing.EndpointReference;

public class CollaborationWSUtils
{
    protected static EndpointReference targetEPR = new EndpointReference("http://syros.eurodyn.com:8082/mermig/services/MermigWebService");
    protected static final String NAME_SPACE = "http://mermig.ed.com";

    protected static OMFactory fac = OMAbstractFactory.getOMFactory();
    protected static OMNamespace omNs = fac.createOMNamespace(NAME_SPACE, "ns");
    public static final String USER_NAME = "demode";
    public static final String USER_PWD = "demode123";
    public static final String DEFAULT_FOLDER_ID = "1170";
    // Document Status
    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_WORKING = "WORKING";
    public static final String STATUS_NON_WORKING = "NON-WORKING";
    // Document Type
    public static final String TYPE_1 = "Deliverable";
    public static final String TYPE_2 = "Working document";
    public static final String TYPE_3 = "Administrative and financial";
    public static final String TYPE_4 = "Management Report";
    public static final String TYPE_5 = "Meeting Minutes";
    public static final String TYPE_6 = "Meeting Agenda";
    public static final String TYPE_7 = "FAQ";
    public static final String TYPE_8 = "Readme";
    // Forum Status
    public static final String FORUM_STATUS_ACTIVE = "active";
    public static final String FORUM_STATUS_CLOSED = "closed";
    
    //
    public static final String workspaceIDStr = "10";
    public static final long workspaceID = Long.valueOf(workspaceIDStr);
    
    public static final DateFormat WS_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat WS_TIME_FORMAT = new SimpleDateFormat("HH:mm");
    public static final String CALENDAR_MODIFY_SE = "series";
    public static final String CALENDAR_MODIFY_OC = "occurence";
    public static final String CALENDAR_EVENT = "event";
    public static final String CALENDAR_MEETING = "meeting";
    //
    public static final String REC_0 = "once";
    public static final String REC_1 = "daily";
    public static final String REC_2 = "weekly";
    public static final String REC_3 = "every2weeks";
    public static final String REC_4 = "monthlyByDate";
    public static final String REC_5 = "monthlyByWeekday";
    public static final String REC_6 = "yearly";
    public static final String REC_7 = "mondayToFriday";
    public static final String REC_8 = "monWedFri";
    public static final String REC_9 = "tueThu";

    public static final String SUCCESS_CODE = "SUCCESS";
    //    
    public static final String AUTHENTICATION_ERROR_CODE = "AUTHENTICATION_ERROR";
    public static final String AUTHORIZATION_ERROR_CODE = "AUTHORIZATION_ERROR";
    public static final String INTERNAL_OPERATION_ERROR_CODE = "INTERNAL_OPERATION_ERROR";
    public static final String INTERNAL_BINDING_CODE = "INTERNAL_BINDING_CODE";
    public static final String XSD_VALIDATION_ERROR_CODE = "XSD_VALIDATION_ERROR";
    public static final String STATUS_CODE = "STATUS_CODE";
    public static final String STATUS_MESSAGE = "STATUS_MESSAGE";

    public static QName getQName(String localName)
    {
	return (new QName(NAME_SPACE, localName));
    }
    
    public static OMElement getInfoPayLoad()
    {
	OMElement method = fac.createOMElement("getInfo", omNs);
	OMElement userElement = fac.createOMElement("username", omNs);
	userElement.addChild(fac.createOMText(userElement, USER_NAME));
	OMElement pwdElement = fac.createOMElement("password", omNs);
	pwdElement.addChild(fac.createOMText(pwdElement, USER_PWD));

	method.addChild(userElement);
	method.addChild(pwdElement);

	return method;
    }
    
    public static String getFirstElement(HashMap valuesMap) throws Exception
    {
	String id = "";
	Iterator iterator = valuesMap.keySet().iterator();
	while( iterator. hasNext() ){
	    id = (String)valuesMap.get(iterator.next());
	    break;
	}
	return id;
    }
}
