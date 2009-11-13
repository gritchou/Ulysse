package org.qualipso.factory.test.sessionbean;

import static org.hamcrest.text.StringContains.containsString;
import static org.qualipso.factory.test.jmock.action.SaveParamsAction.saveParams;
import static org.qualipso.factory.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;

import java.util.UUID;
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
import org.qualipso.factory.membership.MembershipServiceBean;
import org.qualipso.factory.membership.entity.Group;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.membership.entity.ProfileInfo;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.auth.AuthenticationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;

import com.bm.testsuite.BaseSessionBeanFixture;
import com.bm.testsuite.dataloader.EntityInitialDataSet;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 8 June 2009
 */
public class MembershipServiceTest extends BaseSessionBeanFixture<MembershipServiceBean> {
	
	private static Log logger = LogFactory.getLog(MembershipServiceTest.class);
	
	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = { Profile.class, Group.class };

	private Mockery mockery;
	private BindingService binding;
	private AuthenticationService authentication;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	
	public MembershipServiceTest() {
		super(MembershipServiceBean.class, usedBeans, new ProfileInitialDataSet());
	}

	public void setUp() throws Exception {
		super.setUp();
		logger.debug("injecting mock partners session beans");
		mockery = new Mockery();
		binding = mockery.mock(BindingService.class);
		authentication = mockery.mock(AuthenticationService.class);
		pep = mockery.mock(PEPService.class);
		pap = mockery.mock(PAPService.class);
		notification = mockery.mock(NotificationService.class);
		getBeanToTest().setAuthenticationService(authentication);
		getBeanToTest().setNotificationService(notification);
		getBeanToTest().setBindingService(binding);
		getBeanToTest().setPEPService(pep);
		getBeanToTest().setPAPService(pap);
	}
	
	public void testGetConnectedProfile() {
		logger.debug("testing getConnectedProfile(...)");
		
		final Sequence sequence1 = mockery.sequence("sequence1");
		
		try {
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
				}
			});
			getBeanToTest().getEntityManager().getTransaction().begin();
			assertTrue(getBeanToTest().getProfilePathForConnectedIdentifier().equals("/profiles/guest"));
			assertTrue(getBeanToTest().getProfilePathForConnectedIdentifier().equals("/profiles/jayblanc"));
			getBeanToTest().getEntityManager().getTransaction().commit();
			mockery.assertIsSatisfied();
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	public void testCRUDProfile() {
		logger.debug("testing CRUDProfile(...)");

		final Sequence sequence1 = mockery.sequence("sequence1");
		final Vector<Object> params1 = new Vector<Object>();
		
		try {
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/profiles/jayblanc"))); will(saveParams(params1)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/guest"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.create"))); inSequence(sequence1);
				}
			});
			
			MembershipService service = getBeanToTest(); 
			service.createProfile("jayblanc", "Jérôme Blanchard", "jayblanc@gmail.com", 0);
			
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles/jayblanc")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/jayblanc"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.read"))); inSequence(sequence1);
				}
			});
        
            assertTrue(service.readProfile("/profiles/jayblanc").getEmail().equals("jayblanc@gmail.com"));
            
            mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles/jayblanc")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/jayblanc"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.update"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles/jayblanc")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/jayblanc"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.read"))); inSequence(sequence1);
				}
			});
            
            service.updateProfile("/profiles/jayblanc", "Jerome Blanchard", "jayblanc@free.fr", Profile.BANNED );
            assertTrue(service.readProfile("/profiles/jayblanc").getEmail().equals("jayblanc@free.fr"));
            
            mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles/jayblanc")), with(equal("delete"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/jayblanc"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).unbind(with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.delete"))); inSequence(sequence1);
				}
			});
            
            service.deleteProfile("/profiles/jayblanc");
			
			mockery.assertIsSatisfied();
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	public void testUpdateProfileOnlineStatus() {
		logger.debug("testing updateProfileOnlineStatus(...)");
		
		final Sequence sequence1 = mockery.sequence("sequence1");
		final Vector<Object> params = new Vector<Object>();
		
		try {
			//Creating the profile
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/profiles/jayblanc"))); will(saveParams(params)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/guest"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.create"))); inSequence(sequence1);
				}
			});
			getBeanToTest().getEntityManager().getTransaction().begin();
			getBeanToTest().createProfile("jayblanc", "Jérôme Blanchard", "jayblanc@gmail.com", Profile.INACTIVATED);
			getBeanToTest().getEntityManager().getTransaction().commit();
			mockery.assertIsSatisfied();
			
			
			//Changing the profile online status
			final Node node = new Node();
			node.setId("fakenodeid");
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles/jayblanc")), with(equal("update")));inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/jayblanc"))); will(returnValue(params.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.update"))); inSequence(sequence1);
				}
			});
			getBeanToTest().getEntityManager().getTransaction().begin();
			getBeanToTest().updateProfileOnlineStatus("/profiles/jayblanc", Profile.ONLINE);
			getBeanToTest().getEntityManager().getTransaction().commit();
			mockery.assertIsSatisfied();
			
			
			//Verifying the updated changes.
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles/jayblanc")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/jayblanc"))); will(returnValue(params.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.read"))); inSequence(sequence1);
				}
			});
			getBeanToTest().getEntityManager().getTransaction().begin();
			Profile profile = getBeanToTest().readProfile("/profiles/jayblanc");
			assertEquals(profile.getResourcePath(), "/profiles/jayblanc");
			assertEquals(profile.getFullname(), "Jérôme Blanchard");
			assertEquals(profile.getEmail(), "jayblanc@gmail.com");
			assertEquals(profile.getAccountStatus(), Profile.INACTIVATED);
			assertEquals(profile.getOnlineStatus(), Profile.ONLINE);
			getBeanToTest().getEntityManager().getTransaction().commit();
			mockery.assertIsSatisfied();
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	public void testUpdateProfileLastLoginDate() {
		logger.debug("testing updateProfileLastLoginDate(...)");
		
		final Sequence sequence1 = mockery.sequence("sequence1");
		final Vector<Object> params = new Vector<Object>();
		
		try {
			//Creating the profile
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/profiles/jayblanc"))); will(saveParams(params)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/guest"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.create"))); inSequence(sequence1);
				}
			});
			getBeanToTest().getEntityManager().getTransaction().begin();
			getBeanToTest().createProfile("jayblanc", "Jérôme Blanchard", "jayblanc@gmail.com", Profile.INACTIVATED);
			getBeanToTest().getEntityManager().getTransaction().commit();
			mockery.assertIsSatisfied();
			
			//Updating the profile
			final Node node = new Node();
			node.setId("fakenodeid");
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles/jayblanc")), with(equal("update")));inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/jayblanc"))); will(returnValue(params.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.update"))); inSequence(sequence1);
				}
			});
			getBeanToTest().getEntityManager().getTransaction().begin();
			getBeanToTest().updateProfileLastLoginDate("/profiles/jayblanc");
			getBeanToTest().getEntityManager().getTransaction().commit();
			mockery.assertIsSatisfied();
			
			
			//Verifying the updates changes.
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles/jayblanc")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/jayblanc"))); will(returnValue(params.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.read"))); inSequence(sequence1);
				}
			});
			getBeanToTest().getEntityManager().getTransaction().begin();
			Profile profile = getBeanToTest().readProfile("/profiles/jayblanc");
			assertEquals(profile.getResourcePath(), "/profiles/jayblanc");
			assertEquals(profile.getFullname(), "Jérôme Blanchard");
			assertEquals(profile.getEmail(), "jayblanc@gmail.com");
			assertEquals(profile.getAccountStatus(), Profile.INACTIVATED);
			assertEquals(profile.getOnlineStatus(), Profile.OFFLINE);
			getBeanToTest().getEntityManager().getTransaction().commit();
			mockery.assertIsSatisfied();
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	public void testProfileInfosManagment() {
		logger.debug("testing setProfileInfo(...), getProfileInfo(...), listProfileInfos(...)");
		
		final Sequence sequence1 = mockery.sequence("sequence1");
		final Vector<Object> params = new Vector<Object>();
		
		try {
			//Creating the profile
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/profiles/jayblanc"))); will(saveParams(params)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/guest"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.create"))); inSequence(sequence1);
				}
			});
			getBeanToTest().getEntityManager().getTransaction().begin();
			getBeanToTest().createProfile("jayblanc", "Jérôme Blanchard", "jayblanc@gmail.com", Profile.INACTIVATED);
			getBeanToTest().getEntityManager().getTransaction().commit();
			mockery.assertIsSatisfied();
			
			//Adding infos in the profile
			final Node node = new Node();
			node.setId("fakenodeid");
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/profiles/jayblanc")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/jayblanc"))); will(returnValue(params.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.set-info"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/profiles/jayblanc")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/jayblanc"))); will(returnValue(params.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.set-info"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/profiles/jayblanc")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/jayblanc"))); will(returnValue(params.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.set-info"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/profiles/jayblanc")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/jayblanc"))); will(returnValue(params.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/jayblanc")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.set-info"))); inSequence(sequence1);
				}
			});
			getBeanToTest().getEntityManager().getTransaction().begin();
			getBeanToTest().setProfileInfo("/profiles/jayblanc", "adresse", "615, rue du jardin botanic");
			getBeanToTest().setProfileInfo("/profiles/jayblanc", "zip", "54600");
			getBeanToTest().setProfileInfo("/profiles/jayblanc", "city", "Villers lès Nancy");
			getBeanToTest().setProfileInfo("/profiles/jayblanc", "phone", "+33354958568");
			getBeanToTest().getEntityManager().getTransaction().commit();
			mockery.assertIsSatisfied();
			
			
			//Verifying the infos changes.
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/profiles/jayblanc")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/jayblanc"))); will(returnValue(params.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.get-info"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/profiles/jayblanc")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/jayblanc"))); will(returnValue(params.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.list-info"))); inSequence(sequence1);
				}
			});
			getBeanToTest().getEntityManager().getTransaction().begin();
			ProfileInfo zip = getBeanToTest().getProfileInfo("/profiles/jayblanc", "zip");
			ProfileInfo[] pinfos = getBeanToTest().listProfileInfos("/profiles/jayblanc");
			assertEquals(zip.getValue(), "54600");
			assertEquals(pinfos.length, 4);
			getBeanToTest().getEntityManager().getTransaction().commit();
			mockery.assertIsSatisfied();
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	public void testCRUDGroup() {
		logger.debug("testing CRUDGroup(...)");

		final Sequence sequence1 = mockery.sequence("sequence1");
		final Vector<Object> params1 = new Vector<Object>();
		
		try {
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/group1"))); will(saveParams(params1)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/group1"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.create"))); inSequence(sequence1);
				}
			});
			
			MembershipService service = getBeanToTest(); 
			service.createGroup("/group1", "group1", "The group 1");
			
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/group1")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.read"))); inSequence(sequence1);
				}
			});
        
            assertTrue(service.readGroup("/group1").getName().equals("group1"));
            
            mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/group1")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.update"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/group1")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.read"))); inSequence(sequence1);
				}
			});
            
            service.updateGroup("/group1", "Group1", "The good group 1");
            assertTrue(service.readGroup("/group1").getName().equals("Group1"));
            
            mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/group1")), with(equal("delete"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).unbind(with(equal("/group1"))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.delete"))); inSequence(sequence1);
				}
			});
            
            service.deleteGroup("/group1");
			
			mockery.assertIsSatisfied();
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}	
	
	public void testIsMember() {
		logger.debug("testing isMember(...)");

		final Sequence sequence1 = mockery.sequence("sequence1");
		final Vector<Object> params1 = new Vector<Object>();
		final Vector<Object> params2 = new Vector<Object>();
		final Vector<Object> params3 = new Vector<Object>();
		final Vector<Object> params4 = new Vector<Object>();
		final Vector<Object> params5 = new Vector<Object>();
		final Vector<Object> params6 = new Vector<Object>();
		final Vector<Object> params7 = new Vector<Object>();
		
		try {
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/group1"))); will(saveParams(params1)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/group1"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.create"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/group2"))); will(saveParams(params2)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group2")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group2")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group2")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/group2"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group2")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group2")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.create"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/group3"))); will(saveParams(params3)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group3")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group3")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group3")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/group3"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group3")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group3")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.create"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/group4"))); will(saveParams(params4)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group4")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group4")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group4")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/group4"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group4")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group4")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.create"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/profiles/profile1"))); will(saveParams(params5)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile1")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile1")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/guest"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/profiles/profile1"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile1")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/profile1"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.create"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/profiles/profile2"))); will(saveParams(params6)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile2")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile2")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile2")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/guest"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/profiles/profile2"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile2")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/profile2"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile2")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.create"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/profiles/profile3"))); will(saveParams(params7)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile3")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile3")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile3")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/guest"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/profiles/profile3"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile3")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/profile3"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile3")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.create"))); inSequence(sequence1);
				}
			});
			
			MembershipService service = getBeanToTest(); 
			service.createGroup("/group1", "group1", "The group 1");
			service.createGroup("/group2", "group2", "The group 2");
			service.createGroup("/group3", "group3", "The group 3");
			service.createGroup("/group4", "group4", "The group 4");
			
			service.createProfile("profile1", "Jérôme Blanchard", "jayblanc@gmail.com", 0);
			service.createProfile("profile2", "Jérôme Blanchard", "jayblanc@free.fr", 0);
			service.createProfile("profile3", "Jérôme Blanchard", "jayblanc@hotmail.com", 0);
			
			mockery.assertIsSatisfied();
			
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/group1")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group2"))); will(returnValue(params2.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.add-member"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/group2")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group2"))); will(returnValue(params2.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group3"))); will(returnValue(params3.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group2")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.add-member"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/group3")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group3"))); will(returnValue(params3.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group4"))); will(returnValue(params4.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group3")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.add-member"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/group4")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group4"))); will(returnValue(params4.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group2"))); will(returnValue(params2.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group4")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.add-member"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/group2")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group2"))); will(returnValue(params2.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/profile2"))); will(returnValue(params6.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group2")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.add-member"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/group3")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group3"))); will(returnValue(params3.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/profile3"))); will(returnValue(params7.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group3")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.add-member"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/group4")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group4"))); will(returnValue(params4.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/profile1"))); will(returnValue(params5.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group4")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.add-member"))); inSequence(sequence1);
				}
			});
			
			service.addMemberInGroup("/group1", "/group2");
			service.addMemberInGroup("/group2", "/group3");
			service.addMemberInGroup("/group3", "/group4");
			service.addMemberInGroup("/group4", "/group2");
			service.addMemberInGroup("/group2", "/profiles/profile2");
			service.addMemberInGroup("/group3", "/profiles/profile3");
			service.addMemberInGroup("/group4", "/profiles/profile1");
			
			mockery.assertIsSatisfied();
			
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/group4")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group4"))); will(returnValue(params4.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/profile1"))); will(returnValue(params5.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group4"))); will(returnValue(params4.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.is-member"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/group4")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group4"))); will(returnValue(params4.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group2"))); will(returnValue(params2.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group4"))); will(returnValue(params4.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.is-member"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/group1")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/profile1"))); will(returnValue(params5.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group2"))); will(returnValue(params2.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group3"))); will(returnValue(params3.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group4"))); will(returnValue(params4.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.is-member"))); inSequence(sequence1);
				}
			});
        
            assertTrue(service.isMember("/group4", "/profiles/profile1"));
            assertTrue(service.isMember("/group4", "/group2"));
            assertTrue(service.isMember("/group1", "/profiles/profile1"));
            
            
   			mockery.assertIsSatisfied();
    			
    			
   		} catch (Exception e) {
   			logger.error(e.getMessage(), e);
   			fail(e.getMessage());
   		}
   	}	
	
	public void testListMembers() {
		logger.debug("testing listMembers(...)");

		final Sequence sequence1 = mockery.sequence("sequence1");
		final Vector<Object> params1 = new Vector<Object>();
		final Vector<Object> params2 = new Vector<Object>();
		final Vector<Object> params3 = new Vector<Object>();
		final Vector<Object> params4 = new Vector<Object>();
		final Vector<Object> params5 = new Vector<Object>();
		
		try {
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/group1"))); will(saveParams(params1)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/group1"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.create"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("jayblanc")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal("/")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/group2"))); will(saveParams(params2)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group2")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group2")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group2")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/group2"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group2")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/jayblanc"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group2")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.create"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/profiles/profile1"))); will(saveParams(params3)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile1")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile1")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/guest"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/profiles/profile1"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile1")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/profile1"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile1")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.create"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/profiles/profile2"))); will(saveParams(params4)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile2")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile2")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile2")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/guest"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/profiles/profile2"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile2")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/profile2"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile2")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.create"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/profiles")), with(equal("create"))); inSequence(sequence1);
					oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(equal("/profiles/profile3"))); will(saveParams(params5)); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile3")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile3")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile3")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal("/profiles/guest"))); inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)), with(containsString("/profiles/profile3"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile3")), with(equal(FactoryResourceProperty.OWNER)), with(equal("/profiles/profile3"))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/profiles/profile3")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.profile.create"))); inSequence(sequence1);
				}
			});
			
			MembershipService service = getBeanToTest(); 
			service.createGroup("/group1", "group1", "The group 1");
			service.createGroup("/group2", "group2", "The group 2");
			service.createProfile("profile1", "Jérôme Blanchard", "jayblanc@gmail.com", 0);
			service.createProfile("profile2", "Jérôme Blanchard", "jayblanc@free.fr", 0);
			service.createProfile("profile3", "Jérôme Blanchard", "jayblanc@hotmail.com", 0);
			
			mockery.assertIsSatisfied();
			
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/group1")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group2"))); will(returnValue(params2.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.add-member"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/group1")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/profile1"))); will(returnValue(params3.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.add-member"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/group1")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/profile2"))); will(returnValue(params4.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.add-member"))); inSequence(sequence1);
					
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/group1")), with(equal("update"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/profiles/profile3"))); will(returnValue(params5.get(0))); inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/group1")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.add-member"))); inSequence(sequence1);
				}
			});
			
			service.addMemberInGroup("/group1", "/group2");
			service.addMemberInGroup("/group1", "/profiles/profile1");
			service.addMemberInGroup("/group1", "/profiles/profile2");
			service.addMemberInGroup("/group1", "/profiles/profile3");
			
			mockery.assertIsSatisfied();
			
			mockery.checking(new Expectations() {
				{
					oneOf(authentication).getConnectedIdentifier(); will(returnValue("guest")); inSequence(sequence1);
					oneOf(pep).checkSecurity(with(equal("/profiles/guest")), with(equal("/group1")), with(equal("read"))); inSequence(sequence1);
					oneOf(binding).lookup(with(equal("/group1"))); will(returnValue(params1.get(0))); inSequence(sequence1);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("membership.group.list-members"))); inSequence(sequence1);
				}
			});
        
            String[] members = service.listMembers("/group1");
            assertTrue(members.length == 4);
            for ( String member : members ) {
            	logger.debug(member);
            }
            
   			mockery.assertIsSatisfied();
    			
    			
   		} catch (Exception e) {
   			logger.error(e.getMessage(), e);
   			fail(e.getMessage());
   		}
   	}	
	
	public static class ProfileInitialDataSet extends EntityInitialDataSet<Profile> {

		public ProfileInitialDataSet() {
			super(Profile.class);
		}

		public void create() {
			Profile profile = new Profile();
			profile.setId(UUID.randomUUID().toString());
			profile.setFullname("Toto TOTO");
			profile.setEmail("toto@email.com");
			profile.setAccountStatus(0);
			this.add(profile);
		}
	}

}
