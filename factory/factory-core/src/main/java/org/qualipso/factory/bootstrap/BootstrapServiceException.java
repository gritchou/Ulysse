package org.qualipso.factory.bootstrap;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 4 September 2009
 */
@WebFault
@SuppressWarnings("serial")
public class BootstrapServiceException extends FactoryException {
    public BootstrapServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public BootstrapServiceException(String message) {
        super(message);
    }

    public BootstrapServiceException(Exception rootCause) {
        super(rootCause);
    }
}