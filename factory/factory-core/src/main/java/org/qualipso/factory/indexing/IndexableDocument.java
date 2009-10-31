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
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
public class IndexableDocument {
    public String resourceURI;
    public String resourceService;
    public String resourceType;
    public String resourceShortName;
    public IndexableContent indexableContent;

    public String getResourceURI() throws IndexingServiceException {
        return resourceURI;
    }

    public String getResourceService() throws IndexingServiceException {
        return resourceService;
    }

    public String getResourceType() throws IndexingServiceException {
        return resourceType;
    }

    public String getResourceShortName() throws IndexingServiceException {
        return resourceShortName;
    }

    public IndexableContent getIndexableContent() throws IndexingServiceException {
        return indexableContent;
    }
}
