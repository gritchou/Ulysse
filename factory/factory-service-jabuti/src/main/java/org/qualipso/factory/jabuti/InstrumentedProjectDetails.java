package org.qualipso.factory.jabuti;

import javax.activation.DataHandler;

/**
 * Description of the class InstrumentedProjectDetails.
 *
 *
 */
public class InstrumentedProjectDetails {

	private DataHandler file;
	private String commandLine;

	public DataHandler getFile() {
		return file;
	}
	public void setFile(DataHandler file) {
		this.file = file;
	}
	public String getCommandLine() {
		return commandLine;
	}
	public void setCommandLine(String commandLine) {
		this.commandLine = commandLine;
	}
}