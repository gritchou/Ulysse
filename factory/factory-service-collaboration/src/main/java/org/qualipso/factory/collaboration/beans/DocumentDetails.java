package org.qualipso.factory.collaboration.beans;

import java.io.Serializable;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.collaboration.utils.CollaborationUtils;

/**
 * The Class DocumentDetails.
 */
@XmlType(name = DocumentDetails.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ DocumentDetails.RESOURCE_NAME, propOrder = { "name",
	"author", "date", "type", "keywords", "version",
	"status", "fileName", "mimeType", "binaryContent" })
@SuppressWarnings("serial")
public class DocumentDetails implements Serializable {

    /**
     * Instantiates a new document details.
     */
    public DocumentDetails(){
    }
    
    /**
     * Instantiates a new document details.
     * 
     * @param name the name
     * @param date the date
     * @param type the type
     * @param keywords the keywords
     * @param version the version
     * @param status the status
     * @param fileName the file name
     * @param mimeType the mime type
     * @param binaryContent the binary content
     */
    public DocumentDetails(String name,
	    String date, String type, String keywords, String version,
	    String status, String fileName, String mimeType,
	    byte[] binaryContent){
	setName(name);
	setDate(date);
	//setAuthor(author);
	setType(type);
	setKeywords(keywords);
	setVersion(version);
	setStatus(status);
	setFileName(fileName);
	setMimeType(mimeType);
	setBinaryContent(binaryContent);
    }
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7181414172684110934L;
    
    /** The Constant RESOURCE_NAME. */
    public static final String RESOURCE_NAME = "document-details"; 

    /** The name. */
    private String name;
    
    /** The author. */
    private String author;
    
    /** The date. */
    private String date = "";// yyyy-mm-dd
    
    /** The type. */
    private String type = CollaborationUtils.TYPE_8;
    
    /** The keywords. */
    private String keywords = "qualipso,factory";
    
    /** The version. */
    private String version = "1.0";
    
    /** The status. */
    private String status = CollaborationUtils.STATUS_DRAFT;
    
    /** The file name. */
    private String fileName = "";
    
    /** The mime type. */
    private String mimeType = "text/plain";
    
    /** The binary content. */
    private byte[] binaryContent = null;

    /**
     * Gets the name.
     * 
     * @return the name
     */
    @XmlElement(name = "name", required = true)
    @Transient
    public String getName() {
	return name;
    }

    /**
     * Sets the name.
     * 
     * @param value the new name
     */
    public void setName(String value) {
	this.name = value;
    }

    /**
     * Gets the author.
     * 
     * @return the author
     */
    @XmlElement(name = "author", required = true)
    @Transient
    public String getAuthor() {
	return author;
    }

    /**
     * Sets the author.
     * 
     * @param author the new author
     */
    public void setAuthor(String author) {
	this.author = author;
    }

    /**
     * Gets the date.
     * 
     * @return the date
     */
    @XmlElement(name = "date", required = true)
    @Transient
    public String getDate() {
	return date;
    }

    /**
     * Sets the date.
     * 
     * @param date the new date
     */
    public void setDate(String date) {
	this.date = date;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    @XmlElement(name = "type", required = true)
    @Transient
    public String getType() {
	return type;
    }

    /**
     * Sets the type.
     * 
     * @param type the new type
     */
    public void setType(String type) {
	this.type = type;
    }

    /**
     * Gets the keywords.
     * 
     * @return the keywords
     */
    @XmlElement(name = "keywords", required = true)
    @Transient
    public String getKeywords() {
	return keywords;
    }

    /**
     * Sets the keywords.
     * 
     * @param keywords the new keywords
     */
    public void setKeywords(String keywords) {
	this.keywords = keywords;
    }

    /**
     * Gets the version.
     * 
     * @return the version
     */
    @XmlElement(name = "version", required = true)
    @Transient
    public String getVersion() {
	return version;
    }

    /**
     * Sets the version.
     * 
     * @param version the new version
     */
    public void setVersion(String version) {
	this.version = version;
    }

    /**
     * Gets the status.
     * 
     * @return the status
     */
    @XmlElement(name = "status", required = true)
    @Transient
    public String getStatus() {
	return status;
    }

    /**
     * Sets the status.
     * 
     * @param status the new status
     */
    public void setStatus(String status) {
	this.status = status;
    }

    /**
     * Gets the file name.
     * 
     * @return the file name
     */
    @XmlElement(name = "fileName", required = true)
    @Transient
    public String getFileName() {
	return fileName;
    }

    /**
     * Sets the file name.
     * 
     * @param fileName the new file name
     */
    public void setFileName(String fileName) {
	this.fileName = fileName;
    }

    /**
     * Gets the mime type.
     * 
     * @return the mime type
     */
    @XmlElement(name = "mimeType", required = true)
    @Transient
    public String getMimeType() {
	return mimeType;
    }

    /**
     * Sets the mime type.
     * 
     * @param mimeType the new mime type
     */
    public void setMimeType(String mimeType) {
	this.mimeType = mimeType;
    }

    /**
     * Gets the binary content.
     * 
     * @return the binary content
     */
    @XmlElement(name = "binaryContent", required = true)
    @Transient
    public byte[] getBinaryContent() {
	return binaryContent;
    }

    /**
     * Sets the binary content.
     * 
     * @param binaryContent the new binary content
     */
    public void setBinaryContent(byte[] binaryContent) {
	this.binaryContent = binaryContent;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("Document");
	sb.append("\nname " + this.name);
	sb.append("\nauthor " + this.author);
	sb.append("\ndate " + this.date);
	sb.append("\nkeywords " + this.keywords);
	sb.append("\ntype " + this.type);
	sb.append("\nstatus " + this.status);
	sb.append("\n fileName " + this.fileName);
	sb.append(" mimeType " + this.mimeType);
	if (this.binaryContent != null) {
	    sb.append("bc size" + this.binaryContent.length);
	}
	return sb.toString();
    }

}
