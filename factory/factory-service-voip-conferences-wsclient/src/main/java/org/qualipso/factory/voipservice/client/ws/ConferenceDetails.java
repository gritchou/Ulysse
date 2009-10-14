
package org.qualipso.factory.voipservice.client.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConferenceDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConferenceDetails">
 *   &lt;complexContent>
 *     &lt;extension base="{http://org.qualipso.factory.ws/service/voip}factoryResource">
 *       &lt;sequence>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="adminPin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="startDateHR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="endDateHR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="maxUsers" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="userCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="owner" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="agenda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accessType" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="permanent" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="recorded" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *       &lt;attribute name="confNo" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="path" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConferenceDetails", namespace = "http://org.qualipso.factory.ws/resource/conference-detail", propOrder = {
    "value",
    "pin",
    "adminPin",
    "startDate",
    "startDateHR",
    "endDate",
    "endDateHR",
    "maxUsers",
    "userCount",
    "owner",
    "name",
    "agenda",
    "accessType",
    "permanent",
    "recorded"
})
public class ConferenceDetails
    extends FactoryResource
{

    @XmlElement(required = true)
    protected String value;
    protected String pin;
    protected String adminPin;
    protected Long startDate;
    protected String startDateHR;
    protected Long endDate;
    protected String endDateHR;
    protected Integer maxUsers;
    protected Integer userCount;
    protected String owner;
    protected String name;
    protected String agenda;
    protected short accessType;
    protected boolean permanent;
    protected boolean recorded;
    @XmlAttribute(required = true)
    protected int confNo;
    @XmlAttribute(required = true)
    protected String path;

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
     * Gets the value of the pin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPin() {
        return pin;
    }

    /**
     * Sets the value of the pin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPin(String value) {
        this.pin = value;
    }

    /**
     * Gets the value of the adminPin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdminPin() {
        return adminPin;
    }

    /**
     * Sets the value of the adminPin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdminPin(String value) {
        this.adminPin = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setStartDate(Long value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the startDateHR property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartDateHR() {
        return startDateHR;
    }

    /**
     * Sets the value of the startDateHR property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartDateHR(String value) {
        this.startDateHR = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEndDate(Long value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the endDateHR property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndDateHR() {
        return endDateHR;
    }

    /**
     * Sets the value of the endDateHR property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndDateHR(String value) {
        this.endDateHR = value;
    }

    /**
     * Gets the value of the maxUsers property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxUsers() {
        return maxUsers;
    }

    /**
     * Sets the value of the maxUsers property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxUsers(Integer value) {
        this.maxUsers = value;
    }

    /**
     * Gets the value of the userCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUserCount() {
        return userCount;
    }

    /**
     * Sets the value of the userCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUserCount(Integer value) {
        this.userCount = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwner(String value) {
        this.owner = value;
    }

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
     * Gets the value of the agenda property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgenda() {
        return agenda;
    }

    /**
     * Sets the value of the agenda property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgenda(String value) {
        this.agenda = value;
    }

    /**
     * Gets the value of the accessType property.
     * 
     */
    public short getAccessType() {
        return accessType;
    }

    /**
     * Sets the value of the accessType property.
     * 
     */
    public void setAccessType(short value) {
        this.accessType = value;
    }

    /**
     * Gets the value of the permanent property.
     * 
     */
    public boolean isPermanent() {
        return permanent;
    }

    /**
     * Sets the value of the permanent property.
     * 
     */
    public void setPermanent(boolean value) {
        this.permanent = value;
    }

    /**
     * Gets the value of the recorded property.
     * 
     */
    public boolean isRecorded() {
        return recorded;
    }

    /**
     * Sets the value of the recorded property.
     * 
     */
    public void setRecorded(boolean value) {
        this.recorded = value;
    }

    /**
     * Gets the value of the confNo property.
     * 
     */
    public int getConfNo() {
        return confNo;
    }

    /**
     * Sets the value of the confNo property.
     * 
     */
    public void setConfNo(int value) {
        this.confNo = value;
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
