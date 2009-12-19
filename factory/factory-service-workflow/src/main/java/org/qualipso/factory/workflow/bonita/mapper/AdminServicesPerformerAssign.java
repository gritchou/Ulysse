package org.qualipso.factory.workflow.bonita.mapper;

import java.util.Set;


import org.ow2.bonita.definition.PerformerAssign;
import org.ow2.bonita.definition.RoleMapper;
import org.ow2.bonita.facade.QueryAPIAccessor;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.runtime.ActivityBody;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.var.Enumeration;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.qualipso.factory.workflow.bonita.hook.ProcessNVUtil;

public class AdminServicesPerformerAssign implements PerformerAssign
{

    public String selectUser(final QueryAPIAccessor arg0,
            final ActivityInstance<ActivityBody> arg1, final Set<String> arg2)
            throws Exception {
        final QueryRuntimeAPI qApi = arg0.getQueryRuntimeAPI();
        final ActivityInstanceUUID actInstUuid = arg1.getUUID();

        final String serviceChoicePerfAssignVar = (String) qApi.getVariable(
                actInstUuid, ProcessNVUtil.SERVICECHOICEPERFASSIGNVAR);
        if ((serviceChoicePerfAssignVar == null)
                || ("".equals(serviceChoicePerfAssignVar))) {
            return null;
        } else {
            return serviceChoicePerfAssignVar;
        }
    }

}
