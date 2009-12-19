package org.qualipso.factory.jabuti;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;
import org.qualipso.factory.core.entity.File;

/**
 * @author Rafael Messias Martins
 * @date Sometime in 2009
 */
@Remote
@WebService(name = "JabutiService", targetNamespace = "http://org.qualipso.factory.ws/service/jabuti")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface JabutiService extends FactoryService {
	
	public String SERVICE_NAME = "JabutiService";
		
	@WebMethod
	@WebResult(name = "projectId")
	public JabutiProject createProject(String projectName, File projectFile) 
	throws InvalidFileFault, InvalidNameFault;

	@WebMethod
	@WebResult(name = "message")
	public String updateProject(JabutiProject project) 
	throws InvalidProjectIdFault, InvalidFileFault;

	@WebMethod
	@WebResult(name = "message")
	public String deleteProject(JabutiProject project)
	throws InvalidProjectIdFault;

	@WebMethod
	@WebResult(name = "message")
	public String cleanProject(JabutiProject project) 
	throws InvalidProjectIdFault;
	
//	@WebMethod
//	@WebResult(name = "message")
//	public String ignoreClasses(String IdUserName, String projectId, String[] classes) 
//	throws InvalidProjectIdFault, ClassNotFoundFault, InvalidExpressionFault;
//	
//	@WebMethod
//	@WebResult(name = "message")
//	public String selectClassesToInstrument(String IdUserName, String projectId, String[] classes) 
//	throws InvalidProjectIdFault, ClassNotFoundFault, InvalidExpressionFault;
//
//	@WebMethod
//	@WebResult(name = "message")
//	public RequiredElementsDetails[] getAllRequiredElements(String IdUserName, String projectId) 
//	throws InvalidProjectIdFault, OperationSequenceFault;
//	
//	@WebMethod
//	@WebResult(name = "methods")
//	public Method[] getRequiredElementsByCriterion(String IdUserName, String projectId, String criterion) 
//	throws InvalidProjectIdFault, OperationSequenceFault, InvalidCriterionFault;
//	
//	@WebMethod
//	@WebResult(name = "graphImages")
//	public GraphDetails[] getGraph(String IdUserName, String projectId, String[] classes) 
//	throws InvalidProjectIdFault, OperationSequenceFault, ClassNotFoundFault, InvalidExpressionFault;
//	
//	@WebMethod
//	@WebResult(name = "message")
//	public String addTestCases(String IdUserName, String projectId, String testSuiteClass, DataHandler testCaseFile) 
//	throws InvalidFileFault, InvalidProjectIdFault, ClassNotFoundFault, OperationSequenceFault;
//
//	@WebMethod
//	@WebResult(name = "project")
//	public InstrumentedProjectDetails getInstrumentedProject(String IdUserName, String projectId) 
//	throws InvalidProjectIdFault, OperationSequenceFault;
//
//	@WebMethod
//	@WebResult(name = "message")
//	public String sendTraceFile(String IdUserName, String projectId, DataHandler tracefile) 
//	throws InvalidProjectIdFault, OperationSequenceFault, InvalidFileFault;	
//
//	@WebMethod
//	@WebResult(name = "criteria")
//	public CoverageCriterionDetails[] getCoverageByCriteria(String IdUserName, String projectId) 
//	throws InvalidProjectIdFault, OperationSequenceFault;
//
//	@WebMethod
//	@WebResult(name = "message")
//	public CoverageDetails[] getCoverageByClasses(String IdUserName, String projectId) 
//	throws InvalidProjectIdFault, OperationSequenceFault;
//
//	@WebMethod
//	@WebResult(name = "methodCoverage")
//	public CoverageDetails[] getCoverageByMethods(String IdUserName, String projectId) 
//	throws InvalidProjectIdFault, OperationSequenceFault;
//
//	@WebMethod
//	@WebResult(name = "methodCoverage")
//	public MethodDetails[] getAllCoveredAndUncoveredRequiredElements(String IdUserName, String projectId) 
//	throws InvalidProjectIdFault, OperationSequenceFault;
//
//	@WebMethod
//	@WebResult(name = "metrics")
//	public MetricResClass[] getMetrics(String IdUserName, String projectId, String[] classes)
//	throws InvalidProjectIdFault, ClassNotFoundFault, InvalidExpressionFault;

}
