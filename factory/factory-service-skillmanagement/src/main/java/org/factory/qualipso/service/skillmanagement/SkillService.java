package org.factory.qualipso.service.skillmanagement;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


import org.factory.qualipso.service.tempuri.*;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.FactoryNamingConvention;


@Remote
@WebService(name = SkillService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + SkillService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface SkillService extends FactoryService {
	
	public static final String SERVICE_NAME = "skill";
	
	
	@WebMethod
	@WebResult(name="ArrayOfSupportSQLUser")
	public ArrayOfSupportSQLUser selectMyUser(int id,String iduser) throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="int")
   	public int controlAdmin(String iduser) throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="int")
	public int controlNameTopic(String name) throws SkillServiceException;

	@WebMethod
	@WebResult(name="int")
	public int controlUserTyp(int id) throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="int")
	public int controlTopEnable(int id) throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="int")
	public int controlNameTypology(String name) throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="int")
	public int controlNameCompetence(String name) throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="int")
	public int controlAddTopicCompetence(int idcomp,int idtop) throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="int")
	public int controlUserTop(int id) throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="int")
	public int controlSkill(String iduser,int idtop) throws SkillServiceException;
	
	@WebMethod
	public void disableTyp(int id) throws SkillServiceException;
	
	@WebMethod
	public void disableTop(int id) throws SkillServiceException;
	
	@WebMethod
	public void insertTyp(String name,String description) throws SkillServiceException;
	
	@WebMethod
	public void insertTop(String name, String description, int idTypology, String flag) throws SkillServiceException;
	
	@WebMethod
	public void insertCompTop(int idC, int idT, int order) throws SkillServiceException;
	
	@WebMethod
	public void insertComp(String name, String description) throws SkillServiceException;
	
	@WebMethod
	public void insertMySkill(String iduser, int idtopic, int idlevel, String descr) throws SkillServiceException;
	
	@WebMethod
	public void deleteTyp(int id) throws SkillServiceException;
	
	@WebMethod
	public void deleteTop(int id) throws SkillServiceException;
	
	@WebMethod
	public void deleteTopComp(int id, int idtop) throws SkillServiceException;
	
	@WebMethod
	public void deleteMySkill(int id, String iduser) throws SkillServiceException;
	
	@WebMethod
	public void deleteCompWithTopic(int id) throws SkillServiceException;
	
	@WebMethod
	public void enableTyp(int id) throws SkillServiceException;
	
	@WebMethod
	public void enableAll(int id) throws SkillServiceException;
	
	@WebMethod
	public void enableTop(int id) throws SkillServiceException;
	
	@WebMethod
	public void updateTyp(String name, String description, int id) throws SkillServiceException;
	
	@WebMethod
	public void updateTop(String name, String description, int id) throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="ArrayOfSupportSQL")
	public ArrayOfSupportSQL selectTyp() throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="ArrayOfSupportSQL")
	public ArrayOfSupportSQL selectTypTrue() throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="ArrayOfSupportSQLTop")
	public ArrayOfSupportSQLTop selectTop() throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="ArrayOfSupportSQLTop")
	public ArrayOfSupportSQLTop selectTopSkill() throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="ArrayOfSupportSQLComp")
	public ArrayOfSupportSQLComp selectComp() throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="ArrayOfSupportSQLCompTop")
	public ArrayOfSupportSQLCompTop selectTopComp(int id) throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="ArrayOfSupportSQLLev")
	public ArrayOfSupportSQLLev selectLev() throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="ArrayOfSupportSQLGapTopic")
	public ArrayOfSupportSQLGapTopic searchGapTopic(int id,String iduser) throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="ArrayOfSupportSQLUserComp")
	public ArrayOfSupportSQLUserComp searchCompUser(int id) throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="ArrayOfSupportSQLUser")
	public ArrayOfSupportSQLUser searchAllUser(int id,String iduser) throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="ArrayOfSupportSQLUser")
	public ArrayOfSupportSQLUser searchTypUser(int id,String iduser,int idtyp) throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="ArrayOfSupportSQLUser")
	public ArrayOfSupportSQLUser searchTopUser(int id,String iduser,int idtop) throws SkillServiceException;
	
	@WebMethod
	@WebResult(name="String")
	public String getProfilesPath() throws SkillServiceException;
	
	
}