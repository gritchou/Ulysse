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
package org.qualipso.factory;

import java.io.Serializable;

import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
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

    public FactoryResourceIdentifier() {
    }

    public FactoryResourceIdentifier(String service, String type, String id) {
        this.service = service;
        this.type = type;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getService() {
        return service;
    }

    public static FactoryResourceIdentifier deserialize(String serializedResourceId) {
        if (serializedResourceId == null) {
            return null;
        }

        StringTokenizer tokenizer = new StringTokenizer(serializedResourceId, "/");

        return new FactoryResourceIdentifier(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken());
    }

    public String serialize() {
        return this.getService() + "/" + this.getType() + "/" + this.getId();
    }

    public String toString() {
        return "Service:" + getService() + "; Type:" + getType() + "; Id:" + getId();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
