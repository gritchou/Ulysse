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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryResourceIdentifier;


/**
 * Class which represent the result of a search with a query
 * @author Benjamin DREUX
 * @author cynthia FLORENTIN 
 */
//TODO move this class to the factory level (FactorySearchResult)
@XmlType(name = "SearchResult", namespace = "http://org.qualipso.factory.ws/index", propOrder =  {
    "path", "score", "explain", "name", "type", "identifier"}
)
public class SearchResult implements SearchResultI {
    
	private static final long serialVersionUID = 1L;
	private String path;
    private float score;
    private String explain;
    private String name;
    private String type;
    private FactoryResourceIdentifier resourceIdentifier;

    /**
     * @see SearchResultI#getPath()
     */
    @XmlAttribute(name = "id", required = true)
    public String getPath() {
        return path;
    }
    
    /**
     * @see SearchResultI#setPath(String)
     */
    public void setPath(String path) {
        this.path = path;
    }
    
    /**
     * @see SearchResultI#getScore()
     */
    @XmlAttribute(name = "score", required = true)
    public float getScore() {
        return score;
    }

    /**
     * @see SearchResultI#setScore(float)
     */
    public void setScore(float score) {
        this.score = score;
    }

    /**
     * @see SearchResultI#getExplain()
     */
    @XmlAttribute(name = "explain", required = true)
    public String getExplain() {
        return explain;
    }
    
    /**
     * @see SearchResultI#setExplain(String)
     */
    public void setExplain(String explain) {
        this.explain = explain;
    }
    
    /**
     * @see SearchResultI#getName()
     */
    @XmlAttribute(name = "name", required = true)
    public String getName() {
        return name;
    }
    
    /**
     * @see SearchResultI#setName(String)
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @see SearchResultI#getType()
     */
    @XmlAttribute(name = "type", required = true)
    public String getType() {
        return type;
    }
    
    /**
     * @see SearchResultI#setType(String)
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @see SearchResultI#getResourceIdentifier()
     */
    @XmlTransient
    public FactoryResourceIdentifier getResourceIdentifier() {
        return resourceIdentifier;
    }
    
    /**
     * @see SearchResultI#setResourceIdentifier(FactoryResourceIdentifier)
     */
    public void setResourceIdentifier(FactoryResourceIdentifier resourceIdentifier) {
        this.resourceIdentifier = resourceIdentifier;
    }
    
    /**
     * @see SearchResultI#getIdentifier()
     */
    @XmlAttribute(name = "factory-resource-identifier", required = true)
    public String getIdentifier() {
        return resourceIdentifier.serialize();
    }

    /**
     * @see SearchResultI#setIdentifier(String)
     */
    public void setIdentifier(String identifier) {
        this.resourceIdentifier = FactoryResourceIdentifier.deserialize(identifier);
    }
}
