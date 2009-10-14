package org.qualipso.factory.ssh.command;

import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.CommandFactory;
import org.qualipso.factory.ssh.SSHServiceException;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 September 2009
 */
public class SSHCommandFactory implements CommandFactory {
	
	private static Log logger = LogFactory.getLog(SSHCommandFactory.class);
	
	private HashMap<String, String> commands;
	
	public SSHCommandFactory() {
		commands = new HashMap<String, String> ();
	}
	
	public Command createCommand(String command) {
		logger.debug("searching a suitable command for expression : " + command);
		//TODO parse the command better
		String[] parts = command.split(" ");
		String commandName = parts[0];
		String[] commandArgs = new String[0];
		if ( parts.length > 1 ) {
			commandArgs = Arrays.copyOfRange(parts, 1, parts.length);
		}
		if ( commands.containsKey(commandName)) {
			String className = commands.get(commandName);
			logger.debug("command found : " + className + " building new one...");
			try {
				Class commandClass = Class.forName(className);
				SSHCommand sshCommand = (SSHCommand) commandClass.getConstructor(String[].class).newInstance(new Object[] {commandArgs});
				return sshCommand;
			} catch ( Exception e ) {
				logger.error("error in command creation ", e);
				return new NoOPCommand();
			}
		} else {
			logger.debug("no command found");
			return new FakeCommand();
		}
	}
	
	public void registerCommand(String name, String className) throws SSHServiceException {
		if ( commands.containsKey(name) ) { 
			throw new SSHServiceException("A command with name " + name + " is already registered");
		}
		commands.put(name, className);
	}

}
