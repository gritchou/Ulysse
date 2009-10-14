package org.qualipso.factory.voipservice.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "iax_buddies")
public class IaxBuddies implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 30)
	private String name;

	@Column(length = 512)
	private String userid ="";
	
	@Column(length = 30, unique=true)
	private String username;

	@Column(nullable = false, length = 6, name="type")
	private String utype;

	@Column(length = 50)
	private String secret;

	@Column(length = 32)
	private String md5secret;

	@Column(length = 100)
	private String dbsecret;

	@Column(length = 10)
	private String notransfer= "yes";

	@Column(length = 100)
	private String inkeys;

	@Column(length = 100)
	private String outkeys;

	@Column(length = 100,nullable=false)
	private String auth = "cleartext,md5";

	@Column(length = 100)
	private String accountcode;

	@Column(length = 100)
	private String amaflags;

	@Column(length = 100)
	private String callerid;

	@Column(length = 100,nullable=false)
	private String context;

	@Column(length = 15)
	private String defaultip;

	@Column(length = 31, nullable=false)
	private String host="dynamic";

	@Column(length = 5)
	private String language;

	@Column(length = 50)
	private String mailbox;

	@Column(length = 95)
	private String deny;

	@Column(length = 95)
	private String permit;

	@Column(length = 4)
	private String qualify="yes";

	@Column(length = 100)
	private String disallow;

	@Column(length = 100)
	private String allow;

	@Column(length = 15)
	private String ipaddr;

	private Integer port=0;

	private Integer regseconds=0;

	@Column(length = 255, nullable=false)
	private String email = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getQualipsoUser() {
		return userid;
	}

	public void setQualipsoUser(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getType() {
		return utype;
	}

	public void setType(String utype) {
		this.utype = utype;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getMd5secret() {
		return md5secret;
	}

	public void setMd5secret(String md5secret) {
		this.md5secret = md5secret;
	}

	public String getDbsecret() {
		return dbsecret;
	}

	public void setDbsecret(String dbsecret) {
		this.dbsecret = dbsecret;
	}

	public String getNotransfer() {
		return notransfer;
	}

	public void setNotransfer(String notransfer) {
		this.notransfer = notransfer;
	}

	public String getInkeys() {
		return inkeys;
	}

	public void setInkeys(String inkeys) {
		this.inkeys = inkeys;
	}

	public String getOutkeys() {
		return outkeys;
	}

	public void setOutkeys(String outkeys) {
		this.outkeys = outkeys;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getAccountcode() {
		return accountcode;
	}

	public void setAccountcode(String accountcode) {
		this.accountcode = accountcode;
	}

	public String getAmaflags() {
		return amaflags;
	}

	public void setAmaflags(String amaflags) {
		this.amaflags = amaflags;
	}

	public String getCallerid() {
		return callerid;
	}

	public void setCallerid(String callerid) {
		this.callerid = callerid;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getDefaultip() {
		return defaultip;
	}

	public void setDefaultip(String defaultip) {
		this.defaultip = defaultip;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getMailbox() {
		return mailbox;
	}

	public void setMailbox(String mailbox) {
		this.mailbox = mailbox;
	}

	public String getDeny() {
		return deny;
	}

	public void setDeny(String deny) {
		this.deny = deny;
	}

	public String getPermit() {
		return permit;
	}

	public void setPermit(String permit) {
		this.permit = permit;
	}

	public String getQualify() {
		return qualify;
	}

	public void setQualify(String qualify) {
		this.qualify = qualify;
	}

	public String getDisallow() {
		return disallow;
	}

	public void setDisallow(String disallow) {
		this.disallow = disallow;
	}

	public String getAllow() {
		return allow;
	}

	public void setAllow(String allow) {
		this.allow = allow;
	}

	public String getIpaddr() {
		return ipaddr;
	}

	public void setIpaddr(String ipaddr) {
		this.ipaddr = ipaddr;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getRegseconds() {
		return regseconds;
	}

	public void setRegseconds(Integer regseconds) {
		this.regseconds = regseconds;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}
}
