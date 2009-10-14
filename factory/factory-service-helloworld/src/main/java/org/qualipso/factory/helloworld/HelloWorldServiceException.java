package org.qualipso.factory.helloworld;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 june 2009
 */
@WebFault
@SuppressWarnings("serial")
public class HelloWorldServiceException extends FactoryException {
    public HelloWorldServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public HelloWorldServiceException(String message) {
        super(message);
    }

    public HelloWorldServiceException(Exception rootCause) {
        super(rootCause);
    }
}


