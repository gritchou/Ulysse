package org.qualipso.factory.ssh;

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
 * Implementation of the SSH Service.<br/>
 * <br/>
 * The implementation starts an SSH Server in order to listen for clients and commands. Authentication of this SSH server module
 * relies on the system authentication using a JAAS connector.
 * <br/>
 * Implementation is based on a EJB 3.0 Stateless Session Bean. Because internal visibility only, this bean does not implement
 * Remote interface but only Local one.
 * Bean name follow naming conventions of the factory and use the specific local service prefix.<br/>
 * <br/>
 * Bean security is configured for JBoss AS 5 and rely on JAAS to ensure Authentication and Authorization of user.<br/>
 * <br/>
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 21 September 2009
 */
@Stateless(name = SSHService.SERVICE_NAME, mappedName = FactoryNamingConvention.LOCAL_SERVICE_PREFIX + SSHService.SERVICE_NAME)
@SecurityDomain(value = "JBossWSDigest")
public class SSHServiceBean implements SSHService {
	
	private static Log logger = LogFactory.getLog(SSHServiceBean.class);
	
	private static SSHCommandFactory commandFactory;
    private static SSHShellFactory shellFactory;
    private static SshServer sshd = null;
    private SessionContext ctx;
    private int port = 3333;
    
    @Resource
    public void setSessionContext(SessionContext ctx) {
        this.ctx = ctx;
    }

    public SessionContext getSessionContext() {
        return this.ctx;
    }

    public SSHServiceBean () {
    	logger.debug("new SSHServiceBean instance created");
    }
	
	@PostConstruct
	public void init() throws Exception {
		if (commandFactory == null) {
            commandFactory = new SSHCommandFactory();
            logger.debug("sshd command factory created");
        }

        if (shellFactory == null) {
            shellFactory = new SSHShellFactory();
            logger.debug("sshd shell factory created");
        }

        if (sshd == null) {
        	try {
	            sshd = SshServer.setUpDefaultServer();
	            sshd.setSessionFactory(new SSHSessionFactory());
	            sshd.setCommandFactory(commandFactory);
	            sshd.setShellFactory(shellFactory);
	            sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("data/hostkey.ser"));
	            sshd.setPort(port);
	            
	            JaasPasswordAuthenticator authenticator = new JaasPasswordAuthenticator();
	            sshd.setPasswordAuthenticator(authenticator);
	            sshd.start();
	            logger.debug("sshd started on port " + port);
        	} catch ( Exception e ) {
        		logger.error("unable to start sshd", e);
        	}
        }
	}
	
	@PreDestroy
	public void destroy() throws Exception {
		logger.debug("stopping sshd");
        sshd.stop();
        logger.debug("ssd stopped");
	}
	
	@Override
    public void registerCommand(String name, String classname) throws SSHServiceException {
        logger.info("registerCommand(...) called");
        logger.debug("params : name=" + name + ", classname=" + classname);
        commandFactory.registerCommand(name, classname);
    }
    
    @Override
    public void importShellCommands(String packageName) throws SSHServiceException {
        logger.info("importShellCommand(...) called");
        logger.debug("params : packageName=" + packageName);
        shellFactory.importCommands(packageName);
    }

}
