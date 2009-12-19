package org.claros.chat.utility;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.claros.chat.ajax.Logout;

/**
 * @version 	1.0
 * @author		Umut Gškbayrak
 */
public class SessionListener implements HttpSessionListener {
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent arg0) {
		// do nothing
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent arg0) {
		HttpSession sess = (HttpSession)arg0.getSession();
		Logout.logout(sess);
	}
}
