package org.qualipso.factory.collaboration.ws;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

// TODO: Auto-generated Javadoc
/**
 * The Class CollaborationWSUtils.
 */
public class CollaborationWSUtils {

    /** The Constant USER_NAME. */
    public static final String USER_NAME = CollaborationProperties
	    .getInstance().USER_NAME;

    /** The Constant USER_PWD. */
    protected static final String USER_PWD = CollaborationProperties
	    .getInstance().USER_PWD;

    /** The Constant DEFAULT_FOLDER_ID. */
    public static final String DEFAULT_FOLDER_ID = CollaborationProperties
	    .getInstance().DEFAULT_FOLDER_ID;

    /** The Constant DEFAULT_WORKSPACE_STR. */
    public static final String DEFAULT_WORKSPACE_STR = CollaborationProperties
	    .getInstance().DEFAULT_WORKSPACE_ID;

    /** The Constant DEFAULT_WORKSPACE. */
    public static final long DEFAULT_WORKSPACE = Long
	    .valueOf(DEFAULT_WORKSPACE_STR);
    //
    /** The Constant NAME_SPACE. */
    protected static final String NAME_SPACE = "http://mermig.ed.com";

    // Document Status
    /** The Constant STATUS_DRAFT. */
    public static final String STATUS_DRAFT = "DRAFT";

    /** The Constant STATUS_WORKING. */
    public static final String STATUS_WORKING = "WORKING";

    /** The Constant STATUS_NON_WORKING. */
    public static final String STATUS_NON_WORKING = "NON-WORKING";
    // Document Type
    /** The Constant TYPE_1. */
    public static final String TYPE_1 = "Deliverable";

    /** The Constant TYPE_2. */
    public static final String TYPE_2 = "Working document";

    /** The Constant TYPE_3. */
    public static final String TYPE_3 = "Administrative and financial";

    /** The Constant TYPE_4. */
    public static final String TYPE_4 = "Management Report";

    /** The Constant TYPE_5. */
    public static final String TYPE_5 = "Meeting Minutes";

    /** The Constant TYPE_6. */
    public static final String TYPE_6 = "Meeting Agenda";

    /** The Constant TYPE_7. */
    public static final String TYPE_7 = "FAQ";

    /** The Constant TYPE_8. */
    public static final String TYPE_8 = "Readme";
    // Forum Status
    /** The Constant FORUM_STATUS_ACTIVE. */
    public static final String FORUM_STATUS_ACTIVE = "active";

    /** The Constant FORUM_STATUS_CLOSED. */
    public static final String FORUM_STATUS_CLOSED = "closed";
    //
    /** The Constant WS_DATE_FORMAT. */
    public static final DateFormat WS_DATE_FORMAT = new SimpleDateFormat(
	    "yyyy-MM-dd");

    /** The Constant WS_TIME_FORMAT. */
    public static final DateFormat WS_TIME_FORMAT = new SimpleDateFormat(
	    "HH:mm");

    /** The Constant CALENDAR_MODIFY_SE. */
    public static final String CALENDAR_MODIFY_SE = "series";

    /** The Constant CALENDAR_MODIFY_OC. */
    public static final String CALENDAR_MODIFY_OC = "occurence";

    /** The Constant CALENDAR_EVENT. */
    public static final String CALENDAR_EVENT = "event";

    /** The Constant CALENDAR_MEETING. */
    public static final String CALENDAR_MEETING = "meeting";
    //
    /** The Constant REC_0. */
    public static final String REC_0 = "once";

    /** The Constant REC_1. */
    public static final String REC_1 = "daily";

    /** The Constant REC_2. */
    public static final String REC_2 = "weekly";

    /** The Constant REC_3. */
    public static final String REC_3 = "every2weeks";

    /** The Constant REC_4. */
    public static final String REC_4 = "monthlyByDate";

    /** The Constant REC_5. */
    public static final String REC_5 = "monthlyByWeekday";

    /** The Constant REC_6. */
    public static final String REC_6 = "yearly";

    /** The Constant REC_7. */
    public static final String REC_7 = "mondayToFriday";

    /** The Constant REC_8. */
    public static final String REC_8 = "monWedFri";

    /** The Constant REC_9. */
    public static final String REC_9 = "tueThu";

    /** The Constant SUCCESS_CODE. */
    public static final String SUCCESS_CODE = "SUCCESS";
    //    
    /** The Constant AUTHENTICATION_ERROR_CODE. */
    public static final String AUTHENTICATION_ERROR_CODE = "AUTHENTICATION_ERROR";

    /** The Constant AUTHORIZATION_ERROR_CODE. */
    public static final String AUTHORIZATION_ERROR_CODE = "AUTHORIZATION_ERROR";

    /** The Constant INTERNAL_OPERATION_ERROR_CODE. */
    public static final String INTERNAL_OPERATION_ERROR_CODE = "INTERNAL_OPERATION_ERROR";

    /** The Constant INTERNAL_BINDING_CODE. */
    public static final String INTERNAL_BINDING_CODE = "INTERNAL_BINDING_CODE";

    /** The Constant XSD_VALIDATION_ERROR_CODE. */
    public static final String XSD_VALIDATION_ERROR_CODE = "XSD_VALIDATION_ERROR";

    /** The Constant STATUS_CODE. */
    public static final String STATUS_CODE = "STATUS_CODE";

    /** The Constant STATUS_MESSAGE. */
    public static final String STATUS_MESSAGE = "STATUS_MESSAGE";

}
