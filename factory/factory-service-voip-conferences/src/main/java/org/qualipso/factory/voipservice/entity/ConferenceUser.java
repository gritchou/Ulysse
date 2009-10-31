package org.qualipso.factory.voipservice.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@IdClass(ConferenceUser.ConferenceUserPK.class)
@Table(name = "conference_users")
public class ConferenceUser implements Comparable<ConferenceUser>, Serializable {
	private static final long serialVersionUID = -6232725221873099730L;

	public static class ConferenceUserPK implements Serializable {
		private static final long serialVersionUID = -1708524926810695494L;

		private Integer id;

		@ManyToOne
		@JoinColumn(name = "conference", nullable = false, insertable=false, updatable=false)
		private MeetMe meetMe;

		public ConferenceUserPK() {
		}
		
		public int hashCode() {
			return (int) (id + "_" + meetMe.getConfno()).hashCode();
		}

		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (obj == null) return false;
			if (!(obj instanceof ConferenceUserPK)) return false;
			ConferenceUserPK pk = (ConferenceUserPK) obj;
			return pk.meetMe.equals(meetMe) && pk.id.equals(id);
		}

		public MeetMe getMeetMe() {
			return meetMe;
		}

		public void setMeetMe(MeetMe meetMe) {
			this.meetMe = meetMe;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
	}

	@Id
	private Integer id;

	@Id
	private MeetMe meetMe;

	@ManyToOne
	@JoinColumn(name = "username")
	private SipConf sipConf;

	private Long joinDate;
	private boolean talking;
	private boolean muted;

	public MeetMe getMeetMe() {
		return meetMe;
	}

	public void setMeetMe(MeetMe meetMe) {
		this.meetMe = meetMe;
	}

	public SipConf getSipConf() {
		return sipConf;
	}

	public void setSipConf(SipConf sipConf) {
		this.sipConf=sipConf;
	}

	public boolean isTalking() {
		return talking;
	}

	public void setTalking(boolean talking) {
		this.talking = talking;
	}

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	public Long getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Long joinDate) {
		this.joinDate = joinDate;
	}

	public int compareTo(ConferenceUser cUser) {
		if (cUser.joinDate<this.joinDate) {
			return 1;
		} else if (cUser.joinDate>this.joinDate) {
			return -1;
		} else {
			return 0;
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
