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
package org.qualipso.factory.core.entity;

import org.qualipso.factory.FactoryNamingConvention;

import javax.activation.DataHandler;

import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The FilData contains only data.<br/>
 * <br/>
 * This object ensure better XML serialization using specific JAXB annotation @XmlAttachementRef.
 * Strategy is to put data outside the SOAP envelop using a reference in order to allow
 * possibility to treat data flow not in memory.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 19 august 2009
 */
@XmlRootElement(name = "file-data")
@XmlType(name = "file-data", namespace = FactoryNamingConvention.RESOURCE_NAMESPACE + "file-data", propOrder =  {
    "data"}
)
public class FileData {
    private DataHandler data;

    public FileData() {
    }

    public FileData(DataHandler data) {
        this.data = data;
    }

    @XmlElement(name = "data", required = true)
    @XmlAttachmentRef
    public DataHandler getData() {
        return data;
    }

    public void setData(DataHandler data) {
        this.data = data;
    }
}
