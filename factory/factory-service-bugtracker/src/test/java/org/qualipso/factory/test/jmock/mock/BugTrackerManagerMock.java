package org.qualipso.factory.test.jmock.mock;

import org.mantisbt.connect.IMCSession;
import org.qualipso.factory.bugtracker.core.BugTrackerManager;
import org.qualipso.factory.bugtracker.exception.BugTrackerServiceException;

/**
 * 
 * BugTrackerManager Mock (use MCSession mock)
 *
 */
public class BugTrackerManagerMock extends BugTrackerManager {

	public BugTrackerManagerMock() throws BugTrackerServiceException {
		super();
	}

	/* (non-Javadoc)
	 * @see org.qualipso.factory.bugtracker.core.BugTrackerManager#createWSSession()
	 */
	@Override
	protected IMCSession createWSSession() throws BugTrackerServiceException {
		return new MCSessionMock();
	}
	
	

}
