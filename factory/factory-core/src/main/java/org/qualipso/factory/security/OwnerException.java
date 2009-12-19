package org.qualipso.factory.security;

import org.qualipso.factory.FactoryException;


@SuppressWarnings("serial")
public class OwnerException extends FactoryException {
    /**
     * Class constructor specifying message and root cause.
     *
     * @param message
     * @param rootCause
     */
    public OwnerException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     *
     * @param message
     */
    public OwnerException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     *
     * @param rootCause
     */
    public OwnerException(Exception rootCause) {
        super(rootCause);
    }
}
