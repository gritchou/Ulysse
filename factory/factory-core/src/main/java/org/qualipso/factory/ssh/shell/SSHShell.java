package org.qualipso.factory.ssh.shell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.ShellFactory.Environment;
import org.apache.sshd.server.ShellFactory.ExitCallback;
import org.apache.sshd.server.ShellFactory.SessionAware;
import org.apache.sshd.server.ShellFactory.Shell;
import org.apache.sshd.server.session.ServerSession;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 6 october 2009
 */
public class SSHShell implements Shell, SessionAware {

	private static Log logger = LogFactory.getLog(SSHShell.class);
	private InputStream in;
	private OutputStream out;
	private OutputStream err;
	private ExitCallback callback;
	private Environment env;
	private ServerSession session;

	@Override
	public void setInputStream(InputStream in) {
		logger.info("setting input stream");
		this.in = in;
	}

	@Override
	public void setOutputStream(OutputStream out) {
		logger.info("setting output stream");
		this.out = out;
	}

	@Override
	public void setErrorStream(OutputStream err) {
		logger.info("setting error stream");
		this.err = err;
	}

	@Override
	public void setExitCallback(ExitCallback callback) {
		logger.info("setting exit callback");
		this.callback = callback;
	}

	@Override
	public void setSession(ServerSession session) {
		logger.info("setting server session");
		this.session = session;
	}

	public Environment getEnv() {
		return env;
	}

	@Override
	public void start(Environment env) throws IOException {
		logger.debug("starting shell");
		this.env = env;
		for ( String key : env.getEnv().keySet() ) {
			logger.debug("$" + key + ":" + env.getEnv().get(key));
		}
		new Thread("shell") {
			@Override
			public void run() {
				runImpl();
			}
		}.start();

	}

	private void runImpl() {
		try {
			try {
				logger.info("Starting Shell");

				logger.info("Shell completed, bye");
			} catch (Exception e) {
				logger.error("Error Executing Shell", e);
			}
			logger.info("Shell completed normally");
		} finally {
			try {
				out.flush();
			} catch (IOException err) {
				logger.error("Error running impl", err);
			}
			try {
				err.flush();
			} catch (IOException err) {
				logger.error("Error running impl", err);
			}
			callback.onExit(0);
		}

	}

	@Override
	public void destroy() {
		logger.debug("shell destroyed");
	}

}
