package org.qualipso.factory.voipservice.email;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import org.apache.log4j.Logger;
import org.qualipso.factory.voipservice.utils.AsteriskConferenceUtils;

/**
 * SMTP authentication for VoIP application
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */
public class SMTPAuthenticator extends Authenticator {
	private static final Logger log = Logger.getLogger(SMTPAuthenticator.class);
	
	/**
	 * object with properties  
	 */
	private Properties properties = null;

	/**
	 * Default constructor
	 */
	public SMTPAuthenticator() {
		super();
		//load properties from file
		properties = AsteriskConferenceUtils.getProperties();
	}

	/* (non-Javadoc)
	 * @see javax.mail.Authenticator#getPasswordAuthentication()
	 */
	public PasswordAuthentication getPasswordAuthentication() {
		//get username and password for password authentication 
		String user = properties.getProperty(AsteriskConferenceUtils.MAIL_SMTP_USER);
		String pass = properties.getProperty(AsteriskConferenceUtils.MAIL_SMTP_PASSWORD);

		log.debug("user = " + user);
		log.debug("pass = " + pass);
		
		// pasword authentication for SMTP mails
		return new PasswordAuthentication(user, pass);
}

	/**
	 * Get email addres
	 * @return email address
	 */
	public String getAddress() {
		return properties.getProperty(AsteriskConferenceUtils.MAIL_SMTP_ADDRESS);
	}

	/**
	 * Get full name for email address
	 * @return fullname full name 
	 */
	public String getFullName() {
		return properties.getProperty(AsteriskConferenceUtils.MAIL_SMTP_FULLNAME);
	}
}
