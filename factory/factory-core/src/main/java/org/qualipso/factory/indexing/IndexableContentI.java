package org.qualipso.factory.indexing;

import java.io.Serializable;

/**
 * <p>Interface which allows to have fields of the document Indexable. 
 * So that they may be represented like a list of hits on the document
 * A field is a section of a Document. Values may be free text, provided as a String
 * or they may be atomic keywords. Such keywords may be used to represent dates, urls, etc. 
 * Fields will be storing in the index. </p>
 * @author cynthia FLORENTIN 
 *
 */
public interface IndexableContentI extends Serializable{
	
	/**
	 * <p>Add a new element keyword in the object IndexableContent </p>
	 * @param content represent a keyword in the document
	 * @throws IndexingServiceException
	 */
	public void addContentPart(String content) throws IndexingServiceException;
	
	/**
	 * <p>Allows to represent the list of keyword's hits on the document.<p>
	 * @return a list of String
	 */
    public String toString() ;
}
