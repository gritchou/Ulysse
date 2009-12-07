package org.qualipso.factory.search;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.indexing.SearchResult;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @author Benjamin DREUX (benjiiiiii@gmail.com)
 * @date 26 June 2009
 */
@Remote
@WebService(name = SearchService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + SearchService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface SearchService extends FactoryService {
	
	public static final String SERVICE_NAME = "search";
	public static final String[] RESOURCE_TYPE_LIST = new String[] {};
	
	
	/**
	*Perform research against index using the given query.
	*@param query query to search for.
	*@param Result of the search action using the query against index.
	*@thow SearchServiceException if an internal error occur during the process.
	*/
	@WebMethod
	@WebResult(name = "search-result-list")
	public ArrayList<SearchResult> searchResource(String path) throws SearchServiceException;
	
}
