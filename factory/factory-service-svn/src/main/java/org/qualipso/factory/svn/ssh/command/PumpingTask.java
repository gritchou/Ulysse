package org.qualipso.factory.svn.ssh.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.svn.exception.SVNServiceException;
import org.qualipso.factory.svn.ssh.command.filter.RequestFilter;
import org.qualipso.factory.svn.ssh.command.filter.component.RequestComponent;

/**
 * @author Gerald Oster (oster@loria.fr)
 * @date 9 October 2009
 */
public abstract class PumpingTask implements Runnable {

	/**
	 * LogFactory
	 */
	private static final Log logger = LogFactory.getLog(PumpingTask.class);

	/**
	 * InputStream
	 */
	protected InputStream in;
	
	/**
	 * OutputStream
	 */
	protected OutputStream out;
	
	/**
	 * Name of the thread
	 */
	protected TaskType type;
	
	/**
	 * QueryAnalyser
	 */
	protected RequestFilter queryAnalyser;

	/**
	 * SVNServeSSHCommand
	 */
	protected SVNServeSSHCommand command;
	
	/**
	 * Constructor
	 * @param in InputStream
	 * @param out OutputStream
	 * @param type of the thread
	 * @param command SVNServeSSHCommand
	 */
	protected PumpingTask(InputStream in, OutputStream out, TaskType type, SVNServeSSHCommand command) {
		this.in = in;
		this.out = out;
		this.type = type;
		this.command = command;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		logger.debug("pumping task " + this.type.toString() + " started");
		Map<Integer, RequestComponent> requestInfos = new HashMap<Integer, RequestComponent>();
		try {
			int bytesRead = 0;
			
			byte[] buffer = new byte[4096];
			while ((bytesRead = in.read(buffer)) != -1) {
				if (out != null && bytesRead > 0) {
					String request = new String(buffer, 0, bytesRead);
					logger.debug(this.type.toString() + " read/write [" + request + "]");
					filter(request, requestInfos, type);
					out.write(buffer, 0, bytesRead);
					out.flush();
					
					Thread.yield();
				}
			}
		} 
		catch (IOException e) {
			// FIXME: not sure how to avoid the IOException when input is close
			// and it tries to read. Unfortunately, it seems that in.available
			// is blocking.
			
			// logger.error("could not forward data through pipes", e);
		} catch (SVNServiceException e) {
			throwSVNServiceException(e);
		}
		logger.debug("pumping " + this.type.toString() + "  task terminated");
	}
	
	/**
	 * Filter the request
	 * @param request to filter
	 * @param requestComponents of the filter
	 * @param type of the task
	 * @throws SVNServiceException if an error occurred
	 */
	protected abstract void filter(String request, Map<Integer, RequestComponent> requestComponents, TaskType type) throws SVNServiceException;
	
	/**
	 * Throw error on the outpuStream
	 * @param e
	 */
	public void throwSVNServiceException(SVNServiceException e) {
		command.stopThread();
	}
}
