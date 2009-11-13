package org.qualipso.factory.svn;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.svn.entity.SVNRepository;


/**
 * @author Gerald Oster (oster@loria.fr)
 * @date 9 October 2009
 */
@Remote
@WebService(name = SVNService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + SVNService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface SVNService extends FactoryService {

	public static final String SERVICE_NAME = "svn";
	public static final String[] RESOURCE_TYPE_LIST = new String[] {SVNRepository.RESOURCE_NAME};
	
	@WebMethod
	public void createSVNRepository(String path, String name, String description) throws SVNServiceException;
	
	@WebMethod
	@WebResult(name = "svn-repository")
	public SVNRepository readSVNRepository(String path) throws SVNServiceException;
	
	@WebMethod
	public void updateSVNRepository(String path, String name, String description) throws SVNServiceException;
	
	@WebMethod
	public void deleteSVNRepository(String path) throws SVNServiceException;
		
}
