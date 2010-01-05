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
 * Christophe Bouthier / INRIA
 * 
 */
package org.qualipso.factory.indexing;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.qualipso.factory.FactoryResourceIdentifier;

/**
 * <p>
 * Class which specify the property of indexable document. Documents are the
 * unit of indexing. A Document is a set of fields. Each field has a name and a
 * textual value.
 * </p>
 * 
 * @see IndexableDocumentI
 * @author Benjamin DREUX
 * @author cynthia FLORENTIN
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
public class IndexableDocument {
    private FactoryResourceIdentifier resourceIdentifier;
    private String resourceService;
    private String resourceType;
    private String path;
    private IndexableContent indexableContent;

    /**
     * <p>
     * Set a Factory Resource Identifier (FRI) reference of document with a
     * FactoryResourceIdentifier
     * </p>
     * 
     * @param resourceFRI
     *            is a FactoryResourceIdentifier
     */
    public void setResourceIdentifier(FactoryResourceIdentifier resourceIdentifier) {
        this.resourceIdentifier = resourceIdentifier;
    }

    /**
     * <p>
     * Set the service of document with the string
     * </p>
     * 
     * @param resourceService
     *            is a string
     */
    public void setResourceService(String resourceService) {
        this.resourceService = resourceService;
    }

    /**
     * <p>
     * Set the type of document with a string
     * </p>
     * 
     * @param resourceType
     *            is a string
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * <p>
     * set an object of type IndexableContent
     * </p>
     * 
     * @param indexableContent
     */
    public void setIndexableContent(IndexableContent indexableContent) {
        this.indexableContent = indexableContent;
    }

    /**
     *@see IndexableDocumentI#setResourcePath()
     **/
    public void setResourcePath(String path) {
        this.path = path;
    }

    /**
     * @see IndexableDocumentI#getResourceURI()
     */
    public FactoryResourceIdentifier getResourceIdentifier() {
        return resourceIdentifier;

    }

    /**
     * @see IndexableDocumentI#getResourceService()
     */
    public String getResourceService() {
        return resourceService;
    }

    /**
     * @see IndexableDocumentI#getResourceType()
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * @see IndexableDocumentI#getIndexableContent()
     */
    public IndexableContent getIndexableContent() {
        return indexableContent;
    }

    /**
     * @see IndexableDocumentI#getResourcePath()
     **/
    public String getResourcePath() {
        return path;
    }

    /**
     * <p>
     * Give a document. A Document has a list of fields; each field has a name
     * and a textual value. A field Index specifies whether and how a field
     * should be indexed. Index the tokens produced by running the field's value
     * through an Analyzer.
     * </p>
     * 
     * @return a Lucene Document is a record in the index.
     */
    public Document getDocument() {
        Document document = new Document();
        document.add(new Field("IDENTIFIER", resourceIdentifier.serialize(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("SERVICE", resourceService, Field.Store.YES, Field.Index.NO));
        document.add(new Field("TYPE", resourceType, Field.Store.YES, Field.Index.NO));
        document.add(new Field("PATH", path, Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("CONTENT", indexableContent.toString(), Field.Store.YES, Field.Index.ANALYZED));
        return document;
    }
}
