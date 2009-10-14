
package org.qualipso.factory.helloworld.client.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Profile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Profile">
 *   &lt;complexContent>
 *     &lt;extension base="{http://org.qualipso.factory.ws/service/helloworld}factoryResource">
 *       &lt;sequence>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="fullname" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="account-status" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="online-status" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="last-login-date" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
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
@XmlType(name = "Profile", namespace = "http://org.qualipso.factory.ws/resource/profile", propOrder = {
    "email"
})
public class Profile
    extends FactoryResource
{

    @XmlElement(required = true)
    protected String email;
    @XmlAttribute(required = true)
    protected String fullname;
    @XmlAttribute(name = "account-status", required = true)
    protected int accountStatus;
    @XmlAttribute(name = "online-status", required = true)
    protected int onlineStatus;
    @XmlAttribute(name = "last-login-date", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastLoginDate;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute(required = true)
    protected String path;

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the fullname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * Sets the value of the fullname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullname(String value) {
        this.fullname = value;
    }

    /**
     * Gets the value of the accountStatus property.
     * 
     */
    public int getAccountStatus() {
        return accountStatus;
    }

    /**
     * Sets the value of the accountStatus property.
     * 
     */
    public void setAccountStatus(int value) {
        this.accountStatus = value;
    }

    /**
     * Gets the value of the onlineStatus property.
     * 
     */
    public int getOnlineStatus() {
        return onlineStatus;
    }

    /**
     * Sets the value of the onlineStatus property.
     * 
     */
    public void setOnlineStatus(int value) {
        this.onlineStatus = value;
    }

    /**
     * Gets the value of the lastLoginDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * Sets the value of the lastLoginDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastLoginDate(XMLGregorianCalendar value) {
        this.lastLoginDate = value;
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
