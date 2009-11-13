
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
 *         &lt;element name="idtyp" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "idtyp"
})
@XmlRootElement(name = "SearchTypUser")
public class SearchTypUser {

    protected String iduser;
    protected int idtyp;

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
     * Gets the value of the idtyp property.
     * 
     */
    public int getIdtyp() {
        return idtyp;
    }

    /**
     * Sets the value of the idtyp property.
     * 
     */
    public void setIdtyp(int value) {
        this.idtyp = value;
    }

}
