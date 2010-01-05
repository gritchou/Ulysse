package org.qualipso.factory.chat;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

@WebFault
@SuppressWarnings("serial")
public class ChatOSServiceException extends FactoryException {

    /**
     * Instantiates a new chat service exception.
     * 
     * @param message the message
     * @param rootCause the root cause
     */
    public ChatOSServiceException(String message, Exception rootCause) {
	super(message, rootCause);
    }

    /**
     * Instantiates a new chat service exception.
     * 
     * @param message the message
     */
    public ChatOSServiceException(String message) {
	super(message);
    }

    /**
     * Instantiates a new chat service exception.
     * 
     * @param rootCause the root cause
     */
    public ChatOSServiceException(Exception rootCause) {
	super(rootCause);
    }
}
