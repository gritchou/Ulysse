package org.qualipso.factory.indexing;

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
import org.qualipso.factory.FactoryService;

/**
 * Define the expectation to allow a service to index it's resource.
 */
public interface IndexableService extends FactoryService {

    /**
     * Get the indexable Content related to the resource located at a given
     * path.
     * 
     * @param path
     *            the path to the resource.
     * @return the IndexableDocument correspoding to the resource to index.
     * @thows IndexingServiceExcpetion if the resource can't be found.
     */
    public IndexableDocument getIndexableDocument(String path) throws IndexingServiceException;
}
