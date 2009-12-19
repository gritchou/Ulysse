package org.qualipso.factory.collaboration.ws;

import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class CollaborationProperties.
 */
public class CollaborationProperties {

    /** The logger. */
    private static Log logger = LogFactory
	    .getLog(CollaborationProperties.class);

    /** The Constant PROPERTIES_FILE_NAME. */
    private static final String PROPERTIES_FILE_NAME = "mermigws";

    /** The Constant PROP_NAME_ENDPOINT. */
    private static final String PROP_NAME_ENDPOINT = "endpoint";

    /** The Constant PROP_NAME_USERNAME. */
    private static final String PROP_NAME_USERNAME = "username";

    /** The Constant PROP_NAME_PASSWORD. */
    private static final String PROP_NAME_PASSWORD = "password";

    /** The Constant PROP_NAME_FOLDER. */
    private static final String PROP_NAME_FOLDER = "default.folder";

    /** The Constant PROP_NAME_WORKSPACE. */
    private static final String PROP_NAME_WORKSPACE = "default.workspace";
    //
    /** The MERMI g_ endpoint. */
    protected String MERMIG_ENDPOINT = "http://syros.eurodyn.com:8082/mermig/services/MermigWebService";

    /** The USE r_ name. */
    protected String USER_NAME = "demode";

    /** The USE r_ pwd. */
    protected String USER_PWD = "demode123";

    /** The DEFAUL t_ folde r_ id. */
    protected String DEFAULT_FOLDER_ID = "170";

    /** The DEFAUL t_ workspac e_ id. */
    protected String DEFAULT_WORKSPACE_ID = "10";

    /** The class instance. */
    private static CollaborationProperties classInstance = null;

    /**
     * Instantiates a new collaboration properties.
     */
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

    /**
     * Gets the single instance of CollaborationProperties.
     * 
     * @return single instance of CollaborationProperties
     */
    public static CollaborationProperties getInstance() {
	if (classInstance == null) {
	    classInstance = new CollaborationProperties();
	}
	return classInstance;
    }

}
