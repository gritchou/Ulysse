package org.qualipso.factory.indexing;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.qualipso.factory.notification.NotificationServiceBean;

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
	public void index(IndexableContent content) throws IndexingServiceException {
		//TODO
		logger.warn("index(...) called ### NOT IMPLEMENTED");
		logger.debug("params : content=\r\n" + content + "\r\n}");
	}

	@Override
	public void reindex(IndexableContent content) throws IndexingServiceException {
		//TODO
		logger.warn("reindex(...) called ### NOT IMPLEMENTED");
		logger.debug("params : content=\r\n" + content + "\r\n}");
	}

	@Override
	public void remove(String path) throws IndexingServiceException {
		//TODO
		logger.warn("remove(...) called ### NOT IMPLEMENTED");
		logger.debug("params : path=" + path);
	}

	@Override
	public SearchResult[] search(String pathPattern, String service, String resource, String query) throws IndexingServiceException {
		//TODO
		logger.warn("search(...) called ### NOT IMPLEMENTED");
		logger.debug("params : pathPattern=" + pathPattern + ", service=" + service + ", resource=" + resource + ", query=" + query);
		return new SearchResult[0];
	}
}
