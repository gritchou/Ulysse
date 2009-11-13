package org.qualipso.factory.collaboration.document.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;
import org.qualipso.factory.collaboration.document.DocumentService;
import org.qualipso.factory.collaboration.utils.CollaborationUtils;

@Entity
@XmlType(name = Document.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ Document.RESOURCE_NAME, propOrder = { "name", "author", "date",
	"type", "keywords", "version", "status", "fileName", "mimeType",
	"binaryContent", "size", "parentFolderID" })
@SuppressWarnings("serial")
public class Document extends FactoryResource {
    public static final String RESOURCE_NAME = "document";
    @Id
    private String id;
    // we persist id,name,path and parent id and author
    private String path;
    private String name;
    private String parentFolderID = CollaborationUtils.DEFAULT_FOLDER_ID;
    private String author;
    // We don't persist date,type,keywords,version,status,file details
    private String date = "";// yyyy-mm-dd
    private String type = CollaborationUtils.TYPE_8;
    private String keywords = "qualipso,factory";
    private String version = "1.0";
    private String status = CollaborationUtils.STATUS_DRAFT;
    private String fileName = "";
    private String mimeType = "text/plain";
    private byte[] binaryContent = null;
    private String size = "";

    public Document() {
    }

    @XmlAttribute(name = "id", required = true)
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    @XmlAttribute(name = "path", required = true)
    @Transient
    @Override
    public String getResourcePath() {
	return path;
    }

    public void setResourcePath(String path) {
	this.path = path;
    }

    @XmlElement(name = "name", required = true)
    public String getName() {
	return name;
    }

    public void setName(String value) {
	this.name = value;
    }

    @XmlElement(name = "parentFolderID", required = false)
    public String getParentFolderID() {
	return parentFolderID;
    }

    public void setParentFolderID(String parentFolderID) {
	this.parentFolderID = parentFolderID;
    }

    @XmlElement(name = "author", required = true)
    public String getAuthor() {
	return author;
    }

    public void setAuthor(String author) {
	this.author = author;
    }

    @Override
    @XmlTransient
    public FactoryResourceIdentifier getFactoryResourceIdentifier() {
	return new FactoryResourceIdentifier(DocumentService.SERVICE_NAME,
		Document.RESOURCE_NAME, getId());
    }

    @Override
    @XmlTransient
    public String getResourceName() {
	return getName();
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("Document. Path " + this.path);
	sb.append("\nID " + this.id);
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

    @Transient
    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    @Transient
    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    @Transient
    public String getKeywords() {
	return keywords;
    }

    public void setKeywords(String keywords) {
	this.keywords = keywords;
    }

    @Transient
    public String getVersion() {
	return version;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    @Transient
    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    @Transient
    public String getFileName() {
	return fileName;
    }

    public void setFileName(String fileName) {
	this.fileName = fileName;
    }

    @Transient
    public String getMimeType() {
	return mimeType;
    }

    public void setMimeType(String mimeType) {
	this.mimeType = mimeType;
    }

    @Transient
    public byte[] getBinaryContent() {
	return binaryContent;
    }

    public void setBinaryContent(byte[] binaryContent) {
	this.binaryContent = binaryContent;
    }

    @Transient
    public String getSize() {
	return size;
    }

    public void setSize(String size) {
	this.size = size;
    }

}
