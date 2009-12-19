package org.qualipso.factory.collaboration.ws.beans;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class FolderDTO.
 */
public class FolderDTO implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6895405253938595418L;

    /** The id. */
    private String id;

    /** The name. */
    private String name;

    /** The parent folder id. */
    private String parentFolderId = "";
    // We don't persist the following:
    /** The description. */
    private String description;

    /** The date. */
    private String date = "";

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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer("Folder.");
	sb.append("\nID " + this.id);
	sb.append("\nname " + this.name);
	sb.append("\ndate " + this.date);
	sb.append("\nparentFolderID " + this.parentFolderId);
	sb.append("\nsdescription " + this.description);
	return sb.toString();
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription() {
	return description;
    }

    /**
     * Sets the description.
     * 
     * @param description the new description
     */
    public void setDescription(String description) {
	this.description = description;
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

}
