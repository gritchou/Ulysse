package org.qualipso.factory.git.ssh.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.CommandFactory.ExitCallback;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.git.GITService;
import org.qualipso.factory.git.GITServiceLocal;
import org.qualipso.factory.ssh.command.SSHCommand;

public class UploadPackSSHCommand extends SSHCommand {
	
	private static Log logger = LogFactory.getLog(UploadPackSSHCommand.class);
	private ExitCallback callback;
	private OutputStream err;
	private OutputStream out;
	private InputStream in;
	
	public UploadPackSSHCommand(String[] args) {
		super(args);
	}
	
	@Override
	public void setErrorStream(OutputStream err) {
		this.err = err;
	}

	@Override
	public void setExitCallback(ExitCallback callback) {
		this.callback = callback;
	}

	@Override
	public void setInputStream(InputStream in) {
		this.in = in;
	}

	@Override
	public void setOutputStream(OutputStream out) {
		this.out = out;
	}

	@Override
	public void start() throws IOException {
		new Thread("UploadPackSSHCommand:" + System.currentTimeMillis()) {
			@Override
			public void run() {
				runImpl();
			}
		}.start();
	}
	
	private void runImpl() {
		try {
			try {
				logger.info("Starting Upload Pack");
				
				String path = getArg(0).replaceAll("'", "");
				Context ctx = new InitialContext();
				GITServiceLocal service = (GITServiceLocal) ctx.lookup(FactoryNamingConvention.getLocalJNDINameForService(GITService.SERVICE_NAME));
				service.pullFromGITRepository(path, in, out, err);
				
				logger.info("Completing Upload Pack");
			} catch (Exception e) {
				logger.error("Error Executing Command" , e);
			}
			logger.info("Command completed normally");
		} finally {
			try {
				out.flush();
			} catch (IOException err) {
				logger.error("Error running impl" , err);
			}
			try {
				err.flush();
			} catch (IOException err) {
				logger.error("Error running impl" , err);
			}
			callback.onExit(0);
		}
	}

}
