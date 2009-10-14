package org.qualipso.factory.voipservice.test.sessionbean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.browser.BrowserService;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.voipservice.VoIPConferenceServiceBean;
import org.qualipso.factory.voipservice.VoIPConferenceService;
import org.qualipso.factory.voipservice.entity.ConferenceDetails;
import org.qualipso.factory.voipservice.entity.ParticipantsInfo;

import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo
 * @date 24/07/2009
 */
public class VoIPConferenceServiceTest extends BaseSessionBeanFixture<VoIPConferenceServiceBean> {
    private static Log log = LogFactory.getLog(VoIPConferenceServiceTest.class);

    @SuppressWarnings("unchecked")
    private static final Class[] usedBeans = { ParticipantsInfo.class, ConferenceDetails.class };
    private Mockery mockery;
    private BindingService binding;
    private MembershipService membership;
    private PEPService pep;
    private PAPService pap;
    private NotificationService notification;
    private BrowserService browser;
    private CoreService core;
    private VoIPConferenceService voipConferenceWS;

    public VoIPConferenceServiceTest() {
	super(VoIPConferenceServiceBean.class, usedBeans);
    }

    public void setUp() throws Exception {
	super.setUp();
	log.debug("injecting mock partners session beans");
	mockery = new Mockery();
	binding = mockery.mock(BindingService.class);
	membership = mockery.mock(MembershipService.class);
	pep = mockery.mock(PEPService.class);
	pap = mockery.mock(PAPService.class);
	notification = mockery.mock(NotificationService.class);
	browser = mockery.mock(BrowserService.class);
	core = mockery.mock(CoreService.class);
	voipConferenceWS = mockery.mock(VoIPConferenceService.class);
	getBeanToTest().setMembershipService(membership);
	getBeanToTest().setNotificationService(notification);
	getBeanToTest().setBindingService(binding);
	getBeanToTest().setPEPService(pep);
	getBeanToTest().setPAPService(pap);
    }

    public void testCRD() {
	log.debug("****************************************************************");
	log.debug("testing CRD VoIP Conference(...)");
	log.debug("****************************************************************");
	final Sequence sequence1 = mockery.sequence("sequence1");
	try {
	    //TODO - update
	    mockery.checking(new Expectations() {{}});
	    VoIPConferenceService service = getBeanToTest();
	    service.getServiceVersion();

	    mockery.assertIsSatisfied();
	} catch (Exception e) {
	    log.error(e.getMessage(), e);
	    fail(e.getMessage());
	}
    }
}
