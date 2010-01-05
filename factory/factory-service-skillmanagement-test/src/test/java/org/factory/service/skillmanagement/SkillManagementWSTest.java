package org.factory.service.skillmanagement;

import static org.junit.Assert.fail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.factory.qualipso.service.skillmanagement.wsclient.ws.factory.qualipso.org.service.skill.Skill;
import org.factory.qualipso.service.skillmanagement.wsclient.ws.factory.qualipso.org.service.skill.Skill_Service;
import org.junit.Test;


public class SkillManagementWSTest {

 private static Log logger=LogFactory.getLog(SkillManagementWSTest.class);
 
  @Test
  public void testSkill()
  {
	  try{
		  Skill port=new Skill_Service().getSkillServiceBeanPort();
		  int message=port.controlAdmin("glaria");
		  logger.debug("message Return:"+message);
		  
	  }catch(Exception e) { logger.debug(e.getMessage()); }
  }
}
