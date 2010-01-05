package org.qualipso.factory.workflow.mapper;

import java.util.Set;


import org.ow2.bonita.definition.RoleMapper;
import org.ow2.bonita.facade.QueryAPIAccessor;
import org.ow2.bonita.facade.runtime.var.Enumeration;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.qualipso.factory.workflow.hook.ProcessNVUtil;

public class AdminServicesMapper implements RoleMapper
{

   public Set<String> searchMembers(final QueryAPIAccessor arg0, final ProcessInstanceUUID arg1, final String arg2)
            throws Exception
   {
      Set<String> users = null;
      //final String projectId = ProcessNVUtil.getNovaForgeProjectId();

      // get ISVALIDATORITERATION and VALIDATORREMAINIGITERATION
      // to get the right group if multiple groups have been defined
      // according: novaForge project/document type
      final String list = null; 
//    	  (String) arg0.getQueryRuntimeAPI().getProcessInstanceVariable(arg1,
//               ProcessNVUtil.VALIDATORINITIALLIST);
      if ( (list == null) || ("".equals(list)) )
      {
//         if ( (Boolean) arg0.getQueryRuntimeAPI().getProcessInstanceVariable(arg1, ProcessNVUtil.ISVALIDATORITERATION) )
//         {
            // get the right group
            // patch bonita 4.1.1
//            final int remainningIteration = ((Long) arg0.getQueryRuntimeAPI().getProcessInstanceVariable(arg1,
//                     ProcessNVUtil.VALIDATORREMAINIGITERATION)).intValue();

            //final long groupIndex = ProcessNVUtil.getValidationRoleNb(documentType, projectId) - remainningIteration;
            //users = ProcessNVUtil.getValidationGroupMembers(groupIndex, documentType, projectId);
            return users;

//         }
      }
      return users;

   }
}
