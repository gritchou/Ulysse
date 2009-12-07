package org.qualipso.factory.indexing;

import java.io.Serializable;

import org.qualipso.factory.FactoryResourceIdentifier;

/**
 * <p>Interface which allows to get back the answer of a request 
 * for a research with certain property as the type of document, its path,
 * a description on document, etc....</p>
 * @author cynthia FLORENTIN 
 *
 */

public interface SearchResultI extends Serializable{
	
	/**
	 * <p>Give the path on the result of search</p>
	 * @return a string which correspond the path
	 */
    public String getPath();
    
    /**
     * <p>Set the path of document</p>
     * @param path, a string which correspond the path
     */
    public void setPath(String path);
    
    /**
     * <p>Give the percentage of correspondence between the query
     *  and the number of total result</p> 
     * @return a number of type float which correspond the score
     */
    public float getScore();
    
    /**
     * <p>Set the score of document which is the percent</p>
     * @param score, a number of type float which correspond the score
     */
    public void setScore(float score);
    
    /**
     * <p>Give a little description who is associated to the path of document</p>
     * @return a string which is a description of document
     */
    public String getExplain();
    
    /**
     * <p>Set the description of document</p>
     * @param explain, a string which is a description of document
     */
    public void setExplain(String explain) ;
    
    /**
     * <p>Give the name of document</p>
     * @return the string which is the name
     */
    public String getName() ;
    
    /**
     * <p>Set a name of document</p>
     * @param name, the string which is the name of document
     */
    public void setName(String name) ;
    
    /**
     * <p>Give the type of document</p>
     * @return the type of document
     */
    public String getType() ;

    /**
     * <p>Set the type of document</p>
     * @param type, a string which is the type of document
     */
    public void setType(String type);
    
    /**
     * <p>Give an object which is a ressource of identifier </p>
     * @return an object FactoryResourceIdentifier
     * @see org.qualipso.factory.FactoryResourceIdentifier
     */
    public FactoryResourceIdentifier getFactoryResourceIdentifier() ;
    
    /**
     * <p>Set a object FactoryResourceIdentifier</p>
     * @param resourceIdentifier is an object FactoryResourceIdentifier
     * @see org.qualipso.factory.FactoryResourceIdentifier
     */
    public void setFactoryResourceIdentifier(FactoryResourceIdentifier resourceIdentifier) ;
   
    /**
     * <p>Set a object FactoryResourceIdentifier</p>
     * @param resourceIdentifier is a String
     * @see org.qualipso.factory.FactoryResourceIdentifier
     */
    public void setFactoryResourceIdentifier(String resourceIdentifier) ;
    
    
    
}
