package org.qualipso.factory.binding;

/**
 * Thrown when you try to get a property that is not define on this node or on one of this parent.
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June 2009
 */
@SuppressWarnings("serial")
public class PropertyNotFoundException extends BindingServiceException {
    
	/**
     * Class constructor specifying message and root cause.
     * 
     * @param message
     * @param rootCause
     */
    public PropertyNotFoundException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     * 
     * @param message
     */
    public PropertyNotFoundException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     * 
     * @param rootCause
     */
    public PropertyNotFoundException(Exception rootCause) {
        super(rootCause);
    }
}
