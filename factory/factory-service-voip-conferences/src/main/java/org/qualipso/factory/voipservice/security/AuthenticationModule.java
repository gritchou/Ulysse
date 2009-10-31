package org.qualipso.factory.voipservice.security;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.qualipso.factory.voipservice.VoIPConferenceServiceException;
import org.qualipso.factory.voipservice.conference.AsteriskConferenceSettings;
import org.qualipso.factory.voipservice.manager.AsteriskJavaListener;
import org.qualipso.factory.voipservice.utils.AsteriskConferenceUtils;

/**
 * Authentication module - used for granting permission for calling methods
 * 
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @author <a href="mailto:debe@man.poznan.pl">Marcin Wrzos</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo
 * @date 24/07/2009
 */
public class AuthenticationModule {

    /**
     * @param userId
     * @param pass
     */
    public AuthenticationModule(String userId, String pass) throws VoIPConferenceServiceException {
	try {
	    @SuppressWarnings("unused")
	    AsteriskJavaListener instance = AsteriskJavaListener.getInstance();
	} catch (VoIPConferenceServiceException e) {
	    throw new VoIPConferenceServiceException(e.getLocalizedMessage());
	}
	AuthenticationModule.authenticate(userId, pass);
    }

    /**
     * Authentication is done by factory - in this method everyone is allowed to
     * call methods
     * 
     * @param userId
     * @return
     * @throws java.lang.SecurityException
     */
    static public boolean authenticate(String userId, String pass) throws java.lang.SecurityException {
	checkDB(null);
	if (userId == null || userId.equals("")) {
	    throw new java.lang.SecurityException("Cannot authenticate user");
	}
	return true;
    }

    /**
     * Check is user is super admin
     * 
     * @param userId
     * @return
     */
    static public boolean isSuperUser(String userId, String pass) {
	if (userId != null) {
	    return true;
	} else {
	    return false;
	}
    }

    public static synchronized void checkDB(EntityManager em) {
	boolean close = false;
	if (em == null) {
	    em = AsteriskConferenceUtils.getEntityManager();
	    close = true;
	}
	try {
	    em.getTransaction().begin();
	} catch (Exception e) {
	    e.printStackTrace();
	    return;
	}
	
	try {
	    Query query = em.createQuery("FROM SipConf");
	    query.getResultList();
	    em.getTransaction().commit();
	} catch (Exception e) {
	    AsteriskConferenceSettings.resetDatabase();
	}
	if (close == true) {
	    em.close();
	}
    }

}
