package org.qualipso.factory.membership;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.membership.entity.Group;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.membership.entity.ProfileInfo;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pep.PEPService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 8 june 2009
 */
@Remote
@WebService(name = "MembershipService", targetNamespace = "http://org.qualipso.factory.ws/service/membership")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface MembershipService extends FactoryService {
	
	public static final String PROFILES_PATH = "/profiles";
	public static final String UNAUTHENTIFIED_IDENTIFIER = "guest";
	public static final String SUPERUSER_IDENTIFIER = "root";
	
	@WebMethod
	@WebResult(name = "profiles-path")
	public String getProfilesPath() throws MembershipServiceException;
	
	@WebMethod
	@WebResult(name = "connected-profile-path")
	public String getProfilePathForConnectedIdentifier() throws MembershipServiceException;
	
	@WebMethod
	@WebResult(name = "profile-path")
	public String getProfilePathForIdentifier(String identifier) throws MembershipServiceException;
	
	@WebMethod
	public void createProfile(String identifier, String fullname, String email, int accountStatus) throws MembershipServiceException;
	
	@WebMethod
	@WebResult(name = "profile")
	public Profile readProfile(String path) throws MembershipServiceException;
	
	@WebMethod
	public void deleteProfile(String path) throws MembershipServiceException;
	
	@WebMethod
	public void updateProfile(String path, String fullname, String email, int accountStatus) throws MembershipServiceException;
	
	@WebMethod
	public void updateProfileOnlineStatus(String path, int onlineStatus) throws MembershipServiceException;
	
	@WebMethod
	public void updateProfileLastLoginDate(String path) throws MembershipServiceException;
	
	@WebMethod
	public void setProfileInfo(String path, String name, String value) throws MembershipServiceException;
	
	@WebMethod
	@WebResult(name = "profile-info")
	public ProfileInfo getProfileInfo(String path, String name) throws MembershipServiceException;
	
	@WebMethod
	@WebResult(name = "profile-infos")
	public ProfileInfo[] listProfileInfos(String path) throws MembershipServiceException;
	
	@WebMethod
	public void createGroup (String path, String name, String description)  throws MembershipServiceException;
	
	@WebMethod
	@WebResult(name = "group")
	public Group readGroup(String path) throws MembershipServiceException;
	
	@WebMethod
	public void updateGroup(String path, String name, String description) throws MembershipServiceException;
	
	@WebMethod
	public void deleteGroup(String path) throws MembershipServiceException;
	
	@WebMethod
	public void addMemberInGroup(String path, String member) throws MembershipServiceException;
	
	@WebMethod
	public void removeMemberFromGroup(String path, String member) throws MembershipServiceException;
	
	@WebMethod
	@WebResult(name = "members-list")
	public String[] listMembers(String path) throws MembershipServiceException;
	
	@WebMethod
	public boolean isMember(String path, String member) throws MembershipServiceException;
	
}
