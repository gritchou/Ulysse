package org.qualipso.factory.jabuti.test;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.jabuti.CoverageCriterionDetails;
import org.qualipso.factory.jabuti.CoverageDetails;
import org.qualipso.factory.jabuti.InstrumentedProjectDetails;
import org.qualipso.factory.jabuti.JabutiService;
import org.qualipso.factory.jabuti.JabutiServiceBean;
import org.qualipso.factory.jabuti.Method;
import org.qualipso.factory.jabuti.MethodDetails;
import org.qualipso.factory.jabuti.RequiredElementsDetails;
import org.qualipso.factory.jabuti.WsCriterion;

import com.bm.testsuite.BaseSessionBeanFixture;

/**
 * @author 
 * @date 
 */
public class JabutiServiceTest extends BaseSessionBeanFixture<JabutiServiceBean> {
    
	private static Log logger = LogFactory.getLog(JabutiServiceTest.class);
    
	@SuppressWarnings("unchecked")
	private static final Class[] usedBeans = { };
	
	private static String projectId = "";
	private JabutiService jabutiService;
	
    public JabutiServiceTest() {
    	super(JabutiServiceBean.class, usedBeans);
    }
    
    @BeforeClass
    public void setUp() throws Exception {
		super.setUp();
		jabutiService = new JabutiServiceBean();
//		((JabutiServiceBean)jabutiService).init();
	}
    
    @Test
    public void testCreateProject() {    	
    	try {    		    		
            File file = new File(getClass().getResource("/vending.jar")
            		.getPath());
            byte[] fileBytes = new byte[(int)file.length()];
            new FileInputStream(file).read(fileBytes);
            projectId = jabutiService.createProject("SA", "vending", 
            		fileBytes);            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testUpdateProject() {
    	try {    		
    		testCreateProject();
    		File file = new File(getClass().getResource("/vending2.jar")
            		.getPath());                          
    		byte[] fileBytes = new byte[(int)file.length()];
            new FileInputStream(file).read(fileBytes);         
            String msg = jabutiService.updateProject("SA", projectId, 
            		fileBytes);
            assertEquals(msg, "project updated.");
    	} catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testDeleteProject() {
    	try {    		
    		testCreateProject();
            String msg = jabutiService.deleteProject("SA", projectId);
            assertEquals(msg, "The project was removed.");
    	} catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testCleanProject() {
    	try {    		
    		testCreateProject();
            String msg = jabutiService.cleanProject("SA", projectId);
            assertEquals(msg, "The project was cleaned.");
    	} catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testIgnoreClasses() {
    	try {    		
    		testCreateProject();
    		String[] classes = new String[]{"Dispenser", "TestDriver"};
            String msg = jabutiService.ignoreClasses("SA", projectId, classes);
            assertEquals(msg, "the ignored classes were recorded.");
    	} catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testSelectClasses() {
    	try {    		
    		testCreateProject();
    		String[] classes = new String[]{"*"};
            String msg = jabutiService.selectClassesToInstrument("SA", 
            		projectId, classes);
            assertEquals(msg, "The classes were instrumented.");
    	} catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testGetAllRequiredElements() {
    	try {   		
    		testSelectClasses();
    		RequiredElementsDetails[] details = 
    				jabutiService.getAllRequiredElements("SA", projectId);
    		for (int i = 0; i < details.length; i++) {
				System.out.println("--method: " + details[i].getMethodName());
				WsCriterion c[] = details[i].getCriterion();
				for (int j = 0; j < c.length; j++) {
					System.out.println("----Criterion: " + c[j].getName());
					System.out.print("----Elements: ");
					String elems[] = c[j].getElements();
					if(elems != null)
						for (int j2 = 0; j2 < elems.length; j2++) {
							System.out.print(elems[j2] + ", ");
						}
					System.out.println();
				}
			}	
    	} catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testGetRequiredElementsByCriterion() {
    	logger.info("testGetRequiredElementsByCriterion()");
    	try {    		
    		testSelectClasses();
    		Method[] methods = 
    				jabutiService.getRequiredElementsByCriterion(
    						"SA", projectId, "All-Nodes-ei");
    		
    		for (Method m : methods) {
    			System.out.println(m.getName());
    		}
    		
    	} catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testAddTestCases() {
    	logger.info("testAddTestCases()");
    	try {    		
    		testSelectClasses();    		
    		String testSuiteClass = "vending.DispenserTestCase";
    		File file = new File(getClass().getResource("/vending_test.jar")
    				.getPath());
    		byte[] fileBytes = new byte[(int)file.length()];
            new FileInputStream(file).read(fileBytes);
			String msg = jabutiService.addTestCases(
					"SA", projectId, testSuiteClass, fileBytes);
			assertEquals(msg, "Test Case file was added.");			
    	} catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testGetInstrumentedProject() {
    	logger.info("testGetInstrumentedProject()");
    	try {    		  
    		testAddTestCases();
    		InstrumentedProjectDetails instrProject =
    			jabutiService.getInstrumentedProject("SA", projectId);
    	} catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testSendTraceFile() {
    	logger.info("testSendTraceFile()");
    	try {    		
    		testSelectClasses();    	
    		File file = new File(getClass().getResource("/vending.trc")
    				.getPath());
    		byte[] fileBytes = new byte[(int)file.length()];
            new FileInputStream(file).read(fileBytes);
    		String msg = jabutiService.sendTraceFile("SA", projectId, 
    				fileBytes);
    		assertEquals(msg, "ok");
    	} catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testGetCoverageByCriteria() {
    	logger.info("testGetCoverageByCriteria()");
    	try {
    		testSendTraceFile();
    		CoverageCriterionDetails[] details =
    				jabutiService.getCoverageByCriteria("SA", projectId);
    		for (int i = 0; i < details.length; i++) {
				System.out.println(details[i].getCriterionName());
				System.out.println(details[i].getNumberOfCoveredElements() + 
						" of " + details[i].getNumberOfElements() + 
						" (" + details[i].getCoveragePercentage() + "%)");
			}
    	} catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testGetCoverageByClasses() {
    	logger.info("testGetCoverageByClasses()");
    	try {
    		testSendTraceFile();
    		CoverageDetails[] details =
    				jabutiService.getCoverageByClasses("SA", projectId);
    		for (int i = 0; i < details.length; i++) {
				System.out.println(details[i].getName());				
			}
    	} catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testGetCoverageByMethods() {
    	logger.info("testGetCoverageByMethods()");
    	try {
    		testSendTraceFile();
    		CoverageDetails[] details =
    				jabutiService.getCoverageByClasses("SA", projectId);
    		for (int i = 0; i < details.length; i++) {
				System.out.println(details[i].getName());				
			}
    	} catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testGetAllCoveredUncoveredReqElements() {
    	logger.info("testGetAllCoveredUncoveredReqElements()");
    	try {
    		testSendTraceFile();
    		MethodDetails[] details =
    				jabutiService.getAllCoveredAndUncoveredRequiredElements(
    						"SA", projectId);
    		for (int i = 0; i < details.length; i++) {
				System.out.println(details[i].getMethodName());				
			}
    	} catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @After
    public void tearDown() {
    	// TODO limpar todos os projetos criados
    	// Não dá pra deletar direto; delete() só funciona com dir. vazio
    	File folder = new File("jabuti");
    	for (File f : folder.listFiles()) {
    		if (f.isDirectory()) {
    			for (File f2 : folder.listFiles()) {
    				f2.delete();
    			}
    		}
    		f.delete();
    	}
    	folder.delete();
    }
}
