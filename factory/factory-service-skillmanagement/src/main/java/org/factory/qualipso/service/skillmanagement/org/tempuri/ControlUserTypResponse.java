
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
 *         &lt;element name="ControlUserTypResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "controlUserTypResult"
})
@XmlRootElement(name = "ControlUserTypResponse")
public class ControlUserTypResponse {

    @XmlElement(name = "ControlUserTypResult")
    protected int controlUserTypResult;

    /**
     * Gets the value of the controlUserTypResult property.
     * 
     */
    public int getControlUserTypResult() {
        return controlUserTypResult;
    }

    /**
     * Sets the value of the controlUserTypResult property.
     * 
     */
    public void setControlUserTypResult(int value) {
        this.controlUserTypResult = value;
    }

}
