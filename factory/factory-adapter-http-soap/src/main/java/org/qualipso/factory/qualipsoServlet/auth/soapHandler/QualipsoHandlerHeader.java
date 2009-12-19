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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

import org.jboss.ws.extensions.security.Util;
import org.jboss.ws.extensions.security.nonce.DefaultNonceFactory;
import org.jboss.ws.extensions.security.nonce.DefaultNonceGenerator;
import org.jboss.ws.extensions.security.nonce.NonceGenerator;
import org.jboss.ws.extensions.security.operation.SendUsernameOperation;
import org.jboss.ws.extensions.security.element.UsernameToken ;
import org.jboss.ws.extensions.security.element.Timestamp;
import org.jboss.xb.binding.SimpleTypeBindings;

/**
 * Manage the WS-security into the soap request for QualiPSo factory.
 * Use example in http://www.javadb.com/using-a-message-handler-to-alter-the-soap-header-in-a-web-service-client
 * And in http://issues.apache.org/jira/secure/attachment/12334810/WsseClientHandler.java
 * 
 * @author Emmanuel Meier
 */

public class QualipsoHandlerHeader implements SOAPHandler<SOAPMessageContext> 
{
	protected static final String HTTP_WSS_USERNAME_PASSWORD_DIGEST = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest";
	protected static final String HTTP_WSS_USERNAME_PASSWORD_TEXT = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText";
	protected static final String HTTP_SCHEMA_WSS_WSSECURITY_XSD = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
	protected static final String HTTP_NS_WSS_XSD = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
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
		
		
		
    	SOAPElement security = theHeader.addChildElement("Security", "wsse", QualipsoHandlerHeader.HTTP_SCHEMA_WSS_WSSECURITY_XSD);
    	security.setAttribute(env.getPrefix()+":mustUnderstand", "1");
    	
    	SOAPElement timestamp = security.addChildElement("Timestamp", "wsu",QualipsoHandlerHeader.HTTP_NS_WSS_XSD);
    	timestamp.setAttribute("wsu:Id", Util.generateId("Timestamp"));
    	
    	SOAPElement createdDate = timestamp.addChildElement("Created", "wsu");
    	
    	Calendar createdTime = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    	Date todayDate = createdTime.getTime(); 
    	todayDate.setTime(todayDate.getTime() - 3600000); // - less 1 hour - 3600000 in winter, 7200000 in summer
    	
    	createdDate.addTextNode(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").format(todayDate));
    	
    	Date expireDate = (Date)todayDate.clone();
    	expireDate.setTime(todayDate.getTime() + 300000); // ttl = 300
    	
    	SOAPElement expiredDate = timestamp.addChildElement("Expired", "wsu");
    	expiredDate.addTextNode(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").format(expireDate));
    	
    	DefaultNonceGenerator nonceGenered = new DefaultNonceGenerator();
    	String nonceDigest = nonceGenered.generateNonce();
    	
    	SOAPElement usernameToken = security.addChildElement("UsernameToken", "wsse");
    	usernameToken.setAttribute("wsu:Id", Util.generateId("UsernameToken"));
    	//usernameToken.addAttribute(new QName("wsu",), QualipsoHandlerHeader.HTTP_NS_WSS_XSD);
    	usernameToken.setAttribute("xmlns:wsu", QualipsoHandlerHeader.HTTP_NS_WSS_XSD);
    	
    	SOAPElement username = usernameToken.addChildElement("Username", "wsse");
    	username.addTextNode(loginName);

    	SOAPElement password = usernameToken.addChildElement("Password", "wsse");
    	password.setAttribute("Type", QualipsoHandlerHeader.HTTP_WSS_USERNAME_PASSWORD_DIGEST);
    	String passwordDigest = /* SendUsernameOperation.createPasswordDigest(nonceDigest, 
    								SimpleTypeBindings.marshalDateTime(createdTime), passWord); */
    		passWord;
    	if(passwordDigest == null)
    	{
    		throw new Exception("Error in creation of the password Digest in header soap !");
    	}
    	password.addTextNode(passwordDigest);///*getBase64Digest(utf8decode(passWord))*/);

    	SOAPElement nonce = usernameToken.addChildElement("Nonce", "wsse");
    	nonce.addTextNode(nonceDigest);
    	
    	Date creatDate = (Date)todayDate.clone();
    	creatDate.setTime(todayDate.getTime() + 1000);
    	SOAPElement myDate = usernameToken.addChildElement("Created", "wsu");
    	
    	myDate.addTextNode(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'").format(creatDate));
    	System.out.println("header wsse : "+env.getHeader().toString());
	}
	
	/**
	 * Above the code is written in this url : 
	 * http://issues.apache.org/jira/secure/attachment/12334810/WsseClientHandler.java
	 */
	
}
