package org.qualipso.factory.indexing;
/**
 * @author Benjamin Dreux(benjiiiiii@gmail.com)
 * @date 28 oct 2009
 * */
import java.util.ArrayList;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.qualipso.factory.FactoryResourceIdentifier;

public class Index implements IndexI {

	@Override
	public void index(FactoryResourceIdentifier fri) throws IndexingServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void reindex(FactoryResourceIdentifier fri) throws IndexingServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(FactoryResourceIdentifier fri) throws IndexingServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<SearchResult> search(String query) throws IndexingServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
