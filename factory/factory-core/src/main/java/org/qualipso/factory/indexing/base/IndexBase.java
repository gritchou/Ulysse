package org.qualipso.factory.indexing.base;

import java.util.ArrayList;

import javax.ejb.Local;

import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.indexing.IndexableDocument;
import org.qualipso.factory.indexing.IndexingService;
import org.qualipso.factory.indexing.IndexingServiceException;
import org.qualipso.factory.indexing.SearchResult;

@Local
/**
 * <p>Interface which own the logic of indexation.
 * His utilisation depends on the text search engine library</p>
 * @author cynthia FLORENTIN 
 * @author philippe SCHMUCKER
 */
public interface IndexBase {
    /**
     * <p>
     * Index an indexable document
     * </p>
     * 
     * @param doc
     *            is an indexable document
     * @throws IndexingServiceException
     * @see {@link IndexingService}{@link #index(IndexableDocument)}
     */
    public void index(IndexableDocument doc) throws IndexingServiceException;

    /**
     * <p>
     * Update the index with an indexable document and an Factory ressource
     * Identifier
     * </p>
     * 
     * @param path
     *            path to the indexed resource
     * @param doc
     *            an Object of type IndexableDocument
     * @throws IndexingServiceException
     * @see {@link IndexingService}
     *      {@link #reindex(FactoryResourceIdentifier, IndexableDocument)}
     */
    public void reindex(String path, IndexableDocument doc) throws IndexingServiceException;

    /**
     * <p>
     * Remove the resource's indexing data from the index, or throws an
     * IndexingServiceExcpetion if an error occurs.
     * </p>
     * 
     * @param path
     *            path to the indexed resource
     * @throws IndexingServiceException
     * @see {@link IndexingService}{@link #remove(FactoryResourceIdentifier)}
     */
    public void remove(String path) throws IndexingServiceException;

    /**
     * <p>
     * Allows to do a query for the search
     * </p>
     * 
     * @param query
     *            is a String
     * @return an arraylist of query's result
     * @throws IndexingServiceException
     */
    public ArrayList<SearchResult> search(String query) throws IndexingServiceException;

}
