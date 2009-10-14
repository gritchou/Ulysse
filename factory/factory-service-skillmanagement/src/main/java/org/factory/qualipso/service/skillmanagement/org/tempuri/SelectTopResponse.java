
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
 *         &lt;element name="SelectTopResult" type="{http://tempuri.org/}ArrayOfSupportSQLTop" minOccurs="0"/>
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
    "selectTopResult"
})
@XmlRootElement(name = "SelectTopResponse")
public class SelectTopResponse {

    @XmlElement(name = "SelectTopResult")
    protected ArrayOfSupportSQLTop selectTopResult;

    /**
     * Gets the value of the selectTopResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSupportSQLTop }
     *     
     */
    public ArrayOfSupportSQLTop getSelectTopResult() {
        return selectTopResult;
    }

    /**
     * Sets the value of the selectTopResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSupportSQLTop }
     *     
     */
    public void setSelectTopResult(ArrayOfSupportSQLTop value) {
        this.selectTopResult = value;
    }

}
