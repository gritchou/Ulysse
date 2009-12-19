package org.qualipso.factory.workflow.bonita.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ws.core.StubExt;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.qualipso.factory.client.ws.Bonita;
import org.qualipso.factory.client.ws.Bootstrap;
import org.qualipso.factory.client.ws.BootstrapServiceException_Exception;
import org.qualipso.factory.client.ws.Bootstrap_Service;
import org.qualipso.factory.client.ws.Project;
import org.qualipso.factory.client.ws.ProjectServiceException_Exception;
import org.qualipso.factory.client.ws.Project_Service;
import org.qualipso.factory.client.ws.Workflow;
import org.qualipso.factory.client.ws.Workflow_Service;

/**
 * @author Emmanuel Rias (emmanuel.rias@bull.net)
 * @date 22 june 2009
 */
public class BonitaServiceTest {

	private static Log logger = LogFactory.getLog(BonitaServiceTest.class);

	private Project project;
	private Workflow workflow;

	public BonitaServiceTest() {
		project = new Project_Service().getProjectServiceBeanPort();
		workflow = new Workflow_Service().getBonitaServiceBeanPort();
	}

	@Test
	public void testStartWorkflow() {
		logger.debug("testing startWorkflow(...)");

//		try {
//			((StubExt) workflow).setConfigName("Standard WSSecurity Client");
//			Map<String, Object> reqContext = ((BindingProvider) workflow)
//					.getRequestContext();
//			reqContext.put(StubExt.PROPERTY_AUTH_TYPE,
//					StubExt.PROPERTY_AUTH_TYPE_WSSE);
//			reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
//			reqContext.put(BindingProvider.PASSWORD_PROPERTY, "root");
//
//			try {
//				workflow.deployProjectWorkflow();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			workflow.instantiateProjectWorkflow();
//			List<Bonita> bonitas = workflow.getTasksReadyForProfile(
//					"/profiles/root").getItem();
//			for (int i = 0; i < bonitas.size(); i++) {
//				Bonita bonita = bonitas.get(i);
//				System.out.println("Bonita = " + bonita.getDateOfCreation());
//				System.out.println("Bonita = " + bonita.getInitiator());
//				System.out.println("Bonita = " + bonita.getTask());
//				System.out.println("Bonita = " + bonita.getTaskState());
//				System.out.println("Bonita = " + bonita.isProjectValidated());
//				callBonitaTask(bonita);
//			}
//
//			assertEquals(true, true);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		}
		
		// Add unit test there
		assertEquals(true, true); 
	}

	public void callBonitaTask(Bonita bonita) {
		if (bonita.getTask().equals("CreateProject")
				&& bonita.getTaskState().equals("READY")) {
			System.out.println("Bonita = " + bonita.getDateOfCreation());
			System.out.println("Bonita = " + bonita.getInitiator());
			System.out.println("Bonita = " + bonita.getTask());
			System.out.println("Bonita = " + bonita.getTaskState());
			System.out.println("Bonita = " + bonita.isProjectValidated());
			workflow.performTaskCreateProject("/profiles/root/testworkflowTF",
					"Test", "Summary", "Licence", bonita);
		} else if (bonita.getTask().equals("ProjectValidation")
				&& bonita.getTaskState().equals("READY")) {
			System.out.println("Bonita = " + bonita.getDateOfCreation());
			System.out.println("Bonita = " + bonita.getInitiator());
			System.out.println("Bonita = " + bonita.getTask());
			System.out.println("Bonita = " + bonita.getTaskState());
			System.out.println("Bonita = " + bonita.isProjectValidated());
			workflow.performTaskValidateProject(true, bonita);
		}

	}

	@BeforeClass
	public static void init() {
		try {
			Bootstrap bootstrap = new Bootstrap_Service()
					.getBootstrapServiceBeanPort();
			((StubExt) bootstrap).setConfigName("Standard WSSecurity Client");
			Map<String, Object> reqContext = ((BindingProvider) bootstrap)
					.getRequestContext();
			reqContext.put(StubExt.PROPERTY_AUTH_TYPE,
					StubExt.PROPERTY_AUTH_TYPE_WSSE);
			reqContext.put(BindingProvider.USERNAME_PROPERTY, "root");
			reqContext.put(BindingProvider.PASSWORD_PROPERTY, "root");

			bootstrap.bootstrap();

		} catch (BootstrapServiceException_Exception e) {
			logger.error("unable to bootstrap factory", e);
		}
	}

	@Before
	public void setup() throws ProjectServiceException_Exception {
	}

}