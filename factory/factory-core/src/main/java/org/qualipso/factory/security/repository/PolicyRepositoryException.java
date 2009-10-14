package org.qualipso.factory.security.repository;

import org.qualipso.factory.FactoryException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 28 August 2009
 */
@SuppressWarnings("serial")
public class PolicyRepositoryException extends FactoryException {
	public PolicyRepositoryException(String message, Exception exception) {
		super(message, exception);
	}

	public PolicyRepositoryException(String message) {
		super(message);
	}

	public PolicyRepositoryException(Exception exception) {
		super(exception);
	}
}
