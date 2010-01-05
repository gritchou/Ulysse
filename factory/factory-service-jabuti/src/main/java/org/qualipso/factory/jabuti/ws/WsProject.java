package org.qualipso.factory.jabuti.ws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipFile;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.qualipso.factory.jabuti.CoverageCriterionDetails;
import org.qualipso.factory.jabuti.CoverageDetails;
import org.qualipso.factory.jabuti.CriterionCoveredUncovered;
import org.qualipso.factory.jabuti.InstrumentedProjectDetails;
import org.qualipso.factory.jabuti.Method;
import org.qualipso.factory.jabuti.MethodDetails;
import org.qualipso.factory.jabuti.RequiredElementsDetails;
import org.qualipso.factory.jabuti.WsCriterion;
import org.qualipso.factory.jabuti.persistence.Dao;
import org.qualipso.factory.jabuti.persistence.JabutiServiceProject;
import org.qualipso.factory.jabuti.persistence.ProjectStates;

import br.jabuti.criteria.AbstractCriterion;
import br.jabuti.criteria.Criterion;
import br.jabuti.lookup.Program;
import br.jabuti.probe.ProberInstrum;
import br.jabuti.project.ClassFile;
import br.jabuti.project.ClassMethod;
import br.jabuti.project.Coverage;
import br.jabuti.project.JabutiProject;
import br.jabuti.project.TestSet;

public class WsProject {
	
	Properties props = null;
	String JABUTI_PROJECT_HOME = null;
	
	public WsProject(Properties props) {
		this.props = props;
		JABUTI_PROJECT_HOME = props.getProperty("JABUTI_PERSISTENCE_HOME");
		if(!JABUTI_PROJECT_HOME.endsWith("/"))
			JABUTI_PROJECT_HOME += "/";
	}
	
	/**
	 * 
	 * @param projectname
	 * @param file
	 * @return
	 */
	public String[] create(String projectname, File file)
	{
		String ret[] = new String[2];
		
		String id = String.valueOf(System.nanoTime());
		File projdir = new File(JABUTI_PROJECT_HOME + id);
		projdir.mkdir();		
		if (!projdir.exists() || !projdir.isDirectory()) {
			System.out.println("Error while creating the Project directory: " + id);
		}
		else {
			System.out.println("Succesfully created the Project directory: " + id);
		}
				
		saveFile(file, JABUTI_PROJECT_HOME + id + "/file.jar");
		
		//save project name
		JabutiServiceProject jsp = new JabutiServiceProject();
		jsp.setProjid(id);
		jsp.setName(projectname);
		jsp.setState(ProjectStates.IDLE);
		Dao dao = new Dao(props);
		dao.insert(jsp);
		
		ret[0] = id;			//project id			
			
		return ret;
	}
	
	/**
	 * 
	 * @param projectId
	 * @param file
	 */
	public void update(String projectId, File file)
	{
		File projdir = new File(JABUTI_PROJECT_HOME + projectId);
		projdir.mkdir();
				
		saveFile(file, JABUTI_PROJECT_HOME + projectId + "/file.jar");		
	}
	
	public void delete(String projectId)
	{
		File projdir = new File(JABUTI_PROJECT_HOME + projectId);
		VerifingData.removeDir(projdir);
		projdir.delete();
		Dao dao = new Dao(props);
		dao.delete(projectId);
	}
	
	public void clean(String projectId)
	{
		//remove instrumented project
		File instrProj = new File(JABUTI_PROJECT_HOME + projectId+"/file_inst.jar");
		instrProj.delete();
		//remove trace file
		File traceFile = new File(JABUTI_PROJECT_HOME + projectId+"/proj.trc");
		traceFile.delete();
		//remove test cases
		File testCases = new File(JABUTI_PROJECT_HOME + projectId+"/file_test.jar");
		testCases.delete();
		//remove package with instrumented project, testcases and command lines
		File packagefile = new File(JABUTI_PROJECT_HOME + projectId + "/package.jar");
		packagefile.delete();	
		
		Dao dao = new Dao(props);
		JabutiServiceProject jsp = dao.get(projectId);
		jsp.setState(ProjectStates.IDLE);
		dao.update(jsp);
	}
	
	/**
	 * 
	 * @param id
	 * @param password
	 * @param classes
	 * @return
	 */
	public String[] selectClassesToInstrument(String projid, String classes[])
	{
		String ret[] = new String[1];
		String classpath = JABUTI_PROJECT_HOME + projid;
		String projfilename = classpath + "/proj.jbt";
		String instrumentedfilename = classpath + "/file_inst.jar";
		String projjarfilename = classpath + "/file.jar";
		
		//persistence
		Dao dao = new Dao(props);
		JabutiServiceProject jsp = dao.get(projid);
		
		try {
			ZipFile zippedFile = new JarFile(projjarfilename);
			Program program = new Program(zippedFile, true, null);

			HashSet toInstrument = getClassesByExpression(program, classes);
			if(jsp.getIgnoredclasses() != null)
			{
				ArrayList<String> toRemove = new ArrayList<String>();
				String ignoreclasses[] = jsp.getIgnoredclasses().split(",");
				HashSet toIgnore = getClassesByExpression(program, ignoreclasses);
				//edit toInstrument
				for (Iterator iterator  = toInstrument.iterator(); iterator.hasNext();) {
					String c = (String) iterator.next();
					if(toIgnore.contains(c))
						toRemove.add(c);
				}
				
				for (Iterator iterator = toRemove.iterator(); iterator.hasNext();) {
					String string = (String) iterator.next();
					toInstrument.remove(string);	
				}
				
			}
			
			JabutiProject jbtProject = new JabutiProject(program, classpath);
			jbtProject.setProjectFile( new File(projfilename) );
			
			//select instrument files
			Iterator it = toInstrument.iterator();
			while(it.hasNext())
			{
				String c = (String) it.next();
				jbtProject.addInstr(c);
			}
			
			jbtProject.rebuild();
			TestSet.initialize( jbtProject, jbtProject.getTraceFileName() );		
			ProberInstrum.instrumentProject(jbtProject, instrumentedfilename, props);
			jbtProject.saveProject();
			
			//persistence
			jsp.setSelectedclasses(genArrayToString(classes));
			jsp.setState(ProjectStates.READY);
			dao.update(jsp);
			
			ret[0] = "Classes are successfully instrumented.";
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ret[0] = "An error happened during the instrumentation process.";
		}
		return ret;
	}
	
	public void ignoreClasses(String projectId, String[] classes)
	{					
		//persistence
		Dao dao = new Dao(props);
		JabutiServiceProject jsp = dao.get(projectId);
		jsp.setIgnoredclasses(genArrayToString(classes));
		dao.update(jsp);
	}
	
//	public GraphDetails[] getGraph(String projectId, String[] classes)
//	{
//		String classpath = JABUTI_PROJECT_HOME + projectId;
//		String projjarfilename = classpath + "/file.jar";
//		
//		//persistence
//		Dao dao = new Dao(props);
//		JabutiServiceProject jsp = dao.get(projectId);
//		
//		try {
//			ZipFile zippedFile = new JarFile(projjarfilename);
//			Program program = new Program(zippedFile, true, null);
//
//			HashSet toGetGraphs = getClassesByExpression(program, classes);
//			
//			//how to generate the images?? - TODO
//			
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		
//		return null;
//	}
	
	public RequiredElementsDetails[] getAllRequiredElements(String projectId)
	{
		String projfilename = JABUTI_PROJECT_HOME + projectId + "/proj.jbt";
		JabutiProject jbtProject = JabutiProject.reloadProj(projfilename, true);
		
		ArrayList<RequiredElementsDetails> ret = new ArrayList<RequiredElementsDetails>();

		Program program = jbtProject.getProgram();
		String classes[] = program.getCodeClasses();
		for (int i = 0; i < classes.length; i++) {
			ClassFile cf = jbtProject.getClassFile(classes[i]);

			HashMap methods = cf.getMethodsTable();
			Object[] names = methods.keySet().toArray(new String[0]);
			Arrays.sort(names);

			for (int x = 0; x < names.length; x++) {
				String mName = (String) names[x];
				
				RequiredElementsDetails red = new RequiredElementsDetails();
				red.setMethodName(classes[i] + "." + mName);
				ClassMethod cm = cf.getMethod(mName);
				
				WsCriterion criteria[] = new WsCriterion[8];
				for (int j = 0; j < Criterion.NUM_CRITERIA; j++) {
					criteria[j] = new WsCriterion();
					criteria[j].setName(Criterion.names[j][0]);
					
					Criterion criterion = cm.getCriterion(j);
					Object[] requirements = criterion.getRequirements();
					String reqs[] = new String[requirements.length];
					for (int k = 0; k < requirements.length; k++) {
						reqs[k] = requirements[k].toString();
					}		
					criteria[j].setElements(reqs);
				}
				red.setCriterion(criteria);
				ret.add(red);
			}
		}		
		
		RequiredElementsDetails ret2[] = new RequiredElementsDetails[ret.size()];
		for (int i = 0; i < ret2.length; i++) {
			ret2[i] = ret.get(i);
		}
		
		return ret2;
	}
	
	public void addTestCases(String projectId, String testSuiteClass, File file) {
		String testjarfilename = JABUTI_PROJECT_HOME + projectId + "/file_test.jar";
		
		//save test case file
		saveFile(file,  testjarfilename);
		
		//save the TestSuite class name
		Dao dao = new Dao(props);
		JabutiServiceProject jsp = dao.get(projectId);
		jsp.setTestsuiteclassname(testSuiteClass);
		jsp.setState(ProjectStates.TESTCASESADDED);
		dao.update(jsp);
	}
	
	public InstrumentedProjectDetails getInstrumentedProject(String projectId)
	{
		Dao dao = new Dao(props);
		JabutiServiceProject jsp = dao.get(projectId);
		jsp.setState(ProjectStates.WAITINGEXECUTION);
		dao.update(jsp);
		
		InstrumentedProjectDetails ret = new InstrumentedProjectDetails();
		ret.setCommandLine("java -cp Jabuti-bin-2007-12-19.zip br.jabuti.junitexec.JUnitJabutiCore -trace test.trc -cp package.jar -tcClass " + jsp.getTestsuiteclassname());
		ret.setFile(createPackageFile(projectId));
		
		return ret;
	}
	
	private DataHandler createPackageFile(String id)
	{
		File packagefile = null;
		//to put the  necessary jabuti files to execute the testcases
		try {
			JarFile projinstfile = new JarFile(JABUTI_PROJECT_HOME + id + "/file_inst.jar");
			JarFile projtestfile = new JarFile(JABUTI_PROJECT_HOME + id + "/file_test.jar");
			packagefile = new File(JABUTI_PROJECT_HOME + id + "/package.jar");

			FileOutputStream fos = new FileOutputStream(packagefile);
			JarOutputStream jarOut = new JarOutputStream(fos);
			
			Enumeration<JarEntry> entries = projinstfile.entries();
			byte buffer[] = new byte[1];
			int bytesLidos;
			while(entries.hasMoreElements())
			{
				JarEntry entry = entries.nextElement();
				if(!entry.isDirectory()) {
					
					JarEntry entry2 = new JarEntry(entry.getName());
					
					jarOut.putNextEntry(entry2);
					InputStream is = projinstfile.getInputStream(entry);
					
					while( (bytesLidos = is.read( buffer )) > 0 ) {
						jarOut.write( buffer, 0, bytesLidos );
					}					
				}
			}
			
			entries = projtestfile.entries();
			while(entries.hasMoreElements())
			{ 
				JarEntry entry = entries.nextElement();
				if(!entry.isDirectory()) {
					
					JarEntry entry2 = new JarEntry(entry.getName()); 
					jarOut.putNextEntry(entry2);
					InputStream is = projtestfile.getInputStream(entry);
					while( (bytesLidos = is.read( buffer )) > 0 ) {
						jarOut.write( buffer, 0, bytesLidos );
					}		
				}
			}
			
			// Rafael
//			String projTomcatPath = props.getProperty("JABUTI_TOMCAT_PROJECT_HOME");
//  			if(!projTomcatPath.endsWith("/"))
//  				projTomcatPath += "/";
//  			
//  			projTomcatPath += "WEB-INF/lib";
			
			//mareler
			// Rafael
			
			File junitJar = getFileFromInputStream("junit.jar",
					getClass().getResourceAsStream("/junit.jar"));
			JarFile projJunit = new JarFile(junitJar);
			
			File junitExecJar = getFileFromInputStream("junitexec.jar",
					getClass().getResourceAsStream("/junitexec.jar"));
			JarFile projJunitExec = new JarFile(junitExecJar);
			
			File utilJar = getFileFromInputStream("util.jar",
					getClass().getResourceAsStream("/util.jar"));
			JarFile projUtil = new JarFile(utilJar);
			
			entries = projJunit.entries();	
			while(entries.hasMoreElements())
			{ 
				JarEntry entry = entries.nextElement();
				if(!entry.isDirectory()) {
					
					JarEntry entry2 = new JarEntry(entry.getName()); 
					jarOut.putNextEntry(entry2);
					InputStream is = projJunit.getInputStream(entry);
					while( (bytesLidos = is.read( buffer )) > 0 ) {
						jarOut.write( buffer, 0, bytesLidos );
					}		
				}
			}
			
			entries = projUtil.entries();	
			while(entries.hasMoreElements())
			{ 
				JarEntry entry = entries.nextElement();
				if(!entry.isDirectory()) {
					
					JarEntry entry2 = new JarEntry(entry.getName()); 
					jarOut.putNextEntry(entry2);
					InputStream is = projUtil.getInputStream(entry);
					while( (bytesLidos = is.read( buffer )) > 0 ) {
						jarOut.write( buffer, 0, bytesLidos );
					}		
				}
			}			
			
			entries = projJunitExec.entries();
			while(entries.hasMoreElements())
			{
				JarEntry entry = entries.nextElement();
				if(!entry.isDirectory()) {
					
					JarEntry entry2 = new JarEntry(entry.getName());
					
					jarOut.putNextEntry(entry2);
					InputStream is = projJunitExec.getInputStream(entry);
					
					while( (bytesLidos = is.read( buffer )) > 0 ) {
						jarOut.write( buffer, 0, bytesLidos );
					}					
				}
			}
			jarOut.close();
			fos.close();
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		FileDataSource fds = new FileDataSource(packagefile);
		DataHandler datahandler = new DataHandler(fds);
		
		return datahandler;
	}
	
	/**
	 * 
	 * @param id
	 * @param traceFile
	 */
	public void sendTraceFile(String id, File traceFile)
	{
		saveFile(traceFile, JABUTI_PROJECT_HOME + id + "/proj.trc");		
		//load the new coverage
		String projfilename = JABUTI_PROJECT_HOME + id + "/proj.jbt";
		JabutiProject jbtProject = JabutiProject.reloadProj(projfilename, true);	

		TestSet.loadTraceFile(jbtProject);
		TestSet.updateOverallCoverage( jbtProject );		
		
		//persistence
		Dao dao = new Dao(props);
		JabutiServiceProject jsp = dao.get(id);
		jsp.setState(ProjectStates.EXECUTED);
		dao.update(jsp);
		
		// Saving the updated project
		try {
			jbtProject.saveProject();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @param projectId
	 * @return
	 */
	public CoverageCriterionDetails[] getCoverageByCriteria(String projectId)
	{
		String projfile = JABUTI_PROJECT_HOME + projectId + "/proj.jbt";
		JabutiProject jbtProject = JabutiProject.reloadProj(projfile, true);
		
		CoverageCriterionDetails details[] = new CoverageCriterionDetails[Criterion.NUM_CRITERIA];
		for (int i = 0; i < Criterion.NUM_CRITERIA; i++) {
			details[i] = new CoverageCriterionDetails();
			details[i].setCriterionName(AbstractCriterion.getName(i));
			Coverage coverage = jbtProject.getProjectCoverage(i);
			details[i].setNumberOfElements(coverage.getNumberOfRequirements());
			details[i].setNumberOfCoveredElements(coverage.getNumberOfCovered());
			details[i].setCoveragePercentage(coverage.getPercentage());
		}
		return details;
	}
	
	/**
	 * 
	 * @param projectId
	 * @return
	 */
	public CoverageDetails[] getCoverageByClasses(String projectId)
	{
		String projfile = JABUTI_PROJECT_HOME + projectId + "/proj.jbt";
		JabutiProject jbtProject = JabutiProject.reloadProj(projfile, true);
		
		Hashtable allClasses = jbtProject.getClassFilesTable();
		Object[] classNames = allClasses.keySet().toArray();
		Arrays.sort(classNames);
		CoverageDetails details[] = new CoverageDetails[classNames.length];
		for (int i = 0; i < classNames.length; i++) {
			details[i] = new CoverageDetails();
			String cName = (String) classNames[i];
			ClassFile cf = (ClassFile) allClasses.get(cName);			
			
			details[i].setName(cName);
			
			CoverageCriterionDetails coverDetails[] = new CoverageCriterionDetails[Criterion.NUM_CRITERIA];
			for (int j = 0; j < Criterion.NUM_CRITERIA; j++) {
				coverDetails[j] = new CoverageCriterionDetails();
				Coverage cCov = cf.getClassFileCoverage(j);
				
				coverDetails[j].setCriterionName(AbstractCriterion.getName(j));
				coverDetails[j].setNumberOfElements(cCov.getNumberOfRequirements());
				coverDetails[j].setNumberOfCoveredElements(cCov.getNumberOfCovered());
				coverDetails[j].setCoveragePercentage(cCov.getPercentage());
			}			
			details[i].setCriteria(coverDetails);
		}
		
		return details;
	}
	
	/**
	 * 
	 * @param projectId
	 * @return
	 */
	public CoverageDetails[] getCoverageByMethods(String projectId) {
		String projfile = JABUTI_PROJECT_HOME + projectId + "/proj.jbt";
		JabutiProject jbtProject = JabutiProject.reloadProj(projfile, true);
		
		Hashtable allClasses = jbtProject.getClassFilesTable();
		Object[] classNames = allClasses.keySet().toArray();
		Arrays.sort(classNames);
		
		ArrayList<CoverageDetails> ret = new ArrayList<CoverageDetails>();
		
		for (int i = 0; i < classNames.length; i++) {
			
			String cName = (String) classNames[i];
			ClassFile cf = (ClassFile) allClasses.get(cName);			
			
			HashMap methods = cf.getMethodsTable();
			Object[] methodNames = methods.keySet().toArray(new String[0]);
			Arrays.sort(methodNames);			
			for (int j = 0; j < methodNames.length; j++) {
				String mName = (String) methodNames[j];
				ClassMethod cm = (ClassMethod) methods.get(mName);				

				CoverageDetails detail = new CoverageDetails();
				detail.setName(cName + "." + mName);

				CoverageCriterionDetails coverDetails[] = new CoverageCriterionDetails[Criterion.NUM_CRITERIA];
				for (int k = 0; k < Criterion.NUM_CRITERIA; k++) {
					coverDetails[k] = new CoverageCriterionDetails();
					Coverage mCov = cm.getClassMethodCoverage(k);
					coverDetails[k].setCriterionName(AbstractCriterion.getName(k));
					coverDetails[k].setNumberOfElements(mCov.getNumberOfRequirements());
					coverDetails[k].setNumberOfCoveredElements(mCov.getNumberOfCovered());
					coverDetails[k].setCoveragePercentage(mCov.getPercentage());
				}			
				
				detail.setCriteria(coverDetails);
				ret.add(detail);
			}			
		}
		
		CoverageDetails details[] = new CoverageDetails[ret.size()];
		for (int i = 0; i < details.length; i++) {
			details[i] = ret.get(i);
		}
		return details;
	}
	
	/**
	 * 
	 * @param projectId
	 * @param criterion
	 * @return
	 */
	public Method[] getRequiredElementsByCriterion(String projectId, String criterion) {
		String projfile = JABUTI_PROJECT_HOME + projectId + "/proj.jbt";
		JabutiProject jbtProject = JabutiProject.reloadProj(projfile, true);
		Hashtable allClasses = jbtProject.getClassFilesTable();
		Object[] classNames = allClasses.keySet().toArray();
		Arrays.sort(classNames);
		
		int criterionnumber = -1;
		for(int i = 0; i < Criterion.NUM_CRITERIA; i++)
		{
			if(Criterion.names[i][0].equals(criterion)) {
				criterionnumber = i;
				break;
			}
		}
		if(criterionnumber == -1)
		{
			//exception here
			System.out.println("error");
		}
		
		ArrayList<Method> ret = new ArrayList<Method>();

		for (int i = 0; i < classNames.length; i++) {
			
			String cName = (String) classNames[i];
			ClassFile cf = (ClassFile) allClasses.get(cName);			
			
			HashMap methods = cf.getMethodsTable();
			Object[] methodNames = methods.keySet().toArray(new String[0]);
			Arrays.sort(methodNames);			
			for (int j = 0; j < methodNames.length; j++) {
				String mName = (String) methodNames[j];
				ClassMethod cm = (ClassMethod) methods.get(mName);
				
				Method method = new Method();
				method.setName(cName + "." + mName);
				
				Criterion critdetails = cm.getCriterion(criterionnumber);
				Object requirements[] = critdetails.getRequirements();
				String requirements2[] = new String[requirements.length];
				for (int k = 0; k < requirements.length; k++) {
					requirements2[k] = requirements[k].toString();
				}
				
				method.setElements(requirements2);
				ret.add(method);
			}
		}
		
		Method ret1[] = new Method[ret.size()];
		for (int i = 0; i < ret1.length; i++) {
			ret1[i] = ret.get(i);
		}
		
		return ret1;
	}
	
	/**
	 * 
	 * @param projectId
	 * @return
	 */
	public MethodDetails[] getAllCoveredAndUncoveredRequiredElements(String projectId) {
		String projfile = JABUTI_PROJECT_HOME + projectId + "/proj.jbt";
		JabutiProject jbtProject = JabutiProject.reloadProj(projfile, true);
		Hashtable allClasses = jbtProject.getClassFilesTable();
		Object[] classNames = allClasses.keySet().toArray();
		Arrays.sort(classNames);
				
		ArrayList<MethodDetails> ret = new ArrayList<MethodDetails>();

		for (int i = 0; i < classNames.length; i++) {
			
			String cName = (String) classNames[i];
			ClassFile cf = (ClassFile) allClasses.get(cName);			
			
			HashMap methods = cf.getMethodsTable();
			Object[] methodNames = methods.keySet().toArray(new String[0]);
			Arrays.sort(methodNames);			
			for (int j = 0; j < methodNames.length; j++) {
				String mName = (String) methodNames[j];
				ClassMethod cm = (ClassMethod) methods.get(mName);
				
				MethodDetails methoddetails = new MethodDetails();
				methoddetails.setMethodName(cName + "." + mName);
				CriterionCoveredUncovered criteria[] = new CriterionCoveredUncovered[Criterion.NUM_CRITERIA];
				
				for(int k = 0; k < Criterion.NUM_CRITERIA; k++)
				{
					criteria[k] = new CriterionCoveredUncovered();
					criteria[k].setName(Criterion.names[k][0]);
					
					Criterion critdetails = cm.getCriterion(k);
					Object requirements[] = critdetails.getRequirements();
					HashSet covered = critdetails.getCoveredRequirements();
					ArrayList<String> coveredelem = new ArrayList<String>();
					ArrayList<String> uncoveredelem = new ArrayList<String>();
					
					for (int l = 0; l < requirements.length; l++) {
						if(covered.contains(requirements[l])) {	//covered
							coveredelem.add(requirements[l].toString());
						}
						else {	//uncovered
							uncoveredelem.add(requirements[l].toString());
						}
						
					}
					String temp[] = new String[coveredelem.size()];
					for (int l = 0; l < temp.length; l++)
						temp[l] = coveredelem.get(l);
					
					criteria[k].setCoveredElements(temp);
					
					temp = new String[uncoveredelem.size()];
					for (int l = 0; l < temp.length; l++)
						temp[l] = uncoveredelem.get(l);

					criteria[k].setUncoveredElements(temp);
				}
				methoddetails.setCriteria(criteria);
				ret.add(methoddetails);
			}
		}
		
		MethodDetails ret1[] = new MethodDetails[ret.size()];
		for (int i = 0; i < ret1.length; i++) {
			ret1[i] = ret.get(i);
		}
		
		return ret1;	
	}
	
	/**
	 * 
	 * @param projectId
	 * @param classes
	 * @return
	 * @throws ClassNotFoundFault
	 * @throws InvalidExpressionFault
	 */
//	public MetricResClass[] getMetrics(String projectId, String[] classes) 
//		throws ClassNotFoundFault, InvalidExpressionFault
//	{
//		String classpath = JABUTI_PROJECT_HOME + projectId;
//		String projjarfilename = classpath + "/file.jar";
//		MetricResClass ret[] = null;
//		
//		try {
//			ZipFile zippedFile = new JarFile(projjarfilename);
//			Program program = new Program(zippedFile, true, null);
//			String metricClasses[] = getClassesByExpressionS(program, classes);
//			Metrics metrics = new Metrics(program, (String[]) metricClasses);
//			ret = new MetricResClass[metricClasses.length];
//
//			for (int i = 0; i < metricClasses.length; i++) {
//				ret[i] = new MetricResClass();
//				ret[i].setName(metricClasses[i]);				
//				//add the metrics
//				OoMetric oometrics[] = new OoMetric[27];
//				
//				oometrics[0] = new OoMetric();
//				oometrics[0].setName("ANPM");
//				oometrics[0].setValue(metrics.anpm(metricClasses[i]));
//
//				oometrics[1] = new OoMetric();
//				oometrics[1].setName("AMZ_LOCM");
//				oometrics[1].setValue(metrics.amz_locm(metricClasses[i]));
//				
//				oometrics[2] = new OoMetric();
//				oometrics[2].setName("AMZ_SIZE");
//				oometrics[2].setValue(metrics.amz_size(metricClasses[i]));
//				
//				oometrics[3] = new OoMetric();
//				oometrics[3].setName("CBO");
//				oometrics[3].setValue(metrics.cbo(metricClasses[i]));
//				
//				oometrics[4] = new OoMetric();
//				oometrics[4].setName("CC_AVG");
//				oometrics[4].setValue(metrics.cc_avg(metricClasses[i]));
//
//				oometrics[5] = new OoMetric();
//				oometrics[5].setName("CC_MAX");
//				oometrics[5].setValue(metrics.cc_max(metricClasses[i]));
//
//				oometrics[6] = new OoMetric();
//				oometrics[6].setName("DIT");
//				oometrics[6].setValue(metrics.dit(metricClasses[i]));
//
//				oometrics[7] = new OoMetric();
//				oometrics[7].setName("LCOM");
//				oometrics[7].setValue(metrics.lcom(metricClasses[i]));
//
//				oometrics[8] = new OoMetric();
//				oometrics[8].setName("LCOM_2");
//				oometrics[8].setValue(metrics.lcom_2(metricClasses[i]));
//
//				oometrics[9] = new OoMetric();
//				oometrics[9].setName("LCOM_3");
//				oometrics[9].setValue(metrics.lcom_3(metricClasses[i]));
//
//				oometrics[10] = new OoMetric();
//				oometrics[10].setName("MNPM");
//				oometrics[10].setValue(metrics.mnpm(metricClasses[i]));
//
//				oometrics[11] = new OoMetric();
//				oometrics[11].setName("NCM");
//				oometrics[11].setValue(metrics.ncm(metricClasses[i]));
//
//				oometrics[12] = new OoMetric();
//				oometrics[12].setName("NCM_2");
//				oometrics[12].setValue(metrics.ncm_2(metricClasses[i]));
//
//				oometrics[13] = new OoMetric();
//				oometrics[13].setName("NCV");
//				oometrics[13].setValue(metrics.ncv(metricClasses[i]));
//
//				oometrics[14] = new OoMetric();
//				oometrics[14].setName("NII");
//				oometrics[14].setValue(metrics.nii(metricClasses[i]));
//
//				oometrics[15] = new OoMetric();
//				oometrics[15].setName("NIV");
//				oometrics[15].setValue(metrics.niv(metricClasses[i]));
//
//				oometrics[16] = new OoMetric();
//				oometrics[16].setName("NMAS");
//				oometrics[16].setValue(metrics.nmas(metricClasses[i]));
//
//				oometrics[17] = new OoMetric();
//				oometrics[17].setName("NMIS");
//				oometrics[17].setValue(metrics.nmis(metricClasses[i]));
//
//				oometrics[18] = new OoMetric();
//				oometrics[18].setName("NMOS");
//				oometrics[18].setValue(metrics.nmos(metricClasses[i]));
//
//				oometrics[19] = new OoMetric();
//				oometrics[19].setName("NOC");
//				oometrics[19].setValue(metrics.noc(metricClasses[i]));
//
//				oometrics[20] = new OoMetric();
//				oometrics[20].setName("NPIM");
//				oometrics[20].setValue(metrics.npim(metricClasses[i]));
//
//				oometrics[21] = new OoMetric();
//				oometrics[21].setName("RFC");
//				oometrics[21].setValue(metrics.rfc(metricClasses[i]));
//
//				oometrics[22] = new OoMetric();
//				oometrics[22].setName("SI");
//				oometrics[22].setValue(metrics.si(metricClasses[i]));
//
//				oometrics[23] = new OoMetric();
//				oometrics[23].setName("WMC_L");
//				oometrics[23].setValue(metrics.wmc_1(metricClasses[i]));
//
//				oometrics[24] = new OoMetric();
//				oometrics[24].setName("WMC_CC");
//				oometrics[24].setValue(metrics.wmc_cc(metricClasses[i]));
//
//				oometrics[25] = new OoMetric();
//				oometrics[25].setName("WMC_LOCM");
//				oometrics[25].setValue(metrics.wmc_locm(metricClasses[i]));
//
//				oometrics[26] = new OoMetric();
//				oometrics[26].setName("WMC_SIZE");
//				oometrics[26].setValue(metrics.wmc_size(metricClasses[i]));
//				
//				ret[i].setMetrics(oometrics);
//			}
//		} 
//		catch (IOException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			//launch exceptions
//			e.printStackTrace();
//		}
//		
//		return ret;
//	}
	
	/**********************Auxiliary methods**************************/
	/**
	 * 
	 * @param prog
	 * @param classes
	 * @return
	 */
	private HashSet getClassesByExpression(Program prog, String classes[]) throws Exception
	{
		HashSet set = new HashSet();
		if(classes.length <= 0)
			throw new Exception("No class especified.");
		
		String c[] = prog.getCodeClasses();
		
		for (int i = 0; i < classes.length; i++) {			
			if(classes[i].equals("*") && i == 0)		//all classes 
			{
				for (int j = 0; j < c.length; j++) {
					set.add(c[j]);
				}
				break;
			}
			else if(!AuxiliaryOperations.isValidClassString(classes[i])) 
			{
				throw new Exception("Invalid package or class name.");
			}
			else if(classes[i].endsWith(".*")) 			//specific package
			{		
				if(containsPackage(c, classes[i]))
				{
					String pack = classes[i].substring(0, classes[i].length() - 2);
					for (int j = 0; j < c.length; j++) {
						if(c[j].startsWith(pack))
							set.add(c[j]);
					}
				}
				else
					throw new Exception("A package does not exist in the project.");
			}
			else 										//a specific class
			{		
				if(containsClass(c, classes[i]))
					set.add(classes[i]);
				else
					throw new Exception("A class does not exist in the project.");
			}
		}
		
		//just print
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			String object = (String) iterator.next();
			System.out.println(object);
		}
		
		return set;
	}
	
	private String[] getClassesByExpressionS(Program prog, String classes[]) throws Exception
	{
		HashSet set = getClassesByExpression(prog, classes);
		String ret[] = new String[set.size()];
		int i = 0;
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			ret[i++] = (String) iterator.next();
		}
		return ret;
	}
	
	private String genArrayToString(String c[])
	{
		String ret = "";
		for (int i = 0; i < c.length; i++) {
			ret += c[i];
			
			if((i+1) != c.length)
				ret += ",";
		}
		return ret;
	}
	
	private boolean containsClass(String classes[], String c)
	{
		for (int i = 0; i < classes.length; i++) {
			if(c.equals(classes[i]))
				return true;
		}
		return false;
	}

	private boolean containsPackage(String classes[], String pack)
	{
		pack = pack.substring(0, pack.length() - 2);
		for (int i = 0; i < classes.length; i++) {
			if(classes[i].startsWith(pack))
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param file
	 * @param filename
	 */
	private void saveFile(File file, String filename) {
		File cfile = new File(filename);		
		//copy jar file to the right directory
		try {
			InputStream in = new FileInputStream(file);
	        OutputStream out = new FileOutputStream(cfile);   
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	        in.close();
	        out.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private File getFileFromInputStream(String name, InputStream in) {
		try {
			String prefix = name.split("\\.")[0];
			String suffix = "." + name.split("\\.")[1];
	        File file = File.createTempFile(prefix, suffix);
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }	        
	        out.close();
	        return file;
	    } catch (IOException e) {
	    	// ...
	    }
	    return null;
	}
	
	/****************************************************************************************************/
	//old methods
	/**
	 * 
	 * @param id
	 * @param password
	 * @param testfile
	 * @param testsuiteclassname
	 * @return
	 */
//	public String[] selectTestCases(String id, String password, File testfile, String testsuiteclassname)
//	{
//		String ret[] = new String[1];
//		if(verify(id, password)) {
//			String testjarfilename = S_Project.JABUTI_PROJECT_HOME + id + "/file_test.jar";
//			String instrumentedfilename = S_Project.JABUTI_PROJECT_HOME + id + "/file_inst.jar";			
//			String classpath = testjarfilename + File.pathSeparator + instrumentedfilename;
//			//It is necessary to include the external jar files used in the project.
//			classpath += File.pathSeparator + "/home/aendo/arquivos/QualipsoFolder/Eclipse/commonlib/commons-email-1.1/commons-email-1.1.jar";
//			String projfilename = S_Project.JABUTI_PROJECT_HOME + id + "/proj.jbt";
//			
//			//save test case file
//			saveFile(testfile,  testjarfilename);
//
//			//run test cases
//			HashMap<String, String> hm = null;
//			try {
//				hm = JUnitJabutiCore.runCollecting(classpath, testsuiteclassname, System.out);
//				Set<String> testSet = hm.keySet();
//				JabutiProject jbtProject = JabutiProject.reloadProj(projfilename, true);
//				JUnitJabutiCore.runInstrumenting(classpath, testsuiteclassname, jbtProject.getTraceFileName(), testSet, System.out);
//				
//				TestSet.loadTraceFile(jbtProject);
//				TestSet.updateOverallCoverage( jbtProject );
//				System.out.println(jbtProject.coverage2TXT(""));
//
//				// Saving the updated project
//				jbtProject.saveProject();
//				ret[0] = "Test cases are successfully included and executed.";
//			} 
//			catch (Exception e) {
//				e.printStackTrace();
//				ret[0] = "An error was found during the test cases execution.";
//			}
//		}
//		else
//			ret[0] = "Invalid id and password. No test cases were added.";
//		
//		return ret;
//	}
	
	/**
	 * 
	 * @param id
	 * @param password
	 * @return
	 */
//	private boolean verify(String id, String password)
//	{
//		if(!id.equals("")) {
//			File projdir = new File(S_Project.JABUTI_PROJECT_HOME + id);
//			return projdir.exists();
//		}
//		return false;
//	}	
}
