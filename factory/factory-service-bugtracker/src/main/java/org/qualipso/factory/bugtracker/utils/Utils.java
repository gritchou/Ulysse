/**
 * 
 */
package org.qualipso.factory.bugtracker.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathHelper;


/**
 * @author Cédric TRAN-XUAN
 *
 */
public class Utils
{

	/**
	 * prefix for the issue path in the factory
	 */
	private static final String PREFIX_ISSUE_PATH_PART = "issue_";
	
	
	/**
	 * Method to generate an issue path in the factory
	 * @param projectPath path of the project in the factory
	 * @param idBugtracker id obtained by the bugtracker
	 * @return Issue path
	 * @throws InvalidPathException if projectPAth is invalid
	 */
	public static String generatePathIssueFactory(String projectPath, String idBugtracker) throws InvalidPathException {
		if (projectPath == null) {
			throw new InvalidPathException("projectPath can not be null");
		}
		
		PathHelper.valid(projectPath);
		StringBuffer sb = new StringBuffer(projectPath);
		sb.append("/");
		sb.append(PREFIX_ISSUE_PATH_PART);
		sb.append(idBugtracker);
		return sb.toString();
	}
	
	/**
	 * Return the issue id of the bugtracker
	 * @param issuePath
	 * @return
	 * @throws InvalidPathException if the issuePath is not valid
	 */
	public static long getIdBugTracker(String issuePath) throws InvalidPathException {
        String idBugtracker = getIdIssue(issuePath);

        if (!StringUtils.isNumeric(idBugtracker)) {
        	throw new InvalidPathException("idBugtracker must be a long");
        }
		return Long.parseLong(idBugtracker);
	}
	
	/**
	 * Return the issue id of the bugtracker
	 * @param issuePath
	 * @return
	 * @throws InvalidPathException if the issuePath is not valid
	 */
	public static String getIdIssue(String issuePath) throws InvalidPathException {
		if (StringUtils.isEmpty(issuePath)) {
			throw new InvalidPathException("issuePath can not be null");
		}
		String pathPartIssue = PathHelper.getPathPart(issuePath);
		int indexPrefix = pathPartIssue.indexOf(PREFIX_ISSUE_PATH_PART);
        if (indexPrefix < 0) {
            throw new InvalidPathException("Invalid issue path: " + issuePath);
        }
        String idBugtracker = pathPartIssue.substring(indexPrefix + PREFIX_ISSUE_PATH_PART.length());
		return idBugtracker;
	}
	
	/**
	 * Copy an array to a list
	 * @param <T> type
	 * @param tab array
	 * @return list
	 */
	public static <T> List<T> copyToList(T[] tab) {
		List<T>  list = new ArrayList<T>();
		if (tab != null) {
			for (T object : tab) {
				list.add(object);
			}
		}
		return list;
	}
}
