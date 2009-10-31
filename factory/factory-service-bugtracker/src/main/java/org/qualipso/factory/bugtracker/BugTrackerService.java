package org.qualipso.factory.bugtracker;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;
import org.qualipso.factory.bugtracker.dto.IssueAttributesDto;
import org.qualipso.factory.bugtracker.dto.IssueDto;
import org.qualipso.factory.bugtracker.exception.BugTrackerServiceException;

/**
 * @author Emmanuel Rias (emmanuel.rias@bull.net)
 * @date 30 june 2009
 */
@Remote
@WebService(name = "BugTrackerService", targetNamespace = "http://org.qualipso.factory.ws/service")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface BugTrackerService extends FactoryService {
	
	@WebMethod
	@WebResult(name = "issue")
	public IssueDto[] getAllIssues(String path) throws BugTrackerServiceException;
	
	@WebMethod
	@WebResult(name = "issue")
	public IssueDto getIssue(String path) throws BugTrackerServiceException;
	
	@WebMethod
	@WebResult(name = "path")
	public String createIssue(String path, IssueDto issueDto) throws BugTrackerServiceException;
	
	@WebMethod
	public void updateIssue(String path, IssueDto issueDto) throws BugTrackerServiceException;
	
	@WebMethod
	public void deleteIssue(String path) throws BugTrackerServiceException;
	
	@WebMethod
	@WebResult(name = "issueAttributes")
	public IssueAttributesDto getIssueAttributes() throws BugTrackerServiceException;
}
