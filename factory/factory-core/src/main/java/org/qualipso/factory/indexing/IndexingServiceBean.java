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
 * Christophe Bouthier / INRIA
 * 
 */
package org.qualipso.factory.indexing;

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.indexing.base.IndexBase;
import org.qualipso.factory.indexing.base.LuceneIndexBase;

/**
 * <p>
 * Class which implements Indexing Service
 * </p>
 * 
 * @author Benjamin Dreux (benjiiiiii@gmail.com)
 * @author Cynthia FLORENTIN
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 25 october 2009
 */

@Stateless(name = IndexingService.SERVICE_NAME, mappedName = FactoryNamingConvention.SERVICE_PREFIX + IndexingService.SERVICE_NAME)
@SecurityDomain(value = "JBossWSDigest")
public class IndexingServiceBean implements IndexingService {

	private static Log logger = LogFactory.getLog(IndexingServiceBean.class);

	private SessionContext ctx;
	private IndexBase index;
	private ConnectionFactory connectionFactory;
	private Topic topic;

	public IndexingServiceBean() {
	}

	@Resource
	public void setSessionContext(SessionContext ctx) {
		this.ctx = ctx;
	}

	public SessionContext getSessionContext() {
		return this.ctx;
	}

	public void setIndex(IndexBase index) {
		this.index = index;
	}

	public IndexBase getIndex() {
		return this.index;
	}

	@Resource(mappedName = "ConnectionFactory")
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public ConnectionFactory getConnectionFatory() {
		return this.connectionFactory;
	}

	@Resource(mappedName = "topics/factoryIndexing")
	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public Topic getTopic() {
		return this.topic;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void index(String path) throws IndexingServiceException {
		logger.info("index(...) called ");
		logger.debug("params : Path=\r\n" + path + "\r\n}");
		String action = "index";

		sendMessage(action, path);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void reindex(String path) throws IndexingServiceException {
		logger.info("reindex(...) called ");
		logger.debug("params : Path=\r\n" + path + "\r\n}");
		String action = "reindex";

		sendMessage(action, path);

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remove(String path) throws IndexingServiceException {
		logger.info("remove(...) called ");
		logger.debug("params : Path=\r\n" + path + "\r\n}");
		String action = "remove";

		sendMessage(action, path);

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ArrayList<SearchResult> search(String query) throws IndexingServiceException {
		logger.info("search(...) called ");
		logger.debug("params : query=" + query);
		index = LuceneIndexBase.getInstance();
		ArrayList<SearchResult> unCheckRes = index.search(query);
		return unCheckRes;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void sendMessage(String action, String path) throws IndexingServiceException {
		logger.info("sendMessage(...) called ");
		logger.debug("params : action= " + action + " path=" + path);
		try {
			Connection connection = connectionFactory.createConnection();
			Session session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(topic);
			Message message = session.createMessage();
			message.setStringProperty("action", action);
			message.setStringProperty("path", path);
			connection.start();
			producer.send(message);
			producer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			logger.error("Unable to send message " + action, e);
			throw new IndexingServiceException("Unable to send message" + action, e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public FactoryResource findResource(String path) throws FactoryException {
		logger.info("findResource(...) called");
		logger.debug("params : path=" + path);

		throw new IndexingServiceException("Indexing service does not manage any resource");
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
