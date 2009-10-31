package org.qualipso.factory.binding;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June May 2009
 */
@SuppressWarnings("serial")
public class IdentifierAlreadyBoundException extends BindingServiceException {
    public IdentifierAlreadyBoundException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public IdentifierAlreadyBoundException(String message) {
        super(message);
    }

    public IdentifierAlreadyBoundException(Exception rootCause) {
        super(rootCause);
    }
}
