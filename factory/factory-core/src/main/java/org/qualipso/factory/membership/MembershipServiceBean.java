package org.qualipso.factory.membership;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.wsf.spi.annotation.WebContext;
import org.qualipso.factory.FactoryException;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.FactoryResourceProperty;
import org.qualipso.factory.binding.BindingService;
import org.qualipso.factory.binding.PathHelper;
import org.qualipso.factory.core.CoreServiceException;
import org.qualipso.factory.membership.entity.Group;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.membership.entity.ProfileInfo;
import org.qualipso.factory.eventqueue.entity.Event;
import org.qualipso.factory.notification.NotificationService;
import org.qualipso.factory.security.auth.AuthenticationService;
import org.qualipso.factory.security.pap.PAPService;
import org.qualipso.factory.security.pap.PAPServiceHelper;
import org.qualipso.factory.security.pep.PEPService;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 8 june 2009
 */
@Stateless(name = "Membership", mappedName = FactoryNamingConvention.JNDI_SERVICE_PREFIX + "MembershipService")
@WebService(endpointInterface = "org.qualipso.factory.membership.MembershipService", targetNamespace = "http://org.qualipso.factory.ws/service/membership", serviceName = "MembershipService", portName = "MembershipServicePort")
@WebContext(contextRoot = "/factory-core", urlPattern = "/membership")
@SOAPBinding(style = Style.RPC)
@SecurityDomain(value = "JBossWSDigest")
@EndpointConfig(configName = "Standard WSSecurity Endpoint")
public class MembershipServiceBean implements MembershipService {

	private static final String SERVICE_NAME = "MembershipService";
	private static final String[] RESOURCE_TYPE_LIST = new String[] {"Profile", "Group"};
	
	private static Log logger = LogFactory.getLog(MembershipServiceBean.class);
	
	private PEPService pep;
	private PAPService pap;
	private BindingService binding;
	private NotificationService notification;
	private AuthenticationService authentication;
	private SessionContext ctx;
	private EntityManager em;
	
	
	public MembershipServiceBean() {
	}
	
	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public EntityManager getEntityManager() {
		return this.em;
	}

	@Resource
	public void setSessionContext(SessionContext ctx) {
		this.ctx = ctx;
	}

	public SessionContext getSessionContext() {
		return this.ctx;
	}

	@EJB
	public void setBindingService(BindingService binding) {
		this.binding = binding;
	}

	public BindingService getBindingService() {
		return this.binding;
	}

	@EJB
	public void setPEPService(PEPService pep) {
		this.pep = pep;
	}

	public PEPService getPEPService() {
		return this.pep;
	}

	@EJB
	public void setPAPService(PAPService pap) {
		this.pap = pap;
	}

	public PAPService getPAPService() {
		return this.pap;
	}

	@EJB
	public void setNotificationService(NotificationService notification) {
		this.notification = notification;
	}

	public NotificationService getNotificationService() {
		return this.notification;
	}

	@EJB
	public void setAuthenticationService(AuthenticationService authentication) {
		this.authentication = authentication;
	}

	public AuthenticationService getAuthenticationService() {
		return this.authentication;
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String getProfilesPath() throws MembershipServiceException {
		return PROFILES_PATH;
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String getProfilePathForConnectedIdentifier() throws MembershipServiceException {
		logger.info("getProfilePathForConnectedIdentifier(...) called");
		
		try {
			return PROFILES_PATH + "/" + authentication.getConnectedIdentifier();
		} catch ( Exception e ) {
			throw new MembershipServiceException(e);
		}
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String getProfilePathForIdentifier(String identifier) throws MembershipServiceException {
		logger.info("getProfilePathForIdentifier(...) called");
		
		try {
			return PROFILES_PATH + "/" + identifier;
		} catch ( Exception e ) {
			throw new MembershipServiceException(e);
		}
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createProfile(String identifier, String fullname, String email, int accountStatus) throws MembershipServiceException {
		logger.info("createProfile(...) called");
		logger.debug("params : identifier=" + identifier + ", fullname=" + fullname + ", email=" + email + ", accountStatus=" + accountStatus);
		
		String path = getProfilePathForIdentifier(identifier);
		logger.debug("generated profile path : " + path);
		
		try {
			String caller = getProfilePathForConnectedIdentifier();
			
			pep.checkSecurity(caller, PathHelper.getParentPath(path), "create");
			
			Profile profile = new Profile();
			profile.setId(UUID.randomUUID().toString());
			profile.setFullname(fullname);
			profile.setEmail(email);
			profile.setAccountStatus(accountStatus);
			profile.setOnlineStatus(Profile.OFFLINE);
			profile.setLastLoginDate(new Date(0));
			em.persist(profile);
			
			binding.bind(profile.getFactoryResourceIdentifier(), path);
			binding.setProperty(path, FactoryResourceProperty.CREATION_TIMESTAMP, System.currentTimeMillis() + "");
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			binding.setProperty(path, FactoryResourceProperty.AUTHOR, caller);
			
			String policyId = UUID.randomUUID().toString();
			pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, path, path));
			
			binding.setProperty(path, FactoryResourceProperty.OWNER, path);
			binding.setProperty(path, FactoryResourceProperty.POLICY_ID, policyId);
			
			notification.throwEvent(new Event(path, caller, "Profile", "membership.profile.create", ""));
		} catch ( Exception e ) {
			logger.error("unable to create a profile", e);
			ctx.setRollbackOnly();
			throw new MembershipServiceException("unable to create a profile", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Profile readProfile(String path) throws MembershipServiceException {
		logger.info("readProfile(...) called");
		logger.debug("params : path=" + path);
		
		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "read");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, "Profile");
		
			Profile profile = em.find(Profile.class, identifier.getId());
			if ( profile == null ) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}
			profile.setResourcePath(path);

			notification.throwEvent(new Event(path, caller, "Profile", "membership.profile.read", ""));
			
			return profile;
		} catch ( Exception e ) {
			logger.error("unable to read a profile", e);
			throw new MembershipServiceException("unable to read a profile", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateProfile(String path, String fullname, String email, int accountStatus) throws MembershipServiceException {
		logger.info("updateProfile(...) called");
		logger.debug("params : path=" + path + ", fullname=" + fullname + ", email=" + email + ", accountStatus=" + accountStatus);
		
		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "update");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, "Profile");
		
			Profile profile = em.find(Profile.class, identifier.getId());
			if ( profile == null ) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}
			profile.setFullname(fullname);
			profile.setEmail(email);
			profile.setAccountStatus(accountStatus);
			em.merge(profile);
		
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller, "Profile", "membership.profile.update", ""));
		} catch ( Exception e ) {
			logger.error("unable to update a profile", e);
			ctx.setRollbackOnly();
			throw new MembershipServiceException("unable to update a profile", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateProfileLastLoginDate(String path) throws MembershipServiceException {
		logger.info("updateProfileLastLoginDate(...) called");
		logger.debug("params : path=" + path);
		
		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "update");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, "Profile");
			
			Profile profile = em.find(Profile.class, identifier.getId());
			if ( profile == null ) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}
			profile.setLastLoginDate(new Date());
			em.merge(profile);
		
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller, "Profile", "membership.profile.update", ""));
		} catch ( Exception e ) {
			logger.error("unable to update the profile last login date", e);
			ctx.setRollbackOnly();
			throw new MembershipServiceException("unable to update the profile last login date", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateProfileOnlineStatus(String path, int onlineStatus) throws MembershipServiceException {
		logger.info("updateProfileOnlineStatus(...) called");
		logger.debug("params : path=" + path + ", onlineStatus=" + onlineStatus);
		
		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "update");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, "Profile");
			
			Profile profile = em.find(Profile.class, identifier.getId());
			if ( profile == null ) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}
			profile.setOnlineStatus(onlineStatus);
			em.merge(profile);
		
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller, "Profile", "membership.profile.update", ""));
		} catch ( Exception e ) {
			logger.error("unable to update the profile online status", e);
			ctx.setRollbackOnly();
			throw new MembershipServiceException("unable to update the profile online status", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteProfile(String path) throws MembershipServiceException {
		logger.info("deleteProfile(...) called");
		logger.debug("params : path=" + path);
		
		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "delete");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, "Profile");
			
			Profile profile = em.find(Profile.class, identifier.getId());
			if ( profile == null ) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}
			em.remove(profile);

			binding.unbind(path);
			//TODO remove security rule for this profile.
			notification.throwEvent(new Event(path, caller, "Profile", "membership.profile.delete", ""));
		} catch ( Exception e ) {
			logger.error("unable to delete profile", e);
			ctx.setRollbackOnly();
			throw new MembershipServiceException("unable to delete profile", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void setProfileInfo(String path, String name, String value) throws MembershipServiceException {
		logger.info("setProfileInfo(...) called");
		logger.debug("params : path=" + path + ", name=" + name + ", value=" + value);
		
		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "update");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, "Profile");
			
			Profile profile = em.find(Profile.class, identifier.getId());
			if ( profile == null ) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}
			profile.setProfileInfo(name, value);
			em.merge(profile);
		
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller, "Profile", "membership.profile.info.set", "name:" + name + ", value:" + value));
		} catch ( Exception e ) {
			logger.error("unable to set profile info", e);
			ctx.setRollbackOnly();
			throw new MembershipServiceException("unable to set profile info", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ProfileInfo getProfileInfo(String path, String name) throws MembershipServiceException {
		logger.info("getProfileInfo(...) called");
		logger.debug("params : path=" + path + ", name=" + name);
		
		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "read");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, "Profile");
		
			Profile profile = em.find(Profile.class, identifier.getId());
			if ( profile == null ) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}
			
			ProfileInfo pinfo = new ProfileInfo();
			pinfo.setName(name);
			pinfo.setValue(profile.getProfileInfo(name));
			
			notification.throwEvent(new Event(path, caller, "Profile", "membership.profile.info.get", "name:" + name));
			return pinfo;
		} catch ( Exception e ) {
			logger.error("unable to get profile info", e);
			throw new MembershipServiceException("unable to get profile info", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ProfileInfo[] listProfileInfos(String path) throws MembershipServiceException {
		logger.info("listProfileInfos(...) called");
		logger.debug("params : path=" + path);
		
		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "read");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, "Profile");
		
			Profile profile = em.find(Profile.class, identifier.getId());
			if ( profile == null ) {
				throw new MembershipServiceException("unable to find a profile for id " + identifier.getId());
			}
			
			Vector<ProfileInfo> pinfos = new Vector<ProfileInfo> ();
			for ( String pinfokey : profile.getProfileInfos().keySet() ) {
				ProfileInfo pinfo = new ProfileInfo();
				pinfo.setName(pinfokey);
				pinfo.setValue(profile.getProfileInfo(pinfokey));
				pinfos.add(pinfo);
			}
			
			notification.throwEvent(new Event(path, caller, "Profile", "membership.profile.info.list", ""));
			ProfileInfo[] infos = new ProfileInfo[0];
			return pinfos.toArray(infos); 
		} catch ( Exception e ) {
			logger.error("unable to list profile infos", e);
			throw new MembershipServiceException("unable to list profile infos", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createGroup(String path, String name, String description) throws MembershipServiceException {
		logger.warn("createGroup(...) called");
		logger.debug("params : path=" + path + ", name=" + name + ", description=" + description);
		
		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, PathHelper.getParentPath(path), "create");
			
			Group group = new Group();
			group.setId(UUID.randomUUID().toString());
			group.setName(name);
			group.setDescription(description);
			em.persist(group);
			
			binding.bind(group.getFactoryResourceIdentifier(), path);
			binding.setProperty(path, FactoryResourceProperty.CREATION_TIMESTAMP, System.currentTimeMillis() + "");
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			binding.setProperty(path, FactoryResourceProperty.AUTHOR, caller);
			
			String policyId = UUID.randomUUID().toString();
			pap.createPolicy(policyId, PAPServiceHelper.buildOwnerPolicy(policyId, path, path));
			
			binding.setProperty(path, FactoryResourceProperty.OWNER, caller);
			binding.setProperty(path, FactoryResourceProperty.POLICY_ID, policyId);
			
			notification.throwEvent(new Event(path, caller, "Group", "membership.group.create", ""));
		} catch ( Exception e ) {
			logger.error("unable to create group", e);
			ctx.setRollbackOnly();
			throw new MembershipServiceException("unable to create group", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Group readGroup(String path) throws MembershipServiceException {
		logger.warn("readGroup(...) called");
		logger.debug("params : path=" + path);
		
		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "read");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, "Group");
		
			Group group = em.find(Group.class, identifier.getId());
			if ( group == null ) {
				throw new MembershipServiceException("unable to find a group for id " + identifier.getId());
			}
			group.setResourcePath(path);

			notification.throwEvent(new Event(path, caller, "Group", "membership.group.read", ""));
			
			return group;
		} catch ( Exception e ) {
			logger.error("unable to read group", e);
			throw new MembershipServiceException("unable to read group", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateGroup(String path, String name, String description) throws MembershipServiceException {
		logger.warn("updateGroup(...) called");
		logger.debug("params : path=" + path + ", name=" + name + ", description=" + description);
		
		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "update");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, "Group");
		
			Group group = em.find(Group.class, identifier.getId());
			if ( group == null ) {
				throw new MembershipServiceException("unable to find a group for id " + identifier.getId());
			}
			group.setName(name);
			group.setDescription(description);
			em.merge(group);
		
			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller, "Group", "membership.group.update", ""));
		} catch ( Exception e ) {
			logger.error("unable to update group", e);
			ctx.setRollbackOnly();
			throw new MembershipServiceException("unable to update group", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteGroup(String path) throws MembershipServiceException {
		logger.warn("deleteGroup(...) called");
		logger.debug("params : path=" + path);
		
		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "delete");
			
			FactoryResourceIdentifier identifier = binding.lookup(path);
			checkResourceType(identifier, "Group");
			
			Group group = em.find(Group.class, identifier.getId());
			if ( group == null ) {
				throw new MembershipServiceException("unable to find a group for id " + identifier.getId());
			}
			em.remove(group);

			binding.unbind(path);
			//TODO remove security rule for this group.
			notification.throwEvent(new Event(path, caller, "Group", "membership.group.delete", ""));
		} catch ( Exception e ) {
			logger.error("unable to delete group", e);
			ctx.setRollbackOnly();
			throw new MembershipServiceException("unable to delete group", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void addMemberInGroup(String path, String member) throws MembershipServiceException {
		logger.warn("addMemberInGroup(...) called");
		logger.debug("params : path=" + path + ", member=" + member);
		
		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "update");
			
			FactoryResourceIdentifier groupIdentifier = binding.lookup(path);
			checkResourceType(groupIdentifier, "Group");
			
			FactoryResourceIdentifier memberIdentifier = binding.lookup(member);
			Vector<String> memberTypes = new Vector<String> ();
			memberTypes.add("Group");
			memberTypes.add("Profile");
			checkResourceType(memberIdentifier, memberTypes);
			
			Group group = em.find(Group.class, groupIdentifier.getId());
			if ( group == null ) {
				throw new MembershipServiceException("unable to find a group for id " + groupIdentifier.getId());
			}
			if ( memberIdentifier.getType().equals("Group") ) {
				group.addMember("G:" + member);
			} else {
				group.addMember("P:" + member);
			}
			em.merge(group);

			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller, "Group", "membership.group.add-member", ""));
		} catch ( Exception e ) {
			logger.error("unable to add member in group", e);
			ctx.setRollbackOnly();
			throw new MembershipServiceException("unable to add member in group", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removeMemberFromGroup(String path, String member) throws MembershipServiceException {
		logger.warn("removeMemberFromGroup(...) called");
		logger.debug("params : path=" + path + ", member=" + member);
		
		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "update");
			
			FactoryResourceIdentifier groupIdentifier = binding.lookup(path);
			checkResourceType(groupIdentifier, "Group");
			
			FactoryResourceIdentifier memberIdentifier = binding.lookup(member);
			Vector<String> memberTypes = new Vector<String> ();
			memberTypes.add("Group");
			memberTypes.add("Profile");
			checkResourceType(memberIdentifier, memberTypes);
			
			Group group = em.find(Group.class, groupIdentifier.getId());
			if ( group == null ) {
				throw new MembershipServiceException("unable to find a group for id " + groupIdentifier.getId());
			}
			if ( memberIdentifier.getType().equals("Group") ) {
				group.removeMember("G:" + member);
			} else {
				group.removeMember("P:" + member);
			}
			em.merge(group);

			binding.setProperty(path, FactoryResourceProperty.LAST_UPDATE_TIMESTAMP, System.currentTimeMillis() + "");
			notification.throwEvent(new Event(path, caller, "Group", "membership.group.remove-member", ""));
		} catch ( Exception e ) {
			logger.error("unable to remove member from group", e);
			ctx.setRollbackOnly();
			throw new MembershipServiceException("unable to remove member from group", e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isMember(String path, String member) throws MembershipServiceException {
		logger.warn("isMember(...) called");
		logger.debug("params : path=" + path + ", member=" + member);
		
		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "read");
			
			FactoryResourceIdentifier groupIdentifier = binding.lookup(path);
			checkResourceType(groupIdentifier, "Group");
			
			FactoryResourceIdentifier memberIdentifier = binding.lookup(member);
			Vector<String> memberTypes = new Vector<String> ();
			memberTypes.add("Group");
			memberTypes.add("Profile");
			checkResourceType(memberIdentifier, memberTypes);
			
			boolean isMember = false;
			if ( memberIdentifier.getType().equals("Group") ) {
				isMember = isMember(path, "G:" + member,new Vector<String> ());
			} else {
				isMember = isMember(path, "P:" + member,new Vector<String> ());
			}
			
			notification.throwEvent(new Event(path, caller, "Group", "membership.group.is-member", ""));
			
			return isMember;
		} catch ( Exception e ) {
			logger.error("error in isMember", e);
			throw new MembershipServiceException("error in isMember", e);
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private boolean isMember(String path, String memberId, List<String> cycle) throws MembershipServiceException {
		try {
			FactoryResourceIdentifier groupIdentifier = binding.lookup(path);
			checkResourceType(groupIdentifier, "Group");
			
			Group group = em.find(Group.class, groupIdentifier.getId());
			
			cycle.add(path);
			
			if ( group.isMember(memberId) ) {
				return true;
			}
			
			for (String cmember : group.getMembers() ) {
				if ( cmember.startsWith("G:") ) {
					if ( !cycle.contains(cmember.substring(2)) ) {
						return isMember(cmember.substring(2), memberId, cycle);
					}
				}
			}
			
			return false;
		} catch ( Exception e ) {
			throw new MembershipServiceException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String[] listMembers(String path) throws MembershipServiceException {
		logger.warn("listMembers(...)");
		logger.debug("params : path=" + path);
		try {
			String caller = getProfilePathForConnectedIdentifier();
			pep.checkSecurity(caller, path, "read");
			
			FactoryResourceIdentifier groupIdentifier = binding.lookup(path);
			checkResourceType(groupIdentifier, "Group");
			
			Group group = em.find(Group.class, groupIdentifier.getId());
			
			String[] membersId = group.getMembers();
			String[] members = new String[membersId.length];
			
			for ( int i=0; i<membersId.length; i++ ) {
				members[i] = membersId[i].substring(2);
			}
			
			notification.throwEvent(new Event(path, caller, "Group", "membership.group.list-members", ""));
			
			return members;
		} catch ( Exception e ) {
			logger.error("error in listMembers", e);
			throw new MembershipServiceException("error in listMembers", e);
		}
	}
	
	//private internal methods
	
	private void checkResourceType(FactoryResourceIdentifier identifier, String resourceType) throws MembershipServiceException {
		Vector<String> types = new Vector<String> ();
		types.add(resourceType);
		this.checkResourceType(identifier, types);
	}
	
	private void checkResourceType(FactoryResourceIdentifier identifier, Vector<String> resourceTypes) throws MembershipServiceException {
		if ( !identifier.getService().equals(getServiceName()) ) {
			throw new MembershipServiceException("resource identifier " + identifier + " does not refer to service " + getServiceName());
		}
		if ( !resourceTypes.contains(identifier.getType()) ) {
			throw new MembershipServiceException("resource identifier " + identifier + " does not refer to one of the desired resource types");
		}
	}
	
	//Factory Service Methods
	
	@Override
	public String[] getResourceTypeList() {
		return RESOURCE_TYPE_LIST;
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public FactoryResource findResource(String path) throws FactoryException {
		logger.info("findResource(...) called");
		logger.debug("params : path=" + path);
		
		try {
			FactoryResourceIdentifier identifier = binding.lookup(path);
			
			if ( !identifier.getService().equals("MembershipService") ) {
				throw new CoreServiceException("Resource " + identifier + " is not managed by Membership Service");
			}
			
			if ( identifier.getType().equals("Group") ) {
				return readGroup(path);
			} 
			
			if ( identifier.getType().equals("Profile") ) {
				return readProfile(path);
			} 
			
			throw new CoreServiceException("Resource " + identifier + " is not managed by Membership Service");
			
		} catch (Exception e) {
			logger.error("unable to find the resource at path " + path, e);
			throw new CoreServiceException("unable to find the resource at path " + path, e);
		}
	}
	
}
