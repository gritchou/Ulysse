package org.qualipso.factory.git.test;

import static org.hamcrest.text.StringContains.containsString;
import static org.qualipso.factory.git.test.jmock.action.SaveParamsAction.saveParams;
import static org.qualipso.factory.git.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;

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
import org.qualipso.factory.git.GITService;
import org.qualipso.factory.git.GITServiceBean;
import org.qualipso.factory.git.entity.GITRepository;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;

import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 22 September 2009
 */
public class GITServiceTest extends BaseSessionBeanFixture<GITServiceBean> {
    
	private static Log logger = LogFactory.getLog(GITServiceTest.class);
    
	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = { GITRepository.class, Node.class, Profile.class };
    
	private Mockery mockery;
	private BindingService binding;
	private MembershipService membership;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	
    public GITServiceTest() {
    	super(GITServiceBean.class, usedBeans);
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
    
    public void testCRUDGITRepository() {
        logger.debug("testing testCRUDGITREpository(...)");
        
        final Sequence sequence1 = mockery.sequence("sequence1");
        final Vector<Object> params1 = new Vector<Object>();
        
        try {
			mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/repo1"))); will(saveParams(params1));  inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/repo1"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("git.git-repository.create"))); inSequence(sequence1);
				}
			});
			
			GITService service = getBeanToTest();
			service.createGITRepository("/repo1", "My GIT Repository 1", "A super git repository");
            
            mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/repo1")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/repo1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("git.git-repository.read"))); inSequence(sequence1);
				}
			});
        
            assertTrue(service.readGITRepository("/repo1").getName().equals("My GIT Repository 1"));
            
            mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/repo1")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/repo1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("git.git-repository.update"))); inSequence(sequence1);
				}
			});
            
            service.updateGITRepository("/repo1", "My GIT Repository", "a git repo");
            
            mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/repo1")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/repo1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("git.git-repository.read"))); inSequence(sequence1);
				}
			});
        
            GITRepository gr = service.readGITRepository("/repo1"); 
            assertTrue(gr.getName().equals("My GIT Repository"));
            assertTrue(gr.getDescription().equals("a git repo"));
            
            mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/repo1")), with(equal("delete"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/repo1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).getProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); will(returnValue("aFakePolicyID"));  inSequence(sequence1);
					oneOf(pap).deletePolicy("aFakePolicyID"); inSequence(sequence1);
					oneOf(binding).unbind(with(equal("/repo1"))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("git.git-repository.delete"))); inSequence(sequence1);
				}
			});
            
            service.deleteGITRepository("/repo1");
            
            mockery.assertIsSatisfied();
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }
}
