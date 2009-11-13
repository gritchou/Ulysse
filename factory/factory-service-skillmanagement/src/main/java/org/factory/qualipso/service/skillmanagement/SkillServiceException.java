package org.factory.qualipso.service.skillmanagement;

import javax.xml.ws.WebFault;
import org.qualipso.factory.FactoryException;

/**
 * @author Carmine De Nicola 
 * @date 24 july 2009
 */


@WebFault
@SuppressWarnings("serial")

public class SkillServiceException extends FactoryException {
	
	public SkillServiceException(String message, Exception rootCause){
		super(message,rootCause);
	}
	
	public SkillServiceException(String message) {		
        super(message);
    }

    public SkillServiceException(Exception rootCause) {
        super(rootCause);
    }

}
