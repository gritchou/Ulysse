/**
 * 
 * Copyright (C) 2006-2010 THALES
 * http://www.thalesgroup.fr - emmanuel.meier@thalesgroup.com
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the license LGPL.
 *
 * Initial author :
 * Emmanuel Meier from Thales Services, THERESIS Competence Center Open Source Software
 * 
 */


package org.qualipso.factory.qualipsoServlet.auth.soapHandler;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.handler.MessageContext;

import org.jboss.security.Base64Encoder;
import org.jboss.security.auth.login.Token;
import org.jboss.ws.extensions.security.nonce.DefaultNonceFactory;

/**
 * Manage the WS-security into the soap request for QualiPSo factory.
 * Use example in http://www.javadb.com/using-a-message-handler-to-alter-the-soap-header-in-a-web-service-client
 * And in http://issues.apache.org/jira/secure/attachment/12334810/WsseClientHandler.java
 * 
 * @author Emmanuel Meier
 */

public class QualipsoHandlerHeader implements SOAPHandler<SOAPMessageContext> 
{
	private String loginName;
	private String passWord;
	
	/**
	 * insert headers into the message context soap, the username and the password must be initiated.
	 */
	public boolean handleMessage(SOAPMessageContext messageSoap) 
	{
        Boolean OperationResult = (Boolean) messageSoap.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
    
        if (OperationResult.booleanValue()) 
        {
            SOAPMessage message = messageSoap.getMessage();
            
            // insert wsse nodes into the header of the soap message
            try {
            	SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
                
            	this.insertWSSEheader(envelope);            	
            }
            catch(Exception exception)
            {
            	System.err.println(exception.getMessage());
            	exception.printStackTrace();
            }
        }
        
        return OperationResult;
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close(MessageContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean handleFault(SOAPMessageContext arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * set particular variables : loginName & passWord
	 */
	public void setCredentials(String aUserName,String aPassword)
	{
		this.loginName = aUserName;
		this.passWord = aPassword;
	}
	
	public void insertWSSEheader(SOAPEnvelope env) throws Exception
	{
		SOAPHeader theHeader;
		if(env.getHeader() == null)
		{
			theHeader = env.addHeader();
		}
		else
		{
			theHeader = env.getHeader();
		}
		
    	SOAPElement security = theHeader.addChildElement("Security", "wsse", 
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
    	security.setAttribute(env.getPrefix()+":mustUnderstand", "1");
    	
    	SOAPElement timestamp = security.addChildElement("Timestamp", "wsu",
    		"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
    	timestamp.setAttribute("wsu:Id", "timestamp");
    	
    	SOAPElement createdDate = timestamp.addChildElement("Created", "wsu");    	
    	Date todayDate = Calendar.getInstance().getTime(); 
    	todayDate.setTime(todayDate.getTime() - 7200000); // - less 2 hours - 7200000
    	
    	createdDate.addTextNode(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").format(todayDate));
    	
    	Date expireDate = (Date)todayDate.clone();
    	expireDate.setTime(todayDate.getTime() + 300000);
    	
    	SOAPElement expiredDate = timestamp.addChildElement("Expired", "wsu");
    	expiredDate.addTextNode(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").format(expireDate));
    	
    	SOAPElement usernameToken = security.addChildElement("UsernameToken", "wsse");
    	usernameToken.setAttribute("xmlns:wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");

    	SOAPElement username = usernameToken.addChildElement("Username", "wsse");
    	username.addTextNode(loginName);

    	SOAPElement password = usernameToken.addChildElement("Password", "wsse");
    	password.setAttribute("Type", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest");
    	password.addTextNode(getBase64Digest(utf8decode(passWord)));

    	SOAPElement nonce = usernameToken.addChildElement("Nonce", "wsse");
    	nonce.addTextNode(new DefaultNonceFactory().getGenerator().generateNonce());
    	
    	Date creatDate = (Date)todayDate.clone();
    	creatDate.setTime(todayDate.getTime() + 1000);
    	SOAPElement myDate = usernameToken.addChildElement("Created", "wsu");
    	
    	myDate.addTextNode(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").format(creatDate));
	}
	
	/**
	 * Above the code is written in this url : 
	 * http://issues.apache.org/jira/secure/attachment/12334810/WsseClientHandler.java
	 */
	private static byte[] utf8decode(String input) 
	{
		// UTF-8 encoding
		byte[] ret = null;
		try {
			ret = input.getBytes("UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	private static synchronized String getBase64Digest(byte[] password) 
	{
		try {
			
			MessageDigest messageDigester = MessageDigest.getInstance("SHA-1");

			// SHA-1 ( nonce + created + password )
			messageDigester.reset();
			messageDigester.update(password);
		
			return Base64Encoder.encode(messageDigester.digest());
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}

}
