package org.qualipso.factory.voipservice.security;


/**
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */
public class AsteriskConferenceSecurityParams {
	String userId = null;
	String pass = null;
	String path = null;
	Integer confNo = null;
	
	
	public AsteriskConferenceSecurityParams(String userId, String pass, String path, Integer confNo) {
		this.userId = userId;
		this.pass = pass;
		this.path = path;
		this.confNo = confNo;
	}

	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getPass() {
		return pass;
	}


	public void setPass(String pass) {
		this.pass = pass;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public Integer getConfNo() {
		return confNo;
	}


	public void setConfNo(Integer confNo) {
		this.confNo = confNo;
	}
}
