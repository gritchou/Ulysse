
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
 *         &lt;element name="SelectMyUserResult" type="{http://tempuri.org/}ArrayOfSupportSQLUser" minOccurs="0"/>
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
    "selectMyUserResult"
})
@XmlRootElement(name = "SelectMyUserResponse")
public class SelectMyUserResponse {

    @XmlElement(name = "SelectMyUserResult")
    protected ArrayOfSupportSQLUser selectMyUserResult;

    /**
     * Gets the value of the selectMyUserResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSupportSQLUser }
     *     
     */
    public ArrayOfSupportSQLUser getSelectMyUserResult() {
        return selectMyUserResult;
    }

    /**
     * Sets the value of the selectMyUserResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSupportSQLUser }
     *     
     */
    public void setSelectMyUserResult(ArrayOfSupportSQLUser value) {
        this.selectMyUserResult = value;
    }

}
