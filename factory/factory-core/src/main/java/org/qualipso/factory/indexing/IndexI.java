package org.qualipso.factory.indexing;

import java.util.ArrayList;
import javax.ejb.Local;
import org.qualipso.factory.FactoryResourceIdentifier;

@Local
public interface IndexI {
	public void index(FactoryResourceIdentifier fri) throws IndexingServiceException;

    public void reindex(FactoryResourceIdentifier fri) throws IndexingServiceException;

    public void remove(FactoryResourceIdentifier fri) throws IndexingServiceException;

    public ArrayList<SearchResult> search(String query) throws IndexingServiceException;

}
