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
 *
 */
package org.qualipso.factory.membership.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;


/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 8 June 2009
 */

@Entity
@XmlType(name = "Group", namespace = "http://org.qualipso.factory.ws/resource/group", propOrder =  {
    "name", "description", "membersList"}
)
@Table(name = "`GROUP`")
@SuppressWarnings("serial")
public class Group extends FactoryResource {
	
	@Id
	private String id;
	private String path;
	private String name;
	private String description;
	private String membersList;
	
	public Group() {
		membersList = "";
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
	
	@XmlElement(name="name", required=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name="description", required=true)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement(name="members-list", required=true)
	public String getMembersList() {
		return membersList;
	}

	public void setMembersList(String membersList) {
		this.membersList = membersList;
	}
	
	public boolean isMember(String member) {
        if (membersList.indexOf(member) != -1) {
            return true;
        }

        return false;
    }

	public void addMember(String member) {
        if (!isMember(member)) {
            if (membersList.length() > 0) {
                membersList += ("," + member);
            } else {
                membersList += member;
            }
        }
    }

	public void removeMember(String member) {
        if (isMember(member)) {
            membersList = membersList.replaceAll("(" + member + "){1},?", "");
        }
    }
	
	public String[] getMembers() {
        return membersList.split(",");
    }
	
	@Override
	@XmlTransient
	public FactoryResourceIdentifier getFactoryResourceIdentifier() {
		return new FactoryResourceIdentifier("MembershipService", "Group", getId());
	}
	
	@Override
	@XmlTransient
	public String getResourceName() {
		return getName();
	}
	
}
