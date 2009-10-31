package org.qualipso.factory.ssh.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.CommandFactory.Command;
import org.apache.sshd.server.CommandFactory.ExitCallback;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.browser.BrowserService;
import org.qualipso.factory.membership.MembershipService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 September 2009
 */
public class FakeCommand implements Command {
	
	private static Log logger = LogFactory.getLog(FakeCommand.class);
	private ExitCallback callback;
	private OutputStream err;
	private OutputStream out;
	private InputStream in;
	
	private MembershipService membership;
	private BrowserService browser;
	
	public FakeCommand() {
	}

	@EJB
	public void setMembership(MembershipService membership) {
		this.membership = membership;
	}

	public MembershipService getMembership() {
		return membership;
	}
	
	@EJB
	public void setBrowser(BrowserService browser) {
		this.browser = browser;
	}

	public BrowserService getBrowser() {
		return browser;
	}

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
		logger.info("Executing Fake Command");
		
		try {
			Context ctx = new InitialContext();
			membership = (MembershipService) ctx.lookup(FactoryNamingConvention.JNDI_SERVICE_PREFIX + "MembershipService");
			
			logger.debug("connected as : " + membership.getProfilePathForConnectedIdentifier());
			err.write(("connected as : " + membership.getProfilePathForConnectedIdentifier() + "\r\n").getBytes());
			
			String[] childs = browser.listChildren("/");
			
			for (String child : childs) {
				logger.debug(child);
				err.write((child + "\r\n").getBytes());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			err.write(e.getMessage().getBytes());
		} 
		
		err.flush();
		callback.onExit(0);
	}

}
