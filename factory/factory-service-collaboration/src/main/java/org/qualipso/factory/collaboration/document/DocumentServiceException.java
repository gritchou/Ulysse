package org.qualipso.factory.collaboration.document;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

@WebFault
@SuppressWarnings("serial")
public class DocumentServiceException extends FactoryException {
    public DocumentServiceException(String message, Exception rootCause) {
	super(message, rootCause);
    }

    public DocumentServiceException(String message) {
	super(message);
    }

    public DocumentServiceException(Exception rootCause) {
	super(rootCause);
    }
}
