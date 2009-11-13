package org.qualipso.factory.binding;


/**
 * Thrown when a you try to bind a path that is already binded to another FactoryResourceIdentifier.
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June May 2009
 */
@SuppressWarnings("serial")
public class PathAlreadyBoundException extends BindingServiceException {
	
	/**
     * Class constructor specifying message and root cause.
     * 
     * @param message
     * @param rootCause
     */
    public PathAlreadyBoundException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     * 
     * @param message
     */
    public PathAlreadyBoundException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     * 
     * @param rootCause
     */
    public PathAlreadyBoundException(Exception rootCause) {
        super(rootCause);
    }
}
