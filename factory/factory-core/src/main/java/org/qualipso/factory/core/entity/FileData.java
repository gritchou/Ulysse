package org.qualipso.factory.core.entity;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Jerome Blanchard (jayblanc@gmail.com)
 * @date 19 august 2009
 */
@XmlRootElement(name="file-data")
@XmlType(name = "FileData", namespace = "http://org.qualipso.factory.ws/resource/file-data", propOrder =  {
    "data"})
public class FileData {
	
	private DataHandler data;

	public FileData() {
	}

	public FileData (DataHandler data) {
		this.data = data;
	}

	@XmlElement(name="data", required=true)
	@XmlAttachmentRef
	public DataHandler getData() {
		return data;
	}
	  
	public void setData(DataHandler data) {
		this.data = data;
	}
	
}
