package org.qualipso.factory.ssh;

import javax.ejb.Local;

@Local
public interface SSHService {
	
	public static final String SERVICE_NAME = "ssh";
	
	public void registerCommand(String name, String classname) throws SSHServiceException;
	
	public void importShellCommands(String packageName) throws SSHServiceException;

}
