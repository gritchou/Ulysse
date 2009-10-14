package org.factory.qualipso.service.skillmanagement;


import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryResource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.Stateless;
import org.factory.qualipso.service.skillmanagement.SkillService;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.factory.qualipso.service.skillmanagement.SkillServiceException;
import org.factory.qualipso.service.skillmanagement.org.tempuri.*;

@Stateless(mappedName = "SkillService")
@WebService(endpointInterface = "org.factory.qualipso.service.skillmanagement.SkillService", targetNamespace = "http://org.qualipso.factory.ws/service/skillmanagement", serviceName = "SkillService", portName = "SkillService")
@WebContext(contextRoot = "/factory-service-skillmanagement", urlPattern = "/skillmanagement")
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class SkillServiceBean implements SkillService {
	private static Log logger = LogFactory.getLog(SkillServiceBean.class);
	
	
	public SkillServiceBean(){
	
	}
	
	 @Override
	    public String[] getResourceTypeList() {
	            return new String[0];
	    }

	    @Override
	    public String getServiceName() {
	            return "SkillService";
	    }
	    
	    @Override
		@TransactionAttribute(TransactionAttributeType.REQUIRED)
		public FactoryResource findResource(String path) throws FactoryException {
			logger.info("findResource(...) called");
			logger.debug("params : path=" + path);
			
			throw new CoreServiceException("No Resource are managed by SkillManagementService");
		}
	    
	    @Override
	    public ArrayOfSupportSQL selectTyp() throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.selectTyp();
	    }
	    
	    @Override
	    public ArrayOfSupportSQL selectTypTrue() throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.selectTypTrue();
	    } 
	    
	    @Override
	    public ArrayOfSupportSQLTop selectTop() throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.selectTop();
	    }
	    
	    @Override
	    public ArrayOfSupportSQLTop selectTopSkill() throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.selectTopSkill();
	    }
	    
	    @Override
	    public ArrayOfSupportSQLComp selectComp() throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.selectComp();
	    }
	    
	    @Override
	    public ArrayOfSupportSQLCompTop selectTopComp(int id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.selectTopComp(id);
	    }
	    
	    @Override
	    public ArrayOfSupportSQLLev selectLev() throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.selectLev();
	    }
	    
	    @Override
	    public ArrayOfSupportSQLUser selectMyUser(String id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.selectMyUser(id);
	    }
	    
	    @Override
	    public ArrayOfSupportSQLUser searchAllUser(String id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.searchAllUser(id);
	    }
	    
	    @Override
	    public ArrayOfSupportSQLUser searchTypUser(String id, int idtyp) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.searchTypUser(id, idtyp);
	    }
	    
	    @Override
	    public ArrayOfSupportSQLUser searchTopUser(String id, int idtop) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.searchTopUser(id, idtop);
	    }
	    
	    @Override
	    public ArrayOfSupportSQLUserComp searchCompUser(int id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.searchCompUser(id);
	    }
	    
	    @Override
	    public ArrayOfSupportSQLGapTopic searchGapTopic(int id,String iduser) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.searchGapTopic(id, iduser);
	    }
	    
	    @Override
	    public void disableTyp(int id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.disableTyp(id);
	    }
	    
	    @Override
	    public void disableTop(int id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.disableTop(id);
	    }
	    
	    @Override
	    public void insertTyp(String name,String description) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.insertTyp(name, description);
	    }
	    
	    @Override
	    public void insertTop(String name,String description,int idTypology,int flag) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.insertTop(name, description, idTypology, flag);
	    }
	    
	    @Override
	    public void insertCompTop(int idC,int idT,int order) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.insertCompTop(idC, idT, order);
	    }
	    
	    @Override
	    public void insertComp(String name,String description) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.insertComp(name, description);
	    }
	    
	    @Override
	    public void insertMySkill(String iduser,int idtopic,int idlevel,String descr) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.insertMySkill(iduser, idtopic, idlevel, descr);
	    }
	    
	    @Override
	    public void deleteTyp(int id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.deleteTyp(id);
	    }
	    
	    @Override
	    public void deleteTop(int id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.deleteTop(id);
	    }
	    
	    @Override
	    public void deleteTopComp(int id,int idtop) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.deleteTopComp(id, idtop);
	    }
	    
	    @Override
	    public void deleteMySkill(int id,String iduser) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.deleteMySkill(id, iduser);
	    }
	    
	    @Override
	    public void deleteCompWithTopic(int id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.deleteCompWithTopic(id);
	    }
	    
	    @Override
	    public void enableTyp(int id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.enableTyp(id);
	    }
	    
	    @Override
	    public void enableAll(int id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.enableAll(id);
	    }
	    
	    @Override
	    public void enableTop(int id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.enableTop(id);
	    }
	    
	    @Override
	    public int controlUserTyp(int id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.controlUserTyp(id);
	    }
	    
	    @Override
	    public int controlTopEnable(int id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.controlTopEnable(id);
	    }
	    
	    @Override
	    public int controlNameTopic(String name) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.controlNameTopic(name);
	    }
	    
	    @Override
	    public int controlNameTypology(String name) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.controlNameTypology(name);
	    }
	    
	    @Override
	    public int controlNameCompetence(String name) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.controlNameCompetence(name);
	    }
	    
	    @Override
	    public int controlAddTopicCompetence(int idcomp,int idtop) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.controlAddTopicCompetence(idcomp, idtop);
	    }
	    
	    @Override
	    public int controlUserTop(int id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.controlUserTop(id);
	    }
	    
	    @Override
	    public int controlAdmin(String iduser) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.controlAdmin(iduser);
	    }
	    
	    @Override
	    public int controlSkill(String iduser,int idtop) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	return soap.controlSkill(iduser, idtop);
	    }
	    
	    @Override
	    public void updateTyp(String name,String description,int id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.updateTyp(name, description, id);
	    }
	    
	    @Override
	    public void updateTop(String name,String description,int id) throws SkillServiceException {
	    	Service1 serv=new Service1();
	    	Service1Soap soap=serv.getService1Soap();
	    	soap.updateTop(name, description, id);
	    }
	    
	    
}
