package org.qualipso.factory.git;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 21 september 2009
 */
@WebFault
@SuppressWarnings("serial")
public class GITServiceException extends FactoryException {
    public GITServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public GITServiceException(String message) {
        super(message);
    }

    public GITServiceException(Exception rootCause) {
        super(rootCause);
    }
}


