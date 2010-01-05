package org.qualipso.factory.workflow;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathAlreadyBoundException;
import org.qualipso.factory.security.pep.AccessDeniedException;
import org.qualipso.factory.workflow.entity.Bonita;


@Remote
@WebService(name = BonitaService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + BonitaService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface BonitaService extends FactoryService {
    public static final String SERVICE_NAME = "workflow";
	public static final String[] RESOURCE_TYPE_LIST = new String[] { Bonita.RESOURCE_NAME };


	@WebMethod
	void deployProjectWorkflow();
	@WebMethod
	void unDeployProjectWorkflow();
//	@WebMethod
//	void instantiateProjectWorkflow(HashMap<String, Object> variables);
	@WebMethod
	ProcessInstanceUUID instantiateProjectWorkflow(String path) throws AccessDeniedException, PathAlreadyBoundException, InvalidPathException, BonitaServiceException;
	@WebMethod
	void deleteInstance(Bonita instantiateResult);
	@WebMethod
	Bonita[] getTasksReadyForProfile(String profile) throws AccessDeniedException, PathAlreadyBoundException, InvalidPathException, BonitaServiceException;
	@WebMethod
	void performTaskCreateProject(String path, String name, String summary, String licence, Bonita instantiateResult);
	@WebMethod
	void performTaskValidateProject(Boolean validate, Bonita bonita);
	@WebMethod
	void suspendTask(Bonita instantiateResult);
	@WebMethod
	void resumeTask(Bonita instantiateResult);
	}