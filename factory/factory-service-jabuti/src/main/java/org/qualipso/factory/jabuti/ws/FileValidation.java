package org.qualipso.factory.jabuti.ws;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class FileValidation {
	private String message;
	
	// TODO This gets hard when dealing with factory files... should do it
	// later.
	public boolean validateFile(File f)	{
		//verify if it is a zip file
		try {
			ZipFile zipfile = new JarFile(f);
			
			//to do verify if there is .class files
		} 
		catch (ZipException e) {
			message = "No valid jar file.";
			return false;
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public String getMessage() {
		return message;
	}
}
