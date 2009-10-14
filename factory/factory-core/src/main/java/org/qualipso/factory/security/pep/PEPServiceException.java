package org.qualipso.factory.security.pep;

import org.qualipso.factory.FactoryException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 26 May 2009
 */
@SuppressWarnings("serial")
public class PEPServiceException extends FactoryException {
    public PEPServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public PEPServiceException(String message) {
        super(message);
    }

    public PEPServiceException(Exception rootCause) {
        super(rootCause);
    }
}