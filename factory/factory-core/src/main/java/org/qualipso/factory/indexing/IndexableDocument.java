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


    public IndexableDocument(){
    }
    public void setResourceFRI(String resourceFRI){
    	this.resourceFRI = resourceFRI;
    }
    public void setResourceService(String resourceService){
    	this.resourceService = resourceService;
    }
    public void setResourceType(String resourceType){
    	this.resourceType = resourceType;
    }
    public void setResourceShortName(String resourceShortName){
    	this.resourceShortName = resourceShortName;
    }
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
	public Document getDocument() {
		Document document = new Document();
        document.add(new Field("FRI", resourceFRI , Field.Store.YES, Field.Index.UN_TOKENIZED));
        document.add(new Field("SERVICE", resourceService, Field.Store.YES, Field.Index.NO));
        document.add(new Field("TYPE", resourceType, Field.Store.YES, Field.Index.NO));
        document.add(new Field("CONTENT", indexableContent.toString(), Field.Store.YES, Field.Index.TOKENIZED));
        document.add(new Field("NAME", resourceShortName, Field.Store.YES, Field.Index.TOKENIZED));

        return document;
	}
}
