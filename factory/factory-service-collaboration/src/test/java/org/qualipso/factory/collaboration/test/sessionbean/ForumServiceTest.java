package org.qualipso.factory.collaboration.test.sessionbean;

import static org.hamcrest.text.StringContains.containsString;
import static org.qualipso.factory.collaboration.test.jmock.action.SaveParamsAction.saveParams;
import static org.qualipso.factory.collaboration.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;

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

// TODO: Auto-generated Javadoc
/**
 * The Class ForumServiceTest.
 */
public class ForumServiceTest extends BaseSessionBeanFixture<ForumServiceBean>
{
    
    /** The logger. */
    private static Log logger = LogFactory.getLog(ForumServiceTest.class);
    
    /** The Constant usedBeans. */
    @SuppressWarnings("unchecked")
    private static final Class[] usedBeans = { Forum.class,ThreadMessage.class };

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
    
    /** The forum ws. */
    private ForumWService forumWS;
    
    /** The document service. */
    private DocumentService documentService;
    

    /**
     * Instantiates a new forum service test.
     */
    public ForumServiceTest()
    {
	super(ForumServiceBean.class, usedBeans);
    }

    /* (non-Javadoc)
     * @see com.bm.testsuite.BaseSessionBeanFixture#setUp()
     */
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
	forumWS = mockery.mock(ForumWService.class);
	documentService = mockery.mock(DocumentService.class);
	getBeanToTest().setMembershipService(membership);
	getBeanToTest().setNotificationService(notification);
	getBeanToTest().setBindingService(binding);
	getBeanToTest().setPEPService(pep);
	getBeanToTest().setPAPService(pap);
	getBeanToTest().setBrowser(browser);
	getBeanToTest().setCore(core);
	getBeanToTest().setForumWS(forumWS);
	getBeanToTest().setDocumentService(documentService);
    }
    

    /**
     * Test crd.
     */
    public void testCRD()
    {
	logger.debug("****************************************************************");
	logger.debug("testing CRD Forum(...)");
	logger.debug("****************************************************************");
	final Sequence sequence1 = mockery.sequence("sequence1");
	final Vector<Object> params2 = new Vector<Object>();
	final Vector<Object> params3 = new Vector<Object>();
	try
	{
	    final String rootForums = "/forums"; 
	    final String forumName = "F"+ System.currentTimeMillis();
	    //final String forumNameNorm = CollaborationUtils.normalizeForPath(forumName);
	    final String forumId = CollaborationUtils.normalizeForPath(UUID.randomUUID().toString());
	    ForumService service = getBeanToTest();
	    // TEST Create Forum
	    mockery.checking(new Expectations()
	    {
		{
		    oneOf(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/jayblanc"));inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal(rootForums)), with(equal("create")));inSequence(sequence1);
		    // mock WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    resultMap.put("forumId", forumId);
		    oneOf(forumWS).createForum(with(any(String.class)));will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)),with(equal(rootForums+"/"+forumId)));will(saveParams(params3));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal(rootForums+"/"+forumId)),with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)),with(any(String.class)));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal(rootForums+"/"+forumId)),with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)),with(any(String.class)));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal(rootForums+"/"+forumId)),with(equal(FactoryResourceProperty.AUTHOR)),with(equal("/profiles/jayblanc")));inSequence(sequence1);
		    oneOf(pap).createPolicy(with(any(String.class)),with(containsString(rootForums+"/"+forumId)));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal(rootForums+"/"+forumId)),with(equal(FactoryResourceProperty.OWNER)),with(equal("/profiles/jayblanc")));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal(rootForums+"/"+forumId)),with(equal(FactoryResourceProperty.POLICY_ID)),with(any(String.class)));inSequence(sequence1);
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(ForumService.SERVICE_NAME,Forum.RESOURCE_NAME,"create"))));inSequence(sequence1);
		}
	    });
	    service.createForum(rootForums, forumName);
	    
	    // TEST Read Forum properties
	    mockery.checking(new Expectations()
	    {
		{
		    oneOf(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/jayblanc"));inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal(rootForums+"/"+forumId)), with(equal("read")));inSequence(sequence1);
		    oneOf(binding).lookup(with(equal(rootForums+"/"+forumId)));will(returnValue(params3.get(0)));inSequence(sequence1);
		    // Mock WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    ForumDTO forum = new ForumDTO();
		    forum.setId(forumId);
		    forum.setName(forumName);
		    forum.setDate("2009-09-29");
		    resultMap.put("forum", forum);
		    oneOf(forumWS).readForum(with(any(String.class)));will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(ForumService.SERVICE_NAME,Forum.RESOURCE_NAME,"read"))));inSequence(sequence1);
		}
	    });
	    Forum myForum = service.readForumProperties("/forums/" + forumId);
	    assertNotNull(myForum);
	    assertTrue(myForum.getName().equals(forumName));
	    logger.debug("****************************************************************");
	    logger.debug("                     THREAD MESSAGES TEST");
	    logger.debug("****************************************************************");
	    
	    final String threadId = CollaborationUtils.normalizeForPath(UUID.randomUUID().toString());
	    final String threadName = "T" + System.currentTimeMillis();
	    final String randomText = "Lorem Ipsum "+System.currentTimeMillis();
	    // TEST Create Thread
	    mockery.checking(new Expectations()
	    {
		{
		    //
		    oneOf(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/jayblanc"));inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal("/forums/" + forumId)), with(equal("create")));inSequence(sequence1);
		    // Mock WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    resultMap.put("messageId", threadId);
		    oneOf(forumWS).createThreadMessage(with(any(MessageDTO.class)),with(any(String[].class)));will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)),with(equal(rootForums+"/"+ forumId+"/"+threadId)));will(saveParams(params2));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal("/forums/" + forumId+"/"+threadId)),with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)),with(any(String.class)));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal(rootForums+"/"+ forumId+"/"+threadId)),with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)),with(any(String.class)));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal(rootForums+"/"+ forumId+"/"+threadId)),with(equal(FactoryResourceProperty.AUTHOR)),with(equal("/profiles/jayblanc")));inSequence(sequence1);
		    oneOf(pap).createPolicy(with(any(String.class)),with(containsString(rootForums+"/"+ forumId+"/"+threadId)));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal(rootForums+"/"+ forumId+"/"+threadId)),with(equal(FactoryResourceProperty.OWNER)),with(equal("/profiles/jayblanc")));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal(rootForums+"/"+ forumId+"/"+threadId)),with(equal(FactoryResourceProperty.POLICY_ID)),with(any(String.class)));inSequence(sequence1);
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(ForumService.SERVICE_NAME,ThreadMessage.RESOURCE_NAME,"create"))));inSequence(sequence1);
		}
	    });
	    ThreadMessageDetails tmd = new ThreadMessageDetails(threadName, myForum.getId(), randomText, "2009-08-28", false);
	    service.createThreadMessage("/forums/"+ forumId, tmd);
    
	    // TEST Read Thread
	    mockery.checking(new Expectations()
	    {
		{
		    oneOf(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/jayblanc"));inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal(rootForums+"/"+ forumId+"/"+threadId)), with(equal("read")));inSequence(sequence1);
		    oneOf(binding).lookup(with(equal(rootForums+"/"+ forumId+"/"+threadId)));will(returnValue(params2.get(0)));inSequence(sequence1);
		    // mock WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    MessageDTO msg = new MessageDTO();
		    msg.setId(threadId);
		    msg.setForumId(forumId);
		    msg.setName(threadName);
		    msg.setDatePosted("2009-09-29");
		    msg.setMessageBody(randomText);
		    resultMap.put("ThreadMessage", msg);
		    oneOf(forumWS).readThreadMessage(forumId,threadId);will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(ForumService.SERVICE_NAME,ThreadMessage.RESOURCE_NAME,"read"))));inSequence(sequence1);
		}
	    });
	    ThreadMessage myMsg = service.readThreadMessageProperties(rootForums+"/"+ forumId+"/" + threadId);
	    assertNotNull(myMsg);
	    assertTrue(myMsg.getName().equals(threadName));
	    // TEST Delete thread
	    mockery.checking(new Expectations()
	    {
		{
		    oneOf(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/jayblanc"));inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal(rootForums+"/"+forumId+"/" + threadId)), with(equal("delete")));inSequence(sequence1);
		    oneOf(binding).lookup(with(equal(rootForums+"/"+forumId+"/" + threadId)));will(returnValue(params2.get(0)));inSequence(sequence1);
		    // mock broswer.hasChildren(()
		    oneOf(browser).hasChildren(with(any(String.class)));will(returnValue(false));inSequence(sequence1);
		    // mock WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    oneOf(forumWS).deleteThreadMessage(threadId);will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(binding).getProperty(with(equal(rootForums+"/"+forumId+"/" + threadId)), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); inSequence(sequence1);
		    oneOf(pap).deletePolicy(with(any(String.class))); inSequence(sequence1);
		    oneOf(binding).unbind(with(equal(rootForums +"/"+forumId+"/" + threadId)));inSequence(sequence1);
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(ForumService.SERVICE_NAME,ThreadMessage.RESOURCE_NAME,"delete"))));inSequence(sequence1);
		}
	    });
	    service.deleteThreadMessage(rootForums+"/"+forumId+"/" + threadId);
	    logger.debug("****************************************************************");
	    
	    //
	    // TEST Delete Forum
	    mockery.checking(new Expectations()
	    {
		{
		    // Delete the forum
		    oneOf(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/jayblanc"));inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal(rootForums+"/"+forumId)), with(equal("delete")));inSequence(sequence1);
		    oneOf(binding).lookup(with(equal(rootForums+"/"+forumId)));will(returnValue(params3.get(0)));inSequence(sequence1);
		    // mock broswer.hasChildren(()
		    oneOf(browser).hasChildren(with(any(String.class)));will(returnValue(false));inSequence(sequence1);
		    // mock WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    oneOf(forumWS).deleteForum(forumId);will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(binding).getProperty(with(equal(rootForums+"/"+forumId)), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); inSequence(sequence1);
		    oneOf(pap).deletePolicy(with(any(String.class))); inSequence(sequence1);
		    oneOf(binding).unbind(with(equal(rootForums+"/"+forumId)));inSequence(sequence1);
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo(Event.buildEventType(ForumService.SERVICE_NAME,Forum.RESOURCE_NAME,"delete"))));inSequence(sequence1);
		}
	    });
	    service.deleteForum(rootForums +"/"+ forumId);
	    mockery.assertIsSatisfied();

	} catch (Exception e)
	{
	    logger.error(e.getMessage(), e);
	    fail(e.getMessage());
	}
    }    
   
}
