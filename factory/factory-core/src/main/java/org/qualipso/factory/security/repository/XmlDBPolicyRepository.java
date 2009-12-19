/*
 *
 * Qualipso Factory
 * Copyright (C) 2006-2010 INRIA
 * http://www.inria.fr - molli@loria.fr
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of LGPL. See licenses details in LGPL.txt
 *
 * Initial authors :
 *
 * Jérôme Blanchard / INRIA
 * Pascal Molli / Nancy Université
 * Gérald Oster / Nancy Université
 * Christophe Bouthier / INRIA
 * 
 */
package org.qualipso.factory.security.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import com.sun.xacml.EvaluationCtx;

/**
 * Implementation of an XMLDB PolicyRepository. (not complete)<br/>
 * <br/>
 * This implementation will provide : <br/>
 * <ul>
 * <li>Transaction support</li>
 * <li>Optimization support</li>
 * <li>Cache support</li>
 * </ul>
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 31 august 2009
 */
public class XmlDBPolicyRepository extends PolicyRepository {
	private static final String XMLDB_DRIVER = "org.exist.xmldb.DatabaseImpl";
	private static final String XMLDB_BASE_COLLECTION = "xmldb:exist:///db";
	private static final String COLLECTION_NAME = "qualipso";
	private static Log logger = LogFactory.getLog(XmlDBPolicyRepository.class);
	private Collection baseCollection;
	private Collection collection;

	public XmlDBPolicyRepository() {
	}

	public void init() throws PolicyRepositoryException {
		try {
			Class c = Class.forName(XMLDB_DRIVER);
			Database database = (Database) c.newInstance();
			DatabaseManager.registerDatabase(database);
			database.setProperty("create-database", "true");

			baseCollection = DatabaseManager.getCollection(XMLDB_BASE_COLLECTION);
			baseCollection.setProperty("encoding", "ISO-8859-1");

			logger.debug("Got Base Collection");

			String[] collections = baseCollection.listChildCollections();
			boolean collectionExists = false;

			for (String collection : collections) {
				if (collection.equals(COLLECTION_NAME)) {
					collectionExists = true;
					break;
				}
			}

			if (!collectionExists) {
				CollectionManagementService service = (CollectionManagementService) baseCollection.getService("CollectionManagementService", "1.0");
				collection = service.createCollection(COLLECTION_NAME);
			} else {
				collection = baseCollection.getChildCollection(COLLECTION_NAME);
			}

		} catch (Exception e) {
			throw new PolicyRepositoryException("Error during init", e);
		}
	}

	public void shutdown() throws PolicyRepositoryException {
		try {
			if (baseCollection != null) {
				baseCollection.close();
				logger.debug("Closed base (db) collection");
			}
		} catch (Exception e) {
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
			throw new PolicyRepositoryException("unable to add policy ", e);
		}
	}

	@Override
	public byte[] getPolicy(String id) throws PolicyRepositoryException {
		try {
			XMLResource document = (XMLResource) collection.getResource(id);
			byte[] policy = ((String) document.getContent()).getBytes();
			logger.debug("policy retreived");

			return policy;
		} catch (XMLDBException e) {
			throw new PolicyRepositoryException("unable to retreive policy ", e);
		}
	}

	@Override
	public void updatePolicy(String id, byte[] policy) throws PolicyRepositoryException {
		try {
			XMLResource document = (XMLResource) collection.getResource(id);
			if (document == null) {
				throw new PolicyRepositoryException("unable to update policy, no policy found for id: " + id);
			}
			document.setContent(policy);
			collection.storeResource(document);
			logger.debug("policy updated");
		} catch (XMLDBException e) {
			throw new PolicyRepositoryException("unable to update policy ", e);
		}
	}

	@Override
	public void deletePolicy(String id) throws PolicyRepositoryException {
		try {
			XMLResource document = (XMLResource) collection.getResource(id);
			collection.removeResource(document);
		} catch (XMLDBException e) {
			throw new PolicyRepositoryException("unable to retreive policy ", e);
		}
	}

	@Override
	public List<String> listPolicies() throws PolicyRepositoryException {
		try {
			String[] policies = collection.listResources();
			Vector<String> vpolicies = new Vector<String>();

			for (int i = 0; i < policies.length; i++) {
				vpolicies.add(policies[i]);
			}

			return vpolicies;
		} catch (XMLDBException e) {
			throw new PolicyRepositoryException("unable to list policies ", e);
		}
	}

	@Override
	public Map<String, byte[]> getPolicies(EvaluationCtx eval) throws PolicyRepositoryException {
		try {
			String request = "for $policy in /Policy"
					+ " where $policy/Target/Resources/Resource/ResourceMatch/AttributeValue/text() = \"" + eval.getResourceId().encode() + "\""
					+ " or $policy/Target/Resources[count(AnyResource) = 1]"
					+ " return $policy";
			logger.debug("Executing Xquery to retreive relevent policies: \r\n" + request);
			Map<String, byte[]> results = query(request);
			logger.debug("Found " + results.size() + " relevent policies");
			return results;
		} catch (Exception e) {
			throw new PolicyRepositoryException("unable to get policies for given evalutation context", e);
		}
	}

	@Override
	public void purge() throws PolicyRepositoryException {
		logger.warn("purging policy repository will remove all existing policies");
		try {
			logger.debug("number of policies in collection before purge : " + collection.getResourceCount());
			CollectionManagementService service = (CollectionManagementService) baseCollection.getService("CollectionManagementService", "1.0");
			service.removeCollection(COLLECTION_NAME);
			collection = service.createCollection(COLLECTION_NAME);
			logger.debug("number of policies in collection after purge : " + collection.getResourceCount());
		} catch (Exception e) {
			throw new PolicyRepositoryException("Error during purge", e);
		}
	}

	private Map<String, byte[]> query(String XQuery) throws PolicyRepositoryException {
		try {
			XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			service.setProperty("indent", "yes");

			ResourceSet result = service.query(XQuery);
			ResourceIterator i = result.getIterator();
			HashMap<String, byte[]> results = new HashMap<String, byte[]> ();

			while (i.hasMoreResources()) {
				Resource r = i.nextResource();
				logger.debug("XML resource foudn for query : ");
				logger.debug("id: " + r.getId());
				logger.debug("content: " + r.getContent());
				results.put(r.getId(), ((String)r.getContent()).getBytes());
			}

			return results;
		} catch (Exception e) {
			logger.error("Error in query", e);
			throw new PolicyRepositoryException(e);
		}
	}
}
