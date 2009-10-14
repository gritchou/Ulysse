package org.qualipso.factory.collaboration.forum.entity;

import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.qualipso.factory.FactoryResource;
import org.qualipso.factory.FactoryResourceIdentifier;

@Entity
@XmlType(name = "ThreadMessage", namespace = "http://org.qualipso.factory.ws/resource/thread-message", propOrder = { "name","author", "parentId",
	"forumId", "messageBody", "datePosted","numReplies","messageReplies" })
@SuppressWarnings("serial")	
public class ThreadMessage extends FactoryResource
{

    private static final long serialVersionUID = 1482783023713344338L;
    
    public ThreadMessage(){
	
    }

    @Id
    private String id;
    private String path;
    private String name;
    private String parentId;
    private String forumId;
    private String author;
    // We don't persist the following (messageBody,datePosted,numReplies,messageReplies)
    private String messageBody;
    private String datePosted;
    private String numReplies = "0"; 
    private HashMap<String,ThreadMessage> messageReplies = null;

    @XmlAttribute(name = "id", required = true)
    public String getId()
    {
	return id;
    }

    public void setId(String id)
    {
	this.id = id;
    }

    @XmlAttribute(name = "path", required = true)
    @Transient
    @Override
    public String getResourcePath()
    {
	return path;
    }

    public void setResourcePath(String path)
    {
	this.path = path;
    }

    @XmlElement(name = "name", required = true)
    public String getName()
    {
	return name;
    }

    public void setName(String value)
    {
	this.name = value;
    }
    
    @Override
    @XmlTransient
    public FactoryResourceIdentifier getFactoryResourceIdentifier()
    {
	return new FactoryResourceIdentifier("ForumService", "ThreadMessage", getId());
    }

    @Override
    @XmlTransient
    public String getResourceName()
    {
	return getName();
    }    

    @XmlElement(name = "author", required = true)
    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    @XmlElement(name = "parentId", required = true)
    public String getParentId()
    {
	return parentId;
    }

    public void setParentId(String parentId)
    {
	this.parentId = parentId;
    }

    @XmlElement(name = "forumId", required = true)
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
	StringBuffer sb = new StringBuffer("Thread. Path "+this.path);
	sb.append("\nID "+this.id);
	sb.append("\nname "+this.name);
	sb.append("\ndatePosted "+this.datePosted);
	sb.append("\nforumId "+this.forumId);
	sb.append("\nmessageBody "+this.messageBody);
	sb.append("\nparentId "+this.parentId);
	return sb.toString();
    }
    
    @Transient
    public String getMessageBody()
    {
	return messageBody;
    }

    public void setMessageBody(String messageBody)
    {
	this.messageBody = messageBody;
    }

    @Transient
    public String getDatePosted()
    {
	return datePosted;
    }

    public void setDatePosted(String datePosted)
    {
	this.datePosted = datePosted;
    }
    
    @Transient
    public String getNumReplies()
    {
        return numReplies;
    }
    
    public void setNumReplies(String numReplies)
    {
        this.numReplies = numReplies;
    }
    
    @Transient
    public HashMap<String,ThreadMessage> getMessageReplies()
    {
	return messageReplies;
    }

    public void setMessageReplies(HashMap<String,ThreadMessage> messageReplies)
    {
	this.messageReplies = messageReplies;
    }

}
