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
package org.qualipso.factory.greeting.entity;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.greeting.GreetingService;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 june 2009
 */
@Entity
@XmlType(name = Name.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE + Name.RESOURCE_NAME, propOrder =  {
    "value"}
)
@SuppressWarnings("serial")
public class Name extends FactoryResource {
    public static final String RESOURCE_NAME = "name";
    @Id
    private String id;
    private String value;
    private String path;

    public Name() {
    }

    @XmlAttribute(name = "id", required = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name = "path", required = true)
    @Transient
    @Override
    public String getResourcePath() {
        return path;
    }

    public void setResourcePath(String path) {
        this.path = path;
    }

    @XmlElement(name = "value", required = true)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    @XmlTransient
    public FactoryResourceIdentifier getFactoryResourceIdentifier() {
        return new FactoryResourceIdentifier(GreetingService.SERVICE_NAME, Name.RESOURCE_NAME, getId());
    }

    @Override
    @XmlTransient
    public String getResourceName() {
        return getValue();
    }
}
