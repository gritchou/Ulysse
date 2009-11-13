package org.qualipso.factory.binding;


/**
 * Thrown when a path is not valid (ie does not fullfill with PathHelper.valid())
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June 2009
 */
@SuppressWarnings("serial")
public class InvalidPathException extends BindingServiceException {
    
	/**
     * Class constructor specifying message and root cause.
     * 
     * @param message
     * @param rootCause
     */
    public InvalidPathException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     * 
     * @param message
     */
    public InvalidPathException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     * 
     * @param rootCause
     */
    public InvalidPathException(Exception rootCause) {
        super(rootCause);
    }
}
