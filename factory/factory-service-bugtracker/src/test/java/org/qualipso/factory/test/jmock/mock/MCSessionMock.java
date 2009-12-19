package org.qualipso.factory.test.jmock.mock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.mantisbt.connect.AccessLevel;
import org.mantisbt.connect.Enumeration;
import org.mantisbt.connect.IMCSession;
import org.mantisbt.connect.MCException;
import org.mantisbt.connect.Viewstate;
import org.mantisbt.connect.model.IAccount;
import org.mantisbt.connect.model.ICustomFieldDefinition;
import org.mantisbt.connect.model.IFilter;
import org.mantisbt.connect.model.IIssue;
import org.mantisbt.connect.model.IIssueHeader;
import org.mantisbt.connect.model.IMCAttribute;
import org.mantisbt.connect.model.INote;
import org.mantisbt.connect.model.IProject;
import org.mantisbt.connect.model.IProjectAttachment;
import org.mantisbt.connect.model.IProjectVersion;
import org.mantisbt.connect.model.IRelationship;
import org.mantisbt.connect.model.Issue;
import org.mantisbt.connect.model.MCAttribute;
import org.mantisbt.connect.model.Project;

/**
 * 
 * mantisconnect mock
 *
 */
public class MCSessionMock implements IMCSession {

	/**
	 * Date last updated for the issue mocked
	 */
	private static Date dateLastUpdated = null;
	
	/**
	 * Date submitted of the issue mocked
	 */
	private static Date dateSubmitted = null;
	
	static {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			dateSubmitted = sdf.parse("10/11/2008");
			dateLastUpdated = sdf.parse("12/11/2008");
		} 
		catch (ParseException e) {
		}
	}
	
	//******* MOCK ********
	
	@Override
	public IMCAttribute[] getEnum(Enumeration arg0) throws MCException {
		if ( arg0.equals(Enumeration.PRIORITIES)) {
			return createAttribute("priority", 4);
			
		}
		else if ( arg0.equals(Enumeration.RESOLUTIONS)) {
			return createAttribute("resolution", 5);
		}
		else if ( arg0.equals(Enumeration.SEVERITIES)) {
			return createAttribute("severity", 6);
		}
		else if ( arg0.equals(Enumeration.STATUS)) {
			return createAttribute("status", 7);
		}
		else {
			throw new MCException ("Mock Not Implemented for getEnum(" + arg0.toString() + ")");
		}
	}
	
    /**
     * Add nb attribute to the list
     * @param attributes result
     * @param name of the attribute
     * @param nb attributes to add
     */
    private IMCAttribute[] createAttribute(String name, int nb) {
    	final IMCAttribute[] attributes = new MCAttribute[nb];
    	for (int i = 0; i < nb; i++) {
    		IMCAttribute attribute = new MCAttribute(i, name + i);
    		attributes[i] = attribute;
		}
    	return attributes;
    }
    
	@Override
	public long addIssue(IIssue arg0) throws MCException {
		return 10;
	}
	
	@Override
	public long addProject(IProject arg0) throws MCException {
		return 0;
	}

	
	@Override
	public boolean deleteIssue(long arg0) throws MCException {
		return true;
	}
	
	
	@Override
	public IIssue getIssue(long arg0) throws MCException {
		return createDefaultIssue(arg0);
	}
	
	/**
	 * Create a default issue
	 * @param id of the issue
	 * @return an issue
	 * @throws MCException if an error occurred
	 */
	private IIssue createDefaultIssue(final long id) throws MCException {
		IIssue issue = new Issue () {
			@Override
			public long getId() {
				return id;
			}

			/* (non-Javadoc)
			 * @see org.mantisbt.connect.model.Issue#getDateLastUpdated()
			 */
			@Override
			public Date getDateLastUpdated() {
				return getDateLastUpdatedMocked(id);
			}

			/* (non-Javadoc)
			 * @see org.mantisbt.connect.model.Issue#getDateSubmitted()
			 */
			@Override
			public Date getDateSubmitted() {
				return getDateSubmittedMocked(id);
			}
			
			
		};
		issue.setSummary("summary" + id);
		issue.setDescription("description" + id);
		issue.setPriority(getEnum(Enumeration.PRIORITIES)[0]);
		issue.setResolution(getEnum(Enumeration.RESOLUTIONS)[0]);
		issue.setSeverity(getEnum(Enumeration.SEVERITIES)[0]);
		issue.setStatus(getEnum(Enumeration.STATUS)[0]);
		return issue;
	}
	

	@Override
	public IProject getProject(String arg0) throws MCException {
		IProject project = new Project(){

			/* (non-Javadoc)
			 * @see org.mantisbt.connect.model.Project#getId()
			 */
			@Override
			public long getId() {
				return 1;
			}
		};
		project.setName(arg0);
		return project;
	}
	
	@Override
	public IIssue[] getProjectIssues(long arg0) throws MCException {
		IIssue[] issues = new IIssue[5];
		for (int i = 0; i < 5; i++) {
			issues[i] = createDefaultIssue(i + 1);
		}
		
		return issues;
	}
	
	@Override
	public boolean updateIssue(IIssue arg0) throws MCException {
		return true;
	}

	@Override
	public String[] getCategories(long arg0) throws MCException {
		String[] results = new String[2];
		results[0] = "AZ";
		results[1] = "RT";
		return results;
	}
	
	//******* NOT MOCK ********

	@Override
	public boolean updateVersion(IProjectVersion arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}
	
	@Override
	public IProjectVersion[] getReleasedVersions(long arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}
	
	
	@Override
	public long addIssueAttachment(long arg0, String arg1, String arg2,
			byte[] arg3) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public long addNote(long arg0, INote arg1) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public long addProjectAttachment(long arg0, String arg1, String arg2,
			String arg3, String arg4, byte[] arg5) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public long addRelationship(long arg0, IRelationship arg1)
			throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public long addVersion(IProjectVersion arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}


	@Override
	public boolean deleteIssueAttachment(long arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public boolean deleteNote(long arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public boolean deleteProject(long arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public boolean deleteProjectAttachment(long arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public boolean deleteRelationship(long arg0, long arg1) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public boolean deleteVersion(long arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public void flush() {
		throw new RuntimeException("Mock Not Implemented");
	}

	@Override
	public IProject[] getAccessibleProjects() throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public long getBiggestIssueId(long arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public String getConfigString(String arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public ICustomFieldDefinition[] getCustomFieldDefinitions(long arg0)
			throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IMCAttribute getDefaultIssuePriority() throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IMCAttribute getDefaultIssueSeverity() throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public Viewstate getDefaultIssueViewState() throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public Viewstate getDefaultNoteViewState() throws MCException {
		throw new MCException("Mock Not Implemented");
	}


	@Override
	public String getEnum(String arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IFilter[] getFilters(long arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public long getIdFromSummary(String arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public byte[] getIssueAttachment(long arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IIssueHeader[] getIssueHeaders(long arg0, long arg1)
			throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IIssueHeader[] getIssueHeaders(long arg0, long arg1, int arg2)
			throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IIssueHeader[] getIssueHeaders(long arg0, long arg1, int arg2,
			int arg3) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IIssue[] getIssues(long arg0, long arg1) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IIssue[] getIssues(long arg0, long arg1, int arg2)
			throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IIssue[] getIssues(long arg0, long arg1, int arg2, int arg3)
			throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public byte[] getProjectAttachment(long arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IProjectAttachment[] getProjectAttachments(long arg0)
			throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IIssueHeader[] getProjectIssueHeaders(long arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IIssueHeader[] getProjectIssueHeaders(long arg0, int arg1)
			throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IIssueHeader[] getProjectIssueHeaders(long arg0, int arg1, int arg2)
			throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IIssue[] getProjectIssues(long arg0, int arg1) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IIssue[] getProjectIssues(long arg0, int arg1, int arg2)
			throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IAccount[] getProjectUsers(long arg0, AccessLevel arg1)
			throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IProjectVersion[] getUnreleasedVersions(long arg0)
			throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public String getVersion() throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IProjectVersion[] getVersions(long arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public boolean issueExists(long arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IIssue newIssue(long arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public INote newNote(String arg0) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	@Override
	public IProject getProject(long l) throws MCException {
		throw new MCException("Mock Not Implemented");
	}

	
	/**
	 * get Date submitted for the issue mocked
	 * @param idIssue of the mocked issue
	 * @return date
	 */
	public static Date getDateSubmittedMocked(long idIssue) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateSubmitted);
		cal.add(Calendar.MINUTE, (int)idIssue);
		return cal.getTime();
	}
	
	/**
	 * get Date submitted for the issue mocked
	 * @param idIssue of the mocked issue
	 * @return date
	 */
	public static Date getDateLastUpdatedMocked(long idIssue) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateLastUpdated);
		cal.add(Calendar.MINUTE, (int)idIssue + 10);
		return cal.getTime();
	}
}
