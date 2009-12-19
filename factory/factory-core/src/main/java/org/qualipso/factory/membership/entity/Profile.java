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
package org.qualipso.factory.membership.entity;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.membership.MembershipService;

import java.util.Date;
import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * The Profile class representing a Profile FactoryResource.<br/>
 * <br/>
 * A Profile contains the list of groups it belongs.
 *
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 5 june 2009
 */
@Entity
@XmlType(name = Profile.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE + Profile.RESOURCE_NAME, propOrder =  {
    "fullname", "email", "accountStatus", "onlineStatus", "lastLoginDate", "groupsList"}
)
@SuppressWarnings("serial")
public class Profile extends FactoryResource {
    public static final String RESOURCE_NAME = "profile";
    public static final int OFFLINE = 0;
    public static final int ONLINE = 1;
    public static final int INACTIVATED = 0;
    public static final int ACTIVATED = 1;
    public static final int BANNED = 1;
    @Id
    private String id;
    private String path;
    private String fullname;
    private String email;
    private int accountStatus;
    private int onlineStatus;
    private Date lastLoginDate;
    private String groupsList;
    private HashMap<String, String> infos;

    public Profile() {
        infos = new HashMap<String, String>(1);
        groupsList = "";
    }

    @XmlAttribute(name = "id", required = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name = "path", required = true)
    @Transient
    @Override
    public String getResourcePath() {
        return path;
    }

    public void setResourcePath(String path) {
        this.path = path;
    }

    @XmlElement(name = "email", required = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @XmlAttribute(name = "fullname", required = true)
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @XmlAttribute(name = "account-status", required = true)
    public int getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(int accountStatus) {
        this.accountStatus = accountStatus;
    }

    @XmlAttribute(name = "online-status", required = true)
    public int getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(int onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    @XmlAttribute(name = "last-login-date", required = true)
    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    @XmlTransient
    public HashMap<String, String> getProfileInfos() {
        return infos;
    }

    public void setProfileInfos(HashMap<String, String> infos) {
        this.infos = infos;
    }

    public void setProfileInfo(String key, String value) {
        infos.put(key, value);
    }

    public String getProfileInfo(String key) {
        return infos.get(key);
    }

    @XmlElement(name = "groups-list", required = true)
    public String getGroupsList() {
        return groupsList;
    }

    public void setGroupsList(String groupsList) {
        this.groupsList = groupsList;
    }

    public boolean isMemberOf(String group) {
        if (groupsList.indexOf(group) != -1) {
            return true;
        }

        return false;
    }

    public void addGroup(String group) {
        if (!isMemberOf(group)) {
            if (groupsList.length() > 0) {
                groupsList += ("," + group);
            } else {
                groupsList += group;
            }
        }
    }

    public void removeGroup(String group) {
        if (isMemberOf(group)) {
            groupsList = groupsList.replaceAll("(" + group + "){1},?", "");
        }
    }

    public String[] getGroups() {
        if (groupsList.equals("")) {
            return new String[0];
        }

        return groupsList.split(",");
    }

    @Override
    @XmlTransient
    public FactoryResourceIdentifier getFactoryResourceIdentifier() {
        return new FactoryResourceIdentifier(MembershipService.SERVICE_NAME, Profile.RESOURCE_NAME, getId());
    }

    @Override
    @XmlTransient
    public String getResourceName() {
        return getFullname();
    }
}
