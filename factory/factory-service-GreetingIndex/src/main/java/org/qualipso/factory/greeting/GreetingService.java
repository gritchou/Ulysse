package org.qualipso.factory.greeting;

import javax.ejb.Remote;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.greeting.entity.Name;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 june 2009
 */
@Remote
public interface GreetingService extends FactoryService {
	
	
	public void createName(String path, String name) throws GreetingServiceException;
	
	
	public Name readName(String path) throws GreetingServiceException;
	
	
	public void updateName(String path, String name) throws GreetingServiceException;
	
	
	public void deleteName(String path) throws GreetingServiceException;
	
	public String sayHello(String path) throws GreetingServiceException;
	
	
	/*@WebMethod
    @WebResult(name = "message")
    public Event createEvent(String string, String caller,String name, String arg1, String arg) throws GreetingServiceException;*/
	

    public String throwEventOK() throws GreetingServiceException;
	
	
    public String throwEventKO() throws GreetingServiceException;
	
	/*@WebMethod
    @WebResult(name = "message")
    public createQueue(String path) throws GreetingServiceException;
	
	
	@WebMethod
    @WebResult(name = "message")
	public List getEvents(String path) throws GreetingServiceException;*/
	
	
	
	
}
