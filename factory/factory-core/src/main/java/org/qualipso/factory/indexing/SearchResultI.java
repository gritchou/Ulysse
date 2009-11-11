package org.qualipso.factory.indexing;

import java.io.Serializable;

import org.qualipso.factory.FactoryResourceIdentifier;

/**
 * Interface which allows to get back the answer of a request 
 * for a research with certain property as the type of document, its path,
 * a description on document, etc....
 * @author cynthia FLORENTIN 
 *
 */

public interface SearchResultI extends Serializable{
	
	/**
	 * Give the path on the result of search
	 * @return a string which correspond the path
	 */
    public String getPath();
    
    /**
     * Set the path of document
     * @param path, a string which correspond the path
     */
    public void setPath(String path);
    
    /**
     * Give the percentage of correspondence between the query
     *  and the number of total result 
     * @return a number of type float which correspond the score
     */
    public float getScore();
    
    /**
     * Set the score of document which is the percent
     * @param score, a number of type float which correspond the score
     */
    public void setScore(float score);
    
    /**
     * Give a little description who is associated to the path of document
     * @return a string which is a description of document
     */
    public String getExplain();
    
    /**
     * Set the description of document
     * @param explain, a string which is a description of document
     */
    public void setExplain(String explain) ;
    
    /**
     * Give the name of document
     * @return the string which is the name
     */
    public String getName() ;
    
    /**
     * Set a name of document
     * @param name, the string which is the name of document
     */
    public void setName(String name) ;
    
    /**
     * Give the type of document
     * @return the type of document
     */
    public String getType() ;

    /**
     * Set the type of document
     * @param type, a string which is the type of document
     */
    public void setType(String type);
    
    /**
     * Give an object which is a ressource of identifier 
     * @return an object FactoryResourceIdentifier
     * @see org.qualipso.factory.FactoryResourceIdentifier
     */
    public FactoryResourceIdentifier getResourceIdentifier() ;
    
    /**
     * Set a object FactoryResourceIdentifier
     * @param resourceIdentifier is an object FactoryResourceIdentifier
     * @see org.qualipso.factory.FactoryResourceIdentifier
     */
    public void setResourceIdentifier(FactoryResourceIdentifier resourceIdentifier) ;
    
    /**
     * Give a string which represent the identifier of document
     * @return a string of identifier
     */
    public String getIdentifier() ;
    
    /**
     * Set a identifier of document
     * @param identifier, a string of identifier's document 
     */
    public void setIdentifier(String identifier) ;
    
}
