package org.qualipso.factory.collaboration.ws;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CollaborationWSUtils {
    public static final String USER_NAME = CollaborationProperties
	    .getInstance().USER_NAME;
    protected static final String USER_PWD = CollaborationProperties
	    .getInstance().USER_PWD;
    public static final String DEFAULT_FOLDER_ID = CollaborationProperties
	    .getInstance().DEFAULT_FOLDER_ID;
    public static final String DEFAULT_WORKSPACE_STR = CollaborationProperties
	    .getInstance().DEFAULT_WORKSPACE_ID;
    public static final long DEFAULT_WORKSPACE = Long
	    .valueOf(DEFAULT_WORKSPACE_STR);
    //
    protected static final String NAME_SPACE = "http://mermig.ed.com";

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
    public static final DateFormat WS_DATE_FORMAT = new SimpleDateFormat(
	    "yyyy-MM-dd");
    public static final DateFormat WS_TIME_FORMAT = new SimpleDateFormat(
	    "HH:mm");
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

}
