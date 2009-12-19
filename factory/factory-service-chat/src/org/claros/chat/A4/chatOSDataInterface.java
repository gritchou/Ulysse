package org.claros.chat.A4;
import java.util.ArrayList;
import java.util.Calendar;

public interface chatOSDataInterface {
	
	/**
	 * Gets all the existent conversations (both one to one and group conversations).
	 * 
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getAllConversations();
	
	/**
	 * Gets all the existent conversations (both one to one and group conversations) from an
	 * specific date.
	 * 
	 * @param date
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getAllNewConversations(Calendar date);

}
