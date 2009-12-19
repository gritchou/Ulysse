package org.qualipso.factory.security;

import org.qualipso.factory.FactoryException;

@SuppressWarnings("serial")
public class SubjectException extends FactoryException {
    /**
     * Class constructor specifying message and root cause.
     *
     * @param message
     * @param rootCause
     */
    public SubjectException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     *
     * @param message
     */
    public SubjectException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     *
     * @param rootCause
     */
    public SubjectException(Exception rootCause) {
        super(rootCause);
    }
}
