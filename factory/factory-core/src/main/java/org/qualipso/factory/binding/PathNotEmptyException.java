package org.qualipso.factory.binding;

/**
 * Thrown when a you try to unbind a path which has children
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 21 July 2009
 */
@SuppressWarnings("serial")
public class PathNotEmptyException extends BindingServiceException {
    
	/**
     * Class constructor specifying message and root cause.
     * 
     * @param message
     * @param rootCause
     */
    public PathNotEmptyException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     * 
     * @param message
     */
    public PathNotEmptyException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     * 
     * @param rootCause
     */
    public PathNotEmptyException(Exception rootCause) {
        super(rootCause);
    }
}
