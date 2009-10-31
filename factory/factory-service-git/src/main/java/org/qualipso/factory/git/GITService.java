package org.qualipso.factory.git;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;
import org.qualipso.factory.git.entity.GITRepository;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteOutputStream;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 21 september 2009
 */
@Remote
@WebService(name = "GITService", targetNamespace = "http://org.qualipso.factory.ws/service/git")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface GITService extends FactoryService {
	
	@WebMethod
	public void createGITRepository(String path, String name, String description) throws GITServiceException;
	
	@WebMethod
	@WebResult(name = "git-repository")
	public GITRepository readGITRepository(String path) throws GITServiceException;
	
	@WebMethod
	public void updateGITRepository(String path, String name, String description) throws GITServiceException;
	
	@WebMethod
	public void deleteGITRepository(String path) throws GITServiceException;
	
	@WebMethod(exclude=true)
	public void remotePushToGITRepository(String path, RemoteInputStream in, RemoteOutputStream out, RemoteOutputStream messages) throws GITServiceException;
	
	@WebMethod(exclude=true)
	public void remotePullFromGITRepository(String path, RemoteInputStream in, RemoteOutputStream out, RemoteOutputStream messages) throws GITServiceException;
	
}
