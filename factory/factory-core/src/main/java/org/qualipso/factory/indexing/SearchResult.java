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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryResourceIdentifier;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
//TODO move this class to the factory level (FactorySearchResult)
@XmlType(name = "SearchResult", namespace = "http://org.qualipso.factory.ws/index", propOrder =  {
    "path", "score", "explain", "name", "type", "identifier"}
)
@SuppressWarnings("serial")
public class SearchResult implements Serializable {
    private String path;
    private float score;
    private String explain;
    private String name;
    private String type;
    private FactoryResourceIdentifier resourceIdentifier;

    public SearchResult() {
    }

    @XmlAttribute(name = "id", required = true)
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @XmlAttribute(name = "score", required = true)
    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @XmlAttribute(name = "explain", required = true)
    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    @XmlAttribute(name = "name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "type", required = true)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlTransient
    public FactoryResourceIdentifier getResourceIdentifier() {
        return resourceIdentifier;
    }

    public void setResourceIdentifier(FactoryResourceIdentifier resourceIdentifier) {
        this.resourceIdentifier = resourceIdentifier;
    }
    
    @XmlAttribute(name = "factory-resource-identifier", required = true)
    public String getIdentifier() {
        return resourceIdentifier.serialize();
    }

    public void setIdentifier(String identifier) {
        this.resourceIdentifier = FactoryResourceIdentifier.deserialize(identifier);
    }
}
