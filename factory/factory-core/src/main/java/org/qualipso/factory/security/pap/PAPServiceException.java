package org.qualipso.factory.security.pap;

import org.qualipso.factory.FactoryException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 May 2009
 */
@SuppressWarnings("serial")
public class PAPServiceException extends FactoryException {
    public PAPServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public PAPServiceException(String message) {
        super(message);
    }

    public PAPServiceException(Exception rootCause) {
        super(rootCause);
    }
}