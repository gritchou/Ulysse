package org.qualipso.factory.voipservice.security;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.voipservice.VoIPConferenceServiceException;
import org.qualipso.factory.voipservice.conference.AsteriskConferenceSettings;
import org.qualipso.factory.voipservice.manager.AsteriskJavaListener;
import org.qualipso.factory.voipservice.utils.AsteriskConferenceUtils;
import org.qualipso.factory.voipservice.utils.AuthData;

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

    public static Logger log = Logger.getLogger(AuthenticationModule.class);

    /**
     * @param userId
     * @param pass
     */
    public AuthenticationModule(String userId, String pass, AuthData authData) throws VoIPConferenceServiceException {
	try {
	    @SuppressWarnings("unused")
	    AsteriskJavaListener instance = AsteriskJavaListener.getInstance();
	} catch (VoIPConferenceServiceException e) {
	    throw new VoIPConferenceServiceException(e.getLocalizedMessage());
	}
	AuthenticationModule.authenticate(userId, pass, authData);
    }

    /**
     * Authentication is done by factory - in this method everyone is allowed to
     * call methods
     * 
     * @param userId
     * @return
     * @throws java.lang.SecurityException
     * @throws VoIPConferenceServiceException
     */
    static public boolean authenticate(String userId, String pass, AuthData authData)
	    throws java.lang.SecurityException, VoIPConferenceServiceException {
	checkDB(null);

	if (authData != null) {
	    String caller = authData.getProfilePathForConnectedIdentifier();
	    if (caller == null) {
		// throw new VoIPConferenceServiceException (
		// "Could not get connected profile");
	    }
	    authData.checkSecurity();
	}

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
	synchronized (log) {
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
}
