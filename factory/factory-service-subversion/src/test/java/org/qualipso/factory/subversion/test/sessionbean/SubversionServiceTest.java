package org.qualipso.factory.subversion.test.sessionbean;

import static org.hamcrest.text.StringContains.containsString;
import static org.qualipso.factory.test.jmock.action.SaveParamsAction.saveParams;
import static org.qualipso.factory.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.subversion.SubversionService;
import org.qualipso.factory.subversion.SubversionServiceBean;
import org.qualipso.factory.subversion.entity.Repository;
import org.qualipso.factory.subversion.entity.SVNLogEntry;

import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * @author Emmanuel Rias (emmanuel.rias@bull.net)
 * @date 22 june 2009
 */
public class SubversionServiceTest extends
		BaseSessionBeanFixture<SubversionServiceBean> {

	private static Log logger = LogFactory.getLog(SubversionServiceTest.class);

	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = { org.qualipso.factory.subversion.entity.Repository.class };

	private Mockery mockery;
	private BindingService binding;
	private MembershipService membership;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;

	public SubversionServiceTest() {
		super(SubversionServiceBean.class, usedBeans);
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

	public void testFindRepositoryByName() {
		logger.debug("testing findProjectByName(...)");

		final Vector<Object> params = new Vector<Object>();
		final Sequence sequence1 = mockery.sequence("sequence1");

		try {
			mockery.checking(new Expectations() {
				{
					allowing(pep);
					allowing(pap);
					allowing(notification);
					allowing(binding);
					allowing(membership).getProfilePathForConnectedIdentifier();
					will(returnValue("/profiles/riase"));
					oneOf(binding).bind(
							with(any(FactoryResourceIdentifier.class)),
							with(equal("/subversion")));
					will(saveParams(params));
					inSequence(sequence1);
					oneOf(binding)
							.setProperty(
									with(equal("/subversion")),
									with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)),
									with(any(String.class)));
					inSequence(sequence1);
					oneOf(binding)
							.setProperty(
									with(equal("/subversion")),
									with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)),
									with(any(String.class)));
					inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/subversion")),
							with(equal(FactoryResourceProperty.AUTHOR)),
							with(equal("/profiles/riase")));
					inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)),
							with(containsString("/subversion")));
					inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/subversion")),
							with(equal(FactoryResourceProperty.OWNER)),
							with(equal("/profiles/riase")));
					inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/subversion")),
							with(equal(FactoryResourceProperty.POLICY_ID)),
							with(any(String.class)));
					inSequence(sequence1);
					oneOf(notification).throwEvent(
							with(anEventWithTypeEqualsTo("hello.name.create")));
					inSequence(sequence1);
					allowing(binding).bind(
							with(any(FactoryResourceIdentifier.class)),
							with(any(String.class)));
					will(saveParams(params));
					allowing(binding).setProperty(with(any(String.class)),
							with(any(String.class)), with(any(String.class)));
				}
			});

			SubversionService service = getBeanToTest();
			Repository repository = service.findRepositoryByName("/subversion",
					"Repo1");

			mockery.checking(new Expectations() {
				{
					allowing(pep);
					allowing(pap);
					allowing(notification);
					allowing(membership).getProfilePathForConnectedIdentifier();
					will(returnValue("/profiles/riase"));
					// allowing(binding).lookup(with(any(String.class)));
					// will(returnValue(params.get(0)));
				}
			});

			assertEquals("Repo1", repository.getName());
			// mockery.assertIsSatisfied();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	public void testGetCommitsInfos() {
		logger.debug("testing getCommitsInfos(...)");

		final Vector<Object> params = new Vector<Object>();
		final Sequence sequence1 = mockery.sequence("sequence1");

		try {
			mockery.checking(new Expectations() {
				{
					allowing(pep);
					allowing(pap);
					allowing(notification);
					allowing(binding);
					allowing(membership).getProfilePathForConnectedIdentifier();
					will(returnValue("/profiles/riase"));
					oneOf(binding).bind(
							with(any(FactoryResourceIdentifier.class)),
							with(equal("/subversion")));
					will(saveParams(params));
					inSequence(sequence1);
					oneOf(binding)
							.setProperty(
									with(equal("/subversion")),
									with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)),
									with(any(String.class)));
					inSequence(sequence1);
					oneOf(binding)
							.setProperty(
									with(equal("/subversion")),
									with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)),
									with(any(String.class)));
					inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/subversion")),
							with(equal(FactoryResourceProperty.AUTHOR)),
							with(equal("/profiles/riase")));
					inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)),
							with(containsString("/subversion")));
					inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/subversion")),
							with(equal(FactoryResourceProperty.OWNER)),
							with(equal("/profiles/riase")));
					inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/subversion")),
							with(equal(FactoryResourceProperty.POLICY_ID)),
							with(any(String.class)));
					inSequence(sequence1);
					oneOf(notification).throwEvent(
							with(anEventWithTypeEqualsTo("hello.name.create")));
					inSequence(sequence1);
					allowing(binding).bind(
							with(any(FactoryResourceIdentifier.class)),
							with(any(String.class)));
					will(saveParams(params));
					allowing(binding).setProperty(with(any(String.class)),
							with(any(String.class)), with(any(String.class)));
				}
			});

			SubversionService service = getBeanToTest();
			SVNLogEntry[] svnLogEntries = service.getCommitsInfo("/subversion",
					new Repository("Repo1"));
			mockery.checking(new Expectations() {
				{
					allowing(pep);
					allowing(pap);
					allowing(notification);
					allowing(membership).getProfilePathForConnectedIdentifier();
					will(returnValue("/profiles/riase"));
					// allowing(binding).lookup(with(any(String.class)));
					// will(returnValue(params.get(0)));
				}
			});

			// mockery.assertIsSatisfied();

		} catch (Exception e) {
			logger.error(e.getCause().getMessage(), e);
			fail(e.getCause().getMessage());
		}
	}

	public void testCreateRepository() {
		logger.debug("testing createRepository(...)");

		final Vector<Object> params = new Vector<Object>();
		final Sequence sequence1 = mockery.sequence("sequence1");

		try {
			mockery.checking(new Expectations() {
				{
					allowing(pep);
					allowing(pap);
					allowing(notification);
					allowing(binding);
					allowing(membership).getProfilePathForConnectedIdentifier();
					will(returnValue("/profiles/riase"));
					oneOf(binding).bind(
							with(any(FactoryResourceIdentifier.class)),
							with(equal("/subversion")));
					will(saveParams(params));
					inSequence(sequence1);
					oneOf(binding)
							.setProperty(
									with(equal("/subversion")),
									with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)),
									with(any(String.class)));
					inSequence(sequence1);
					oneOf(binding)
							.setProperty(
									with(equal("/subversion")),
									with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)),
									with(any(String.class)));
					inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/subversion")),
							with(equal(FactoryResourceProperty.AUTHOR)),
							with(equal("/profiles/riase")));
					inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)),
							with(containsString("/subversion")));
					inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/subversion")),
							with(equal(FactoryResourceProperty.OWNER)),
							with(equal("/profiles/riase")));
					inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/subversion")),
							with(equal(FactoryResourceProperty.POLICY_ID)),
							with(any(String.class)));
					inSequence(sequence1);
					oneOf(notification).throwEvent(
							with(anEventWithTypeEqualsTo("hello.name.create")));
					inSequence(sequence1);
					allowing(binding).bind(
							with(any(FactoryResourceIdentifier.class)),
							with(any(String.class)));
					will(saveParams(params));
					allowing(binding).setProperty(with(any(String.class)),
							with(any(String.class)), with(any(String.class)));
				}
			});

			SubversionService service = getBeanToTest();
			service.createRepository("/subversion",
					"Repo1");

			mockery.checking(new Expectations() {
				{
					allowing(pep);
					allowing(pap);
					allowing(notification);
					allowing(membership).getProfilePathForConnectedIdentifier();
					will(returnValue("/profiles/riase"));
					// allowing(binding).lookup(with(any(String.class)));
					// will(returnValue(params.get(0)));
				}
			});

			// mockery.assertIsSatisfied();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (e.getCause().getMessage().equals(" the SVN project 'Repo1' already exists.")) {
				assertEquals(true, true);
			} else {
				fail(e.getMessage());
			}
		}
	}

//	public void testDeleteRepository() {
//		logger.debug("testing deleteRepository(...)");
//
//		final Vector<Object> params = new Vector<Object>();
//		final Sequence sequence1 = mockery.sequence("sequence1");
//
//		try {
//			mockery.checking(new Expectations() {
//				{
//					allowing(pep);
//					allowing(pap);
//					allowing(notification);
//					allowing(binding);
//					allowing(membership).getProfilePathForConnectedIdentifier();
//					will(returnValue("/profiles/riase"));
//					oneOf(binding).bind(
//							with(any(FactoryResourceIdentifier.class)),
//							with(equal("/subversion")));
//					will(saveParams(params));
//					inSequence(sequence1);
//					oneOf(binding)
//							.setProperty(
//									with(equal("/subversion")),
//									with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)),
//									with(any(String.class)));
//					inSequence(sequence1);
//					oneOf(binding)
//							.setProperty(
//									with(equal("/subversion")),
//									with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)),
//									with(any(String.class)));
//					inSequence(sequence1);
//					oneOf(binding).setProperty(with(equal("/subversion")),
//							with(equal(FactoryResourceProperty.AUTHOR)),
//							with(equal("/profiles/riase")));
//					inSequence(sequence1);
//					oneOf(pap).createPolicy(with(any(String.class)),
//							with(containsString("/subversion")));
//					inSequence(sequence1);
//					oneOf(binding).setProperty(with(equal("/subversion")),
//							with(equal(FactoryResourceProperty.OWNER)),
//							with(equal("/profiles/riase")));
//					inSequence(sequence1);
//					oneOf(binding).setProperty(with(equal("/subversion")),
//							with(equal(FactoryResourceProperty.POLICY_ID)),
//							with(any(String.class)));
//					inSequence(sequence1);
//					oneOf(notification).throwEvent(
//							with(anEventWithTypeEqualsTo("hello.name.create")));
//					inSequence(sequence1);
//					allowing(binding).bind(
//							with(any(FactoryResourceIdentifier.class)),
//							with(any(String.class)));
//					will(saveParams(params));
//					allowing(binding).setProperty(with(any(String.class)),
//							with(any(String.class)), with(any(String.class)));
//				}
//			});
//
//			SubversionService service = getBeanToTest();
//			service.deleteRepository("/subversion",
//					"Repo1");
//
//			mockery.checking(new Expectations() {
//				{
//					allowing(pep);
//					allowing(pap);
//					allowing(notification);
//					allowing(membership).getProfilePathForConnectedIdentifier();
//					will(returnValue("/profiles/riase"));
//					// allowing(binding).lookup(with(any(String.class)));
//					// will(returnValue(params.get(0)));
//				}
//			});
//
//			// mockery.assertIsSatisfied();
//
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			fail(e.getMessage());
//		}
//	}


	public void testSetUserRole() {
		logger.debug("testing setUserRole(...)");

		final Vector<Object> params = new Vector<Object>();
		final Sequence sequence1 = mockery.sequence("sequence1");

		try {
			mockery.checking(new Expectations() {
				{
					allowing(pep);
					allowing(pap);
					allowing(notification);
					allowing(binding);
					allowing(membership).getProfilePathForConnectedIdentifier();
					will(returnValue("/profiles/riase"));
					oneOf(binding).bind(
							with(any(FactoryResourceIdentifier.class)),
							with(equal("/subversion")));
					will(saveParams(params));
					inSequence(sequence1);
					oneOf(binding)
							.setProperty(
									with(equal("/subversion")),
									with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)),
									with(any(String.class)));
					inSequence(sequence1);
					oneOf(binding)
							.setProperty(
									with(equal("/subversion")),
									with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)),
									with(any(String.class)));
					inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/subversion")),
							with(equal(FactoryResourceProperty.AUTHOR)),
							with(equal("/profiles/riase")));
					inSequence(sequence1);
					oneOf(pap).createPolicy(with(any(String.class)),
							with(containsString("/subversion")));
					inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/subversion")),
							with(equal(FactoryResourceProperty.OWNER)),
							with(equal("/profiles/riase")));
					inSequence(sequence1);
					oneOf(binding).setProperty(with(equal("/subversion")),
							with(equal(FactoryResourceProperty.POLICY_ID)),
							with(any(String.class)));
					inSequence(sequence1);
					oneOf(notification).throwEvent(
							with(anEventWithTypeEqualsTo("hello.name.create")));
					inSequence(sequence1);
					allowing(binding).bind(
							with(any(FactoryResourceIdentifier.class)),
							with(any(String.class)));
					will(saveParams(params));
					allowing(binding).setProperty(with(any(String.class)),
							with(any(String.class)), with(any(String.class)));
				}
			});

			SubversionService service = getBeanToTest();
			service.setUserRole("/subversion", "Repo1", "administrator", "anonymous");

			mockery.checking(new Expectations() {
				{
					allowing(pep);
					allowing(pap);
					allowing(notification);
					allowing(membership).getProfilePathForConnectedIdentifier();
					will(returnValue("/profiles/riase"));
					// allowing(binding).lookup(with(any(String.class)));
					// will(returnValue(params.get(0)));
				}
			});

			// mockery.assertIsSatisfied();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
}