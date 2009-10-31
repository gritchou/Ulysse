package org.qualipso.factory.voipservice.panel.servlet;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qualipso.factory.voipservice.client.ws.ConferenceDetails;
import org.qualipso.factory.voipservice.panel.client.QualipsoVoIPConferenceServiceClient;
import org.qualipso.factory.voipservice.panel.util.PanelTld;


public class MainServlet extends QualipsoHTTPServlet {
	static final long serialVersionUID = 1L;
	static private int defaultMaxUsers = 10;

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 * @throws ConsumerException 
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String act = request.getParameter("act");
		if (act == null) {
			conferenceMain(request, response);
		} else if (act.equals("add")) {
			conferenceAdd(request, response);
		} else if (act.equals("del")) {
			conferenceDel(request, response);
		} else if (act.equals("edit")) {
			conferenceEdit(request, response);
		} else if (act.equals("detail")) {
			conferenceDetails(request, response);
		} else if (act.equals("pastDetail")) {
			pastConferenceDetails(request, response);
		} else if (act.equals("pastDel")) {
			pastConferenceDel(request, response);
		} else if (act.equals("ban")) {
			userBan(request, response);
		} else if (act.equals("unban")) {
			userUnban(request, response);
		} else if (act.equals("mute")) {
			userMute(request, response);
		} else if (act.equals("unmute")) {
			userUnmute(request, response);
		} else if (act.equals("kick")) {
			userKick(request, response);
		} else if (act.equals("lock")) {
			conferenceLock(request, response);
		} else if (act.equals("unlock")) {
			conferenceUnlock(request, response);
		} else if (act.equals("end")) {
			conferenceEnd(request, response);
		} else if (act.equals("logout")) {
			logout(request, response);
		}

		//FIXME dodac banned list w widoku administratora
		//FIXME dodac linki z view do edit dla wlasciciela i odwrotnie
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void conferenceMain(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		log.debug("### servletServiceEndpoint =" + servletServiceEndpoint);

		QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);

		List<ConferenceDetails> myDetailsList = asterisk.getMyConferencesDetailsList(servletUserId, servletUserPass,servletUsername, servletProjectId);
		request.setAttribute("myConferences", myDetailsList);

		List<ConferenceDetails> invitedToDetailsList = asterisk.getInvitedToConferencesDetailsList(servletUserId, servletUserPass,servletUsername, servletProjectId);
		request.setAttribute("invitedToConferences", invitedToDetailsList);

		List<ConferenceDetails> pastDetailsList = asterisk.getPastConferencesDetailsList(servletUserId, servletUserPass,servletUsername, servletProjectId);
		request.setAttribute("pastConferences", pastDetailsList);

		List<ConferenceDetails> publicDetailsList = asterisk.getPublicConferencesDetailsList(servletUserId, servletUserPass, servletProjectId);
		request.setAttribute("publicConferences", publicDetailsList);

		request.getRequestDispatcher("/jsp/main.jsp").forward(request, response);
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void conferenceAdd(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {

		//TODO create proper owner list (add groups)
		//add user plus all groups he belongs to
		//List<String> list=asterisk.getUserList();
		List<String> list = new ArrayList<String>();
		list.add(servletUsername);
		request.setAttribute("ownerOptions", list);

		request.setAttribute("defaultMaxUsers", defaultMaxUsers);
		request.setAttribute("startDate",
		PanelTld.formatDate(new Date().getTime() / 1000));
		request.setAttribute("endDate", PanelTld.formatDate(new Date().getTime() / 1000));

		request.getRequestDispatcher("/jsp/add.jsp").forward(request, response);
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void conferenceDel(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {

		QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);
		try {
			Integer confId = Integer.parseInt(request.getParameter("id"));
			asterisk.removeConference(servletUserId, servletUserPass,confId);
		} catch (NumberFormatException nfex) {
			throw new ServletException();
		}
		response.sendRedirect("");
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void conferenceEnd(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

			QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);
			try {
				Integer confId = Integer.parseInt(request.getParameter("id"));
				asterisk.endConference(servletUserId, servletUserPass, confId);
			} catch (NumberFormatException nfex) {
				throw new ServletException();
			}
			response.sendRedirect("");
		}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void conferenceEdit(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);

		// TODO create proper owner list
		//add user plus all groups he belongs to
		//List<String> list=asterisk.getUserList();
		List<String> list = new ArrayList<String>();
		list.add(servletUsername);
		request.setAttribute("ownerOptions", list);

		Integer confNo = Integer.parseInt(request.getParameter("id"));
		ConferenceDetails confDetails = asterisk.getConferenceDetails(servletUserId, servletUserPass,confNo);

		request.setAttribute("owner", confDetails.getOwner());

		Integer ownerIndex = list.indexOf(confDetails.getOwner());
		request.setAttribute("ownerIndex", ownerIndex);

		request.setAttribute("startDate", PanelTld.formatDate(confDetails.getStartDate()));
		request.setAttribute("endDate", PanelTld.formatDate(confDetails.getEndDate()));

		request.setAttribute("confTime", 	Boolean.toString(confDetails.isPermanent()));
		request.setAttribute("confAccess", confDetails.getAccessType());

		List<String> invitedList = asterisk.getInvitedUsersList(servletUserId, servletUserPass,confNo);
		request.setAttribute("invitedUsers", invitedList);

		request.setAttribute("pin", confDetails.getPin());
		request.setAttribute("adminPin", confDetails.getAdminPin());
		request.setAttribute("maxUsers", confDetails.getMaxUsers());

		request.setAttribute("name", confDetails.getName());
		request.setAttribute("agenda", confDetails.getAgenda());
		request.setAttribute("confRecord", confDetails.isRecorded());

		request.getRequestDispatcher("/jsp/edit.jsp").forward(request, response);
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void conferenceDetails(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);

		Integer confId;
		try {
			confId = Integer.parseInt(request.getParameter("id"));
		} catch (NumberFormatException nfex) {
			throw new ServletException();
		}

		ConferenceDetails details = asterisk.getConferenceDetails(servletUserId, servletUserPass,confId);
		request.setAttribute("conference", details);
		request.setAttribute("isRecorded", details.isRecorded());
		request.setAttribute("confTime", details.isPermanent());

		if (details.getConfNo()==-1) {
			request.getRequestDispatcher("/jsp/invalidconference.jsp").forward(request, response);
		} else {
			List<String> invitedList = asterisk.getInvitedUsersList(servletUserId, servletUserPass, confId);
			request.setAttribute("invitedUsers", invitedList);

			List<String> bannedList = asterisk.getBannedUsersList(servletUserId, servletUserPass, confId);
			request.setAttribute("bannedUsers", bannedList);

			List<QualipsoVoIPConferenceServiceClient.ParticipantsInfo> list = asterisk.getParticipantsList(servletUserId, servletUserPass,confId);
			request.setAttribute("participants", list);
			request.getRequestDispatcher("/jsp/details.jsp").forward(request, response);
		}
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void pastConferenceDetails(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);

		Integer id;

		try {
			id = Integer.parseInt(request.getParameter("id"));
		} catch (NumberFormatException nfex) {
			throw new ServletException();
		}

		ConferenceDetails details = asterisk.getConferenceDetails(servletUsername, servletUserPass,id);
		request.setAttribute("conference", details);

		if (details.getConfNo()==-1) {
			request.getRequestDispatcher("/jsp/invalidconference.jsp").forward(request, response);
		} else {
			List<String> list = asterisk.getPastConferenceInvitedUsers(servletUsername, servletUserPass, id);
			request.setAttribute("invitedUsers", list);

			String recordings=asterisk.getPastConferenceRecordings(servletUsername, servletUserPass, id);
			request.setAttribute("recordings", recordings);

			request.getRequestDispatcher("/jsp/pastDetails.jsp").forward(request, response);
		}
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void pastConferenceDel(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);

		Integer id;

		try {
			id = Integer.parseInt(request.getParameter("id"));
		} catch (NumberFormatException nfex) {
			throw new ServletException();
		}

		asterisk.removeConference(servletUsername, servletUserPass, id);
		response.sendRedirect("");
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void userBan(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);

		Integer confId;
		Integer userConferenceId;

		try {
			confId = Integer.parseInt(request.getParameter("id"));
			userConferenceId = Integer.parseInt(request.getParameter("user"));
		} catch (NumberFormatException nfex) {
			throw new ServletException();
		}

		asterisk.banUser(servletUserId, servletUserPass, confId, userConferenceId);
		response.sendRedirect("index.jsp?act=detail&id=" + confId);
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void userUnban(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);

		Integer confId;
		String user = request.getParameter("user");

		try {
			confId = Integer.parseInt(request.getParameter("id"));
		} catch (NumberFormatException nfex) {
			throw new ServletException();
		}

		asterisk.unbanUser(servletUserId, servletUserPass,confId, user);
		response.sendRedirect("index.jsp?act=detail&id=" + confId);
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void userMute(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);

		Integer confId;
		Integer userConferenceId;

		try {
			confId = Integer.parseInt(request.getParameter("id"));
			userConferenceId = Integer.parseInt(request.getParameter("user"));
		} catch (NumberFormatException nfex) {
			throw new ServletException();
		}

		asterisk.muteUser(servletUserId, servletUserPass,confId,userConferenceId);
		response.sendRedirect("index.jsp?act=detail&id=" + confId);
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void userUnmute(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);

		Integer confId;
		Integer userConferenceId;

		try {
			confId = Integer.parseInt(request.getParameter("id"));
			userConferenceId = Integer.parseInt(request.getParameter("user"));
		} catch (NumberFormatException nfex) {
			throw new ServletException();
		}

		asterisk.unmuteUser(servletUserId, servletUserPass,confId, userConferenceId);
		response.sendRedirect("index.jsp?act=detail&id=" + confId);
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void userKick(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);

		Integer confId;
		Integer userConferenceId;

		try {
			confId = Integer.parseInt(request.getParameter("id"));
			userConferenceId = Integer.parseInt(request.getParameter("user"));
		} catch (NumberFormatException nfex) {
			throw new ServletException();
		}

		asterisk.kickUser(servletUserId, servletUserPass,confId, userConferenceId);
		response.sendRedirect("index.jsp?act=detail&id=" + confId);
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void conferenceLock(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);

		try {
			Integer confId = Integer.parseInt(request.getParameter("id"));
			asterisk.lockConference(servletUserId, servletUserPass,confId);
			//TODO map path
			response.sendRedirect("index.jsp?act=detail&id=" + confId);
		} catch (NumberFormatException nfex) {
			throw new ServletException();
		}
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void conferenceUnlock(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);

		try {
			Integer confId = Integer.parseInt(request.getParameter("id"));
			asterisk.unlockConference(servletUserId, servletUserPass,confId);
			//TODO map path
			response.sendRedirect("index.jsp?act=detail&id=" + confId);
		} catch (NumberFormatException nfex) {
			throw new ServletException();
		}
	}
}
