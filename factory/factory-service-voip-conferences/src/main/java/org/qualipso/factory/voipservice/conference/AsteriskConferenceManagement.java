package org.qualipso.factory.voipservice.conference;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.asteriskjava.live.ManagerCommunicationException;
import org.qualipso.factory.voipservice.VoIPConferenceServiceException;
import org.qualipso.factory.voipservice.entity.ConferenceUser;
import org.qualipso.factory.voipservice.entity.MeetMe;
import org.qualipso.factory.voipservice.entity.SipConf;
import org.qualipso.factory.voipservice.manager.AsteriskJavaManager;
import org.qualipso.factory.voipservice.security.AsteriskConferenceSecurity;
import org.qualipso.factory.voipservice.security.AsteriskConferenceSecurityParams;
import org.qualipso.factory.voipservice.security.AuthenticationModule;
import org.qualipso.factory.voipservice.utils.AsteriskConferenceUtils;
import org.qualipso.factory.voipservice.utils.AuthData;


/**
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @author <a href="mailto:debe@man.poznan.pl">Marcin Wrzos</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */
public class AsteriskConferenceManagement implements AsteriskConferenceSecurity {

	private static Logger log = Logger.getLogger(AsteriskConferenceManagement.class);
	private MeetMe meetMe = null;
	
	/**
	 * @param userId
	 * @throws VoIPConferenceServiceException 
	 * @throws SecurityException 
	 */
	public AsteriskConferenceManagement(String userId, String pass, AuthData authData) throws SecurityException, VoIPConferenceServiceException {
		AuthenticationModule.authenticate(userId, pass, authData);
	}

	/**
	 * @param userId
	 * @param confNo
	 * @return
	 */
	public boolean lockConf(String userId, String pass, Integer confNo) {
		meetMe = getMeetMe(confNo);

		//check permission
		if (false == checkPermission(new AsteriskConferenceSecurityParams(userId, pass, null, confNo))) {
			return false;
		}			

		try {
			AsteriskJavaManager asm = AsteriskJavaManager.getInstance();
			return asm.lockConf(confNo);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param userId
	 * @param confNo
	 * @return
	 */
	public boolean unlockConf(String userId, String pass,Integer confNo) {
		meetMe = getMeetMe(confNo);

		//check permission
		if (false == checkPermission(new AsteriskConferenceSecurityParams(userId, pass, null, confNo))) {
			return false;
		}

		try {
			AsteriskJavaManager asm = AsteriskJavaManager.getInstance();
			return asm.unlockConf(confNo);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public boolean banUser(String userId, String pass, Integer confNo, Integer userConferenceId) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();
		
		meetMe = em.find(MeetMe.class, confNo);

		//check permission
		if (false == checkPermission(new AsteriskConferenceSecurityParams(userId, pass, null, confNo))) {
			em.getTransaction().commit();
			em.close();
			return false;
		}

		ConferenceUser.ConferenceUserPK pk = new ConferenceUser.ConferenceUserPK();
		pk.setId(userConferenceId);
		pk.setMeetMe(meetMe);
		ConferenceUser conferenceUser = em.find(ConferenceUser.class, pk);

		//when user exist then add to banned list
		if (conferenceUser != null) {
			SipConf sipConf = conferenceUser.getSipConf();
			meetMe.addBannedUser(sipConf);
		}
		em.getTransaction().commit();
		em.close();

		//kick user from conference
		try {
			AsteriskJavaManager asm = AsteriskJavaManager.getInstance();
			return asm.kickUser(confNo, userConferenceId);
		}
		catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param username
	 * @return
	 */
	public boolean unbanUser(String userId, String pass,Integer confNo, String voipUsername) {
		meetMe = getMeetMe(confNo);
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();
		
		//check permission
		if (false == checkPermission(new AsteriskConferenceSecurityParams(userId, pass, null, confNo))) {
			return false;
		}

		//find user in sip_conf table
		Query query = em.createQuery("FROM SipConf sc WHERE sc.username = :username");
		query.setParameter("username", voipUsername);
		SipConf sipConf = (SipConf)query.getSingleResult();
		
		//remove user from banned user table
		meetMe.removeBannedUser(sipConf);
		em.getTransaction().commit();
		em.close();

		return true;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public boolean muteUser(String userId, String pass, Integer confNo, Integer userConferenceId) {
		meetMe = getMeetMe(confNo);

		//check permission
		if (false == checkPermission(new AsteriskConferenceSecurityParams(userId, pass, null, confNo))) {
			return false;
		}

		try {
			AsteriskJavaManager asm = AsteriskJavaManager.getInstance();
			return asm.muteUser(confNo, userConferenceId);
		}
		catch (ManagerCommunicationException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public boolean unmuteUser(String userId, String pass,Integer confNo,Integer userConferenceId) {
		meetMe = getMeetMe(confNo);

		//check permission
		if (false == checkPermission(new AsteriskConferenceSecurityParams(userId, pass, null, confNo))) {
			return false;
		}

		try {
			AsteriskJavaManager asm = AsteriskJavaManager.getInstance();
			return asm.unmuteUser(confNo, userConferenceId);
		}
		catch (ManagerCommunicationException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public boolean kickUser(String userId, String pass, Integer confNo, Integer userConferenceId) {
		meetMe = getMeetMe(confNo);
		
		//check permission
		if (false == checkPermission(new AsteriskConferenceSecurityParams(userId, pass, null, confNo))) {
			return false;
		}
		
		try {
			AsteriskJavaManager asm = AsteriskJavaManager.getInstance();
			return asm.kickUser(confNo, userConferenceId);
		}
		catch (ManagerCommunicationException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Get MeetMe object
	 * @param confNo
	 * @return
	 */
	private MeetMe getMeetMe(Integer confNo) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		MeetMe meetMe = em.find(MeetMe.class, confNo);
		em.getTransaction().commit();
		em.close();
		
		return meetMe;
	}

	public boolean checkPermission(AsteriskConferenceSecurityParams params) {
		
		if (true == AuthenticationModule.isSuperUser(params.getUserId(), params.getPass()) ||  
				(meetMe != null && meetMe.getOwner().equals(params.getUserId()))) {
			return true;
		}
		
		log.error("permission denied");
		return false;
	}
}
