package org.qualipso.factory.voipservice.conference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.asteriskjava.live.ManagerCommunicationException;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.hibernate.beans.domain.Organisation;
import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.qualipso.factory.voipservice.VoIPConferenceServiceException;
import org.qualipso.factory.voipservice.entity.ConferenceUser;
import org.qualipso.factory.voipservice.entity.ExtensionsConf;
import org.qualipso.factory.voipservice.entity.MeetMe;
import org.qualipso.factory.voipservice.entity.PastConference;
import org.qualipso.factory.voipservice.entity.SipConf;
import org.qualipso.factory.voipservice.manager.AsteriskJavaManager;
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
public class AsteriskConferences {
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(AsteriskConferences.class);

	/**
	 * @param userId
	 * @throws VoIPConferenceServiceException 
	 * @throws SecurityException 
	 */
	public AsteriskConferences(String userId, String pass, AuthData authData) {
		try {
		    AuthenticationModule.authenticate(userId, pass, authData);
		} catch (SecurityException e) {
		    e.printStackTrace();
		} catch (VoIPConferenceServiceException e) {
		    e.printStackTrace();
		}
	}

	/**
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Integer[] listConfferences(String userId, String project) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		Query query = null;
		if (project == null) {
			em.createQuery("FROM MeetMe mm ORDER BY mm.confno");
		} else {
			query = em.createQuery("FROM MeetMe mm where mm.project = :project ORDER BY mm.confno");
			query.setParameter("project", project);
		}
		
		List<MeetMe> conferenceList = (List<MeetMe>)(query.getResultList());
		List<Integer> conferenceNumbers = new ArrayList<Integer>(conferenceList.size());
		for (MeetMe meetMe : conferenceList) {
			if (meetMe.hasExpired()) {
				cleenUpConference(em,meetMe);
			} else if (meetMe.getAccessType() == MeetMe.AccessTypes.PUBLIC || meetMe.getOwner().equals(userId)) {
				conferenceNumbers.add(meetMe.getConfno());
			}
		}

		em.getTransaction().commit();
		em.close();
		
		return conferenceNumbers.toArray(new Integer[conferenceNumbers.size()]);
	}

	/**
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Integer[] listConfferencesByOwner(String userId, String project) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		Query query = null;
		
		if (project == null) {
			query = em.createQuery("FROM MeetMe mm WHERE mm.owner = :owner ORDER BY mm.confno");
			query.setParameter("owner", userId);
		} else {
			query = em.createQuery("FROM MeetMe mm WHERE mm.owner = :owner and mm.project = :project ORDER BY mm.confno");
			query.setParameter("owner", userId);
			query.setParameter("project", project);
		}		
		
		List<MeetMe> conferenceList = (List<MeetMe>)query.getResultList();
		List<Integer> conferenceNumbers = new ArrayList<Integer>(conferenceList.size());
		for (MeetMe meetMe : conferenceList) {
			if (meetMe.hasExpired()) {
				cleenUpConference(em,meetMe);
			} else {
				conferenceNumbers.add(meetMe.getConfno());
			}
		}

		em.getTransaction().commit();
		em.close();
		
		return conferenceNumbers.toArray(new Integer[conferenceNumbers.size()]);
	}

	/**
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Integer[] listConfferencesByInvitation(String userId, String project) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		Query query = null;
		if (project == null) {
			query = em.createQuery("FROM MeetMe mm ORDER BY mm.confno");
		} else {
			query = em.createQuery("FROM MeetMe mm where mm.project = :project ORDER BY mm.confno");
			query.setParameter("project", project);
		}
		
		List<MeetMe> conferenceList = (List<MeetMe>)query.getResultList();
		List<Integer> conferenceNumbers = new ArrayList<Integer>(conferenceList.size());
		for (MeetMe meetMe : conferenceList) {
			if (meetMe.hasExpired()) {
				cleenUpConference(em,meetMe);
			} else if (meetMe.getInvitedUsersList().contains(userId)) {
				conferenceNumbers.add(meetMe.getConfno());
			}
		}

		em.getTransaction().commit();
		em.close();

		return conferenceNumbers.toArray(new Integer[conferenceNumbers.size()]);
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Integer[] listPublicConfferences(String project) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		Query query = null;
		if (project == null) {
			query = em.createQuery("FROM MeetMe mm WHERE mm.accessType = :atype ORDER BY mm.confno");
			query.setParameter("atype", MeetMe.AccessTypes.PUBLIC);
		} else {
			query = em.createQuery("FROM MeetMe mm WHERE mm.accessType = :atype and mm.project = :project ORDER BY mm.confno");
			query.setParameter("atype", MeetMe.AccessTypes.PUBLIC);
			query.setParameter("project", project);
		}
		
		List<MeetMe> conferenceList = (List<MeetMe>)query.getResultList();
		
		List<Integer> conferenceNumbers = new ArrayList<Integer>(conferenceList.size());
		log.debug("conferenceList.size=" + conferenceList.size());
		for (MeetMe meetMe : conferenceList) {
			if (meetMe.hasExpired()) {
				cleenUpConference(em, meetMe);
			} else {
				conferenceNumbers.add(meetMe.getConfno());
			}
		}
		log.debug("conferenceNumbers.size=" + conferenceNumbers.size());
		em.getTransaction().commit();
		em.close();
		return conferenceNumbers.toArray(new Integer[conferenceNumbers.size()]);
	}

	/**
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Integer[] listPastConfferences(String userId, String project) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		cleenupConferences(em, userId);

		Query query = null;
		if (project == null) {
			query = em.createQuery("FROM PastConference pc WHERE pc.owner = :owner ORDER BY pc.confno");
			query.setParameter("owner", userId);
		} else {
			query = em.createQuery("FROM PastConference pc WHERE pc.owner = :owner and project = :project ORDER BY pc.confno");
			query.setParameter("owner", userId);
			query.setParameter("project", project);
		}
		
		List<PastConference> pastConferenceList = (List<PastConference>)(query.getResultList());
		List<Integer> conferenceNumbers = new ArrayList<Integer>(pastConferenceList.size());
		for (PastConference pastConference : pastConferenceList) {
			conferenceNumbers.add(pastConference.getConfno());
		}

		em.getTransaction().commit();
		em.close();
		
		return conferenceNumbers.toArray(new Integer[conferenceNumbers.size()]);
	}
	
	/**
	 * @param userId
	 * @param owner
	 * @param accessType
	 * @param permanent
	 * @param pin
	 * @param adminpin
	 * @param startDate
	 * @param endDate
	 * @param maxUsers
	 * @param name
	 * @param agenda
	 * @param recorded
	 * @return
	 */
	public Integer createConference(String userId, String pass, String owner, short accessType,boolean permanent,String pin,String adminpin,Long startDate,Long endDate,Integer maxUsers,String name,String agenda,boolean recorded, String project,
		AuthData authData) {
		if (!AuthenticationModule.isSuperUser(userId, pass)) {
			return null;
		}

		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();
		
		MeetMe meetMe = new MeetMe();
		if (pin != null) {
			meetMe.setPin(pin);
		}
		if (adminpin != null) {
			meetMe.setAdminpin(adminpin);
		}
		if (!permanent) {
			meetMe.setStartDate(startDate);
			meetMe.setEndDate(endDate);
		} else {
			meetMe.setStartDate(null);
			meetMe.setEndDate(null);
		}
		
		meetMe.setMaxUsers(maxUsers);
		meetMe.setOwner(owner);
		meetMe.setName(name);
		meetMe.setAgenda(agenda);
		meetMe.setAccessType(accessType);
		meetMe.setPermanent(permanent);
		meetMe.setRecorded(recorded);
		meetMe.setProject(project);
		em.persist(meetMe);
		log.debug("save conference");
		Integer confNumber = meetMe.getConfno();

		//set extensions (for calling)
		ExtensionsConf extConf = new ExtensionsConf();
		extConf.setContext(AsteriskConferenceUtils.CONFERENCE_NAME);
		extConf.setPriority(1);
		extConf.setExten(confNumber.toString());
		extConf.setApp("Macro");
		extConf.setAppdata(AsteriskConferenceUtils.CONFERENCE_NAME);
		em.persist(extConf);
		log.debug("save extension");

		
		AsteriskConferenceUtils.initOpenmeetingsPaths();
		
		 List organisations = new ArrayList();
		    Organisation o = Organisationmanagement.getInstance().getOrganisationById(1L);
		    organisations.add(o.getOrganisation_id().intValue());
		Long room_id = Roommanagement.getInstance().addRoom(3L, name, 1L, agenda, 
			new Long(maxUsers), true, 
			organisations, false, false, null, false);
		Rooms r = Roommanagement.getInstance().getRoomById(room_id);
		r.setRooms_id(new Long(confNumber));
		r .setRoomtype(Roommanagement.getInstance().getRoomTypesById(1L));
		Roommanagement.getInstance().deleteRoomById(3L, new Long(confNumber));
		Roommanagement.getInstance().updateRoomObject(r);
		log.debug("save room");

		log.debug("created conference room: " + confNumber);
		
		if (!permanent) {
		    try {
			
			String calendarPath = "/projects/" + project + "/calendar/voip/" + confNumber;
			
	    	    	org.qualipso.factory.voipservice.client.ws.CalendarDetails details = new org.qualipso.factory.voipservice.client.ws.CalendarDetails();
	    	    	details.setName(name);
	    	    	details.setLocation("voip call conference");
	    	    	
	    	    	details.setContactName(owner);
	    	    	Query query = em.createQuery("FROM SipConf sc WHERE sc.username = :username");
			query.setParameter("username", owner);
			SipConf sipConf = (SipConf)query.getSingleResult();
			details.setContactEmail(sipConf.getEmail());
	    	    	details.setContactPhone("sip:" + owner);
	    	    	
	    	    	SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
	    	    	SimpleDateFormat sf2 = new SimpleDateFormat("HH:mm:ss");
	    	    	details.setDate(sf1.format((new Date(startDate * 1000))));
	    	    	details.setStartTime(sf2.format((new Date(startDate* 1000))));
	    	    	details.setEndTime(sf2.format((new Date(endDate* 1000))));
	    	    	
	    	    	log.info("### start=" + sf1.format((new Date(startDate* 1000))));
	    	    	log.info("### start time=" + sf2.format((new Date(startDate* 1000))));
	    	    	log.info("### end time=" + sf2.format((new Date(endDate* 1000))));
	    	
	    	    	org.qualipso.factory.voipservice.client.ws.Calendar_Service  calendar = new org.qualipso.factory.voipservice.client.ws.Calendar_Service();
	    	    	org.qualipso.factory.voipservice.client.ws.StringArray test =  calendar.getCalendarPort().createEvent(calendarPath, details);
	    	    	List<String> items = test.getItem();
	    	    	for (String item: items) {
	    	    	    System.out.println("### calendar item=" + item);
	    	    	}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
		
		em.getTransaction().commit();
		em.close();
		
		return confNumber;
	}

	/**
	 * @param username
	 * @param confNumber
	 * @return
	 */
	public boolean endConference(String username, String pass, Integer confNumber) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		
		em.getTransaction().begin();
		MeetMe meetMe = em.find(MeetMe.class, confNumber);
		if (meetMe == null) {
			em.getTransaction().commit();
			em.close();
			return false;
		}
		if (!AuthenticationModule.isSuperUser(username, pass) && !meetMe.getOwner().equals(username)) {
			em.getTransaction().commit();
			em.close();
			return false;
		}

		Set<ConferenceUser> userList = meetMe.getUsersList();
		try {
			AsteriskJavaManager asm = AsteriskJavaManager.getInstance();
			asm.lockConf(confNumber);
			for (ConferenceUser conferenceUser : userList) {
				asm.kickUser(confNumber, conferenceUser.getId());
			}
		}
		catch (ManagerCommunicationException e) {
			log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}

		cleenUpConference(em, meetMe);

		em.getTransaction().commit();
		em.close();
		return true;
	}

	/**
	 * @param username
	 * @param confNumber
	 * @return
	 */
	public Integer removeConference(String username, String pass, Integer confNumber) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		MeetMe meetMe = em.find(MeetMe.class, confNumber);
		if (meetMe == null) {
			PastConference pastConference = em.find(PastConference.class, confNumber);
			if (pastConference != null) {
				if (!AuthenticationModule.isSuperUser(username, pass) && !pastConference.getOwner().equals(username)) {
					em.getTransaction().commit();
					em.close();
					return null;
				}
				pastConference.clearInvitedUserList();
				pastConference.clearBannedUserList();
				em.flush();

				Query query = em.createQuery("DELETE FROM PastConference pc WHERE pc.confno = :confno");
				query.setParameter("confno", confNumber);
				int count = query.executeUpdate();

				query = em.createQuery("DELETE FROM Rooms r WHERE r.rooms_id =  :rooms_id");
				query.setParameter("rooms_id", new Long(confNumber));
				query.executeUpdate();

				em.getTransaction().commit();
				em.close();
				return count;
			}
			
			em.getTransaction().commit();
			em.close();
			return null;
		}
		
		if (!AuthenticationModule.isSuperUser(username, pass) && !meetMe.getOwner().equals(username)) {
			em.getTransaction().commit();
			em.close();
			return null;
		}
		Integer count = removeConference(em, meetMe);

    	    	String calendarPath = "/projects/" + meetMe.getProject()  + "/calendar/voip/" + confNumber;
	    	try {
	    	    	org.qualipso.factory.voipservice.client.ws.Calendar_Service  calendar = new org.qualipso.factory.voipservice.client.ws.Calendar_Service();
	    	    	calendar.getCalendarPort().deleteEvent(calendarPath);
		} catch (Exception e) {
			    e.printStackTrace();
		}
		
		em.getTransaction().commit();
		em.close();
		return count;
	}

	/**
	 * @param em
	 * @param meetMe
	 * @return
	 */
	private static Integer removeConference(EntityManager em, MeetMe meetMe) {
		if (meetMe == null) {
			return null;
		}

		meetMe.clearInvitedUserList();
		meetMe.clearBannedUserList();
		Query query = em.createQuery("DELETE FROM ConferenceUser cu WHERE cu.meetMe = :meetMe");
		query.setParameter("meetMe", meetMe);
		int count = query.executeUpdate();
		em.flush();

		query = em.createQuery("DELETE FROM ExtensionsConf ec WHERE ec.exten = :confno AND ec.context = :context");
		query.setParameter("confno", meetMe.getConfno().toString());
		query.setParameter("context", AsteriskConferenceUtils.CONFERENCE_NAME);
		count = query.executeUpdate();

		query = em.createQuery("DELETE FROM MeetMe mm WHERE mm.confno = :confno");
		query.setParameter("confno", meetMe.getConfno());
		count = query.executeUpdate();

		return count;
	}

	/**
	 * @param em
	 * @param username
	 */
	@SuppressWarnings("unchecked")
	public void cleenupConferences(EntityManager em, String username) {
		// check if any of the existing conferences has ended
		Query query = em.createQuery("FROM MeetMe mm WHERE mm.owner = :owner ORDER BY mm.confno");
		query.setParameter("owner", username);
		List<MeetMe> conferenceList = (List<MeetMe>)query.getResultList();
		for (MeetMe meetMe : conferenceList) {
			if (meetMe.hasExpired()) {
				cleenUpConference(em, meetMe);
			}
		}
	}

	/**
	 * @param em
	 * @param meetMee
	 * @return
	 */
	private boolean cleenUpConference(EntityManager em, MeetMe meetMee) {

		List<String> invitedUsers = new ArrayList<String>();
		for (String string : meetMee.getInvitedUsersList()) {
			invitedUsers.add(string);
		}
		List<String> bannedUsers = new ArrayList<String>();
		for (String string : meetMee.getBannedUserList()) {
			bannedUsers.add(string);
		}

		Integer count = removeConference(em,meetMee);
		if (count != 1) {
			return false;
		}

		AsteriskConferenceUtils.initOpenmeetingsPaths();
		
		Rooms r = Roommanagement.getInstance().getRoomById(new Long(meetMee.getConfno()));
		r.setDeleted("true");
		Roommanagement.getInstance().updateRoomObject(r);

		PastConference pastConference = new PastConference();
		pastConference.setPinInt(meetMee.getPinInt());
		pastConference.setAdminpinInt(meetMee.getAdminpinInt());
		if (!meetMee.isPermanent()) {
			pastConference.setStartDate(meetMee.getStartDate());
			pastConference.setEndDate(meetMee.getEndDate());
		}
		pastConference.setConfno(meetMee.getConfno());
		pastConference.setMaxUsers(meetMee.getMaxUsers());
		pastConference.setOwner(meetMee.getOwner());
		pastConference.setName(meetMee.getName());
		pastConference.setAgenda(meetMee.getAgenda());
		pastConference.setAccessType(meetMee.getAccessType());
		pastConference.setPermanent(meetMee.isPermanent());
		pastConference.setRecorded(meetMee.isRecorded());
		pastConference.setProject(meetMee.getProject());

		for (String username : invitedUsers) {
			Query query = em.createQuery("FROM SipConf sc WHERE sc.username = :username");
			query.setParameter("username", username);
			SipConf sipConf = (SipConf)query.getSingleResult();
			if (sipConf != null)
				pastConference.addInvitedUser(sipConf);
		}
		for (String username : bannedUsers) {
			Query query = em.createQuery("FROM SipConf sc WHERE sc.username = :username");
			query.setParameter("username", username);
			SipConf sipConf = (SipConf)query.getSingleResult();
			if (sipConf != null)
				pastConference.addBannedUser(sipConf);
		}

		if (meetMee.isRecorded()) {
			String recordPath = AsteriskConferenceUtils.getProperties().getProperty("voip.recordrings.link");
			String recordingsPaths = recordPath+meetMee.getConfno()+"/";
			pastConference.setRecordings(recordingsPaths);
		}

		em.persist(pastConference);
		return true;
	}
}
