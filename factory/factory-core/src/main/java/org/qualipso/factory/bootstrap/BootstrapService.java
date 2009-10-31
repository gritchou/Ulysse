package org.qualipso.factory.bootstrap;

import javax.ejb.Remote;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 4 September 2009
 */
@Remote
@WebService(name = "BootstrapService", targetNamespace = "http://org.qualipso.factory.ws/service/bootstrap")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface BootstrapService extends FactoryService {
	
	public static final String VERSION="1.0";
	
	public void bootstrap() throws BootstrapServiceException;

}
