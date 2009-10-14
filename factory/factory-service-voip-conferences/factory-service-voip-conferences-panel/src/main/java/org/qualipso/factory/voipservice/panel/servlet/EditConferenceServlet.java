package org.qualipso.factory.voipservice.panel.servlet;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qualipso.factory.voipservice.panel.client.QualipsoVoIPConferenceServiceClient;
import org.qualipso.factory.voipservice.panel.util.PanelTld;

public class EditConferenceServlet extends QualipsoHTTPServlet {
	static final long serialVersionUID = 2L;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String idConf = request.getParameter("idConf");
		Integer confNumber;
		try {
			confNumber = Integer.parseInt(idConf);
		} catch (NumberFormatException e) {
			response.getOutputStream().println("Invalid conference number");
			return;
		}

		String confTime = request.getParameter("confTime");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String confAccess = request.getParameter("confAccess");
		String maxUsers = request.getParameter("maxUsers");
		String pin = request.getParameter("pin");
		String adminpin = request.getParameter("adminpin");
		String name = request.getParameter("name");
		String agenda = request.getParameter("agenda");
		String owner = request.getParameter("owner");
		String recorded = request.getParameter("recorded");
		String[] invited = request.getParameterValues("invitedUsers");

		Calendar today = new GregorianCalendar();
		response.setDateHeader("Last-Modified", today.getTime().getTime());
		response.setDateHeader("Expires", 0);
		response.setHeader("Pragma","no-cache");
		response.setHeader("Cache-Control","no-cache");

		Integer maxUsersNumber = null;
		try {
			if (pin==null || pin.equals(""))
				pin="";
			else
				Integer.parseInt(pin);
		} catch (NumberFormatException e) {
			response.getOutputStream().println("Entered pin is invalid");
		}
		try {
			if (adminpin==null || adminpin.equals(""))
				adminpin="";
			else
			Integer.parseInt(adminpin);
		} catch (NumberFormatException e) {
			response.getOutputStream().println("Entered adminpin is invalid");
		}

		try {
			maxUsersNumber = Integer.parseInt(maxUsers);
		} catch (NumberFormatException e) {
			response.getOutputStream().print("Invalid 'Max Users' format");
		}

		boolean permanent = false;

		if ((confTime != null) && confTime.equals("true")) {
			permanent = true;
		}

		Long startDateNumber = 0L;
		Long endDateNumber = 0L;

		try {
			if (!permanent) {
				if ((startDate == null) || (endDate == null)) {
					throw new NullPointerException();
				}
				startDateNumber = PanelTld.getDateFormat().parse(startDate).getTime() / 1000;
				endDateNumber = PanelTld.getDateFormat().parse(endDate).getTime() / 1000;
			}
			short accessType = Short.parseShort(confAccess);

			boolean rec=false;
			if (recorded!=null && recorded.equals("on")) {
				rec=true;
			}

			QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);
			asterisk.editConference(servletUserId, servletUserPass,confNumber, owner, accessType, permanent,pin, adminpin, startDateNumber, endDateNumber,maxUsersNumber, invited, name, agenda,rec);
			response.sendRedirect("");
		} catch (ParseException e) {
			response.getOutputStream().print("Invalid Date Format");
		} catch (NullPointerException e) {
			response.getOutputStream().print("Invalid Date Format");
		} catch (NumberFormatException e) {
			response.getOutputStream().print("Invalid access type format");
		}
	}
}
