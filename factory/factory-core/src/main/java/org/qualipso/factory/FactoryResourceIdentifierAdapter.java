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

import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * A utility class for XML processing allowing FactoryResourceIdentifier to be marshal/unmarshal from/to xml files.
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
public class FactoryResourceIdentifierAdapter extends XmlAdapter<FactoryResourceIdentifier, String> {
    
	/**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public FactoryResourceIdentifier marshal(String serializedFactoryResourceIdentifier)
        throws Exception {
        return FactoryResourceIdentifier.deserialize(serializedFactoryResourceIdentifier);
    }

    /**
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public String unmarshal(FactoryResourceIdentifier factoryResourceIdentifier)
        throws Exception {
        return factoryResourceIdentifier.toString();
    }
}
