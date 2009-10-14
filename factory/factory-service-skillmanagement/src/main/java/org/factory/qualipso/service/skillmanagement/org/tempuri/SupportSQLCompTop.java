
package org.factory.qualipso.service.skillmanagement.org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SupportSQLCompTop complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SupportSQLCompTop">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id_competence" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Competence" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id_topic" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
@XmlType(name = "SupportSQLCompTop", propOrder = {
    "idCompetence",
    "competence",
    "idTopic",
    "topic",
    "level"
})
public class SupportSQLCompTop {

    @XmlElement(name = "id_competence")
    protected int idCompetence;
    @XmlElement(name = "Competence")
    protected String competence;
    @XmlElement(name = "id_topic")
    protected int idTopic;
    @XmlElement(name = "Topic")
    protected String topic;
    @XmlElement(name = "Level")
    protected String level;

    /**
     * Gets the value of the idCompetence property.
     * 
     */
    public int getIdCompetence() {
        return idCompetence;
    }

    /**
     * Sets the value of the idCompetence property.
     * 
     */
    public void setIdCompetence(int value) {
        this.idCompetence = value;
    }

    /**
     * Gets the value of the competence property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompetence() {
        return competence;
    }

    /**
     * Sets the value of the competence property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompetence(String value) {
        this.competence = value;
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
