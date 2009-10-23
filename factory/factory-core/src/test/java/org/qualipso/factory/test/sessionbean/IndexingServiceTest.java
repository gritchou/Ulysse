package org.qualipso.factory.test.sessionbean;
import static org.hamcrest.text.StringContains.containsString;
import static org.qualipso.factory.test.jmock.action.SaveParamsAction.saveParams;
import static org.qualipso.factory.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.FactoryResourceIdentifier;

import org.qualipso.factory.eventqueue.EventQueueService;
import org.qualipso.factory.indexing.IndexingService;
import org.qualipso.factory.indexing.IndexingServiceBean;
import org.qualipso.factory.membership.MembershipService;


import org.qualipso.factory.notification.Event;

import org.qualipso.factory.security.pep.PEPService;

import com.bm.testsuite.BaseSessionBeanFixture;

public class IndexingServiceTest extends BaseSessionBeanFixture<IndexingServiceBean> {

	private static Log logger = LogFactory.getLog(IndexingServiceTest.class);

	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = {Event.class};
	public IndexingServiceTest() {
    	super(IndexingServiceBean.class, usedBeans);
    }
	private Mockery mockery;
	private MembershipService membership;
	private PEPService pep;
	
	
	
	@BeforeClass
	public void setUp() throws Exception {
		super.setUp();
		logger.debug("injecting mock partners session beans");
		mockery = new Mockery();
		membership = mockery.mock(MembershipService.class);
		pep = mockery.mock(PEPService.class);
		
		
		
		getBeanToTest().setMembershipService(membership);
		getBeanToTest().setPEPService(pep);
		
	}
	@Test
	public void testCRUDIndexing(){
		logger.debug("testing CRUDIndex..");
		 final Sequence sequence1 = mockery.sequence("sequence1");
	     
	     try {
				mockery.checking(new Expectations() {
					{
						
						//queueSender.send(message);
						
					}
				});
				IndexingService service = getBeanToTest();
				service.index(new FactoryResourceIdentifier());
				
	     }catch(Exception e){
	    	 logger.error(e.getMessage(), e);
	          fail(e.getMessage());
	     }
	     
	}
}
