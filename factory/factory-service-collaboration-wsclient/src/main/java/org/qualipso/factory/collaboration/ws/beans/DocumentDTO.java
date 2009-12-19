package org.qualipso.factory.collaboration.ws.beans;

import java.io.Serializable;

import org.qualipso.factory.collaboration.ws.CollaborationWSUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class DocumentDTO.
 */
public class DocumentDTO implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8443295014160068549L;

    /** The id. */
    private String id;

    /** The resource id. */
    private String resourceId;

    /** The name. */
    private String name;

    /** The parent folder id. */
    private String parentFolderID = CollaborationWSUtils.DEFAULT_FOLDER_ID;

    /** The author. */
    private String author;

    /** The date. */
    private String date = "";// yyyy-mm-dd

    /** The type. */
    private String type = CollaborationWSUtils.TYPE_8;

    /** The keywords. */
    private String keywords = "qualipso,factory";

    /** The version. */
    private String version = "1.0";

    /** The status. */
    private String status = CollaborationWSUtils.STATUS_DRAFT;

    /** The file name. */
    private String fileName = "";

    /** The mime type. */
    private String mimeType = "text/plain";

    /** The binary content. */
    private byte[] binaryContent = null;

    /** The size. */
    private String size = "";

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId() {
	return id;
    }

    /**
     * Sets the id.
     * 
     * @param id the new id
     */
    public void setId(String id) {
	this.id = id;
    }

    /**
     * Gets the resource id.
     * 
     * @return the resource id
     */
    public String getResourceId() {
	return resourceId;
    }

    /**
     * Sets the resource id.
     * 
     * @param resourceId the new resource id
     */
    public void setResourceId(String resourceId) {
	this.resourceId = resourceId;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
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
     * Gets the parent folder id.
     * 
     * @return the parent folder id
     */
    public String getParentFolderID() {
	return parentFolderID;
    }

    /**
     * Sets the parent folder id.
     * 
     * @param parentFolderID the new parent folder id
     */
    public void setParentFolderID(String parentFolderID) {
	this.parentFolderID = parentFolderID;
    }

    /**
     * Gets the author.
     * 
     * @return the author
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("Document");
	sb.append("\nID " + this.id);
	sb.append("\nResourceId " + this.resourceId);
	sb.append("\nname " + this.name);
	sb.append("\nauthor " + this.author);
	sb.append("\ndate " + this.date);
	sb.append("\nkeywords " + this.keywords);
	sb.append("\ntype " + this.type);
	sb.append("\nparentFolderID " + this.parentFolderID);
	sb.append("\nstatus " + this.status);
	//
	sb.append("\n fileName " + this.fileName);
	sb.append(" mimeType " + this.mimeType);
	sb.append(" size " + this.size);
	if (this.binaryContent != null) {
	    sb.append("bc size" + this.binaryContent.length);
	}
	return sb.toString();
    }

    /**
     * Gets the date.
     * 
     * @return the date
     */
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

    /**
     * Gets the size.
     * 
     * @return the size
     */
    public String getSize() {
	return size;
    }

    /**
     * Sets the size.
     * 
     * @param size the new size
     */
    public void setSize(String size) {
	this.size = size;
    }

}
