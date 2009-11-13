
package org.factory.qualipso.service.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="iduser" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idtopic" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="idlevel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="descr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "iduser",
    "idtopic",
    "idlevel",
    "descr"
})
@XmlRootElement(name = "InsertMySkill")
public class InsertMySkill {

    protected String iduser;
    protected int idtopic;
    protected int idlevel;
    protected String descr;

    /**
     * Gets the value of the iduser property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIduser() {
        return iduser;
    }

    /**
     * Sets the value of the iduser property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIduser(String value) {
        this.iduser = value;
    }

    /**
     * Gets the value of the idtopic property.
     * 
     */
    public int getIdtopic() {
        return idtopic;
    }

    /**
     * Sets the value of the idtopic property.
     * 
     */
    public void setIdtopic(int value) {
        this.idtopic = value;
    }

    /**
     * Gets the value of the idlevel property.
     * 
     */
    public int getIdlevel() {
        return idlevel;
    }

    /**
     * Sets the value of the idlevel property.
     * 
     */
    public void setIdlevel(int value) {
        this.idlevel = value;
    }

    /**
     * Gets the value of the descr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescr() {
        return descr;
    }

    /**
     * Sets the value of the descr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescr(String value) {
        this.descr = value;
    }

}
