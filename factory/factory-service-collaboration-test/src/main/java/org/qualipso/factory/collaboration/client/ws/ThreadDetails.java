
package org.qualipso.factory.collaboration.client.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for thread-details complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="thread-details">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="forumId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="author" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="messageBody" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="datePosted" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="isReply" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "thread-details", namespace = "http://org.qualipso.factory.ws/resource/thread-details", propOrder = {
    "name",
    "forumId",
    "author",
    "messageBody",
    "datePosted",
    "isReply"
})
public class ThreadDetails {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String forumId;
    @XmlElement(required = true)
    protected String author;
    @XmlElement(required = true)
    protected String messageBody;
    @XmlElement(required = true)
    protected String datePosted;
    protected boolean isReply;

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
     * Gets the value of the isReply property.
     * 
     */
    public boolean isIsReply() {
        return isReply;
    }

    /**
     * Sets the value of the isReply property.
     * 
     */
    public void setIsReply(boolean value) {
        this.isReply = value;
    }

}
