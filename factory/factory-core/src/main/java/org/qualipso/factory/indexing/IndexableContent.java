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
 * @author Benjamin DREUX
 * @author cynthia FLORENTIN
 * @see IndexableContentI
 */
@SuppressWarnings("serial")
public class IndexableContent implements IndexableContentI {
    private StringBuffer sb;
    
    /**
     * Constructor with a type of object to represent the list
     */
    public IndexableContent() {
        sb = new StringBuffer();
    }
    
    /**
     * @see IndexableContentI#addContentPart(String)
     */
    public void addContentPart(String content) throws IndexingServiceException {
        sb.append(content);
    }
    
    /**
     * @see IndexableContentI#toString()
     */
    public String toString() {
        return sb.toString();
    }
}
