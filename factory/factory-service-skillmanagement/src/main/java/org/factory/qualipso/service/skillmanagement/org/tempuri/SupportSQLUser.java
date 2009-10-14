
package org.factory.qualipso.service.skillmanagement.org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SupportSQLUser complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SupportSQLUser">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id_skill" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="id_user" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id_topic" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Topic" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id_level" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Level" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AorK" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupportSQLUser", propOrder = {
    "idSkill",
    "idUser",
    "idTopic",
    "topic",
    "idLevel",
    "level",
    "aorK"
})
public class SupportSQLUser {

    @XmlElement(name = "id_skill")
    protected int idSkill;
    @XmlElement(name = "id_user")
    protected String idUser;
    @XmlElement(name = "id_topic")
    protected int idTopic;
    @XmlElement(name = "Topic")
    protected String topic;
    @XmlElement(name = "id_level")
    protected int idLevel;
    @XmlElement(name = "Level")
    protected String level;
    @XmlElement(name = "AorK")
    protected String aorK;

    /**
     * Gets the value of the idSkill property.
     * 
     */
    public int getIdSkill() {
        return idSkill;
    }

    /**
     * Sets the value of the idSkill property.
     * 
     */
    public void setIdSkill(int value) {
        this.idSkill = value;
    }

    /**
     * Gets the value of the idUser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdUser() {
        return idUser;
    }

    /**
     * Sets the value of the idUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdUser(String value) {
        this.idUser = value;
    }

    /**
     * Gets the value of the idTopic property.
     * 
     */
    public int getIdTopic() {
        return idTopic;
    }

    /**
     * Sets the value of the idTopic property.
     * 
     */
    public void setIdTopic(int value) {
        this.idTopic = value;
    }

    /**
     * Gets the value of the topic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Sets the value of the topic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTopic(String value) {
        this.topic = value;
    }

    /**
     * Gets the value of the idLevel property.
     * 
     */
    public int getIdLevel() {
        return idLevel;
    }

    /**
     * Sets the value of the idLevel property.
     * 
     */
    public void setIdLevel(int value) {
        this.idLevel = value;
    }

    /**
     * Gets the value of the level property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLevel() {
        return level;
    }

    /**
     * Sets the value of the level property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLevel(String value) {
        this.level = value;
    }

    /**
     * Gets the value of the aorK property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAorK() {
        return aorK;
    }

    /**
     * Sets the value of the aorK property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAorK(String value) {
        this.aorK = value;
    }

}
