package org.qualipso.factory.collaboration.ws.beans;

import java.util.HashMap;

public class MessageDTO
{
    private String id;
    private String name;
    private String parentId;
    private String forumId;
    private String author;
    private String messageBody;
    private String datePosted;
    private String numReplies = "0"; 
    private HashMap<String,MessageDTO> messageReplies = null;

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
    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getParentId()
    {
	return parentId;
    }

    public void setParentId(String parentId)
    {
	this.parentId = parentId;
    }

    public String getForumId()
    {
	return forumId;
    }

    public void setForumId(String forumId)
    {
	this.forumId = forumId;
    }
    
    @Override
    public String toString(){
	StringBuffer sb = new StringBuffer("Thread.");
	sb.append("\nID "+this.id);
	sb.append("\nname "+this.name);
	sb.append("\ndatePosted "+this.datePosted);
	sb.append("\nforumId "+this.forumId);
	sb.append("\nmessageBody "+this.messageBody);
	sb.append("\nparentId "+this.parentId);
	return sb.toString();
    }
    
    public String getMessageBody()
    {
	return messageBody;
    }

    public void setMessageBody(String messageBody)
    {
	this.messageBody = messageBody;
    }


    public String getDatePosted()
    {
	return datePosted;
    }

    public void setDatePosted(String datePosted)
    {
	this.datePosted = datePosted;
    }
    
    
    public String getNumReplies()
    {
        return numReplies;
    }
    
    public void setNumReplies(String numReplies)
    {
        this.numReplies = numReplies;
    }
    
    
    public HashMap<String,MessageDTO> getMessageReplies()
    {
	return messageReplies;
    }

    public void setMessageReplies(HashMap<String,MessageDTO> messageReplies)
    {
	this.messageReplies = messageReplies;
    }
}
