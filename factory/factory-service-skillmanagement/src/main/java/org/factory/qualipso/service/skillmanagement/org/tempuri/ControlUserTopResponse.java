
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
 *         &lt;element name="ControlUserTopResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "controlUserTopResult"
})
@XmlRootElement(name = "ControlUserTopResponse")
public class ControlUserTopResponse {

    @XmlElement(name = "ControlUserTopResult")
    protected int controlUserTopResult;

    /**
     * Gets the value of the controlUserTopResult property.
     * 
     */
    public int getControlUserTopResult() {
        return controlUserTopResult;
    }

    /**
     * Sets the value of the controlUserTopResult property.
     * 
     */
    public void setControlUserTopResult(int value) {
        this.controlUserTopResult = value;
    }

}
