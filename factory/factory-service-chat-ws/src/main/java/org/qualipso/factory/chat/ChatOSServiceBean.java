package org.qualipso.factory.chat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.qualipso.factory.utils.ListItem;
import org.qualipso.factory.utils.ListItemArray;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.browser.BrowserService;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;

@Stateless(name = "ChatOSServiceBean", mappedName = FactoryNamingConvention.SERVICE_PREFIX
		+ ChatOSService.SERVICE_NAME)
	@WebService(endpointInterface = "org.qualipso.factory.chat.ChatOSService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE
		+ ChatOSService.SERVICE_NAME, serviceName = ChatOSService.SERVICE_NAME, portName = ChatOSService.SERVICE_NAME
		+ "Port")
	@WebContext(contextRoot = "/factory-service-chat", urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX
		+ ChatOSService.SERVICE_NAME)
	@SOAPBinding(style = Style.RPC)
	@SecurityDomain(value = "JBossWSDigest")
	@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class ChatOSServiceBean implements ChatOSService {
	
	/** The binding. */
    private BindingService binding;
    
    /** The pep. */
    private PEPService pep;
    
    /** The pap. */
    private PAPService pap;
    
    /** The notification. */
    private NotificationService notification;
    
    /** The membership. */
    private MembershipService membership;
    //
    /** The browser. */
    private BrowserService browser;
    
    /** The core. */
    private CoreService core;
    //
    /** The ctx. */
    private SessionContext ctx;
    
    /* Connection pool */
	private DataSource dataSource;
	
	ListItemArray conversations = null;
	
	public ListItemArray getAllConversations() {
		
		initializeDataSource();
		
		conversations = getConversations();
		
		return conversations;		
	}
	
	public ListItemArray getAllNewConversations(String date) {
	
		initializeDataSource();
	
		conversations = getNewConversations(date);
	
		return conversations;		
	}
	
	private void initializeDataSource() throws ChatOSServiceException {
		
		Properties properties = new Properties();
		
		try {
			
			properties.load(new FileInputStream("conf/datasource_config.properties"));
			
			dataSource = BasicDataSourceFactory.createDataSource(properties);
			
		} catch(FileNotFoundException e) {
			
			throw new ChatOSServiceException("Unable to find the conf file", e);
			
		} catch(IOException e) {
			
			throw new ChatOSServiceException("Unable to read the conf file", e);
			
		} catch(Exception e) {
			
			throw new ChatOSServiceException("An error ocurred while reading the conf file", e);
		}
	}
	
	private void endConnection(Connection con) throws ChatOSServiceException {
		
		try {
			
			if(null != con) {
			
				con.close();
			}
		
		} catch(SQLException e) {
			
			throw new ChatOSException("Unable to connect to db", e);
		}
	}
	
	public String isDBOpen() throws ChatOSServiceException {
		
		boolean status = true;
		
		try {	
			
			Connection con = DataSource.getConnection;
			
		} catch (Exception e) {
			
			status = false;
		}
		
		return String.valueOf(status);
	}
	
	private ListItemArray getConversations() throws ChatOSServiceException {
		
		ListItemArray sc = null;
		ListItem item = null;
		String data = null;
		
		Connection con = null;
		
		try {
			
			con = dataSource.getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("select * from queue where type like 'S'");
			
			while(rs.next()) {
				
				data = rs.getObject("TYPE") + " " + rs.getObject("MSG_FROM") + " " + rs.getObject("MSG_TO") 
					   + " " + rs.getObject("MSG_BODY") + " " + rs.getObject("MSG_TIME");
				
				item.setData(data);
				
				sc.getItem().add(item);
			}
		
		} catch(SQLException e) {
			
			throw new ChatOSException("An error ocurred while obteining data from the db", e);
		
		} finally {
			
			endConnection(con);
		}
		
		return sc;		
	}

	@SuppressWarnings("static-access")
	private ListItemArray getNewConversations(String date) throws ChatOSServiceException {
		
		//Date Format: yyyy/MM/dd
	
		ListItemArray sc = null;
		ListItem item = null;
		String data = null;
		String initDate = null;
	
		Connection con = null;
	
		try {
			
			sc.getItem();
			
			con = dataSource.getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("select * from queue where type like 'S' and date(msg_time) between '" + initDate + "' and" +
					       " date(SYSDATE())");
		
			while(rs.next()) {
				
				data = rs.getObject("TYPE") + " " + rs.getObject("MSG_FROM") + " " + rs.getObject("MSG_TO") 
					   + " " + rs.getObject("MSG_BODY") + " " + rs.getObject("MSG_TIME");
				
				item.setData(data);
							
				sc.getItem().add(item);
				
			}
			
		} catch(SQLException e) {
		
			throw new ChatOSServiceException("An error ocurred while obteining data from the db", e);
	
		} finally {
		
			endConnection(con);
		}
	
		return sc;		
	}
}