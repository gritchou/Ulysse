package org.qualipso.factory.xmldb.exist;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestEXistDB {
	
	private static final Log logger = LogFactory.getLog(TestEXistDB.class);
	
	private static File dataDirectory = null;
	private static EXistDB db;
	
	public TestEXistDB() {
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
	}
	
	@AfterClass
	public static void after() throws Exception {
		db.stopService();
		delete(dataDirectory);
	}
	
	@Test
	public void testStatus() {
		String status= db.getStatus();
		logger.debug("Status : " + status);
		assertTrue(status.indexOf("running") > 0);
		
		String dataDir = db.getDataDirectory();
		logger.debug("DataDirectory : " + dataDir);
		assertTrue(dataDir.indexOf("eXist") > 0);
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
