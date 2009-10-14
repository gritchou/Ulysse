package org.qualipso.factory.membership;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 8 june 2009
 */
@WebFault
@SuppressWarnings("serial")
public class MembershipServiceException extends FactoryException {
    public MembershipServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public MembershipServiceException(String message) {
        super(message);
    }

    public MembershipServiceException(Exception rootCause) {
        super(rootCause);
    }
}
