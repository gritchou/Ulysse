package org.qualipso.factory.workflow;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.DeploymentException;
import org.ow2.bonita.facade.exception.IllegalTaskStateException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.PackageNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.TaskNotFoundException;
import org.ow2.bonita.facade.exception.UndeletableInstanceException;
import org.ow2.bonita.facade.exception.UndeletablePackageException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.runtime.var.Enumeration;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.PackageDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.facade.uuid.TaskUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BonitaException;
import org.ow2.bonita.util.Misc;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathAlreadyBoundException;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.notification.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.AccessDeniedException;
import org.qualipso.factory.security.pep.PEPService;
import org.qualipso.factory.workflow.entity.Bonita;
import org.qualipso.factory.workflow.hook.ProcessNVUtil;

import sun.security.acl.PrincipalImpl;

@SuppressWarnings("serial")
@Stateless(name = BonitaService.SERVICE_NAME, mappedName = FactoryNamingConvention.SERVICE_PREFIX + BonitaService.SERVICE_NAME)
@WebService(endpointInterface = "org.qualipso.factory.workflow.BonitaService", targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + BonitaService.SERVICE_NAME, serviceName = BonitaService.SERVICE_NAME)
@WebContext(contextRoot = "/" + BonitaService.SERVICE_NAME, urlPattern = FactoryNamingConvention.WEB_SERVICE_URL_PATTERN_PREFIX + BonitaService.SERVICE_NAME)
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class BonitaServiceBean implements BonitaService {
	private static final String[] RESOURCE_TYPE_LIST = new String[] { "Bonita" };
	private static Log logger = LogFactory.getLog(BonitaServiceBean.class);

	private BindingService binding;
	private MembershipService membership;
	private PEPService pep;
	private PAPService pap;
	private NotificationService notification;
	private SessionContext ctx;
	private EntityManager em;

	
	public static String PackageId = "ProjectCreation";
	public static String ProcessId = "ProjectCreation";
	LoginContext loginContext = null;

	public void initBonita() {
//		String jaasLoginProp = "java.security.auth.login.config";
//		String envProp = GlobalEnvironmentFactory.DEFAULT_ENVIRONMENT;
////		System.out.println("Env prop = " + envProp);
////		System.out.println("Setting Jaas Login property... ");
////		if (System.getProperty(jaasLoginProp) == null) {
////			String defaultJaasLogin = this.getClass().getClassLoader()
////					.getResource("jaas-standard.cfg").getPath();
////			System.setProperty(jaasLoginProp, defaultJaasLogin);
////			System.out.println("No jaas login property (" + jaasLoginProp
////					+ ") has been defined. Using by default : "
////					+ defaultJaasLogin + "");
////		}
//		System.setProperty("org.ow2.bonita.api-type", "Standard"); 
//		System.out.println("Setting Environment property...");
//		if (System.getProperty(envProp) == null) {
//			String defaultEnv = this.getClass().getClassLoader().getResource(
//			"bonita-environment.xml").getPath();
//
//			System.setProperty(envProp, defaultEnv);
//			System.out.println("No environment property (" + envProp
//					+ ") has been defined. Using by default : " + defaultEnv
//					+ "</br>");
//		}
//		try {
//			System.out.println("Creating login context...");
//			loginContext = new LoginContext("JBossWSDigest",
//					new SimpleCallbackHandler("root","root"));
//			System.out.println("Login context created.");
//			System.out.println("Login...");
//			loginContext.login();
//			System.out.println("Login success = " + loginContext.getSubject());
//			Subject subject = org.ow2.bonita.identity.auth.SecurityContext.getSubject();
//			System.out.println("Subject = " + subject);
//		} catch (LoginException e) {
//			e.printStackTrace();
//		}
		loginBonita("root", "root");
	}

	public void logoutBonita() {
		try {
			System.out.println("Retrieve login context...");
			System.out.println("Login context retrevied.");
			System.out.println("Logout...");
			loginContext.logout();
			System.out.println("Logout success");
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	public boolean loginBonita(String login, String password) {
//		String jaasLoginProp = "java.security.auth.login.config";
//		String envProp = GlobalEnvironmentFactory.DEFAULT_ENVIRONMENT;
//		
//		System.out.println("Setting Jaas Login property... ");
//		if (System.getProperty(jaasLoginProp) == null) {
//			String defaultJaasLogin = this.getClass().getClassLoader()
//					.getResource("jaas-standard.cfg").getPath();
//			System.setProperty(jaasLoginProp, defaultJaasLogin);
//			System.out.println("No jaas login property (" + jaasLoginProp
//					+ ") has been defined. Using by default : "
//					+ defaultJaasLogin + "");
//		}
//		System.setProperty("org.ow2.bonita.api-type", "EJB3"); 
//		org.qualipso.factory.workflow.bonita.security.SecurityContext.setUser("root");
//		System.out.println("Setting Environment property...");
//		if (System.getProperty(envProp) == null) {
//			String defaultEnv = this.getClass().getClassLoader().getResource(
//					"bonita-environment.xml").getPath();
//
//			System.setProperty(envProp, defaultEnv);
//			System.out.println("No environment property (" + envProp
//					+ ") has been defined. Using by default : " + defaultEnv
//					+ "</br>");
//		}
		try {
			System.out.println("Creating login context...");
			Principal principal = new PrincipalImpl( membership.getProfilePathForConnectedIdentifier() );
			Set<Principal> principalsSet = new HashSet();
			principalsSet.add(principal);
			System.out.println("Login = " + membership.getProfilePathForConnectedIdentifier());
			Subject subject = new Subject(true, principalsSet, principalsSet, principalsSet);
			
//			loginContext = new LoginContext("JBossWSDigest",
//					new SimpleCallbackHandler("/profiles/root","root"));
			loginContext = new LoginContext("Bonita", subject);
//			new SimpleCallbackHandler("/profiles/root","root"));

			//						new StandardCallbackHandler());
			System.out.println("Login context created.");
			System.out.println("Login...");
			loginContext.login();
			System.out.println("Login success");
			
			return true;
		} catch (LoginException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void deployProjectWorkflow() {
		InputStream barFile2 = this.getClass().getClassLoader().getResourceAsStream(
				"qualipsoworkflow.bar");
		
		try {
		    String caller = membership.getProfilePathForConnectedIdentifier();
		    System.out.println("Caller = " + caller);
		    //pep.checkSecurity(caller, "/profiles/root/testworlflow", "create");
			System.out.println("Bar file = " + barFile2);
			initBonita();
			AccessorUtil.getManagementAPI().deployBar(
					org.ow2.bonita.util.Misc.getAllContentFrom(barFile2));
			System.out.println(("Procces Qualipso project was succesully deployed"));
		} catch (DeploymentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unDeployProjectWorkflow() {
		//initBonita();
		loginBonita("root", "root");
		ProcessDefinition process = null;
		try {
			System.out.println("Getting Process: PackageID["
					+ BonitaServiceBean.PackageId + "], ProcessID["
					+ BonitaServiceBean.ProcessId + "]");
			QueryDefinitionAPI qda = AccessorUtil.getQueryDefinitionAPI();
			System.out.println("Query definition");
			process = AccessorUtil.getQueryDefinitionAPI().getLastProcess(
					PackageId, ProcessId);
			System.out.println("Done");
			if (!process.equals(null)) {
				PackageDefinitionUUID packageUUID = process
						.getPackageDefinitionUUID();
				ProcessDefinitionUUID processUUID = process
						.getProcessDefinitionUUID();
				System.out.println("Deleting all Process ...");
				AccessorUtil.getRuntimeAPI().deleteAllProcessInstances(
						processUUID);
				System.out.println("Done");
				System.out.println("Undeploying Process ...");
				AccessorUtil.getManagementAPI().undeploy(packageUUID);
				AccessorUtil.getManagementAPI().deletePackage(packageUUID);
				System.out
						.println(("Package with UUID " + packageUUID + "  was succesully undeployed and deleted>"));
			}
		} catch (ProcessNotFoundException e) {
			System.out.println("ProcessNotFound");
			// e.printStackTrace();
		} catch (DeploymentException e) {
			e.printStackTrace();
		} catch (PackageNotFoundException e) {
			System.out.println("PackageNotFound");
			// e.printStackTrace();
		} catch (UndeletablePackageException e) {
			e.printStackTrace();
		} catch (UndeletableInstanceException e) {
			e.printStackTrace();
		}
	}

//	public void instantiateProjectWorkflow(HashMap<String, Object> variables) {
//		ProcessDefinition process = null;
//
//		initBonita();
//
//		try {
//			// System.out.println("Dans le try de recuperation du procces");
//			process = AccessorUtil.getQueryDefinitionAPI().getLastProcess(
//					PackageId, ProcessId);
//		} catch (ProcessNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		// System.out.println("ici on a la definition de
//		// process!!!!!!!!!!!"+process);
//		ProcessInstanceUUID instanceUUID = null;
//		final PackageDefinitionUUID packageUUID = process
//				.getPackageDefinitionUUID();
//		Misc.badStateIfNull(packageUUID, "packageUUID is null in process : "
//				+ process.getProcessId());
//		try {
//			System.out.println("Instantiate Process[" + ProcessId + "].");
//			instanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(
//					process.getUUID());
//			this.setProcessInstanceVariables(variables, instanceUUID);
//			// System.out.println("ici on a la definition de l'instance
//			// crée!!!!!!!!!!!"+instanceUUID);
//			System.out.println("Done Instance[" + instanceUUID + "]");
//		} catch (BonitaException e) {
//			e.printStackTrace();
//		}
//	}

	public ProcessInstanceUUID instantiateProjectWorkflow(String path) throws AccessDeniedException, PathAlreadyBoundException, InvalidPathException, BonitaServiceException {
		ProcessDefinition process = null;

		initBonita();

		try {
			// System.out.println("Dans le try de recuperation du procces");
			process = AccessorUtil.getQueryDefinitionAPI().getLastProcess(
					PackageId, ProcessId);
		} catch (ProcessNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// System.out.println("ici on a la definition de
		// process!!!!!!!!!!!"+process);
		ProcessInstanceUUID instanceUUID = null;
		final PackageDefinitionUUID packageUUID = process
				.getPackageDefinitionUUID();
		Misc.badStateIfNull(packageUUID, "packageUUID is null in process : "
				+ process.getProcessId());
		try {
			System.out.println("Instantiate Process[" + ProcessId + "].");
			instanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(
					process.getUUID());
			// System.out.println("ici on a la definition de l'instance
			// crée!!!!!!!!!!!"+instanceUUID);
			// service orchestration
			Bonita bonita = new Bonita();
			bonita.setInstanceUUID(instanceUUID.toString());
			bonita.setId(instanceUUID.toString());

			pep.checkSecurity(membership.getConnectedIdentifierSubjects(), PathHelper.getParentPath(path), "create");
			String caller = membership.getProfilePathForConnectedIdentifier();
			binding.bind(bonita.getFactoryResourceIdentifier(), path);
			binding.setProperty(path, FactoryResourceProperty.CREATION_TIMESTAMP, System.currentTimeMillis() + "");
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			binding.setProperty(path, FactoryResourceProperty.AUTHOR, caller);

			// create default policy
			String policyId = UUID.randomUUID().toString();
			pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, caller, path));
			binding.setProperty(path, FactoryResourceProperty.OWNER, caller);
			binding.setProperty(path, FactoryResourceProperty.POLICY_ID, policyId);

			notification.throwEvent(new Event(path, caller, BonitaService.SERVICE_NAME, Event.buildEventType(BonitaService.SERVICE_NAME, Bonita.RESOURCE_NAME, "create"), ""));

			System.out.println("Done Instance[" + instanceUUID + "]");
			return instanceUUID;
		} catch (AccessDeniedException e2) {
			ctx.setRollbackOnly();
			throw e2;
		} catch (InvalidPathException e3) {
			ctx.setRollbackOnly();
			throw e3;
		} catch (PathAlreadyBoundException e4) {
			ctx.setRollbackOnly();
			throw e4;
		} catch (BonitaException e5) {
			e5.printStackTrace();
		} catch (Exception e) {
			ctx.setRollbackOnly();
			throw new BonitaServiceException("unable to instanciate the workflow at path: " + path, e);

		}		
		return null;
	}

	public void instantiateAndPerform(String path, String name, String summary, String licence) throws AccessDeniedException, PathAlreadyBoundException, InvalidPathException, BonitaServiceException {
		ProcessDefinition process = null;

		initBonita();

		try {
			// System.out.println("Dans le try de recuperation du procces");
			process = AccessorUtil.getQueryDefinitionAPI().getLastProcess(
					PackageId, ProcessId);
		} catch (ProcessNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// System.out.println("ici on a la definition de
		// process!!!!!!!!!!!"+process);
		ProcessInstanceUUID instanceUUID = null;
		final PackageDefinitionUUID packageUUID = process
				.getPackageDefinitionUUID();
		Misc.badStateIfNull(packageUUID, "packageUUID is null in process : "
				+ process.getProcessId());
		try {
			System.out.println("Instantiate Process[" + ProcessId + "].");
			instanceUUID = AccessorUtil.getRuntimeAPI().instantiateProcess(
					process.getUUID());
			System.out.println("Done Instance[" + instanceUUID + "]");
			Bonita[] bonitas = this.getTasksReadyForProfile(membership.getProfilePathForConnectedIdentifier());
			for(int i=0; i<bonitas.length; i++) {
				Bonita bonita = (Bonita) bonitas[i];
				if (bonita.getInstanceUUID().equals(
						instanceUUID.toString())) {
					this.performTaskCreateProject(path, name, summary, licence, bonita);
				}
			}

		} catch (BonitaException e) {
			e.printStackTrace();
		}
	}

	public void deleteInstance(Bonita instantiateResult) {
		initBonita();
		ProcessInstanceUUID processInstanceUUID;
		processInstanceUUID = getInstanceUUIDFromString(instantiateResult
				.getInstanceUUID());
		try {
			AccessorUtil.getRuntimeAPI().deleteProcessInstance(
					processInstanceUUID);
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UndeletableInstanceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setProcessInstanceActivityCreateProject(String path, String name, String summary, String licence,
			Bonita bonita) {
		initBonita();
		ProcessInstanceUUID processInstanceUUID;
		processInstanceUUID = getInstanceUUIDFromString(bonita
				.getInstanceUUID());
		ActivityInstanceUUID activityInstanceUUID;
		activityInstanceUUID = getActivityInstanceUUIDFromString(bonita);
		try {
			AccessorUtil.getRuntimeAPI()
			.setProcessInstanceVariable(
					processInstanceUUID, ProcessNVUtil.PATH,
					path);
			AccessorUtil.getRuntimeAPI()
			.setProcessInstanceVariable(
					processInstanceUUID, ProcessNVUtil.LICENCE,
					licence);
			AccessorUtil.getRuntimeAPI()
			.setProcessInstanceVariable(
					processInstanceUUID, ProcessNVUtil.NAME,
					name);
			AccessorUtil.getRuntimeAPI()
			.setProcessInstanceVariable(
					processInstanceUUID, ProcessNVUtil.SUMMARY,
					summary);
		} catch (VariableNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setProcessInstanceActivityValidateProject(Boolean validate,
			Bonita bonita) {
		initBonita();
		ProcessInstanceUUID processInstanceUUID;
		processInstanceUUID = getInstanceUUIDFromString(bonita
				.getInstanceUUID());
		ActivityInstanceUUID activityInstanceUUID;
		activityInstanceUUID = getActivityInstanceUUIDFromString(bonita);
		try {
			AccessorUtil.getRuntimeAPI()
			.setProcessInstanceVariable(
					processInstanceUUID, ProcessNVUtil.PROJECTVALIDATED,
					validate);
		} catch (VariableNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void setProcessInstanceVariables(HashMap<String, Object> variables,
			ProcessInstanceUUID processInstanceUUID) {
		initBonita();
		for (Iterator<String> i = variables.keySet().iterator(); i.hasNext();) {
			String temp = i.next();

			try {
				if (variables.get(temp) instanceof String) {
					// System.out.println(temp+ " est de type String");
					if (temp.equals("requestType")
							|| temp.equals("requestProduction")
							|| temp.equals("requestPriority")) {
						Enumeration enumTemp = (Enumeration) AccessorUtil
								.getQueryRuntimeAPI()
								.getProcessInstanceVariable(
										processInstanceUUID, temp);
						Enumeration enumTemp1 = new Enumeration(enumTemp
								.getPossibleValues(), (String) variables
								.get(temp));
						System.out.println("Creation du nouveau Enumeration: ("
								+ enumTemp.getPossibleValues() + "   "
								+ (String) variables.get(temp) + ")");
						AccessorUtil.getRuntimeAPI()
								.setProcessInstanceVariable(
										processInstanceUUID, temp, enumTemp1);
					} else {
						// System.out.println("Nouvelle instanciation de varibal
						// process. Varibles: " +temp+"Valeur: "+ (String)
						// variables.get(temp));
						AccessorUtil.getRuntimeAPI()
								.setProcessInstanceVariable(
										processInstanceUUID, temp,
										(String) variables.get(temp));
					}
				} else if (variables.get(temp) instanceof Date) {
					// System.out.println(temp+ " est de type Date");
					AccessorUtil.getRuntimeAPI().setProcessInstanceVariable(
							processInstanceUUID, temp,
							(Date) variables.get(temp));
				}
			} catch (InstanceNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (VariableNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void initBrouillonVariables(Bonita instantiateResult) {
		initBonita();
		Map<String, Object> mapProcessInstanceVariables = new HashMap<String, Object>();
		ProcessInstanceUUID processInstanceUUID;
		processInstanceUUID = getInstanceUUIDFromString(instantiateResult
				.getInstanceUUID());
		ActivityInstanceUUID activityInstanceUUID;
		activityInstanceUUID = getActivityInstanceUUIDFromString(instantiateResult);
		try {
			mapProcessInstanceVariables = AccessorUtil.getQueryRuntimeAPI()
					.getProcessInstanceVariables(processInstanceUUID);
			if (instantiateResult.getTask().equals("Request")) {
				if (!(mapProcessInstanceVariables.get("projectEntity") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID, "projectEntityRequest",
							mapProcessInstanceVariables.get("projectEntity"));
				}
				if (!(mapProcessInstanceVariables.get("projectName") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID, "projectNameRequest",
							mapProcessInstanceVariables.get("projectName"));
				}
				if (!(mapProcessInstanceVariables.get("requestTitle") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID, "requestTitleRequest",
							mapProcessInstanceVariables.get("requestTitle"));
				}
				if (!(mapProcessInstanceVariables.get("initiatorName") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID, "initiatorNameRequest",
							mapProcessInstanceVariables.get("initiatorName"));
				}
				if (!(mapProcessInstanceVariables.get("userAcceptance") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID, "userAcceptanceRequest",
							mapProcessInstanceVariables.get("userAcceptance"));
				}
				if (!(mapProcessInstanceVariables.get("initiatorReference") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID,
							"initiatorReferenceRequest",
							mapProcessInstanceVariables
									.get("initiatorReference"));
				}
				if (!(mapProcessInstanceVariables.get("requestProduction") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID,
							"requestProductionRequest",
							mapProcessInstanceVariables
									.get("requestProduction"));
				}
				if (!(mapProcessInstanceVariables.get("requestVersion") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID, "requestVersionRequest",
							mapProcessInstanceVariables.get("requestVersion"));
				}
				if (!(mapProcessInstanceVariables.get("requestPriority") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID, "requestPriorityRequest",
							mapProcessInstanceVariables.get("requestPriority"));
				}
				if (!(mapProcessInstanceVariables.get("requestReason") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID, "requestReasonRequest",
							mapProcessInstanceVariables.get("requestReason"));
				}
				if (!(mapProcessInstanceVariables.get("requestDescription") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID,
							"requestDescriptionRequest",
							mapProcessInstanceVariables
									.get("requestDescription"));
				}
				if (!(mapProcessInstanceVariables.get("requestType") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID, "requestTypeRequest",
							mapProcessInstanceVariables.get("requestType"));
				}
				if (!(mapProcessInstanceVariables.get("endDateWished") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID, "endDateWishedRequest",
							mapProcessInstanceVariables.get("endDateWished"));
				}
			} else if (instantiateResult.getTask().equals("Validation")) {
				AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
						activityInstanceUUID, "projectEntityValidation",
						mapProcessInstanceVariables.get("projectEntity"));
				AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
						activityInstanceUUID, "projectNameValidation",
						mapProcessInstanceVariables.get("projectName"));
				AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
						activityInstanceUUID, "userAcceptanceValidation",
						mapProcessInstanceVariables.get("userAcceptance"));
				if (!(mapProcessInstanceVariables.get("userProject") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID, "userProjectValidation",
							mapProcessInstanceVariables.get("userProject"));
				}
				AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
						activityInstanceUUID, "requestProductionValidation",
						mapProcessInstanceVariables.get("requestProduction"));
				AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
						activityInstanceUUID, "requestPriorityValidation",
						mapProcessInstanceVariables.get("requestPriority"));
				AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
						activityInstanceUUID, "requestTypeValidation",
						mapProcessInstanceVariables.get("requestType"));
			} else if (instantiateResult.getTask().equals("Evaluation")) {
				if (!(mapProcessInstanceVariables.get("userContract") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID, "userContractEvaluation",
							mapProcessInstanceVariables.get("userContract"));
				}
				AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
						activityInstanceUUID, "requestProductionEvaluation",
						mapProcessInstanceVariables.get("requestProduction"));
			} else if (instantiateResult.getTask()
					.equals("Contract_Validation")) {
				if (!(mapProcessInstanceVariables.get("contractJob") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID,
							"contractJobContract_Validation",
							mapProcessInstanceVariables.get("contractJob"));
				}
				if (!(mapProcessInstanceVariables.get("contractComponent") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID,
							"contractComponentContract_Validation",
							mapProcessInstanceVariables
									.get("contractComponent"));
				}
			} else if (instantiateResult.getTask().equals("Costing_Validation")) {
				AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
						activityInstanceUUID,
						"userAcceptanceCosting_Validation",
						mapProcessInstanceVariables.get("userAcceptance"));
			} else if (instantiateResult.getTask().equals("Follow_Up")) {
				if (!(mapProcessInstanceVariables.get("requestVersion") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID, "requestVersionFollow_Up",
							mapProcessInstanceVariables.get("requestVersion"));
				}
				if (!(mapProcessInstanceVariables.get("initWorkload") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID, "actualWorkloadFollow_Up",
							mapProcessInstanceVariables.get("initWorkload"));
				}
				if (!(mapProcessInstanceVariables.get("initDateAcceptance") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID,
							"finalDateAcceptanceFollow_Up",
							mapProcessInstanceVariables
									.get("initDateAcceptance"));
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID,
							"actualDateAcceptanceFollow_Up",
							mapProcessInstanceVariables
									.get("initDateAcceptance"));
				}
				if (!(mapProcessInstanceVariables.get("initDateBegin") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID, "actualDateBeginFollow_Up",
							mapProcessInstanceVariables.get("initDateBegin"));
				}
				if (!(mapProcessInstanceVariables.get("initDateProduction") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID,
							"actualDateProductionFollow_Up",
							mapProcessInstanceVariables
									.get("initDateProduction"));
				}
			} else if (instantiateResult.getTask().equals("Production")) {
				if (!(mapProcessInstanceVariables.get("actualDateProduction") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID,
							"finalDateProductionProduction",
							mapProcessInstanceVariables
									.get("actualDateProduction"));
				} else if (!(mapProcessInstanceVariables
						.get("initDateProduction") == null)) {
					AccessorUtil.getRuntimeAPI().setActivityInstanceVariable(
							activityInstanceUUID,
							"finalDateProductionProduction",
							mapProcessInstanceVariables
									.get("initDateProduction"));
				}
			}
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActivityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VariableNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public synchronized HashMap<String, Object> getProcessInstanceVariables(
			Bonita instantiateResult) {
		initBonita();
		Map<String, Object> mapProcessInstanceVariables = new HashMap<String, Object>();
		HashMap<String, Object> mapProcessInstanceVariablesDTO = new HashMap<String, Object>();
		ProcessInstanceUUID processInstanceUUID;
		processInstanceUUID = getInstanceUUIDFromString(instantiateResult
				.getInstanceUUID());
		ActivityInstanceUUID activityInstanceUUID;
		activityInstanceUUID = getActivityInstanceUUIDFromString(instantiateResult);
		try {
			// System.out.println("Brouillons:
			// "+instantiateResult.getBrouillon());

//			if (!instantiateResult.getBrouillon()) {
//				//this.initBrouillonVariables(instantiateResult);
//				mapProcessInstanceVariables = AccessorUtil.getQueryRuntimeAPI()
//						.getProcessInstanceVariables(processInstanceUUID);
//				for (Iterator<String> i = mapProcessInstanceVariables.keySet()
//						.iterator(); i.hasNext();) {
//					String temp = i.next();
//					if (mapProcessInstanceVariables.get(temp) instanceof String) {
//						mapProcessInstanceVariablesDTO.put(temp, mapProcessInstanceVariables.get(temp));
//					} else if (mapProcessInstanceVariables.get(temp) instanceof Enumeration) {
//						Enumeration enumTemp = (Enumeration) mapProcessInstanceVariables
//								.get(temp);
//						mapProcessInstanceVariablesDTO.put(temp, enumTemp.getSelectedValue());
//					} else if (mapProcessInstanceVariables.get(temp) instanceof Date) {
//						// System.out.println(mapProcessInstanceVariables.get(temp)+
//						// " est de type Date");
//						mapProcessInstanceVariablesDTO.put(temp, 
//								mapProcessInstanceVariables.get(temp));
//					}
//				}
//				// System.out.println("On a bien récuperer les variables
//				// globales.Premier");
//			} else {
				mapProcessInstanceVariables = AccessorUtil.getQueryRuntimeAPI()
						.getActivityInstanceVariables(activityInstanceUUID);
				for (Iterator<String> i = mapProcessInstanceVariables.keySet()
						.iterator(); i.hasNext();) {
					String temp = i.next();
					if (mapProcessInstanceVariables.get(temp) instanceof String) {
						mapProcessInstanceVariablesDTO.put(temp, 
								mapProcessInstanceVariables.get(temp));
					} else if (mapProcessInstanceVariables.get(temp) instanceof Enumeration) {
						Enumeration enumTemp = (Enumeration) mapProcessInstanceVariables
								.get(temp);
						mapProcessInstanceVariablesDTO.put(temp, 
								enumTemp.getSelectedValue());
					} else if (mapProcessInstanceVariables.get(temp) instanceof Date) {
						// System.out.println(mapProcessInstanceVariables.get(temp)+
						// " est de type Date");
						mapProcessInstanceVariablesDTO.put(temp, 
								mapProcessInstanceVariables.get(temp));
					}
//				}
				// System.out.println("On a bien récuperer les variables
				// globales.");
			}
//		} catch (InstanceNotFoundException e) {
//			e.printStackTrace();
		} catch (ActivityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mapProcessInstanceVariablesDTO;
	}

	public synchronized HashMap<String, Object> getProcessInstanceVariables2(
			Bonita instantiateResult) {
		initBonita();
		Map<String, Object> mapProcessInstanceVariables = new HashMap<String, Object>();
		HashMap<String, Object> mapProcessInstanceVariablesDTO = new HashMap<String, Object>();
		ProcessInstanceUUID processInstanceUUID;
		processInstanceUUID = getInstanceUUIDFromString(instantiateResult
				.getInstanceUUID());
		try {
			mapProcessInstanceVariables = AccessorUtil.getQueryRuntimeAPI()
					.getProcessInstanceVariables(processInstanceUUID);
			for (Iterator<String> i = mapProcessInstanceVariables.keySet()
					.iterator(); i.hasNext();) {
				String temp = i.next();
				if (mapProcessInstanceVariables.get(temp) instanceof String) {
					mapProcessInstanceVariablesDTO.put(temp, 
							mapProcessInstanceVariables.get(temp));
				} else if (mapProcessInstanceVariables.get(temp) instanceof Enumeration) {
					Enumeration enumTemp = (Enumeration) mapProcessInstanceVariables
							.get(temp);
					mapProcessInstanceVariablesDTO.put(temp, enumTemp
							.getSelectedValue());
				} else if (mapProcessInstanceVariables.get(temp) instanceof Date) {
					// System.out.println(mapProcessInstanceVariables.get(temp)+
					// " est de type Date");
					mapProcessInstanceVariablesDTO.put(temp, 
							mapProcessInstanceVariables.get(temp));
				}
			}
			// System.out.println("On a bien récuperer les variables
			// globales.Deuxième");
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		}
		return mapProcessInstanceVariablesDTO;
	}

	private synchronized ProcessInstanceUUID getInstanceUUIDFromString(
			String instance) {
		initBonita();
		Set<ProcessInstance> monHashSet = new HashSet<ProcessInstance>();
		monHashSet = AccessorUtil.getQueryRuntimeAPI().getProcessInstances();
		Iterator<ProcessInstance> it = monHashSet.iterator();
		while (it.hasNext()) {
			ProcessInstance processInstance = (ProcessInstance) it.next();
			if (processInstance.getProcessInstanceUUID().toString().equals(
					instance)) {
				return processInstance.getProcessInstanceUUID();
			}
		}
		return null;
	}

	private TaskUUID getTaskUUIDFromString(Bonita instantiateResult) {
		initBonita();
		Set<ProcessInstance> monHashSet = new HashSet<ProcessInstance>();
		monHashSet = AccessorUtil.getQueryRuntimeAPI().getProcessInstances();
		Iterator<ProcessInstance> it = monHashSet.iterator();
		while (it.hasNext()) {
			ProcessInstance processInstance = (ProcessInstance) it.next();
			if (processInstance.getProcessInstanceUUID().toString().equals(
					instantiateResult.getInstanceUUID())) {
				try {
					Set<ActivityInstance<TaskInstance>> set1;
					set1 = AccessorUtil.getQueryRuntimeAPI().getTasks(
							processInstance.getProcessInstanceUUID());
					Iterator<ActivityInstance<TaskInstance>> it1 = set1
							.iterator();
					while (it1.hasNext()) {
						ActivityInstance<TaskInstance> taskInstance = (ActivityInstance<TaskInstance>) it1
								.next();
						if (taskInstance.getBody().getUUID().toString().equals(
								instantiateResult.getTaskUUID().toString())) {
							return taskInstance.getBody().getUUID();
						}
					}
				} catch (InstanceNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private ActivityInstanceUUID getActivityInstanceUUIDFromString(
			Bonita instantiateResult) {
		initBonita();
		Set<ProcessInstance> monHashSet = new HashSet<ProcessInstance>();
		monHashSet = AccessorUtil.getQueryRuntimeAPI().getProcessInstances();
		Iterator<ProcessInstance> it = monHashSet.iterator();
		while (it.hasNext()) {
			ProcessInstance processInstance = (ProcessInstance) it.next();
			if (processInstance.getProcessInstanceUUID().toString().equals(
					instantiateResult.getInstanceUUID())) {
				try {
					Set<ActivityInstance<TaskInstance>> set1;
					set1 = AccessorUtil.getQueryRuntimeAPI().getTasks(
							processInstance.getProcessInstanceUUID());
					Iterator<ActivityInstance<TaskInstance>> it1 = set1
							.iterator();
					while (it1.hasNext()) {
						ActivityInstance<TaskInstance> taskInstance = (ActivityInstance<TaskInstance>) it1
								.next();
						if (taskInstance.getUUID().toString().equals(
								instantiateResult.getActivityInstanceUUID()
										.toString())) {
							return taskInstance.getUUID();
						}
					}
				} catch (InstanceNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public Bonita[] getTasksReadyForProfile(String profile) throws AccessDeniedException, PathAlreadyBoundException, InvalidPathException, BonitaServiceException {
		initBonita();
		//Temporary implementation to avoid several call from gadget
		try {
			deployProjectWorkflow();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//default path for the moment
		instantiateProjectWorkflow(profile + "/workflow");
		//endTemporary implementation

		Set<ProcessInstance> processInstances = new HashSet<ProcessInstance>();
		HashSet<Bonita> bonitas = new HashSet<Bonita>();
		processInstances = AccessorUtil.getQueryRuntimeAPI().getProcessInstances();
		Iterator<ProcessInstance> it = processInstances.iterator();
		while (it.hasNext()) {
			Bonita bonita = new Bonita();
			ProcessInstance processInstance = (ProcessInstance) it.next();
			System.out.println("Find process instance : "
					+ processInstance.getUUID());
			try {
				Collection<ActivityInstance<TaskInstance>> tasks = AccessorUtil
						.getQueryRuntimeAPI().getTasks(
								processInstance.getUUID());
				
				Iterator<ActivityInstance<TaskInstance>> it1 = tasks
						.iterator();
				while (it1.hasNext()) {
					bonita.setInstanceUUID(processInstance.getUUID().toString());
					bonita.setId(processInstance.getUUID().toString());
					ActivityInstance<TaskInstance> taskInstance = (ActivityInstance<TaskInstance>) it1
							.next();
					bonita.setTask(taskInstance.getActivityId().toString());
					bonita.setTaskUUID(taskInstance.getBody().getUUID()
							.toString());
					bonita.setInitiator(processInstance.getStartedBy());
					bonita.setActivityInstanceUUID(taskInstance.getUUID()
							.toString());
					bonita.setTaskState(taskInstance.getBody().getState()
							.toString());
					bonita.setDateOfCreation(taskInstance.getBody()
							.getCreatedDate().toString());
					if (bonita.getInitiator().equals(profile) && bonita.getTaskState().equals("READY")) {
						bonitas.add(bonita);
					}
				}
			} catch (InstanceNotFoundException e) {
				e.printStackTrace();
			}
		} 

		return (Bonita[]) bonitas.toArray(new Bonita[0]);
	}

	public void performTaskCreateProject(String path, String name, String summary, String licence,
			Bonita bonita) {
		
		initBonita();
		TaskUUID taskUUID;
		taskUUID = getTaskUUIDFromString(bonita);
		try {
			setProcessInstanceActivityCreateProject(path, name, summary, licence, bonita);
			AccessorUtil.getRuntimeAPI().startTask(taskUUID, true);
			AccessorUtil.getRuntimeAPI().finishTask(taskUUID, true);
		} catch (TaskNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalTaskStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void performTaskValidateProject(Boolean validate,
			Bonita bonita) {
		initBonita();
		TaskUUID taskUUID;
		taskUUID = getTaskUUIDFromString(bonita);
		try {
			setProcessInstanceActivityValidateProject(validate, bonita);
			AccessorUtil.getRuntimeAPI().startTask(taskUUID, true);
			AccessorUtil.getRuntimeAPI().finishTask(taskUUID, true);
		} catch (TaskNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalTaskStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getID(String string) {
		StringTokenizer st = new StringTokenizer(string, "$");
		st.nextToken();
		return st.nextToken();
	}

	public void resumeTask(Bonita instantiateResult) {
		initBonita();
		TaskUUID taskUUID;
		taskUUID = getTaskUUIDFromString(instantiateResult);
		try {
			AccessorUtil.getRuntimeAPI().resumeTask(taskUUID, true);
		} catch (TaskNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalTaskStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void suspendTask(Bonita instantiateResult) {
		initBonita();
		TaskUUID taskUUID;
		taskUUID = getTaskUUIDFromString(instantiateResult);
		try {
			AccessorUtil.getRuntimeAPI().suspendTask(taskUUID, true);
		} catch (TaskNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalTaskStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public FactoryResource findResource(String path) throws FactoryException {
		try {
			FactoryResourceIdentifier identifier = binding.lookup(path);

			if (!identifier.getService().equals(BonitaService.SERVICE_NAME)) {
				throw new CoreServiceException("Resource " + identifier + " is not managed by " + SERVICE_NAME);
			}

			if (identifier.getType().equals(Bonita.RESOURCE_NAME)) {
				return getTasksReadyForProfile(membership.getProfilePathForConnectedIdentifier())[0];
			}

			throw new CoreServiceException("Resource " + identifier + " is not managed by " + SERVICE_NAME);

		} catch (Exception e) {
			throw new CoreServiceException("unable to find the resource at path " + path, e);
		}
	}

	@Override
	public String[] getResourceTypeList() {
		return RESOURCE_TYPE_LIST;
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	@EJB(name = "MembershipService")
	public void setMembershipService(MembershipService membership) {
		this.membership = membership;
	}

	public MembershipService getMembershipService() {
		return this.membership;
	}


	@EJB(name = "NotificationService")
	public void setNotificationService(NotificationService notification) {
		this.notification = notification;
	}

	
	@EJB(name = "BindingService")
	public void setBindingService(BindingService binding) {
		this.binding = binding;
	}

	public BindingService getBindingService() {
		return this.binding;
	}

	@EJB(name = "PEPService")
	public void setPEPService(PEPService pep) {
		this.pep = pep;
	}

	public PEPService getPEPService() {
		return this.pep;
	}

	@EJB(name = "PAPService")
	public void setPAPService(PAPService pap) {
		this.pap = pap;
	}

	public PAPService getPAPService() {
		return this.pap;
	}

	public SessionContext getCtx() {
		return ctx;
	}
	@Resource
	public void setCtx(SessionContext ctx) {
		this.ctx = ctx;
	}

	public EntityManager getEm() {
		return em;
	}
	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

}