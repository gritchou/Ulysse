package org.qualipso.factory.voipservice.conference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.voipservice.VoIPConferenceServiceException;
import org.qualipso.factory.voipservice.entity.ConferenceUser;
import org.qualipso.factory.voipservice.entity.MeetMe;
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
public class AsteriskConferenceStatus implements AsteriskConferenceSecurity {
	protected static Logger log = Logger.getLogger(AsteriskConferenceStatus.class);
	private MeetMe meetMe = null;
	
	/**
	 * @param userId
	 * @throws MembershipServiceException 
	 * @throws VoIPConferenceServiceException 
	 */
	public AsteriskConferenceStatus(String userId, String pass, AuthData authData) throws VoIPConferenceServiceException {
		AuthenticationModule.authenticate(userId, pass, authData);
	}

	/**
	 * @param userId
	 * @param confNo
	 * @return
	 */
	public Integer[] listParticipants(String userId, String pass, Integer confNo) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		Integer[] participants = new Integer[0];
		meetMe = em.find(MeetMe.class, confNo);
		
		//check permission
		if (false == checkPermission(new AsteriskConferenceSecurityParams(userId, pass, null, confNo))) {
			em.getTransaction().commit();
			em.close();
			throw new java.lang.SecurityException();
		}
		
		if (meetMe != null) {
			Set<ConferenceUser> userConferenceList = meetMe.getUsersList();
			List<ConferenceUser> list = new ArrayList<ConferenceUser>(userConferenceList);
			Collections.sort(list);
	
			int size = list.size();
			participants = new Integer[size];
	
			for (int i = 0; i < size; ++i) {
				participants[i] = list.get(i).getId();
			}
		}
		em.getTransaction().commit();
		em.close();

		return participants;
	}


	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public String getUserName(String userId, String pass, Integer confNo, Integer userConferenceId) {
		ConferenceUser conferenceUser = getConferenceUser(userId, pass, confNo, userConferenceId);
		if (conferenceUser == null) {
			return "";
		}
		return conferenceUser.getSipConf().getUsername();
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public Long getJoinDate(String userId, String pass, Integer confNo, Integer userConferenceId) {
		ConferenceUser conferenceUser = getConferenceUser(userId, pass, confNo, userConferenceId);
		if (conferenceUser == null) {
			return null;
		}
		return conferenceUser.getJoinDate();
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public boolean isUserBanned(String userId, String pass, Integer confNo, Integer userConferenceId) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		boolean banned = false;
		
		ConferenceUser conferenceUser = getConferenceUser(userId, pass, confNo, userConferenceId);
		meetMe = getMeetMe(userId, pass, confNo, userConferenceId);

		if (conferenceUser != null && meetMe != null) {
			String username = conferenceUser.getSipConf().getUsername();
			banned = meetMe.getBannedUserList().contains(username);
		}

		em.getTransaction().commit();
		em.close();
		
		return banned;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public boolean isUserMuted(String userId, String pass, Integer confNo, Integer userConferenceId) {
		log.debug("start isUserMuted");
		ConferenceUser conferenceUser = getConferenceUser(userId, pass, confNo, userConferenceId);
		if (conferenceUser == null) {
			return false;
		}
		log.debug("start isUserMuted = "  + conferenceUser);

		return conferenceUser.isMuted();
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public boolean isUserTalking(String userId, String pass, Integer confNo, Integer userConferenceId) {
		ConferenceUser conferenceUser = getConferenceUser(userId, pass, confNo, userConferenceId);
		if (conferenceUser == null) {
			return false;
		}
		return conferenceUser.isTalking();
	}


	/**
	 * @param userId
	 * @param pass
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	private MeetMe getMeetMe(String userId, String pass, Integer confNo, Integer userConferenceId) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		MeetMe meetMe = em.find(MeetMe.class, confNo);

		em.getTransaction().commit();
		em.close();

		return meetMe;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	private ConferenceUser getConferenceUser(String userId, String pass, Integer confNo, Integer userConferenceId) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		meetMe = getMeetMe(userId, pass, confNo, userConferenceId);

		//check permission
		if (false == checkPermission(new AsteriskConferenceSecurityParams(userId, pass, null, confNo))) {
			em.getTransaction().commit();
			em.close();
			throw new java.lang.SecurityException();
		}

		ConferenceUser.ConferenceUserPK pk = new ConferenceUser.ConferenceUserPK();
		pk.setId(userConferenceId);
		pk.setMeetMe(meetMe);
		
		ConferenceUser conferenceUser = em.find(ConferenceUser.class, pk);
		
		try {
			em.getTransaction().commit();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		em.close();
		
		return conferenceUser;
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
