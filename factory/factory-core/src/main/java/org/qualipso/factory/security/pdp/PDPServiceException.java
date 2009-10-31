package org.qualipso.factory.security.pdp;

import org.qualipso.factory.FactoryException;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@SuppressWarnings("serial")
public class PDPServiceException extends FactoryException {
	public PDPServiceException(String message, Exception rootCause) {
		super(message, rootCause);
	}

	public PDPServiceException(String message) {
		super(message);
	}

	public PDPServiceException(Exception rootCause) {
		super(rootCause);
	}
}
