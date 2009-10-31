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
import org.qualipso.factory.collaboration.calendar.CalendarService;
import org.qualipso.factory.collaboration.calendar.CalendarServiceBean;
import org.qualipso.factory.collaboration.calendar.entity.CalendarItem;
import org.qualipso.factory.collaboration.utils.CollaborationUtils;
import org.qualipso.factory.collaboration.ws.CalendarWService;
import org.qualipso.factory.collaboration.ws.beans.CalendarDTO;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.core.entity.Folder;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;

import com.bm.testsuite.BaseSessionBeanFixture;

public class CalendarServiceTest extends BaseSessionBeanFixture<CalendarServiceBean>
{

    private static Log logger = LogFactory.getLog(CalendarServiceTest.class);

    @SuppressWarnings("unchecked")
    private static final Class[] usedBeans = { CalendarItem.class };

    private Mockery mockery;
    private BindingService binding;
    private MembershipService membership;
    private PEPService pep;
    private PAPService pap;
    private NotificationService notification;
    private BrowserService browser;
    private CoreService core;
    private CalendarWService calendarWS;

    public CalendarServiceTest()
    {
	super(CalendarServiceBean.class, usedBeans);
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
	calendarWS = mockery.mock(CalendarWService.class);
	getBeanToTest().setMembershipService(membership);
	getBeanToTest().setNotificationService(notification);
	getBeanToTest().setBindingService(binding);
	getBeanToTest().setPEPService(pep);
	getBeanToTest().setPAPService(pap);
	getBeanToTest().setBrowser(browser);
	getBeanToTest().setCore(core);
	getBeanToTest().setCalendarWS(calendarWS);
    }
    

    public void testCRUDCalendar()
    {
	logger.debug("****************************************************************");
	logger.debug("testing CRUD Calendar(...)");
	logger.debug("****************************************************************");
	final Sequence sequence1 = mockery.sequence("sequence1");
	final Vector<Object> params1 = new Vector<Object>();
	
	try
	{
	    final String randomNameStr = "E" + System.currentTimeMillis();
	    final String randomName4Path = CollaborationUtils.normalizeForPath(randomNameStr);
	    final String ocId = CollaborationUtils.normalizeForPath(UUID.randomUUID().toString());
	    final String sId = CollaborationUtils.normalizeForPath(UUID.randomUUID().toString());
	    final String path = "/calendar/2010/9/2/"+ocId;
	    CalendarService service = getBeanToTest();
	    // TEST Create Event
	    mockery.checking(new Expectations()
	    {
		{
		    //
		    oneOf(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/jayblanc"));inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal("/calendar")), with(equal("create")));inSequence(sequence1);
		    // mock Mermig WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    resultMap.put("seriesID", sId);
		    HashMap ocIds = new HashMap();
		    ocIds.put(ocId, ocId);
		    resultMap.put("occurenceIds", ocIds);
		    oneOf(calendarWS).createEvent(randomNameStr,"Athens", "2010-09-02", "10:00:00", "18:00:00", "strusos","gstro@delos.eurodyn.com", "2108094500", "once", 1);will(returnValue(resultMap));inSequence(sequence1);
		    // mock core
		    Folder folder = new Folder();
		    folder.setId(UUID.randomUUID().toString());
		    folder.setName("2010");
		    oneOf(core).readFolder("/calendar/2010");will(returnValue(folder));inSequence(sequence1);
		    folder.setName("9");
		    oneOf(core).readFolder("/calendar/2010/9");will(returnValue(folder));inSequence(sequence1);
		    folder.setName("2");
		    oneOf(core).readFolder("/calendar/2010/9/2");will(returnValue(folder));inSequence(sequence1);
		    //
		    oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)),with(equal(path)));will(saveParams(params1));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal(path)),with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)),with(any(String.class)));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal(path)),with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)),with(any(String.class)));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal(path)),with(equal(FactoryResourceProperty.AUTHOR)),with(equal("/profiles/jayblanc")));inSequence(sequence1);
		    oneOf(pap).createPolicy(with(any(String.class)),with(containsString(path)));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal(path)),with(equal(FactoryResourceProperty.OWNER)),with(equal("/profiles/jayblanc")));inSequence(sequence1);
		    oneOf(binding).setProperty(with(equal(path)),with(equal(FactoryResourceProperty.POLICY_ID)),with(any(String.class)));inSequence(sequence1);
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("collaboration.calendar.create")));inSequence(sequence1);
		}
	    });
	    service.createEvent("/calendar", randomNameStr, "Athens", "2010-09-02", "10:00:00", "18:00:00", "strusos","gstro@delos.eurodyn.com", "2108094500", "once", 1);
	    // TEST Read Event
	    mockery.checking(new Expectations()
	    {
		{
		    oneOf(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/jayblanc"));inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal(path)), with(equal("read")));inSequence(sequence1);
		    oneOf(binding).lookup(with(equal(path)));will(returnValue(params1.get(0)));inSequence(sequence1);
		    //mock the WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    CalendarDTO cItem = new CalendarDTO();
		    cItem.setLocation("Athens");
		    resultMap.put("calendar-item", cItem);
		    oneOf(calendarWS).readEvent(ocId, sId, "occurence");will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("collaboration.calendar.read")));inSequence(sequence1);
		}
	    });
	    CalendarItem event = service.readEvent(path);
	    assertNotNull(event);
	    assertTrue(event.getName().equals(randomNameStr));
	    assertTrue(event.getLocation().equals("Athens"));
	    
	    //TEST Update Event
	    mockery.checking(new Expectations()
	    {
		{
		    //Update the event : 
		    oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal(path)), with(equal("update"))); inSequence(sequence1);
		    oneOf(binding).lookup(with(equal(path))); will(returnValue(params1.get(0))); inSequence(sequence1);
		    //mock the ws
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    oneOf(calendarWS).updateEvent(ocId, sId, randomNameStr, "Athens", "2010-09-02", "10:30:00", "18:30:00","strusos", "gstro@delos.eurodyn.com", "2108094500");will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(binding).setProperty(with(equal(path)), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence1);
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("collaboration.calendar.update"))); inSequence(sequence1);
		    //Read the event:
		    oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue("/profiles/jayblanc")); inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")), with(equal(path)), with(equal("read"))); inSequence(sequence1);
		    oneOf(binding).lookup(with(equal(path))); will(returnValue(params1.get(0))); inSequence(sequence1);
		    //mock the WS
		    CalendarDTO cItem = new CalendarDTO();
		    cItem.setLocation("Athens");
		    cItem.setStartTime("10:30:00");
		    resultMap.put("calendar-item", cItem);
		    oneOf(calendarWS).readEvent(ocId, sId, "occurence");will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("collaboration.calendar.read"))); inSequence(sequence1);
		    
		}
	    });
	    service.updateEvent(path, randomNameStr, "Athens", "2010-09-02", "10:30:00", "18:30:00","strusos", "gstro@delos.eurodyn.com", "2108094500");
	    assertTrue(service.readEvent(path).getStartTime().equals("10:30:00"));
	    
	    
	    mockery.checking(new Expectations()
	    {
		{
		    // Delete the event :
		    oneOf(membership).getProfilePathForConnectedIdentifier();will(returnValue("/profiles/jayblanc"));inSequence(sequence1);
		    oneOf(pep).checkSecurity(with(equal("/profiles/jayblanc")),with(equal(path)), with(equal("delete")));inSequence(sequence1);
		    oneOf(binding).lookup(with(equal(path)));will(returnValue(params1.get(0)));inSequence(sequence1);
		    //mock the WS
		    HashMap<String, Object> resultMap = new HashMap<String, Object>();
		    resultMap.put("statusCode", "SUCCESS");
		    resultMap.put("statusMessage", "done");
		    oneOf(calendarWS).deleteEvent(ocId);will(returnValue(resultMap));inSequence(sequence1);
		    //
		    oneOf(binding).getProperty(with(equal(path)), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); inSequence(sequence1);
		    oneOf(pap).deletePolicy(with(any(String.class))); inSequence(sequence1);
		    oneOf(binding).unbind(with(equal(path)));inSequence(sequence1);
		    oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("collaboration.calendar.delete")));inSequence(sequence1);
		}
	    });
	    service.deleteEvent(path);
	    
	    
	    mockery.assertIsSatisfied();

	} catch (Exception e)
	{
	    logger.error(e.getMessage(), e);
	    fail(e.getMessage());
	}
    }
}
