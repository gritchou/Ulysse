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
import javax.persistence.OneToMany;
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
@Table(name = "sip_conf")
public class SipConf implements Serializable {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -2234139177249045142L;

	@Id
	@Column(length = 80)
	private String name="";

	@Column(length = 512)
	private String userid ="";
	
	@Column(nullable = false,length = 100)
	private String fullcontact="";

	@Column(length = 20)
	private String accountcode;

	@Column(length = 7)
	private String amaflags;

	@Column(length = 10)
	private String callgroup;

	@Column(length = 80)
	private String callerid;

	@Column(length = 3)
	private String canreinvite ="no";

	@Column(length = 80)
	private String context;

	@Column(length = 15)
	private String defaultip;

	@Column(length = 7)
	private String dtmfmode;

	@Column(length = 80)
	private String fromuser;

	@Column(length = 80)
	private String fromdomain;

	@Column(nullable = false, length = 31)
	private String host="";

	@Column(length = 4)
	private String insecure;

	@Column(length = 2)
	private String language;

	@Column(length = 50)
	private String mailbox;

	@Column(length = 80)
	private String md5secret;

	@Column(nullable = false, length = 5)
	private String nat="yes";

	@Column(length = 95)
	private String permit;

	@Column(length = 95)
	private String deny;

	@Column(length = 95)
	private String mask;

	@Column(length = 10)
	private String pickupgroup;

	@Column(nullable = false, length = 5)
	private String port="";

	@Column(length = 3)
	private String qualify;

	@Column(length = 1)
	private String restrictcid;

	@Column(length = 3)
	private String rtptimeout;

	@Column(length = 3)
	private String rtpholdtimeout;

	@Column(length = 80)
	private String secret;

	@Column(nullable = false)
	private String type="friend";

	@Column(nullable = false, length = 80, unique = true)
	private String username="";

	@Column(length = 100)
	private String disallow="";

	@Column(length = 100)
	private String allow="g729;ilbc;gsm;ulaw;alaw;h261;h263;h263p";

	@Column(length = 100)
	private String musiconhold;

	@Column(nullable = false, length = 200)
	private String regseconds= "0";

	@Column(nullable = false, length = 15)
	private String ipaddr="";

	@Column(nullable = false, length = 80)
	private String regexten="";

	@Column(length = 3)
	private String cancallforward="yes";

	@Column(length = 255, nullable=false)
	private String email = "";

	@Column(length = 80, nullable=false)
	private String defaultuser = "";

	@Column(length = 255, nullable=false)
	private String lastms = "0";
	
	@Column(length = 100, nullable=true)
	private String regserver = "";
	
	@Column(length = 100, nullable=true)
	private String useragent = "";
	
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="sipConf")
	private Set<ConferenceUser> conferences=new HashSet<ConferenceUser>();

	@SuppressWarnings("unused")
	@OneToMany(cascade=CascadeType.ALL, mappedBy="sipConf")
	private Set<ConferenceUser> pastConferences = new HashSet<ConferenceUser>();

	public SipConf() {}

	public String getAccountcode() {
		return accountcode;
	}

	public void setAccountcode(String accountcode) {
		this.accountcode = accountcode;
	}

	public String getAllow() {
		return allow;
	}

	public void setAllow(String allow) {
		this.allow = allow;
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

	public String getCallgroup() {
		return callgroup;
	}

	public void setCallgroup(String callgroup) {
		this.callgroup = callgroup;
	}

	public String getCancallforward() {
		return cancallforward;
	}

	public void setCancallforward(String cancallforward) {
		this.cancallforward = cancallforward;
	}

	public String getCanreinvite() {
		return canreinvite;
	}

	public void setCanreinvite(String canreinvite) {
		this.canreinvite = canreinvite;
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

	public String getDeny() {
		return deny;
	}

	public void setDeny(String deny) {
		this.deny = deny;
	}

	public String getDisallow() {
		return disallow;
	}

	public void setDisallow(String disallow) {
		this.disallow = disallow;
	}

	public String getDtmfmode() {
		return dtmfmode;
	}

	public void setDtmfmode(String dtmfmode) {
		this.dtmfmode = dtmfmode;
	}

	public String getFromdomain() {
		return fromdomain;
	}

	public void setFromdomain(String fromdomain) {
		this.fromdomain = fromdomain;
	}

	public String getFromuser() {
		return fromuser;
	}

	public void setFromuser(String fromuser) {
		this.fromuser = fromuser;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	/*
	public Long getId() {
		return id;
	}

	private void setId(Long id) {
		this.id = id;
	}
	*/

	public List<Integer> getConferences() {
		List<Integer> conferenceList=new ArrayList<Integer>();
		Iterator<ConferenceUser> iterator=conferences.iterator();
		while (iterator.hasNext()) {
			conferenceList.add(((ConferenceUser)iterator.next()).getMeetMe().getConfno());
		}
		return conferenceList;
	}


	public String getInsecure() {
		return insecure;
	}

	public void setInsecure(String insecure) {
		this.insecure = insecure;
	}

	public String getIpaddr() {
		return ipaddr;
	}

	public void setIpaddr(String ipaddr) {
		this.ipaddr = ipaddr;
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

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getMd5secret() {
		return md5secret;
	}

	public void setMd5secret(String md5secret) {
		this.md5secret = md5secret;
	}

	public String getMusiconhold() {
		return musiconhold;
	}

	public void setMusiconhold(String musiconhold) {
		this.musiconhold = musiconhold;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQualispoUser() {
		return userid;
	}

	public void setQualipsoUser(String userid) {
		this.userid = userid;
	}

	public String getNat() {
		return nat;
	}

	public void setNat(String nat) {
		this.nat = nat;
	}

	public String getPermit() {
		return permit;
	}

	public void setPermit(String permit) {
		this.permit = permit;
	}

	public String getPickupgroup() {
		return pickupgroup;
	}

	public void setPickupgroup(String pickupgroup) {
		this.pickupgroup = pickupgroup;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getQualify() {
		return qualify;
	}

	public void setQualify(String qualify) {
		this.qualify = qualify;
	}

	public String getRegexten() {
		return regexten;
	}

	public void setRegexten(String regexten) {
		this.regexten = regexten;
	}

	public String getRegseconds() {
		return regseconds;
	}

	public void setRegseconds(String regseconds) {
		this.regseconds = regseconds;
	}

	public String getRestrictcid() {
		return restrictcid;
	}

	public void setRestrictcid(String restrictcid) {
		this.restrictcid = restrictcid;
	}

	public String getRtpholdtimeout() {
		return rtpholdtimeout;
	}

	public void setRtpholdtimeout(String rtpholdtimeout) {
		this.rtpholdtimeout = rtpholdtimeout;
	}

	public String getRtptimeout() {
		return rtptimeout;
	}

	public void setRtptimeout(String rtptimeout) {
		this.rtptimeout = rtptimeout;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullcontact() {
		return fullcontact;
	}

	public void setFullcontact(String fullcontact) {
		this.fullcontact = fullcontact;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setDefaultuser(String defaultuser) {
	    this.defaultuser = defaultuser;
	}

	public String getDefaultuser() {
	    return defaultuser;
	}

	public void setLastms(String lastms) {
	    this.lastms = lastms;
	}

	public String getLastms() {
	    return lastms;
	}

	public void setRegserver(String regserver) {
	    this.regserver = regserver;
	}

	public String getRegserver() {
	    return regserver;
	}

	public void setUseragent(String useragent) {
	    this.useragent = useragent;
	}

	public String getUseragent() {
	    return useragent;
	}
}