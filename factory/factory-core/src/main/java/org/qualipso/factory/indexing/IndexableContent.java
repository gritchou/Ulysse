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

/**
 * <p>
 * Class which allows to have fields of the document Indexable. So that they
 * may be represented like a list of hits on the document A field is a section
 * of a Document. Values may be free text, provided as a String or they may be
 * atomic keywords. Such keywords may be used to represent dates, urls, etc.
 * Fields will be storing in the index.
 * </p>
 * 
 * @author Benjamin DREUX
 * @author Cynthia FLORENTIN
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 * @see IndexableContentI
 */
@SuppressWarnings("serial")
public class IndexableContent {
    private StringBuffer sb;

    /**
     * <p>
     * Constructor with a type of object to represent the list
     * </p>
     */
    /**
     * Class constructor
     * 
     */
    public IndexableContent() {
        sb = new StringBuffer();
    }

    /**
     * <p>
     * Add a new element keyword in the object IndexableContent
     * </p>
     * 
     * @param content represent a keyword in the document
     * @throws IndexingServiceException
     */
    public void addContentPart(String content) throws IndexingServiceException {
        sb.append(content + " ");
    }

    /**
     * <p>
     * Allows to represent the list of keyword's hits on the document.
     * <p>
     * 
     * @return a list of String
     */
    @Override
    public String toString() {
        return sb.toString();
    }
}
