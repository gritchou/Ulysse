package org.qualipso.factory.jabuti;

import java.rmi.RemoteException;

import javax.activation.DataHandler;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.core.CoreService;
import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.core.entity.File;
import org.qualipso.factory.core.entity.FileData;
import org.qualipso.factory.jabuti.service.InvalidFileFaultException2;
import org.qualipso.factory.jabuti.service.InvalidNameFaultException5;
import org.qualipso.factory.jabuti.service.InvalidProjectIdFaultException0;
import org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub;
import org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.CleanProject;
import org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.CleanProjectResponse;
import org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.CreateProject;
import org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.CreateProjectResponse;
import org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.DeleteProject;
import org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.DeleteProjectResponse;
import org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.UpdateProject;
import org.qualipso.factory.jabuti.service.JaBUTiService1_0Stub.UpdateProjectResponse;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;

/**
 * @author Rafael Messias Martins
 * @date Sometime in 2009
 */
@Stateless(name = "Jabuti", 
		mappedName = FactoryNamingConvention.SERVICE_PREFIX + "JabutiService")
@WebService(endpointInterface = "org.qualipso.factory.jabuti.JabutiService", 
		targetNamespace = "http://org.qualipso.factory.ws/service/jabuti", 
		serviceName = "JabutiService", portName = "JabutiServicePort")
@WebContext(contextRoot = "/factory-service-jabuti", urlPattern = "/jabuti")
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class JabutiServiceBean implements JabutiService {

	private static Log logger = LogFactory.getLog(JabutiServiceBean.class);

	private JaBUTiService1_0Stub stub;

	public JabutiServiceBean() {
		// ??
	}

	private void connect() throws AxisFault {
		stub = new JaBUTiService1_0Stub(
		"http://www.labes.icmc.usp.br:9991/jabutiprojectSvn/services/JaBUTiService1_0");
		stub._getServiceClient().getOptions().setProperty(
				Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
		stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(4000000);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public JabutiProject createProject(String projectName, File projectFile) 
	throws InvalidFileFault, InvalidNameFault {
		try {
			connect();

			CreateProject input = new CreateProject();

			//input.setProjectName("Vending");			
			input.setProjectName(projectName);			

			//input.setIdUserName("user");
			// TODO: Fix this to get only the profile name, not the whole path.
			String userName = membership.getProfilePathForConnectedIdentifier();
			input.setIdUserName(userName);

			//File file = new File("/home/andre/ifiles/doctoral/install/eclipseworkspace/sort.jar");
			//File file = new File(projectFile);

			// Uploading/Resource creation is up to the user and must be done previously.

			FileData projectData = core.getFileData(projectFile.getResourcePath());
			DataHandler datahandler = projectData.getData();
			input.setProjectFile(datahandler);

			CreateProjectResponse output;			
			output = stub.createProject(input);
			
			String projId = output.get_return();
			
			JabutiProject jp = new JabutiProject(projId, projectName);
			
			//System.out.println("project id: " + projId);

			return jp;

		} catch (AxisFault e) {			
			e.printStackTrace();
			//return e.toString();
		} catch (RemoteException e) {			
			e.printStackTrace();
			//return e.toString();
		} catch (InvalidNameFaultException5 e) {			
			e.printStackTrace();
			//return e.toString();
		} catch (InvalidFileFaultException2 e) {			
			e.printStackTrace();
			//return e.toString();
		} catch (MembershipServiceException e) {			
			e.printStackTrace();
			//return e.toString();
		} catch (CoreServiceException e) {			
			e.printStackTrace();
			//return e.toString();
		}
		
		return null;
	}

	public String updateProject(JabutiProject project) 
	throws InvalidProjectIdFault, InvalidFileFault {
		try {
			connect();

			UpdateProject input = new UpdateProject();

			input.setProjectId(project.getProjectId());
			
			// TODO: Fix this to get only the profile name, not the whole path.
			String userName = membership.getProfilePathForConnectedIdentifier();
			input.setIdUserName(userName);
			
			FileData projectData = core.getFileData(project.getResourcePath());
			DataHandler datahandler = projectData.getData();
			input.setProjectFile(datahandler);

			UpdateProjectResponse output;
			String message = "";
			output = stub.updateProject(input);
			message = output.get_return();

			return message;

		} catch (AxisFault e) {			
			e.printStackTrace();
			//return e.toString();
		} catch (RemoteException e) {			
			e.printStackTrace();
			//return e.toString();
		} catch (InvalidFileFaultException2 e) {			
			e.printStackTrace();
			//return e.toString();
		} catch (InvalidProjectIdFaultException0 e) {			
			e.printStackTrace();
			//return e.toString(); 
		}
		catch (MembershipServiceException e) {			
			e.printStackTrace();
			//return e.toString();
		} catch (CoreServiceException e) {			
			e.printStackTrace();
			//return e.toString();
		}
		
		return null;
	}
	
	public String cleanProject(JabutiProject project) 
	throws InvalidProjectIdFault {
		try {
			connect();

			CleanProject input = new CleanProject();

			input.setProjectId(project.getProjectId());
			
			// TODO: Fix this to get only the profile name, not the whole path.
			String userName = membership.getProfilePathForConnectedIdentifier();
			input.setIdUserName(userName);
			
			CleanProjectResponse output;
			String message = "";
			output = stub.cleanProject(input);
			message = output.get_return();

			return message;

		} catch (AxisFault e) {			
			e.printStackTrace();
			//return e.toString();
		} catch (RemoteException e) {			
			e.printStackTrace();
			//return e.toString();
		} catch (InvalidProjectIdFaultException0 e) {			
			e.printStackTrace();
			//return e.toString(); 
		}
		catch (MembershipServiceException e) {			
			e.printStackTrace();
			//return e.toString();
		}
		
		return null;
	}
	
	public String deleteProject(JabutiProject project) 
	throws InvalidProjectIdFault {
		try {
			connect();

			DeleteProject input = new DeleteProject();

			input.setProjectId(project.getProjectId());
			
			// TODO: Fix this to get only the profile name, not the whole path.
			String userName = membership.getProfilePathForConnectedIdentifier();
			input.setIdUserName(userName);
			
			DeleteProjectResponse output;
			String message = "";
			output = stub.deleteProject(input);
			message = output.get_return();
			
			core.deleteFile(project.getResourcePath());

			return message;

		} catch (AxisFault e) {			
			e.printStackTrace();
			//return e.toString();
		} catch (RemoteException e) {			
			e.printStackTrace();
			//return e.toString();
		} catch (InvalidProjectIdFaultException0 e) {			
			e.printStackTrace();
			//return e.toString(); 
		}
		catch (MembershipServiceException e) {			
			e.printStackTrace();
			//return e.toString();
		} catch (CoreServiceException e) {			
			e.printStackTrace();
			//return e.toString();
		}
		
		return null;
	}

	// ------------------------ Métodos exigidos pela interface FactoryService

	@Override
	public String[] getResourceTypeList() {
		return new String[0];
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

		throw new FactoryException("No Resource are managed by Jabuti Service");
	}

	// --------------------- Membership, Core

	private MembershipService membership;

	@EJB
	public void setMembershipService(MembershipService membership) {
		this.membership = membership;
	}

	public MembershipService getMembershipService() {
		return this.membership;
	}

	private CoreService core;

	@EJB
	public void setCoreService(CoreService core) {
		this.core = core;
	}

	public CoreService getCoreService() {
		return this.core;
	}

}