import org.jboss.ws.core.StubExt;
import org.qualipso.factory.client.ws.Project;
import org.qualipso.factory.client.ws.ProjectServiceException_Exception;
import org.qualipso.factory.client.ws.Project_Service;
import org.qualipso.factory.client.ws.Project_Type;


public class testAccess {
	private static final String PROJECT_NAME = "junitproject_ws";
	
	
	/**
	 * User guest
	 */
	private static final String PROFILE_GUEST = "/profiles/guest";
	
	/**
	 * User guest
	 */
	private static final String PROFILE_ROOT = "/profiles/guest";
	
	/**
	 * project path
	 */
	private static final String PROJECT_PATH = PROFILE_GUEST + "/" + PROJECT_NAME;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Factory project creation");
		Project_Service projectServiceWS = new Project_Service();
		Project projectService = projectServiceWS.getProjectServiceBeanPort();
		((StubExt) projectService).setConfigName("Standard WSSecurity Client");
        //Test if project exist
        try {
        	Project_Type project = projectService.getProject(PROJECT_PATH);
        }
        catch (ProjectServiceException_Exception e) {
        	// Create Project
        	try {
				projectService.createProject(PROJECT_PATH, PROJECT_NAME, "description projet", "license projet");
			} catch (ProjectServiceException_Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }

		System.out.println("Factory project succesfully created");

	}

}
