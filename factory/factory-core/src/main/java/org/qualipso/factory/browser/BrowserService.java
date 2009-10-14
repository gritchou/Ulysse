package org.qualipso.factory.browser;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 16 june 2009
 */
@Remote
@WebService(name = "BrowserService", targetNamespace = "http://org.qualipso.factory.ws/service/browser")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface BrowserService extends FactoryService {
	
	@WebMethod
	@WebResult(name = "resource")
	public FactoryResource findResource(@WebParam(name = "path") String path) throws BrowserServiceException;

	@WebMethod
	@WebResult(name = "has-children")
	public boolean hasChildren(@WebParam(name = "path") String path) throws BrowserServiceException;
	
	@WebMethod
	@WebResult(name = "children")
	public String[] listChildren(@WebParam(name = "path") String path) throws BrowserServiceException;

	@WebMethod
	@WebResult(name = "children")
	public String[] listChildrenOfType(@WebParam(name = "path") String path, @WebParam(name = "service-pattern") String servicePattern, @WebParam(name = "type-pattern") String typePattern) throws BrowserServiceException;

}
