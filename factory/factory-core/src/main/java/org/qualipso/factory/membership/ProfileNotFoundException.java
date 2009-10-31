package org.qualipso.factory.membership;

import javax.xml.ws.WebFault;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 9 june 2009
 */
@WebFault
@SuppressWarnings("serial")
public class ProfileNotFoundException extends MembershipServiceException {
    public ProfileNotFoundException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException(Exception rootCause) {
        super(rootCause);
    }
}
