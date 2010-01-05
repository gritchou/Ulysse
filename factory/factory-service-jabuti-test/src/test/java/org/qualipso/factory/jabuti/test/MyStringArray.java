package org.qualipso.factory.jabuti.test;

import java.util.ArrayList;
import java.util.Arrays;

import org.qualipso.factory.jabuti.client.ws.StringArray;

public class MyStringArray extends StringArray {
	
	// TODO This default no-arg constructor is required by JAXB.
	public MyStringArray() {
		item = new ArrayList<String>();
	}
	
	public MyStringArray(String[] array) {
		item = Arrays.asList(array);
	}
	
}
