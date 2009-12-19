package org.qualipso.factory.collaboration.calendar;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * The Class CalendarServiceException.
 */
@WebFault
@SuppressWarnings("serial")
public class CalendarServiceException extends FactoryException {

    /**
     * Instantiates a new calendar service exception.
     * 
     * @param message the message
     * @param rootCause the root cause
     */
    public CalendarServiceException(String message, Exception rootCause) {
	super(message, rootCause);
    }

    /**
     * Instantiates a new calendar service exception.
     * 
     * @param message the message
     */
    public CalendarServiceException(String message) {
	super(message);
    }

    /**
     * Instantiates a new calendar service exception.
     * 
     * @param rootCause the root cause
     */
    public CalendarServiceException(Exception rootCause) {
	super(rootCause);
    }
}
