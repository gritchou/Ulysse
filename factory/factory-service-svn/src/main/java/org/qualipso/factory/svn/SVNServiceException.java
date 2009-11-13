package org.qualipso.factory.svn;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * @author Gerald Oster (oster@loria.fr)
 * @date 9 October 2009
 */
@WebFault
@SuppressWarnings("serial")
public class SVNServiceException extends FactoryException {
    public SVNServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public SVNServiceException(String message) {
        super(message);
    }

    public SVNServiceException(Exception rootCause) {
        super(rootCause);
    }
}


