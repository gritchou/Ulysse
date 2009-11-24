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


/**
 * <p> Class which implements IndexableDocumentI</p>
 * @see IndexableDocumentI
 * @author Benjamin DREUX
 * @author cynthia FLORENTIN
 */

public class IndexableDocument implements IndexableDocumentI{
    public String resourceFRI;
    public String resourceService;
    public String resourceType;
    public String resourceShortName;
    public IndexableContent indexableContent;


    /**
     * <p>Set a Factory Resource Identifier (FRI) reference of document with a string</p>
     * @param resourceFRI is a string
     */
    public void setResourceFRI(String resourceFRI){
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
     * @see IndexableDocumentI#getResourceURI()
     */
    public String getResourceFRI() throws IndexingServiceException {
        return resourceFRI;

    }
    
    /**
     * @see IndexableDocumentI#getResourceService()
     */
    public String getResourceService() throws IndexingServiceException {
        return resourceService;
    }
    
    /**
     * @see IndexableDocumentI#getResourceType()
     */
    public String getResourceType() throws IndexingServiceException {
        return resourceType;
    }
    
    /**
     * @see IndexableDocumentI#getResourceShortName()
     */
    public String getResourceShortName() throws IndexingServiceException {
        return resourceShortName;
    }
    
    /**
     * @see IndexableDocumentI#getIndexableContent()
     */
    public IndexableContentI getIndexableContent() throws IndexingServiceException {
        return indexableContent;
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
        document.add(new Field("FRI", resourceFRI , Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("SERVICE", resourceService, Field.Store.YES, Field.Index.NO));
        document.add(new Field("TYPE", resourceType, Field.Store.YES, Field.Index.NO));
        document.add(new Field("CONTENT", indexableContent.toString(), Field.Store.YES, Field.Index.ANALYZED));
        document.add(new Field("NAME", resourceShortName, Field.Store.YES, Field.Index.ANALYZED));
        return document;
	}
}
