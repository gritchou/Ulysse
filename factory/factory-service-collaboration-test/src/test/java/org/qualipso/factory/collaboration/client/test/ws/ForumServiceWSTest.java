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
import org.qualipso.factory.collaboration.client.ws.BootstrapService;
import org.qualipso.factory.collaboration.client.ws.BootstrapServiceException_Exception;
import org.qualipso.factory.collaboration.client.ws.BootstrapService_Service;
import org.qualipso.factory.collaboration.client.ws.DocumentService;
import org.qualipso.factory.collaboration.client.ws.DocumentService_Service;
import org.qualipso.factory.collaboration.client.ws.Forum;
import org.qualipso.factory.collaboration.client.ws.ForumService;
import org.qualipso.factory.collaboration.client.ws.ForumService_Service;
import org.qualipso.factory.collaboration.client.ws.ThreadMessage;
import org.qualipso.factory.collaboration.utils.TestUtils;

public class ForumServiceWSTest
{
    private static Log logger = LogFactory.getLog(ForumServiceWSTest.class);

    private DocumentService dsPort;
    private ForumService fsPort;
    
    public ForumServiceWSTest()
    {
	DocumentService_Service ds = new DocumentService_Service();
	dsPort = ds.getDocumentServicePort();
	ForumService_Service cs = new ForumService_Service();
	fsPort = cs.getForumServicePort();
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
	    ((StubExt) dsPort).setConfigName("Standard WSSecurity Client");
	    ((StubExt) fsPort).setConfigName("Standard WSSecurity Client");
	    //
	    Map<String, Object> reqContext = ((BindingProvider) dsPort).getRequestContext();
	    reqContext.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	    reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
	    reqContext.put(BindingProvider.PASSWORD_PROPERTY, "root");
	    //
	    Map<String, Object> reqContext2 = ((BindingProvider) fsPort).getRequestContext();
	    reqContext2.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	    reqContext2.put(BindingProvider.USERNAME_PROPERTY, "root");
	    reqContext2.put(BindingProvider.PASSWORD_PROPERTY, "root");
	    //
	    String forumName = "F" + System.currentTimeMillis();
	    String threadMessgage = "MSG" + System.currentTimeMillis();
	    String threadMessgageReply = "REMSG" + System.currentTimeMillis();
	    String rootFolder = "FOR" + System.currentTimeMillis();
	    String tempText = "Folder for Forums";
	    String randomText = "Lorem Ipsum.Lorem Ipsum.Lorem Ipsum.Lorem Ipsum.Lorem Ipsum.Lorem Ipsum.";
	    //
	    String rootPath = "/" + TestUtils.normalizeForPath(rootFolder);
	    String forumPath = rootPath + "/" + TestUtils.normalizeForPath(forumName);
	    String threadPath = forumPath + "/" + TestUtils.normalizeForPath(threadMessgage);
	    String threadReplyPath = threadPath + "/" + TestUtils.normalizeForPath(threadMessgageReply);
	    //
	    logger.debug("**************************************************************");
	    logger.debug("Create Forums Folder:" + rootPath);
	    dsPort.createFolder(rootPath, rootFolder, tempText);
	    logger.debug("    OK");
	    logger.debug("Create Forum:" + forumPath);
	    fsPort.createForum(rootPath, forumName);
	    logger.debug("    OK");
	    logger.debug("Read Forum:" + forumPath);
	    Forum forum = fsPort.readForum(forumPath);
	    assertNotNull(forum);
	    logger.debug("New forum " + forum.getId());
	    logger.debug("    OK");
	    logger.debug("Update Forum:" + forumPath);
	    fsPort.updateForum(forumPath, forumName, "2009-09-30");
	    logger.debug("    OK");
	    logger.debug("Read Forum:" + forumPath);
	    forum = fsPort.readForum(forumPath);
	    assertNotNull(forum);
	    logger.debug("    OK");
	    logger.debug("**************************************************************");
	    logger.debug("                       THREADS");
	    logger.debug("Create Thread:" + threadPath);
	    fsPort.createThreadMessage(threadPath, threadMessgage, forum.getId(), randomText, "2009-09-01", "false");
	    logger.debug("    OK");
	    logger.debug("Read Thread:" + threadPath);
	    ThreadMessage tm = fsPort.readThreadMessage(threadPath);
	    logger.debug("New thread " + tm.getId() + " Author " + tm.getAuthor() + " Date " + tm.getDatePosted());
	    assertNotNull(tm);
	    logger.debug("    OK");
	    logger.debug("Reply Thread:" + threadReplyPath);
	    fsPort.createThreadMessage(threadReplyPath, threadMessgageReply, forum.getId(), "RE:" + randomText,
		    "2009-09-01", "true");
	    logger.debug("    OK");
	    logger.debug("Read Thread:" + threadReplyPath);
	    tm = fsPort.readThreadMessage(threadReplyPath);
	    logger.debug("Reply thread " + tm.getId() + " Author " + tm.getAuthor() + " Date " + tm.getDatePosted());
	    logger.debug("    OK");
	    logger.debug("Delete Thread:" + threadReplyPath);
	    fsPort.deleteThreadMessage(threadReplyPath);
	    logger.debug("    OK");
	    logger.debug("Delete Thread:" + threadPath);
	    fsPort.deleteThreadMessage(threadPath);
	    logger.debug("    OK");
	    logger.debug("**************************************************************");
	    logger.debug("Delete Forum:" + forumPath);
	    fsPort.deleteForum(forumPath);
	    logger.debug("    OK");
	    logger.debug("Delete Folder:" + rootPath);
	    dsPort.deleteFolder(rootPath);
	    logger.debug("    OK");
	    logger.debug("**************************************************************");
	} catch (Exception e)
	{
	    e.printStackTrace();
	    fail(e.getMessage());
	}
    }
}
