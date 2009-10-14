
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
 *         &lt;element name="ControlAddTopicCompetenceResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "controlAddTopicCompetenceResult"
})
@XmlRootElement(name = "ControlAddTopicCompetenceResponse")
public class ControlAddTopicCompetenceResponse {

    @XmlElement(name = "ControlAddTopicCompetenceResult")
    protected int controlAddTopicCompetenceResult;

    /**
     * Gets the value of the controlAddTopicCompetenceResult property.
     * 
     */
    public int getControlAddTopicCompetenceResult() {
        return controlAddTopicCompetenceResult;
    }

    /**
     * Sets the value of the controlAddTopicCompetenceResult property.
     * 
     */
    public void setControlAddTopicCompetenceResult(int value) {
        this.controlAddTopicCompetenceResult = value;
    }

}
