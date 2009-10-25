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

@SuppressWarnings("serial")
@MessageDriven(name="LongProcessMessageBean", activationConfig = {
	    @ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Queue"),
	    @ActivationConfigProperty(propertyName="destination", propertyValue="queue/QualipsoFactory/Indexing")

	})


public class IndexerBean implements MessageDrivenBean, MessageListener, Indexer {
	
	@Resource
    MessageDrivenContext msgDrivCtx;

	@Override
	public void ejbRemove() throws EJBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMessageDrivenContext(MessageDrivenContext arg0)
			throws EJBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void index(FactoryResourceIdentifier fri)
			throws IndexingServiceException {
		// TODO Auto-generated method stub
		
	}

	public void reindex(FactoryResourceIdentifier fri)
			throws IndexingServiceException {
		// TODO Auto-generated method stub
		
	}

	public void remove(FactoryResourceIdentifier fri)
			throws IndexingServiceException {
		// TODO Auto-generated method stub
		
	}

	public ArrayList<SearchResult> search(String query)
			throws IndexingServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
