package org.claros.chat.ajax;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.claros.chat.controllers.TrafficController;
import org.claros.chat.threads.ChatListener;
import org.claros.chat.threads.ChatSender;
import org.jivesoftware.smack.XMPPConnection;

public class Logout extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 418575745334176421L;

	/**
	 * Constructor of the object.
	 */
	public Logout() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy();
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Expires","-1");
		response.setHeader("Pragma","no-cache");
		response.setHeader("Cache-control","no-cache");

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		logout(request.getSession(false));
		out.print("ok");
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * 
	 * @param request
	 */
	public static void logout(HttpSession sess) {
		if (sess != null) {
			String user = null;
			try {
				XMPPConnection conn = (XMPPConnection)sess.getAttribute("conn");
				if (conn != null) {
					user = conn.getUser();
					conn.close();
					sess.setAttribute("conn", null);
				}
			} catch (Throwable e) {}

			try {
				ChatListener listener = TrafficController.getListener(user);
				if (listener != null) {
					listener.terminate();
					if (user != null) {
						TrafficController.removeListener(user);
					}
				}
			} catch (Throwable e) {}
			
			try {
				ChatSender sender = TrafficController.getSender(user);
				if (sender != null) {
					sender.terminate();
					if (user != null) {
						TrafficController.removeSender(user);
					}
				}
			} catch (Throwable e) {}

			try {
				sess.invalidate();
			} catch (Throwable e) {}
		}
	}

}
