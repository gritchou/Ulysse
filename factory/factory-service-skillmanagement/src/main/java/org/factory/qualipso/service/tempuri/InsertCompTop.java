
package org.factory.qualipso.service.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="id_c" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="id_t" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="order" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "idC",
    "idT",
    "order"
})
@XmlRootElement(name = "InsertCompTop")
public class InsertCompTop {

    @XmlElement(name = "id_c")
    protected int idC;
    @XmlElement(name = "id_t")
    protected int idT;
    protected int order;

    /**
     * Gets the value of the idC property.
     * 
     */
    public int getIdC() {
        return idC;
    }

    /**
     * Sets the value of the idC property.
     * 
     */
    public void setIdC(int value) {
        this.idC = value;
    }

    /**
     * Gets the value of the idT property.
     * 
     */
    public int getIdT() {
        return idT;
    }

    /**
     * Sets the value of the idT property.
     * 
     */
    public void setIdT(int value) {
        this.idT = value;
    }

    /**
     * Gets the value of the order property.
     * 
     */
    public int getOrder() {
        return order;
    }

    /**
     * Sets the value of the order property.
     * 
     */
    public void setOrder(int value) {
        this.order = value;
    }

}
