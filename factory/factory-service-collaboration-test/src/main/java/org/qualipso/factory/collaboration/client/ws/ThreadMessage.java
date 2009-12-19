
package org.qualipso.factory.collaboration.client.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for thread-message complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="thread-message">
 *   &lt;complexContent>
 *     &lt;extension base="{http://org.qualipso.factory.ws/service/forum-management}factoryResource">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="author" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parentId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="forumId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="messageBody" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="datePosted" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="messages" type="{http://org.qualipso.factory.ws/resource/thread-message}thread-message" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="attachments" type="{http://org.qualipso.factory.ws/resource/attachment-details}attachment-details" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="replies" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="path" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "thread-message", namespace = "http://org.qualipso.factory.ws/resource/thread-message", propOrder = {
    "name",
    "author",
    "parentId",
    "forumId",
    "messageBody",
    "datePosted",
    "messages",
    "attachments",
    "replies"
})
public class ThreadMessage
    extends FactoryResource
{

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String author;
    @XmlElement(required = true)
    protected String parentId;
    @XmlElement(required = true)
    protected String forumId;
    protected String messageBody;
    protected String datePosted;
    @XmlElement(nillable = true)
    protected List<ThreadMessage> messages;
    protected List<AttachmentDetails> attachments;
    protected int replies;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute(required = true)
    protected String path;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the author property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the value of the author property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthor(String value) {
        this.author = value;
    }

    /**
     * Gets the value of the parentId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * Sets the value of the parentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentId(String value) {
        this.parentId = value;
    }

    /**
     * Gets the value of the forumId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForumId() {
        return forumId;
    }

    /**
     * Sets the value of the forumId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForumId(String value) {
        this.forumId = value;
    }

    /**
     * Gets the value of the messageBody property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageBody() {
        return messageBody;
    }

    /**
     * Sets the value of the messageBody property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageBody(String value) {
        this.messageBody = value;
    }

    /**
     * Gets the value of the datePosted property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatePosted() {
        return datePosted;
    }

    /**
     * Sets the value of the datePosted property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatePosted(String value) {
        this.datePosted = value;
    }

    /**
     * Gets the value of the messages property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the messages property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMessages().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ThreadMessage }
     * 
     * 
     */
    public List<ThreadMessage> getMessages() {
        if (messages == null) {
            messages = new ArrayList<ThreadMessage>();
        }
        return this.messages;
    }

    /**
     * Gets the value of the attachments property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attachments property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttachments().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AttachmentDetails }
     * 
     * 
     */
    public List<AttachmentDetails> getAttachments() {
        if (attachments == null) {
            attachments = new ArrayList<AttachmentDetails>();
        }
        return this.attachments;
    }

    /**
     * Gets the value of the replies property.
     * 
     */
    public int getReplies() {
        return replies;
    }

    /**
     * Sets the value of the replies property.
     * 
     */
    public void setReplies(int value) {
        this.replies = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

}
