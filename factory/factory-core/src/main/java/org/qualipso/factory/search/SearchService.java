package org.qualipso.factory.search;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;
import org.qualipso.factory.indexing.SearchResult;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 26 June 2009
 */
@Remote
@WebService(name = "SearchService", targetNamespace = "http://org.qualipso.factory.ws/service/search")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface SearchService extends FactoryService {
	
	@WebMethod
	@WebResult(name = "search-result-list")
	public SearchResult[] searchResource(String path) throws SearchServiceException;
	
}
