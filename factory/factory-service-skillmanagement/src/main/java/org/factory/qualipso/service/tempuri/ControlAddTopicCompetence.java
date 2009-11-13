
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
 *         &lt;element name="idcomp" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="idtop" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "idcomp",
    "idtop"
})
@XmlRootElement(name = "ControlAddTopicCompetence")
public class ControlAddTopicCompetence {

    protected int idcomp;
    protected int idtop;

    /**
     * Gets the value of the idcomp property.
     * 
     */
    public int getIdcomp() {
        return idcomp;
    }

    /**
     * Sets the value of the idcomp property.
     * 
     */
    public void setIdcomp(int value) {
        this.idcomp = value;
    }

    /**
     * Gets the value of the idtop property.
     * 
     */
    public int getIdtop() {
        return idtop;
    }

    /**
     * Sets the value of the idtop property.
     * 
     */
    public void setIdtop(int value) {
        this.idtop = value;
    }

}
