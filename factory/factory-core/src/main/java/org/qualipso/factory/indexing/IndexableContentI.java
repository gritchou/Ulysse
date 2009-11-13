package org.qualipso.factory.indexing;

import java.io.Serializable;

/**
 * Interface which allows to have fields of the document Indexable. 
 * So that they may be represented like a list of hits on the document
 * A field is a section of a Document. Values may be free text, provided as a String
 * or they may be atomic keywords. Such keywords may be used to represent dates, urls, etc. 
 * Fields will be storing in the index. 
 * @author cynthia FLORENTIN 
 *
 */
public interface IndexableContentI extends Serializable{
	
	/**
	 * Add a new element keyword in the object IndexableContent 
	 * @param content represent a keyword in the document
	 * @throws IndexingServiceException
	 */
	public void addContentPart(String content) throws IndexingServiceException;
	
	/**
	 * Allows to represent the list of keyword's hits on the document.
	 * @return a list of String
	 */
    public String toString() ;
}
