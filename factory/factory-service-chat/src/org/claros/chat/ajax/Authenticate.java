package org.claros.chat.ajax;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.claros.chat.controllers.TrafficController;
import org.claros.chat.threads.ChatListener;
import org.claros.chat.threads.ChatSender;
//import org.jivesoftware.smack.GoogleTalkConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class Authenticate extends HttpServlet {
	private static Log log = LogFactory.getLog(Authenticate.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 6002125607784963740L;
	/**
	 * Constructor of the object.
	 */
	public Authenticate() {
		super();
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
		response.setHeader("Expires","-1");
		response.setHeader("Pragma","no-cache");
		response.setHeader("Cache-control","no-cache");

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String username = request.getParameter("username");
		request.getSession().setAttribute("user", username);
		String password = request.getParameter("password");
		String server = request.getParameter("server");
		
		XMPPConnection connection = null;

		try {
			
			System.out.println("SERVER ========> " + server);
			if(server == "" || server.equals("openfire") || server.equals("Jabber")) {
				try {
					connection = new XMPPConnection("", 5222);
				} catch (Exception e1) {
					System.out.println("Cannot initialize connection with the jabber server");
					throw e1;
				}
			}
			//}

			try {
				connection.login(username, password);
				System.out.println("HE PODIDO CONECTARME");
				log.debug("connection established for user: " + username);
			} catch (XMPPException e) {
				 if (connection != null) 
				 {
					 connection.close();
				 }
				 // try to create an account for a new user
			      XMPPConnection newConn = null;
			      try 
			      {
			        newConn = new XMPPConnection("", 5222);
			        newConn.getAccountManager().createAccount(username, password);
			        System.out.println("Crea usuario");
			      }
			      catch (XMPPException innerE) 
			      {
			    	  System.out.println("Cannot create new account on the jabber server.");
			      }
			      finally 
			      {
			          newConn.close();
			        }
     			        // try again login - there should be a new account
			        try {
			        	connection = new XMPPConnection("", 5222);
			        	connection.login(username, password);

			        }
			        catch (XMPPException innerE) 
			        {
			          System.out.println("Cannot login to the jabber server.");
			        }

			      }

				//throw e;				
			request.getSession().setAttribute("conn", connection);
			
			// start the user's chat listener thread
			String user = connection.getUser();
			if (request.getSession().getAttribute("listener") == null) {
				ChatListener list = new ChatListener(user, connection);
				TrafficController.addListener(user, list);
				list.start();
				log.debug("listener created and added to the traffic controller for user: " + username);
			}

			if (request.getSession().getAttribute("sender") == null) {
				// start user's chat sender thread
				ChatSender sender = new ChatSender(username, connection);
				TrafficController.addSender(user, sender);
				sender.start();
				log.debug("sender created and added to the traffic controller for user: " + username);
			}
			
			out.print("ok");
		} catch (Exception e) {
			out.print("fail");
		}
		System.out.println("CONNECTION =======> " + connection);
		
		log.debug("authentication complete finishing servlet for user: " + username);
//		out.flush();
//		out.close();
	}
}
