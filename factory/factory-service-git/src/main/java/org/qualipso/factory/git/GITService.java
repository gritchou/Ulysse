package org.qualipso.factory.git;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathAlreadyBoundException;
import org.qualipso.factory.binding.PathNotEmptyException;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.git.entity.GITRepository;
import org.qualipso.factory.security.pep.AccessDeniedException;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteOutputStream;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 21 september 2009
 */
@Remote
@WebService(name = GITService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + GITService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface GITService extends FactoryService {
	
	public static final String SERVICE_NAME = "git";
	public static final String[] RESOURCE_TYPE_LIST = new String[] {GITRepository.RESOURCE_NAME};
	
	@WebMethod
	public void createGITRepository(String path, String name, String description) throws GITServiceException, AccessDeniedException, InvalidPathException, PathAlreadyBoundException;
	
	@WebMethod
	@WebResult(name = "git-repository")
	public GITRepository readGITRepository(String path) throws GITServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;
	
	@WebMethod
	public void updateGITRepository(String path, String name, String description) throws GITServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;
	
	@WebMethod
	public void deleteGITRepository(String path) throws GITServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException, PathNotEmptyException;
	
	@WebMethod(exclude=true)
	public void remotePushToGITRepository(String path, RemoteInputStream in, RemoteOutputStream out, RemoteOutputStream messages) throws GITServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;
	
	@WebMethod(exclude=true)
	public void remotePullFromGITRepository(String path, RemoteInputStream in, RemoteOutputStream out, RemoteOutputStream messages) throws GITServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;
	
}
