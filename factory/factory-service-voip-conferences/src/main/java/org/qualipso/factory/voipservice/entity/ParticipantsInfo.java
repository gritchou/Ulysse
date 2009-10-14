package org.qualipso.factory.voipservice.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;

/**
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */

@Entity
@XmlType(name = "ParticipantsInfo", namespace = "http://org.qualipso.factory.ws/resource/participants-info", propOrder =  {
    "value", "time", "isBanned", "isTalking", "isMuted"}
)
@SuppressWarnings("serial")
public class ParticipantsInfo extends FactoryResource {

	@Id
	private String id = null;
	private String username = null;
	private String time = null;
	private String isBanned = null;
	private String isTalking = null;
	private String isMuted = null;
	
	//factory variables
	private String value;
	private String path;
	
	/**
	 * 
	 */
	public ParticipantsInfo() {
		
	}
	
	/**
	 * @param id
	 * @param username
	 * @param time
	 * @param isBanned
	 * @param isTalking
	 * @param isMuted
	 */
	public ParticipantsInfo(String id, String username, String time, String isBanned, 
			String isTalking, String isMuted) {
		this.id = id;
		this.username = username;
		this.time = time;
		this.isBanned = isBanned;
		this.isTalking = isTalking;
		this.isMuted = isMuted;
	}

	@XmlAttribute(name = "id", required = true)
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return
	 */
	@XmlAttribute(name = "username", required = false)
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return
	 */
	@XmlAttribute(name = "time", required = false)
	public String getTime() {
		return time;
	}

	/**
	 * @param time
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @return
	 */
	@XmlAttribute(name = "isBanned", required = false)
	public String getIsBanned() {
		return isBanned;
	}

	/**
	 * @param isBanned
	 */
	public void setIsBanned(String isBanned) {
		this.isBanned = isBanned;
	}

	/**
	 * @return
	 */
	@XmlAttribute(name = "isTalking", required = false)
	public String getIsTalking() {
		return isTalking;
	}

	/**
	 * @param isTalking
	 */
	public void setIsTalking(String isTalking) {
		this.isTalking = isTalking;
	}

	/**
	 * @return
	 */
	@XmlAttribute(name = "isMuted", required = false)
	public String getIsMuted() {
		return isMuted;
	}

	/**
	 * @param isMuted
	 */
	public void setIsMuted(String isMuted) {
		this.isMuted = isMuted;
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
		return new FactoryResourceIdentifier("QualipsoVoIPConferenceServiceInterface", "ParticipantsInfo", getId());
	}

	@XmlTransient
	public String getResourceName() {
		return getValue();
	}
}