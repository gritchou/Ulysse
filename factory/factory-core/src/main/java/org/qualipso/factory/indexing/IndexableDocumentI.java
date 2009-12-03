
package org.qualipso.factory.indexing;

import org.apache.lucene.document.Document;

/**
 * <p>Interface which specify the property of indexable document. 
 * Documents are the unit of indexing. 
 * A Document is a set of fields. Each field has a name and a textual value. </p>
 * @author cynthia FLORENTIN
 *
 */
public interface IndexableDocumentI {
	
  /**
   * <p>Give a Factory Resource Identifier (FRI) reference of document. </p>
   * @return The string form of this URI
   */
  public String getResourceFRI();

  /**
   * <p>Give the service of document</p>
   * @return a string which represent the service of document
   */
  public String getResourceService();
    
  /**
   * <p>Give the type of document. </p>
   * The type is defined according to the extension of document.  
   * @return a string which represent the type of document
   */
  public String getResourceType();
    
  /**
   * <p>Give the name of document</p>
   * @return a name of document
   */
  public String getResourceShortName();
    
  /**
   * <p>Give an object of type IndexableContent</p>
   * @see IndexableContentI 
   * @return an indexable content
   */
  public IndexableContentI getIndexableContent();
  
  /**
   * <p>Set a Factory Resource Identifier (FRI) reference of document with a string</p>
   * @param resourceFRI is a string
   */
  public void setResourceFRI(String resourceFRI);
  
  /**
   * <p>Set the service of document with the string</p>
   * @param resourceService is a string
   */
  public void setResourceService(String resourceService);
  
  /**
   * <p>Set the type of document with a string</p>
   * @param resourceType is a string
   */
  public void setResourceType(String resourceType);
  
  /**
   * <p>Set the name of document with a string</p>
   * @param resourceShortName is a string
   */
  public void setResourceShortName(String resourceShortName);
  
  /**
   * <p>set an object of type IndexableContent</p>
   * @param indexableContent
   */
  public void setIndexableContent(IndexableContent indexableContent);
  
  /**
   * <p> Give a document. 
   * A Document has a list of fields; each field has a name and a textual value.  
   * A field Index specifies whether and how a field should be indexed. 
   * Index the tokens produced by running the field's value through an Analyzer.
   * </p>
   * @return a Lucene Document is a record in the index.
   */
	public Document getDocument() ;
}
