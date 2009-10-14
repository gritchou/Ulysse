package org.qualipso.factory.greeting.test.sessionbean;

import static org.hamcrest.text.StringContains.containsString;
import static org.qualipso.factory.greeting.test.jmock.action.SaveParamsAction.saveParams;
import static org.qualipso.factory.greeting.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.entity.Node;
import org.qualipso.factory.greeting.GreetingService;
import org.qualipso.factory.greeting.GreetingServiceBean;
import org.qualipso.factory.greeting.entity.Name;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;

import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 22 june 2009
 */
public class GreetingServiceTest extends BaseSessionBeanFixture<GreetingServiceBean> {
    
	private static Log logger = LogFactory.getLog(GreetingServiceTest.class);
    
	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = { Name.class, Node.class, Profile.class };
    
	private Mockery mockery;
	private BindingService binding;
	private MembershipService membership;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	
    public GreetingServiceTest() {
    	super(GreetingServiceBean.class, usedBeans);
    }
    
    public void setUp() throws Exception {
		super.setUp();
		logger.debug("injecting mock partners session beans");
		mockery = new Mockery();
		binding = mockery.mock(BindingService.class);
		membership = mockery.mock(MembershipService.class);
		pep = mockery.mock(PEPService.class);
		pap = mockery.mock(PAPService.class);
		notification = mockery.mock(NotificationService.class);
		getBeanToTest().setMembershipService(membership);
		getBeanToTest().setNotificationService(notification);
		getBeanToTest().setBindingService(binding);
		getBeanToTest().setPEPService(pep);
		getBeanToTest().setPAPService(pap);
	}
    
    public void testCRUDName() {
        logger.debug("testing CRUDName(...)");
        
        final Sequence sequence1 = mockery.sequence("sequence1");
        final Vector<Object> params1 = new Vector<Object>();
        final Vector<Object> params2 = new Vector<Object>();
        
        try {
			mockery.checking(new Expectations() {
				{
					//All calls to partners services are mocked and expectations are defined to ensure correct calls.
					//This call sequence expectation define what partners methods are called while creating a Name resource in the Hello service :
					//in this case, we assume that the caller is identified as jayblanc so it's profile path is /profiles/jayblanc
					//note that because of binding mock service, the binding is not really called so we have to store the resource id to be able to get it back we looking up in the binding. 
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/names")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/names/sheldon"))); will(saveParams(params1));  inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/names/sheldon")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/names/sheldon")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/names/sheldon")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/names/sheldon"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/names/sheldon")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/names/sheldon")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("greeting.name.create"))); inSequence(sequence1);
					
					//Second time for second name : 
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/names")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/names/howard"))); will(saveParams(params2)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/names/howard")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/names/howard")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/names/howard")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/names/howard"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/names/howard")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/names/howard")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("greeting.name.create"))); inSequence(sequence1);
				}
			});
			
			GreetingService service = getBeanToTest();
			service.createName("/names/sheldon", "Sheldon Cooper");
			service.createName("/names/howard", "Howard Wolowitz");
            
            mockery.checking(new Expectations() {
				{
					//Reading the first name : 
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/names/sheldon")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/names/sheldon"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("greeting.name.read"))); inSequence(sequence1);
					
					//Reading the second name : 
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/names/howard")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/names/howard"))); will(returnValue(params2.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("greeting.name.read"))); inSequence(sequence1);
				}
			});
        
            assertTrue(service.readName("/names/sheldon").getValue().equals("Sheldon Cooper"));
            assertTrue(service.readName("/names/howard").getValue().equals("Howard Wolowitz"));
            
            mockery.checking(new Expectations() {
				{
					//Update the first name : 
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/names/sheldon")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/names/sheldon"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/names/sheldon")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("greeting.name.update"))); inSequence(sequence1);
					
					//Update the second name : 
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/names/howard")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/names/howard"))); will(returnValue(params2.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/names/howard")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("greeting.name.update"))); inSequence(sequence1);
					
					//Reading the first name : 
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/names/sheldon")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/names/sheldon"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("greeting.name.read"))); inSequence(sequence1);
					
					//Reading the second name : 
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/names/howard")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/names/howard"))); will(returnValue(params2.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("greeting.name.read"))); inSequence(sequence1);
				}
			});
            
            service.updateName("/names/sheldon", "Sheldon Cooper **");
            service.updateName("/names/howard", "Howard Wolowitz *");
            assertTrue(service.readName("/names/sheldon").getValue().equals("Sheldon Cooper **"));
            assertTrue(service.readName("/names/howard").getValue().equals("Howard Wolowitz *"));
            
            mockery.checking(new Expectations() {
				{
					//Delete the first name : 
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/names/sheldon")), with(equal("delete"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/names/sheldon"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).getProperty(with(equal("/names/sheldon")), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); inSequence(sequence1);
					oneOf(pap).deletePolicy(with(any(String.class))); inSequence(sequence1);
					oneOf(binding).unbind(with(equal("/names/sheldon"))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("greeting.name.delete"))); inSequence(sequence1);
					
					//Update the second name : 
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/names/howard")), with(equal("delete"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/names/howard"))); will(returnValue(params2.get(0))); inSequence(sequence1);
					oneOf(binding).getProperty(with(equal("/names/howard")), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); inSequence(sequence1);
					oneOf(pap).deletePolicy(with(any(String.class))); inSequence(sequence1);
					oneOf(binding).unbind(with(equal("/names/howard"))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("greeting.name.delete"))); inSequence(sequence1);
				}
			});
            
            service.deleteName("/names/sheldon");
            service.deleteName("/names/howard");
            
            mockery.assertIsSatisfied();
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }
    
    public void testSayHelloName() {
        logger.debug("testing sayHelloName(...)");
        
        final Vector<Object> params = new Vector<Object>();
        
        try {
			mockery.checking(new Expectations() {
				{
					allowing(pep);
					allowing(pap);
					allowing(notification);
					allowing(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); 
					allowing(binding).bind(with(any(FactoryResourceIdentifier.class)), with(any(String.class))); will(saveParams(params));
					allowing(binding).setProperty(with(any(String.class)), with(any(String.class)), with(any(String.class))); 
				}
			});
			
			GreetingService service = getBeanToTest();
			service.createName("/names/sheldon", "Sheldon Cooper");

			mockery.checking(new Expectations() {
				{
					allowing(pep);
					allowing(pap);
					allowing(notification);
					allowing(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); 
					allowing(binding).lookup(with(any(String.class))); will(returnValue(params.get(0))); 
				}
			});
			
			String message = service.sayHello("/names/sheldon");
			logger.debug("hello message : " + message);
            assertTrue(message.equals("Hello dear Sheldon Cooper !!"));
            
            mockery.assertIsSatisfied();
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }
}
