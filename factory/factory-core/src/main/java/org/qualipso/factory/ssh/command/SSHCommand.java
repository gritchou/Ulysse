package org.qualipso.factory.ssh.command;

import org.apache.sshd.server.CommandFactory.Command;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 September 2009
 */
public abstract class SSHCommand implements Command {
	
	private String[] args;
	
	public SSHCommand() {
		this.args = new String[0];
	}
	
	public SSHCommand(String[] args) {
		this.args = args;
	}
	
	public String[] getArgs() {
		return args;
	}
	
	public String getArg(int pos) {
		return args[pos];
	}
	
}
