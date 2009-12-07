package org.qualipso.factory.test.sessionbean;

import java.util.ArrayList;

import javax.ejb.SessionContext;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.indexing.IndexI;
import org.qualipso.factory.indexing.IndexingService;
import org.qualipso.factory.indexing.IndexingServiceBean;
import org.qualipso.factory.indexing.SearchResult;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.security.pep.PEPService;

import com.bm.testsuite.BaseSessionBeanFixture;

public class IndexingServiceTest extends BaseSessionBeanFixture<IndexingServiceBean> {

	private static Log logger = LogFactory.getLog(IndexingServiceTest.class);

	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = {};
	public IndexingServiceTest() {
    	super(IndexingServiceBean.class, usedBeans);
    }
	private Mockery mockery;
	private PEPService pep;
	private MembershipService membership;
	private SessionContext ctx;
	private Queue indexingQueue;
	private QueueConnectionFactory queueConnectionFactory;
	private IndexI index;
	
	
	@BeforeClass
	public void setUp() throws Exception {
		super.setUp();
		logger.debug("injecting mock partners session beans");
		mockery = new Mockery();
		membership = mockery.mock(MembershipService.class);
		pep = mockery.mock(PEPService.class);
		ctx = mockery.mock(SessionContext.class);
		indexingQueue = mockery.mock(Queue.class);
		queueConnectionFactory = mockery.mock(QueueConnectionFactory.class);
		index = mockery.mock(IndexI.class);
		
		
		getBeanToTest().setMembershipService(membership);
		getBeanToTest().setPEPService(pep);
		getBeanToTest().setSessionContext(ctx);
		getBeanToTest().setIndexingQueue(indexingQueue);
		getBeanToTest().setQueueConnectionFactory(queueConnectionFactory);
		getBeanToTest().setIndex(index);
		
		
	}
	@Test
	public void testCRUDIndexing(){
		logger.debug("testing CRUDIndex..");
		 final Sequence sequence1 = mockery.sequence("sequence1");
		 final Sequence sequence2 = mockery.sequence("sequence2");
		 final Sequence sequence3 = mockery.sequence("sequence3");
		 final Sequence sequence4 = mockery.sequence("sequence4");
	     
	     try {
				mockery.checking(new Expectations() {
					{
					oneOf(queueConnectionFactory).createQueueConnection().createQueueSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE).createSender(indexingQueue).send(with(any(Message.class)));
					inSequence(sequence1);
					}
				});
				IndexingService service = getBeanToTest();
				service.index(new String(), new String());
				mockery.assertIsSatisfied();
				
				mockery.checking(new Expectations() {
					{
					oneOf(queueConnectionFactory).createQueueConnection().createQueueSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE).createSender(indexingQueue).send(with(any(Message.class)));
					inSequence(sequence2);
					}
				});
				service.reindex(new String(), new String());
				mockery.assertIsSatisfied();
				
				mockery.checking(new Expectations() {
					{
					oneOf(queueConnectionFactory).createQueueConnection().createQueueSession(true, javax.jms.Session.AUTO_ACKNOWLEDGE).createSender(indexingQueue).send(with(any(Message.class)));
					inSequence(sequence3);
					}
				});
				service.remove(new String(), new String());
				mockery.assertIsSatisfied();
				
				mockery.checking(new Expectations() {
					{
					allowing(index).search("bug AND forge");will(returnValue(new ArrayList<SearchResult>()));inSequence(sequence4);
					allowing(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/kermit"));inSequence(sequence4);
					allowing(pep).checkSecurity(with(any(String.class)), with(any(String.class)), with(any(String.class)));inSequence(sequence4);
					

					}
				});
				service.search("bug AND forge");
				mockery.assertIsSatisfied();
				
				
				
				
	     }catch(Exception e){
	    	 logger.error(e.getMessage(), e);
	          fail(e.getMessage());
	     }
	     
	}
}
