package org.qualipso.factory.voipservice.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.openmeetings.app.data.basic.FieldLanguageDaoImpl;
import org.openmeetings.app.data.conference.Roommanagement;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Statemanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.hibernate.beans.adresses.States;
import org.openmeetings.app.hibernate.beans.domain.Organisation;
import org.openmeetings.app.hibernate.beans.lang.FieldLanguage;
import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.installation.ImportInitvalues;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.qualipso.factory.voipservice.VoIPConferenceServiceBean;
import org.qualipso.factory.voipservice.email.QualipsoConferenceEmail;
import org.qualipso.factory.voipservice.entity.ExtensionsConf;
import org.qualipso.factory.voipservice.entity.IaxBuddies;
import org.qualipso.factory.voipservice.entity.SipConf;

import com.twmacinta.util.MD5;

/**
 * Class with utils methods
 * 
 * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
 * @company Poznan Supercomputing and Networking Center
 * @license LGPL
 * @project QualiPSo
 * @date 24/07/2009
 */
public class AsteriskConferenceUtils {
    private static Logger log = Logger.getLogger(AsteriskConferenceUtils.class);

    /**
     * Private class for astman connection management
     * 
     * @author <a href="mailto:janny@man.poznan.pl">Dariusz Janny</a>
     * 
     */
    public static class AstmanConnection {
	private String host;
	private Integer port;
	private String login;
	private String pass;

	/**
	 * contructor
	 * 
	 * @param host
	 * @param port
	 * @param login
	 * @param pass
	 */
	public AstmanConnection(String host, Integer port, String login, String pass) {
	    this.host = host;
	    this.port = port;
	    this.login = login;
	    this.pass = pass;
	}

	/**
	 * @return
	 */
	public String getHost() {
	    return this.host;
	}

	/**
	 * @return
	 */
	public Integer getPort() {
	    return this.port;
	}

	/**
	 * @return
	 */
	public String getLogin() {
	    return this.login;
	}

	/**
	 * @return
	 */
	public String getPass() {
	    return this.pass;
	}
    }

    // hibernate properties
    public static final String ASTERISKDATABASESERVICEPERSISTENCE = "factory-service-voip-conferences-peristence";
    public static final String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
    public static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    public static final String HIBERNATE_FORMAT_SQL = "hibernate.format_sql";

    // creatinch DB schema
    public static final String CREATE = "create";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    // email properties
    public static final String MAIL_SMTP_USER = "mail.smtp.user";
    public static final String MAIL_SMTP_PASSWORD = "mail.smtp.password";
    public static final String MAIL_SMTP_ADDRESS = "mail.smtp.address";
    public static final String MAIL_SMTP_FULLNAME = "mail.smtp.fullname";

    // other variables
    public static final String DEFAULT_CONTEXT = "psnc-sip";
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String CONFERENCE_NAME = "conference";

    private static String VOIP_PROPERTIES = "voip-service.properties";

    private static EntityManagerFactory emf = null;

    /**
     * Getting entity manager
     * 
     * @return entity manager
     */
    public static EntityManager getEntityManager() {
	return getEntityManager(false);
    }

    /**
     * @param create
     *            when true then DB schema is re-created
     * @return
     */
    public static EntityManager getEntityManager(boolean create) {
	// overwrite default values - setup DB connection from properties file
	Map<String, String> map = new HashMap<String, String>();

	// when call re-create DB schema (remove old and create new)
	if (create) {
	    map.put(HIBERNATE_HBM2DDL_AUTO, CREATE);
	    map.put(HIBERNATE_SHOW_SQL, TRUE);
	    map.put(HIBERNATE_FORMAT_SQL, TRUE);

	    emf = Persistence.createEntityManagerFactory(ASTERISKDATABASESERVICEPERSISTENCE, map);
	} else {
	    map.put(HIBERNATE_SHOW_SQL, FALSE);
	    map.put(HIBERNATE_FORMAT_SQL, FALSE);
	}

	// create hibernate entity
	if (emf == null) {
	    emf = Persistence.createEntityManagerFactory(ASTERISKDATABASESERVICEPERSISTENCE);
	}

	EntityManager em = emf.createEntityManager();

	return em;
    }

    /**
     * Get astman listener connection
     * 
     * @return
     */
    public static AstmanConnection getAstmanListenerConnection() {
	Properties properties = AsteriskConferenceUtils.getProperties();

	String serverAddress = properties.getProperty("asterisk.astman.server");
	String serverPort = properties.getProperty("asterisk.astman.port");
	Integer intServerPort = Integer.parseInt(serverPort);
	String serverLogin = properties.getProperty("asterisk.astman.listener.login");
	String serverPassword = properties.getProperty("asterisk.astman.listener.password");

	return new AstmanConnection(serverAddress, intServerPort, serverLogin, serverPassword);
    }

    /**
     * Get astman manager connection
     * 
     * @return
     */
    public static AstmanConnection getAstmanManagerConnection() {
	Properties properties = AsteriskConferenceUtils.getProperties();

	String serverAddress = properties.getProperty("asterisk.astman.server");
	String serverPort = properties.getProperty("asterisk.astman.port");
	Integer intServerPort = Integer.parseInt(serverPort);
	String serverLogin = properties.getProperty("asterisk.astman.manager.login");
	String serverPassword = properties.getProperty("asterisk.astman.manager.password");

	return new AstmanConnection(serverAddress, intServerPort, serverLogin, serverPassword);
    }

    /**
     * close entity manager
     */
    public static void close() {
	try {
	    emf.close();
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    emf = null;
	}
    }

    /**
     * Format date
     * 
     * @param dateLong
     * @return
     */
    public static String formatDate(Long dateLong) {
	if (dateLong == null) {
	    return "";
	} else if (dateLong < 0) {
	    return "Not set";
	}

	Date date = new Date(dateLong * 1000);
	return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

    /**
     * Load properties from file
     * 
     * @return
     */
    public static Properties getProperties() {
	Properties properties = null;

	if (properties == null) {
	    try {
		properties = new Properties();
		java.net.URL url = VoIPConferenceServiceBean.class.getClassLoader().getResource(VOIP_PROPERTIES);
		properties.load(url.openStream());

		return properties;
	    } catch (IOException ee) {
		log.error(ee.getMessage());
		ee.printStackTrace();

		return new Properties();
	    }
	}

	return properties;
    }

    /**
     * @return table of users
     */
    @SuppressWarnings("unchecked")
    public static String[] listUsers() {
	EntityManager em = AsteriskConferenceUtils.getEntityManager();
	em.getTransaction().begin();

	List<SipConf> userList = (List<SipConf>) em.createQuery("FROM SipConf sc ORDER BY sc.username").getResultList();

	int i = 0;
	String[] userNames = new String[userList.size()];
	for (SipConf userSip : userList) {
	    userNames[i] = userSip.getUsername();
	    ++i;
	}

	em.getTransaction().commit();
	em.close();

	return userNames;
    }

    /**
     * @param username
     * @param secret
     * @param email
     * @param firstname
     * @param lastname
     * @param qualipso_username
     * @return
     */
    public static String createUser(String username, String secret, String email, String firstname, String lastname,
	    String qualipso_username) {

	return createAndStoreUser(username, secret, "friend", username, "dynamic", "no", "all",
		AsteriskConferenceUtils.DEFAULT_CONTEXT, email, firstname, lastname, qualipso_username);
    }

    public static void initOpenmeetingsPaths() {
	ScopeApplicationAdapter.webAppPath = "openmeetings";
    }
    
    public static void initOpenmeetings() {
	Properties properties = AsteriskConferenceUtils.getProperties();
	initOpenmeetingsPaths();
	String filePath = "/openmeetings/languages/";
	String url_feed =  "http://groups.google.com/group/openmeetings-dev/feed/atom_v1_0_msgs.xml";
	String url_feed2 =  "http://groups.google.com/group/openmeetings-user/feed/atom_v1_0_msgs.xml";
	
	URL url = QualipsoConferenceEmail.class.getResource(filePath);
	System.out.println(url.toString());	
	
	try {
	    HibernateUtil.rebuildOldSession();
	    ImportInitvalues.getInstance().loadInitLanguages(url.toString());
	} catch (Exception e1) {
	    e1.printStackTrace();
	}
	ImportInitvalues.getInstance().loadMainMenu();
	
	try {
	    ImportInitvalues.getInstance().loadErrorMappingsFromXML(url.toString());
	} catch (Exception e) {
	    e.printStackTrace();
	}
	ImportInitvalues.getInstance().loadSalutations();
	
	FieldLanguage fl = FieldLanguageDaoImpl.getInstance().getLanguages().get(0);
	ImportInitvalues.getInstance().loadConfiguration("org.openmeetings.utils.crypt.MD5Implementation", 
		"0", 
		(String)properties.get("mail.smtp.host"), (String)properties.get("mail.smtp.port"),
		"", 
		(String)properties.get("mail.smtp.user"), (String)properties.get("mail.smtp.password"),
		fl.getLanguage_id() +"", "", "", url_feed, url_feed2,
		"0", "0", "TimesNewRoman", "", "0");

	Organisationmanagement.getInstance().addOrganisation("QualiPSo", 1L);
	//ImportInitvalues.getInstance().loadDefaultRooms();
	Roommanagement.getInstance().addRoomType("conference");
	Roommanagement.getInstance().addRoomType("audience");
	
	// AppointMent Categories
	ImportInitvalues.getInstance().loadInitAppointmentCategories();
	
	// Appointment Remindertypes
	ImportInitvalues.getInstance().loadInitAppointmentReminderTypes();    
    }

    /**
     * @param name
     * @param secret
     * @param type
     * @param username
     * @param host
     * @param canreinvite
     * @param allow
     * @param context
     * @param email
     * @param firstname
     * @param lastname
     * @param qualipso_username
     * @return
     */
    public static String createAndStoreUser(String name, String secret, String type, String username, String host,
	    String canreinvite, String allow, String context, String email, String firstname, String lastname,
	    String qualipso_username) {

	if (username == null || username.trim().equals("")) {
	    return "empty username";
	}
	if (secret == null || secret.trim().equals("")) {
	    return "empty password";
	}
	if (firstname == null || firstname.trim().equals("")) {
	    return "empty firstname";
	}
	if (lastname == null || lastname.trim().equals("")) {
	    return "empty lastname";
	}

	MD5 md5 = new MD5();
	try {
	    md5.Update(secret, null);
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	}
	String md5secret = md5.asHex();

	EntityManager em = AsteriskConferenceUtils.getEntityManager();
	em.getTransaction().begin();

	SipConf sipConf = new SipConf();
	try {
	    sipConf.setName(name);
	    sipConf.setQualipsoUser(qualipso_username);
	    sipConf.setSecret(secret);
	    sipConf.setType(type);
	    sipConf.setUsername(username);
	    sipConf.setHost(host);
	    sipConf.setCanreinvite(canreinvite);
	    sipConf.setAllow(allow);
	    sipConf.setContext(context);
	    sipConf.setEmail(email);

	    em.persist(sipConf);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	try {
	    IaxBuddies iaxBuddies = new IaxBuddies();
	    iaxBuddies.setName(name);
	    iaxBuddies.setQualipsoUser(qualipso_username);
	    iaxBuddies.setSecret(secret);
	    iaxBuddies.setType(type);
	    iaxBuddies.setUsername(username);
	    iaxBuddies.setHost(host);
	    iaxBuddies.setAllow(allow);
	    iaxBuddies.setContext(context);
	    iaxBuddies.setCallerid(username);
	    iaxBuddies.setEmail(email);

	    if ("yes".equals(canreinvite)) {
		iaxBuddies.setNotransfer("no");
	    } else {
		iaxBuddies.setNotransfer("yes");
	    }
	    em.persist(iaxBuddies);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	try {
	    ExtensionsConf extConf = new ExtensionsConf();
	    extConf.setExten(username);
	    extConf.setApp("Dial");
	    extConf.setAppdata("SIP/" + username + "&IAX2/" + username + "|10|t");
	    extConf.setPriority(1);
	    extConf.setContext(context);
	    em.persist(extConf);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	Long level_id = new Long(1);
	if (name.equals("voip_admin")) {
	    level_id = new Long(3);
	} else {
	    level_id = new Long(1);
	}
	try {
	    AsteriskConferenceUtils.initOpenmeetingsPaths();
	    
	    FieldLanguage fl = FieldLanguageDaoImpl.getInstance().getLanguages().get(0);
	    States sm = (States)Statemanagement.getInstance().getStates().get(0);
	    List organisations = new ArrayList();
	    Organisation o = Organisationmanagement.getInstance().getOrganisationById(1L);
	    organisations.add(o.getOrganisation_id().intValue());
	    
	    Long user_id = Usermanagement.getInstance().registerUserInit(new Long(3), level_id, 1, 1, 
	    	name, secret, lastname, firstname, email, new Date(), 
	    	"", "", "", "", 
	    	sm.getState_id(), 
	    	"", fl.getLanguage_id(), false, 
	    	organisations,
	    	"");

	} catch (Exception e) {
	    e.printStackTrace();
	}

	em.getTransaction().commit();
	em.close();

	return sipConf.getName();
    }

    /**
     * @param extension
     * @param context
     */
    public static void setupConfferenceExtension(String extension, String context) {
	EntityManager em = AsteriskConferenceUtils.getEntityManager();
	em.getTransaction().begin();

	ExtensionsConf extConf = new ExtensionsConf();
	extConf.setExten(extension);
	extConf.setApp("Goto");
	extConf.setAppdata(extension + "|s|1");
	extConf.setContext(context);
	extConf.setPriority(1);
	em.persist(extConf);

	extConf = new ExtensionsConf();
	extConf.setContext(extension);
	extConf.setPriority(1);
	extConf.setExten("s");
	extConf.setApp("Set");
	extConf.setAppdata("mainloop = 1");
	em.persist(extConf);

	extConf = new ExtensionsConf();
	extConf.setContext(extension);
	extConf.setPriority(2);
	extConf.setExten("s");
	extConf.setApp("Answer");
	em.persist(extConf);

	extConf = new ExtensionsConf();
	extConf.setContext(extension);
	extConf.setPriority(3);
	extConf.setExten("s");
	extConf.setApp("Wait");
	extConf.setAppdata("2");
	em.persist(extConf);

	extConf = new ExtensionsConf();
	extConf.setContext(extension);
	extConf.setPriority(4);
	extConf.setExten("s");
	extConf.setApp("Background");
	extConf.setAppdata("conf-getconfno");
	em.persist(extConf);

	extConf = new ExtensionsConf();
	extConf.setContext(extension);
	extConf.setPriority(5);
	extConf.setExten("s");
	extConf.setApp("WaitExten");
	extConf.setAppdata("8");
	em.persist(extConf);

	extConf = new ExtensionsConf();
	extConf.setContext(extension);
	extConf.setPriority(10);
	extConf.setExten("s");
	extConf.setApp("Set");
	extConf.setAppdata("mainloop = $[${mainloop}+1]");
	em.persist(extConf);

	extConf = new ExtensionsConf();
	extConf.setContext(extension);
	extConf.setPriority(11);
	extConf.setExten("s");
	extConf.setApp("GotoIf");
	extConf.setAppdata("$[${mainloop} <= 3]?3");
	em.persist(extConf);

	extConf = new ExtensionsConf();
	extConf.setContext(extension);
	extConf.setPriority(12);
	extConf.setExten("s");
	extConf.setApp("HangUp");
	em.persist(extConf);

	extConf = new ExtensionsConf();
	extConf.setContext(extension);
	extConf.setPriority(1);
	extConf.setExten("t");
	extConf.setApp("Goto");
	extConf.setAppdata("s|10");
	em.persist(extConf);

	extConf = new ExtensionsConf();
	extConf.setContext(extension);
	extConf.setPriority(1);
	extConf.setExten("i");
	extConf.setApp("Background");
	extConf.setAppdata("conf-invalid");
	em.persist(extConf);

	extConf = new ExtensionsConf();
	extConf.setContext(extension);
	extConf.setPriority(2);
	extConf.setExten("i");
	extConf.setApp("Goto");
	extConf.setAppdata("s|10");
	em.persist(extConf);

	em.getTransaction().commit();
	em.close();
    }

    /**
     * @param username
     * @return
     */
    public static boolean removeUser(String username) {
	EntityManager em = AsteriskConferenceUtils.getEntityManager();
	em.getTransaction().begin();

	// delete from sip
	Query query = em.createQuery("DELETE FROM SipConf sc WHERE sc.username = :username");
	query.setParameter("username", username);
	@SuppressWarnings("unused")
	int count = query.executeUpdate();

	// delete from IAX2
	query = em.createQuery("DELETE FROM IaxBuddies ib WHERE ib.username = :username");
	query.setParameter("username", username);
	count = query.executeUpdate();

	// delete from extension plan
	query = em.createQuery("DELETE FROM ExtensionsConf ec WHERE ec.exten = :exten");
	query.setParameter("exten", username);
	count = query.executeUpdate();

	em.getTransaction().commit();
	em.close();

	return true;
    }

    /**
	 * 
	 */
    public static void clearDatabase() {
	EntityManager em = AsteriskConferenceUtils.getEntityManager(true);
	em.getTransaction().begin();

	try {
	    em.createQuery("DELETE FROM ExtensionsConf").executeUpdate();
	} catch (Exception e) {
	    log.error(e.getMessage());
	}
	try {
	    em.createQuery("DELETE FROM SipConf").executeUpdate();
	} catch (Exception e) {
	    log.error(e.getMessage());
	}
	try {
	    em.createQuery("DELETE FROM MeetMe").executeUpdate();
	} catch (Exception e) {
	    log.error(e.getMessage());
	}
	try {
	    em.createQuery("DELETE FROM IaxBuddies").executeUpdate();
	} catch (Exception e) {
	    log.error(e.getMessage());
	}
	try {
	    em.createQuery("DELETE FROM PastConference").executeUpdate();
	} catch (Exception e) {
	    log.error(e.getMessage());
	}

	try {
	    em.createQuery("DELETE FROM Recording").executeUpdate();
	} catch (Exception e) {
	    log.error(e.getMessage());
	}

	try {
	    em.createQuery("DELETE FROM Invitations").executeUpdate();
	} catch (Exception e) {
	    log.error(e.getMessage());
	}

	try {
	    em.createQuery("DELETE FROM Organisation_Users").executeUpdate();
	} catch (Exception e) {
	    log.error(e.getMessage());
	}

	try {
	    em.createQuery("DELETE FROM Users").executeUpdate();
	} catch (Exception e) {
	    log.error(e.getMessage());
	}

	try {
	    em.createQuery("DELETE FROM Rooms").executeUpdate();
	} catch (Exception e) {
	    log.error(e.getMessage());
	}

	try {
	    em.createQuery("DELETE FROM RoomTypes").executeUpdate();
	} catch (Exception e) {
	    log.error(e.getMessage());
	}

	em.getTransaction().commit();
	em.close();
    }
}