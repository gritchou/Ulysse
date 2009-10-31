package org.qualipso.factory.greeting;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 june 2009
 */
@WebFault
@SuppressWarnings("serial")
public class GreetingServiceException extends FactoryException {
    public GreetingServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public GreetingServiceException(String message) {
        super(message);
    }

    public GreetingServiceException(Exception rootCause) {
        super(rootCause);
    }
}

