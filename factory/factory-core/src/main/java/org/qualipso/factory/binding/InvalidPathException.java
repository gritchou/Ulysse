package org.qualipso.factory.binding;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June 2009
 */
@SuppressWarnings("serial")
public class InvalidPathException extends BindingServiceException {
    public InvalidPathException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public InvalidPathException(String message) {
        super(message);
    }

    public InvalidPathException(Exception rootCause) {
        super(rootCause);
    }
}
