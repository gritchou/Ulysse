package org.claros.chat.A4;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;

public class DataSourceFactory {
	
	/* Connection pool */
	private DataSource dataSource;
	
	ArrayList<String> conversations = null;
	
	public ArrayList<String> getAllConversations() {
		
		initializeDataSource();
		
		conversations = getConversations();
		
		return conversations;		
	}
	
	/*public ArrayList<String> getAllMucConversations() {
		
		initializeDataSource();
		
		conversations = getMucConversations();
		
		return conversations;		
	}*/

	public ArrayList<String> getAllNewConversations(Calendar date) {
	
		initializeDataSource();
	
		conversations = getNewConversations(date);
	
		return conversations;		
	}

	/*public ArrayList<String> getAllNewMucConversations(Calendar date) {
	
		initializeDataSource();
	
		conversations = getNewMucConversations(date);
	
		return conversations;		
	}*/
	
	private void initializeDataSource() {
		
		Properties properties = new Properties();
		
		try {
			
			properties.load(new FileInputStream("WebContent/WEB-INF/config/datasource_config.properties"));
			
			dataSource = BasicDataSourceFactory.createDataSource(properties);
			
		} catch(FileNotFoundException e) {
			
			e.printStackTrace();
			
		} catch(IOException e) {
			
			e.printStackTrace();
			
		} catch(Exception e) {
			
			e.printStackTrace();
		}
	}
	
	private void endConnection(Connection con) {
		
		try {
			
			if(null != con) {
			
				con.close();
			}
		
		} catch(SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	private ArrayList<String> getConversations() {
		
		ArrayList<String> sc = null;
		String data = null;
		
		Connection con = null;
		
		try {
			
			sc = new ArrayList<String>();
			
			con = dataSource.getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("select * from queue where type like 'S'");
			
			while(rs.next()) {
				
				data = rs.getObject("TYPE") + " " + rs.getObject("MSG_FROM") + " " + rs.getObject("MSG_TO") 
					   + " " + rs.getObject("MSG_BODY") + " " + rs.getObject("MSG_TIME");
				
				sc.add(data);
			}
		
		} catch(SQLException e) {
			
			e.printStackTrace();
		
		} finally {
			
			endConnection(con);
		}
		
		return sc;		
	}
	
	/*private ArrayList<String> getMucConversations() {
		
		ArrayList<String> sc = null;
		String data = null;
		
		Connection con = null;
		
		try {
			
			con = dataSource.getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("select * from queue where type like 'M'");
			
			while(rs.next()) {
				
				data = rs.getObject("TYPE") + " " + rs.getObject("MSG_FROM") + " " + rs.getObject("MSG_TO") 
					   + " " + rs.getObject("MSG_BODY") + " " + rs.getObject("MSG_TIME");
				
				sc.add(data);
			}
		
		} catch(SQLException e) {
			
			e.printStackTrace();
		
		} finally {
			
			endConnection(con);
		}
		
		return sc;		
	}*/

	@SuppressWarnings("static-access")
	private ArrayList<String> getNewConversations(Calendar date) {
	
		ArrayList<String> sc = null;
		String data = null;
		String initDate = null;
	
		Connection con = null;
	
		try {
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			initDate = dateFormat.format(date.getTime());
			
			//initDate = date.get(Calendar.YEAR) + "/" + date.get(Calendar.MONTH) + "/" + date.get(Calendar.DATE);
			
			sc = new ArrayList<String>();
			
			con = dataSource.getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("select * from queue where type like 'S' and date(msg_time) between '" + initDate + "' and" +
					       " date(SYSDATE())");
		
			while(rs.next()) {
				
				data = rs.getObject("TYPE") + " " + rs.getObject("MSG_FROM") + " " + rs.getObject("MSG_TO") 
					   + " " + rs.getObject("MSG_BODY") + " " + rs.getObject("MSG_TIME");
				
				sc.add(data);
				
			}
			
		} catch(SQLException e) {
		
			e.printStackTrace();
	
		} finally {
		
			endConnection(con);
		}
	
		return sc;		
	}

	/*private ArrayList<String> getNewMucConversations(Calendar date) {
	
		ArrayList<String> sc = null;
		String data = null;
	
		Connection con = null;
	
		try {
			
			con = dataSource.getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("select * from queue where type like 'M' and msg_time >= " + date);
		
			while(rs.next()) {
				
				data = rs.getObject("TYPE") + " " + rs.getObject("MSG_FROM") + " " + rs.getObject("MSG_TO") 
					   + " " + rs.getObject("MSG_BODY") + " " + rs.getObject("MSG_TIME");
				
				sc.add(data);				
			}
			
		} catch(SQLException e) {
		
			e.printStackTrace();
	
		} finally {
		
			endConnection(con);
		}
	
		return sc;			
	}*/
}