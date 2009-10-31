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
import org.qualipso.factory.collaboration.client.ws.CollaborationFolder;
import org.qualipso.factory.collaboration.client.ws.Document;
import org.qualipso.factory.collaboration.client.ws.DocumentService;
import org.qualipso.factory.collaboration.client.ws.DocumentService_Service;
import org.qualipso.factory.collaboration.utils.TestUtils;

public class DocumentServiceWSTest
{
    private static Log logger = LogFactory.getLog(DocumentServiceWSTest.class);

    private DocumentService dsPort;

    public DocumentServiceWSTest()
    {
	DocumentService_Service ds = new DocumentService_Service();
	dsPort = ds.getDocumentServicePort();
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
	    //
	    Map<String, Object> reqContext = ((BindingProvider) dsPort).getRequestContext();
	    reqContext.put(StubExt.PROPERTY_AUTH_TYPE, StubExt.PROPERTY_AUTH_TYPE_WSSE);
	    reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
	    reqContext.put(BindingProvider.PASSWORD_PROPERTY, "root");
	    //
	    String rootFolder = "R" + System.currentTimeMillis();
	    String randomFolder = "F" + System.currentTimeMillis();
	    String randomDocument = "D" + System.currentTimeMillis();
	    //
	    String rootPath = "/" + TestUtils.normalizeForPath(rootFolder);
	    String folderPath = rootPath + "/" + TestUtils.normalizeForPath(randomFolder);
	    String docPath = folderPath + "/" + TestUtils.normalizeForPath(randomDocument);
	    //
	    String tempText = "Lorem ipsum";
	    String fileName = "D_" + System.currentTimeMillis() + ".txt";
	    //
	    logger.debug("**************************************************************");
	    logger.debug("Create Folder:" + rootPath);
	    String folderId = dsPort.createFolder(rootPath, rootFolder, tempText);
	    assertNotNull(folderId);
	    logger.debug("New folder " + folderId);
	    logger.debug("    OK");
	    logger.debug("Create Folder:" + folderPath);
	    folderId = dsPort.createFolder(folderPath, randomFolder, tempText);
	    assertNotNull(folderId);
	    logger.debug("New folder " + folderId);
	    logger.debug("    OK");
	    logger.debug("Read Folder:" + folderPath);
	    CollaborationFolder fold = dsPort.readFolder(folderPath);
	    assertNotNull(fold);
	    logger.debug("    OK");
	    logger.debug("Update Folder:" + folderPath);
	    dsPort.updateFolder(folderPath, randomFolder, "New text" + System.currentTimeMillis());
	    logger.debug("    OK");
	    logger.debug("Read Folder:" + folderPath);
	    fold = dsPort.readFolder(folderPath);
	    assertNotNull(fold);
	    logger.debug("    OK");
	    logger.debug("Create Document:" + docPath);
	    dsPort.createDocument(docPath, randomDocument, "2009-09-01", "Readme", "qualipso", "1.0", "DRAFT",
		    fileName, "text/plain", "TG9yZW0gaXBzdW0=".getBytes());
	    logger.debug("    OK");
	    logger.debug("Read Document:" + docPath);
	    Document doc = dsPort.readDocument(docPath);
	    assertNotNull(doc);
	    logger.debug("New Document " + doc.getId());
	    logger.debug("    OK");
	    logger.debug("Update Document:" + docPath);
	    dsPort.updateDocument(docPath, randomDocument, "Readme", "qualipso,factory,document", "DRAFT", fileName,
		    "text/plain", "TG9yZW0gaXBzdW0=".getBytes());
	    logger.debug("    OK");
	    logger.debug("Read Document:" + docPath);
	    doc = dsPort.readDocument(docPath);
	    assertNotNull(doc);
	    logger.debug("    OK");
	    logger.debug("Delete Document:" + docPath);
	    dsPort.deleteDocument(docPath);
	    logger.debug("    OK");
	    logger.debug("Delete Folder:" + folderPath);
	    dsPort.deleteFolder(folderPath);
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
