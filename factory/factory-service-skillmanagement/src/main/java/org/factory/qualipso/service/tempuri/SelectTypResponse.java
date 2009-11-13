
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
 *         &lt;element name="SelectTypResult" type="{http://tempuri.org/}ArrayOfSupportSQL" minOccurs="0"/>
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
    "selectTypResult"
})
@XmlRootElement(name = "SelectTypResponse")
public class SelectTypResponse {

    @XmlElement(name = "SelectTypResult")
    protected ArrayOfSupportSQL selectTypResult;

    /**
     * Gets the value of the selectTypResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSupportSQL }
     *     
     */
    public ArrayOfSupportSQL getSelectTypResult() {
        return selectTypResult;
    }

    /**
     * Sets the value of the selectTypResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSupportSQL }
     *     
     */
    public void setSelectTypResult(ArrayOfSupportSQL value) {
        this.selectTypResult = value;
    }

}
