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
package org.qualipso.factory;

import java.io.Serializable;

import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * This class represent a unique identifier for a resource instance.<br/>
 * <br/>
 * This identifier contains :
 * <ul>
 * <li> the name of the service which is able to manage this type of resource,
 * <li> the type of the resource,
 * <li> the external id of this resource.
 * </ul>
 * The external id is used to be able for a service to recover the real resource data in
 * the external storage subsysteme.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@XmlJavaTypeAdapter(FactoryResourceIdentifierAdapter.class)
@XmlType(name = "FactoryResourceIdentifier", namespace = "http://org.qualipso.factory.ws/factory")
@SuppressWarnings("serial")
public class FactoryResourceIdentifier implements Serializable {
    private String service;
    private String type;
    private String id;

    /**
     * Class constructor
     */
    public FactoryResourceIdentifier() {
    }

    /**
     * Class constructor specifying the service name, the resource type and the resource id
     *
     * @param service the name of service which manage this resource
     * @param type the type of this resource
     * @param id the external id of this resource
     */
    public FactoryResourceIdentifier(String service, String type, String id) {
        this.service = service;
        this.type = type;
        this.id = id;
    }

    /**
     * @return the external id of this resource
     */
    public String getId() {
        return id;
    }

    /**
     * @return the type of this resource
     */
    public String getType() {
        return type;
    }

    /**
     * @return the service name which manage this resource
     */
    public String getService() {
        return service;
    }

    /**
     * @param serializedResourceId a serialized representation of a FactoryResourceIndentifier
     * @return The parsed FactoryResourceIdentifier
     */
    public static FactoryResourceIdentifier deserialize(String serializedResourceId) {
        if (serializedResourceId == null) {
            return null;
        }

        StringTokenizer tokenizer = new StringTokenizer(serializedResourceId, "/");

        return new FactoryResourceIdentifier(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken());
    }

    /**
     * @return a serialized representation of this FactoryResourceIdentifier
     */
    public String serialize() {
        return this.getService() + "/" + this.getType() + "/" + this.getId();
    }

    @Override
    public String toString() {
        return "Service:" + getService() + "; Type:" + getType() + "; Id:" + getId();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((id == null) ? 0 : id.hashCode());
        result = (prime * result) + ((service == null) ? 0 : service.hashCode());
        result = (prime * result) + ((type == null) ? 0 : type.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        FactoryResourceIdentifier other = (FactoryResourceIdentifier) obj;

        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }

        if (service == null) {
            if (other.service != null) {
                return false;
            }
        } else if (!service.equals(other.service)) {
            return false;
        }

        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }

        return true;
    }
}
