package org.qualipso.factory.workflow.bonita.hook;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.ws.BindingProvider;

import org.jboss.ws.core.StubExt;
import org.ow2.bonita.definition.Hook;
import org.ow2.bonita.facade.QueryAPIAccessor;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.runtime.ActivityBody;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.qualipso.factory.client.ws.Project;
import org.qualipso.factory.client.ws.ProjectServiceException_Exception;
import org.qualipso.factory.client.ws.Project_Service;
import org.qualipso.factory.client.ws.Project_Type;

public class CreateFactoryProject implements Hook {

	private static Logger LOG = Logger.getLogger(CreateFactoryProject.class
			.getName());

	public void execute(final QueryAPIAccessor arg0,
			final ActivityInstance<ActivityBody> arg1) throws Exception {

		final QueryRuntimeAPI qApi = arg0.getQueryRuntimeAPI();
		final QueryRuntimeAPI rApi = arg0.getQueryRuntimeAPI();
		final ProcessInstanceUUID processInstanceUUID = arg1
				.getProcessInstanceUUID();
		final ActivityInstanceUUID actInstUuid = arg1.getUUID();

		System.out.println("Factory project creation");

		Project_Service projectServiceWS = new Project_Service();
		Project projectService = projectServiceWS.getProjectServiceBeanPort();
		((StubExt) projectService).setConfigName("Standard WSSecurity Client");
		Map<String, Object> reqContext2 = ((BindingProvider) projectService)
				.getRequestContext();
		reqContext2.put(StubExt.PROPERTY_AUTH_TYPE,
				StubExt.PROPERTY_AUTH_TYPE_WSSE);
		reqContext2.put(BindingProvider.USERNAME_PROPERTY, "root");
		reqContext2.put(BindingProvider.PASSWORD_PROPERTY, "root");
		if (CreateFactoryProject.LOG.isLoggable(Level.FINE)) {
			CreateFactoryProject.LOG.fine("validatorInitialList = ");
		}

		try {

			// Service bootstrap = (BootstrapService)
			// ctx.lookup(FactoryNamingConvention.getJNDINameForService("bootstrap"));
			Project_Type projectType = projectService
					.getProject((String) AccessorUtil.getAPIAccessor()
							.getQueryRuntimeAPI().getProcessInstanceVariable(
									processInstanceUUID, ProcessNVUtil.PATH));

			System.out.println("setup - Project " + projectType.getName()
					+ "  exists");
		} catch (ProjectServiceException_Exception e) {
			// Create Project
			System.out.println("setup - Project "
					+ AccessorUtil.getAPIAccessor().getQueryRuntimeAPI()
							.getProcessInstanceVariable(processInstanceUUID,
									ProcessNVUtil.PATH)
					+ "  doesn't exist -> creation");
			projectService.createProject((String) AccessorUtil.getAPIAccessor()
					.getQueryRuntimeAPI().getProcessInstanceVariable(
							processInstanceUUID, ProcessNVUtil.NAME),
					(String) AccessorUtil.getAPIAccessor().getQueryRuntimeAPI()
							.getProcessInstanceVariable(processInstanceUUID,
									ProcessNVUtil.NAME), (String) AccessorUtil
							.getAPIAccessor().getQueryRuntimeAPI()
							.getProcessInstanceVariable(processInstanceUUID,
									ProcessNVUtil.SUMMARY),
					(String) AccessorUtil.getAPIAccessor().getQueryRuntimeAPI()
							.getProcessInstanceVariable(processInstanceUUID,
									ProcessNVUtil.LICENCE));
		}

	}
}
