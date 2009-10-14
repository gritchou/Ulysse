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
@Table(name = "queue_table")
public class QueueTable implements Serializable {
	private static final long serialVersionUID = -7080473072870967454L;

	@Id
	@Column(nullable = false, length = 128)
	private String name;
	
	@Column(length = 128)
	private String musiconhold;
	
	@Column(length = 128)
	private String announce;
	
	@Column(length = 128)
	private String context;
	  
	private Long timeout;
	  
	private Boolean monitor_join;
	  
	@Column(length = 128)
	private String monitor_format;
	  
	@Column(length = 128)
	private String queue_youarenext;
	  
	@Column(length = 128)
	private String queue_thereare;
	  
	@Column(length = 128)
	private String queue_callswaiting;
	  
	@Column(length = 128)
	private String queue_holdtime;
	  
	@Column(length = 128)
	private String queue_minutes;
	  
	@Column(length = 128)
	private String queue_seconds;
	  
	@Column(length = 128)
	private String queue_lessthan;
	  
	@Column(length = 128)
	private String queue_thankyou;
	  
	@Column(length = 128)
	private String queue_reporthold;
	  
	private Long announce_frequency;
	  
	private Long announce_round_seconds;
	  
	@Column(length = 128)
	private String announce_holdtime;
	  
	private Long retry;
	  
	private Long wrapuptime;
	  
	private Long maxlen;
	  
	private Long servicelevel;
	  
	@Column(length = 128)
	private String strategy;
	  
	@Column(length = 128)
	private String joinempty;
	  
	@Column(length = 128)
	private String leavewhenempty;
	  
	private Boolean eventmemberstatus;
	  
	private Boolean eventwhencalled;
	  
	private Boolean reportholdtime;
	  
	private Long memberdelay;
	  
	private Long weight;
	  
	private Boolean timeoutrestart;
	
	public QueueTable() {}

	public String getAnnounce() {
		return announce;
	}

	public void setAnnounce(String announce) {
		this.announce = announce;
	}

	public Long getAnnounce_frequency() {
		return announce_frequency;
	}

	public void setAnnounce_frequency(Long announce_frequency) {
		this.announce_frequency = announce_frequency;
	}

	public String getAnnounce_holdtime() {
		return announce_holdtime;
	}

	public void setAnnounce_holdtime(String announce_holdtime) {
		this.announce_holdtime = announce_holdtime;
	}

	public Long getAnnounce_round_seconds() {
		return announce_round_seconds;
	}

	public void setAnnounce_round_seconds(Long announce_round_seconds) {
		this.announce_round_seconds = announce_round_seconds;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public Boolean getEventmemberstatus() {
		return eventmemberstatus;
	}

	public void setEventmemberstatus(Boolean eventmemberstatus) {
		this.eventmemberstatus = eventmemberstatus;
	}

	public Boolean getEventwhencalled() {
		return eventwhencalled;
	}

	public void setEventwhencalled(Boolean eventwhencalled) {
		this.eventwhencalled = eventwhencalled;
	}

	public String getJoinempty() {
		return joinempty;
	}

	public void setJoinempty(String joinempty) {
		this.joinempty = joinempty;
	}

	public String getLeavewhenempty() {
		return leavewhenempty;
	}

	public void setLeavewhenempty(String leavewhenempty) {
		this.leavewhenempty = leavewhenempty;
	}

	public Long getMaxlen() {
		return maxlen;
	}

	public void setMaxlen(Long maxlen) {
		this.maxlen = maxlen;
	}

	public Long getMemberdelay() {
		return memberdelay;
	}

	public void setMemberdelay(Long memberdelay) {
		this.memberdelay = memberdelay;
	}

	public String getMonitor_format() {
		return monitor_format;
	}

	public void setMonitor_format(String monitor_format) {
		this.monitor_format = monitor_format;
	}

	public Boolean getMonitor_join() {
		return monitor_join;
	}

	public void setMonitor_join(Boolean monitor_join) {
		this.monitor_join = monitor_join;
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

	public String getQueue_callswaiting() {
		return queue_callswaiting;
	}

	public void setQueue_callswaiting(String queue_callswaiting) {
		this.queue_callswaiting = queue_callswaiting;
	}

	public String getQueue_holdtime() {
		return queue_holdtime;
	}

	public void setQueue_holdtime(String queue_holdtime) {
		this.queue_holdtime = queue_holdtime;
	}

	public String getQueue_lessthan() {
		return queue_lessthan;
	}

	public void setQueue_lessthan(String queue_lessthan) {
		this.queue_lessthan = queue_lessthan;
	}

	public String getQueue_minutes() {
		return queue_minutes;
	}

	public void setQueue_minutes(String queue_minutes) {
		this.queue_minutes = queue_minutes;
	}

	public String getQueue_reporthold() {
		return queue_reporthold;
	}

	public void setQueue_reporthold(String queue_reporthold) {
		this.queue_reporthold = queue_reporthold;
	}

	public String getQueue_seconds() {
		return queue_seconds;
	}

	public void setQueue_seconds(String queue_seconds) {
		this.queue_seconds = queue_seconds;
	}

	public String getQueue_thankyou() {
		return queue_thankyou;
	}

	public void setQueue_thankyou(String queue_thankyou) {
		this.queue_thankyou = queue_thankyou;
	}

	public String getQueue_thereare() {
		return queue_thereare;
	}

	public void setQueue_thereare(String queue_thereare) {
		this.queue_thereare = queue_thereare;
	}

	public String getQueue_youarenext() {
		return queue_youarenext;
	}

	public void setQueue_youarenext(String queue_youarenext) {
		this.queue_youarenext = queue_youarenext;
	}

	public Boolean getReportholdtime() {
		return reportholdtime;
	}

	public void setReportholdtime(Boolean reportholdtime) {
		this.reportholdtime = reportholdtime;
	}

	public Long getRetry() {
		return retry;
	}

	public void setRetry(Long retry) {
		this.retry = retry;
	}

	public Long getServicelevel() {
		return servicelevel;
	}

	public void setServicelevel(Long servicelevel) {
		this.servicelevel = servicelevel;
	}

	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	public Long getTimeout() {
		return timeout;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	public Boolean getTimeoutrestart() {
		return timeoutrestart;
	}

	public void setTimeoutrestart(Boolean timeoutrestart) {
		this.timeoutrestart = timeoutrestart;
	}

	public Long getWeight() {
		return weight;
	}

	public void setWeight(Long weight) {
		this.weight = weight;
	}

	public Long getWrapuptime() {
		return wrapuptime;
	}

	public void setWrapuptime(Long wrapuptime) {
		this.wrapuptime = wrapuptime;
	}
	
	
}