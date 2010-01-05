package org.qualipso.factory.voipservice.conference;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.hibernate.beans.rooms.Rooms;
import org.qualipso.factory.collaboration.beans.CalendarEvent;
import org.qualipso.factory.voipservice.VoIPConferenceServiceException;
import org.qualipso.factory.voipservice.email.QualipsoConferenceEmail;
import org.qualipso.factory.voipservice.entity.ConferenceDetails;
import org.qualipso.factory.voipservice.entity.MeetMe;
import org.qualipso.factory.voipservice.entity.PastConference;
import org.qualipso.factory.voipservice.entity.SipConf;
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
public class AsteriskConferenceSettings {
    	private static Logger log = Logger.getLogger(AsteriskConferenceSettings.class);
    	
	/**
	 * @param userId
	 * @throws VoIPConferenceServiceException 
	 * @throws SecurityException 
	 */
	public AsteriskConferenceSettings(String userId, String pass, AuthData authData) throws SecurityException, VoIPConferenceServiceException {
		AuthenticationModule.authenticate(userId, pass, authData);
	}


	/**
	 * @param username
	 * @param confNo
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
	public Integer editConference(String username, String pass,Integer confNo, String owner, short accessType,boolean permanent,String pin,String adminpin,Long startDate,Long endDate,Integer maxUsers,String name,String agenda,boolean recorded, AuthData authData) {
		EntityManager em=AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		MeetMe meetMe = em.find(MeetMe.class, confNo);
		if (!AuthenticationModule.isSuperUser(username, pass) && !meetMe.getOwner().equals(username)) {
			em.getTransaction().commit();
			em.close();
			return new Integer(-1);
		}

		meetMe.setPin(pin);
		meetMe.setAdminpin(adminpin);
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

		em.persist(meetMe);

		AsteriskConferenceUtils.initOpenmeetingsPaths();
		Rooms r = Roommanagement.getInstance().getRoomById(new Long(confNo));
		r.setComment(agenda);
		r.setName(name);
		r.setNumberOfPartizipants(new Long(maxUsers));
		Roommanagement.getInstance().updateRoomObject(r);

		
		if (!permanent) {
		    	try {
		    	    	String calendarPath = "/projects/" + meetMe.getProject()  + "/calendar/voip/" + meetMe.getConfno();
		    	    	
		    	    	CalendarEvent details = new CalendarEvent();
		    	    	details.setName(name);

		    	    	details.setContactName(owner);
		    	    	Query query = em.createQuery("FROM SipConf sc WHERE sc.username = :username");
				query.setParameter("username", owner);
				SipConf sipConf = (SipConf)query.getSingleResult();
				details.setContactEmail(sipConf.getEmail());
				details.setContactPhone("sip:" + owner);
		    	    	
		    	    	details.setLocation("call conference");
		    	    	SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
		    	    	SimpleDateFormat sf2 = new SimpleDateFormat("HH:mm:ss");
		    	    	details.setDate(sf1.format((new Date(startDate* 1000))));
		    	    	details.setStartTime(sf2.format((new Date(startDate* 1000))));
		    	    	details.setEndTime(sf2.format((new Date(endDate* 1000))));
		    	    	
		    	    	authData.updateCalendar().updateEvent(calendarPath, details);
   			} catch (Exception e) {
   			    log.error(e.getMessage());
			}
		}
		
		em.getTransaction().commit();
		em.close();
		return confNo;
	}

	/**
	 * @param userList
	 * @param meetMe
	 */
	private void notifyParticipants(List<String> userList, MeetMe meetMe) {
		if (userList==null || userList.size()==0) {
			return;
		}

		QualipsoConferenceEmail qm = new QualipsoConferenceEmail();
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("META-INF/mail.txt");
			BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));

			String contents="";
			String line;
			while (( line = input.readLine()) != null){
				contents += line;
				contents += System.getProperty("line.separator");
			}
			contents = contents.replaceAll("<confno>", Integer.toString(meetMe.getConfno()));
			if (meetMe.getPin().equals("")) {
				contents = contents.replaceAll("<pin>", "none");
			} else {
				contents = contents.replaceAll("<pin>", meetMe.getPin());
			}

			contents = contents.replaceAll("<name>", meetMe.getName());
			contents = contents.replaceAll("<agenda>", meetMe.getAgenda());
			if (meetMe.getStartDate() != null && meetMe.getStartDate() != -1) {
				contents = contents.replaceAll("<startDate>", AsteriskConferenceUtils.DATE_FORMAT.format(new Date(1000*meetMe.getStartDate())));
			} else {
				contents = contents.replaceAll("<startDate>", "Not set");
			}

			if (meetMe.getEndDate() != null && meetMe.getEndDate() != -1) {
				contents = contents.replaceAll("<endDate>", AsteriskConferenceUtils.DATE_FORMAT.format(new Date(1000*meetMe.getEndDate())));
			} else {
				contents = contents.replaceAll("<endDate>", "Not set");
			}

			HashMap<String, SipConf> invitedUsers = meetMe.getInvitedUsersMap();
			for (String user : userList) {
				String pContents = contents.replaceAll("<user>", user);
				pContents = pContents.replaceAll("<userAddress>", invitedUsers.get(user).getEmail());

				String subject = "Invitation to a conference: "+meetMe.getName();
				qm.sendMessage(AsteriskConferenceUtils.getProperties(), subject,
						pContents, invitedUsers.get(user).getEmail());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * @param userId
	 * @param confno
	 * @param userList
	 * @return
	 */
	public boolean setConferenceUserList(String userId, String pass, Integer confno, String[] userList) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();

		em.getTransaction().begin();
		MeetMe meetMe = em.find(MeetMe.class, confno);

		if (!AuthenticationModule.isSuperUser(userId, pass) && !meetMe.getOwner().equals(userId)) {
			em.getTransaction().commit();
			em.close();
			return false;
		}

		List<String> newList = new ArrayList<String>();
		for (String string : userList) {
		    	log.debug("invited: " + string);
			newList.add(string);
		}
		meetMe.clearInvitedUserList();

		for (String username : userList) {
			Query query = em.createQuery("FROM SipConf sc WHERE sc.username = :username");
			query.setParameter("username", username);
			try {
				if (query.getSingleResult() == null) {
					continue;
				}
			} catch (javax.persistence.NoResultException ex) {
				continue;
			}

			SipConf sipConf = (SipConf)query.getSingleResult();
			meetMe.addInvitedUser(sipConf);
			log.debug("confirmed: " + username);
		}

		// if user not in sip conf, than don't try to send him an invitation
		newList.retainAll(meetMe.getInvitedUsersList());

		notifyParticipants(newList, meetMe);

		em.getTransaction().commit();
		em.close();
		return true;
	}

	/**
	 * @param userId
	 * @param username
	 * @param confno
	 * @return
	 */
	public boolean addUserToConference(String userId, String pass,String username, Integer confno) {
		EntityManager em=AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();
		MeetMe meetMe=em.find(MeetMe.class, confno);

		if (!AuthenticationModule.isSuperUser(userId, pass) && !meetMe.getOwner().equals(userId)) {
			em.getTransaction().commit();
			em.close();
			return false;
		}

		Query query=em.createQuery("FROM SipConf sc WHERE sc.username = :username");
		query.setParameter("username", username);
		SipConf sipConf=(SipConf)query.getSingleResult();
		meetMe.addInvitedUser(sipConf);

		em.getTransaction().commit();
		em.close();
		return true;
	}

	/**
	 * @param userId
	 * @param username
	 * @param confno
	 * @return
	 */
	public boolean removeUserFromConference(String userId, String pass,String username, Integer confno) {
		EntityManager em=AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();
		MeetMe meetMe=em.find(MeetMe.class, confno);

		if (!AuthenticationModule.isSuperUser(userId, pass) && !meetMe.getOwner().equals(userId)) {
			em.getTransaction().commit();
			em.close();
			return false;
		}

		Query query=em.createQuery("FROM SipConf sc WHERE sc.username = :username");
		query.setParameter("username", username);
		SipConf sipConf=(SipConf)query.getSingleResult();
		if (sipConf!=null) {
			meetMe.removeInvitedUser(sipConf);
		}
		em.getTransaction().commit();
		em.close();
		return true;
	}


	/**
	 * @param username
	 * @param conferenceNumber
	 * @return
	 */
	public String[] listInvitedUsers(String username, Integer conferenceNumber) {
		EntityManager em=AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		MeetMe meetMe = em.find(MeetMe.class, conferenceNumber);
		List<String> userNames = new ArrayList<String>();
		if (meetMe != null) {
			if (!meetMe.getOwner().equals(username)) {
				em.getTransaction().commit();
				em.close();
				return null;
			}
			
			userNames = meetMe.getInvitedUsersList();
		} else {
			PastConference pastConference = em.find(PastConference.class, conferenceNumber);

			//check if group is an owner
			if (pastConference == null || pastConference.getOwner() ==null ||  !pastConference.getOwner().equals(username)) {
				em.getTransaction().commit();
				em.close();
				return null;
			}

			userNames = pastConference.getInvitedUsersList();
		}
		
		Collections.sort(userNames,String.CASE_INSENSITIVE_ORDER);
		em.getTransaction().commit();
		em.close();
		return userNames.toArray(new String[userNames.size()]);
	}

	/**
	 * @param username
	 * @param conferenceNumber
	 * @return
	 */
	public String[] listBannedUsers(String username, Integer conferenceNumber) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		MeetMe meetMe=em.find(MeetMe.class, conferenceNumber);
		if (!meetMe.getOwner().equals(username)) {
			em.getTransaction().commit();
			em.close();
			return null;
		}

		List<String> userNames = meetMe.getBannedUserList();
		Collections.sort(userNames,String.CASE_INSENSITIVE_ORDER);
		em.getTransaction().commit();
		em.close();
		return userNames.toArray(new String[userNames.size()]);
	}

	/**
	 * @param userId
	 * @param confNo
	 * @return
	 */
	public ConferenceDetails getConferenceDetails(String userId, String pass, Integer confNo, AuthData authData) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		new AsteriskConferences(userId, pass, authData).cleenupConferences(em, userId);

		MeetMe meetMe = em.find(MeetMe.class, confNo);
		ConferenceDetails conferenceDetails = new ConferenceDetails();

		if (meetMe == null) {
			conferenceDetails.setConfNo(-1);

			PastConference pastConference = em.find(PastConference.class, confNo);
			if (pastConference == null) {
				em.getTransaction().commit();
				em.close();
				conferenceDetails.setConfNo(-1);
				return conferenceDetails;
			} else if (!pastConference.getOwner().equals(userId)) {
				em.getTransaction().commit();
				em.close();
				return null;
			} else {
				setupConferenceDetails(conferenceDetails, confNo, pastConference);
			}

			em.getTransaction().commit();
			em.close();
			
			return conferenceDetails;
		} // don't have rights
		//else if (!meetMe.getOwner().equals(userId)) {
			//em.getTransaction().commit();
			//em.close();
			//return null;
		//} 
		else {
			setupConferenceDetails(conferenceDetails, confNo, meetMe);
		}
		
		em.getTransaction().commit();
		em.close();
		return conferenceDetails;
	}

	private void setupConferenceDetails (ConferenceDetails conferenceDetails, Integer confNo, Object obj) {
		if (obj instanceof MeetMe) {
			MeetMe conf = (MeetMe)obj;
			
			conferenceDetails.setConfNo(confNo);
			conferenceDetails.setPin(conf.getPin());
			conferenceDetails.setAdminPin(conf.getAdminpin());
			conferenceDetails.setStartDate(conf.getStartDate());
			conferenceDetails.setEndDate(conf.getEndDate());
			conferenceDetails.setMaxUsers(conf.getMaxUsers());
			conferenceDetails.setUserCount(conf.getMembers());
			conferenceDetails.setAgenda(conf.getAgenda());
			conferenceDetails.setName(conf.getName());
			conferenceDetails.setOwner(conf.getOwner());
			conferenceDetails.setAccessType(conf.getAccessType());
			conferenceDetails.setPermanent(conf.isPermanent());
			conferenceDetails.setRecorded(conf.isRecorded());
		} else if (obj instanceof PastConference) {
			PastConference conf = (PastConference)obj;
			
			conferenceDetails.setConfNo(confNo);
			conferenceDetails.setPin(conf.getPin());
			conferenceDetails.setAdminPin(conf.getAdminpin());
			
			conferenceDetails.setStartDate(conf.getStartDate());
			conferenceDetails.setEndDate(conf.getEndDate());
			conferenceDetails.setMaxUsers(conf.getMaxUsers());
			conferenceDetails.setAgenda(conf.getAgenda());
			conferenceDetails.setName(conf.getName());
			conferenceDetails.setOwner(conf.getOwner());
			conferenceDetails.setAccessType(conf.getAccessType());
			conferenceDetails.setPermanent(conf.isPermanent());
			conferenceDetails.setRecorded(conf.isRecorded());
		}
	}
	

	/**
	 * @param userId
	 * @param confNo
	 * @return
	 */
	public String getRecordings(String userId, String pass, Integer confNo, AuthData authData) {

		EntityManager em=AsteriskConferenceUtils.getEntityManager();
		em.getTransaction().begin();

		new AsteriskConferences(userId, pass, authData).cleenupConferences(em, userId);

		PastConference pastConference=em.find(PastConference.class, confNo);
		if (pastConference == null) {
			em.getTransaction().commit();
			em.close();
			return "";
		} else if (!pastConference.getOwner().equals(userId)) {
			em.getTransaction().commit();
			em.close();
			return "";
		}

		String result = pastConference.getRecordings();

		em.getTransaction().commit();
		em.close();
		return result;
	}
	
	//#############################################################################################
	//DATABASE MANAGEMENT
	//#############################################################################################
	/**
	 * Reset database information
	 * @return operation status
	 */
	public static synchronized boolean resetDatabase() {
	    synchronized (log) {
		AsteriskConferenceUtils.clearDatabase();
		AsteriskConferenceUtils.setupConfferenceExtension(AsteriskConferenceUtils.CONFERENCE_NAME, AsteriskConferenceUtils.DEFAULT_CONTEXT);

		try {
		    AsteriskConferenceUtils.initOpenmeetings();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		//create admin user
		String res = AsteriskConferenceUtils.createUser(
				"voip_admin", "admin",
				"janny@man.poznan.pl",
				"Dariusz", "Janny",
				"voip_admin");	
		
	    }
	    return true;
	}
}
