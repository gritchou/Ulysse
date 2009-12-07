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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.qualipso.factory.FactoryResourceIdentifier;


/**
 * <p> Class which implements IndexableDocumentI</p>
 * @see IndexableDocumentI
 * @author Benjamin DREUX
 * @author cynthia FLORENTIN
 */

public class IndexableDocument implements IndexableDocumentI{
    private FactoryResourceIdentifier resourceFRI;
    private String resourceService;
    private String resourceType;
    private String resourceShortName;
  	private String path;
    private IndexableContent indexableContent;


    /**
     * <p>Set a Factory Resource Identifier (FRI) reference of document with a FactoryResourceIdentifier</p>
     * @param resourceFRI is a FactoryResourceIdentifier
     */
    public void setResourceFRI(FactoryResourceIdentifier resourceFRI){
    	this.resourceFRI = resourceFRI;
    }
    
    /**
     * <p>Set the service of document with the string</p>
     * @param resourceService is a string
     */
    public void setResourceService(String resourceService){
    	this.resourceService = resourceService;
    }
    
    /**
     * <p>Set the type of document with a string</p>
     * @param resourceType is a string
     */
    public void setResourceType(String resourceType){
    	this.resourceType = resourceType;
    }
    
    /**
     * <p>Set the name of document with a string</p>
     * @param resourceShortName is a string
     */
    public void setResourceShortName(String resourceShortName){
    	this.resourceShortName = resourceShortName;
    }
    
    /**
     * <p>set an object of type IndexableContent</p>
     * @param indexableContent
     */
    public void setIndexableContent(IndexableContent indexableContent){
    	this.indexableContent = indexableContent;
    }
    /**
    *@see IndexableDocumentI#setResourcePath()
    **/
    public void setResourcePath(String path){
    	this.path = path;
    }
    
    /**
     * @see IndexableDocumentI#getResourceURI()
     */
    public FactoryResourceIdentifier getResourceFRI() {
        return resourceFRI;

    }
    
    /**
     * @see IndexableDocumentI#getResourceService()
     */
    public String getResourceService(){
        return resourceService;
    }
    
    /**
     * @see IndexableDocumentI#getResourceType()
     */
    public String getResourceType(){
        return resourceType;
    }
    
    /**
     * @see IndexableDocumentI#getResourceShortName()
     */
    public String getResourceShortName(){
        return resourceShortName;
    }
    
    /**
     * @see IndexableDocumentI#getIndexableContent()
     */
    public IndexableContent getIndexableContent(){
        return indexableContent;
    }
    /**
    * @see  IndexableDocumentI#getResourcePath()
    **/
    public String getResourcePath(){
    	return path;
    }
    
    /**
     * <p> Give a document. 
     * A Document has a list of fields; each field has a name and a textual value.  
     * A field Index specifies whether and how a field should be indexed. 
     * Index the tokens produced by running the field's value through an Analyzer.
     * </p>
     * @return a Lucene Document is a record in the index.
     */
	public Document getDocument() {
		Document document = new Document();
        document.add(new Field("FRI", resourceFRI.serialize() , Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("SERVICE", resourceService, Field.Store.YES, Field.Index.NO));
        document.add(new Field("TYPE", resourceType, Field.Store.YES, Field.Index.NO));
		document.add(new Field("PATH", path, Field.Store.YES, Field.Index.NOT_ANALYZED ));
        document.add(new Field("CONTENT", indexableContent.toString(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("NAME", resourceShortName, Field.Store.YES, Field.Index.ANALYZED));
        return document;
	}
}
