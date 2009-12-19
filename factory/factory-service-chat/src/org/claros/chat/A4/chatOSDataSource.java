package org.claros.chat.A4;
import java.util.ArrayList;
import java.util.Calendar;

public class chatOSDataSource implements chatOSDataInterface {
	
	DataSourceFactory dsf = null;
	ArrayList<String> conversations = null;
	
	public ArrayList<String> getAllConversations() {
		
		dsf = new DataSourceFactory();
		
		try {
			
			conversations = new ArrayList<String>();
		
			conversations = dsf.getAllConversations();
		
		} catch(Exception e) {
			
			e.printStackTrace();
		}
		
		return conversations;		
	}
	
	/*public ArrayList<String> getAllMucConversations() {
		
		dsf = new DataSourceFactory();
		
		conversations = dsf.getAllMucConversations();
		
		return conversations;	
	}*/
	
	public ArrayList<String> getAllNewConversations(Calendar date) {
		
		dsf = new DataSourceFactory();
		
		try {
			
			conversations = new ArrayList<String>();
		
			conversations = dsf.getAllNewConversations(date);
		
		} catch(Exception e) {
			
			e.printStackTrace();
		}
		
		return conversations;	
	}
	
	/*public ArrayList<String> getAllNewMucConversations(Calendar date) {
		
		dsf = new DataSourceFactory();
		
		conversations = dsf.getAllNewMucConversations(date);
		
		return conversations;	
	}*/
}