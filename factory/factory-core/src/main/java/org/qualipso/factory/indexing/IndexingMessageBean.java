package org.qualipso.factory.indexing;
/*
 *
 * Qualipso Factory
 * Copyright (C) 2006-2010 INRIA
 * http://www.inria.fr - molli@loria.fr
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of LGPL. See licenses details in LGPL.txt
 *
 * Initial authors :
 *
 * Jérôme Blanchard / INRIA
 * Pascal Molli / Nancy Université
 * Gérald Oster / Nancy Université
 *
 */
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
import org.qualipso.factory.FactoryNamingConvention;

/**
  * @date 2 dec 2009
  * @author Benjamin Dreux(benjiiiiii@gmail.com)
  */


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
		index.reindex(path, toIndexableDocument(service, path));
		
	}

	private void remove(FactoryService service, String path) throws IndexingServiceException {
		logger.info("remove(...) called");
        logger.debug("params : service=" + service+" path="+path);
		index = Index.getInstance();
		index.remove(path);	
		
	}
	private FactoryService getService(String service) throws IndexingServiceException{
		try{
		FactoryService fs = (FactoryService)ctx.lookup(FactoryNamingConvention.getJNDINameForService(service));
		return fs;
        }catch(Exception e){
			logger.error("unable to locate service  " + service, e);
			throw new IndexingServiceException("Unable to locate Service "+service);
		}
	}
	private IndexableDocument toIndexableDocument(FactoryService service, String path) throws IndexingServiceException{
		try{

        IndexableService is = (IndexableService) service ;
		IndexableDocument doc = is.getIndexableDocument(path);

		return doc;

		}catch(Exception e){
			logger.error("unable to convert resource " +path, e);
			throw new IndexingServiceException("unable to convert resource"+e);
		}
		
	}


}
