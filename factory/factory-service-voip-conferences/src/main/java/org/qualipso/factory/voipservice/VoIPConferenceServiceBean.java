package org.qualipso.factory.voipservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.voipservice.conference.AsteriskConferenceManagement;
import org.qualipso.factory.voipservice.conference.AsteriskConferenceSettings;
import org.qualipso.factory.voipservice.conference.AsteriskConferenceStatus;
import org.qualipso.factory.voipservice.conference.AsteriskConferences;
import org.qualipso.factory.voipservice.entity.ConferenceDetails;
import org.qualipso.factory.voipservice.entity.ParticipantsInfo;
import org.qualipso.factory.voipservice.entity.SipConf;
import org.qualipso.factory.voipservice.manager.AsteriskJavaListener;
import org.qualipso.factory.voipservice.security.AuthenticationModule;
import org.qualipso.factory.voipservice.utils.AsteriskConferenceUtils;
import org.qualipso.factory.voipservice.utils.AuthData;

/**
 * This is VoIP conference service
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */
@Stateless(name = "VoIPConference", mappedName = FactoryNamingConvention.SERVICE_PREFIX  + "VoIPConferenceService")
@WebService(endpointInterface = "org.qualipso.factory.voipservice.VoIPConferenceService", targetNamespace = "http://org.qualipso.factory.ws/service/voip", serviceName = "VoIPConferenceService", portName = "VoIPConferenceServicePort")
@WebContext(contextRoot = "/factory-service-voip", urlPattern = "/voipconference")
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class VoIPConferenceServiceBean implements VoIPConferenceService {
	private static Logger log = Logger.getLogger(VoIPConferenceServiceBean.class);
	
	private static final String SERVICE_NAME = "VoIPConferenceService";
	private static final String[] RESOURCE_TYPE_LIST = new String[]{"ParticipantsInfo" , "ConferenceDetails"};
	
	//factory variables
	private CoreService core;
	private BindingService binding;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	private MembershipService membership;
	private SessionContext ctx;
	private EntityManager em;
	private AuthData authData;
	
	//#############################################################################################
	/*
	 *  FACTORY BASE METHODS
	 */
	//#############################################################################################

	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public EntityManager getEntityManager() {
		return this.em;
	}

	@Resource
	public void setSessionContext(SessionContext ctx) {
		this.ctx = ctx;
	}

	public SessionContext getSessionContext() {
		return this.ctx;
	}

	@EJB
	public void setBindingService(BindingService binding) {
		this.binding = binding;
	}

	public BindingService getBindingService() {
		return this.binding;
	}
	
	@EJB
	public void setCoreService(CoreService core) {
		this.core = core;
	}

	public CoreService getCoreService() {
		return this.core;
	}

	@EJB
	public void setPEPService(PEPService pep) {
		this.pep = pep;
	}

	public PEPService getPEPService() {
		return this.pep;
	}

	@EJB
	public void setPAPService(PAPService pap) {
		this.pap = pap;
	}

	public PAPService getPAPService() {
		return this.pap;
	}

	@EJB
	public void setNotificationService(NotificationService notification) {
		this.notification = notification;
	}

	public NotificationService getNotificationService() {
		return this.notification;
	}

	@EJB
	public void setMembershipService(MembershipService membership) {
		this.membership = membership;
	}

	public MembershipService getMembershipService() {
		return this.membership;
	}
	
	//#############################################################################################
	/*
	 *  CONSTURCTOR
	 */
	//#############################################################################################
	/**
	 * Default constructor
	 */
	public VoIPConferenceServiceBean() throws VoIPConferenceServiceException {
		try {
		    	authData = new AuthData();
		    	authData.setCoreService(core);
			AsteriskJavaListener.getInstance();
		} catch (Exception e) {
			// noop
		}
	}
	
	//#############################################################################################
	/*
	 *  USER STATUS AND MANAGEMENT IN CONFERENCE
	 */
	//#############################################################################################
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean kickUser(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException {
		AsteriskConferenceManagement acm = new AsteriskConferenceManagement(userId, pass, authData);
		return acm.kickUser(userId, pass, confNo, userConferenceId);
	}
		
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean banUser(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException {
		AsteriskConferenceManagement acm = new AsteriskConferenceManagement(userId, pass, authData);
		boolean status = false;
		try {
			status = acm.banUser(userId, pass, confNo, userConferenceId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean unbanUser(String userId, String pass, Integer confNo, String voipUsername) throws VoIPConferenceServiceException {
		AsteriskConferenceManagement acm = new AsteriskConferenceManagement(userId, pass, authData);
		return acm.unbanUser(userId, pass, confNo, voipUsername);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean isUserBanned(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException {
		AsteriskConferenceStatus acs = new AsteriskConferenceStatus(userId, pass, authData);
		return acs.isUserBanned(userId, pass, confNo, userConferenceId);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String[] listBannedUsers(String userId, String pass, Integer confNo) throws VoIPConferenceServiceException {
		AsteriskConferenceSettings acs = new AsteriskConferenceSettings(userId, pass, authData);
		return acs.listBannedUsers(userId, confNo);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean muteUser(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException {
		AsteriskConferenceManagement acm = new AsteriskConferenceManagement(userId, pass, authData);
		return acm.muteUser(userId, pass, confNo, userConferenceId);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean unmuteUser(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException {
		AsteriskConferenceManagement acm = new AsteriskConferenceManagement(userId, pass, authData);
		return acm.unmuteUser(userId, pass, confNo, userConferenceId);
	}	
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean isUserMuted(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException {
		AsteriskConferenceStatus acs = new AsteriskConferenceStatus(userId, pass, authData);
		return acs.isUserMuted(userId, pass, confNo, userConferenceId);
	}	
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean isUserTalking(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException {
		AsteriskConferenceStatus acs = new AsteriskConferenceStatus(userId, pass, authData);
		return acs.isUserTalking(userId, pass, confNo, userConferenceId);
	}	
	
	//#############################################################################################
	/*
	 *  CONFERENCE STATUS AND MANAGEMENT
	 */
	//#############################################################################################
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean lockConf(String userId, String pass, Integer confNo) throws VoIPConferenceServiceException {
		AsteriskConferenceManagement acm = new AsteriskConferenceManagement(userId, pass, authData);
		return acm.lockConf(userId, pass, confNo);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean unlockConf(String userId, String pass, Integer confNo) throws VoIPConferenceServiceException {
		AsteriskConferenceManagement acm = new AsteriskConferenceManagement(userId, pass, authData);
		return acm.unlockConf(userId, pass, confNo);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean endConference(String userId, String pass, Integer confNo) throws VoIPConferenceServiceException {
		AsteriskConferences ac = new AsteriskConferences(userId, pass, authData);
		return ac.endConference(userId, pass, confNo);
	}
	
	//#############################################################################################
	// USER PROFILE MANAGEMENT
	//#############################################################################################
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String[] listUsers(String userId, String pass) throws VoIPConferenceServiceException {
		new AuthenticationModule(userId, pass, authData);
		return AsteriskConferenceUtils.listUsers();
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String activateVoipProfile(String userId, String pass,
			String voipUsername,
			String secret, String email, String firstname, String lastname,
			String qualipsoUserId) throws VoIPConferenceServiceException {
		new AuthenticationModule(userId, pass, authData);
		try {
			if (AuthenticationModule.isSuperUser(userId, pass)) {
				if (isVoIPProfileExists(userId, pass, voipUsername)) {return "user exists";}
				if (!usernameFromQualipsoUsers(userId, pass, qualipsoUserId).equals("")) {
					return "qualispo user exists";
				}
				String res = AsteriskConferenceUtils.createUser(voipUsername, secret, email, firstname, lastname, qualipsoUserId);
				
				return res;
			} else {
				return "not enough privileges";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return "";
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean deactivateVoipProfile(String userId, String pass, String voipUsername) throws VoIPConferenceServiceException {
		new AuthenticationModule(userId, pass, authData);

		if (AuthenticationModule.isSuperUser(userId, pass)) {
			return AsteriskConferenceUtils.removeUser(voipUsername);
		} else {
			log.error("permission denied for deactivateVoipProfile");
			return false;
		}
	}	

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean isVoIPProfileExists(String userId, String pass, String voipUsername) throws VoIPConferenceServiceException {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		Query query = em.createQuery("FROM SipConf sc WHERE sc.username = :username");
		query.setParameter("username", voipUsername);

		boolean result = true;
		try {
			if (query.getSingleResult() == null) {
				result = false;
			} else {
				result = true;
			}
		} catch (javax.persistence.NoResultException e) {
			result = false;
		}

		em.getTransaction().commit();
		em.close();
		return result;
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String loginStatus(String userId, String pass, String confNo) throws VoIPConferenceServiceException {
		boolean logged = false;
		try {
			logged = AuthenticationModule.authenticate(userId, pass, authData);
		} catch (Exception e) {
			logged = false;
		}
		if (logged) {
			return "OK";
		}
		return "FAIL";
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String getVoipUsername(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException {
		AsteriskConferenceStatus acs = new AsteriskConferenceStatus(userId, pass, authData);
		return acs.getUserName(userId, pass, confNo, userConferenceId);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String usernameFromQualipsoUsers(String userId, String pass, String voipUsername) throws VoIPConferenceServiceException {
		String result = "";
		try {
			EntityManager em = AsteriskConferenceUtils.getEntityManager();
			em.getTransaction().begin();
			
			Query query = em.createQuery("FROM SipConf sc WHERE sc.userid = :userId");
			query.setParameter("userId", voipUsername);

			if (query.getResultList() == null || query.getResultList().size() == 0) {
				result = "";
			} else {
				SipConf user = (SipConf) query.getResultList().get(0);
				result = user.getName();
			}
			em.getTransaction().commit();
			em.close();
			em = null;
		} catch (Exception e) {
			result = "";
			log.error(e.getMessage());
			e.printStackTrace();
		} 
		return result;
	}	
	//#############################################################################################
	//CONFERENCE MANAGEMENT
	//#############################################################################################
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer editConference(String userId, String pass, Integer confNo,
			String owner, short accessType, boolean permanent, String pin,
			String adminpin, Long startDate, Long endDate, Integer maxUsers,
			String name, String agenda, boolean recorded) throws VoIPConferenceServiceException {
		AsteriskConferenceSettings acs = new AsteriskConferenceSettings(userId, pass, authData);
		return acs.editConference(userId, pass, confNo, owner, accessType,
				permanent, pin, adminpin, startDate, endDate, maxUsers, name,
				agenda, recorded);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer editConferenceHR(String userId, String pass, Integer confNo,
			String owner, short accessType, boolean permanent, String pin,
			String adminpin, String startDateHR, String endDateHR, Integer maxUsers,
			String name, String agenda, boolean recorded) throws VoIPConferenceServiceException {
		AsteriskConferenceSettings acs = new AsteriskConferenceSettings(userId, pass, authData);

		SimpleDateFormat sf = (new SimpleDateFormat("yyyy-MM-dd HH:mm"));
		Long start = new Long(0);
		try {
			if (startDateHR != null && !startDateHR.equals("")) {
				start = sf.parse(startDateHR).getTime() / 1000;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Long end = new Long(0);
		try {
			if (endDateHR != null && !endDateHR.equals("")) {
				end = sf.parse(endDateHR).getTime() / 1000;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return acs.editConference(userId, pass, confNo, owner, accessType,
				permanent, pin, adminpin, start, end, maxUsers, name,
				agenda, recorded);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer createConference(String userId, String pass, String owner,
			short accessType, boolean permanent, String pin, String adminpin,
			Long startDate, Long endDate, Integer maxUsers, String name,
			String agenda, boolean recorded, String project) throws VoIPConferenceServiceException {
		AsteriskConferences ac = new AsteriskConferences(userId, pass, authData);
		return ac.createConference(userId, pass, owner, accessType, permanent,
				pin, adminpin, startDate, endDate, maxUsers, name, agenda,
				recorded, project, authData);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer createConferenceHR(String userId, String pass, String owner,
			short accessType, boolean permanent, String pin, String adminpin,
			String startDateHR, String endDateHR, Integer maxUsers,
			String name, String agenda, boolean recorded, String project) throws VoIPConferenceServiceException {
		AsteriskConferences ac = new AsteriskConferences(userId, pass, authData);

		SimpleDateFormat sf = (new SimpleDateFormat("yyyy-MM-dd HH:mm"));
		Long start = new Long(0);
		log.info("### start date HR =  " + startDateHR);
		log.info("### endDateHR =  " + endDateHR);
		
		try {
			if (startDateHR != null && !startDateHR.equals("")) {
				start = sf.parse(startDateHR).getTime() / 1000;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Long end = new Long(0);
		try {
			if (endDateHR != null && !endDateHR.equals("")) {
				end = sf.parse(endDateHR).getTime() / 1000;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return ac.createConference(userId, pass, owner, accessType, permanent,
				pin, adminpin, start, end, maxUsers, name, agenda, recorded, project, authData);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean addUserToConference(String userId, String pass, String voipUsername, Integer confNo) throws VoIPConferenceServiceException {
		AsteriskConferenceSettings acmet = new AsteriskConferenceSettings(userId, pass, authData);
		return acmet.addUserToConference(userId, pass, voipUsername, confNo);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean removeUserFromConference(String userId, String pass, String voipUsername, Integer confNo) throws VoIPConferenceServiceException {
		AsteriskConferenceSettings acs = new AsteriskConferenceSettings(userId, pass, authData);
		return acs.removeUserFromConference(userId, pass, voipUsername, confNo);
	}	
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ConferenceDetails getConferenceDetails(String userId, String pass, Integer confNo) throws VoIPConferenceServiceException {
		AsteriskConferenceSettings acmet = new AsteriskConferenceSettings(userId, pass, authData);
		return acmet.getConferenceDetails(userId, pass, confNo, authData);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ConferenceDetails[] getConferencesDetailsList(String userId, String pass, String voipUsername, String type, 
		String project) throws VoIPConferenceServiceException {
		Integer[] list = null;

		log.debug("type=" + type);
		if (type != null) {
			if (type.equals("public")) {
				list = listPublicConfferences(userId, pass, project);
			} else if (type.equals("past")) {
				list = listPastConfferences(userId, pass, voipUsername, project);
			} else if (type.equals("owner")) {
				list = listConfferencesByOwner(userId, pass, voipUsername, project);
			} else if (type.equals("invitation")) {
				list = listConfferencesByInvitation(userId, pass, voipUsername, project);
			}
		}

		if (list == null) {
			return new ConferenceDetails[0];
		}

		List<ConferenceDetails> detailsList = new ArrayList<ConferenceDetails>(list.length);

		for (Integer confNo : list) {
			log.debug("confNo=" + confNo);
			ConferenceDetails details = getConferenceDetails(userId, pass, confNo);
			detailsList.add(details);
		}
		return detailsList.toArray(new ConferenceDetails[0]);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer removeConference(String userId, String pass, Integer confNumber) throws VoIPConferenceServiceException {
		AsteriskConferences ac = new AsteriskConferences(userId, pass, authData);
		return ac.removeConference(userId, pass, confNumber);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer[] listParticipants(String userId, String pass, Integer confNo) throws VoIPConferenceServiceException {
		AsteriskConferenceStatus acs = new AsteriskConferenceStatus(userId, pass, authData);
		return acs.listParticipants(userId, pass, confNo);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ParticipantsInfo[] getParticipantsListHR(String userId, String pass,Integer confNo) throws VoIPConferenceServiceException {
		ArrayList<ParticipantsInfo> partList = new ArrayList<ParticipantsInfo>();

		try {
			Integer[] usersList = listParticipants(userId, pass, confNo);

			if (usersList != null) {
				for (int userConferenceId : usersList) {
					ParticipantsInfo info = new ParticipantsInfo(userConferenceId + "",
							getVoipUsername(userId, pass, confNo, userConferenceId),
							AsteriskConferenceUtils.formatDate(getJoinDate(userId, pass, confNo, userConferenceId) / 1000),
							isUserBanned(userId, pass, confNo, userConferenceId) + "", //isUserBanned
							isUserTalking(userId, pass, confNo, userConferenceId) + "", //isUserTalking
							isUserMuted(userId, pass, confNo, userConferenceId) + "");  //isUserMuted
					partList.add(info);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return partList.toArray(new ParticipantsInfo[0]);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String[] listInvitedUsers(String userId, String pass, Integer confNo) throws VoIPConferenceServiceException {
		AsteriskConferenceSettings acs = new AsteriskConferenceSettings(userId, pass, authData);
		return acs.listInvitedUsers(userId, confNo);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer[] listConfferences(String userId, String pass, String project) throws VoIPConferenceServiceException {
		AsteriskConferences ac = new AsteriskConferences(userId, pass, authData);
		return ac.listConfferences(userId, project);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean isPastConference(String userId, String pass, String voipUsername, Integer confNo, String project) throws VoIPConferenceServiceException {
		Integer[] past = listPastConfferences(userId, pass, voipUsername, project);
		int length = past.length;
		for (int i = 0; i < length; ++i) {
			if (past[i].intValue() == confNo.intValue()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer[] listPastConfferences(String userId, String pass, String voipUsername, String project) throws VoIPConferenceServiceException {
		AsteriskConferences ac = new AsteriskConferences(userId, pass, authData);
		return ac.listPastConfferences(voipUsername, project);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String getRecordings(String userId, String pass, Integer confNo) throws VoIPConferenceServiceException {
		AsteriskConferenceSettings acmet = new AsteriskConferenceSettings(userId, pass, authData);
		return acmet.getRecordings(userId, pass, confNo, authData);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer[] listConfferencesByOwner(String userId, String pass, String voipUsername, String project) throws VoIPConferenceServiceException {
		AsteriskConferences ac = new AsteriskConferences(userId, pass, authData);
		return ac.listConfferencesByOwner(voipUsername, project);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer[] listConfferencesByInvitation(String userId, String pass, String voipUsername, String project) throws VoIPConferenceServiceException {
		AsteriskConferences ac = new AsteriskConferences(userId, pass, authData);
		return ac.listConfferencesByInvitation(voipUsername, project);
	} 

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer[] listPublicConfferences(String userId, String pass, String project) throws VoIPConferenceServiceException {
		AsteriskConferences ac = new AsteriskConferences(userId, pass, authData);
		return ac.listPublicConfferences(project);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean setConferenceUserList(String userId, String pass, Integer confNo, String[] userList) throws VoIPConferenceServiceException {
		AsteriskConferenceSettings acs = new AsteriskConferenceSettings(userId, pass, authData);
		boolean result = false;
		try {
			result = acs.setConferenceUserList(userId, pass, confNo, userList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Long getJoinDate(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException {
		AsteriskConferenceStatus acs = new AsteriskConferenceStatus(userId, pass, authData);
		return acs.getJoinDate(userId, pass, confNo, userConferenceId);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String getJoinDateHR(String userId, String pass, Integer confNo, Integer userConferenceId)  throws VoIPConferenceServiceException {
		return AsteriskConferenceUtils.formatDate(getJoinDate(userId, pass, confNo, userConferenceId));
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean resetDatabase(String userId, String pass) throws VoIPConferenceServiceException {
		return AsteriskConferenceSettings.resetDatabase();
	}
	//#############################################################################################
	//#############################################################################################
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String getServiceVersion() throws VoIPConferenceServiceException {
		return AsteriskConferenceUtils.getProperties().getProperty("voip.service.version");
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String isDBOpen() throws VoIPConferenceServiceException {
		boolean status = true;
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		
		try {
			//open transaction and commit
			EntityTransaction t = em.getTransaction();
			t.begin();
			t.commit();
		} catch (Exception e) {
			//in case of any error
			status = false;
		}
		return String.valueOf(status);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String getAsteriskVersion() throws VoIPConferenceServiceException {
		String version = "";
		try {	
			version = AsteriskJavaListener.getInstance().getVersion();
		} catch (Exception e) {
			return "connection error: " + e.getLocalizedMessage();
		}
		return version;
	}
	//#############################################################################################
	//#############################################################################################
	@Override
	public String[] getResourceTypeList() {
		return RESOURCE_TYPE_LIST;
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}
	//#############################################################################################
	//#############################################################################################
	@SuppressWarnings("unused")
	private void checkPermission(String userId, String path) throws Exception {
		String caller = membership.getProfilePathForConnectedIdentifier();
		if (caller == null) {
			throw new VoIPConferenceServiceException("Could not get connected profile");
	    }
		pep.checkSecurity(caller, path, "create");
	}
	
	@SuppressWarnings("unused")
	private void bindAndNotify (String path, String caller) throws Exception {
	    // Need to set some properties on the node
	    binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");

	    // Using the notification service to throw an event
	    notification.throwEvent(new Event(path, caller, "ConferenceDetails", "voip.conference.update", ""));
	}

	@Override
	public FactoryResource findResource(String path) throws FactoryException {
	    return null;
	}
}
