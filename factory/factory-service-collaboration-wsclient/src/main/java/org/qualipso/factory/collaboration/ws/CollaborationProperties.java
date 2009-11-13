package org.qualipso.factory.collaboration.ws;

import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CollaborationProperties {
    private static Log logger = LogFactory
	    .getLog(CollaborationProperties.class);

    private static final String PROPERTIES_FILE_NAME = "mermigws";
    private static final String PROP_NAME_ENDPOINT = "endpoint";
    private static final String PROP_NAME_USERNAME = "username";
    private static final String PROP_NAME_PASSWORD = "password";
    private static final String PROP_NAME_FOLDER = "default.folder";
    private static final String PROP_NAME_WORKSPACE = "default.workspace";
    //
    protected String MERMIG_ENDPOINT = "http://syros.eurodyn.com:8082/mermig/services/MermigWebService";
    protected String USER_NAME = "demode";
    protected String USER_PWD = "demode123";
    protected String DEFAULT_FOLDER_ID = "170";
    protected String DEFAULT_WORKSPACE_ID = "10";

    private static CollaborationProperties classInstance = null;

    private CollaborationProperties() {
	ResourceBundle resource = null;
	try {
	    resource = ResourceBundle.getBundle(PROPERTIES_FILE_NAME);
	} catch (Exception e) {
	    logger.warn("Cannot load properties file: " + PROPERTIES_FILE_NAME
		    + " Reason: " + e.getMessage(), e);
	}
	// if we cant read properties file or properties empty we use the
	// default values.
	if (resource != null) {
	    String endpoint = resource.getString(PROP_NAME_ENDPOINT);
	    if (endpoint != null && endpoint.length() > 0) {
		this.MERMIG_ENDPOINT = endpoint;
	    }
	    String user = resource.getString(PROP_NAME_USERNAME);
	    if (user != null && user.length() > 0) {
		this.USER_NAME = user;
	    }
	    String pwd = resource.getString(PROP_NAME_PASSWORD);
	    if (pwd != null && pwd.length() > 0) {
		this.USER_PWD = pwd;
	    }
	    String folder = resource.getString(PROP_NAME_FOLDER);
	    if (folder != null && folder.length() > 0) {
		this.DEFAULT_FOLDER_ID = folder;
	    }
	    String workspace = resource.getString(PROP_NAME_WORKSPACE);
	    if (workspace != null && workspace.length() > 0) {
		this.DEFAULT_WORKSPACE_ID = workspace;
	    }
	    if (logger.isDebugEnabled()) {
		logger.debug(PROP_NAME_ENDPOINT + "=" + this.MERMIG_ENDPOINT);
		logger.debug(PROP_NAME_USERNAME + "=" + this.USER_NAME);
		logger.debug(PROP_NAME_FOLDER + "=" + this.DEFAULT_FOLDER_ID);
		logger.debug(PROP_NAME_WORKSPACE + "="
			+ this.DEFAULT_WORKSPACE_ID);
	    }
	}
    }

    public static CollaborationProperties getInstance() {
	if (classInstance == null) {
	    classInstance = new CollaborationProperties();
	}
	return classInstance;
    }

}
