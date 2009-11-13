package org.qualipso.factory.svn.ssh.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Gerald Oster (oster@loria.fr)
 * @date 9 October 2009
 */
public class PumpingTask implements Runnable {

	/**
	 * LogFactory
	 */
	private static final Log logger = LogFactory.getLog(PumpingTask.class);

	/**
	 * InputStream
	 */
	private InputStream in;
	
	/**
	 * OutputStream
	 */
	private OutputStream out;
	
	/**
	 * Name of the thread
	 */
	private String name;

	/**
	 * Constructor
	 * @param in InputStream
	 * @param out OutputStream
	 * @param name of the thread
	 */
	public PumpingTask(InputStream in, OutputStream out, String name) {
		this.in = in;
		this.out = out;
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		logger.debug("pumping task " + this.name + " started");
		try {
			int bytesRead = 0;
			byte[] buffer = new byte[4096];
			while ((bytesRead = in.read(buffer)) != -1) {
				if (out != null && bytesRead > 0) {
					logger.debug(this.name + " read/write " + new String(buffer));
					out.write(buffer, 0, bytesRead);
					out.flush();
					Thread.yield();
				}
			}
		} catch (IOException e) {
			// FIXME: not sure how to avoid the IOException when input is close
			// and it tries to read. Unfortunately, it seems that in.available
			// is blocking.
			
			// logger.error("could not forward data through pipes", e);
		}
		logger.debug("pumping " + this.name + "  task terminated");
	}
}
