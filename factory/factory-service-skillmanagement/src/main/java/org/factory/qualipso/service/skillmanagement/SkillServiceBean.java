package org.factory.qualipso.service.skillmanagement;

import javax.ejb.Stateless;
import org.factory.qualipso.service.skillmanagement.SkillService;
import org.factory.qualipso.service.tempuri.*;

import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryResource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;


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
    public ArrayOfSupportSQLUser selectMyUser(int id,String iduser) throws SkillServiceException
    {
    	Service1 serv=new Service1();
    	return serv.getService1Soap().selectMyUser(iduser);
    }
    
    @Override
    public int controlAdmin (String iduser) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	return ser.getService1Soap().controlAdmin(iduser);
    }
            
    @Override
    public int controlNameTopic(String name) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	return ser.getService1Soap().controlNameTopic(name);
    }
    
    @Override
    public int controlUserTyp(int id) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	return ser.getService1Soap().controlUserTyp(id);
    }
    
    @Override
    public int controlTopEnable(int id) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	return ser.getService1Soap().controlTopEnable(id);
    }
    
    @Override
    public int controlNameTypology(String name) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	return ser.getService1Soap().controlNameTypology(name);
    }

    @Override
    public int controlNameCompetence(String name) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	return ser.getService1Soap().controlNameCompetence(name);
    }
    
    @Override
    public int controlAddTopicCompetence(int idcomp,int idtop) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	return ser.getService1Soap().controlAddTopicCompetence(idcomp, idtop);
    }
    
    @Override
    public int controlUserTop(int id) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	return ser.getService1Soap().controlUserTop(id);
    }
    
    @Override
    public int controlSkill(String iduser,int idtop) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	return ser.getService1Soap().controlSkill(iduser, idtop);
    }
    
    @Override
    public void disableTyp(int id) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().disableTyp(id);
    }
    
    @Override
    public void disableTop(int id) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().disableTop(id);
    }
    
    @Override
    public void insertTyp(String name, String description) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().insertTyp(name, description);
    }
    
    @Override
    public void insertTop(String name, String description, int idTypology, String flag) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().insertTop(name, description, idTypology, flag);
    }
    
    @Override
    public void insertCompTop(int idC, int idT, int order) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().insertCompTop(idC, idT, order);
    }
    
    @Override
    public void insertComp(String name, String description) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().insertComp(name, description);
    }
    
    @Override
    public void insertMySkill(String iduser, int idtopic, int idlevel, String descr) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().insertMySkill(iduser, idtopic, idlevel, descr);
    }
    
    @Override
    public void deleteTyp(int id) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().deleteTyp(id);
    }
    
    @Override
    public void deleteTop(int id) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().deleteTop(id);
    }
    
    @Override
    public void deleteTopComp(int id, int idtop) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().deleteTopComp(id, idtop);
    }
    
    @Override
    public void deleteMySkill(int id, String iduser) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().deleteMySkill(id, iduser);
    }
    
    @Override
    public void deleteCompWithTopic(int id) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().deleteCompWithTopic(id);
    }
    
    @Override
    public void enableTyp(int id) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().enableTyp(id);
    }
    
    @Override
    public void enableAll(int id) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().enableAll(id);
    }
    
    @Override
    public void enableTop(int id) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().enableTop(id);
    }
    
    @Override
    public void updateTyp(String name, String description, int id) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().updateTyp(name, description, id);
    }
    
    @Override
    public void updateTop(String name, String description, int id) throws SkillServiceException
    {
    	Service1 ser=new Service1();
    	ser.getService1Soap().updateTop(name, description, id);
    }
    
    @Override
    public ArrayOfSupportSQL selectTyp() throws SkillServiceException
    {
    	Service1 serv=new Service1();
    	return serv.getService1Soap().selectTyp();
    }
    
    @Override
    public ArrayOfSupportSQL selectTypTrue() throws SkillServiceException
    {
    	Service1 serv=new Service1();
    	return serv.getService1Soap().selectTypTrue();
    }
    
    @Override
    public ArrayOfSupportSQLTop selectTop() throws SkillServiceException
    {
    	Service1 serv=new Service1();
    	return serv.getService1Soap().selectTop();
    }
    
    @Override
    public ArrayOfSupportSQLTop selectTopSkill() throws SkillServiceException
    {
    	Service1 serv=new Service1();
    	return serv.getService1Soap().selectTopSkill();
    }
    
    @Override
    public ArrayOfSupportSQLComp selectComp() throws SkillServiceException
    {
    	Service1 serv=new Service1();
    	return serv.getService1Soap().selectComp();
    }
    
    @Override
    public ArrayOfSupportSQLCompTop selectTopComp(int id) throws SkillServiceException
    {
    	Service1 serv=new Service1();
    	return serv.getService1Soap().selectTopComp(id);
    }
    
    @Override
    public ArrayOfSupportSQLLev selectLev() throws SkillServiceException
    {
    	Service1 serv=new Service1();
    	return serv.getService1Soap().selectLev();
    }

	   
    @Override
    public ArrayOfSupportSQLGapTopic searchGapTopic(int id,String iduser) throws SkillServiceException
    {
    	Service1 serv=new Service1();
    	return serv.getService1Soap().searchGapTopic(id, iduser);
    }
    
    @Override
    public ArrayOfSupportSQLUserComp searchCompUser(int id) throws SkillServiceException
    {
    	Service1 serv=new Service1();
    	return serv.getService1Soap().searchCompUser(id);
    }
    
    @Override
    public ArrayOfSupportSQLUser searchAllUser(int id,String iduser) throws SkillServiceException
    {
    	Service1 serv=new Service1();
    	return serv.getService1Soap().searchAllUser(iduser);
    }
    
    @Override
    public ArrayOfSupportSQLUser searchTypUser(int id,String iduser,int idtyp) throws SkillServiceException
    {
    	Service1 serv=new Service1();
    	return serv.getService1Soap().searchTypUser(iduser, idtyp);
    }
    
    @Override
    public ArrayOfSupportSQLUser searchTopUser(int id,String iduser,int idtop) throws SkillServiceException
    {
    	Service1 serv=new Service1();
    	return serv.getService1Soap().searchTopUser(iduser, idtop);
    }
}
