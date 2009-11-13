package org.qualipso.factory.bootstrap;

import javax.ejb.Remote;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;

/**
 * Bootstrap Service provide a way to create all initial resources, security rules, needed to have a useable system.
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 4 September 2009
 */
@Remote
@WebService(name = BootstrapService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + BootstrapService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface BootstrapService extends FactoryService {
	
	public static final String SERVICE_NAME = "bootstrap";
	public static final String[] RESOURCE_TYPE_LIST = new String[] {};
	public static final String VERSION="1.1";
	
	public void bootstrap() throws BootstrapServiceException;

}
