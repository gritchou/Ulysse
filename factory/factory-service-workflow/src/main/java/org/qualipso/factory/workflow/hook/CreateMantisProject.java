package org.qualipso.factory.workflow.bonita.hook;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ow2.bonita.definition.Hook;
import org.ow2.bonita.facade.QueryAPIAccessor;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.runtime.ActivityBody;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

public class CreateMantisProject implements Hook {

	private static Logger LOG = Logger.getLogger(CreateMantisProject.class.getName());

	public void execute(final QueryAPIAccessor arg0,
			final ActivityInstance<ActivityBody> arg1) throws Exception {

		final QueryRuntimeAPI qApi = arg0.getQueryRuntimeAPI();
		final QueryRuntimeAPI rApi = arg0.getQueryRuntimeAPI();
		final ProcessInstanceUUID processInstanceUUID = arg1
				.getProcessInstanceUUID();
		final ActivityInstanceUUID actInstUuid = arg1.getUUID();

		
		System.out.println("Mantis project creation");
		
		if (CreateMantisProject.LOG.isLoggable(Level.FINE)) {
			CreateMantisProject.LOG.fine("validatorInitialList = ");
		}

	}
}
