package org.qualipso.factory.voipservice.utils;

import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.security.pep.PEPService;

public class AuthData {
    private MembershipService membership;
    private CoreService core;
    private PEPService pep;
    private String action;
    private String path;
    
    public String getProfilePathForConnectedIdentifier() {
	if (membership != null) { 
	    try {
		return membership.getProfilePathForConnectedIdentifier();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	} 
	return null;
    }
    
    public void checkSecurity() {
	if (pep != null) {
	    try {
		pep.checkSecurity(getProfilePathForConnectedIdentifier(), path, action);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }    
    
    public void setCoreService(CoreService core) {
	this.core = core;
    }
    
    public CoreService getCoreService() {
	return this.core;
    }
}
