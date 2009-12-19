package org.qualipso.factory.jabuti;

import java.io.File;

import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;

public class JabutiProject extends FactoryResource {
	
	private String id;
	private String name;
	private File projectFile;
	
	public JabutiProject(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public File getProjectFile() {
		return projectFile;
	}
	
	public String getProjectId() {
		return id;
	}
	
	// ----------------------- Métodos da interface "FactoryResource"

	public String getResourcePath() {
		// ????
		return null;
	}
	
	public FactoryResourceIdentifier getFactoryResourceIdentifier() {		
		return new FactoryResourceIdentifier(
				"JabutiService",
				"JabutiProject",
				id);		
	}
	
	public String getResourceName() {
		return name;
	}
	
}
