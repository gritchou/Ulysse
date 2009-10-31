package org.qualipso.factory.subversion;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * @author Emmanuel Rias (emmanuel.rias@bull.net)
 * @date 30 june 2009
 */
@WebFault
@SuppressWarnings("serial")
public class SubversionServiceException extends FactoryException {
    public SubversionServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public SubversionServiceException(String message) {
        super(message);
    }

    public SubversionServiceException(Exception rootCause) {
        super(rootCause);
    }
}

