package org.qualipso.factory.ssh;

import javax.ejb.Local;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 21 Setpember 2009
 */
@Local
public interface SSHService {
	
	public static final String SERVICE_NAME = "ssh";
	
	public void registerCommand(String name, String classname) throws SSHServiceException;

}
