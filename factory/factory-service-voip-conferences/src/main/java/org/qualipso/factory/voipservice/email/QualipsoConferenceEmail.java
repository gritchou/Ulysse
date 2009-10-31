package org.qualipso.factory.voipservice.email;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.qualipso.factory.voipservice.utils.AsteriskConferenceUtils;

/**
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo 
 * @date 24/07/2009
 */
public class QualipsoConferenceEmail {
	private static final Logger log = Logger.getLogger(QualipsoConferenceEmail.class);
	
	/**
	 * Test - sending email
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		Properties props = AsteriskConferenceUtils.getProperties();
		String subject = "test subject";
		String content = "test content";
		String to ="janny@man.poznan.pl";
		new QualipsoConferenceEmail().sendMessage(props, subject, content , to);
	} 

	/**
	 * Send mail message
	 * @param props properties for email server authentication 
	 * @param subject message subject
	 * @param content message content
	 * @param to message reseipient
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */ 
	public void sendMessage(Properties props, String subject, String content, String to) throws UnsupportedEncodingException, MessagingException {
		log.debug("authenticator...");
		SMTPAuthenticator auth = new SMTPAuthenticator();
		
		String prefix = "mail.smtp.socketFactory.port";
		if (props.get(prefix) != null 
			&& props.get(prefix).equals("${"+prefix+"}") ) {
		    props.remove(prefix);
		}
		 prefix = "mail.smtp.socketFactory.class";
		if (props.get(prefix) != null 
			&& props.get(prefix).equals("${"+prefix+"}") ) {
		    props.remove(prefix);
		}
		prefix = "mail.smtp.socketFactory.fallback";
		if (props.get(prefix) != null 
			&& props.get(prefix).equals("${"+prefix+"}") ) {
		    props.remove(prefix);
		}	
		
		Session mailSession = Session.getDefaultInstance(props, auth);

		//setup message object
		MimeMessage message = new MimeMessage(mailSession);
		message.setSubject(subject);
		message.setText(content);
		message.setFrom(new InternetAddress(auth.getAddress(), auth.getFullName()));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

		//init transport object
		Transport transport = mailSession.getTransport();
		log.debug("connect...");
		transport.connect();
		log.debug("send message...");
		transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
		log.debug("close...");
		transport.close();
		log.debug("mail connection closed");
	}
}
