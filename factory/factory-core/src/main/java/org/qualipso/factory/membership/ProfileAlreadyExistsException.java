package org.qualipso.factory.membership;

import javax.xml.ws.WebFault;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 9 june 2009
 */
@WebFault
@SuppressWarnings("serial")
public class ProfileAlreadyExistsException extends MembershipServiceException {
    public ProfileAlreadyExistsException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public ProfileAlreadyExistsException(String message) {
        super(message);
    }

    public ProfileAlreadyExistsException(Exception rootCause) {
        super(rootCause);
    }
}
