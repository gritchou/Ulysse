package org.qualipso.factory.svn.test;

import static org.hamcrest.text.StringContains.containsString;
import static org.qualipso.factory.svn.test.jmock.action.SaveParamsAction.saveParams;
import static org.qualipso.factory.svn.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;

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
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.ssh.SSHService;
import org.qualipso.factory.svn.SVNService;
import org.qualipso.factory.svn.SVNServiceBean;
import org.qualipso.factory.svn.entity.SVNRepository;

import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * @author Gerald Oster (oster@loria.fr)
 * @date 9 October 2009
 */
public class SVNServiceTest extends BaseSessionBeanFixture<SVNServiceBean> {
    
	private static Log logger = LogFactory.getLog(SVNServiceTest.class);
    
	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = { SVNRepository.class, Node.class, Profile.class };
    
	private Mockery mockery;
	private BindingService binding;
	private MembershipService membership;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	private SSHService ssh;
	
    public SVNServiceTest() {
    	super(SVNServiceBean.class, usedBeans);
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
		ssh = mockery.mock(SSHService.class);
		getBeanToTest().setMembershipService(membership);
		getBeanToTest().setNotificationService(notification);
		getBeanToTest().setBindingService(binding);
		getBeanToTest().setPEPService(pep);
		getBeanToTest().setPAPService(pap);
	}
    
    public void testCRUDSVNRepository() {
        logger.debug("testing testCRUDSVNRepository(...)");
        
        final Sequence sequence1 = mockery.sequence("sequence1");
        final Vector<Object> params1 = new Vector<Object>();
        
        try {
			mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/oster")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/oster")), with(equal("/")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/repo1"))); will(saveParams(params1));  inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/oster"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/repo1"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/oster"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("svn.svn-repository.create"))); inSequence(sequence1);
				}
			});
			
			SVNService service = getBeanToTest();
			service.createSVNRepository("/repo1", "My SVN Repository 1", "A super svn repository");
            
            mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/oster")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/oster")), with(equal("/repo1")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/repo1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("svn.svn-repository.read"))); inSequence(sequence1);
				}
			});
        
            assertTrue(service.readSVNRepository("/repo1").getName().equals("My SVN Repository 1"));
            
            mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/oster")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/oster")), with(equal("/repo1")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/repo1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("svn.svn-repository.update"))); inSequence(sequence1);
				}
			});
            
            service.updateSVNRepository("/repo1", "My SVN Repository", "a svn repo");
            
            mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/oster")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/oster")), with(equal("/repo1")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/repo1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("svn.svn-repository.read"))); inSequence(sequence1);
				}
			});
        
            SVNRepository svnr = service.readSVNRepository("/repo1"); 
            assertTrue(svnr.getName().equals("My SVN Repository"));
            assertTrue(svnr.getDescription().equals("a svn repo"));
            
            mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/oster")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/oster")), with(equal("/repo1")), with(equal("delete"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/repo1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).getProperty(with(equal("/repo1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); will(returnValue("aFakePolicyID"));  inSequence(sequence1);
					oneOf(pap).deletePolicy("aFakePolicyID"); inSequence(sequence1);
					oneOf(binding).unbind(with(equal("/repo1"))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("svn.svn-repository.delete"))); inSequence(sequence1);
				}
			});
            
            service.deleteSVNRepository("/repo1");
            
            mockery.assertIsSatisfied();
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }
}
