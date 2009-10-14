package org.qualipso.factory.search;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 26 June 2009
 */
@WebFault
@SuppressWarnings("serial")
public class SearchServiceException extends FactoryException {
    public SearchServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public SearchServiceException(String message) {
        super(message);
    }

    public SearchServiceException(Exception rootCause) {
        super(rootCause);
    }
}
