package org.qualipso.factory.indexing.base;

import org.qualipso.factory.indexing.IndexingServiceException;

public class IndexBaseFactory {
	
	public static IndexBase getIndexBase() throws IndexingServiceException {
		return LuceneIndexBase.getInstance();
	}

}
