package org.qualipso.factory.jabuti;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;

/**
 * @author Rafael Messias Martins
 * @date Sometime in 2009
 */
@Remote
@WebService(name = JabutiService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + JabutiService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface JabutiService extends FactoryService {

	public String SERVICE_NAME = "jabuti";
	public static final String[] RESOURCE_TYPE_LIST = new String[] {};

	@WebMethod
	@WebResult(name = "projectId")
	public String createProject(String IdUserName, String projectName, byte[] projectFile) 
	throws JabutiServiceException;

	@WebMethod
	@WebResult(name = "message")
	public String updateProject(String IdUserName, String projectId, byte[] projectFile) 
	throws InvalidProjectIdFault, InvalidFileFault, JabutiServiceException;

	@WebMethod
	@WebResult(name = "message")
	public String deleteProject(String IdUserName, String projectId) 
	throws InvalidProjectIdFault, JabutiServiceException;

	@WebMethod
	@WebResult(name = "message")
	public String cleanProject(String IdUserName, String projectId) 
	throws InvalidProjectIdFault, JabutiServiceException;

	@WebMethod
	@WebResult(name = "message")
	public String ignoreClasses(String IdUserName, String projectId, String[] classes) 
	throws InvalidProjectIdFault, ClassNotFoundFault, InvalidExpressionFault, 
		JabutiServiceException;

	@WebMethod
	@WebResult(name = "message")
	public String selectClassesToInstrument(String IdUserName, String projectId, String[] classes) 
	throws InvalidProjectIdFault, ClassNotFoundFault, InvalidExpressionFault,
		JabutiServiceException;

	@WebMethod
	@WebResult(name = "requiredElementsDetails")
	public RequiredElementsDetails[] getAllRequiredElements(String IdUserName, String projectId) 
	throws InvalidProjectIdFault, OperationSequenceFault, 
		JabutiServiceException;

	@WebMethod
	@WebResult(name = "methods")
	public Method[] getRequiredElementsByCriterion(String IdUserName, String projectId, String criterion) 
	throws InvalidProjectIdFault, OperationSequenceFault, 
		InvalidCriterionFault, JabutiServiceException;

	@WebMethod
	@WebResult(name = "message")
	public String addTestCases(String IdUserName, String projectId, String testSuiteClass, byte[] testCaseFile) 
	throws InvalidFileFault, InvalidProjectIdFault, ClassNotFoundFault, OperationSequenceFault, JabutiServiceException;

	@WebMethod
	@WebResult(name = "instrumentedProjectDetails")
	public InstrumentedProjectDetails getInstrumentedProject(String IdUserName, String projectId) 
	throws InvalidProjectIdFault, OperationSequenceFault, JabutiServiceException;

	@WebMethod
	@WebResult(name = "message")
	public String sendTraceFile(String IdUserName, String projectId, byte[] tracefile) 
	throws InvalidProjectIdFault, OperationSequenceFault, InvalidFileFault, JabutiServiceException;

	@WebMethod
	@WebResult(name = "criteria")
	public CoverageCriterionDetails[] getCoverageByCriteria(String IdUserName, String projectId) 
	throws InvalidProjectIdFault, OperationSequenceFault, JabutiServiceException;

	@WebMethod
	@WebResult(name = "message")
	public CoverageDetails[] getCoverageByClasses(String IdUserName, String projectId) 
	throws InvalidProjectIdFault, OperationSequenceFault, JabutiServiceException;

	@WebMethod
	@WebResult(name = "methodCoverage")
	public CoverageDetails[] getCoverageByMethods(String IdUserName, String projectId) 
	throws InvalidProjectIdFault, OperationSequenceFault, JabutiServiceException;

	@WebMethod
	@WebResult(name = "methodCoverage")
	public MethodDetails[] getAllCoveredAndUncoveredRequiredElements(String IdUserName, String projectId) 
	throws InvalidProjectIdFault, OperationSequenceFault, JabutiServiceException;

}
