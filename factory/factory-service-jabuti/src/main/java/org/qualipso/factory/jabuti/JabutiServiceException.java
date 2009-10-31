package org.qualipso.factory.jabuti;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * @author 
 * @date 
 */
@WebFault
@SuppressWarnings("serial")
public class JabutiServiceException extends FactoryException {
    public JabutiServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public JabutiServiceException(String message) {
        super(message);
    }

    public JabutiServiceException(Exception rootCause) {
        super(rootCause);
    }
}


