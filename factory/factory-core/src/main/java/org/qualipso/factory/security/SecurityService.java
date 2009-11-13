package org.qualipso.factory.security;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 23 june 2009
 */
@Remote
@WebService(name = SecurityService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + SecurityService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface SecurityService extends FactoryService {
	
	public static final String SERVICE_NAME = "security";
	public static final String[] RESOURCE_TYPE_LIST = new String[] {};
	
	@WebMethod
	@WebResult(name = "security-policy")
	public String getSecurityPolicy(String path) throws SecurityServiceException;
	
	@WebMethod
	public void addSecurityRule(String path, String subject, String permissions) throws SecurityServiceException;
	
	@WebMethod
	public void editSecurityRule(String path, String subject, String permissions) throws SecurityServiceException;
	
	@WebMethod
	public void removeSecurityRule(String path, String subject) throws SecurityServiceException;
	
	@WebMethod
	public void changeOwner(String path, String newOwner) throws SecurityServiceException;
	
}
