package org.qualipso.factory.test.sessionbean;

import static org.hamcrest.text.StringContains.containsString;
import static org.qualipso.factory.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.bugtracker.BugTrackerService;
import org.qualipso.factory.bugtracker.BugTrackerServiceBean;
import org.qualipso.factory.bugtracker.dto.IssueAttributesDto;
import org.qualipso.factory.bugtracker.dto.IssueDto;
import org.qualipso.factory.bugtracker.entity.Issue;
import org.qualipso.factory.bugtracker.exception.BugTrackerServiceException;
import org.qualipso.factory.bugtracker.utils.Utils;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.test.jmock.mock.BugTrackerManagerMock;
import org.qualipso.factory.test.jmock.mock.MCSessionMock;

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
	private static final Class[] usedBeans = {Issue.class};
    
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
	 * ForumService
	 */
	//private ForumService forumService;
	
	/**
	 * DocumentService
	 */
	//private DocumentService documentService;
	

	/**
	 * user path
	 */
	private static final String USER_PATH = "/profiles/anonyme";
	
	private static final String [] SUBJECTS = new String[]{
		USER_PATH
	};
	
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
		//forumService = mockery.mock(ForumService.class);
		//documentService = mockery.mock(DocumentService.class);
		getBeanToTest().setMembershipService(membership);
		getBeanToTest().setNotificationService(notification);
		getBeanToTest().setBindingService(binding);
		getBeanToTest().setPEPService(pep);
		getBeanToTest().setPAPService(pap);
		getBeanToTest().setBugTrackerManager(new BugTrackerManagerMock());
		//getBeanToTest().setDocumentService(documentService);
		//getBeanToTest().setForumService(forumService);
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
				oneOf(membership).getConnectedIdentifierSubjects(); will(returnValue(SUBJECTS)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal("/test/issue_1")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal("/test/issue_1"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/1")));
				
				oneOf(binding).lookup(with(equal("/profiles/toto"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "1")));
				oneOf(binding).lookup(with(equal("/profiles/titi"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "2")));
				//oneOf(membership).readProfile(with(equal("/profiles/toto"))); will(returnValue(generateProfile("toto", "fullname_toto"))); inSequence(sequence);
				//oneOf(membership).readProfile(with(equal("/profiles/titi"))); will(returnValue(generateProfile("titi", "fullname_titi"))); inSequence(sequence);
				
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("issue.read"))); inSequence(sequence);
				
		}
		});
		
		// create an issue
		createIssueFactory("1", "/profiles/toto", "/profiles/titi");
		
		// run service
		BugTrackerService service = getBeanToTest();
		IssueDto dto = service.getIssue("/test", "1");
		
		// Test result
		assertEquals("/test/issue_1", dto.getIssuePath());
		assertEquals("/test", dto.getProjectPath());
		assertEquals("1", dto.getNum());
		assertEquals("description1", dto.getDescription());
		assertEquals("summary1", dto.getSummary());
		assertEquals("/profiles/toto", dto.getReporter());
		assertEquals("/profiles/titi", dto.getAssignee());
		//assertEquals("fullname_toto", dto.getReporter());
		//assertEquals("fullname_titi", dto.getAssignee());
		assertEquals(service.getIssueAttributes().getPriorities().get(0).getId(), dto.getPriority().getId());
		assertEquals(service.getIssueAttributes().getResolutions().get(0).getId(), dto.getResolution().getId());
		assertEquals(service.getIssueAttributes().getSeverities().get(0).getId(), dto.getSeverity().getId());
		assertEquals(service.getIssueAttributes().getStatus().get(0).getId(), dto.getStatus().getId());
		assertNotNull(dto.getDateCreation());
		assertNotNull(dto.getDateModification());
    	
				
		/*
		 * Test exception
		 */
		//path null
		try {
			getIssueEjb(null, "1");
			fail("path cannot be null");
		}
		catch (BugTrackerServiceException e) {
			//OK
		}
		//num null
		try {
			getIssueEjb("/test", null);
			fail("num cannot be null");
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
				oneOf(membership).getConnectedIdentifierSubjects(); will(returnValue(SUBJECTS)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(path)), with(equal("create"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(path))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + PROJECT_NAME)));
				
				oneOf(binding).bind(with(any(FactoryResourceIdentifier.class)), with(containsString(path + "/issue_"))); inSequence(sequence);
				oneOf(binding).setProperty(with(containsString(path + "/issue_")), with(equal(FactoryResourceProperty.CREATION_TIMESTAMP)), with(any(String.class))); inSequence(sequence);
				oneOf(binding).setProperty(with(containsString(path + "/issue_")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence);
				oneOf(binding).setProperty(with(containsString(path + "/issue_")), with(equal(FactoryResourceProperty.AUTHOR)), with(equal(USER_PATH))); inSequence(sequence);
				oneOf(pap).createPolicy(with(any(String.class)), with(containsString(path + "/issue_"))); inSequence(sequence);
				oneOf(binding).setProperty(with(containsString(path + "/issue_")), with(equal(FactoryResourceProperty.OWNER)), with(equal(USER_PATH))); inSequence(sequence);
				oneOf(binding).setProperty(with(containsString(path + "/issue_")), with(equal(FactoryResourceProperty.POLICY_ID)), with(any(String.class))); inSequence(sequence);
			
				//oneOf(forumService).createForum(with(containsString(path + "/issue_")),  with(any(String.class))); inSequence(sequence);
				//oneOf(documentService).createFolder(with(containsString(path + "/issue_")), with(any(String.class)), with(any(String.class))); inSequence(sequence);
				
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("issue.create"))); inSequence(sequence);
				
		}
		});
		
		BugTrackerService service = getBeanToTest();
		String numIssue = service.createIssue(path, issueDto);
		
    	// Check the created issue
		assertEquals("10", numIssue);
    	
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
					oneOf(membership).getConnectedIdentifierSubjects(); will(returnValue(SUBJECTS)); inSequence(sequence);
					oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(issuePath)), with(equal("delete"))); inSequence(sequence);
					oneOf(binding).lookup(with(equal(issuePath))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + Utils.getIdBugTracker(issuePath))));
					oneOf(binding).getProperty(with(equal(issuePath)), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); will(returnValue("policyIdToDelete"));
					oneOf(pap).deletePolicy(with(equal("policyIdToDelete"))); inSequence(sequence);
					oneOf(binding).unbind(with(equal(issuePath))); inSequence(sequence);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("issue.delete"))); inSequence(sequence);
					
			}
			});
		
		BugTrackerService service = getBeanToTest();
		service.deleteIssue(PROJECT_PATH, "1");
        
        
        /*
         * Test with exception
         */
        // path null
        try {
        	service.deleteIssue(null, "1");
        	fail();
        }
        catch (BugTrackerServiceException e) {
        	// OK
        }
        
        /*
         * Test with exception
         */
        // path null
        try {
        	service.deleteIssue(PROJECT_PATH, null);
        	fail();
        }
        catch (BugTrackerServiceException e) {
        	// OK
        }
    }

    
	/**
     * test the EJB service findResource
     * @throws Exception if an error occurred
     */
    public void testFindResource() throws Exception {
		/*
		 * Test OK
		 */
    	final Sequence sequence = mockery.sequence("sequence1");
		mockery.checking(new Expectations() {
			{
				oneOf(membership).getConnectedIdentifierSubjects(); will(returnValue(SUBJECTS)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal("/test/issue_1")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal("/test/issue_1"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/1")));
				oneOf(binding).lookup(with(equal("/profiles/toto"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "1")));
				oneOf(binding).lookup(with(equal("/profiles/titi"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "2")));
				//oneOf(membership).readProfile(with(equal("/profiles/toto"))); will(returnValue(generateProfile("toto", "fullname_toto"))); inSequence(sequence);
				//oneOf(membership).readProfile(with(equal("/profiles/titi"))); will(returnValue(generateProfile("titi", "fullname_titi"))); inSequence(sequence);
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("issue.read"))); inSequence(sequence);
				
		}
		});
		
		// create an issue
		createIssueFactory("1", "/profiles/toto", "/profiles/titi");
		
		// run service
		BugTrackerService service = getBeanToTest();
		FactoryResource resource = service.findResource("/test/issue_1");
		
		// Test result
		resource.getFactoryResourceIdentifier();
		resource.getResourceName();
		assertEquals("/test/issue_1", resource.getResourcePath());
		assertEquals("1", resource.getResourceName());
		assertEquals(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/1"), resource.getFactoryResourceIdentifier());
		
		Issue issue = (Issue) resource;
		assertEquals("1", issue.getId());
		assertEquals("/profiles/toto", issue.getReporter());
		assertEquals("/profiles/titi", issue.getAssigned());
		assertEquals(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/1"), issue.getFactoryResourceIdentifier());
		assertEquals("/test/issue_1", issue.getResourcePath());
		assertEquals("1", issue.getResourceName());
    }
    
    /**
     * get issue by EJB service
     * @param issuePath path of the issue
     * @return the issue
     * @throws Exception if an exception occurred
     */
    private IssueDto getIssueEjb(final String projectPath, final String numIssue) throws Exception {
        
    	final Sequence sequence = mockery.sequence("sequence1");
    	if (projectPath != null && numIssue != null) {
    		final String issuePath = Utils.generatePathIssueFactory(projectPath, numIssue);
  			mockery.checking(new Expectations() {
  				{
  					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
  					oneOf(membership).getConnectedIdentifierSubjects(); will(returnValue(SUBJECTS)); inSequence(sequence);
  					oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(issuePath)), with(equal("read"))); inSequence(sequence);
  					oneOf(binding).lookup(with(equal(issuePath))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + Utils.getIdBugTracker(issuePath))));
  					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("issue.read"))); inSequence(sequence);
  					
  			}
  			});
    	}
		BugTrackerService service = getBeanToTest();
		IssueDto dto = service.getIssue(projectPath, numIssue);
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
				oneOf(membership).getConnectedIdentifierSubjects(); will(returnValue(SUBJECTS)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH)), with(equal("getAllIssues"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + PROJECT_NAME)));
				
				//issue1
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_1")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_1"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/1")));
				oneOf(binding).lookup(with(equal("/profiles/toto"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "1")));
				oneOf(binding).lookup(with(equal("/profiles/titi"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "2")));
				//oneOf(membership).readProfile(with(equal("/profiles/toto"))); will(returnValue(generateProfile("toto", "fullname_toto"))); inSequence(sequence);
				//oneOf(membership).readProfile(with(equal("/profiles/titi"))); will(returnValue(generateProfile("titi", "fullname_titi"))); inSequence(sequence);
				//issue2 (no assigned)
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_2")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_2"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/2")));
				oneOf(binding).lookup(with(equal("/profiles/tutu"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "3")));
				//oneOf(membership).readProfile(with(equal("/profiles/tutu"))); will(returnValue(generateProfile("tutu", "fullname_tutu"))); inSequence(sequence);
				//issue3
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_3")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_3"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/3")));
				oneOf(binding).lookup(with(equal("/profiles/tutu"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "3")));
				oneOf(binding).lookup(with(equal("/profiles/tutu"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "3")));
				//oneOf(membership).readProfile(with(equal("/profiles/tutu"))); will(returnValue(generateProfile("tutu", "fullname_tutu"))); inSequence(sequence);
				//oneOf(membership).readProfile(with(equal("/profiles/tutu"))); will(returnValue(generateProfile("tutu", "fullname_tutu"))); inSequence(sequence);
				//issue4 (no assigned)
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_4")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_4"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/4")));
				oneOf(binding).lookup(with(equal("/profiles/toto"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "1")));
				//oneOf(membership).readProfile(with(equal("/profiles/toto"))); will(returnValue(generateProfile("tutu", "fullname_toto"))); inSequence(sequence);
				//issue5 (no assigned)
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_5")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_5"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/5")));
				oneOf(binding).lookup(with(equal("/profiles/toto"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "1")));
				//oneOf(membership).readProfile(with(equal("/profiles/toto"))); will(returnValue(generateProfile("tutu", "fullname_toto"))); inSequence(sequence);
				
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.allIssues"))); inSequence(sequence);
				
			}
		});
		
		//create issue
		createIssueFactory("1", "/profiles/toto", "/profiles/titi");
		createIssueFactory("2", "/profiles/tutu", null);
		createIssueFactory("3", "/profiles/tutu", "/profiles/tutu");
		createIssueFactory("4", "/profiles/toto", null);
		createIssueFactory("5", "/profiles/toto", null);
		
		//run service
		BugTrackerService service = getBeanToTest();
		IssueDto[] dto = service.getAllIssues(PROJECT_PATH);
		
		// check result
		assertEquals(5, dto.length);
		for (int i = 0; i < 5; i++) {
			assertEquals(PROJECT_PATH + "/issue_" + (i + 1), dto[i].getIssuePath());
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
     * get issue by EJB service
     * @param issuePath path of the issue
     * @return the issue
     * @throws Exception if an exception occurred
     */
    public void testGetNewIssues() throws Exception {
		final Sequence sequence = mockery.sequence("sequence1");
    	
		mockery.checking(new Expectations() {
			{
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
				oneOf(membership).getConnectedIdentifierSubjects(); will(returnValue(SUBJECTS)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH)), with(equal("getNewIssues"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + PROJECT_NAME)));
				
				//issue4 (no assigned)
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_4")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_4"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/4")));
				oneOf(binding).lookup(with(equal("/profiles/toto"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "1")));
				//oneOf(membership).readProfile(with(equal("/profiles/toto"))); will(returnValue(generateProfile("tutu", "fullname_toto"))); inSequence(sequence);
				//issue5 (no assigned)
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_5")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_5"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/5")));
				oneOf(binding).lookup(with(equal("/profiles/toto"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "1")));
				//oneOf(membership).readProfile(with(equal("/profiles/toto"))); will(returnValue(generateProfile("tutu", "fullname_toto"))); inSequence(sequence);
				
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.newIssues"))); inSequence(sequence);
				
			}
		});
		
		//create issue
		createIssueFactory("1", "/profiles/toto", "/profiles/titi");
		createIssueFactory("2", "/profiles/tutu", null);
		createIssueFactory("3", "/profiles/tutu", "/profiles/tutu");
		createIssueFactory("4", "/profiles/toto", null);
		createIssueFactory("5", "/profiles/toto", null);
		
		//Date creation to test
		Date dateCreationIssue3 = MCSessionMock.getDateSubmittedMocked(3);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateCreationIssue3);
		cal.add(Calendar.MILLISECOND, 2);
		Date dateCreationJustAfterIssue3 = cal.getTime();
		assertTrue(dateCreationJustAfterIssue3.before(MCSessionMock.getDateLastUpdatedMocked(3)));
		
		
		
		//run service
		BugTrackerService service = getBeanToTest();
		IssueDto[] dto = service.getNewIssues(PROJECT_PATH, dateCreationJustAfterIssue3);
		
		// check result
		assertEquals(2, dto.length);
		assertEquals(PROJECT_PATH + "/issue_4", dto[0].getIssuePath());
		assertEquals(PROJECT_PATH + "/issue_5", dto[1].getIssuePath());
	
		
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
     * get issue by EJB service
     * @param issuePath path of the issue
     * @return the issue
     * @throws Exception if an exception occurred
     */
    public void testGetNewIssuesWithDateNull() throws Exception {
        
    	final Sequence sequence = mockery.sequence("sequence1");
    	
		mockery.checking(new Expectations() {
			{
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
				oneOf(membership).getConnectedIdentifierSubjects(); will(returnValue(SUBJECTS)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH)), with(equal("getNewIssues"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + PROJECT_NAME)));
				
				//issue1
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_1")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_1"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/1")));
				oneOf(binding).lookup(with(equal("/profiles/toto"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "1")));
				oneOf(binding).lookup(with(equal("/profiles/titi"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "2")));
				//oneOf(membership).readProfile(with(equal("/profiles/toto"))); will(returnValue(generateProfile("toto", "fullname_toto"))); inSequence(sequence);
				//oneOf(membership).readProfile(with(equal("/profiles/titi"))); will(returnValue(generateProfile("titi", "fullname_titi"))); inSequence(sequence);
				//issue2 (no assigned)
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_2")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_2"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/2")));
				oneOf(binding).lookup(with(equal("/profiles/tutu"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "3")));
				//oneOf(membership).readProfile(with(equal("/profiles/tutu"))); will(returnValue(generateProfile("tutu", "fullname_tutu"))); inSequence(sequence);
				//issue3
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_3")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_3"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/3")));
				oneOf(binding).lookup(with(equal("/profiles/tutu"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "3")));
				oneOf(binding).lookup(with(equal("/profiles/tutu"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "3")));
				//oneOf(membership).readProfile(with(equal("/profiles/tutu"))); will(returnValue(generateProfile("tutu", "fullname_tutu"))); inSequence(sequence);
				//oneOf(membership).readProfile(with(equal("/profiles/tutu"))); will(returnValue(generateProfile("tutu", "fullname_tutu"))); inSequence(sequence);
				//issue4 (no assigned)
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_4")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_4"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/4")));
				oneOf(binding).lookup(with(equal("/profiles/toto"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "1")));
				//oneOf(membership).readProfile(with(equal("/profiles/toto"))); will(returnValue(generateProfile("tutu", "fullname_toto"))); inSequence(sequence);
				//issue5 (no assigned)
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_5")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_5"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/5")));
				oneOf(binding).lookup(with(equal("/profiles/toto"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "1")));
				//oneOf(membership).readProfile(with(equal("/profiles/toto"))); will(returnValue(generateProfile("tutu", "fullname_toto"))); inSequence(sequence);
				
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.newIssues"))); inSequence(sequence);
				
			}
		});
		
		//create issue
		createIssueFactory("1", "/profiles/toto", "/profiles/titi");
		createIssueFactory("2", "/profiles/tutu", null);
		createIssueFactory("3", "/profiles/tutu", "/profiles/tutu");
		createIssueFactory("4", "/profiles/toto", null);
		createIssueFactory("5", "/profiles/toto", null);
		
		//run service
		BugTrackerService service = getBeanToTest();
		IssueDto[] dto = service.getNewIssues(PROJECT_PATH, null);
		
		// check result
		assertEquals(5, dto.length);
		for (int i = 0; i < 5; i++) {
			assertEquals(PROJECT_PATH + "/issue_" + (i + 1), dto[i].getIssuePath());
		}
	
		
		/*
		 * Test with exception
		 */
		try {
			service.getNewIssues(null, null);
			fail("path cannot be null");
		}
		catch (BugTrackerServiceException e) {
			//OK
		}
    }
    
    
    /**
     * get issue by EJB service
     * @param issuePath path of the issue
     * @return the issue
     * @throws Exception if an exception occurred
     */
    public void testGetModifiedIssues() throws Exception {
		final Sequence sequence = mockery.sequence("sequence1");
    	
		mockery.checking(new Expectations() {
			{
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
				oneOf(membership).getConnectedIdentifierSubjects(); will(returnValue(SUBJECTS)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH)), with(equal("getModifiedIssues"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + PROJECT_NAME)));
				
				//issue4 (no assigned)
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_4")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_4"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/4")));
				oneOf(binding).lookup(with(equal("/profiles/toto"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "1")));
				//oneOf(membership).readProfile(with(equal("/profiles/toto"))); will(returnValue(generateProfile("tutu", "fullname_toto"))); inSequence(sequence);
				//issue5 (no assigned)
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_5")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_5"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/5")));
				oneOf(binding).lookup(with(equal("/profiles/toto"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "1")));
				//oneOf(membership).readProfile(with(equal("/profiles/toto"))); will(returnValue(generateProfile("tutu", "fullname_toto"))); inSequence(sequence);
				
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.modifiedIssues"))); inSequence(sequence);
				
			}
		});
		
		//create issue
		createIssueFactory("1", "/profiles/toto", "/profiles/titi");
		createIssueFactory("2", "/profiles/tutu", null);
		createIssueFactory("3", "/profiles/tutu", "/profiles/tutu");
		createIssueFactory("4", "/profiles/toto", null);
		createIssueFactory("5", "/profiles/toto", null);
		
		//Date creation to test
		Date dateModificationIssue3 = MCSessionMock.getDateLastUpdatedMocked(3);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateModificationIssue3);
		cal.add(Calendar.MILLISECOND, 2);
		Date dateModificationJustAfterIssue3 = cal.getTime();
		
		
		//run service
		BugTrackerService service = getBeanToTest();
		IssueDto[] dto = service.getModifiedIssues(PROJECT_PATH, dateModificationJustAfterIssue3);
		
		// check result
		assertEquals(2, dto.length);
		assertEquals(PROJECT_PATH + "/issue_4", dto[0].getIssuePath());
		assertEquals(PROJECT_PATH + "/issue_5", dto[1].getIssuePath());
	
		
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
     * get issue by EJB service
     * @param issuePath path of the issue
     * @return the issue
     * @throws Exception if an exception occurred
     */
    public void testGetModifiedIssuesWithDateNull() throws Exception {
        
    	final Sequence sequence = mockery.sequence("sequence1");
    	
		mockery.checking(new Expectations() {
			{
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
				oneOf(membership).getConnectedIdentifierSubjects(); will(returnValue(SUBJECTS)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH)), with(equal("getModifiedIssues"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + PROJECT_NAME)));
				
				//issue1
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_1")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_1"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/1")));
				oneOf(binding).lookup(with(equal("/profiles/toto"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "1")));
				oneOf(binding).lookup(with(equal("/profiles/titi"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "2")));
				//oneOf(membership).readProfile(with(equal("/profiles/toto"))); will(returnValue(generateProfile("toto", "fullname_toto"))); inSequence(sequence);
				//oneOf(membership).readProfile(with(equal("/profiles/titi"))); will(returnValue(generateProfile("titi", "fullname_titi"))); inSequence(sequence);
				//issue2 (no assigned)
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_2")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_2"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/2")));
				oneOf(binding).lookup(with(equal("/profiles/tutu"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "3")));
				//oneOf(membership).readProfile(with(equal("/profiles/tutu"))); will(returnValue(generateProfile("tutu", "fullname_tutu"))); inSequence(sequence);
				//issue3
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_3")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_3"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/3")));
				oneOf(binding).lookup(with(equal("/profiles/tutu"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "3")));
				oneOf(binding).lookup(with(equal("/profiles/tutu"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "3")));
				//oneOf(membership).readProfile(with(equal("/profiles/tutu"))); will(returnValue(generateProfile("tutu", "fullname_tutu"))); inSequence(sequence);
				//oneOf(membership).readProfile(with(equal("/profiles/tutu"))); will(returnValue(generateProfile("tutu", "fullname_tutu"))); inSequence(sequence);
				//issue4 (no assigned)
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_4")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_4"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/4")));
				oneOf(binding).lookup(with(equal("/profiles/toto"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "1")));
				//oneOf(membership).readProfile(with(equal("/profiles/toto"))); will(returnValue(generateProfile("tutu", "fullname_toto"))); inSequence(sequence);
				//issue5 (no assigned)
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(PROJECT_PATH + "/issue_5")), with(equal("read"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH + "/issue_5"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + "/5")));
				oneOf(binding).lookup(with(equal("/profiles/toto"))); will(returnValue(FactoryResourceIdentifier.deserialize(MembershipService.SERVICE_NAME + "/" + Profile.RESOURCE_NAME + "/" + "1")));
				//oneOf(membership).readProfile(with(equal("/profiles/toto"))); will(returnValue(generateProfile("tutu", "fullname_toto"))); inSequence(sequence);
				
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.modifiedIssues"))); inSequence(sequence);
				
			}
		});
		
		//create issue
		createIssueFactory("1", "/profiles/toto", "/profiles/titi");
		createIssueFactory("2", "/profiles/tutu", null);
		createIssueFactory("3", "/profiles/tutu", "/profiles/tutu");
		createIssueFactory("4", "/profiles/toto", null);
		createIssueFactory("5", "/profiles/toto", null);
		
		//run service
		BugTrackerService service = getBeanToTest();
		IssueDto[] dto = service.getModifiedIssues(PROJECT_PATH, null);
		
		// check result
		assertEquals(5, dto.length);
		for (int i = 0; i < 5; i++) {
			assertEquals(PROJECT_PATH + "/issue_" + (i + 1), dto[i].getIssuePath());
		}
	
		
		/*
		 * Test with exception
		 */
		try {
			service.getModifiedIssues(null, null);
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
    	IssueDto createdIssue = createIssueDtoForUpdate("desc", "sum", "/test", "12");
    	
    	Issue issue = new Issue("12", USER_PATH, null);
    	getBeanToTest().getEntityManager().persist(issue);
    	
    	final Sequence sequence = mockery.sequence("sequence1");
    	
    	mockery.checking(new Expectations() {
			{
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
				oneOf(membership).getConnectedIdentifierSubjects(); will(returnValue(SUBJECTS)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal("/test/issue_12")), with(equal("update"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal("/test/issue_12"))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + Utils.getIdBugTracker("/test/issue_12"))));
				
				oneOf(binding).setProperty(with(equal("/test/issue_12")), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence);
			
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("issue.update"))); inSequence(sequence);
				
		}
		});
		
		BugTrackerService service = getBeanToTest();
		service.updateIssue(createdIssue);
    	
    	/*
    	 * Test with exception
    	 */
   	
    	// IssueDto null
    	try {
    		service.updateIssue(null);
    		fail("An exception would occured, 'IssueDto' is mandatory");
    	}
    	catch (BugTrackerServiceException e) {
    		//OK
    	}
    	
    	// Test with 'description' missing
    	createdIssue.setDescription(null);
    	try {
    		service.updateIssue(createdIssue);
    		fail("An exception would occured, 'description' is mandatory");
    	}
    	catch (BugTrackerServiceException e) {
    		//OK
    	}
    	
    	// Test with 'summary' missing
    	createdIssue.setDescription("desc");
    	createdIssue.setSummary(null);
    	try {
    		service.updateIssue(createdIssue);
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
				oneOf(membership).getConnectedIdentifierSubjects(); will(returnValue(SUBJECTS)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(SUBJECTS)), with(equal(path)), with(equal("create"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(path))); will(returnValue(FactoryResourceIdentifier.deserialize(BugTrackerService.SERVICE_NAME + "/" + Issue.RESOURCE_NAME + "/" + PROJECT_NAME)));
				
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
    	return dto;
    }
    
    /**
     * Create an issueDto for update method
     * @param description of the issue
     * @param summary of the issue
     * @param projectPath of the project
     * @param numIssue of the issue
     * @return an IssueDto
     */
    private IssueDto createIssueDtoForUpdate(String description, String summary, String projectPath, String numIssue) {
    	IssueDto dto = new IssueDto();
    	dto.setDescription(description);
    	dto.setSummary(summary);
    	dto.setProjectPath(projectPath);
    	dto.setNum(numIssue);
    	try {
			dto.setIssuePath(Utils.generatePathIssueFactory(projectPath, numIssue));
		} catch (InvalidPathException e) {
		}
		
		dto.setDateLastUpdate(MCSessionMock.getDateLastUpdatedMocked(Integer.parseInt(numIssue)).getTime());
    	dto.setDateModification(MCSessionMock.getDateLastUpdatedMocked(Integer.parseInt(numIssue)));
    	dto.setDateCreation(MCSessionMock.getDateSubmittedMocked(Integer.parseInt(numIssue)));
    	return dto;
    }
    
    /**
     * Create an issue on factory DB
     * @param id of the issue
     * @param reporter of issue 
     * @param assigned of the issue
     */
    private void createIssueFactory(String id, String reporter, String assigned) {
    	Issue issue = new Issue();
		issue.setId(id);
		issue.setReporter(reporter);
		issue.setAssigned(assigned);
		getBeanToTest().getEntityManager().persist(issue);
    }
    
    
    /**
     * generate a profile (id and fullname)
     * @param id of a profile
     * @param fullname of a profile
     * @return a Profile
     */
    private Profile generateProfile(String id, String fullname) {
    	final Profile profile = new Profile();
    	profile.setId(id);
    	profile.setFullname(fullname);
    	return profile;
    }
}