package org.qualipso.factory.security.pep;

import org.qualipso.factory.FactoryException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 30 november 2009
 */
@SuppressWarnings("serial")
public class AccessDeniedException extends FactoryException {
	/**
     * Class constructor specifying message and root cause.
     *
     * @param message
     * @param rootCause
     */
    public AccessDeniedException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     *
     * @param message
     */
    public AccessDeniedException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     *
     * @param rootCause
     */
    public AccessDeniedException(Exception rootCause) {
        super(rootCause);
    }
}
