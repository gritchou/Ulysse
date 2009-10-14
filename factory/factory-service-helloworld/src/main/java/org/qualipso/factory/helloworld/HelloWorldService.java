package org.qualipso.factory.helloworld;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 june 2009
 */
@Remote
@WebService(name = "HelloWorldService", targetNamespace = "http://org.qualipso.factory.ws/service/helloworld")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface HelloWorldService extends FactoryService {
	
	@WebMethod
	@WebResult(name = "message")
	public String sayHelloWorld() throws HelloWorldServiceException;

}
