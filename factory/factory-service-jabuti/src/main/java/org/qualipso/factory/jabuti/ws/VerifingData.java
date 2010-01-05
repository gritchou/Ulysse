package org.qualipso.factory.jabuti.ws;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.activation.DataHandler;

import org.qualipso.factory.jabuti.JabutiServiceException;
import org.qualipso.factory.jabuti.persistence.Dao;
import org.qualipso.factory.jabuti.persistence.JabutiServiceProject;

public class VerifingData {

	public boolean existProject(String id, Properties p)
	{
		Dao dao = new Dao(p);
		JabutiServiceProject jsp = dao.get(id);
		if(jsp == null)
			return false;
		return true;
	}

	public boolean isProjectInstrumented(String id)
	{
		//to do

		return true;
	}	

	public boolean isThereClassInFile(String classname, File f)
	{
		//to do

		return true;
	}

	public static void removeDir(File dir) {
		File[] files = dir.listFiles();

		for(int i = 0; i<files.length; i++) {
			if(files[i].isDirectory()) {
				removeDir(files[i]);
			}
			files[i].delete();
		}
	}

	public static File saveTempFile(DataHandler projectFile)
	throws JabutiServiceException
	{		
		File f = null;
		try {
			f = File.createTempFile("JabutiServiceTempFile" + String.valueOf(
					System.nanoTime()), ".tmp");
			FileOutputStream fos = new FileOutputStream(f);
			projectFile.writeTo(fos);
			fos.flush();
			fos.close(); 	    	
		}
		catch (Exception e) {
			throw new JabutiServiceException(e);
		}
		return f;
	}

}
