package org.qualipso.factory.xmldb.exist;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.rpc.ServiceException;

import org.exist.EXistException;
import org.exist.storage.BrokerPool;
import org.exist.util.Configuration;
import org.jboss.system.ServiceMBeanSupport;

/**
 * 
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 11 november 2009
 */
public class EXistDB extends ServiceMBeanSupport implements EXistDBMBean {

	protected String confFile;
	protected Configuration configuration;
	protected String eXistHome;

	private static final String DEFAULT_CONFIG = "<?xml version='1.0' encoding='UTF-8'?>"
			+ "<exist>"
			+ "<cluster dbaPassword='admin' dbaUser='admin' exclude='/db/system,/db/system/config' journalDir='data/journal' protocol='UDP(mcast_addr=228.1.2.3;mcast_port=45566;ip_ttl=8;ip_mcast=true;mcast_send_buf_size=800000;mcast_recv_buf_size=150000;ucast_send_buf_size=800000;ucast_recv_buf_size=150000;loopback=true):PING(timeout=2000;num_initial_members=3;up_thread=true;down_thread=true):MERGE2(min_interval=10000;max_interval=20000):FD(shun=true;up_thread=true;down_thread=true;timeout=2500;max_tries=5):VERIFY_SUSPECT(timeout=3000;num_msgs=3;up_thread=true;down_thread=true):pbcast.NAKACK(gc_lag=50;retransmit_timeout=300,600,1200,2400,4800;max_xmit_size=8192;up_thread=true;down_thread=true):UNICAST(timeout=300,600,1200,2400,4800;window_size=100;min_threshold=10;down_thread=true):pbcast.STABLE(desired_avg_gossip=20000;up_thread=true;down_thread=true):FRAG(frag_size=8192;down_thread=true;up_thread=true):pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;shun=true;print_local_addr=true)'/>"
			+ "<db-connection cacheSize='48M' collectionCacheSize='128' database='native' files='data' free_mem_min='5' pageSize='4096'>"
			+ "<pool max='20' min='1' sync-period='120000' wait-before-shutdown='120000'/>"
			+ "<recovery enabled='yes' group-commit='no' journal-dir='data' size='100M' sync-on-commit='no'/>"
			+ "<watchdog output-size-limit='10000' query-timeout='-1'/>"
			+ "</db-connection>"
			+ "<indexer caseSensitive='yes' index-depth='5' preserve-whitespace-mixed-content='no' stemming='no' suppress-whitespace='both' tokenizer='org.exist.storage.analysis.SimpleTokenizer' track-term-freq='yes' validation='auto'>"
			+ "<stopwords file='stopword'/>"
			+ "<index>"
			+ "<fulltext attributes='true' default='all'>"
			+ "<exclude path='/auth'/>"
			+ "</fulltext>"
			+ "</index>"
			+ "<entity-resolver>"
			+ "<catalog file='catalog.xml'/>"
			+ "</entity-resolver>"
			+ "</indexer>"
			+ "<serializer add-exist-id='none' compress-output='no' enable-xinclude='yes' enable-xsl='no' indent='yes' match-tagging-attributes='no' match-tagging-elements='yes'/>"
			+ "<transformer class='org.apache.xalan.processor.TransformerFactoryImpl'/>" + "<xquery enable-java-binding='yes'>" + "<builtin-modules>"
			+ "<module class='org.exist.xquery.functions.util.UtilModule' uri='http://exist-db.org/xquery/util'/>"
			+ "<module class='org.exist.xquery.functions.transform.TransformModule' uri='http://exist-db.org/xquery/transform'/>"
			+ "<module class='org.exist.xquery.functions.xmldb.XMLDBModule' uri='http://exist-db.org/xquery/xmldb'/>"
			+ "<module class='org.exist.xquery.functions.request.RequestModule' uri='http://exist-db.org/xquery/request'/>"
			+ "<module class='org.exist.xquery.functions.response.ResponseModule' uri='http://exist-db.org/xquery/response'/>"
			+ "<module class='org.exist.xquery.functions.session.SessionModule' uri='http://exist-db.org/xquery/session'/>"
			+ "<module class='org.exist.xquery.functions.text.TextModule' uri='http://exist-db.org/xquery/text'/>"
			+ "<module class='org.exist.xquery.modules.example.ExampleModule' uri='http://exist-db.org/xquery/examples'/>"
			+ "<module class='org.exist.xquery.functions.validation.ValidationModule' uri='http://exist-db.org/xquery/validation'/>"
			+ "<module class='org.exist.xquery.functions.system.SystemModule' uri='http://exist-db.org/xquery/system'/>"
			+ "</builtin-modules>" + "</xquery>"
			+ "<xupdate allowed-fragmentation='5' enable-consistency-checks='no' growth-factor='20'/>" + "</exist>";

	public String getEXistHome() {
		return eXistHome;
	}

	public void setEXistHome(String eXistHome) {
		this.eXistHome = eXistHome;
	}

	public void startService() throws Exception {
		String jbossServerDir = System.getProperty("jboss.home");
		log.debug("JBossBaseDir is " + jbossServerDir + ", eXistHome is " + eXistHome);
		File eXistHomeDir = new File(jbossServerDir, eXistHome);
		log.debug("eXistHomeDir set to " + eXistHomeDir);
		eXistHome = eXistHomeDir.getAbsolutePath();

		log.debug("eXistHome set to " + eXistHome);
		if (!eXistHomeDir.exists()) {
			log.debug("exist home directory not found at " + eXistHome + ", creating new directory");
			eXistHomeDir.mkdirs();
		}
		System.setProperty("exist.home", eXistHome);

		File dataDir = new File(eXistHomeDir, "data");
		if (!dataDir.exists()) {
			log.debug("creating data dir in exist home");
			dataDir.mkdir();
		}

		confFile = new File(eXistHome, "conf.xml").getAbsolutePath();
		log.debug("confFile set to " + confFile);
		File f = new File(confFile);
		if (!f.exists()) {
			log.debug("Config file does not exist, creating default configuration...");
			createDefaultConfigFile(f);
		}
		if (!f.canRead()) {
			throw new ServiceException("configuration file " + confFile + " is not readable");
		}
		configuration = new Configuration(confFile, eXistHome);
		if (configuration == null) {
			throw new Exception("Failed to create configuration for database");
		}
		if (!BrokerPool.isConfigured()) {
			log.debug("Configuring database");
			BrokerPool.configure(1, 5, configuration);
		}
	}

	public void stopService() throws Exception {
		if (BrokerPool.isConfigured()) {
			BrokerPool.stop();
		}
	}

	private void createDefaultConfigFile(File file) throws IOException {
		FileWriter writer = new FileWriter(file);
		writer.write(DEFAULT_CONFIG);
		writer.flush();
		writer.close();
	}

	public String getStatus() {
		String output = "";
		if (!BrokerPool.isConfigured())
			output += "Server is not running ...";
		else
			output += "The database server is running ...";
		return output;
	}

	public String getConfiguration() {
		String output = "";
		try {
			if (!BrokerPool.isConfigured())
				output += "Server is not running ...";
			else {
				BrokerPool pool = BrokerPool.getInstance();
				Configuration configuration = pool.getConfiguration();
				output += "Configuration : " + configuration.getConfigFilePath();
			}
		} catch (EXistException e) {
			output += e.toString();
		}
		return output;
	}

	public String getDataDirectory() {
		String output = "";
		try {
			if (!BrokerPool.isConfigured())
				output += "Server is not running ...";
			else {
				BrokerPool pool = BrokerPool.getInstance();
				Configuration configuration = pool.getConfiguration();
				output += "Data directory:" + (String) configuration.getProperty("db-connection.data-dir");
			}
		} catch (EXistException e) {
			output += e.toString();
		}
		return output;
	}

	public String getActiveInstances() {
		String output = "";
		try {
			if (!BrokerPool.isConfigured())
				output += "Server is not running ...";
			else {
				BrokerPool pool = BrokerPool.getInstance();
				output += "Active instances:" + pool.active();
			}
		} catch (EXistException e) {
			output += e.toString();
		}
		return output;
	}

	public String getAvailableInstances() {
		String output = "";
		try {
			if (!BrokerPool.isConfigured())
				output += "Server is not running ...";
			else {
				BrokerPool pool = BrokerPool.getInstance();
				output += "Available instances:" + pool.available();
			}
		} catch (EXistException e) {
			output += e.toString();
		}
		return output;
	}

}
