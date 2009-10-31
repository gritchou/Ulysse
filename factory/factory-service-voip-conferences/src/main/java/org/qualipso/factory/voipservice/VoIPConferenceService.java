package org.qualipso.factory.voipservice;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryService;
import org.qualipso.factory.voipservice.entity.ConferenceDetails;
import org.qualipso.factory.voipservice.entity.ParticipantsInfo;

/**
 * This is VoIP conference service (factory interface)
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */
@Remote
@WebService(name = "VoIPConferenceService", targetNamespace = "http://org.qualipso.factory.ws/service/voip")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface VoIPConferenceService extends FactoryService {

	//#############################################################################################
	/*
	 *  USER STATUS AND MANAGEMENT IN CONFERENCE
	 */
	//#############################################################################################
	/**
	 * Removes this user from the conference room.
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo conference number
	 * @param userConferenceId userid in conference
	 * @return kick status (conference owner may kick user)
	 */
	@WebMethod
	@WebResult
	public boolean kickUser(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException;
	
	/**
	 * Bans user from entering the conference (kicks out of conference and adds
	 * to the ban list)
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo conference number
	 * @param userConferenceId userid in conference
	 * @return ban status
	 */
	@WebMethod
	@WebResult
	public boolean banUser(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException;

	/**
	 * Unban user
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo conference number
	 * @param voipUsername username for unban
	 * @return unban status
	 */
	@WebMethod
	@WebResult
	public boolean unbanUser(String userId, String pass, Integer confNo, String voipUsername)  throws VoIPConferenceServiceException;
	
	/**
	 * Check if user is banned
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo conference number
	 * @param userConferenceId userid in conference
	 * @return if is user banned
	 */
	@WebMethod
	@WebResult
	public boolean isUserBanned(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException;
	
	/**
	 * Lists all the users that are banned from a conference
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo selected conference number
	 * @return list of banned users
	 */
	@WebMethod
	@WebResult
	public String[] listBannedUsers(String userId, String pass, Integer confNo) throws VoIPConferenceServiceException;
	
	/**
	 * Stops sending voice from this user to the conference room.
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo conference number
	 * @param userConferenceId userid in conference
	 * @return mute status
	 */
	@WebMethod
	@WebResult
	public boolean muteUser(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException;

	/**
	 *  (Re)starts sending voice from this user to the conference room.
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo conference number
	 * @param userConferenceId userid in conference
	 * @return unmute status
	 */
	@WebMethod
	@WebResult
	public boolean unmuteUser(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException;
	
	/**
	 * Check if user is muted 
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo conference number
	 * @param userConferenceId userid in conference
	 * @return true when user is muted in conference
	 */
	@WebMethod
	@WebResult
	public boolean isUserMuted(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException;
	
	/**
	 * Check if user is talking during conference
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo conference number
	 * @param userConferenceId userid in conference
	 * @return true when user is talking during calling method
	 */
	@WebMethod
	@WebResult
	public boolean isUserTalking(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException;
	
	//#############################################################################################
	/*
	 *  CONFERENCE STATUS AND MANAGEMENT
	 */
	//#############################################################################################
	/**
	 * Lock conference
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo conference number
	 * @return lock conference status
	 */
	@WebMethod
	@WebResult
	public boolean lockConf(String userId, String pass, Integer confNo) throws VoIPConferenceServiceException;

	/**
	 * Unlock conference
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo conference number
	 * @return unlock conference status
	 */
	@WebMethod
	@WebResult
	public boolean unlockConf(String userId, String pass, Integer confNo) throws VoIPConferenceServiceException;

	/**
	 * Ends the selected conference and adds it to the past conferences list
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo conference number
	 * @return returns true if the conference was succesfully ended
	 */
	@WebMethod
	@WebResult
	public boolean endConference(String userId, String pass, Integer confNo) throws VoIPConferenceServiceException;
	
	//#############################################################################################
	// USER PROFILE MANAGEMENT
	//#############################################################################################
	/**
	 * Returns the list of all users that have VoIP accounts on the server
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @return list of VoIP users
	 */
	@WebMethod
	@WebResult
	public String[] listUsers(String userId, String pass) throws VoIPConferenceServiceException;
	
	/**
	 * Creates a new standard VoIP account (SIP and IAX)
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param voipUsername voip username account
	 * @param secret voip password for account 
	 * @param email user email
	 * @param firstname user firstname
	 * @param lastname user last name
	 * @param qualipsoUserId qualipso user id
	 * @return created username account
	 */
	@WebMethod
	@WebResult
	public String activateVoipProfile(String userId, String pass,
			String voipUsername,
			String secret, String email, String firstname, String lastname,
			String qualipsoUserId) throws VoIPConferenceServiceException;
	
	/**
	 * Removes VoIP user account (SIP and IAX)
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param voipUsername voip username
	 * @return 
	 */
	@WebMethod
	@WebResult
	public boolean deactivateVoipProfile(String userId, String pass, String voipUsername) throws VoIPConferenceServiceException;
	
	/**
	 * Check if VoIP profile exists
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param voipUsername voip username
	 * @return true when profile exists, false in other case
	 */
	@WebMethod
	@WebResult
	public boolean isVoIPProfileExists(String userId, String pass, String voipUsername) throws VoIPConferenceServiceException;
	
	/**
	 * Check if user can be logged into system
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo conference number
	 * @return status
	 */
	@WebMethod
	@WebResult
	public String loginStatus(String userId, String pass, String confNo) throws VoIPConferenceServiceException;
	
	/**
	 * Get the voipUsername of selected conference participant
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo selected conference number
	 * @param userConferenceId userid in conference
	 * @return voip username for selected user
	 */
	@WebMethod
	@WebResult
	public String getVoipUsername(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException;

	/**
	 * Get qualipso username from requested asterisk user
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param voipUsername voip username
	 * @return qualispo username
	 */
	@WebMethod
	@WebResult
	public String usernameFromQualipsoUsers(String userId, String pass, String voipUsername) throws VoIPConferenceServiceException;
	
	//#############################################################################################
	//CONFERENCE MANAGEMENT
	//#############################################################################################
	/**
	 * Edit the conference room settings
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo  Conference room number
	 * @param owner New owner of the conference
	 * @param accessType Conference AccessType
	 * @param permanent Is the conference permanent
	 * @param pin Conference access pin number
	 * @param adminpin Conference admin access pin number
	 * @param startDate When the conference begins
	 * @param endDate When the conference ends
	 * @param maxUsers Maximum number of participants
	 * @param name Name of the conference
	 * @param agenda Agenda for the conference
	 * @param recorded  Is the conference recorded
	 * @return -1 when error occured, in other case return edited conference number
	 */
	@WebMethod
	@WebResult
	public Integer editConference(String userId, String pass, Integer confNo,
			String owner, short accessType, boolean permanent, String pin,
			String adminpin, Long startDate, Long endDate, Integer maxUsers,
			String name, String agenda, boolean recorded) throws VoIPConferenceServiceException;

	/**
	 * Edit the conference room settings (humna readable format)
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo  Conference room number
	 * @param owner New owner of the conference
	 * @param accessType Conference AccessType
	 * @param permanent Is the conference permanent
	 * @param pin Conference access pin number
	 * @param adminpin Conference admin access pin number
	 * @param startDateHR When the conference begins (human readable format)
	 * @param endDateHR When the conference ends (human readable format)
	 * @param maxUsers Maximum number of participants
	 * @param name Name of the conference
	 * @param agenda Agenda for the conference
	 * @param recorded  Is the conference recorded
	 * @return -1 when error occured, in other case return edited conference number
	 * @return
	 */
	@WebMethod
	@WebResult
	public Integer editConferenceHR(String userId, String pass, Integer confNo,
			String owner, short accessType, boolean permanent, String pin,
			String adminpin, String startDateHR, String endDateHR, Integer maxUsers,
			String name, String agenda, boolean recorded) throws VoIPConferenceServiceException;

	/**
	 * Create conference
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param owner Owner name
	 * @param accessType Conference access type
	 * @param permanent Is conference permanent
	 * @param pin Conference access pin
	 * @param adminpin Conference admin access pin
	 * @param startDate When the conference begins
	 * @param endDate When the conference ends
	 * @param maxUsers Maximum number of participants
	 * @param name Name of the conference
	 * @param agenda Conference agenda
	 * @param recorded Is the conference recorded
	 * @param project project reference
	 * @return conference number (-1 in case of error)
	 */
	@WebMethod
	@WebResult
	public Integer createConference(String userId, String pass, String owner,
			short accessType, boolean permanent, String pin, String adminpin,
			Long startDate, Long endDate, Integer maxUsers, String name,
			String agenda, boolean recorded, String project) throws VoIPConferenceServiceException;

	/**
	 * Create conference (human readable format)
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param owner Owner name
	 * @param accessType Conference access type
	 * @param permanent Is conference permanent
	 * @param pin Conference access pin
	 * @param adminpin Conference admin access pin
	 * @param startDateHR When the conference begins
	 * @param endDateHR When the conference ends
	 * @param maxUsers Maximum number of participants
	 * @param name Name of the conference
	 * @param agenda Conference agenda
	 * @param recorded Is the conference recorded
	 * @param project project reference
	 * @return conference number (-1 in case of error)
	 */
	@WebMethod
	@WebResult
	public Integer createConferenceHR(String userId, String pass, String owner,
			short accessType, boolean permanent, String pin, String adminpin,
			String startDateHR, String endDateHR, Integer maxUsers,
			String name, String agenda, boolean recorded, String project) throws VoIPConferenceServiceException;
	
	/**
	 * Adds user to the conference invited list
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param voipUsername voip username
	 * @param confNo selected conference number 
	 * @return false if error occured
	 */
	@WebMethod
	@WebResult
	public boolean addUserToConference(String userId, String pass, String voipUsername, Integer confNo) throws VoIPConferenceServiceException;
	
	/**
	 * Remove user from conference invited users list
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param voipUsername voip username
	 * @param confNo selected conference number 
	 * @return false if error occured
	 */
	@WebMethod
	@WebResult
	public boolean removeUserFromConference(String userId, String pass, String voipUsername, Integer confNo) throws VoIPConferenceServiceException;	
	
	/**
	 * Gets details of the conference room
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo selected conference number
	 * @return ConferenceDetails object with information about conference room
	 */
	@WebMethod
	@WebResult
	public ConferenceDetails getConferenceDetails(String userId, String pass, Integer confNo) throws VoIPConferenceServiceException;
	
	/**
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param voipUsername voip username
	 * @param type type of conferences
	 * @param project project identifier
	 * @return list of conferences
	 */
	@WebMethod
	@WebResult
	public ConferenceDetails[] getConferencesDetailsList(String userId, String pass, String voipUsername, String type, String project) throws VoIPConferenceServiceException;

	/**
	 * Remove existing conference
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNumber conference number
	 * @return removing status
	 */
	@WebMethod
	@WebResult
	public Integer removeConference(String userId, String pass, Integer confNumber) throws VoIPConferenceServiceException;
	
	/**
	 * Get the participants list of a conference
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo conference number
	 * @return list of participants
	 */
	@WebMethod
	@WebResult
	public Integer[] listParticipants(String userId, String pass, Integer confNo) throws VoIPConferenceServiceException;

	/**
	 * Get list in humanreadable format 
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo selected conference number
	 * @return participants list in human readable format
	 */
	@WebMethod
	@WebResult
	public ParticipantsInfo[] getParticipantsListHR(String userId, String pass,Integer confNo) throws VoIPConferenceServiceException;

	/**
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo selected conference number
	 * @return list of invited users
	 */
	@WebMethod
	@WebResult
	public String[] listInvitedUsers(String userId, String pass, Integer confNo) throws VoIPConferenceServiceException;
	
	/**
	 * Get the list of all active conferences
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param project project identifier
	 * @return list of all active conferences
	 */
	@WebMethod
	@WebResult
	public Integer[] listConfferences(String userId, String pass, String project) throws VoIPConferenceServiceException;
	
	/**
	 * Check if selected conference is past
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param voipUsername voip username
	 * @param confNo selected conference number
	 * @param project project reference
	 * @return conference status
	 */
	@WebMethod
	@WebResult
	public boolean isPastConference(String userId, String pass, String voipUsername, Integer confNo, String project) throws VoIPConferenceServiceException;
	
	/**
	 * Get the list of all past conferences of a user
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param voipUsername voip username
	 * @param project project reference
	 * @return list of conference
	 */
	@WebMethod
	@WebResult
	public Integer[] listPastConfferences(String userId, String pass, String voipUsername, String project) throws VoIPConferenceServiceException;
	
	/**
	 * Gets paths to conference recordings
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo selected conference number
	 * @return object containing paths to conference recordings
	 */
	@WebMethod
	@WebResult
	public String getRecordings(String userId, String pass, Integer confNo) throws VoIPConferenceServiceException;
	
	/**
	 * Get the list of conferences by owner
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param voipUsername voip username
	 * @param project project reference
	 * @return list of user's conferences
	 */
	@WebMethod
	@WebResult
	public Integer[] listConfferencesByOwner(String userId, String pass, String voipUsername, String project) throws VoIPConferenceServiceException;

	/**
	 * Get the list of conferences user invited to
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param voipUsername voip username
	 * @param project project reference
	 * @return list of conferences
	 */
	@WebMethod
	@WebResult
	public Integer[] listConfferencesByInvitation(String userId, String pass, String voipUsername, String project) throws VoIPConferenceServiceException;

	/**
	 * Get the list of public conferences
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param project project reference
	 * @return list of conferences
	 */
	@WebMethod
	@WebResult
	public Integer[] listPublicConfferences(String userId, String pass, String project) throws VoIPConferenceServiceException;
	
	/**
	 * Sets the conference list of invited users
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo conference number
	 * @param userList List of users to invite
	 * @return false if error occured
	 */
	@WebMethod
	@WebResult
	public boolean setConferenceUserList(String userId, String pass, Integer confNo, String[] userList) throws VoIPConferenceServiceException;
	
	/**
	 * Get the join date of a conference participant
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo selected conference number
	 * @param userConferenceId userid in conference
	 * @return join date in seconds
	 */
	@WebMethod
	@WebResult
	public Long getJoinDate(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException;

	/**
	 * Get the join date of a conference participant in human readable format
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @param confNo selected conference number
	 * @param userConferenceId userid in conference
	 * @return join date in human readable format
	 */
	@WebMethod
	@WebResult
	public String getJoinDateHR(String userId, String pass, Integer confNo, Integer userConferenceId) throws VoIPConferenceServiceException;
	
	//#############################################################################################
	//DATABASE MANAGEMENT
	//#############################################################################################

	/**
	 * Resets the database to it's initial settings (removes all users and conferences)
	 * @param userId userid (authenticate in calling method) 
	 * @param pass password (authenticate in calling method)
	 * @return status
	 */
	@WebMethod
	@WebResult
	public boolean resetDatabase(String userId, String pass) throws VoIPConferenceServiceException;
	
	//#############################################################################################
	//#############################################################################################
	/**
	 * Get service version
	 * @return service version
	 */
	@WebMethod
	@WebResult
	public String getServiceVersion() throws VoIPConferenceServiceException;
	
	/**
	 * Get asterisk version
	 * @return asterisk version
	 */
	@WebMethod
	@WebResult
	public String getAsteriskVersion() throws VoIPConferenceServiceException;
	
	/**
	 * Check database status
	 * @return database status
	 */
	@WebMethod
	@WebResult
	public String isDBOpen() throws VoIPConferenceServiceException;	
}
