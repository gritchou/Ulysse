package org.qualipso.factory.jabuti;

import java.io.Serializable;

import javax.activation.DataHandler;
import javax.activation.DataSource;

public class MyDataHandler extends DataHandler implements Serializable {
	
	static final long serialVersionUID = 1L;
	
	public MyDataHandler() {
		this(null);
		// I think all custom classes must have an empty constructor... 
	}
	
	public MyDataHandler(DataSource ds) {
		super(ds);
	}
	
}