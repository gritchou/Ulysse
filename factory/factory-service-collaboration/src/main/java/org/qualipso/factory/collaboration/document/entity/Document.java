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

/**
 * The Class Document.
 */
@Entity
@XmlType(name = Document.RESOURCE_NAME, namespace = FactoryNamingConvention.RESOURCE_NAMESPACE
	+ Document.RESOURCE_NAME, propOrder = { "name", "author", "date",
	"type", "keywords", "version", "status", "fileName", "mimeType",
	"binaryContent", "size", "parentFolderId","resourceId" })
@SuppressWarnings("serial")
public class Document extends FactoryResource {
    
    /** The Constant RESOURCE_NAME. */
    public static final String RESOURCE_NAME = "document";
    
    /** The id. */
    @Id
    private String id;
    // we persist id,name,path and parent id and author
    /** The path. */
    private String path;
    
    /** The name. */
    private String name;
    
    /** The parent folder id. */
    private String parentFolderId = CollaborationUtils.DEFAULT_FOLDER_ID;
    
    /** The author. */
    private String author;
    // We don't persist resourceId,date,type,keywords,version,status,file details
    /** The resource id. */
    private String resourceId = "";
    
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
    
    /** The size. */
    private String size = "";

    /**
     * Instantiates a new document.
     */
    public Document() {
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    @XmlAttribute(name = "id", required = true)
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

    /* (non-Javadoc)
     * @see org.qualipso.factory.FactoryResource#getResourcePath()
     */
    @XmlAttribute(name = "path", required = true)
    @Transient
    @Override
    public String getResourcePath() {
	return path;
    }

    /**
     * Sets the resource path.
     * 
     * @param path the new resource path
     */
    public void setResourcePath(String path) {
	this.path = path;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    @XmlElement(name = "name", required = true)
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
    @XmlElement(name = "parentFolderId", required = false)
    public String getParentFolderId() {
	return parentFolderId;
    }

    /**
     * Sets the parent folder id.
     * 
     * @param parentFolderId the new parent folder id
     */
    public void setParentFolderId(String parentFolderId) {
	this.parentFolderId = parentFolderId;
    }

    /**
     * Gets the author.
     * 
     * @return the author
     */
    @XmlElement(name = "author", required = true)
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

    /* (non-Javadoc)
     * @see org.qualipso.factory.FactoryResource#getFactoryResourceIdentifier()
     */
    @Override
    @XmlTransient
    public FactoryResourceIdentifier getFactoryResourceIdentifier() {
	return new FactoryResourceIdentifier(DocumentService.SERVICE_NAME,
		Document.RESOURCE_NAME, getId());
    }

    /* (non-Javadoc)
     * @see org.qualipso.factory.FactoryResource#getResourceName()
     */
    @Override
    @XmlTransient
    public String getResourceName() {
	return getName();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("Document. Path " + this.path);
	sb.append("\nID " + this.id);
	sb.append("\nResourceId " + this.resourceId);
	sb.append("\nname " + this.name);
	sb.append("\nauthor " + this.author);
	sb.append("\ndate " + this.date);
	sb.append("\nkeywords " + this.keywords);
	sb.append("\ntype " + this.type);
	sb.append("\nparentFolderID " + this.parentFolderId);
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
     * Gets the resource id.
     * 
     * @return the resource id
     */
    @Transient
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
     * Gets the date.
     * 
     * @return the date
     */
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

    /**
     * Gets the size.
     * 
     * @return the size
     */
    @Transient
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
