package org.qualipso.factory.jabuti;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.jabuti.ws.FileValidation;
import org.qualipso.factory.jabuti.ws.VerifingData;
import org.qualipso.factory.jabuti.ws.WsProject;



/**
 * @author Rafael Messias Martins
 * @date Sometime in 2009
 */
@Stateless(
		name = JabutiService.SERVICE_NAME, 
		mappedName = FactoryNamingConvention.SERVICE_PREFIX + 
			JabutiService.SERVICE_NAME)
@WebService(
		endpointInterface = "org.qualipso.factory.jabuti.JabutiService", 
		targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + 
			JabutiService.SERVICE_NAME, 
		serviceName = JabutiService.SERVICE_NAME)
@WebContext(
		contextRoot = FactoryNamingConvention.WEB_SERVICE_ROOT_MODULE_CONTEXT +
			"-" + JabutiService.SERVICE_NAME, 
		urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX + 
			JabutiService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class JabutiServiceBean implements JabutiService {

	private static Log logger = LogFactory.getLog(JabutiServiceBean.class);

//	private MembershipService membership;
//	private CoreService core;
	
	private Properties props;
	
	public JabutiServiceBean() {
		// The init code cannot be here because the services are not bound yet.
	}
	
	private void init() throws JabutiServiceException {
		// initialization code
		
		props = new Properties();
//		props.setProperty("JABUTI_PERSISTENCE_HOME", "/jabuti/files");
		props.setProperty("JABUTI_PERSISTENCE_HOME", "jabuti");
//		props.setProperty("JABUTI_TEMP_HOME", "/jabuti/temp");
		props.setProperty("db.url", "jdbc:hsqldb:file:db/jabutidb;shutdown=true");
		props.setProperty("db.user", "SA");
		props.setProperty("db.password", "");
		
		// Database Test
		try {
			String url = props.getProperty("db.url");
	        String user = props.getProperty("db.user");
	        String pass = props.getProperty("db.password");
			
        	Class.forName("org.hsqldb.jdbcDriver");
            Connection CONN = DriverManager.getConnection(url, user, pass);            
            Statement st = CONN.createStatement();
            
            try {
            	// check if the table 'project' exists
            	ResultSet rs = st.executeQuery("SELECT * FROM project");
            	rs.close();
            }
            catch (SQLException e) {
            	// if not, create it
            	st.executeUpdate("DROP TABLE project IF EXISTS");            
            	st.executeUpdate("CREATE TABLE project ( " +
            			" id varchar(50) NOT NULL," +
            			" name varchar(200) NOT NULL," +
            			" testsuite varchar(500) default NULL," +
            			" selectedclasses LONGVARCHAR," +
            			" ignoredclasses LONGVARCHAR," +
            			" state integer default NULL," +
            			" PRIMARY KEY (id))"); 
            }                   
            st.close();
            CONN.close();
        } 
        catch (SQLException e) {
        	logger.error("Error during database connection: " + e);
        	e.printStackTrace();
        	throw new JabutiServiceException(e);
        }
        catch (ClassNotFoundException e) {
        	logger.error("Error on class-loading of database driver: " + e);
        	e.printStackTrace();
        	throw new JabutiServiceException(e);
        }
        
        // Persistence Test
        
        String dir = props.getProperty("JABUTI_PERSISTENCE_HOME");
        
        // For the original only:
        if (dir.startsWith("/")) {
        	dir = dir.substring(1, dir.length());
        }
        
        if(!dir.endsWith("/"))
			dir += "/";
		
		File directory = new File(dir);
		if(!directory.isDirectory()) {
			logger.info("Persistence directory does not exist. Trying to create it...");
			directory.mkdir();
			if(directory.exists() && directory.isDirectory()) {
				logger.info("Persistence directory created succesfully!");
			}
			else {
				logger.info("Couldn't create Persistence directory!");
			}
		}

		try {
			File file = new File(dir + "test.txt");
			PrintWriter pw = new PrintWriter(file);
			pw.write("testing...");
			pw.close();

			file.delete();

			System.out.println("Persistence directory: OK");
		}
		catch(Exception e) {	        		        	
			logger.error("Error: Persistence directory - no permission " +
					"to read/write" + e);
			e.printStackTrace();
			throw new JabutiServiceException(e);			
		}
        
        // Factory Resource version:
//        String dir1 = "jabuti";
//        String path1 = "/" + dir1;
//        String dir2 = "files";
//        String path2 = path1 + "/" + dir2;
//        String dir3 = "temp";
//        String path3 = path1 + "/" + dir3;
        
//        Folder folder;
//        try {        	
//        	// check if the data/temp folder exists
//        	folder = core.readFolder(path2);
//        	folder = core.readFolder(path3);
//        }		
//        catch (PathNotFoundException e) {
//        	// if not, create it
//        	try {
//        		core.createFolder(path1, dir1, "Base path for JaBUTi data.");
//        		core.createFolder(path2, dir2, "Folder for JaBUTi testing projects.");
//        		core.createFolder(path3, dir3, "Temporary folder for JaBUTi.");
//        	}
//        	catch (FactoryException e2) {
//        		logger.error("Problem creating JaBUTi data/temp folder: " + e2);
//        		e2.printStackTrace();
//        		throw new JabutiServiceException(e2);
//        	}
//        }
//        catch (FactoryException e) {
//        	logger.error("Problem reading JaBUTi data/temp folder: " + e);
//        	e.printStackTrace();
//        	throw new JabutiServiceException(e);
//        }
	}
		
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String createProject(String IdUserName, String projectName,
			byte[] projectFile) 
	throws JabutiServiceException {
		logger.debug("createProject(...) called");
		
		init();
		
		// MyDataHandler not really necessary...
		MyDataHandler projectFileHandler = new MyDataHandler(
				new ByteArrayDataSource(projectFile, 
						"application/java-archive"));
		
		//save the attached file in a temporary directory 
//		File f = saveTempFile(projectFileHandler);
		File f = VerifingData.saveTempFile(projectFileHandler);

		FileValidation fv = new FileValidation();
		if(fv.validateFile(f)) {
			try {
				WsProject control = new WsProject(props);
				String ret[] = control.create(projectName, f);
				return ret[0];
			}
			catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
				throw new JabutiServiceException(e);
			}
		}
		else {
			InvalidFileFault e = new InvalidFileFault(fv.getMessage());
			logger.error(e);
			e.printStackTrace();
			throw new JabutiServiceException(e);
		}
			
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String updateProject(String IdUserName, String projectId, byte[] projectFile) 
	throws InvalidProjectIdFault, InvalidFileFault, JabutiServiceException
	{
		
		init();
		
		DataHandler projectFileHandler = new DataHandler(
				new ByteArrayDataSource(projectFile, "application/java-archive"));
		
		VerifingData verifingdata = new VerifingData();
		if(verifingdata.existProject(projectId, props))
		{
			File f = VerifingData.saveTempFile(projectFileHandler);
			FileValidation fv = new FileValidation();
			if(fv.validateFile(f)) {
				WsProject control = new WsProject(props);
				control.update(projectId, f);

				return "project updated.";
			}
			else
				throw new InvalidFileFault(fv.getMessage());
		}
		else
			throw new InvalidProjectIdFault("The project does not exist.");
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String deleteProject(String IdUserName, String projectId) 
	throws InvalidProjectIdFault, JabutiServiceException
	{
		init();
		VerifingData verifingdata = new VerifingData();
		if(verifingdata.existProject(projectId, props)) {
			WsProject control = new WsProject(props);
			control.delete(projectId);
			return "The project was removed.";			
		}
		else 
			throw new InvalidProjectIdFault("The project does not exist.");
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String cleanProject(String IdUserName, String projectId) 
	throws InvalidProjectIdFault, JabutiServiceException
	{
		init();
		VerifingData verifingdata = new VerifingData();
		if(verifingdata.existProject(projectId, props)) {
			WsProject control = new WsProject(props);
			control.clean(projectId);
			return "The project was cleaned.";			
		}
		else 
			throw new InvalidProjectIdFault("The project does not exist.");
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String ignoreClasses(String IdUserName, String projectId, String[] classes) 
	throws InvalidProjectIdFault, ClassNotFoundFault, InvalidExpressionFault, JabutiServiceException
	{
		init();
		VerifingData verifingdata = new VerifingData();
		if(verifingdata.existProject(projectId, props)) {
			WsProject control = new WsProject(props);
			control.ignoreClasses(projectId, classes);
			return "the ignored classes were recorded.";
		}
		else 
			throw new InvalidProjectIdFault("The project does not exist.");
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String selectClassesToInstrument(String IdUserName, String projectId, String[] classes) 
	throws InvalidProjectIdFault, ClassNotFoundFault, InvalidExpressionFault, JabutiServiceException
	{
		init();
		VerifingData verifingdata = new VerifingData();
		if(verifingdata.existProject(projectId, props)) {
			WsProject control = new WsProject(props);
			control.selectClassesToInstrument(projectId, classes);

			return "The classes were instrumented.";			
		}
		else 
			throw new InvalidProjectIdFault("The project does not exist.");
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RequiredElementsDetails[] getAllRequiredElements(String IdUserName, String projectId) 
	throws InvalidProjectIdFault, OperationSequenceFault, JabutiServiceException
	{
		init();
		VerifingData verifingdata = new VerifingData();
		if(verifingdata.existProject(projectId, props)) {
			if(verifingdata.isProjectInstrumented(projectId)) {
				WsProject control = new WsProject(props);
				return control.getAllRequiredElements(projectId);
			}
			else
				throw new OperationSequenceFault("The project's classes are not instrumented.");
		}
		else 
			throw new InvalidProjectIdFault("The project does not exist.");
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Method[] getRequiredElementsByCriterion(String IdUserName, String projectId, String criterion) 
	throws InvalidProjectIdFault, OperationSequenceFault, InvalidCriterionFault, JabutiServiceException
	{
		init();
		WsProject control = new WsProject(props);
		return control.getRequiredElementsByCriterion(projectId, criterion);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String addTestCases(String IdUserName, String projectId, String testSuiteClass, byte[] testCaseFile) 
	throws InvalidFileFault, InvalidProjectIdFault, ClassNotFoundFault, OperationSequenceFault, JabutiServiceException
	{
		init();
		DataHandler testCaseFileHandler = new DataHandler(
				new ByteArrayDataSource(testCaseFile, "application/java-archive"));
		
		VerifingData verifingdata = new VerifingData();
		if(verifingdata.existProject(projectId, props))
		{
			File f = VerifingData.saveTempFile(testCaseFileHandler);
			FileValidation fv = new FileValidation();
			if(fv.validateFile(f)) {
				if(verifingdata.isThereClassInFile(testSuiteClass, f)) {
					WsProject control = new WsProject(props);
					control.addTestCases(projectId, testSuiteClass, f);
					return "Test Case file was added.";
				}
				else
					throw new ClassNotFoundFault("Class " + testSuiteClass + " was not found in testcase file.");
			}
			else
				throw new InvalidFileFault(fv.getMessage());
		}
		else
			throw new InvalidProjectIdFault("The project does not exist.");	
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public InstrumentedProjectDetails getInstrumentedProject(String IdUserName, String projectId) 
	throws InvalidProjectIdFault, OperationSequenceFault, JabutiServiceException
	{
		init();
		VerifingData verifingdata = new VerifingData();
		if(verifingdata.existProject(projectId, props)) {
			if(verifingdata.isProjectInstrumented(projectId)) {
				WsProject control = new WsProject(props);
				return control.getInstrumentedProject(projectId);
			}
			else
				throw new OperationSequenceFault("The project's classes are not instrumented.");	
			//test cases are not added
			//to do
		}
		else 
			throw new InvalidProjectIdFault("The project does not exist.");
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String sendTraceFile(String IdUserName, String projectId, byte[] traceFile) 
	throws InvalidProjectIdFault, OperationSequenceFault, InvalidFileFault, JabutiServiceException
	{
		init();
		DataHandler traceFileHandler = new DataHandler(
				new ByteArrayDataSource(traceFile, "application/java-archive"));
		
		VerifingData verifingdata = new VerifingData();
		if(verifingdata.existProject(projectId, props)) {
			if(verifingdata.isProjectInstrumented(projectId)) {
				WsProject control = new WsProject(props);

				File f = VerifingData.saveTempFile(traceFileHandler);
				control.sendTraceFile(projectId, f);

				return "ok";
			}
			else
				throw new OperationSequenceFault("The project's classes are not instrumented.");	
			//test cases are not added
			//to do
		}
		else 
			throw new InvalidProjectIdFault("The project does not exist.");
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public CoverageCriterionDetails[] getCoverageByCriteria(String IdUserName, String projectId) 
	throws InvalidProjectIdFault, OperationSequenceFault, JabutiServiceException
	{
		init();
		WsProject control = new WsProject(props);
		return control.getCoverageByCriteria(projectId);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public CoverageDetails[] getCoverageByClasses(String IdUserName, String projectId) 
	throws InvalidProjectIdFault, OperationSequenceFault, JabutiServiceException
	{
		init();
		WsProject control = new WsProject(props);
		return control.getCoverageByClasses(projectId);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public CoverageDetails[] getCoverageByMethods(String IdUserName, String projectId) 
	throws InvalidProjectIdFault, OperationSequenceFault, JabutiServiceException
	{
		init();
		WsProject control = new WsProject(props);
		return control.getCoverageByMethods(projectId);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public MethodDetails[] getAllCoveredAndUncoveredRequiredElements(String IdUserName, String projectId) 
	throws InvalidProjectIdFault, OperationSequenceFault, JabutiServiceException
	{
		init();
		WsProject control = new WsProject(props);
		return control.getAllCoveredAndUncoveredRequiredElements(projectId);
	}

//	public File saveTempFile(DataHandler dataHandler) 
//	throws JabutiServiceException {
//		logger.debug("saveTempFile(...) called");
//		File f = null;
//	    try {
//	    	String filename = "tmp-" + String.valueOf(System.nanoTime());
//	    	String path = props.getProperty("JABUTI_TEMP_HOME") + "/" + filename;
//	    	String type = "application/java-archive";
//	    	String desc = "";
//	    	FileData data = new FileData(dataHandler);
//	    	logger.debug("checkpoint 1");
//	    	core.createFile(path, filename, type, desc, data);
//	    	logger.debug("checkpoint 2");
//	    	f = core.readFile(path);
//	    	logger.debug("checkpoint 3");
//	    }
//	    catch (FactoryException e) {
//	    	logger.error("Error creating JaBUTi temp file: " + e);
//			e.printStackTrace();
//			throw new JabutiServiceException(e);
//		}
//		return f;
//	}
	
	// --------------------- Membership, Core

//	@EJB
//	public void setMembershipService(MembershipService membership) {
//		this.membership = membership;
//	}
//
//	public MembershipService getMembershipService() {
//		return this.membership;
//	}

//	@EJB
//	public void setCoreService(CoreService core) {
//		this.core = core;
//	}
//
//	public CoreService getCoreService() {
//		return this.core;
//	}
	
	// --------------------- FactoryService methods
	
	@Override
	public String[] getResourceTypeList() {
		return RESOURCE_TYPE_LIST;
	}

	@Override
	public String getServiceName() {
		return JabutiService.SERVICE_NAME;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public FactoryResource findResource(String path) throws FactoryException {
		logger.info("findResource(...) called");
		logger.debug("params : path=" + path);

		throw new FactoryException("No resources are managed by Jabuti Service");
	}

}

