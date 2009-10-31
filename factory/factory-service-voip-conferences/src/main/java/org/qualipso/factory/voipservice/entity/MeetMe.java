package org.qualipso.factory.voipservice.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @author <a href="mailto:debe@man.poznan.pl">Marcin Wrzos</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */

@Entity
@Table(name = "meetme")
public class MeetMe implements Serializable {
	private static final long serialVersionUID = -8191694506540365026L;

	@Id
	@GeneratedValue(generator="MeetMeSeq")
	@SequenceGenerator(name="MeetMeSeq",sequenceName="MEETME_SEQ",initialValue=999)
	private Integer confno;

	private Integer pin;

	private Integer adminpin;

	@Column(nullable = false)
	private Integer members=0;

	@Column(nullable = false, length = 80)
	private String owner;

	private String name;

	private String agenda;

	@Column(nullable = false)
	private short accessType = 0;

	@Column(nullable = false)
	private short permanent = 0;

	@Column(nullable = false)
	private short recorded = 0;

	@Column(name = "start_date")
	private Long startDate;
	@Column(name = "end_date")
	private Long endDate;
	
	@Column(nullable = false, length = 80)
	private String project;
	
	@Column(nullable = false, name = "max_users")
	private Integer maxUsers = 20;

	@OneToMany(cascade=CascadeType.ALL, mappedBy="meetMe")
	private Set<ConferenceUser> users = new HashSet<ConferenceUser>();

	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(
			name="invited_conference_users",
			joinColumns=@JoinColumn(name="confno"),
			inverseJoinColumns=@JoinColumn(name="username",referencedColumnName="name")
	)
	private Set<SipConf> invitedUsers=new HashSet<SipConf>();

	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(
			name="banned_conference_users",
			joinColumns=@JoinColumn(name="confno"),
			inverseJoinColumns=@JoinColumn(name="username",referencedColumnName="name")
	)
	private Set<SipConf> bannedUsers = new HashSet<SipConf>();

	public class AccessTypes {
		public static final short PUBLIC = 0;
		public static final short PIN = 1;
		public static final short LIST = 2;
		public static final short PIN_LIST = 3;
	}

	public MeetMe() {}

	public String getAdminpin() {
		if (adminpin==null)
			return "";
		else
			return adminpin.toString();
	}

	public Integer getAdminpinInt() {
		return adminpin;
	}

	public void setAdminpin(String adminpin) throws NumberFormatException {
		try {
			if (adminpin==null || adminpin.equals(""))
				this.adminpin = null;
			else
				this.adminpin = Integer.parseInt(adminpin);
		}
		catch (NumberFormatException e) {
			throw new NumberFormatException("Enter a valid admin pin number!");
		}
	}

	public Integer getConfno() {
		return confno;
	}

	@SuppressWarnings("unused")
	private void setConfno(Integer confno) {
		this.confno = confno;
	}

	public Integer getMembers() {
		return members;
	}

	@SuppressWarnings("unused")
	private void setMembers(Integer members) {
		this.members = members;
	}

	public String getPin() {
		if (pin==null)
			return "";
		else
			return pin.toString();
	}

	public Integer getPinInt() {
		return pin;
	}

	public void setPin(String pin) throws NumberFormatException {
		try {
			if (pin==null || pin.equals(""))
				this.pin = null;
			else
				this.pin = Integer.parseInt(pin);
		}
		catch (NumberFormatException e) {
			throw new NumberFormatException("Enter a valid pin number!");
		}
	}

	public Integer getMaxUsers() {
		return maxUsers;
	}

	public void setMaxUsers(Integer maxUsers) {
		if (maxUsers > 50)
			this.maxUsers = 50;
		else
			this.maxUsers = maxUsers;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public Long getStartDate() {
		return startDate;
	}

	public Set<ConferenceUser> getUsersList() {
		return users;
	}

	public void removeInvitedUser(SipConf sipConf) {
		invitedUsers.remove(sipConf);
	}

	public void addInvitedUser(SipConf sipConf) {
		invitedUsers.add(sipConf);
	}

	public List<String> getInvitedUsersList() {
		List<String> userList = new ArrayList<String>();
		Iterator<SipConf> iterator=invitedUsers.iterator();
		while (iterator.hasNext()) {
			userList.add(((SipConf)iterator.next()).getUsername());
		}
		return userList;
	}

	public HashMap<String, SipConf> getInvitedUsersMap() {
		HashMap<String, SipConf> invitedUs = new HashMap<String, SipConf>();
		Iterator<SipConf> iterator = invitedUsers.iterator();
		while (iterator.hasNext()) {
			SipConf tmp = iterator.next();
			invitedUs.put(tmp.getUsername(), tmp);
		}
		return invitedUs;
	}

	public void clearInvitedUserList() {
		invitedUsers.clear();
	}

	public void removeBannedUser(SipConf sipConf) {
		bannedUsers.remove(sipConf);
	}

	public void addBannedUser(SipConf sipConf) {
		bannedUsers.add(sipConf);
	}

	public List<String> getBannedUserList() {
		List<String> bUserList = new ArrayList<String>();

		try {
			if (bannedUsers.size() > 0) {
				Iterator<SipConf> iterator = bannedUsers.iterator();
				while (iterator.hasNext()) {
					bUserList.add(((SipConf)iterator.next()).getUsername());
				}
			}
		} catch(Exception ex) {
			//
		}

		return bUserList;
	}

	public void clearBannedUserList() {
		bannedUsers.clear();
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	
	public String getAgenda() {
		if (agenda == null) {
			return "";
		} else {
			return agenda;
		}
	}

	public void setAgenda(String agenda) {
		this.agenda = agenda;
	}

	public void setAccessType(short type) {
		accessType = type;
	}

	public short getAccessType() {
		return accessType;
	}

	public boolean isPermanent() {
		return (permanent==1);
	}

	public void setPermanent(boolean permanent) {
		this.permanent = permanent?(short)1:0;
	}

	public String getName() {
		if (name == null) {
			return "";
		} else {
			return name;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean hasExpired() {
		if (!isPermanent()
				&& endDate != null
				&& members<=0
				&& new Date().getTime() > endDate * 1000) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isRecorded() {
		return (recorded == 1);
	}

	public void setRecorded(boolean recorded) {
		this.recorded = recorded?(short)1:0;
	}
}
