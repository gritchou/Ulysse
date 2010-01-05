
package org.factory.qualipso.service.skillmanagement.wsclient.org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SupportSQLGapTopic complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SupportSQLGapTopic">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Typology" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Topic" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Level" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupportSQLGapTopic", propOrder = {
    "typology",
    "topic",
    "level"
})
public class SupportSQLGapTopic {

    @XmlElement(name = "Typology")
    protected String typology;
    @XmlElement(name = "Topic")
    protected String topic;
    @XmlElement(name = "Level")
    protected String level;

    /**
     * Gets the value of the typology property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypology() {
        return typology;
    }

    /**
     * Sets the value of the typology property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypology(String value) {
        this.typology = value;
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

}
