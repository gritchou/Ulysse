package org.qualipso.factory.xmldb;

import java.util.Arrays;
import java.util.HashMap;

import org.exist.xmldb.DatabaseInstanceManager;
import org.jboss.system.ServiceMBeanSupport;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

/**
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com) 
 * @date 11 november 2009
 */
public class XMLDB extends ServiceMBeanSupport implements XMLDBMBean {

	private String baseCollectionURI;
	private String driver;
	private HashMap<String, String> systemProperties;
	private Collection baseCollection;

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getBaseCollectionURI() {
		return baseCollectionURI;
	}

	public void setBaseCollectionURI(String baseCollectionURI) {
		this.baseCollectionURI = baseCollectionURI;
	}

	public Collection getBaseCollection() throws XMLDBException {
		baseCollection = DatabaseManager.getCollection(baseCollectionURI);
		return baseCollection;
	}
	
	public void setSystemProperties(HashMap<String, String> properties) {
		this.systemProperties = properties;
	}
	
	public HashMap<String, String> getSystemProperties() {
		return systemProperties;
	}

	@SuppressWarnings("unchecked")
	public void startService() throws Exception {
		Class c = Class.forName(driver);
		Database database = (Database) c.newInstance();
		DatabaseManager.registerDatabase(database);
		database.setProperty("create-database", "true");

		baseCollection = getBaseCollection();
		baseCollection.setProperty("encoding", "ISO-8859-1");

		log.debug("Got Base Collection");

		String[] collections = baseCollection.listChildCollections();
		log.debug("ChildCollections " + Arrays.asList(collections));
	}

	public void stopService() throws Exception {
		if (getState() == STARTED) {
			if (baseCollection != null) {
				baseCollection.close();
				log.debug("Closed base (db) collection");
				
				DatabaseInstanceManager manager = (DatabaseInstanceManager) baseCollection.getService("DatabaseInstanceManager", "1.0");
				manager.shutdown();
				log.debug("Manager shutdown");
			}
		}
	}

}
