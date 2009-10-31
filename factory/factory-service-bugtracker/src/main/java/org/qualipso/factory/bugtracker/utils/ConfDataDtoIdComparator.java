/**
 * 
 * Copyright (C) 2006-2010 THALES
 * http://www.thalesgroup.fr - gregory.cunha@thalesgroup.com
 *
 * This software is free software; you can redistribute it and/or
 * modify it under the terms of the license LGPL.
 *
 * Initial author :
 * Gregory Cunha from Thales Service, THERESIS Competence Center Open Source Software
 *
 */
package org.qualipso.factory.bugtracker.utils;

import java.util.Comparator;

import org.qualipso.factory.bugtracker.dto.ConfDataDto;

/**
 * Comparator for ConfDataDto Id
 * Compare length and after syntax of id
 *
 */
public class ConfDataDtoIdComparator implements Comparator<ConfDataDto> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(ConfDataDto o1, ConfDataDto o2) {
		int result = 0;
		
		if (o1 == null || o1.getId() == null) {
			result = -1;
		}
		else if (o2 == null || o2.getId() == null) {
			result = 1;
		}
		else if (o1.getId().length() > o2.getId().length()) {
			result = 1;
		}
		else if (o1.getId().length() < o2.getId().length()) {
			result = -1;
		}
		else {
			result = o1.getId().compareTo(o2.getId());
		}
		return result;
	}

}
