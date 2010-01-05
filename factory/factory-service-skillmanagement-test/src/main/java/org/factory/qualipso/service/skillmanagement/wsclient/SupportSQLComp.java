
package org.factory.qualipso.service.skillmanagement.wsclient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SupportSQLComp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SupportSQLComp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id_competence" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Competence" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupportSQLComp", propOrder = {
    "idCompetence",
    "competence",
    "description"
})
public class SupportSQLComp {

    @XmlElement(name = "id_competence")
    protected int idCompetence;
    @XmlElement(name = "Competence")
    protected String competence;
    @XmlElement(name = "Description")
    protected String description;

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
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

}
