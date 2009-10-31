package org.qualipso.factory.voipservice.manager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.asteriskjava.live.AsteriskChannel;
import org.asteriskjava.live.AsteriskQueueEntry;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.AsteriskServerListener;
import org.asteriskjava.live.CallerId;
import org.asteriskjava.live.DefaultAsteriskServer;
import org.asteriskjava.live.ManagerCommunicationException;
import org.asteriskjava.live.MeetMeRoom;
import org.asteriskjava.live.MeetMeUser;
import org.asteriskjava.live.internal.AsteriskAgentImpl;
import org.asteriskjava.manager.ManagerEventListener;
import org.asteriskjava.manager.event.ConnectEvent;
import org.asteriskjava.manager.event.ManagerEvent;
import org.qualipso.factory.voipservice.VoIPConferenceServiceException;
import org.qualipso.factory.voipservice.conference.AsteriskConferenceSettings;
import org.qualipso.factory.voipservice.entity.ConferenceUser;
import org.qualipso.factory.voipservice.entity.MeetMe;
import org.qualipso.factory.voipservice.entity.SipConf;
import org.qualipso.factory.voipservice.security.AuthenticationModule;
import org.qualipso.factory.voipservice.utils.AsteriskConferenceUtils;
import org.qualipso.factory.voipservice.utils.AsteriskConferenceUtils.AstmanConnection;

/**
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @author <a href="mailto:debe@man.poznan.pl">Marcin Wrzos</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */
public class AsteriskJavaListener implements AsteriskServerListener, PropertyChangeListener, ManagerEventListener {
	private AsteriskServer asteriskServer = null;
	private static Logger log = Logger.getLogger(AsteriskJavaListener.class);

	/**
	 * Init asterisk listener instance
	 * @author <a href="mailto:debe@man.poznan.pl">Marcin Wrzos</a>
	 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
	 */
	private static class Instance {
		static AsteriskJavaListener instance = null;
		
		public static AsteriskJavaListener getInstance() throws VoIPConferenceServiceException {
			if (instance == null) {
				instance = new AsteriskJavaListener();
			}
			return instance;
		}		
	}

	/**
	 * Return asterisk listener instance
	 * @return
	 */
	public static AsteriskJavaListener getInstance() throws VoIPConferenceServiceException {
		return Instance.getInstance();
	}

	
	/**
	 * Get asterisk version
	 * @return asterisk version
	 */
	public String getVersion() {
		return asteriskServer.getVersion();
	}
	
	/**
	 * Private constructor 
	 */
	private AsteriskJavaListener() throws VoIPConferenceServiceException {
		AstmanConnection ac = AsteriskConferenceUtils.getAstmanListenerConnection();
		asteriskServer = new DefaultAsteriskServer(ac.getHost(), ac.getLogin(), ac.getPass());
		try {
			//log.debug("asterisk version: " + asteriskServer.getVersion());
		} catch(ManagerCommunicationException mcx) {
			log.error(mcx.getMessage()
					+"\n; host:\'" + ac.getHost() + "\'"
					+";login:\'" + ac.getLogin() + "\'"
					+";pass:\'" + ac.getPass() + "\'");
		}
		try {
			update();
			asteriskServer.getManagerConnection().addEventListener(this);
			asteriskServer.addAsteriskServerListener(this);
		} catch(ManagerCommunicationException mcx) {
			log.error(mcx.getMessage());
		} catch (VoIPConferenceServiceException e) {
			throw new VoIPConferenceServiceException(e.getLocalizedMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.asteriskjava.live.AsteriskServerListener#onNewMeetMeUser(org.asteriskjava.live.MeetMeUser)
	 */
	public void onNewMeetMeUser(MeetMeUser meetMeUser) {
		EntityManager em = AsteriskConferenceUtils.getEntityManager();

		em.getTransaction().begin();
		MeetMe meetMe = em.find(MeetMe.class, Integer.parseInt(meetMeUser.getRoom().getRoomNumber()));

		String name = null;
		CallerId callerId = meetMeUser.getChannel().getCallerId();
		if (meetMeUser.getChannel().getName().startsWith("IAX2/")) {
			name = callerId.getName();
		} else {
			name = callerId.getNumber();
		}
		SipConf sipConf = em.find(SipConf.class, name);

		if (meetMe != null && sipConf != null) {
			ConferenceUser.ConferenceUserPK pk = new ConferenceUser.ConferenceUserPK();
			pk.setMeetMe(meetMe);
			pk.setId(meetMeUser.getUserNumber());
			ConferenceUser cu = em.find(ConferenceUser.class,pk);
			
			if (cu != null) {
				cu.setJoinDate(meetMeUser.getDateJoined().getTime());
				cu.setMuted(meetMeUser.isMuted());
				cu.setTalking(meetMeUser.isTalking());
				cu.setSipConf(sipConf);
			} else {
				cu = new ConferenceUser();
				cu.setId(meetMeUser.getUserNumber());
				cu.setMeetMe(meetMe);
				cu.setSipConf(sipConf);
				cu.setJoinDate(meetMeUser.getDateJoined().getTime());
				cu.setMuted(meetMeUser.isMuted());
				cu.setTalking(false);
				em.persist(cu);
			}
		}
		em.getTransaction().commit();

		em.close();
		meetMeUser.addPropertyChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
		log.debug("start propertyChange");
		if ("state".equals(propertyChangeEvent.getPropertyName()) &&
			"LEFT".equals(propertyChangeEvent.getNewValue().toString()) &&
			propertyChangeEvent.getSource() instanceof MeetMeUser) {

			log.debug("### start propertyChange - LEFT");
			
			MeetMeUser meetMeUser=(MeetMeUser)propertyChangeEvent.getSource();
			EntityManager em=AsteriskConferenceUtils.getEntityManager();
			em.getTransaction().begin();

			MeetMe meetMe=em.find(MeetMe.class, Integer.parseInt(meetMeUser.getRoom().getRoomNumber()));

			String name;
			if (meetMeUser.getChannel().getName().startsWith("IAX2/")) {
				name=meetMeUser.getChannel().getCallerId().getName();
			} else {
				name=meetMeUser.getChannel().getCallerId().getNumber();
			}
			SipConf sipConf=em.find(SipConf.class,name);

			if (meetMe!=null && sipConf !=null) {
				ConferenceUser.ConferenceUserPK pk=new ConferenceUser.ConferenceUserPK();
				pk.setMeetMe(meetMe);
				pk.setId(meetMeUser.getUserNumber());
				ConferenceUser cu=em.find(ConferenceUser.class,pk);
				if (cu!=null) {
					em.remove(cu);
				}
			}
			em.getTransaction().commit();
			em.close();
		}

		if ("talking".equals(propertyChangeEvent.getPropertyName()) ||
			"muted".equals(propertyChangeEvent.getPropertyName())) {
			if (propertyChangeEvent.getSource() instanceof MeetMeUser) {
				log.debug("### start propertyChange - muted");
				
				MeetMeUser meetMeUser = (MeetMeUser)propertyChangeEvent.getSource();
				EntityManager em = AsteriskConferenceUtils.getEntityManager();
				em.getTransaction().begin();

				MeetMe meetMe = em.find(MeetMe.class, Integer.parseInt(meetMeUser.getRoom().getRoomNumber()));

				String name = "";
				if (meetMeUser.getChannel().getName().startsWith("IAX2/")) {
					name = meetMeUser.getChannel().getCallerId().getName();
				} else {
					name = meetMeUser.getChannel().getCallerId().getNumber();
				}
				
				log.debug("### start propertyChange - name=" + name);
				
				SipConf sipConf = em.find(SipConf.class, name);

				if (meetMe != null && sipConf != null) {
					ConferenceUser.ConferenceUserPK pk=new ConferenceUser.ConferenceUserPK();
					pk.setMeetMe(meetMe);
					pk.setId(meetMeUser.getUserNumber());
					ConferenceUser conferenceUser=em.find(ConferenceUser.class, pk);
					
					log.debug("### start propertyChange - conferenceUser=" + conferenceUser);
					if (conferenceUser == null) {
						conferenceUser = new ConferenceUser();
						conferenceUser.setId(meetMeUser.getUserNumber());
						conferenceUser.setMeetMe(meetMe);
						conferenceUser.setSipConf(sipConf);
						conferenceUser.setJoinDate(meetMeUser.getDateJoined().getTime());
						conferenceUser.setMuted(meetMeUser.isMuted());
						conferenceUser.setTalking(meetMeUser.isTalking());
						em.persist(conferenceUser);
						meetMeUser.addPropertyChangeListener(this);
					} else {
						conferenceUser.setJoinDate(meetMeUser.getDateJoined().getTime());
						conferenceUser.setMuted(meetMeUser.isMuted());
						conferenceUser.setTalking(meetMeUser.isTalking());
						conferenceUser.setSipConf(sipConf);
					}
				}
				em.getTransaction().commit();
				em.close();
			}
		}
	}

	/**
	 * Update qualipso voip conferences data 
	 */
	private void update() throws VoIPConferenceServiceException {
		//print asterisk version
		try {
			log.debug("asterisk version: " + asteriskServer.getVersion());
		} catch (ManagerCommunicationException e) {
			log.error(e.getMessage());
			//e.printStackTrace();
		}

		//get entity manager
		EntityManager em = AsteriskConferenceUtils.getEntityManager();
		
		AuthenticationModule.checkDB(em);
		
		try {
			em.getTransaction().begin();
			Query query = em.createQuery("DELETE FROM ConferenceUser cu");
			query.executeUpdate();
			em.getTransaction().commit();
		} catch (Exception e) {
			log.error("deleting from conference user: " + e.getLocalizedMessage());
			em.getTransaction().rollback();
			
			throw new VoIPConferenceServiceException(e.getLocalizedMessage());
		}
				
		em.getTransaction().begin();
		try {
			Collection<MeetMeRoom> meetMeRooms = asteriskServer.getMeetMeRooms();
			//log.debug("### [Qualipso] meetmerooms: "+meetMeRooms.size());

			for (MeetMeRoom meetMeRoom : meetMeRooms) {
				MeetMe meetMe=em.find(MeetMe.class,Integer.parseInt(meetMeRoom.getRoomNumber()));
				for (MeetMeUser user : meetMeRoom.getUsers()) {
					String name = null;
					if (user.getChannel().getName().startsWith("IAX2/")) {
						name = user.getChannel().getCallerId().getName();
					} else {
						name = user.getChannel().getCallerId().getNumber();
					}
					SipConf sipConf = em.find(SipConf.class,name);

					if (meetMe!=null && sipConf !=null) {
						ConferenceUser cu=new ConferenceUser();
						cu.setId(user.getUserNumber());
						cu.setMeetMe(meetMe);
						cu.setSipConf(sipConf);
						cu.setJoinDate(user.getDateJoined().getTime());
						cu.setMuted(user.isMuted());
						cu.setTalking(user.isTalking());
						em.persist(cu);
					}
					user.addPropertyChangeListener(this);
				}
			}
			em.getTransaction().commit();
		} catch (ManagerCommunicationException mcx) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			log.error("AsteriskJavaListener onManagerEvent:" + mcx.getMessage());
		} finally {
			em.close();
		}
	}

	
	/* (non-Javadoc)
	 * @see org.asteriskjava.manager.ManagerEventListener#onManagerEvent(org.asteriskjava.manager.event.ManagerEvent)
	 */
	public void onManagerEvent(ManagerEvent event) {
		if (event instanceof ConnectEvent) {
			try {
				update();
			} catch (VoIPConferenceServiceException e) {
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.asteriskjava.live.AsteriskServerListener#onNewAgent(org.asteriskjava.live.internal.AsteriskAgentImpl)
	 */
	public void onNewAgent(AsteriskAgentImpl arg0) {
		log.debug("event: onNewAgent");
	}

	/* (non-Javadoc)
	 * @see org.asteriskjava.live.AsteriskServerListener#onNewQueueEntry(org.asteriskjava.live.AsteriskQueueEntry)
	 */
	public void onNewQueueEntry(AsteriskQueueEntry arg0) {
		log.debug("event: onNewQueueEntry");
	}

	/* (non-Javadoc)
	 * @see org.asteriskjava.live.AsteriskServerListener#onNewAsteriskChannel(org.asteriskjava.live.AsteriskChannel)
	 */
	public void onNewAsteriskChannel(AsteriskChannel arg0) {
		log.debug("event: onNewAsteriskChannel");
	}
}
