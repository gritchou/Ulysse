package org.qualipso.factory.test.sb;

import static org.hamcrest.text.StringContains.containsString;
import static org.qualipso.factory.test.jmock.matcher.EventWithTypeEqualsToMatcher.anEventWithTypeEqualsTo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.mantisbt.connect.MCException;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.bugtracker.BugTrackerService;
import org.qualipso.factory.bugtracker.BugTrackerServiceBean;
import org.qualipso.factory.bugtracker.core.BugTrackerManager;
import org.qualipso.factory.bugtracker.dto.IssueAttributesDto;
import org.qualipso.factory.bugtracker.dto.IssueDto;
import org.qualipso.factory.bugtracker.dto.ProjectDto;
import org.qualipso.factory.bugtracker.dto.ProjectDtoBuilder;
import org.qualipso.factory.bugtracker.exception.BugTrackerServiceException;
import org.qualipso.factory.bugtracker.utils.Utils;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;

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
	private static final String PROJECT_PATH = "/" + PROJECT_NAME;
	
	/**
	 * Indicate if on the tear down, issues must be deleted
	 */
	private boolean needDeleteDataOnTearDown = false;
	
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
	}
    
    
    
    
    /* (non-Javadoc)
	 * @see com.bm.testsuite.BaseFixture#tearDown()
	 */
	@Override
	public void tearDown() throws Exception {
		if (needDeleteDataOnTearDown) {
			//delete all test data
			IssueDto[] issues = BugTrackerManager.getInstance().getAllIssues(
					FactoryResourceIdentifier.deserialize("ProjectService/Project/" + PROJECT_NAME), 
					PROJECT_PATH);
			
			logger.info("Delete on tear down " + issues.length + " test issues");
			for (IssueDto issueDto : issues) {
				deleteIssueTest(issueDto.getPath());
			}
		}
		needDeleteDataOnTearDown = false;
		super.tearDown();
	}

	/**
     * test the EJB service getIssue
     * @throws Exception if an error occurred
     */
    public void testGetIssue() throws Exception {
		/*
		 * Test OK
		 */
		// create an issue
		IssueDto issueDto = createIssueDto("desc bug test", "sum bug test");
		
		String issuePath = createIssueTest(issueDto, PROJECT_NAME, PROJECT_PATH);
    	
		// run service
		IssueDto issue = getIssueEjb(issuePath);
		
		 //Check the issue
		assertEquals(issueDto.getDescription(), issue.getDescription());
		assertEquals(issueDto.getSummary(), issue.getSummary());
		assertEquals(issuePath, issue.getPath());
		assertNotNull(issue.getPriority());
		assertNotNull(issue.getResolution());
		assertNotNull(issue.getSeverity());
		assertNotNull(issue.getStatus());
    	
		// delete issue after test
		needDeleteDataOnTearDown = true;
		//deleteIssueTest(issuePath);
		
		/*
		 * Test exception
		 */
		//path null
		try {
			issue = getIssueEjb(null);
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
    	String issuePath = createIssueEjb(PROJECT_PATH, issueDto);
    	
    	// Check the created issue
    	IssueDto createdIssue = getIssueTest(issuePath);
    	assertEquals(issueDto.getDescription(), createdIssue.getDescription());
    	assertEquals(issueDto.getSummary(), createdIssue.getSummary());
    	assertEquals(issuePath, createdIssue.getPath());
    	assertNotNull(createdIssue.getPriority());
    	assertNotNull(createdIssue.getResolution());
    	assertNotNull(createdIssue.getSeverity());
    	assertNotNull(createdIssue.getStatus());
    	
    	
    	/*
    	 * Test with enum value
    	 */
    	IssueAttributesDto attributes = BugTrackerManager.getInstance().getIssueAttributesDto();
    	issueDto = createIssueDto("desc bug test", "sum bug test");
    	issueDto.setPriority(attributes.getPriorities().get(attributes.getPriorities().size() - 1));
    	issueDto.setResolution(attributes.getResolutions().get(attributes.getResolutions().size() - 1));
    	issueDto.setSeverity(attributes.getSeverities().get(attributes.getSeverities().size() - 1));
    	issueDto.setStatus(attributes.getStatus().get(attributes.getStatus().size() - 1));
    	
    	// run service
    	issuePath = createIssueEjb(PROJECT_PATH, issueDto);
    	
    	// Check the created issue
    	createdIssue = getIssueTest(issuePath);
    	assertEquals(issueDto.getDescription(), createdIssue.getDescription());
    	assertEquals(issueDto.getSummary(), createdIssue.getSummary());
    	assertEquals(issuePath, createdIssue.getPath());
    	assertEquals(issueDto.getPriority().getId(), createdIssue.getPriority().getId());
    	assertEquals(issueDto.getSeverity().getId(), createdIssue.getSeverity().getId());
    	// resolution is always 'open' for a created bug
    	assertFalse(issueDto.getResolution().getId().equals(createdIssue.getResolution().getId()));
    	assertEquals("10", createdIssue.getResolution().getId());
    	// status is always 'new' for a created bug
    	assertFalse(issueDto.getStatus().getId().equals(createdIssue.getStatus().getId()));
    	assertEquals("10", createdIssue.getStatus().getId());
    	
    	
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
    	
    	needDeleteDataOnTearDown = true;
    }
    
    /**
     * Test the EJb service deleteIssue
     * @throws Exception if an error occurred
     */
    public void testDeleteIssue() throws Exception {
    	/*
    	 * Test OK
    	 */
    	// create an issue
    	IssueDto issueDto = createIssueDto("desc bug test", "sum bug test");
    	
    	String issuePath = createIssueTest(issueDto, PROJECT_NAME, PROJECT_PATH);
    	
    	//check that the issue exists
    	IssueDto issueCreated = getIssueTest(issuePath);
    	assertNotNull(issueCreated);
    	
    	
    	//delete this issue
        deleteIssueEjb(issuePath);
        
        //check that the issue doesn't exist
        try {
        	issueCreated = getIssueTest(issuePath);
        	fail();
        }
        catch (BugTrackerServiceException e) {
        	assertEquals(e.getCause().getClass(), MCException.class);
        }
        
        /*
         * Test with exception
         */
        // path null
        try {
        	deleteIssueEjb(null);
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
    public IssueDto getIssueEjb(final String issuePath) throws Exception {
        
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
    	/*
    	 * Test with result
    	 */
    	//Insert test data
    	final Map<String, IssueDto> issues = new HashMap<String, IssueDto>();
    	IssueDto dto1 = createIssueDto("desc dto 1", "sum dto 1");
    	final String path1 = createIssueTest(dto1, PROJECT_NAME, PROJECT_PATH);
    	issues.put(path1, dto1);
    	IssueDto dto2 = createIssueDto("desc dto 2", "sum dto 2");
    	final String path2 = createIssueTest(dto2, PROJECT_NAME, PROJECT_PATH);
    	issues.put(path2, dto2);
    	IssueDto dto3 = createIssueDto("desc dto 3", "sum dto 3");
    	final String path3 = createIssueTest(dto3, PROJECT_NAME, PROJECT_PATH);
    	issues.put(path3, dto3);
    	
		mockery.checking(new Expectations() {
			{
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(USER_PATH)), with(equal(PROJECT_PATH)), with(equal("getAllIssues"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(PROJECT_PATH))); will(returnValue(FactoryResourceIdentifier.deserialize("ProjectService/Project/" + PROJECT_NAME)));
				for (int i = 0; i < issues.size(); i++) {
					oneOf(pep).checkSecurity(with(equal(USER_PATH)), with(containsString(PROJECT_PATH + "/issue_")), with(equal("issue.read"))); inSequence(sequence);
				}
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.allIssues"))); inSequence(sequence);
				
			}
		});
		
		//run service
		BugTrackerService service = getBeanToTest();
		IssueDto[] dto = service.getAllIssues(PROJECT_PATH);
		
		// check result
		Set<String> issuePathResult = new HashSet<String>();
		assertEquals(issues.size(), dto.length);
		
		for (IssueDto issueDto : dto) {
			IssueDto issueDtoExpected = issues.get(issueDto.getPath());
			assertNotNull(issueDtoExpected);
			assertEquals(issueDtoExpected.getDescription(), issueDto.getDescription());
	    	assertEquals(issueDtoExpected.getSummary(), issueDto.getSummary());
	    	issuePathResult.add(issueDto.getPath());
		}
		assertEquals(issues.size(), issuePathResult.size());
		
		//Delete test data
		needDeleteDataOnTearDown = true;
		//for (String path : issues.keySet()) {
		//	deleteIssueTest(path);
		//}
		
		
		/*
		 * Test with no result
		 */
		mockery.checking(new Expectations() {
			{
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(USER_PATH)), with(equal("/noresult")), with(equal("getAllIssues"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal("/noresult"))); will(returnValue(FactoryResourceIdentifier.deserialize("ProjectService/Project/" + "noresult")));
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("project.allIssues"))); inSequence(sequence);
				
			}
		});
		
		//run service
		dto = service.getAllIssues("/noresult");
		
		// check result
		assertEquals(0, dto.length);
		
		
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
    	BugTrackerService service = getBeanToTest();
    	IssueAttributesDto attributes = service.getIssueAttributes();
    	
    	assertEquals(6, attributes.getPriorities().size());
    	assertEquals(9, attributes.getResolutions().size());
    	assertEquals(8, attributes.getSeverities().size());
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
    	// create data test
    	IssueAttributesDto attributes = BugTrackerManager.getInstance().getIssueAttributesDto();
    	IssueDto issueDto = createIssueDto("desc bug test", "sum bug test");
    	issueDto.setPriority(attributes.getPriorities().get(0));
    	issueDto.setResolution(attributes.getResolutions().get(0));
    	issueDto.setSeverity(attributes.getSeverities().get(0));
    	issueDto.setStatus(attributes.getStatus().get(0));
    	String issuePath = createIssueEjb(PROJECT_PATH, issueDto);
    	// Check the created issue
    	final IssueDto createdIssue = getIssueTest(issuePath);
    	assertEquals(issueDto.getDescription(), createdIssue.getDescription());
    	assertEquals(issueDto.getSummary(), createdIssue.getSummary());
    	assertEquals(issuePath, createdIssue.getPath());
    	assertEquals(issueDto.getPriority().getId(), createdIssue.getPriority().getId());
    	assertEquals(issueDto.getSeverity().getId(), createdIssue.getSeverity().getId());
    	assertEquals(issueDto.getResolution().getId(), createdIssue.getResolution().getId());
    	assertEquals(issueDto.getStatus().getId(), createdIssue.getStatus().getId());
    	
    	//Update Issue
    	createdIssue.setDescription("desc bug test modif");
    	createdIssue.setSummary("sum bug test modif");
    	createdIssue.setPriority(attributes.getPriorities().get(1));
    	createdIssue.setResolution(attributes.getResolutions().get(2));
    	createdIssue.setSeverity(attributes.getSeverities().get(3));
    	createdIssue.setStatus(attributes.getStatus().get(4));
    	
    	final Sequence sequence = mockery.sequence("sequence1");
    	
    	mockery.checking(new Expectations() {
			{
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(USER_PATH)), with(equal(createdIssue.getPath())), with(equal("update"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(createdIssue.getPath()))); will(returnValue(FactoryResourceIdentifier.deserialize("BugTrackerService/Issue/" + Utils.getIdBugTracker(createdIssue.getPath()))));
				
				oneOf(binding).setProperty(with(equal(createdIssue.getPath())), with(equal(FactoryResourceProperty.LAST_UPDATE_TIMESTAMP)), with(any(String.class))); inSequence(sequence);
			
				oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("issue.update"))); inSequence(sequence);
				
		}
		});
		
		BugTrackerService service = getBeanToTest();
		service.updateIssue(createdIssue.getPath(), createdIssue);
		
		// check result
		IssueDto issueUpdated = getIssueTest(createdIssue.getPath());
		
    	assertEquals(createdIssue.getDescription(), issueUpdated.getDescription());
    	assertEquals(createdIssue.getSummary(), issueUpdated.getSummary());
    	assertEquals(createdIssue.getPath(), issueUpdated.getPath());
    	assertEquals(createdIssue.getPriority().getId(), issueUpdated.getPriority().getId());
    	assertEquals(createdIssue.getSeverity().getId(), issueUpdated.getSeverity().getId());
    	assertEquals(createdIssue.getResolution().getId(), issueUpdated.getResolution().getId());
    	assertEquals(createdIssue.getStatus().getId(), issueUpdated.getStatus().getId());
    	assertTrue(createdIssue.getDateLastUpdate() < issueUpdated.getDateLastUpdate());
    	
    	/*
    	 * Test with exception
    	 */
    	// issue not up to date
    	/*final IssueDto issueNotUpToDate = getIssueTest(createdIssue.getPath());
    	issueNotUpToDate.setDateLastUpdate(0);
    	mockery.checking(new Expectations() {
			{
				oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
				oneOf(pep).checkSecurity(with(equal(USER_PATH)), with(equal(issueNotUpToDate.getPath())), with(equal("update"))); inSequence(sequence);
				oneOf(binding).lookup(with(equal(issueNotUpToDate.getPath()))); will(returnValue(FactoryResourceIdentifier.deserialize("BugTrackerService/Issue/" + Utils.getIdBugTracker(issueNotUpToDate.getPath()))));
		}
		});
    	try {
    		service.updateIssue(issueNotUpToDate.getPath(), issueNotUpToDate);
    		fail("Issue is not up to date");
    	}
    	catch (BugTrackerServiceException e) {
    		//OK
    	}*/
    	
    	
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
    	
    	this.needDeleteDataOnTearDown = true;
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
     * Create an issue by BugTrackerManager
     * @param issueDto to create
     * @param projectName of the issue
     * @param projectPath where is created the issue
     * @return the issue path
     * @throws Exception if an error occurred
     */
    private String createIssueTest(IssueDto issueDto, String projectName, String projectPath) throws Exception {
    	
    	//Search the project
		long idProject = 0;
		ProjectDto project = BugTrackerManager.getInstance().findProjectByName(projectName);
		if (project == null) {
			ProjectDto dto = ProjectDtoBuilder.createDefault(projectName, projectName);
			idProject = BugTrackerManager.getInstance().createProject(dto);
		}
		else {
			idProject = project.getId();
		}
		
		long idIssue = BugTrackerManager.getInstance().createIssue(issueDto, idProject);
		
		return Utils.generatePathIssueFactory(projectPath, idIssue);
    }
    
    /**
     * delete issue by EJB service
     * @param path of the issue
     * @throws Exception if an exception occurred
     */
    private void deleteIssueEjb(final String path) throws Exception {
    	final Sequence sequence = mockery.sequence("sequence1");
    	if (path != null) {
	    	mockery.checking(new Expectations() {
				{
					oneOf(membership).getProfilePathForConnectedIdentifier(); will(returnValue(USER_PATH)); inSequence(sequence);
					oneOf(pep).checkSecurity(with(equal(USER_PATH)), with(equal(path)), with(equal("delete"))); inSequence(sequence);
					oneOf(binding).lookup(with(equal(path))); will(returnValue(FactoryResourceIdentifier.deserialize("BugTrackerService/Issue/" + Utils.getIdBugTracker(path))));
					oneOf(binding).getProperty(with(equal(path)), with(equal(FactoryResourceProperty.POLICY_ID)), with(equal(false))); will(returnValue("policyIdToDelete"));
					oneOf(pap).deletePolicy(with(equal("policyIdToDelete"))); inSequence(sequence);
					oneOf(binding).unbind(with(equal(path))); inSequence(sequence);
					oneOf(notification).throwEvent(with(anEventWithTypeEqualsTo("issue.delete"))); inSequence(sequence);
					
			}
			});
    	}
		
		BugTrackerService service = getBeanToTest();
		service.deleteIssue(path);
		
    }
    
    /**
     * delete issue by BugTrackerManager
     * @param path of the issue
     * @throws Exception if an exception occurred
     */
    private void deleteIssueTest(final String path) throws Exception {
		BugTrackerManager.getInstance().deleteIssue(path);
    }
    
    
    /**
     * get issue by BugTrackerManager
     * @param issuePath path of the issue
     * @return the issue
     * @throws BugTrackerServiceException if an exception occurred
     */
    private IssueDto getIssueTest(String issuePath) throws BugTrackerServiceException {
    	IssueDto issueDto = BugTrackerManager.getInstance().getIssue(issuePath);
    	return issueDto;
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

}