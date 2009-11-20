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
 *
 */
package org.qualipso.factory.indexing;


import javax.ejb.Local;
import java.util.ArrayList;
import org.qualipso.factory.FactoryResourceIdentifier;


/**
 * @author benjamin Dreux(benjiiiiii@gmail.co)
 * @author Jerome Blanchard jayblanc@gmail.com
 * @date 20 May 2009
 */
@Local

public interface IndexingService {
    public static final String SERVICE_NAME = "IndexingService";
    public static final String[] RESOURCE_TYPE_LIST = new String[] {};
	/**
	 * Index the resource identified by the given factoryResourceIdentifier, or
	 * throws an IndexingServiceException if an error occurs.
	 * @param fri The identifier of the resource.
	 * @throws IndexingServiceException if the message can't be send to the JMS queue.
	 */
	public void index(FactoryResourceIdentifier fri) throws IndexingServiceException;

	/**
	 * Update the indexed data of the resource identified by the given
	 * FactoryResourceIdentfier, or throws an IndexingServiceExcpetion if an
	 * error occurs.
	 * @param fri the identifier of the resource.
	 * @throws IndexingServiceExcpetion if the message can't be send to the JMS queue.
	 */
    public void reindex(FactoryResourceIdentifier fri) throws IndexingServiceException;

    /**
     * Remove the resource's indexing data from the index, or throws 
     * an IndexingServiceExcpetion if an error occurs.
     * @param fri the identifier of the resource.
     * @throws IndexingServiceException if the message can't be send to the queue.
     */
    public void remove(FactoryResourceIdentifier fri) throws IndexingServiceException;
    
    /**
     * Search in the index a match to the query.
     * @param query the query to run the index against. This query use the Lucene syntax.
     * @return A list of SearchResult. Each result match to the query.
     * @throws IndexingServiceException
     */
    public ArrayList<SearchResult> search(String query) throws IndexingServiceException;
}
