package org.qualipso.factory.voipservice.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "past_conferences")
public class PastConference implements Serializable {
	private static final long serialVersionUID = 5870611295068909435L;

	@Id
	private Integer confno;

	private Integer pin;

	private Integer adminpin;

	@Column(nullable = false, length = 80)
	private String owner;

	private String name;

	private String agenda;

	private String recordings;
	
	private String project;

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

	@Column(nullable = false, name = "max_users")
	private Integer maxUsers=20;

	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(
			name="past_invited_conference_users",
			joinColumns=@JoinColumn(name="confno"),
			inverseJoinColumns=@JoinColumn(name="username",referencedColumnName="name")
	)
	private Set<SipConf> users=new HashSet<SipConf>();

	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(
			name="past_banned_conference_users",
			joinColumns=@JoinColumn(name="confno"),
			inverseJoinColumns=@JoinColumn(name="username",referencedColumnName="name")
	)
	private Set<SipConf> bannedUsers=new HashSet<SipConf>();

	public class AccessTypes {
		public static final short PUBLIC=0;
		public static final short PIN=1;
		public static final short PIN_LIST=2;
		public static final short LIST=3;
	}

	public PastConference() {}

	public String getAdminpin() {
		if (adminpin==null)
			return "";
		else
			return adminpin.toString();
	}

	public void setAdminpinInt(Integer adminpin) {
		this.adminpin = adminpin;
	}

	public Integer getConfno() {
		return confno;
	}

	public void setConfno(Integer confno) {
		this.confno = confno;
	}

	public String getPin() {
		if (pin==null)
			return "";
		else
			return pin.toString();
	}

	public void setPinInt(Integer pin) {
		this.pin = pin;
	}

	public Integer getMaxUsers() {
		return maxUsers;
	}

	public void setMaxUsers(Integer maxUsers) {
		if (maxUsers>50)
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

	public void removeInvitedUser(SipConf sipConf) {
		users.remove(sipConf);
	}

	public void addInvitedUser(SipConf sipConf) {
		users.add(sipConf);
	}

	public List<String> getInvitedUsersList() {
		List<String> userList=new ArrayList<String>();
		Iterator<SipConf> iterator=users.iterator();
		while (iterator.hasNext()) {
			userList.add(((SipConf)iterator.next()).getUsername());
		}

		return userList;
	}

	public void clearInvitedUserList() {
		users.clear();
	}
	public void removeBannedUser(SipConf sipConf) {
		bannedUsers.remove(sipConf);
	}

	public void addBannedUser(SipConf sipConf) {
		bannedUsers.add(sipConf);
	}

	public List<String> getBannedUserList() {
		List<String> bUserList=new ArrayList<String>();
		Iterator<SipConf> iterator=bannedUsers.iterator();
		while (iterator.hasNext()) {
			bUserList.add(((SipConf)iterator.next()).getUsername());
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

	public String getAgenda() {
		if (agenda==null)
			return "";
		else
			return agenda;
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
		if (name==null)
			return "";
		else
			return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRecorded() {
		return (recorded==1);
	}

	public void setRecorded(boolean recorded) {
		this.recorded = recorded?(short)1:0;
	}

	public String getRecordings() {
		return recordings;
	}

	public void setRecordings(String recordings) {
		this.recordings = recordings;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getProject() {
		return project;
	}
}