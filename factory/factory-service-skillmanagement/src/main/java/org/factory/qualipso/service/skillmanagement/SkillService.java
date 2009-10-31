package org.factory.qualipso.service.skillmanagement;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import org.factory.qualipso.service.skillmanagement.SkillServiceException;
import org.factory.qualipso.service.skillmanagement.org.tempuri.*;
import org.qualipso.factory.FactoryService;


@Remote
@WebService(name = "SkillService", targetNamespace = "http://org.qualipso.factory.ws/service/skillmanagement")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface SkillService extends FactoryService{

	@WebMethod(operationName = "SelectTyp", action = "http://tempuri.org/SelectTyp")
    @WebResult(name = "SelectTypResult", targetNamespace = "http://tempuri.org/")
    public ArrayOfSupportSQL selectTyp() throws SkillServiceException;

	@WebMethod(operationName = "SelectTypTrue", action = "http://tempuri.org/SelectTypTrue")
    @WebResult(name = "SelectTypTrueResult", targetNamespace = "http://tempuri.org/")
    public ArrayOfSupportSQL selectTypTrue() throws SkillServiceException;

	@WebMethod(operationName = "SelectTop", action = "http://tempuri.org/SelectTop")
    @WebResult(name = "SelectTopResult", targetNamespace = "http://tempuri.org/")
    public ArrayOfSupportSQLTop selectTop() throws SkillServiceException;
	
	@WebMethod(operationName = "SelectTopSkill", action = "http://tempuri.org/SelectTopSkill")
    @WebResult(name = "SelectTopSkillResult", targetNamespace = "http://tempuri.org/")
    public ArrayOfSupportSQLTop selectTopSkill() throws SkillServiceException;
	
	@WebMethod(operationName = "SelectComp", action = "http://tempuri.org/SelectComp")
    @WebResult(name = "SelectCompResult", targetNamespace = "http://tempuri.org/")
    public ArrayOfSupportSQLComp selectComp() throws SkillServiceException;
	
	@WebMethod(operationName = "SelectTopComp", action = "http://tempuri.org/SelectTopComp")
    @WebResult(name = "SelectTopCompResult", targetNamespace = "http://tempuri.org/")
    public ArrayOfSupportSQLCompTop selectTopComp(int id) throws SkillServiceException;
	
	@WebMethod(operationName = "SelectLev", action = "http://tempuri.org/SelectLev")
    @WebResult(name = "SelectLevResult", targetNamespace = "http://tempuri.org/")
    public ArrayOfSupportSQLLev selectLev() throws SkillServiceException;
	
	@WebMethod(operationName = "SelectMyUser", action = "http://tempuri.org/SelectMyUser")
    @WebResult(name = "SelectMyUserResult", targetNamespace = "http://tempuri.org/")
    public ArrayOfSupportSQLUser selectMyUser(String id) throws SkillServiceException;
	
	 @WebMethod(operationName = "SearchAllUser", action = "http://tempuri.org/SearchAllUser")
	 @WebResult(name = "SearchAllUserResult", targetNamespace = "http://tempuri.org/")
	 public ArrayOfSupportSQLUser searchAllUser(String id) throws SkillServiceException;
	 
	 @WebMethod(operationName = "SearchTypUser", action = "http://tempuri.org/SearchTypUser")
	 @WebResult(name = "SearchTypUserResult", targetNamespace = "http://tempuri.org/")
	 public ArrayOfSupportSQLUser searchTypUser(String id,int idtyp) throws SkillServiceException;
	 
	 @WebMethod(operationName = "SearchTopUser", action = "http://tempuri.org/SearchTopUser")
	 @WebResult(name = "SearchTopUserResult", targetNamespace = "http://tempuri.org/")
	 public ArrayOfSupportSQLUser searchTopUser(String id,int idtop) throws SkillServiceException;
	 
	 @WebMethod(operationName = "SearchCompUser", action = "http://tempuri.org/SearchCompUser")
	 @WebResult(name = "SearchCompUserResult", targetNamespace = "http://tempuri.org/")
	 public ArrayOfSupportSQLUserComp searchCompUser(int id) throws SkillServiceException;
	 
	 @WebMethod(operationName = "SearchGapTopic", action = "http://tempuri.org/SearchGapTopic")
	 @WebResult(name = "SearchGapTopicResult", targetNamespace = "http://tempuri.org/")
	 public ArrayOfSupportSQLGapTopic searchGapTopic(int id,String iduser) throws SkillServiceException;
	 
	 @WebMethod(operationName = "DisableTyp", action = "http://tempuri.org/DisableTyp")
	 public void disableTyp(int id) throws SkillServiceException;
	 
	 @WebMethod(operationName = "DisableTop", action = "http://tempuri.org/DisableTop")
	 public void disableTop(int id) throws SkillServiceException;
	 
	 @WebMethod(operationName = "InsertTyp", action = "http://tempuri.org/InsertTyp")
	 public void insertTyp(String name,String description) throws SkillServiceException;
	 
	 @WebMethod(operationName = "InsertTop", action = "http://tempuri.org/InsertTop")
	 public void insertTop(String name,String description,int idTypology,int flag) throws SkillServiceException;
	 
	 @WebMethod(operationName = "InsertCompTop", action = "http://tempuri.org/InsertCompTop")
	 public void insertCompTop(int idC,int idT,int order) throws SkillServiceException;
	 
	 @WebMethod(operationName = "InsertComp", action = "http://tempuri.org/InsertComp")
	 public void insertComp(String name,String description) throws SkillServiceException;
	 
	 @WebMethod(operationName = "InsertMySkill", action = "http://tempuri.org/InsertMySkill")
	 public void insertMySkill(String iduser,int idtopic,int idlevel,String descr) throws SkillServiceException;
	 
	 @WebMethod(operationName = "DeleteTyp", action = "http://tempuri.org/DeleteTyp")
	 public void deleteTyp(int id) throws SkillServiceException;
	 
	 @WebMethod(operationName = "DeleteTop", action = "http://tempuri.org/DeleteTop")
	 public void deleteTop(int id) throws SkillServiceException;
	 
	 @WebMethod(operationName = "DeleteTopComp", action = "http://tempuri.org/DeleteTopComp")
	 public void deleteTopComp(int id,int idtop) throws SkillServiceException;
	 
	 @WebMethod(operationName = "DeleteMySkill", action = "http://tempuri.org/DeleteMySkill")
	 public void deleteMySkill(int id,String iduser) throws SkillServiceException;
	 
	 @WebMethod(operationName = "DeleteCompWithTopic", action = "http://tempuri.org/DeleteCompWithTopic")
	 public void deleteCompWithTopic(int id) throws SkillServiceException;
	 
	 @WebMethod(operationName = "EnableTyp", action = "http://tempuri.org/EnableTyp")
	 public void enableTyp(int id) throws SkillServiceException;

	 @WebMethod(operationName = "EnableAll", action = "http://tempuri.org/EnableAll")
	 public void enableAll(int id) throws SkillServiceException;

	 @WebMethod(operationName = "EnableTop", action = "http://tempuri.org/EnableTop")
	 public void enableTop( int id) throws SkillServiceException;
	 
	 @WebMethod(operationName = "ControlUserTyp", action = "http://tempuri.org/ControlUserTyp")
	@WebResult(name = "ControlUserTypResult", targetNamespace = "http://tempuri.org/")
	 public int controlUserTyp(int id) throws SkillServiceException;

	 @WebMethod(operationName = "ControlTopEnable", action = "http://tempuri.org/ControlTopEnable")
	 @WebResult(name = "ControlTopEnableResult", targetNamespace = "http://tempuri.org/")
	 public int controlTopEnable(int id) throws SkillServiceException;

	 @WebMethod(operationName = "ControlNameTopic", action = "http://tempuri.org/ControlNameTopic")
	 @WebResult(name = "ControlNameTopicResult", targetNamespace = "http://tempuri.org/")
	 public int controlNameTopic(String name) throws SkillServiceException;

	 @WebMethod(operationName = "ControlNameTypology", action = "http://tempuri.org/ControlNameTypology")
	 @WebResult(name = "ControlNameTypologyResult", targetNamespace = "http://tempuri.org/")
	 public int controlNameTypology(String name) throws SkillServiceException;

	 @WebMethod(operationName = "ControlNameCompetence", action = "http://tempuri.org/ControlNameCompetence")
	 @WebResult(name = "ControlNameCompetenceResult", targetNamespace = "http://tempuri.org/")
	 public int controlNameCompetence(String name) throws SkillServiceException;

	 @WebMethod(operationName = "ControlAddTopicCompetence", action = "http://tempuri.org/ControlAddTopicCompetence")
	 @WebResult(name = "ControlAddTopicCompetenceResult", targetNamespace = "http://tempuri.org/")
	 public int controlAddTopicCompetence(int idcomp,int idtop) throws SkillServiceException;
	 
	 @WebMethod(operationName = "ControlUserTop", action = "http://tempuri.org/ControlUserTop")
	 @WebResult(name = "ControlUserTopResult", targetNamespace = "http://tempuri.org/")
	 public int controlUserTop(int id) throws SkillServiceException;

	 @WebMethod(operationName = "ControlAdmin", action = "http://tempuri.org/ControlAdmin")
	 @WebResult(name = "ControlAdminResult", targetNamespace = "http://tempuri.org/")
	 public int controlAdmin(String iduser) throws SkillServiceException;

	 @WebMethod(operationName = "ControlSkill", action = "http://tempuri.org/ControlSkill")
	 @WebResult(name = "ControlSkillResult", targetNamespace = "http://tempuri.org/")
	 public int controlSkill(String iduser,int idtop) throws SkillServiceException;

	 @WebMethod(operationName = "UpdateTyp", action = "http://tempuri.org/UpdateTyp")
	 public void updateTyp(String name,String description,int id) throws SkillServiceException;

	 @WebMethod(operationName = "UpdateTop", action = "http://tempuri.org/UpdateTop")
	 public void updateTop(String name,String description,int id) throws SkillServiceException;
	 
	 
	 
}
