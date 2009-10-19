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

import javax.ejb.Local;
import java.util.ArrayList;
import org.qualipso.factory.FactoryResourceIdentifier;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 20 May 2009
 */
@Local
public interface IndexingService {
	public void index(FactoryResourceIdentifier fri) throws IndexingServiceException;

    public void reindex(FactoryResourceIdentifier fri) throws IndexingServiceException;

    public void remove(FactoryResourceIdentifier fri) throws IndexingServiceException;

    public ArrayList<SearchResult> search(String query) throws IndexingServiceException;
}
