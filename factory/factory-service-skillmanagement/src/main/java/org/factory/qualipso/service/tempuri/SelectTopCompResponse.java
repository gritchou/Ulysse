
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
 *         &lt;element name="SelectTopCompResult" type="{http://tempuri.org/}ArrayOfSupportSQLCompTop" minOccurs="0"/>
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
    "selectTopCompResult"
})
@XmlRootElement(name = "SelectTopCompResponse")
public class SelectTopCompResponse {

    @XmlElement(name = "SelectTopCompResult")
    protected ArrayOfSupportSQLCompTop selectTopCompResult;

    /**
     * Gets the value of the selectTopCompResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSupportSQLCompTop }
     *     
     */
    public ArrayOfSupportSQLCompTop getSelectTopCompResult() {
        return selectTopCompResult;
    }

    /**
     * Sets the value of the selectTopCompResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSupportSQLCompTop }
     *     
     */
    public void setSelectTopCompResult(ArrayOfSupportSQLCompTop value) {
        this.selectTopCompResult = value;
    }

}
