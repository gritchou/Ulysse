/**
 * 
 */
package org.qualipso.factory.subversion.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.subversion.entity.Repository;
import org.qualipso.factory.subversion.entity.SVNLogEntry;
import org.qualipso.factory.subversion.entity.SVNLogEntryPath;

/**
 * @author Cédric TRAN-XUAN
 * 
 */
public class Utils {
	public static final String toString(Repository repository) {
		String result = null;

		if (repository != null) {
			result = new StringBuilder().append("name: ").append(
					repository.getName()).append(" - id: ").append(
					repository.getId()).append(" - summary: ").append(
					repository.getSummary()).toString();
		}

		return result;
	}

	public static final Repository toRepository(
			org.qualipso.factory.subversion.wsclient.SCMServiceStub.SCMRepository repositoryResponse) {
		Repository repository = new Repository();
		repository.setId(repositoryResponse.getName());
		repository.setName(repositoryResponse.getName());
		repository.setSummary(repositoryResponse.getSummary());
		return repository;
	}

	public static final SVNLogEntry[] toSVNLogEntry(
			org.qualipso.factory.subversion.wsclient.SCMServiceStub.SVNLogEntry[] svnLogEntries) {
		Collection<SVNLogEntry> logEntries = new ArrayList<SVNLogEntry>();
		for (int i = 0; i < svnLogEntries.length; i++) {
			SVNLogEntry svnlogEntry = new SVNLogEntry();
			svnlogEntry.setAuthor(svnLogEntries[i].getAuthor());
			svnlogEntry.setDate(svnLogEntries[i].getDate().getTime());
			svnlogEntry.setMessage(svnLogEntries[i].getMessage());
			svnlogEntry.setRevision(svnLogEntries[i].getRevision());
			svnlogEntry.setChangedPaths(toSVNLogEntryPath(svnLogEntries[i]
					.getChangedPaths()));
			logEntries.add(svnlogEntry);
		}
		return logEntries.toArray(new SVNLogEntry[0]);
	}

	public static final ArrayList<SVNLogEntryPath> toSVNLogEntryPath(
			org.qualipso.factory.subversion.wsclient.SCMServiceStub.SVNLogEntryPath[] svnLogEntryPaths) {
		ArrayList<SVNLogEntryPath> logEntryPaths = new ArrayList<SVNLogEntryPath>();
		if (svnLogEntryPaths != null) {
			for (int i = 0; i < svnLogEntryPaths.length; i++) {
				SVNLogEntryPath svnlogEntryPath = new SVNLogEntryPath();
				svnlogEntryPath.setCopyPath(svnLogEntryPaths[i].getCopyPath());
				svnlogEntryPath.setCopyRevision(svnLogEntryPaths[i]
						.getCopyRevision());
				svnlogEntryPath.setOriPath(svnLogEntryPaths[i].getPath());
				svnlogEntryPath.setType(svnLogEntryPaths[i].getType());
				logEntryPaths.add(svnlogEntryPath);
			}
		}
		return logEntryPaths;
	}

	public static final Profile toProfile(
			org.qualipso.factory.subversion.wsclient.SCMServiceStub.SCMUser userResponse) {
		Profile profile = new Profile();
		profile.setId(userResponse.getId());
		profile.setFullname(userResponse.getFirstName()
				+ userResponse.getLastName());
		profile.setEmail(userResponse.getEmail());
		return profile;
	}

}
