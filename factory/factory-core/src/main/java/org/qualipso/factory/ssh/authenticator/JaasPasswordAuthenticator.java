package org.qualipso.factory.ssh.authenticator;

import javax.security.auth.login.LoginContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.jboss.security.auth.callback.UsernamePasswordHandler;
import org.qualipso.factory.ssh.session.SSHServerSession;

public class JaasPasswordAuthenticator implements PasswordAuthenticator {

	private static Log logger = LogFactory.getLog(JaasPasswordAuthenticator.class);
	
	@Override
	public Object authenticate(final String username, final String password, final ServerSession session) {
		logger.debug("trying to authenticate user {" + username + "}");
		try {
			//Trying to authenticate throw the ServerSide LoginModule stack to check if account is valid.
			LoginContext lc = new LoginContext("JBossWSDigest", new UsernamePasswordHandler(username, password));
	        lc.login();
	        logger.debug("valid login");
	        lc.logout();
		} catch (Exception e) {
			logger.debug("invalid login and/or password");
			return null;
		}    
		try {
			//Now that login/password are valid, performing a client login to ensure Security Association process.
			LoginContext lc = new LoginContext("client-login", new UsernamePasswordHandler(username, password));
	        lc.login();
	        if ( session instanceof SSHServerSession ) {
	        	((SSHServerSession)session).setLoginContext(lc);
	        } else {
	        	logger.warn("ServerSession is not of type SSHServerSession : unable to set LoginContext for futur logout");
	        }
	        logger.debug("user logged");
	        return username;
		} catch (Exception e) {
			logger.debug("login failed");
			return null;
		}
	}


}
