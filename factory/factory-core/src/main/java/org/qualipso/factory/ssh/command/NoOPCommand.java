package org.qualipso.factory.ssh.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.CommandFactory.Command;
import org.apache.sshd.server.CommandFactory.ExitCallback;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 September 2009
 */
public class NoOPCommand implements Command {
	
	private static Log logger = LogFactory.getLog(NoOPCommand.class);
	private ExitCallback callback;
	private OutputStream err;
	private OutputStream out;
	private InputStream in;

	public void setExitCallback(ExitCallback callback) {
		this.callback = callback;
	}

	public void setErrorStream(OutputStream err) {
		this.err = err;
	}

	public void setInputStream(InputStream in) {
		this.in = in;
	}

	public void setOutputStream(OutputStream out) {
		this.out = out;
	}

	public void start() throws IOException {
		logger.info("Executing NoOP Command");
		err.write("Command not found\r\n".getBytes());
		err.flush();
		callback.onExit(-1);
	}

}
