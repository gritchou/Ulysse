/*
 *
 * Qualipso Factory
 * Copyright (C) 2006-2010 INRIA
 * http://www.inria.fr - molli@loria.fr
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of LGPL. See licenses details in LGPL.txt
 *
 * Initial authors :
 *
 * Jérôme Blanchard / INRIA
 * Pascal Molli / Nancy Université
 * Gérald Oster / Nancy Université
 * Christophe Bouthier / INRIA
 * 
 */
package org.qualipso.factory.core;

import java.io.InputStream;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathAlreadyBoundException;
import org.qualipso.factory.binding.PathNotEmptyException;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.core.entity.File;
import org.qualipso.factory.core.entity.FileData;
import org.qualipso.factory.core.entity.Folder;
import org.qualipso.factory.core.entity.Link;
import org.qualipso.factory.security.pep.AccessDeniedException;


/**
 * Core Service is the minimal service for using resources in the factory.<br/>
 * <br/>
 * This service handles very basic resources Folder, File and Link.<br/>
 * Those three resources are sufficient to create a kind of filesystem. Nevertheless, the actual
 * implementation of the Link resource doesn't assume direct access to pointed resource when
 * you want to read the link. It only contains another path to another resource. In the same
 * way, there is no warranty on the contained path integrity.<br/>
 * <br/>
 * This external service is visible for factory users as a WebService or using RMI.<br/>
 * <br/>
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 24 july 2009
 */
@Remote
@WebService(name = CoreService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + CoreService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface CoreService extends FactoryService {
    public static final String SERVICE_NAME = "core";
    public static final String[] RESOURCE_TYPE_LIST = new String[] { Folder.RESOURCE_NAME, File.RESOURCE_NAME, Link.RESOURCE_NAME };

    /**
     * Create a Link and bind it in the naming tree.
     *
     * @param path the path to bind this Link
     * @param toPath the path pointed by this link.
     * @throws CoreServiceException
     */
    @WebMethod
    public void createLink(@WebParam(name = "path")
    String path, @WebParam(name = "to-path")
    String toPath) throws CoreServiceException, AccessDeniedException, InvalidPathException, PathAlreadyBoundException;

    /**
     * Read a Link from this path
     *
     * @param path the path of the Link
     * @return the Link FactoryResource
     * @throws CoreServiceException
     */
    @WebMethod
    @WebResult(name = "link")
    public Link readLink(@WebParam(name = "path")
    String path) throws CoreServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Update the pointed path of a Link
     *
     * @param path the path of the link to update
     * @param toPath the new pointed path
     * @throws CoreServiceException
     */
    @WebMethod
    public void updateLink(@WebParam(name = "path")
    String path, @WebParam(name = "to-path")
    String toPath) throws CoreServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Delete a Link.
     *
     * @param path the path of the Link to delete.
     * @throws CoreServiceException
     */
    @WebMethod
    public void deleteLink(@WebParam(name = "path")
    String path) throws CoreServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException, PathNotEmptyException;

    /**
     * Create a Folder and bind it to the specified path.<br/>
     * <br/>
     * A folder allows you to organize other resources in the naming tree. You can give a name and a description to a Folder.
     *
     * @param path the path of the new Folder
     * @param name the name of this Folder.
     * @param description the description of this Folder.
     * @throws CoreServiceException
     */
    @WebMethod
    public void createFolder(@WebParam(name = "path")
    String path, @WebParam(name = "name")
    String name, @WebParam(name = "description")
    String description) throws CoreServiceException, AccessDeniedException, InvalidPathException, PathAlreadyBoundException;

    /**
     * Read a Folder binded to the given path.
     *
     * @param path the path of this Folder.
     * @return the Folder FactoryResource
     * @throws CoreServiceException
     */
    @WebMethod
    @WebResult(name = "folder")
    public Folder readFolder(@WebParam(name = "path")
    String path) throws CoreServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Update a Folder binded to the given path.
     *
     * @param path the path of this Folder
     * @param name the new name
     * @param description the new description
     * @throws CoreServiceException
     */
    @WebMethod
    public void updateFolder(@WebParam(name = "path")
    String path, @WebParam(name = "name")
    String name, @WebParam(name = "description")
    String description) throws CoreServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Delete a Folder binded to the given path.
     *
     * @param path the path of the Folder to delete.
     * @throws CoreServiceException
     */
    @WebMethod
    public void deleteFolder(@WebParam(name = "path")
    String path) throws CoreServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException, PathNotEmptyException;

    /**
     * Create a File and bind it in the naming tree.
     *
     * @param path the path of this file.
     * @param filename the filename
     * @param contenttype the content type, if not specified, should be determine using standard strategies. If impossible to determine, set to application/octet-stream
     * @param description the description of the file
     * @param filedata an object representing the file content that ensure correct XML serialization optimizations.
     * @throws CoreServiceException
     */
    @WebMethod
    public void createFile(@WebParam(name = "path")
    String path, @WebParam(name = "filename")
    String filename, @WebParam(name = "content-type")
    String contenttype, @WebParam(name = "description")
    String description, @WebParam(name = "file-data")
    FileData filedata) throws CoreServiceException, AccessDeniedException, InvalidPathException, PathAlreadyBoundException;

    /**
     * Internal method to create a File directly from a java.io.File object. <br/>
     * <br/>
     * This method is not accessible throw WebService but only using RMI.<br/>
     *
     * @param path the path of this file
     * @param filename the filename
     * @param contenttype the content type, if not specified, should be determine using standard strategies. If impossible to determine, set to application/octet-stream
     * @param description the description of the file
     * @param data the java.io.File.
     * @throws CoreServiceException
     */
    @WebMethod(exclude = true)
    public void createFile(String path, String filename, String contenttype, String description, java.io.File data)
        throws CoreServiceException, AccessDeniedException, InvalidPathException, PathAlreadyBoundException;

    /**
     * Internal method to create a File directly from a java.io.File object. <br/>
     * <br/>
     * This method is not accessible throw WebService but only using RMI.<br/>
     *
     * @param path the path of this file
     * @param filename the filename
     * @param contenttype the content type, if not specified, should be determine using standard strategies. If impossible to determine, set to application/octet-stream
     * @param description the description of the file
     * @param data the InputStream of the file content.
     * @throws CoreServiceException
     */
    @WebMethod(exclude = true)
    public void createFile(String path, String filename, String contenttype, String description, InputStream data)
        throws CoreServiceException, AccessDeniedException, InvalidPathException, PathAlreadyBoundException;

    /**
     * Read the File FactoryResource. This will not include any data. To retrieve File data, call the specific getFileData method.
     *
     * @param path the path of this File
     * @return the File FactoryResource
     * @throws CoreServiceException
     */
    @WebMethod
    @WebResult(name = "file")
    public File readFile(@WebParam(name = "path")
    String path) throws CoreServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Update a File binded to the specified path.
     *
     * @param path the path of the File resource.
     * @param filename the new filename
     * @param contenttype the new content type
     * @param description the new description
     * @param filedata the new data.
     * @throws CoreServiceException
     */
    @WebMethod
    public void updateFile(@WebParam(name = "path")
    String path, @WebParam(name = "filename")
    String filename, @WebParam(name = "content-type")
    String contenttype, @WebParam(name = "description")
    String description, @WebParam(name = "file-data")
    FileData filedata) throws CoreServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Update a File binded to the specified path.<br/>
     * <br/>
     * This method is not accessible throw WebService but only using RMI.<br/>
     *
     * @param path the path of the File resource.
     * @param filename the new filename
     * @param contenttype the new content type
     * @param description the new description
     * @param data the new data java.io.File.
     * @throws CoreServiceException
     */
    @WebMethod(exclude = true)
    public void updateFile(String path, String filename, String contenttype, String description, java.io.File data)
        throws CoreServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Update a File binded to the specified path.<br/>
     * <br/>
     * This method is not accessible throw WebService but only using RMI.<br/>
     *
     * @param path the path of the File resource.
     * @param filename the new filename
     * @param contenttype the new content type
     * @param description the new description
     * @param data the new data InputStream.
     * @throws CoreServiceException
     */
    @WebMethod(exclude = true)
    public void updateFile(String path, String filename, String contenttype, String description, InputStream data)
        throws CoreServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Delete a file from the specified path.
     *
     * @param path the path of the File to delete.
     * @throws CoreServiceException
     */
    @WebMethod
    public void deleteFile(@WebParam(name = "path")
    String path) throws CoreServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException, PathNotEmptyException;

    /**
     * Retrieve the data associated with this File.
     *
     * @param path the path of the File resource to get data from.
     * @return an object representing the file content that ensure correct XML serialization optimizations.
     * @throws CoreServiceException
     */
    @WebMethod
    @WebResult(name = "file-data")
    public FileData getFileData(@WebParam(name = "path")
    String path) throws CoreServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;
}
