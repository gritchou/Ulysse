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
package org.qualipso.factory.test.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.qualipso.factory.bugtracker.dto.ConfDataDto;
import org.qualipso.factory.bugtracker.utils.ConfDataDtoIdComparator;

import junit.framework.TestCase;

/**
 * Test ConfDataDtoIdComparator class
 *
 */
public class ConfDataDtoIdComparatorTest extends TestCase {

	/**
	 * Test the comparator
	 */
	public void testComparator() {
		List<ConfDataDto> list = new ArrayList<ConfDataDto>();
		
		list.add(createConfDataDto("10"));
		list.add(createConfDataDto("2"));
		list.add(createConfDataDto("12"));
		list.add(createConfDataDto("11"));
		list.add(createConfDataDto("300"));
		list.add(createConfDataDto("41"));
		
		Collections.sort(list, new ConfDataDtoIdComparator());
		
		int i = 0;
		assertEquals("2", list.get(i++).getId());
		assertEquals("10", list.get(i++).getId());
		assertEquals("11", list.get(i++).getId());
		assertEquals("12", list.get(i++).getId());
		assertEquals("41", list.get(i++).getId());
		assertEquals("300", list.get(i++).getId());
	}
	
	
	/**
	 * Create a ConfDataDto for test
	 * @param id of the ConfDataDto
	 * @return ConfDataDto with the given id
	 */
	private ConfDataDto createConfDataDto(String id) {
		ConfDataDto confDataDto = new ConfDataDto();
		confDataDto.setId(id);
		return confDataDto;
	}
}
