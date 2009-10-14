
package org.qualipso.factory.voipservice.client.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ParticipantsInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ParticipantsInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{http://org.qualipso.factory.ws/service/voip}factoryResource">
 *       &lt;sequence>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="time" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isBanned" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isTalking" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isMuted" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="path" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="username" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParticipantsInfo", namespace = "http://org.qualipso.factory.ws/resource/participants-info", propOrder = {
    "value"
})
public class ParticipantsInfo
    extends FactoryResource
{

    @XmlElement(required = true)
    protected String value;
    @XmlAttribute
    protected String time;
    @XmlAttribute
    protected String isBanned;
    @XmlAttribute
    protected String isTalking;
    @XmlAttribute
    protected String isMuted;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute(required = true)
    protected String path;
    @XmlAttribute
    protected String username;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the time property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTime(String value) {
        this.time = value;
    }

    /**
     * Gets the value of the isBanned property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsBanned() {
        return isBanned;
    }

    /**
     * Sets the value of the isBanned property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsBanned(String value) {
        this.isBanned = value;
    }

    /**
     * Gets the value of the isTalking property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsTalking() {
        return isTalking;
    }

    /**
     * Sets the value of the isTalking property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsTalking(String value) {
        this.isTalking = value;
    }

    /**
     * Gets the value of the isMuted property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsMuted() {
        return isMuted;
    }

    /**
     * Sets the value of the isMuted property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsMuted(String value) {
        this.isMuted = value;
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

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

}
