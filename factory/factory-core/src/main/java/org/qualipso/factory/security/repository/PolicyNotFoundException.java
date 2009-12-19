package org.qualipso.factory.security.repository;


@SuppressWarnings("serial")
public class PolicyNotFoundException extends PolicyRepositoryException {
    /**
     * Class constructor specifying message and root cause.
     *
     * @param message
     * @param rootCause
     */
    public PolicyNotFoundException(String message, Exception exception) {
        super(message, exception);
    }

    /**
     * Class constructor specifying message.
     *
     * @param message
     */
    public PolicyNotFoundException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     *
     * @param rootCause
     */
    public PolicyNotFoundException(Exception exception) {
        super(exception);
    }
}
