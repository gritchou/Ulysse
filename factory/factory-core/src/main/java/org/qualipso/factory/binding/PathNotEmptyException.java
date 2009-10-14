package org.qualipso.factory.binding;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 21 July 2009
 */
@SuppressWarnings("serial")
public class PathNotEmptyException extends BindingServiceException {
    public PathNotEmptyException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public PathNotEmptyException(String message) {
        super(message);
    }

    public PathNotEmptyException(Exception rootCause) {
        super(rootCause);
    }
}
