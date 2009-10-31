package org.qualipso.factory.collaboration.ws.beans;

import org.qualipso.factory.collaboration.ws.CollaborationWSUtils;

public class DocumentDTO
{

    private String id;
    private String name;
    private String parentFolderID = CollaborationWSUtils.DEFAULT_FOLDER_ID;
    private String author;
    private String date = "";//yyyy-mm-dd
    private String type = CollaborationWSUtils.TYPE_8;
    private String keywords = "qualipso,factory";
    private String version = "1.0";
    private String status = CollaborationWSUtils.STATUS_DRAFT;
    private String fileName = "";
    private String mimeType = "text/plain";
    private byte[] binaryContent = null;
    private String size = "";

    public String getId()
    {
	return id;
    }

    public void setId(String id)
    {
	this.id = id;
    }

    public String getName()
    {
	return name;
    }

    public void setName(String value)
    {
	this.name = value;
    }
    
    public String getParentFolderID()
    {
        return parentFolderID;
    }

    public void setParentFolderID(String parentFolderID)
    {
        this.parentFolderID = parentFolderID;
    }

    public String getAuthor()
    {
	return author;
    }

    public void setAuthor(String author)
    {
	this.author = author;
    }

    
    @Override
    public String toString(){
	StringBuffer sb = new StringBuffer("Document");
	sb.append("\nID "+this.id);
	sb.append("\nname "+this.name);
	sb.append("\nauthor "+this.author);
	sb.append("\ndate "+this.date);
	sb.append("\nkeywords "+this.keywords);
	sb.append("\ntype "+this.type);
	sb.append("\nparentFolderID "+this.parentFolderID);
	sb.append("\nstatus "+this.status);
	//
	sb.append("\n fileName "+this.fileName);
	sb.append(" mimeType "+this.mimeType);
	sb.append(" size "+this.size);
	if(this.binaryContent!=null){
	    sb.append( "bc size"+this.binaryContent.length);
	}
	return sb.toString();
    }
    
    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }
    
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getKeywords()
    {
        return keywords;
    }

    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }
    
    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }
    
    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }
    
    public byte[] getBinaryContent()
    {
	return binaryContent;
    }

    public void setBinaryContent(byte[] binaryContent)
    {
	this.binaryContent = binaryContent;
    }
    
    public String getSize()
    {
        return size;
    }

    public void setSize(String size)
    {
        this.size = size;
    }
    
}
