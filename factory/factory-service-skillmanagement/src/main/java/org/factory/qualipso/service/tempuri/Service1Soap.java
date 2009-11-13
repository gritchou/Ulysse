
package org.factory.qualipso.service.tempuri;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "Service1Soap", targetNamespace = "http://tempuri.org/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface Service1Soap {


    /**
     * 
     * @return
     *     returns org.factory.qualipso.service.tempuri.ArrayOfSupportSQL
     */
    @WebMethod(operationName = "SelectTyp", action = "http://tempuri.org/SelectTyp")
    @WebResult(name = "SelectTypResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SelectTyp", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SelectTyp")
    @ResponseWrapper(localName = "SelectTypResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SelectTypResponse")
    public ArrayOfSupportSQL selectTyp();

    /**
     * 
     * @return
     *     returns org.factory.qualipso.service.tempuri.ArrayOfSupportSQL
     */
    @WebMethod(operationName = "SelectTypTrue", action = "http://tempuri.org/SelectTypTrue")
    @WebResult(name = "SelectTypTrueResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SelectTypTrue", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SelectTypTrue")
    @ResponseWrapper(localName = "SelectTypTrueResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SelectTypTrueResponse")
    public ArrayOfSupportSQL selectTypTrue();

    /**
     * 
     * @return
     *     returns org.factory.qualipso.service.tempuri.ArrayOfSupportSQLTop
     */
    @WebMethod(operationName = "SelectTop", action = "http://tempuri.org/SelectTop")
    @WebResult(name = "SelectTopResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SelectTop", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SelectTop")
    @ResponseWrapper(localName = "SelectTopResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SelectTopResponse")
    public ArrayOfSupportSQLTop selectTop();

    /**
     * 
     * @return
     *     returns org.factory.qualipso.service.tempuri.ArrayOfSupportSQLTop
     */
    @WebMethod(operationName = "SelectTopSkill", action = "http://tempuri.org/SelectTopSkill")
    @WebResult(name = "SelectTopSkillResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SelectTopSkill", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SelectTopSkill")
    @ResponseWrapper(localName = "SelectTopSkillResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SelectTopSkillResponse")
    public ArrayOfSupportSQLTop selectTopSkill();

    /**
     * 
     * @return
     *     returns org.factory.qualipso.service.tempuri.ArrayOfSupportSQLComp
     */
    @WebMethod(operationName = "SelectComp", action = "http://tempuri.org/SelectComp")
    @WebResult(name = "SelectCompResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SelectComp", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SelectComp")
    @ResponseWrapper(localName = "SelectCompResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SelectCompResponse")
    public ArrayOfSupportSQLComp selectComp();

    /**
     * 
     * @param id
     * @return
     *     returns org.factory.qualipso.service.tempuri.ArrayOfSupportSQLCompTop
     */
    @WebMethod(operationName = "SelectTopComp", action = "http://tempuri.org/SelectTopComp")
    @WebResult(name = "SelectTopCompResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SelectTopComp", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SelectTopComp")
    @ResponseWrapper(localName = "SelectTopCompResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SelectTopCompResponse")
    public ArrayOfSupportSQLCompTop selectTopComp(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id);

    /**
     * 
     * @return
     *     returns org.factory.qualipso.service.tempuri.ArrayOfSupportSQLLev
     */
    @WebMethod(operationName = "SelectLev", action = "http://tempuri.org/SelectLev")
    @WebResult(name = "SelectLevResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SelectLev", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SelectLev")
    @ResponseWrapper(localName = "SelectLevResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SelectLevResponse")
    public ArrayOfSupportSQLLev selectLev();

    /**
     * 
     * @param iduser
     * @return
     *     returns org.factory.qualipso.service.tempuri.ArrayOfSupportSQLUser
     */
    @WebMethod(operationName = "SelectMyUser", action = "http://tempuri.org/SelectMyUser")
    @WebResult(name = "SelectMyUserResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SelectMyUser", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SelectMyUser")
    @ResponseWrapper(localName = "SelectMyUserResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SelectMyUserResponse")
    public ArrayOfSupportSQLUser selectMyUser(
        @WebParam(name = "iduser", targetNamespace = "http://tempuri.org/")
        String iduser);

    /**
     * 
     * @param iduser
     * @return
     *     returns org.factory.qualipso.service.tempuri.ArrayOfSupportSQLUser
     */
    @WebMethod(operationName = "SearchAllUser", action = "http://tempuri.org/SearchAllUser")
    @WebResult(name = "SearchAllUserResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SearchAllUser", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SearchAllUser")
    @ResponseWrapper(localName = "SearchAllUserResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SearchAllUserResponse")
    public ArrayOfSupportSQLUser searchAllUser(
        @WebParam(name = "iduser", targetNamespace = "http://tempuri.org/")
        String iduser);

    /**
     * 
     * @param iduser
     * @param idtyp
     * @return
     *     returns org.factory.qualipso.service.tempuri.ArrayOfSupportSQLUser
     */
    @WebMethod(operationName = "SearchTypUser", action = "http://tempuri.org/SearchTypUser")
    @WebResult(name = "SearchTypUserResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SearchTypUser", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SearchTypUser")
    @ResponseWrapper(localName = "SearchTypUserResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SearchTypUserResponse")
    public ArrayOfSupportSQLUser searchTypUser(
        @WebParam(name = "iduser", targetNamespace = "http://tempuri.org/")
        String iduser,
        @WebParam(name = "idtyp", targetNamespace = "http://tempuri.org/")
        int idtyp);

    /**
     * 
     * @param iduser
     * @param idtop
     * @return
     *     returns org.factory.qualipso.service.tempuri.ArrayOfSupportSQLUser
     */
    @WebMethod(operationName = "SearchTopUser", action = "http://tempuri.org/SearchTopUser")
    @WebResult(name = "SearchTopUserResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SearchTopUser", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SearchTopUser")
    @ResponseWrapper(localName = "SearchTopUserResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SearchTopUserResponse")
    public ArrayOfSupportSQLUser searchTopUser(
        @WebParam(name = "iduser", targetNamespace = "http://tempuri.org/")
        String iduser,
        @WebParam(name = "idtop", targetNamespace = "http://tempuri.org/")
        int idtop);

    /**
     * 
     * @param id
     * @return
     *     returns org.factory.qualipso.service.tempuri.ArrayOfSupportSQLUserComp
     */
    @WebMethod(operationName = "SearchCompUser", action = "http://tempuri.org/SearchCompUser")
    @WebResult(name = "SearchCompUserResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SearchCompUser", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SearchCompUser")
    @ResponseWrapper(localName = "SearchCompUserResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SearchCompUserResponse")
    public ArrayOfSupportSQLUserComp searchCompUser(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id);

    /**
     * 
     * @param iduser
     * @param id
     * @return
     *     returns org.factory.qualipso.service.tempuri.ArrayOfSupportSQLGapTopic
     */
    @WebMethod(operationName = "SearchGapTopic", action = "http://tempuri.org/SearchGapTopic")
    @WebResult(name = "SearchGapTopicResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "SearchGapTopic", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SearchGapTopic")
    @ResponseWrapper(localName = "SearchGapTopicResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.SearchGapTopicResponse")
    public ArrayOfSupportSQLGapTopic searchGapTopic(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id,
        @WebParam(name = "iduser", targetNamespace = "http://tempuri.org/")
        String iduser);

    /**
     * 
     * @param id
     */
    @WebMethod(operationName = "DisableTyp", action = "http://tempuri.org/DisableTyp")
    @RequestWrapper(localName = "DisableTyp", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.DisableTyp")
    @ResponseWrapper(localName = "DisableTypResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.DisableTypResponse")
    public void disableTyp(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id);

    /**
     * 
     * @param id
     */
    @WebMethod(operationName = "DisableTop", action = "http://tempuri.org/DisableTop")
    @RequestWrapper(localName = "DisableTop", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.DisableTop")
    @ResponseWrapper(localName = "DisableTopResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.DisableTopResponse")
    public void disableTop(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id);

    /**
     * 
     * @param description
     * @param name
     */
    @WebMethod(operationName = "InsertTyp", action = "http://tempuri.org/InsertTyp")
    @RequestWrapper(localName = "InsertTyp", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.InsertTyp")
    @ResponseWrapper(localName = "InsertTypResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.InsertTypResponse")
    public void insertTyp(
        @WebParam(name = "name", targetNamespace = "http://tempuri.org/")
        String name,
        @WebParam(name = "description", targetNamespace = "http://tempuri.org/")
        String description);

    /**
     * 
     * @param flag
     * @param description
     * @param name
     * @param idTypology
     */
    @WebMethod(operationName = "InsertTop", action = "http://tempuri.org/InsertTop")
    @RequestWrapper(localName = "InsertTop", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.InsertTop")
    @ResponseWrapper(localName = "InsertTopResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.InsertTopResponse")
    public void insertTop(
        @WebParam(name = "name", targetNamespace = "http://tempuri.org/")
        String name,
        @WebParam(name = "description", targetNamespace = "http://tempuri.org/")
        String description,
        @WebParam(name = "id_typology", targetNamespace = "http://tempuri.org/")
        int idTypology,
        @WebParam(name = "flag", targetNamespace = "http://tempuri.org/")
        String flag);

    /**
     * 
     * @param order
     * @param idT
     * @param idC
     */
    @WebMethod(operationName = "InsertCompTop", action = "http://tempuri.org/InsertCompTop")
    @RequestWrapper(localName = "InsertCompTop", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.InsertCompTop")
    @ResponseWrapper(localName = "InsertCompTopResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.InsertCompTopResponse")
    public void insertCompTop(
        @WebParam(name = "id_c", targetNamespace = "http://tempuri.org/")
        int idC,
        @WebParam(name = "id_t", targetNamespace = "http://tempuri.org/")
        int idT,
        @WebParam(name = "order", targetNamespace = "http://tempuri.org/")
        int order);

    /**
     * 
     * @param description
     * @param name
     */
    @WebMethod(operationName = "InsertComp", action = "http://tempuri.org/InsertComp")
    @RequestWrapper(localName = "InsertComp", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.InsertComp")
    @ResponseWrapper(localName = "InsertCompResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.InsertCompResponse")
    public void insertComp(
        @WebParam(name = "name", targetNamespace = "http://tempuri.org/")
        String name,
        @WebParam(name = "description", targetNamespace = "http://tempuri.org/")
        String description);

    /**
     * 
     * @param iduser
     * @param idlevel
     * @param idtopic
     * @param descr
     */
    @WebMethod(operationName = "InsertMySkill", action = "http://tempuri.org/InsertMySkill")
    @RequestWrapper(localName = "InsertMySkill", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.InsertMySkill")
    @ResponseWrapper(localName = "InsertMySkillResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.InsertMySkillResponse")
    public void insertMySkill(
        @WebParam(name = "iduser", targetNamespace = "http://tempuri.org/")
        String iduser,
        @WebParam(name = "idtopic", targetNamespace = "http://tempuri.org/")
        int idtopic,
        @WebParam(name = "idlevel", targetNamespace = "http://tempuri.org/")
        int idlevel,
        @WebParam(name = "descr", targetNamespace = "http://tempuri.org/")
        String descr);

    /**
     * 
     * @param id
     */
    @WebMethod(operationName = "DeleteTyp", action = "http://tempuri.org/DeleteTyp")
    @RequestWrapper(localName = "DeleteTyp", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.DeleteTyp")
    @ResponseWrapper(localName = "DeleteTypResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.DeleteTypResponse")
    public void deleteTyp(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id);

    /**
     * 
     * @param id
     */
    @WebMethod(operationName = "DeleteTop", action = "http://tempuri.org/DeleteTop")
    @RequestWrapper(localName = "DeleteTop", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.DeleteTop")
    @ResponseWrapper(localName = "DeleteTopResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.DeleteTopResponse")
    public void deleteTop(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id);

    /**
     * 
     * @param id
     * @param idtop
     */
    @WebMethod(operationName = "DeleteTopComp", action = "http://tempuri.org/DeleteTopComp")
    @RequestWrapper(localName = "DeleteTopComp", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.DeleteTopComp")
    @ResponseWrapper(localName = "DeleteTopCompResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.DeleteTopCompResponse")
    public void deleteTopComp(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id,
        @WebParam(name = "idtop", targetNamespace = "http://tempuri.org/")
        int idtop);

    /**
     * 
     * @param iduser
     * @param id
     */
    @WebMethod(operationName = "DeleteMySkill", action = "http://tempuri.org/DeleteMySkill")
    @RequestWrapper(localName = "DeleteMySkill", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.DeleteMySkill")
    @ResponseWrapper(localName = "DeleteMySkillResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.DeleteMySkillResponse")
    public void deleteMySkill(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id,
        @WebParam(name = "iduser", targetNamespace = "http://tempuri.org/")
        String iduser);

    /**
     * 
     * @param id
     */
    @WebMethod(operationName = "DeleteCompWithTopic", action = "http://tempuri.org/DeleteCompWithTopic")
    @RequestWrapper(localName = "DeleteCompWithTopic", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.DeleteCompWithTopic")
    @ResponseWrapper(localName = "DeleteCompWithTopicResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.DeleteCompWithTopicResponse")
    public void deleteCompWithTopic(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id);

    /**
     * 
     * @param id
     */
    @WebMethod(operationName = "EnableTyp", action = "http://tempuri.org/EnableTyp")
    @RequestWrapper(localName = "EnableTyp", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.EnableTyp")
    @ResponseWrapper(localName = "EnableTypResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.EnableTypResponse")
    public void enableTyp(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id);

    /**
     * 
     * @param id
     */
    @WebMethod(operationName = "EnableAll", action = "http://tempuri.org/EnableAll")
    @RequestWrapper(localName = "EnableAll", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.EnableAll")
    @ResponseWrapper(localName = "EnableAllResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.EnableAllResponse")
    public void enableAll(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id);

    /**
     * 
     * @param id
     */
    @WebMethod(operationName = "EnableTop", action = "http://tempuri.org/EnableTop")
    @RequestWrapper(localName = "EnableTop", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.EnableTop")
    @ResponseWrapper(localName = "EnableTopResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.EnableTopResponse")
    public void enableTop(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id);

    /**
     * 
     * @param id
     * @return
     *     returns int
     */
    @WebMethod(operationName = "ControlUserTyp", action = "http://tempuri.org/ControlUserTyp")
    @WebResult(name = "ControlUserTypResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "ControlUserTyp", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlUserTyp")
    @ResponseWrapper(localName = "ControlUserTypResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlUserTypResponse")
    public int controlUserTyp(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id);

    /**
     * 
     * @param id
     * @return
     *     returns int
     */
    @WebMethod(operationName = "ControlTopEnable", action = "http://tempuri.org/ControlTopEnable")
    @WebResult(name = "ControlTopEnableResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "ControlTopEnable", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlTopEnable")
    @ResponseWrapper(localName = "ControlTopEnableResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlTopEnableResponse")
    public int controlTopEnable(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id);

    /**
     * 
     * @param name
     * @return
     *     returns int
     */
    @WebMethod(operationName = "ControlNameTopic", action = "http://tempuri.org/ControlNameTopic")
    @WebResult(name = "ControlNameTopicResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "ControlNameTopic", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlNameTopic")
    @ResponseWrapper(localName = "ControlNameTopicResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlNameTopicResponse")
    public int controlNameTopic(
        @WebParam(name = "name", targetNamespace = "http://tempuri.org/")
        String name);

    /**
     * 
     * @param name
     * @return
     *     returns int
     */
    @WebMethod(operationName = "ControlNameTypology", action = "http://tempuri.org/ControlNameTypology")
    @WebResult(name = "ControlNameTypologyResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "ControlNameTypology", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlNameTypology")
    @ResponseWrapper(localName = "ControlNameTypologyResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlNameTypologyResponse")
    public int controlNameTypology(
        @WebParam(name = "name", targetNamespace = "http://tempuri.org/")
        String name);

    /**
     * 
     * @param name
     * @return
     *     returns int
     */
    @WebMethod(operationName = "ControlNameCompetence", action = "http://tempuri.org/ControlNameCompetence")
    @WebResult(name = "ControlNameCompetenceResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "ControlNameCompetence", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlNameCompetence")
    @ResponseWrapper(localName = "ControlNameCompetenceResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlNameCompetenceResponse")
    public int controlNameCompetence(
        @WebParam(name = "name", targetNamespace = "http://tempuri.org/")
        String name);

    /**
     * 
     * @param idcomp
     * @param idtop
     * @return
     *     returns int
     */
    @WebMethod(operationName = "ControlAddTopicCompetence", action = "http://tempuri.org/ControlAddTopicCompetence")
    @WebResult(name = "ControlAddTopicCompetenceResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "ControlAddTopicCompetence", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlAddTopicCompetence")
    @ResponseWrapper(localName = "ControlAddTopicCompetenceResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlAddTopicCompetenceResponse")
    public int controlAddTopicCompetence(
        @WebParam(name = "idcomp", targetNamespace = "http://tempuri.org/")
        int idcomp,
        @WebParam(name = "idtop", targetNamespace = "http://tempuri.org/")
        int idtop);

    /**
     * 
     * @param id
     * @return
     *     returns int
     */
    @WebMethod(operationName = "ControlUserTop", action = "http://tempuri.org/ControlUserTop")
    @WebResult(name = "ControlUserTopResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "ControlUserTop", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlUserTop")
    @ResponseWrapper(localName = "ControlUserTopResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlUserTopResponse")
    public int controlUserTop(
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id);

    /**
     * 
     * @param iduser
     * @return
     *     returns int
     */
    @WebMethod(operationName = "ControlAdmin", action = "http://tempuri.org/ControlAdmin")
    @WebResult(name = "ControlAdminResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "ControlAdmin", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlAdmin")
    @ResponseWrapper(localName = "ControlAdminResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlAdminResponse")
    public int controlAdmin(
        @WebParam(name = "iduser", targetNamespace = "http://tempuri.org/")
        String iduser);

    /**
     * 
     * @param iduser
     * @param idtop
     * @return
     *     returns int
     */
    @WebMethod(operationName = "ControlSkill", action = "http://tempuri.org/ControlSkill")
    @WebResult(name = "ControlSkillResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "ControlSkill", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlSkill")
    @ResponseWrapper(localName = "ControlSkillResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.ControlSkillResponse")
    public int controlSkill(
        @WebParam(name = "iduser", targetNamespace = "http://tempuri.org/")
        String iduser,
        @WebParam(name = "idtop", targetNamespace = "http://tempuri.org/")
        int idtop);

    /**
     * 
     * @param id
     * @param description
     * @param name
     */
    @WebMethod(operationName = "UpdateTyp", action = "http://tempuri.org/UpdateTyp")
    @RequestWrapper(localName = "UpdateTyp", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.UpdateTyp")
    @ResponseWrapper(localName = "UpdateTypResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.UpdateTypResponse")
    public void updateTyp(
        @WebParam(name = "name", targetNamespace = "http://tempuri.org/")
        String name,
        @WebParam(name = "description", targetNamespace = "http://tempuri.org/")
        String description,
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id);

    /**
     * 
     * @param id
     * @param description
     * @param name
     */
    @WebMethod(operationName = "UpdateTop", action = "http://tempuri.org/UpdateTop")
    @RequestWrapper(localName = "UpdateTop", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.UpdateTop")
    @ResponseWrapper(localName = "UpdateTopResponse", targetNamespace = "http://tempuri.org/", className = "org.factory.qualipso.service.tempuri.UpdateTopResponse")
    public void updateTop(
        @WebParam(name = "name", targetNamespace = "http://tempuri.org/")
        String name,
        @WebParam(name = "description", targetNamespace = "http://tempuri.org/")
        String description,
        @WebParam(name = "id", targetNamespace = "http://tempuri.org/")
        int id);

}