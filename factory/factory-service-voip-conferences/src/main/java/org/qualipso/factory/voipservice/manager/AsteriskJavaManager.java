package org.qualipso.factory.voipservice.manager;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.asteriskjava.live.AsteriskServer;
import org.asteriskjava.live.DefaultAsteriskServer;
import org.asteriskjava.live.ManagerCommunicationException;
import org.asteriskjava.live.MeetMeRoom;
import org.asteriskjava.live.MeetMeUser;
import org.asteriskjava.manager.ManagerConnectionState;
import org.qualipso.factory.voipservice.utils.AsteriskConferenceUtils;
import org.qualipso.factory.voipservice.utils.AsteriskConferenceUtils.AstmanConnection;

/**
 * Manage user connection in asterisk server (kick, mute, unmute user; lock/unlock conference)
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */

public class AsteriskJavaManager {

	/**
	 * Comment for <code>asteriskServer</code>
	 */
	private AsteriskServer asteriskServer = null;

	/**
	 * Comment for <code>clog</code>
	 */
	private static Logger log = Logger.getLogger(AsteriskJavaManager.class);

	/**
	 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
	 */
	private static class Instance {
		static AsteriskJavaManager instance = new AsteriskJavaManager();
	}

	/**
	 * private constructor
	 */
	private AsteriskJavaManager() {
		//get connection data
		AstmanConnection ac = AsteriskConferenceUtils.getAstmanManagerConnection();

		//init asterisk server
		asteriskServer = new DefaultAsteriskServer(ac.getHost(), ac.getLogin(), ac.getPass());
		try {
			//log information about asterisk version
			log.debug("asterisk version: " + asteriskServer.getVersion());
		} catch (Exception e) {
			log.error(e.getMessage() 
					+ "\n;host: " + ac.getHost() 
					+ ";login: " + ac.getLogin()
					+ ";pass: " + ac.getPass()
			);
		}
	}

	/**
	 * @return
	 * @throws ManagerCommunicationException
	 */
	public static AsteriskJavaManager getInstance() throws ManagerCommunicationException {
		log.debug("manager state...");
		ManagerConnectionState managerState = Instance.instance.asteriskServer.getManagerConnection().getState();
		
		if (managerState.equals(ManagerConnectionState.INITIAL)) {
			Instance.instance = new AsteriskJavaManager();
		} 
		managerState = Instance.instance.asteriskServer.getManagerConnection().getState();
		
		log.debug("manager state is: " + managerState);
		if (!managerState.equals(ManagerConnectionState.CONNECTED)) {
			throw new ManagerCommunicationException("connection status: " + managerState, null);
		} 

		return Instance.instance;
	}

	/**
	 * Lock/unlock conference
	 * @param confNo
	 * @param confNo
	 * @return
	 * @throws ManagerCommunicationException
	 */
	private boolean lock_unlockConf(Integer confNo, boolean isLock) throws ManagerCommunicationException {
		MeetMeRoom room = asteriskServer.getMeetMeRoom(confNo.toString());
		if (true == isLock) {
			room.lock();
		} else {
			room.unlock();
		}

		return true;
	}

	/**
	 * Kick user from conference
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 * @throws ManagerCommunicationException
	 */
	public boolean kickUser(Integer confNo, Integer userConferenceId) throws ManagerCommunicationException {
		MeetMeRoom room = asteriskServer.getMeetMeRoom(confNo.toString());
		Collection<MeetMeUser> usersCollection = room.getUsers();

		for (MeetMeUser meetMeUser : usersCollection) {
			if (meetMeUser.getUserNumber().equals(userConferenceId)) {
				meetMeUser.kick();
				return true;
			}
		}

		return false;
	}

	
	/**
	 * Mute / unmute user
	 * @param confNo
	 * @param userConferenceId
	 * @param isMute
	 * @return
	 * @throws ManagerCommunicationException
	 */
	private boolean mute_unmuteUser(Integer confNo, Integer userConferenceId, boolean isMute) throws ManagerCommunicationException {
		MeetMeRoom room = asteriskServer.getMeetMeRoom(confNo.toString());
		Collection<MeetMeUser> usersCollection = room.getUsers();

		for (MeetMeUser meetMeUser : usersCollection) {
			if (meetMeUser.getUserNumber().equals(userConferenceId)) {
				if (true == isMute) {
					meetMeUser.mute();
				} else {
					meetMeUser.unmute();
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 * @throws ManagerCommunicationException
	 */
	public boolean muteUser(Integer confNo, Integer userConferenceId) throws ManagerCommunicationException {
		return mute_unmuteUser(confNo, userConferenceId, true);
	}

	/**
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 * @throws ManagerCommunicationException
	 */
	public boolean unmuteUser(Integer confNo, Integer userConferenceId) throws ManagerCommunicationException {
		return mute_unmuteUser(confNo, userConferenceId, false);
	}
	
	/**
	 * Lock conference
	 * @param confNo
	 * @return
	 * @throws ManagerCommunicationException
	 */
	public boolean lockConf(Integer confNo) throws ManagerCommunicationException {
		return lock_unlockConf(confNo, true);
	}
	
	/**
	 * Unlock conference
	 * @param confNo
	 * @return
	 * @throws ManagerCommunicationException
	 */
	public boolean unlockConf(Integer confNo) throws ManagerCommunicationException {
		return lock_unlockConf(confNo, false);
	}
	
	public static void main (String[] argc) {
		try {
			AsteriskJavaManager.getInstance();
		} catch (ManagerCommunicationException e) {
			e.printStackTrace();
		}
	}
	
}
