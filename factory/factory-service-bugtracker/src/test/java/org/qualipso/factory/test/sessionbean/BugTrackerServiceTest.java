package org.qualipso.factory.test.sessionbean;

import static org.hamcrest.text.StringContains.containsString;
import static org.qualipso.factory.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.bugtracker.BugTrackerService;
import org.qualipso.factory.bugtracker.BugTrackerServiceBean;
import org.qualipso.factory.bugtracker.dto.IssueAttributesDto;
import org.qualipso.factory.bugtracker.dto.IssueDto;
import org.qualipso.factory.bugtracker.exception.BugTrackerServiceException;
import org.qualipso.factory.bugtracker.utils.Utils;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.test.jmock.mock.BugTrackerManagerMock;

import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * BugTrackerService Test
 */
public class BugTrackerServiceTest extends BaseSessionBeanFixture<BugTrackerServiceBean> {
    
	/**
	 * Logger
	 */
	private static Log logger = LogFactory.getLog(BugTrackerServiceTest.class);
    
	/**
	 * usedBeans
	 */
	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = {};
    
	/**
	 * Mockery
	 */
	private Mockery mockery;
	
	/**
	 * BindingService
	 */
	private BindingService binding;
	
	/**
	 * MembershipService
	 */
	private MembershipService membership;
	
	/**
	 * PEPService
	 */
	private PEPService pep;
	
	/**
	 * PAPService
	 */
	private PAPService pap;
	
	/**
	 * NotificationService
	 */
	private NotificationService notification;
	
	/**
	 * user path
	 */
	private static final String USER_PATH = "/profiles/anonyme";
	
	/**
	 * project name mantis
	 */
	private static final String PROJECT_NAME = "junitproject";
	
	/**
	 * project path
	 */
	public static final String PROJECT_PATH = "/" + PROJECT_NAME;

	/**
	 * Constructor
	 */
    public BugTrackerServiceTest() {
    	super(BugTrackerServiceBean.class, usedBeans);
    }
    
    /**
     * Setup
     */
    @Override
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
		getBeanToTest().setBugTrackerManager(new BugTrackerManagerMock());
	}
    
    
	/**
     * test the EJB service getIssue
     * @throws Exception if an error occurred
     */
    public void testGetIssue() throws Exception {
		/*
		 * Test OK
		 */
    	final Sequence sequence = mockery.sequence("sequence1");
		mockery.checking(new Expectations() {
			{
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(USER_PATH)), with(equal("/test/issue_1")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal("/test/issue_1"))); will(returnValue(FactoryResourceIdentifier.deserialize("BugTrackerService/Issue/1")));
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("issue.read"))); inSequence(sequence);
				
		}
		});
		// run service
		BugTrackerService service = getBeanToTest();
		IssueDto dto = service.getIssue("/test/issue_1");
		
		// Test result
		assertEquals("/test/issue_1", dto.getPath());
		assertEquals("description1", dto.getDescription());
		assertEquals("summary1", dto.getSummary());
		assertEquals(service.getIssueAttributes().getPriorities().get(0).getId(), dto.getPriority().getId());
		assertEquals(service.getIssueAttributes().getResolutions().get(0).getId(), dto.getResolution().getId());
		assertEquals(service.getIssueAttributes().getSeverities().get(0).getId(), dto.getSeverity().getId());
		assertEquals(service.getIssueAttributes().getStatus().get(0).getId(), dto.getStatus().getId());
    	
				
		/*
		 * Test exception
		 */
		//path null
		try {
			getIssueEjb(null);
			fail("path cannot be null");
		}
		catch (BugTrackerServiceException e) {
			//OK
		}
    }
    
    /**
     * Test the EJB service createIssue
     * @throws Exception if an error occurred
     */
    public void testCreateIssue() throws Exception {
    	
    	/*
    	 * Test OK
    	 */
    	IssueDto issueDto = createIssueDto("desc bug test", "sum bug test");
    	
    	// run service
    	final String path = PROJECT_PATH;
    	final Sequence sequence = mockery.sequence("sequence1");
    	
    	mockery.checking(new Expectations() {
			{
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(USER_PATH)), with(equal(path)), with(equal("create"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(path))); will(returnValue(FactoryResourceIdentifier.deserialize("ProjectService/Project/" + PROJECT_NAME)));
				
				oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(containsString(path + "/issue_"))); inSequence(sequence);
				oneOf(binding).setProperty(with(containsString(path + "/issue_")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence);
				oneOf(binding).setProperty(with(containsString(path + "/issue_")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence);
				oneOf(binding).setProperty(with(containsString(path + "/issue_")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal(USER_PATH))); inSequence(sequence);
				oneOf(pap).createPolicy(with(any(String.class)), with(containsString(path + "/issue_"))); inSequence(sequence);
				oneOf(binding).setProperty(with(containsString(path + "/issue_")), with(equal(FactoryResourceProperty.OWNER)), with(equal(USER_PATH))); inSequence(sequence);
				oneOf(binding).setProperty(with(containsString(path + "/issue_")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence);
			
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("issue.create"))); inSequence(sequence);
				
		}
		});
		
		BugTrackerService service = getBeanToTest();
		String issuePath = service.createIssue(path, issueDto);
		
    	// Check the created issue
		assertEquals(path + "/issue_10", issuePath);
    	
    	/*
    	 * Test with exception
    	 */
    	// projectPath null
    	issueDto = createIssueDto("desc bug test", "sum bug test");
    	try {
    		createIssueEjb(null, issueDto);
    		fail("An exception would occured, 'description' is mandatory");
    	}
    	catch (BugTrackerServiceException e) {
    		//OK
    	}
    	
    	// IssueDto null
    	try {
    		createIssueEjb(PROJECT_PATH, null);
    		fail("An exception would occured, 'description' is mandatory");
    	}
    	catch (BugTrackerServiceException e) {
    		//OK
    	}
    	
    	// Test with 'description' missing
    	issueDto = createIssueDto(null, "sum bug test");
    	try {
    		createIssueEjb(PROJECT_PATH, issueDto);
    		fail("An exception would occured, 'description' is mandatory");
    	}
    	catch (BugTrackerServiceException e) {
    		//OK
    	}
    	
    	// Test with 'summary' missing
    	issueDto = createIssueDto("desc bug test", null);
    	try {
    		createIssueEjb(PROJECT_PATH, issueDto);
    		fail("An exception would occured, 'summary' is mandatory");
    	}
    	catch (BugTrackerServiceException e) {
    		//OK
    	}
    }
    
    /**
     * Test the EJb service deleteIssue
     * @throws Exception if an error occurred
     */
    public void testDeleteIssue() throws Exception {
    	/*
    	 * Test OK
    	 */
    	final String issuePath = PROJECT_PATH + "/issue_1";
    	
    	//delete this issue
    	final Sequence sequence = mockery.sequence("sequence1");
	    	mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
					oneOf(pep).checkSecurity(with(equal(USER_PATH)), with(equal(issuePath)), with(equal("delete"))); inSequence(sequence);
					oneOf(binding).lookup(with(equal(issuePath))); will(returnValue(FactoryResourceIdentifier.deserialize("BugTrackerService/Issue/" + Utils.getIdBugTracker(issuePath))));
					oneOf(binding).getProperty(with(equal(issuePath)), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); will(returnValue("policyIdToDelete"));
					oneOf(pap).deletePolicy(with(equal("policyIdToDelete"))); inSequence(sequence);
					oneOf(binding).unbind(with(equal(issuePath))); inSequence(sequence);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("issue.delete"))); inSequence(sequence);
					
			}
			});
		
		BugTrackerService service = getBeanToTest();
		service.deleteIssue(issuePath);
        
        
        /*
         * Test with exception
         */
        // path null
        try {
        	service.deleteIssue(null);
        	fail();
        }
        catch (BugTrackerServiceException e) {
        	// OK
        }
    }

    
    /**
     * get issue by EJB service
     * @param issuePath path of the issue
     * @return the issue
     * @throws Exception if an exception occurred
     */
    private IssueDto getIssueEjb(final String issuePath) throws Exception {
        
    	final Sequence sequence = mockery.sequence("sequence1");
    	if (issuePath != null) {
  			mockery.checking(new Expectations() {
  				{
  					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
  					oneOf(pep).checkSecurity(with(equal(USER_PATH)), with(equal(issuePath)), with(equal("read"))); inSequence(sequence);
  					oneOf(binding).lookup(with(equal(issuePath))); will(returnValue(FactoryResourceIdentifier.deserialize("BugTrackerService/Issue/" + Utils.getIdBugTracker(issuePath))));
  					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("issue.read"))); inSequence(sequence);
  					
  			}
  			});
    	}
		BugTrackerService service = getBeanToTest();
		IssueDto dto = service.getIssue(issuePath);
		return dto;
    }
    
    /**
     * get issue by EJB service
     * @param issuePath path of the issue
     * @return the issue
     * @throws Exception if an exception occurred
     */
    public void testGetAllIssues() throws Exception {
        
    	final Sequence sequence = mockery.sequence("sequence1");
    	
		mockery.checking(new Expectations() {
			{
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(USER_PATH)), with(equal(PROJECT_PATH)), with(equal("getAllIssues"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH))); will(returnValue(FactoryResourceIdentifier.deserialize("ProjectService/Project/" + PROJECT_NAME)));
				for (int i = 0; i < 5; i++) {
					oneOf(pep).checkSecurity(with(equal(USER_PATH)), with(containsString(PROJECT_PATH + "/issue_")), with(equal("issue.read"))); inSequence(sequence);
				}
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.allIssues"))); inSequence(sequence);
				
			}
		});
		
		//run service
		BugTrackerService service = getBeanToTest();
		IssueDto[] dto = service.getAllIssues(PROJECT_PATH);
		
		// check result
		assertEquals(5, dto.length);
		for (int i = 0; i < 5; i++) {
			assertEquals(PROJECT_PATH + "/issue_" +i, dto[i].getPath());
		}
	
		
		/*
		 * Test with exception
		 */
		try {
			service.getAllIssues(null);
			fail("path cannot be null");
		}
		catch (BugTrackerServiceException e) {
			//OK
		}
    }
    
    /**
     * test getIssueAttributes
     * @throws Exception if an exception occurred
     */
    public void testGetIssueAttributes() throws Exception {
    	/*final Sequence sequence = mockery.sequence("sequence1");
    	
    	mockery.checking(new Expectations() {
			{
				IssueAttributesDto issueAttributesDto = new IssueAttributesDto();
				addAttribute(issueAttributesDto.getPriorities(), "priority", 3);
				addAttribute(issueAttributesDto.getResolutions(), "resolution", 4);
				addAttribute(issueAttributesDto.getSeverities(), "severity", 5);
				addAttribute(issueAttributesDto.getStatus(), "status", 6);		
			}
		});*/
    	
    	BugTrackerService service = getBeanToTest();
    	IssueAttributesDto attributes = service.getIssueAttributes();
    	
    	assertEquals(4, attributes.getPriorities().size());
    	assertEquals(5, attributes.getResolutions().size());
    	assertEquals(6, attributes.getSeverities().size());
    	assertEquals(7, attributes.getStatus().size());
    }
    
    /**
     * Test updateIssue
     * @throws Exception if an error occurred
     */
    public void testUpdateIssue() throws Exception {
    	/*
    	 * Test OK
    	 */
    	IssueDto createdIssue = createIssueDto("desc", "sum");
    	
    	final Sequence sequence = mockery.sequence("sequence1");
    	
    	mockery.checking(new Expectations() {
			{
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(USER_PATH)), with(equal("/test/issue_12")), with(equal("update"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal("/test/issue_12"))); will(returnValue(FactoryResourceIdentifier.deserialize("BugTrackerService/Issue/" + Utils.getIdBugTracker("/test/issue_12"))));
				
				oneOf(binding).setProperty(with(equal("/test/issue_12")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence);
			
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("issue.update"))); inSequence(sequence);
				
		}
		});
		
		BugTrackerService service = getBeanToTest();
		service.updateIssue("/test/issue_12", createdIssue);
    	
    	/*
    	 * Test with exception
    	 */
    	
    	
    	// projectPath null
    	try {
    		service.updateIssue(null, createdIssue);
    		fail("An exception would occured, 'description' is mandatory");
    	}
    	catch (BugTrackerServiceException e) {
    		//OK
    	}
    	
    	// IssueDto null
    	try {
    		service.updateIssue(PROJECT_PATH, null);
    		fail("An exception would occured, 'description' is mandatory");
    	}
    	catch (BugTrackerServiceException e) {
    		//OK
    	}
    	
    	// Test with 'description' missing
    	createdIssue.setDescription(null);
    	try {
    		service.updateIssue(PROJECT_PATH, createdIssue);
    		fail("An exception would occured, 'description' is mandatory");
    	}
    	catch (BugTrackerServiceException e) {
    		//OK
    	}
    	
    	// Test with 'summary' missing
    	createdIssue.setDescription("desc");
    	createdIssue.setSummary(null);
    	try {
    		service.updateIssue(PROJECT_PATH, createdIssue);
    		fail("An exception would occured, 'summary' is mandatory");
    	}
    	catch (BugTrackerServiceException e) {
    		//OK
    	}
    }
    
    
    /**
     * Create an issue by EJB service
     * @param path of the project
     * @param issueDto to create
     * @return the issue path
     * @throws Exception
     */
    private String createIssueEjb(final String path, IssueDto issueDto) throws Exception {
    	final Sequence sequence = mockery.sequence("sequence1");
    	
    	mockery.checking(new Expectations() {
			{
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(USER_PATH)), with(equal(path)), with(equal("create"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(path))); will(returnValue(FactoryResourceIdentifier.deserialize("ProjectService/Project/" + PROJECT_NAME)));
				
				oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(containsString(path + "/issue_"))); inSequence(sequence);
				oneOf(binding).setProperty(with(containsString(path + "/issue_")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence);
				oneOf(binding).setProperty(with(containsString(path + "/issue_")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence);
				oneOf(binding).setProperty(with(containsString(path + "/issue_")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal(USER_PATH))); inSequence(sequence);
				oneOf(pap).createPolicy(with(any(String.class)), with(containsString(path + "/issue_"))); inSequence(sequence);
				oneOf(binding).setProperty(with(containsString(path + "/issue_")), with(equal(FactoryResourceProperty.OWNER)), with(equal(USER_PATH))); inSequence(sequence);
				oneOf(binding).setProperty(with(containsString(path + "/issue_")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence);
			
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("issue.create"))); inSequence(sequence);
				
		}
		});
		
		BugTrackerService service = getBeanToTest();
		String pathIssue = service.createIssue(path, issueDto);
		
		return pathIssue;
    }
    
    
    /**
     * Create an issueDto
     * @param description of the issue
     * @param summary of the issue
     * @return an IssueDto
     */
    private IssueDto createIssueDto(String description, String summary) {
    	IssueDto dto = new IssueDto();
    	dto.setDescription(description);
    	dto.setSummary(summary);
    	dto.setDateLastUpdate(111);
    	return dto;
    }
}