package org.qualipso.factory.indexing;

import javax.ejb.Local;


import org.qualipso.factory.FactoryResourceIdentifier;

@Local
public interface IndexerService  {
	
	public void index(FactoryResourceIdentifier fri) throws IndexingServiceException;

    public void reindex(FactoryResourceIdentifier fri) throws IndexingServiceException;

    public void remove(FactoryResourceIdentifier fri) throws IndexingServiceException;

}
