package org.qualipso.factory.chat;

import java.util.Calendar;

import org.qualipso.factory.utils.ListItemArray;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;

@Remote
@WebService(name = ChatOSService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE
	+ ChatOSService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface chatOSService extends FactoryService {
	
	public static final String SERVICE_NAME = "chat-management";
	
	public String isDBOpen() throws ChatOSServiceException;
	
	/**
	 * Gets all the existent conversations (both one to one and group conversations).
	 * 
	 * @return ListItemArray
	 */
	@WebMethod
	@WebResult(name = "item")
	public ListItemArray getAllConversations() throws ChatOSServiceException;
	
	/**
	 * Gets all the existent conversations (both one to one and group conversations) from an
	 * specific date.
	 * 
	 * @param date
	 * @return ListItemArray
	 */
	@WebMethod
	@WebResult(name = "item")
	public ListItemArray getAllNewConversations(@WebParam(name = "date") String date);

}
