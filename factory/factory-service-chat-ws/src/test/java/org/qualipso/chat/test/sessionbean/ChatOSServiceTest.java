package org.qualipso.chat.test.sessionbean;

import static org.hamcrest.text.StringContains.containsString;
import static org.qualipso.chat.test.jmock.action.SaveParamsAction.saveParams;
import static org.qualipso.chat.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;

import java.util.HashMap;
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
import org.qualipso.factory.browser.BrowserService;
import org.qualipso.factory.collaboration.beans.ThreadMessageDetails;
import org.qualipso.factory.collaboration.document.DocumentService;
import org.qualipso.factory.collaboration.forum.ForumService;
import org.qualipso.factory.collaboration.forum.ForumServiceBean;
import org.qualipso.factory.collaboration.forum.entity.Forum;
import org.qualipso.factory.collaboration.forum.entity.ThreadMessage;
import org.qualipso.factory.collaboration.utils.CollaborationUtils;
import org.qualipso.factory.collaboration.ws.ForumWService;
import org.qualipso.factory.collaboration.ws.beans.ForumDTO;
import org.qualipso.factory.collaboration.ws.beans.MessageDTO;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;

import com.bm.testsuite.BaseSessionBeanFixture;

public class ChatOSServiceTest extends BaseSessionBeanFixture<ChatOSServiceBean>
{
    
    /** The logger. */
    private static Log logger = LogFactory.getLog(ChatOSServiceTest.class);
    
    /** The mockery. */
    private Mockery mockery;
    
    /** The binding. */
    private BindingService binding;
    
    /** The membership. */
    private MembershipService membership;
    
    /** The pep. */
    private PEPService pep;
    
    /** The pap. */
    private PAPService pap;
    
    /** The notification. */
    private NotificationService notification;
    
    /** The browser. */
    private BrowserService browser;
    
    /** The core. */
    private CoreService core;    
    
    private static ChatOSService service;

    public ChatOSServiceTest()
    {
	super(ChatOSServiceBean.class, usedBeans);
    }

    public void setUp() throws Exception
    {
	super.setUp();
	logger.debug("injecting mock partners session beans");
	mockery = new Mockery();
	binding = mockery.mock(BindingService.class);
	membership = mockery.mock(MembershipService.class);
	pep = mockery.mock(PEPService.class);
	pap = mockery.mock(PAPService.class);
	notification = mockery.mock(NotificationService.class);
	browser = mockery.mock(BrowserService.class);
	core = mockery.mock(CoreService.class);
	getBeanToTest().setMembershipService(membership);
	getBeanToTest().setNotificationService(notification);
	getBeanToTest().setBindingService(binding);
	getBeanToTest().setPEPService(pep);
	getBeanToTest().setPAPService(pap);
	getBeanToTest().setBrowser(browser);
	getBeanToTest().setCore(core);
    }
    
    public void testStatus() {
    	log.debug("testing status");

    	try {

    	    String value = service.getServiceVersion();

    	    value = service.isDBOpen();
    	    log.debug("DB status: " + value);
    	    
    	    assertTrue(value.equals("true"));

    	    log.debug("end status test");
    	} catch (Exception e) {
    	    log.debug(e.getLocalizedMessage());
    	}
        }
   
}
