package org.qualipso.factory.ssh.authenticator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

public class DummyPasswordAuthenticator implements PasswordAuthenticator {
	
	private static Log logger = LogFactory.getLog(DummyPasswordAuthenticator.class);
	
	@Override
	public Object authenticate(String username, String password, ServerSession session) {
		logger.debug("authentication for user " + username + " password " + password);
		return username;
	}

}
