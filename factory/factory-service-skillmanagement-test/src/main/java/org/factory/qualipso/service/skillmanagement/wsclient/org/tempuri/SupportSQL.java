
package org.factory.qualipso.service.skillmanagement.wsclient.org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SupportSQL complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SupportSQL">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id_typology" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Typology" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Active" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Data_creazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Data_modifica" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupportSQL", propOrder = {
    "idTypology",
    "typology",
    "description",
    "active",
    "dataCreazione",
    "dataModifica"
})
public class SupportSQL {

    @XmlElement(name = "id_typology")
    protected int idTypology;
    @XmlElement(name = "Typology")
    protected String typology;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "Active")
    protected boolean active;
    @XmlElement(name = "Data_creazione")
    protected String dataCreazione;
    @XmlElement(name = "Data_modifica")
    protected String dataModifica;

    /**
     * Gets the value of the idTypology property.
     * 
     */
    public int getIdTypology() {
        return idTypology;
    }

    /**
     * Sets the value of the idTypology property.
     * 
     */
    public void setIdTypology(int value) {
        this.idTypology = value;
    }

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

    /**
     * Gets the value of the active property.
     * 
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the value of the active property.
     * 
     */
    public void setActive(boolean value) {
        this.active = value;
    }

    /**
     * Gets the value of the dataCreazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataCreazione() {
        return dataCreazione;
    }

    /**
     * Sets the value of the dataCreazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataCreazione(String value) {
        this.dataCreazione = value;
    }

    /**
     * Gets the value of the dataModifica property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataModifica() {
        return dataModifica;
    }

    /**
     * Sets the value of the dataModifica property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataModifica(String value) {
        this.dataModifica = value;
    }

}
