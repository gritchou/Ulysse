package org.qualipso.factory.ssh.shell;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.ShellFactory;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 21 September 2009
 */
public class SSHShellFactory implements ShellFactory {
	
	private static Log logger = LogFactory.getLog(SSHShellFactory.class);

	@Override
	public Shell createShell() {
		logger.debug("creating a new shell");
		return new SSHShell();
	}

}
