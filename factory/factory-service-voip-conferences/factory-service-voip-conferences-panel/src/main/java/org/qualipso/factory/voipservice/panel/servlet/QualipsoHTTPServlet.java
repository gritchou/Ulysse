/*
 * Created on: 2008-02-11, 12:27:22
 * File: QualipsoHTTPServlet.java
 * Package: org.qualipso.factory.voipservice.panel
 * $Revision:  $
 * $Author: janny $
 *
 * This software is the confidential and proprietary information of
 * Poznan Supercomputing and Networking Center (&quot;Confidential Information&quot;).
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into
 * with PSNC.
 *
 * */ 
package org.qualipso.factory.voipservice.panel.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.qualipso.factory.voipservice.panel.client.QualipsoVoIPConferenceServiceClient;

public abstract class QualipsoHTTPServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 8965581292799181638L;

	/**
	 *
	 */
	protected static Logger log = Logger.getLogger(QualipsoHTTPServlet.class);

	/**
	 *
	 */
	protected String servletEncoding = null;

	/**
	 *
	 */
	protected String servletServiceEndpoint = null;

	//asterisk username
	protected String servletUsername = null;
	
	/**
	 *
	 */
	//qualispo username
	protected String servletUserId = null;
	
	protected String servletProjectId = "qualipso";
	
	/**
	 *
	 */
	protected String servletUserPass = null;

	private static String USERID = "userid";
	private static String USERNAME = "username";
	private static String PASS = "pass";
	
	public static QualipsoVoIPConferenceServiceClient asteriskService = null;

	/**
	 *
	 */
	public QualipsoHTTPServlet() {
		super();
	}


	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		servletEncoding = getServletContext().getInitParameter("PARAMETER_ENCODING");
		servletServiceEndpoint=getServletContext().getInitParameter("SERVICE_ENDPOINT");
		asteriskService = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);
		
	}

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (checkCredentials(request, response)) {
			updateRequest(request);
			processRequest(request, response);
		}
	}

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (checkCredentials(request, response)) {
			updateRequest(request);
			processRequest(request, response);
		}
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	abstract protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;


	/**
	 * @param request
	 */
	protected void updateRequest(HttpServletRequest request) {
		if (servletEncoding != null) {
			try {
				request.setCharacterEncoding(servletEncoding);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws ServletException 
	 */
	private boolean checkCredentials(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);

		String sessionUser 	= (String)session.getAttribute(USERID);
		servletUserId 		= (String)request.getParameter(USERID);

		if (sessionUser == null) sessionUser = "";
		if (servletUserId == null) servletUserId = "";

		if (servletUserId.equals("") && !sessionUser.equals("")) {
			servletUserId = sessionUser;
		}
		servletUsername = asteriskService.getUsernameFromQualipsoUsers(servletUserId);

		if (!servletUserId.equals("")) {
			if (!sessionUser.equals(servletUserId)) {
				session.setAttribute(USERID, servletUserId);
			}
		} 
		
		Cookie cookie_id = new Cookie(USERID, servletUserId);
		response.addCookie(cookie_id);
		cookie_id = new Cookie(USERNAME, servletUsername);
		response.addCookie(cookie_id);
		cookie_id = new Cookie(PASS, "empty");
		response.addCookie(cookie_id);
		
		log.debug("servletUserId=" + servletUserId);
		
		if (servletUserId.equals("")) {
			request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
			return false;
		}

		return true;
	}
	
	
	protected void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(true);
	    session.removeAttribute(USERID);
	    session.invalidate();

	    Cookie cookie_id = new Cookie(USERID, "");
	    response.addCookie(cookie_id);
	    response.sendRedirect(request.getContextPath() + "/");
	}
}
