
package org.factory.qualipso.service.skillmanagement.org.tempuri;

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
 *         &lt;element name="ControlAdminResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "controlAdminResult"
})
@XmlRootElement(name = "ControlAdminResponse")
public class ControlAdminResponse {

    @XmlElement(name = "ControlAdminResult")
    protected int controlAdminResult;

    /**
     * Gets the value of the controlAdminResult property.
     * 
     */
    public int getControlAdminResult() {
        return controlAdminResult;
    }

    /**
     * Sets the value of the controlAdminResult property.
     * 
     */
    public void setControlAdminResult(int value) {
        this.controlAdminResult = value;
    }

}
