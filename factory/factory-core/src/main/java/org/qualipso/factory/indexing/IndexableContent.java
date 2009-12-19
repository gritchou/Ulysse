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
 * Class which implements IndexableContentI
 * </p>
 * 
 * @author Benjamin DREUX
 * @author Cynthia FLORENTIN
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 * @see IndexableContentI
 */
@SuppressWarnings("serial")
public class IndexableContent implements IndexableContentI {
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
     * @see IndexableContentI#addContentPart(String)
     */
    /**
     * Adding content part
     * 
     * @param the
     *            content
     */
    public void addContentPart(String content) throws IndexingServiceException {
        sb.append(content);
    }

    /**
     * @see IndexableContentI#toString()
     */
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return sb.toString();
    }
}
