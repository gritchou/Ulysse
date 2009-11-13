
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
 *         &lt;element name="SelectTypTrueResult" type="{http://tempuri.org/}ArrayOfSupportSQL" minOccurs="0"/>
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
    "selectTypTrueResult"
})
@XmlRootElement(name = "SelectTypTrueResponse")
public class SelectTypTrueResponse {

    @XmlElement(name = "SelectTypTrueResult")
    protected ArrayOfSupportSQL selectTypTrueResult;

    /**
     * Gets the value of the selectTypTrueResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSupportSQL }
     *     
     */
    public ArrayOfSupportSQL getSelectTypTrueResult() {
        return selectTypTrueResult;
    }

    /**
     * Sets the value of the selectTypTrueResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSupportSQL }
     *     
     */
    public void setSelectTypTrueResult(ArrayOfSupportSQL value) {
        this.selectTypTrueResult = value;
    }

}
