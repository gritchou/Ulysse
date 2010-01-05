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
package org.qualipso.factory.indexing;

import java.util.ArrayList;

import javax.ejb.Local;

import org.qualipso.factory.FactoryService;

/**
 * Indexing Service allow content indexation in the system.<br/>
 * <br/>
 * It provides methods to allow adding content in the index base and method to
 * perform search on resources.<br/>
 * This service rely on the ability of FactoryResource and FactoryService to
 * provide full text view of the data. <br/>
 * <br/>
 * This internal service is not visible remotely and should only be used by
 * trusted services. <br/>
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @author Benjamin Dreux (benjiiiiii@gmail.com)
 * @date 20 May 2009
 */
@Local
public interface IndexingService extends FactoryService {
    public static final String SERVICE_NAME = "indexing";
    public static final String[] RESOURCE_TYPE_LIST = new String[] {};

    /**
     * Index the resource identified by the given factoryResourceIdentifier, or
     * throws an IndexingServiceException if an error occurs.
     * 
     * @param path
     *            The path to the resource to index.
     * @throws IndexingServiceException
     *             if the message can't be send to the JMS queue.
     */
    public void index(String path) throws IndexingServiceException;

    /**
     * Update the indexed data of the resource identified by the given
     * FactoryResourceIdentfier, or throws an IndexingServiceExcpetion if an
     * error occurs.
     * 
     * @param path
     *            The path to the resource to reindex.
     * @throws IndexingServiceExcpetion
     *             if the message can't be send to the JMS queue.
     */
    public void reindex(String path) throws IndexingServiceException;

    /**
     * Remove the resource's indexing data from the index, or throws an
     * IndexingServiceExcpetion if an error occurs.
     * 
     * @param path
     *            The path to the resource to stop to index.
     * @throws IndexingServiceException
     *             if the message can't be send to the queue.
     */
    public void remove(String path) throws IndexingServiceException;

    /**
     * Search in the index a match to the query.
     * 
     * @param query
     *            the query to run the index against. This query use the Lucene
     *            syntax.
     * @return A list of SearchResult. Each result match to the query.
     * @throws IndexingServiceException
     */
    public ArrayList<SearchResult> search(String query) throws IndexingServiceException;
}
