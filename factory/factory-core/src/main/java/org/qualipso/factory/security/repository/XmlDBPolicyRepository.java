package org.qualipso.factory.security.repository;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import com.sun.xacml.EvaluationCtx;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 31 august 2009
 */
public class XmlDBPolicyRepository extends PolicyRepository {

	private static final String XMLDB_DRIVER = "org.exist.xmldb.DatabaseImpl";
	private static final String XMLDB_COLLECTION = "xmldb:exist://localhost:8080/exist/xmlrpc/qualipso"; 
	private static Log logger = LogFactory.getLog(XmlDBPolicyRepository.class);
	private Collection collection;

	public XmlDBPolicyRepository() {
	}

	public void init() throws PolicyRepositoryException {
		try {
			Class<?> cl = Class.forName(XMLDB_DRIVER);
			Database database = (Database) cl.newInstance();
			DatabaseManager.registerDatabase(database);

			collection = DatabaseManager.getCollection(XMLDB_COLLECTION);
			
			logger.info("XmlDBPolicyRepository initialized");
		} catch (Exception e) {
			logger.error("Error during initialization", e);
			throw new PolicyRepositoryException("Error during initialization", e);
		}
	}
	
	public void shutdown() throws PolicyRepositoryException {
		try {
			collection.close();
		} catch ( Exception e ) {
			logger.error("Error during shutdown", e);
			throw new PolicyRepositoryException("Error during shutdown", e);
		}
	}
	

	@Override
	public void addPolicy(String id, byte[] policy) throws PolicyRepositoryException {
		try {
			XMLResource document = (XMLResource) collection.createResource(id, "XMLResource");
			document.setContent(policy);
			collection.storeResource(document);
			logger.debug("policy added");
		} catch (XMLDBException e) {
			logger.error("unable to add policy", e);
			throw new PolicyRepositoryException("unable to add policy ", e);
		}
	}

	@Override
	public byte[] getPolicy(String id) throws PolicyRepositoryException {
		try {
			XMLResource document = (XMLResource) collection.getResource(id);
			logger.debug("policy retreived");
			return (byte[]) document.getContent();
		} catch (XMLDBException e) {
			logger.error("unable to retreive policy", e);
			throw new PolicyRepositoryException("unable to retreive policy ", e);
		}
	}

	@Override
	public void updatePolicy(String id, byte[] policy) throws PolicyRepositoryException {
		try {
			XMLResource document = (XMLResource) collection.createResource(id, "XMLResource");
			document.setContent(policy);
			collection.storeResource(document);
			logger.debug("policy updated");
		} catch (XMLDBException e) {
			logger.error("unable to update policy", e);
			throw new PolicyRepositoryException("unable to update policy ", e);
		}
	}

	@Override
	public void deletePolicy(String id) throws PolicyRepositoryException {
		try {
			XMLResource document = (XMLResource) collection.getResource(id);
			collection.removeResource(document);
		} catch (XMLDBException e) {
			logger.error("unable to retreive policy", e);
			throw new PolicyRepositoryException("unable to retreive policy ", e);
		}
	}

	@Override
	public List<String> listPolicies() throws PolicyRepositoryException {
		try {
			String[] policies = collection.listResources();
			Vector<String> vpolicies = new Vector<String> ();
			for ( int i=0; i<policies.length; i++ ) {
				vpolicies.add(policies[i]);
			}
			return vpolicies;
		} catch (XMLDBException e) {
			logger.error("unable to list policies", e);
			throw new PolicyRepositoryException("unable to list policies ", e);
		}
	}

	@Override
	public Map<String, byte[]> getPolicies(EvaluationCtx eval) throws PolicyRepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

}
