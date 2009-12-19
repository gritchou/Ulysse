package org.qualipso.factory.xmldb;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.xmldb.exist.EXistDB;
import org.qualipso.factory.xmldb.exist.TestEXistDB;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

public class TestXMLDB {
	
private static final Log logger = LogFactory.getLog(TestEXistDB.class);
	
	private static File dataDirectory = null;
	private static EXistDB db;
	private static XMLDB xmldb;
	
	public TestXMLDB() {
		if ( dataDirectory == null ) {
			dataDirectory = new File("data");
			if ( !dataDirectory.exists() ) {
				dataDirectory.mkdirs();
			}
			logger.debug("Base test data directory : " + dataDirectory);
		}
	}
	
	@BeforeClass
	public static void before() throws Exception {
		db = new EXistDB();
		db.setEXistHome("data/eXist");
		db.startService();
		
		xmldb = new XMLDB();
		xmldb.setDriver("org.exist.xmldb.DatabaseImpl");
		xmldb.setBaseCollectionURI("xmldb:exist:///db");
		xmldb.startService();
	}
	
	@AfterClass
	public static void after() throws Exception {
		xmldb.stopService();
		db.stopService();
		delete(dataDirectory);
	}
	
	@Test
	public void testGetCollection() throws XMLDBException {
		Collection collection = xmldb.getBaseCollection();
		logger.debug("base collection name : " + collection.getName());
	}
	
	private static void delete(File file) {
		if ( file.isDirectory() ) {
			for ( File child : file.listFiles() ) {
				delete(child);
			}
		} 
		file.delete();
	}

}
