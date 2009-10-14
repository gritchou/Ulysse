package org.qualipso.factory.core;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 july 2009
 */
@WebFault
@SuppressWarnings("serial")
public class CoreServiceException extends FactoryException {
    public CoreServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public CoreServiceException(String message) {
        super(message);
    }

    public CoreServiceException(Exception rootCause) {
        super(rootCause);
    }
}

