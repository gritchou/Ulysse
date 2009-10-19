package org.qualipso.factory.indexing;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.notification.NotificationServiceBean;
import java.util.ArrayList;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 16 june 2009
 */
@Stateless(name = "Indexing", mappedName = "IndexingService")
@SecurityDomain(value = "JBossWSDigest")
public class IndexingServiceBean implements IndexingService {
	
	private static Log logger = LogFactory.getLog(NotificationServiceBean.class);
    
    public IndexingServiceBean() {
	}

	@Override
	public void index(FactoryResourceIdentifier fri) throws IndexingServiceException {
		//TODO
		logger.warn("index(...) called ### NOT IMPLEMENTED");
		logger.debug("params : FactoryResourceIdentifier=\r\n" + fri + "\r\n}");
	}

	@Override
	public void reindex(FactoryResourceIdentifier fri) throws IndexingServiceException {
		//TODO
		logger.warn("reindex(...) called ### NOT IMPLEMENTED");
		logger.debug("params : FactoryResourceIdentifier=\r\n" + fri + "\r\n}");
	}

	@Override
	public void remove(FactoryResourceIdentifier fri) throws IndexingServiceException {
		//TODO
		logger.warn("remove(...) called ### NOT IMPLEMENTED");
		logger.debug("params : FactoryResourceIdentifier=" + fri);
	}

	@Override
	public ArrayList<SearchResult> search(String query) throws IndexingServiceException {
		//TODO
		logger.warn("search(...) called ### NOT IMPLEMENTED");
		logger.debug("params : query=" + query);
		return null;
	}
}
