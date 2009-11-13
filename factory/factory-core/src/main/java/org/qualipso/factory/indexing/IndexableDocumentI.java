/**
 * 
 */
package org.qualipso.factory.indexing;

/**
 * Interface which specify the property of indexable document. 
 * Documents are the unit of indexing. 
 * A Document is a set of fields. Each field has a name and a textual value. 
 * @author cynthia FLORENTIN
 *
 */
public interface IndexableDocumentI {
	
  /**
   * Give a Factory Resource Identifier (FRI) reference of document. 
   * @return The string form of this URI
   * @throws IndexingServiceException
   */
  public String getResourceFRI() throws IndexingServiceException ;

  /**
   * Give the service of document
   * @return a string which represent the service of document
   * @throws IndexingServiceException
   */
  public String getResourceService() throws IndexingServiceException ;
    
  /**
   * Give the type of document. 
   * The type is defined according to the extension of document.  
   * @return a string which represent the type of document
   * @throws IndexingServiceException
   */
  public String getResourceType() throws IndexingServiceException ;
    
  /**
   * Give the name of document
   * @return
   * @throws IndexingServiceException
   */
  public String getResourceShortName() throws IndexingServiceException ;
    
  /**
   * Give an object of type IndexableContent
   * @see IndexableContentI 
   * @return an indexable content
   * @throws IndexingServiceException
   */
  public IndexableContentI getIndexableContent() throws IndexingServiceException ;
}
