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

import org.hibernate.annotations.Index;


/**
 * Billing system
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @author <a href="mailto:debe@man.poznan.pl">Marcin Wrzos</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */
@Entity
@Table(name = "cdr")
public class Cdr implements Serializable {

	private static final long serialVersionUID = -3196624091684458131L;

	@Id
	@GeneratedValue
	private Long tmp_key;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date calldate=new Date();

	@Column(nullable = false, length = 80)
	private String clid="";

	@Index(name="src_dst_index")
	@Column(nullable = false, length = 80)
	private String src="";

	@Index(name="src_dst_index")
	@Column(nullable = false, length = 80)
	private String dst="";

	@Column(nullable = false, length = 80)
	private String dcontext="";

	@Column(nullable = false, length = 80)
	private String channel="";

	@Column(nullable = false, length = 80)
	private String dstchannel="";

	@Column(nullable = false, length = 80)
	private String lastapp="";

	@Column(nullable = false, length = 80)
	private String lastdata="";

	@Column(nullable = false)
	private Long duration=0l;

	@Column(nullable = false)
	private Long billsec=0l;

	@Column(nullable = false, length = 45)
	private String disposition="";

	@Column(nullable = false)
	private Long amaflags=0l;

	@Column(nullable = false, length = 20)
	private String accountcode="";

	@Column(nullable = false, length = 32)
	private String uniqueid;

	@Column(nullable = false, length = 255)
	private String userfield="";

	public Cdr() {}

	public String getAccountcode() {
		return accountcode;
	}

	public void setAccountcode(String accountcode) {
		this.accountcode = accountcode;
	}

	public Long getAmaflags() {
		return amaflags;
	}

	public void setAmaflags(Long amaflags) {
		this.amaflags = amaflags;
	}

	public Long getBillsec() {
		return billsec;
	}

	public void setBillsec(Long billsec) {
		this.billsec = billsec;
	}

	public Date getCalldate() {
		return calldate;
	}

	public void setCalldate(Date calldate) {
		this.calldate = calldate;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getClid() {
		return clid;
	}

	public void setClid(String clid) {
		this.clid = clid;
	}

	public String getDcontext() {
		return dcontext;
	}

	public void setDcontext(String dcontext) {
		this.dcontext = dcontext;
	}

	public String getDisposition() {
		return disposition;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}

	public String getDst() {
		return dst;
	}

	public void setDst(String dst) {
		this.dst = dst;
	}

	public String getDstchannel() {
		return dstchannel;
	}

	public void setDstchannel(String dstchannel) {
		this.dstchannel = dstchannel;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public String getLastapp() {
		return lastapp;
	}

	public void setLastapp(String lastapp) {
		this.lastapp = lastapp;
	}

	public String getLastdata() {
		return lastdata;
	}

	public void setLastdata(String lastdata) {
		this.lastdata = lastdata;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getUniqueid() {
		return uniqueid;
	}

	public void setUniqueid(String uniqueid) {
		this.uniqueid = uniqueid;
	}

	public Long gettmpKey() {
		return tmp_key;
	}

	public void setTmpKey(Long tmp_key) {
		this.tmp_key = tmp_key;
	}
	
	public String getUserfield() {
		return userfield;
	}

	public void setUserfield(String userfield) {
		this.userfield = userfield;
	}
}