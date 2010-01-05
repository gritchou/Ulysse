package org.qualipso.factory.voipservice.utils;

import org.apache.log4j.Logger;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathAlreadyBoundException;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.browser.BrowserService;
import org.qualipso.factory.collaboration.calendar.CalendarServiceBean;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.AccessDeniedException;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.voipservice.VoIPConferenceServiceException;

public class AuthData {
    private BindingService binding;
    
    public static Logger log = Logger.getLogger(AuthData.class);

    private PEPService pep;
    private PAPService pap;
    private NotificationService notification;
    private MembershipService membership;
    private BrowserService browser;
    private CoreService core;
    
    private String action;
    private String path;
    
    CalendarServiceBean calendar;
    
    public String getProfilePathForConnectedIdentifier() {
	if (membership != null) { 
	    try {
		return membership.getProfilePathForConnectedIdentifier();
	    } catch (Exception e) {
		//e.printStackTrace();
	    }
	} 
	return null;
    }
    
    public void checkSecurity() {
	if (pep != null) {
	    try {
		pep.checkSecurity(getProfilePathForConnectedIdentifier(), path, action);
	    } catch (Exception e) {
		//e.printStackTrace();
	    }
	}
    }    
   
    public BindingService getBinding() {
        return binding;
    }

    public void setBinding(BindingService binding) {
        this.binding = binding;
    }

    public PEPService getPep() {
        return pep;
    }

    public void setPep(PEPService pep) {
        this.pep = pep;
    }

    public PAPService getPap() {
        return pap;
    }

    public void setPap(PAPService pap) {
        this.pap = pap;
    }

    public NotificationService getNotification() {
        return notification;
    }

    public void setNotification(NotificationService notification) {
        this.notification = notification;
    }

    public MembershipService getMembership() {
        return membership;
    }

    public void setMembership(MembershipService membership) {
        this.membership = membership;
    }

    public BrowserService getBrowser() {
        return browser;
    }

    public void setBrowser(BrowserService browser) {
        this.browser = browser;
    }

    public CoreService getCore() {
        return core;
    }

    public void setCore(CoreService core) {
        this.core = core;
    }
    
    
    public CalendarServiceBean updateCalendar() {
	 calendar = new CalendarServiceBean();
    	 calendar.setMembershipService(getMembership());
    	 calendar.setBindingService(getBinding());
    	 calendar.setCore(getCore());
    	 calendar.setNotificationService(getNotification());
    	 calendar.setBrowser(getBrowser());
    	 calendar.setPAPService(getPap());
    	 calendar.setPEPService(getPep());
    	 calendar.setCalendarWS(new org.qualipso.factory.collaboration.ws.CalendarWSBean()); 
    	
    	return calendar;
    }
    

	public void saveFolderPath(String path, String name, String description)
	    throws VoIPConferenceServiceException {
	try {
	    core.readFolder(path);
	    log.info("Path " + path + " allready exitst");
	  
	} catch (AccessDeniedException e) {
	    throw new VoIPConferenceServiceException(e.getMessage(),e);
	} catch (InvalidPathException e) {
	    throw new VoIPConferenceServiceException(e.getMessage(),e);
	} catch (PathNotFoundException ex) {
	    try {
		core.createFolder(path, name, description);
		log.info("Crated Path " + path + " succesfully");
	    } catch (CoreServiceException e) {
		throw new VoIPConferenceServiceException(
			"Unable to create date path." + path);
	    } catch (AccessDeniedException e) {
		throw new VoIPConferenceServiceException(e.getMessage(),e);
	    } catch (InvalidPathException e) {
		throw new VoIPConferenceServiceException(e.getMessage(),e);
	    } catch (PathAlreadyBoundException e) {
		throw new VoIPConferenceServiceException(e.getMessage(),e);
	    }
	} catch (CoreServiceException e) {
	    throw new VoIPConferenceServiceException(e.getMessage(),e);
	}

}
}
