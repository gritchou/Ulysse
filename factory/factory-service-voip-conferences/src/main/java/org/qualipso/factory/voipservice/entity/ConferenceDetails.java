package org.qualipso.factory.voipservice.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.voipservice.utils.AsteriskConferenceUtils;

/**
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */

@Entity
@XmlType(name = "ConferenceDetails", namespace = "http://org.qualipso.factory.ws/resource/conference-detail", propOrder =  {
    "value", "confNo", "pin", "adminPin", "startDate", "startDateHR", "endDate", "endDateHR", "maxUsers", 
    "userCount", "owner", "name", "agenda", "accessType", "permanent", "recorded"}
)
@SuppressWarnings("serial")
public class ConferenceDetails extends FactoryResource {
	
	@Id
   	private Integer confNo = null;
   	private String pin = null;
   	private String adminPin = null;
   	private Long startDate = null;
   	private Long endDate = null;
   	
   	private String startDateHR = null;
   	private String endDateHR = null;
   	
   	private Integer maxUsers = null;
   	private Integer userCount = null;
   	private String owner = null;
   	private String name = null;
   	private String agenda = null;
	private short accessType = 0;
	private boolean permanent = false;
	private boolean recorded = false;

	private String value;
	private String path;
	
	/**
	 * @return
	 */
	@XmlAttribute(name = "confNo", required = true)
	public Integer getConfNo() {
		return confNo;
	}

	/**
	 * @param confNo
	 */
	public void setConfNo(Integer confNo) {
		this.confNo = confNo;
	}

	/**
	 * @return
	 */
	@XmlElement(name="pin", required=false)
	public String getPin() {
		return pin;
	}

	/**
	 * @param pin
	 */
	public void setPin(String pin) {
		this.pin = pin;
	}

	/**
	 * @return
	 */
	@XmlElement(name="adminPin", required=false)
	public String getAdminPin() {
		return adminPin;
	}

	/**
	 * @param adminPin
	 */
	public void setAdminPin(String adminPin) {
		this.adminPin = adminPin;
	}

	/**
	 * @return
	 */
	@XmlElement(name="startDate", required=false)
	public Long getStartDate() {
		return startDate;
	}

	/**
	 * @return
	 */
	@XmlElement(name="startDateHR", required=false)
	public String getStartDateHR() {
		return startDateHR;
	}
	
	/**
	 * @param startDate
	 */
	public void setStartDate(Long startDate) {
		this.startDate = startDate;
		if (startDate != null && startDate != 0) {
			this.startDateHR = AsteriskConferenceUtils.DATE_FORMAT.format(new Date(1000*startDate));
		} else {
			this.startDateHR = "";
		}
	}

	/**
	 * @return
	 */
	@XmlElement(name="endDate", required=false)
	public Long getEndDate() {
		return endDate;
	}

	/**
	 * @return
	 */
	@XmlElement(name="endDateHR", required=false)
	public String getEndDateHR() {
		return endDateHR;
	}

	
	/**
	 * @param endDate
	 */
	public void setEndDate(Long endDate) {
		this.endDate = endDate;
		if (endDate != null && endDate != 0) {
			this.endDateHR = AsteriskConferenceUtils.DATE_FORMAT.format(new Date(1000*endDate));
		} else {
			this.endDateHR = "";
		}
	}

	/**
	 * @return
	 */
	@XmlElement(name="maxUsers", required=false)
	public Integer getMaxUsers() {
		return maxUsers;
	}

	/**
	 * @param maxUsers
	 */
	public void setMaxUsers(Integer maxUsers) {
		this.maxUsers = maxUsers;
	}

	/**
	 * @return
	 */
	@XmlElement(name="userCount", required=false)
	public Integer getUserCount() {
		return userCount;
	}

	/**
	 * @param userCount
	 */
	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}

	/**
	 * @return
	 */
	@XmlElement(name="owner", required=false)
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return
	 */
	@XmlElement(name="name", required=false)
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	@XmlElement(name="agenda", required=false)
	public String getAgenda() {
		return agenda;
	}

	/**
	 * @param agenda
	 */
	public void setAgenda(String agenda) {
		this.agenda = agenda;
	}

	/**
	 * @return
	 */
	@XmlElement(name="accessType", required=false)
	public short getAccessType() {
		return accessType;
	}

	/**
	 * @param accessType
	 */
	public void setAccessType(short accessType) {
		this.accessType = accessType;
	}

	/**
	 * @return
	 */
	@XmlElement(name="permanent", required=false)
	public boolean isPermanent() {
		return permanent;
	}

	/**
	 * @param permanent
	 */
	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}

	/**
	 * @return
	 */
	@XmlElement(name="recorded", required=false)
	public boolean isRecorded() {
		return recorded;
	}

	/**
	 * @param recorded
	 */
	public void setRecorded(boolean recorded) {
		this.recorded = recorded;
	}
	
	@XmlAttribute(name = "path", required = true)
	@Transient
	public String getResourcePath() {
		return path;
	}
	
	@XmlElement(name = "value", required = true)
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setResourcePath(String path) {
		this.path = path;
	}
	
	@XmlTransient
	public FactoryResourceIdentifier getFactoryResourceIdentifier() {
		return new FactoryResourceIdentifier("QualipsoVoIPConferenceServiceInterface", "ConferenceDetails", String.valueOf(getConfNo()));
	}

	@XmlTransient
	public String getResourceName() {
		return getValue();
	}
}
