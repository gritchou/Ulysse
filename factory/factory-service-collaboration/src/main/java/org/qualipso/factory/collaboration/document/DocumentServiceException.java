package org.qualipso.factory.collaboration.document;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * The Class DocumentServiceException.
 */
@WebFault
@SuppressWarnings("serial")
public class DocumentServiceException extends FactoryException {
    
    /**
     * Instantiates a new document service exception.
     * 
     * @param message the message
     * @param rootCause the root cause
     */
    public DocumentServiceException(String message, Exception rootCause) {
	super(message, rootCause);
    }

    /**
     * Instantiates a new document service exception.
     * 
     * @param message the message
     */
    public DocumentServiceException(String message) {
	super(message);
    }

    /**
     * Instantiates a new document service exception.
     * 
     * @param rootCause the root cause
     */
    public DocumentServiceException(Exception rootCause) {
	super(rootCause);
    }
}
