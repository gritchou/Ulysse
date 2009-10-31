package org.qualipso.factory.voipservice.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @author <a href="mailto:debe@man.poznan.pl">Marcin Wrzos</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */

@Entity
@Table(name = "voicemail_users")
public class VoicemailUsers implements Serializable {
	private static final long serialVersionUID = 601479002286241149L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private Long customer_id=0l;

	@Column(nullable = false, length = 50)
	private String context="";

	@Column(nullable = false)
	private Long mailbox=0l;

	@Column(nullable = false, length = 4)
	private String password="0";

	@Column(nullable = false, length = 50)
	private String fullname="";

	@Column(nullable = false, length = 50)
	private String email="";

	@Column(nullable = false, length = 50)
	private String pager="";

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date stamp;

	public VoicemailUsers() {}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public Long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMailbox() {
		return mailbox;
	}

	public void setMailbox(Long mailbox) {
		this.mailbox = mailbox;
	}

	public String getPager() {
		return pager;
	}

	public void setPager(String pager) {
		this.pager = pager;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getStamp() {
		return stamp;
	}

	public void setStamp(Date stamp) {
		this.stamp = stamp;
	}
}