package org.qualipso.factory.indexing;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;

import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.security.pep.PEPServiceException;

import java.util.ArrayList;
import java.util.Iterator;





/**
 * @author Benjamin Dreux (benjiiiiii@gmail.com)
 * @date 25 october 2009
 */
@Stateless(name = "Indexing", mappedName = "IndexingService")
@SecurityDomain(value = "JBossWSDigest")
public class IndexingServiceBean implements IndexingService {
	
	private static Log logger = LogFactory.getLog(IndexingServiceBean.class);
	static String indexingQueuePath = "queue/QualipsoFactory/Indexing";
	
	private PEPService pep;
	private MembershipService membership;
	private SessionContext ctx;
	private Queue indexingQueue;
	private QueueConnectionFactory queueConnectionFactory;
	private Indexer indexer;
	
    
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
	@EJB
	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

	public Indexer getIndexer() {
		return this.indexer;
	}
	
	@Resource(mappedName="jms/QueueConnectionFactory")
	public void setQueueConnectionFactory(QueueConnectionFactory queueConnectionFactory){
		this.queueConnectionFactory = queueConnectionFactory;
	}
	public QueueConnectionFactory getQueueConnectionFatory(){
		return this.queueConnectionFactory;
	}
	
	@Resource(mappedName="jms/queue/QualipsoFactory/indexing")
	public void setIndexingQueue(Queue indexingQueue){
		this.indexingQueue = indexingQueue;
	}
	public Queue getIndexingQueue(){
		return this.indexingQueue;
	}
	
	@Override
	public void index(FactoryResourceIdentifier fri) throws IndexingServiceException {
		logger.debug("index(...) called ");
		logger.debug("params : FactoryResourceIdentifier=\r\n" + fri + "\r\n}");
		String action = "index";
		
        sendMessage(action,fri);
	}
	@Override
	public void reindex(FactoryResourceIdentifier fri) throws IndexingServiceException {
		logger.debug("reindex(...) called ");
		logger.debug("params : FactoryResourceIdentifier=\r\n" + fri + "\r\n}");
		String action = "reindex";
		
        sendMessage(action,fri);

	}

	@Override
	public void remove(FactoryResourceIdentifier fri) throws IndexingServiceException {
		logger.debug("remove(...) called ");
		logger.debug("params : FactoryResourceIdentifier=" + fri);
		String action = "remove";
		
        sendMessage(action,fri);

	}

	@Override
	public ArrayList<SearchResult> search(String query) throws IndexingServiceException {
		logger.debug("search(...) called ");
		logger.debug("params : query=" + query);
		ArrayList<SearchResult> unCheckRes = indexer.search(query);
		return filter(unCheckRes);
	}
	private ArrayList<SearchResult> filter(ArrayList<SearchResult> uncheckedRes) throws IndexingServiceException {
		Iterator<SearchResult> iter = uncheckedRes.iterator();
		ArrayList<SearchResult> checkedRes = new ArrayList<SearchResult>();
		try{	
			String profile = membership.getProfilePathForConnectedIdentifier();
			while(iter.hasNext()){
				SearchResult current = iter.next();
				FactoryResourceIdentifier fri = current.getResourceIdentifier();
				try{
					pep.checkSecurity(profile, fri.toString(), "read");
					checkedRes.add(current);
				}catch(PEPServiceException e){}
			}
		}catch(MembershipServiceException e){
			logger.error("Error in indexingservice when filtring searchResult", e);
            ctx.setRollbackOnly();
            throw new IndexingServiceException("Error in indexingservice when filtring searchResult", e);  
		}
		return checkedRes;
	}

	private void sendMessage(String action, FactoryResourceIdentifier fri) throws IndexingServiceException{
		try {
            QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
            QueueSession queueSession = queueConnection.createQueueSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE);
            QueueSender queueSender = queueSession.createSender(indexingQueue);
            Message message = queueSession.createMessage();
            message.setStringProperty("action", "index");
            message.setStringProperty("uri", fri.toString());
            queueSender.send(message);
            queueSender.close();
            queueSession.close();
            queueConnection.close();
        } catch (Exception e) {
        	logger.error("Error in indexingservice when do " + action, e);
            ctx.setRollbackOnly();
            throw new IndexingServiceException("Error due to indexingService when do " + action, e);   
        }
	}
 
    
}
