package org.qualipso.factory.workflow.hook;

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
import org.qualipso.factory.client.ws.SVNServiceException_Exception;
import org.qualipso.factory.client.ws.Svn;
import org.qualipso.factory.client.ws.SvnRepository;
import org.qualipso.factory.client.ws.Svn_Service;

public class CreateSubversionRepository implements Hook {

	private static Logger LOG = Logger
			.getLogger(CreateSubversionRepository.class.getName());

	public void execute(final QueryAPIAccessor arg0,
			final ActivityInstance<ActivityBody> arg1) throws Exception {

		final QueryRuntimeAPI qApi = arg0.getQueryRuntimeAPI();
		final QueryRuntimeAPI rApi = arg0.getQueryRuntimeAPI();
		final ProcessInstanceUUID processInstanceUUID = arg1
				.getProcessInstanceUUID();
		final ActivityInstanceUUID actInstUuid = arg1.getUUID();

		Svn_Service svnServiceWS = new Svn_Service();
		Svn svnService = svnServiceWS.getSVNServiceBeanPort();
		((StubExt) svnService).setConfigName("Standard WSSecurity Client");
		Map<String, Object> reqContext2 = ((BindingProvider) svnService)
				.getRequestContext();
		reqContext2.put(StubExt.PROPERTY_AUTH_TYPE,
				StubExt.PROPERTY_AUTH_TYPE_WSSE);
		reqContext2.put(BindingProvider.USERNAME_PROPERTY, "root");
		reqContext2.put(BindingProvider.PASSWORD_PROPERTY, "root");
		System.out.println("Subversion repository creation");

		if (CreateSubversionRepository.LOG.isLoggable(Level.FINE)) {
			CreateSubversionRepository.LOG.fine("validatorInitialList = ");
		}
		String svnRepoName = (String) AccessorUtil.getAPIAccessor()
		.getQueryRuntimeAPI().getProcessInstanceVariable(
				processInstanceUUID, ProcessNVUtil.PATH) + "/repo" +(String) AccessorUtil
				.getAPIAccessor().getQueryRuntimeAPI()
				.getProcessInstanceVariable(processInstanceUUID,
						ProcessNVUtil.NAME);
		try {

			// Service bootstrap = (BootstrapService)
			// ctx.lookup(FactoryNamingConvention.getJNDINameForService("bootstrap"));
			SvnRepository svnRepository = svnService
					.readSVNRepository(svnRepoName);

			System.out.println("setup - Subversion repository "
					+ svnRepository.getPath() + "  exists");
		} catch (SVNServiceException_Exception e) {
			// Create Project
			System.out.println("setup - Subversion repository "
					+ AccessorUtil.getAPIAccessor().getQueryRuntimeAPI()
							.getProcessInstanceVariable(processInstanceUUID,
									ProcessNVUtil.PATH)
					+ "  doesn't exist -> creation");
			svnService.createSVNRepository(svnRepoName, (String) AccessorUtil
					.getAPIAccessor().getQueryRuntimeAPI()
					.getProcessInstanceVariable(processInstanceUUID,
							ProcessNVUtil.NAME), (String) AccessorUtil
					.getAPIAccessor().getQueryRuntimeAPI()
					.getProcessInstanceVariable(processInstanceUUID,
							ProcessNVUtil.SUMMARY));
		}

	}
}
