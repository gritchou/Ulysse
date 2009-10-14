package org.qualipso.factory.helloworld.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.qualipso.factory.helloworld.HelloWorldService;
import org.qualipso.factory.helloworld.HelloWorldServiceBean;
import org.qualipso.factory.membership.MembershipService;

import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 june 2009
 */
public class HelloWorldServiceTest extends BaseSessionBeanFixture<HelloWorldServiceBean> {
    
	private static Log logger = LogFactory.getLog(HelloWorldServiceTest.class);
    
	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = { };
    
	private Mockery mockery;
	private MembershipService membership;
	
    public HelloWorldServiceTest() {
    	super(HelloWorldServiceBean.class, usedBeans);
    }
    
    public void setUp() throws Exception {
		super.setUp();
		logger.debug("injecting mock partners session beans");
		mockery = new Mockery();
		membership = mockery.mock(MembershipService.class);
		getBeanToTest().setMembershipService(membership);
	}
    
    public void testHelloWorld() {
        logger.debug("testing sayHelloWorld()");
        
        try {
			mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/guest"));
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc"));
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/anotheruser"));
				}
			});
        
			HelloWorldService service = getBeanToTest();
            logger.info("message : " + service.sayHelloWorld());
            assertTrue(service.sayHelloWorld().equals("/profiles/jayblanc says : Hello World !!"));
            assertTrue(service.sayHelloWorld().equals("/profiles/anotheruser says : Hello World !!"));
            
            mockery.assertIsSatisfied();
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }
}
