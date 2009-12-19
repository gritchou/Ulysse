package org.qualipso.factory.security.repository;

@SuppressWarnings("serial")
public class PolicyAlreadyExistsException extends PolicyRepositoryException {
    /**
     * Class constructor specifying message and root cause.
     *
     * @param message
     * @param rootCause
     */
    public PolicyAlreadyExistsException(String message, Exception exception) {
        super(message, exception);
    }

    /**
     * Class constructor specifying message.
     *
     * @param message
     */
    public PolicyAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     *
     * @param rootCause
     */
    public PolicyAlreadyExistsException(Exception exception) {
        super(exception);
    }
}
