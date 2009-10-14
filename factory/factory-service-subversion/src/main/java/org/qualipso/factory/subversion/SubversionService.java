package org.qualipso.factory.subversion;

import java.util.ArrayList;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.subversion.entity.Repository;
import org.qualipso.factory.subversion.entity.SVNLogEntry;

/**
 * @author Emmanuel Rias (emmanuel.rias@bull.net)
 * @date 30 june 2009
 */
@Remote
@WebService(name = "SubversionService", targetNamespace = "http://org.qualipso.factory.ws/service/subversion")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface SubversionService extends FactoryService {

	@WebMethod
	public void createRepository(String path, String name)
			throws SubversionServiceException;

	@WebMethod
	public void deleteRepository(String path, String name)
			throws SubversionServiceException;

	@WebMethod
	@WebResult(name = "repository")
	public Repository findRepositoryByName(String path, String name)
			throws SubversionServiceException;
	
    public boolean removeUsers(final ArrayList<Profile> users,
            final Repository repository) throws SubversionServiceException;

    public boolean setUserRole(final String path, String repositoryName,
            final String role, final String userName) throws SubversionServiceException;
    
	@WebMethod
	@WebResult(name = "svninfo")
    public SVNLogEntry[] getCommitsInfo( String path, Repository repository ) throws SubversionServiceException;
    
	@WebMethod
	@WebResult(name = "svninfo")
    public SVNLogEntry[] getCommitsInfoFromRev( String path, Repository repository, String revision ) throws SubversionServiceException;
    
	@WebMethod
	@WebResult(name = "svninfo")
    public SVNLogEntry[] getCommitsInfoFromRevTo( String path, Repository repository, String revisionFrom, String revisionTo) throws SubversionServiceException;
    
	@WebMethod
    public void getModifiedLines( Repository repository, String fileName) throws SubversionServiceException;
    
	@WebMethod
    public void getModifiedLinesFromRev( Repository repository, String fileName, String revision) throws SubversionServiceException;



}
