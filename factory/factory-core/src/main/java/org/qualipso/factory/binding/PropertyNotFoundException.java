package org.qualipso.factory.binding;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 18 June 2009
 */
@SuppressWarnings("serial")
public class PropertyNotFoundException extends BindingServiceException {
    public PropertyNotFoundException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public PropertyNotFoundException(String message) {
        super(message);
    }

    public PropertyNotFoundException(Exception rootCause) {
        super(rootCause);
    }
}
