package org.qualipso.factory.greeting;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;
import org.qualipso.factory.greeting.entity.Name;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 june 2009
 */
@Remote
@WebService(name = "GreetingService", targetNamespace = "http://org.qualipso.factory.ws/service/greeting")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface GreetingService extends FactoryService {
	
	@WebMethod
	public void createName(String path, String name) throws GreetingServiceException;
	
	@WebMethod
	@WebResult(name = "name")
	public Name readName(String path) throws GreetingServiceException;
	
	@WebMethod
	public void updateName(String path, String name) throws GreetingServiceException;
	
	@WebMethod
	public void deleteName(String path) throws GreetingServiceException;
	
	@WebMethod
	@WebResult(name = "message")
	public String sayHello(String path) throws GreetingServiceException;
	
	
}
