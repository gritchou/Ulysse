package org.qualipso.factory.jabuti;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;

/**
 * @author 
 * @date 
 */
@Remote
@WebService(name = "JabutiService", targetNamespace = "http://org.qualipso.factory.ws/service/jabuti")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface JabutiService extends FactoryService {
	
	@WebMethod
	@WebResult(name = "message")
	public String sayJabuti() throws JabutiServiceException;

}
