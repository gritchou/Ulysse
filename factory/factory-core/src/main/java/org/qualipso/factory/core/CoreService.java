package org.qualipso.factory.core;

import java.io.InputStream;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;
import org.qualipso.factory.core.entity.File;
import org.qualipso.factory.core.entity.FileData;
import org.qualipso.factory.core.entity.Folder;
import org.qualipso.factory.core.entity.Link;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 july 2009
 */
@Remote
@WebService(name = "CoreService", targetNamespace = "http://org.qualipso.factory.ws/service/core")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface CoreService extends FactoryService {
	
	@WebMethod
	public void createLink(@WebParam(name = "path") String path, @WebParam(name = "to-path") String toPath) throws CoreServiceException;
	
	@WebMethod
	@WebResult(name = "link")
	public Link readLink(@WebParam(name = "path") String path) throws CoreServiceException;
	
	@WebMethod
	public void updateLink(@WebParam(name = "path") String path, @WebParam(name = "to-path") String toPath) throws CoreServiceException;
	
	@WebMethod
	public void deleteLink(@WebParam(name = "path") String path) throws CoreServiceException;
	
	@WebMethod
	public void createFolder(@WebParam(name = "path") String path, @WebParam(name = "name") String name, @WebParam(name = "description") String description) throws CoreServiceException;
	
	@WebMethod
	@WebResult(name = "folder")
	public Folder readFolder(@WebParam(name = "path") String path) throws CoreServiceException;
	
	@WebMethod
	public void updateFolder(@WebParam(name = "path") String path, @WebParam(name = "name") String name, @WebParam(name = "description") String description) throws CoreServiceException;
	
	@WebMethod
	public void deleteFolder(@WebParam(name = "path") String path) throws CoreServiceException;
	
	@WebMethod
	public void createFile(@WebParam(name = "path") String path, @WebParam(name = "filename") String filename, @WebParam(name = "content-type") String contenttype, @WebParam(name = "description") String description, @WebParam(name = "file-data") FileData filedata) throws CoreServiceException;
	
	@WebMethod(exclude=true)
	public void createFile(String path, String filename,String contenttype, String description, java.io.File data) throws CoreServiceException;
	
	@WebMethod(exclude=true)
	public void createFile(String path, String filename, String contenttype, String description, InputStream data) throws CoreServiceException;
	
	@WebMethod
	@WebResult(name = "file")
	public File readFile(@WebParam(name = "path") String path) throws CoreServiceException;
	
	@WebMethod
	public void updateFile(@WebParam(name = "path") String path, @WebParam(name = "filename") String filename, @WebParam(name = "content-type") String contenttype, @WebParam(name = "description") String description, @WebParam(name = "file-data") FileData filedata) throws CoreServiceException;
	
	@WebMethod(exclude=true)
	public void updateFile(String path, String filename, String contenttype, String description, java.io.File data) throws CoreServiceException;
	
	@WebMethod(exclude=true)
	public void updateFile(String path, String filename,String contenttype, String description, InputStream data) throws CoreServiceException;
	
	@WebMethod
	public void deleteFile(@WebParam(name = "path") String path) throws CoreServiceException;
	
	@WebMethod
	@WebResult(name = "file-data")
	public FileData getFileData(@WebParam(name = "path") String path) throws CoreServiceException;

}
