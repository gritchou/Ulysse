package org.qualipso.factory.binding;



/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June 2009
 */
@SuppressWarnings("serial")
public class PathNotFoundException extends BindingServiceException {
    public PathNotFoundException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public PathNotFoundException(String message) {
        super(message);
    }

    public PathNotFoundException(Exception rootCause) {
        super(rootCause);
    }
}
