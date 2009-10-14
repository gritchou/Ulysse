package org.qualipso.factory.voipservice;

import javax.xml.ws.WebFault;

import org.qualipso.factory.FactoryException;

/**
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */
@WebFault
@SuppressWarnings("serial")
public class VoIPConferenceServiceException extends FactoryException {
    public VoIPConferenceServiceException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public VoIPConferenceServiceException(String message) {
        super(message);
    }

    public VoIPConferenceServiceException(Exception rootCause) {
        super(rootCause);
    }
}
