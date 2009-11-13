package org.qualipso.factory.ssh;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.ssh.authenticator.JaasPasswordAuthenticator;
import org.qualipso.factory.ssh.command.SSHCommandFactory;
import org.qualipso.factory.ssh.session.SSHSessionFactory;
import org.qualipso.factory.ssh.shell.SSHShellFactory;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 21 September 2009
 */
@Stateless(name = SSHService.SERVICE_NAME, mappedName = FactoryNamingConvention.LOCAL_SERVICE_PREFIX + SSHService.SERVICE_NAME)
@SecurityDomain(value = "JBossWSDigest")
public class SSHServiceBean implements SSHService {
	
	private static Log logger = LogFactory.getLog(SSHServiceBean.class);
	
	private SessionContext ctx;
	private static SSHCommandFactory commandFactory;
	private static SSHShellFactory shellFactory;
	
	private static SshServer sshd = null;
	
	public SSHServiceBean() {
	}
	
	@Resource
	public void setSessionContext(SessionContext ctx) {
		this.ctx = ctx;
	}

	public SessionContext getSessionContext() {
		return this.ctx;
	}

	@PostConstruct
	public void init() throws IOException {
		if (commandFactory == null ) {
			commandFactory = new SSHCommandFactory();
			logger.debug("command factory created");
		}
		if (shellFactory == null ) {
			shellFactory = new SSHShellFactory();
			logger.debug("shell factory created");
		}
		if (sshd == null) {
			sshd = SshServer.setUpDefaultServer();
			sshd.setSessionFactory(new SSHSessionFactory());
			sshd.setPort(3333);
			sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("data/hostkey.ser"));
			sshd.setCommandFactory(commandFactory);
			sshd.setShellFactory(shellFactory);
			JaasPasswordAuthenticator authenticator = new JaasPasswordAuthenticator();
			sshd.setPasswordAuthenticator(authenticator);
			sshd.start();
			logger.debug("ssh server started on port 3333");
		}
	}
	
	@PreDestroy
	public void destroy() {
		logger.debug("stopping ssh server");
		sshd.stop();
		logger.debug("ssh server stopped");
	}
	
	@Override
	public void registerCommand(String name, String classname) throws SSHServiceException {
		logger.info("registerCommand(...) called");
		logger.debug("params : name=" + name + ", classname=" + classname);
		commandFactory.registerCommand(name, classname);
	}
	
}
