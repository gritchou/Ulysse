package org.qualipso.factory.ssh;


@SuppressWarnings("serial")
public class SSHServiceException extends Exception {
    /**
     * Class constructor specifying message and root cause.
     *
     * @param message
     * @param rootCause
     */
    public SSHServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     *
     * @param message
     */
    public SSHServiceException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     *
     * @param rootCause
     */
    public SSHServiceException(Exception rootCause) {
        super(rootCause);
    }

    /**
     * Class constructor
     */
    public SSHServiceException() {
        super();
    }
}
