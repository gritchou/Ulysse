package org.qualipso.factory.indexing;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.jboss.ejb3.annotation.Depends;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.greeting.GreetingService;

@MessageDriven(mappedName = "indexingeQueue", activationConfig = {
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = "indexingQueue"),
	@ActivationConfigProperty(propertyName = "messagingType", propertyValue = "javax.jms.MessageListener") })
@Depends ("jboss.mq.destination:service=Queue,name=indexingQueue")
@SecurityDomain(value = "JBossWSDigest")


public class IndexingMessageBean implements MessageListener{
	
	@Resource
    private MessageDrivenContext ctx;
	
	private Index index;
	
	public void onMessage(Message msg){
		try{
			if (msg.propertyExists("path")&&msg.propertyExists("action")&&msg.propertyExists("service")){
				FactoryService service = getService(msg.getStringProperty("service"));
				String path = msg.getStringProperty("service");
				if(msg.getStringProperty("action").equals("index"))
					this.index(service, path);
				if(msg.getStringProperty("action").equals("reindex"))
					this.reindex(service, path);
				if(msg.getStringProperty("action").equals("remove"))
					this.remove(service, path);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	
	private void index(FactoryService service, String path) throws IndexingServiceException {
		index = Index.getInstance();
		index.index(toIndexableDocument(service,path));
		
	}

	private void reindex(FactoryService service, String path) throws IndexingServiceException {
		index = Index.getInstance();
		index.reindex(getFactoryResourceIdentifier(service, path), toIndexableDocument(service, path));
		
	}

	private void remove(FactoryService service, String path) throws IndexingServiceException {
		index = Index.getInstance();
		index.remove(getFactoryResourceIdentifier(service, path));	
		
	}
	private FactoryService getService(String service) throws IndexingServiceException{
		FactoryService fs = (FactoryService)ctx.lookup(service);
		if (fs == null)
			throw new IndexingServiceException("Unable to locate Service "+service);
		else
			return fs;
	}
	private FactoryResourceIdentifier getFactoryResourceIdentifier(FactoryService service, String path) throws IndexingServiceException{
		try{
		FactoryResource resource = service.findResource(path);
		FactoryResourceIdentifier fri = resource.getFactoryResourceIdentifier();
		return fri;
		}catch(FactoryException e){
			throw new IndexingServiceException("Unable to locate service "+service);
		}
	}
	private IndexableDocument toIndexableDocument(FactoryService service, String path) throws IndexingServiceException{
		FactoryResourceIdentifier fri =  getFactoryResourceIdentifier(service, path);

		String resourceFRI = fri.toString();
		String resourceService = fri.getService();
		String resourceType  = fri.getType();
		String resourceShortName = fri.getId();
		
		
		//TODO
		//	this implem need to be replace by the following
		
		//real
		//this one mean that toIndexableContent must be in the interfaceFactoryService
		//IndexableContent indexableContent = service.toIndexableContent(path);

		
		try{
		//TODO remove the cast
		IndexableContent indexableContent = ((GreetingService) service).toIndexableContent(path);

		IndexableDocument doc = new IndexableDocument();
		doc.setIndexableContent(indexableContent);
		doc.setResourceService(resourceService);
		doc.setResourceShortName(resourceShortName);
		doc.setResourceType(resourceType);
		doc.setResourceFRI(resourceFRI);
		return doc;

		}catch(Exception e){
			throw new IndexingServiceException("unable to convert resource"+e);
		}
		
	}


}
