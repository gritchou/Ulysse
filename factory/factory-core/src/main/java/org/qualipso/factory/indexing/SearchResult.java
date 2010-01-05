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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResourceIdentifier;

/**
 * <p>
 * Class which allows to get back the answer of a request for a research
 * with certain property as the type of document, its path, a description on
 * document, etc....
 * </p>
 * 
 * @author Benjamin DREUX
 * @author Cynthia FLORENTIN
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */

// TODO move this class to the factory level (FactorySearchResult)
@XmlType(name = "search-result", namespace = FactoryNamingConvention.SEARCH_NAMESPACE, propOrder = { "path", "score", "explain", "name", "type", "identifier" })
public class SearchResult implements Serializable {

    private static final long serialVersionUID = 1L;
    private String path;
    private float score;
    private String explain;
    private String name;
    private String type;
    private FactoryResourceIdentifier resourceFRI;

    /**
     * <p>
     * Give the path on the result of search
     * </p>
     * 
     * @return a string which correspond the path
     */
    @XmlAttribute(name = "id", required = true)
    public String getPath() {
        return path;
    }

    /**
     * <p>
     * Set the path of document
     * </p>
     * 
     * @param path, a string which correspond the path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * <p>
     * Give the percentage of correspondence between the query and the number of
     * total result
     * </p>
     * 
     * @return a number of type float which correspond the score
     */
    @XmlAttribute(name = "score", required = true)
    public float getScore() {
        return score;
    }

    /**
     * <p>
     * Set the score of document which is the percent
     * </p>
     * 
     * @param score, a number of type float which correspond the score
     */
    public void setScore(float score) {
        this.score = score;
    }

    /**
     * <p>
     * Give a little description who is associated to the path of document
     * </p>
     * 
     * @return a string which is a description of document
     */
    @XmlAttribute(name = "explain", required = true)
    public String getExplain() {
        return explain;
    }

    /**
     * <p>
     * Set the description of document
     * </p>
     * 
     * @param explain
     *            , a string which is a description of document
     */
    public void setExplain(String explain) {
        this.explain = explain;
    }

    /**
     * <p>
     * Give the name of document
     * </p>
     * 
     * @return the string which is the name
     */
    @XmlAttribute(name = "name", required = true)
    public String getName() {
        return name;
    }

    /**
     * <p>
     * Set a name of document
     * </p>
     * 
     * @param name, the string which is the name of document
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>
     * Give the type of document
     * </p>
     * 
     * @return the type of document
     */
    @XmlAttribute(name = "type", required = true)
    public String getType() {
        return type;
    }

    /**
     * <p>
     * Set the type of document
     * </p>
     * 
     * @param type, a string which is the type of document
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * <p>
     * Give an object which is a ressource of identifier
     * </p>
     * 
     * @return an object FactoryResourceIdentifier
     * @see org.qualipso.factory.FactoryResourceIdentifier
     */
    @XmlTransient
    public FactoryResourceIdentifier getFactoryResourceIdentifier() {
        return resourceFRI;
    }

    /**
     * <p>
     * Set a object FactoryResourceIdentifier
     * </p>
     * 
     * @param resourceIdentifier
     *            is an object FactoryResourceIdentifier
     * @see org.qualipso.factory.FactoryResourceIdentifier
     */
    public void setFactoryResourceIdentifier(FactoryResourceIdentifier resourceIdentifier) {
        this.resourceFRI = resourceIdentifier;
    }

    /**
     * <p>
     * Set a object FactoryResourceIdentifier
     * </p>
     * 
     * @param resourceIdentifier
     *            is a String
     * @see org.qualipso.factory.FactoryResourceIdentifier
     */
    public void setFactoryResourceIdentifier(String resourceIdentifier) {
        this.resourceFRI = FactoryResourceIdentifier.deserialize(resourceIdentifier);
    }

}
