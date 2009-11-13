
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
 *         &lt;element name="SelectCompResult" type="{http://tempuri.org/}ArrayOfSupportSQLComp" minOccurs="0"/>
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
    "selectCompResult"
})
@XmlRootElement(name = "SelectCompResponse")
public class SelectCompResponse {

    @XmlElement(name = "SelectCompResult")
    protected ArrayOfSupportSQLComp selectCompResult;

    /**
     * Gets the value of the selectCompResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSupportSQLComp }
     *     
     */
    public ArrayOfSupportSQLComp getSelectCompResult() {
        return selectCompResult;
    }

    /**
     * Sets the value of the selectCompResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSupportSQLComp }
     *     
     */
    public void setSelectCompResult(ArrayOfSupportSQLComp value) {
        this.selectCompResult = value;
    }

}
