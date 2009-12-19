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
import java.util.Iterator;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.security.pep.PEPService;

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

    private PEPService pep;
    private MembershipService membership;
    private SessionContext ctx;
    private IndexI index;

    @Resource(mappedName = "ConnectionFactory")
    private QueueConnectionFactory queueConnectionFactory;

    @Resource(mappedName = "queue/indexingQueue")
    private Queue indexingQueue;

    public IndexingServiceBean() {
    }

    @Resource
    public void setSessionContext(SessionContext ctx) {
        this.ctx = ctx;
    }

    public SessionContext getSessionContext() {
        return this.ctx;
    }

    @EJB
    public void setPEPService(PEPService pep) {
        this.pep = pep;
    }

    public PEPService getPEPService() {
        return this.pep;
    }

    @EJB
    public void setMembershipService(MembershipService membership) {
        this.membership = membership;
    }

    public MembershipService getMembershipService() {
        return this.membership;
    }

    public void setIndex(IndexI index) {
        this.index = index;
    }

    public IndexI getIndex() {
        return this.index;
    }

    public void setQueueConnectionFactory(QueueConnectionFactory queueConnectionFactory) {
        this.queueConnectionFactory = queueConnectionFactory;
    }

    public QueueConnectionFactory getQueueConnectionFatory() {
        return this.queueConnectionFactory;
    }

    public void setIndexingQueue(Queue indexingQueue) {
        this.indexingQueue = indexingQueue;
    }

    public Queue getIndexingQueue() {
        return this.indexingQueue;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void index(String fs, String path) throws IndexingServiceException {
        logger.info("index(...) called ");
        logger.debug("params : FactoryService=\r\n" + fs + "\r\n}");
        logger.debug("params : Path=\r\n" + path + "\r\n}");
        String action = "index";

        sendMessage(action, fs, path);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void reindex(String fs, String path) throws IndexingServiceException {
        logger.info("reindex(...) called ");
        logger.debug("params : FactoryService=\r\n" + fs + "\r\n}");
        logger.debug("params : Path=\r\n" + path + "\r\n}");
        String action = "reindex";

        sendMessage(action, fs, path);

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remove(String fs, String path) throws IndexingServiceException {
        logger.info("remove(...) called ");
        logger.debug("params : FactoryService=\r\n" + fs + "\r\n}");
        logger.debug("params : Path=\r\n" + path + "\r\n}");
        String action = "remove";

        sendMessage(action, fs, path);

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public ArrayList<SearchResult> search(String query) throws IndexingServiceException {
        logger.info("search(...) called ");
        logger.debug("params : query=" + query);
        index = Index.getInstance();
        ArrayList<SearchResult> unCheckRes = index.search(query);
        return filter(unCheckRes);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    private ArrayList<SearchResult> filter(ArrayList<SearchResult> uncheckedRes) throws IndexingServiceException {
        logger.info("filter(...) called ");
        logger.debug("params : UnchedSearchResult= " + uncheckedRes);
        Iterator<SearchResult> iter = uncheckedRes.iterator();
        ArrayList<SearchResult> checkedRes = new ArrayList<SearchResult>();
        try {
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
        } catch (Exception e) {
            logger.error("unable to filter result of search", e);
            ctx.setRollbackOnly();
            throw new IndexingServiceException("Error in indexingservice when filtring searchResult", e);
        }
        return checkedRes;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void sendMessage(String action, String fs, String path) throws IndexingServiceException {
        logger.info("sendMessage(...) called ");
        logger.debug("params : action= " + action + " Service=" + fs + " path=" + path);
        try {
            QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
            try {
                QueueSession queueSession = queueConnection.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
                try {
                    QueueSender queueSender = queueSession.createSender(indexingQueue);
                    try {

                        Message message = queueSession.createMessage();
                        message.setStringProperty("action", action);
                        message.setStringProperty("service", fs);
                        message.setStringProperty("path", path);
                        queueConnection.start();
                        queueSender.send(message);
                        queueSender.close();
                        queueSession.close();
                        queueConnection.close();
                    } finally {
                        queueSender.close();
                    }
                } finally {
                    queueSession.close();
                }
            } finally {
                queueConnection.close();
            }
        } catch (JMSException e) {
            logger.error("Unable to send message " + action, e);
            ctx.setRollbackOnly();
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
