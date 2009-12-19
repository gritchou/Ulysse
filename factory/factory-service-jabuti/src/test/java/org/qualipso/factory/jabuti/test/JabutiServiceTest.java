package org.qualipso.factory.jabuti.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.qualipso.factory.jabuti.JabutiService;
import org.qualipso.factory.jabuti.JabutiServiceBean;
import org.qualipso.factory.membership.MembershipService;

import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * @author 
 * @date 
 */
public class JabutiServiceTest extends BaseSessionBeanFixture<JabutiServiceBean> {
    
	private static Log logger = LogFactory.getLog(JabutiServiceTest.class);
    
	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = { };
    
	private Mockery mockery;
	private MembershipService membership;
	
    public JabutiServiceTest() {
    	super(JabutiServiceBean.class, usedBeans);
    }
    
    public void setUp() throws Exception {
		super.setUp();
		logger.debug("injecting mock partners session beans");
		mockery = new Mockery();
		membership = mockery.mock(MembershipService.class);
//		getBeanToTest().setMembershipService(membership);
	}
    
    public void testHelloWorld() {
//        logger.debug("testing sayHelloWorld()");
//        
//        try {
//			mockery.checking(new Expectations() {
//				{
//					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/guest"));
//					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc"));
//					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/anotheruser"));
//				}
//			});
//        
//			JabutiService service = getBeanToTest();
//            logger.info("message : " + service.sayJabuti());
//            assertTrue(service.sayJabuti().equals("/profiles/jayblanc says : Hello World !!"));
//            assertTrue(service.sayJabuti().equals("/profiles/anotheruser says : Hello World !!"));
//            
//            mockery.assertIsSatisfied();
//            
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            fail(e.getMessage());
//        }
    }
}
