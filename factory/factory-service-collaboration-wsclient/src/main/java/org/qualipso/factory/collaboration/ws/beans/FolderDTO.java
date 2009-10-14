package org.qualipso.factory.collaboration.ws.beans;

public class FolderDTO
{
    private String id;
    private String name;
    private String parentFolderId = "";
    //We don't persist the following:
    private String description;
    private String date = "";

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
    


    public String getParentFolderId()
    {
        return parentFolderId;
    }

    public void setParentFolderId(String parentFolderId)
    {
        this.parentFolderId = parentFolderId;
    }
    
    @Override
    public String toString(){
	StringBuffer sb = new StringBuffer("Folder.");
	sb.append("\nID "+this.id);
	sb.append("\nname "+this.name);
	sb.append("\ndate "+this.date);
	sb.append("\nparentFolderID "+this.parentFolderId);
	sb.append("\nsdescription "+this.description);
	return sb.toString();
    }
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

}
