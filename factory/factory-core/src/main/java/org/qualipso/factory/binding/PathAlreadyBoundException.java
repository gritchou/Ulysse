package org.qualipso.factory.binding;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June May 2009
 */
@SuppressWarnings("serial")
public class PathAlreadyBoundException extends BindingServiceException {
    public PathAlreadyBoundException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public PathAlreadyBoundException(String message) {
        super(message);
    }

    public PathAlreadyBoundException(Exception rootCause) {
        super(rootCause);
    }
}
