package org.qualipso.factory.indexing;

import java.util.ArrayList;
import javax.ejb.Local;
import org.qualipso.factory.FactoryResourceIdentifier;

@Local
/**
 * <p>Interface which own the logic of indexation.
 * His utilisation depends on the text search engine library</p>
 * @author cynthia FLORENTIN 
 * @author philippe SCHMUCKER
 */
public interface IndexI {
	/**
	 * <p>Index an indexable document</p>
	 * @param doc is an indexable document 
	 * @throws IndexingServiceException
	 * @see {@link IndexingService}{@link #index(IndexableDocument)}
	 */
	public void index(IndexableDocument doc ) throws IndexingServiceException;
	
	/**
	 * <p>Update the index with an indexable document and an Factory ressource Identifier</p>
	 * @param fri an Object of type FactoryResourceIdentifier
	 * @param doc an Object of type IndexableDocument
	 * @throws IndexingServiceException
	 * @see {@link IndexingService}{@link #reindex(FactoryResourceIdentifier, IndexableDocument)}
	 */
    public void reindex(FactoryResourceIdentifier fri, IndexableDocument doc) throws IndexingServiceException;
    
    /**
     * <p>Remove the resource's indexing data from the index, or throws 
     * an IndexingServiceExcpetion if an error occurs.</p>
     * @param fri an Object of type FactoryResourceIdentifier
     * @throws IndexingServiceException
     * @see {@link IndexingService}{@link #remove(FactoryResourceIdentifier)}
     */
    public void remove(FactoryResourceIdentifier fri) throws IndexingServiceException;
    
    /**
     * <p>Allows to do a query for the search</p>
     * @param query is a String
     * @return an arraylist of query's result
     * @throws IndexingServiceException
     */
    public ArrayList<SearchResult> search(String query) throws IndexingServiceException;

}
