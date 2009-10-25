package org.qualipso.factory.indexing;

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.qualipso.factory.FactoryResourceIdentifier;


public interface Indexer  {
	
	public void index(FactoryResourceIdentifier fri) throws IndexingServiceException;

    public void reindex(FactoryResourceIdentifier fri) throws IndexingServiceException;

    public void remove(FactoryResourceIdentifier fri) throws IndexingServiceException;

    public ArrayList<SearchResult> search(String query) throws IndexingServiceException;
}
