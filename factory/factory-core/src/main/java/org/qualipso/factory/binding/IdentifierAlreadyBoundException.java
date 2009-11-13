package org.qualipso.factory.binding;

/**
 * Thrown when you try to bind a FactoryResourceIdentifier that is already binded somewhere in the naming tree.
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June May 2009
 */
@SuppressWarnings("serial")
public class IdentifierAlreadyBoundException extends BindingServiceException {
    
	/**
     * Class constructor specifying message and root cause.
     * 
     * @param message
     * @param rootCause
     */
    public IdentifierAlreadyBoundException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    /**
     * Class constructor specifying message.
     * 
     * @param message
     */
    public IdentifierAlreadyBoundException(String message) {
        super(message);
    }

    /**
     * Class constructor specifying root cause
     * 
     * @param rootCause
     */
    public IdentifierAlreadyBoundException(Exception rootCause) {
        super(rootCause);
    }
}
