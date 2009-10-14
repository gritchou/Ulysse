package org.qualipso.factory.collaboration.client.test.ws;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ws.core.StubExt;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.collaboration.client.ws.BootstrapService;
import org.qualipso.factory.collaboration.client.ws.BootstrapServiceException_Exception;
import org.qualipso.factory.collaboration.client.ws.BootstrapService_Service;
import org.qualipso.factory.collaboration.client.ws.CalendarItem;
import org.qualipso.factory.collaboration.client.ws.CalendarService;
import org.qualipso.factory.collaboration.client.ws.CalendarService_Service;
import org.qualipso.factory.collaboration.client.ws.CoreService;
import org.qualipso.factory.collaboration.client.ws.CoreService_Service;
import org.qualipso.factory.collaboration.client.ws.StringArray;

public class CalendarServiceWSTest
{
    private static Log logger = LogFactory.getLog(CalendarServiceWSTest.class);

    private CalendarService csPort;
    private CoreService corePort;

    public CalendarServiceWSTest()
    {
	CalendarService_Service cs = new CalendarService_Service();
	csPort = cs.getCalendarServicePort();
	CoreService_Service core = new CoreService_Service();
	corePort = core.getCoreServicePort();
    }

    @BeforeClass
    public static void init()
    {
	try
	{
	    BootstrapService port = new BootstrapService_Service().getBootstrapServicePort();
	    ((StubExt) port).setConfigName("Standard WSSecurity Client");
	    port.bootstrap();
	} catch (BootstrapServiceException_Exception e)
	{
	    logger.error("unable to bootstrap factory", e);
	}
    }

    @Test
    public void testCRUD()
    {

	try
	{
	    ((StubExt) csPort).setConfigName("Standard WSSecurity Client");
	    ((StubExt) corePort).setConfigName("Standard WSSecurity Client");
	    //
	    Map<String, Object> reqContext = ((BindingProvider) csPort).getRequestContext();
	    reqContext.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	    reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
	    reqContext.put(BindingProvider.PASSWORD_PROPERTY, "root");
	    //
	    Map<String, Object> reqContextCore = ((BindingProvider) corePort).getRequestContext();
	    reqContextCore.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	    reqContextCore.put(BindingProvider.USERNAME_PROPERTY, "root");
	    reqContextCore.put(BindingProvider.PASSWORD_PROPERTY, "root");
	    //
	    String rootName = "c"+System.currentTimeMillis();
	    String eventName = "e" + System.currentTimeMillis();
	    String rootPath = "/"+rootName;
	    //
	    logger.debug("**************************************************************");
	    //The service creates the "+rootPath+" if it does not exist, so we should not create anything now.
	    //But in any case.. we do it.. :)
	    corePort.createFolder(rootPath, rootName, "folder for storing events");
	    StringArray paths = csPort.createEvent(rootPath, eventName, "Athens", "2010-10-01", "10:00:00", "18:00:00", "strusos",
		    "gstro@delos.eurodyn.com", "2108094500","once",1);
	    String oldPath = paths.getItem().get(0);
	    logger.debug("    OK");
	    logger.debug("Read Event:" + paths.getItem().get(0));
	    CalendarItem ev = csPort.readEvent(paths.getItem().get(0));
	    assertNotNull(ev);
	    logger.debug("New event " + ev.getId());
	    logger.debug("    OK");
	    logger.debug("Update Event:" + oldPath);
	    logger.debug("We change date so the path is changed.");
	    String newPath = csPort.updateEvent(oldPath, eventName, "Athens", "2009-10-01", "10:30:00", "18:30:00", "strusos",
		    "gstro@delos.eurodyn.com", "2108094500");
	    logger.debug("    OK");
	    logger.debug("Read Event:" +newPath);
	    ev = csPort.readEvent(newPath);
	    assertNotNull(ev);
	    logger.debug("    OK");
	    logger.debug("Delete Event:" + newPath);
	    csPort.deleteEvent(newPath);
	    logger.debug("    OK");
	    logger.debug("Deleting folders");
	    // Created paths are /c1254146744156/2010/9/3/1445
	    String parent = PathHelper.getParentPath(oldPath);
	    if(parent!=null &&!parent.equals("/")){
		//delete day
		corePort.deleteFolder(parent);
		parent = PathHelper.getParentPath(parent);
		if(parent!=null &&!parent.equals("/")){
		    //delete month
		    corePort.deleteFolder(parent);
		    parent = PathHelper.getParentPath(parent);
			if(parent!=null &&!parent.equals("/")){
			    //delete year
			    corePort.deleteFolder(parent);
			}
		}
	    }
	    parent = PathHelper.getParentPath(newPath);
	    if(parent!=null &&!parent.equals("/")){
		//delete day
		corePort.deleteFolder(parent);
		parent = PathHelper.getParentPath(parent);
		if(parent!=null &&!parent.equals("/")){
		    //delete month
		    corePort.deleteFolder(parent);
		    parent = PathHelper.getParentPath(parent);
			if(parent!=null &&!parent.equals("/")){
			    //delete year
			    corePort.deleteFolder(parent);
			}
		}
	    }
	    corePort.deleteFolder(rootPath);
	    logger.debug("    OK");
	    logger.debug("**************************************************************");
	} catch (Exception e)
	{
	    e.printStackTrace();
	    fail(e.getMessage());
	}
    }
}
