package org.qualipso.factory.voipservice.panel.servlet;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.qualipso.factory.voipservice.client.ws.ConferenceDetails;
import org.qualipso.factory.voipservice.panel.client.QualipsoVoIPConferenceServiceClient;
import org.qualipso.factory.voipservice.panel.client.QualipsoVoIPConferenceServiceClient.ParticipantsInfo;
import org.qualipso.factory.voipservice.panel.util.PanelTld;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Servlet implementation class for Servlet: AjaxServlet
 *
 */
 public class AjaxServlet extends QualipsoHTTPServlet {

	 static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public AjaxServlet() {
		super();
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		log.debug("### processRequest - AJAX");
		String pathInfo=request.getPathInfo();

		if ("/Info".equals(pathInfo)) {
			JSONObject json=new JSONObject();
			try {
				Integer idConf = Integer.parseInt(request.getParameter("idConf"));

				QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);
				List<ParticipantsInfo> participantList = asterisk.getParticipantsList(servletUserId, servletUserPass, idConf);
				JSONArray jsonArray = new JSONArray();
				if (participantList != null) {
					for (ParticipantsInfo participantsInfo : participantList) {
						jsonArray.put(participantsInfo.toJSONObject());
					}
				}
				json.put("participants",jsonArray);

				List<String> bannedList=asterisk.getBannedUsersList(servletUserId, servletUserPass, idConf);
				if (bannedList!=null)
					jsonArray=new JSONArray(bannedList);
				else
					jsonArray=new JSONArray();
				json.put("bannedUsers",jsonArray);

				List<String> invitedList=asterisk.getInvitedUsersList(servletUserId, servletUserPass, idConf);
				if (invitedList!=null)
					jsonArray=new JSONArray(invitedList);
				else
					jsonArray=new JSONArray();
				json.put("invitedUsers",jsonArray);

				ConferenceDetails confDetails=asterisk.getConferenceDetails(servletUserId, servletUserPass, idConf);
				json.put("details", toJSONObject(confDetails));

				response.setContentType("application/x-json");
				response.getWriter().print(json);
			}
			catch (NumberFormatException nfex) {
				response.getWriter().println("a");
			}
			catch (JSONException jex) {
				response.getWriter().println("b");
			}
		}
		else if ("/Kick".equals(pathInfo)) {
			try {
				Integer idConf = Integer.parseInt(request.getParameter("idConf"));
				Integer userConferenceId = Integer.parseInt(request.getParameter("userConfId"));

				QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);
				asterisk.kickUser(servletUserId, servletUserPass, idConf, userConferenceId);
			}
			catch (NumberFormatException nfex) {
				response.getWriter().println("Kick Exception");
			}
		}
		else if ("/Ban".equals(pathInfo)) {
			try {
				Integer idConf = Integer.parseInt(request.getParameter("idConf"));
				Integer userConferenceId = Integer.parseInt(request.getParameter("userConfId"));

				QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);
				asterisk.banUser(servletUserId, servletUserPass, idConf, userConferenceId);
			}
			catch (NumberFormatException nfex) {
				response.getWriter().println("Ban Exception");
			}
		}
		else if ("/Unban".equals(pathInfo)) {
			try {
				Integer idConf = Integer.parseInt(request.getParameter("idConf"));
				String username = request.getParameter("username");

				QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);
				asterisk.unbanUser(servletUserId, servletUserPass, idConf, username);
			}
			catch (NumberFormatException nfex) {
				response.getWriter().println("Unban Exception");
			}
		}
		else if ("/Mute".equals(pathInfo)) {
			try {
				Integer idConf = Integer.parseInt(request.getParameter("idConf"));
				Integer userConferenceId = Integer.parseInt(request.getParameter("userConfId"));

				QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);
				asterisk.muteUser(servletUserId, servletUserPass, idConf, userConferenceId);
			}
			catch (NumberFormatException nfex) {
				response.getWriter().println("Mute Exception");
			}
		}
		else if ("/Unmute".equals(pathInfo)) {
			try {
				Integer idConf = Integer.parseInt(request.getParameter("idConf"));
				Integer userConferenceId = Integer.parseInt(request.getParameter("userConfId"));

				QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);
				asterisk.unmuteUser(servletUserId, servletUserPass, idConf, userConferenceId);
			}
			catch (NumberFormatException nfex) {
				response.getWriter().println("Unmute Exception");
			}
		}
		else if ("/Lock".equals(pathInfo)) {
			try {
				Integer idConf = Integer.parseInt(request.getParameter("idConf"));

				QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);
				asterisk.lockConference(servletUserId, servletUserPass, idConf);
			}
			catch (NumberFormatException nfex) {
				response.getWriter().println("Lock Exception");
			}
		}
		else if ("/Unlock".equals(pathInfo)) {
			try {
				Integer idConf = Integer.parseInt(request.getParameter("idConf"));

				QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);
				asterisk.unlockConference(servletUserId, servletUserPass, idConf);
			}
			catch (NumberFormatException nfex) {
				response.getWriter().println("Unlock Exception");
			}
		}
		else if ("/End".equals(pathInfo)) {
			try {
				Integer idConf = Integer.parseInt(request.getParameter("idConf"));

				QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);
				asterisk.endConference(servletUserId, servletUserPass, idConf);
			}
			catch (NumberFormatException nfex) {
				response.getWriter().println("End Exception");
			}
		}
		else if ("/Mashup".equals(pathInfo)) {
			try {
				QualipsoVoIPConferenceServiceClient asterisk = new QualipsoVoIPConferenceServiceClient(servletServiceEndpoint);

				List<ConferenceDetails> myConferences=asterisk.getMyConferencesDetailsList(servletUserId, servletUserPass, servletUsername, servletProjectId);
				List<ConferenceDetails> invitedToConferences=asterisk.getInvitedToConferencesDetailsList(servletUserId, servletUserPass, servletUsername, servletProjectId);
				List<ConferenceDetails> publicConf=asterisk.getPublicConferencesDetailsList(servletUserId, servletUserPass, servletProjectId);
				List<ConferenceDetails> pastConf=asterisk.getPastConferencesDetailsList(servletUserId, servletUserPass, servletUsername, servletProjectId);

				String xmlResponse = toXML(myConferences,
						invitedToConferences,
						publicConf,
						pastConf,
						4);

				response.setContentType("text/xml");
				response.getWriter().print(xmlResponse);
			}
			catch (NumberFormatException nfex) {
				response.getWriter().println("Mashup Exception");
			}
		}
	}

	private JSONObject toJSONObject(ConferenceDetails confDetails) {
		JSONObject json = null;
		try {
			json= new JSONObject();
			json.put("number", confDetails.getConfNo());
			json.put("name", confDetails.getName());
			json.put("agenda", confDetails.getAgenda());
			json.put("permanent", confDetails.isPermanent());
			json.put("startDate", PanelTld.formatDate(confDetails.getStartDate()));
			json.put("endDate", PanelTld.formatDate(confDetails.getEndDate()));
			json.put("owner", confDetails.getOwner());
			json.put("accessType", confDetails.getAccessType());
			json.put("pin", confDetails.getPin());
			json.put("adminPin", confDetails.getAdminPin());
			json.put("recorded",confDetails.isRecorded());
			json.put("maxUsers", confDetails.getMaxUsers());
		}
		catch (JSONException je) {
			//swallow
		}
		return json;
	}

	private Element toXML(Document doc,ConferenceDetails confDetails) {

		Element conference = doc.createElement("conference");

		Element element = doc.createElement("number");
		Text text = doc.createTextNode(confDetails.getConfNo()+"");
		element.appendChild(text);
		conference.appendChild(element);

		element = doc.createElement("name");
		text = doc.createTextNode(confDetails.getName());
		element.appendChild(text);
		conference.appendChild(element);

		element = doc.createElement("startDate");
		text = doc.createTextNode(PanelTld.formatDate(confDetails.getStartDate()));
		element.appendChild(text);
		conference.appendChild(element);

		element = doc.createElement("endDate");
		text = doc.createTextNode(PanelTld.formatDate(confDetails.getEndDate()));
		element.appendChild(text);
		conference.appendChild(element);

		element = doc.createElement("owner");
		text = doc.createTextNode(confDetails.getOwner());
		element.appendChild(text);
		conference.appendChild(element);

		element = doc.createElement("link");
		text = doc.createTextNode("/asteriskConferencePanel/index.jsp?act=detail&id="+confDetails.getConfNo());
		element.appendChild(text);
		conference.appendChild(element);

		return conference;
	}

	private Element createElement(Document doc, String nodeName, List<ConferenceDetails> list, int limit) {
		Element child = doc.createElement(nodeName);

		int i=0;
		for (ConferenceDetails conferenceDetails : list) {
			child.appendChild(toXML(doc, conferenceDetails));
			++i;
			if (i == limit) {
				break;
			}
		}

		return child;
	}

	private String toXML(List<ConferenceDetails> myList,
			List<ConferenceDetails> invitedList,
			List<ConferenceDetails> publicConf,
			List<ConferenceDetails> pastConf,
			int limit) {

		String xmlString="";
		try {

			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element root = doc.createElement("conferences");
			doc.appendChild(root);


			Element child = createElement(doc, "my", myList, limit);
			root.appendChild(child);

			child = createElement(doc, "invited", invitedList, limit);
			root.appendChild(child);

			child = createElement(doc, "public", publicConf, limit);
			root.appendChild(child);

			child = createElement(doc, "past", pastConf, limit);
			root.appendChild(child);

			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			xmlString = sw.toString();
		}
		catch (ParserConfigurationException e) {
			xmlString="<conferences exception='true' />";
		}
		catch (TransformerException e) {
			xmlString="<conferences exception='true' />";
		}
		return xmlString;
	}
}