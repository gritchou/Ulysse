package org.qualipso.factory.indexing;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.Depends;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.FactoryException;
//import org.qualipso.factory.greeting.GreetingService;
import org.qualipso.factory.indexing.IndexingServiceBean;
import org.qualipso.factory.FactoryNamingConvention;

@MessageDriven(mappedName = "indexingeQueue", activationConfig = {
	@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
	@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/indexingQueue"),
	@ActivationConfigProperty(propertyName = "messagingType", propertyValue = "javax.jms.MessageListener") })
@Depends ("jboss.mq.destination:service=Queue,name=indexingQueue")



public class IndexingMessageBean implements MessageListener{

	private static Log logger = LogFactory.getLog(IndexingMessageBean.class);
	@Resource
    private MessageDrivenContext ctx;
	
	private Index index;
	
	public void onMessage(Message msg){
		logger.info("onMessage(...) called");
        logger.debug("params : message=" + msg);
		try{
				FactoryService service = getService(msg.getStringProperty("service"));
				String path = msg.getStringProperty("path");
				if(msg.getStringProperty("action").equals("index"))
					this.index(service, path);
				if(msg.getStringProperty("action").equals("reindex"))
					this.reindex(service, path);
				if(msg.getStringProperty("action").equals("remove"))
					this.remove(service, path);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	
	private void index(FactoryService service, String path) throws IndexingServiceException {
		logger.info("index(...) called");
        logger.debug("params : service=" + service+" path="+path);
		index = Index.getInstance();
		index.index(toIndexableDocument(service,path));
		
	}

	private void reindex(FactoryService service, String path) throws IndexingServiceException {
		logger.info("reindex(...) called");
        logger.debug("params : service=" + service+" path="+path);
		index = Index.getInstance();
		index.reindex(getFactoryResourceIdentifier(service, path), toIndexableDocument(service, path));
		
	}

	private void remove(FactoryService service, String path) throws IndexingServiceException {
		logger.info("remove(...) called");
        logger.debug("params : service=" + service+" path="+path);
		index = Index.getInstance();
		index.remove(getFactoryResourceIdentifier(service, path));	
		
	}
	private FactoryService getService(String service) throws IndexingServiceException{
		try{
		FactoryService fs = (FactoryService)ctx.lookup(FactoryNamingConvention.getJNDINameForService(service));
		return fs;}
		catch(Exception e){
			logger.error("unable to locate service  " + service, e);
			throw new IndexingServiceException("Unable to locate Service "+service);
		}
	}
	private FactoryResourceIdentifier getFactoryResourceIdentifier(FactoryService service, String path) throws IndexingServiceException{
		try{
		FactoryResource resource = service.findResource(path);
		FactoryResourceIdentifier fri = resource.getFactoryResourceIdentifier();
		return fri;
		}catch(FactoryException e){
			logger.error("unable to retrieve resource " + path, e);
			throw new IndexingServiceException("unable to retrieve resource "+path);
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
		IndexableContent indexableContent = new IndexableContent();

		IndexableDocument doc = new IndexableDocument();
		doc.setIndexableContent(indexableContent);
		doc.setResourceService(resourceService);
		doc.setResourceShortName(resourceShortName);
		doc.setResourceType(resourceType);
		doc.setResourceFRI(resourceFRI);
		return doc;

		}catch(Exception e){
			logger.error("unable to convert resource " +path, e);
			throw new IndexingServiceException("unable to convert resource"+e);
		}
		
	}


}
