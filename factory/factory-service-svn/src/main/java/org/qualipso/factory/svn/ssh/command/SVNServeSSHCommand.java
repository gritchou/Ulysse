package org.qualipso.factory.svn.ssh.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.CommandFactory.ExitCallback;
import org.qualipso.factory.ssh.command.SSHCommand;
import org.qualipso.factory.svn.ssh.command.filter.RequestFilter;
import org.qualipso.factory.svn.utils.SVNProperties;

/**
 * @author Gerald Oster (oster@loria.fr)
 * @date 9 October 2009
 */
public class SVNServeSSHCommand extends SSHCommand {
	
	/**
	 * Logger
	 */
	private static Log logger = LogFactory.getLog(SVNServeSSHCommand.class);
	
	/**
	 * ExitCallback
	 */
	private ExitCallback callback;
	
	/**
	 * OutputStream of ssh command
	 */
	private OutputStream out;
	
	/**
	 * InputStream of ssh command
	 */
	private InputStream in;
	
	/**
	 * OutputStream of ssh command errors
	 */
	private OutputStream err;
	
	
	private Thread s2cError;
	private Thread s2c;
	private Thread c2s;
	
	
	public SVNServeSSHCommand() {
		super();
	}

	public SVNServeSSHCommand(String[] args) {
		super(args);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.sshd.server.CommandFactory.Command#setErrorStream(java.io.OutputStream)
	 */
	@Override
	public void setErrorStream(OutputStream err) {
		this.err = err;
	}

	/* (non-Javadoc)
	 * @see org.apache.sshd.server.CommandFactory.Command#setExitCallback(org.apache.sshd.server.CommandFactory.ExitCallback)
	 */
	@Override
	public void setExitCallback(ExitCallback callback) {
		this.callback = callback;
	}

	/* (non-Javadoc)
	 * @see org.apache.sshd.server.CommandFactory.Command#setInputStream(java.io.InputStream)
	 */
	@Override
	public void setInputStream(InputStream in) {
		this.in = in;
	}

	/* (non-Javadoc)
	 * @see org.apache.sshd.server.CommandFactory.Command#setOutputStream(java.io.OutputStream)
	 */
	@Override
	public void setOutputStream(OutputStream out) {
		this.out = out;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.sshd.server.CommandFactory.Command#start()
	 */
	@Override
	public void start() {
		new Thread("SVNServeSSHCommand:" + System.currentTimeMillis()) {
			/* (non-Javadoc)
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				runImpl();
			}
		}.start();
	}

	/**
	 * Run job for SVNServeSSHCommand
	 */
	public void runImpl() {
		logger.debug("starting SvnServerCommand");

		String repositoryPath = SVNProperties.getInstance().getRootDirRepositories();
		String cmdSvnserve = SVNProperties.getInstance().getCmdSvnServe();
		
		logger.debug("repositoryPath = " + repositoryPath);
		logger.debug("cmdSvnserve = " + cmdSvnserve);
		
		String[] cmds = { cmdSvnserve, "-t", "-r"+repositoryPath };
		ProcessBuilder pb = new ProcessBuilder(cmds);
		Process process;
		try {
			process = pb.start();
			
			
			RequestFilter queryAnalyser = new RequestFilter();
			
			s2cError = new Thread(new PumpingTaskError(process.getErrorStream(), err, this), TaskType.ERROR.toString());
			s2c = new Thread(new PumpingTaskS2C(process.getInputStream(), out, this, queryAnalyser), TaskType.S2C.toString());
			c2s = new Thread(new PumpingTaskC2S(in, process.getOutputStream(), this, queryAnalyser), TaskType.C2S.toString());

			s2c.setDaemon(true);
			c2s.setDaemon(true);
			s2cError.setDaemon(true);

			s2c.start();
			c2s.start();
			s2cError.start();

			try {
				s2c.join();
				c2s.join();
				s2cError.join();
			} catch (InterruptedException e) {
				logger.error("error while waiting end of pumping tasks: " + e.getMessage());
			}

			int status = 0;
			try {
				status = process.waitFor();
			} catch (InterruptedException e) {
				logger.error("error while waiting end of svnserve process: " + e.getMessage());
			} finally {
				callback.onExit(status);
				logger.info("completing SvnServerCommand");
			}

		} catch (IOException ex) {
			logger.error("process start failed: " + ex.getMessage());
		}
	}
	
	public synchronized void stopThread() {
		s2c.interrupt();
		c2s.interrupt();
		s2cError.interrupt();
		
		callback.onExit(-1);
		logger.info("completing SvnServerCommand");
	}
}

