package org.qualipso.factory.indexing;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.eventqueue.EventQueueService;

import org.qualipso.factory.membership.MembershipService;

import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationServiceBean;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;

import java.util.ArrayList;





/**
 * @author Benjamin Dreux (benjiiiiii@gmail.com)
 * @date 22 october 2009
 */
@Stateless(name = "Indexing", mappedName = "IndexingService")
@SecurityDomain(value = "JBossWSDigest")
public class IndexingServiceBean implements IndexingService {
	
	private static Log logger = LogFactory.getLog(NotificationServiceBean.class);
	static String indexingQueue;
	private BindingService binding;
	private PEPService pep;
	private PAPService pap;
	private MembershipService membership;
	private SessionContext ctx;
	private EventQueueService eventQueue;
	
    
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
	public void setBindingService(BindingService binding) {
		this.binding = binding;
	}

	public BindingService getBindingService() {
		return this.binding;
	}

	@EJB
	public void setPEPService(PEPService pep) {
		this.pep = pep;
	}

	public PEPService getPEPService() {
		return this.pep;
	}

	@EJB
	public void setPAPService(PAPService pap) {
		this.pap = pap;
	}

	public PAPService getPAPService() {
		return this.pap;
	}
	@EJB
	public void setMembershipService(MembershipService membership) {
		this.membership = membership;
	}

	public MembershipService getMembershipService() {
		return this.membership;
	}
	@EJB
	public void setEventQueueService(EventQueueService eventQueue) {
		this.eventQueue = eventQueue;
	}

	public EventQueueService getEventQueueService() {
		return this.eventQueue;
	}

	@Override
	public void index(FactoryResourceIdentifier fri) throws IndexingServiceException {

//		logger.warn("index(...) called ### NOT IMPLEMENTED");
		logger.debug("params : FactoryResourceIdentifier=\r\n" + fri + "\r\n}");

		try{
		Event e = new Event();
		e.setThrowedBy("indeingService");
		e.setEventType("index");
		e.setArgs(fri.toString());
		
		eventQueue.pushEvent(getIndexingQueue(), e);
		
		}catch(Exception e){
			logger.error("unable create indexing queue", e);
            ctx.setRollbackOnly();
            throw new IndexingServiceException("unable create indexing queue", e);
		}
	}

	@Override
	public void reindex(FactoryResourceIdentifier fri) throws IndexingServiceException {
		//TODO
		logger.warn("reindex(...) called ### NOT IMPLEMENTED");
		logger.debug("params : FactoryResourceIdentifier=\r\n" + fri + "\r\n}");
	}

	@Override
	public void remove(FactoryResourceIdentifier fri) throws IndexingServiceException {
		//TODO
		logger.warn("remove(...) called ### NOT IMPLEMENTED");
		logger.debug("params : FactoryResourceIdentifier=" + fri);
	}

	@Override
	public ArrayList<SearchResult> search(String query) throws IndexingServiceException {
		//TODO
		logger.warn("search(...) called ### NOT IMPLEMENTED");
		logger.debug("params : query=" + query);
		return null;
	}
    public String getIndexingQueue() throws IndexingServiceException {
        if (indexingQueue == null) {
        	try {
        		eventQueue.createEventQueue("indexing");
        		indexingQueue = "indexing";
        		} catch (Exception e) {
        			logger.error("unable create indexing queue", e);
                    ctx.setRollbackOnly();
                    throw new IndexingServiceException("unable create indexing queue", e);
				
			}
        }
        return indexingQueue;
    }
 
    
}
