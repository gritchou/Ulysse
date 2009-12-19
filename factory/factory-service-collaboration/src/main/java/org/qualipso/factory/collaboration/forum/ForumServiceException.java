package org.qualipso.factory.collaboration.forum;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * The Class ForumServiceException.
 */
@WebFault
@SuppressWarnings("serial")
public class ForumServiceException extends FactoryException {

    /**
     * Instantiates a new forum service exception.
     * 
     * @param message the message
     * @param rootCause the root cause
     */
    public ForumServiceException(String message, Exception rootCause) {
	super(message, rootCause);
    }

    /**
     * Instantiates a new forum service exception.
     * 
     * @param message the message
     */
    public ForumServiceException(String message) {
	super(message);
    }

    /**
     * Instantiates a new forum service exception.
     * 
     * @param rootCause the root cause
     */
    public ForumServiceException(Exception rootCause) {
	super(rootCause);
    }
}
