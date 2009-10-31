package org.qualipso.factory.browser;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 16 june 2009
 */
@WebFault
@SuppressWarnings("serial")
public class BrowserServiceException extends FactoryException {
    public BrowserServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public BrowserServiceException(String message) {
        super(message);
    }

    public BrowserServiceException(Exception rootCause) {
        super(rootCause);
    }
}

