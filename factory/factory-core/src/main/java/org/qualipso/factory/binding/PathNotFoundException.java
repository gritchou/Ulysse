package org.qualipso.factory.binding;



/**
 * Thrown when a you try to lookup a path that does not exists
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June 2009
 */
@SuppressWarnings("serial")
public class PathNotFoundException extends BindingServiceException {

	/**
     * Class constructor specifying message and root cause.
     * 
     * @param message
     * @param rootCause
     */
    public PathNotFoundException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     * 
     * @param message
     */
    public PathNotFoundException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     * 
     * @param rootCause
     */
    public PathNotFoundException(Exception rootCause) {
        super(rootCause);
    }

}
