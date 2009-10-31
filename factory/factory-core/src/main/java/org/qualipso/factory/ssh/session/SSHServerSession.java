package org.qualipso.factory.ssh.session;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.future.CloseFuture;
import org.apache.sshd.server.session.ServerSession;

public class SSHServerSession extends ServerSession {
	
	private static Log logger = LogFactory.getLog(SSHServerSession.class);
	private LoginContext lc;
	
	public SSHServerSession(SshServer server, IoSession ioSession) throws Exception {
		super(server, ioSession);
	}
	
	public void setLoginContext(LoginContext lc) {
		this.lc = lc;
	}

	@Override
	public CloseFuture close(boolean immediately) {
		logger.debug("closing ssh server session");
		try {
			lc.logout();
		} catch (LoginException e) {
			logger.error("unable to logout ", e);
		}
		return super.close(immediately);
	}
	
	

}
