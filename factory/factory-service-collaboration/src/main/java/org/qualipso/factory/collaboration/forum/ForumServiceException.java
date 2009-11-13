package org.qualipso.factory.collaboration.forum;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

@WebFault
@SuppressWarnings("serial")
public class ForumServiceException extends FactoryException {

    public ForumServiceException(String message, Exception rootCause) {
	super(message, rootCause);
    }

    public ForumServiceException(String message) {
	super(message);
    }

    public ForumServiceException(Exception rootCause) {
	super(rootCause);
    }
}
