package org.qualipso.factory.helloworld;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 june 2009
 */
@Remote
@WebService(name = HelloWorldService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + HelloWorldService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface HelloWorldService extends FactoryService {
	
	public static final String SERVICE_NAME = "helloworld";
	public static final String[] RESOURCE_TYPE_LIST = new String[] {};
	
	@WebMethod
	@WebResult(name = "message")
	public String sayHelloWorld() throws HelloWorldServiceException;

}
