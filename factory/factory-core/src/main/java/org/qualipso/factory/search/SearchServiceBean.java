package org.qualipso.factory.search;

/*
 * @author Benjamin DREUX (benjiiiiii@gmail.com)
 * @date 7/12/09
 */
import java.util.ArrayList;
import java.util.Iterator;

import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.indexing.IndexingService;
import org.qualipso.factory.indexing.SearchResult;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.security.pep.PEPService;

@Stateless(name = SearchService.SERVICE_NAME, mappedName = FactoryNamingConvention.SERVICE_PREFIX + SearchService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.search.SearchService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE
        + SearchService.SERVICE_NAME, serviceName = SearchService.SERVICE_NAME)
@WebContext(contextRoot = FactoryNamingConvention.WEB_SERVICE_CORE_MODULE_CONTEXT, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX
        + SearchService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class SearchServiceBean implements SearchService {
    private static Log logger = LogFactory.getLog(SearchServiceBean.class);
    private IndexingService indexing;
    private MembershipService membership;
    private PEPService pep;
    private SessionContext ctx;

    @EJB
    public void setIndexingService(IndexingService indexing) {
        this.indexing = indexing;
    }

    public IndexingService getIndexingService() {
        return indexing;
    }

    @EJB
    public void setMembershipService(MembershipService membership) {
        this.membership = membership;
    }

    public MembershipService getMembershipService() {
        return membership;
    }

    @EJB
    public void setPEPService(PEPService pep) {
        this.pep = pep;
    }

    public PEPService getPEPService() {
        return pep;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ArrayList<SearchResult> searchResource(String query) throws SearchServiceException {
    	logger.info("SearchResource(...) called");
        logger.debug("params : query=" + query);
        
        try {
        	Iterator<SearchResult> iter = indexing.search(query).iterator();
    		ArrayList<SearchResult> checkedRes = new ArrayList<SearchResult>();
    		String profile = membership.getProfilePathForConnectedIdentifier();
			while (iter.hasNext()) {
				SearchResult current = iter.next();
				String path = current.getPath();
				try {
					pep.checkSecurity(profile, path, "read");
					checkedRes.add(current);
				} catch (Exception e) {
				}
			}
			return checkedRes;
		} catch (Exception e) {
			logger.error("Unable to perform " + query + " query against index", e);
            ctx.setRollbackOnly();
            throw new SearchServiceException("Unable to perform " + query + " query against index", e);
		}
	}

    @Override
    public FactoryResource findResource(String path) throws FactoryException {
        logger.info("findResource(...) called");
        logger.debug("params : path=" + path);

        throw new SearchServiceException(SearchService.SERVICE_NAME + " service does not manage any resource");

    }

    @Override
    public String[] getResourceTypeList() {
        return RESOURCE_TYPE_LIST;
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

}
