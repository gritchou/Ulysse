
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
 *         &lt;element name="ControlSkillResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "controlSkillResult"
})
@XmlRootElement(name = "ControlSkillResponse")
public class ControlSkillResponse {

    @XmlElement(name = "ControlSkillResult")
    protected int controlSkillResult;

    /**
     * Gets the value of the controlSkillResult property.
     * 
     */
    public int getControlSkillResult() {
        return controlSkillResult;
    }

    /**
     * Sets the value of the controlSkillResult property.
     * 
     */
    public void setControlSkillResult(int value) {
        this.controlSkillResult = value;
    }

}
