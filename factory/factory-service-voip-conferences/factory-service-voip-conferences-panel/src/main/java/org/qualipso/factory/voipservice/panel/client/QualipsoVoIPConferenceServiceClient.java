package org.qualipso.factory.voipservice.panel.client;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.qualipso.factory.voipservice.client.ws.ConferenceDetails;
import org.qualipso.factory.voipservice.client.ws.IntArray;
import org.qualipso.factory.voipservice.client.ws.StringArray;
import org.qualipso.factory.voipservice.client.ws.VoIPConferenceService;
import org.qualipso.factory.voipservice.client.ws.VoIPConferenceService_Service;
import org.qualipso.factory.voipservice.panel.util.PanelTld;

public class QualipsoVoIPConferenceServiceClient {
	private static Logger log = Logger.getLogger(QualipsoVoIPConferenceServiceClient.class);
	
	//private org.qualipso.factory.voipservice.stub.QualipsoVoIPConferenceServiceStub asteriskStub;
	private VoIPConferenceService_Service service_service;
	private VoIPConferenceService service;

	/**
	 * @param serviceEndpoint
	 */
	public QualipsoVoIPConferenceServiceClient(String serviceEndpoint) {
		try {
		    service_service = new VoIPConferenceService_Service(new URL(serviceEndpoint), new QName("http://org.qualipso.factory.ws/service/voip", "VoIPConferenceService"));
		    service = service_service.getVoIPConferenceServicePort();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param userId
	 * @param username
	 * @return
	 */
	public List<Integer> getMyConferencesList(String userId, String pass, String username, String project) {
		try {
			IntArray response = service.listConfferencesByOwner(userId, pass, username, project);
			List<Integer> list = response.getItem();

			return list;
		} catch (Exception rex) {
			rex.printStackTrace();

			return null;
		}
	}

	/**
	 * @param userId
	 * @param username
	 * @return
	 */
	public List<Integer> getInvitedToConferencesList(String userId, String pass,String username, String project) {
		try {
			IntArray response = service.listConfferencesByInvitation(userId, pass, username, project);
			List<Integer> list = response.getItem();

			return list;
		} catch (Exception rex) {
			rex.printStackTrace();
			return null;
		}
	}

	/**
	 * @param userId
	 * @return
	 * @throws org.qualipso.factory.voipservice.stub.QualipsoVoIPConferenceServiceException 
	 */
	public List<Integer> getPublicConferencesList(String userId, String pass, String project) {
		try {
			IntArray response = service.listPublicConfferences(userId, pass, project);
			List<Integer> list = response.getItem();

			return list;
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param userId
	 * @param username
	 * @return
	 */
	public List<Integer> getPastConferencesList(String userId, String pass, String username, String project) {
		try {
			IntArray response = service.listPastConfferences(userId, pass, username, project);
			List<Integer> list = response.getItem();
			return list;
		}  catch (Exception e) {
		    e.printStackTrace();
		    return null;
		}
	}

	/**
	 * @param userId
	 * @param username
	 * @return
	 */
	public List<ConferenceDetails> getMyConferencesDetailsList(String userId, String pass, String username, String project) {
		List<Integer> list = getMyConferencesList(userId, pass, username, project);

		if (list == null) {
			return new ArrayList<ConferenceDetails>();
		}

		List<ConferenceDetails> detailsList = new ArrayList<ConferenceDetails>(list.size());

		for (Integer confNo : list) {
			ConferenceDetails details = getConferenceDetails(userId, pass, confNo);
			detailsList.add(details);
		}

		return detailsList;
	}

	/**
	 * @param userId
	 * @param username
	 * @return
	 */
	public List<ConferenceDetails> getInvitedToConferencesDetailsList(String userId, String pass,String username, String project) {
		List<Integer> list = getInvitedToConferencesList(userId, pass,username, project);

		if (list == null) {
			return new ArrayList<ConferenceDetails>();
		}

		List<ConferenceDetails> detailsList = new ArrayList<ConferenceDetails>(list.size());

		for (Integer confNo : list) {
			ConferenceDetails details = getConferenceDetails(userId, pass, confNo);
			detailsList.add(details);
		}

		return detailsList;
	}

	/**
	 * @param userId
	 * @return
	 */
	public List<ConferenceDetails> getPublicConferencesDetailsList(String userId, String pass, String project) {
		List<Integer> list = getPublicConferencesList(userId, pass, project);

		if (list == null) {
			return new ArrayList<ConferenceDetails>();
		}

		List<ConferenceDetails> detailsList = new ArrayList<ConferenceDetails>(list.size());

		for (Integer confNo : list) {
			ConferenceDetails details = getConferenceDetails(userId, pass, confNo);
			detailsList.add(details);
		}

		return detailsList;
	}

	/**
	 * @param userId
	 * @param username
	 * @return
	 */
	public List<ConferenceDetails> getPastConferencesDetailsList(String userId, String pass,String username, String project) {
		List<Integer> list = getPastConferencesList(userId, pass, username, project);

		if (list == null) {
			return new ArrayList<ConferenceDetails>();
		}

		List<ConferenceDetails> detailsList = new ArrayList<ConferenceDetails>(list.size());

		for (Integer confNo : list) {
			ConferenceDetails details = getConferenceDetails(userId, pass, confNo);
			detailsList.add(details);
		}

		return detailsList;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @return
	 */
	public ConferenceDetails getConferenceDetails(String userId, String pass,Integer confNo) {
		try {
			ConferenceDetails response4 = service.getConferenceDetails(userId, pass, confNo);

			return response4;
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return null;
	}


	/**
	 * @param userId
	 * @param confNo
	 * @return
	 */
	public String getPastConferenceRecordings(String userId, String pass, Integer confNo) {
		try {
			String resp=service.getRecordings(userId, pass, confNo);
			return resp;
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return null;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @return
	 */
	public List<String> getPastConferenceInvitedUsers(String userId, String pass,Integer confNo) {
		try {
			StringArray response3 = service.listInvitedUsers(userId, pass, confNo);
			List<String> list = response3.getItem();
			return list;
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return null;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @return
	 */
	public List<String> getInvitedUsersList(String userId, String pass,Integer confNo) {
		try {
			StringArray response3 = service.listInvitedUsers(userId, pass, confNo);
			List<String> list = response3.getItem();
			return list;
		} catch (Exception rex) {
			rex.printStackTrace();
		}
		return null;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @return
	 */
	public List<String> getBannedUsersList(String userId, String pass,Integer confNo) {
		try {
			StringArray response3 = service.listBannedUsers(userId, pass, confNo);
			List<String> list = response3.getItem();

			return list;
		} catch (Exception rex) {
			rex.printStackTrace();
		}
		return null;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @return
	 */
	public List<ParticipantsInfo> getParticipantsList(String userId, String pass,Integer confNo) {
		try {
			IntArray response3 = service.listParticipants(userId, pass, confNo);
			List<Integer> usersList = response3.getItem();
			List<ParticipantsInfo> list = new ArrayList<ParticipantsInfo>();

			if (usersList != null) {
				for (int userConferenceId : usersList) {
					ParticipantsInfo participantsInfo = new ParticipantsInfo();

					participantsInfo.id = userConferenceId;
					participantsInfo.username = getUserName(userId, pass, confNo, userConferenceId);
					participantsInfo.time = PanelTld.formatDate(getJoinDate(userId, pass, confNo, userConferenceId));
					participantsInfo.isBanned = isUserBanned(userId, pass, confNo, userConferenceId);
					participantsInfo.isTalking = isUserTalking(userId, pass, confNo, userConferenceId);
					participantsInfo.isMuted = isUserMuted(userId, pass, confNo, userConferenceId);
					list.add(participantsInfo);
				}
			}

			return list;
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return null;
	}

	/**
	 * @param userId
	 * @return
	 */
	public List<String> getUserList(String userId, String pass) {
		try {

			StringArray response = service.listUsers(userId, pass);
			List<String> list = response.getItem();

			return list;
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return null;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public String getUserName(String userId, String pass,Integer confNo, Integer userConferenceId) {
		try {
			String username = service.getVoipUsername(userId, pass, confNo, userConferenceId);
			return username;
		}  catch (Exception e) {
		    e.printStackTrace();
		} 
		return null;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public Long getJoinDate(String userId, String pass,Integer confNo, Integer userConferenceId) {
		try {
			Long time = service.getJoinDate(userId, pass, confNo, userConferenceId);
			return time/1000;
		} catch (Exception rex) {
			rex.printStackTrace();
		}
		return null;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public boolean banUser(String userId, String pass,Integer confNo, Integer userConferenceId) {
		try {
			boolean response = service.banUser(userId, pass, confNo, userConferenceId);
			return response;
		} catch (Exception rex) {
			rex.printStackTrace();
		}
		return false;
	}

	/**
h	 * @param userId
	 * @param confNo
	 * @param username
	 * @return
	 */
	public boolean unbanUser(String userId, String pass,Integer confNo, String username) {
		try {
			boolean response = service.unbanUser(userId, pass, confNo, username);
			return response;
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return false;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public boolean isUserBanned(String userId, String pass,Integer confNo, Integer userConferenceId) {
		try {
			return service.isUserBanned(userId, pass, confNo, userConferenceId);
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return false;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public boolean muteUser(String userId, String pass,Integer confNo, Integer userConferenceId) {
		try {
			boolean response = service.muteUser(userId, pass, confNo, userConferenceId);
			return response;
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return false;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public boolean unmuteUser(String userId, String pass,Integer confNo, Integer userConferenceId) {
		try {
			boolean response = service.unmuteUser(userId, pass, confNo, userConferenceId);
			return response;
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return false;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public boolean isUserMuted(String userId, String pass,Integer confNo, Integer userConferenceId) {
		try {
			return service.isUserMuted(userId, pass, confNo, userConferenceId);
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return false;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public boolean kickUser(String userId, String pass,Integer confNo, Integer userConferenceId) {
		try {
			boolean response = service.kickUser(userId, pass, confNo, userConferenceId);

			return response;
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return false;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param userConferenceId
	 * @return
	 */
	public boolean isUserTalking(String userId, String pass,Integer confNo, Integer userConferenceId) {
		try {
			return service.isUserTalking(userId, pass, confNo, userConferenceId);
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return false;
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
	 * @param users
	 * @param name
	 * @param agenda
	 * @param recorded
	 * @return
	 */
	public Integer createConference(String userId, String pass,String owner, short accessType,
			boolean permanent, String pin, String adminpin,
			Long startDate,Long endDate, Integer maxUsers, String[] users, String name,
			String agenda, boolean recorded, String project) {
		try {
			if (!permanent) {
			}
			else {
			    startDate = -1L;
			    endDate = -1L;
			}



			int response = service.createConference(userId, pass, owner, accessType, permanent,
				pin, adminpin, startDate, endDate, maxUsers, name, agenda, recorded, project);
			
			Integer confNum = new Integer(response);

			StringArray sa = new StringArray();
			List<String> sal = sa.getItem();
			List<String> list = new ArrayList<String>(users.length);  
			for (String s : users) {  
			      list.add(s);  
			}  
			sal = list;
			
			boolean usersResponse = service.setConferenceUserList(userId, pass, confNum, sa);
			System.out.println(usersResponse);

			return confNum;
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return null;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @param owner
	 * @param accessType
	 * @param permanent
	 * @param pin
	 * @param adminpin
	 * @param startDate
	 * @param endDate
	 * @param maxUsers
	 * @param users
	 * @param name
	 * @param agenda
	 * @param recorded
	 * @return
	 */
	public Integer editConference(String userId, String pass,Integer confNo, String owner,short accessType, boolean permanent, String pin, String adminpin,Long startDate, Long endDate, Integer maxUsers, String[] users,String name, String agenda,boolean recorded) {
		try {
			if (!permanent) {
			}
			else {
			    startDate = -1L;
			    endDate = -1L;
			}
	
			int response = service.editConference(userId, pass, confNo,
				owner, accessType, permanent, pin,
				adminpin, startDate, endDate, maxUsers,
				name, agenda, recorded);

			StringArray sa = new StringArray();
			List<String> sal = sa.getItem();
			List<String> list = new ArrayList<String>(users.length);  
			for (String s : users) {  
			      list.add(s);  
			}  
			sal = list;
			
			boolean setResponse = service.setConferenceUserList(userId, pass, confNo, sa);

			//setResponse;
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return null;
	}

	/**
	 * @param userId
	 * @param confNumber
	 * @return
	 */
	public boolean endConference(String userId, String pass,Integer confNumber) {
		try {
			boolean response=service.endConference(userId, pass, confNumber);
			boolean result = response;

			return result;
		} catch (Exception rex) {
			rex.printStackTrace();
		}
		return false;
	}

	/**
	 * @param userId
	 * @param confNumber
	 * @return
	 */
	public Integer removeConference(String userId, String pass,Integer confNumber) {
		try {
			int response = service.removeConference(userId, pass, confNumber);
			Integer count = response;

			return count;
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return null;
	}

	/**
	 * @param userId
	 * @param confNo
	 * @return
	 */
	public boolean lockConference(String userId, String pass,Integer confNo) {
		try {
			boolean response = service.lockConf(userId, pass, confNo);
			return response;
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return false;
	}

	public boolean unlockConference(String userId, String pass,Integer confNo) {
		try {
			boolean response = service.unlockConf(userId, pass, confNo);
			return response;
		} catch (Exception rex) {
			rex.printStackTrace();
		}

		return false;
	}

	/**
	 * @author <a href="mailto:janny@man.poznan.pl">janny@man.poznan.pl</a>
	 */
	public class AccessTypes {
		public static final short PUBLIC = 0;
		public static final short PIN = 1;
		public static final short PIN_LIST = 2;
		public static final short LIST = 3;
	}

	/**
	 * @author <a href="mailto:janny@man.poznan.pl">janny@man.poznan.pl</a>
	 */
	public class ParticipantsInfo {
		private int id;
		private String username;
		private String time;
		private boolean isBanned;
		private boolean isTalking;
		private boolean isMuted;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public boolean isBanned() {
			return isBanned;
		}

		public void setBanned(boolean isBanned) {
			this.isBanned = isBanned;
		}

		public boolean isTalking() {
			return isTalking;
		}

		public void setTalking(boolean isTalking) {
			this.isTalking = isTalking;
		}

		public boolean isMuted() {
			return isMuted;
		}

		public void setMuted(boolean isMuted) {
			this.isMuted = isMuted;
		}

		public JSONObject toJSONObject() {
			JSONObject json = null;
			try {
				json= new JSONObject();
				json.put("id",id);
				json.put("username", username);
				json.put("time", time);
				json.put("isBanned", isBanned);
				json.put("isTalking", isTalking);
				json.put("isMuted", isMuted);
			}
			catch (JSONException je) {
				//swallow
			}
			return json;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}	
	
	public String getUsernameFromQualipsoUsers(String userid) {
		try {
			String username = service.usernameFromQualipsoUsers(userid, "", userid);

			return username;
		} catch (Exception rex) {
			rex.printStackTrace();
		}
		return null;
	}
	
	
	public String getServiceVersion() throws RemoteException, Exception  {
		String ver = service.getServiceVersion();
		System.out.println("getServiceVersion=" + ver);
		return "";
	}
	
	public String isDBOpen() throws RemoteException, Exception  {
		String ver = service.isDBOpen();
		System.out.println("isDBOpen=" + ver);
		return "";
	}	

	public static final void main(String[] argc) {
		QualipsoVoIPConferenceServiceClient cl = new QualipsoVoIPConferenceServiceClient("http://localhost:3000/factory-service-voip/voipconference?wsdl");
		try {
		    cl.isDBOpen();
		} catch (RemoteException e) {
		    e.printStackTrace();
		} catch (Exception  e) {
		    e.printStackTrace();
		}
	}
}
