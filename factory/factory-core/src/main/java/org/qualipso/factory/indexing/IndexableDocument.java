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


/**
 * @see IndexableDocumentI
 * @author Benjamin DREUX
 * @author cynthia FLORENTIN
 */
public class IndexableDocument implements IndexableDocumentI{
    public String resourceURI;
    public String resourceService;
    public String resourceType;
    public String resourceShortName;
    public IndexableContent indexableContent;
    
    /**
     * @see IndexableDocumentI#getResourceURI()
     */
    public String getResourceURI() throws IndexingServiceException {
        return resourceURI;
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
}
