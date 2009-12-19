/*
 *
 * Qualipso Factory
 * Copyright (C) 2006-2010 INRIA
 * http://www.inria.fr - molli@loria.fr
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of LGPL. See licenses details in LGPL.txt
 *
 * Initial authors :
 *
 * Jérôme Blanchard / INRIA
 * Pascal Molli / Nancy Université
 * Gérald Oster / Nancy Université
 * Christophe Bouthier / INRIA
 * 
 */
package org.qualipso.factory.membership;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryService;
import org.qualipso.factory.binding.InvalidPathException;
import org.qualipso.factory.binding.PathAlreadyBoundException;
import org.qualipso.factory.binding.PathNotEmptyException;
import org.qualipso.factory.binding.PathNotFoundException;
import org.qualipso.factory.binding.PropertyNotFoundException;
import org.qualipso.factory.membership.entity.Group;
import org.qualipso.factory.membership.entity.Profile;
import org.qualipso.factory.membership.entity.ProfileInfo;
import org.qualipso.factory.security.pep.AccessDeniedException;

import javax.ejb.Remote;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * Membership Service is a public service which manage 2 resources : Profile & Group.<br/>
 * <br/>
 * Membership Service allows to manage user Profiles in a dedicated folder of the factory.
 * Those two resources allows to organise user in order to be able to set security rule on group of users.
 * <br/>
 * Each reference to a user in the factory points to the path of its profile. The Profile resource is the most important
 * resource of the factory.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 8 june 2009
 */
@Remote
@WebService(name = MembershipService.SERVICE_NAME, targetNamespace = FactoryNamingConvention.SERVICE_NAMESPACE + MembershipService.SERVICE_NAME)
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface MembershipService extends FactoryService {
    public static final String SERVICE_NAME = "membership";
    public static final String[] RESOURCE_TYPE_LIST = new String[] { Profile.RESOURCE_NAME, Group.RESOURCE_NAME };
    public static final String PROFILES_PATH = "/profiles";
    public static final String UNAUTHENTIFIED_IDENTIFIER = "guest";
    public static final String SUPERUSER_IDENTIFIER = "root";

    /**
     * Get the Factory folder for storing all profiles.
     *
     * @return the basic profile path.
     * @throws MembershipServiceException
     */
    @WebMethod
    @WebResult(name = "profiles-path")
    public String getProfilesPath();

    /**
     * Get the complete profile path of the connected user. <br/>
     * <br/>
     * For example, when user is not authenticate, profile path is /profiles/guest
     *
     * @return A String representation of the connected identifier profile path
     * @throws MembershipServiceException
     */
    @WebMethod
    @WebResult(name = "connected-profile-path")
    public String getProfilePathForConnectedIdentifier();

    /**
     * Get the complete profile path for the specified user identifier.
     *
     * @param identifier the identifier you want to  retrieve the profile path of.
     * @return A String representation of the identifier profile path
     * @throws MembershipServiceException
     */
    @WebMethod
    @WebResult(name = "profile-path")
    public String getProfilePathForIdentifier(String identifier);

    /**
     * Get all subjects for the connected identifier, including all is groups.
     *
     * @return a String array of all connected identifier subjects
     * @throws MembershipServiceException
     */
    @WebMethod
    @WebResult(name = "subjects")
    public String[] getConnectedIdentifierSubjects() throws MembershipServiceException, InvalidPathException, PathNotFoundException;

    /**
     * Create a Profile.
     *
     * @param identifier the user identifier
     * @param fullname the user full name
     * @param email the user email
     * @param accountStatus the user accountStatus
     * @throws MembershipServiceException
     */
    @WebMethod
    public void createProfile(String identifier, String fullname, String email, int accountStatus)
        throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathAlreadyBoundException;

    /**
     * Read a profile from this path.
     *
     * @param path the path of the Profile
     * @return the Profile object at this path.
     * @throws MembershipServiceException
     */
    @WebMethod
    @WebResult(name = "profile")
    public Profile readProfile(String path) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Delete a profile.
     *
     * @param path the path of the profile to delete
     * @throws MembershipServiceException
     */
    @WebMethod
    public void deleteProfile(String path) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException, PathNotEmptyException ;

    /**
     * Update the profile binded to the given path.
     *
     * @param path the path of the profile to update
     * @param fullname the new full name of this profile
     * @param email the new email of this profile
     * @param accountStatus the new account status of this profile.
     * @throws MembershipServiceException
     */
    @WebMethod
    public void updateProfile(String path, String fullname, String email, int accountStatus)
        throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Update the online status of a profile.
     *
     * @param path the path of the profile to update
     * @param onlineStatus the new online status code
     * @throws MembershipServiceException
     */
    @WebMethod
    public void updateProfileOnlineStatus(String path, int onlineStatus)
        throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Update the last login date of a profile.
     *
     * @param path the path of the profile to update
     * @throws MembershipServiceException
     */
    @WebMethod
    public void updateProfileLastLoginDate(String path)
        throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Add a meta data to a profile.<br/>
     * <br/>
     * A profile info is a simple key/value pair.
     *
     * @param path the path of the profile
     * @param name the key of the profile info
     * @param value the value of this profile info
     * @throws MembershipServiceException
     */
    @WebMethod
    public void setProfileInfo(String path, String name, String value)
        throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Get the value of the profile info specified by this key.
     *
     * @param path the path of the profile.
     * @param name the key of the profile info
     * @return the value of this profile info.
     * @throws MembershipServiceException
     */
    @WebMethod
    @WebResult(name = "profile-info")
    public ProfileInfo getProfileInfo(String path, String name)
        throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException, PropertyNotFoundException;

    /**
     * Get all the profile info attached to the specified profile.
     *
     * @param path the path of the profile
     * @return an array containing all profile info
     * @throws MembershipServiceException
     */
    @WebMethod
    @WebResult(name = "profile-infos")
    public ProfileInfo[] listProfileInfos(String path)
        throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Create a group and bind it to the specified path.
     *
     * @param path the path of the group
     * @param name the name of this group
     * @param description the description of this group
     * @throws MembershipServiceException
     */
    @WebMethod
    public void createGroup(String path, String name, String description)
        throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathAlreadyBoundException;

    /**
     * Read a group binded to the given path
     *
     * @param path the path of the group
     * @return the Group FactoryResource
     * @throws MembershipServiceException
     */
    @WebMethod
    @WebResult(name = "group")
    public Group readGroup(String path) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Update a group binded to the given path
     *
     * @param path the path of the group to update
     * @param name the new name of the group
     * @param description the new description of the group.
     * @throws MembershipServiceException
     */
    @WebMethod
    public void updateGroup(String path, String name, String description)
        throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Delete a group binded to the given path
     *
     * @param path the path of the group to delete
     * @throws MembershipServiceException
     */
    @WebMethod
    public void deleteGroup(String path) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException, PathNotEmptyException;

    /**
     * Add a member to the group binded to the specified path
     *
     * @param path the path of the group
     * @param member the member profile path to add
     * @throws MembershipServiceException
     */
    @WebMethod
    public void addMemberInGroup(String path, String member)
        throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Remove a member from the group binded to the specified path.
     *
     * @param path the path of the group
     * @param member the member profile path to remove
     * @throws MembershipServiceException
     */
    @WebMethod
    public void removeMemberFromGroup(String path, String member)
        throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * List all members of the group binded to the specified path.
     *
     * @param path the path of the group
     * @return A String array of all group's members
     * @throws MembershipServiceException
     */
    @WebMethod
    @WebResult(name = "members-list")
    public String[] listMembers(String path) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * List all groups of the Profile binded to the specified path.
     *
     * @param path the path of the Profile
     * @return A String array of all Profile's groups
     * @throws MembershipServiceException
     */
    @WebMethod
    @WebResult(name = "groups-list")
    public String[] listGroups(String path) throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;

    /**
     * Check if a Profile is really members of a Group.
     *
     * @param path the path of the Group
     * @param member the path of the Profile
     * @return true if this Profile is member of the Group
     * @throws MembershipServiceException
     */
    @WebMethod
    public boolean isMember(String path, String member)
        throws MembershipServiceException, AccessDeniedException, InvalidPathException, PathNotFoundException;
}
